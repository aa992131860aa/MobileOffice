package com.mobileoffice.controller.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobileoffice.MainActivity;
import com.mobileoffice.R;
import com.mobileoffice.SealAppContext;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.json.PhotoJson;
import com.mobileoffice.json.RoleListJson;
import com.mobileoffice.json.TokenJson;
import com.mobileoffice.json.WechatJson;
import com.mobileoffice.http.URL;
import com.mobileoffice.utils.ActivityStack;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.DisplayUtil;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;
import com.thinkcool.circletextimageview.CircleTextImageView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import io.rong.imkit.RongIM;

/**
 * Created by 99213 on 2017/6/14.
 */

public class ConfirmActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = "ConfirmActivity";
    private LinearLayout ll_confirm_back;
    private TextView tv_confirm_hospital;
    private EditText edt_confirm_name;
    private EditText edt_confirm_phone;
    private TextView tv_confirm_role;
    private String phone;
    private String wechatUuid;
    private String token;
    //圆形头像
    private CircleTextImageView ctiv_confirm;
    //完成
    private Button btn_confirm_finish;
    private static Context mContext;
    private String photoFile;
    private String phoneToken;
    private int[] color = new int[3];
    private int randomNum;
    private String roleName;
    private int roleId;

    @Override
    protected void initVariable() {

        phone = getIntent().getStringExtra("phone");
        wechatUuid = getIntent().getStringExtra("wechatUuid");
        edt_confirm_phone.setText(phone);
        color[0] = R.color.yellow;
        color[1] = R.color.blue;
        color[2] = R.color.green;
        Random random = new Random();
        randomNum = random.nextInt(3);
        ctiv_confirm.setFillColorResource(color[randomNum]);
        ctiv_confirm.setBackgroundResource(color[randomNum]);
        loadRoleList();

//        String edtPhone = "18398850872";
//        String trueName = "打野";
//        ctiv_confirm.setText(trueName.substring(0,1));

//        ctiv_confirm.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        ctiv_confirm.layout(0, 0, ctiv_confirm.getMeasuredWidth(), ctiv_confirm.getMeasuredHeight());
//        ctiv_confirm.setDrawingCacheEnabled(true);
//        ctiv_confirm.buildDrawingCache();
        //       Bitmap bitmap = convertViewToBitmap(ctiv_confirm,ctiv_confirm.getMeasuredWidth(),ctiv_confirm.getMeasuredHeight());
        //       ToastUtil.showToast("bitmap:"+bitmap);
        //      saveBitmapFile(bitmap,edtPhone,trueName);
        // ctiv_confirm.setDrawingCacheEnabled(false);
    }

    //    public static Bitmap convertViewToBitmap(View view, int bitmapWidth, int bitmapHeight){
