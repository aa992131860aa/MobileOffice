package com.mobileoffice.json;

import java.util.List;

/**
 * Created by 99213 on 2017/12/7.
 */

public class WorkloadAllJson {

    /**
     * result : 0
     * obj : [{"time":"2017-11","trueName":"唐","photoUrl":URL.TOMCAT_SOCKET+"images/20171025154715.jpg","roleName":"科室协调员","phone":"15336568476","workloads":[{"postRole":"转运医师","count":1}]},{"time":"2017-11","trueName":"张三","photoUrl":URL.TOMCAT_SOCKET+"images/20171121141311.jpg","roleName":"医生","phone":"17606505795","workloads":[{"postRole":"转运医师","count":6}]},{"time":"2017-11","trueName":"张绍康","photoUrl":"http://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTIo4zDs4WvnMHZMY5TQlxTu7q6HfrA2X44hNSW8cxf2U4x83vaAn7zOWqHAaqsvvV7Wlpmer2RtWQ/0","roleName":"管理员","phone":"18639805480","workloads":[{"postRole":"转运医师","count":1}]},{"time":"2017-11","trueName":"汪荣","photoUrl":URL.TOMCAT_SOCKET+"images/20171020153418.jpg","roleName":"管理员","phone":"13738106091","workloads":[{"postRole":"转运医师","count":8},{"postRole":"转运医师","count":8}]},{"time":"2017-11","trueName":"黄光涛","photoUrl":URL.TOMCAT_SOCKET+"images/20170801184228.jpg","roleName":"医生","phone":"13732278960","workloads":[{"postRole":"转运医师","count":1}]},{"time":"2017-11","trueName":"黄涛","photoUrl":URL.TOMCAT_SOCKET+"images/20170908164107.jpg","roleName":"医生","phone":"15355481205","workloads":[{"postRole":"转运医师","count":20}]}]
     */

    private int result;
    private List<ObjBean> obj;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public List<ObjBean> getObj() {
        return obj;
    }

    public void setObj(List<ObjBean> obj) {
        this.obj = obj;
    }

    public static class ObjBean {
        /**
         * time : 2017-11
         * trueName : 唐
         * photoUrl : http://116.62.28.28:8080/transbox/images/20171025154715.jpg
         * roleName : 科室协调员
         * phone : 15336568476
         * workloads : [{"postRole":"转运医师","count":1}]
         */

        private String time;
        private String trueName;
        private String photoUrl;
        private String roleName;
        private String phone;
        private List<Workload> workloads;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getTrueName() {
            return trueName;
        }

        public void setTrueName(String trueName) {
            this.trueName = trueName;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }

        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public List<Workload> getWorkloads() {
            return workloads;
        }

        public void setWorkloads(List<Workload> workloads) {
            this.workloads = workloads;
        }
    }
}
