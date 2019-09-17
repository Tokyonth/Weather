package com.tokyonth.weather.model.bean.entity;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class Index{

    @SerializedName("iname")
    private String name;
    @Nullable
    @SerializedName("ivalue")
    private String value;
    @Nullable
    private String detail;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

}
