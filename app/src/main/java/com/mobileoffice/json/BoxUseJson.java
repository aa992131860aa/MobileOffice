package com.mobileoffice.json;

import com.mobileoffice.entity.BoxUse;

import java.util.List;

/**
 * Created by 99213 on 2018/1/10.
 */

public class BoxUseJson {
    private int result;
    private String msg;
    private List<BoxUse> obj;

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

    public List<BoxUse> getObj() {
        return obj;
    }

    public void setObj(List<BoxUse> obj) {
        this.obj = obj;
    }
}
