package com.mobileoffice.utils;

import android.widget.RelativeLayout;

import com.mobileoffice.entity.PathInfo;
import com.mobileoffice.json.ContactListJson;
import com.mobileoffice.json.TransferJson;
import com.mobileoffice.json.TransferRecordJson;

import java.util.ArrayList;

/**
 * Created by cy on 2015/8/31.
 */
public class CONSTS {
    //mainActivity的广播常量
    public static String MAIN_ACTION = "com.mobileoffice.main";
    public static String TRANSFER_ACTION = "com.mobileoffice.transfer";
    public static String NO_START = "transferStatus=1";
    public static String START = "transferStatus=2";
    public static String STOP = "transferStatus=3";
    public static String DELETE = "transferStatus=4";

    //联系人
    public static ArrayList<ContactListJson.ObjBean> CONTACT_LIST = new ArrayList<>();
    //priview的联系人
    public static ArrayList<ContactListJson.ObjBean> CONTACT_LIST_PREVIEW = new ArrayList<>();
    public static ArrayList<Integer> CONTACT_POSITION = new ArrayList<>();
    public static int PREVIEW_FLAG = 0;
    //新品上市时间
    public final static String NEW_PRODUCT_DAY = "90";
    //医院名称
    public static String HOSPITAL_NAME = "";
    //医院名称
    public static String HOSPITAL_NAME_TRANSFER = "";
    public static String HOSPITAL_NAME_TRANSFER_NAME = "";
    public static String HOSPITAL_NAME_TRANSFER_PHONE = "";
    //登录时间
    public static long LOGIN_TIME = 0;
    public static int pageSize = 20;
    public static int curPageNO = 0;
    //自动开始的标识，0为完全的转运，1为不完全的转运
    public static int AURO_TRANSFER_FINISH = 0;
    public static int AURO_TRANSFER_FINISH_NO = 1;
    //系统消息未读数
    public static int UNREAD_PUSH_NUM = 0;
    //最新的系统消息
    public static String NEW_SYSTEM_MESSAGE = "";
    //最新的系统时间
    public static String NEW_SYSTEM_TIME = "";

    //weiview的高度
    public static int VP_HEIGHT = 0;

    //显示的日期个数
    public static int DATE_SMALL_SIZE = 5;
    public static int DATE_LARGET_SIZE = 10;

    public static int SMALL_NUM_SIZE = 10;
    public static int LARGET_NUM_SIZE = 20;

    //分页 分页的大小
    public static int PAGE_SIZE = 20;
    //转运状态
    public static final String TRANSFERING = "transfering";
    //转运的修改和新建 0新建 1修改
    public static int TRANSFER_STATUS = 0;
    public static final String DOWN = "down";
    public static final int LOAD_ERROR = 0;
    public static final int LOAD_CONTINUE = 1;
    public static final int LOAD_FINISH = 2;
    public static final String WECHAT_APP_ID = "wx5c063ed0e9370b6a";
    public static final String WECHAT_APP_SECRET = "635a176fbef8fc8577e8fbe6d2f17c2b";
    public final static int SEND_OK = 0;
    public final static int SEND_FAIL = 1;
    public final static int BAD_PARAM = 2;
    public final static int NO_REGISTER = 4;
    //修改的转运,预览界面
    public  static TransferJson.ObjBean MODIFY_TRANSFER ;
    //器官段号
    public static String ORGAN_SEG = "";
    //判断是否为消息点击
    public static boolean IS_NEWS = false;
    public static RelativeLayout NO_START_VIEW = null;
    public static int IS_START = 0;
    //0 为微信登录    1为微信分享
    public static int WECHAT_SHARE_LOGIN = 0;

    //好友列表
    //填写的类型
    public static String TYPE = "scan";

    public static boolean IS_HISTORY_MODIFY = false;

    //心脏跳动
    public static boolean HEART_START = true;
    //淡入淡出
    public static boolean TEXT_PUSH = true;
    //管理员
    public static int MANAGER = 1;
    //角色 医生
    public static int ROLE_DOCTOR = 2;
    //角色 OPO协调员
    public static int ROLE_OPO = 3;

    //角色 护士
    public static int ROLE_NURSE = 5;

    //是否开启自动更新
    public static boolean IS_REFRESH = true;
    //自动更新刷新时间
    public static int REFRESH_TIME = 1000 * 60 * 2;

    public static ArrayList<TransferRecordJson.ObjBean> OPEN_RECORDS = new ArrayList<>();
    public static ArrayList<TransferRecordJson.ObjBean> COLLISION_RECORDS = new ArrayList<>();
    public static ArrayList<PathInfo> PATH_RECORDS = new ArrayList<>();

    public final static String YUN_BA_APPKEY = "59f95a6ccd3e3d932f093eb9";
    public final static String YUN_BA_SECKEY = "sec-0rWAc2PXXMQ8CrDlMeT65JZgkC5apbxYQcWmVjwrqx6b028h";
    public final static String PAD_YUN_BA_APPKEY = "5a1630172a27c9d728f52fd5";
    public final static String PAD_YUN_BA_SECKEY = "sec-WPlU861mPVEhxTmQp0IuvC8nqEjc84A0uaWo0NgF2OiCWxhK";

    //小米的appid appscret
    public final static String XIAO_MI_APP_ID = "2882303761517633611";
    public final static String XIAO_MI_APP_KEY = "5521763359611";
}
