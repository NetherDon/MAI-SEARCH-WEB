package ru.search.web.web.elements;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.demidko.aot.WordformMeaning;

public interface IToken extends ISearchElement
{
    public String getName();
    public List<WordformMeaning> getMeanings();

    public default Set<String> getAllKeys()
    {
        Set<String> result = new HashSet<>();

        result.add(getName());

        for (WordformMeaning meaning : this.getMeanings())
        {
            for (WordformMeaning transform : meaning.getTransformations())
            {
                result.add(transform.toString().toLowerCase());
            }
        }

        return result;
    }
}
