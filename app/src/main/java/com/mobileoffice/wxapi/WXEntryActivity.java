package com.mobileoffice.wxapi;

import com.google.gson.Gson;
import com.mobileoffice.MainActivity;
import com.mobileoffice.R;
import com.mobileoffice.SealAppContext;
import com.mobileoffice.application.LocalApplication;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.controller.login.ConfirmActivity;
import com.mobileoffice.controller.login.LoginActivity;
import com.mobileoffice.json.WechatJson;
import com.mobileoffice.http.URL;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;

import io.rong.imkit.RongIM;


public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
    private String TAG = "WXEntryActivity";
    // 请求access_token地址格式，要替换里面的APPID，SECRET还有CODE
    public static String GetCodeRequest = "https://api.weixin.qq.com/sns/oauth2/access_token?"
            + "appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
    // 请求unionid地址格式，要替换里面的ACCESS_TOKEN和OPENID
    public static String GetUnionIDRequest = "https://api.weixin.qq.com/sns/userinfo?"
            + "access_token=ACCESS_TOKEN&openid=OPENID";
    private String newGetCodeRequest = "";
    private String newGetUnionIDRequest = "";
    private String mOpenId = "";
    private String mAccess_token = "";


    @Override
    protected void initVariable() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        if(CONSTS.WECHAT_SHARE_LOGIN ==1){
            finish();
        }
        LocalApplication.api.handleIntent(getIntent(), this);
    }

    @Override
    protected void initData() {

    }


    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
        LogUtil.e(TAG, "onReq:" + req.getType());
        //ToastUtil.showToast("req:" + req);
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:

                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:

                break;
            default:
                break;
        }
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        int result = resp.getType();
        if (result == 1) {
            switch (resp.errCode) {
                // 同意授权
                case BaseResp.ErrCode.ERR_OK:

                    SendAuth.Resp respLogin = (SendAuth.Resp) resp;
                    // 获得code
                    String code = respLogin.code;

                    // 把code，APPID，APPSECRET替换到要请求的地址里，成为新的请求地址
                    newGetCodeRequest = getCodeRequest(code);
                    RequestParams params = new RequestParams(newGetCodeRequest);
                    x.http().post(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String response) {
                            LogUtil.e(TAG, "responseOut:" + response);
                            parseAccessTokenJSON(response);
                            // 将解析得到的access_token和openid在请求unionid地址里替换
                            newGetUnionIDRequest = getUnionID(mAccess_token, mOpenId);
                            // 请求新的unionid地址，解析出返回的unionid等数据
                            RequestParams innerParams = new RequestParams(newGetUnionIDRequest);
                            x.http().post(innerParams, new CommonCallback<String>() {
                                @Override
                                public void onSuccess(String response) {
                                    LogUtil.e(TAG, "responseInner:" + response);
                                    parseUnionIdJson(response);

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
                        public void onError(Throwable ex, boolean isOnCallback) {

                        }

                        @Override
                        public void onCancelled(CancelledException cex) {

                        }

                        @Override
                        public void onFinished() {

                        }
                    });

                    Timer timer = new Timer();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {// TODO Auto-generated method stub
                            WXEntryActivity.this.finish();
                        }
                    };
                    timer.schedule(task, 1000);
                    break;
                // 拒绝授权
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    finish();
                    break;
                // 取消操作
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    finish();
                    break;
                default:
                    break;
            }

        }

    }

    /**
     * * 替换GetCodeRequest 将APP ID，APP SECRET，code替换到链接里 * * @param code *
     * 授权时，微信回调给的 * @return URL
     */
    public static String getCodeRequest(String code) {
        String result = null;
        GetCodeRequest = GetCodeRequest.replace("APPID", urlEnodeUTF8(CONSTS.WECHAT_APP_ID));
        GetCodeRequest = GetCodeRequest.replace("SECRET", urlEnodeUTF8(CONSTS.WECHAT_APP_SECRET));
        GetCodeRequest = GetCodeRequest.replace("CODE", urlEnodeUTF8(code));
        result = GetCodeRequest;
        return result;
    }

    /**
     * * 替换GetUnionID * * @param access_token * @param open_id * @return
     */
    public static String getUnionID(String access_token, String open_id) {
        String result = null;
        GetUnionIDRequest = GetUnionIDRequest.replace("ACCESS_TOKEN", urlEnodeUTF8(access_token));
        GetUnionIDRequest = GetUnionIDRequest.replace("OPENID", urlEnodeUTF8(open_id));
        result = GetUnionIDRequest;
        return result;
    }


    public static String urlEnodeUTF8(String code) {
        String result = code;
        try {
            result = URLEncoder.encode(code, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * * 解析access_token返回的JSON数据 * * @param response
     */
    private void parseAccessTokenJSON(String response) {
        // TODO Auto-generated method stubtry
        try {
            JSONObject jsonObject = new JSONObject(response);
            mAccess_token = jsonObject.getString("access_token");
            String expiresIn = jsonObject.getString("expires_in");
            String refreshToken = jsonObject.getString("refresh_token");
            mOpenId = jsonObject.getString("openid");
            String scope = jsonObject.getString("scope");
            //将获取到的数据写进SharedPreferences里
            SharePreUtils.putString("access_token", mAccess_token, this);
            SharePreUtils.putString("expires_in", expiresIn, this);
            SharePreUtils.putString("refresh_token", refreshToken, this);
            SharePreUtils.putString("openid", mOpenId, this);
            SharePreUtils.putString("scope", scope, this);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * * 解析unionid数据 * @param response
     */
    private void parseUnionIdJson(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            String openid = jsonObject.getString("openid");
            String nickname = jsonObject.getString("nickname");
            String sex = jsonObject.getString("sex");
            String province = jsonObject.getString("province");
            String city = jsonObject.getString("city");
            String country = jsonObject.getString("country");
            String headimgurl = jsonObject.getString("headimgurl");
            String unionid = jsonObject.getString("unionid");
            SharePreUtils.putString("wechatName", nickname, this);
            SharePreUtils.putString("wechatUrl", headimgurl, this);
            SharePreUtils.putBoolean("haveSign", true, this);
            SharePreUtils.putString("wechatUuid", unionid, this);
            loadWechatData(nickname,headimgurl,unionid);
            //

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * wechat info
     */
    private void loadWechatData(String wechatName, String wechatUrl, final String wechatUuid) {
        RequestParams params = new RequestParams(URL.USER);
        params.addQueryStringParameter("action", "wechat");
        params.addQueryStringParameter("wechatName", wechatName);
        params.addQueryStringParameter("wechatUrl", wechatUrl);
        params.addQueryStringParameter("wechatUuid", wechatUuid);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                WechatJson wechatJson = gson.fromJson(result, WechatJson.class);
                Log.e(TAG,"result:"+result);
                if (wechatJson != null && wechatJson.getResult() == CONSTS.SEND_OK) {
                       //微信已存在
                      if(wechatJson.getObj()!=null&&"0".equals(wechatJson.getObj().getStatus())){
                          WechatJson.ObjBean objBean = wechatJson.getObj();
                          SharePreUtils.putString("phone",objBean.getPhone(),WXEntryActivity.this);
                          SharePreUtils.putString("bind",objBean.getBind(),WXEntryActivity.this);
                          SharePreUtils.putString("name",objBean.getHospital_name(),WXEntryActivity.this);
                          SharePreUtils.putString("flag",objBean.getIs_upload_photo(),WXEntryActivity.this);
                          SharePreUtils.putString("photoFile",objBean.getPhoto_url(),WXEntryActivity.this);
                          SharePreUtils.putString("token",objBean.getToken(),WXEntryActivity.this);
                          SharePreUtils.putString("trueName",objBean.getTrue_name(),WXEntryActivity.this);
                          SharePreUtils.putString("wechatName",objBean.getWechat_name(),WXEntryActivity.this);
                          SharePreUtils.putString("wechatUrl",objBean.getWechat_url(),WXEntryActivity.this);
                          SharePreUtils.putString("hospital",objBean.getHospital_name(),WXEntryActivity.this);
                          SharePreUtils.putString("isUpdate", objBean.getIs_update(), WXEntryActivity.this);
                          SharePreUtils.putString("roleName", objBean.getRoleName(), WXEntryActivity.this);
                          SharePreUtils.putInt("roleId", Integer.parseInt(objBean.getRole_id()), WXEntryActivity.this);
                          SharePreUtils.putBoolean("confirm", true, WXEntryActivity.this);
                          SharePreUtils.putString("create_time",objBean.getCreate_time(),WXEntryActivity.this);
                          SharePreUtils.putString("temperatureStatus", objBean.getTemperatureStatus(), WXEntryActivity.this);
                          SharePreUtils.putString("openStatus", objBean.getOpenStatus(), WXEntryActivity.this);
                          SharePreUtils.putString("collisionStatus", objBean.getCollisionStatus(), WXEntryActivity.this);
                          Intent i = new Intent(WXEntryActivity.this, MainActivity.class);
                          String token = SharePreUtils.getString("token", "", WXEntryActivity.this);
                          RongIM.connect(token, SealAppContext.getInstance().getConnectCallback());
                          startActivity(i);
                      }else{
                          Intent i = new Intent(WXEntryActivity.this, ConfirmActivity.class);
                          i.putExtra("wechatUuid",wechatUuid);
                          startActivity(i);
                      }

                } else {
                    ToastUtil.showToast("网络错误,请重试.",WXEntryActivity.this);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(TAG,"ex:"+ex.getMessage());
                ToastUtil.showToast("网络错误,请重试",WXEntryActivity.this);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }
}