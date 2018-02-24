package com.mobileoffice.json;

import com.mobileoffice.entity.PathInfo;

import java.util.List;

/**
 * Created by 99213 on 2017/8/10.
 */

public class TransferRecordJson {
    /**
     * result : 0
     * msg : 获取转运记录成功
     * obj : [{"transferRecordId":74386,"transfer_id":"456","type":"0","currentCity":"杭州","distance":"1","longitude":"118.23","latitude":"29.13","temperature":"2","power":"99","humidity":"88","dbStatus":"N","createAt":"2017-07-26 19:49:10.0","modifyAt":"2017-07-26 19:49:10.0","press1":"60","press2":"68","flow1":"355","flow2":"350","pupple":"0"},{"transferRecordId":74387,"transfer_id":"456","type":"0","currentCity":"杭州","distance":"3","longitude":"118.24","latitude":"29.14","temperature":"2","power":"99","humidity":"88","dbStatus":"N","createAt":"2017-07-26 19:49:10.0","modifyAt":"2017-07-26 19:50:10.0","press1":"60","press2":"68","flow1":"355","flow2":"350","pupple":"0"},{"transferRecordId":74388,"transfer_id":"456","type":"0","currentCity":"杭州","distance":"4","longitude":"118.25","latitude":"29.15","temperature":"3","power":"99","humidity":"88","dbStatus":"N","createAt":"2017-07-26 19:49:10.0","modifyAt":"2017-07-26 19:51:10.0","press1":"60","press2":"68","flow1":"355","flow2":"350","pupple":"0"}]
     */

    private int result;
    private String msg;
    private List<ObjBean> obj;
    private List<ObjBean> open;
    private List<ObjBean> collision;
    private List<PathInfo> info;

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

    public List<ObjBean> getObj() {
        return obj;
    }

    public void setObj(List<ObjBean> obj) {
        this.obj = obj;
    }

    public List<ObjBean> getOpen() {
        return open;
    }

    public void setOpen(List<ObjBean> open) {
        this.open = open;
    }

    public List<ObjBean> getCollision() {
        return collision;
    }

    public void setCollision(List<ObjBean> collision) {
        this.collision = collision;
    }

    public List<PathInfo> getInfo() {
        return info;
    }

    public void setInfo(List<PathInfo> info) {
        this.info = info;
    }

    public static class ObjBean {
        /**
         * transferRecordId : 74386
         * transfer_id : 456
         * type : 0
         * currentCity : 杭州
         * distance : 1
         * longitude : 118.23
         * latitude : 29.13
         * temperature : 2
         * power : 99
         * humidity : 88
         * dbStatus : N
         * createAt : 2017-07-26 19:49:10.0
         * modifyAt : 2017-07-26 19:49:10.0
         * press1 : 60
         * press2 : 68
         * flow1 : 355
         * flow2 : 350
         * pupple : 0
         */

        private int transferRecordId;
        private String transfer_id;
        private String type;
        private String currentCity;
        private String distance;
        private String longitude;
        private String latitude;
        private String temperature;
        private String power;
        private String humidity;
        private String dbStatus;
        private String createAt;
        private String modifyAt;
        private String press1;
        private String press2;
        private String flow1;
        private String flow2;
        private String pupple;
        private String collision;
        private String recordAt;
        private String open;

        public int getTransferRecordId() {
            return transferRecordId;
        }

        public void setTransferRecordId(int transferRecordId) {
            this.transferRecordId = transferRecordId;
        }

        public String getTransfer_id() {
            return transfer_id;
        }

        public void setTransfer_id(String transfer_id) {
            this.transfer_id = transfer_id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCurrentCity() {
            return currentCity;
        }

        public void setCurrentCity(String currentCity) {
            this.currentCity = currentCity;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
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

        public String getTemperature() {
            return temperature;
        }

        public void setTemperature(String temperature) {
            this.temperature = temperature;
        }

        public String getPower() {
            return power;
        }

        public void setPower(String power) {
            this.power = power;
        }

        public String getHumidity() {
            return humidity;
        }

        public void setHumidity(String humidity) {
            this.humidity = humidity;
        }

        public String getDbStatus() {
            return dbStatus;
        }

        public void setDbStatus(String dbStatus) {
            this.dbStatus = dbStatus;
        }

        public String getCreateAt() {
            return createAt;
        }

        public void setCreateAt(String createAt) {
            this.createAt = createAt;
        }

        public String getModifyAt() {
            return modifyAt;
        }

        public void setModifyAt(String modifyAt) {
            this.modifyAt = modifyAt;
        }

        public String getPress1() {
            return press1;
        }

        public void setPress1(String press1) {
            this.press1 = press1;
        }

        public String getPress2() {
            return press2;
        }

        public void setPress2(String press2) {
            this.press2 = press2;
        }

        public String getFlow1() {
            return flow1;
        }

        public void setFlow1(String flow1) {
            this.flow1 = flow1;
        }

        public String getFlow2() {
            return flow2;
        }

        public void setFlow2(String flow2) {
            this.flow2 = flow2;
        }

        public String getPupple() {
            return pupple;
        }

        public void setPupple(String pupple) {
            this.pupple = pupple;
        }

        public String getCollision() {
            return collision;
        }

        public void setCollision(String collision) {
            this.collision = collision;
        }

        public String getRecordAt() {
            return recordAt;
        }

        public void setRecordAt(String recordAt) {
            this.recordAt = recordAt;
        }

        public String getOpen() {
            return open;
        }

        public void setOpen(String open) {
            this.open = open;
        }


    }
}
