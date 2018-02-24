package com.mobileoffice.json;

import java.util.List;

/**
 * Created by 99213 on 2017/6/27.
 */

public class PushListJson {
    /**
     * result : 0
     * msg : 获取系统消息列表成功
     * obj : [{"id":53,"content":"sdasd","createTime":"06-26 10:10"},{"id":52,"content":"ss","type":"system","createTime":"06-26 10:10"},{"id":51,"content":"系统消息","type":"system","createTime":"06-26 10:10"},{"id":46,"content":"唐金梅 请求添加好友","type":"add","createTime":"06-26 10:10"},{"id":45,"content":"唐金梅 请求添加好友","type":"add","createTime":"06-26 10:05"},{"id":44,"content":"唐金梅 请求添加好友","type":"add","createTime":"06-26 10:02"},{"id":43,"content":"唐金梅 请求添加好友","type":"add","createTime":"06-26 09:53"},{"id":42,"content":"唐金梅 请求添加好友","type":"add","createTime":"06-26 09:42"}]
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
         * id : 53
         * content : sdasd
         * createTime : 06-26 10:10
         * type : system
         */

        private int id;
        private String phone;
        private String content;
        private String createTime;
        private String type;
        private String otherId;
        private String trueName;
        private String hospital;
        private String photoFile;
        private String wechatUrl;
        private String isUploadPhoto;
        private String transferId;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getOtherId() {
            return otherId;
        }

        public void setOtherId(String otherId) {
            this.otherId = otherId;
        }

        public String getTrueName() {
            return trueName;
        }

        public void setTrueName(String trueName) {
            this.trueName = trueName;
        }

        public String getHospital() {
            return hospital;
        }

        public void setHospital(String hospital) {
            this.hospital = hospital;
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

        public String getIsUploadPhoto() {
            return isUploadPhoto;
        }

        public void setIsUploadPhoto(String isUploadPhoto) {
            this.isUploadPhoto = isUploadPhoto;
        }

        public String getTransferId() {
            return transferId;
        }

        public void setTransferId(String transferId) {
            this.transferId = transferId;
        }

        @Override
        public String toString() {
            return "ObjBean{" +
                    "id=" + id +
                    ", phone='" + phone + '\'' +
                    ", content='" + content + '\'' +
                    ", createTime='" + createTime + '\'' +
                    ", type='" + type + '\'' +
                    ", otherId='" + otherId + '\'' +
                    ", trueName='" + trueName + '\'' +
                    ", hospital='" + hospital + '\'' +
                    ", photoFile='" + photoFile + '\'' +
                    ", wechatUrl='" + wechatUrl + '\'' +
                    ", isUploadPhoto='" + isUploadPhoto + '\'' +
                    ", transferId='" + transferId + '\'' +
                    '}';
        }
    }
}
