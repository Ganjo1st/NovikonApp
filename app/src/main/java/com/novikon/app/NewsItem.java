package com.novikon.app;

public class NewsItem {
    private String title;
    private String content;
    private int views;
    private int likes;

    public NewsItem(String title, String content, int views, int likes) {
        this.title = title;
        this.content = content;
        this.views = views;
        this.likes = likes;
    }

    public String getTitle() { return title; }
    public String getContent() { return content; }
    public int getViews() { return views; }
    public int getLikes() { return likes; }
}
