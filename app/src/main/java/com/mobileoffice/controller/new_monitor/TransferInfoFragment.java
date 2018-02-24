package com.mobileoffice.controller.new_monitor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.lljjcoder.citypickerview.widget.CityPicker;
import com.mobileoffice.R;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by 99213 on 2017/6/29.
 */

public class TransferInfoFragment extends NewMonitorBaseFragment implements View.OnClickListener, AMapLocationListener {
    private static final String TAG = "TransferInfoFragment";
    private View root;
    private static ViewPager viewPager;
    private TextView edt_frg_transfer_info_start;
    private TextView edt_frg_transfer_info_end;
    private LinearLayout ll_frg_transfer_info_plane;
    private ImageView iv_frg_transfer_info_plane;
    private LinearLayout ll_frg_transfer_info_train;
    private ImageView iv_frg_transfer_info_train;
    private LinearLayout ll_frg_transfer_info_car;
    private ImageView iv_frg_transfer_info_car;
    private LinearLayout ll_frg_transfer_info_other;
    private ImageView iv_frg_transfer_info_other;
    private EditText edt_frg_transfer_info_open_psd;
    private TextView tv_frg_transfer_info_pre;
    private TextView tv_frg_transfer_info_next;

    private String tracfficType;
    private String tracfficNumber;


    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mlocationClient = null;
    private LocationManager mLocationManager;
    private String fromCity = "";
    private static final int CAMERA_CODE_PERMISSION = 0x1112;
    private static TextView textView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.frg_transfer_info, container, false);
        tracfficType = "救护车";
        initView();
        if ("".equals(edt_frg_transfer_info_start.getText().toString().trim()) && objBean.getFromCity() == null) {
            loadLocation();
        }
        return root;
    }

    private void loadLocation() {


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Do not have the permission of camera, request it.
            //ToastUtil.showToast("gg1:"+"".equals(fromCity),getActivity());
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, CAMERA_CODE_PERMISSION);
        } else {
            location();
            //ToastUtil.showToast("gg2:"+"".equals(fromCity),getActivity());

        }
    }


    private void cityPicker() {
        CityPicker cityPicker = new CityPicker.Builder(getActivity())
                .textSize(20)
                .title("地址选择")
                .titleBackgroundColor("#f5f5f5")
                .titleTextColor("#1d4499")
                .backgroundPop(0x60000000)
                .confirTextColor("#057dff")
                .cancelTextColor("#057dff")
                .province("浙江省")
                .city("杭州市")
                .district("江干区")
                .textColor(Color.parseColor("#050505"))
                .provinceCyclic(true)
                .cityCyclic(false)
                .districtCyclic(false)
                .visibleItemsCount(5)
                .itemPadding(10)
                .onlyShowProvinceAndCity(false)
                .build();
        cityPicker.show();

        //监听方法，获取选择结果
        cityPicker.setOnCityItemClickListener(new CityPicker.OnCityItemClickListener() {
            @Override
            public void onSelected(String... citySelected) {
                //省份
                String province = citySelected[0];
                //城市
                String city = citySelected[1];
                //区县（如果设定了两级联动，那么该项返回空）
                String district = citySelected[2];
                //邮编
                String code = citySelected[3];
                edt_frg_transfer_info_start.setText(city + district);
                objBean.setTrueFromCity(null);
                objBean.setFromCity(edt_frg_transfer_info_start.getText().toString());
            }

            @Override
            public void onCancel() {
                // Toast.makeText(MainActivity.this, "已取消", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initView() {
        edt_frg_transfer_info_start = (TextView) root.findViewById(R.id.edt_frg_transfer_info_start);
        edt_frg_transfer_info_end = (TextView) root.findViewById(R.id.edt_frg_transfer_info_end);
        ll_frg_transfer_info_plane = (LinearLayout) root.findViewById(R.id.ll_frg_transfer_info_plane);
        iv_frg_transfer_info_plane = (ImageView) root.findViewById(R.id.iv_frg_transfer_info_plane);
        ll_frg_transfer_info_train = (LinearLayout) root.findViewById(R.id.ll_frg_transfer_info_train);
        iv_frg_transfer_info_train = (ImageView) root.findViewById(R.id.iv_frg_transfer_info_train);
        ll_frg_transfer_info_car = (LinearLayout) root.findViewById(R.id.ll_frg_transfer_info_car);
        iv_frg_transfer_info_car = (ImageView) root.findViewById(R.id.iv_frg_transfer_info_car);
        ll_frg_transfer_info_other = (LinearLayout) root.findViewById(R.id.ll_frg_transfer_info_other);
        iv_frg_transfer_info_other = (ImageView) root.findViewById(R.id.iv_frg_transfer_info_other);
        edt_frg_transfer_info_open_psd = (EditText) root.findViewById(R.id.edt_frg_transfer_info_open_psd);
        tv_frg_transfer_info_pre = (TextView) root.findViewById(R.id.tv_frg_transfer_info_pre);
        tv_frg_transfer_info_next = (TextView) root.findViewById(R.id.tv_frg_transfer_info_next);

        edt_frg_transfer_info_start.setOnClickListener(this);
        edt_frg_transfer_info_end.setOnClickListener(this);
        ll_frg_transfer_info_plane.setOnClickListener(this);
        ll_frg_transfer_info_train.setOnClickListener(this);
        ll_frg_transfer_info_car.setOnClickListener(this);
        ll_frg_transfer_info_other.setOnClickListener(this);
        tv_frg_transfer_info_pre.setOnClickListener(this);
        tv_frg_transfer_info_next.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        //设置所选的医院
        String hospitalName = SharePreUtils.getString("hospital", "", getActivity());
        //ToastUtil.showToast("H"+hospitalName,getActivity());
        if (!"".equals(hospitalName) && edt_frg_transfer_info_end != null) {
            edt_frg_transfer_info_end.setText(hospitalName);
        }
    }

    private void initTransferWay() {
        iv_frg_transfer_info_plane.setImageResource(R.drawable.newtrs_table3_plane);
        iv_frg_transfer_info_train.setImageResource(R.drawable.newtrs_table3_train);
        iv_frg_transfer_info_car.setImageResource(R.drawable.newtrs_table3_car);
        iv_frg_transfer_info_other.setImageResource(R.drawable.newtrs_table3_else);
    }

    private void location() {
        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        mlocationClient = new AMapLocationClient(getActivity());
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
        LogUtil.e(TAG, "启动定位");
    }

    public static TransferInfoFragment newIntance(ViewPager v, TextView t) {
        TransferInfoFragment transferInfoFragment = new TransferInfoFragment();
        viewPager = v;
        textView = t;
        return transferInfoFragment;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edt_frg_transfer_info_start:
                cityPicker();
                break;
            case R.id.edt_frg_transfer_info_end:
                startActivity(new Intent(getActivity(), HospitalChoseActivity.class));
                break;
            case R.id.ll_frg_transfer_info_plane:
                initTransferWay();
                iv_frg_transfer_info_plane.setImageResource(R.drawable.newtrs_table3_plane_on);
                tracfficType = "飞机";
                break;
            case R.id.ll_frg_transfer_info_train:
                initTransferWay();
                iv_frg_transfer_info_train.setImageResource(R.drawable.newtrs_table3_train_on);
                tracfficType = "火车";
                break;
            case R.id.ll_frg_transfer_info_car:
                initTransferWay();
                iv_frg_transfer_info_car.setImageResource(R.drawable.newtrs_table3_car_on);
                tracfficType = "救护车";
                break;
            case R.id.ll_frg_transfer_info_other:
                initTransferWay();
                iv_frg_transfer_info_other.setImageResource(R.drawable.newtrs_table3_else_on);
                tracfficType = "其他";
                break;
            case R.id.tv_frg_transfer_info_pre:
                viewPager.setCurrentItem(1);
                break;
            case R.id.tv_frg_transfer_info_next:
                fromCity = edt_frg_transfer_info_start.getText().toString().trim();
                if ("".equals(fromCity)) {

                    loadLocation();
                    ToastUtil.showToast("请填写起始地", getActivity());
                } else {

                    objBean.setFromCity(fromCity);
                    objBean.setToHospName(edt_frg_transfer_info_end.getText().toString().trim());

                    objBean.setTracfficType(tracfficType);
                    objBean.setTracfficNumber(edt_frg_transfer_info_open_psd.getText().toString().trim());
                    viewPager.setCurrentItem(3);
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

                fromCity = amapLocation.getCity() + amapLocation.getDistrict();
                edt_frg_transfer_info_start.setText(fromCity);
                //edt_frg_transfer_info_start.setSelection(edt_frg_transfer_info_start.getText().toString().length());
                mlocationClient.stopLocation();
                objBean.setTrueFromCity(amapLocation.getCity() + amapLocation.getDistrict() + amapLocation.getStreet());
                objBean.setFromCity(fromCity);

            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // User agree the permission
                    location();
                    //ToastUtil.showToast("你选择了location", getActivity());
                } else {
                    // User disagree the permission
                    ToastUtil.showToast("没有开启定位权限", getActivity());
                    LogUtil.e(TAG, "没有开启定位权限");
                }

            }
            break;


        }
    }

    @Override
    public void initData() {

        if ("".equals(edt_frg_transfer_info_start.getText().toString().trim()) && objBean.getFromCity() == null) {
            loadLocation();
        }

        if (CONSTS.TRANSFER_STATUS == 0) {
            textView.setText("新建转运(3/4)");
        } else if (CONSTS.TRANSFER_STATUS == 1) {

            if (objBean.getAutoTransfer() == CONSTS.AURO_TRANSFER_FINISH_NO) {
                textView.setText("完善资料(3/4)");
            } else {
                textView.setText("修改转运(3/4)");
            }
        }
        String hospitalName = SharePreUtils.getString("hospital", "", getActivity());
        //ToastUtil.showToast("H"+hospitalName,getActivity());
        if (!"".equals(hospitalName) && edt_frg_transfer_info_end != null) {
            edt_frg_transfer_info_end.setText(hospitalName);
        }

        if (objBean.getFromCity() != null && !"".equals(objBean.getFromCity())) {
            edt_frg_transfer_info_start.setText(objBean.getFromCity());
        }
        //ToastUtil.showToast("Type:"+objBean.getTracfficType(),getActivity());
        if (objBean.getTracfficType() != null && !"".equals(objBean.getTracfficType())) {

            initTransferWay();
            if ("飞机".equals(objBean.getTracfficType())) {
                iv_frg_transfer_info_plane.setImageResource(R.drawable.newtrs_table3_plane_on);
            } else if ("火车".equals(objBean.getTracfficType())) {
                iv_frg_transfer_info_train.setImageResource(R.drawable.newtrs_table3_train_on);
            } else if ("救护车".equals(objBean.getTracfficType())) {
                iv_frg_transfer_info_car.setImageResource(R.drawable.newtrs_table3_car_on);
            } else if ("其他".equals(objBean.getTracfficType())) {
                iv_frg_transfer_info_other.setImageResource(R.drawable.newtrs_table3_else_on);
            }
            tracfficType = objBean.getTracfficType();
            edt_frg_transfer_info_open_psd.setText(objBean.getTracfficNumber());
        }


        if ("".equals(edt_frg_transfer_info_end.getText().toString().trim())) {
            edt_frg_transfer_info_end.setText(SharePreUtils.getString("hospital", "", getActivity()));
        }
    }
}
