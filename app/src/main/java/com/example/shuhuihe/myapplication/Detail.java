package com.example.shuhuihe.myapplication;

/**
 * Created by shuhuihe on 11/25/17.
 */

public class Detail {

    String label;
    String content;
    boolean isIncreased;

    public Detail(String label, String content) {
        this.label = label;
        this.content = content;

    }

    public Detail(String label, String content, boolean isIncreased) {
        this.label = label;
        this.content = content;
        this.isIncreased = isIncreased;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isIncreased() {
        return isIncreased;
    }

    public void setIncreased(boolean increased) {
        isIncreased = increased;
    }
}
