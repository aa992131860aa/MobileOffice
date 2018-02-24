package com.mobileoffice.controller.cloud_monitor.frag;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.mobileoffice.R;
import com.mobileoffice.utils.CONSTS;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * Created by 99213 on 2017/8/9.
 */

public class CommonOneFragmentBefor extends TransferBaseFragment implements View.OnClickListener {
    private View root;
    private String mOrganSeg;

    //webView 显示
    private WebView webView;
    private WebView web_column;

    //切换图表的按钮
    private LinearLayout ll_line;
    private LinearLayout ll_column;
    private LinearLayout ll_big;

    private ImageView iv_line;
    private ImageView iv_column;

    private String baseUrl = "http://116.62.28.28:8888/WebReport/ReportServer?formlet=";

    //当前选择的图表的位置
    private int position;

    //图表的类型模式
    private String mType;

    private String TAG = "CommonOneFragment";
    private ProgressBar pb_web;
    Handler mHandler = new Handler();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.frag_web, container, false);
        webView = (WebView) root.findViewById(R.id.wv_one);
        web_column = (WebView) root.findViewById(R.id.wv_two);

        ll_line = (LinearLayout) root.findViewById(R.id.ll_line);
        ll_column = (LinearLayout) root.findViewById(R.id.ll_column);
        ll_big = (LinearLayout) root.findViewById(R.id.ll_big);


        iv_line = (ImageView) root.findViewById(R.id.iv_line);
        iv_column = (ImageView) root.findViewById(R.id.iv_column);
        pb_web = (ProgressBar) root.findViewById(R.id.pb_web);
        ll_line.setOnClickListener(this);
        ll_column.setOnClickListener(this);
        ll_big.setOnClickListener(this);

        mType = getArguments().getString("type");
        mOrganSeg = getArguments().getString("organSeg");

        if ("power".equals(mType)) {
            web_column.loadUrl("file:///android_asset/power_column.html");
            //web_column.loadUrl("http://baidu.com");
            //webView.loadUrl(baseUrl + "power_line.frm&organSeg=" + mOrganSeg + "&count=" + COUNT);
            webView.loadUrl("file:///android_asset/power_line.html");
            //webView.loadUrl("http://www.baidu.com");
        } else if ("temperature".equals(mType)) {
            web_column.loadUrl(baseUrl + "temperature_column.frm&organSeg=" + mOrganSeg + "&count=" + COUNT);
            webView.loadUrl(baseUrl + "temperature_line.frm&organSeg=" + mOrganSeg + "&count=" + COUNT);
        } else if ("distance".equals(mType)) {
            web_column.loadUrl(baseUrl + "distance_column.frm&organSeg=" + mOrganSeg + "&count=" + COUNT);
            webView.loadUrl(baseUrl + "distance_line.frm&organSeg=" + mOrganSeg + "&count=" + COUNT);
        } else if ("humidity".equals(mType)) {
            web_column.loadUrl(baseUrl + "humidity_column.frm&organSeg=" + mOrganSeg + "&count=" + COUNT);
            webView.loadUrl(baseUrl + "humidity_line.frm&organSeg=" + mOrganSeg + "&count=" + COUNT);
        } else if ("flow1".equals(mType)) {
            web_column.loadUrl(baseUrl + "flow1_column.frm&organSeg=" + mOrganSeg + "&count=" + COUNT);
            webView.loadUrl(baseUrl + "flow1_line.frm&organSeg=" + mOrganSeg + "&count=" + COUNT);
        } else if ("flow2".equals(mType)) {
            web_column.loadUrl(baseUrl + "flow2_column.frm&organSeg=" + mOrganSeg + "&count=" + COUNT);
            webView.loadUrl(baseUrl + "flow2_line.frm&organSeg=" + mOrganSeg + "&count=" + COUNT);
        } else if ("press1".equals(mType)) {
            web_column.loadUrl(baseUrl + "press1_column.frm&organSeg=" + mOrganSeg + "&count=" + COUNT);
            webView.loadUrl(baseUrl + "press1_line.frm&organSeg=" + mOrganSeg + "&count=" + COUNT);
        } else if ("press2".equals(mType)) {
            web_column.loadUrl(baseUrl + "press2_column.frm&organSeg=" + mOrganSeg + "&count=" + COUNT);
            webView.loadUrl(baseUrl + "press2_line.frm&organSeg=" + mOrganSeg + "&count=" + COUNT);
        } else if ("pupple".equals(mType)) {
            web_column.loadUrl(baseUrl + "pupple_column.frm&organSeg=" + mOrganSeg + "&count=" + COUNT);
            webView.loadUrl(baseUrl + "pupple_line.frm&organSeg=" + mOrganSeg + "&count=" + COUNT);
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(position==0){
                    showLine();
                }else if(position==1){
                    showColumn();
                }

            }
        }, 3000);


        web_column.getSettings().setJavaScriptEnabled(true);    //启用JS脚本

        // 设置不可缩放
        web_column.getSettings().setBuiltInZoomControls(false);
        web_column.getSettings().setSupportZoom(false);
        web_column.getSettings().setDisplayZoomControls(false);


        // 设置支持屏幕适配
        web_column.getSettings().setUseWideViewPort(true);
        web_column.getSettings().setLoadWithOverviewMode(true);
        web_column.setVerticalScrollBarEnabled(false); //垂直不显示
        web_column.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView, int i) {
                super.onProgressChanged(webView, i);

            }
        });
        web_column.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
                refreshColumnData();
            }
        });




        webView.getSettings().setJavaScriptEnabled(true);    //启用JS脚本

        // 设置不可缩放
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setDisplayZoomControls(false);


        // 设置支持屏幕适配
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setVerticalScrollBarEnabled(false); //垂直不显示
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView, int i) {
                super.onProgressChanged(webView, i);

                if (i < 100) {

                    hide();
                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
                refreshLineData();
            }
        });

