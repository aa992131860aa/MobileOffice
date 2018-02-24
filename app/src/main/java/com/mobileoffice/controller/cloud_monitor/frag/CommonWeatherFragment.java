package com.mobileoffice.controller.cloud_monitor.frag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.mobileoffice.R;
import com.mobileoffice.controller.cloud_monitor.TransferDetailActivity;
import com.mobileoffice.json.MarkerBean;
import com.mobileoffice.json.Weather;
import com.mobileoffice.json.WeatherHight;
import com.mobileoffice.json.WeatherHourJson;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.ToastUtil;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 99213 on 2017/8/13.
 */

public class CommonWeatherFragment extends TransferBaseFragment {
    private View root;
    private ProgressBar pb_web;

    private List<WeatherHourJson.ShowapiResBodyBean.HourListBean> mDetails;
    private String TAG = "CommonWeatherFragment";
    private WebView webView;
    private TransferDetailActivity mTransferDetailActivity;

    @Override
    public void initData() {
        mTransferDetailActivity = (TransferDetailActivity) getActivity();
        mTransferDetailActivity.hideMap();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.common_weather, container, false);
        webView = (WebView) root.findViewById(R.id.wv_weather);
        pb_web = (ProgressBar) root.findViewById(R.id.pb_web);

        mDetails = new ArrayList<>();
        //recycler();
        return root;
    }

    private void initWv() {
        if(webView==null) {
            return;
        }
        webView.loadUrl("file:///android_asset/weather.html");
        webView.getSettings().setJavaScriptEnabled(true);    //启用JS脚本
        // 设置不可缩放
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setDisplayZoomControls(false);

        pb_web.setVisibility(View.VISIBLE);
        webView.setVisibility(View.INVISIBLE);
        // 设置支持屏幕适配
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setVerticalScrollBarEnabled(false); //垂直不显示
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView, int i) {
                super.onProgressChanged(webView, i);


            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);

                if (mDetails!=null&&mDetails.size() > 0) {
                    String dataX = "";
                    String dataY = "";
                    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                    SimpleDateFormat sdfAll = new SimpleDateFormat("yyyyMMddHHmm");
                    List<Weather> weathers = new ArrayList<>();
                    List<WeatherHight> weatherHights = new ArrayList<WeatherHight>();

                    for (int i = 0; i < mDetails.size(); i++) {
                        //LogUtil.e(TAG,"weather:"+mDetails.get(i).getTime()+","+mDetails.get(i).getWeather_code());
                        if (i % 2 != 0) {
                            continue;
                        }
                        try {
                            Log.e(TAG, mDetails.get(i).getTime()+","+mDetails.get(i).getWeather_code());
                            //Log.e(TAG,"b:"+sdf.parse(mDetails.get(i).getTime()));

                            Weather weather = new Weather();
                            weather.setDate(sdf.format(sdfAll.parse(mDetails.get(i).getTime())));
                            weather.setTemperature(Integer.parseInt(mDetails.get(i).getTemperature()));
                            weathers.add(weather);

                            WeatherHight weatherHight = new WeatherHight();
                            weatherHight.setY(Integer.parseInt(mDetails.get(i).getTemperature()));
                            MarkerBean markerBean = new MarkerBean();
                            markerBean.setSymbol("url(http://app1.showapi.com/weather/icon/day/"+mDetails.get(i).getWeather_code()+".png)");
                            markerBean.setHeight(20);
                            markerBean.setWidth(20);
                            weatherHight.setMarker(markerBean);
                            weatherHights.add(weatherHight);
                            // dataY[i] = mDetails.get(i).getTemperature();

                        } catch (ParseException e) {
                            e.printStackTrace();
                            Log.e(TAG, "error:" + e.getMessage());
                        }

                    }
                    Log.e(TAG, dataX + "," + dataY);

                    //dataY = "[{ y: 26.5, marker: { symbol: 'url(https://www.highcharts.com/demo/gfx/sun.png)'}},{ y: 26.5, marker: { symbol: 'url(https://www.highcharts.com/demo/gfx/sun.png)'}},{ y: 26.5, marker: { symbol: 'url(https://www.highcharts.com/demo/gfx/sun.png)'}}]";

                    pb_web.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                    webView.loadUrl("javascript:set('" + new Gson().toJson(weathers) + "','" + new Gson().toJson(weatherHights) + "')");
                }
            }
        });

    }




    public static final CommonWeatherFragment newInstance() {
        CommonWeatherFragment fragment = new CommonWeatherFragment();

        return fragment;
    }

    public void refresh(List<WeatherHourJson.ShowapiResBodyBean.HourListBean> hourListBeen) {
        LogUtil.e(TAG, "resultHour4:" + hourListBeen);
        mDetails = hourListBeen;

            initWv();

        // mDetailContentAdapter.refresh(hourListBeen);
        //ToastUtil.showToast("gg", mContext);
        //mDetails = hourListBeen;
        //
    }

//    private void recycler() {
//
//        WeatherHourJson.ShowapiResBodyBean.HourListBean bb = new WeatherHourJson.ShowapiResBodyBean.HourListBean();
//        bb.setTime("1111");
//        bb.setWeather("ccc");
//
//        rv_content = (RecyclerView) root.findViewById(R.id.rv_weather);
//        mLinearLayoutManager = new LinearLayoutManager(getActivity());
//        mDetailContentAdapter = new WeatherContentAdapter(getActivity(), mDetails);
//        rv_content.setLayoutManager(mLinearLayoutManager);
//        rv_content.setAdapter(mDetailContentAdapter);
//        LogUtil.e(TAG, "resultHour5:" + mDetailContentAdapter);
////        mDetails.add(bb);
////        mDetails.add(bb);
////        mDetails.add(bb);
////        mDetailContentAdapter.refresh(mDetails);
//    }

//    class WeatherContentAdapter extends BaseAdapter {
//        private Context mContext;
//        private List<WeatherHourJson.ShowapiResBodyBean.HourListBean> mLists;
//        private com.mobileoffice.controller.cloud_monitor.frag.WeatherContentAdapter.HospitalChildListener mHospitalChildListener;
//        private String TAG = "WeatherContentAdapter";
//
//
//        public WeatherContentAdapter(Context context, List<WeatherHourJson.ShowapiResBodyBean.HourListBean> lists) {
//            mContext = context;
//            mLists = lists;
//
//        }
//
//        public void refresh(List<WeatherHourJson.ShowapiResBodyBean.HourListBean> lists) {
//            mLists = lists;
//            notifyDataSetChanged();
//            LogUtil.e(TAG, "lists size:" + lists.size());
//        }
//
//
//        @Override
//        public int getCount() {
//            return mLists.size();
//        }
//
//        @Override
//        public Object getItem(int i) {
//            return mLists.get(i);
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return i;
//        }
//
//        @Override
//        public View getView(int i, View view, ViewGroup viewGroup) {
//            View holder = LayoutInflater.from(mContext).inflate(R.layout.weather_item, viewGroup, false);
//            TextView tvTime = (TextView) holder.findViewById(R.id.tv_time);
//            tvTime.setText(mLists.get(i).getTime());
//            return view;
//        }
//
//
//    }
}
