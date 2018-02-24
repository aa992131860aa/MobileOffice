package com.mobileoffice.controller.me;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobileoffice.R;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.PhotoJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;
import com.mobileoffice.view.RoundImageView;
import com.squareup.picasso.Picasso;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * Created by 99213 on 2017/5/25.
 */

public class PersonInfoActivity extends BaseActivity implements View.OnClickListener {
    private PopupWindow pop;
    private LinearLayout ll_popup;
    private RoundImageView riv_person_info;
    private static final int PHOTO = 1;//拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private File tempFile;
    private static final int REQ_CODE_PERMISSION = 0x1111;
    private static final int CAMERA_CODE_PERMISSION = 0x1112;
    private String TAG = "PersonInfoActivity";
    private String photoFile = "";

    //获取头像
    private String photoFileShare;
    private String wechatUrl;
    private String flag;


    private LinearLayout ll_person_info;

    private LinearLayout ll_person_info_phone;
    private TextView tv_person_info_name;
    private TextView tv_person_info_phone;
    private TextView tv_person_info_hospital;
    private TextView tv_role;

    //返回
    private LinearLayout ll_person_info_back;

    private String trueName;
    private String phone;
    private String hospital;
    private String bind;
    private String roleName;
    private int roleId;

    //绑定选择框
    private AlertDialog.Builder mAlertDialog;
    private Context mContext;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.person_info);
        mContext = this;
        riv_person_info = (RoundImageView) findViewById(R.id.riv_person_info);
        ll_person_info_back = (LinearLayout) findViewById(R.id.ll_person_info_back);
        ll_person_info = (LinearLayout) findViewById(R.id.ll_person_info);


        tv_person_info_name = (TextView) findViewById(R.id.tv_person_info_name);
        tv_person_info_phone = (TextView) findViewById(R.id.tv_person_info_phone);
        tv_person_info_hospital = (TextView) findViewById(R.id.tv_person_info_hospital);
        ll_person_info_phone = (LinearLayout) findViewById(R.id.ll_person_info_phone);
        tv_role = (TextView) findViewById(R.id.tv_role);

        ll_person_info.setOnClickListener(this);
        ll_person_info_back.setOnClickListener(this);
        ll_person_info_phone.setOnClickListener(this);
    }

    @Override
    protected void initVariable() {
        photoFile = this.getExternalCacheDir() + "/photoFile.jpg";


        photoFileShare = SharePreUtils.getString("photoFile", "", this);
        wechatUrl = SharePreUtils.getString("wechatUrl", "", this);
        flag = SharePreUtils.getString("flag", "", this);
        trueName = SharePreUtils.getString("trueName", "", this);
        phone = SharePreUtils.getString("phone", "", this);
        hospital = SharePreUtils.getString("hospital", "", this);
        bind = SharePreUtils.getString("bind", "", this);
        roleId = SharePreUtils.getInt("roleId",-1,this);
        roleName = SharePreUtils.getString("roleName","",this);

        tv_person_info_name.setText(trueName);
        //if ("1".equals(bind)) {
            tv_person_info_phone.setText(phone);
        //}
        tv_person_info_hospital.setText(hospital);
        tv_role.setText(roleName);
        loadPhoto();


    }

    private void loadPhoto() {
        //自己的头像
        if ("1".equals(flag) && !photoFileShare.equals("")) {
            Picasso.with(this).load(photoFileShare).error(R.drawable.msg_2list_linkman).into(riv_person_info);
            LogUtil.e(TAG, "photoFileShare:" + photoFileShare);
        }
        //微信的头像
        else if ("0".equals(flag) && !wechatUrl.equals("")) {
            Picasso.with(this).load(wechatUrl).error(R.drawable.msg_2list_linkman).into(riv_person_info);
            LogUtil.e(TAG, "wechatUrl:" + wechatUrl);
        }

    }


    @Override
    protected void initData() {

    }

    /****
     * 头像提示框
     */
    public void showPopupWindow() {
        pop = new PopupWindow(this);
        View view = getLayoutInflater().inflate(R.layout.item_popupwindows,
                null);
        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
        pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);
        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
        Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
        Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(PersonInfoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // Do not have the permission of camera, request it.
                    ActivityCompat.requestPermissions(PersonInfoActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_CODE_PERMISSION);
                } else {
                    // Have gotten the permission
                    openCamera();
                }
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(PersonInfoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // Do not have the permission of camera, request it.
                    ActivityCompat.requestPermissions(PersonInfoActivity.this, new String[]{Manifest.permission.CAMERA}, REQ_CODE_PERMISSION);
                } else {
                    // Have gotten the permission
                    openPhone();
                }
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
    }

    private void openCamera() {
//        Intent camera = new_monitor Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(camera, PHOTO);
        File file = new File(getCacheDir() + "/photo.jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, getImageContentUri(this,file));
        startActivityForResult(intent, PHOTO);
    }

    private void openPhone() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_person_info:
                showPopupWindow();
                ll_popup.startAnimation(AnimationUtils.loadAnimation(
                        this, R.anim.activity_translate_in));
                pop.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.ll_person_info_back:
                finish();
                break;
            //bind手机
//            case R.id.ll_person_info_phone:
//                String nameOut = tv_person_info_phone.getText().toString();
//                //showAlertDialog("", "绑定手机后,其他人可看到", new String[]{"取消", "确定"}, false, true, "dialog").show();
//                mAlertDialog = new AlertDialog.Builder(mContext);
//                //mAlertDialog.setTitle("tis");
//                if ("未绑定".equals(nameOut)) {
//                    mAlertDialog.setMessage("绑定后,其他人可以看到手机号码");
//                } else {
//                    mAlertDialog.setMessage("解绑后,其他人不可以看到手机号码");
//                }
//
//                mAlertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//                mAlertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String name = tv_person_info_phone.getText().toString();
//                        if ("未绑定".equals(name)) {
//                            bindPhone(phone, "1");
//                        } else {
//                            bindPhone(phone, "0");
//                        }
//
//                    }
//                });
//                mAlertDialog.show();
//                break;
        }
    }

    private void bindPhone(final String phone, final String pBind) {
        RequestParams params = new RequestParams(URL.USER);
        params.addBodyParameter("action","bindPhone");
        params.addBodyParameter("phone", phone);
        params.addQueryStringParameter("bind", pBind);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                PhotoJson photoJson = gson.fromJson(result, PhotoJson.class);
                if (photoJson != null && photoJson.getResult() == 0) {
                    SharePreUtils.putString("bind", pBind, mContext);
                    if ("0".equals(pBind)) {
                        tv_person_info_phone.setText("未绑定");
                    } else {
                        tv_person_info_phone.setText(phone);
                    }
                } else {
                    if ("0".equals(pBind)) {
                        ToastUtil.showToast("解绑失败",PersonInfoActivity.this);
                    } else {
                        ToastUtil.showToast("绑定失败",PersonInfoActivity.this);
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

    /*
          * 剪切图片
          */
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.e(TAG, "requestCode:" + requestCode);

        if (requestCode == PHOTO) {
            File f = new File(getCacheDir() + "/photo.jpg");
            if (data!=null&&data.hasExtra("data")) {
                Bitmap bitmap = data.getParcelableExtra("data");
                riv_person_info.setImageBitmap(bitmap);
                saveBitmapFile(bitmap, photoFile);
            }
            LogUtil.e(TAG, "file:" + f.exists() + ":" + data.getData() + ":" + data.getParcelableExtra("data"));
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                //crop(uri);
            }
        } else if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                crop(uri);
            }
        } else if (requestCode == PHOTO_REQUEST_CUT) {
            // 从剪切图片返回的数据
            if (data != null) {
                Bitmap bitmap = data.getParcelableExtra("data");
                riv_person_info.setImageBitmap(bitmap);
                saveBitmapFile(bitmap, photoFile);
            }
            try {
                // 将临时文件删除
                tempFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void saveBitmapFile(Bitmap bitmap, String path) {
        //ToastUtil.showToast("path:" + path);
        File file = new File(path);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            RequestParams params = new RequestParams(URL.PHOTO_URL);
            params.setMultipart(true);
            LogUtil.e(TAG, "url:" + URL.PHOTO_URL);
            params.addBodyParameter("phone", SharePreUtils.getString("phone","",this));
            params.addBodyParameter("flag", "1");
            params.addBodyParameter("photoFile", new File(photoFile), "image/jpeg");

            x.http().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Gson gson = new Gson();
                    PhotoJson photoJson = gson.fromJson(result, PhotoJson.class);
                    if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                        if (photoJson.getObj() != null) {
                            SharePreUtils.putString("photoFile", photoJson.getObj().getPhotoUrl(), PersonInfoActivity.this);
                            SharePreUtils.putString("flag", "1", PersonInfoActivity.this);

                            UserInfo    mUserInfo = new UserInfo(phone, trueName, Uri.parse(photoJson.getObj().getPhotoUrl()));
                            RongIM.getInstance().refreshUserInfoCache(mUserInfo);
                            RongIM.getInstance().setMessageAttachedUserInfo(true);


                            LogUtil.e(TAG, "result1:" + phone+",trueName:"+trueName+",url:"+photoJson.getObj().getPhotoUrl());
                            ToastUtil.showToast("修改成功",PersonInfoActivity.this);

                        }
                    }
                    //ToastUtil.showToast("result:" + result);

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    LogUtil.e(TAG, "onError:" + ex.getMessage());
                }

                @Override
                public void onCancelled(CancelledException cex) {
                    LogUtil.e(TAG, "onCancelled:" + cex.getMessage());
                }

                @Override
                public void onFinished() {
                    LogUtil.e(TAG, "onFinished");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // User agree the permission
                    openPhone();
                } else {
                    // User disagree the permission
                    ToastUtil.showToast("请在app设置界面开启照相权限",PersonInfoActivity.this);
                }
            }
            break;
            case CAMERA_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // User agree the permission
                    openCamera();
                } else {
                    // User disagree the permission
                    ToastUtil.showToast("请在app设置界面开启照相权限",PersonInfoActivity.this);
                }
            }
            break;
        }
    }


}
