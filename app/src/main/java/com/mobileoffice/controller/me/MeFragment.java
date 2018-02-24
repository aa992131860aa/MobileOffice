package com.mobileoffice.controller.me;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobileoffice.R;
import com.mobileoffice.controller.cloud_monitor.CloudMonitorFragment;
import com.mobileoffice.controller.cloud_monitor.HistoryActivity;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.YunBaJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;
import com.squareup.picasso.Picasso;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by 99213 on 2017/5/19.
 */

public class MeFragment extends Fragment implements View.OnClickListener {
    private View root;
    private ImageView iv_me_photo;
    //头像
    private LinearLayout ll_me_info;
    private String photoFile;
    private String wechatUrl;
    private String flag;

    private String TAG = "MeFragment";
    private TextView tv_me_name;
    private LinearLayout ll_me_history;
    private LinearLayout ll_about_me;
    //设置
    private LinearLayout ll_me_site;

    //使用指南
    private LinearLayout ll_use;

    //我的客服
    private LinearLayout ll_service;
    //我的文件
    private LinearLayout ll_me_folder;

    //工作量统计
    private LinearLayout ll_me_workload;
    private int mRoleId;

    public static final MeFragment newInstance() {
        MeFragment fragment = new MeFragment();

        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.me, null);

        mRoleId = SharePreUtils.getInt("roleId",0,getActivity());
        tv_me_name = (TextView) root.findViewById(R.id.tv_me_name);
        ll_me_site = (LinearLayout) root.findViewById(R.id.ll_me_site);
        ll_use = (LinearLayout) root.findViewById(R.id.ll_use);
        ll_service = (LinearLayout) root.findViewById(R.id.ll_service);
        ll_about_me = (LinearLayout) root.findViewById(R.id.ll_about_me);
        ll_me_folder = (LinearLayout) root.findViewById(R.id.ll_me_folder);
        ll_me_workload = (LinearLayout) root.findViewById(R.id.ll_me_workload);
        //startActivity(new Intent(getActivity(), NewsIMActivity.class));
        tv_me_name.setText(SharePreUtils.getString("trueName", "姓名", getActivity()));

        ll_use.setOnClickListener(this);
        ll_service.setOnClickListener(this);
        ll_about_me.setOnClickListener(this);
        ll_me_folder.setOnClickListener(this);
        ll_me_workload.setOnClickListener(this);
        init();
        if(mRoleId==CONSTS.MANAGER){
            ll_me_workload.setVisibility(View.GONE);
        }else{
            ll_me_workload.setVisibility(View.VISIBLE);
        }

        //ToastUtil.showToast("gg");
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadPhoto();
    }

    private void init() {

        iv_me_photo = (ImageView) root.findViewById(R.id.iv_me_photo);
        ll_me_info = (LinearLayout) root.findViewById(R.id.ll_me_info);
        ll_me_history = (LinearLayout) root.findViewById(R.id.ll_me_history);
        ll_me_info.setOnClickListener(this);
        ll_me_history.setOnClickListener(this);
        ll_me_site.setOnClickListener(this);
    }

    private void loadPhoto() {

        photoFile = SharePreUtils.getString("photoFile", "", getActivity());
        wechatUrl = SharePreUtils.getString("wechatUrl", "", getActivity());
        flag = SharePreUtils.getString("flag", "", getActivity());

        //自己的头像
        if ("1".equals(flag) && !photoFile.equals("")) {
            Picasso.with(getActivity()).load(photoFile).error(R.drawable.msg_2list_linkman).into(iv_me_photo);
            LogUtil.e(TAG, "photoFile:" + photoFile);
        }
        //微信的头像
        else if ("0".equals(flag) && !wechatUrl.equals("")) {
            Picasso.with(getActivity()).load(wechatUrl).error(R.drawable.msg_2list_linkman).into(iv_me_photo);
            LogUtil.e(TAG, "wechatUrl:" + wechatUrl);
        }

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.ll_me_info:
                intent.setClass(getContext(), PersonInfoActivity.class);
                startActivity(intent);

                break;
            case R.id.ll_me_history:
                intent.setClass(getContext(), HistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_me_site:
                intent.setClass(getContext(), SiteActivity.class);
                startActivity(intent);

                //sendPushJson("18398850872", "添加好友", "请求添加");
                break;
            case R.id.ll_use:
                intent.setClass(getContext(), UseActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_service:
                intent.setClass(getContext(), MyServiceActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_about_me:
                intent.setClass(getContext(), AboutMeActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_me_folder:
                intent.setClass(getContext(), PdfInfoDetailActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_me_workload:

                if(mRoleId==CONSTS.ROLE_DOCTOR||mRoleId==CONSTS.ROLE_NURSE) {
                    intent.setClass(getContext(), WorkloadPersonActivity.class);
                    startActivity(intent);
                }else if(mRoleId==CONSTS.ROLE_OPO){
                    intent.setClass(getContext(), WorkloadContactPersonActivity.class);
                    startActivity(intent);
                }

                break;
        }
    }


}
