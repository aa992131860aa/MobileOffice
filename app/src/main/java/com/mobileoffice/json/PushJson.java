package com.mobileoffice.json;

/**
 * Created by 99213 on 2017/6/27.
 */

public class PushJson {


    /**
     * result : 0
     * msg : 获取未读数成功
     * obj : {"createTime":"06-26 10:10","count":8,"content":"sdasd"}
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
         * createTime : 06-26 10:10
         * count : 8
         * content : sdasd
         */

        private String createTime;
        private int count;
        private String content;

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
