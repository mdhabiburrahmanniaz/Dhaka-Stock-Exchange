package com.appszonepro.dhakastockexchange.helpar;

public class DseModel {
    private int id;
    private String name;
    private float price;
    private float change_price;
    private String persentage;
    private String time;

    public DseModel(int id, String name, float price, float change_price, String persentage, String time) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.change_price = change_price;
        this.persentage = persentage;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public float getChangePrice() {
        return change_price;
    }

    public String getPersentage() {
        return persentage;
    }

    public String getTime() {
        return time;
    }
}
