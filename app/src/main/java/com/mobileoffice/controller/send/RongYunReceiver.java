package com.mobileoffice.controller.send;

import android.content.Context;

import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.ToastUtil;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import io.rong.push.notification.PushMessageReceiver;
import io.rong.push.notification.PushNotificationMessage;

/**
 * Created by 99213 on 2017/5/11.
 */


public class RongYunReceiver extends PushMessageReceiver {
    private String TAG = "RongYunReceiver";
    /**
     * 用来接收服务器发来的通知栏消息(消息到达客户端时触发)
     * 默认return false，通知消息会以融云 SDK 的默认形式展现
     * 如果需要自定义通知栏的展示，在这里实现⾃己的通知栏展现代码，只要return true即可
     */
    @Override
    public boolean onNotificationMessageArrived(Context context, PushNotificationMessage message) {

        LogUtil.e(TAG,"arrived:"+message.getPushContent() );
        return false;
    }

    /**
     * ⽤户点击通知栏消息时触发 (注意:如果⾃定义了通知栏的展现，则不会触发)
     * 默认 return false
     * 如果需要自定义点击通知时的跳转，return true即可
     */
    @Override
    public boolean onNotificationMessageClicked(Context context, PushNotificationMessage message) {
        //ToastUtil.showToast("click:"+message.getConversationType().getName()+","+message.getTargetId(),context);
        //RongIM.getInstance().startConversation(context, Conversation.ConversationType.PRIVATE,message.getTargetId(),message.getPushTitle());
        LogUtil.e(TAG,"clicked:"+message);
        return false;
    }
}