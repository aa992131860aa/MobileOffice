package com.mobileoffice.controller.cloud_monitor;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobileoffice.R;
import com.mobileoffice.application.LocalApplication;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.entity.Transfer;
import com.mobileoffice.http.HttpHelper;
import com.mobileoffice.http.HttpObserver;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.ContactPeresonCoditionJson;
import com.mobileoffice.json.TransferHistoryJson;
import com.mobileoffice.json.TransferJson;
import com.mobileoffice.json.TransferingJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.SpacesItemDecoration;
import com.mobileoffice.utils.ToastUtil;
import com.mobileoffice.view.CalendarPopup;
import com.mobileoffice.view.OrganTypePopup;
import com.mobileoffice.view.Popwindow;
import com.mobileoffice.view.com.bigkoo.pickerview.TimePickerView;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by 99213 on 2017/6/7.
 */

public class HistoryActivity extends BaseActivity implements View.OnClickListener, TransferHistoryAdapter.OnRecyclerViewItemClickListener, TransferHistoryAdapter.OnRecyclerViewLoadErrorClickListener {
    //返回
    private LinearLayout ll_history_back;
    //筛选条件时间
    private RelativeLayout rl_history_date;
    private TextView tv_history_date;
    //筛选条件器官
    private RelativeLayout rl_history_organ;
    private TextView tv_history_organ;
    //筛选条件转运人
    private RelativeLayout rl_history_transfer_person;
    private TextView tv_history_transfer_person;
    //筛选条件起始地
    private RelativeLayout rl_history_start_local;
    private TextView tv_history_start_local;

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
    private List<String> mIntegritys;

    private CalendarPopup mCalendarPopup;
    private List<String> mContactPersonDatas;
    private List<String> mAddressDatas;
    private String phone;
    private String startTime = "";
    private String endTime = "";
    private String organ = "";
    private String transferPerson = "";
    private String startAddress = "";

    //完整度
    private String integrity;

    private RelativeLayout rl_integrity;
    private TextView tv_integrity;


    @Override
    protected void initVariable() {
        //initTimePicker();
        phone = SharePreUtils.getString("phone", "", this);
        mContactPersonDatas = new ArrayList<>();
        mOrganTypeDatas = new ArrayList<>();
        mIntegritys = new ArrayList<>();
        mIntegritys.add("全部");
        mIntegritys.add("待完善");
        mIntegritys.add("已完善");
        mAddressDatas = new ArrayList<>();
        mOrganTypeDatas.add("全部");
        mOrganTypeDatas.add("心脏");
        mOrganTypeDatas.add("肝脏");
        mOrganTypeDatas.add("肺");
        mOrganTypeDatas.add("肾脏");
        mOrganTypeDatas.add("胰脏");
        mOrganTypeDatas.add("眼角膜");

        loadContactPerson();
        loadContactAddress();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.history);
        ll_history_back = (LinearLayout) findViewById(R.id.ll_history_back);

        rl_history_date = (RelativeLayout) findViewById(R.id.rl_history_date);
        rl_history_organ = (RelativeLayout) findViewById(R.id.rl_history_organ);
        rl_history_transfer_person = (RelativeLayout) findViewById(R.id.rl_history_transfer_person);
        rl_history_start_local = (RelativeLayout) findViewById(R.id.rl_history_start_local);
        tv_history_date = (TextView) findViewById(R.id.tv_history_date);
        tv_history_organ = (TextView) findViewById(R.id.tv_history_organ);
        tv_history_transfer_person = (TextView) findViewById(R.id.tv_history_transfer_person);
        tv_history_start_local = (TextView) findViewById(R.id.tv_history_start_local);
        ll_history_title = (LinearLayout) findViewById(R.id.ll_history_title);
        ll_history_contain = (LinearLayout) findViewById(R.id.ll_history_contain);
        rv_history = (RecyclerView) findViewById(R.id.rv_history);
        rl_no_content = (RelativeLayout) findViewById(R.id.rl_no_content);
        pb_history = (ProgressBar) findViewById(R.id.pb_history);
        iv_history_chart = (ImageView) findViewById(R.id.iv_history_chart);
        ll_history_search = (LinearLayout) findViewById(R.id.ll_history_search);

        rl_integrity = (RelativeLayout) findViewById(R.id.rl_integrity);
        tv_integrity = (TextView) findViewById(R.id.tv_integrity);


        ll_history_back.setOnClickListener(this);

