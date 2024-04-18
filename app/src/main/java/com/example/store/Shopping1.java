package com.example.store;

import java.io.Serializable;

public class Shopping1 implements Serializable {
    private String name;
    private String imageUrl;
    private int price;
    private String rating;
    private String ID;
    private String MT;
    private boolean isChecked;
    private int quantity;

    public Shopping1(String name, String imageUrl, int price, String rating,String ID,String MT) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.rating = rating;
        this.ID = ID;
        this. MT = MT;
        this.isChecked = false;
        this.quantity = 1;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
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
    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
    public int getQuantity() { // Phương thức getter cho quantity
        return quantity;
    }

    public void setQuantity(int quantity) { // Phương thức setter cho quantity
        this.quantity = quantity;
    }
}
