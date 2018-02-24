package com.mobileoffice.controller.message.contact;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobileoffice.MainActivity;
import com.mobileoffice.R;
import com.mobileoffice.SealAppContext;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.controller.login.ConfirmActivity;
import com.mobileoffice.controller.login.LoginActivity;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.ContactSearchJson;
import com.mobileoffice.json.PersonInfoJson;
import com.mobileoffice.json.PhotoJson;
import com.mobileoffice.json.RoleJson;
import com.mobileoffice.json.WechatJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;
import com.mobileoffice.view.RoundImageView;
import com.squareup.picasso.Picasso;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import io.rong.imkit.RongIM;

/**
 * Created by 99213 on 2017/6/25.
 */

public class ContactPersonInfoActivity extends BaseActivity implements View.OnClickListener {
    private ContactSearchJson.ObjBean mObjBean;

    private LinearLayout ll_contact_person_info_back;
    private RoundImageView riv_contact_person_info;
    private TextView tv_contact_person_info_name;
    private TextView tv_contact_person_info_phone;
    private TextView tv_contact_person_info_hospital;
    private Button btn_contact_person_info_add;
    private LinearLayout ll_contact_person_info_send;
    private LinearLayout ll_contact_person_info_call;

    private TextView tv_contact_person_info_del;
    private String phone;

    //删除
    private LinearLayout ll_contact_person_info_delete;

