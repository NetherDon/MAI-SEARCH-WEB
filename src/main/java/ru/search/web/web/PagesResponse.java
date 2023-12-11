package ru.search.web.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ru.search.web.web.database.PageDocument;

public class PagesResponse
{
    private double time = 0;
    private List<Page> pages = new ArrayList<>();

    public double getTime()
    {
        return this.time;
    }

    public List<Page> getPages()
    {
        return this.pages;
    }

    public void addPage(String title, String url, String description, String text)
    {
        this.pages.add(new Page(title, url, description, text));
    }

    public void addPage(PageDocument page)
    {
        addPage(page.title(), page.url(), page.description(), page.text());
    }

    public void addPages(Collection<PageDocument> pages)
    {
        pages.forEach(this::addPage);
    }

    public void setTime(double time)
    {
        this.time = time;
    }
    
    public static class Page
    {
        private String title;
        private String url;
        private String description;
        private String text;

        public Page(String title, String url, String description, String text)
        {
            this.title = title;
            this.url = url;
            this.description = description;
            this.text = text;
        }

        public String getTitle() { return this.title; }
        public String getUrl() { return this.url; }
        public String getDescription() { return this.description; }
        public String getText() { return this.text; }
    }
}
