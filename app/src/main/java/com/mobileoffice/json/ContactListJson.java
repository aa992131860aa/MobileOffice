package com.mobileoffice.json;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 99213 on 2017/6/29.
 */

public class ContactListJson {


    /**
     * result : 0
     * msg : 插入联系人成功
     * obj : [{"usersId":"66","trueName":"张绍康","contactPhone":"18639805480","isUploadPhoto":"0","photoFile":URL.TOMCAT_SOCKET+"images/start.png","wechatUrl":"http://wx.qlogo.cn/mmopen/6gC9cndE2XxwszT8K8ZItia22rWHZTEOmj5aSSIc47eYlUAZeg0GcpbibiawXnib0b8ajSibnsCicwlYKJia22Th3A2fD2FMJcJ5WvX/0"},{"usersId":"53","trueName":"张小康","contactPhone":"18398850875","isUploadPhoto":"0","photoFile":URL.TOMCAT_SOCKET+"images/start.png","wechatUrl":"http://wx.qlogo.cn/mmopen/vjR4uslsUIKQ4vPCr8Uq2AGBn7ibj59Mv7zDL9iaibW4gr1VoutIvWOQYDvUy3dEPiczibFbWsUbLC7dZfCjh6tILPj3VQur9279D/0"}]
     */

    private int result;
    private String msg;
    private List<ObjBean> obj;

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

    public static class ObjBean implements Serializable{
        /**
         * usersId : 66
         * trueName : 张绍康
         * contactPhone : 18639805480
         * isUploadPhoto : 0
         * photoFile : http://116.62.28.28:8080/transbox/images/start.png
         * wechatUrl : http://wx.qlogo.cn/mmopen/6gC9cndE2XxwszT8K8ZItia22rWHZTEOmj5aSSIc47eYlUAZeg0GcpbibiawXnib0b8ajSibnsCicwlYKJia22Th3A2fD2FMJcJ5WvX/0
         */

        private String usersId;
        private String trueName;
        private String contactPhone;
        private String isUploadPhoto;
        private String photoFile;
        private String wechatUrl;
        private String PinYin;
        private String FirstPinYin;
        private String phone;
        private String name;
        private int roleId;
        private String postRole;

        public String getUsersId() {
            return usersId;
        }

        public void setUsersId(String usersId) {
            this.usersId = usersId;
        }

        public String getPinYin() {
            return PinYin;
        }

        public void setPinYin(String pinYin) {
            PinYin = pinYin;
        }

        public String getFirstPinYin() {
            return FirstPinYin;
        }

        public void setFirstPinYin(String firstPinYin) {
            FirstPinYin = firstPinYin;
        }

        public String getTrueName() {
            return trueName;
        }

        public void setTrueName(String trueName) {
            this.trueName = trueName;
        }

        public String getContactPhone() {
            return contactPhone;
        }

        public void setContactPhone(String contactPhone) {
            this.contactPhone = contactPhone;
        }

        public String getIsUploadPhoto() {
            return isUploadPhoto;
        }

        public void setIsUploadPhoto(String isUploadPhoto) {
            this.isUploadPhoto = isUploadPhoto;
        }

        public String getPhotoFile() {
            return photoFile;
        }

        public void setPhotoFile(String photoFile) {
            this.photoFile = photoFile;
        }

        public String getWechatUrl() {
            return wechatUrl;
        }

        public void setWechatUrl(String wechatUrl) {
            this.wechatUrl = wechatUrl;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getRoleId() {
            return roleId;
        }

        public void setRoleId(int roleId) {
            this.roleId = roleId;
        }

        public String getPostRole() {
            return postRole;
        }

        public void setPostRole(String postRole) {
            this.postRole = postRole;
        }

        @Override
        public String toString() {
            return "ObjBean{" +
                    "usersId='" + usersId + '\'' +
                    ", trueName='" + trueName + '\'' +
                    ", contactPhone='" + contactPhone + '\'' +
                    ", isUploadPhoto='" + isUploadPhoto + '\'' +
                    ", photoFile='" + photoFile + '\'' +
                    ", wechatUrl='" + wechatUrl + '\'' +
                    ", PinYin='" + PinYin + '\'' +
                    ", FirstPinYin='" + FirstPinYin + '\'' +
                    ", phone='" + phone + '\'' +
                    ", name='" + name + '\'' +
                    ", roleId=" + roleId +
                    ", postRole='" + postRole + '\'' +
                    '}';
        }
    }
}
