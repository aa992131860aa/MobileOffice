package com.mobileoffice.controller.message;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobileoffice.R;
import com.mobileoffice.utils.LogUtil;

import io.rong.common.RLog;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imkit.model.ConversationKey;
import io.rong.imkit.model.ConversationProviderTag;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.utils.RongDateUtils;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

/**
 * Created by 99213 on 2017/8/26.
 */
@ConversationProviderTag(
        conversationType = "group",
        portraitPosition = 1
)
public class GroupProviderTemp implements IContainerItemProvider.ConversationProvider<UIConversation> {
    private static final String TAG = "GroupProviderTemp";
    private Context mContext;

    public GroupProviderTemp() {

    }

    public View newView(Context context, ViewGroup viewGroup) {
        mContext = context;
        View result = LayoutInflater.from(context).inflate(io.rong.imkit.R.layout.rc_item_base_conversation, (ViewGroup) null);
        GroupProviderTemp.ViewHolder holder = new GroupProviderTemp.ViewHolder();
        holder.title = (TextView) result.findViewById(io.rong.imkit.R.id.rc_conversation_title);
        holder.time = (TextView) result.findViewById(io.rong.imkit.R.id.rc_conversation_time);
        holder.content = (TextView) result.findViewById(io.rong.imkit.R.id.rc_conversation_content);
        holder.notificationBlockImage = (ImageView) result.findViewById(io.rong.imkit.R.id.rc_conversation_msg_block);
        holder.readStatus = (ImageView) result.findViewById(io.rong.imkit.R.id.rc_conversation_status);
        result.setTag(holder);
        return result;
    }

