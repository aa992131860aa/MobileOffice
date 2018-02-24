package com.mobileoffice.controller.cloud_monitor.frag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mobileoffice.R;
import com.mobileoffice.controller.BaseFragment;
import com.mobileoffice.http.URL;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.view.NestedScrollWebView;

/**
 * Created by 99213 on 2017/11/17.
 */

public class HistoryChartFragment extends BaseFragment {
    View root  ;
    NestedScrollWebView wv_history_chart;
    private String mUrl = "";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         root = inflater.inflate(R.layout.history_chart_frag,container,false);
        wv_history_chart = (NestedScrollWebView) root.findViewById(R.id.wv_history_chart);
        mUrl = URL.STATISTICS  + "&op=h5";
        loadWeb();
        return root;

    }
    private void loadWeb() {
        wv_history_chart.getSettings().setJavaScriptEnabled(true);
        //加载需要显示的网页
        wv_history_chart.loadUrl(cjkEncode(mUrl));

        wv_history_chart.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView, int i) {
                super.onProgressChanged(webView, i);
                //LogUtil.e(TAG, "progress:" + i);

            }
        });

        wv_history_chart.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);

               // LogUtil.e(TAG, "s:" + s);

            }
        });
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
}
