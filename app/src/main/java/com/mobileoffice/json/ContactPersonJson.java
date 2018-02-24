package com.mobileoffice.json;

import java.util.List;

/**
 * Created by 99213 on 2017/7/11.
 */

public class ContactPersonJson {

    /**
     * result : 0
     * msg : 获取所有群组成功
     * obj : [{"groupId":1,"usersIds":"57,66","organSeg":"1","groupName":"测试1","phone":"18639805480"},{"groupId":2,"usersIds":"1,23,66","organSeg":"12345","groupName":"第二单群","phone":"18639805480"},{"groupId":5,"usersIds":"1,57,66","organSeg":"18398850872","groupName":"测试1","phone":"18639805480"}]
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
         * groupId : 1
         * usersIds : 57,66
         * organSeg : 1
         * groupName : 测试1
         * phone : 18639805480
         */

        private int groupId;
        private String usersIds;
        private String organSeg;
        private String groupName;
        private String phone;
        private String createTime;

        public int getGroupId() {
            return groupId;
        }

        public void setGroupId(int groupId) {
            this.groupId = groupId;
        }

        public String getUsersIds() {
            return usersIds;
        }

        public void setUsersIds(String usersIds) {
            this.usersIds = usersIds;
        }

        public String getOrganSeg() {
            return organSeg;
        }

        public void setOrganSeg(String organSeg) {
            this.organSeg = organSeg;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
    }
}
