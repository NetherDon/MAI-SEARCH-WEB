package ru.search.web.web;

import java.util.Collection;

import ru.search.web.web.database.PageDocument;

public class ShowMoreResponse 
{
    private double time = 0;
    private String pages = "";

    public double getTime() { return this.time; }
    public String getPages() { return this.pages; }

    public void setTime(double time) { this.time = time; }
    public void addPage(PageDocument page) { this.pages += page.asHTML(); }
    public void addPages(Collection<PageDocument> pages) { pages.forEach(this::addPage); }
}
