package com.example.mvvm.model;

public class Fastfood {
    public String name;
    public String image;
    public Integer price;
    public Boolean status;
    public Fastfood() {
    }

    public Fastfood(String name, String image, Integer price, Boolean status) {
        this.name = name;
        this.image = image;
        this.price = price;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
