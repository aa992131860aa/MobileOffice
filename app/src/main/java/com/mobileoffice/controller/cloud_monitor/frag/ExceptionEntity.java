package com.mobileoffice.controller.cloud_monitor.frag;

/**
 * Created by 99213 on 2017/8/11.
 */

public class ExceptionEntity {
    private String time;
    private String content;
    private double longitude;
    private double latitude;

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

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
