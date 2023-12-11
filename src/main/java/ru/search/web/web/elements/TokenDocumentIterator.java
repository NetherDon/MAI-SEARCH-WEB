package ru.search.web.web.elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import ru.search.web.web.database.PageDocument;
import ru.search.web.web.database.TokenDocument;

public class TokenDocumentIterator implements Iterator<TokenDocument>
{
    private final Iterator<TokenDocument> parent;

    public TokenDocumentIterator(Iterator<TokenDocument> parent)
    {
        this.parent = parent;
    }

    public List<PageDocument> getDocuments(MongoCollection<PageDocument> collection, int count)
    {
        Set<ObjectId> ids = new HashSet<>();
        while (ids.size() < 50 && this.hasNext())
        {
            TokenDocument document = this.next();
            ObjectId documentId = document.documentId();
            ids.add(documentId);
        }

        Document filter = new Document(
            "_id",
            new Document(
                "$in",
                ids  
            )
        );

        MongoCursor<PageDocument> cursor = collection
            .find(filter)
            .batchSize(1_000)
            .noCursorTimeout(true)
            .cursor();

        List<PageDocument> result = new ArrayList<>();
        while (cursor.hasNext())
        {
            result.add(cursor.next());
        }
        return result;
    }

    @Override
    public boolean hasNext() 
    {
        return this.parent.hasNext();
    }

    @Override
    public TokenDocument next() 
    {
        return this.parent.next();
    }
    
}
