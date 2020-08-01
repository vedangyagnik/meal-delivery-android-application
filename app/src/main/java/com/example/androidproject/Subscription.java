package com.example.androidproject;

import android.net.Uri;

import java.io.Serializable;

public class Subscription implements Serializable {
    String id;
    String length;
    String price;
    String discount;
    String description;
    String alWarning;
    String mealCount;
    String imgUrl;

    public Subscription(String id, String length, String price, String discount, String description, String alWarning, String mealCount, String imgUrl) {
        this.id = id;
        this.length = length;
        this.price = price;
        this.discount = discount;
        this.description = description;
        this.alWarning = alWarning;
        this.mealCount = mealCount;
        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAlWarning() {
        return alWarning;
    }

    public void setAlWarning(String alWarning) {
        this.alWarning = alWarning;
    }

    public String getMealCount() {
        return mealCount;
    }

    public void setMealCount(String mealCount) {
        this.mealCount = mealCount;
    }
}
