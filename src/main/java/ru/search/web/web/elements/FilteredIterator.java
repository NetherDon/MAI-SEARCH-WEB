package ru.search.web.web.elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import ru.search.web.web.database.TokenDocument;

public abstract class FilteredIterator implements Iterator<TokenDocument>, IAvoidable
{
    private TokenBlacklist blacklist;
    private final boolean avoided;

    public FilteredIterator(boolean avoided)
    {
        this.avoided = avoided;
    }

    protected void onWhitelistRead(Iterator<TokenDocument> child)
    {

    }

    protected void splitChilds(Collection<Iterator<TokenDocument>> childs)
    {
        List<Iterator<TokenDocument>> blacklist = new ArrayList<>();
        for (Iterator<TokenDocument> child : childs)
        {
            if (child instanceof IAvoidable blacklisted && blacklisted.isAvoided())
            {
                blacklist.add(child);
            }
            else
            {
                this.onWhitelistRead(child);
            }
        }

        this.blacklist = new TokenBlacklist(blacklist);
    }

    @Override
    public boolean isAvoided() 
    {
        return this.avoided;
    }

    protected TokenBlacklist getBlacklist()
    {
        return this.blacklist;
    }
}
