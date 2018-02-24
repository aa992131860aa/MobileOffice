package com.mobileoffice.json;

/**
 * Created by 99213 on 2017/6/17.
 */

public class WechatJson {

    /**
     * result : 0
     * msg : 登录认证成功
     * obj : {"wechat_name":"fantasy","wechat_url":"http://wx.qlogo.cn/mmopen/vjR4uslsUIKQ4vPCr8Uq2AGBn7ibj59Mv7zDL9iaibW4gr1VoutIvWOQYDvUy3dEPiczibFbWsUbLC7dZfCjh6tILPj3VQur9279D/0","bind":"0","true_name":"陈杨","is_upload_photo":"1","photo_url":"http://192.168.1.5:8080/transbox/images/20170625180756.png","token":"SNPrQAl9seQfT7EhL1mkizF0LOtfnojkMz5JSfRserOwT2qw3OqDGslnXihSiZMKtNAZyM+liswL6myvWXrwTo8hw2AxBFIx","hospital_name":"南京军区第117医院","status":"0"}
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

    public static class ObjBean {
        /**
         * wechat_name : fantasy
         * wechat_url : http://wx.qlogo.cn/mmopen/vjR4uslsUIKQ4vPCr8Uq2AGBn7ibj59Mv7zDL9iaibW4gr1VoutIvWOQYDvUy3dEPiczibFbWsUbLC7dZfCjh6tILPj3VQur9279D/0
         * bind : 0
         * true_name : 陈杨
         * is_upload_photo : 1
         * photo_url : http://192.168.1.5:8080/transbox/images/20170625180756.png
         * token : SNPrQAl9seQfT7EhL1mkizF0LOtfnojkMz5JSfRserOwT2qw3OqDGslnXihSiZMKtNAZyM+liswL6myvWXrwTo8hw2AxBFIx
         * hospital_name : 南京军区第117医院
         * status : 0
         */

        private String wechat_name;
        private String wechat_url;
        private String bind;
        private String true_name;
        private String is_upload_photo;
        private String photo_url;
        private String token;
        private String hospital_name;
        private String status;
        private String phone;
        private String is_update;
        private String roleName;
        private String role_id;
        private String create_time;
        private String temperatureStatus;
        private String openStatus;
        private String collisionStatus;
        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getWechat_name() {
            return wechat_name;
        }

        public void setWechat_name(String wechat_name) {
            this.wechat_name = wechat_name;
        }

        public String getWechat_url() {
            return wechat_url;
        }

        public void setWechat_url(String wechat_url) {
            this.wechat_url = wechat_url;
        }

        public String getBind() {
            return bind;
        }

        public void setBind(String bind) {
            this.bind = bind;
        }

        public String getTrue_name() {
            return true_name;
        }

        public void setTrue_name(String true_name) {
            this.true_name = true_name;
        }

        public String getIs_upload_photo() {
            return is_upload_photo;
        }

        public void setIs_upload_photo(String is_upload_photo) {
            this.is_upload_photo = is_upload_photo;
        }

        public String getPhoto_url() {
            return photo_url;
        }

        public void setPhoto_url(String photo_url) {
            this.photo_url = photo_url;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getHospital_name() {
            return hospital_name;
        }

        public void setHospital_name(String hospital_name) {
            this.hospital_name = hospital_name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getIs_update() {
            return is_update;
        }

        public void setIs_update(String is_update) {
            this.is_update = is_update;
        }

        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }

        public String getRole_id() {
            return role_id;
        }

        public void setRole_id(String role_id) {
            this.role_id = role_id;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getTemperatureStatus() {
            return temperatureStatus;
        }

        public void setTemperatureStatus(String temperatureStatus) {
            this.temperatureStatus = temperatureStatus;
        }

        public String getOpenStatus() {
            return openStatus;
        }

        public void setOpenStatus(String openStatus) {
            this.openStatus = openStatus;
        }

        public String getCollisionStatus() {
            return collisionStatus;
        }

        public void setCollisionStatus(String collisionStatus) {
            this.collisionStatus = collisionStatus;
        }
    }
}
