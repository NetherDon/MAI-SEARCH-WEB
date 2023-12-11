package ru.search.web.web;

import java.util.Stack;

import ru.search.web.web.elements.ISearchElement;
import ru.search.web.web.elements.OperationSearchElement;
import ru.search.web.web.elements.operator.AndSearchOperator;
import ru.search.web.web.elements.operator.OrSearchOperator;

public class SearchParser 
{
    public static ISearchElement parse(String line)
    {
        Stack<OperationSearchElement> elements = new Stack<>();
        TokenReader reader = new TokenReader(line);

        OperationSearchElement operation = new OperationSearchElement();

        boolean avoidNext = false;
        String token = reader.nextToken();
        while (true)
        {
            String nextToken = reader.nextToken();
            if (token.isEmpty())
            {
                break;
            }

            if ("(".equals(token))
            {
                elements.push(operation);
                operation = new OperationSearchElement();
                elements.peek().add(operation);
                if (avoidNext)
                {
                    operation.avoid();
                    avoidNext = false;
                }
            }
            else if (")".equals(token))
            {
                if (!elements.isEmpty())
                {
                    operation = elements.pop();
                }
            }
            else if ("!".equals(token))
            {
                if (!TokenReader.isMeta(nextToken) || "(".equals(nextToken))
                {
                    avoidNext = true;
                }
            }
            else if ("&".equals(token))
            {
                operation.trySetOperator(AndSearchOperator.get());
            }
            else if ("|".equals(token))
            {
                operation.trySetOperator(OrSearchOperator.get());
            }
            else
            {
                operation.add(token, avoidNext);
                avoidNext = false;
            }

            token = nextToken;
        }

        while (!elements.isEmpty())
        {
            operation = elements.pop();
        }

        return operation;
    }
}
