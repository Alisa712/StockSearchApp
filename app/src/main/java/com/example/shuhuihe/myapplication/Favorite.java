package com.example.shuhuihe.myapplication;

/**
 * Created by shuhuihe on 11/27/17.
 */

public class Favorite {
    String symbol;
    Float price;
    Float change;
    Float changePercent;
    Long timestamp;
    String changeInfo;
    boolean isIncresing;

    public Favorite(String symbol, Float price, Float change, Float changePercent, Long timestamp, String changeInfo, boolean isIncresing) {
        this.symbol = symbol;
        this.price = price;
        this.change = change;
        this.changePercent = changePercent;
        this.timestamp = timestamp;
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

    public Float getChange() {
        return change;
    }

    public void setChange(Float change) {
        this.change = change;
    }

    public Float getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(Float changePercent) {
        this.changePercent = changePercent;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
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
