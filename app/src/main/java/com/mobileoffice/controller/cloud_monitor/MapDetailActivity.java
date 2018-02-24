package com.mobileoffice.controller.cloud_monitor;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.TranslateAnimation;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceListener;
import com.amap.api.trace.TraceLocation;
import com.mobileoffice.R;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.controller.cloud_monitor.frag.TransferBaseFragment;
import com.mobileoffice.entity.PathInfo;
import com.mobileoffice.json.TransferJson;
import com.mobileoffice.json.TransferRecordJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.mobileoffice.utils.DisplayUtil.dip2px;

/**
 * Created by 99213 on 2017/8/18.
 */

//器官：肝脏 直线总距离：405KM 剩余直线距离：250KM 已耗时：3小时20分钟
public class MapDetailActivity extends BaseActivity implements View.OnClickListener, TraceListener {
    private String TAG = "MapDetailActivity";
    private MapView mMapView;
    private LinearLayout ll_top;
    private LinearLayout ll_content;
    private TextView tv_start;
    private TextView tv_end;
    private TextView tv_organ;
    private TextView tv_time;
    private TextView tv_distance;
    private TextView tv_distance_total;
    private TextView tv_left;
    private TextView tv_right;
    private ViewPager vp_content;
    private ImageView iv_arrows;
    private LinearLayout ll_back;


    //初始化地图控制器对象
    AMap aMap;

    //线段
    Polyline polyline;
    PolylineOptions mPolylineOptions;
    List<LatLng> latLngs = new ArrayList<LatLng>();
    private double centerLat;
    private double centerLog;

    //主信息
    TransferJson.ObjBean objBean;

    //vp content
    private TransferFragmentPagerAdapter mTransferFragmentPagerAdapter;
    private List<Fragment> mContentFragment;
    private MapLeftFragment mMapLeftFragment;
    private MapRightFragment mMapRightFragment;
    private boolean isStart = true;

