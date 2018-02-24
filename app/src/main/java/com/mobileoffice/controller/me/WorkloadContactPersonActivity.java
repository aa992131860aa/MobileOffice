package com.mobileoffice.controller.me;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobileoffice.R;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.PhotoJson;
import com.mobileoffice.json.WorkloadAllJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;
import com.mobileoffice.view.WebLoginPopup;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 99213 on 2017/12/6.
 */

public class WorkloadContactPersonActivity extends BaseActivity implements View.OnClickListener, WebLoginPopup.OnClickChangeListener {

    private LinearLayout ll_back;
    private TextView tv_left;
    private TextView tv_right;
    private TextView tv_time;
    private LinearLayout ll_no_data;
    private RecyclerView rv_content;
    private List<WorkloadAllJson.ObjBean> mWorkloadAlls;

    private LinearLayoutManager mLinearLayoutManager;
    private WorkloadContactPersonAdapter mWorkloadContactPersonAdapter;
    private String mPhone;

    private String mTime;
    private String mCreateTime;
    private String mLastTime;
    private int mYear;
    private int mMonth;
    private int mCreateYear;
    private int mCreateMonth;
    private int mLastYear;
    private int mLastMonth;
    private boolean isShowLeft = true;
    private boolean isShowRight = true;

    //全体工作量统计
    private LinearLayout ll_workload_all;
    private TextView tv_title;
    private String workloadAll;

    //密码输入弹出框
    private WebLoginPopup mWebLoginPopup;

    @Override
    protected void initVariable() {
        mPhone = SharePreUtils.getString("phone", "", this);
        mCreateTime = SharePreUtils.getString("create_time", "", this);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        mLastTime = sdf.format(new Date());
        mTime = sdf.format(new Date());

        initRecycler();
        workloadAll = getIntent().getStringExtra("workloadAll");
        if (workloadAll == null) {

            loadWorkloadAll(mPhone, null, null, null, workloadAll);
            ll_workload_all.setVisibility(View.VISIBLE);

        } else {
            loadWorkloadAll(mPhone, null, null, null, workloadAll);
            ll_workload_all.setVisibility(View.GONE);
            tv_title.setText("全体工作量统计");
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.workload_contact_person);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        tv_left = (TextView) findViewById(R.id.tv_left);
        tv_right = (TextView) findViewById(R.id.tv_right);
        tv_time = (TextView) findViewById(R.id.tv_time);
        ll_no_data = (LinearLayout) findViewById(R.id.ll_no_data);
        rv_content = (RecyclerView) findViewById(R.id.rv_content);
        ll_workload_all = (LinearLayout) findViewById(R.id.ll_workload_all);
        tv_title = (TextView) findViewById(R.id.tv_title);

        ll_back.setOnClickListener(this);
        tv_left.setOnClickListener(this);
        tv_right.setOnClickListener(this);
        ll_workload_all.setOnClickListener(this);
    }

    /**
     * 初始化recyclerView
     */
    private void initRecycler() {

        mLinearLayoutManager = new LinearLayoutManager(this);
        //rv_content.addItemDecoration(new DividerGridItemDecoration(this));
        mWorkloadAlls = new ArrayList<>();
        mWorkloadContactPersonAdapter = new WorkloadContactPersonAdapter(mWorkloadAlls, this);
        rv_content.setLayoutManager(mLinearLayoutManager);
        rv_content.setAdapter(mWorkloadContactPersonAdapter);

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
            case R.id.tv_left:

                tv_left.setTextColor(getResources().getColor(R.color.font_black_c));
                if (mTime == null || !mTime.contains("-") || !mCreateTime.contains("1")) {
                    return;
                }
                mYear = Integer.parseInt(mTime.split("-")[0]);
                mMonth = Integer.parseInt(mTime.split("-")[1]);

                mCreateYear = Integer.parseInt(mCreateTime.split("-")[0]);
                mCreateMonth = Integer.parseInt(mCreateTime.split("-")[1]);

                //ToastUtil.showToast(mCreateTime+",,,,"+mTime+",,,,"+mStartMonth,this);

                if (mMonth == 1) {
                    mMonth = 12;
                    mYear--;
                } else {
                    mMonth--;
                }

                if (mYear * 100 + mMonth < mCreateYear * 100 + mCreateMonth) {

                    return;
                }

                if (mYear * 100 + mMonth == mCreateYear * 100 + mCreateMonth) {
                    tv_left.setTextColor(getResources().getColor(R.color.font_black_c));
                    isShowLeft = false;
                }
                loadWorkloadAll(mPhone, mYear + "-" + (mMonth < 10 ? "0" + mMonth : mMonth), "left", null, workloadAll);
                break;
            case R.id.tv_right:
                tv_right.setTextColor(getResources().getColor(R.color.font_black_c));
                if (mTime == null || !mTime.contains("-") || !mLastTime.contains("-")) {
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

                if (mYear * 100 + mMonth > mLastYear * 100 + mLastMonth) {

                    return;
                }

                if (mYear * 100 + mMonth == mLastYear * 100 + mLastMonth) {
                    tv_right.setTextColor(getResources().getColor(R.color.font_black_c));
                    isShowRight = false;
                }
//                else {
//                    iv_left.setImageResource(R.drawable.mine_5work_left);
//                }
                loadWorkloadAll(mPhone, mYear + "-" + (mMonth < 10 ? "0" + mMonth : mMonth), null, "right", workloadAll);

                break;
            //全体
            case R.id.ll_workload_all:
                mWebLoginPopup = new WebLoginPopup(this);
                mWebLoginPopup.setOnClickChangeListener(this);
                mWebLoginPopup.showPopupWindow(v);

                break;
        }
    }

