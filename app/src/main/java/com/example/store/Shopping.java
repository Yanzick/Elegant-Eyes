package com.example.store;

import java.io.Serializable;

public class Shopping implements Serializable {
    private String name;
    private String imageUrl;
    private String price;
    private String rating;
    private String ID;
    private String MT;

    public Shopping(String name, String imageUrl, String price, String rating,String ID,String MT) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.rating = rating;
        this.ID = ID;
        this. MT = MT;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getMT() {
        return MT;
    }

    public void setMT(String MT) {
        this.MT = MT;
    }
}
