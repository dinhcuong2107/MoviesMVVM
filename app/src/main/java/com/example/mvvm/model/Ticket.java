package com.example.mvvm.model;

public class Ticket {
    public String key_movies;
    public String key_user;
    public String seat;
    public Boolean paper;
    public Integer price;
    public String time;
    public String key_fastfood;
    public String quantity_fastfood;
    public Boolean status;

    public Ticket() {
    }

    public Ticket(String key_movies, String key_user, String seat, Boolean paper, Integer price, String time, String key_fastfood, String quantity_fastfood, Boolean status) {
        this.key_movies = key_movies;
        this.key_user = key_user;
        this.seat = seat;
        this.paper = paper;
        this.price = price;
        this.time = time;
        this.key_fastfood = key_fastfood;
        this.quantity_fastfood = quantity_fastfood;
        this.status = status;
    }

    public String getKey_movies() {
        return key_movies;
    }

    public void setKey_movies(String key_movies) {
        this.key_movies = key_movies;
    }

    public String getKey_user() {
        return key_user;
    }

    public void setKey_user(String key_user) {
        this.key_user = key_user;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public Boolean getPaper() {
        return paper;
    }

    public void setPaper(Boolean paper) {
        this.paper = paper;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getKey_fastfood() {
        return key_fastfood;
    }

    public void setKey_fastfood(String key_fastfood) {
        this.key_fastfood = key_fastfood;
    }

    public String getQuantity_fastfood() {
        return quantity_fastfood;
    }

    public void setQuantity_fastfood(String quantity_fastfood) {
        this.quantity_fastfood = quantity_fastfood;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
