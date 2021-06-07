package com.example.android.maadhyam;

public class Cart {
    public String category;
    public String brand;
    public String quantity;
    public String no_of_items;
    public String bill;
    public String delivery_address;

    public Cart(String category, String brand,String quantity,String no_of_items,String bill,String delivery_address) {
        this.category = category;
        this.brand = brand;
        this.quantity = quantity;
        this.no_of_items = no_of_items;
        this.bill = bill;
        this.delivery_address = delivery_address;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getCategory() {
        return category;
    }

    public String getBrand() {
        return brand;
    }

    public String getBill() {
        return bill;
    }

    public String getNo_of_items() {
        return no_of_items;
    }

    public String getDelivery_address() {
        return delivery_address;
    }
}
