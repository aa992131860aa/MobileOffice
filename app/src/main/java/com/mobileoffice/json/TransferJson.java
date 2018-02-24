package com.mobileoffice.json;

import java.io.Serializable;

/**
 * Created by 99213 on 2017/7/13.
 */

public class TransferJson{


    /**
     * result : 0
     * msg : 获取转运中的信息
     * obj : {"transferid":"369","organSeg":"1260","openPsd":"1111","fromCity":"成都市","toHospName":"树兰（杭州）医院","tracfficType":"火车","tracfficNumber":"","organ":"肝","organNum":"1","blood":"A","bloodNum":"1","sampleOrgan":"脾脏","sampleOrganNum":"1","opoName":"上海市肺科医院OPO","contactName":"卢双堂","contactPhone":"18398850874","phone":"18398850872","trueName":"fantasy","getTime":"2017-07-12 21:46","isStart":"0","startLong":"1","startLati":"2","endLong":"3","endLati":"4"}
     */

    private int result;
    private String msg;
    private ObjBean obj;

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

    public ObjBean getObj() {
        return obj;
    }

    public void setObj(ObjBean obj) {
        this.obj = obj;
    }

    public static class ObjBean implements Serializable{
        /**
         * transferid : 369
         * organSeg : 1260
         * openPsd : 1111
         * fromCity : 成都市
         * toHospName : 树兰（杭州）医院
         * tracfficType : 火车
         * tracfficNumber :
         * organ : 肝
         * organNum : 1
         * blood : A
         * bloodNum : 1
         * sampleOrgan : 脾脏
         * sampleOrganNum : 1
         * opoName : 上海市肺科医院OPO
         * contactName : 卢双堂
         * contactPhone : 18398850874
         * phone : 18398850872
         * trueName : fantasy
         * getTime : 2017-07-12 21:46
         * isStart : 0
         * startLong : 1
         * startLati : 2
         * endLong : 3
         * endLati : 4
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
        private String startLong;
        private String startLati;
        private String endLong;
        private String endLati;
        private String distance;
        private String toHosp;
        private String boxNo;
        private String status;
        private String nowDistance;
        private String time;
        private String opoContactName;
        private String opoContactPhone;
        private String modifyOrganSeg;
        private int autoTransfer;
        private String oldOrganSeg;
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
        private int deviceStatus = 0;

        public int getDeviceStatus() {
            return deviceStatus;
        }

        public void setDeviceStatus(int deviceStatus) {
            this.deviceStatus = deviceStatus;
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

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
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

        public String getOldOrganSeg() {
            return oldOrganSeg;
        }

        public void setOldOrganSeg(String oldOrganSeg) {
            this.oldOrganSeg = oldOrganSeg;
        }
    }
}
