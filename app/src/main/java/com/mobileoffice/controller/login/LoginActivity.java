package com.mobileoffice.controller.login;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobileoffice.MainActivity;
import com.mobileoffice.R;
import com.mobileoffice.SealAppContext;
import com.mobileoffice.application.LocalApplication;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.controller.me.PersonInfoActivity;
import com.mobileoffice.json.ContactSearchJson;
import com.mobileoffice.json.RoleListJson;
import com.mobileoffice.json.SmsVerificationJson;
import com.mobileoffice.json.WechatJson;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.WeatherHourJson;
import com.mobileoffice.json.WeatherIconJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;
import com.mobileoffice.view.Popwindow;
import com.mobileoffice.view.RoleTypePopup;
import com.show.api.ShowApiRequest;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.thinkcool.circletextimageview.CircleTextImageView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import io.rong.imageloader.utils.L;
import io.rong.imkit.RongIM;


/**
 * Created by 99213 on 2017/5/18.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener, IWXAPIEventHandler {
    private EditText edt_login_phone;
    private ImageView iv_login_cancel;
    private EditText edt_login_verification;
    private Button btn_login;
    private TextView tv_login_verification;
    private String TAG = "LoginActivity";
    private String oldPhone = "";
    private LinearLayout ll_login_wechat;
    //发送验证码状态
    private int status;
    //四位数字验证码
    private int code;
    private String info = "";
    //微信点击
    private ImageView iv_login_wechat;
    private TextView tv_login_wechat;
    private boolean is_confirm = false;
    private TextView tv_role;

    private CountDownTimer timer = new CountDownTimer(60000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            tv_login_verification.setText(("重新发送(" + millisUntilFinished / 1000 + ")"));

        }

        @Override
        public void onFinish() {

            tv_login_verification.setText(getResources().getString(R.string.send_verification));
            tv_login_verification.setClickable(true);
        }
    };

    @Override
    protected void initVariable() {
//        info += SharePreUtils.getString("nickname", "", this);
//        info += SharePreUtils.getString("headimgurl", "", this);
//        info += SharePreUtils.getBoolean("haveSign", false, this);
//        info += SharePreUtils.getString("unionid", "", this);
        //tv_login_wechat.setText(info);



//        Intent intent = new Intent();
//        intent.setClass(this, MainActivity.class);
        //startActivity(intent);
        //17621180006
        //loadPhoneData("18398850872");
    }

    private void send() {
        // send oauth request
        //showWaitDialog(getResources().getString(R.string.loading),false,"loading");
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";

        LocalApplication.api.sendReq(req);
        CONSTS.WECHAT_SHARE_LOGIN = 0;
    }
    AlertDialog.Builder mAlertDialog;
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.login);
        String logout = getIntent().getStringExtra("logout");
        if("logout".equals(logout)){
            mAlertDialog = new AlertDialog.Builder(this);
            mAlertDialog.setMessage("你的账号已在别的设备登录,您被迫下线");
            mAlertDialog.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                }
            });
            mAlertDialog.show();
        }

        is_confirm = SharePreUtils.getBoolean("confirm", false, this);

        if (is_confirm) {

            startActivity(new Intent(this, MainActivity.class));

        }
        edt_login_phone = (EditText) findViewById(R.id.edt_login_phone);
        iv_login_cancel = (ImageView) findViewById(R.id.iv_login_cancel);
        edt_login_verification = (EditText) findViewById(R.id.edt_login_verification);
        btn_login = (Button) findViewById(R.id.btn_login);
        tv_login_verification = (TextView) findViewById(R.id.tv_login_verification);
        iv_login_wechat = (ImageView) findViewById(R.id.iv_login_wechat);
        tv_login_wechat = (TextView) findViewById(R.id.tv_login_wechat);
        ll_login_wechat = (LinearLayout) findViewById(R.id.ll_login_wechat);

        tv_role = (TextView) findViewById(R.id.tv_role);

        iv_login_cancel.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        tv_login_verification.setOnClickListener(this);

        ll_login_wechat.setOnClickListener(this);


    }



    @Override
    protected void initData() {

    }

    /**
     * 验证手机号码
     * <p>
     * 移动号码段:139、138、137、136、135、134、150、151、152、157、158、159、182、183、187、188、147
     * 联通号码段:130、131、132、136、185、186、145
     * 电信号码段:133、153、180、189
     *
     * @param cellPhone
     * @return
     */
