package com.example.android.maadhyam;

public class Registered {
    public String gst_number;
    public String address;
    public String alternate_number;

    public Registered(String gst_number, String address,String alternate_number) {
        this.gst_number = gst_number;
        this.address = address;
        this.alternate_number = alternate_number;
    }

    public String getAddress() {
        return address;
    }

    public String getAlternate_number() {
        return alternate_number;
    }

    public String getGst_number() {
        return gst_number;
    }
}
