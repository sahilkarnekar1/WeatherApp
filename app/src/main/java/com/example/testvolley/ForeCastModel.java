package com.example.testvolley;

public class ForeCastModel {

    String date,avgtemp_c,text,icon;

    public ForeCastModel() {

    }

    public ForeCastModel(String date, String avgtemp_c, String text, String icon) {
        this.date = date;
        this.avgtemp_c = avgtemp_c;
        this.text = text;
        this.icon = icon;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAvgtemp_c() {
        return avgtemp_c;
    }

    public void setAvgtemp_c(String avgtemp_c) {
        this.avgtemp_c = avgtemp_c;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
