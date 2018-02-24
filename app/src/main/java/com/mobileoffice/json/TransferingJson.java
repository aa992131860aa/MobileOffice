package com.mobileoffice.json;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 99213 on 2017/7/1.
 */

public class TransferingJson {

    /**
     * result : 0
     * msg : 获取转运中的信息
     * obj : [{"transferid":"279","organSeg":"54321","openPsd":"","fromCity":"杭州市滨江区","toHospName":"浙江大学医学院附属第二医院","tracfficType":"火车","tracfficNumber":"147","organ":"肝","organNum":"3","blood":"B","bloodNum":"2","sampleOrgan":"血管","sampleOrganNum":"1","opoName":"树兰(杭州)医院OPO","contactName":"陈杨","contactPhone":"18398850872","phone":"18639805480","trueName":"张绍康","getTime":"2017-07-01 09:14","isStart":"1"}]
     */

    private int result;
    private String msg;
    private List<TransferJson.ObjBean> obj;

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

    public List<TransferJson.ObjBean> getObj() {
        return obj;
    }

    public void setObj(List<TransferJson.ObjBean> obj) {
        this.obj = obj;
    }

    public static class ObjBean implements Serializable{
        /**
         * transferid : 279
         * organSeg : 54321
         * openPsd :
         * fromCity : 杭州市滨江区
         * toHospName : 浙江大学医学院附属第二医院
         * tracfficType : 火车
         * tracfficNumber : 147
         * organ : 肝
         * organNum : 3
         * blood : B
         * bloodNum : 2
         * sampleOrgan : 血管
         * sampleOrganNum : 1
         * opoName : 树兰(杭州)医院OPO
         * contactName : 陈杨
         * contactPhone : 18398850872
         * phone : 18639805480
         * trueName : 张绍康
         * getTime : 2017-07-01 09:14
         * isStart : 1
         */

        private String transferid;
        private String organSeg;
        private String openPsd;
        private String fromCity;
        private String toHospName;
        private String tracfficType;
        private String tracfficNumber;
        private String organ;
        private String organNum;
        private String blood;
        private String bloodNum;
        private String sampleOrgan;
        private String sampleOrganNum;
        private String opoName;
        private String contactName;
        private String contactPhone;
        private String phone;
        private String trueName;
        private String getTime;
        private String isStart;
        private String distance;
        private String startLong;
        private String startLati;
        private String endLong;
        private String endLati;
        private String toHosp;
        private String boxNo;
        private String oldOrganSeg;
        private String status;
        private String nowDistance;
        private String time;
        //opo人员
        private String opoContactName;
        private String opoContactPhone;
        //定位真实位置
        private String trueFromCity;
        //电话号码
        private String phones;
        private String phone1;
        private String phone2;
        private String pushException;

        private String trueUrl;
        private String contactUrl;
        private String opoContactUrl;

        private String modifyOrganSeg;
        private int autoTransfer;

        public String getPhone1() {
            return phone1;
        }

        public void setPhone1(String phone1) {
            this.phone1 = phone1;
        }

        public String getPhone2() {
            return phone2;
        }

        public void setPhone2(String phone2) {
            this.phone2 = phone2;
        }

        public String getTransferid() {
            return transferid;
        }

        public void setTransferid(String transferid) {
            this.transferid = transferid;
        }

        public String getOrganSeg() {
            return organSeg;
        }

        public void setOrganSeg(String organSeg) {
            this.organSeg = organSeg;
        }

        public String getOpenPsd() {
            return openPsd;
        }

        public void setOpenPsd(String openPsd) {
            this.openPsd = openPsd;
        }

        public String getFromCity() {
            return fromCity;
        }

        public void setFromCity(String fromCity) {
            this.fromCity = fromCity;
        }

        public String getToHospName() {
            return toHospName;
        }

        public void setToHospName(String toHospName) {
            this.toHospName = toHospName;
        }

        public String getTracfficType() {
            return tracfficType;
        }

        public void setTracfficType(String tracfficType) {
            this.tracfficType = tracfficType;
        }

        public String getTracfficNumber() {
            return tracfficNumber;
        }

        public void setTracfficNumber(String tracfficNumber) {
            this.tracfficNumber = tracfficNumber;
        }

        public String getOrgan() {
            return organ;
        }

        public void setOrgan(String organ) {
            this.organ = organ;
        }

        public String getOrganNum() {
            return organNum;
        }

        public void setOrganNum(String organNum) {
            this.organNum = organNum;
        }

        public String getBlood() {
            return blood;
        }

        public void setBlood(String blood) {
            this.blood = blood;
        }

        public String getBloodNum() {
            return bloodNum;
        }

        public void setBloodNum(String bloodNum) {
            this.bloodNum = bloodNum;
        }

        public String getSampleOrgan() {
            return sampleOrgan;
        }

        public void setSampleOrgan(String sampleOrgan) {
            this.sampleOrgan = sampleOrgan;
        }

        public String getSampleOrganNum() {
            return sampleOrganNum;
        }

        public void setSampleOrganNum(String sampleOrganNum) {
            this.sampleOrganNum = sampleOrganNum;
        }

        public String getOpoName() {
            return opoName;
        }

        public void setOpoName(String opoName) {
            this.opoName = opoName;
        }

        public String getContactName() {
            return contactName;
        }

        public void setContactName(String contactName) {
            this.contactName = contactName;
        }

        public String getContactPhone() {
            return contactPhone;
        }

