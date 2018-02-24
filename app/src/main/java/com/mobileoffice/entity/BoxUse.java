package com.mobileoffice.entity;

/**
 * Created by 99213 on 2018/1/10.
 */

public class BoxUse {
    private int boxId;
    private String boxNo;
    private String transferStatus;
    private String status;
    public int getBoxId() {
        return boxId;
    }
    public void setBoxId(int boxId) {
        this.boxId = boxId;
    }
    public String getBoxNo() {
        return boxNo;
    }
    public void setBoxNo(String boxNo) {
        this.boxNo = boxNo;
    }
    public String getTransferStatus() {
        return transferStatus;
    }
    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
