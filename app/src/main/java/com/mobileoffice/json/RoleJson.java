package com.mobileoffice.json;

/**
 * Created by 99213 on 2017/8/18.
 */

public class RoleJson {

    /**
     * result : 0
     * msg : 获取角色列表成功
     * obj : {"roleId":2,"roleName":"医生"}
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
         * roleId : 2
         * roleName : 医生
         */

        private int roleId = -1;
        private String roleName;

        public int getRoleId() {
            return roleId;
        }

        public void setRoleId(int roleId) {
            this.roleId = roleId;
        }

        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }
    }
}
