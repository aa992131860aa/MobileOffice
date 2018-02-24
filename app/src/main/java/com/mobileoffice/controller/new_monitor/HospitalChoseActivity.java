package com.mobileoffice.controller.new_monitor;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.mobileoffice.R;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.controller.login.HospitalParentAdapter;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.ProviceJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by 99213 on 2017/6/15.
 */

public class HospitalChoseActivity extends BaseActivity implements AMapLocationListener, HospitalParentAdapter.HospitalParentListener, HospitalChildAdapter.HospitalChildListener {
    private RecyclerView rv_hospital_parent;

    private LinearLayoutManager mLinearLayoutManager;
    private LinearLayoutManager mLinearLayoutManagerChild;
    private List<String> mParents;
    private List<String> mChilds;
    private Context mContext;
    private HospitalParentAdapter mHospitalParentAdapter;
    private HospitalChildAdapter mHospitalChildAdapter;

    private LocationManager mLocationManager;
    private static final int CAMERA_CODE_PERMISSION = 0x1112;
    private static final long MIN_TIME = 1000l;
    private static final float MIN_DISTANCE = 10f;
    private String TAG = "HospitalChoseActivity";
    private String provice = "";
    //声明mLocationOption对象
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mlocationClient = null;

    //医院
    private RecyclerView rv_hospital_child;
    private TextView tv_hospital_loading;
    private LinearLayout ll_hospital_no;
    //上一个view
    private View lastView;

    //返回
    private LinearLayout ll_hospital_back;

    @Override
    protected void initVariable() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Do not have the permission of camera, request it.
            ActivityCompat.requestPermissions(HospitalChoseActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, CAMERA_CODE_PERMISSION);
        } else {
            location();
        }


        //省份
        mParents = new ArrayList<>();
        mLinearLayoutManager = new LinearLayoutManager(this);
        mHospitalParentAdapter = new HospitalParentAdapter(this, mParents);
        mHospitalParentAdapter.setHospitalParentListener(this);
        rv_hospital_parent.setLayoutManager(mLinearLayoutManager);
        rv_hospital_parent.setAdapter(mHospitalParentAdapter);
        loadParentData();

        //医院
        mChilds = new ArrayList<>();
        mLinearLayoutManagerChild = new LinearLayoutManager(this);
        mHospitalChildAdapter = new HospitalChildAdapter(this, mChilds);
        mHospitalChildAdapter.setHospitalParentListener(this);
        rv_hospital_child.setLayoutManager(mLinearLayoutManagerChild);
        rv_hospital_child.setAdapter(mHospitalChildAdapter);
    }

    private void location() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mlocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(1000);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();
    }

    private void loadParentData() {
        RequestParams params = new RequestParams(URL.USER);
        params.addQueryStringParameter("action", "hospital");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                ProviceJson proviceJson = gson.fromJson(result, ProviceJson.class);
                if (proviceJson != null && proviceJson.getResult() == CONSTS.SEND_OK) {
                    mParents = proviceJson.getObj() == null ? new ArrayList<String>() : proviceJson.getObj();
                    List<String> temp = new ArrayList<>();
                    temp.add(0, "定位中..");
                    for (int i = 0; i < mParents.size(); i++) {
                        temp.add(mParents.get(i));
                    }


                    mHospitalParentAdapter.refresh(temp, -1);
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

    /**
     * 根据省份获取医院
     */
    private void loadChildData(String province) {
        RequestParams params = new RequestParams(URL.USER);
        params.addQueryStringParameter("action", "hospital");
        params.addQueryStringParameter("province", province);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                ProviceJson proviceJson = gson.fromJson(result, ProviceJson.class);
                if (proviceJson != null && proviceJson.getResult() == CONSTS.SEND_OK) {
                    mChilds = proviceJson.getObj() == null ? new ArrayList<String>() : proviceJson.getObj();
                    mHospitalChildAdapter.refresh(mChilds);

                    rv_hospital_child.setVisibility(View.VISIBLE);
                    tv_hospital_loading.setVisibility(View.GONE);
                    ll_hospital_no.setVisibility(View.GONE);

                } else {

                    rv_hospital_child.setVisibility(View.GONE);
                    tv_hospital_loading.setVisibility(View.GONE);
                    ll_hospital_no.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                rv_hospital_child.setVisibility(View.GONE);
                tv_hospital_loading.setVisibility(View.GONE);
                ll_hospital_no.setVisibility(View.VISIBLE);
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
        setContentView(R.layout.hospital);
        mContext = this;
        rv_hospital_parent = (RecyclerView) findViewById(R.id.rv_hospital_parent);
        rv_hospital_child = (RecyclerView) findViewById(R.id.rv_hospital_child);
        ll_hospital_no = (LinearLayout) findViewById(R.id.ll_hospital_no);
        tv_hospital_loading = (TextView) findViewById(R.id.tv_hospital_loading);
        ll_hospital_back = (LinearLayout) findViewById(R.id.ll_hospital_back);
        ll_hospital_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void initData() {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // User agree the permission
                    location();
                } else {
                    // User disagree the permission
                    ToastUtil.showToast("没有开启定位权限", HospitalChoseActivity.this);
                    LogUtil.e(TAG, "没有开启定位权限");
                }

            }
            break;


        }
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                LogUtil.e(TAG, "latitude:" + amapLocation.getLatitude() + ",longitude:" + amapLocation.getLatitude());
                LogUtil.e(TAG, "provice:" + amapLocation.getProvince());

                provice = amapLocation.getProvince();

                //已经获取到省份信息
                if (mParents.size() > 1) {
                    mlocationClient.stopLocation();

                    List<String> temp = new ArrayList<>();
                    temp.add(provice + "(GPS)");
                    for (int i = 0; i < mParents.size(); i++) {
                        if (provice.contains(mParents.get(i))) {

                        } else {

                            temp.add(mParents.get(i));
                        }
                    }
                    mHospitalParentAdapter.refresh(temp, -1);
                    //加载医院
                    loadChildData(provice);

                }

            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }

    private List<Address> getLocationList(double latitude, double longitude) {
        Log.i(TAG, "getLocationList");
        Geocoder gc = new Geocoder(this, Locale.getDefault());
        List<Address> locationList = null;
        try {
            locationList = gc.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return locationList;
    }

    @Override
    public void OnHospitalParentClick(View thisView, View firstView, String province, int position) {
        if (lastView != null) {
            lastView.setBackgroundColor(getResources().getColor(R.color.bg));
            ((TextView) lastView).setTextColor(getResources().getColor(R.color.font_black_3));
        }
        firstView.setBackgroundColor(getResources().getColor(R.color.bg));
        ((TextView) firstView).setTextColor(getResources().getColor(R.color.font_black_3));

        thisView.setBackgroundColor(getResources().getColor(R.color.white));
        ((TextView) thisView).setTextColor(getResources().getColor(R.color.highlight));

        lastView = thisView;
        mHospitalParentAdapter.refresh(position);
        loadChildData(province);
    }

    @Override
    public void OnHospitalParentClick(View thisView, String hospital) {
        CONSTS.HOSPITAL_NAME_TRANSFER = hospital;
        SharePreUtils.putString("hospital", hospital, this);
        Intent intent = new Intent();
        intent.putExtra("hospitalName",hospital);
        setResult(300,intent);
        finish();
    }
}
