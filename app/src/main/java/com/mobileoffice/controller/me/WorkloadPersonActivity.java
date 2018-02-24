package com.mobileoffice.controller.me;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobileoffice.R;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.PhotoJson;
import com.mobileoffice.json.Workload;
import com.mobileoffice.json.WorkloadJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.DividerGridItemDecoration;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;
import com.mobileoffice.view.WebLoginPopup;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 99213 on 2017/12/6.
 */

public class WorkloadPersonActivity extends BaseActivity implements View.OnClickListener , WebLoginPopup.OnClickChangeListener {

    private RecyclerView rv_content;
    private TextView tv_month;
    private TextView tv_year;
    private LinearLayout ll_left;
    private LinearLayout ll_right;
    private LinearLayout ll_back;
    private ImageView iv_left;
    private ImageView iv_right;

    private GridLayoutManager mGridLayoutManager;
    private WorkloadPersonAdapter mWorkloadPersonAdapter;
    private List<Workload> mWorkloads;
    private String mPhone;
    private int mRoleId = 2;
    private String mTime;
    private String mLastTime;
    private int mYear;
    private int mMonth;
    private int mStartYear;
    private int mStartMonth;
    private int mLastYear;
    private int mLastMonth;
    private String mCreateTime;
    private boolean isShowLeft = true;
    private boolean isShowRight = true;

    //全体工作量统计
    private LinearLayout ll_workload_all;
    //密码输入弹出框
    private WebLoginPopup mWebLoginPopup;
    @Override
    protected void initVariable() {

        mPhone = SharePreUtils.getString("phone", "", this);
        mRoleId = SharePreUtils.getInt("roleId", 2, this);
        mCreateTime = SharePreUtils.getString("create_time", "", this);


        initRecycler();
        loadWorkload(mPhone, null, mRoleId + "",null,null,null);

        iv_right.setImageResource(R.drawable.cloud_3xinxi_right_no);

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.workload_person);
        rv_content = (RecyclerView) findViewById(R.id.rv_content);
        tv_month = (TextView) findViewById(R.id.tv_month);
        tv_year = (TextView) findViewById(R.id.tv_year);
        ll_left = (LinearLayout) findViewById(R.id.ll_left);
        ll_right = (LinearLayout) findViewById(R.id.ll_right);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        ll_workload_all = (LinearLayout) findViewById(R.id.ll_workload_all);

