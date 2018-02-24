package com.mobileoffice.controller.cloud_monitor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.TranslateAnimation;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.mobileoffice.R;
import com.mobileoffice.application.LocalApplication;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.controller.cloud_monitor.frag.CommonOneFragment;
import com.mobileoffice.controller.cloud_monitor.frag.CommonRVFragment;
import com.mobileoffice.controller.cloud_monitor.frag.CommonWeatherFragment;
import com.mobileoffice.controller.cloud_monitor.frag.TransferBaseFragment;
import com.mobileoffice.entity.PathInfo;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.TransferJson;
import com.mobileoffice.json.TransferRecordJson;
import com.mobileoffice.json.TransferingJson;
import com.mobileoffice.json.WeatherHourJson;
import com.mobileoffice.json.WeatherIconJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.DisplayUtil;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.SeekBarHeart;
import com.mobileoffice.utils.ToastUtil;
import com.mobileoffice.view.TouchLinearLayout;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

import static com.mobileoffice.utils.DisplayUtil.dip2px;

/**
 * Created by 99213 on 2017/5/27.
 */

public class TransferDetailActivity extends BaseActivity implements View.OnClickListener {
    //基本参数
    private RelativeLayout rl_temperature;
    private RelativeLayout rl_temperature_big;
    private RelativeLayout rl_humidity;
    private RelativeLayout rl_humidity_big;
    private RelativeLayout rl_flow;
    private RelativeLayout rl_flow_big;
    private RelativeLayout rl_stress;
    private RelativeLayout rl_stress_big;

    private TextView iv_temperature_warning;
    private TextView tv_temperature;
    private TextView iv_humidity_warning;
    private TextView tv_humidity;
    private TextView iv_flow_warning;
    private TextView tv_flow;
    private TextView tv_stress_warning;
    private TextView tv_stress;

    private TextView iv_temperature_warning_big;
    private TextView tv_temperature_big;
    private TextView iv_humidity_warning_big;
    private TextView tv_humidity_big;
    private TextView iv_flow_warning_big;
    private TextView tv_flow_big;
    private TextView iv_stress_warning_big;
    private TextView tv_stress_big;

    private LinearLayout ll_conversation;

    //起始位置
    private TextView tv_transfer_detail_start;
    //结束位置
    private TextView tv_transfer_detail_end;
    //起始天气位置
    private LinearLayout ll_start_weather;
    //结束天气位置
    private LinearLayout ll_end_weather;

    private TextView tv_start_temperature;
    private TextView tv_end_temperature;

    private ImageView iv_organ;
    private TextView tv_organ;
    private TextView tv_time;
    private TextView tv_distance;
    private ImageView iv_start_icon;
    private TextView tv_start_content;
    private ImageView iv_end_icon;
    private TextView tv_end_content;
    private TextView tv_organ_seg;

    private WebView wv_data;
    private String TAG = "TransferDetailActivity";
    private String request;
    private int titleIndex = 0;
    private int oneIndex = 0;
    private int width;
    private int height;
    private int addWidth;
    private RelativeLayout.LayoutParams layoutParamsSpace;
    private RelativeLayout.LayoutParams layoutParams;
    //初始化地图控制器对象
    AMap mAMap;

    private TextView tv_one;
    private TextView tv_two;
    private TextView tv_three;
    private TextView tv_four;

    private int top = 0;

    //小方块的layout
    private TouchLinearLayout ll_data;


    private LinearLayout ll_hospital_back;


    int small;
    int smallImage;
    TransferJson.ObjBean objBean;
    private LinearLayout ll_info;

    private ImageView iv_one;
    private ImageView iv_big_one;

    private ImageView iv_two;
    private ImageView iv_big_two;

    private ImageView iv_three;
    private ImageView iv_big_three;

    private ImageView iv_four;
    private ImageView iv_big_four;

    private int rlOne = 0;
    private int rlTwo = 0;
    private int rlThree = 0;
    private int rlFour = 0;

    private TextView tv_two_line;
    private TextView tv_three_line;


    /**
     * 手指按下时的x坐标
     */
    private int xDown;
    /**
     * 手指按下时的y坐标
     */
    private int yDown;
    /**
     * 手指移动时的x坐标
     */
    private int xMove;
    /**
     * 手指移动时的y坐标
     */
    private int yMove;
    //屏幕的宽度
    private int mScreenWidth = 0;
    //滑动的最小距离
    private int mTouchSlope = 0;
    //处理一次滑动
    private boolean isSlope = false;
    //处理一次滑动
    private boolean isRlSlope = false;

    private TouchLinearLayout ll_content;

    //viewpager
    private ViewPager vp_content;
    private TransferFragmentPagerAdapter mTransferFragmentPagerAdapter;
    private List<Fragment> mContentFragment;
    private FragmentManager fm;

    private int rlIndex = 0;
    private LinearLayout ll_data_over;
    //上一个pager的位置
    private int lastPosition;
    private CommonWeatherFragment commonOneFragment14;
    private List<WeatherHourJson.ShowapiResBodyBean.HourListBean> mHourList;

    //赋值给变量
    private String power;
    private String temperature;
    private String distance;
    private String collision;
    private String humidity;
    private String open;
    private String flow1;
    private String flow2;
    private String press1;
    private String press2;
    private String pupple;
    private String weather;

    private SeekBarHeart sbh_transfer_item;

    private RelativeLayout rl_top;

    private Thread thread;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private LinearLayout ll_no_data;
    private int overDistance;

    private MapView mv_detail;

    //从系统消息传来的类型 open,collision,temperature
    private String mType;

    //定时更新
    private ScheduledExecutorService mScheduledExecutorService;

    public LinearLayout ll_loading;

    private ModifyTransferPopup mModifyTransferPopup;
    private int maskFlag = 0;
    private boolean mIsDetailMap = false;
    public ArrayList<TransferRecordJson.ObjBean> openRecords = new ArrayList<>();
    public ArrayList<TransferRecordJson.ObjBean> collisionRecords = new ArrayList<>();
    public ArrayList<PathInfo> pathInfos = new ArrayList<>();


    //是否刷新initFlag
    private boolean isRefreshFlag = true;

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mv_detail.onDestroy();
        CONSTS.IS_REFRESH = false;
        CONSTS.OPEN_RECORDS = new ArrayList<>();
        CONSTS.COLLISION_RECORDS = new ArrayList<>();
        CONSTS.PATH_RECORDS = new ArrayList<>();
        ll_loading = null;
        if (mScheduledExecutorService != null) {
            mScheduledExecutorService.shutdown();
        }
    }

    private void loadTransfer(String organSeg) {
        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "getTransfer");
        params.addBodyParameter("organSeg", organSeg);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //ToastUtil.showToast(result,TransferDetailActivity.this);
                TransferingJson transferJson = new Gson().fromJson(result, TransferingJson.class);
                if (transferJson != null && transferJson.getResult() == CONSTS.SEND_OK) {
                    String pDistance = transferJson.getObj().get(0).getDistance();
                    String pNowDistance = transferJson.getObj().get(0).getNowDistance();
                    //剩余距离
                    try {

                        if ("0.0".equals(pDistance)) {
                            sbh_transfer_item.setCount(100, objBean.getOrganSeg());
                        } else {
                            int count = (int) (Double.parseDouble(pNowDistance) / Double.parseDouble(pDistance) * 100);
                            if (count > 100) {
                                count = 100;
                            }
                            sbh_transfer_item.setCount(count, objBean.getOrganSeg());
                        }
                        int distance = (int) (Double.parseDouble(pDistance) - Double.parseDouble(pNowDistance));
                        if (distance < 0) {
                            distance = 0;
                        }
                        overDistance = distance;
                        tv_distance.setText("剩余" + distance + "km");
                        objBean.setNowDistance(distance + ".0");
                    } catch (Exception e) {
                        tv_distance.setText("剩余" + pDistance + "km");
                        objBean.setNowDistance(pDistance + ".0");
                    }
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.showToast(ex.getMessage(), TransferDetailActivity.this);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initVariable() {

        CONSTS.IS_REFRESH = true;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //ToastUtil.showToast("status:"+"transfering".equals(objBean.getStatus()),TransferDetailActivity.this);
                if (CONSTS.IS_REFRESH) {

                    loadTransferRecord(objBean.getOrganSeg(), false);
                    loadTransferRecordTen(objBean.getOrganSeg(), false);
//                    loadTransferRecordSample(objBean.getOrganSeg());
                    loadTransfer(objBean.getOrganSeg());

                }
            }
        };


        mContentFragment = new ArrayList<>();
        mScreenWidth = LocalApplication.getInstance().screenW;
        mTouchSlope = mScreenWidth / 8;
        /**
         *    Drawable drawable= getResources().getDrawable(R.drawable.drawable);
         *    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
         *    myTextview.setCompoundDrawables(drawable,null,null,null);
         */

        rl_temperature.post(new Runnable() {
            @Override
            public void run() {
                //初始化方块
                width = rl_temperature.getWidth();
                height = rl_temperature.getHeight();


                addWidth = (int) getResources().getDimension(R.dimen.top_add);
                layoutParamsSpace = new RelativeLayout.LayoutParams(width + addWidth, height + addWidth * 2);
                layoutParamsSpace.addRule(RelativeLayout.BELOW, R.id.tv_content);
                layoutParams = new RelativeLayout.LayoutParams(width + addWidth * 2, height + addWidth * 2);
                layoutParams.addRule(RelativeLayout.BELOW, R.id.tv_content);


                rl_temperature_big.setLayoutParams(layoutParamsSpace);
                rl_temperature_big.setVisibility(View.VISIBLE);
                rl_temperature.setVisibility(View.INVISIBLE);

                small = iv_temperature_warning.getTop();
                iv_temperature_warning_big.setY(small);
                smallImage = tv_temperature.getTop();

                top = (int) getResources().getDimension(R.dimen.me_space);
                if (tv_temperature_big.getTop() > 0) {
                    tv_temperature_big.setY(smallImage);
                } else {
                    tv_temperature_big.setY(top);
                }

                LogUtil.e(TAG, "small:" + iv_temperature_warning_big.getTop() + ",smallImage:" + tv_temperature_big.getTop());
                LogUtil.e(TAG, "small1:" + small + ",smallImage1:" + smallImage);
            }
        });


        objBean = (TransferJson.ObjBean) getIntent().getSerializableExtra("transfer");
        mType = getIntent().getStringExtra("type");
        if (objBean != null) {
            tv_organ.setText(objBean.getOrgan());
            if ("心脏".equals(objBean.getOrgan())) {
                iv_organ.setImageResource(R.drawable.newtrs_table2_heart_on);
            } else if ("肝脏".equals(objBean.getOrgan())) {
                iv_organ.setImageResource(R.drawable.newtrs_table2_liver_on);
            } else if ("肺".equals(objBean.getOrgan())) {
                iv_organ.setImageResource(R.drawable.newtrs_table2_lung_on);
            } else if ("肾脏".equals(objBean.getOrgan())) {
                iv_organ.setImageResource(R.drawable.newtrs_table2_kidney_on);
            } else if ("眼角膜".equals(objBean.getOrgan())) {
                iv_organ.setImageResource(R.drawable.newtrs_table2_cornea_on);
            } else if ("胰脏".equals(objBean.getOrgan())) {
                iv_organ.setImageResource(R.drawable.newtrs_table2_pancreas_on);
            }
            if (!"".equals(objBean.getModifyOrganSeg())) {
                tv_organ_seg.setText(objBean.getModifyOrganSeg());
            } else {
                tv_organ_seg.setText(objBean.getOrganSeg());
            }
            tv_time.setText(objBean.getGetTime().split(" ")[0]);
            tv_transfer_detail_start.setText(objBean.getFromCity().split("市")[0]);
            tv_transfer_detail_end.setText(objBean.getToHosp());


            //剩余距离
            try {
                if ("0.0".equals(objBean.getDistance())) {
                    sbh_transfer_item.setCount(100, objBean.getOrganSeg());
                } else {
                    int count = (int) (Double.parseDouble(objBean.getNowDistance()) / Double.parseDouble(objBean.getDistance()) * 100);
                    sbh_transfer_item.setCount(count, objBean.getOrganSeg());
                }
                int distance = (int) (Double.parseDouble(objBean.getDistance()) - Double.parseDouble(objBean.getNowDistance()));
                if (distance < 0) {
                    distance = 0;
                }
                tv_distance.setText("剩余" + distance + "km");
                objBean.setNowDistance(distance + ".0");
            } catch (Exception e) {
                tv_distance.setText("剩余" + objBean.getDistance() + "km");
                objBean.setNowDistance(objBean.getDistance() + ".0");
            }


            loadWeather(objBean.getFromCity(), iv_start_icon, tv_start_content, tv_start_temperature, 0);
            loadWeather(objBean.getToHosp(), iv_end_icon, tv_end_content, tv_end_temperature, 1);
            loadHourWeather(objBean.getToHosp(), iv_end_icon, tv_end_content, tv_end_temperature);

            TransferBaseFragment.TRANSFER_RECORD = new ArrayList<>();
            //showWaitDialog(getResources().getString(R.string.loading), false, "loading");
            loadTransferRecord(objBean.getOrganSeg(), true);
            loadTransferRecordTen(objBean.getOrganSeg(), true);
//            loadTransferRecordSample(objBean.getOrganSeg());

        }

        mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        if (objBean != null && "transfering".equals(objBean.getStatus())) {
            mScheduledExecutorService.scheduleAtFixedRate(runnable, CONSTS.REFRESH_TIME, CONSTS.REFRESH_TIME, TimeUnit.MILLISECONDS);
        }

