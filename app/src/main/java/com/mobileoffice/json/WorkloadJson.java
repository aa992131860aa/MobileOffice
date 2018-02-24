package com.mobileoffice.json;

import java.util.List;

/**
 * Created by 99213 on 2017/12/7.
 */

public class WorkloadJson {

    /**
     * result : 0
     * obj : [{"postRole":"转运医师","count":0,"time":"2017-12"},{"postRole":"修肝医师","count":0,"time":"2017-12"},{"postRole":"接肝医师","count":0,"time":"2017-12"},{"postRole":"主刀医师","count":0,"time":"2017-12"},{"postRole":"麻醉医师","count":0,"time":"2017-12"},{"postRole":"一助","count":0,"time":"2017-12"},{"postRole":"二助","count":0,"time":"2017-12"},{"postRole":"三助","count":0,"time":"2017-12"}]
     */

    private int result;
    private List<Workload> obj;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public List<Workload> getObj() {
        return obj;
    }

    public void setObj(List<Workload> obj) {
        this.obj = obj;
    }
}
