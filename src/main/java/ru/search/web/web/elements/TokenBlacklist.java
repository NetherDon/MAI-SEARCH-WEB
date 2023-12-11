package ru.search.web.web.elements;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import ru.search.web.web.database.TokenDocument;
import ru.search.web.web.misc.Utils;

public class TokenBlacklist
{
    private final List<IteratorCursor> cursors;
    private TokenDocument currentElement;

    public TokenBlacklist(Collection<Iterator<TokenDocument>> iterators)
    {
        this.cursors = iterators.stream().map(IteratorCursor::new).toList();
        next();
    }

    public boolean find(TokenDocument document)
    {
        while (this.currentElement != null)
        {
            if (this.currentElement.equals(document))
            {
                return true;
            }
            else if (this.currentElement.compareTo(document) < 0)
            {
                next();
                continue;
            }
            else
            {
                return false;
            }
        }

        return false;
    }

    private void next()
    {
        this.currentElement = Utils.findNextOr(this.cursors, null);
    }
}
