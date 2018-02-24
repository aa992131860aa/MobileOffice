package com.mobileoffice.controller.message;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobileoffice.R;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.ToastUtil;

import io.rong.common.RLog;
import io.rong.imkit.RongContext;
import io.rong.imkit.R.drawable;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imkit.model.ConversationProviderTag;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.model.UIConversation.UnreadRemindType;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imkit.widget.ProviderContainerView;
import io.rong.imkit.widget.adapter.BaseAdapter;
import io.rong.imkit.widget.provider.IContainerItemProvider.ConversationProvider;
import io.rong.imlib.model.Conversation.ConversationType;

public class ConversationListAdapter extends BaseAdapter<UIConversation> {
    private static final String TAG = "ConversationListAdapter";
    LayoutInflater mInflater;
    Context mContext;
    private ConversationListAdapter.OnPortraitItemClick mOnPortraitItemClick;
    private UIConversation data;

    public long getItemId(int position) {
        UIConversation conversation = (UIConversation) this.getItem(position);
        return conversation == null ? 0L : (long) conversation.hashCode();
    }

    public ConversationListAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(this.mContext);
    }

    public int findGatheredItem(ConversationType type) {
        int index = this.getCount();
        int position = -1;

        while (index-- > 0) {
            UIConversation uiConversation = (UIConversation) this.getItem(index);
            if (uiConversation.getConversationType().equals(type)) {
                position = index;
                break;
            }
        }

        return position;
    }

    public int findPosition(ConversationType type, String targetId) {
        int index = this.getCount();
        int position = -1;

        while (index-- > 0) {
            if (((UIConversation) this.getItem(index)).getConversationType().equals(type) && ((UIConversation) this.getItem(index)).getConversationTargetId().equals(targetId)) {
                position = index;
                break;
            }
        }

        return position;
    }

    protected View newView(Context context, int position, ViewGroup group) {
        View result = this.mInflater.inflate(R.layout.rc_item_conversation_app, (ViewGroup) null);
        ConversationListAdapter.ViewHolder holder = new ConversationListAdapter.ViewHolder();
        holder.layout = this.findViewById(result, id.rc_item_conversation);
        holder.leftImageLayout = this.findViewById(result, id.rc_item1);
        holder.rightImageLayout = this.findViewById(result, id.rc_item2);
        holder.leftImageView = (AsyncImageView) this.findViewById(result, id.rc_left);
        holder.rightImageView = (AsyncImageView) this.findViewById(result, id.rc_right);
        holder.contentView = (ProviderContainerView) this.findViewById(result, id.rc_content);
        holder.unReadMsgCount = (TextView) this.findViewById(result, id.rc_unread_message);
        holder.unReadMsgCountRight = (TextView) this.findViewById(result, id.rc_unread_message_right);
        holder.unReadMsgCountIcon = (ImageView) this.findViewById(result, id.rc_unread_message_icon);
        holder.unReadMsgCountRightIcon = (ImageView) this.findViewById(result, id.rc_unread_message_icon_right);
        result.setTag(holder);
        return result;
    }

    protected void bindView(View v, int position, final UIConversation data) {
        ConversationListAdapter.ViewHolder holder = (ConversationListAdapter.ViewHolder) v.getTag();
        if (data != null) {
            ConversationProvider provider = RongContext.getInstance().getConversationTemplate(data.getConversationType().getName());
            //ToastUtil.showToast(data.getConversationType().getName()+","+data.getConversationType().getValue(),mContext);
            if (provider == null) {
                RLog.e("ConversationListAdapter", "provider is null");
            } else {
                View view = holder.contentView.inflate(provider);

                provider.bindView(view, position, data);



                if (data.isTop()) {
                    holder.layout.setBackgroundDrawable(this.mContext.getResources().getDrawable(R.drawable.top_list_selector));


                } else {
                    holder.layout.setBackgroundDrawable(this.mContext.getResources().getDrawable(drawable.rc_item_list_selector));
                }

                //LogUtil.e(TAG, "name:"+data.getConversationType().getName()+","+data.getUIConversationTitle()+":"+data.getConversationContent());

                ConversationProviderTag tag = RongContext.getInstance().getConversationProviderTag(data.getConversationType().getName());
                boolean defaultId = false;
                int defaultId1 =  R.drawable.msg_1index_team;
                if (tag.portraitPosition() == 1) {
                    holder.leftImageLayout.setVisibility(View.VISIBLE);
                    if (data.getConversationType().equals(ConversationType.GROUP)) {


                        if(data.getUIConversationTitle().contains("-心脏")){
                            defaultId1 = R.drawable.msg_1index_team1;
                        }else if(data.getUIConversationTitle().contains("-肝脏")){
                            LogUtil.e(TAG,"groupName:"+data.getUIConversationTitle()+","+data.getUIConversationTitle().contains("肝脏")+",id:"+data.getConversationTargetId()+",");

                            defaultId1 = R.drawable.msg_1index_team2;
                        }else if(data.getUIConversationTitle().contains("-肺")){
                            defaultId1 = R.drawable.msg_1index_team3;
                        }else if(data.getUIConversationTitle().contains("-肾脏")){
                            defaultId1 = R.drawable.msg_1index_team4;
                        }else if(data.getUIConversationTitle().contains("-胰脏")){
                            defaultId1 = R.drawable.msg_1index_team5;
                        }else if(data.getUIConversationTitle().contains("-眼角膜")){
                            defaultId1 = R.drawable.msg_1index_team6;
                        }
                        else{
                            defaultId1 =  R.drawable.msg_1index_team;
                        }

                        if("".equals(data.getUIConversationTitle())){
                           // LogUtil.e(TAG,"groupName:"+data.getUIConversationTitle()+",id:"+data.getConversationTargetId()+",");

                        }
                    } else if (data.getConversationType().equals(ConversationType.DISCUSSION)) {
                        defaultId1 = drawable.rc_default_discussion_portrait;
                    } else {
                        defaultId1 = R.drawable.msg_2list_linkman;
                    }

                    holder.leftImageLayout.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            if (ConversationListAdapter.this.mOnPortraitItemClick != null) {
                                ConversationListAdapter.this.mOnPortraitItemClick.onPortraitItemClick(v, data);
                            }

                        }
                    });
                    holder.leftImageLayout.setOnLongClickListener(new OnLongClickListener() {
                        public boolean onLongClick(View v) {
                            if (ConversationListAdapter.this.mOnPortraitItemClick != null) {
                                ConversationListAdapter.this.mOnPortraitItemClick.onPortraitItemLongClick(v, data);
                            }

                            return true;
                        }
                    });
                    if (data.getConversationGatherState()) {
                        holder.leftImageView.setAvatar((String) null, defaultId1);
                    } else if (data.getIconUrl() != null) {
                        holder.leftImageView.setAvatar(data.getIconUrl().toString(), defaultId1);
                    } else {
                        holder.leftImageView.setAvatar((String) null, defaultId1);
                    }

                    if (data.getUnReadMessageCount() > 0) {
                        holder.unReadMsgCountIcon.setVisibility(View.VISIBLE);
                        if (data.getUnReadType().equals(UnreadRemindType.REMIND_WITH_COUNTING)) {
                            if (data.getUnReadMessageCount() > 99) {
                                holder.unReadMsgCount.setText(this.mContext.getResources().getString(string.rc_message_unread_count));
                            } else {
                                holder.unReadMsgCount.setText(Integer.toString(data.getUnReadMessageCount()));
                            }

                            holder.unReadMsgCount.setVisibility(View.VISIBLE);
                            holder.unReadMsgCountIcon.setImageResource(drawable.rc_unread_count_bg);
                        } else {
                            holder.unReadMsgCount.setVisibility(View.GONE);
                            holder.unReadMsgCountIcon.setImageResource(drawable.rc_unread_remind_list_count);
                        }
                    } else {
                        holder.unReadMsgCountIcon.setVisibility(View.GONE);
                        holder.unReadMsgCount.setVisibility(View.GONE);
                    }

                    holder.rightImageLayout.setVisibility(View.GONE);
                } else if (tag.portraitPosition() == 2) {
                    holder.rightImageLayout.setVisibility(View.VISIBLE);
                    holder.rightImageLayout.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            if (ConversationListAdapter.this.mOnPortraitItemClick != null) {
                                ConversationListAdapter.this.mOnPortraitItemClick.onPortraitItemClick(v, data);
                            }

                        }
                    });
                    holder.rightImageLayout.setOnLongClickListener(new OnLongClickListener() {
                        public boolean onLongClick(View v) {
                            if (ConversationListAdapter.this.mOnPortraitItemClick != null) {
                                ConversationListAdapter.this.mOnPortraitItemClick.onPortraitItemLongClick(v, data);
                            }

                            return true;
                        }
                    });
                    if (data.getConversationType().equals(ConversationType.GROUP)) {
                        if(data.getUIConversationTitle().contains("-心脏")){
                            defaultId1 = R.drawable.msg_1index_team1;
                        }else if(data.getUIConversationTitle().contains("-肝脏")){
                            defaultId1 = R.drawable.msg_1index_team2;
                        }else if(data.getUIConversationTitle().contains("-肺")){
                            defaultId1 = R.drawable.msg_1index_team3;
                        }else if(data.getUIConversationTitle().contains("-肾脏")){
                            defaultId1 = R.drawable.msg_1index_team4;
                        }else if(data.getUIConversationTitle().contains("-胰脏")){
                            defaultId1 = R.drawable.msg_1index_team5;
                        }else if(data.getUIConversationTitle().contains("-眼角膜")){
                            defaultId1 = R.drawable.msg_1index_team6;
                        }
                        else{
                            defaultId1 =  R.drawable.msg_1index_team;
                        }

                    } else if (data.getConversationType().equals(ConversationType.DISCUSSION)) {
                        defaultId1 = drawable.rc_default_discussion_portrait;
                    } else {
                        defaultId1 = R.drawable.msg_2list_linkman;
                    }

                    if (data.getConversationGatherState()) {
                        holder.rightImageView.setAvatar((String) null, defaultId1);
                    } else if (data.getIconUrl() != null) {
                        holder.rightImageView.setAvatar(data.getIconUrl().toString(), defaultId1);
                    } else {
                        holder.rightImageView.setAvatar((String) null, defaultId1);
                    }

                    if (data.getUnReadMessageCount() > 0) {
                        holder.unReadMsgCountRightIcon.setVisibility(View.VISIBLE);
                        if (data.getUnReadType().equals(UnreadRemindType.REMIND_WITH_COUNTING)) {
                            holder.unReadMsgCount.setVisibility(View.VISIBLE);
                            if (data.getUnReadMessageCount() > 99) {
                                holder.unReadMsgCountRight.setText(this.mContext.getResources().getString(string.rc_message_unread_count));
                            } else {
                                holder.unReadMsgCountRight.setText(Integer.toString(data.getUnReadMessageCount()));
                            }

                            holder.unReadMsgCountRightIcon.setImageResource(drawable.rc_unread_count_bg);
                        } else {
                            holder.unReadMsgCount.setVisibility(View.GONE);
                            holder.unReadMsgCountRightIcon.setImageResource(drawable.rc_unread_remind_without_count);
                        }
                    } else {
                        holder.unReadMsgCountIcon.setVisibility(View.GONE);
                        holder.unReadMsgCount.setVisibility(View.GONE);
                    }

                    holder.leftImageLayout.setVisibility(View.GONE);
                } else {
                    if (tag.portraitPosition() != 3) {
                        throw new IllegalArgumentException("the portrait position is wrong!");
                    }

                    holder.rightImageLayout.setVisibility(View.GONE);
                    holder.leftImageLayout.setVisibility(View.GONE);
                }

            }
        }
    }

    public void setOnPortraitItemClick(ConversationListAdapter.OnPortraitItemClick onPortraitItemClick) {
        this.mOnPortraitItemClick = onPortraitItemClick;
    }

    public interface OnPortraitItemClick {
        void onPortraitItemClick(View var1, UIConversation var2);

        boolean onPortraitItemLongClick(View var1, UIConversation var2);
    }

    class ViewHolder {
        View layout;
        View leftImageLayout;
        View rightImageLayout;
        AsyncImageView leftImageView;
        TextView unReadMsgCount;
        ImageView unReadMsgCountIcon;
        AsyncImageView rightImageView;
        TextView unReadMsgCountRight;
        ImageView unReadMsgCountRightIcon;
        ProviderContainerView contentView;

        ViewHolder() {
        }
    }
    class TitleViewHolder {
        TextView title;
        TextView time;
        TextView content;
        ImageView notificationBlockImage;
        ImageView readStatus;

        TitleViewHolder() {
        }
    }
}
