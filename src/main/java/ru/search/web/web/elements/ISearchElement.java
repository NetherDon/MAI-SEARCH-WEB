package ru.search.web.web.elements;

import java.util.Iterator;
import java.util.List;

import com.mongodb.client.MongoCollection;

import ru.search.web.web.database.TokenDocument;

public interface ISearchElement
{
    //Возвращает итератор, обходящий документы, получившиеся в результате запроса
    public Iterator<TokenDocument> getDocuments(MongoCollection<TokenDocument> tokens);
    //Проверка текста на наличие в нем данного элемента
    public boolean test(String text);
    public List<ISearchElement> getChilds();
    //true - элемент не должен находиться в результате запроса
    public boolean isAvoided();
    public void setAvoided(boolean avoided);
}
