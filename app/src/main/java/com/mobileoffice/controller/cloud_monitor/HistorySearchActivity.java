package com.mobileoffice.controller.cloud_monitor;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobileoffice.R;
import com.mobileoffice.application.LocalApplication;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.controller.message.contact.ContactPersonInfoActivity;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.ContactPeresonCoditionJson;
import com.mobileoffice.json.ContactSearchJson;
import com.mobileoffice.json.PhotoJson;
import com.mobileoffice.json.TransferHistoryJson;
import com.mobileoffice.json.TransferJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.SpacesItemDecoration;
import com.mobileoffice.utils.ToastUtil;
import com.mobileoffice.view.CalendarPopup;
import com.mobileoffice.view.OrganTypePopup;
import com.mobileoffice.view.com.bigkoo.pickerview.TimePickerView;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by 99213 on 2017/6/21.
 */

public class HistorySearchActivity extends BaseActivity implements View.OnClickListener, TransferHistoryAdapter.OnRecyclerViewItemClickListener, TransferHistoryAdapter.OnRecyclerViewLoadErrorClickListener {
    //返回
    private LinearLayout ll_history_back;


    private LinearLayout ll_history_title;

    private LinearLayout ll_history_contain;
    private LinearLayout ll_history_search;

    /**
     * recycler view
     */
    private TimePickerView pvTime;
    private RecyclerView rv_history;
    private List<TransferJson.ObjBean> mTransfers;
    private LinearLayoutManager mLayoutManager;
    private TransferHistoryAdapter mTransferAdapter;
    private int page = 0;
    private int lastVisibleItem;
    //没有数据
    private RelativeLayout rl_no_content;
    private String TAG = "HistoryActivity";

    //等待按钮
    private ProgressBar pb_history;

    //图表
    private ImageView iv_history_chart;

    //加载状态
    private int mLoadType = -1;
    //器官类型
    private OrganTypePopup mOrganTypePopup;
    private List<String> mOrganTypeDatas;

    private CalendarPopup mCalendarPopup;
    private List<String> mContactPersonDatas;
    private List<String> mAddressDatas;
    private String phone;
    private String startTime = "";
    private String endTime = "";
    private String organ = "";
    private String transferPerson = "";
    private String startAddress = "";
    private EditText edt_contact_person_add;

    @Override
    protected void initVariable() {
        //initTimePicker();肝
        phone = SharePreUtils.getString("phone", "", this);

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.history_search);
        ll_history_back = (LinearLayout) findViewById(R.id.ll_history_back);


        ll_history_title = (LinearLayout) findViewById(R.id.ll_history_title);
        ll_history_contain = (LinearLayout) findViewById(R.id.ll_history_contain);
        rv_history = (RecyclerView) findViewById(R.id.rv_history);
        rl_no_content = (RelativeLayout) findViewById(R.id.rl_no_content);
        pb_history = (ProgressBar) findViewById(R.id.pb_history);
        iv_history_chart = (ImageView) findViewById(R.id.iv_history_chart);
        ll_history_search = (LinearLayout) findViewById(R.id.ll_history_search);
        edt_contact_person_add = (EditText) findViewById(R.id.edt_contact_person_add);

        ll_history_back.setOnClickListener(this);


