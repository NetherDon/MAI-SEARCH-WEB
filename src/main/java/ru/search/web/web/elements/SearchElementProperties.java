package ru.search.web.web.elements;

import java.util.ArrayList;
import java.util.List;

public class SearchElementProperties 
{
    private final List<String> tokens = new ArrayList<>();
    private boolean avoided;

    public SearchElementProperties addToken(String... tokens)
    {
        for (String token : tokens)
        {
            this.tokens.add(token);
        }
        return this;
    }

    public SearchElementProperties setAvoided(boolean flag)
    {
        this.avoided = flag;
        return this;
    }

    public String[] getTokens()
    {
        return this.tokens.toArray(new String[0]);
    }

    public boolean isAvoided()
    {
        return this.avoided;
    }
}
