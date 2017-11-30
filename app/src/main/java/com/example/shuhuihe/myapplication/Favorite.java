package com.example.shuhuihe.myapplication;

import android.support.annotation.NonNull;

import java.util.Comparator;

/**
 * Created by shuhuihe on 11/27/17.
 */

public class Favorite implements Comparable<Favorite> {
    String symbol;
    Float price;
    Float change;
    Float changePercent;
    Long timestamp;
    String changeInfo;
    boolean isIncresing;

    public Favorite(String symbol, Float price, Float change, Float changePercent, Long timestamp, String changeInfo, boolean isIncresing) {
        super();
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

    @Override
    public int compareTo(@NonNull Favorite favorite) {
        return 0;
    }

    public static Comparator<Favorite> defaultComp = new Comparator<Favorite>() {

        @Override
        public int compare(Favorite t1, Favorite t2) {
            long timestamp1 = t1.getTimestamp();
            long timestamp2 = t2.getTimestamp();

            return timestamp1 > timestamp2 ? 1 : -1;

        }
    };

    public static Comparator<Favorite> symbolComp = new Comparator<Favorite>() {

        @Override
        public int compare(Favorite t1, Favorite t2) {
            String symbol1 = t1.getSymbol().toUpperCase();
            String symbol2 = t2.getSymbol().toUpperCase();
            return symbol1.compareTo(symbol2);
        }
    };

    public static Comparator<Favorite> symbolCompReverse = new Comparator<Favorite>() {
        @Override
        public int compare(Favorite t1, Favorite t2) {
            String symbol1 = t1.getSymbol().toUpperCase();
            String symbol2 = t2.getSymbol().toUpperCase();
            return symbol2.compareTo(symbol1);
        }
    };

    public static Comparator<Favorite> priceComp = new Comparator<Favorite>() {
        @Override
        public int compare(Favorite t1, Favorite t2) {
            Float price1 = t1.getPrice();
            Float price2 = t2.getPrice();

            return price1>price2 ? 1 : -1;
        }
    };

    public static Comparator<Favorite> priceCompReverse = new Comparator<Favorite>() {
        @Override
        public int compare(Favorite t1, Favorite t2) {
            Float price1 = t1.getPrice();
            Float price2 = t2.getPrice();

            return price1<price2 ? 1 : -1;
        }
    };

    public static Comparator<Favorite> changeComp = new Comparator<Favorite>() {
        @Override
        public int compare(Favorite t1, Favorite t2) {
            Float price1 = t1.getChange();
            Float price2 = t2.getChange();

            return price1>price2 ? 1 : -1;
        }
    };

    public static Comparator<Favorite> changeCompReverse = new Comparator<Favorite>() {
        @Override
        public int compare(Favorite t1, Favorite t2) {
            Float price1 = t1.getChange();
            Float price2 = t2.getChange();

            return price1<price2 ? 1 : -1;
        }
    };

    public static Comparator<Favorite> changePercentComp = new Comparator<Favorite>() {
        @Override
        public int compare(Favorite t1, Favorite t2) {
            Float price1 = t1.getChangePercent();
            Float price2 = t2.getChangePercent();

            return price1>price2 ? 1 : -1;
        }
    };

    public static Comparator<Favorite> changePercentCompReverse = new Comparator<Favorite>() {
        @Override
        public int compare(Favorite t1, Favorite t2) {
            Float price1 = t1.getChangePercent();
            Float price2 = t2.getChangePercent();

            return price1<price2 ? 1 : -1;
        }
    };
}
