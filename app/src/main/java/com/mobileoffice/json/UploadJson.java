package com.mobileoffice.json;

import com.mobileoffice.entity.Upload;

import java.util.List;

/**
 * Created by 99213 on 2017/10/18.
 */

public class UploadJson {
    private int result;
    private String msg;
    private Upload obj;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Upload getObj() {
        return obj;
    }

    public void setObj(Upload obj) {
        this.obj = obj;
    }
}
