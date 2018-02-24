package com.mobileoffice;


import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acker.simplezxing.activity.CaptureActivity;
import com.google.gson.Gson;
import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiAvailability;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.push.HuaweiPush;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.controller.cloud_monitor.CloudMonitorFragment;
import com.mobileoffice.controller.link.LinkFragment;
import com.mobileoffice.controller.me.MeFragment;
import com.mobileoffice.controller.message.MessageFragment;
import com.mobileoffice.controller.new_monitor.NewMonitorActivity;
import com.mobileoffice.controller.new_monitor.NewMonitorModeActivity;
import com.mobileoffice.controller.new_monitor.NoScrollViewPager;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.ContactListJson;
import com.mobileoffice.json.PersonInfoJson;
import com.mobileoffice.json.PushJson;
import com.mobileoffice.json.PushListJson;
import com.mobileoffice.json.RoleJson;
import com.mobileoffice.json.UploadJson;
import com.mobileoffice.utils.ActivityStack;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;
import com.mobileoffice.utils.UpdateManager;
import com.mobileoffice.view.DragPointView;
import com.taobao.sophix.SophixManager;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imkit.manager.UnReadMessageManager;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;
import io.yunba.android.manager.YunBaManager;

//import static com.huawei.hms.activity.BridgeActivity.EXTRA_RESULT;


/**
 * @author AM  模拟了两个用户实现用户信息提供者 建议大家自己拿到例子后重新 配置 appkey 和 token 不然很多开发者用一个 appkey 和 token 会经常被顶号
 *         因为是主要做 会话列表 会话界面 和 用户头像的 示例 为了避免代码 过于冗余 这边没有对 群组(聚合等) 位置消息 和 语音通话 做实现 如果有报错 请参考官网 Demo 实现
 */
