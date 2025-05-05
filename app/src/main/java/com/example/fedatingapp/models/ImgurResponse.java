package com.example.fedatingapp.models;



public class ImgurResponse {
    public boolean success;
    public int status;
    public Data data;

    public static class Data {
        public String id;
        public String deletehash;
        public String link;
        public String title;
        public String description;
        public String type;
        public int width;
        public int height;
        public long size;

        public Data(String id, String deletehash, String link, String title, String description, String type, int width, int height, long size) {
            this.id = id;
            this.deletehash = deletehash;
            this.link = link;
            this.title = title;
            this.description = description;
            this.type = type;
            this.width = width;
            this.height = height;
            this.size = size;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDeletehash() {
            return deletehash;
        }

        public void setDeletehash(String deletehash) {
            this.deletehash = deletehash;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public ImgurResponse(boolean success, int status, Data data) {
        this.success = success;
        this.status = status;
        this.data = data;
    }
}