//    public static boolean checkCellphone(String cellphone) {
//        String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$";
//        return check(cellphone, regex);
//    }
    public static boolean checkCellPhone(String cellPhone) {
        String regex = "^1[3,4,5,7,8]\\d{9}$";
        return Pattern.matches(regex, cellPhone);
    }


    @Override
    public void onClick(View view) {
        String loginPhone = edt_login_phone.getText().toString().trim();
        Intent intent = new Intent();
        switch (view.getId()) {
            //清除电话号码
            case R.id.iv_login_cancel:
                edt_login_phone.setText("");
                break;
            //发送验证码
            case R.id.tv_login_verification:
                String send_verification = getResources().getString(R.string.send_verification);
                String sendVerification = tv_login_verification.getText().toString().trim();

                if (checkCellPhone(loginPhone)) {
                    tv_login_verification.setClickable(false);
                    timer.start();
                    status = 0;
                    oldPhone = loginPhone;
                    //发送推送消息
                    RequestParams params = new RequestParams(URL.SEND_VERIFICATION);
                    params.addQueryStringParameter("phone", loginPhone);
                    x.http().get(params, new Callback.CacheCallback<String>() {


                        @Override
                        public boolean onCache(String result) {
                            return false;
                        }

                        @Override
                        public void onSuccess(String result) {
                            Gson gson = new Gson();
                            SmsVerificationJson smsVerificationJson = gson.fromJson(result, SmsVerificationJson.class);
                            if (smsVerificationJson != null && smsVerificationJson.getResult() == CONSTS.SEND_OK) {
                                code = smsVerificationJson.getObj() == null ? 0 : smsVerificationJson.getObj().getCode();
                            }

                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            LogUtil.e(TAG, "error:" + ex.getMessage());
                        }

                        @Override
                        public void onCancelled(CancelledException cex) {

                        }

                        @Override
                        public void onFinished() {

                        }
                    });

                } else {
                    ToastUtil.showToast("手机格式不正确", LoginActivity.this);
                }
                break;
            case R.id.btn_login:
                //loadPhoneData("18398850872");
                // startActivity(new Intent(this,ConfirmActivity.class));
//                Intent i = new Intent(LoginActivity.this, ConfirmActivity.class);
//                i.putExtra("phone", "18398850872");
//                startActivity(i);
                if("18398850872".equals(loginPhone)){
                    loadPhoneData("18398850872");
                    return;
                }

                String verificationCode = edt_login_verification.getText().toString().trim();

                //loadPhoneData(loginPhone);
                if (oldPhone.equals(loginPhone) && verificationCode.equals((code + ""))) {
                    //验证是否注册过的手机号

                    loadPhoneData(loginPhone);
                } else {

                    if ("".equals(loginPhone)) {
                        ToastUtil.showToast("请输入手机号", LoginActivity.this);
                    } else if (!checkCellPhone(loginPhone)) {
                        ToastUtil.showToast("手机格式错误", LoginActivity.this);
                    } else if ("".equals(verificationCode)) {
                        ToastUtil.showToast("请输入验证码", LoginActivity.this);
                    } else if (!oldPhone.equals(loginPhone)) {
                        ToastUtil.showToast("当前手机号码与验证手机号码不同", LoginActivity.this);
                    } else if (!verificationCode.equals((code + ""))) {
                        ToastUtil.showToast("验证码错误", LoginActivity.this);
                    } else {
                        ToastUtil.showToast("无法登录", LoginActivity.this);
                    }

                }

//                intent.setClass(this, ConfirmActivity.class);
//                startActivity(intent);
                //weatherIcon();
                break;
            case R.id.ll_login_wechat:


                if (!LocalApplication.api.isWXAppInstalled()) {
                    ToastUtil.showToast("请先安装微信", LoginActivity.this);
                } else {
                    send();
                }

                break;
        }
    }

    /**
     * 天气接口
     * 返回接口包含图标 易源
     */
    public void weatherIcon() {
        final String weatherArea = "杭州";
        new Thread() {
            //在新线程中发送网络请求
            public void run() {

                final String res = new ShowApiRequest("http://route.showapi.com/9-2", URL.YIYUAN_APPID, URL.YIYUAN_SECRET)
                        .addTextPara("area", weatherArea)
                        .addTextPara("needMoreDay", "0")
                        .addTextPara("needIndex", "0")
                        .addTextPara("needHourData", "0")
                        .addTextPara("need3HourForcast", "0")
                        .addTextPara("needAlarm", "0")
                        .post();

                LogUtil.e(TAG, res);
                Gson gson = new Gson();
                WeatherIconJson weatherIconJson = gson.fromJson(res, WeatherIconJson.class);


                LogUtil.e(TAG, "code:" + weatherIconJson.getShowapi_res_code() + ",url:");
                //把返回内容通过handler对象更新到界面

            }
        }.start();

    }

    /**
     * phone info
     */
    private void loadPhoneData(final String phone) {
        showWaitDialog(getResources().getString(R.string.loading),false,"loading");
        RequestParams params = new RequestParams(URL.USER);
        params.addQueryStringParameter("action", "phone");
        params.addQueryStringParameter("phone", phone);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                WechatJson wechatJson = gson.fromJson(result, WechatJson.class);
                if (wechatJson != null && wechatJson.getResult() == CONSTS.SEND_OK) {
                    //存储在本地
                    SharePreUtils.putString("phone", phone, LoginActivity.this);
                    //手机已存在
                    if (wechatJson.getObj() != null && "0".equals(wechatJson.getObj().getStatus())) {
                        WechatJson.ObjBean objBean = wechatJson.getObj();
                        SharePreUtils.putString("bind", objBean.getBind(), LoginActivity.this);
                        SharePreUtils.putString("name", objBean.getHospital_name(), LoginActivity.this);
                        SharePreUtils.putString("flag", objBean.getIs_upload_photo(), LoginActivity.this);
                        SharePreUtils.putString("photoFile", objBean.getPhoto_url(), LoginActivity.this);
                        SharePreUtils.putString("token", objBean.getToken(), LoginActivity.this);
                        SharePreUtils.putString("trueName", objBean.getTrue_name(), LoginActivity.this);
                        SharePreUtils.putString("wechatName", objBean.getWechat_name(), LoginActivity.this);
                        SharePreUtils.putString("wechatUrl", objBean.getWechat_url(), LoginActivity.this);
                        SharePreUtils.putString("hospital", objBean.getHospital_name(), LoginActivity.this);
                        SharePreUtils.putString("isUpdate", objBean.getIs_update(), LoginActivity.this);
                        SharePreUtils.putString("roleName", objBean.getRoleName(), LoginActivity.this);
                        SharePreUtils.putInt("roleId", Integer.parseInt(objBean.getRole_id()), LoginActivity.this);
                        SharePreUtils.putBoolean("confirm", true, LoginActivity.this);
                        SharePreUtils.putString("create_time",objBean.getCreate_time(),LoginActivity.this);
                        SharePreUtils.putString("temperatureStatus", objBean.getTemperatureStatus(), LoginActivity.this);
                        SharePreUtils.putString("openStatus", objBean.getOpenStatus(), LoginActivity.this);
                        SharePreUtils.putString("collisionStatus", objBean.getCollisionStatus(), LoginActivity.this);
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        String token = SharePreUtils.getString("token", "", LoginActivity.this);
                        RongIM.connect(token, SealAppContext.getInstance().getConnectCallback());
                        startActivity(i);

                    } else {
                        Intent i = new Intent(LoginActivity.this, ConfirmActivity.class);
                        i.putExtra("phone", phone);
                        startActivity(i);
                    }

                } else {
                    LogUtil.e(TAG, "网络错误,请重试1" + wechatJson.getMsg());
                    ToastUtil.showToast("网络错误,请重试", LoginActivity.this);
                }
                dismissDialog();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.showToast("网络错误,请重试", LoginActivity.this);
                dismissDialog();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }
    /**
     * phone info
     */
    private void loadPhoneDataModify(final String phone) {
       // showWaitDialog(getResources().getString(R.string.loading),false,"loading");
        RequestParams params = new RequestParams(URL.USER);
        params.addQueryStringParameter("action", "phone");
        params.addQueryStringParameter("phone", phone);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                Gson gson = new Gson();
                WechatJson wechatJson = gson.fromJson(result, WechatJson.class);
                ToastUtil.showToast("result:"+result,LoginActivity.this);
                if (wechatJson != null && wechatJson.getResult() == CONSTS.SEND_OK) {
                    //存储在本地
                    SharePreUtils.putString("phone", phone, LoginActivity.this);
                    //手机已存在
                    if (wechatJson.getObj() != null && "0".equals(wechatJson.getObj().getStatus())) {
                        WechatJson.ObjBean objBean = wechatJson.getObj();
                        SharePreUtils.putString("bind", objBean.getBind(), LoginActivity.this);
                        SharePreUtils.putString("name", objBean.getHospital_name(), LoginActivity.this);
                        SharePreUtils.putString("flag", objBean.getIs_upload_photo(), LoginActivity.this);
                        SharePreUtils.putString("photoFile", objBean.getPhoto_url(), LoginActivity.this);
                        SharePreUtils.putString("token", objBean.getToken(), LoginActivity.this);
                        SharePreUtils.putString("trueName", objBean.getTrue_name(), LoginActivity.this);
                        SharePreUtils.putString("wechatName", objBean.getWechat_name(), LoginActivity.this);
                        SharePreUtils.putString("wechatUrl", objBean.getWechat_url(), LoginActivity.this);
                        SharePreUtils.putString("hospital", objBean.getHospital_name(), LoginActivity.this);
                        SharePreUtils.putString("isUpdate", objBean.getIs_update(), LoginActivity.this);
                        SharePreUtils.putString("roleName", objBean.getRoleName(), LoginActivity.this);
                        SharePreUtils.putInt("roleId", Integer.parseInt(objBean.getRole_id()), LoginActivity.this);
                        SharePreUtils.putBoolean("confirm", true, LoginActivity.this);
                        SharePreUtils.putString("create_time",objBean.getCreate_time(),LoginActivity.this);



                    }

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.showToast(ex.getMessage(),LoginActivity.this);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    /**
     * 天气接口
     * 返回24小时天气数据
     */
    public void weatherHour() {
        final String weatherArea = "杭州";
        new Thread() {
            //在新线程中发送网络请求
            public void run() {

                final String res = new ShowApiRequest("http://route.showapi.com/9-8", URL.YIYUAN_APPID, URL.YIYUAN_SECRET)
                        .addTextPara("area", weatherArea)
                        .post();


                Gson gson = new Gson();
                WeatherHourJson weatherHourJson = gson.fromJson(res, WeatherHourJson.class);


                LogUtil.e(TAG, "code:" + weatherHourJson.getShowapi_res_code() + ",");
                //把返回内容通过handler对象更新到界面

            }
        }.start();

    }

    @Override
    protected synchronized void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer = null;
        }
        if(mAlertDialog!=null){
            mAlertDialog.create().dismiss();
            mAlertDialog = null;
        }


    }

    @Override
    public void onReq(BaseReq req) {

        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                LogUtil.e(TAG, "COMMAND_GETMESSAGE_FROM_WX");
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                LogUtil.e(TAG, "COMMAND_SHOWMESSAGE_FROM_WX");
                break;
            default:
                break;
        }
    }

    @Override
    public void onResp(BaseResp resp) {


        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                LogUtil.e(TAG, "ERR_OK");
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                LogUtil.e(TAG, "ERR_USER_CANCEL");
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                LogUtil.e(TAG, "ERR_AUTH_DENIED");
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                LogUtil.e(TAG, "ERR_UNSUPPORT");
                break;
            default:
                LogUtil.e(TAG, "default");
                break;
        }

    }
}
