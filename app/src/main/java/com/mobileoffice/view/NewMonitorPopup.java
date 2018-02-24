package com.mobileoffice.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobileoffice.MainActivity;
import com.mobileoffice.R;
import com.mobileoffice.controller.new_monitor.NewMonitorBaseFragment;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.ContactListJson;
import com.mobileoffice.json.PhotoJson;
import com.mobileoffice.json.TransferJson;
import com.mobileoffice.json.TransferingJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.ConstantsUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Field;
import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Group;

public class NewMonitorPopup extends PopupWindow {
    private Context mContext;
    private int statusBarHeight = 0;

    public NewMonitorPopup(final Activity context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.new_monitor_pop, null);
        TextView tv_new_monitor_pop_content = (TextView) v.findViewById(R.id.tv_new_monitor_pop_content);
        TextView tv_new_monitor_pop_know = (TextView) v.findViewById(R.id.tv_new_monitor_pop_know);
        TextView tv_new_monitor_pop_start = (TextView) v.findViewById(R.id.tv_new_monitor_pop_start);

        this.setContentView(v);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(new BitmapDrawable());

        mContext.sendBroadcast(new Intent(CONSTS.MAIN_ACTION));
//        if (CONSTS.TRANSFER_STATUS == 1) {
//            tv_new_monitor_pop_know.setVisibility(View.GONE);
//        }
        tv_new_monitor_pop_know.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ConstantsUtil.sendTransferStatus(NewMonitorBaseFragment.objBean.getBoxNo(), "设备推送", CONSTS.NO_START, context);
                CONSTS.IS_START = 0;
                dismiss();
                //String content = "本次转运医师:" + NewMonitorBaseFragment.objBean.getTrueName() + ",科室协调员:" + NewMonitorBaseFragment.objBean.getContactName() + "。器官段号:" + NewMonitorBaseFragment.objBean.getOrganSeg() + "，" + NewMonitorBaseFragment.objBean.getFromCity() + "的" + NewMonitorBaseFragment.objBean.getOrgan() + "转运已开始。";
                //sendListTransferSms(NewMonitorBaseFragment.objBean.getPhones(), content);
                noticeTransfer(NewMonitorBaseFragment.objBean.getOrganSeg());

            }
        });
        tv_new_monitor_pop_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                loadEndLocation(mContext);
                noticeTransfer(NewMonitorBaseFragment.objBean.getOrganSeg());

            }
        });

        int distance = (int) MainActivity.distance;
        if (distance == 0) {
            distance = 1024;
        }
        if (MainActivity.temperature == null || MainActivity.temperature.equals("")) {
            MainActivity.temperature = "28";
        }
        if (MainActivity.weather == null || MainActivity.weather.equals("")) {
            MainActivity.weather = "晴";
        }
        if (MainActivity.endLocation == null || MainActivity.endLocation.equals("")) {
            MainActivity.endLocation = "上海";
        }
        tv_new_monitor_pop_content.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;【温馨提示】本次转运直线距离为<font color='#f72361'>" + distance + "km</font>，此时目的地<font color='#f72361'>" + MainActivity.endLocation + "气温为" + MainActivity.weather + "，" + MainActivity.temperature + "℃</font>，祝转运顺利！"));
        //南充市
    }

    /**
     * 通知云监控改变
     *
     * @param organSeg
     */
    private void noticeTransfer(String organSeg) {
        RequestParams params = new RequestParams(URL.PUSH);
        params.addBodyParameter("action", "sendPushTransfer");
        params.addBodyParameter("organSeg", organSeg);
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

    private void sendGroupMessage() {
        RequestParams params = new RequestParams(URL.RONG);
        params.addBodyParameter("action", "sendGroupMessageStart");
        params.addBodyParameter("phone", SharePreUtils.getString("phone", "", mContext));
        params.addBodyParameter("organSeg", NewMonitorBaseFragment.objBean.getOrganSeg());
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

    private void sendListTransferSms(String phones, String content) {

        RequestParams params = new RequestParams(URL.SMS);
        params.addBodyParameter("action", "sendListTransfer");
        params.addBodyParameter("phones", phones);
        params.addBodyParameter("content", content);
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

    /**
     * 获取结束的经纬度
     */
    private void loadEndLocation(final Context pContext) {

        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "updateStart");
        params.addBodyParameter("organSeg", NewMonitorBaseFragment.objBean.getOrganSeg());
        params.addBodyParameter("isStart", "1");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    ConstantsUtil.sendTransferStatus(NewMonitorBaseFragment.objBean.getBoxNo(), "设备推送", CONSTS.START, pContext);
                    CONSTS.IS_START = 1;
                    sendGroupMessage();
                    if (CONSTS.NO_START_VIEW != null) {
                        CONSTS.NO_START_VIEW.setVisibility(View.GONE);
                    }
                    getGroupName();
                    String content = "本次转运医师：" + NewMonitorBaseFragment.objBean.getTrueName() + "，科室协调员：" + NewMonitorBaseFragment.objBean.getContactName() + "。器官段号：" + NewMonitorBaseFragment.objBean.getOrganSeg() + "，" + NewMonitorBaseFragment.objBean.getFromCity() + "的" + NewMonitorBaseFragment.objBean.getOrgan() + "转运已开始。";
                    sendListTransferSms(NewMonitorBaseFragment.objBean.getPhones(), content);
                    //初始化状态
                    MainActivity.distance = 0;
                    MainActivity.temperature = "";
                    MainActivity.weather = "";
                    MainActivity.endLocation = "";
                    NewMonitorBaseFragment.objBean = new TransferJson.ObjBean();
                    CONSTS.CONTACT_LIST = new ArrayList<ContactListJson.ObjBean>();
                    dismiss();


                } else {
                    ToastUtil.showToast("创建失败", mContext);
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.showToast("网络错误", mContext);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 获取群组名
     */
    private void getGroupName() {
        final String organSeg = NewMonitorBaseFragment.objBean.getOrganSeg();
        RequestParams params = new RequestParams(URL.RONG);
        params.addBodyParameter("action", "getGroupName");
        params.addBodyParameter("organSeg", organSeg);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    RongIM.getInstance().refreshGroupInfoCache(new Group(organSeg, photoJson.getMsg(), Uri.parse(URL.TOMCAT_SOCKET+"images/team.png")));
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.showToast("网络错误", mContext);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = mContext.getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }

    /**
     * 显示popupWindow
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
//            this.showAsDropDown(parent, -20, 5);
            this.showAsDropDown(parent, 0, 0);
        } else {
            this.dismiss();
        }
    }


    public interface OnClickChangeListener {
        public void OnClickChange(int position);
    }

    public void setOnClickChangeListener(OnClickChangeListener clickChangeListener) {
        mListener = clickChangeListener;
    }

    private OnClickChangeListener mListener;

}