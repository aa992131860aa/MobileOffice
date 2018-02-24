package com.mobileoffice.controller.cloud_monitor.frag;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.mobileoffice.R;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.ToastUtil;


/**
 * Created by 99213 on 2017/8/10.
 */

public class ChartBigActivity extends BaseActivity implements View.OnClickListener{
    private WebView wv_chart;
    private ImageView iv_back;
    private String url = null;
    private String TAG = "ChartBigActivity";
    private String type;
    private ProgressBar pb_web;
    @Override
    protected void initVariable() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.chart_big);
        wv_chart = (WebView) findViewById(R.id.wv_chart);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        pb_web = (ProgressBar) findViewById(R.id.pb_web);
        iv_back.setOnClickListener(this);
        url = getIntent().getStringExtra("url");
        type = getIntent().getStringExtra("type");

        wv_chart.loadUrl(url);
        wv_chart.getSettings().setJavaScriptEnabled(true);    //启用JS脚本

        // 设置不可缩放
        wv_chart.getSettings().setBuiltInZoomControls(false);
        wv_chart.getSettings().setSupportZoom(false);
        wv_chart.getSettings().setDisplayZoomControls(false);


        // 设置支持屏幕适配
        wv_chart.getSettings().setUseWideViewPort(true);
        wv_chart.getSettings().setLoadWithOverviewMode(true);
        wv_chart.setVerticalScrollBarEnabled(false); //垂直不显示
        wv_chart.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView, int i) {
                super.onProgressChanged(webView, i);
                Log.e(TAG,"position:"+i);
                if(i<100){
                    pb_web.setVisibility(View.VISIBLE);
                    wv_chart.setVisibility(View.GONE);
                }
            }
        });
        wv_chart.setWebViewClient(new WebViewClient() {
            //当点击链接时,希望覆盖而不是打开新窗口

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                refreshLineData();
                pb_web.setVisibility(View.GONE);
                wv_chart.setVisibility(View.VISIBLE);
            }
        });

    }
    private void refreshLineData() {
        String jsData = new Gson().toJson(TransferBaseFragment.TRANSFER_RECORD);
        //ToastUtil.showToast(TransferBaseFragment.TRANSFER_RECORD.size()+",",this);
        if("line".equals(type)) {
            wv_chart.loadUrl("javascript:set('" + jsData + "'," + CONSTS.DATE_LARGET_SIZE + ")");
        }else if("column".equals(type)){
            wv_chart.loadUrl("javascript:set('" + jsData + "'," + CONSTS.DATE_LARGET_SIZE + "," + CONSTS.LARGET_NUM_SIZE + ")");
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
