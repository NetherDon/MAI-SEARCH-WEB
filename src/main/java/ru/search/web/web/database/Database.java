package ru.search.web.web.database;

import java.util.HashMap;
import java.util.Map;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Database
{
    private static final String TOKEN_KEY = "Tokens2";
    private static final String PAGES_KEY = "Documents";

    private final String uri;
    private final String name;

    private final Map<String, MongoCollection<?>> collections = new HashMap<>();
    private MongoClient client = null;
    private MongoDatabase database = null;

    public Database(String uri, String name)
    {
        this.uri = uri;
        this.name = name;
    }

    public void connect()
    {
        this.client = new MongoClient(new MongoClientURI(this.uri));
		this.database = this.client.getDatabase(this.name).withCodecRegistry(
            CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(
                    PojoCodecProvider.builder().register(TokenDocument.class, PageDocument.class).build()
                )
            )
        );
    }

    public <T> MongoCollection<T> openCollection(String name, Class<T> clazz)
    {
        MongoCollection<T> collection =  this.database.getCollection(name, clazz);
        this.collections.put(name, collection);
        return collection;
    }

    public void openDefaultCollections()
    {
        openCollection(TOKEN_KEY, TokenDocument.class);
        openCollection(PAGES_KEY, PageDocument.class);
    }

    @SuppressWarnings("unchecked")
    public <T> MongoCollection<T> getCollection(String name)
    {
        return (MongoCollection<T>)this.collections.get(name);
    }

    public MongoCollection<TokenDocument> getTokens()
    {
        return getCollection(TOKEN_KEY);
    }

    public MongoCollection<PageDocument> getPages()
    {
        return getCollection(PAGES_KEY);
    }

    public void close()
    {
        this.client.close();
    }
}
