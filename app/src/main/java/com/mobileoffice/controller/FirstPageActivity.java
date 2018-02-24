package com.mobileoffice.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.mobileoffice.MainActivity;
import com.mobileoffice.R;
import com.mobileoffice.SealAppContext;
import com.mobileoffice.controller.login.LoginActivity;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.ContactListJson;
import com.mobileoffice.json.ContactPersonJson;
import com.mobileoffice.json.WechatJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.UserInfo;

/**
 * Created by 99213 on 2017/7/17.
 */

public class FirstPageActivity extends AppCompatActivity {
    private Context mContext;
    private String TAG = "FirstPageActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.first_page);

        skip();

    }


    /**
     * 1、欢迎界面，延迟俩秒跳转
     * 2、如果第一次进入，跳转到skipActivity，实现viewpager导航
     * 3、如果不是第一次进入则跳转到登录界面，实现登录
     * 4、否则进入主界面
     */
    @SuppressLint("HandlerLeak")
    private void skip() {

        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    Intent intent;
                    //ToastUtil.showToast(SharePreUtils.getBoolean("first", false, mContext)+"",mContext);
                    if (!SharePreUtils.getBoolean("first", false, mContext)) {
                        intent = new Intent(FirstPageActivity.this, SkipActivity.class);

                    } else {
                        if (SharePreUtils.getBoolean("confirm", false, mContext)) {
                            intent = new Intent(FirstPageActivity.this, MainActivity.class);
                            loadPhoneDataModify(SharePreUtils.getString("phone","",FirstPageActivity.this));
                        } else {
                            intent = new Intent(FirstPageActivity.this, LoginActivity.class);
                        }

                    }

                    startActivity(intent);
                    finish();
                }
            }
        }.sendEmptyMessageDelayed(1, 2000);
        boolean confirm = SharePreUtils.getBoolean("confirm", false, mContext);
        if (confirm) {
            String token = SharePreUtils.getString("token", "", this);
            //ToastUtil.showToast("token:"+token,this);
            RongIM.connect(token, SealAppContext.getInstance().getConnectCallback());

        }
        loadContactPerson();
        getContactList();

    }

    /**
     * 加载联系人
     */
    private void getContactList() {

        RequestParams params = new RequestParams(URL.CONTACT);
        params.addBodyParameter("action", "getContactList");
        params.addBodyParameter("phone", SharePreUtils.getString("phone", "", this));
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
                                UserInfo mUserInfo;
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

    /**
     * 加载群组信息
     */
    private void loadContactPerson() {

        RequestParams params = new RequestParams(URL.RONG);
        params.addBodyParameter("action", "getGroupList");
        params.addBodyParameter("phone", SharePreUtils.getString("phone", "", this));
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ContactPersonJson contactPersonJson = new Gson().fromJson(result, ContactPersonJson.class);
                if (contactPersonJson != null && contactPersonJson.getResult() == CONSTS.SEND_OK) {

                    List<ContactPersonJson.ObjBean> mObjBean = contactPersonJson.getObj();

                    //ToastUtil.showToast(contactPersonJson.getMsg()+","+contactPersonJson.getObj().size(),FirstPageActivity.this);
                    for (int i = 0; i < mObjBean.size(); i++) {
                        if (mObjBean.get(i).getOrganSeg() == null || mObjBean.get(i).getGroupName() == null) {
                            continue;
                        }

                        //                        if(data.getUIConversationTitle().contains("心脏")){
//                            defaultId1 = R.drawable.msg_1index_team1;
//                        }else if(data.getUIConversationTitle().contains("肝")){
//                            defaultId1 = R.drawable.msg_1index_team2;
//                        }else if(data.getUIConversationTitle().contains("肺")){
//                            defaultId1 = R.drawable.msg_1index_team3;
//                        }else if(data.getUIConversationTitle().contains("肾")){
//                            defaultId1 = R.drawable.msg_1index_team4;
//                        }else if(data.getUIConversationTitle().contains("胰脏")){
//                            defaultId1 = R.drawable.msg_1index_team5;
//                        }else if(data.getUIConversationTitle().contains("眼角膜")){
//                            defaultId1 = R.drawable.msg_1index_team6;
//                        }

                        if (mObjBean.get(i).getGroupName().contains("-心脏")) {
                            RongIM.getInstance().refreshGroupInfoCache(new Group(mObjBean.get(i).getOrganSeg(), mObjBean.get(i).getGroupName(), Uri.parse(URL.TOMCAT_SOCKET+"images/team1.png")));
                        } else if (mObjBean.get(i).getGroupName().contains("-肝脏")) {
                            LogUtil.e(TAG,"groupName:"+mObjBean.get(i).getGroupName());
                            RongIM.getInstance().refreshGroupInfoCache(new Group(mObjBean.get(i).getOrganSeg(), mObjBean.get(i).getGroupName(), Uri.parse(URL.TOMCAT_SOCKET+"images/team2.png")));
                        } else if (mObjBean.get(i).getGroupName().contains("-肺")) {
                            RongIM.getInstance().refreshGroupInfoCache(new Group(mObjBean.get(i).getOrganSeg(), mObjBean.get(i).getGroupName(), Uri.parse(URL.TOMCAT_SOCKET+"images/team3.png")));
                        } else if (mObjBean.get(i).getGroupName().contains("-肾脏")) {
                            RongIM.getInstance().refreshGroupInfoCache(new Group(mObjBean.get(i).getOrganSeg(), mObjBean.get(i).getGroupName(), Uri.parse(URL.TOMCAT_SOCKET+"images/team4.png")));
                        } else if (mObjBean.get(i).getGroupName().contains("-胰脏")) {
                            RongIM.getInstance().refreshGroupInfoCache(new Group(mObjBean.get(i).getOrganSeg(), mObjBean.get(i).getGroupName(), Uri.parse(URL.TOMCAT_SOCKET+"images/team5.png")));
                        } else if (mObjBean.get(i).getGroupName().contains("-眼角膜")) {
                            RongIM.getInstance().refreshGroupInfoCache(new Group(mObjBean.get(i).getOrganSeg(), mObjBean.get(i).getGroupName(), Uri.parse(URL.TOMCAT_SOCKET+"images/team6.png")));
                        } else {
                            RongIM.getInstance().refreshGroupInfoCache(new Group(mObjBean.get(i).getOrganSeg(), mObjBean.get(i).getGroupName(), Uri.parse(URL.TOMCAT_SOCKET+"images/team.png")));
                        }
                    }


                } else {

                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //ToastUtil.showToast("ex:"+ex.getMessage(),FirstPageActivity.this);
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
                //ToastUtil.showToast("result:"+result,FirstPageActivity.this);
                if (wechatJson != null && wechatJson.getResult() == CONSTS.SEND_OK) {
                    //存储在本地
                    SharePreUtils.putString("phone", phone, FirstPageActivity.this);
                    //手机已存在
                    if (wechatJson.getObj() != null && "0".equals(wechatJson.getObj().getStatus())) {
                        WechatJson.ObjBean objBean = wechatJson.getObj();
                        SharePreUtils.putString("bind", objBean.getBind(), FirstPageActivity.this);
                        SharePreUtils.putString("name", objBean.getHospital_name(), FirstPageActivity.this);
                        SharePreUtils.putString("flag", objBean.getIs_upload_photo(), FirstPageActivity.this);
                        SharePreUtils.putString("photoFile", objBean.getPhoto_url(), FirstPageActivity.this);
                        SharePreUtils.putString("token", objBean.getToken(), FirstPageActivity.this);
                        SharePreUtils.putString("trueName", objBean.getTrue_name(), FirstPageActivity.this);
                        SharePreUtils.putString("wechatName", objBean.getWechat_name(), FirstPageActivity.this);
                        SharePreUtils.putString("wechatUrl", objBean.getWechat_url(), FirstPageActivity.this);
                        SharePreUtils.putString("hospital", objBean.getHospital_name(), FirstPageActivity.this);
                        SharePreUtils.putString("isUpdate", objBean.getIs_update(), FirstPageActivity.this);
                        SharePreUtils.putString("roleName", objBean.getRoleName(), FirstPageActivity.this);
                        SharePreUtils.putInt("roleId", Integer.parseInt(objBean.getRole_id()), FirstPageActivity.this);
                        SharePreUtils.putBoolean("confirm", true, FirstPageActivity.this);
                        SharePreUtils.putString("create_time", objBean.getCreate_time(), FirstPageActivity.this);
                        SharePreUtils.putString("temperatureStatus", objBean.getTemperatureStatus(), FirstPageActivity.this);
                        SharePreUtils.putString("openStatus", objBean.getOpenStatus(), FirstPageActivity.this);
                        SharePreUtils.putString("collisionStatus", objBean.getCollisionStatus(), FirstPageActivity.this);


                    }

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //ToastUtil.showToast(ex.getMessage(),FirstPageActivity.this);
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
