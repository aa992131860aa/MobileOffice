package com.mobileoffice.controller.cloud_monitor.frag;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.mobileoffice.R;
import com.mobileoffice.controller.BaseActivity;

/**
 * Created by 99213 on 2017/8/10.
 */

public class ChartBigActivityBefor extends BaseActivity implements View.OnClickListener{
    private WebView wv_chart;
    private ImageView iv_back;
    private String url = null;
    @Override
    protected void initVariable() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.chart_big);
        wv_chart = (WebView) findViewById(R.id.wv_chart);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        url = getIntent().getStringExtra("url");

        wv_chart.loadUrl(url);
        WebSettings settingsColumn = wv_chart.getSettings();
        settingsColumn.setSupportZoom(true);          //支持缩放
        settingsColumn.setBuiltInZoomControls(true);  //启用内置缩放装置
        settingsColumn.setJavaScriptEnabled(true);    //启用JS脚本
        wv_chart.getSettings().setDisplayZoomControls(false);//设定缩放控件隐藏
        wv_chart.setWebViewClient(new WebViewClient() {
            //当点击链接时,希望覆盖而不是打开新窗口
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);  //加载新的url
                return true;    //返回true,代表事件已处理,事件流到此终止
            }
        });
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
