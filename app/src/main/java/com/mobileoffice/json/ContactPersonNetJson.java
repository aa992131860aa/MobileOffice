package com.mobileoffice.json;

import java.util.List;

/**
 * Created by 99213 on 2017/7/20.
 */

public class ContactPersonNetJson {

    /**
     * result : 0
     * msg : 转运中-上海-杭州-心脏转运组
     * obj : [{"usersId":"67","trueName":"陈杨","phone":"18398850872","isUploadPhoto":"1","photoFile":URL.TOMCAT_SOCKET+"images/20170719141359.jpg","wechatUrl":"http://wx.qlogo.cn/mmhead/gwhELYibibFdRqTkyfkutX7eErvRvFDY6rU1UhFiaE6s5qYoX9yRgPUZw/0","bind":"0","isCreate":"0"},{"usersId":"52","trueName":"卢小堂","phone":"18398850874","isUploadPhoto":"1","photoFile":URL.TOMCAT_SOCKET+"images/end.png","wechatUrl":"http://wx.qlogo.cn/mmopen/vjR4uslsUIKQ4vPCr8Uq2AGBn7ibj59Mv7zDL9iaibW4gr1VoutIvWOQYDvUy3dEPiczibFbWsUbLC7dZfCjh6tILPj3VQur9279D/0","bind":"0","isCreate":"1"}]
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

    public static class ObjBean {
        /**
         * usersId : 67
         * trueName : 陈杨
         * phone : 18398850872
         * isUploadPhoto : 1
         * photoFile : http://116.62.28.28:8080/transbox/images/20170719141359.jpg
         * wechatUrl : http://wx.qlogo.cn/mmhead/gwhELYibibFdRqTkyfkutX7eErvRvFDY6rU1UhFiaE6s5qYoX9yRgPUZw/0
         * bind : 0
         * isCreate : 0
         */

        private String usersId;
        private String trueName;
        private String phone;
        private String isUploadPhoto;
        private String photoFile;
        private String wechatUrl;
        private String bind;
        private String isCreate;

        public String getUsersId() {
            return usersId;
        }

        public void setUsersId(String usersId) {
            this.usersId = usersId;
        }

        public String getTrueName() {
            return trueName;
        }

        public void setTrueName(String trueName) {
            this.trueName = trueName;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
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

        public String getBind() {
            return bind;
        }

        public void setBind(String bind) {
            this.bind = bind;
        }

        public String getIsCreate() {
            return isCreate;
        }

        public void setIsCreate(String isCreate) {
            this.isCreate = isCreate;
        }
    }
}
