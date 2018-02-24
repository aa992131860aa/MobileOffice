package com.mobileoffice.controller.me;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobileoffice.R;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.utils.ToastUtil;

/**
 * Created by 99213 on 2017/8/14.
 */

public class MyServiceActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout ll_back;
    private LinearLayout ll_btn;
    private TextView tv_phone;
    private AlertDialog.Builder mAlertDialog;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private String callPhone = "";
    @Override
    protected void initVariable() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.about_me);
        callPhone = "057186792723";
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_btn = (LinearLayout) findViewById(R.id.ll_btn);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        ll_back.setOnClickListener(this);
        ll_btn.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_back:
                finish();
                break;
            case R.id.ll_btn:
                mAlertDialog = new AlertDialog.Builder(this);
                //mAlertDialog.setTitle("tis");

                mAlertDialog.setMessage("是否拨打客服电话?");


                mAlertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                mAlertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (ContextCompat.checkSelfPermission(MyServiceActivity.this,
                                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // 没有获得授权，申请授权
                            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) MyServiceActivity.this,
                                    Manifest.permission.CALL_PHONE)) {
                                // 返回值：
//                          如果app之前请求过该权限,被用户拒绝, 这个方法就会返回true.
//                          如果用户之前拒绝权限的时候勾选了对话框中”Don’t ask again”的选项,那么这个方法会返回false.
//                          如果设备策略禁止应用拥有这条权限, 这个方法也返回false.
                                // 弹窗需要解释为何需要该权限，再次请求授权
                                ToastUtil.showToast("未授权！", MyServiceActivity.this);

                                // 帮跳转到该应用的设置界面，让用户手动授权
//                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                            Uri uri = Uri.fromParts("package", getPackageName(), null);
//                            intent.setData(uri);
//                            startActivity(intent);
                            } else {
                                // 不需要解释为何需要该权限，直接请求授权
                                ActivityCompat.requestPermissions(MyServiceActivity.this,
                                        new String[]{Manifest.permission.CALL_PHONE},
                                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
                            }
                        } else {
                            // 已经获得授权，可以打电话
                            CallPhone(callPhone);
                        }

                    }
                });

                    mAlertDialog.show();

                break;
        }
    }
    // 处理权限申请的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 授权成功，继续打电话
                    CallPhone(callPhone);
                } else {
                    // 授权失败！
                    ToastUtil.showToast("未授权拨打电话权限", this);

                }
                break;
            }
        }

    }

    private void CallPhone(String phone) {

        // 拨号：激活系统的拨号组件
        Intent intent = new Intent(); // 意图对象：动作 + 数据
        intent.setAction(Intent.ACTION_CALL); // 设置动作
        Uri data = Uri.parse("tel:" + phone); // 设置数据
        intent.setData(data);
        startActivity(intent); // 激活Activity组件

    }
}
