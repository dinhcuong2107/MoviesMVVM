package com.example.mvvm.model;

public class SeriesFilms {
    public String name;
    public Integer totalEpisode;
    public Integer price;
    public String epsodecode;
    public Boolean status;

    public SeriesFilms() {
    }

    public SeriesFilms(String name, Integer totalEpisode, Integer price, String epsodecode, Boolean status) {
        this.name = name;
        this.totalEpisode = totalEpisode;
        this.price = price;
        this.epsodecode = epsodecode;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTotalEpisode() {
        return totalEpisode;
    }

    public void setTotalEpisode(Integer totalEpisode) {
        this.totalEpisode = totalEpisode;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getEpsodecode() {
        return epsodecode;
    }

    public void setEpsodecode(String epsodecode) {
        this.epsodecode = epsodecode;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
