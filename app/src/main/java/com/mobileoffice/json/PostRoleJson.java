package com.mobileoffice.json;

import java.util.List;

/**
 * Created by 99213 on 2017/9/24.
 */

public class PostRoleJson {

    /**
     * result : 0
     * msg : 获取省份成功
     * obj : {"id":0,"opoInfoId":2,"name":"北京大学第一医院OPO","opoInfoContacts":[{"contactName":"卢大明","contactPhone":"18398850879"},{"contactName":"卢明","contactPhone":"18398850879"}]}
     */

    private int result;
    private String msg;
    private List<PostRole> obj;

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

    public List<PostRole> getObj() {
        return obj;
    }

    public void setObj(List<PostRole> obj) {
        this.obj = obj;
    }

    public  class PostRole{
        private int postRoleId;
        private String postRole;
        public int getPostRoleId() {
            return postRoleId;
        }
        public void setPostRoleId(int postRoleId) {
            this.postRoleId = postRoleId;
        }
        public String getPostRole() {
            return postRole;
        }
        public void setPostRole(String postRole) {
            this.postRole = postRole;
        }
    }
}
