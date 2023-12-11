package ru.search.web.web;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import ru.search.web.web.database.PageDocument;
import ru.search.web.web.database.TokenDocument;
import ru.search.web.web.elements.ISearchElement;
import ru.search.web.web.elements.TokenDocumentIterator;

@Controller
public class SearchController 
{
    private static final int PAGES_PER_REQUEST = 50;
    private static final LinkedHashMap<String, Iterator<TokenDocument>> CACHE = new LinkedHashMap<>();

    @GetMapping("/")
    public String getMain()
    {
        return "index";
    }

    @GetMapping("/search")
    public String search(ModelMap model, HttpSession session, @RequestParam("text") String query)
    {
        long startTime = System.currentTimeMillis();

        ISearchElement root = SearchParser.parse(query);

        TokenDocumentIterator queryResult = new TokenDocumentIterator(
            root.getDocuments(WebApplication.DATABASE.getTokens())
        );

        List<PageDocument> pages = queryResult.getDocuments(WebApplication.DATABASE.getPages(), PAGES_PER_REQUEST);

        boolean flag = true;
        String htmlList = "";
        for (PageDocument page : pages)
        {
            htmlList += page.asHTML();
            boolean g = root.test(page.text());
            System.out.println(String.format("%6b [%s] [%s]", g, page.id(), page.title()));
            flag &= g;
        }
        System.out.println(flag);

        addToCache(session.getId(), queryResult);

        double time = getTime(startTime);

        model.put("pages", htmlList);
        model.put("time", time);
        model.put("query", query);

        return "search";
    }

    @GetMapping("/search/more")
    public ResponseEntity<ShowMoreResponse> showMore(HttpSession session)
    {
        long startTime = System.currentTimeMillis();

        ShowMoreResponse response = new ShowMoreResponse();
        var cursor = getCursor(session.getId());
        if (cursor == null)
        {
            response.setTime(getTime(startTime));
            return ResponseEntity.ok(response);
        }

        TokenDocumentIterator iterator = new TokenDocumentIterator(cursor);
        List<PageDocument> pages = iterator.getDocuments(WebApplication.DATABASE.getPages(), PAGES_PER_REQUEST);
        response.addPages(pages);

        response.setTime(getTime(startTime));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/test")
    public ResponseEntity<String> test(HttpSession session)
    {
        System.out.println(session.getId());
        return ResponseEntity.ok("");
    }

    private static void addToCache(String sessionId, Iterator<TokenDocument> iterator)
    {
        if (CACHE.size() > 10)
        {
            CACHE.remove(CACHE.entrySet().stream().findFirst().get().getKey());
        }

        CACHE.put(sessionId, iterator);
    }
    
    private static Iterator<TokenDocument> getCursor(String sessionId)
    {
        var cursor = CACHE.getOrDefault(sessionId, null);
        if (cursor != null)
        {
            CACHE.put(sessionId, cursor);
        }

        return cursor;
    }

    private static double getTime(long startTime)
    {
        return (double)(System.currentTimeMillis() - startTime) / 1000;
    }
}
