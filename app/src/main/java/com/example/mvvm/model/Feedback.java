package com.example.mvvm.model;

public class Feedback {
    public String key_film;
    public String key_user;
    public String comment;
    public String time;
    public Boolean status;

    public Feedback() {
    }

    public Feedback(String key_film, String key_user, String comment, String time, Boolean status) {
        this.key_film = key_film;
        this.key_user = key_user;
        this.comment = comment;
        this.time = time;
        this.status = status;
    }

    public String getKey_film() {
        return key_film;
    }

    public void setKey_film(String key_film) {
        this.key_film = key_film;
    }

    public String getKey_user() {
        return key_user;
    }

    public void setKey_user(String key_user) {
        this.key_user = key_user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
