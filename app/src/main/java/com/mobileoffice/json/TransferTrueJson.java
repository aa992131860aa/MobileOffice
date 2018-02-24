package com.mobileoffice.json;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 99213 on 2017/7/1.
 */

public class TransferTrueJson {

    /**
     * result : 0
     * msg : 获取转运中的信息
     * obj : [{"transferid":"279","organSeg":"54321","openPsd":"","fromCity":"杭州市滨江区","toHospName":"浙江大学医学院附属第二医院","tracfficType":"火车","tracfficNumber":"147","organ":"肝","organNum":"3","blood":"B","bloodNum":"2","sampleOrgan":"血管","sampleOrganNum":"1","opoName":"树兰(杭州)医院OPO","contactName":"陈杨","contactPhone":"18398850872","phone":"18639805480","trueName":"张绍康","getTime":"2017-07-01 09:14","isStart":"1"}]
     */

    private int result;
    private String msg;
    private TransferJson.ObjBean obj;

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


    public TransferJson.ObjBean getObj() {
        return obj;
    }

    public void setObj(TransferJson.ObjBean obj) {
        this.obj = obj;
    }
}
