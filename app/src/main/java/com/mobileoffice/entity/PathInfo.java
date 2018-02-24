package com.mobileoffice.entity;

import java.io.Serializable;

/**
 * Created by 99213 on 2018/1/9.
 */

public class PathInfo implements Serializable{
    private int orderId;
    private String month;
    private String time;
    private String content;
    private String longitude;
    private String latitude;
    private String lnglat;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getMonth() {
        return month;
    }
    public void setMonth(String month) {
        this.month = month;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getLnglat() {
        return lnglat;
    }
    public void setLnglat(String lnglat) {
        this.lnglat = lnglat;
    }
    public String getLongitude() {
        return longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public String getLatitude() {
        return latitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