//        webView.setWebViewClient(new WebViewClient() {
//            //当点击链接时,希望覆盖而不是打开新窗口
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);  //加载新的url
//                return true;    //返回true,代表事件已处理,事件流到此终止
//            }
//        });
//
//        //点击后退按钮,让WebView后退一页(也可以覆写Activity的onKeyDown方法)
//        webView.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
//                        webView.goBack();   //后退
//                        return true;    //已处理
//                    }
//                }
//                return false;
//            }
//        });


        return root;
    }

    private void hide() {
        pb_web.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
        web_column.setVisibility(View.GONE);
    }

    private void showLine() {
        pb_web.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        web_column.setVisibility(View.GONE);
    }

    private void showColumn() {
        pb_web.setVisibility(View.GONE);
        webView.setVisibility(View.GONE);
        web_column.setVisibility(View.VISIBLE);
    }

    private void refreshLineData() {
        String jsData = new Gson().toJson(TransferBaseFragment.TRANSFER_RECORD);

        webView.loadUrl("javascript:set('" + jsData + "'," + CONSTS.DATE_SMALL_SIZE + ")");
    }
    private void   refreshColumnData() {
        String jsData = new Gson().toJson(TransferBaseFragment.TRANSFER_RECORD);

        web_column.loadUrl("javascript:set('" + jsData + "'," + CONSTS.DATE_SMALL_SIZE + ","+CONSTS.SMALL_NUM_SIZE+")");
    }

    public static final CommonOneFragmentBefor newInstance(String organSeg, String type) {
        CommonOneFragmentBefor fragment = new CommonOneFragmentBefor();
        Bundle bundle = new Bundle();
        bundle.putString("organSeg", organSeg);
        bundle.putString("type", type);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_line:
                position = 0;
                showLine();
                iv_line.setBackgroundResource(R.drawable.cloud_2detail_2line_on);
                iv_column.setBackgroundResource(R.drawable.cloud_2detail_2bar);
                break;
            case R.id.ll_column:
                position = 1;
                showColumn();
                iv_line.setBackgroundResource(R.drawable.cloud_2detail_2line);
                iv_column.setBackgroundResource(R.drawable.cloud_2detail_2bar_on);
                break;
            case R.id.ll_big:

                Intent intent = new Intent();
                //折线图
                if (position == 0) {

                    if ("power".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", baseUrl + "power_line.frm&organSeg=" + mOrganSeg + "&count=" + COUNT_ALL);
                    } else if ("temperature".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", baseUrl + "temperature_line.frm&organSeg=" + mOrganSeg + "&count=" + COUNT_ALL);
                    } else if ("distance".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", baseUrl + "distance_line.frm&organSeg=" + mOrganSeg + "&count=" + COUNT_ALL);
                    } else if ("humidity".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", baseUrl + "humidity_line.frm&organSeg=" + mOrganSeg + "&count=" + COUNT_ALL);
                    } else if ("flow1".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", baseUrl + "flow1_line.frm&organSeg=" + mOrganSeg + "&count=" + COUNT_ALL);
                    } else if ("flow2".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", baseUrl + "flow2_line.frm&organSeg=" + mOrganSeg + "&count=" + COUNT_ALL);
                    } else if ("press1".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", baseUrl + "press1_line.frm&organSeg=" + mOrganSeg + "&count=" + COUNT_ALL);
                    } else if ("press2".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", baseUrl + "press2_line.frm&organSeg=" + mOrganSeg + "&count=" + COUNT_ALL);
                    } else if ("pupple".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", baseUrl + "pupple_line.frm&organSeg=" + mOrganSeg + "&count=" + COUNT_ALL);
                    }
                } else if (position == 1) {
                    if ("power".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", baseUrl + "power_column.frm&organSeg=" + mOrganSeg + "&count=" + COUNT_ALL);
                    } else if ("temperature".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", baseUrl + "temperature_column.frm&organSeg=" + mOrganSeg + "&count=" + COUNT_ALL);
                    } else if ("distance".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", baseUrl + "distance_column.frm&organSeg=" + mOrganSeg + "&count=" + COUNT_ALL);
                    } else if ("humidity".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", baseUrl + "humidity_column.frm&organSeg=" + mOrganSeg + "&count=" + COUNT_ALL);
                    } else if ("flow1".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", baseUrl + "flow1_column.frm&organSeg=" + mOrganSeg + "&count=" + COUNT_ALL);
                    } else if ("flow2".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", baseUrl + "flow2_column.frm&organSeg=" + mOrganSeg + "&count=" + COUNT_ALL);
                    } else if ("press1".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", baseUrl + "press1_column.frm&organSeg=" + mOrganSeg + "&count=" + COUNT_ALL);
                    } else if ("press2".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", baseUrl + "press2_column.frm&organSeg=" + mOrganSeg + "&count=" + COUNT_ALL);
                    } else if ("pupple".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", baseUrl + "pupple_column.frm&organSeg=" + mOrganSeg + "&count=" + COUNT_ALL);
                    }
                }

                startActivity(intent);

                break;
        }
    }
}
