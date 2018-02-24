package com.mobileoffice.controller.me;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobileoffice.MainActivity;
import com.mobileoffice.R;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.entity.Upload;
import com.mobileoffice.http.HttpHelper;
import com.mobileoffice.http.HttpObserver;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.UploadJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.ToastUtil;
import com.mobileoffice.utils.UpdateManager;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by 99213 on 2017/8/14.
 */

public class AboutMeActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_version;
    private TextView tv_update;
    private TextView tv_feedback;
    private LinearLayout ll_back;
    private String mUrl;
    private static final int REQ_CODE_PERMISSION = 0x1111;
    private static final int REQ_FILE_CODE_PERMISSION = 0x1112;
    @Override
    protected void initVariable() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.service);
        tv_version = (TextView) findViewById(R.id.tv_version);
        tv_update = (TextView) findViewById(R.id.tv_update);
        tv_feedback = (TextView) findViewById(R.id.tv_feedback);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);

        tv_version.setText("V" + getVersionName(this));

        tv_update.setOnClickListener(this);
        tv_feedback.setOnClickListener(this);
        ll_back.setOnClickListener(this);
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
        return "";
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {

            case R.id.tv_update:
                update();
                break;
            case R.id.tv_feedback:
                intent.setClass(this, FeedbackActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_back:
                finish();
                break;
        }
    }
    private void update(){


        RequestParams params = new RequestParams(URL.UPLOAD);
        params.addBodyParameter("action","app");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //ToastUtil.showToast(result,MainActivity.this);
                UploadJson upload = new Gson().fromJson(result, UploadJson.class);
                if(upload!=null&&upload.getResult()== CONSTS.SEND_OK){
                    int oldVersion = getVersionCode(AboutMeActivity.this);
                    int newVersion = upload.getObj().getVersion();
                    String url = upload.getObj().getUrl();
                    mUrl = url;
                    //ToastUtil.showToast("new:"+newVersion+",old:"+oldVersion,MainActivity.this);
                    //  LogUtil.e(TAG,"model1:"+model.getVersion());
                    if (newVersion > oldVersion) {
                        if (ContextCompat.checkSelfPermission(AboutMeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED||ContextCompat.checkSelfPermission(AboutMeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            // Do not have the permission of camera, request it.
                            ActivityCompat.requestPermissions(AboutMeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, REQ_FILE_CODE_PERMISSION);
                        } else {
                            // Have gotten the permission
                            //startCaptureActivityForResult();
                            UpdateManager updateManager = new UpdateManager(AboutMeActivity.this, url);
                            updateManager.checkUpdateInfo();
                            //LogUtil.e(TAG,"model2:"+model.getVersion());
                        }


                    }else{
                        ToastUtil.showToast("您已是最新版本了",AboutMeActivity.this);
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

//        //自动更新
//        new HttpHelper().getUploadAPK().subscribe(new HttpObserver<Upload>() {
//            @Override
//            public void onComplete() {
//
//            }
//
//            @Override
//            public void onSuccess(Upload model) {
//                if (model != null) {
//                    int oldVersion = getVersionCode(AboutMeActivity.this);
//                    int newVersion = model.getVersion();
//                    String url = model.getUrl();
//
//                    if (newVersion > oldVersion) {
//                        UpdateManager updateManager = new UpdateManager(AboutMeActivity.this, url);
//                        updateManager.checkUpdateInfo();
//                    }else{
//                        ToastUtil.showToast("您已是最新版本了",AboutMeActivity.this);
//                    }
//
//                }
//
//            }
//
//
//        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case REQ_FILE_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // User agree the permission
                    UpdateManager updateManager = new UpdateManager(this, mUrl);
                    updateManager.checkUpdateInfo();
                } else {
                    // User disagree the permission
                    ToastUtil.showToast("请在app设置界面获取存储权限", AboutMeActivity.this);
                }
            }
            break;
        }
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
}
