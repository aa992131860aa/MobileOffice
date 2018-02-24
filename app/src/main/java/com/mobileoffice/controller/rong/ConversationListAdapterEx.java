package com.mobileoffice.controller.rong;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobileoffice.R;
import com.mobileoffice.controller.message.ConversationListAdapter;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.ToastUtil;

import io.rong.imkit.RongIM;
import io.rong.imkit.model.UIConversation;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * Created by 99213 on 2017/5/15.
 */

public class ConversationListAdapterEx extends ConversationListAdapter {
    private String TAG = "ConversationListAdapterEx";
    public ConversationListAdapterEx(Context context) {
        super(context);
    }
    @Override
    protected View newView(Context context, int position, ViewGroup group) {

        return super.newView(context, position, group);
    }

    @Override
    protected void bindView(View v, int position,final UIConversation data) {
        //LogUtil.e(TAG,"ID:"+data.getConversationTargetId());
        super.bindView(v, position, data);
        if(data.getConversationType().equals(Conversation.ConversationType.DISCUSSION))
            data.setUnreadType(UIConversation.UnreadRemindType.REMIND_ONLY);


        //data.setconv
//        if(data.getConversationType().getName().equals("group")&&(data.getUIConversationTitle().contains("待转运")||data.getUIConversationTitle().contains("转运中"))){
//
//
//            RongIM.getInstance().setConversationToTop(Conversation.ConversationType.GROUP,data.getConversationTargetId(),false, new RongIMClient.ResultCallback<Boolean>() {
//                @Override
//                public void onSuccess(Boolean aBoolean) {
//                      LogUtil.e(TAG,"ok:"+data.getConversationTargetId());
//                }
//
//                @Override
//                public void onError(RongIMClient.ErrorCode errorCode) {
//
//                }
//            });
//        }


    }
}
