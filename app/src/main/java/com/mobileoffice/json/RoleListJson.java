package com.mobileoffice.json;

import java.util.List;

/**
 * Created by 99213 on 2017/8/18.
 */

public class RoleListJson {

    /**
     * result : 0
     * msg : 获取角色列表成功
     * obj : [{"roleId":1,"roleName":"管理员"},{"roleId":2,"roleName":"医生"},{"roleId":3,"roleName":"OPO协调员"},{"roleId":4,"roleName":"护士"}]
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
         * roleId : 1
         * roleName : 管理员
         */

        private int roleId;
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
