package com.mobileoffice.controller.cloud_monitor.frag;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobileoffice.R;
import com.mobileoffice.controller.cloud_monitor.TransferDetailActivity;
import com.mobileoffice.utils.CONSTS;


import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by 99213 on 2017/8/9.
 */

public class CommonOneFragment extends TransferBaseFragment implements View.OnClickListener {
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

    //当前的状态,是否是转运中
    private String mStatus;
    private Thread thread;
    private LinearLayout mLl_loading;
    private TextView tv_title;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            // ToastUtil.showToast("position:"+position,getActivity());
            if (position == 0) {
                webView.reload();
            } else if (position == 1) {
                web_column.reload();
            }
        }
    };
    //定时器
    private ScheduledExecutorService executorService;
    private TransferDetailActivity mTransferDetailActivity;

    private LinearLayout mLlLoading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.frag_web, container, false);
        mTransferDetailActivity = (TransferDetailActivity) getActivity();
        webView = (WebView) root.findViewById(R.id.wv_one);
        web_column = (WebView) root.findViewById(R.id.wv_two);

        ll_line = (LinearLayout) root.findViewById(R.id.ll_line);
        ll_column = (LinearLayout) root.findViewById(R.id.ll_column);
        ll_big = (LinearLayout) root.findViewById(R.id.ll_big);
        tv_title = (TextView) root.findViewById(R.id.tv_title) ;


        iv_line = (ImageView) root.findViewById(R.id.iv_line);
        iv_column = (ImageView) root.findViewById(R.id.iv_column);
        pb_web = (ProgressBar) root.findViewById(R.id.pb_web);
        ll_line.setOnClickListener(this);
        ll_column.setOnClickListener(this);
        ll_big.setOnClickListener(this);

        mType = getArguments().getString("type");
        mOrganSeg = getArguments().getString("organSeg");
        mStatus = getArguments().getString("status");
        //mLlLoading = (LinearLayout) getArguments().getSerializable("ll_loading");

        if ("power".equals(mType)) {
            web_column.loadUrl("file:///android_asset/power_column.html");
            webView.loadUrl("file:///android_asset/power_line.html");
            tv_title.setText("电量监控");
        } else if ("temperature".equals(mType)) {
            web_column.loadUrl("file:///android_asset/temperature_column.html");
            webView.loadUrl("file:///android_asset/temperature_line.html");
            tv_title.setText("温度监控");
        } else if ("distance".equals(mType)) {
            web_column.loadUrl("file:///android_asset/distance_column.html");
            webView.loadUrl("file:///android_asset/distance_line.html");
            tv_title.setText("距离监控");
        } else if ("humidity".equals(mType)) {
            web_column.loadUrl("file:///android_asset/humidity_column.html");
            webView.loadUrl("file:///android_asset/humidity_line.html");
            tv_title.setText("湿度监控");
        } else if ("flow1".equals(mType)) {
            web_column.loadUrl("file:///android_asset/flow1_column.html");
            webView.loadUrl("file:///android_asset/flow1_line.html");
            tv_title.setText("流速1监控");
        } else if ("flow2".equals(mType)) {
            web_column.loadUrl("file:///android_asset/flow2_column.html");
            webView.loadUrl("file:///android_asset/flow2_line.html");
            tv_title.setText("流速2监控");
        } else if ("press1".equals(mType)) {
            web_column.loadUrl("file:///android_asset/press1_column.html");
            webView.loadUrl("file:///android_asset/press1_line.html");
            tv_title.setText("压力1监控");
        } else if ("press2".equals(mType)) {
            web_column.loadUrl("file:///android_asset/press2_column.html");
            webView.loadUrl("file:///android_asset/press2_line.html");
            tv_title.setText("压力2监控");
        } else if ("pupple".equals(mType)) {
            web_column.loadUrl("file:///android_asset/pupple_column.html");
            webView.loadUrl("file:///android_asset/pupple_line.html");
            tv_title.setText("气泡监控");
        }




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
                if (position == 0) {
                    showLine();
                } else if (position == 1) {
                    showColumn();
                }

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

                if (position == 0) {
                    showLine();
                } else if (position == 1) {
                    showColumn();
                }
            }
        });


        TransferDetailActivity transferDetailActivity = (TransferDetailActivity) getActivity();

        return root;
    }


    private void hide() {
//        if (TransferDetailActivity.ll_loading != null) {
//            TransferDetailActivity.ll_loading.setVisibility(View.VISIBLE);
//        }
        pb_web.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
        web_column.setVisibility(View.GONE);
    }

    private void showLine() {
//        if (TransferDetailActivity.ll_loading != null) {
//            TransferDetailActivity.ll_loading.setVisibility(View.GONE);
//        }
        pb_web.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        web_column.setVisibility(View.GONE);
    }

    private void showColumn() {
//        if (TransferDetailActivity.ll_loading != null) {
//            TransferDetailActivity.ll_loading.setVisibility(View.GONE);
//        }
        pb_web.setVisibility(View.GONE);
        webView.setVisibility(View.GONE);
        web_column.setVisibility(View.VISIBLE);
    }

    private void refreshLineData() {

        String jsData = new Gson().toJson(TransferBaseFragment.TRANSFER_RECORD_TEN);

        webView.loadUrl("javascript:set('" + jsData + "'," + CONSTS.DATE_SMALL_SIZE + ",'" + mStatus + "')");
        //ToastUtil.showToast("data:"+jsData,getActivity());
    }

    private void refreshColumnData() {
        String jsData = new Gson().toJson(TransferBaseFragment.TRANSFER_RECORD_TEN);
        web_column.loadUrl("javascript:set('" + jsData + "'," + CONSTS.DATE_SMALL_SIZE + "," + CONSTS.SMALL_NUM_SIZE + ")");
    }
    public static final CommonOneFragment newInstance(String organSeg, String type, String status) {
        CommonOneFragment fragment = new CommonOneFragment();
        Bundle bundle = new Bundle();
        bundle.putString("organSeg", organSeg);
        bundle.putString("type", type);
        bundle.putString("status", status);


        fragment.setArguments(bundle);

        return fragment;
    }


    @Override
    public void initData() {
        mTransferDetailActivity.hideMap();
        if (position == 0) {
            iv_line.setBackgroundResource(R.drawable.cloud_2detail_2line_on);
            iv_column.setBackgroundResource(R.drawable.cloud_2detail_2bar);
        } else if (position == 1) {
            iv_line.setBackgroundResource(R.drawable.cloud_2detail_2line);
            iv_column.setBackgroundResource(R.drawable.cloud_2detail_2bar_on);
        }


        Runnable runnable = new Thread() {
            @Override
            public void run() {
                super.run();
                if (CONSTS.IS_REFRESH) {

                    handler.sendEmptyMessage(1);
                }
            }
        };


        //自动刷新
        executorService = Executors.newSingleThreadScheduledExecutor();
        if ("transfering".equals(mStatus)) {
            executorService.scheduleAtFixedRate(runnable, CONSTS.REFRESH_TIME, CONSTS.REFRESH_TIME + 5000, TimeUnit.MILLISECONDS);
        }
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
                        intent.putExtra("url", "file:///android_asset/power_line.html");
                    } else if ("temperature".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", "file:///android_asset/temperature_line.html");
                    } else if ("distance".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", "file:///android_asset/distance_line.html");
                    } else if ("humidity".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", "file:///android_asset/humidity_line.html");
                    } else if ("flow1".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", "file:///android_asset/flow1_line.html");
                    } else if ("flow2".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", "file:///android_asset/flow2_line.html");
                    } else if ("press1".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", "file:///android_asset/press1_line.html");
                    } else if ("press2".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", "file:///android_asset/press2_line.html");
                    } else if ("pupple".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", "file:///android_asset/pupple_line.html");

                    }

                    intent.putExtra("type", "line");
                } else if (position == 1) {
                    if ("power".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", "file:///android_asset/power_column.html");

                    } else if ("temperature".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", "file:///android_asset/temperature_column.html");
                    } else if ("distance".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", "file:///android_asset/distance_column.html");
                    } else if ("humidity".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", "file:///android_asset/humidity_column.html");
                    } else if ("flow1".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", "file:///android_asset/flow1_column.html");
                    } else if ("flow2".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", "file:///android_asset/flow2_column.html");
                    } else if ("press1".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", "file:///android_asset/press1_column.html");
                    } else if ("press2".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", "file:///android_asset/press2_column.html");
                    } else if ("pupple".equals(mType)) {
                        intent.setClass(getActivity(), ChartBigActivity.class);
                        intent.putExtra("url", "file:///android_asset/pupple_column.html");
                    }
                    intent.putExtra("type", "column");
                }

                startActivity(intent);

                break;
        }
    }
}
