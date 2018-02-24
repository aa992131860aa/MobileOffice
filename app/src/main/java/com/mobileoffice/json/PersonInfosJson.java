package com.mobileoffice.json;

import java.util.List;

/**
 * Created by 99213 on 2017/7/24.
 */

public class PersonInfosJson {

    /**
     * result : 0
     * msg : 获取人员信息成功
     * obj : [{"wechat_name":"fantasy","wechat_url":"http://wx.qlogo.cn/mmhead/gwhELYibibFdRqTkyfkutX7eErvRvFDY6rU1UhFiaE6s5qYoX9yRgPUZw/0","bind":"0","true_name":"陈杨","phone":"18398850872","is_upload_photo":"1","usersId":"67","photo_url":URL.TOMCAT_SOCKET+"images/20170719141359.jpg","token":"ewPQHy4MxOGRi3VSB79nxIHG+QwJkKfEJI+L+SinK3aI6V33aWI+GygcGRacrY6edBPchgeqtjgVXD5/O1k83BqrebQ1dVA5","hospital_name":"树兰（杭州）医院"},{"wechat_name":"fantasy","wechat_url":"http://wx.qlogo.cn/mmhead/gwhELYibibFdRqTkyfkutX7eErvRvFDY6rU1UhFiaE6s5qYoX9yRgPUZw/0","bind":"0","true_name":"陈杨","phone":"18398850872","is_upload_photo":"1","usersId":"67","photo_url":URL.TOMCAT_SOCKET+"images/20170719141359.jpg","token":"ewPQHy4MxOGRi3VSB79nxIHG+QwJkKfEJI+L+SinK3aI6V33aWI+GygcGRacrY6edBPchgeqtjgVXD5/O1k83BqrebQ1dVA5","hospital_name":"树兰（杭州）医院"},{"wechat_name":"fantasy","wechat_url":"http://wx.qlogo.cn/mmhead/gwhELYibibFdRqTkyfkutX7eErvRvFDY6rU1UhFiaE6s5qYoX9yRgPUZw/0","bind":"0","true_name":"陈杨","phone":"18398850872","is_upload_photo":"1","usersId":"67","photo_url":URL.TOMCAT_SOCKET+"images/20170719141359.jpg","token":"ewPQHy4MxOGRi3VSB79nxIHG+QwJkKfEJI+L+SinK3aI6V33aWI+GygcGRacrY6edBPchgeqtjgVXD5/O1k83BqrebQ1dVA5","hospital_name":"树兰（杭州）医院"},{"wechat_name":"fantasy","wechat_url":"http://wx.qlogo.cn/mmhead/gwhELYibibFdRqTkyfkutX7eErvRvFDY6rU1UhFiaE6s5qYoX9yRgPUZw/0","bind":"0","true_name":"陈杨","phone":"18398850872","is_upload_photo":"1","usersId":"67","photo_url":URL.TOMCAT_SOCKET+"images/20170719141359.jpg","token":"ewPQHy4MxOGRi3VSB79nxIHG+QwJkKfEJI+L+SinK3aI6V33aWI+GygcGRacrY6edBPchgeqtjgVXD5/O1k83BqrebQ1dVA5","hospital_name":"树兰（杭州）医院"},{"wechat_name":"fantasy","wechat_url":"http://wx.qlogo.cn/mmhead/gwhELYibibFdRqTkyfkutX7eErvRvFDY6rU1UhFiaE6s5qYoX9yRgPUZw/0","bind":"0","true_name":"陈杨","phone":"18398850872","is_upload_photo":"1","usersId":"67","photo_url":URL.TOMCAT_SOCKET+"images/20170719141359.jpg","token":"ewPQHy4MxOGRi3VSB79nxIHG+QwJkKfEJI+L+SinK3aI6V33aWI+GygcGRacrY6edBPchgeqtjgVXD5/O1k83BqrebQ1dVA5","hospital_name":"树兰（杭州）医院"}]
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
         * wechat_name : fantasy
         * wechat_url : http://wx.qlogo.cn/mmhead/gwhELYibibFdRqTkyfkutX7eErvRvFDY6rU1UhFiaE6s5qYoX9yRgPUZw/0
         * bind : 0
         * true_name : 陈杨
         * phone : 18398850872
         * is_upload_photo : 1
         * usersId : 67
         * photo_url : http://116.62.28.28:8080/transbox/images/20170719141359.jpg
         * token : ewPQHy4MxOGRi3VSB79nxIHG+QwJkKfEJI+L+SinK3aI6V33aWI+GygcGRacrY6edBPchgeqtjgVXD5/O1k83BqrebQ1dVA5
         * hospital_name : 树兰（杭州）医院
         */

        private String wechat_name;
        private String wechat_url;
        private String bind;
        private String true_name;
        private String phone;
        private String is_upload_photo;
        private String usersId;
        private String photo_url;
        private String token;
        private String hospital_name;
        private String usersOtherId;

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

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getIs_upload_photo() {
            return is_upload_photo;
        }

        public void setIs_upload_photo(String is_upload_photo) {
            this.is_upload_photo = is_upload_photo;
        }

        public String getUsersId() {
            return usersId;
        }

        public void setUsersId(String usersId) {
            this.usersId = usersId;
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

        public String getUsersOtherId() {
            return usersOtherId;
        }

        public void setUsersOtherId(String usersOtherId) {
            this.usersOtherId = usersOtherId;
        }
    }
}