//        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
//        view.draw(new Canvas(bitmap));
//
//        return bitmap;
//    }
    public static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, DisplayUtil.dip2px(mContext, 50), DisplayUtil.dip2px(mContext, 50));
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();

        return bitmap;
    }

    PopupWindow pop;
    LinearLayoutManager mLinearLayoutManager;
    RoleAdapter mRoleAdapter;
    RecyclerView rv_role;
    List<RoleListJson.ObjBean> mRoleList;

    /****
     * 头像提示框
     */
    public void showPopupWindow() {
        //mRoleList = new ArrayList<>();
        pop = new PopupWindow(this);
        View view = getLayoutInflater().inflate(R.layout.role_pop,
                null);
        rv_role = (RecyclerView) view.findViewById(R.id.rv_role);
        pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);
        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);

        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
                //rv_role.clearAnimation();
            }
        });
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRoleAdapter = new RoleAdapter(this, mRoleList);
        rv_role.setLayoutManager(mLinearLayoutManager);
        rv_role.setAdapter(mRoleAdapter);
        mRoleAdapter.setHospitalParentListener(new RoleAdapter.HospitalChildListener() {
            @Override
            public void OnHospitalParentClick(View thisView, int position) {
                tv_confirm_role.setText(mRoleList.get(position).getRoleName());
                roleId = mRoleList.get(position).getRoleId();
                roleName = mRoleList.get(position).getRoleName();
                pop.dismiss();
                //ToastUtil.showToast(mRoleList.get(position).getRoleName(),ConfirmActivity.this);
            }
        });

    }

    private void loadRoleList() {
        RequestParams params = new RequestParams(URL.USER);
        params.addBodyParameter("action", "role");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                RoleListJson roleListJson = new Gson().fromJson(result, RoleListJson.class);
                if (roleListJson != null && roleListJson.getResult() == CONSTS.SEND_OK) {
                    mRoleList = roleListJson.getObj();
                } else {
                    ToastUtil.showToast("网络错误", ConfirmActivity.this);
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.showToast("网络错误", ConfirmActivity.this);
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
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.confirm);

        mContext = this;
        ll_confirm_back = (LinearLayout) findViewById(R.id.ll_confirm_back);
        tv_confirm_hospital = (TextView) findViewById(R.id.tv_confirm_hospital);
        edt_confirm_name = (EditText) findViewById(R.id.edt_confirm_name);
        edt_confirm_phone = (EditText) findViewById(R.id.edt_confirm_phone);
        btn_confirm_finish = (Button) findViewById(R.id.btn_confirm_finish);
        ctiv_confirm = (CircleTextImageView) findViewById(R.id.ctiv_confirm);
        tv_confirm_role = (TextView) findViewById(R.id.tv_confirm_role);


        ll_confirm_back.setOnClickListener(this);
        tv_confirm_hospital.setOnClickListener(this);
        btn_confirm_finish.setOnClickListener(this);
        tv_confirm_role.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }


    public void saveBitmapFile(Bitmap bitmap, final String edtPhone, final String trueName) {
        photoFile = this.getExternalCacheDir() + "/" + phone + ".jpg";
        File file = new File(photoFile);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            RequestParams params = new RequestParams(URL.PHOTO_URL);
            params.setMultipart(true);
            LogUtil.e(TAG, "url:" + URL.PHOTO_URL);
            params.addBodyParameter("photoFile", new File(photoFile), "image/jpeg");
            params.addBodyParameter("phone", edtPhone);
            params.addBodyParameter("flag", "1");
            x.http().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    LogUtil.e(TAG, "result:" + result);
                    Gson gson = new Gson();
                    PhotoJson photoJson = gson.fromJson(result, PhotoJson.class);
                    if (null != photoJson && photoJson.getResult() == CONSTS.SEND_OK) {
                        if (photoJson.getObj() != null) {
                            String photoUrlTemp = photoJson.getObj().getPhotoUrl();
                            SharePreUtils.putString("photoFile", photoJson.getObj().getPhotoUrl(), mContext);
                            SharePreUtils.putString("flag", "1", mContext);
                            createToken(edtPhone, trueName, photoUrlTemp);
                        }
                    }
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
    protected void onStart() {
        super.onStart();
        tv_confirm_hospital.setText(CONSTS.HOSPITAL_NAME);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_confirm_back:
                finish();
                break;
            case R.id.tv_confirm_hospital:
                startActivity(new Intent(this, HospitalChoseActivity.class));
                break;
            case R.id.btn_confirm_finish:

                String trueName = edt_confirm_name.getText().toString().trim();
                String hospital = tv_confirm_hospital.getText().toString().trim();
                String edtPhone = edt_confirm_phone.getText().toString().trim();
                String role = tv_confirm_role.getText().toString().trim();
                if ("".equals(trueName)) {
                    ToastUtil.showToast("请填写姓名", ConfirmActivity.this);
                } else if ("".equals(edtPhone)) {
                    ToastUtil.showToast("请填写手机号码", ConfirmActivity.this);
                } else if ("".equals(hospital)) {
                    ToastUtil.showToast("请选择医院", ConfirmActivity.this);
                }
                else if ("".equals(role)) {
                    ToastUtil.showToast("请选择角色", ConfirmActivity.this);
                }
                else if (!checkCellPhone(edtPhone)) {
                    ToastUtil.showToast("手机格式错误", ConfirmActivity.this);
                } else if (phone != null && !phone.equals(edtPhone)) {
                    ToastUtil.showToast("当前手机号码不是验证手机号码", ConfirmActivity.this);
                }
                //微信验证
                else if (phone == null) {

                    String wechatName = SharePreUtils.getString("wechatName", "", this);
                    String wechatUrl = SharePreUtils.getString("wechatUrl", "", this);

                    loadConfirmWechatData(edtPhone, trueName, hospital, wechatUuid, wechatName, wechatUrl);

                }
                //手机验证
                else if (wechatUuid == null) {
                    loadConfirmPhoneData(edtPhone, trueName, hospital);
                    ctiv_confirm.setText(trueName.substring(0, 1));

                    Bitmap bitmap = convertViewToBitmap(ctiv_confirm);
                    saveBitmapFile(bitmap, edtPhone, trueName);

                }
                break;
            case R.id.tv_confirm_role:

                showPopupWindow();
                //rv_role.startAnimation(AnimationUtils.loadAnimation(LoginActivity.this, R.anim.activity_translate_in));
                pop.showAtLocation(view, Gravity.BOTTOM, 0, 0);


                break;
        }
    }

    public static boolean checkCellPhone(String cellPhone) {
        String regex = "^1[3,4,5,7,8]\\d{9}$";
        return Pattern.matches(regex, cellPhone);
    }

    private void createToken(String userId, String userName, String photoUrl) {
        RequestParams params = new RequestParams(URL.RONG);
        params.addQueryStringParameter("action", "token");
        params.addBodyParameter("userId", userId);
        params.addBodyParameter("userName", userName);
        params.addBodyParameter("photoUrl", photoUrl);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                TokenJson datasJson = gson.fromJson(result, TokenJson.class);

                if (null != datasJson && datasJson.getResult() == CONSTS.SEND_OK) {
                    if (datasJson.getObj() != null) {
                        token = datasJson.getObj().getToken();
                        SharePreUtils.putString("token", token, mContext);

                        RongIM.connect(token, SealAppContext.getInstance().getConnectCallback());
                        LogUtil.e(TAG, "tokenPut:" + token);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(TAG, "ex:" + ex.getMessage());
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
    private void loadConfirmPhoneData(final String phone, final String trueName, final String hospital) {

        showWaitDialog(getResources().getString(R.string.loading),false,"loading");
        RequestParams params = new RequestParams(URL.USER);
        params.addQueryStringParameter("action", "confirmPhone");
        params.addQueryStringParameter("phone", phone);
        params.addQueryStringParameter("trueName", trueName);
        params.addQueryStringParameter("hospital", hospital);
        params.addBodyParameter("roleId",roleId+"");


        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                WechatJson wechatJson = gson.fromJson(result, WechatJson.class);
                if (wechatJson != null && wechatJson.getResult() == CONSTS.SEND_OK) {
                    //存储在本地
                    SharePreUtils.putString("phone", phone, ConfirmActivity.this);
                    SharePreUtils.putString("trueName", trueName, ConfirmActivity.this);
                    SharePreUtils.putString("hospital", hospital, ConfirmActivity.this);
                    SharePreUtils.putString("roleName", roleName, ConfirmActivity.this);
                    SharePreUtils.putInt("roleId", roleId, ConfirmActivity.this);
                    SharePreUtils.putBoolean("confirm", true, mContext);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    SharePreUtils.putString("create_time",sdf.format(new Date()),ConfirmActivity.this);
                    SharePreUtils.putString("temperatureStatus", "0", ConfirmActivity.this);
                    SharePreUtils.putString("openStatus", "0", ConfirmActivity.this);
                    SharePreUtils.putString("collisionStatus", "0", ConfirmActivity.this);
                    Intent i = new Intent(ConfirmActivity.this, MainActivity.class);

                    ActivityStack.getInstance().finishAllActivity();
                    startActivity(i);

                } else if(wechatJson != null && wechatJson.getResult() == CONSTS.NO_REGISTER){
                    ToastUtil.showToast("暂无注册权限,请联系医院后台管理员", ConfirmActivity.this);
                }else{
                    ToastUtil.showToast("网络错误,请重试", ConfirmActivity.this);
                }
                dismissDialog();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.showToast("网络错误,请重试", ConfirmActivity.this);
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
    private void loadConfirmWechatData(final String phone, final String trueName, final String hospital, final String wechatUuid, String wechatName, final String wechatUrl) {

          showWaitDialog(getResources().getString(R.string.loading),false,"loading");
        RequestParams params = new RequestParams(URL.USER);
        params.addQueryStringParameter("action", "confirmWechat");
        params.addQueryStringParameter("phone", phone);
        params.addQueryStringParameter("trueName", trueName);
        params.addQueryStringParameter("hospital", hospital);
        params.addQueryStringParameter("wechatUuid", wechatUuid);
        params.addQueryStringParameter("wechatName", wechatName);
        params.addQueryStringParameter("wechatUrl", wechatUrl);
        params.addBodyParameter("roleId",roleId+"");


        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                WechatJson wechatJson = gson.fromJson(result, WechatJson.class);
                if (wechatJson != null && wechatJson.getResult() == CONSTS.SEND_OK) {
                    createToken(phone, trueName, wechatUrl);
                    //存储在本地
                    SharePreUtils.putString("phone", phone, ConfirmActivity.this);
                    SharePreUtils.putString("trueName", trueName, ConfirmActivity.this);
                    SharePreUtils.putString("hospital", hospital, ConfirmActivity.this);
                    SharePreUtils.putString("flag", "0", ConfirmActivity.this);
                    SharePreUtils.putBoolean("confirm", true, mContext);
                    SharePreUtils.putString("roleName", roleName, ConfirmActivity.this);
                    SharePreUtils.putInt("roleId", roleId, ConfirmActivity.this);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    SharePreUtils.putString("create_time",sdf.format(new Date()),ConfirmActivity.this);
                    SharePreUtils.putString("temperatureStatus", "0", ConfirmActivity.this);
                    SharePreUtils.putString("openStatus", "0", ConfirmActivity.this);
                    SharePreUtils.putString("collisionStatus", "0", ConfirmActivity.this);
                    Intent i = new Intent(ConfirmActivity.this, MainActivity.class);
                    ActivityStack.getInstance().finishAllActivity();
                    startActivity(i);

                }else if(wechatJson != null && wechatJson.getResult() == CONSTS.NO_REGISTER){
                    ToastUtil.showToast("暂无注册权限,请联系医院后台管理员", ConfirmActivity.this);
                } else {

                    ToastUtil.showToast("网络错误,请重试", ConfirmActivity.this);
                    LogUtil.e(TAG, "ER:" + wechatJson.getMsg());
                }
                dismissDialog();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.showToast("网络错误,请重试", ConfirmActivity.this);
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
}
