package com.example.mvvm.model;

public class Products {
    public String key_product;
    public int quantity;

    public Products() {
    }

    public Products(String key_product, int quantity) {
        this.key_product = key_product;
        this.quantity = quantity;
    }

    public String getKey_product() {
        return key_product;
    }

    public void setKey_product(String key_product) {
        this.key_product = key_product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
