package com.mobileoffice.controller.me;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobileoffice.R;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.controller.cloud_monitor.TransferSiteActivity;
import com.mobileoffice.controller.login.LoginActivity;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.PhotoJson;
import com.mobileoffice.utils.ActivityStack;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.CleanMessageUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import io.rong.imkit.RongIM;

/**
 * Created by 99213 on 2017/7/17.
 */

public class SiteActivity extends BaseActivity implements View.OnClickListener {
    //退出
    private TextView tv_logout;
    private LinearLayout ll_site_back;
    //自动更新
    private Switch switch_update;
    //清除缓存
    private LinearLayout ll_cache;
    private TextView tv_cache_size;

    private String phone;
    private String isUpdate;
    private AlertDialog.Builder mAlertDialog;


    //推送设置
    private Switch switch_temperature;
    private Switch switch_open;
    private Switch switch_collision;

    private String temperatureStatus;
    private String openStatus;
    private String collisionStatus;


    @Override
    protected void initVariable() {
        phone = SharePreUtils.getString("phone", "", this);
        isUpdate = SharePreUtils.getString("isUpdate", "", this);
        temperatureStatus = SharePreUtils.getString("temperatureStatus", "0", this);
        openStatus = SharePreUtils.getString("openStatus", "0", this);
        collisionStatus = SharePreUtils.getString("collisionStatus", "0", this);

        if("1".equals(temperatureStatus)){
            switch_temperature.setChecked(false);
        }
        if("1".equals(openStatus)){
            switch_open.setChecked(false);
        }
        if("1".equals(collisionStatus)){
            switch_collision.setChecked(false);
        }

        //设置是否更新
        if ("1".equals(isUpdate)) {
            switch_update.setChecked(true);
        }
        //ToastUtil.showToast("temperatureStatus:" + temperatureStatus + ",openStatus:" + openStatus + ",collisionStatus:" + collisionStatus, this);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.site);
        tv_logout = (TextView) findViewById(R.id.tv_logout);
        ll_site_back = (LinearLayout) findViewById(R.id.ll_site_back);
        switch_update = (Switch) findViewById(R.id.switch_update);
        switch_temperature = (Switch) findViewById(R.id.switch_temperature);
        switch_collision = (Switch) findViewById(R.id.switch_collision);
        switch_open = (Switch) findViewById(R.id.switch_open);
        ll_cache = (LinearLayout) findViewById(R.id.ll_cache);
        tv_cache_size = (TextView) findViewById(R.id.tv_cache_size);
        switch_update.setChecked(false);

        tv_logout.setOnClickListener(this);
        ll_site_back.setOnClickListener(this);
        switch_update.setOnClickListener(this);
        ll_cache.setOnClickListener(this);


        switch_temperature.setOnClickListener(this);
        switch_collision.setOnClickListener(this);
        switch_open.setOnClickListener(this);

        //设置缓存大小
        try {
            tv_cache_size.setText(CleanMessageUtil.getTotalCacheSize(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void initData() {

    }

    private void updateByPhone(final String phone, final String isUpdate) {
        showWaitDialog(getResources().getString(R.string.loading), true, "");
        RequestParams params = new RequestParams(URL.USER);
        params.addBodyParameter("action", "update");
        params.addBodyParameter("phone", phone);
        params.addBodyParameter("isUpdate", isUpdate);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    SharePreUtils.putString("isUpdate", isUpdate, SiteActivity.this);
                }
                dismissDialog();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
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

    private void setPushSite(final Switch s,final String phone, final String type,final String status) {
        //showWaitDialog(getResources().getString(R.string.loading), true, "");
        RequestParams params = new RequestParams(URL.USER);
        params.addBodyParameter("action", "setPushSite");
        params.addBodyParameter("phone", phone);
        params.addBodyParameter("type", type);
        params.addBodyParameter("status", status);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    if ("temperature".equals(type)) {
                        SharePreUtils.putString("temperatureStatus", status, SiteActivity.this);
                    } else if ("collision".equals(type)) {
                        SharePreUtils.putString("collisionStatus", status, SiteActivity.this);
                    } else if ("open".equals(type)) {
                        SharePreUtils.putString("openStatus", status, SiteActivity.this);
                    }
                }else {
                    if("temperature".equals(type)){
                        ToastUtil.showToast("温度示警设置失败",SiteActivity.this);
                        if("0".equals(status)){
                            s.setChecked(false);
                        }else{
                            s.setChecked(true);
                        }
                    }
                    if("collision".equals(type)){
                        ToastUtil.showToast("碰撞提醒设置失败",SiteActivity.this);
                        if("0".equals(status)){
                            s.setChecked(false);
                        }else{
                            s.setChecked(true);
                        }
                    }
                    if("open".equals(type)){
                        ToastUtil.showToast("开箱提醒设置失败",SiteActivity.this);
                        if("0".equals(status)){
                            s.setChecked(false);
                        }else{
                            s.setChecked(true);
                        }
                    }
                }

               // dismissDialog();

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //dismissDialog();
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
            case R.id.tv_logout:
                mAlertDialog = new AlertDialog.Builder(this);


                mAlertDialog.setMessage("是否退出当前账号");


                mAlertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                mAlertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        quit(true);
                    }
                });
                mAlertDialog.show();

                break;
            case R.id.ll_site_back:
                finish();
                break;
            case R.id.switch_update:
                //ToastUtil.showToast("is"+switch_update.isChecked(),this);
                if (switch_update.isChecked()) {
                    updateByPhone(phone, "1");
                } else {
                    updateByPhone(phone, "0");
                }
                break;
            case R.id.ll_cache:
                CleanMessageUtil.clearAllCache(this);
                ToastUtil.showToast("已清除多余的缓存", this);
                try {
                    tv_cache_size.setText(CleanMessageUtil.getTotalCacheSize(this));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.switch_temperature:
                if (switch_temperature.isChecked()) {
                    setPushSite(switch_temperature,phone, "temperature", "0");
                } else {
                    setPushSite(switch_temperature,phone, "temperature", "1");
                }
                break;
            case R.id.switch_open:
                if (switch_open.isChecked()) {
                    setPushSite(switch_temperature,phone, "open", "0");
                } else {
                    setPushSite(switch_temperature,phone, "open", "1");
                }
                break;
            case R.id.switch_collision:
                if (switch_collision.isChecked()) {
                    setPushSite(switch_temperature,phone, "collision", "0");
                } else {
                    setPushSite(switch_temperature,phone, "collision", "1");
                }
                break;
        }
    }

