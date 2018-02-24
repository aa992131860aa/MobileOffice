package com.mobileoffice.controller.cloud_monitor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobileoffice.R;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.controller.cloud_monitor.frag.HistoryChartFragment;
import com.mobileoffice.http.URL;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.view.NestedScrollWebView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by 99213 on 2017/6/8.
 */

public class HistoryChartActivity extends BaseActivity implements View.OnClickListener {
    private NestedScrollWebView wv_history_chart;
    private LinearLayout ll_back;
    private String action;
    private String phone;
    private String startTime = "";
    private String endTime = "";
    private String organ = "";
    private String transferPerson = "";
    private String startAddress = "";
    private String condition = "";
    private String mSql = "";
    private String mUrl = "";
    private String TAG = "HistoryChartActivity";
    private ViewPager vp_content;
    private MainFragmentPagerAdapter mMainFragmentPagerAdapter;
    private List<Fragment> mFragments;
    private TextView tv_time;

    @Override
    protected void initVariable() {
        action = getIntent().getStringExtra("action");
        phone = getIntent().getStringExtra("phone");
        startTime = getIntent().getStringExtra("startTime");
        endTime = getIntent().getStringExtra("endTime");
        organ = getIntent().getStringExtra("organ");
        transferPerson = getIntent().getStringExtra("transferPerson");
        startAddress = getIntent().getStringExtra("startAddress");

        try {
            dealCondition();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        loadWeb();

        //fragent();
    }

    private void fragent() {
        mFragments = new ArrayList<>();
        HistoryChartFragment cloudMonitorFragment = new HistoryChartFragment();
        mFragments.add(cloudMonitorFragment);
        mMainFragmentPagerAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
        vp_content.setAdapter(mMainFragmentPagerAdapter);
    }

    private void loadWeb() {
        wv_history_chart.getSettings().setJavaScriptEnabled(true);
        //加载需要显示的网页
        wv_history_chart.loadUrl(cjkEncode(mUrl));

        wv_history_chart.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView, int i) {
                super.onProgressChanged(webView, i);
                LogUtil.e(TAG, "progress:" + i);
                showWaitDialog(getResources().getString(R.string.loading),true,"");

            }
        });

        wv_history_chart.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);

                LogUtil.e(TAG, "s:" + s);
                dismissDialog();
            }
        });
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.history_chart);
        wv_history_chart = (NestedScrollWebView) findViewById(R.id.wv_history_chart);
        //vp_content = (ViewPager) findViewById(R.id.vp_content);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        tv_time = (TextView) findViewById(R.id.tv_time);
        //设置WebView属性，能够执行Javascript脚本


        ll_back.setOnClickListener(this);
    }

    private void dealCondition() throws ParseException {
        String timeTitle = "";
        String startDate = "";
        String endDate = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdfSort = new SimpleDateFormat("M-d");
        endDate = sdfSort.format(new Date());
        startDate = SharePreUtils.getString("create_time", "", this);
        startDate = sdfSort.format(sdf.parse(startDate));
        if ("getTransferHistory".equals(action)) {
            SimpleDateFormat sdfAll = new SimpleDateFormat("yyyy-MM-dd");
            if (startTime != null && !"".equals(startTime)) {
                condition += "&startTime=" + startTime;

                startDate = sdfSort.format(sdfAll.parse(startTime));
            }
            if (endTime != null && !"".equals(endTime)) {
                condition += "&endTime=" + endTime;
                endDate = sdfSort.format(sdfAll.parse(endTime));
            }
            if (organ != null && !"".equals(organ)) {
                condition += "&organ=" + organ;
            }
            if (transferPerson != null && !"".equals(transferPerson)) {
                condition += "&transferPerson=" + transferPerson;
            }

            if (startAddress != null && !"".equals(startAddress)) {
                condition += "&startAddress=" + startAddress;
            }

            if (phone != null && !"".equals(phone)) {
                condition += "&phone=" + phone;
            }

            mUrl = URL.STATISTICS + condition + "&op=h5";

        }
        timeTitle = startDate + " - " + endDate + " 转运统计分析";
        tv_time.setText(timeTitle);
        LogUtil.e(TAG, mUrl);

    }

    private String cjkEncode(String text) {

        if (text == null) {
            return "";
        }
        String newText = "";
        for (int i = 0; i < text.length(); i++) {
            char code = text.charAt(i);
            if (code >= 128 || code == 91 || code == 93) {  //91 is "[", 93 is "]".
                newText += "[" + Integer.toHexString((int) code) + "]";

            } else {
                newText += text.charAt(i);
            }
        }
        return newText;

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                finish();
                break;
        }
    }

    public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
        List<Fragment> innerFragments;

        public MainFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
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

//    @Override
//    //设置回退
//    //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if ((keyCode == KeyEvent.KEYCODE_BACK) && wv_history_chart.canGoBack()) {
//            wv_history_chart.goBack(); //goBack()表示返回WebView的上一页面
//            return true;
//        }
//        return false;
//    }
}
