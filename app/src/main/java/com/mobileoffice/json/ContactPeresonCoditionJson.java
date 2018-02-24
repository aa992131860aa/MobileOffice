package com.mobileoffice.json;

import java.util.List;

/**
 * Created by 99213 on 2017/7/16.
 */

public class ContactPeresonCoditionJson {

    /**
     * result : 0
     * msg : 获得成功
     * obj : ["fantasy","唐金梅","张绍康"]
     */

    private int result;
    private String msg;
    private List<String> obj;

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

    public List<String> getObj() {
        return obj;
    }

    public void setObj(List<String> obj) {
        this.obj = obj;
    }
}