    private void quit(final boolean isKicked) {

        SharedPreferences.Editor editor = getSharedPreferences("config", Context.MODE_PRIVATE).edit();
        if (!isKicked) {
            editor.putBoolean("exit", true);
        }
        editor.putString("loginToken", "");
        //editor.putString(SealConst.SEALTALK_LOGIN_ID, "");
        editor.putInt("getAllUserInfoState", 0);
        editor.apply();
        /*//这些数据清除操作之前一直是在login界面,因为app的数据库改为按照userID存储,退出登录时先直接删除
        //这种方式是很不友好的方式,未来需要修改同app server的数据同步方式
        //SealUserInfoManager.getInstance().deleteAllUserInfo();*/
        //SealUserInfoManager.getInstance().closeDB();
        RongIM.getInstance().logout();

        SharePreUtils.putInt("unread_push_num", 0, this);
        SharePreUtils.putString("new_system_message", "", this);
        SharePreUtils.putString("new_system_time", "", this);

        Intent loginActivityIntent = new Intent();
        ActivityStack.getInstance().finishAllActivity();
        SharePreUtils.putBoolean("confirm", false, this);
        loginActivityIntent.setClass(this, LoginActivity.class);

        loginActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        if (isKicked) {
            loginActivityIntent.putExtra("kickedByOtherClient", true);
        }
        startActivity(loginActivityIntent);
    }
}
