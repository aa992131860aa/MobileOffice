package com.mobileoffice.controller.cloud_monitor;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobileoffice.R;
import com.mobileoffice.controller.cloud_monitor.frag.TransferBaseFragment;
import com.mobileoffice.entity.PathInfo;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by 99213 on 2017/8/11.
 */

public class MapLeftFragment extends TransferBaseFragment implements DetailContentAdapter.OnDetailClickListener {
    private View root;
    //时间轴
    private RecyclerView rv_content;
    private LinearLayoutManager mLinearLayoutManager;
    private DetailContentAdapter mDetailContentAdapter;
    private List<PathInfo> mDetails = new ArrayList<>();

    private TextView tv_exception;
    private MapDetailActivity mMapDetailActivity;
    private String getTime;
    //定时更新
    private ScheduledExecutorService executorService;
    //转运状态
    private String mStatus;
    //是否反转数据
    boolean isReverse = true;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dealData();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.common_rv, container, false);
        //mDetails = (ArrayList<PathInfo>) getArguments().getSerializable("pathInfos");
        getTime = (String) getArguments().get("getTime");
        mStatus = (String) getArguments().get("status");
        tv_exception = (TextView) root.findViewById(R.id.tv_exception);

        tv_exception.setVisibility(View.GONE);
        mMapDetailActivity = (MapDetailActivity) getActivity();
        recycler();
        dealData();
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
            executorService.scheduleAtFixedRate(runnable, CONSTS.REFRESH_TIME, CONSTS.REFRESH_TIME + 1000, TimeUnit.MILLISECONDS);
        }
        return root;
    }


    public static final MapLeftFragment newInstance(ArrayList<PathInfo> pathInfos, String getTime, String status) {
        MapLeftFragment fragment = new MapLeftFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("pathInfos", pathInfos);
        bundle.putString("getTime", getTime);
        bundle.putString("status", status);
        bundle.putString("type", "");
        fragment.setArguments(bundle);

        return fragment;
    }


    private void recycler() {

        //mDetails.add("1");
        rv_content = (RecyclerView) root.findViewById(R.id.rv_content);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mDetailContentAdapter = new DetailContentAdapter(getActivity(), mDetails);
        rv_content.setLayoutManager(mLinearLayoutManager);
        rv_content.setAdapter(mDetailContentAdapter);
        mDetailContentAdapter.setDetailClickListener(this);


    }

    private void dealData() {
        mDetails = new ArrayList<>();
        mDetails = CONSTS.PATH_RECORDS;

        for(int i=mDetails.size()-1;i>=0;i--){

            String content = mDetails.get(i).getContent();

            if("转运器官已装箱,开始转运".equals(content)){
                mDetails.remove(i);
            }
        }

        PathInfo pathInfo = new PathInfo();
        pathInfo.setContent("转运器官已装箱,开始转运");
        try {
            pathInfo.setMonth(getTime.split(" ")[0].substring(5, 10));
            pathInfo.setTime(getTime.split(" ")[1].substring(0,5));
        } catch (Exception e) {
            pathInfo.setMonth(getTime.split(" ")[0]);
        }
        if(mDetails.size()>0){
            if(mDetails.get(mDetails.size()-1).getOrderId()>mDetails.get(0).getOrderId()){
                isReverse = true;
                mDetails.add(0, pathInfo);
            }else{
                isReverse = false;
                mDetails.add(pathInfo);
            }
        }else{
            mDetails.add(0, pathInfo);
        }

        //ToastUtil.showToast("size:"+CONSTS.PATH_RECORDS.size()+","+mDetails.size(),getContext());
        if(isReverse) {
            Collections.reverse(mDetails);
        }
        mDetailContentAdapter.refresh(mDetails);
    }

    @Override
    public void initData() {



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CONSTS.PATH_RECORDS = new ArrayList<>();
    }

    @Override
    public void OnClick(View view, int position) {
        if (mDetails.size() - 1 == position) {

        } else {
            try {
                mMapDetailActivity.setCollisionOpen(Double.parseDouble(mDetails.get(position).getLatitude()), Double.parseDouble(mDetails.get(position).getLongitude()));
            }catch (Exception e) {

            }
        }
    }
}
