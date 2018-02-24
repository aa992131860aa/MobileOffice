package com.mobileoffice.controller.link;


import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobileoffice.R;
import com.mobileoffice.controller.cloud_monitor.CloudMonitorFragment;

/**
 * Created by 99213 on 2017/5/19.
 * <p>
 * 医管局：http://www.nhfpc.gov.cn/yzygj/s3585/201605/940f44e39f1e452e8e35c37593025537.shtml?from=timeline&isappinstalled=0
 * 肝：http://www.cltr.org
 * 肾：http://www.csrkt.org/main/index.do
 * 心脏：http://chtr.cotr.cn/login.jsp
 * 肺：http://clutr.cotr.cn/login.jsp
 * 人体器官分配：https://www.cot.org.cn
 * 人体器官管理中心脏: http://www.china-organdonation.org
 */
public class LinkFragment extends Fragment implements View.OnClickListener {
    private View root;
    private LinearLayout ll_link_jsw;
    private LinearLayout ll_link_shen;
    private LinearLayout ll_link_fenpei;
    private LinearLayout ll_link_xin;
    private LinearLayout ll_link_fei;
    private LinearLayout ll_link_gan;
    private LinearLayout ll_link_guanli;

    private TextView tv_link_jsw;
    private TextView tv_link_shen;
    private TextView tv_link_fenpei;
    private TextView tv_link_xin;
    private TextView tv_link_fei;
    private TextView tv_link_gan;
    private TextView tv_link_guanli;


    private String ygj = "http://www.nhfpc.gov.cn/yzygj/s3585/201605/940f44e39f1e452e8e35c37593025537.shtml?from=timeline&isappinstalled=0";
    private String gan = "http://www.cltr.org";
    private String shen = "http://www.csrkt.org/main/index.do";
    private String xin = "http://chtr.cotr.cn/login.jsp";
    private String fei = "http://clutr.cotr.cn/login.jsp";
    private String fenpei = "https://www.cot.org.cn";
    private String guanli = "http://www.china-organdonation.org";
    public static final LinkFragment newInstance() {
        LinkFragment fragment = new LinkFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("organSeg", organSeg);
//        bundle.putString("type", type);
//        fragment.setArguments(bundle);

        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.link, container, false);

        ll_link_jsw = (LinearLayout) root.findViewById(R.id.ll_link_jsw);
        tv_link_jsw = (TextView) root.findViewById(R.id.tv_link_jsw);
        ll_link_jsw.setOnClickListener(this);

        ll_link_shen = (LinearLayout) root.findViewById(R.id.ll_link_shen);
        tv_link_shen = (TextView) root.findViewById(R.id.tv_link_shen);
        ll_link_shen.setOnClickListener(this);

        ll_link_fenpei = (LinearLayout) root.findViewById(R.id.ll_link_fenpei);
        tv_link_fenpei = (TextView) root.findViewById(R.id.tv_link_fenpei);
        ll_link_fenpei.setOnClickListener(this);

        ll_link_xin = (LinearLayout) root.findViewById(R.id.ll_link_xin);
        tv_link_xin = (TextView) root.findViewById(R.id.tv_link_xin);
        ll_link_xin.setOnClickListener(this);

        ll_link_fei = (LinearLayout) root.findViewById(R.id.ll_link_fei);
        tv_link_fei = (TextView) root.findViewById(R.id.tv_link_fei);
        ll_link_fei.setOnClickListener(this);

        ll_link_gan = (LinearLayout) root.findViewById(R.id.ll_link_gan);
        tv_link_gan = (TextView) root.findViewById(R.id.tv_link_gan);
        ll_link_gan.setOnClickListener(this);

        ll_link_guanli = (LinearLayout) root.findViewById(R.id.ll_link_guanli);
        tv_link_guanli = (TextView) root.findViewById(R.id.tv_link_guanli);
        ll_link_guanli.setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.ll_link_jsw:
                intent.setClass(getActivity(), LikeWebActivity.class);
                intent.putExtra("title",tv_link_jsw.getText().toString().trim());
                intent.putExtra("url", ygj);
                startActivity(intent);
                break;
            case R.id.ll_link_shen:
                intent.setClass(getActivity(), LikeWebActivity.class);
                intent.putExtra("title",tv_link_shen.getText().toString().trim());
                intent.putExtra("url", shen);
                startActivity(intent);
                break;
            case R.id.ll_link_fenpei:
                intent.setClass(getActivity(), LikeWebActivity.class);
                intent.putExtra("title",tv_link_fenpei.getText().toString().trim());
                intent.putExtra("url", fenpei);
                startActivity(intent);
                break;
            case R.id.ll_link_xin:
                intent.setClass(getActivity(), LikeWebActivity.class);
                intent.putExtra("title",tv_link_xin.getText().toString().trim());
                intent.putExtra("url", xin);
                startActivity(intent);
                break;
            case R.id.ll_link_fei:
                intent.setClass(getActivity(), LikeWebActivity.class);
                intent.putExtra("title",tv_link_fei.getText().toString().trim());
                intent.putExtra("url", fei);
                startActivity(intent);
                break;
            case R.id.ll_link_gan:
                intent.setClass(getActivity(), LikeWebActivity.class);
                intent.putExtra("title",tv_link_gan.getText().toString().trim());
                intent.putExtra("url", gan);
                startActivity(intent);
                break;
            case R.id.ll_link_guanli:
                intent.setClass(getActivity(), LikeWebActivity.class);
                intent.putExtra("title",tv_link_guanli.getText().toString().trim());
                intent.putExtra("url", guanli);
                startActivity(intent);
                break;
        }
    }
}
