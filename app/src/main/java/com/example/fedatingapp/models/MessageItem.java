package com.example.fedatingapp.models;

public class MessageItem {

    private int senderId;
    private String name;
    private String content;
    private int count;
    private String picture;


    public MessageItem() {
    }

    public MessageItem(int id, String name, String content, int count, String picture) {
        this.senderId = id;
        this.name = name;
        this.content = content;
        this.count = count;
        this.picture = picture;
    }

    public int getId() {
        return senderId;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public int getCount() {
        return count;
    }

    public String getPicture() {
        return picture;
    }

    public void setId(int id) {
        this.senderId = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}