package com.novikon.app;

public class NewsItem {
    public int updateId;
    public String title;
    public String description;
    public String fullText;
    public int date;
    public String photoUrl;
    public String caption;

    public NewsItem(int updateId, String title, String description, String fullText, int date, String photoUrl, String caption) {
        this.updateId = updateId;
        this.title = title;
        this.description = description;
        this.fullText = fullText;
        this.date = date;
        this.photoUrl = photoUrl;
        this.caption = caption;
    }
}
