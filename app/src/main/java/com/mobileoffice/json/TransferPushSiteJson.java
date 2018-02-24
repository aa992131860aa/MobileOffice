package com.mobileoffice.json;

/**
 * Created by 99213 on 2017/11/15.
 */

public class TransferPushSiteJson {
    private int result;
    private String msg;
    private TransferPushSite obj;

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

    public TransferPushSite getObj() {
        return obj;
    }

    public void setObj(TransferPushSite obj) {
        this.obj = obj;
    }

    public class TransferPushSite {
        private int temperatureStatus;
        private int openStatus;
        private int collisionStatus;

        public int getTemperatureStatus() {
            return temperatureStatus;
        }

        public void setTemperatureStatus(int temperatureStatus) {
            this.temperatureStatus = temperatureStatus;
        }

        public int getOpenStatus() {
            return openStatus;
        }

        public void setOpenStatus(int openStatus) {
            this.openStatus = openStatus;
        }

        public int getCollisionStatus() {
            return collisionStatus;
        }

        public void setCollisionStatus(int collisionStatus) {
            this.collisionStatus = collisionStatus;
        }
    }
}
