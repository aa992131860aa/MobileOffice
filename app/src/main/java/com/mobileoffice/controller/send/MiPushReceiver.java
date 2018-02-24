package com.mobileoffice.controller.send;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import java.util.List;

import io.rong.push.PushService;
import io.rong.push.common.RLog;

/**
 * Created by 99213 on 2017/12/12.
 */

public class MiPushReceiver extends PushMessageReceiver{
    private static final String TAG = "MiMessageReceiver";
    private String mRegId;

    public MiPushReceiver() {
    }

    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        RLog.v("MiMessageReceiver", "onReceivePassThroughMessage is called. " + message.toString());
    }

    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        RLog.v("MiMessageReceiver", "onNotificationMessageClicked is called. " + message.toString());
        Intent intent = new Intent();
        intent.setAction("io.rong.push.intent.MI_MESSAGE_CLICKED");
        intent.setPackage(context.getPackageName());
        intent.putExtra("message", message);
        context.sendBroadcast(intent);
        //ToastUtil.showToast("bb,"+message,context);
    }

    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        RLog.v("MiMessageReceiver", "onNotificationMessageArrived is called. " + message.toString());
        Intent intent = new Intent();
        intent.setAction("io.rong.push.intent.MI_MESSAGE_ARRIVED");
        intent.setPackage(context.getPackageName());
        intent.putExtra("message", message);
        context.sendBroadcast(intent);
    }

    public void onCommandResult(Context context, MiPushCommandMessage message) {
        RLog.v("MiMessageReceiver", "onCommandResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = arguments != null && arguments.size() > 0?(String)arguments.get(0):null;
        if("register".equals(command)) {
            if(message.getResultCode() == 0L) {
                this.mRegId = cmdArg1;
                SharedPreferences sp = context.getSharedPreferences("RongPushAppConfig", 0);
                SharedPreferences.Editor editor = sp.edit();
                if(sp.getString("pushType", "").equals("MI")) {
                    String token = sp.getString("token", "");
                    if(token.equals(this.mRegId)) {
                        return;
                    }

                    editor.putString("token", this.mRegId);
                } else {
                    RLog.d("MiMessageReceiver", "write to cache.");
                    editor.putString("pushType", "MI");
                    editor.putString("token", this.mRegId);
                }

                editor.apply();
                RLog.e("MiMessageReceiver", "send to pushService.");

                try {
                    Intent intent = new Intent(context, PushService.class);
                    intent.setAction("io.rong.push.intent.action.REGISTRATION_INFO");
                    String regInfo = "MI|" + this.mRegId;
                    intent.putExtra("regInfo", regInfo);
                    context.startService(intent);
                } catch (Exception var10) {
                    editor.putString("pushType", "");
                    editor.putString("token", "");
                    editor.apply();
                    RLog.e("MiMessageReceiver", "SecurityException. Failed to send token to PushService.");
                }
            } else {
                RLog.e("MiMessageReceiver", "Failed to get register id from MI." + message.getResultCode());
            }
        }

    }

    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        RLog.v("MiMessageReceiver", "onReceiveRegisterResult is called. " + message.toString());
    }
}
