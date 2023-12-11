package ru.search.web.web.elements;

import java.util.Iterator;

import ru.search.web.web.database.TokenDocument;

public class IteratorCursor 
{
    public final Iterator<TokenDocument> iterator;
    private TokenDocument cursor;

    public IteratorCursor(Iterator<TokenDocument> iterator)
    {
        this.iterator = iterator;
        this.tryNext();
    }

    public void tryNext()
    {
        this.cursor = this.iterator.hasNext() ? this.iterator.next() : null;
    }

    public TokenDocument get()
    {
        return this.cursor;
    }

    public boolean hasValue()
    {
        return this.cursor != null;
    }
}
