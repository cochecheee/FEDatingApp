package com.example.fedatingapp.models;

public class Notification {
    private String content;
    private String type;

    public Notification(String content, String type) {
        this.content = content;
        this.type = type;
    }

    public Notification() {
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public String getType() {
        return type;
    }
}