    /**
     * 加载工作量json数据
     *
     * @param pPhone
     * @param pTime
     */
    private void loadWorkloadAll(String pPhone, final String pTime, final String pLeft, final String pRight, final String workloadAll) {
        RequestParams params = new RequestParams(URL.WORKLOAD);
        if (workloadAll == null) {
            params.addBodyParameter("action", "workloadAllLiver");
            params.addBodyParameter("contactPhone", pPhone);
        } else {
            String hospitalName = SharePreUtils.getString("hospital", "", WorkloadContactPersonActivity.this);
            params.addBodyParameter("action", "workloadHospitalLiver");
            params.addBodyParameter("hospital", hospitalName);
        }
        if (pTime != null) {
            params.addBodyParameter("time", pTime);
        }
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                WorkloadAllJson workloadAllJson = new Gson().fromJson(result, WorkloadAllJson.class);
                //ToastUtil.showToast(result+","+pTime, WorkloadContactPersonActivity.this);
                if (workloadAllJson != null && workloadAllJson.getResult() == CONSTS.SEND_OK) {
                    mWorkloadAlls = workloadAllJson.getObj();
                    mWorkloadContactPersonAdapter.refresh(mWorkloadAlls);

                    mTime = mWorkloadAlls.get(0).getTime();
                    tv_time.setText(mTime.split("-")[0] + "年" + mTime.split("-")[1] + "月");

                    if (pTime == null) {
                        mLastTime = mTime;
                        if ((mCreateTime.split("-")[0] + "-" + mCreateTime.split("-")[1]).equals(mTime)) {
                            tv_left.setTextColor(getResources().getColor(R.color.font_black_c));
                        }
                    }

                    ll_no_data.setVisibility(View.GONE);
                    rv_content.setVisibility(View.VISIBLE);
                    if (workloadAllJson.getObj().size() == 0) {

                    }
                } else {

                    if (pTime != null) {
                        mTime = pTime;
                    }
                    tv_time.setText(mTime.split("-")[0] + "年" + mTime.split("-")[1] + "月");
                    ll_no_data.setVisibility(View.VISIBLE);
                    rv_content.setVisibility(View.GONE);
                }


                if (pLeft != null && isShowLeft) {
                    tv_left.setTextColor(getResources().getColor(R.color.main));
                }
                if (pRight != null && isShowRight) {
                    tv_right.setTextColor(getResources().getColor(R.color.main));
                }

                if (pLeft != null) {
                    isShowRight = true;
                    tv_right.setTextColor(getResources().getColor(R.color.main));
                }

                if (pRight != null) {
                    isShowLeft = true;
                    tv_left.setTextColor(getResources().getColor(R.color.main));
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //ToastUtil.showToast(ex.getMessage(), WorkloadContactPersonActivity.this);
                if (pLeft != null && isShowLeft) {
                    tv_left.setTextColor(getResources().getColor(R.color.main));
                }
                if (pRight != null && isShowRight) {
                    tv_right.setTextColor(getResources().getColor(R.color.main));
                }
                mTime = pTime;
                ll_no_data.setVisibility(View.VISIBLE);
                rv_content.setVisibility(View.GONE);
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
                    Intent intent = new Intent(WorkloadContactPersonActivity.this, WorkloadContactPersonActivity.class);
                    intent.putExtra("workloadAll", "workloadAll");
                    mWebLoginPopup.dismiss();
                    startActivity(intent);
                } else {
                    ToastUtil.showToast("密码错误", WorkloadContactPersonActivity.this);
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