        public void setContactPhone(String contactPhone) {
            this.contactPhone = contactPhone;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getTrueName() {
            return trueName;
        }

        public void setTrueName(String trueName) {
            this.trueName = trueName;
        }

        public String getGetTime() {
            return getTime;
        }

        public void setGetTime(String getTime) {
            this.getTime = getTime;
        }

        public String getIsStart() {
            return isStart;
        }

        public void setIsStart(String isStart) {
            this.isStart = isStart;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public String getStartLong() {
            return startLong;
        }

        public void setStartLong(String startLong) {
            this.startLong = startLong;
        }

        public String getStartLati() {
            return startLati;
        }

        public void setStartLati(String startLati) {
            this.startLati = startLati;
        }

        public String getEndLong() {
            return endLong;
        }

        public void setEndLong(String endLong) {
            this.endLong = endLong;
        }

        public String getEndLati() {
            return endLati;
        }

        public void setEndLati(String endLati) {
            this.endLati = endLati;
        }

        public String getToHosp() {
            return toHosp;
        }

        public void setToHosp(String toHosp) {
            this.toHosp = toHosp;
        }

        public String getBoxNo() {
            return boxNo;
        }

        public void setBoxNo(String boxNo) {
            this.boxNo = boxNo;
        }

        public String getOldOrganSeg() {
            return oldOrganSeg;
        }

        public void setOldOrganSeg(String oldOrganSeg) {
            this.oldOrganSeg = oldOrganSeg;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getNowDistance() {
            return nowDistance;
        }

        public void setNowDistance(String nowDistance) {
            this.nowDistance = nowDistance;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getOpoContactName() {
            return opoContactName;
        }

        public void setOpoContactName(String opoContactName) {
            this.opoContactName = opoContactName;
        }

        public String getOpoContactPhone() {
            return opoContactPhone;
        }

        public void setOpoContactPhone(String opoContactPhone) {
            this.opoContactPhone = opoContactPhone;
        }

        public String getTrueFromCity() {
            return trueFromCity;
        }

        public void setTrueFromCity(String trueFromCity) {
            this.trueFromCity = trueFromCity;
        }

        public String getPhones() {
            return phones;
        }

        public void setPhones(String phones) {
            this.phones = phones;
        }

        public String getPushException() {
            return pushException;
        }

        public void setPushException(String pushException) {
            this.pushException = pushException;
        }

        public String getTrueUrl() {
            return trueUrl;
        }

        public void setTrueUrl(String trueUrl) {
            this.trueUrl = trueUrl;
        }

        public String getContactUrl() {
            return contactUrl;
        }

        public void setContactUrl(String contactUrl) {
            this.contactUrl = contactUrl;
        }

        public String getOpoContactUrl() {
            return opoContactUrl;
        }

        public void setOpoContactUrl(String opoContactUrl) {
            this.opoContactUrl = opoContactUrl;
        }

        public String getModifyOrganSeg() {
            return modifyOrganSeg;
        }

        public void setModifyOrganSeg(String modifyOrganSeg) {
            this.modifyOrganSeg = modifyOrganSeg;
        }

        public int getAutoTransfer() {
            return autoTransfer;
        }

        public void setAutoTransfer(int autoTransfer) {
            this.autoTransfer = autoTransfer;
        }

        @Override
        public String toString() {
            return "ObjBean{" +
                    "transferid='" + transferid + '\'' +
                    ", organSeg='" + organSeg + '\'' +
                    ", openPsd='" + openPsd + '\'' +
                    ", fromCity='" + fromCity + '\'' +
                    ", toHospName='" + toHospName + '\'' +
                    ", tracfficType='" + tracfficType + '\'' +
                    ", tracfficNumber='" + tracfficNumber + '\'' +
                    ", organ='" + organ + '\'' +
                    ", organNum='" + organNum + '\'' +
                    ", blood='" + blood + '\'' +
                    ", bloodNum='" + bloodNum + '\'' +
                    ", sampleOrgan='" + sampleOrgan + '\'' +
                    ", sampleOrganNum='" + sampleOrganNum + '\'' +
                    ", opoName='" + opoName + '\'' +
                    ", contactName='" + contactName + '\'' +
                    ", contactPhone='" + contactPhone + '\'' +
                    ", phone='" + phone + '\'' +
                    ", trueName='" + trueName + '\'' +
                    ", getTime='" + getTime + '\'' +
                    ", isStart='" + isStart + '\'' +
                    ", distance='" + distance + '\'' +
                    ", startLong='" + startLong + '\'' +
                    ", startLati='" + startLati + '\'' +
                    ", endLong='" + endLong + '\'' +
                    ", endLati='" + endLati + '\'' +
                    ", toHosp='" + toHosp + '\'' +
                    ", boxNo='" + boxNo + '\'' +
                    ", oldOrganSeg='" + oldOrganSeg + '\'' +
                    ", status='" + status + '\'' +
                    ", nowDistance='" + nowDistance + '\'' +
                    ", time='" + time + '\'' +
                    ", opoContactName='" + opoContactName + '\'' +
                    ", opoContactPhone='" + opoContactPhone + '\'' +
                    ", trueFromCity='" + trueFromCity + '\'' +
                    ", phones='" + phones + '\'' +
                    ", phone1='" + phone1 + '\'' +
                    ", phone2='" + phone2 + '\'' +
                    ", pushException='" + pushException + '\'' +
                    ", trueUrl='" + trueUrl + '\'' +
                    ", contactUrl='" + contactUrl + '\'' +
                    ", opoContactUrl='" + opoContactUrl + '\'' +
                    '}';
        }
    }

}
