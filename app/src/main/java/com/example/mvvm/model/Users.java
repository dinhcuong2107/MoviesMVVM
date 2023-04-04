package com.example.mvvm.model;

public class Users {
    public String avatar;
    public String fullname;
    public Boolean male;
    public String birthday;
    public String email;
    public boolean admin;
    public String phonenumber;
    public boolean status;

    public Users() {
    }

    public Users(String avatar, String fullname, Boolean male, String birthday, String email, boolean admin, String phonenumber, boolean status) {
        this.avatar = avatar;
        this.fullname = fullname;
        this.male = male;
        this.birthday = birthday;
        this.email = email;
        this.admin = admin;
        this.phonenumber = phonenumber;
        this.status = status;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Boolean getMale() {
        return male;
    }

    public void setMale(Boolean male) {
        this.male = male;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
