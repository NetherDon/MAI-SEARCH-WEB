package ru.search.web.web.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import ru.search.web.web.database.TokenDocument;
import ru.search.web.web.elements.IteratorCursor;
import ru.search.web.web.elements.TokenBlacklist;

public class Utils 
{
    private static Iterator<TokenDocument> find(MongoCollection<TokenDocument> collection, Document filter, boolean sort)
    {
        System.out.println("Query " + filter.toJson() + " sent");
        FindIterable<TokenDocument> find = collection.find();
        if (sort)
        {
            find.sort(new Document("documentId", 1));
        }
        find.filter(filter).batchSize(500).noCursorTimeout(true);
        MongoCursor<TokenDocument> cursor = find.cursor();
        System.out.println("Query " + filter.toJson() + " passed");
        return cursor;
    }

    public static Iterator<TokenDocument> findByName(MongoCollection<TokenDocument> collection, String name, boolean sort)
    {
        return find(collection, new Document("name", name), sort);
    }

    public static Iterator<TokenDocument> findByName(MongoCollection<TokenDocument> collection, String name)
    {
        return findByName(collection, name, true);
    }

    public static Iterator<TokenDocument> findByNames(MongoCollection<TokenDocument> collection, Collection<String> names, boolean sort)
    {
        return find(collection, 
            new Document(
                "name", 
                new Document(
                    "$in",
                    names
                )
            ), 
            sort
        );
    }

    public static Iterator<TokenDocument> findByNames(MongoCollection<TokenDocument> collection, Collection<String> names)
    {
        return findByNames(collection, names, true);
    }

    public static TokenDocument findNextOr(List<IteratorCursor> cursors, TokenBlacklist blacklist)
    {
        if (cursors.isEmpty())
        {
            return null;
        }

        while (cursors.stream().allMatch(IteratorCursor::hasValue))
            {
                TokenDocument min = null;
                List<IteratorCursor> mins = new ArrayList<>();

                for (IteratorCursor cursor : cursors)
                {
                    TokenDocument doc = cursor.get();
                    ObjectId id = doc.documentId();

                    if (min == null)
                    {
                        min = doc;
                        mins.add(cursor);
                        continue;
                    }

                    if (min.documentId().equals(id))
                    {
                        mins.add(cursor);
                        continue;
                    }

                    if (id.compareTo(min.documentId()) < 0)
                    {
                        min = doc;
                        mins.clear();
                        mins.add(cursor); 
                    }
                }

                mins.stream().forEach(IteratorCursor::tryNext);
                if (blacklist != null && blacklist.find(min))
                {
                    continue;
                }
                
                return min;
            }

            return null;
    }
}
