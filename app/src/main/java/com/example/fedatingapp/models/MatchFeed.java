package com.example.fedatingapp.models;

public class MatchFeed {
    // ID của người dùng kia,
    private String id; // Sử dụng String để linh hoạt
    private String name;
    private String pictureUrl; // ** Đổi tên từ picture để rõ ràng là URL **
    private String location; // Có thể là khoảng cách hoặc địa điểm
    private String date; // Ngày match (dạng String để hiển thị)
    private boolean isNewMatch; // ** Thêm để hiển thị "Nouveau Match !" nếu cần ** (Tùy chọn)

    public MatchFeed() {
    }

    public MatchFeed(String id, String name, String pictureUrl, String location, String date, boolean isNewMatch) {
        this.id = id;
        this.name = name;
        this.pictureUrl = pictureUrl;
        this.location = location;
        this.date = date;
        this.isNewMatch = isNewMatch;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isNewMatch() {
        return isNewMatch;
    }

    public void setNewMatch(boolean newMatch) {
        isNewMatch = newMatch;
    }
}
