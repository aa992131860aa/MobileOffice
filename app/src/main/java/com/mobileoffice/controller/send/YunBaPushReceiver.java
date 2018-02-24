package com.mobileoffice.controller.send;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.PushJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.DemoUtil;
import com.mobileoffice.utils.OSUtils;
import com.mobileoffice.utils.SharePreUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import io.yunba.android.manager.YunBaManager;

/**
 * Created by 99213 on 2017/11/1.
 */

public class YunBaPushReceiver extends BroadcastReceiver {
    private String mPhone;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (YunBaManager.MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {

            String topic = intent.getStringExtra(YunBaManager.MQTT_TOPIC);
            String msg = intent.getStringExtra(YunBaManager.MQTT_MSG);
            String alert = intent.getStringExtra("alert");


            //在这里处理从服务器发布下来的消息， 比如显示通知栏， 打开 Activity 等等
            StringBuilder showMsg = new StringBuilder();
            showMsg.append("Received message from server: ")
                    .append(YunBaManager.MQTT_TOPIC)
                    .append(" = ")
                    .append(topic)
                    .append(" ")
                    .append(YunBaManager.MQTT_MSG)
                    .append(" = ")

                    .append(msg);

            if("".equals(OSUtils.getSystem())) {
                 mPhone = SharePreUtils.getString("phone","",context);
                DemoUtil.showNotifation(context, "系统消息", msg);
                updateSystemCount(context,mPhone);
            }
            //ToastUtil.showToast(topic + "," + msg, context);
        }

    }
    /**
     * 更新系统消息未读数
     */
    private void updateSystemCount(final  Context pContext,String pPhone) {

        RequestParams params = new RequestParams(URL.USER);
        params.addBodyParameter("action", "pushCount");
        params.addBodyParameter("phone", pPhone);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                PushJson pushJson = gson.fromJson(result, PushJson.class);
                if (pushJson != null && pushJson.getResult() == CONSTS.SEND_OK) {

                    //改变系统消息未读数
                    CONSTS.UNREAD_PUSH_NUM = pushJson.getObj() == null ? 0 : pushJson.getObj().getCount();
                    CONSTS.NEW_SYSTEM_MESSAGE = pushJson.getObj() == null ? "" : pushJson.getObj().getContent();
                    CONSTS.NEW_SYSTEM_TIME = pushJson.getObj() == null ? "" : pushJson.getObj().getCreateTime();

                } else {
                    CONSTS.UNREAD_PUSH_NUM = 0;
                }


                SharePreUtils.putInt("unread_push_num", CONSTS.UNREAD_PUSH_NUM, pContext);
                SharePreUtils.putString("new_system_message", CONSTS.NEW_SYSTEM_MESSAGE, pContext);

                Intent i = new Intent("com.mobile.office.system.message");
                pContext.sendBroadcast(i);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                CONSTS.UNREAD_PUSH_NUM = 0;

                CONSTS.NEW_SYSTEM_MESSAGE = "";


                Intent i = new Intent("com.mobile.office.system.message");
                pContext.sendBroadcast(i);
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
