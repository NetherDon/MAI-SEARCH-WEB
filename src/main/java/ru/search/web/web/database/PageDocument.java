package ru.search.web.web.database;

import org.bson.types.ObjectId;

public class PageDocument 
{
    private ObjectId id;
    private String title;
    private String url;
    private String text;
    private String description;

    public ObjectId id() { return this.id; }
    public String title() { return this.title; }
    public String url() { return this.url; }
    public String text() { return this.text; }
    public String description() { return this.description; }

    public void setId(ObjectId id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setUrl(String url) { this.url = url; }
    public void setText(String text) { this.text = text; }
    public void setDescription(String description) { this.description = description; }

    public String asHTML()
    {
        return String.format(
            "<div><a href=%s>%s</a><br><label>%s</label><br><label>%s</label><details><summary>Текст</summary><p>%s</p></details></div>",
            this.url,
            this.title,
            this.url,
            this.description,
            this.text
        );
    }
}
