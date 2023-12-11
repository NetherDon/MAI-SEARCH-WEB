package ru.search.web.web.elements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.github.demidko.aot.WordformMeaning;
import com.mongodb.client.MongoCollection;

import ru.search.web.web.database.TokenDocument;
import ru.search.web.web.misc.Utils;

public class SearchToken implements IToken
{
    private static final int REGEX_FLAGS = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.MULTILINE;

    private String name;
    private List<WordformMeaning> meanings;
    private boolean inverted = false;

    public SearchToken(String name)
    {
        setName(name);
    }

    public void setName(String name)
    {
        this.name = name.toLowerCase();
    }

    @Override
    public String getName() 
    {
        return this.name;
    }

    @Override
    public List<WordformMeaning> getMeanings() 
    {
        if (this.meanings == null)
        {
            this.meanings = WordformMeaning.lookupForMeanings(this.name);
        }

        return this.meanings;
    }

    @Override
    public List<ISearchElement> getChilds() 
    {
        return new ArrayList<>();
    }

    @Override
    public boolean test(String text) 
    {
        Set<String> keySet = getAllKeys();
        String keys = String.join("|", keySet.stream().map(Pattern::quote).toList());
        String regex = String.format("(^|\\W)(%s)(\\W|$)", keys);
        Pattern pattern = Pattern.compile(regex, REGEX_FLAGS);
        return pattern.matcher(text).find();
    }

    @Override
    public Iterator<TokenDocument> getDocuments(MongoCollection<TokenDocument> tokens) 
    {
        return new TokenIterator(Utils.findByNames(tokens, getAllKeys(), true), this.inverted);
    }

    @Override
    public boolean isAvoided() 
    {
        return this.inverted;
    }

    @Override
    public void setAvoided(boolean inverted) 
    {
        this.inverted = inverted;
    }
    
    public SearchToken avoid()
    {
        this.setAvoided(true);
        return this;
    }

    @Override
    public String toString() 
    {
        return this.name;
    }

    private static class TokenIterator extends FilteredIterator
    {
        private final Iterator<TokenDocument> parent;

        public TokenIterator(Iterator<TokenDocument> parent, boolean inverted) 
        {
            super(inverted);
            this.parent = parent;
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
    
}
