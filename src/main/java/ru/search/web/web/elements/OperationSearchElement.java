package ru.search.web.web.elements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mongodb.client.MongoCollection;

import ru.search.web.web.database.TokenDocument;
import ru.search.web.web.elements.operator.AndSearchOperator;
import ru.search.web.web.elements.operator.ISearchOperator;
import ru.search.web.web.elements.operator.OrSearchOperator;

public final class OperationSearchElement implements ISearchElement
{
    private boolean avoided = false;
    private ISearchOperator operator = null;
    private final List<ISearchElement> childs = new ArrayList<>();

    @Override
    public boolean isAvoided() 
    {
        return this.avoided;
    }

    public ISearchOperator getOperator()
    {
        return this.operator == null ? AndSearchOperator.get() : this.operator;
    }

    @Override
    public List<ISearchElement> getChilds()
    {
        return this.childs;
    }

    @Override
    public void setAvoided(boolean avoided) 
    {
        this.avoided = avoided;
    }

    public OperationSearchElement avoid()
    {
        this.setAvoided(true);
        return this;
    }

    public OperationSearchElement setOperator(ISearchOperator operator)
    {
        this.operator = operator;
        return this;
    }

    public OperationSearchElement trySetOperator(ISearchOperator operator)
    {
        if (this.operator == null)
        {
            this.operator = operator;
        }
        return this;
    }

    public OperationSearchElement add(ISearchElement child)
    {
        this.childs.add(child);
        if (this.childs.size() == 2 && this.operator == null)
        {
            this.operator = AndSearchOperator.get();
        }
        return this;
    }

    public OperationSearchElement add(String token)
    {
        return add(new SearchToken(token));
    }

    public OperationSearchElement add(String token, boolean avoid)
    {
        SearchToken t = new SearchToken(token);
        t.setAvoided(avoid);
        return add(t);
    }

    @Override
    public boolean test(String text) 
    {
        return this.getOperator().test(this.childs, text);
    }

    @Override
    public Iterator<TokenDocument> getDocuments(MongoCollection<TokenDocument> tokens)
    {
        return this.getOperator().getDocuments(this.childs, this.avoided, tokens);
    }

    @Override
    public String toString()
    {
        List<String> strings = new ArrayList<>();
        for (ISearchElement child : this.childs)
        {
            String avoid = child.isAvoided() ? "!" : "";
            if (child.getChilds().size() <= 1)
            {
                strings.add(avoid + child.toString());
            }
            else
            {
                strings.add(avoid + "(" + child.toString() + ")");
            }
        }

        return String.join(this.getOperator().getDelimiter(), strings);
    }

    public static OperationSearchElement and()
    {
        return new OperationSearchElement().setOperator(AndSearchOperator.get());
    }

    public static OperationSearchElement or()
    {
        return new OperationSearchElement().setOperator(OrSearchOperator.get());
    }
}
