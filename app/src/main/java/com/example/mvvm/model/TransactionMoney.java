package com.example.mvvm.model;

public class TransactionMoney {
    public String transaction_type; // deposit and payment
    public Integer amount;
//    public Integer balance; // Số dư hiện tại
    public String wallet; // = id users
    public String time;
    public String date;
    public String description;
    public String supporter; // if ( .equals(wallet)) ? nạp online :  nạp tại quầy có nhân viên hỗ trợ
    public Boolean status;
}
