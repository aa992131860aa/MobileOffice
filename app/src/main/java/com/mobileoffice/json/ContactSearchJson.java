package com.mobileoffice.json;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 99213 on 2017/6/23.
 */

public class ContactSearchJson {

    /**
     * result : 0
     * msg : findFriends ok
     * obj : [{"id":49,"true_name":"张绍康","phone":"18639805480","photo_url":"http://192.168.1.5:8080/transbox/images/20170621105042.png","is_upload_photo":"0","name":"浙江大学医学院附属第二医院","is_friend":0}]
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
         * id : 49
         * true_name : 张绍康
         * phone : 18639805480
         * photo_url : http://192.168.1.5:8080/transbox/images/20170621105042.png
         * is_upload_photo : 0
         * name : 浙江大学医学院附属第二医院
         * is_friend : 0
         */

        private int other_id;
        private String true_name;
        private String phone;
        private String photo_url;
        private String is_upload_photo;
        private String wechat_url;
        private String name;
        private int is_friend;
        private String bind;

        public int getOther_id() {
            return other_id;
        }

        public void setOther_id(int other_id) {
            this.other_id = other_id;
        }

        public String getTrue_name() {
            return true_name;
        }

        public void setTrue_name(String true_name) {
            this.true_name = true_name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPhoto_url() {
            return photo_url;
        }

        public void setPhoto_url(String photo_url) {
            this.photo_url = photo_url;
        }

        public String getIs_upload_photo() {
            return is_upload_photo;
        }

        public void setIs_upload_photo(String is_upload_photo) {
            this.is_upload_photo = is_upload_photo;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getIs_friend() {
            return is_friend;
        }

        public void setIs_friend(int is_friend) {
            this.is_friend = is_friend;
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

        @Override
        public String toString() {
            return "ObjBean{" +
                    "other_id=" + other_id +
                    ", true_name='" + true_name + '\'' +
                    ", phone='" + phone + '\'' +
                    ", photo_url='" + photo_url + '\'' +
                    ", is_upload_photo='" + is_upload_photo + '\'' +
                    ", wechat_url='" + wechat_url + '\'' +
                    ", name='" + name + '\'' +
                    ", is_friend=" + is_friend +
                    ", bind='" + bind + '\'' +
                    '}';
        }
    }
}
