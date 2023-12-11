package ru.search.web.web;

public class TokenReader 
{
    private static final String META = "()|&!";

    private final String str;
    private int cursor = 0;

    public TokenReader(String str)
    {
        this.str = str;
    }
    
    public String nextToken()
    {
        String token = "";
        while (cursor < str.length())
        {
            char c = str.charAt(cursor);
            if (c == ' ')
            {
                this.cursor++;
                if (!token.isEmpty())
                {
                    return token;   
                }
                continue;
            }
            else if (isMeta(c))
            {
                if (token.isEmpty())
                {
                    this.cursor++;
                    return String.valueOf(c);
                }
                else
                {
                    return token;
                }
            }
            else
            {
                this.cursor++;
                token += c;
            }
        }

        return token;
    }

    public static boolean isMeta(char c)
    {
        return META.indexOf(c) != -1;
    }
    
    public static boolean isMeta(String str)
    {
        return META.contains(str);
    }
}
