package com.mobileoffice.json;

import java.util.List;

/**
 * Created by 99213 on 2017/7/16.
 */

public class TransferHistoryJson {

    /**
     * result : 0
     * msg : 获取转运中的信息
     * obj : [{"transferid":"352","organSeg":"1236","openPsd":"1236","fromCity":"杭州市江干区","toHospName":"新疆自治区人民医院","tracfficType":"骑车","tracfficNumber":"","organ":"心脏","organNum":"1","blood":"A","bloodNum":"1","sampleOrgan":"脾脏","sampleOrganNum":"1","opoName":"树兰(杭州)医院OPO","contactName":"唐金梅","contactPhone":"15336568476","phone":"18398850872","trueName":"fantasy","getTime":"2017-07-11 09:56","isStart":"0","distance":"3235.5644","toHosp":"杭州市"},{"transferid":"353","organSeg":"1237","openPsd":"","fromCity":"杭州市江干区","toHospName":"树兰（杭州）医院","tracfficType":"火车","tracfficNumber":"","organ":"肝","organNum":"1","blood":"A","bloodNum":"1","sampleOrgan":"脾脏","sampleOrganNum":"1","opoName":"树兰(杭州)医院OPO","contactName":"唐金梅","contactPhone":"15336568476","phone":"18398850872","trueName":"fantasy","getTime":"2017-07-10 10:02","isStart":"0","distance":"8.6142","toHosp":"杭州市"}]
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

    public static class ObjBean {
        /**
         * transferid : 352
         * organSeg : 1236
         * openPsd : 1236
         * fromCity : 杭州市江干区
         * toHospName : 新疆自治区人民医院
         * tracfficType : 骑车
         * tracfficNumber :
         * organ : 心脏
         * organNum : 1
         * blood : A
         * bloodNum : 1
         * sampleOrgan : 脾脏
         * sampleOrganNum : 1
         * opoName : 树兰(杭州)医院OPO
         * contactName : 唐金梅
         * contactPhone : 15336568476
         * phone : 18398850872
         * trueName : fantasy
         * getTime : 2017-07-11 09:56
         * isStart : 0
         * distance : 3235.5644
         * toHosp : 杭州市
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
        private String toHosp;
        private String startLong;
        private String startLati;
        private String endLong;
        private String endLati;
        private String boxNo;
        private String status;
        private String nowDistance;
        private String time;
        private String modifyOrganSeg;
        private int autoTransfer;


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

        public String getToHosp() {
            return toHosp;
        }

        public void setToHosp(String toHosp) {
            this.toHosp = toHosp;
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
    }
}
