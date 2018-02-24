package com.mobileoffice.controller.message.contact;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.mobileoffice.MainActivity;
import com.mobileoffice.R;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.PhotoJson;
import com.mobileoffice.json.PushListJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.SpacesItemDecoration;
import com.mobileoffice.utils.ToastUtil;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by 99213 on 2017/5/23.
 */

public class SystemMessageActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = "SystemMessageActivity";
    private Context mContext;

    private List<PushListJson.ObjBean> mPushes = new ArrayList<>();
    private RecyclerView rv_system_message;
    private LinearLayoutManager mLayoutManager;
    private SystemMessageAdapter mSystemMessageAdapter;
    private int page = 0;
    private int lastVisibleItem;
    private String phone = "";
    private LinearLayout ll_system_message_no;
    private LinearLayout ll_system_message_back;

    private LinearLayout ll_system_message_progress;
    private View mHeaderView;

    //第一次进来
    private boolean isFirst = true;
    private AlertDialog.Builder mAlertDialog;

    //上一次滚动的位置
    private int lastPosition;

    /**
     * 更新未读数
     */
    private void updateSystemPosition() {
        RequestParams params = new RequestParams(URL.USER);
        params.addBodyParameter("action", "updateSystemPosition");
        params.addBodyParameter("phone", phone);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                PushListJson pushListJson = gson.fromJson(result, PushListJson.class);
                if (pushListJson != null && pushListJson.getResult() == CONSTS.SEND_OK) {
                    CONSTS.UNREAD_PUSH_NUM = 0;
                    SharePreUtils.putInt("unread_push_num", CONSTS.UNREAD_PUSH_NUM, SystemMessageActivity.this);
                    Intent i = new Intent("com.mobile.office.system.message");
                    sendBroadcast(i);
                }

                LogUtil.e(TAG, "result" + result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(TAG, ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void loadPUshData() {
        RequestParams params = new RequestParams(URL.USER);
        params.addBodyParameter("action", "getPushList");
        params.addBodyParameter("phone", phone);
        params.addBodyParameter("page", page + "");
        params.addBodyParameter("pageSize", CONSTS.PAGE_SIZE + "");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                Gson gson = new Gson();
                PushListJson pushListJson = gson.fromJson(result, PushListJson.class);
                if (pushListJson != null && pushListJson.getResult() == CONSTS.SEND_OK) {
                    Collections.reverse(pushListJson.getObj());
                    for (int i = 0; i < pushListJson.getObj().size(); i++) {
                        LogUtil.e(TAG, "size:" + pushListJson.getObj().get(i).getContent() + ":" + pushListJson.getObj().get(i).getPhone());
                    }
                    mPushes.addAll(0, pushListJson.getObj() == null ? new ArrayList<PushListJson.ObjBean>() : pushListJson.getObj());

                    mSystemMessageAdapter.refreshList(mPushes, false);
                    ll_system_message_no.setVisibility(View.GONE);
                    rv_system_message.setVisibility(View.VISIBLE);
                    ll_system_message_progress.setVisibility(View.GONE);
                    rv_system_message.smoothScrollToPosition(pushListJson.getObj().size());
                    rv_system_message.scrollToPosition(pushListJson.getObj().size());
                    if (page == 0 && isFirst) {
                        isFirst = false;
                        rv_system_message.scrollToPosition(pushListJson.getObj().size() - 1);
                    }
                } else {
                    page--;
                    updateSystemPosition();
                    if (mPushes.size() == 0) {
                        ll_system_message_no.setVisibility(View.VISIBLE);
                        rv_system_message.setVisibility(View.GONE);
                        ll_system_message_progress.setVisibility(View.GONE);
                    } else {

                        mSystemMessageAdapter.refreshList(mPushes, false);
                    }
                }

                // ToastUtil.showToast("finish",getApplicationContext());
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ll_system_message_progress.setVisibility(View.GONE);
                mSystemMessageAdapter.refreshList(null, false);
                //  rv_system_message.refreshComplete();
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
    protected void initVariable() {
        phone = SharePreUtils.getString("phone", "", this);
        if (CONSTS.UNREAD_PUSH_NUM != 0) {
            clearUnreadPushMessageNum(phone);
        }

        ll_system_message_no.setVisibility(View.VISIBLE);
        rv_system_message.setVisibility(View.GONE);
        mLayoutManager = new LinearLayoutManager(this);
        //RecycleView 增加边距
        int spacingInPixels = DensityUtil.dip2px(10);
        rv_system_message.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        rv_system_message.setLayoutManager(mLayoutManager);
        //rv_system_message.setPullRefreshEnabled(false);
        mHeaderView = LayoutInflater.from(this).inflate(R.layout.system_history, (ViewGroup) findViewById(android.R.id.content), false);
        //rv_system_message.addHeaderView(mHeaderView);
        mPushes = new ArrayList<>();
        mSystemMessageAdapter = new SystemMessageAdapter(mPushes, this);
        rv_system_message.setAdapter(mSystemMessageAdapter);

        mSystemMessageAdapter.setOnItemClickListener(new SystemMessageAdapter.OnRecyclerViewItemClickListener() {

            @Override
            public void onItemClick(android.view.View view, int position) {

            }

            @Override
            public void onAgreeItemClick(View view, int position) {
                mPushes.get(position).setType("agree");
            }

            @Override
            public void onRefuseItemClick(View view, int position) {
                mPushes.get(position).setType("refuse");
            }

            @Override
            public void onItemLongClick(View view, final int position) {
                mAlertDialog = new AlertDialog.Builder(mContext);
                //mAlertDialog.setTitle("tis");

                mAlertDialog.setMessage("是否删除系统消息");


                mAlertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                mAlertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        deleteSystem(position - 1);

                    }
                });
                mAlertDialog.show();
            }
        });
//        rv_system_message.setLoadingListener(new XRecyclerView.LoadingListener() {
//            @Override
//            public void onRefresh() {
//                page++;
//                loadPUshData();
//            }
//
//            @Override
//            public void onLoadMore() {
//
//            }
//        });

        //recyclerview滚动监听
        rv_system_message.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //0：当前屏幕停止滚动；1时：屏幕在滚动 且 用户仍在触碰或手指还在屏幕上；2时：随用户的操作，屏幕上产生的惯性滑动；
                // 滑动状态停止并且剩余少于两个item时，自动加载下一页
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem == 0) {
                    mSystemMessageAdapter.refreshList(null, true);
                    page++;
                    loadPUshData();
                }
                LogUtil.e(TAG, "newState:" + newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //获取加载的最后一个可见视图在适配器的位置。
                //lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
                lastVisibleItem = mLayoutManager.findFirstCompletelyVisibleItemPosition();
                LogUtil.e(TAG, "position:" + lastVisibleItem);
            }
        });
        ll_system_message_progress.setVisibility(View.VISIBLE);
        loadPUshData();

    }

    private void deleteSystem(final int position) {
        showWaitDialog(getResources().getString(R.string.loading), true, "loading");
        final RequestParams params = new RequestParams(URL.USER);
        params.addBodyParameter("action", "deleteSystemNews");
        params.addBodyParameter("type", mPushes.get(position).getType());
        params.addBodyParameter("content", mPushes.get(position).getContent());
        params.addBodyParameter("pushId", mPushes.get(position).getId() + "");
        params.addBodyParameter("phone", SharePreUtils.getString("phone", "", mContext));
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    mPushes.remove(position);
                    mSystemMessageAdapter.refreshList(mPushes, false);

                    ToastUtil.showToast("删除成功", SystemMessageActivity.this);
                }
                dismissDialog();

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                dismissDialog();
                ToastUtil.showToast("b" + ex.getMessage(), SystemMessageActivity.this);
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
    protected synchronized void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.system_message);
        mContext = this;
        ll_system_message_no = (LinearLayout) findViewById(R.id.ll_system_message_no);

        rv_system_message = (RecyclerView) findViewById(R.id.rv_system_message);
        ll_system_message_back = (LinearLayout) findViewById(R.id.ll_system_message_back);
        ll_system_message_progress = (LinearLayout) findViewById(R.id.ll_system_message_progress);
        ll_system_message_back.setOnClickListener(this);

    }

    @Override
    protected void initData() {

    }

    /**
     * 清楚系统消息未读数
     */
    private void clearUnreadPushMessageNum(String user_info_id) {
        RequestParams params = new RequestParams(URL.PUSH);
        params.addBodyParameter("action", "clearUnreadPushMessageNum");
        params.addBodyParameter("user_info_id", user_info_id);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                CONSTS.UNREAD_PUSH_NUM = 0;
                SharePreUtils.putInt("unread_push_num", 0, mContext);
                sendBroadcast(new Intent("com.mobile.office.system.message"));
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_system_message_back:
                finish();
                String systemType = getIntent().getStringExtra("systemType");
                if ("system".equals(systemType)) {
                    Intent i = new Intent(this, MainActivity.class);
                    i.putExtra("systemType", "system");
                    startActivity(i);
                }

                break;
        }
    }
}
