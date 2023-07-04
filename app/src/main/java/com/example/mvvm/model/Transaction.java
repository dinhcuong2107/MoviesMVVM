package com.example.mvvm.model;

public class Transaction {
    public String transaction_type; // deposit and payment
    public Integer amount;
    public String wallet; // = id users
    public String time;
    public String date;
    public String description;
    public String supporter; // if ( .equals(wallet)) ? nạp online :  nạp tại quầy có nhân viên hỗ trợ
    public Boolean status;
}
