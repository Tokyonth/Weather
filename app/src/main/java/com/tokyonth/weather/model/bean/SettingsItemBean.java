package com.tokyonth.weather.model.bean;

public class SettingsItemBean {

    private int type;
    private String title;
    private String sub;
    private int color;

    public SettingsItemBean(int type, String title, String sub, int color) {
        this.type = type;
        this.title = title;
        this.sub = sub;
        this.color = color;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }


}
