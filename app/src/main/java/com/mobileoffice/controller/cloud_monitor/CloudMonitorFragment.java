package com.mobileoffice.controller.cloud_monitor;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mobileoffice.R;
import com.mobileoffice.controller.new_monitor.NewMonitorActivity;
import com.mobileoffice.controller.new_monitor.NewMonitorBaseFragment;
import com.mobileoffice.entity.MessageEvent;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.TransferJson;
import com.mobileoffice.json.TransferTrueJson;
import com.mobileoffice.json.TransferingJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.SpacesItemDecoration;
import com.mobileoffice.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 99213 on 2017/5/19.
 */

public class CloudMonitorFragment extends NewMonitorBaseFragment implements View.OnClickListener, TransferAdapter.OnRecyclerViewItemClickListener {
    private View root;
    private XRecyclerView rv_cloud_monitor;
    private List<TransferJson.ObjBean> mTransfers;
    private LinearLayoutManager mLayoutManager;
    private TransferAdapter mTransferAdapter;
    private int page = 0;
    private int lastVisibleItem;
    //没有数据
    private RelativeLayout rl_no_content;
    //历史
    private TextView tv_cloud_monitor_history;
    private String TAG = "CloudMonitorFragment";
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private String callPhone = "";
    private AlertDialog.Builder mAlertDialog;
    private List<Fragment> mFragment = new ArrayList<>();

    private ProgressBar pb_wait;