    private String name;
    private String otherPhone;
    private String hospital;
    private String bind;
    private String is_upload_photo;
    private String photoFile;
    private String wechatUrl;
    private int is_friend;
    private String TAG = "ContactPersonInfoActivity";
    private AlertDialog.Builder mAlertDialog;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private String contactPhone = null;
    @Override
    protected void initVariable() {
        phone = SharePreUtils.getString("phone", "", this);
        mObjBean = (ContactSearchJson.ObjBean) getIntent().getSerializableExtra("contactPerson");
        contactPhone = getIntent().getStringExtra("contactPhone");
        if(contactPhone!=null){
            loadPhoneData(contactPhone);
            ll_contact_person_info_delete.setVisibility(View.GONE);
        }else {
            LogUtil.e(TAG, "result:" + mObjBean);
            initPersonData();
        }

    }
    /**
     * 加载用户信息
     */
    private void loadPhoneData(final String phone) {
        RequestParams params = new RequestParams(URL.USER);
        params.addQueryStringParameter("action", "personInfo");
        params.addQueryStringParameter("phone", phone);
         showWaitDialog("加载中...",false,"loading");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                PersonInfoJson personInfoJson = gson.fromJson(result,PersonInfoJson.class);
                if(personInfoJson!=null&&personInfoJson.getResult()==CONSTS.SEND_OK){
                    mObjBean = new ContactSearchJson.ObjBean();
                    PersonInfoJson.ObjBean newObjBean = personInfoJson.getObj();
                    mObjBean.setName(newObjBean.getHospital_name());
                    mObjBean.setTrue_name(newObjBean.getTrue_name());
                    mObjBean.setBind(newObjBean.getBind());
                    mObjBean.setPhone(newObjBean.getPhone());
                    mObjBean.setPhoto_url(newObjBean.getPhoto_url());
                    mObjBean.setWechat_url(newObjBean.getWechat_url());
                    mObjBean.setIs_upload_photo(newObjBean.getIs_upload_photo());
                    mObjBean.setIs_friend(0);
                    initPersonData();

                }else{
                    ToastUtil.showToast("没有该人员的信息",ContactPersonInfoActivity.this);
                    finish();
                }
                dismissDialog();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                    dismissDialog();
                ToastUtil.showToast("没有该人员的信息",ContactPersonInfoActivity.this);
                finish();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }
    private void initPersonData(){
        if (mObjBean != null) {
            hospital = mObjBean.getName();
            name = mObjBean.getTrue_name();
            bind = mObjBean.getBind();
            otherPhone = mObjBean.getPhone();
            photoFile = mObjBean.getPhoto_url();
            wechatUrl = mObjBean.getWechat_url();
            is_upload_photo = mObjBean.getIs_upload_photo();
            is_friend = mObjBean.getIs_friend();

            tv_contact_person_info_name.setText(name);
            tv_contact_person_info_hospital.setText(hospital);
            //绑定手机

            tv_contact_person_info_phone.setText(otherPhone);

            //微信
            if ("0".equals(is_upload_photo) && wechatUrl != null && !"".equals(wechatUrl)) {
                Picasso.with(this).load(wechatUrl).error(R.drawable.msg_2list_linkman).into(riv_contact_person_info);
            } else if ("1".equals(is_upload_photo) && photoFile != null && !"".equals(photoFile)) {
                Picasso.with(this).load(photoFile).error(R.drawable.msg_2list_linkman).into(riv_contact_person_info);
            } else {
                if (wechatUrl != null && !"".equals(wechatUrl)) {
                    Picasso.with(this).load(wechatUrl).error(R.drawable.msg_2list_linkman).into(riv_contact_person_info);
                } else if (photoFile != null && !"".equals(photoFile)) {
                    Picasso.with(this).load(photoFile).error(R.drawable.msg_2list_linkman).into(riv_contact_person_info);
                } else {
                    riv_contact_person_info.setImageResource(R.drawable.msg_2list_linkman);
                }
            }
            //ToastUtil.showToast("is_upload_photo:" + is_upload_photo + ",wechartUrl:" + wechatUrl + ",photoFile:" + photoFile, this);
            //朋友
            if (is_friend == 0) {
                tv_contact_person_info_del.setVisibility(View.VISIBLE);
                ll_contact_person_info_send.setVisibility(View.VISIBLE);
                ll_contact_person_info_call.setVisibility(View.VISIBLE);
                btn_contact_person_info_add.setVisibility(View.GONE);
            } else if (is_friend == 1) {
                tv_contact_person_info_del.setVisibility(View.GONE);
                ll_contact_person_info_send.setVisibility(View.GONE);
                ll_contact_person_info_call.setVisibility(View.GONE);
                btn_contact_person_info_add.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.contact_person_info);
        ll_contact_person_info_back = (LinearLayout) findViewById(R.id.ll_contact_person_info_back);
        riv_contact_person_info = (RoundImageView) findViewById(R.id.riv_contact_person_info);
        tv_contact_person_info_name = (TextView) findViewById(R.id.tv_contact_person_info_name);

        tv_contact_person_info_phone = (TextView) findViewById(R.id.tv_contact_person_info_phone);
        tv_contact_person_info_hospital = (TextView) findViewById(R.id.tv_contact_person_info_hospital);
        btn_contact_person_info_add = (Button) findViewById(R.id.btn_contact_person_info_add);
        ll_contact_person_info_send = (LinearLayout) findViewById(R.id.ll_contact_person_info_send);
        ll_contact_person_info_call = (LinearLayout) findViewById(R.id.ll_contact_person_info_call);
        tv_contact_person_info_del = (TextView) findViewById(R.id.tv_contact_person_info_del);
        ll_contact_person_info_delete = (LinearLayout) findViewById(R.id.ll_contact_person_info_delete);


        ll_contact_person_info_back.setOnClickListener(this);
        btn_contact_person_info_add.setOnClickListener(this);
        ll_contact_person_info_send.setOnClickListener(this);
        ll_contact_person_info_call.setOnClickListener(this);
        //tv_contact_person_info_del.setOnClickListener(this);
        ll_contact_person_info_delete.setOnClickListener(this);

    }

    @Override
    protected void initData() {

    }

    private void loadAdd(final View view) {
        RequestParams params = new RequestParams(URL.PUSH);
        String trueName = SharePreUtils.getString("trueName", "", this);
        params.addBodyParameter("action", "add");
        params.addBodyParameter("phone", SharePreUtils.getString("phone","",this));
        params.addBodyParameter("content", trueName + " 请求添加好友");
        params.addBodyParameter("type", "add");
        params.addBodyParameter("otherId", mObjBean.getOther_id() + "");
        params.addBodyParameter("targetPhone", otherPhone);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                PhotoJson photoJson = gson.fromJson(result, PhotoJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {

                    ((Button) view).setText("验证中");
                    ((Button) view).setBackgroundResource(R.drawable.edit_border_gray);
                    ((Button) view).setEnabled(false);

                }
                LogUtil.e(TAG, "result:" + result);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_contact_person_info_back:
                finish();
                break;


            //添加联系人
            case R.id.btn_contact_person_info_add:
                loadAdd(v);
                break;

            //发送消息
            case R.id.ll_contact_person_info_send:
                LogUtil.e(TAG,"otherPhone:"+otherPhone+",name:"+name);
                RongIM.getInstance().startPrivateChat(this, otherPhone, name);
                break;

            //拨打电话
            case R.id.ll_contact_person_info_call:

                    // 检查是否获得了权限（Android6.0运行时权限）
                    if (ContextCompat.checkSelfPermission(ContactPersonInfoActivity.this,
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // 没有获得授权，申请授权
                        if (ActivityCompat.shouldShowRequestPermissionRationale(ContactPersonInfoActivity.this,
                                Manifest.permission.CALL_PHONE)) {
                            // 返回值：
//                          如果app之前请求过该权限,被用户拒绝, 这个方法就会返回true.
//                          如果用户之前拒绝权限的时候勾选了对话框中”Don’t ask again”的选项,那么这个方法会返回false.
//                          如果设备策略禁止应用拥有这条权限, 这个方法也返回false.
                            // 弹窗需要解释为何需要该权限，再次请求授权
                            ToastUtil.showToast("未授权！", ContactPersonInfoActivity.this);

                            // 帮跳转到该应用的设置界面，让用户手动授权
//                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                            Uri uri = Uri.fromParts("package", getPackageName(), null);
//                            intent.setData(uri);
//                            startActivity(intent);
                        } else {
                            // 不需要解释为何需要该权限，直接请求授权
                            ActivityCompat.requestPermissions(ContactPersonInfoActivity.this,
                                    new String[]{Manifest.permission.CALL_PHONE},
                                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
                        }
                    } else {
                        // 已经获得授权，可以打电话
                        CallPhone();
                    }


                break;
            //删除
            case R.id.ll_contact_person_info_delete:

                //ToastUtil.showToast("gg",this);
                mAlertDialog = new AlertDialog.Builder(this);
                mAlertDialog.setMessage("您确定要删除该联系人吗?");
                mAlertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                mAlertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteContact();

                    }
                });
                mAlertDialog.show();
                break;
        }
    }

    private void deleteContact() {
        RequestParams params = new RequestParams(URL.CONTACT);
        params.addBodyParameter("action", "delete");
        params.addBodyParameter("phone", phone);
        params.addBodyParameter("otherPhone", otherPhone);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    ToastUtil.showToast("删除联系人成功", ContactPersonInfoActivity.this);
                    finish();
                } else {
                    ToastUtil.showToast("删除联系人失败", ContactPersonInfoActivity.this);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.showToast("删除联系人失败", ContactPersonInfoActivity.this);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    // 处理权限申请的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 授权成功，继续打电话
                    CallPhone();
                } else {
                    // 授权失败！
                    ToastUtil.showToast("未授权拨打电话权限", this);

                }
                break;
            }
        }

    }

    private void CallPhone() {

        // 拨号：激活系统的拨号组件
        Intent intent = new Intent(); // 意图对象：动作 + 数据
        intent.setAction(Intent.ACTION_CALL); // 设置动作
        Uri data = Uri.parse("tel:" + otherPhone); // 设置数据
        intent.setData(data);
        startActivity(intent); // 激活Activity组件

    }
}
