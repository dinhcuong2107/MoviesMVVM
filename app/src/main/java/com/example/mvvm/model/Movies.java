package com.example.mvvm.model;
public class Movies {
    public String key_film;
    public String time;
    public String date;
    public String cinema;
    public Integer price;
    public Boolean status;

    public Movies() {
    }
    public Movies(String key_film, String time, String date, String cinema, Integer price, Boolean status) {
        this.key_film = key_film;
        this.time = time;
        this.date = date;
        this.cinema = cinema;
        this.price = price;
        this.status = status;
    }

    public String getKey_film() {
        return key_film;
    }

    public void setKey_film(String key_film) {
        this.key_film = key_film;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCinema() {
        return cinema;
    }

    public void setCinema(String cinema) {
        this.cinema = cinema;
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
