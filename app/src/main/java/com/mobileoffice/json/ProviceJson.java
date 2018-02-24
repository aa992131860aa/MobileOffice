package com.mobileoffice.json;

import java.util.List;

/**
 * Created by 99213 on 2017/6/16.
 */

public class ProviceJson {

    /**
     * result : 0
     * msg : 请求医院省份成功
     * obj : ["上海","云南","内蒙古","北京","吉林","四川","天津","宁夏","安徽","山东","山西","广东","广西","新疆","江苏","江西","河北","河南","浙江","海南","湖北","湖南","甘肃","福建","西藏","贵州","辽宁","重庆","陕西","青海","黑龙江"]
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