    //标题
    private RelativeLayout rl_title;
    private String mPhone;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.cloud_monitor, null);
        mPhone = SharePreUtils.getString("phone", "", getActivity());
        rv_cloud_monitor = (XRecyclerView) root.findViewById(R.id.rv_cloud_monitor);
        rl_no_content = (RelativeLayout) root.findViewById(R.id.rl_no_content);
        tv_cloud_monitor_history = (TextView) root.findViewById(R.id.tv_cloud_monitor_history);
        rl_title = (RelativeLayout) root.findViewById(R.id.rl_title);
        pb_wait = (ProgressBar) root.findViewById(R.id.pb_wait);
        rl_no_content.setOnClickListener(this);
        tv_cloud_monitor_history.setOnClickListener(this);
        rl_title.setOnClickListener(this);
        EventBus.getDefault().register(this);
        initDataBefore();
        pb_wait.setVisibility(View.VISIBLE);
        page = 0;
        loadData();

        rv_cloud_monitor.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                //refresh data here
                page = 0;
                mTransfers = new ArrayList<>();
                loadData();

            }

            @Override
            public void onLoadMore() {
                // load more data here
                page++;
                loadData();
            }
        });

        rv_cloud_monitor.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        rv_cloud_monitor.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);
        //动态加载广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(CONSTS.TRANSFER_ACTION);
        getActivity().registerReceiver(myReceiver, filter);

        return root;
    }


    @Override
    public void onStart() {
        super.onStart();

        //loadData();
    }

    //订阅方法，当接收到事件的时候，会调用该方法
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent messageEvent) {
        //ToastUtil.showToast("message:" + messageEvent.getMessage(), mContext);
        if ("transfer".equals(messageEvent.getMessage())) {
            //mTransferAdapter.notifyDataSetChanged();
        }
    }


    public static final CloudMonitorFragment newInstance() {
        CloudMonitorFragment fragment = new CloudMonitorFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("organSeg", organSeg);
//        bundle.putString("type", type);
//        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onStop() {
        super.onStop();
        CONSTS.HEART_START = false;
        CONSTS.TEXT_PUSH = false;
        LogUtil.e(TAG, "onStop:");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        getActivity().unregisterReceiver(myReceiver);
        CONSTS.TEXT_PUSH = false;
        LogUtil.e(TAG, "onStop2");
    }


    private void loadData() {
        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "getTransferList");
        params.addBodyParameter("phone", SharePreUtils.getString("phone", "", getActivity()));
        params.addBodyParameter("page", page + "");
        params.addBodyParameter("pageSize", CONSTS.PAGE_SIZE + "");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                pb_wait.setVisibility(View.GONE);
                TransferingJson transferingJson = new Gson().fromJson(result, TransferingJson.class);
                LogUtil.e(TAG, "result11:" + result);
                if (transferingJson != null && transferingJson.getResult() == CONSTS.SEND_OK) {
                    //LogUtil.e(TAG, "result22:" + transferingJson.getObj().size());
                    if (transferingJson.getObj() != null && transferingJson.getObj().size() > 0) {
                        LogUtil.e(TAG, "result22:" + transferingJson.getObj().size());
                        if (page == 0) {
                            mTransfers = transferingJson.getObj();
                        } else {
                            mTransfers.addAll(transferingJson.getObj());
                        }

                        mTransferAdapter.refreshList(mTransfers);
                        rl_no_content.setVisibility(View.GONE);
                        rv_cloud_monitor.setVisibility(View.VISIBLE);
                        CONSTS.IS_START = 1;
                        for (int i = 0; i < mTransfers.size(); i++) {
                            if ("0".equals(mTransfers.get(i).getIsStart())) {
                                CONSTS.IS_START = 0;
                            }
                        }
                    } else {
                        rl_no_content.setVisibility(View.VISIBLE);
                        rv_cloud_monitor.setVisibility(View.GONE);
                        CONSTS.IS_START = 1;
                    }
                } else {
                    LogUtil.e(TAG, "result33最近更新最近更新最近更新:" + transferingJson);
                    if (page == 0) {
                        CONSTS.IS_START = 1;
                        rl_no_content.setVisibility(View.VISIBLE);
                        rv_cloud_monitor.setVisibility(View.GONE);
                    }
                }
                rv_cloud_monitor.refreshComplete();
                rv_cloud_monitor.loadMoreComplete();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                rl_no_content.setVisibility(View.VISIBLE);
                rv_cloud_monitor.setVisibility(View.GONE);
                CONSTS.IS_START = 1;
                LogUtil.e(TAG, "ex:" + ex.getMessage());
                rv_cloud_monitor.refreshComplete();
                rv_cloud_monitor.loadMoreComplete();
                pb_wait.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }


    private void initDataBefore() {
        mTransfers = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mTransferAdapter = new TransferAdapter(mTransfers, getActivity());

        //RecycleView 增加边距
        int spacingInPixels = DensityUtil.dip2px(15);
        rv_cloud_monitor.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        rv_cloud_monitor.setLayoutManager(mLayoutManager);
        rv_cloud_monitor.setAdapter(mTransferAdapter);

        //点击事件
        mTransferAdapter.setOnItemClickListener(this);
        //recyclerview滚动监听


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_no_content:
                page = 0;
                loadData();
                break;
            //历史信息
            case R.id.tv_cloud_monitor_history:

                startActivity(new Intent(getActivity(), HistoryActivity.class));
                break;
            //点击标题回到顶部
            case R.id.rl_title:
                rv_cloud_monitor.scrollToPosition(0);
                //ToastUtil.showToast("gg",getActivity());
                break;
        }
    }

    private void CallPhone(String phone) {

        // 拨号：激活系统的拨号组件
        Intent intent = new Intent(); // 意图对象：动作 + 数据
        intent.setAction(Intent.ACTION_CALL); // 设置动作
        Uri data = Uri.parse("tel:" + phone); // 设置数据
        intent.setData(data);
        mContext.startActivity(intent); // 激活Activity组件

    }

    @Override
    public void onResume() {
        super.onResume();
        //ToastUtil.showToast("onStart",getActivity());
    }

    // 处理权限申请的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 授权成功，继续打电话
                    CallPhone(callPhone);
                } else {
                    // 授权失败！
                    ToastUtil.showToast("未授权拨打电话权限", getActivity());

                }
                break;
            }
        }

    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onLeftPhoneClick(View view, final String postRole, final String name, final String phone, int position) {
        if ("0".equals(mTransfers.get(position).getIsStart()) && CONSTS.IS_START == 0) {

        } else {

            mAlertDialog = new AlertDialog.Builder(mContext);

            View v = LayoutInflater.from(getContext()).inflate(R.layout.phone_alert, null);
            //mAlertDialog.setMessage("是否拨打\n" + postRole + "  " + name +"  "+phone);
            mAlertDialog.setView(v);
            TextView tv_phone = (TextView) v.findViewById(R.id.tv_phone);
            tv_phone.setText(postRole + "  " + name + "  " + phone);
            mAlertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            mAlertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // 检查是否获得了权限（Android6.0运行时权限）
                    callPhone = phone;
                    if (ContextCompat.checkSelfPermission(mContext,
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // 没有获得授权，申请授权
                        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext,
                                Manifest.permission.CALL_PHONE)) {
                            // 返回值：
//                          如果app之前请求过该权限,被用户拒绝, 这个方法就会返回true.
//                          如果用户之前拒绝权限的时候勾选了对话框中”Don’t ask again”的选项,那么这个方法会返回false.
//                          如果设备策略禁止应用拥有这条权限, 这个方法也返回false.
                            // 弹窗需要解释为何需要该权限，再次请求授权
                            ToastUtil.showToast("未授权！", mContext);

                            // 帮跳转到该应用的设置界面，让用户手动授权
//                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                            Uri uri = Uri.fromParts("package", getPackageName(), null);
//                            intent.setData(uri);
//                            startActivity(intent);
                        } else {
                            // 不需要解释为何需要该权限，直接请求授权
                            ActivityCompat.requestPermissions((Activity) mContext,
                                    new String[]{Manifest.permission.CALL_PHONE},
                                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
                        }
                    } else {
                        // 已经获得授权，可以打电话
                        CallPhone(phone);
                    }
                }
            });

            mAlertDialog.show();


        }
    }

    @Override
    public void onPhoneClick(View view, final String postRole, final String name, final String phone) {


        mAlertDialog = new AlertDialog.Builder(mContext);

        View v = LayoutInflater.from(getContext()).inflate(R.layout.phone_alert, null);
        //mAlertDialog.setMessage("是否拨打\n" + postRole + "  " + name +"  "+phone);
        mAlertDialog.setView(v);
        TextView tv_phone = (TextView) v.findViewById(R.id.tv_phone);
        String post = postRole;
        if ("无".equals(post) || post == null || "".equals(post)) {
            post = "";
        } else if ("转".equals(post)) {
            post = "转运医师  ";
        } else if ("协".equals(post)) {
            post = "协调专员  ";
        } else if ("OPO".equals(post)) {
            post = "OPO人员  ";
        } else {
            post = post + "  ";
        }
        tv_phone.setText(post + name + "  " + phone);


        mAlertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        mAlertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // 检查是否获得了权限（Android6.0运行时权限）
                callPhone = phone;
                if (ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // 没有获得授权，申请授权
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext,
                            Manifest.permission.CALL_PHONE)) {
                        // 返回值：
//                          如果app之前请求过该权限,被用户拒绝, 这个方法就会返回true.
//                          如果用户之前拒绝权限的时候勾选了对话框中”Don’t ask again”的选项,那么这个方法会返回false.
//                          如果设备策略禁止应用拥有这条权限, 这个方法也返回false.
                        // 弹窗需要解释为何需要该权限，再次请求授权
                        ToastUtil.showToast("未授权！", mContext);

                        // 帮跳转到该应用的设置界面，让用户手动授权
//                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                            Uri uri = Uri.fromParts("package", getPackageName(), null);
//                            intent.setData(uri);
//                            startActivity(intent);
                    } else {
                        // 不需要解释为何需要该权限，直接请求授权
                        ActivityCompat.requestPermissions((Activity) mContext,
                                new String[]{Manifest.permission.CALL_PHONE},
                                MY_PERMISSIONS_REQUEST_CALL_PHONE);
                    }
                } else {
                    // 已经获得授权，可以打电话
                    CallPhone(phone);
                }
            }
        });

        mAlertDialog.show();

    }

    @Override
    public void onEditClick(View view, final int position) {
        if (mTransfers.size() == 0) {
            page = 0;
            loadData();
            return;
        }
        if (mPhone.equals(mTransfers.get(position).getPhone()) || mPhone.equals(mTransfers.get(position).getContactPhone()) || mPhone.equals(mTransfers.get(position).getOpoContactPhone())) {

            showWaitDialog(getResources().getString(R.string.loading), true, "");
            RequestParams params = new RequestParams(URL.TRANSFER);
            params.addBodyParameter("action", "getTransferByOrganSeg");
            params.addBodyParameter("organSeg", mTransfers.get(position).getOrganSeg());
            x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    TransferTrueJson objBean = new Gson().fromJson(result, TransferTrueJson.class);
                    if (objBean != null && objBean.getResult() == CONSTS.SEND_OK) {
                        CONSTS.TRANSFER_STATUS = 1;
                        Intent intent = new Intent(mContext, NewMonitorActivity.class);
                        intent.putExtra("modifyTransfer", objBean.getObj());
                        CONSTS.TYPE = "scan";
                        //通知转运监控
                        noticeTransfer(mTransfers.get(position).getOrganSeg(), "");
                        mContext.startActivity(intent);
                    }
                    dismissDialog();
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    dismissDialog();
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });


//            TransferingJson.ObjBean oldObjBean = mTransfers.get(position);
//            TransferingJson.ObjBean TObjBean = new TransferingJson.ObjBean();
//            TObjBean.setPhone(oldObjBean.getPhone());
//            TObjBean.setBlood(oldObjBean.getBlood());
//            TObjBean.setBloodNum(oldObjBean.getBloodNum());
//            TObjBean.setContactName(oldObjBean.getContactName());
//            TObjBean.setContactPhone(oldObjBean.getContactPhone());
//            TObjBean.setDistance(oldObjBean.getDistance());
//            TObjBean.setEndLati(oldObjBean.getEndLati());
//            TObjBean.setEndLong(oldObjBean.getEndLong());
//            TObjBean.setFromCity(oldObjBean.getFromCity());
//            TObjBean.setGetTime(oldObjBean.getGetTime());
//            TObjBean.setIsStart(oldObjBean.getIsStart());
//            TObjBean.setOpenPsd(oldObjBean.getOpenPsd());
//            TObjBean.setOpoName(oldObjBean.getOpoName());
//            TObjBean.setOrgan(oldObjBean.getOrgan());
//            TObjBean.setOrganNum(oldObjBean.getOrganNum());
//            TObjBean.setOrganSeg(oldObjBean.getOrganSeg());
//            TObjBean.setSampleOrgan(oldObjBean.getSampleOrgan());
//            TObjBean.setStartLati(oldObjBean.getStartLati());
//            TObjBean.setStartLong(oldObjBean.getStartLong());
//            TObjBean.setToHosp(oldObjBean.getToHosp());
//            TObjBean.setTracfficType(oldObjBean.getTracfficType());
//            TObjBean.setTracfficNumber(oldObjBean.getTracfficNumber());
//            TObjBean.setSampleOrganNum(oldObjBean.getSampleOrganNum());
//            TObjBean.setToHospName(oldObjBean.getToHospName());
//            TObjBean.setTrueName(oldObjBean.getTrueName());
//            TObjBean.setTransferid(oldObjBean.getTransferid());
//            TObjBean.setBoxNo(oldObjBean.getBoxNo());
//            TObjBean.setOpoContactName(oldObjBean.getOpoContactName());
//            TObjBean.setOpoContactPhone(oldObjBean.getOpoContactPhone());


        } else {
            ToastUtil.showToast("您不是创建转运信息的人,无法操作,请联系创建人", mContext);//眉山市
        }
    }

    @Override
    public void initData() {
        page = 0;
        loadData();
        LogUtil.e(TAG, "initData:");
    }

    /**
     * 通知云监控改变
     *
     * @param organSeg
     */
    private void noticeTransfer(String organSeg, String type) {
        RequestParams params = new RequestParams(URL.PUSH);
        params.addBodyParameter("action", "sendPushTransfer");
        params.addBodyParameter("organSeg", organSeg);
        params.addBodyParameter("type", type);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

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

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String pushType = intent.getStringExtra("pushType");

            if ("transfer".equals(pushType)) {

                page = 0;
                mTransfers = new ArrayList<>();
                loadData();
            }
            //删除转运时的操作
            if ("delete".equals(pushType)) {
                try {
                    Thread.sleep(2500);
                    page = 0;
                    mTransfers = new ArrayList<>();
                    loadData();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    };

}
