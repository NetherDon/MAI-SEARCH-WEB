package ru.search.web.web.elements.operator;

import java.util.Iterator;
import java.util.List;

import com.mongodb.client.MongoCollection;

import ru.search.web.web.database.TokenDocument;
import ru.search.web.web.elements.ISearchElement;

public interface ISearchOperator 
{
    public Iterator<TokenDocument> getDocuments(List<ISearchElement> childs, boolean avoided, MongoCollection<TokenDocument> tokens);
    public boolean test(List<ISearchElement> childs, String text);
    public String getDelimiter();
}
