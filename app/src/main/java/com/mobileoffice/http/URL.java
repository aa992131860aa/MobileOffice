package com.mobileoffice.http;

public interface URL {
    String SOCKET = "http://116.62.28.28:1337";
    //String SOCKET = "http://192.168.1.16:1337";
    //home
    //String TOMCAT_SOCKET = "http://192.168.0.102:8080/transbox/";
    //String TOMCAT_SOCKET = "http://192.168.1.10:8080/transbox/";
    String TOMCAT_SOCKET = "http://www.lifeperfusor.com/transbox/";
    String TOMCAT_SOCKET_CHART = "http://116.62.28.28:8888/WebReport/ReportServer?";
    String BASE_URL = SOCKET + "/transbox/api/";

    String YIYUAN_APPID = "39696";
    String YIYUAN_SECRET = "6b56be2fabc149f5b17272c97e2a4c1c";
    //上传图像
    String PHOTO_URL = TOMCAT_SOCKET + "upload_file.do";
    //发送验证码
    String SEND_VERIFICATION = TOMCAT_SOCKET + "sms.do";
    //发送验证码
    String SMS = TOMCAT_SOCKET + "sendSms.do";
    //医院
    String USER = TOMCAT_SOCKET + "user.do";
    //融云
    String RONG = TOMCAT_SOCKET + "rong.do";
    //好友
    String CONTACT = TOMCAT_SOCKET + "contact.do";
    //转运
    String TRANSFER = TOMCAT_SOCKET + "transfer.do";
    //推送
    String PUSH = TOMCAT_SOCKET + "push.do";
    //天气
    String WEATHER = TOMCAT_SOCKET + "weather.do";
    //opo
    String OPO = TOMCAT_SOCKET + "opo.do";
    //关于我的
    String ME = TOMCAT_SOCKET + "me.do";
    String UPLOAD = TOMCAT_SOCKET + "uploadApp.do";
    //转运详细记录
    String TRANSFER_RECORD = TOMCAT_SOCKET + "transferRecord.do";
    //根据地址后去经纬度
    String GAO_DE_LOCATION_URL = "http://restapi.amap.com/v3/geocode/geo?key=d1a4169090421ca9176490080f183a54&address=";
    String BOX = TOMCAT_SOCKET + "box.do";
    //好友默认图片
    String CONTACT_PERSON_PHOTO = TOMCAT_SOCKET + "images/contact_person.png";
    String STATISTICS = TOMCAT_SOCKET_CHART + "formlet=history_from.frm";
    //formlet=history_from.frm&op=h5  reportlet=history.cpt
    //String BASE_URL = "http://www.lifeperfusor.com/transbox/api/";
    //下载pdf地址
    String DOWNLOAD_PDF =TOMCAT_SOCKET+ "downloadPdf.do";

    //云巴推送地址
    String YUN_BA = "http://rest.yunba.io:8080";

    //工作量统计
    String WORKLOAD =  TOMCAT_SOCKET + "workload.do";

}