        rl_no_content.setOnClickListener(this);
        iv_history_chart.setOnClickListener(this);
        ll_history_search.setOnClickListener(this);
        edt_contact_person_add.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String name = edt_contact_person_add.getText().toString().trim();
                if (!"".equals(name)) {
                    page = 0;
                    loadInitData();
                }
            }
        });
        rl_no_content.setVisibility(View.VISIBLE);
        rv_history.setVisibility(View.GONE);
        pb_history.setVisibility(View.GONE);
    }


    private void loadInitData() {

        rv_history.setVisibility(View.GONE);
        rl_no_content.setVisibility(View.GONE);
        pb_history.setVisibility(View.VISIBLE);

        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "getSearchTransferHistory");
        params.addBodyParameter("phone", phone);
        params.addBodyParameter("page", page + "");
        params.addBodyParameter("pageSize", CONSTS.PAGE_SIZE + "");
        params.addBodyParameter("condition", edt_contact_person_add.getText().toString().trim());


        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                TransferHistoryJson transferHistoryJson = new Gson().fromJson(result, TransferHistoryJson.class);
                if (transferHistoryJson != null && transferHistoryJson.getResult() == CONSTS.SEND_OK) {
                    //mTransfers.addAll(transferHistoryJson.getObj());
                    if (transferHistoryJson.getObj() != null && transferHistoryJson.getObj().size() > 0) {
                        mTransfers = transferHistoryJson.getObj();
                        mLoadType = CONSTS.LOAD_CONTINUE;
                        mTransferAdapter.refreshList(mTransfers, mLoadType);
                        rl_no_content.setVisibility(View.GONE);
                        rv_history.setVisibility(View.VISIBLE);
                        pb_history.setVisibility(View.GONE);
                        if (transferHistoryJson.getObj().size() < 16) {

                            page++;
                            loadContinueata();

                        }
                    } else {

                        rl_no_content.setVisibility(View.VISIBLE);
                        rv_history.setVisibility(View.GONE);
                        pb_history.setVisibility(View.GONE);

                    }
                } else {
                    rl_no_content.setVisibility(View.VISIBLE);
                    rv_history.setVisibility(View.GONE);
                    pb_history.setVisibility(View.GONE);
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                rl_no_content.setVisibility(View.VISIBLE);
                rv_history.setVisibility(View.GONE);
                pb_history.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    private void loadContinueata() {
        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "getSearchTransferHistory");
        params.addBodyParameter("phone", phone);
        params.addBodyParameter("page", page + "");
        params.addBodyParameter("pageSize", CONSTS.PAGE_SIZE + "");
        params.addBodyParameter("condition", edt_contact_person_add.getText().toString().trim());

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e(TAG, "result:" + result);
                TransferHistoryJson transferHistoryJson = new Gson().fromJson(result, TransferHistoryJson.class);
                if (transferHistoryJson != null && transferHistoryJson.getResult() == CONSTS.SEND_OK) {
                    //mTransfers.addAll(transferHistoryJson.getObj());
                    if (transferHistoryJson.getObj() != null && transferHistoryJson.getObj().size() > 0) {
                        mTransfers.addAll(transferHistoryJson.getObj());
                        mLoadType = CONSTS.LOAD_CONTINUE;
                        mTransferAdapter.refreshList(mTransfers, mLoadType);

                    } else {

                        mLoadType = CONSTS.LOAD_FINISH;
                        mTransferAdapter.refreshList(mTransfers, mLoadType);

                    }
                } else {
                    mLoadType = CONSTS.LOAD_FINISH;
                    mTransferAdapter.refreshList(mTransfers, mLoadType);
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mLoadType = CONSTS.LOAD_ERROR;
                mTransferAdapter.refreshList(mTransfers, mLoadType);
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

        mTransfers = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(this);
        mTransferAdapter = new TransferHistoryAdapter(mTransfers, this);

        //RecycleView 增加边距
        int spacingInPixels = DensityUtil.dip2px(10);
        rv_history.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        rv_history.setLayoutManager(mLayoutManager);
        rv_history.setAdapter(mTransferAdapter);

        //点击事件
        mTransferAdapter.setOnItemClickListener(this);
        mTransferAdapter.setOnLoadErrorClickListener(this);
        //recyclerview滚动监听
        rv_history.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //0：当前屏幕停止滚动；1时：屏幕在滚动 且 用户仍在触碰或手指还在屏幕上；2时：随用户的操作，屏幕上产生的惯性滑动；
                // 滑动状态停止并且剩余少于两个item时，自动加载下一页
                LogUtil.e(TAG, "GG:" + mLoadType);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 2 >= mLayoutManager.getItemCount() && mLoadType != CONSTS.LOAD_FINISH) {
                    page++;
                    loadContinueata();
                    LogUtil.e(TAG, "GG1:" + mLoadType);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //获取加载的最后一个可见视图在适配器的位置。
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();

                LogUtil.e(TAG, "GG3:" + mLoadType);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_history_back:
                finish();
                break;

            //没有数据,重新请求数据 广安市
            case R.id.rl_no_content:
                page = 0;
                loadInitData();
                break;
            case R.id.iv_history_chart:
                startActivity(new Intent(this, HistoryChartActivity.class));
                break;
            case R.id.ll_history_search:
                String name = edt_contact_person_add.getText().toString().trim();
                if (!"".equals(name)) {
                    page = 0;
                    loadInitData();
                }
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent();
        TransferJson.ObjBean oldObjBean = mTransfers.get(position);
        TransferJson.ObjBean TObjBean = new TransferJson.ObjBean();
        TObjBean.setPhone(oldObjBean.getPhone());
        TObjBean.setBlood(oldObjBean.getBlood());
        TObjBean.setBloodNum(oldObjBean.getBloodNum());
        TObjBean.setContactName(oldObjBean.getContactName());
        TObjBean.setContactPhone(oldObjBean.getContactPhone());
        TObjBean.setDistance(oldObjBean.getDistance());
        TObjBean.setEndLati(oldObjBean.getEndLati());
        TObjBean.setEndLong(oldObjBean.getEndLong());
        TObjBean.setFromCity(oldObjBean.getFromCity());
        TObjBean.setGetTime(oldObjBean.getGetTime());
        TObjBean.setIsStart(oldObjBean.getIsStart());
        TObjBean.setOpenPsd(oldObjBean.getOpenPsd());
        TObjBean.setOpoName(oldObjBean.getOpoName());
        TObjBean.setOrgan(oldObjBean.getOrgan());
        TObjBean.setOrganNum(oldObjBean.getOrganNum());
        TObjBean.setOrganSeg(oldObjBean.getOrganSeg());
        TObjBean.setSampleOrgan(oldObjBean.getSampleOrgan());
        TObjBean.setStartLati(oldObjBean.getStartLati());
        TObjBean.setStartLong(oldObjBean.getStartLong());
        TObjBean.setToHosp(oldObjBean.getToHosp());
        TObjBean.setTracfficType(oldObjBean.getTracfficType());
        TObjBean.setTracfficNumber(oldObjBean.getTracfficNumber());
        TObjBean.setSampleOrganNum(oldObjBean.getSampleOrganNum());
        TObjBean.setToHospName(oldObjBean.getToHospName());
        TObjBean.setTrueName(oldObjBean.getTrueName());
        TObjBean.setTransferid(oldObjBean.getTransferid());
        TObjBean.setBoxNo(oldObjBean.getBoxNo());
        TObjBean.setNowDistance(oldObjBean.getNowDistance());
        TObjBean.setTime(oldObjBean.getTime());
        intent.putExtra("transfer", TObjBean);
        intent.setClass(this, TransferDetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view) {

    }

    @Override
    public void onLoadErrorClick(View view) {
        ProgressBar pb_history_footer = (ProgressBar) view.findViewById(R.id.pb_history_footer);
        TextView tv_history_footer = (TextView) view.findViewById(R.id.tv_history_footer);
        LinearLayout ll_history_footer = (LinearLayout) view.findViewById(R.id.ll_history_footer);
        ll_history_footer.setEnabled(false);
        pb_history_footer.setVisibility(View.VISIBLE);
        tv_history_footer.setText("加载更多...");
        loadContinueata();
    }
}