    public void bindView(View view, int position, UIConversation data) {
        GroupProviderTemp.ViewHolder holder = (GroupProviderTemp.ViewHolder) view.getTag();
        ProviderTag tag = null;
        if (data == null) {
            holder.title.setText((CharSequence) null);
            holder.time.setText((CharSequence) null);
            holder.content.setText((CharSequence) null);
        } else {
            holder.title.setText(data.getUIConversationTitle());
            if (data.isTop() && data.getUIConversationTitle().contains("转运中") || data.isTop() && data.getUIConversationTitle().contains("待转运")) {
                holder.title.setTextColor(mContext.getResources().getColor(R.color.blue));
            } else {
                holder.title.setTextColor(mContext.getResources().getColor(R.color.font_black));
            }
            LogUtil.e(TAG, "-----------------name:" + data.getConversationType().getName() + "," + data.getUIConversationTitle() + ":" + data.getConversationContent() + ",phone:" + data.getConversationSenderId() + ",groupId:" + data.getConversationTargetId() + ",isTop:" + data.isTop());
            if ("group".equals(data.getConversationType().getName())) {
                if ((!data.isTop() && data.getUIConversationTitle().contains("转运中")) || (!data.isTop() && data.getUIConversationTitle().contains("待转运"))) {
                    RongIM.getInstance().setConversationToTop(Conversation.ConversationType.GROUP, data.getConversationTargetId(), true, new RongIMClient.ResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {

                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
                }
                if ((data.isTop() && data.getUIConversationTitle().contains("已转运")) ) {
                    RongIM.getInstance().setConversationToTop(Conversation.ConversationType.GROUP, data.getConversationTargetId(), false, new RongIMClient.ResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {

                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
                }

                if("".equals(data.getUIConversationTitle())){
                    LogUtil.e(TAG, "++++++++++++++++name:" + data.getConversationType().getName() + "," + data.getUIConversationTitle() + ":" + data.getConversationContent() + ",phone:" + data.getConversationSenderId() + ",groupId:" + data.getConversationTargetId() + ",isTop:" + data.isTop());

//                    RongIM.getInstance().removeConversation(Conversation.ConversationType.GROUP, data.getConversationTargetId(), new RongIMClient.ResultCallback<Boolean>() {
//                        @Override
//                        public void onSuccess(Boolean aBoolean) {
//                           // LogUtil.e(TAG,"-----------------name:onsuccess:"+aBoolean);
//                        }
//
//                        @Override
//                        public void onError(RongIMClient.ErrorCode errorCode) {
//                            //LogUtil.e(TAG,"-----------------name:errorCode:"+errorCode);
//                        }
//                    });
                }
            }
//            RongIM.getInstance().setConversationToTop(Conversation.ConversationType.GROUP, organSeg, true, new RongIMClient.ResultCallback<Boolean>() {
//                @Override
//                public void onSuccess(Boolean aBoolean) {
//
//                }
//
//                @Override
//                public void onError(RongIMClient.ErrorCode errorCode) {
//
//                }
//            });
            String time = RongDateUtils.getConversationListFormatDate(data.getUIConversationTime(), view.getContext());
            holder.time.setText(time);
            if (TextUtils.isEmpty(data.getDraft()) && !data.getMentionedFlag()) {
                boolean key1 = false;

                try {
                    key1 = view.getResources().getBoolean(io.rong.imkit.R.bool.rc_read_receipt);
                } catch (Resources.NotFoundException var10) {
                    RLog.e("PrivateConversationProvider", "rc_read_receipt not configure in rc_config.xml");
                    var10.printStackTrace();
                }

                if (key1) {
                    if (data.getSentStatus() == Message.SentStatus.READ && data.getConversationSenderId().equals(RongIM.getInstance().getCurrentUserId())) {
                        holder.readStatus.setVisibility(View.VISIBLE);
                    } else {
                        holder.readStatus.setVisibility(View.GONE);
                    }
                }

                holder.content.setText(data.getConversationContent());
            } else {
                SpannableStringBuilder key = new SpannableStringBuilder();
                SpannableString status;
                if (data.getMentionedFlag()) {
                    status = new SpannableString(view.getContext().getString(io.rong.imkit.R.string.rc_message_content_mentioned));
                    status.setSpan(new ForegroundColorSpan(view.getContext().getResources().getColor(io.rong.imkit.R.color.rc_mentioned_color)), 0, status.length(), 33);
                    key.append(status).append(" ").append(data.getConversationContent());
                } else {
                    status = new SpannableString(view.getContext().getString(io.rong.imkit.R.string.rc_message_content_draft));
                    status.setSpan(new ForegroundColorSpan(view.getContext().getResources().getColor(io.rong.imkit.R.color.rc_draft_color)), 0, status.length(), 33);
                    key.append(status).append(" ").append(data.getDraft());
                }

                AndroidEmoji.ensure(key);
                holder.content.setText(key);
                holder.readStatus.setVisibility(View.GONE);
            }

            if (RongContext.getInstance() != null && data.getMessageContent() != null) {
                tag = RongContext.getInstance().getMessageProviderTag(data.getMessageContent().getClass());
            }

            if (data.getSentStatus() != null && (data.getSentStatus() == Message.SentStatus.FAILED || data.getSentStatus() == Message.SentStatus.SENDING) && tag != null && tag.showWarning() && data.getConversationSenderId() != null && data.getConversationSenderId().equals(RongIM.getInstance().getCurrentUserId())) {
                Bitmap key2 = BitmapFactory.decodeResource(view.getResources(), io.rong.imkit.R.drawable.rc_conversation_list_msg_send_failure);
                int status1 = key2.getWidth();
                Drawable drawable = null;
                if (data.getSentStatus() == Message.SentStatus.FAILED && TextUtils.isEmpty(data.getDraft())) {
                    drawable = view.getContext().getResources().getDrawable(R.drawable.rc_conversation_list_msg_send_failure);
                } else if (data.getSentStatus() == Message.SentStatus.SENDING && TextUtils.isEmpty(data.getDraft())) {
                    drawable = view.getContext().getResources().getDrawable(R.drawable.rc_conversation_list_msg_sending);
                }

                if (drawable != null) {
                    drawable.setBounds(0, 0, status1, status1);
                    holder.content.setCompoundDrawablePadding(10);
                    holder.content.setCompoundDrawables(drawable, (Drawable) null, (Drawable) null, (Drawable) null);
                }
            } else {
                holder.content.setCompoundDrawables((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
            }

            ConversationKey key3 = ConversationKey.obtain(data.getConversationTargetId(), data.getConversationType());
            Conversation.ConversationNotificationStatus status2 = RongContext.getInstance().getConversationNotifyStatusFromCache(key3);
            if (status2 != null && status2.equals(Conversation.ConversationNotificationStatus.DO_NOT_DISTURB)) {
                holder.notificationBlockImage.setVisibility(View.VISIBLE);
            } else {
                holder.notificationBlockImage.setVisibility(View.GONE);
            }
        }

    }

    public Spannable getSummary(UIConversation data) {
        return null;
    }

    //    public String getTitle(String userId) {
//        UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(userId);
//        return userInfo == null?userId:userInfo.getName();
//    }
//
//    public Uri getPortraitUri(String userId) {
//        UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(userId);
//        return userInfo == null?null:userInfo.getPortraitUri();
//    }
    public String getTitle(String groupId) {
        String name;
        if (RongUserInfoManager.getInstance().getGroupInfo(groupId) == null) {
            name = "";
        } else {
            name = RongUserInfoManager.getInstance().getGroupInfo(groupId).getName();
        }

        return name;
    }

    public Uri getPortraitUri(String id) {
        Uri uri;
        if (RongUserInfoManager.getInstance().getGroupInfo(id) == null) {
            uri = null;
        } else {
            uri = RongUserInfoManager.getInstance().getGroupInfo(id).getPortraitUri();
        }

        return uri;
    }

    class ViewHolder {
        TextView title;
        TextView time;
        TextView content;
        ImageView notificationBlockImage;
        ImageView readStatus;

        ViewHolder() {
        }
    }
}