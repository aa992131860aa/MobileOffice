package com.mobileoffice.entity;

/**
 * Created by 99213 on 2017/5/26.
 */

public class Transfer {
    private String transferid;
    private String transferNumber;
    private int organCount;
    private String boxPin;
    private String fromCity;
    private String toHospName;
    private String tracfficType;
    private String deviceType;
    private String getOrganAt;
    private String startAt;
    private String endAt;
    private String status;
    private String createAt;
    private String modifyAt;
    private BoxInfo boxInfo;
    private OrganInfo organInfo;
    private ToHospitalInfo toHospitalInfo;
    private TransferPersonInfo transferPersonInfo;
    private OpoInfo opoInfo;

    public String getTransferid() {
        return transferid;
    }

    public void setTransferid(String transferid) {
        this.transferid = transferid;
    }

    public String getTransferNumber() {
        return transferNumber;
    }

    public void setTransferNumber(String transferNumber) {
        this.transferNumber = transferNumber;
    }

    public int getOrganCount() {
        return organCount;
    }

    public void setOrganCount(int organCount) {
        this.organCount = organCount;
    }

    public String getBoxPin() {
        return boxPin;
    }

    public void setBoxPin(String boxPin) {
        this.boxPin = boxPin;
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

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getGetOrganAt() {
        return getOrganAt;
    }

    public void setGetOrganAt(String getOrganAt) {
        this.getOrganAt = getOrganAt;
    }

    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    public String getEndAt() {
        return endAt;
    }

    public void setEndAt(String endAt) {
        this.endAt = endAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public BoxInfo getBoxInfo() {
        return boxInfo;
    }

    public void setBoxInfo(BoxInfo boxInfo) {
        this.boxInfo = boxInfo;
    }

    public OrganInfo getOrganInfo() {
        return organInfo;
    }

    public void setOrganInfo(OrganInfo organInfo) {
        this.organInfo = organInfo;
    }

    public ToHospitalInfo getToHospitalInfo() {
        return toHospitalInfo;
    }

    public void setToHospitalInfo(ToHospitalInfo toHospitalInfo) {
        this.toHospitalInfo = toHospitalInfo;
    }

    public TransferPersonInfo getTransferPersonInfo() {
        return transferPersonInfo;
    }

    public void setTransferPersonInfo(TransferPersonInfo transferPersonInfo) {
        this.transferPersonInfo = transferPersonInfo;
    }

    public OpoInfo getOpoInfo() {
        return opoInfo;
    }

    public void setOpoInfo(OpoInfo opoInfo) {
        this.opoInfo = opoInfo;
    }
}