        ll_workload_all.setOnClickListener(this);
        ll_left.setOnClickListener(this);
        ll_right.setOnClickListener(this);
        ll_back.setOnClickListener(this);

    }

    /**
     * 初始化recyclerView
     */
    private void initRecycler() {

        mGridLayoutManager = new GridLayoutManager(this, 4);
        rv_content.addItemDecoration(new DividerGridItemDecoration(this));
        mWorkloads = new ArrayList<>();
        mWorkloadPersonAdapter = new WorkloadPersonAdapter(mWorkloads, this);
        rv_content.setLayoutManager(mGridLayoutManager);
        rv_content.setAdapter(mWorkloadPersonAdapter);

    }

    /**
     * 加载工作量json数据
     *
     * @param pPhone
     * @param pTime
     */
    private void loadWorkload(String pPhone, final String pTime, String pRoleId, final LinearLayout pLayoutClick, final String pLeft, final String pRight) {
        RequestParams params = new RequestParams(URL.WORKLOAD);
        params.addBodyParameter("action", "workloadLiver");
        params.addBodyParameter("roleId", pRoleId);
        params.addBodyParameter("phone", pPhone);
        if(pLayoutClick!=null) {
            pLayoutClick.setEnabled(false);
        }
        if (pTime != null) {
            params.addBodyParameter("time", pTime);
        }
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                WorkloadJson workloadJson = new Gson().fromJson(result, WorkloadJson.class);
                //ToastUtil.showToast(result,WorkloadPersonActivity.this);
                if (workloadJson != null && workloadJson.getResult() == CONSTS.SEND_OK) {
                    mWorkloads = workloadJson.getObj();//)!=null?workloadJson.getObj():new ArrayList<Workload>()
                    mWorkloadPersonAdapter.refresh(mWorkloads);
                    mTime = workloadJson.getObj().get(0).getTime();
                    if(pTime==null){
                        mLastTime = mTime;
                        if((mCreateTime.split("-")[0]+"-"+mCreateTime.split("-")[1]).equals(mTime)){
                            iv_left.setImageResource(R.drawable.mine_5work_left_no);
                        }
                    }

                    tv_year.setText(mTime.split("-")[0]);
                    tv_month.setText(Integer.parseInt(mTime.split("-")[1])+"");

                    if(pLeft!=null){
                        isShowRight = true;
                        iv_right.setImageResource(R.drawable.cloud_3xinxi_right);
                    }

                    if(pRight!=null){
                        isShowLeft = true;
                        iv_left.setImageResource(R.drawable.mine_5work_left);
                    }
                }
                if(pLayoutClick!=null) {
                    pLayoutClick.setEnabled(true);
                }
                if(pLeft!=null&&isShowLeft){
                    iv_left.setImageResource(R.drawable.mine_5work_left);
                }
                if(pRight!=null&&isShowRight){
                    iv_right.setImageResource(R.drawable.mine_5work_right);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if(pLayoutClick!=null) {
                    pLayoutClick.setEnabled(true);
                }
                if(pLeft!=null&&isShowLeft){
                    iv_left.setImageResource(R.drawable.mine_5work_left);
                }
                if(pRight!=null&&isShowRight){
                    iv_left.setImageResource(R.drawable.mine_5work_right);
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
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
            case R.id.ll_left:

                iv_left.setImageResource(R.drawable.mine_5work_left_no);
                if (!mTime.contains("-") || !mCreateTime.contains("-")) {
                    return;
                }
                mYear = Integer.parseInt(mTime.split("-")[0]);
                mMonth = Integer.parseInt(mTime.split("-")[1]);

                mStartYear = Integer.parseInt(mCreateTime.split("-")[0]);
                mStartMonth = Integer.parseInt(mCreateTime.split("-")[1]);

                //ToastUtil.showToast(mCreateTime+",,,,"+mTime+",,,,"+mStartMonth,this);

                if (mMonth == 1) {
                    mMonth = 12;
                    mYear--;
                } else {
                    mMonth--;
                }

                if (mYear*100 + mMonth < mStartYear*100 + mStartMonth) {

                    return;
                }

                if (mYear*100 + mMonth == mStartYear*100 + mStartMonth) {
                    iv_left.setImageResource(R.drawable.mine_5work_left_no);
                    isShowLeft =  false;
                }
//                else {
//                    iv_left.setImageResource(R.drawable.mine_5work_left);
//                }
                loadWorkload(mPhone, mYear + "-" + (mMonth<10?"0"+mMonth:mMonth), mRoleId + "",ll_left,"left",null);
                break;
            case R.id.ll_right:
                iv_right.setImageResource(R.drawable.mine_5work_right_no);
                if (!mTime.contains("-") || !mLastTime.contains("-")) {
                    return;
                }
                mYear = Integer.parseInt(mTime.split("-")[0]);
                mMonth = Integer.parseInt(mTime.split("-")[1]);

                mLastYear = Integer.parseInt(mLastTime.split("-")[0]);
                mLastMonth = Integer.parseInt(mLastTime.split("-")[1]);

                //ToastUtil.showToast(mLastTime+",,,,"+mTime+",,,,"+mStartMonth,this);

                if (mMonth == 12) {
                    mMonth = 1;
                    mYear++;
                } else {
                    mMonth++;
                }

                if (mYear*100 + mMonth > mLastYear*100 + mLastMonth) {

                    return;
                }

                if (mYear*100 + mMonth == mLastYear*100 + mLastMonth) {
                    iv_right.setImageResource(R.drawable.mine_5work_right_no);
                    isShowRight =  false;
                }
//                else {
//                    iv_left.setImageResource(R.drawable.mine_5work_left);
//                }
                loadWorkload(mPhone, mYear + "-" + (mMonth<10?"0"+mMonth:mMonth), mRoleId + "",ll_right,null,"right");

                break;
            //全体
            case R.id.ll_workload_all:
                mWebLoginPopup = new WebLoginPopup(this);
                mWebLoginPopup.setOnClickChangeListener(this);
                mWebLoginPopup.showPopupWindow(v);

                break;
        }
    }

    @Override
    public void OnClickChange(String pwd) {
        if ("".equals(pwd)) {
            ToastUtil.showToast("请输入密码", this);
        } else {
            String hospitalName = SharePreUtils.getString("hospital", "", this);
            loadWebLogin(pwd, hospitalName);
            //ToastUtil.showToast(hospitalName+","+pwd, this);
        }
    }

    private void loadWebLogin(String pwd, String hospitalName) {
        RequestParams params = new RequestParams(URL.USER);
        params.addBodyParameter("action", "loginWeb");
        params.addBodyParameter("hospital", hospitalName);
        params.addBodyParameter("pwd", pwd);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    Intent intent = new Intent(WorkloadPersonActivity.this, WorkloadContactPersonActivity.class);
                    intent.putExtra("workloadAll", "workloadAll");
                    mWebLoginPopup.dismiss();
                    startActivity(intent);
                } else {
                    ToastUtil.showToast("密码错误", WorkloadPersonActivity.this);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
}
