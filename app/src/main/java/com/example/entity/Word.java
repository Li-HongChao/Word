package com.example.entity;

public class Word {

    private String content;
    private String origin;

    @Override
    public String toString() {
        return "Word{" +
                "content='" + content + '\'' +
                ", origin='" + origin + '\'' +
                ", author='" + author + '\'' +
                ", category='" + category + '\'' +
                '}';
    }

    private String author;
    private String category;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
