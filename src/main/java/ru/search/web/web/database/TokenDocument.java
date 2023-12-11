package ru.search.web.web.database;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

public class TokenDocument implements Comparable<TokenDocument>
{
    @BsonProperty("_id")
    private ObjectId id;
    @BsonProperty("name")
    private String name;
    @BsonProperty("documentId")
    private ObjectId documentId;
    
    public ObjectId id() { return this.id; }
    public String name() { return this.name; }
    public ObjectId documentId() { return this.documentId; }

    public void setId(ObjectId id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDocumentId(ObjectId id) { this.documentId = id; }

    @Override
    public boolean equals(Object obj) 
    {
        if (obj == this)
        {
            return true;
        }
        
        if (obj instanceof TokenDocument token)
        {
            return this.documentId.equals(token.documentId);
        }

        return false;
    }
    @Override
    public int compareTo(TokenDocument o) 
    {
        return this.documentId.compareTo(o.documentId);
    }

    
}
