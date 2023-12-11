package ru.search.web.web;

import java.util.ArrayList;
import java.util.List;

public class QueryData 
{
    public static final List<QueryData> ALL = new ArrayList<>();
    public static final QueryData QUERY1 = new QueryData(
        "BlizZard | mario",
        "blizzard|mario",
        "BlizZard", "|", "mario"    
    );
    public static final QueryData QUERY2 = new QueryData(
        "Трейлер  &  spider man",
        "трейлер&spider&man",
        "Трейлер", "&", "spider", "man"
    );
    public static final QueryData QUERY3 = new QueryData(
        "  genshin    примогемы   персонаж))",
        "genshin&примогемы&персонаж",
        "genshin", "примогемы", "персонаж"
    );
    public static final QueryData QUERY4 = new QueryData(
        "Blizzard (Overwatch | marvel)",
        "blizzard&(overwatch|marvel)",
        "Blizzard", "(", "Overwatch", "|", "marvel", ")"
    );
    public static final QueryData QUERY5 = new QueryData(
        "!(примогемы | персонаж) & (genshin | mario",
        "!(примогемы|персонаж)&(genshin|mario)",
        "!", "(", "примогемы", "|", "персонаж", ")", "&", "(", "genshin", "|", "mario"
    );

    

    public final String text;
    public final String[] tokens;
    public final String parseResult;

    public QueryData(String text, String parseResult, String... expectedTokens)
    {
        ALL.add(this);
        this.text = text;
        this.tokens = expectedTokens;
        this.parseResult = parseResult;
    }
}
