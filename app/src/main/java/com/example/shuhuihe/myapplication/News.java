package com.example.shuhuihe.myapplication;

/**
 * Created by shuhuihe on 11/27/17.
 */

public class News {

    String title;
    String link;
    String author;
    String date;

    public News(String title, String link, String author, String date) {
        this.title = title;
        this.link = link;
        this.author = author;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