//        wv_data.setVerticalScrollbarOverlay(true);
//        wv_data.getSettings().setJavaScriptEnabled(true);
//        wv_data.loadUrl("file:///android_asset/morrisHum.html");
//        wv_data.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                initWebViewData();  // 主动刷新
//            }
//        });


        //ToastUtil.showToast("gg", this);


        /**
         * 点击事件(是否已选中)
         * 滑动事件(第一个,左滑是否第一个      最后一个,右滑是否是最后一个)
         *
         */
        ll_content.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                int x = (int) motionEvent.getX();
                int y = (int) motionEvent.getY();
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isSlope = true;
                        xDown = x;
                        yDown = y;
                        //LogUtil.e(TAG, "down:" + motionEvent.getX() + "," + motionEvent.getY());

                        break;
                    case MotionEvent.ACTION_MOVE:
                        xMove = x;
                        yMove = y;
                        int xMDistace = Math.abs(xMove - xDown);
                        int yMDistance = Math.abs(yMove - yDown);
                        //左滑
                        if (xMove > xDown && xMDistace > mTouchSlope && isSlope) {
                            //LogUtil.e(TAG, "moveLeft:" + motionEvent.getX() + "," + motionEvent.getY());
                            isSlope = false;
                            if (titleIndex == 0) {

                            } else if (titleIndex == 1) {
                                titleIndex = 0;
                                tvOne();
                            } else if (titleIndex == 2) {
                                tvTwo();
                                titleIndex = 1;
                            } else if (titleIndex == 3) {
                                tvThree();
                                titleIndex = 2;
                            }
                        }
                        //右滑
                        else if (xMove < xDown && xMDistace > mTouchSlope && isSlope) {
                            //LogUtil.e(TAG, "moveRight:" + motionEvent.getX() + "," + motionEvent.getY());
                            isSlope = false;
                            if (titleIndex == 0) {
                                titleIndex = 1;
                                tvTwo();
                            } else if (titleIndex == 1) {
                                tvThree();
                                titleIndex = 2;
                            } else if (titleIndex == 2) {
                                tvFour();
                                titleIndex = 3;
                            } else if (titleIndex == 3) {

                            }
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        int xDistace = Math.abs(x - xDown);
                        int yDistance = Math.abs(y - yDown);
                        if (xDistace <= 10) {
                            if (x < mScreenWidth / 4) {
                                //LogUtil.e(TAG, "up0:" + motionEvent.getX() + "," + motionEvent.getY());
                                if (titleIndex != 0) {
                                    titleIndex = 0;
                                    tvOne();
                                    vp_content.setCurrentItem(0);
                                }
                            } else if (x < mScreenWidth / 2) {
                                //LogUtil.e(TAG, "up1:" + motionEvent.getX() + "," + motionEvent.getY());
                                if (titleIndex != 1) {
                                    titleIndex = 1;
                                    tvTwo();
                                    vp_content.setCurrentItem(4);
                                }
                            } else if (x < mScreenWidth / 4 * 3) {
                                //LogUtil.e(TAG, "up2:" + motionEvent.getX() + "," + motionEvent.getY());
                                if (titleIndex != 2) {
                                    tvThree();
                                    titleIndex = 2;
                                    vp_content.setCurrentItem(8);
                                }
                            } else if (x < mScreenWidth) {
                                //LogUtil.e(TAG, "up3:" + motionEvent.getX() + "," + motionEvent.getY());
                                if (titleIndex != 3) {
                                    tvFour();
                                    titleIndex = 3;
                                    vp_content.setCurrentItem(12);
                                }
                            }
                        }

                        break;
                }
                return false;
            }
        });

        /**
         * 设置rl的滑行
         */
        ll_data.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int x = (int) motionEvent.getX();
                int y = (int) motionEvent.getY();
                LogUtil.e(TAG, "touch:" + motionEvent.getX() + "," + motionEvent.getY() + "," + motionEvent.getAction());
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isRlSlope = true;
                        xDown = x;
                        yDown = y;
                        LogUtil.e(TAG, "downR:" + motionEvent.getX() + "," + motionEvent.getY());

                        break;
                    case MotionEvent.ACTION_MOVE:
                        xMove = x;
                        yMove = y;
                        int xMDistace = Math.abs(xMove - xDown);
                        int yMDistance = Math.abs(yMove - yDown);
                        LogUtil.e(TAG, "moveLeft:" + motionEvent.getX() + "," + motionEvent.getY());
                        //左滑
                        if (xMove > xDown && xMDistace > mTouchSlope && isRlSlope) {
                            LogUtil.e(TAG, "moveLeft:" + motionEvent.getX() + "," + motionEvent.getY());
                            isRlSlope = false;
                            if (rlIndex == 0) {

                            } else if (rlIndex == 1) {
                                rlIndex = 0;
                                rlOne();
                                if (titleIndex == 0) {
                                    vp_content.setCurrentItem(0);
                                } else if (titleIndex == 1) {
                                    vp_content.setCurrentItem(4);
                                } else if (titleIndex == 2) {
                                    vp_content.setCurrentItem(8);
                                } else if (titleIndex == 3) {
                                    vp_content.setCurrentItem(12);
                                }

                            } else if (rlIndex == 2) {
                                rlTwo();
                                rlIndex = 1;
                                if (titleIndex == 0) {
                                    vp_content.setCurrentItem(1);
                                } else if (titleIndex == 1) {
                                    vp_content.setCurrentItem(5);
                                } else if (titleIndex == 2) {
                                    vp_content.setCurrentItem(9);
                                } else if (titleIndex == 3) {
                                    vp_content.setCurrentItem(13);
                                }
                            } else if (rlIndex == 3) {
                                rlThree();
                                rlIndex = 2;
                                if (titleIndex == 0) {
                                    vp_content.setCurrentItem(2);
                                } else if (titleIndex == 1) {
                                    vp_content.setCurrentItem(6);
                                } else if (titleIndex == 2) {
                                    vp_content.setCurrentItem(10);
                                }
                            }
                        }
                        //右滑
                        else if (xMove < xDown && xMDistace > mTouchSlope && isRlSlope) {
                            LogUtil.e(TAG, "moveRight:" + motionEvent.getX() + "," + motionEvent.getY());
                            isRlSlope = false;
                            if (rlIndex == 0) {
                                rlIndex = 1;
                                rlTwo();
                                if (titleIndex == 0) {
                                    vp_content.setCurrentItem(1);
                                } else if (titleIndex == 1) {
                                    vp_content.setCurrentItem(5);
                                } else if (titleIndex == 2) {
                                    vp_content.setCurrentItem(9);
                                } else if (titleIndex == 3) {
                                    vp_content.setCurrentItem(13);
                                }
                            } else if (rlIndex == 1 && titleIndex != 3) {
                                rlThree();
                                rlIndex = 2;
                                if (titleIndex == 0) {
                                    vp_content.setCurrentItem(2);
                                } else if (titleIndex == 1) {
                                    vp_content.setCurrentItem(6);
                                } else if (titleIndex == 2) {
                                    vp_content.setCurrentItem(10);
                                }
                            } else if (rlIndex == 2) {
                                rlFour();
                                rlIndex = 3;
                                if (titleIndex == 0) {
                                    vp_content.setCurrentItem(3);
                                } else if (titleIndex == 1) {
                                    vp_content.setCurrentItem(7);
                                } else if (titleIndex == 2) {
                                    vp_content.setCurrentItem(11);
                                }
                            } else if (rlIndex == 3) {

                            }
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        int xDistace = Math.abs(x - xDown);
                        int yDistance = Math.abs(y - yDown);
                        LogUtil.e(TAG, "upRl:" + motionEvent.getX() + "," + motionEvent.getY());
                        if (xDistace <= 10) {
                            if (x < mScreenWidth / 4) {
                                LogUtil.e(TAG, "up0:" + motionEvent.getX() + "," + motionEvent.getY());
                                if (rlIndex != 0) {
                                    rlIndex = 0;

                                    rlOne();
                                    if (titleIndex == 0) {
                                        vp_content.setCurrentItem(0);
                                    } else if (titleIndex == 1) {
                                        vp_content.setCurrentItem(4);
                                    } else if (titleIndex == 2) {
                                        vp_content.setCurrentItem(8);
                                    } else if (titleIndex == 3) {
                                        vp_content.setCurrentItem(12);
                                    }

                                }
                            } else if (x < mScreenWidth / 2) {
                                LogUtil.e(TAG, "up1:" + motionEvent.getX() + "," + motionEvent.getY());
                                if (rlIndex != 1) {
                                    rlIndex = 1;
                                    rlTwo();
                                    if (titleIndex == 0) {
                                        vp_content.setCurrentItem(1);
                                    } else if (titleIndex == 1) {
                                        vp_content.setCurrentItem(5);
                                    } else if (titleIndex == 2) {
                                        vp_content.setCurrentItem(9);
                                    } else if (titleIndex == 3) {
                                        vp_content.setCurrentItem(13);
                                    }
                                }
                            } else if (x < mScreenWidth / 4 * 3) {
                                LogUtil.e(TAG, "up2:" + motionEvent.getX() + "," + motionEvent.getY());
                                if (rlIndex != 2) {
                                    if (titleIndex == 3) {

                                    } else {
                                        rlThree();
                                        rlIndex = 2;
                                    }
                                    if (titleIndex == 0) {
                                        vp_content.setCurrentItem(2);
                                    } else if (titleIndex == 1) {
                                        vp_content.setCurrentItem(6);
                                    } else if (titleIndex == 2) {
                                        vp_content.setCurrentItem(10);
                                    }
                                }
                            } else if (x < mScreenWidth) {
                                LogUtil.e(TAG, "up3:" + motionEvent.getX() + "," + motionEvent.getY());
                                if (rlIndex != 3) {
                                    if (titleIndex == 3) {

                                    } else {
                                        rlFour();
                                        rlIndex = 3;
                                    }
                                    if (titleIndex == 0) {
                                        vp_content.setCurrentItem(3);
                                    } else if (titleIndex == 1) {
                                        vp_content.setCurrentItem(7);
                                    } else if (titleIndex == 2) {
                                        vp_content.setCurrentItem(11);
                                    }
                                }
                            }
                        }

                        break;
                }
                return false;
            }
        });


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (maskFlag == 0 && objBean != null && objBean.getAutoTransfer() == CONSTS.AURO_TRANSFER_FINISH_NO) {
            mModifyTransferPopup = new ModifyTransferPopup(this, objBean);
            mModifyTransferPopup.showPopupWindow(ll_content);
            maskFlag = 1;
        }
    }

    private double filterSign(String name, String content) {
        try {

            if ("电量".equals(name)) {
                return Double.parseDouble(content.split("%")[0]);
            } else if ("温度".equals(name)) {
                return Double.parseDouble(content.split("℃")[0]);
            } else if ("距离".equals(name)) {
                return Double.parseDouble(content.split("km")[0]);
            } else if ("碰撞".equals(name)) {

                return Double.parseDouble(content.split("次")[0]);
            } else if ("湿度".equals(name)) {
                return Double.parseDouble(content.split("%")[0]);
            } else if ("开箱".equals(name)) {
                return Double.parseDouble(content.split("次")[0]);
            } else if ("流速1".equals(name)) {
                return Double.parseDouble(content.split("ml")[0]);
            } else if ("流速2".equals(name)) {
                return Double.parseDouble(content.split("ml")[0]);
            } else if ("压力1".equals(name)) {
                return Double.parseDouble(content.split("mmHg")[0]);
            } else if ("压力2".equals(name)) {
                return Double.parseDouble(content.split("mmHg")[0]);
            } else if ("气泡".equals(name)) {
                return Double.parseDouble(content.split("个")[0]);
            } else if ("天气".equals(name)) {
                return Double.parseDouble(content.split("℃")[0]);
            }
            return 0;
        } catch (Exception e) {
            //  ToastUtil.showToast(e.getMessage(),this);
            return 0;
        } finally {

        }


    }

    private void tvOne() {
        contentStatusSwitch();
        titleStatusSwitch();
        oneStatusSwitch();

        tv_one.setTextColor(getResources().getColor(R.color.font_black_6));
        tv_one.setBackgroundResource(R.color.white);
        layoutParamsSpace.leftMargin = 0;
        rl_temperature_big.setLayoutParams(layoutParamsSpace);

        rl_temperature_big.setVisibility(View.VISIBLE);
        rl_temperature.setVisibility(View.INVISIBLE);
        iv_temperature_warning_big.setY(small);
        if (tv_temperature_big.getTop() > 0) {
            tv_temperature_big.setY(smallImage);
        } else {
            tv_temperature_big.setY(top);
        }
        rlIndex = 0;
        //ToastUtil.showToast(":" + iv_temperature_warning.getText().toString(), this);


//        iv_temperature_warning_big.setTextColor(getResources().getColor(R.color.highlight));
//        tv_temperature_big.setTextColor(getResources().getColor(R.color.highlight));
//        Drawable drawable = getResources().getDrawable(R.drawable.cloud_2detail_2power_on);
//        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//        tv_temperature_big.setCompoundDrawables(drawable, null, null, null);
    }

    private void tvTwo() {
        contentStatusSwitch();
        titleStatusSwitch();
        twoStatusSwitch();

        tv_two.setTextColor(getResources().getColor(R.color.font_black_6));
        tv_two.setBackgroundResource(R.color.white);
        layoutParamsSpace.leftMargin = 0;
        rl_temperature_big.setLayoutParams(layoutParamsSpace);

        rl_temperature_big.setVisibility(View.VISIBLE);
        rl_temperature.setVisibility(View.INVISIBLE);
        iv_temperature_warning_big.setY(small);
        if (tv_temperature_big.getTop() > 0) {
            tv_temperature_big.setY(smallImage);
        } else {
            tv_temperature_big.setY(top);
        }
        rlIndex = 0;
    }

    private void tvThree() {
        contentStatusSwitch();
        titleStatusSwitch();
        threeStatusSwitch();
        tv_three.setTextColor(getResources().getColor(R.color.font_black_6));
        tv_three.setBackgroundResource(R.color.white);
        layoutParamsSpace.leftMargin = 0;
        rl_temperature_big.setLayoutParams(layoutParamsSpace);

        rl_temperature_big.setVisibility(View.VISIBLE);
        rl_temperature.setVisibility(View.INVISIBLE);
        iv_temperature_warning_big.setY(small);
        if (tv_temperature_big.getTop() > 0) {
            tv_temperature_big.setY(smallImage);
        } else {
            tv_temperature_big.setY(top);
        }
        rlIndex = 0;
    }

    private void tvFour() {
        titleStatusSwitch();
        fourStatusSwitch();
        tv_four.setTextColor(getResources().getColor(R.color.font_black_6));
        tv_four.setBackgroundResource(R.color.white);
        layoutParamsSpace.leftMargin = 0;
        rl_temperature_big.setLayoutParams(layoutParamsSpace);

        rl_temperature_big.setVisibility(View.VISIBLE);
        rl_temperature.setVisibility(View.INVISIBLE);
        iv_temperature_warning_big.setY(small);
        if (tv_temperature_big.getTop() > 0) {
            tv_temperature_big.setY(smallImage);
        } else {
            tv_temperature_big.setY(top);
        }
        rlIndex = 0;
    }

    /**
     * 获取天气
     */
    private void loadWeather(String address, final ImageView icon, final TextView content, final TextView temperature, final int type) {
        RequestParams params = new RequestParams(URL.WEATHER);
        params.addBodyParameter("action", "weather");
        params.addBodyParameter("weatherArea", address);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                WeatherIconJson weatherIconJson = new Gson().fromJson(result, WeatherIconJson.class);
                if (weatherIconJson != null && weatherIconJson.getShowapi_res_code() == 0) {
                    try {
                        String iconUrl = weatherIconJson.getShowapi_res_body().getNow().getWeather_pic();
                        Glide.with(TransferDetailActivity.this).load(iconUrl).error(R.drawable.cloud_2detail_1h2).into(icon);
                        content.setText(weatherIconJson.getShowapi_res_body().getNow().getWeather());
                        temperature.setText(weatherIconJson.getShowapi_res_body().getNow().getTemperature() + "℃");
                        if (type == 1) {
                            weather = weatherIconJson.getShowapi_res_body().getNow().getTemperature() + "℃";
                        }
                        LogUtil.e(TAG, "weatherResult:" + iconUrl);
                        LogUtil.e(TAG, "result:" + result);

                    } catch (Exception e) {

                    }

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(TAG, "weatherError:" + ex.getMessage());
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
     * 获取天气小时
     */
    private void loadHourWeather(String address, final ImageView icon, final TextView content, final TextView temperature) {
        RequestParams params = new RequestParams(URL.WEATHER);
        params.addBodyParameter("action", "weatherHour");
        params.addBodyParameter("weatherArea", address);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "result:" + result);
                WeatherHourJson weatherIconJson = new Gson().fromJson(result, WeatherHourJson.class);
                if (weatherIconJson != null && weatherIconJson.getShowapi_res_code() == 0) {
                    try {

                        final List<WeatherHourJson.ShowapiResBodyBean.HourListBean> listBeen = weatherIconJson.getShowapi_res_body().getHourList();
                        mHourList = listBeen;
                        for (int i = 0; i < mHourList.size(); i++) {
                            LogUtil.e(TAG, "resultHour2:" + mHourList.get(i).getWeather_code() + "," + mHourList.get(i).getTime() + "," + mHourList.get(i).getWeather());
                        }


                    } catch (Exception e) {
                        LogUtil.e(TAG, "resultHour3:" + e.getMessage() + "," + commonOneFragment14);
                    }

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(TAG, "weatherError:" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    private void initFrg(String organSeg) {

        vp_content.post(new Runnable() {
            @Override
            public void run() {
                CONSTS.VP_HEIGHT = vp_content.getMeasuredHeight() - DisplayUtil.dip2px(TransferDetailActivity.this, 60);
            }
        });

        CommonOneFragment commonOneFragment1 = CommonOneFragment.newInstance(organSeg, "power", objBean.getStatus());
        mContentFragment.add(commonOneFragment1);
        CommonOneFragment commonOneFragment2 = CommonOneFragment.newInstance(organSeg, "temperature", objBean.getStatus());
        mContentFragment.add(commonOneFragment2);
        CommonOneFragment commonOneFragment3 = CommonOneFragment.newInstance(organSeg, "distance", objBean.getStatus());
        mContentFragment.add(commonOneFragment3);
        CommonRVFragment commonOneFragment4 = CommonRVFragment.newInstance("collision", objBean.getStatus(), collisionRecords);
        mContentFragment.add(commonOneFragment4);

        CommonOneFragment commonOneFragment5 = CommonOneFragment.newInstance(organSeg, "temperature", objBean.getStatus());
        mContentFragment.add(commonOneFragment5);
        CommonOneFragment commonOneFragment6 = CommonOneFragment.newInstance(organSeg, "humidity", objBean.getStatus());
        mContentFragment.add(commonOneFragment6);
        CommonRVFragment commonOneFragment7 = CommonRVFragment.newInstance("open", objBean.getStatus(), openRecords);
        mContentFragment.add(commonOneFragment7);
        CommonRVFragment commonOneFragment8 = CommonRVFragment.newInstance("collision", objBean.getStatus(), collisionRecords);
        mContentFragment.add(commonOneFragment8);

        CommonOneFragment commonOneFragment9 = CommonOneFragment.newInstance(organSeg, "flow1", objBean.getStatus());
        mContentFragment.add(commonOneFragment9);
        CommonOneFragment commonOneFragment10 = CommonOneFragment.newInstance(organSeg, "flow2", objBean.getStatus());
        mContentFragment.add(commonOneFragment10);
        CommonOneFragment commonOneFragment11 = CommonOneFragment.newInstance(organSeg, "press1", objBean.getStatus());
        mContentFragment.add(commonOneFragment11);
        CommonOneFragment commonOneFragment12 = CommonOneFragment.newInstance(organSeg, "press2", objBean.getStatus());
        mContentFragment.add(commonOneFragment12);

        CommonOneFragment commonOneFragment13 = CommonOneFragment.newInstance(organSeg, "pupple", objBean.getStatus());
        mContentFragment.add(commonOneFragment13);
        commonOneFragment14 = CommonWeatherFragment.newInstance();
        mContentFragment.add(commonOneFragment14);


        /**
         * viewPager
         */
        fm = getSupportFragmentManager();
        mTransferFragmentPagerAdapter = new TransferFragmentPagerAdapter(getSupportFragmentManager(), mContentFragment);
        vp_content.setAdapter(mTransferFragmentPagerAdapter);
        vp_content.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0) {
                    tvOne();
                    titleIndex = 0;
                } else if (position == 1) {
                    rlIndex = 1;
                    rlTwo();
                } else if (position == 2) {
                    rlIndex = 2;
                    rlThree();
                } else if (position == 3) {

                    if (lastPosition == 4) {
                        tvOne();
                        titleIndex = 0;
                        rlIndex = 3;
                        rlFour();
                    } else {
                        rlIndex = 3;
                        rlFour();
                    }
                } else if (position == 4) {
                    tvTwo();
                    titleIndex = 1;
                } else if (position == 5) {
                    rlIndex = 1;
                    rlTwo();
                } else if (position == 6) {
                    rlIndex = 2;
                    rlThree();
                } else if (position == 7) {

                    if (lastPosition == 8) {
                        tvTwo();
                        titleIndex = 1;
                        rlIndex = 3;
                        rlFour();
                    } else {
                        rlIndex = 3;
                        rlFour();
                    }
                } else if (position == 8) {
                    tvThree();
                    titleIndex = 2;
                } else if (position == 9) {
                    rlIndex = 1;
                    rlTwo();
                } else if (position == 10) {
                    rlIndex = 2;
                    rlThree();
                } else if (position == 11) {

                    if (lastPosition == 12) {
                        tvThree();
                        titleIndex = 2;
                        rlIndex = 3;
                        rlFour();
                    } else {
                        rlIndex = 3;
                        rlFour();
                    }
                } else if (position == 12) {
                    titleIndex = 3;
                    tvFour();
                    rlOne();

                } else if (position == 13) {
                    rlIndex = 1;
                    rlTwo();
                    commonOneFragment14.refresh(mHourList);
                }
                lastPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (vp_content == null) {
            return;
        }
        vp_content.setOffscreenPageLimit(1);
        vp_content.setCurrentItem(0);
        // mType = "open";
        //根据系统传过来的类型,判断是那个
        if ("temperature".equals(mType)) {
            rlIndex = 1;
            rlTwo();
            vp_content.setCurrentItem(1);
        } else if ("collision".equals(mType)) {
            if (lastPosition == 4) {
                tvOne();
                titleIndex = 0;
                rlFour();
            } else {
                rlIndex = 3;
                rlFour();
            }
            vp_content.setCurrentItem(3);
        } else if ("open".equals(mType)) {
            rlIndex = 2;
            titleIndex = 1;
            tvTwo();
            rlThree();
            vp_content.setCurrentItem(6);
        }

    }

    /**
     * 加载详细记录数据
     *
     * @param organSeg
     */
    private void loadTransferRecordTen(final String organSeg, final boolean isRefresh) {
        RequestParams params = new RequestParams(URL.TRANSFER_RECORD);
        params.addBodyParameter("organSeg", organSeg);
        params.addBodyParameter("action", "transferRecordTen");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                // LogUtil.e(TAG, "asfsdresult:" + result);

                TransferRecordJson recordJson = new Gson().fromJson(result, TransferRecordJson.class);

                if (recordJson != null && recordJson.getResult() == CONSTS.SEND_OK) {
                    //ToastUtil.showToast(recordJson.getObj().size()+"",TransferDetailActivity.this);
                    TransferBaseFragment.TRANSFER_RECORD_TEN = recordJson.getObj();


                    if (recordJson.getObj() != null && recordJson.getObj().size() > 0) {
                        power = recordJson.getObj().get(recordJson.getObj().size() - 1).getPower() + "%";
                        temperature = recordJson.getObj().get(recordJson.getObj().size() - 1).getTemperature() + "℃";
                        distance = recordJson.getObj().get(recordJson.getObj().size() - 1).getDistance() + "km";
                        // collision = recordJson.getObj().get(recordJson.getObj().size() - 1).getCollision() + "次";
                        humidity = recordJson.getObj().get(recordJson.getObj().size() - 1).getHumidity() + "%";
                        // open = recordJson.getObj().get(recordJson.getObj().size() - 1).getOpen() + "次";
                        flow1 = recordJson.getObj().get(recordJson.getObj().size() - 1).getFlow1() == null ? "0ml/min" : recordJson.getObj().get(recordJson.getObj().size() - 1).getFlow1() + "ml/min";
                        flow2 = recordJson.getObj().get(recordJson.getObj().size() - 1).getFlow2() == null ? "0ml/min" : recordJson.getObj().get(recordJson.getObj().size() - 1).getFlow2() + "ml/min";
                        press1 = recordJson.getObj().get(recordJson.getObj().size() - 1).getPress1() == null ? "0mmHg" : recordJson.getObj().get(recordJson.getObj().size() - 1).getPress1() + "mmHg";
                        press2 = recordJson.getObj().get(recordJson.getObj().size() - 1).getPress2() == null ? "0mmHg" : recordJson.getObj().get(recordJson.getObj().size() - 1).getPress2() + "mmHg";
                        pupple = recordJson.getObj().get(recordJson.getObj().size() - 1).getPupple() + "个";
                        vp_content.setVisibility(View.VISIBLE);

                        //碰撞
                        if (recordJson.getOpen() != null && recordJson.getOpen().size() > 0) {

                            openRecords = (ArrayList<TransferRecordJson.ObjBean>) recordJson.getOpen();
                            CONSTS.OPEN_RECORDS = (ArrayList<TransferRecordJson.ObjBean>) recordJson.getOpen();

                        }
                        open = openRecords.size() + "次";

                        //开箱
                        if (recordJson.getCollision() != null && recordJson.getCollision().size() > 0) {
                            collisionRecords = (ArrayList<TransferRecordJson.ObjBean>) recordJson.getCollision();
                            CONSTS.COLLISION_RECORDS = (ArrayList<TransferRecordJson.ObjBean>) recordJson.getCollision();
                        }
                        collision = collisionRecords.size() + "次";

                        if (recordJson.getInfo() != null && recordJson.getInfo().size() > 0) {
                            pathInfos = (ArrayList<PathInfo>) recordJson.getInfo();
                            CONSTS.PATH_RECORDS = (ArrayList<PathInfo>) recordJson.getInfo();
                        }

                        if (titleIndex == 0) {

                            tv_temperature_big.setText(power);
                            tv_temperature.setText(power);
                            double temperatureInt = filterSign("温度", temperature);
                            if (temperatureInt < 0 || temperatureInt > 10) {
                                iv_two.setImageResource(R.drawable.cloud_2detail_2tag_da);
                                iv_big_two.setImageResource(R.drawable.cloud_2detail_2tag_da);
                                iv_two.setVisibility(View.VISIBLE);
                                iv_big_two.setVisibility(View.VISIBLE);
                            } else {
                                iv_two.setVisibility(View.GONE);
                                iv_big_two.setVisibility(View.GONE);
                            }


                            tv_humidity_big.setText(temperature);
                            tv_humidity.setText(temperature);


                            tv_flow_big.setText(distance);
                            tv_flow.setText(distance);


                            tv_stress_big.setText(collision);
                            tv_stress.setText(collision);

                            if (filterSign("碰撞", collision) > 0) {
                                iv_four.setImageResource(R.drawable.cloud_2detail_2tag_da);
                                iv_big_four.setImageResource(R.drawable.cloud_2detail_2tag_da);
                                iv_four.setVisibility(View.VISIBLE);
                                iv_big_four.setVisibility(View.VISIBLE);
                            } else {
                                iv_four.setVisibility(View.GONE);
                                iv_big_four.setVisibility(View.GONE);
                            }


                        }
                    } else {
                        ll_no_data.setVisibility(View.VISIBLE);
                        vp_content.setVisibility(View.GONE);

                        if (ll_loading != null) {
                            ll_loading.setVisibility(View.GONE);
                        }
                    }
                    if (isRefresh && isRefreshFlag) {
                        isRefreshFlag = false;
                        initFrg(objBean.getOrganSeg());
                    }
                    if (ll_loading != null) {
                        ll_loading.setVisibility(View.GONE);
                    }
                } else {
                    power = "0%";
                    temperature = "0℃";
                    distance = "0km";
                    collision = "0次";
                    humidity = "0%";
                    open = "0次";
                    flow1 = "0ml/min";
                    flow2 = "0ml/min";
                    press1 = "0mmHg";
                    press2 = "0mmHg";
                    pupple = "0个";
                    ll_no_data.setVisibility(View.VISIBLE);
                    vp_content.setVisibility(View.GONE);

                    if (ll_loading != null) {
                        ll_loading.setVisibility(View.GONE);
                    }
                    if (titleIndex == 0) {

                        tv_temperature_big.setText(power);
                        tv_temperature.setText(power);


                        tv_humidity_big.setText(temperature);
                        tv_humidity.setText(temperature);


                        tv_flow_big.setText(distance);
                        tv_flow.setText(distance);


                        tv_stress_big.setText(collision);
                        tv_stress.setText(collision);


                    }
                }
                dismissDialog();
                //ToastUtil.showToast(result,TransferDetailActivity.this);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                dismissDialog();
                ToastUtil.showToast(ex.getMessage(), TransferDetailActivity.this);
                ll_no_data.setVisibility(View.VISIBLE);
                vp_content.setVisibility(View.GONE);
                if (ll_loading != null) {
                    ll_loading.setVisibility(View.GONE);
                }
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
     * 加载详细记录数据
     *
     * @param organSeg
     */
    private void loadTransferRecord(final String organSeg, final boolean isRefresh) {
        RequestParams params = new RequestParams(URL.TRANSFER_RECORD);
        params.addBodyParameter("organSeg", organSeg);
        params.addBodyParameter("action", "transferRecord");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                mIsDetailMap = true;
                LogUtil.e(TAG, "result:11111111" + result);
                if (ll_loading != null) {
                    ll_loading.setVisibility(View.GONE);
                }
                TransferRecordJson recordJson = new Gson().fromJson(result, TransferRecordJson.class);
                if (recordJson != null && recordJson.getResult() == CONSTS.SEND_OK) {


                    if (recordJson.getObj() != null && recordJson.getObj().size() > 0) {
                        TransferBaseFragment.TRANSFER_RECORD = recordJson.getObj();
                        power = recordJson.getObj().get(recordJson.getObj().size() - 1).getPower() + "%";
                        temperature = recordJson.getObj().get(recordJson.getObj().size() - 1).getTemperature() + "℃";
                        distance = recordJson.getObj().get(recordJson.getObj().size() - 1).getDistance() + "km";
                        //collision = recordJson.getObj().get(recordJson.getObj().size() - 1).getCollision() + "次";
                        humidity = recordJson.getObj().get(recordJson.getObj().size() - 1).getHumidity() + "%";

                        flow1 = recordJson.getObj().get(recordJson.getObj().size() - 1).getFlow1() == null ? "0ml/min" : recordJson.getObj().get(recordJson.getObj().size() - 1).getFlow1() + "ml/min";
                        flow2 = recordJson.getObj().get(recordJson.getObj().size() - 1).getFlow2() == null ? "0ml/min" : recordJson.getObj().get(recordJson.getObj().size() - 1).getFlow2() + "ml/min";
                        press1 = recordJson.getObj().get(recordJson.getObj().size() - 1).getPress1() == null ? "0mmHg" : recordJson.getObj().get(recordJson.getObj().size() - 1).getPress1() + "mmHg";
                        press2 = recordJson.getObj().get(recordJson.getObj().size() - 1).getPress2() == null ? "0mmHg" : recordJson.getObj().get(recordJson.getObj().size() - 1).getPress2() + "mmHg";
                        pupple = recordJson.getObj().get(recordJson.getObj().size() - 1).getPupple() + "个";
                        vp_content.setVisibility(View.VISIBLE);

                        //开箱
                        if (recordJson.getOpen() != null && recordJson.getOpen().size() > 0) {

                            openRecords = (ArrayList<TransferRecordJson.ObjBean>) recordJson.getOpen();
                            CONSTS.OPEN_RECORDS = (ArrayList<TransferRecordJson.ObjBean>) recordJson.getOpen();
                        }
                        open = openRecords.size() + "次";

                        //碰撞
                        if (recordJson.getCollision() != null && recordJson.getCollision().size() > 0) {
                            collisionRecords = (ArrayList<TransferRecordJson.ObjBean>) recordJson.getCollision();
                            CONSTS.COLLISION_RECORDS = (ArrayList<TransferRecordJson.ObjBean>) recordJson.getCollision();
                        }
                        collision = collisionRecords.size() + "次";

                        if (recordJson.getInfo() != null && recordJson.getInfo().size() > 0) {
                            pathInfos = (ArrayList<PathInfo>) recordJson.getInfo();
                            CONSTS.PATH_RECORDS = (ArrayList<PathInfo>) recordJson.getInfo();
                        }
                        //在常用参数时,刷新
                        if (titleIndex == 0) {
                            //电量
                            tv_temperature_big.setText(power);
                            tv_temperature.setText(power);

                            //温度
                            double temperatureInt = filterSign("温度", temperature);
                            if (temperatureInt < 0 || temperatureInt > 10) {
                                iv_two.setImageResource(R.drawable.cloud_2detail_2tag_da);
                                iv_big_two.setImageResource(R.drawable.cloud_2detail_2tag_da);
                                iv_two.setVisibility(View.VISIBLE);
                                iv_big_two.setVisibility(View.VISIBLE);
                            } else {
                                iv_two.setVisibility(View.GONE);
                                iv_big_two.setVisibility(View.GONE);
                            }
                            tv_humidity_big.setText(temperature);
                            tv_humidity.setText(temperature);

                            //距离
                            tv_flow_big.setText(distance);
                            tv_flow.setText(distance);


                            //碰撞
                            tv_stress_big.setText(collision);
                            tv_stress.setText(collision);

                            if (filterSign("碰撞", collision) > 0) {
                                iv_four.setImageResource(R.drawable.cloud_2detail_2tag_da);
                                iv_big_four.setImageResource(R.drawable.cloud_2detail_2tag_da);
                                iv_four.setVisibility(View.VISIBLE);
                                iv_big_four.setVisibility(View.VISIBLE);
                            } else {
                                iv_four.setVisibility(View.GONE);
                                iv_big_four.setVisibility(View.GONE);
                            }


                        }
                        //在转运参数时,刷新
                        else if (titleIndex == 1) {
                            //温度
                            tv_temperature_big.setText(temperature);
                            tv_temperature.setText(temperature);
                            double temperatureInt = filterSign("温度", temperature);
                            if (temperatureInt < 0 || temperatureInt > 10) {
                                iv_one.setImageResource(R.drawable.cloud_2detail_2tag_da);
                                iv_big_one.setImageResource(R.drawable.cloud_2detail_2tag_da);
                                iv_one.setVisibility(View.VISIBLE);
                                iv_big_one.setVisibility(View.VISIBLE);
                            } else {
                                iv_one.setVisibility(View.GONE);
                                iv_big_one.setVisibility(View.GONE);
                            }

                            //湿度
                            tv_humidity_big.setText(humidity);
                            tv_humidity.setText(humidity);

                            //开箱
                            tv_flow_big.setText(open);
                            tv_flow.setText(open);
                            if (filterSign("开箱", open) > 0) {

                                iv_three.setImageResource(R.drawable.open_exception);
                                iv_big_three.setImageResource(R.drawable.open_exception);
                                iv_three.setVisibility(View.VISIBLE);
                                iv_big_three.setVisibility(View.VISIBLE);
                            } else {
                                iv_three.setVisibility(View.GONE);
                                iv_big_three.setVisibility(View.GONE);
                            }


                            //碰撞
                            tv_stress_big.setText(collision);
                            tv_stress.setText(collision);

                            if (filterSign("碰撞", collision) > 0) {
                                iv_four.setImageResource(R.drawable.cloud_2detail_2tag_da);
                                iv_big_four.setImageResource(R.drawable.cloud_2detail_2tag_da);
                                iv_four.setVisibility(View.VISIBLE);
                                iv_big_four.setVisibility(View.VISIBLE);
                            } else {
                                iv_four.setVisibility(View.GONE);
                                iv_big_four.setVisibility(View.GONE);
                            }
                        }

                    } else {
                        ll_no_data.setVisibility(View.VISIBLE);
                        vp_content.setVisibility(View.GONE);
                        if (ll_loading != null) {
                            ll_loading.setVisibility(View.GONE);
                        }
                    }
                    if (isRefresh && isRefreshFlag) {
                        isRefreshFlag = false;
                        initFrg(objBean.getOrganSeg());
                    }
                } else {
                    power = "0%";
                    temperature = "0℃";
                    distance = "0km";
                    collision = "0次";
                    humidity = "0%";
                    open = "0次";
                    flow1 = "0ml/min";
                    flow2 = "0ml/min";
                    press1 = "0mmHg";
                    press2 = "0mmHg";
                    pupple = "0个";
                    ll_no_data.setVisibility(View.VISIBLE);
                    vp_content.setVisibility(View.GONE);
                    if (ll_loading != null) {
                        ll_loading.setVisibility(View.GONE);
                    }
                    if (titleIndex == 0) {

                        tv_temperature_big.setText(power);
                        tv_temperature.setText(power);


                        tv_humidity_big.setText(temperature);
                        tv_humidity.setText(temperature);


                        tv_flow_big.setText(distance);
                        tv_flow.setText(distance);


                        tv_stress_big.setText(collision);
                        tv_stress.setText(collision);


                    }
                }
                dismissDialog();

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                dismissDialog();
                ToastUtil.showToast(ex.getMessage(), TransferDetailActivity.this);
                ll_no_data.setVisibility(View.VISIBLE);
                vp_content.setVisibility(View.GONE);
                if (ll_loading != null) {
                    ll_loading.setVisibility(View.GONE);
                }
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
     * 加载详细记录数据
     *
     * @param organSeg
     */
    private void loadTransferRecordSample(final String organSeg) {
        RequestParams params = new RequestParams(URL.TRANSFER_RECORD);
        params.addBodyParameter("organSeg", organSeg);
        params.addBodyParameter("action", "transferRecordSample");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e(TAG, "result:" + result);

                TransferRecordJson recordJson = new Gson().fromJson(result, TransferRecordJson.class);
                if (recordJson != null && recordJson.getResult() == CONSTS.SEND_OK) {
                    TransferBaseFragment.TRANSFER_RECORD_SAMPLE = recordJson.getObj();
                }
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

    private void initWebViewData() {
        if (request != null && request.length() > 0) {
            String jsData = new Gson().toJson(request);
            if (!TextUtils.isEmpty(jsData)) {
                wv_data.loadUrl("javascript:set('" + jsData + "')");
            }
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.transfer_detail);
        vp_content = (ViewPager) findViewById(R.id.vp_content);
        mv_detail = (MapView) findViewById(R.id.mv_detail);
        ll_content = (TouchLinearLayout) findViewById(R.id.ll_content);
        rl_temperature = (RelativeLayout) findViewById(R.id.rl_temperature);
        rl_temperature_big = (RelativeLayout) findViewById(R.id.rl_temperature_big);
        rl_humidity = (RelativeLayout) findViewById(R.id.rl_humidity);
        rl_humidity_big = (RelativeLayout) findViewById(R.id.rl_humidity_big);
        ll_data_over = (LinearLayout) findViewById(R.id.ll_data_over);
        rl_flow = (RelativeLayout) findViewById(R.id.rl_flow);
        rl_flow_big = (RelativeLayout) findViewById(R.id.rl_flow_big);
        rl_stress = (RelativeLayout) findViewById(R.id.rl_stress);
        rl_stress_big = (RelativeLayout) findViewById(R.id.rl_stress_big);
        sbh_transfer_item = (SeekBarHeart) findViewById(R.id.sbh_transfer_item);
        ll_no_data = (LinearLayout) findViewById(R.id.ll_no_data);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        ll_conversation = (LinearLayout) findViewById(R.id.ll_conversation);
        rl_top = (RelativeLayout) findViewById(R.id.rl_top);

        tv_transfer_detail_start = (TextView) findViewById(R.id.tv_transfer_detail_start);
        tv_transfer_detail_end = (TextView) findViewById(R.id.tv_transfer_detail_end);
        ll_start_weather = (LinearLayout) findViewById(R.id.ll_start_weather);
        ll_end_weather = (LinearLayout) findViewById(R.id.ll_end_weather);
        tv_end_temperature = (TextView) findViewById(R.id.tv_end_temperature);
        tv_start_temperature = (TextView) findViewById(R.id.tv_start_temperature);

        tv_one = (TextView) findViewById(R.id.tv_one);
        tv_two = (TextView) findViewById(R.id.tv_two);
        tv_three = (TextView) findViewById(R.id.tv_three);
        tv_four = (TextView) findViewById(R.id.tv_four);

        ll_data = (TouchLinearLayout) findViewById(R.id.ll_data);

        ll_hospital_back = (LinearLayout) findViewById(R.id.ll_hospital_back);

        iv_temperature_warning = (TextView) findViewById(R.id.iv_temperature_warning);
        tv_temperature = (TextView) findViewById(R.id.tv_temperature);
        iv_humidity_warning = (TextView) findViewById(R.id.iv_humidity_warning);
        tv_humidity = (TextView) findViewById(R.id.tv_humidity);
        iv_flow_warning = (TextView) findViewById(R.id.iv_flow_warning);
        tv_flow = (TextView) findViewById(R.id.tv_flow);
        tv_stress_warning = (TextView) findViewById(R.id.tv_stress_warning);
        tv_stress = (TextView) findViewById(R.id.tv_stress);

        iv_temperature_warning_big = (TextView) findViewById(R.id.iv_temperature_warning_big);
        tv_temperature_big = (TextView) findViewById(R.id.tv_temperature_big);
        iv_humidity_warning_big = (TextView) findViewById(R.id.iv_humidity_warning_big);
        tv_humidity_big = (TextView) findViewById(R.id.tv_humidity_big);
        iv_flow_warning_big = (TextView) findViewById(R.id.iv_flow_warning_big);
        tv_flow_big = (TextView) findViewById(R.id.tv_flow_big);
        iv_stress_warning_big = (TextView) findViewById(R.id.iv_stress_warning_big);
        tv_stress_big = (TextView) findViewById(R.id.tv_stress_big);
        //iv_humidity_big = (ImageView) findViewById(R.id.iv_humidity_big);

        iv_organ = (ImageView) findViewById(R.id.iv_organ);
        tv_organ = (TextView) findViewById(R.id.tv_organ);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_distance = (TextView) findViewById(R.id.tv_distance);
        iv_start_icon = (ImageView) findViewById(R.id.iv_start_icon);
        tv_start_content = (TextView) findViewById(R.id.tv_start_content);
        iv_end_icon = (ImageView) findViewById(R.id.iv_end_icon);
        tv_end_content = (TextView) findViewById(R.id.tv_end_content);
        tv_organ_seg = (TextView) findViewById(R.id.tv_organ_seg);
        ll_info = (LinearLayout) findViewById(R.id.ll_info);
        iv_one = (ImageView) findViewById(R.id.iv_one);
        iv_big_one = (ImageView) findViewById(R.id.iv_big_one);
        iv_two = (ImageView) findViewById(R.id.iv_two);
        iv_big_two = (ImageView) findViewById(R.id.iv_big_two);
        iv_three = (ImageView) findViewById(R.id.iv_three);
        iv_big_three = (ImageView) findViewById(R.id.iv_big_three);
        iv_four = (ImageView) findViewById(R.id.iv_four);
        iv_big_four = (ImageView) findViewById(R.id.iv_big_four);

        tv_two_line = (TextView) findViewById(R.id.tv_two_line);
        tv_three_line = (TextView) findViewById(R.id.tv_three_line);
        //rv_content = (RecyclerView) findViewById(R.id.rv_content);


        tv_one.setOnClickListener(this);
        tv_two.setOnClickListener(this);
        tv_three.setOnClickListener(this);
        tv_four.setOnClickListener(this);
        rl_top.setOnClickListener(this);
        ll_conversation.setOnClickListener(this);

//        rl_temperature.setOnClickListener(this);
//        rl_temperature_big.setOnClickListener(this);
//        rl_humidity.setOnClickListener(this);
//        rl_humidity_big.setOnClickListener(this);
//
//        rl_flow.setOnClickListener(this);
//        rl_stress.setOnClickListener(this);


        ll_hospital_back.setOnClickListener(this);
        ll_info.setOnClickListener(this);
        tv_transfer_detail_start.post(new Runnable() {
            @Override
            public void run() {
                setStartSpace();
            }
        });
        tv_transfer_detail_end.post(new Runnable() {
            @Override
            public void run() {
                setEndSpace();
            }
        });
        ll_start_weather.post(new Runnable() {
            @Override
            public void run() {
                setStartWeatherSpace();
            }
        });
        ll_end_weather.post(new Runnable() {
            @Override
            public void run() {
                setEndWeatherSpace();
            }
        });
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mv_detail.onCreate(savedInstanceState);

        CONSTS.MODIFY_TRANSFER = null;
        if (mAMap == null) {
            mAMap = mv_detail.getMap();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //上一个界面,修改了的值
        if(CONSTS.MODIFY_TRANSFER!=null){
            tv_organ.setText(CONSTS.MODIFY_TRANSFER.getOrgan());
            if ("心脏".equals(CONSTS.MODIFY_TRANSFER.getOrgan())) {
                iv_organ.setImageResource(R.drawable.newtrs_table2_heart_on);
            } else if ("肝脏".equals(CONSTS.MODIFY_TRANSFER.getOrgan())) {
                iv_organ.setImageResource(R.drawable.newtrs_table2_liver_on);
            } else if ("肺".equals(CONSTS.MODIFY_TRANSFER.getOrgan())) {
                iv_organ.setImageResource(R.drawable.newtrs_table2_lung_on);
            } else if ("肾脏".equals(CONSTS.MODIFY_TRANSFER.getOrgan())) {
                iv_organ.setImageResource(R.drawable.newtrs_table2_kidney_on);
            } else if ("眼角膜".equals(CONSTS.MODIFY_TRANSFER.getOrgan())) {
                iv_organ.setImageResource(R.drawable.newtrs_table2_cornea_on);
            } else if ("胰脏".equals(CONSTS.MODIFY_TRANSFER.getOrgan())) {
                iv_organ.setImageResource(R.drawable.newtrs_table2_pancreas_on);
            }
            if (!"".equals(objBean.getModifyOrganSeg())) {
                tv_organ_seg.setText(CONSTS.MODIFY_TRANSFER.getModifyOrganSeg());
            } else {
                tv_organ_seg.setText(CONSTS.MODIFY_TRANSFER.getOrganSeg());
            }

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mv_detail.onSaveInstanceState(outState);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mv_detail.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mv_detail.onPause();
    }


    /**
     * 设置起始地离左边的距离
     */
    private void setStartSpace() {
        int width = tv_transfer_detail_start.getMeasuredWidth();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LogUtil.e(TAG, "width:" + width + ",dimen:" + getResources().getDimension(R.dimen.transfer_space));
        int spaceWidth = (int) getResources().getDimension(R.dimen.transfer_space);
        int dotWidth = (int) getResources().getDimension(R.dimen.dot_size);
        params.leftMargin = spaceWidth - (width / 2 - dotWidth / 2);
        tv_transfer_detail_start.setLayoutParams(params);


    }

    /**
     * 设置起始地离右边的距离
     */
    private void setEndSpace() {
        int width = tv_transfer_detail_end.getMeasuredWidth();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LogUtil.e(TAG, "width:" + width + ",dimen:" + getResources().getDimension(R.dimen.transfer_space));
        int spaceWidth = (int) getResources().getDimension(R.dimen.transfer_space);
        int dotWidth = (int) getResources().getDimension(R.dimen.dot_size);
        params.rightMargin = spaceWidth - (width / 2 - dotWidth / 2);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        tv_transfer_detail_end.setLayoutParams(params);
    }

    /**
     * 设置起始地离左边的距离
     */
    private void setStartWeatherSpace() {
        int width = ll_start_weather.getMeasuredWidth();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LogUtil.e(TAG, "width:" + width + ",dimen:" + getResources().getDimension(R.dimen.transfer_space));
        int spaceWidth = (int) getResources().getDimension(R.dimen.transfer_space);
        int dotWidth = (int) getResources().getDimension(R.dimen.dot_size);
        params.leftMargin = spaceWidth - (width / 2 - dotWidth / 2);
        ll_start_weather.setLayoutParams(params);
    }

    /**
     * 设置起始地离左边的距离
     */
    private void setEndWeatherSpace() {
        int width = ll_end_weather.getMeasuredWidth();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LogUtil.e(TAG, "width:" + width + ",dimen:" + getResources().getDimension(R.dimen.transfer_space));
        int spaceWidth = (int) getResources().getDimension(R.dimen.dot_size);
        int dotWidth = (int) getResources().getDimension(R.dimen.dot_size);
        params.leftMargin = spaceWidth - (width / 2 - dotWidth / 2);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        ll_end_weather.setLayoutParams(params);
    }

    @Override
    protected void initData() {

        mAMap.getUiSettings().setZoomControlsEnabled(false);
        mAMap.getUiSettings().setLogoBottomMargin(-500);

    }

    public void showMap() {
        if (mv_detail.getVisibility() == View.GONE) {
            mv_detail.setVisibility(View.VISIBLE);
            //rl_top.setVisibility(View.GONE);
        }
    }

    public void hideMap() {
        if (mv_detail.getVisibility() == View.VISIBLE) {
            mv_detail.setVisibility(View.GONE);
            //rl_top.setVisibility(View.VISIBLE);
        }
    }

    public void clearMap() {
        mAMap.clear();
    }

    private List<Marker> markers;

    /**
     * 屏幕中心脏marker 跳动
     */
    public void startJumpAnimation(Marker screenMarker) {


        if (screenMarker != null) {
            //根据屏幕距离计算需要移动的目标点
            final LatLng latLng = screenMarker.getPosition();
            Point point = mAMap.getProjection().toScreenLocation(latLng);
            point.y -= dip2px(this, 125);
            LatLng target = mAMap.getProjection()
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

    public void locationCollision(double longitude, double latitude) {

        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(new LatLng(latitude, longitude));
        markerOption.title(objBean.getFromCity()).snippet("");

        markerOption.draggable(true);//设置Marker可拖动
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.drawable.cloud_4location_none)));

        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        // markerOption.setFlat(true);//设置marker平贴地图效果
        Marker screenMarker = mAMap.addMarker(markerOption);
        startJumpAnimation(screenMarker);

        //            //设置中心脏点
        mAMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                new LatLng(latitude, longitude),//新的中心脏点坐标
                11, //新的缩放级别
                0, //俯仰角0°~45°（垂直与地图时为0）
                0  ////偏航角 0~360° (正北方为0)
        )));

    }


    private void fourStatusSwitch() {
        tv_two_line.setVisibility(View.INVISIBLE);
        tv_three_line.setVisibility(View.INVISIBLE);
        Drawable drawable = getResources().getDrawable(R.drawable.cloud_2detail_1bubble);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        Drawable drawableOn = getResources().getDrawable(R.drawable.cloud_2detail_1bubble_on);
        drawableOn.setBounds(0, 0, drawableOn.getMinimumWidth(), drawableOn.getMinimumHeight());
        tv_temperature_big.setCompoundDrawables(drawableOn, null, null, null);
        tv_temperature.setCompoundDrawables(drawable, null, null, null);
        iv_temperature_warning_big.setText("气泡");
        iv_temperature_warning.setText("气泡");
        tv_temperature_big.setText(pupple);
        tv_temperature.setText(pupple);
        iv_one.setVisibility(View.GONE);
        iv_big_one.setVisibility(View.GONE);

        drawable = getResources().getDrawable(R.drawable.cloud_2detail_weather);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        drawableOn = getResources().getDrawable(R.drawable.cloud_2detail_weather_on);
        drawableOn.setBounds(0, 0, drawableOn.getMinimumWidth(), drawableOn.getMinimumHeight());
        tv_humidity_big.setCompoundDrawables(drawableOn, null, null, null);
        tv_humidity.setCompoundDrawables(drawable, null, null, null);
        iv_humidity_warning_big.setText("天气");
        iv_humidity_warning.setText("天气");
        tv_humidity_big.setText(weather);
        tv_humidity.setText(weather);
        iv_two.setVisibility(View.GONE);
        iv_big_two.setVisibility(View.GONE);

        rl_flow.setVisibility(View.INVISIBLE);
        rl_flow_big.setVisibility(View.INVISIBLE);
        iv_three.setVisibility(View.GONE);
        iv_big_three.setVisibility(View.GONE);

        rl_stress.setVisibility(View.INVISIBLE);
        rl_stress_big.setVisibility(View.INVISIBLE);
        iv_four.setVisibility(View.GONE);
        iv_big_four.setVisibility(View.GONE);


    }

    private void showRl() {
        rl_humidity.setVisibility(View.VISIBLE);
        rl_humidity_big.setVisibility(View.VISIBLE);

        rl_flow.setVisibility(View.VISIBLE);
        rl_flow_big.setVisibility(View.VISIBLE);

        rl_stress.setVisibility(View.VISIBLE);
        rl_stress_big.setVisibility(View.VISIBLE);
    }

    private void threeStatusSwitch() {
        tv_two_line.setVisibility(View.VISIBLE);
        tv_three_line.setVisibility(View.VISIBLE);
        //iv_big_two.setVisibility(View.INVISIBLE);
        //iv_two.setVisibility(View.INVISIBLE);

        Drawable drawable = getResources().getDrawable(R.drawable.cloud_2detail_1stress);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        Drawable drawableOn = getResources().getDrawable(R.drawable.cloud_2detail_1stress_on);
        drawableOn.setBounds(0, 0, drawableOn.getMinimumWidth(), drawableOn.getMinimumHeight());
        tv_temperature_big.setCompoundDrawables(drawableOn, null, null, null);
        tv_temperature.setCompoundDrawables(drawable, null, null, null);
        iv_temperature_warning_big.setText("流速1");
        iv_temperature_warning.setText("流速1");
        tv_temperature_big.setText(flow1);
        tv_temperature.setText(flow1);
        iv_one.setVisibility(View.GONE);
        iv_big_one.setVisibility(View.GONE);


        drawable = getResources().getDrawable(R.drawable.cloud_2detail_1stress);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        drawableOn = getResources().getDrawable(R.drawable.cloud_2detail_1stress_on);
        drawableOn.setBounds(0, 0, drawableOn.getMinimumWidth(), drawableOn.getMinimumHeight());
        tv_humidity_big.setCompoundDrawables(drawableOn, null, null, null);
        tv_humidity.setCompoundDrawables(drawable, null, null, null);
        iv_humidity_warning_big.setText("流速2");
        iv_humidity_warning.setText("流速2");
        tv_humidity_big.setText(flow2);
        tv_humidity.setText(flow2);
        iv_two.setVisibility(View.GONE);
        iv_big_two.setVisibility(View.GONE);

        drawable = getResources().getDrawable(R.drawable.cloud_2detail_1wind);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        drawableOn = getResources().getDrawable(R.drawable.cloud_2detail_1wind_on);
        drawableOn.setBounds(0, 0, drawableOn.getMinimumWidth(), drawableOn.getMinimumHeight());
        tv_flow_big.setCompoundDrawables(drawableOn, null, null, null);
        tv_flow.setCompoundDrawables(drawable, null, null, null);
        iv_flow_warning.setText("压力1");
        iv_flow_warning_big.setText("压力1");
        tv_flow_big.setText(press1);
        tv_flow.setText(press1);
        iv_three.setVisibility(View.GONE);
        iv_big_three.setVisibility(View.GONE);

        drawable = getResources().getDrawable(R.drawable.cloud_2detail_1wind);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        drawableOn = getResources().getDrawable(R.drawable.cloud_2detail_1wind_on);
        drawableOn.setBounds(0, 0, drawableOn.getMinimumWidth(), drawableOn.getMinimumHeight());
        tv_stress_big.setCompoundDrawables(drawableOn, null, null, null);
        tv_stress.setCompoundDrawables(drawable, null, null, null);
        iv_stress_warning_big.setText("压力2");
        tv_stress_warning.setText("压力2");
        tv_stress_big.setText(press2);
        tv_stress.setText(press2);
        iv_four.setVisibility(View.GONE);
        iv_big_four.setVisibility(View.GONE);


    }


    private void twoStatusSwitch() {
        tv_two_line.setVisibility(View.VISIBLE);
        tv_three_line.setVisibility(View.VISIBLE);
        //iv_big_two.setVisibility(View.INVISIBLE);
        //iv_two.setVisibility(View.INVISIBLE);

        Drawable drawable = getResources().getDrawable(R.drawable.cloud_2detail_1temp);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        Drawable drawableOn = getResources().getDrawable(R.drawable.cloud_2detail_1temp_on);
        drawableOn.setBounds(0, 0, drawableOn.getMinimumWidth(), drawableOn.getMinimumHeight());
        tv_temperature_big.setCompoundDrawables(drawableOn, null, null, null);
        tv_temperature.setCompoundDrawables(drawable, null, null, null);
        iv_temperature_warning_big.setText("温度");
        iv_temperature_warning.setText("温度");
        tv_temperature_big.setText(temperature);//℃
        tv_temperature.setText(temperature);
        double temperatureInt = filterSign("温度", temperature);
        if (temperatureInt < 0 || temperatureInt > 10) {
            iv_one.setImageResource(R.drawable.cloud_2detail_2tag_da);
            iv_big_one.setImageResource(R.drawable.cloud_2detail_2tag_da);
            iv_one.setVisibility(View.VISIBLE);
            iv_big_one.setVisibility(View.VISIBLE);
        } else {
            iv_one.setVisibility(View.GONE);
            iv_big_one.setVisibility(View.GONE);
        }


        drawable = getResources().getDrawable(R.drawable.cloud_2detail_1h2);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        drawableOn = getResources().getDrawable(R.drawable.cloud_2detail_1h2_on);
        drawableOn.setBounds(0, 0, drawableOn.getMinimumWidth(), drawableOn.getMinimumHeight());
        tv_humidity_big.setCompoundDrawables(drawableOn, null, null, null);
        tv_humidity.setCompoundDrawables(drawable, null, null, null);
        iv_humidity_warning_big.setText("湿度");
        iv_humidity_warning.setText("湿度");
        tv_humidity_big.setText(humidity);
        tv_humidity.setText(humidity);
        iv_two.setVisibility(View.GONE);
        iv_big_two.setVisibility(View.GONE);

        drawable = getResources().getDrawable(R.drawable.cloud_2detail_2open);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        drawableOn = getResources().getDrawable(R.drawable.cloud_2detail_2open_on);
        drawableOn.setBounds(0, 0, drawableOn.getMinimumWidth(), drawableOn.getMinimumHeight());
        tv_flow_big.setCompoundDrawables(drawableOn, null, null, null);
        tv_flow.setCompoundDrawables(drawable, null, null, null);
        iv_flow_warning.setText("开箱");
        iv_flow_warning_big.setText("开箱");
        tv_flow_big.setText(open);
        tv_flow.setText(open);

        if (filterSign("开箱", open) > 0) {

            iv_three.setImageResource(R.drawable.open_exception);
            iv_big_three.setImageResource(R.drawable.open_exception);
            iv_three.setVisibility(View.VISIBLE);
            iv_big_three.setVisibility(View.VISIBLE);
        } else {
            iv_three.setVisibility(View.GONE);
            iv_big_three.setVisibility(View.GONE);
        }


        drawable = getResources().getDrawable(R.drawable.cloud_2detail_2crash);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        drawableOn = getResources().getDrawable(R.drawable.cloud_2detail_2crash_on);
        drawableOn.setBounds(0, 0, drawableOn.getMinimumWidth(), drawableOn.getMinimumHeight());
        tv_stress_big.setCompoundDrawables(drawableOn, null, null, null);
        tv_stress.setCompoundDrawables(drawable, null, null, null);
        iv_stress_warning_big.setText("碰撞");
        tv_stress_warning.setText("碰撞");
        tv_stress_big.setText(collision);
        tv_stress.setText(collision);
        if (filterSign("碰撞", collision) > 0) {
            iv_four.setImageResource(R.drawable.cloud_2detail_2tag_da);
            iv_big_four.setImageResource(R.drawable.cloud_2detail_2tag_da);
            iv_four.setVisibility(View.VISIBLE);
            iv_big_four.setVisibility(View.VISIBLE);
        } else {
            iv_four.setVisibility(View.GONE);
            iv_big_four.setVisibility(View.GONE);
        }
        //ToastUtil.showToast(collision+"bb:"+filterSign("碰撞",collision),this);
    }

    private void oneStatusSwitch() {
        tv_two_line.setVisibility(View.VISIBLE);
        tv_three_line.setVisibility(View.VISIBLE);
        //iv_big_two.setVisibility(View.VISIBLE);
        //iv_two.setVisibility(View.VISIBLE);

        Drawable drawable = getResources().getDrawable(R.drawable.cloud_2detail_2power);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        Drawable drawableOn = getResources().getDrawable(R.drawable.cloud_2detail_2power_on);
        drawableOn.setBounds(0, 0, drawableOn.getMinimumWidth(), drawableOn.getMinimumHeight());
        tv_temperature_big.setCompoundDrawables(drawableOn, null, null, null);
        tv_temperature.setCompoundDrawables(drawable, null, null, null);
        iv_temperature_warning_big.setText("电量");
        iv_temperature_warning.setText("电量");
        tv_temperature_big.setText(power);
        tv_temperature.setText(power);
        iv_one.setVisibility(View.GONE);
        iv_big_one.setVisibility(View.GONE);

        drawable = getResources().getDrawable(R.drawable.cloud_2detail_1temp);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        drawableOn = getResources().getDrawable(R.drawable.cloud_2detail_1temp_on);
        drawableOn.setBounds(0, 0, drawableOn.getMinimumWidth(), drawableOn.getMinimumHeight());
        tv_humidity_big.setCompoundDrawables(drawableOn, null, null, null);
        tv_humidity.setCompoundDrawables(drawable, null, null, null);
        iv_humidity_warning_big.setText("温度");
        iv_humidity_warning.setText("温度");
        tv_humidity_big.setText(temperature);
        tv_humidity.setText(temperature);
        double temperatureInt = filterSign("温度", temperature);
        if (temperatureInt < 0 || temperatureInt > 10) {
            iv_two.setImageResource(R.drawable.cloud_2detail_2tag_da);
            iv_big_two.setImageResource(R.drawable.cloud_2detail_2tag_da);
            iv_two.setVisibility(View.VISIBLE);
            iv_big_two.setVisibility(View.VISIBLE);
        } else {
            iv_two.setVisibility(View.GONE);
            iv_big_two.setVisibility(View.GONE);
        }

        drawable = getResources().getDrawable(R.drawable.cloud_2detail_1juli);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        drawableOn = getResources().getDrawable(R.drawable.cloud_2detail_1juli_on);
        drawableOn.setBounds(0, 0, drawableOn.getMinimumWidth(), drawableOn.getMinimumHeight());
        tv_flow_big.setCompoundDrawables(drawableOn, null, null, null);
        tv_flow.setCompoundDrawables(drawable, null, null, null);
        iv_flow_warning.setText("距离");
        iv_flow_warning_big.setText("距离");
        tv_flow_big.setText(distance);
        tv_flow.setText(distance);
        iv_three.setVisibility(View.GONE);
        iv_big_three.setVisibility(View.GONE);

        drawable = getResources().getDrawable(R.drawable.cloud_2detail_2crash);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        drawableOn = getResources().getDrawable(R.drawable.cloud_2detail_2crash_on);
        drawableOn.setBounds(0, 0, drawableOn.getMinimumWidth(), drawableOn.getMinimumHeight());
        tv_stress_big.setCompoundDrawables(drawableOn, null, null, null);
        tv_stress.setCompoundDrawables(drawable, null, null, null);
        iv_stress_warning_big.setText("碰撞");
        tv_stress_warning.setText("碰撞");
        tv_stress_big.setText(collision);
        tv_stress.setText(collision);
        if (filterSign("碰撞", collision) > 0) {
            iv_four.setImageResource(R.drawable.cloud_2detail_2tag_da);
            iv_four.setImageResource(R.drawable.cloud_2detail_2tag_da);
            iv_four.setVisibility(View.VISIBLE);
            iv_big_four.setVisibility(View.VISIBLE);
        } else {
            iv_four.setVisibility(View.GONE);
            iv_big_four.setVisibility(View.GONE);
        }
    }

    private void titleStatusSwitch() {
        tv_one.setTextColor(getResources().getColor(R.color.white));
        tv_one.setBackgroundResource(R.color.green);

        tv_two.setTextColor(getResources().getColor(R.color.white));
        tv_two.setBackgroundResource(R.color.green);


        tv_three.setTextColor(getResources().getColor(R.color.white));
        tv_three.setBackgroundResource(R.color.green);

        tv_four.setTextColor(getResources().getColor(R.color.white));
        tv_four.setBackgroundResource(R.color.green);
    }

    private void contentStatusSwitch() {
        rl_temperature.setVisibility(View.VISIBLE);
        rl_temperature_big.setVisibility(View.INVISIBLE);

        rl_humidity.setVisibility(View.VISIBLE);
        rl_humidity_big.setVisibility(View.INVISIBLE);

        rl_flow.setVisibility(View.VISIBLE);
        rl_flow_big.setVisibility(View.INVISIBLE);

        rl_stress.setVisibility(View.VISIBLE);
        rl_stress_big.setVisibility(View.INVISIBLE);

    }

    private void contentStatusSwitchFour() {
        rl_temperature.setVisibility(View.VISIBLE);
        rl_temperature_big.setVisibility(View.INVISIBLE);

        rl_humidity.setVisibility(View.VISIBLE);
        rl_humidity_big.setVisibility(View.INVISIBLE);


    }


    private void rlOne() {
        if (titleIndex == 3) {
            contentStatusSwitchFour();
        } else {
            contentStatusSwitch();
        }
        layoutParamsSpace.leftMargin = 0;
        rl_temperature_big.setLayoutParams(layoutParamsSpace);

        rl_temperature_big.setVisibility(View.VISIBLE);
        rl_temperature.setVisibility(View.INVISIBLE);
        iv_temperature_warning_big.setY(small);
        if (tv_temperature_big.getTop() > 0) {
            tv_temperature_big.setY(smallImage);
        } else {
            tv_temperature_big.setY(top);
        }
        //ToastUtil.showToast("iv_temperature_warning:"+iv_temperature_warning.getText().toString(),this);
        iv_temperature_warning_big.setTextColor(getResources().getColor(R.color.highlight));
        tv_temperature_big.setTextColor(getResources().getColor(R.color.highlight));
        Drawable drawable = getResources().getDrawable(R.drawable.cloud_2detail_2power_on);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        //tv_temperature_big.setCompoundDrawables(drawable, null, null, null);

    }

    private void rlTwo() {

        if (titleIndex == 3) {
            contentStatusSwitchFour();
        } else {
            contentStatusSwitch();
        }


        layoutParams.leftMargin = width - addWidth;

        rl_humidity_big.setLayoutParams(layoutParams);
        rl_humidity_big.setVisibility(View.VISIBLE);
        rl_humidity.setVisibility(View.INVISIBLE);
        iv_humidity_warning_big.setY(small);

        if (tv_humidity_big.getTop() > 0) {
            tv_humidity_big.setY(smallImage);
        } else {
            tv_humidity_big.setY(top);
        }
    }

    private void rlThree() {
        contentStatusSwitch();
        layoutParams.leftMargin = width * 2 - addWidth;
        rl_flow_big.setLayoutParams(layoutParams);

        rl_flow_big.setVisibility(View.VISIBLE);
        rl_flow.setVisibility(View.INVISIBLE);
        iv_flow_warning_big.setY(small);

        if (tv_flow_big.getTop() > 0) {
            tv_flow_big.setY(smallImage);
        } else {
            tv_flow_big.setY(top);
        }
    }

    private void rlFour() {

        contentStatusSwitch();

        layoutParamsSpace.leftMargin = 3 * width - addWidth;
        rl_stress_big.setLayoutParams(layoutParamsSpace);

        rl_stress_big.setVisibility(View.VISIBLE);
        rl_stress.setVisibility(View.INVISIBLE);
        iv_stress_warning_big.setY(small);

        if (tv_stress_big.getTop() > 0) {
            tv_stress_big.setY(smallImage);
        } else {
            tv_stress_big.setY(top);
        }
    }

    @Override
    public void onClick(View view) {

        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.rl_top:
                if (!mIsDetailMap) {
                    ToastUtil.showToast("数据加载中，请稍后。。。", this);
                } else {
                    intent.setClass(this, MapDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    CONSTS.PATH_RECORDS = pathInfos;
                    intent.putExtra("transfer", objBean);
                    //intent.putExtra("pathInfos", pathInfos);

                    startActivity(intent);
                }

                break;
            case R.id.ll_hospital_back:
                finish();
                break;


            case R.id.ll_info:
                intent.setClass(this, PreviewInfoActivity.class);
                intent.putExtra("preview", objBean);
                startActivity(intent);
                break;

            case R.id.ll_conversation:
                String groupName = "讨论组";
                if (objBean == null) {
                    return;
                }
                if ("done".equals(objBean.getStatus())) {
                    groupName = "已转运-" + objBean.getFromCity() + "-" + objBean.getOrgan();
                } else if ("transfering".equals(objBean.getStatus()) && "0".equals(objBean.getIsStart())) {
                    groupName = "待转运-" + objBean.getFromCity() + "-" + objBean.getOrgan();
                } else if ("transfering".equals(objBean.getStatus()) && "1".equals(objBean.getIsStart())) {
                    groupName = "转运中-" + objBean.getFromCity() + "-" + objBean.getOrgan();
                }
                RongIM.getInstance().startConversation(this, Conversation.ConversationType.GROUP, objBean.getOrganSeg(), groupName);
                CONSTS.ORGAN_SEG = objBean.getOrganSeg();
                break;


        }
    }


}
