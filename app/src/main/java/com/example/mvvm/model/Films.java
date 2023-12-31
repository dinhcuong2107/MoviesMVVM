package com.example.mvvm.model;

public class Films {
    public String name;
    public String poster;
    public String video;
    public String director;
    public String main_actors;
    public String country;
    public String genre;
    public String year;
    public String inf_short;
    public boolean status;

    public Films() {
    }

    public Films(String name, String poster, String video, String director, String main_actors, String country, String genre, String year, String inf_short, boolean status) {
        this.name = name;
        this.poster = poster;
        this.video = video;
        this.director = director;
        this.main_actors = main_actors;
        this.country = country;
        this.genre = genre;
        this.year = year;
        this.inf_short = inf_short;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }


    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getMain_actors() {
        return main_actors;
    }

    public void setMain_actors(String main_actors) {
        this.main_actors = main_actors;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getInf_short() {
        return inf_short;
    }

    public void setInf_short(String inf_short) {
        this.inf_short = inf_short;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}