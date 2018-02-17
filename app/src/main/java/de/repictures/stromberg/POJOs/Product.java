package de.repictures.stromberg.POJOs;

import java.io.Serializable;

public class Product implements Serializable {

    private String code;
    private String name;
    private String companynumber;
    private String companyname;
    private double price;
    private boolean isSelfBuy;
    private boolean isBuyable;

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

    public String getCompanynumber() {
        return companynumber;
    }

    public void setCompanynumber(String companynumber) {
        this.companynumber = companynumber;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public boolean isBuyable() {
        return isBuyable;
    }

    public void setBuyable(boolean buyable) {
        isBuyable = buyable;
    }
}