package com.example.android.maadhyam;

public class Bill {
    private int in_stock;
    private int amount;

    public Bill(){

    }

    public Bill(int in_stock, int amount) {
        this.in_stock = in_stock;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public int getIn_stock() {
        return in_stock;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setIn_stock(int in_stock) {
        this.in_stock = in_stock;
    }
}