    private LatLng center = new LatLng(39.993167, 116.473274);// 中心脏点
    private MarkerOverlay markerOverlay;
    private List<LatLng> pointList;
    LatLng latLngCenter;
    LatLng latLngStart;
    LatLng latLngEnd;
    LatLng latLngMove;
    public ArrayList<PathInfo> pathInfos = new ArrayList<>();
    private List<Marker> markers;
    private LatLngBounds bounds;
    ScheduledExecutorService executorService;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isStart) {
                if (TransferBaseFragment.TRANSFER_RECORD.size() > 0) {
                    aMap.clear();
                    showDot();
                    move();
                    showLine();
                    //initVp();
                }


            }
        }
    };
    private Thread mThread;
    private LBSTraceClient mTraceClient;
    private List<TraceLocation> mTraceList;
    private int mSequenceLineID = 1;
    private int mCoordinateType = LBSTraceClient.TYPE_AMAP;

    @Override
    protected void initVariable() {
        mTraceClient = new LBSTraceClient(this.getApplicationContext());


        objBean = (TransferJson.ObjBean) getIntent().getSerializableExtra("transfer");
        //pathInfos = (ArrayList<PathInfo>) getIntent().getSerializableExtra("pathInfos");

        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        //ToastUtil.showToast("mMapView:" + mMapView + ",aMap:" + aMap, this);
        showDot();
        move();
        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
        showLine();
        initVp();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        };
        executorService = Executors.newSingleThreadScheduledExecutor();
        if (objBean != null && "transfering".equals(objBean.getStatus())) {
            executorService.scheduleAtFixedRate(runnable, CONSTS.REFRESH_TIME, CONSTS.REFRESH_TIME, TimeUnit.MILLISECONDS);
        }
    }

    private void initVp() {

        mContentFragment = new ArrayList<>();
        mMapLeftFragment = MapLeftFragment.newInstance(CONSTS.PATH_RECORDS, objBean.getGetTime(),objBean.getStatus());
        mMapRightFragment = MapRightFragment.newInstance();
        mContentFragment.add(mMapLeftFragment);
        mContentFragment.add(mMapRightFragment);
        mTransferFragmentPagerAdapter = new TransferFragmentPagerAdapter(getSupportFragmentManager(), mContentFragment);
        vp_content.setAdapter(mTransferFragmentPagerAdapter);
        vp_content.setCurrentItem(0);
        vp_content.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    tv_left.setBackgroundColor(getResources().getColor(R.color.font_black));
                    tv_right.setBackgroundColor(getResources().getColor(R.color.font_black_9));
                } else if (position == 1) {
                    tv_left.setBackgroundColor(getResources().getColor(R.color.font_black_9));
                    tv_right.setBackgroundColor(getResources().getColor(R.color.font_black));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void showDot() {
        mTraceList = new ArrayList<>();
        //初始化一下信息

        if (objBean != null) {
            tv_start.setText(objBean.getFromCity());
            tv_end.setText(objBean.getToHosp());
            tv_organ.setText("器官:" + objBean.getOrgan());
            //ToastUtil.showToast(objBean.getDistance()+","+objBean.getNowDistance(),this);
            if (objBean.getDistance() != null && objBean.getDistance().contains(".")) {
                tv_distance_total.setText("直线总距离:" + objBean.getDistance().split("\\.")[0] + "KM");
            } else {
                tv_distance_total.setText("直线总距离:0KM");
            }
            if (objBean.getNowDistance() != null && objBean.getNowDistance().contains(".")) {
                tv_distance.setText("剩余直线距离:" + objBean.getNowDistance().split("\\.")[0] + "KM");
            } else {
                tv_distance.setText("剩余直线距离:0KM");
            }

            String hour = objBean.getTime() != null ? objBean.getTime().split(":")[0] : "00";
            String minus = objBean.getTime() != null ? objBean.getTime().split(":")[1] : "00";
            if ("0".equals(hour.substring(0, 1))) {
                hour = hour.substring(1, 2);
            }
            try {
                tv_time.setText("共耗时:" + hour + "小时" + minus + "分钟");
            } catch (Exception e) {

            }
        }

        latLngs = new ArrayList<>();
        //处理经纬度
        if (TransferBaseFragment.TRANSFER_RECORD.size() > 0) {

            List<TransferRecordJson.ObjBean> objBeen = TransferBaseFragment.TRANSFER_RECORD;
            if (objBeen != null) {
                for (int i = 0; i < objBeen.size(); i++) {
                    try {
                        latLngs.add(new LatLng(Double.parseDouble(objBeen.get(i).getLatitude()), Double.parseDouble(objBeen.get(i).getLongitude())));
                        TraceLocation traceLocation = new TraceLocation();
                        traceLocation.setLatitude((Double.parseDouble(objBeen.get(i).getLatitude())));
                        traceLocation.setLongitude(Double.parseDouble(objBeen.get(i).getLongitude()));
                        mTraceList.add(traceLocation);
                    } catch (Exception e) {

                    }
                }

            }
        }


        //ToastUtil.showToast("cen:"+ aMap.getMaxZoomLevel(),this);


    }

    /**
     * 屏幕中心脏marker 跳动
     */
    public void startJumpAnimation(Marker screenMarker) {


        if (screenMarker != null) {
            //根据屏幕距离计算需要移动的目标点
            final LatLng latLng = screenMarker.getPosition();
            Point point = aMap.getProjection().toScreenLocation(latLng);
            point.y -= dip2px(this, 125);
            LatLng target = aMap.getProjection()
                    .fromScreenLocation(point);
            //使用TranslateAnimation,填写一个需要移动的目标点
            Animation animation = new TranslateAnimation(target);
            animation.setInterpolator(new Interpolator() {
                @Override
                public float getInterpolation(float input) {
                    // 模拟重加速度的interpolator
                    if (input <= 0.5) {
                        return (float) (0.5f - 2 * (0.5 - input) * (0.5 - input));
                    } else {
                        return (float) (0.5f - Math.sqrt((input - 0.5f) * (1.5f - input)));
                    }
                }
            });
            //整个移动所需要的时间
            animation.setDuration(600);
            //设置动画
            screenMarker.setAnimation(animation);
            //开始动画
            screenMarker.startAnimation();

        } else {
            Log.e("amap", "screenMarker is null");
        }
    }

    public void setCollisionOpen(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        markerOption.title(objBean.getFromCity()).snippet("");

        markerOption.draggable(true);//设置Marker可拖动
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.drawable.cloud_4location_none)));
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        // markerOption.setFlat(true);//设置marker平贴地图效果

        Marker marker = aMap.addMarker(markerOption);
        startJumpAnimation(marker);
    }

    private void move() {
        try {
            double lastLat = 0;
            double lastLon = 0;
            markers = new ArrayList<>();
            if (TransferBaseFragment.TRANSFER_RECORD.size() > 0) {
                centerLat = latLngs.get(0).latitude;
                centerLog = latLngs.get(0).longitude;

            }

            if (!"".equals(objBean.getEndLati()) && !"".equals(objBean.getEndLong())) {
                lastLat = Double.parseDouble(objBean.getEndLati());
                lastLon = Double.parseDouble(objBean.getEndLong());
            }

            //开始点
            if (centerLat == 0.0) {
                centerLat = Double.parseDouble(objBean.getStartLati());
                centerLog = Double.parseDouble(objBean.getStartLong());

            }
            latLngStart = new LatLng(centerLat, centerLog);
            MarkerOptions markerOption = new MarkerOptions();
            markerOption.position(latLngStart);
            markerOption.title(objBean.getFromCity()).snippet("");

            markerOption.draggable(true);//设置Marker可拖动
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(), R.drawable.cloud_4location_start)));
            // 将Marker设置为贴地显示，可以双指下拉地图查看效果
            // markerOption.setFlat(true);//设置marker平贴地图效果
            Marker markerStart = aMap.addMarker(markerOption);

            markers.add(markerStart);


            //结束点
            latLngEnd = new LatLng(lastLat, lastLon);
            markerOption = new MarkerOptions();
            markerOption.position(latLngEnd);
            markerOption.title(objBean.getToHosp()).snippet("");

            markerOption.draggable(true);//设置Marker可拖动
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(), R.drawable.cloud_4location_end)));
            // 将Marker设置为贴地显示，可以双指下拉地图查看效果
            //markerOption.setFlat(true);//设置marker平贴地图效果
            Marker markerEnd = aMap.addMarker(markerOption);
            markers.add(markerEnd);

            latLngMove = null;
            if (latLngs.size() > 0) {
                //行动点
                latLngMove = new LatLng(latLngs.get(latLngs.size() - 1).latitude, latLngs.get(latLngs.size() - 1).longitude);
                markerOption = new MarkerOptions();
                markerOption.position(latLngMove);
                markerOption.title(objBean.getToHosp()).snippet("");

                markerOption.draggable(true);//设置Marker可拖动
                markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.drawable.plane)));
                // 将Marker设置为贴地显示，可以双指下拉地图查看效果
                markerOption.setFlat(true);//设置marker平贴地图效果
                //aMap.addMarker(markerOption);
            }


            pointList = new ArrayList<>();
            pointList.add(latLngStart);
            pointList.add(latLngEnd);
            latLngCenter = new LatLng((latLngStart.latitude + latLngEnd.latitude) / 2,
                    (latLngStart.longitude + latLngEnd.longitude) / 2);
