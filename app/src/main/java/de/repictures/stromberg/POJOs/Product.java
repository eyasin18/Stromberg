package de.repictures.stromberg.POJOs;

import java.io.Serializable;

public class Product implements Serializable {

    private String code;
    private String name;
    private double price;
    private boolean isSelfBuy;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isSelfBuy() {
        return isSelfBuy;
    }

    public void setSelfBuy(boolean selfBuy) {
        isSelfBuy = selfBuy;
    }
}