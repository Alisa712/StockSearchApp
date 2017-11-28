package com.example.shuhuihe.myapplication;

/**
 * Created by shuhuihe on 11/27/17.
 */

public class Favorite {
    String symbol;
    Float price;
    String changeInfo;
    boolean isIncresing;

    public Favorite(String symbol, Float price, String changeInfo, boolean isIncresing) {
        this.symbol = symbol;
        this.price = price;
        this.changeInfo = changeInfo;
        this.isIncresing = isIncresing;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getChangeInfo() {
        return changeInfo;
    }

    public void setChangeInfo(String changeInfo) {
        this.changeInfo = changeInfo;
    }

    public boolean isIncresing() {
        return isIncresing;
    }

    public void setIncresing(boolean incresing) {
        isIncresing = incresing;
    }
}