//            //设置中心脏点
//            aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
//                    latLngCenter,//新的中心脏点坐标
//                    10, //新的缩放级别
//                    0, //俯仰角0°~45°（垂直与地图时为0）
//                    0  ////偏航角 0~360° (正北方为0)
//            )));

            bounds = getLatLngBounds(latLngCenter, pointList);

        } catch (Exception e) {

        }
    }

    //根据中心脏点和自定义内容获取缩放bounds
    private LatLngBounds getLatLngBounds(LatLng centerpoint, List<LatLng> pointList) {
        LatLngBounds.Builder b = LatLngBounds.builder();
        if (centerpoint != null) {
            for (int i = 0; i < pointList.size(); i++) {
                LatLng p = pointList.get(i);
                LatLng p1 = new LatLng((centerpoint.latitude * 2) - p.latitude, (centerpoint.longitude * 2) - p.longitude);
                b.include(p);
                b.include(p1);
            }
        }
        return b.build();
    }

    private void showLine() {

        mPolylineOptions = new PolylineOptions().
                addAll(latLngs).width(dip2px(this, 25)).geodesic(true).color(Color.argb(255, 1, 1, 1));
        mPolylineOptions.setUseTexture(true);
        mPolylineOptions.setCustomTexture(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.texture)));

        polyline = aMap.addPolyline(mPolylineOptions);


    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.map_detail);
        mMapView = (MapView) findViewById(R.id.mv_map);
        ll_top = (LinearLayout) findViewById(R.id.ll_top);
        ll_content = (LinearLayout) findViewById(R.id.ll_content);
        iv_arrows = (ImageView) findViewById(R.id.iv_arrows);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        vp_content = (ViewPager) findViewById(R.id.vp_content);
        tv_left = (TextView) findViewById(R.id.tv_left);
        tv_right = (TextView) findViewById(R.id.tv_right);

        tv_start = (TextView) findViewById(R.id.tv_start);
        tv_end = (TextView) findViewById(R.id.tv_end);
        tv_organ = (TextView) findViewById(R.id.tv_organ);
        tv_distance_total = (TextView) findViewById(R.id.tv_distance_total);
        tv_distance = (TextView) findViewById(R.id.tv_distance);
        tv_time = (TextView) findViewById(R.id.tv_time);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);


        ll_top.setOnClickListener(this);
        ll_back.setOnClickListener(this);

    }


    @Override
    protected void initData() {
        aMap.getUiSettings().setLogoBottomMargin(-500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        isStart = false;
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isStart = false;
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_top:
                //listview列表消息
                if (ll_content.getVisibility() == View.VISIBLE) {
                    ll_content.setVisibility(View.GONE);
                    iv_arrows.setImageResource(R.drawable.cloud_4location_up);
                    move();

                    //startActivity(new Intent(this,TraceActivity.class));

                } else {
                    ll_content.setVisibility(View.VISIBLE);
                    iv_arrows.setImageResource(R.drawable.cloud_4location_down);
                }


                break;
            case R.id.ll_back:
                finish();
                break;
        }
    }

    @Override
    public void onRequestFailed(int i, String s) {
        ToastUtil.showToast("" + s, this);
    }

    @Override
    public void onTraceProcessing(int lineId, int index, List<LatLng> list) {
        LogUtil.e(TAG, "index:" + index + ",size:" + list.size());
    }

    @Override
    public void onFinished(int i, List<LatLng> list, int i1, int i2) {
        //ToastUtil.showToast("size:"+list.size(),this);

    }

    class TransferFragmentPagerAdapter extends FragmentPagerAdapter {
        List<Fragment> innerFragments;

        public TransferFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            innerFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {

            return innerFragments.get(position);

        }

        @Override
        public int getCount() {

            return innerFragments.size();
        }

    }
}