public class MainActivity extends BaseActivity implements DragPointView.OnDragListencer,
        View.OnClickListener,
        IUnReadMessageObserver,HuaweiApiClient.ConnectionCallbacks,HuaweiApiClient.OnConnectionFailedListener{
    private static final int REQ_CODE_HOTFIX = 0x1113;
    public static final String EXTRA_DELEGATE_CLASS_NAME = "intent.extra.DELEGATE_CLASS_OBJECT";
    public static final String EXTRA_DELEGATE_CLASS_EX_NAME = "intent.extra.DELEGATE_CLASS_OBJECT_EX";
    public static final String EXTRA_RESULT = "intent.extra.RESULT";
    private String TAG = "MainActivity";
    private Context mContext;
    //云监控
    private RelativeLayout rl_cloud;
    private ImageView iv_cloud;
    private TextView tv_cloud;
    //消息
    private RelativeLayout rl_message;
    private ImageView iv_message;
    private TextView tv_message;
    private DragPointView dpv_message_num;
    //新建
    private RelativeLayout rl_new;
    private ImageView iv_new;
    private TextView tv_new;
    //互联
    private RelativeLayout rl_link;
    private ImageView iv_link;
    private TextView tv_link;
    //我的
    private RelativeLayout rl_me;
    private ImageView iv_me;
    private TextView tv_me;

    //fragment
    private FragmentManager fm;
    private FragmentTransaction fs;
    private CloudMonitorFragment mCloudMonitorFragment;
    private MessageFragment mMessageFragment;

    private LinkFragment mLinkFragment;
    private MeFragment mMeFragment;
    //会话列表
    private ConversationListFragment listFragment;
    //会话的title
    //private RelativeLayout rl_message_title;

    //头像
    private String photoFile;
    private String wechatUrl;
    private String flag;
    private String trueName;
    UserInfo mUserInfo = null;


    //记录点击的底部按钮位置
    private int index = 0;
    private int messageIndex = 0;

    //系统消息
    //private RelativeLayout rl_message_system;
    //private TextView tv_system_content;
    //private DragPointView dpv_system_num;
    private int systemNum;
    //系统消息未读数广播
    private IntentFilter mIntentFilter;
    private SystemMessageReceiver mSystemMessageReceiver;
    private Conversation.ConversationType[] mConversationsTypes;
    private static final int REQ_CODE_PERMISSION = 0x1111;
    private static final int REQ_FILE_CODE_PERMISSION = 0x1112;
    private static final int REQUEST_HMS_RESOLVE_ERROR = 0x1113;
    private String phone;
    //新建
    private RelativeLayout rl_main_new;

    //消息数
    private int newsCount;
    private TextView tv_system_line;
    public static TextView tv_system_line1;

    //天气
    public static String weather;
    public static String weatherUrl;
    public static String temperature;
    public static String endLocation;

    //距离
    public static double distance;


    //弹出蒙版
    private boolean isMask = true;
    private int maskFlag = 0;

    //viewpager
    public static NoScrollViewPager vp_content;
    private MainFragmentPagerAdapter mMainFragmentPagerAdapter;
    private List<Fragment> mFragments;
    private LinkedHashMap<String, UserInfo> mUserInfoCache;

    private int roleId;
    private String mUrl;
    private HuaweiApiClient client;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("index",index);
    }

    @Override
    protected void initVariable() {

        //创建华为移动服务client实例用以使用华为push服务

        //需要指定api为HuaweiPush.PUSH_API

        //连接回调以及连接失败监听

        client = new HuaweiApiClient.Builder(this)

                .addApi(HuaweiPush.PUSH_API)

                .addConnectionCallbacks(this)

                .addOnConnectionFailedListener(this)

                .build();


        //建议在oncreate的时候连接华为移动服务

        //业务可以根据自己业务的形态来确定client的连接和断开的时机，但是确保connect和disconnect必须成对出现

        client.connect();


        //ToastUtil.showToast("欢迎进入主页",this);
        mUserInfoCache = new LinkedHashMap<>();
        Log.e(TAG, "UNREAD_PUSH_NUM:" + CONSTS.UNREAD_PUSH_NUM);
        phone = SharePreUtils.getString("phone", "", this);
        photoFile = SharePreUtils.getString("photoFile", "", this);
        flag = SharePreUtils.getString("flag", "", this);
        trueName = SharePreUtils.getString("trueName", "", this);
        wechatUrl = SharePreUtils.getString("wechatUrl", "", this);
        newsCount = SharePreUtils.getInt("unread_push_num", -1, this);
        roleId = SharePreUtils.getInt("roleId", -1, this);

        if ("0".equals(flag) && !"".equals(wechatUrl)) {
            mUserInfo = new UserInfo(phone, trueName, Uri.parse(wechatUrl));
        } else if ("1".equals(flag) && !"".equals(photoFile)) {
            mUserInfo = new UserInfo(phone, trueName, Uri.parse(photoFile));
        } else {
            mUserInfo = new UserInfo(phone, trueName, Uri.parse(URL.CONTACT_PERSON_PHOTO));
        }
        RongIM.getInstance().refreshUserInfoCache(mUserInfo);


        //蒙版 第一次加载
        isMask = SharePreUtils.getBoolean("isMask", true, this);

        //动态加载广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(CONSTS.MAIN_ACTION);
        registerReceiver(myReceiver, filter);

        updateContact();

        //推送
//        RongIMClient.setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
//            @Override
//            public boolean onReceived(Message message, int i) {
//                Log.e(TAG, "消息:" + message.getSenderUserId() + ",i:" + i);
//                return false;
//            }
//        });
        updateSystemCount();

        //Log.e(TAG,"phone2:"+phone);

        // 设置极光推送别名
        //JPushInterface.setAlias(this,1,phone);
        JPushInterface.setAlias(this, phone, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set set) {

            }


        });
        //设置云巴推送别名
        YunBaManager.setAlias(getApplicationContext(), phone,
                new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        //DemoUtil.showToast("success", getApplicationContext());
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        if (exception instanceof MqttException) {
                            MqttException ex = (MqttException) exception;
                            String msg = "setAlias failed with error code : " + ex.getReasonCode();
                            //DemoUtil.showToast(msg, getApplicationContext());
                        }
                    }
                }
        );
        fm = getSupportFragmentManager();
        fs = fm.beginTransaction();
        mFragments = new ArrayList<>();
        mCloudMonitorFragment = CloudMonitorFragment.newInstance();
        mMessageFragment = MessageFragment.newInstance();
        mLinkFragment = LinkFragment.newInstance();
        mMeFragment = MeFragment.newInstance();
        mFragments.add(mCloudMonitorFragment);
        mFragments.add(mMessageFragment);
        mFragments.add(mLinkFragment);
        mFragments.add(mMeFragment);
        mMainFragmentPagerAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
        vp_content.setAdapter(mMainFragmentPagerAdapter);
        vp_content.setOffscreenPageLimit(3);
        //vp_content.setCurrentItem(2);

        //enterFragment();

        UnReadMessageManager.getInstance().addObserver(
                new Conversation.ConversationType[]{
                        Conversation.ConversationType.PRIVATE,
                        Conversation.ConversationType.CUSTOMER_SERVICE, Conversation.ConversationType.GROUP},
                this);
        //读取系统未读数
        CONSTS.UNREAD_PUSH_NUM = SharePreUtils.getInt("unread_push_num", 0, this);
        CONSTS.NEW_SYSTEM_MESSAGE = SharePreUtils.getString("new_system_message", CONSTS.NEW_SYSTEM_MESSAGE, this);
        //tv_system_content.setText(CONSTS.NEW_SYSTEM_MESSAGE);
        //动态注册系统未读广播
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.mobile.office.system.message");
        mSystemMessageReceiver = new SystemMessageReceiver();
        registerReceiver(mSystemMessageReceiver, mIntentFilter);

        //清除所有的消息未读数类型
        mConversationsTypes = new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE,
                Conversation.ConversationType.GROUP,
                Conversation.ConversationType.PUBLIC_SERVICE,
                Conversation.ConversationType.APP_PUBLIC_SERVICE,
                Conversation.ConversationType.SYSTEM

        };
        //loadRongServer();
        String systemType = getIntent().getStringExtra("systemType");
        if ("system".equals(systemType)) {
            index = 1;

            changeTextViewColor();

            iv_message.setImageResource(R.drawable.nav_2msg_on);
            tv_message.setTextColor(getResources().getColor(R.color.main));


            CONSTS.IS_NEWS = false;


            vp_content.setCurrentItem(1);
        } else {
            vp_content.setCurrentItem(0);
        }

        //ToastUtil.showToast(SharePreUtils.getString("create_time","",this),this);

    }

    PopupWindow pop;

    /****
     * 头像提示框
     */
    public void showPopupWindow() {
        //mRoleList = new ArrayList<>();
        pop = new PopupWindow(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.start_pop,
                null);
        pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);
        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);

        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();

            }
        });


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (isMask && maskFlag == 0) {
            showPopupWindow();
            pop.showAtLocation(rl_me, Gravity.CENTER, 0, 0);
            SharePreUtils.putBoolean("isMask", false, this);
            maskFlag = 1;
        }
    }

    /**
     * 获取角色信息
     *
     * @param phone
     */
    private void loadRoleInfo(String phone) {
        RequestParams params = new RequestParams(URL.USER);
        params.addBodyParameter("action", "roleInfo");
        params.addBodyParameter("phone", phone);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                RoleJson roleJson = new Gson().fromJson(result, RoleJson.class);
                if (roleJson != null && roleJson.getResult() == CONSTS.SEND_OK) {
                    RoleJson.ObjBean objBean = roleJson.getObj();
                    if (objBean != null) {
//                        roleId = objBean.getRoleId();
//                        roleName = objBean.getRoleName();
//                        SharePreUtils.putInt("roleId", roleId, MainActivity.this);
//                        SharePreUtils.putString("roleName", roleName, MainActivity.this);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        ActivityStack.getInstance().finishAllActivity();
        mContext = this;

        vp_content = (NoScrollViewPager) findViewById(R.id.vp_content);
        rl_cloud = (RelativeLayout) findViewById(R.id.rl_cloud);
        iv_cloud = (ImageView) findViewById(R.id.iv_cloud);
        tv_cloud = (TextView) findViewById(R.id.tv_cloud);

        rl_message = (RelativeLayout) findViewById(R.id.rl_message);
        iv_message = (ImageView) findViewById(R.id.iv_message);
        tv_message = (TextView) findViewById(R.id.tv_message);
        dpv_message_num = (DragPointView) findViewById(R.id.dpv_message_num);

        rl_new = (RelativeLayout) findViewById(R.id.rl_new);
        iv_new = (ImageView) findViewById(R.id.iv_new);
        tv_new = (TextView) findViewById(R.id.tv_new);

        rl_link = (RelativeLayout) findViewById(R.id.rl_link);
        iv_link = (ImageView) findViewById(R.id.iv_link);
        tv_link = (TextView) findViewById(R.id.tv_link);

        rl_me = (RelativeLayout) findViewById(R.id.rl_me);
        iv_me = (ImageView) findViewById(R.id.iv_me);
        tv_me = (TextView) findViewById(R.id.tv_me);

        tv_system_line = (TextView) findViewById(R.id.tv_system_line);
        tv_system_line1 = (TextView) findViewById(R.id.tv_system_line1);
        //rl_message_title = (RelativeLayout) findViewById(R.id.rl_message_title);
        //rl_message_system = (RelativeLayout) findViewById(R.id.rl_message_system);


        //tv_system_content = (TextView) findViewById(R.id.tv_system_content);
        //dpv_system_num = (DragPointView) findViewById(R.id.dpv_system_num);

        rl_main_new = (RelativeLayout) findViewById(R.id.rl_main_new);


        rl_cloud.setOnClickListener(this);
        rl_message.setOnClickListener(this);
        rl_new.setOnClickListener(this);
        rl_link.setOnClickListener(this);
        rl_me.setOnClickListener(this);
        // rl_message_system.setOnClickListener(this);

//        dpv_message_num.setDragListencer(this);
//        dpv_message_num.setOnClickListener(this);
        //rl_message_title.setVisibility(View.GONE);
        // rl_message_system.setVisibility(View.GONE);
        //RongIM.setUserInfoProvider(this, true);


        rl_main_new.setOnClickListener(this);
        //onCheckedChanged(R.id.rl_message,false);

        if(savedInstanceState!=null){
          index =  savedInstanceState.getInt("index");
          changeTextViewColor();
          if(index==0){
              iv_cloud.setImageResource(R.drawable.nav_1cloud_on);
              tv_cloud.setTextColor(getResources().getColor(R.color.main));
          }else if(index ==1){
              iv_message.setImageResource(R.drawable.nav_2msg_on);
              tv_message.setTextColor(getResources().getColor(R.color.main));
          }else if(index ==2){
              iv_link.setImageResource(R.drawable.nav_4link_on);
              tv_link.setTextColor(getResources().getColor(R.color.main));
          }else if(index ==3){
              iv_me.setImageResource(R.drawable.nav_5mine_on);
              tv_me.setTextColor(getResources().getColor(R.color.main));
          }
        }


    }

    /**
     * 加载融云服务
     */
    private void loadRongServer() {

        String token = SharePreUtils.getString("token", "", this);
        Log.e(TAG, "token:" + token);
        RongIM.connect(token, new RongIMClient.ConnectCallback() {

            @Override
            public void onSuccess(String userId) {


//                RongIM.setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
//                    @Override
//                    public boolean onReceived(Message message, int i) {
//                        Log.e(TAG, "userIDI:" + i+":"+message.getContent());
//                        return false;
//                    }
//                });
                Log.e(TAG, "connect success:" + userId);

            }


            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Log.e(TAG, "connect failure errorCode is :" + errorCode.getValue());
            }

            @Override
            public void onTokenIncorrect() {
                Log.e(TAG, "token is error , please check token and appkey ");
            }
        });
    }


    @Override
    protected void initData() {


        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Do not have the permission of camera, request it.
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQ_CODE_HOTFIX);
        } else {
            // Have gotten the permission
            //startCaptureActivityForResult();
            // queryAndLoadNewPatch不可放在attachBaseContext 中，否则无网络权限，建议放在后面任意时刻，如onCreate中
            SophixManager.getInstance().queryAndLoadNewPatch();
        }


        RequestParams params = new RequestParams(URL.UPLOAD);
        params.addBodyParameter("action", "app");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //ToastUtil.showToast(result,MainActivity.this);
                UploadJson upload = new Gson().fromJson(result, UploadJson.class);
                if (upload != null && upload.getResult() == CONSTS.SEND_OK) {
                    int oldVersion = getVersionCode(mContext);
                    int newVersion = upload.getObj().getVersion();
                    String url = upload.getObj().getUrl();
                    mUrl = url;
                    //ToastUtil.showToast("new:"+newVersion+",old:"+oldVersion,MainActivity.this);
                    //  LogUtil.e(TAG,"model1:"+model.getVersion());
                    if (newVersion > oldVersion) {
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            // Do not have the permission of camera, request it.
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQ_FILE_CODE_PERMISSION);
                        } else {
                            // Have gotten the permission
                            //startCaptureActivityForResult();
                            UpdateManager updateManager = new UpdateManager(mContext, url);
                            updateManager.checkUpdateInfo();
                            //LogUtil.e(TAG,"model2:"+model.getVersion());
                        }


                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

        //自动更新
        /**
         new HttpHelper().getUploadAPK().subscribe(new HttpObserver<Upload>() {
        @Override public void onComplete() {

        }



        @Override public void onError(Throwable e) {
        super.onError(e);
        //ToastUtil.showToast(e.getMessage()+":error",MainActivity.this);
        LogUtil.e(TAG,"upload:"+e.getMessage()+","+e.getLocalizedMessage()+","+e.getCause());
        // Toast.makeText(MainActivity.this,e.getMessage()+","+e.getLocalizedMessage()+","+e.getCause(),Toast.LENGTH_LONG).show();
        }

        @Override public void onSuccess(Upload model) {
        if (model != null) {
        int oldVersion = getVersionCode(mContext);
        int newVersion = model.getVersion();
        String url = model.getUrl();
        mUrl = url;
        //ToastUtil.showToast(model.getVersion()+",",MainActivity.this);
        //  LogUtil.e(TAG,"model1:"+model.getVersion());
        if (newVersion > oldVersion) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED||ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        // Do not have the permission of camera, request it.
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, REQ_FILE_CODE_PERMISSION);
        } else {
        // Have gotten the permission
        //startCaptureActivityForResult();
        UpdateManager updateManager = new UpdateManager(mContext, url);
        updateManager.checkUpdateInfo();
        //LogUtil.e(TAG,"model2:"+model.getVersion());
        }


        }


        }

        }


        });
         **/
        //onCheckedChanged(R.id.rl_cloud,false);

    }


    /**
     * 获取版本号
     *
     * @param context 上下文
     * @return 版本号
     */
    public static int getVersionCode(Context context) {
        //获取包管理器
        PackageManager pm = context.getPackageManager();
        //获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);

            //返回版本号
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取版本号
     *
     * @param context 上下文
     * @return 版本号
     */
    public static String getVersionName(Context context) {
        //获取包管理器
        PackageManager pm = context.getPackageManager();
        //获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);

            //返回版本号
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "1.0.0";
    }


    private void changeTextViewColor() {
//        rl_message_title.setVisibility(View.GONE);
//        rl_message_system.setVisibility(View.GONE);

        iv_cloud.setImageResource(R.drawable.nav_1cloud);
        iv_message.setImageResource(R.drawable.nav_2msg);
        //iv_new.setBackgroundDrawable(getResources().getDrawable(R.drawable.rc_ac_audio_file_icon));
        iv_link.setImageResource(R.drawable.nav_4link);
        iv_me.setImageResource(R.drawable.nav_5mine);

        tv_cloud.setTextColor(getResources().getColor(R.color.font_black_3));
        tv_message.setTextColor(getResources().getColor(R.color.font_black_3));
        //tv_new.setTextColor(getResources().getColor(R.color.font_black_3));
        tv_link.setTextColor(getResources().getColor(R.color.font_black_3));
        tv_me.setTextColor(getResources().getColor(R.color.font_black_3));


    }

    private void getContactList() {

        RequestParams params = new RequestParams(URL.CONTACT);
        params.addBodyParameter("action", "getContactList");
        params.addBodyParameter("phone", phone);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    Log.e(TAG, "result:" + result);
                    ContactListJson json = new Gson().fromJson(result, ContactListJson.class);
                    if (json != null && json.getResult() == CONSTS.SEND_OK) {
                        if (json.getObj() != null) {
                            List<ContactListJson.ObjBean> objBeens = json.getObj();
                            for (int i = 0; i < objBeens.size(); i++) {
                                Log.e(TAG, i + "size:" + objBeens.size() + ",phone1111:" + objBeens.get(i).getContactPhone() + ",trueName:" + objBeens.get(i).getTrueName() + ",photoFile:" + objBeens.get(i).getWechatUrl() + "," + objBeens.get(i).getPhotoFile() + "," + objBeens.get(i).getIsUploadPhoto());
                                Log.e(TAG, i + "size:" + objBeens.size() + ",phone1112:" + objBeens.get(i).getContactPhone() + ",trueName:" + objBeens.get(i).getTrueName() + ",photoFile:" + objBeens.get(i).getWechatUrl() + "," + objBeens.get(i).getPhotoFile() + "," + objBeens.get(i).getIsUploadPhoto());

                                if ("0".equals(objBeens.get(i).getIsUploadPhoto()) && !"".equals(objBeens.get(i).getWechatUrl())) {
                                    mUserInfo = new UserInfo(objBeens.get(i).getContactPhone(), objBeens.get(i).getTrueName(), Uri.parse(objBeens.get(i).getWechatUrl()));
                                } else if ("1".equals(objBeens.get(i).getIsUploadPhoto()) && !"".equals(objBeens.get(i).getPhotoFile())) {
                                    mUserInfo = new UserInfo(objBeens.get(i).getContactPhone(), objBeens.get(i).getTrueName(), Uri.parse(objBeens.get(i).getPhotoFile()));
                                } else {
                                    mUserInfo = new UserInfo(objBeens.get(i).getContactPhone(), objBeens.get(i).getTrueName(), Uri.parse(URL.CONTACT_PERSON_PHOTO));
                                }

                                RongIM.getInstance().refreshUserInfoCache(mUserInfo);

                            }
                        }
                    } else {

                    }
                } catch (Exception ex) {
                    Log.e(TAG, "phone1113:" + ex.getMessage() + "," + ex.getLocalizedMessage());
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "phone1114:" + ex.getMessage() + "," + ex.getLocalizedMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();


        //getContactList();
        //refreshUserInfo();

        if (CONSTS.UNREAD_PUSH_NUM == 0) {
            // dpv_system_num.setVisibility(View.GONE);
        } else if (CONSTS.UNREAD_PUSH_NUM > 0 && CONSTS.UNREAD_PUSH_NUM < 100) {
            // dpv_system_num.setVisibility(View.VISIBLE);
            // dpv_system_num.setText(String.valueOf(CONSTS.UNREAD_PUSH_NUM));
        } else {
            // dpv_system_num.setVisibility(View.VISIBLE);
            // dpv_system_num.setText("99+");
        }
//        Log.e(TAG, "name:" + mUserInfo.getName() + ",userId:" + mUserInfo.getUserId());
    }

    @Override
    public void onCountChanged(int count) {
        //ToastUtil.showToast("countChange:"+count,MainActivity.this);
        int c = count + CONSTS.UNREAD_PUSH_NUM;
        if (c == 0) {
            dpv_message_num.setVisibility(View.GONE);
        } else if (c > 0 && c < 100) {
            dpv_message_num.setVisibility(View.VISIBLE);
            dpv_message_num.setText(String.valueOf(c));
        } else {
            dpv_message_num.setVisibility(View.VISIBLE);
            dpv_message_num.setText("99+");
        }

    }

//    /**
//     * 加载 会话列表 ConversationListFragment
//     */
//    private void enterFragment() {
//        listFragment = new ConversationListFragment();
//        listFragment.setAdapter(new ConversationListAdapterEx(RongContext.getInstance()));
//
//        listFragment.setAdapter(new ConversationListAdapterEx(RongContext.getInstance()));
//        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
//                .appendPath("conversationlist")
//                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话非聚合显示
//                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//设置群组会话聚合显示
//                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置讨论组会话非聚合显示
//                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")//设置系统会话非聚合显示
//                .build();
//
//        listFragment.setUri(uri);
//    }

    @Override
    public void onDragOut() {
        dpv_message_num.setVisibility(View.GONE);
        //dpv_system_num.setVisibility(View.GONE);
        CONSTS.UNREAD_PUSH_NUM = 0;
        SharePreUtils.putInt("unread_push_num", 0, this);

        RequestParams params = new RequestParams(URL.PUSH);
        params.addBodyParameter("action", "clearUnreadPushMessageNum");
        params.addBodyParameter("user_info_id", phone);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CONSTS.UNREAD_PUSH_NUM = 0;
                SharePreUtils.putInt("unread_push_num", 0, mContext);
                sendBroadcast(new Intent("com.mobile.office.system.message"));
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

        RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                if (conversations != null && conversations.size() > 0) {
                    for (Conversation c : conversations) {
                        RongIM.getInstance().clearMessagesUnreadStatus(c.getConversationType(), c.getTargetId(), null);
                    }
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {

            }
        }, mConversationsTypes);
    }

    private void loadPUshData() {
        RequestParams params = new RequestParams(URL.USER);
        params.addBodyParameter("action", "getPushList");
        params.addBodyParameter("phone", SharePreUtils.getString("phone", "", this));
        params.addBodyParameter("page", 0 + "");
        params.addBodyParameter("pageSize", 1 + "");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                Gson gson = new Gson();
                PushListJson pushListJson = gson.fromJson(result, PushListJson.class);
                if (pushListJson != null && pushListJson.getResult() == CONSTS.SEND_OK) {
                    if (com.mobileoffice.controller.message.ConversationListFragment.tv_system_time != null) {
                        com.mobileoffice.controller.message.ConversationListFragment.tv_system_time.setText(pushListJson.getObj().get(0).getCreateTime());
                    }
                    if (com.mobileoffice.controller.message.ConversationListFragment.tv_system_content != null) {
                        com.mobileoffice.controller.message.ConversationListFragment.tv_system_content.setText(pushListJson.getObj().get(0).getContent());
                    }

                }

                //ToastUtil.showToast("finish"+result,getActivity());
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

                //  rv_system_message.refreshComplete();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.rl_cloud:
                if (index != 0) {
                    index = 0;
                    changeTextViewColor();
                    CONSTS.IS_NEWS = false;
                    iv_cloud.setImageResource(R.drawable.nav_1cloud_on);
                    tv_cloud.setTextColor(getResources().getColor(R.color.main));

//                    fs = fm.beginTransaction();
//                    fs.replace(R.id.content_main, mCloudMonitorFragment);
//                    fs.commit();
                    //onCheckedChanged(view.getId(),false);
                    vp_content.setCurrentItem(0);
                }
                break;
            case R.id.rl_message:

                index = 1;

                changeTextViewColor();

                iv_message.setImageResource(R.drawable.nav_2msg_on);
                tv_message.setTextColor(getResources().getColor(R.color.main));

//                    fs = fm.beginTransaction();
//
//
//                    fs.add(R.id.content_main, mMessageFragment, "message");
//
//                    fs.commit(); int num = SharePreUtils.getInt("unread_push_num", 0, getActivity());
                CONSTS.IS_NEWS = false;
                //onCheckedChanged(view.getId(),false);

                vp_content.setCurrentItem(1);
                //com.mobileoffice.controller.message.ConversationListFragment.scrollView();
                loadPUshData();
                MessageFragment.vp_message.setCurrentItem(0);
                break;

            case R.id.rl_main_new:
                CONSTS.IS_NEWS = false;
                // tv_new.setTextColor(getResources().getColor(R.color.black));
                // Open Scan Activity
//                if (CONSTS.IS_START == 0) {
//                    ToastUtil.showToast("请处理您未开始的转运", this);//上海
//                } else {

                if (roleId == CONSTS.ROLE_DOCTOR || roleId == CONSTS.ROLE_OPO) {
                    Intent intent = new Intent();
                    intent.setClass(this, NewMonitorModeActivity.class);
                    startActivity(intent);

                } else {
                    ToastUtil.showToast("您没有权限创建新转运", this);//上海
                }
//                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                        // Do not have the permission of camera, request it.
//                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQ_CODE_PERMISSION);
//                    } else {
//                        // Have gotten the permission
//                        startCaptureActivityForResult();
//                    }


                //}

                break;
            case R.id.rl_link:
                if (index != 3) {
                    index = 3;
                    CONSTS.IS_NEWS = false;
                    changeTextViewColor();

                    iv_link.setImageResource(R.drawable.nav_4link_on);
                    tv_link.setTextColor(getResources().getColor(R.color.main));

//                    fs = fm.beginTransaction();
//                    fs.replace(R.id.content_main, mLinkFragment);
//                    //fs.addToBackStack(null);
//                    fs.commit();
                    //onCheckedChanged(view.getId(),false);
                    vp_content.setCurrentItem(2);

                }

                break;
            case R.id.rl_me:
                if (index != 4) {
                    index = 4;
                    CONSTS.IS_NEWS = false;
                    changeTextViewColor();

                    iv_me.setImageResource(R.drawable.nav_5mine_on);
                    tv_me.setTextColor(getResources().getColor(R.color.main));

//                    fs = fm.beginTransaction();
//                    fs.replace(R.id.content_main, mMeFragment);
//                    //fs.addToBackStack(null);
//                    fs.commit();
                    //onCheckedChanged(view.getId(),false);
                    vp_content.setCurrentItem(3);
                }
                break;

        }
    }

    private String fragment1Tag = "cloud";
    private String fragment2Tag = "message";
    private String fragment3Tag = "link";
    private String fragment4Tag = "me";

    public void onCheckedChanged(int checkedId, boolean first) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment1 = fm.findFragmentByTag(fragment1Tag);
        Fragment fragment2 = fm.findFragmentByTag(fragment2Tag);
        Fragment fragment3 = fm.findFragmentByTag(fragment3Tag);
        Fragment fragment4 = fm.findFragmentByTag(fragment4Tag);
        LogUtil.e(TAG, "frag:" + fragment2);
        if (fragment1 != null) {
            ft.hide(fragment1);
        }
        if (fragment2 != null) {
            ft.hide(fragment2);
        }
        if (fragment3 != null) {
            ft.hide(fragment3);
        }
        if (fragment4 != null) {
            ft.hide(fragment4);
        }
        switch (checkedId) {
            case R.id.rl_cloud:
                if (fragment1 == null) {
                    fragment1 = new CloudMonitorFragment();
                    ft.add(R.id.content_main, fragment1, fragment1Tag);
                } else {
                    ft.show(fragment1);
                }
                break;
            case R.id.rl_message:
                if (fragment2 == null) {
                    fragment2 = new MessageFragment();
                    ft.add(R.id.content_main, fragment2, fragment2Tag);
                } else {
                    ft.show(fragment2);
                    if (first) {
                        ft.hide(fragment2);
                    }
                }
                break;
            case R.id.rl_link:
                if (fragment3 == null) {
                    fragment3 = new LinkFragment();
                    ft.add(R.id.content_main, fragment3,
                            fragment3Tag);
                } else {
                    ft.show(fragment3);
                }
                break;
            case R.id.rl_me:
                if (fragment4 == null) {
                    fragment4 = new MeFragment();
                    ft.add(R.id.content_main, fragment4, fragment4Tag);
                } else {
                    ft.show(fragment4);
                }
                break;
            default:
                break;
        }
        ft.commit();
    }

    /**
     * 更新系统消息未读数
     */
    private void updateSystemCount() {

        RequestParams params = new RequestParams(URL.USER);
        params.addBodyParameter("action", "pushCount");
        params.addBodyParameter("phone", phone);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                PushJson pushJson = gson.fromJson(result, PushJson.class);
                if (pushJson != null && pushJson.getResult() == CONSTS.SEND_OK) {

                    //改变系统消息未读数
                    CONSTS.UNREAD_PUSH_NUM = pushJson.getObj() == null ? 0 : pushJson.getObj().getCount();
                    CONSTS.NEW_SYSTEM_MESSAGE = pushJson.getObj() == null ? "" : pushJson.getObj().getContent();
                    CONSTS.NEW_SYSTEM_TIME = pushJson.getObj() == null ? "" : pushJson.getObj().getCreateTime();

                } else {
                    CONSTS.UNREAD_PUSH_NUM = 0;
                }


                SharePreUtils.putInt("unread_push_num", CONSTS.UNREAD_PUSH_NUM, mContext);
                SharePreUtils.putString("new_system_message", CONSTS.NEW_SYSTEM_MESSAGE, mContext);

                Intent i = new Intent("com.mobile.office.system.message");
                sendBroadcast(i);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                CONSTS.UNREAD_PUSH_NUM = 0;

                CONSTS.NEW_SYSTEM_MESSAGE = "";


                Intent i = new Intent("com.mobile.office.system.message");
                sendBroadcast(i);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void startCaptureActivityForResult() {
        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(CaptureActivity.KEY_NEED_BEEP, CaptureActivity.VALUE_BEEP);
        bundle.putBoolean(CaptureActivity.KEY_NEED_VIBRATION, CaptureActivity.VALUE_VIBRATION);
        bundle.putBoolean(CaptureActivity.KEY_NEED_EXPOSURE, CaptureActivity.VALUE_NO_EXPOSURE);
        bundle.putByte(CaptureActivity.KEY_FLASHLIGHT_MODE, CaptureActivity.VALUE_FLASHLIGHT_OFF);
        bundle.putByte(CaptureActivity.KEY_ORIENTATION_MODE, CaptureActivity.VALUE_ORIENTATION_AUTO);
        intent.putExtra(CaptureActivity.EXTRA_SETTING_BUNDLE, bundle);
        startActivityForResult(intent, CaptureActivity.REQ_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // User agree the permission
                    startCaptureActivityForResult();
                } else {
                    // User disagree the permission
                    ToastUtil.showToast("请在app设置界面开启照相权限", MainActivity.this);
                }
            }
            case REQ_CODE_HOTFIX:
                // queryAndLoadNewPatch不可放在attachBaseContext 中，否则无网络权限，建议放在后面任意时刻，如onCreate中
                SophixManager.getInstance().queryAndLoadNewPatch();
                break;

            case REQ_FILE_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // User agree the permission
                    UpdateManager updateManager = new UpdateManager(mContext, mUrl);
                    updateManager.checkUpdateInfo();
                } else {
                    // User disagree the permission
                    ToastUtil.showToast("请在app设置界面获取存储权限", MainActivity.this);
                }
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CaptureActivity.REQ_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        //tvResult.setText(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));  //or do sth
                        // ToastUtil.showToast("ok" + data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
                        Intent intent = new Intent();
                        intent.setClass(this, NewMonitorActivity.class);
                        startActivity(intent);
                        break;
                    case RESULT_CANCELED:
                        if (data != null) {
                            // for some reason camera is not working correctly
                            //tvResult.setText(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
                            //ToastUtil.showToast(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT), MainActivity.this);
                        }
                        break;
                }
                break;
            case REQUEST_HMS_RESOLVE_ERROR:
                if (resultCode == Activity.RESULT_OK) {


                    int result = data.getIntExtra(EXTRA_RESULT, 0);


                    if (result == ConnectionResult.SUCCESS) {

                        Log.i(TAG, "错误成功解决");

                        if (!client.isConnecting() && !client.isConnected()) {

                            client.connect();

                        }

                    } else if (result == ConnectionResult.CANCELED) {

                        Log.i(TAG, "解决错误过程被用户取消");

                    } else if (result == ConnectionResult.INTERNAL_ERROR) {

                        Log.i(TAG, "发生内部错误，重试可以解决");

                        //开发者可以在此处重试连接华为移动服务等操作，导致失败的原因可能是网络原因等

                    } else {

                        Log.i(TAG, "未知返回码");

                    }

                }
                break;
        }
    }
    // 你也可以使用简单的扫描功能，但是一般扫描的样式和行为都是可以自定义的，这里就写关于自定义的代码了
    // 你可以把这个方法作为一个点击事件
