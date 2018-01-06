package de.repictures.stromberg.POJOs;

import java.io.Serializable;

public class PurchaseOrder implements Serializable{

    private int[] amounts;
    private String buyerAccountnumber;
    private String dateTime;
    private Product[] products;
    private int number;
    private boolean completed;
    private boolean madeByUser;

    public int[] getAmounts() {
        return amounts;
    }

    public void setAmounts(int[] amounts) {
        this.amounts = amounts;
    }

    public String getBuyerAccountnumber() {
        return buyerAccountnumber;
    }

    public void setBuyerAccountnumber(String buyerAccountnumber) {
        this.buyerAccountnumber = buyerAccountnumber;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Product[] getProducts() {
        return products;
    }

    public void setProducts(Product[] products) {
        this.products = products;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isMadeByUser() {
        return madeByUser;
    }

    public void setMadeByUser(boolean madeByUser) {
        this.madeByUser = madeByUser;
    }
}
