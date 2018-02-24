package com.mobileoffice.application;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.mobileoffice.SealAppContext;
import com.mobileoffice.controller.message.GroupProviderTemp;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.PreferencesHelper;
import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.xutils.x;

import cn.jpush.android.api.JPushInterface;
import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.RongExtensionManager;
import io.rong.imkit.RongIM;
import io.rong.push.RongPushClient;
import io.rong.push.common.RongException;
import io.yunba.android.manager.YunBaManager;


/**
 * 在开发应用时都会和Activity打交道，而Application使用的就相对较少 Application是用来管理应用程序的全局状态的，比如载入资源文件 在应用程序启动的时候Application会首先创建，然后才会根据情况(Intent)启动相应的Activity或者Service
 *
 * @author blue
 */
public class LocalApplication extends BaseApplication {
    private static LocalApplication instance;
    private static LocalApplication sApp;
    // IWXAPI 是第三方app和微信通信的openapi接口
    public static IWXAPI api;
    private PreferencesHelper mPreferencesHelper;
    private String TAG = "LocalApplication";
    //public DbUtils dbUtils = null;

    //public HttpUtils httpUtils = null;

    // 当前屏幕的高宽
    public int screenW = 0;
    public int screenH = 0;
    private static Context context;

    // 单例模式中获取唯一的MyApplication实例
    public static LocalApplication getInstance() {
        if (instance == null) {
            instance = new LocalApplication();
        }
        return instance;
    }

    public static Context getContext() {
        return context;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // initialize最好放在attachBaseContext最前面，初始化直接在Application类里面，切勿封装到其他类
        SophixManager.getInstance().setContext(this)

                .setAppVersion("1.2.4")
                .setAesKey(null)
                .setEnableDebug(true)

                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                        // 补丁加载回调通知
                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                            // 表明补丁加载成功
                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {

                            // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
                            // 建议: 用户可以监听进入后台事件, 然后调用killProcessSafely自杀，以此加快应用补丁，详见1.3.2.3
                        } else {
                            // 其它错误信息, 查看PatchStatus类说明
                        }
                        //LogUtil.e("Sophix","info:"+info+","+(code == PatchStatus.CODE_LOAD_SUCCESS)+","+(code == PatchStatus.CODE_LOAD_RELAUNCH)+",mode:");
                    }
                }).initialize();
        //LogUtil.e("Sophix",MainActivity.getVersionName(this));
        //LogUtil.e("Sophix","我重新修改了");
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
        return "1.2.4";
    }

    @Override
    public void onCreate() {


        super.onCreate();

        context = this;



        try {
            RongPushClient.registerHWPush(this);
            RongPushClient.registerMiPush(this, CONSTS.XIAO_MI_APP_ID, CONSTS.XIAO_MI_APP_KEY);
            RongPushClient.registerHWPush(this);
            //融云初始化
            RongIM.init(this);

            RongPushClient.checkManifest(this);
        } catch (RongException e) {
            e.printStackTrace();
            LogUtil.e(TAG, "rongyun:" + e.getMessage());
        }
        RongIM.getInstance().registerConversationTemplate(new GroupProviderTemp());
        RongExtensionManager.getInstance().registerExtensionModule(new DefaultExtensionModule());
        SealAppContext.init(this);
        //极光推送初始化
        JPushInterface.setDebugMode(false);
        JPushInterface.init(this);
        //微信初始化
        api = WXAPIFactory.createWXAPI(this, CONSTS.WECHAT_APP_ID);
        api.registerApp(CONSTS.WECHAT_APP_ID);

        //Xutils3 初始化
        x.Ext.init(this);
        // 设置是否输出debug
        x.Ext.setDebug(true);
        instance = this;
        // 得到屏幕的宽度和高度
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenW = dm.widthPixels;
        screenH = dm.heightPixels;

        //云巴推送

        YunBaManager.setThirdPartyEnable(getApplicationContext(), true);
        YunBaManager.setXMRegister("2882303761517633611", "5521763359611");
        YunBaManager.start(getApplicationContext());

        YunBaManager.subscribe(getApplicationContext(), new String[]{"t1"}, new IMqttActionListener() {

            @Override
            public void onSuccess(IMqttToken arg0) {
                Log.d(TAG, "Subscribe topic succeed");
            }

            @Override
            public void onFailure(IMqttToken arg0, Throwable arg1) {
                Log.d(TAG, "Subscribe topic failed");
            }
        });
        LoggerInterface newLogger = new LoggerInterface() {
            @Override
            public void setTag(String tag) {
                // ignore
                Log.d(TAG, "tag:"+tag);
            }
            @Override
            public void log(String content, Throwable t) {
                Log.d(TAG, content, t);
            }
            @Override
            public void log(String content) {
                Log.d(TAG, content);
            }
        };
        Logger.setLogger(this, newLogger);

        //qqbugly
        CrashReport.initCrashReport(getApplicationContext(), "d6c1b71817", false);


    }

    public static LocalApplication get() {
        LocalApplication inst = sApp;  // <<< 在这里创建临时变量
        if (inst == null) {
            synchronized (LocalApplication.class) {
                inst = sApp;
                if (inst == null) {
                    inst = new LocalApplication();
                    sApp = inst;
                }
            }
        }
        return inst;  // <<< 注意这里返回的是临时变量
    }

    public PreferencesHelper getPreferencesHelper() {
        if (mPreferencesHelper == null)
            mPreferencesHelper = new PreferencesHelper(this);
        return mPreferencesHelper;
    }
}
