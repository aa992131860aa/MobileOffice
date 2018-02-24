package com.mobileoffice.json;

import java.util.List;

/**
 * Created by 99213 on 2017/9/5.
 */

public class PdfInfoDetailJson {

    /**
     * result : 0
     * msg : 加载成功
     * obj : [{"pdfInfoId":3,"url":"http://116.62.28.28:8080/transbox/download\\5195648.pdf","organSeg":"5195648","phone":"17606505795","createTime":"2017-09-05 11:09:18","pdfSize":65},{"pdfInfoId":4,"url":"http://116.62.28.28:8080/transbox/download\\7994946.pdf","organSeg":"7994946","phone":"17606505795","createTime":"2017-09-05 11:07:38","pdfSize":66}]
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
         * pdfInfoId : 3
         * url : http://116.62.28.28:8080/transbox/download\5195648.pdf
         * organSeg : 5195648
         * phone : 17606505795
         * createTime : 2017-09-05 11:09:18
         * pdfSize : 65
         */

        private int pdfInfoId;
        private String url;
        private String organSeg;
        private String phone;
        private String createTime;
        private int pdfSize;
        private String organ;

        public int getPdfInfoId() {
            return pdfInfoId;
        }

        public void setPdfInfoId(int pdfInfoId) {
            this.pdfInfoId = pdfInfoId;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getOrganSeg() {
            return organSeg;
        }

        public void setOrganSeg(String organSeg) {
            this.organSeg = organSeg;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getOrgan() {
            return organ;
        }

        public void setOrgan(String organ) {
            this.organ = organ;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public int getPdfSize() {
            return pdfSize;
        }

        public void setPdfSize(int pdfSize) {
            this.pdfSize = pdfSize;
        }
    }
}
