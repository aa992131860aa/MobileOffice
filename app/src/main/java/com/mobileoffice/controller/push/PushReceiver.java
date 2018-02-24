package com.mobileoffice.controller.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.gson.Gson;
import com.mobileoffice.controller.message.contact.SystemMessageActivity;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.PhotoJson;
import com.mobileoffice.json.PushJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import cn.jpush.android.api.JPushInterface;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;

/**
 * Created by 99213 on 2017/5/18.
 */

public class PushReceiver extends BroadcastReceiver {
    private String TAG = "PushReceiver";
    private String phone;

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            systemMessage(context, intent);

            Bundle bundle = intent.getExtras();
            //LogUtil.e(TAG, "action:" + JPushInterface.ACTION_NOTIFICATION_OPENED + ",intentAction:" + intent.getAction() + ",bundle:" + bundle);
            if (bundle != null) {
//                String title3 = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
//                String message1 = bundle.getString(JPushInterface.EXTRA_ALERT);
//                String title1 = bundle.getString(JPushInterface.ACTION_MESSAGE_RECEIVED);
                String title2 = bundle.getString(JPushInterface.EXTRA_MESSAGE);
                //LogUtil.e(TAG, "title1:" + title1 + ",title2:" + title2 + ",title3:" + title3 + ",message1:" + message1 + "," + intent.getAction() + "," + JPushInterface.ACTION_ACTIVITY_OPENDED);
                //通知云转运刷新
                if (title2 != null && title2.contains("transfer:")) {
                    //EventBus.getDefault().post(new MessageEvent("transfer"));
                    Intent boardIntent = new Intent(CONSTS.TRANSFER_ACTION);
                    boardIntent.putExtra("pushType", "transfer");
                    getGroupName(title2.split(":")[1]);
                    context.sendBroadcast(boardIntent);
                    //LogUtil.e(TAG, "title2:" + title2);
                }
                //通知云转运刷新(删除转运时)
                if (title2 != null && title2.contains("delete:")) {
                    //EventBus.getDefault().post(new MessageEvent("transfer"));
                    Intent boardIntent = new Intent(CONSTS.TRANSFER_ACTION);
                    boardIntent.putExtra("pushType", "delete");
                    context.sendBroadcast(boardIntent);


                    //移除会话列表
                    RongIM.getInstance().removeConversation(Conversation.ConversationType.GROUP, title2.split(":")[1], new RongIMClient.ResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {

                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });

                }
            }


            if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                //ToastUtil.showToast("this is a click event");


                if (bundle != null) {
                    String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
                    String message = bundle.getString(JPushInterface.EXTRA_ALERT);
                    LogUtil.e(TAG, "title:" + title + ",message:" + message + "," + intent.getAction() + "," + JPushInterface.ACTION_ACTIVITY_OPENDED);

                    Intent i = new Intent(context, SystemMessageActivity.class);
                    i.putExtra("systemType", "system");
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            }
        } catch (Exception e) {
            //ToastUtil.showToast("已发送推送",context);
        }

    }

    /**
     * 获取群组名
     */
    private void getGroupName(final String organSeg) {

        RequestParams params = new RequestParams(URL.RONG);
        params.addBodyParameter("action", "getGroupName");
        params.addBodyParameter("organSeg", organSeg);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    RongIM.getInstance().refreshGroupInfoCache(new Group(organSeg, photoJson.getMsg(), Uri.parse(URL.TOMCAT_SOCKET + "images/team.png")));
                    if (photoJson.getMsg().contains("已转运")) {
                        RongIM.getInstance().setConversationToTop(Conversation.ConversationType.GROUP, organSeg, false, new RongIMClient.ResultCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean aBoolean) {

                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {

                            }
                        });
                    } else if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                        if (photoJson.getMsg().contains("待转运") || photoJson.getMsg().contains("转运中")) {
                            RongIM.getInstance().setConversationToTop(Conversation.ConversationType.GROUP, organSeg, true, new RongIMClient.ResultCallback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean aBoolean) {

                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {

                                }
                            });
                        }
                    }
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

    /**
     * 1:获取未读数
     * 2:获取当前的推送内容
     * 3:广播推送修改界面
     */
    private void systemMessage(final Context context, final Intent intent) {
        phone = SharePreUtils.getString("phone", "", context);
        RequestParams params = new RequestParams(URL.USER);
        params.addBodyParameter("action", "pushCount");
        params.addBodyParameter("phone", phone);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                Gson gson = new Gson();
                PushJson pushJson = gson.fromJson(result, PushJson.class);
                if (pushJson != null && pushJson.getResult() == CONSTS.SEND_OK) {

                    //改变系统消息未读数
                    CONSTS.UNREAD_PUSH_NUM = pushJson.getObj() == null ? 0 : pushJson.getObj().getCount();
                    CONSTS.NEW_SYSTEM_TIME = pushJson.getObj() == null ? "" : pushJson.getObj().getCreateTime();

                } else {
                    CONSTS.UNREAD_PUSH_NUM = 0;
                    CONSTS.NEW_SYSTEM_TIME = "";
                }
                Bundle bundle = intent.getExtras();
                CONSTS.NEW_SYSTEM_MESSAGE = bundle != null ? bundle.getString(JPushInterface.EXTRA_ALERT) : "";
                SharePreUtils.putInt("unread_push_num", CONSTS.UNREAD_PUSH_NUM, context);
                SharePreUtils.putString("new_system_message", CONSTS.NEW_SYSTEM_MESSAGE, context);
                SharePreUtils.putString("new_system_time", CONSTS.NEW_SYSTEM_TIME, context);

                Intent i = new Intent("com.mobile.office.system.message");
                context.sendBroadcast(i);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                CONSTS.UNREAD_PUSH_NUM = 0;
                Bundle bundle = intent.getExtras();
                CONSTS.NEW_SYSTEM_MESSAGE = bundle != null ? bundle.getString(JPushInterface.EXTRA_ALERT) : "";
                SharePreUtils.putInt("unread_push_num", CONSTS.UNREAD_PUSH_NUM, context);
                SharePreUtils.putString("new_system_message", CONSTS.NEW_SYSTEM_MESSAGE, context);

                Intent i = new Intent("com.mobile.office.system.message");
                context.sendBroadcast(i);
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
