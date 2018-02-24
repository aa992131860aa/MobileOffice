package com.mobileoffice.entity;

import java.sql.Timestamp;

/**
 * Created by 99213 on 2017/5/19.
 */

public class Upload {
    private int id;
    private int version;
    private String url;
    private Timestamp createTime;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
}
