package com.mobileoffice.controller.new_monitor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.acker.simplezxing.activity.CaptureActivity;
import com.google.gson.Gson;
import com.mobileoffice.MainActivity;
import com.mobileoffice.R;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.PhotoJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by 99213 on 2017/7/24.
 */

public class NewMonitorModeActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout ll_back;
    private LinearLayout btn_scan;
    private LinearLayout btn_handle;
    private static final int REQ_CODE_PERMISSION = 0x1111;
    @Override
    protected void initVariable() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.new_monitor_mode);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        btn_scan = (LinearLayout) findViewById(R.id.ll_scan);
        btn_handle = (LinearLayout) findViewById(R.id.ll_handle);

        ll_back.setOnClickListener(this);
        btn_scan.setOnClickListener(this);
        btn_handle.setOnClickListener(this);
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
            case R.id.ll_scan:

                    if (ContextCompat.checkSelfPermission(NewMonitorModeActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // Do not have the permission of camera, request it.
                        ActivityCompat.requestPermissions(NewMonitorModeActivity.this, new String[]{Manifest.permission.CAMERA}, REQ_CODE_PERMISSION);
                    } else {
                        // Have gotten the permission
                        startCaptureActivityForResult();
                    }

                break;
            case R.id.ll_handle:
                Intent intent = new Intent();
                intent.setClass(this, NewMonitorActivity.class);
               CONSTS.TYPE = "handle";
                CONSTS.TRANSFER_STATUS = 0;
                startActivity(intent);
                finish();
                break;
        }
    }
    private void startCaptureActivityForResult() {
        Intent intent = new Intent(NewMonitorModeActivity.this, CaptureActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(CaptureActivity.KEY_NEED_BEEP, CaptureActivity.VALUE_BEEP);
        bundle.putBoolean(CaptureActivity.KEY_NEED_VIBRATION, CaptureActivity.VALUE_VIBRATION);
        bundle.putBoolean(CaptureActivity.KEY_NEED_EXPOSURE, CaptureActivity.VALUE_NO_EXPOSURE);
        bundle.putByte(CaptureActivity.KEY_FLASHLIGHT_MODE, CaptureActivity.VALUE_FLASHLIGHT_OFF);
        bundle.putByte(CaptureActivity.KEY_ORIENTATION_MODE, CaptureActivity.VALUE_ORIENTATION_AUTO);
        intent.putExtra(CaptureActivity.EXTRA_SETTING_BUNDLE, bundle);
        startActivityForResult(intent, CaptureActivity.REQ_CODE);
        //finish();
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
                    ToastUtil.showToast("请在app设置界面开启照相权限", NewMonitorModeActivity.this);
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
                       String result =  data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT);

                        if(result.contains(":")&&result.split(":").length>1){
                           final String boxNo =  result.split(":")[1];
                            RequestParams params = new RequestParams(URL.BOX);
                            params.addBodyParameter("action","start");
                            params.addBodyParameter("boxNo",boxNo);
                            x.http().get(params, new Callback.CommonCallback<String>() {
                                @Override
                                public void onSuccess(String result) {
                                    PhotoJson photoJson = new Gson().fromJson(result,PhotoJson.class);
                                    if(photoJson!=null&&photoJson.getResult()==CONSTS.SEND_OK){
                                        Intent intent = new Intent();
                                        intent.setClass(NewMonitorModeActivity.this, NewMonitorActivity.class);
                                        SharePreUtils.putString("boxNo",boxNo,NewMonitorModeActivity.this);
                                        CONSTS.TYPE = "scan";
                                        CONSTS.TRANSFER_STATUS = 0;
                                        startActivity(intent);
                                        finish();
                                    }else{
                                        ToastUtil.showToast("该箱子无法使用",NewMonitorModeActivity.this);
                                    }
                                }

                                @Override
                                public void onError(Throwable ex, boolean isOnCallback) {
                                    ToastUtil.showToast("箱子无法使用",NewMonitorModeActivity.this);
                                }

                                @Override
                                public void onCancelled(CancelledException cex) {

                                }

                                @Override
                                public void onFinished() {

                                }
                            });
                        }else{
                            ToastUtil.showToast("请扫描正确的二维码",NewMonitorModeActivity.this);
                        }

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
        }
    }
}
