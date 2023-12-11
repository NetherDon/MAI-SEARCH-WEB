package ru.search.web.web.elements.operator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.bson.types.ObjectId;

import com.mongodb.client.MongoCollection;

import ru.search.web.web.database.TokenDocument;
import ru.search.web.web.elements.FilteredIterator;
import ru.search.web.web.elements.ISearchElement;
import ru.search.web.web.elements.IteratorCursor;
import ru.search.web.web.elements.TokenBlacklist;

public class AndSearchOperator implements ISearchOperator
{
    private static AndSearchOperator INSTANCE = null;

    private AndSearchOperator() {}

    public static AndSearchOperator get()
    {
        return INSTANCE == null ? INSTANCE = new AndSearchOperator() : INSTANCE;
    }
    
    @Override
    public String getDelimiter() 
    {
        return "&";
    }

    @Override
    public Iterator<TokenDocument> getDocuments(List<ISearchElement> childs, boolean avoided, MongoCollection<TokenDocument> tokens) 
    {
        return new AndIterator(childs.stream().map((child) -> child.getDocuments(tokens)).toList(), avoided);
    }

    @Override
    public boolean test(List<ISearchElement> childs, String text) 
    {
        for (ISearchElement child : childs)
        {
            if (child.isAvoided())
            {
                if (child.test(text))
                {
                    return false;
                }
            }
            else
            {
                if (!child.test(text))
                {
                    return false;
                }
            }
        }

        return true;
    }
    


    private static class AndIterator extends FilteredIterator
    {
        private final List<IteratorCursor> cursors = new ArrayList<>();
        private TokenDocument nextElement;

        public AndIterator(Collection<Iterator<TokenDocument>> childs, boolean inverted)
        {
            super(inverted);
            this.splitChilds(childs);
            this.nextElement = findNext();
        }

        @Override
        protected void onWhitelistRead(Iterator<TokenDocument> child) 
        {
            this.cursors.add(new IteratorCursor(child));
        }

        @Override
        public boolean hasNext() 
        {
            return this.nextElement != null;
        }

        @Override
        public TokenDocument next() 
        {
            TokenDocument result = this.nextElement;
            if (result == null)
            {
                throw new NoSuchElementException();
            }

            this.nextElement = findNext();
            return result;
        }

        private TokenDocument findNext()
        {
            TokenBlacklist blacklist = getBlacklist();
            if (this.cursors.size() == 0)
            {
                return null;
            }

            while (this.cursors.stream().allMatch(IteratorCursor::hasValue))
            {
                IteratorCursor min = null;
                List<IteratorCursor> minCursors = new ArrayList<>();
                List<String> ids = new ArrayList<>();

                for (IteratorCursor cursor : this.cursors)
                {
                    ObjectId id = cursor.get().documentId();
                    ids.add(id.toString());
                    if (min == null)
                    {
                        min = cursor;
                        minCursors.add(cursor);
                        continue;
                    }

                    if (id.equals(min.get().documentId()))
                    {
                        minCursors.add(cursor);
                        continue;
                    }

                    if (id.compareTo(min.get().documentId()) < 0)
                    {
                        min = cursor;
                        minCursors.clear();
                        minCursors.add(min);
                    }
                }
                
                boolean flag = minCursors.size() == this.cursors.size();

                if (minCursors.size() == this.cursors.size())
                {
                    TokenDocument result = min.get();
                    this.cursors.forEach(IteratorCursor::tryNext);
                    if (blacklist != null && blacklist.find(result))
                    {
                        this.print(ids, flag, true);
                        continue;
                    }

                    this.print(ids, flag, false);
                    return result;
                }

                minCursors.forEach(IteratorCursor::tryNext);
                this.print(ids, flag, false);
            }

            return null;
        }

        private void print(List<String> ids, boolean flag, boolean ignored)
        {
            System.out.println("[And] " + String.join(" - ", ids) + " - " + (flag ? "Equal" : "NonEqual") + " - " + (ignored ? "IGNORED" : "VALID"));
        }
    }
}
