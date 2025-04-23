package com.example.ccafound1;

public class ActivityModel {
    private String name;
    private String item;
    private String date;
    private String description;
    private String imageUrl;

    public ActivityModel() {
    }

    public ActivityModel(String name, String item, String date, String description, String imageUrl) {
        this.name = name;
        this.item = item;
        this.date = date;
        this.description = description;
        this.imageUrl = imageUrl;
    }
    public String getName() {
        return name;
    }
    public String getItem() {
        return item;
    }
    public String getDate() {
        return date;
    }
    public String getDescription() {
        return description;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