//    public void customScan() {
//        new_monitor IntentIntegrator(this)
//                .setOrientationLocked(false)
//                .setCaptureActivity(CustomScanActivity.class) // 设置自定义的activity是CustomActivity
//                .initiateScan(); // 初始化扫描
//    }

    @Override
    protected synchronized void onDestroy() {
        super.onDestroy();
        //保存系统未读数
        SharePreUtils.putInt("unread_push_num", CONSTS.UNREAD_PUSH_NUM, this);
        SharePreUtils.putString("new_system_message", CONSTS.NEW_SYSTEM_MESSAGE, this);
        unregisterReceiver(mSystemMessageReceiver);
        RongIM.getInstance().disconnect();
        unregisterReceiver(myReceiver);
        if (MessageFragment.vp_message != null) {
            MessageFragment.vp_message = null;
        }
        SophixManager.getInstance().killProcessSafely();
    }

    private void refreshUserInfo() {
        flag = SharePreUtils.getString("flag", "", this);
        photoFile = SharePreUtils.getString("photoFile", "", this);
        Log.e(TAG, "phone1:" + phone + ",trueName:" + trueName + ",photoFile:" + photoFile);
        if ("0".equals(flag) && !"".equals(wechatUrl)) {
            mUserInfo = new UserInfo(phone, trueName, Uri.parse(wechatUrl));
        } else if ("1".equals(flag) && !"".equals(photoFile)) {

            mUserInfo = new UserInfo(phone, trueName, Uri.parse(photoFile));
        } else {
            mUserInfo = new UserInfo(phone, trueName, Uri.parse(URL.CONTACT_PERSON_PHOTO));
        }
        RongIM.getInstance().refreshUserInfoCache(mUserInfo);
        RongIM.getInstance().setMessageAttachedUserInfo(true);
    }

    //@Override
    public UserInfo getUserInfo(String s) {

        //tv_me.setText(s);

        photoFile = SharePreUtils.getString("photoFile", "", this);
        flag = SharePreUtils.getString("flag", "", this);
        if ("0".equals(flag) && !"".equals(wechatUrl)) {
            mUserInfo = new UserInfo(phone, trueName, Uri.parse(wechatUrl));
        } else if ("1".equals(flag) && !"".equals(photoFile)) {
            mUserInfo = new UserInfo(phone, trueName, Uri.parse(photoFile));
        } else {
            mUserInfo = new UserInfo(phone, trueName, Uri.parse(URL.CONTACT_PERSON_PHOTO));
        }
        Log.e(TAG, "phone:" + phone + ",trueName:" + trueName + ",photoFile:" + photoFile + ",s:" + s);
        //getContactList();
        //loadUserByPhone(s);
        mUserInfo = mUserInfoCache.get(s);
        return mUserInfo;
    }

    private void loadUserByPhone(final String userPhone) {
        RequestParams params = new RequestParams(URL.USER);
        params.addBodyParameter("action", "personInfo");
        params.addBodyParameter("phone", userPhone);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PersonInfoJson personInfoJson = new Gson().fromJson(result, PersonInfoJson.class);
                if (personInfoJson != null && personInfoJson.getResult() == CONSTS.SEND_OK) {
                    if (personInfoJson.getObj() != null) {
                        ToastUtil.showToast("userId:" + userPhone, MainActivity.this);
                        String pFlag = personInfoJson.getObj().getIs_upload_photo();
                        String pPhone = personInfoJson.getObj().getPhone();
                        String pWechatUrl = personInfoJson.getObj().getWechat_url();
                        String pPhotoFile = personInfoJson.getObj().getPhoto_url();
                        String pTrueName = personInfoJson.getObj().getTrue_name();
                        if ("0".equals(pFlag) && !"".equals(pWechatUrl)) {
                            mUserInfo = new UserInfo(pPhone, pTrueName, Uri.parse(pWechatUrl));
                        } else if ("1".equals(pFlag) && !"".equals(pPhotoFile)) {
                            mUserInfo = new UserInfo(pPhone, pTrueName, Uri.parse(pPhotoFile));
                        } else {
                            mUserInfo = new UserInfo(pPhone, pTrueName, Uri.parse(URL.CONTACT_PERSON_PHOTO));
                        }
                        RongIM.getInstance().refreshUserInfoCache(mUserInfo);

                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    public void onConnected() {
        LogUtil.e("HWSend", "onConnected");
        //ToastUtil.showToast("onConnected",this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        LogUtil.e("HWSend", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        LogUtil.e("HWSend", "onConnectionFailed" + connectionResult.getErrorCode());
        if(HuaweiApiAvailability.getInstance().isUserResolvableError(connectionResult.getErrorCode())) {

            final int errorCode = connectionResult.getErrorCode();
            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    // 此方法必须在主线程调用, xxxxxx.this 为当前界面的activity
//                    HuaweiApiAvailability.getInstance().resolveError(MainActivity.this, errorCode, REQUEST_HMS_RESOLVE_ERROR, new HuaweiApiAvailability.OnUpdateListener() {
//                        @Override
//                        public void onUpdateFailed(@NonNull ConnectionResult connectionResult) {
//                            LogUtil.e("HWSend", "onUpdateFailed"+connectionResult.getErrorCode());
//                        }
//                    });
                }
            });

        } else {

            //其他错误码请参见开发指南或者API文档

        }
    }

    class SystemMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //设置消息未读数
            RongIM.getInstance().getUnreadCount(new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE,
                    Conversation.ConversationType.CUSTOMER_SERVICE, Conversation.ConversationType.GROUP}, new RongIMClient.ResultCallback<Integer>() {
                @Override
                public void onSuccess(Integer count) {
                    //ToastUtil.showToast("count:"+count,MainActivity.this);
                    int c = count + CONSTS.UNREAD_PUSH_NUM;
                    if (c == 0) {
                        dpv_message_num.setVisibility(View.GONE);
                    } else if (c > 0 && c < 100) {
                        dpv_message_num.setVisibility(View.VISIBLE);
                        dpv_message_num.setText(String.valueOf(c));
                    } else {
                        dpv_message_num.setVisibility(View.VISIBLE);
                        dpv_message_num.setText("99+");
                    }
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
            Log.e(TAG, "mainActivity");
            //设置系统消息未读数
//            if (CONSTS.UNREAD_PUSH_NUM == 0) {
//                dpv_system_num.setVisibility(View.GONE);
//
//            } else if (CONSTS.UNREAD_PUSH_NUM > 0 && CONSTS.UNREAD_PUSH_NUM < 100) {
//                dpv_system_num.setVisibility(View.VISIBLE);
//                dpv_system_num.setText(String.valueOf(CONSTS.UNREAD_PUSH_NUM));
//
//            } else {
//                dpv_system_num.setVisibility(View.VISIBLE);
//                dpv_system_num.setText("99+");
//
//            }
            //设置系统消息最新内容

            //tv_system_content.setText(CONSTS.NEW_SYSTEM_MESSAGE);
        }
    }

    private Fragment mContent;

    /**
     * 修改显示的内容 不会重新加载
     **/
    public void switchContent(Fragment to) {
        if (mContent != to) {
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            if (!to.isAdded()) { // 先判断是否被add过
                transaction.hide(mContent).add(R.id.content, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(mContent).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
            mContent = to;
        }

    }

    public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
        List<Fragment> innerFragments;

        public MainFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            innerFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {

            return innerFragments.get(position);

        }

        @Override
        public int getCount() {

            return innerFragments.size();
        }

    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            index = 0;
            changeTextViewColor();
            CONSTS.IS_NEWS = false;
            iv_cloud.setImageResource(R.drawable.nav_1cloud_on);
            tv_cloud.setTextColor(getResources().getColor(R.color.main));

//                    fs = fm.beginTransaction();
//                    fs.replace(R.id.content_main, mCloudMonitorFragment);
//                    fs.commit();
            //onCheckedChanged(view.getId(),false);
            vp_content.setCurrentItem(0);
        }

    };

    /**
     * 更新同一医院的联系人
     */
    private void updateContact() {

        RequestParams params = new RequestParams(URL.CONTACT);
        params.addBodyParameter("action", "updateContact");
        params.addBodyParameter("phone", SharePreUtils.getString("phone", "", this));

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                // ToastUtil.showToast("gg"+ result,MainActivity.this);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //ToastUtil.showToast("gg1"+ ex.getMessage(),MainActivity.this);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    /**
     * 双击返回键退出
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            //Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            RongIM.getInstance().disconnect();
            this.finish();
        }

        return super.onKeyDown(keyCode, event);
    }
}
