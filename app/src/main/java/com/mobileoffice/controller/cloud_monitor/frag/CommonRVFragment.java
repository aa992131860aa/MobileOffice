package com.mobileoffice.controller.cloud_monitor.frag;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobileoffice.R;
import com.mobileoffice.controller.cloud_monitor.DetailContentAdapter;
import com.mobileoffice.controller.cloud_monitor.TransferDetailActivity;
import com.mobileoffice.entity.PathInfo;
import com.mobileoffice.json.TransferRecordJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.ToastUtil;

import java.text.ParseException;
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

public class CommonRVFragment extends TransferBaseFragment implements DetailContentAdapter.OnDetailClickListener {
    private View root;
    //时间轴
    private RecyclerView rv_content;
    private LinearLayoutManager mLinearLayoutManager;
    private DetailContentAdapter mDetailContentAdapter;
    private List<PathInfo> mDetails;
    private String mType;
    private Thread thread;
    private TextView tv_exception;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dealData();
        }
    };
    private LinearLayout ll_no_data;

    //定时更新
    private ScheduledExecutorService executorService;
    //转运状态
    private String mStatus;

    private TransferDetailActivity mTransferDetailActivity;
    ArrayList<TransferRecordJson.ObjBean> records = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.common_rv, container, false);
        mType = getArguments().getString("type");
        mStatus = getArguments().getString("status");
        //records = (ArrayList<TransferRecordJson.ObjBean>) getArguments().getSerializable("records");
        ll_no_data = (LinearLayout) root.findViewById(R.id.ll_no_data);
        tv_exception = (TextView) root.findViewById(R.id.tv_exception);
        recycler();

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
        if ("collision".equals(mType)) {
            tv_exception.setText("碰撞异常");
        } else if ("open".equals(mType)) {
            tv_exception.setText("开箱状况");
        }
        mTransferDetailActivity = (TransferDetailActivity) getActivity();
        mTransferDetailActivity.clearMap();
        return root;
    }


    public static final CommonRVFragment newInstance(String type, String status, ArrayList<TransferRecordJson.ObjBean> records) {
        CommonRVFragment fragment = new CommonRVFragment();
        Bundle bundle = new Bundle();

        bundle.putString("type", type);
        bundle.putString("status", status);
        //bundle.putSerializable("records", records);
        fragment.setArguments(bundle);

        return fragment;
    }


    private void recycler() {
        mDetails = new ArrayList<>();
        //mDetails.add("1");
        rv_content = (RecyclerView) root.findViewById(R.id.rv_content);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mDetailContentAdapter = new DetailContentAdapter(getActivity(), mDetails);
        mDetailContentAdapter.setDetailClickListener(this);
        rv_content.setLayoutManager(mLinearLayoutManager);
        rv_content.setAdapter(mDetailContentAdapter);


    }

    @Override
    public void initData() {
        dealData();
        mTransferDetailActivity.hideMap();
    }

    private void dealData() {
        mDetails = new ArrayList<>();
        int open = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        SimpleDateFormat sdfAll = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if ("collision".equals(mType)) {
            records = CONSTS.COLLISION_RECORDS;
        } else {
            records = CONSTS.OPEN_RECORDS;
        }
        //ToastUtil.showToast("大小:"+records.size(),getContext());
        for (int i = 0; i < records.size(); i++) {
            PathInfo pathInfo = new PathInfo();
            pathInfo.setTime(records.get(i).getRecordAt());
            open++;
            if ("collision".equals(mType)) {

                pathInfo.setContent("设备发生第" + open + "次碰撞");

            } else {
                pathInfo.setContent("设备发生第" + open + "次开箱");
            }
            pathInfo.setLatitude(records.get(i).getLatitude());
            pathInfo.setLongitude(records.get(i).getLongitude());
            String month = "01-01";
            try {
                month = sdf.format(sdfAll.parse(records.get(i).getCreateAt()));

            } catch (ParseException e) {
                e.printStackTrace();

            }
            pathInfo.setMonth(month);


            mDetails.add(pathInfo);
        }

        Collections.reverse(mDetails);
        mDetailContentAdapter.refresh(mDetails);


        if (mDetails.size() > 0) {
            ll_no_data.setVisibility(View.GONE);
            rv_content.setVisibility(View.VISIBLE);
        } else {
            ll_no_data.setVisibility(View.VISIBLE);
            rv_content.setVisibility(View.GONE);
        }

    }


    @Override
    public void OnClick(View view, int position) {
        mTransferDetailActivity.showMap();
        mTransferDetailActivity.locationCollision(Double.parseDouble(mDetails.get(position).getLongitude()), Double.parseDouble(mDetails.get(position).getLatitude()));
    }
}
