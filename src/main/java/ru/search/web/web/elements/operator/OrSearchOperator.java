package ru.search.web.web.elements.operator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.mongodb.client.MongoCollection;

import ru.search.web.web.database.TokenDocument;
import ru.search.web.web.elements.FilteredIterator;
import ru.search.web.web.elements.ISearchElement;
import ru.search.web.web.elements.IToken;
import ru.search.web.web.elements.IteratorCursor;
import ru.search.web.web.misc.Utils;

public final class OrSearchOperator implements ISearchOperator
{
    private static OrSearchOperator INSTANCE = null;

    private OrSearchOperator() {}

    public static OrSearchOperator get()
    {
        return INSTANCE == null ? INSTANCE = new OrSearchOperator() : INSTANCE;
    }

    @Override
    public String getDelimiter() 
    {
        return "|";
    }

    @Override
    public boolean test(List<ISearchElement> childs, String text) 
    {
        boolean flag = false;

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
                flag |= child.test(text);
            }
        }

        return flag;
    }

    @Override
    public Iterator<TokenDocument> getDocuments(List<ISearchElement> childs, boolean avoided, MongoCollection<TokenDocument> tokens) 
    {
        List<Iterator<TokenDocument>> iterators = new ArrayList<>();
        Set<String> tokenNames = new HashSet<>(); 

        for (ISearchElement child : childs)
        {
            if (child instanceof IToken token && !token.isAvoided())
            {
                tokenNames.addAll(token.getAllKeys());
            }
            else
            {
                iterators.add(child.getDocuments(tokens));
            }
        }

        if (tokenNames.size() > 0)
        {
            iterators.add(Utils.findByNames(tokens, tokenNames, true));
        }

        return new OrIterator(iterators, avoided);
    }



    private static class OrIterator extends FilteredIterator
    {
        private final List<IteratorCursor> cursors = new ArrayList<>();
        
        private TokenDocument nextElement = null;

        public OrIterator(Collection<Iterator<TokenDocument>> iterators, boolean avoided)
        {
            super(avoided);
            this.splitChilds(iterators);
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
            TokenDocument document = this.nextElement;
            this.nextElement = findNext();
            return document;
        }

        private TokenDocument findNext()
        {
            return Utils.findNextOr(this.cursors, getBlacklist());
        }
    }
}