        rl_history_date.setOnClickListener(this);
        rl_history_organ.setOnClickListener(this);
        rl_history_transfer_person.setOnClickListener(this);
        rl_history_start_local.setOnClickListener(this);
        rl_no_content.setOnClickListener(this);
        iv_history_chart.setOnClickListener(this);
        ll_history_search.setOnClickListener(this);
        rl_integrity.setOnClickListener(this);
    }

    private void initTimePicker() {
        //控制时间范围(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
        //因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(2013, 0, 23);
        Calendar endDate = Calendar.getInstance();

        //endDate.set(2019, 11, 28);
        //时间选择器
        pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                // 这里回调过来的v,就是show()方法里面所添加的 View 参数，如果show的时候没有添加参数，v则为null

                /*btn_Time.setText(getTime(date));*/
                Button btn = (Button) v;
                //btn.setText(getTime(date));
            }
        })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("", "", "", "点", "分", "")
                .isCenterLabel(false)
                .setDividerColor(Color.DKGRAY)
                .setContentSize(18)
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setBackgroundId(0x00FFFFFF) //设置外部遮罩颜色
                .setDecorView(null)
                .build();
    }

    private void loadInitData() {

        rv_history.setVisibility(View.GONE);
        rl_no_content.setVisibility(View.GONE);
        pb_history.setVisibility(View.VISIBLE);

        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "getTransferHistory");
        params.addBodyParameter("phone", phone);
        params.addBodyParameter("page", page + "");
        params.addBodyParameter("pageSize", CONSTS.PAGE_SIZE + "");
        params.addBodyParameter("startTime", startTime);
        params.addBodyParameter("endTime", endTime);
        params.addBodyParameter("organ", organ);
        params.addBodyParameter("transferPerson", transferPerson);
        params.addBodyParameter("startAddress", startAddress);
        params.addBodyParameter("integrity",integrity);


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
                        if(transferHistoryJson.getObj().size()<16){

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
        params.addBodyParameter("action", "getTransferHistory");
        params.addBodyParameter("phone", phone);
        params.addBodyParameter("page", page + "");
        params.addBodyParameter("pageSize", CONSTS.PAGE_SIZE + "");
        params.addBodyParameter("startTime", startTime);
        params.addBodyParameter("endTime", endTime);
        params.addBodyParameter("organ", organ);
        params.addBodyParameter("transferPerson", transferPerson);
        params.addBodyParameter("startAddress", startAddress);
        params.addBodyParameter("integrity",integrity);

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

    private void loadContactPerson() {
        RequestParams params = new RequestParams(URL.USER);
        params.addBodyParameter("action", "getTransferPerson");
        params.addBodyParameter("phone", phone);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ContactPeresonCoditionJson contactPeresonCoditionJson = new Gson().fromJson(result, ContactPeresonCoditionJson.class);
                if (contactPeresonCoditionJson != null && contactPeresonCoditionJson.getResult() == CONSTS.SEND_OK) {
                    mContactPersonDatas = contactPeresonCoditionJson.getObj();
                    mContactPersonDatas.add(0,"全部");
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

    private void loadContactAddress() {
        RequestParams params = new RequestParams(URL.USER);
        params.addBodyParameter("action", "getStartAddress");
        params.addBodyParameter("phone", phone);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ContactPeresonCoditionJson contactPeresonCoditionJson = new Gson().fromJson(result, ContactPeresonCoditionJson.class);
                if (contactPeresonCoditionJson != null && contactPeresonCoditionJson.getResult() == CONSTS.SEND_OK) {
                    mAddressDatas = contactPeresonCoditionJson.getObj();
                    mAddressDatas.add(0,"全部");
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
        page = 0;
        loadInitData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_history_back:
                finish();
                break;
            case R.id.rl_history_date:
                //new_monitor Popwindow(this).showbagPopWindow(v,pvTime);
                // pvTime.setDate(Calendar.getInstance());
                /* pvTime.show(); //show timePicker*/
                //pvTime.show(v, ll_history_contain);//弹出时间选择器，传递参数过去，回调的时候则可以绑定此view
                mCalendarPopup = new CalendarPopup(this);
                mCalendarPopup.showPopupWindow(ll_history_title);
                mCalendarPopup.setOnClickChangeListener(new CalendarPopup.OnClickChangeListener() {
                    @Override
                    public void OnClickChange(String startDate, String endDate) {
                        startTime = startDate;
                        endTime = endDate;
                        if (!"".equals(startTime) && !"".equals(endTime)) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                if (sdf.parse(endTime).getTime() - sdf.parse(startTime).getTime() >= 0) {
                                    page = 0;
                                    mLoadType = CONSTS.LOAD_CONTINUE;
                                    loadInitData();
                                    mCalendarPopup.dismiss();
                                    tv_history_date.setText(startTime.split("-")[1] + "." + startTime.split("-")[2] + "-" + endTime.split("-")[1] + "." + endTime.split("-")[2]);
                                    tv_history_date.setTextColor(getResources().getColor(R.color.main));
                                } else {
                                    ToastUtil.showToast("结束日期必须大于等于开始日期", LocalApplication.getInstance());
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            if ("".equals(startTime) && "".equals(endTime)) {
                                tv_history_date.setText("时间");
                                tv_history_date.setTextColor(getResources().getColor(R.color.font_black));
                            }else if(!"".equals(startTime)){
                                tv_history_date.setText(startTime.split("-")[1] + "." + startTime.split("-")[2] + "-至今");
                            }else if(!"".equals(endTime)){
                                tv_history_date.setText("过去-"+endTime.split("-")[1] + "." + endTime.split("-")[2] );
                            }
                            page = 0;
                            mLoadType = CONSTS.LOAD_CONTINUE;
                            loadInitData();
                            mCalendarPopup.dismiss();
                        }

                    }
                });
                break;
            case R.id.rl_history_organ:
                //ToastUtil.showToast("gg",this);
                mOrganTypePopup = new OrganTypePopup(this, mOrganTypeDatas);
                mOrganTypePopup.showPopupWindow(ll_history_title);
                mOrganTypePopup.setOnClickChangeListener(new OrganTypePopup.OnClickChangeListener() {
                    @Override
                    public void OnClickChange(int position) {
                        organ = mOrganTypeDatas.get(position);
                        tv_history_organ.setText(organ);
                        tv_history_organ.setTextColor(getResources().getColor(R.color.main));
                        if ("全部".equals(organ)) {
                            organ = "";
                            tv_history_organ.setText("器官");
                            tv_history_organ.setTextColor(getResources().getColor(R.color.font_black));
                        }
                        page = 0;
                        mLoadType = CONSTS.LOAD_CONTINUE;
                        loadInitData();
                        mOrganTypePopup.dismiss();
                    }
                });
                break;
            case R.id.rl_integrity:
                //ToastUtil.showToast("gg",this);

                mOrganTypePopup = new OrganTypePopup(this, mIntegritys);
                mOrganTypePopup.showPopupWindow(ll_history_title);
                mOrganTypePopup.setOnClickChangeListener(new OrganTypePopup.OnClickChangeListener() {
                    @Override
                    public void OnClickChange(int position) {
                        integrity = mIntegritys.get(position);
                        tv_integrity.setText(integrity);
                        tv_integrity.setTextColor(getResources().getColor(R.color.main));
                        if ("全部".equals(integrity)) {
                            integrity = "";
                            tv_integrity.setText("完整度");
                            tv_integrity.setTextColor(getResources().getColor(R.color.font_black));
                        }
                        page = 0;
                        mLoadType = CONSTS.LOAD_CONTINUE;
                        loadInitData();
                        mOrganTypePopup.dismiss();
                    }
                });
                break;
            case R.id.rl_history_transfer_person:
                mOrganTypePopup = new OrganTypePopup(this, mContactPersonDatas);
                mOrganTypePopup.showPopupWindow(ll_history_title);
                mOrganTypePopup.setOnClickChangeListener(new OrganTypePopup.OnClickChangeListener() {
                    @Override
                    public void OnClickChange(int position) {
                        transferPerson = mContactPersonDatas.get(position);
                        tv_history_transfer_person.setText(transferPerson);
                        tv_history_transfer_person.setTextColor(getResources().getColor(R.color.main));
                        if ("全部".equals(transferPerson)) {
                            transferPerson = "";
                            tv_history_transfer_person.setText("转运人");
                            tv_history_transfer_person.setTextColor(getResources().getColor(R.color.font_black));
                        }
                        page = 0;
                        mLoadType = CONSTS.LOAD_CONTINUE;
                        loadInitData();
                        mOrganTypePopup.dismiss();
                    }
                });
                break;
            case R.id.rl_history_start_local:
                mOrganTypePopup = new OrganTypePopup(this, mAddressDatas);
                mOrganTypePopup.showPopupWindow(ll_history_title);
                mOrganTypePopup.setOnClickChangeListener(new OrganTypePopup.OnClickChangeListener() {
                    @Override
                    public void OnClickChange(int position) {
                        startAddress = mAddressDatas.get(position);
                        tv_history_start_local.setText(startAddress);
                        tv_history_start_local.setTextColor(getResources().getColor(R.color.main));
                        if ("全部".equals(startAddress)) {
                            startAddress = "";
                            tv_history_start_local.setText("起始地");
                            tv_history_start_local.setTextColor(getResources().getColor(R.color.font_black));
                        }
                        page = 0;
                        mLoadType = CONSTS.LOAD_CONTINUE;
                        mOrganTypePopup.dismiss();
                        loadInitData();
                    }
                });
                break;
            //没有数据,重新请求数据
            case R.id.rl_no_content:
                page = 0;
                loadInitData();
                break;
            case R.id.iv_history_chart:

                Intent intent = new Intent(this,HistoryChartActivity.class);
                intent.putExtra("action", "getTransferHistory");
                intent.putExtra("phone", phone);
                intent.putExtra("startTime", startTime);
                intent.putExtra("endTime", endTime);
                intent.putExtra("organ", organ);
                intent.putExtra("transferPerson", transferPerson);
                intent.putExtra("startAddress", startAddress);
                intent.putExtra("integrity",integrity);
                startActivity(intent);

                break;
            case R.id.ll_history_search:
                startActivity(new Intent(this,HistorySearchActivity.class));
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent();

        intent.putExtra("transfer", mTransfers.get(position));
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
