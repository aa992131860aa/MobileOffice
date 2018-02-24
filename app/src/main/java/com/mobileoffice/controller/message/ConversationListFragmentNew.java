package com.mobileoffice.controller.message;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobileoffice.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.rong.common.RLog;
import io.rong.eventbus.EventBus;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.IHistoryDataResultCallback;
import io.rong.imkit.fragment.UriFragment;
import io.rong.imkit.manager.InternalModuleManager;
import io.rong.imkit.model.Event;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.utilities.OptionsPopupDialog;

import io.rong.imlib.RongIMClient;

import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Discussion;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.PublicServiceProfile;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ReadReceiptMessage;
import io.rong.push.RongPushClient;

public class ConversationListFragmentNew extends UriFragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, ConversationListAdapter.OnPortraitItemClick {
    private String TAG = "ConversationListFragment";
    private List<ConversationConfig> mConversationsConfig;
    private ConversationListFragmentNew mThis;
    private ConversationListAdapter mAdapter;
    private ListView mList;
    private LinearLayout mNotificationBar;
    private ImageView mNotificationBarImage;
    private TextView mNotificationBarText;
    private boolean isShowWithoutConnected = false;

    public ConversationListFragmentNew() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThis = this;
        TAG = getClass().getSimpleName();
        mConversationsConfig = new ArrayList();
        EventBus.getDefault().register(this);
        InternalModuleManager.getInstance().onLoaded();
    }

    protected void initFragment(Uri uri) {
        RLog.d(TAG, "initFragment " + uri);
        Conversation.ConversationType[] defConversationType = new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE, Conversation.ConversationType.GROUP, Conversation.ConversationType.DISCUSSION, Conversation.ConversationType.SYSTEM, Conversation.ConversationType.CUSTOMER_SERVICE, Conversation.ConversationType.CHATROOM, Conversation.ConversationType.PUBLIC_SERVICE, Conversation.ConversationType.APP_PUBLIC_SERVICE};
        Conversation.ConversationType[] type = defConversationType;
        int arr$ = defConversationType.length;

        int len$;
        for(len$ = 0; len$ < arr$; ++len$) {
            Conversation.ConversationType i$ = type[len$];
            if(uri.getQueryParameter(i$.getName()) != null) {
                ConversationConfig conversationType = new ConversationConfig();
                conversationType.conversationType = i$;
                conversationType.isGathered = uri.getQueryParameter(i$.getName()).equals("true");
                mConversationsConfig.add(conversationType);
            }
        }

        if(mConversationsConfig.size() == 0) {
            String var9 = uri.getQueryParameter("type");
            Conversation.ConversationType[] var10 = defConversationType;
            len$ = defConversationType.length;

            for(int var11 = 0; var11 < len$; ++var11) {
                Conversation.ConversationType var12 = var10[var11];
                if(var12.getName().equals(var9)) {
                    ConversationConfig config = new ConversationConfig();
                    config.conversationType = var12;
                    config.isGathered = false;
                    mConversationsConfig.add(config);
                    break;
                }
            }
        }

        mAdapter.clear();
        if(RongIMClient.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED)) {
            RLog.d(TAG, "RongCloud haven\'t been connected yet, so the conversation list display blank !!!");
            isShowWithoutConnected = true;
        } else {
            getConversationList(getConfigConversationTypes());
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rc_fr_conversationlist_app, container, false);
        mNotificationBar = (LinearLayout)findViewById(view, R.id.rc_status_bar);
        mNotificationBar.setVisibility(View.GONE);
        mNotificationBarImage = (ImageView)findViewById(view, R.id.rc_status_bar_image);
        mNotificationBarText = (TextView)findViewById(view, R.id.rc_status_bar_text);
        View emptyView = findViewById(view, R.id.rc_conversation_list_empty_layout);
        TextView emptyText = (TextView)findViewById(view, R.id.rc_empty_tv);
        emptyText.setText(getActivity().getResources().getString(R.string.rc_conversation_list_empty_prompt));
        mList = (ListView)findViewById(view, R.id.rc_list);
        mList.setEmptyView(emptyView);
        mList.setOnItemClickListener(this);
        mList.setOnItemLongClickListener(this);
        if(mAdapter == null) {
            mAdapter = onResolveAdapter(getActivity());
        }

        mAdapter.setOnPortraitItemClick(this);
        mList.setAdapter(mAdapter);
        return view;
    }

    public void onResume() {
        super.onResume();
        RLog.d(TAG, "onResume " + RongIM.getInstance().getCurrentConnectionStatus());
        RongPushClient.clearAllPushNotifications(getActivity());
        setNotificationBarVisibility(RongIM.getInstance().getCurrentConnectionStatus());
    }

    private void getConversationList(Conversation.ConversationType[] conversationTypes) {
        getConversationList(conversationTypes, new IHistoryDataResultCallback<List<Conversation>>() {
            public void onResult(List<Conversation> data) {
                if(data != null && data.size() > 0) {
                    makeUiConversationList(data);
                    mAdapter.notifyDataSetChanged();
                } else {
                    RLog.w(TAG, "getConversationList return null " + RongIMClient.getInstance().getCurrentConnectionStatus());
                    isShowWithoutConnected = true;
                }

            }

            public void onError() {
            }
        });
    }

    public void getConversationList(Conversation.ConversationType[] conversationTypes, final IHistoryDataResultCallback<List<Conversation>> callback) {
        RongIMClient.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
            public void onSuccess(List<Conversation> conversations) {
                if(callback != null) {
                    callback.onResult(conversations);
                }

            }

            public void onError(RongIMClient.ErrorCode e) {
                if(callback != null) {
                    callback.onError();
                }

            }
        }, conversationTypes);
    }

    public void focusUnreadItem() {
        int first = mList.getFirstVisiblePosition();
        int last = mList.getLastVisiblePosition();
        int visibleCount = last - first + 1;
        int count = mList.getCount();
        if(visibleCount < count) {
            int index;
            if(last < count - 1) {
                index = first + 1;
            } else {
                index = 0;
            }

            if(!selectNextUnReadItem(index, count)) {
                selectNextUnReadItem(0, count);
            }
        }

    }

    private boolean selectNextUnReadItem(int startIndex, int totalCount) {
        int index = -1;

        for(int i = startIndex; i < totalCount; ++i) {
            UIConversation uiConversation = (UIConversation)mAdapter.getItem(i);
            if(uiConversation.getUnReadMessageCount() > 0) {
                index = i;
                break;
            }
        }

        if(index >= 0 && index < totalCount) {
            mList.setSelection(index);
            return true;
        } else {
            return false;
        }
    }
    String content = null;
    private void setNotificationBarVisibility(RongIMClient.ConnectionStatusListener.ConnectionStatus status) {
        if(!getResources().getBoolean(R.bool.rc_is_show_warning_notification)) {
            RLog.e(TAG, "rc_is_show_warning_notification is disabled.");
        } else {

            if(status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.NETWORK_UNAVAILABLE)) {
                content = getResources().getString(R.string.rc_notice_network_unavailable);
            } else if(status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT)) {
                content = getResources().getString(R.string.rc_notice_tick);
            } else if(status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)) {
                mNotificationBar.setVisibility(View.GONE);
            } else if(status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED)) {
                content = getResources().getString(R.string.rc_notice_disconnect);
            } else if(status.equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTING)) {
                content = getResources().getString(R.string.rc_notice_connecting);
            }

            if(content != null) {
                if(mNotificationBar.getVisibility() == View.GONE) {
                    getHandler().postDelayed(new Runnable() {
                        public void run() {
                            if(!RongIMClient.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)) {
                                mNotificationBar.setVisibility(View.VISIBLE);
                                mNotificationBarText.setText(content);
                                if(RongIMClient.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTING)) {
                                    mNotificationBarImage.setImageResource(R.drawable.rc_notification_connecting_animated);
                                } else {
                                    mNotificationBarImage.setImageResource(R.drawable.rc_notification_network_available);
                                }
                            }

                        }
                    }, 4000L);
                } else {
                    mNotificationBarText.setText(content);
                    if(RongIMClient.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTING)) {
                        mNotificationBarImage.setImageResource(R.drawable.rc_notification_connecting_animated);
                    } else {
                        mNotificationBarImage.setImageResource(R.drawable.rc_notification_network_available);
                    }
                }
            }

        }
    }

    public boolean onBackPressed() {
        return false;
    }

    /** @deprecated */
    @Deprecated
    public void setAdapter(ConversationListAdapter adapter) {
        mAdapter = adapter;
        if(mList != null) {
            mList.setAdapter(adapter);
        }

    }

    public ConversationListAdapter onResolveAdapter(Context context) {
        mAdapter = new ConversationListAdapter(context);
        return mAdapter;
    }

    public void onEventMainThread(Event.SyncReadStatusEvent event) {
        Conversation.ConversationType conversationType = event.getConversationType();
        String targetId = event.getTargetId();
        RLog.d(TAG, "SyncReadStatusEvent " + conversationType + " " + targetId);
        int first = mList.getFirstVisiblePosition();
        int last = mList.getLastVisiblePosition();
        int position;
        if(getGatherState(conversationType)) {
            position = mAdapter.findGatheredItem(conversationType);
        } else {
            position = mAdapter.findPosition(conversationType, targetId);
        }

        if(position >= 0) {
            UIConversation uiConversation = (UIConversation)mAdapter.getItem(position);
            uiConversation.clearUnRead(conversationType, targetId);
            if(position >= first && position <= last) {
                mAdapter.getView(position, mList.getChildAt(position - mList.getFirstVisiblePosition()), mList);
            }
        }

    }

    public void onEventMainThread(Event.ReadReceiptEvent event) {
        Conversation.ConversationType conversationType = event.getMessage().getConversationType();
        String targetId = event.getMessage().getTargetId();
        int originalIndex = mAdapter.findPosition(conversationType, targetId);
        boolean gatherState = getGatherState(conversationType);
        if(!gatherState && originalIndex >= 0) {
            UIConversation conversation = (UIConversation)mAdapter.getItem(originalIndex);
            ReadReceiptMessage content = (ReadReceiptMessage)event.getMessage().getContent();
            if(content.getLastMessageSendTime() >= conversation.getUIConversationTime() && conversation.getConversationSenderId().equals(RongIMClient.getInstance().getCurrentUserId())) {
                conversation.setSentStatus(Message.SentStatus.READ);
                mAdapter.getView(originalIndex, mList.getChildAt(originalIndex - mList.getFirstVisiblePosition()), mList);
            }
        }

    }

    public void onEventMainThread(Event.AudioListenedEvent event) {
        Message message = event.getMessage();
        Conversation.ConversationType conversationType = message.getConversationType();
        String targetId = message.getTargetId();
        RLog.d(TAG, "Message: " + message.getObjectName() + " " + conversationType + " " + message.getSentStatus());
        if(isConfigured(conversationType)) {
            boolean gathered = getGatherState(conversationType);
            int position = gathered?mAdapter.findGatheredItem(conversationType):mAdapter.findPosition(conversationType, targetId);
            if(position >= 0) {
                UIConversation uiConversation = (UIConversation)mAdapter.getItem(position);
                if(message.getMessageId() == uiConversation.getLatestMessageId()) {
                    uiConversation.updateConversation(message, gathered);
                    mAdapter.getView(position, mList.getChildAt(position - mList.getFirstVisiblePosition()), mList);
                }
            }
        }

    }

    public boolean shouldUpdateConversation(Message message, int left) {
        return true;
    }

    public void onEventMainThread(Event.OnReceiveMessageEvent event) {
        Message message = event.getMessage();
        String targetId = message.getTargetId();
        Conversation.ConversationType conversationType = message.getConversationType();
        int first = mList.getFirstVisiblePosition();
        int last = mList.getLastVisiblePosition();
        if(isConfigured(message.getConversationType()) && shouldUpdateConversation(event.getMessage(), event.getLeft())) {
            if(message.getMessageId() > 0) {
                boolean gathered = getGatherState(conversationType);
                int position;
                if(gathered) {
                    position = mAdapter.findGatheredItem(conversationType);
                } else {
                    position = mAdapter.findPosition(conversationType, targetId);
                }

                UIConversation uiConversation;
                int index;
                if(position < 0) {
                    uiConversation = UIConversation.obtain(message, gathered);
                    index = getPosition(uiConversation);
                    mAdapter.add(uiConversation, index);
                    mAdapter.notifyDataSetChanged();
                } else {
                    uiConversation = (UIConversation)mAdapter.getItem(position);
                    if(event.getMessage().getSentTime() > uiConversation.getUIConversationTime()) {
                        uiConversation.updateConversation(message, gathered);
                        mAdapter.remove(position);
                        index = getPosition(uiConversation);
                        if(index == position) {
                            mAdapter.add(uiConversation, index);
                            if(index >= first && index <= last) {
                                mAdapter.getView(index, mList.getChildAt(index - mList.getFirstVisiblePosition()), mList);
                            }
                        } else {
                            mAdapter.add(uiConversation, index);
                            if(index >= first && index <= last) {
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        RLog.i(TAG, "ignore update message " + event.getMessage().getObjectName());
                    }
                }

                RLog.i(TAG, "conversation unread count : " + uiConversation.getUnReadMessageCount() + " " + conversationType + " " + targetId);
            }

            if(event.getLeft() == 0) {
                syncUnreadCount();
            }

            RLog.d(TAG, "OnReceiveMessageEvent: " + message.getObjectName() + " " + event.getLeft() + " " + conversationType + " " + targetId);
        }

    }

    public void onEventMainThread(Event.MessageLeftEvent event) {
        if(event.left == 0) {
            syncUnreadCount();
        }

    }

    private void syncUnreadCount() {
        if(mAdapter.getCount() > 0) {
            final int first = mList.getFirstVisiblePosition();
            final int last = mList.getLastVisiblePosition();

            for(int i = 0; i < mAdapter.getCount(); ++i) {
                final UIConversation uiConversation = (UIConversation)mAdapter.getItem(i);
                Conversation.ConversationType conversationType = uiConversation.getConversationType();
                String targetId = uiConversation.getConversationTargetId();
                final int position;
                if(getGatherState(conversationType)) {
                    position = mAdapter.findGatheredItem(conversationType);
                    RongIMClient.getInstance().getUnreadCount(new RongIMClient.ResultCallback<Integer>() {
                        public void onSuccess(Integer integer) {
                            uiConversation.setUnReadMessageCount(integer.intValue());
                            if(position >= first && position <= last) {
                                mAdapter.getView(position, mList.getChildAt(position - mList.getFirstVisiblePosition()), mList);
                            }

                        }

                        public void onError(RongIMClient.ErrorCode e) {
                        }
                    }, new Conversation.ConversationType[]{conversationType});
                } else {
                    position = mAdapter.findPosition(conversationType, targetId);
                    RongIMClient.getInstance().getUnreadCount(conversationType, targetId, new RongIMClient.ResultCallback<Integer>() {
                        public void onSuccess(Integer integer) {
                            uiConversation.setUnReadMessageCount(integer.intValue());
                            if(position >= first && position <= last) {
                                mAdapter.getView(position, mList.getChildAt(position - mList.getFirstVisiblePosition()), mList);
                            }

                        }

                        public void onError(RongIMClient.ErrorCode e) {
                        }
                    });
                }
            }
        }

    }

    public void onEventMainThread(Event.MessageRecallEvent event) {
        RLog.d(TAG, "MessageRecallEvent");
        int count = mAdapter.getCount();

        for(int i = 0; i < count; ++i) {
            UIConversation uiConversation = (UIConversation)mAdapter.getItem(i);
            if(event.getMessageId() == uiConversation.getLatestMessageId()) {
                boolean gatherState = ((UIConversation)mAdapter.getItem(i)).getConversationGatherState();
                final String targetId = ((UIConversation)mAdapter.getItem(i)).getConversationTargetId();
                if(gatherState) {
                    RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
                        public void onSuccess(List<Conversation> conversationList) {
                            if(conversationList != null && conversationList.size() > 0) {
                                UIConversation uiConversation = makeUIConversation(conversationList);
                                int oldPos = mAdapter.findPosition(uiConversation.getConversationType(), targetId);
                                if(oldPos >= 0) {
                                    mAdapter.remove(oldPos);
                                }

                                int newIndex = getPosition(uiConversation);
                                mAdapter.add(uiConversation, newIndex);
                                mAdapter.notifyDataSetChanged();
                            }

                        }

                        public void onError(RongIMClient.ErrorCode e) {
                        }
                    }, new Conversation.ConversationType[]{uiConversation.getConversationType()});
                } else {
                    RongIM.getInstance().getConversation(uiConversation.getConversationType(), uiConversation.getConversationTargetId(), new RongIMClient.ResultCallback<Conversation>() {
                        public void onSuccess(Conversation conversation) {
                            if(conversation != null) {
                                UIConversation temp = UIConversation.obtain(conversation, false);
                                int pos = mAdapter.findPosition(conversation.getConversationType(), conversation.getTargetId());
                                if(pos >= 0) {
                                    mAdapter.remove(pos);
                                }

                                int newPosition = getPosition(temp);
                                mAdapter.add(temp, newPosition);
                                mAdapter.notifyDataSetChanged();
                            }

                        }

                        public void onError(RongIMClient.ErrorCode e) {
                        }
                    });
                }
                break;
            }
        }

    }

    public void onEventMainThread(Event.RemoteMessageRecallEvent event) {
        RLog.d(TAG, "RemoteMessageRecallEvent");
        int count = mAdapter.getCount();

        for(int i = 0; i < count; ++i) {
            UIConversation uiConversation = (UIConversation)mAdapter.getItem(i);
            if(event.getMessageId() == uiConversation.getLatestMessageId()) {
                boolean gatherState = uiConversation.getConversationGatherState();
                final String targetId = ((UIConversation)mAdapter.getItem(i)).getConversationTargetId();
                if(gatherState) {
                    RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
                        public void onSuccess(List<Conversation> conversationList) {
                            if(conversationList != null && conversationList.size() > 0) {
                                UIConversation uiConversation = makeUIConversation(conversationList);
                                int oldPos = mAdapter.findPosition(uiConversation.getConversationType(), targetId);
                                if(oldPos >= 0) {
                                    mAdapter.remove(oldPos);
                                }

                                int newIndex = getPosition(uiConversation);
                                mAdapter.add(uiConversation, newIndex);
                                mAdapter.notifyDataSetChanged();
                            }

                        }

                        public void onError(RongIMClient.ErrorCode e) {
                        }
                    }, new Conversation.ConversationType[]{((UIConversation)mAdapter.getItem(i)).getConversationType()});
                } else {
                    RongIM.getInstance().getConversation(uiConversation.getConversationType(), uiConversation.getConversationTargetId(), new RongIMClient.ResultCallback<Conversation>() {
                        public void onSuccess(Conversation conversation) {
                            if(conversation != null) {
                                UIConversation temp = UIConversation.obtain(conversation, false);
                                int pos = mAdapter.findPosition(conversation.getConversationType(), conversation.getTargetId());
                                if(pos >= 0) {
                                    mAdapter.remove(pos);
                                }

                                int newPosition = getPosition(temp);
                                mAdapter.add(temp, newPosition);
                                mAdapter.notifyDataSetChanged();
                            }

                        }

                        public void onError(RongIMClient.ErrorCode e) {
                        }
                    });
                }
                break;
            }
        }

    }

    public void onEventMainThread(Message message) {
        Conversation.ConversationType conversationType = message.getConversationType();
        String targetId = message.getTargetId();
        RLog.d(TAG, "Message: " + message.getObjectName() + " " + message.getMessageId() + " " + conversationType + " " + message.getSentStatus());
        boolean gathered = getGatherState(conversationType);
        if(isConfigured(conversationType) && message.getMessageId() > 0) {
            int position = gathered?mAdapter.findGatheredItem(conversationType):mAdapter.findPosition(conversationType, targetId);
            UIConversation uiConversation;
            int index;
            if(position < 0) {
                uiConversation = UIConversation.obtain(message, gathered);
                index = getPosition(uiConversation);
                mAdapter.add(uiConversation, index);
                mAdapter.notifyDataSetChanged();
            } else {
                uiConversation = (UIConversation)mAdapter.getItem(position);
                mAdapter.remove(position);
                uiConversation.updateConversation(message, gathered);
                index = getPosition(uiConversation);
                mAdapter.add(uiConversation, index);
                if(position == index) {
                    mAdapter.getView(index, mList.getChildAt(index - mList.getFirstVisiblePosition()), mList);
                } else {
                    mAdapter.notifyDataSetChanged();
                }
            }
        }

    }

    public void onEventMainThread(RongIMClient.ConnectionStatusListener.ConnectionStatus status) {
        RLog.d(TAG, "ConnectionStatus, " + status.toString());
        setNotificationBarVisibility(status);
    }

    public void onEventMainThread(Event.ConnectEvent event) {
        if(isShowWithoutConnected) {
            getConversationList(getConfigConversationTypes());
            isShowWithoutConnected = false;
        }

    }

    public void onEventMainThread(final Event.CreateDiscussionEvent createDiscussionEvent) {
        RLog.d(TAG, "createDiscussionEvent");
        final String targetId = createDiscussionEvent.getDiscussionId();
        if(isConfigured(Conversation.ConversationType.DISCUSSION)) {
            RongIMClient.getInstance().getConversation(Conversation.ConversationType.DISCUSSION, targetId, new RongIMClient.ResultCallback<Conversation>() {
                public void onSuccess(Conversation conversation) {
                    if(conversation != null) {
                        int position;
                        if(getGatherState(Conversation.ConversationType.DISCUSSION)) {
                            position = mAdapter.findGatheredItem(Conversation.ConversationType.DISCUSSION);
                        } else {
                            position = mAdapter.findPosition(Conversation.ConversationType.DISCUSSION, targetId);
                        }

                        conversation.setConversationTitle(createDiscussionEvent.getDiscussionName());
                        UIConversation uiConversation;
                        if(position < 0) {
                            uiConversation = UIConversation.obtain(conversation, getGatherState(Conversation.ConversationType.DISCUSSION));
                            int index = getPosition(uiConversation);
                            mAdapter.add(uiConversation, index);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            uiConversation = (UIConversation) mAdapter.getItem(position);
                            uiConversation.updateConversation(conversation, getGatherState(Conversation.ConversationType.DISCUSSION));
                            mAdapter.getView(position, mList.getChildAt(position - mList.getFirstVisiblePosition()), mList);
                        }
                    }

                }

                public void onError(RongIMClient.ErrorCode e) {
                }
            });
        }

    }

    public void onEventMainThread(final Event.DraftEvent draft) {
        Conversation.ConversationType conversationType = draft.getConversationType();
        String targetId = draft.getTargetId();
        RLog.i(TAG, "Draft : " + conversationType);
        if(isConfigured(conversationType)) {
            final boolean gathered = getGatherState(conversationType);
            final int position = gathered?mAdapter.findGatheredItem(conversationType):mAdapter.findPosition(conversationType, targetId);
            RongIMClient.getInstance().getConversation(conversationType, targetId, new RongIMClient.ResultCallback<Conversation>() {
                public void onSuccess(Conversation conversation) {
                    if(conversation != null) {
                        UIConversation uiConversation;
                        if(position < 0) {
                            if(!TextUtils.isEmpty(draft.getContent())) {
                                uiConversation = UIConversation.obtain(conversation, gathered);
                                int index = getPosition(uiConversation);
                                mAdapter.add(uiConversation, index);
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            uiConversation = (UIConversation) mAdapter.getItem(position);
                            if(TextUtils.isEmpty(draft.getContent()) && !TextUtils.isEmpty(uiConversation.getDraft()) || !TextUtils.isEmpty(draft.getContent()) && TextUtils.isEmpty(uiConversation.getDraft()) || !TextUtils.isEmpty(draft.getContent()) && !TextUtils.isEmpty(uiConversation.getDraft()) && !draft.getContent().equals(uiConversation.getDraft())) {
                                uiConversation.updateConversation(conversation, gathered);
                                mAdapter.getView(position, mList.getChildAt(position - mList.getFirstVisiblePosition()), mList);
                            }
                        }
                    }

                }

                public void onError(RongIMClient.ErrorCode e) {
                }
            });
        }

    }

    public void onEventMainThread(Group groupInfo) {
        RLog.d(TAG, "Group: " + groupInfo.getName() + " " + groupInfo.getId());
        int count = mAdapter.getCount();
        if(groupInfo.getName() != null) {
            int last = mList.getLastVisiblePosition();
            int first = mList.getFirstVisiblePosition();

            for(int i = 0; i < count; ++i) {
                UIConversation uiConversation = (UIConversation)mAdapter.getItem(i);
                uiConversation.updateConversation(groupInfo);
                if(i >= first && i <= last) {
                    mAdapter.getView(i, mList.getChildAt(i - first), mList);
                }
            }

        }
    }

    public void onEventMainThread(Discussion discussion) {
        RLog.d(TAG, "Discussion: " + discussion.getName() + " " + discussion.getId());
        if(isConfigured(Conversation.ConversationType.DISCUSSION)) {
            int last = mList.getLastVisiblePosition();
            int first = mList.getFirstVisiblePosition();
            int position;
            if(getGatherState(Conversation.ConversationType.DISCUSSION)) {
                position = mAdapter.findGatheredItem(Conversation.ConversationType.DISCUSSION);
            } else {
                position = mAdapter.findPosition(Conversation.ConversationType.DISCUSSION, discussion.getId());
            }

            if(position >= 0) {
                for(int i = 0; i == position; ++i) {
                    UIConversation uiConversation = (UIConversation)mAdapter.getItem(i);
                    uiConversation.updateConversation(discussion);
                    if(i >= first && i <= last) {
                        mAdapter.getView(i, mList.getChildAt(i - mList.getFirstVisiblePosition()), mList);
                    }
                }
            }
        }

    }

    public void onEventMainThread(GroupUserInfo groupUserInfo) {
        RLog.d(TAG, "GroupUserInfo " + groupUserInfo.getGroupId() + " " + groupUserInfo.getUserId() + " " + groupUserInfo.getNickname());
        if(groupUserInfo.getNickname() != null && groupUserInfo.getGroupId() != null) {
            int count = mAdapter.getCount();
            int last = mList.getLastVisiblePosition();
            int first = mList.getFirstVisiblePosition();

            for(int i = 0; i < count; ++i) {
                UIConversation uiConversation = (UIConversation)mAdapter.getItem(i);
                if(!getGatherState(Conversation.ConversationType.GROUP) && uiConversation.getConversationTargetId().equals(groupUserInfo.getGroupId()) && uiConversation.getConversationSenderId().equals(groupUserInfo.getUserId())) {
                    uiConversation.updateConversation(groupUserInfo);
                    if(i >= first && i <= last) {
                        mAdapter.getView(i, mList.getChildAt(i - mList.getFirstVisiblePosition()), mList);
                    }
                }
            }

        }
    }

    public void onEventMainThread(UserInfo userInfo) {
        RLog.i(TAG, "UserInfo " + userInfo.getUserId() + " " + userInfo.getName());
        int count = mAdapter.getCount();
        int last = mList.getLastVisiblePosition();
        int first = mList.getFirstVisiblePosition();

        for(int i = 0; i < count && userInfo.getName() != null; ++i) {
            UIConversation uiConversation = (UIConversation)mAdapter.getItem(i);
            if(uiConversation.hasNickname(userInfo.getUserId())) {
                RLog.i(TAG, "has nick name");
            } else {
                uiConversation.updateConversation(userInfo);
                if(i >= first && i <= last) {
                    mAdapter.getView(i, mList.getChildAt(i - first), mList);
                }
            }
        }

    }

    public void onEventMainThread(PublicServiceProfile profile) {
        RLog.d(TAG, "PublicServiceProfile");
        int count = mAdapter.getCount();
        boolean gatherState = getGatherState(profile.getConversationType());

        for(int i = 0; i < count; ++i) {
            UIConversation uiConversation = (UIConversation)mAdapter.getItem(i);
            if(uiConversation.getConversationType().equals(profile.getConversationType()) && uiConversation.getConversationTargetId().equals(profile.getTargetId()) && !gatherState) {
                uiConversation.setUIConversationTitle(profile.getName());
                uiConversation.setIconUrl(profile.getPortraitUri());
                mAdapter.getView(i, mList.getChildAt(i - mList.getFirstVisiblePosition()), mList);
                break;
            }
        }

    }

    public void onEventMainThread(Event.PublicServiceFollowableEvent event) {
        RLog.d(TAG, "PublicServiceFollowableEvent");
        if(!event.isFollow()) {
            int originalIndex = mAdapter.findPosition(event.getConversationType(), event.getTargetId());
            if(originalIndex >= 0) {
                mAdapter.remove(originalIndex);
                mAdapter.notifyDataSetChanged();
            }
        }

    }

    public void onEventMainThread(Event.ConversationUnreadEvent unreadEvent) {
        RLog.d(TAG, "ConversationUnreadEvent");
        Conversation.ConversationType conversationType = unreadEvent.getType();
        String targetId = unreadEvent.getTargetId();
        int position = getGatherState(conversationType)?mAdapter.findGatheredItem(conversationType):mAdapter.findPosition(conversationType, targetId);
        if(position >= 0) {
            int first = mList.getFirstVisiblePosition();
            int last = mList.getLastVisiblePosition();
            UIConversation uiConversation = (UIConversation)mAdapter.getItem(position);
            uiConversation.clearUnRead(conversationType, targetId);
            if(position >= first && position <= last) {
                mAdapter.getView(position, mList.getChildAt(position - mList.getFirstVisiblePosition()), mList);
            }
        }

    }

    public void onEventMainThread(Event.ConversationTopEvent setTopEvent) {
        RLog.d(TAG, "ConversationTopEvent");
        Conversation.ConversationType conversationType = setTopEvent.getConversationType();
        String targetId = setTopEvent.getTargetId();
        int position = mAdapter.findPosition(conversationType, targetId);
        if(position >= 0 && !getGatherState(conversationType)) {
            UIConversation uiConversation = (UIConversation)mAdapter.getItem(position);
            if(uiConversation.isTop() != setTopEvent.isTop()) {
                uiConversation.setTop(!uiConversation.isTop());
                mAdapter.remove(position);
                int index = getPosition(uiConversation);
                mAdapter.add(uiConversation, index);
                if(index == position) {
                    mAdapter.getView(index, mList.getChildAt(index - mList.getFirstVisiblePosition()), mList);
                } else {
                    mAdapter.notifyDataSetChanged();
                }
            }
        }

    }

    public void onEventMainThread(Event.ConversationRemoveEvent removeEvent) {
        RLog.d(TAG, "ConversationRemoveEvent");
        Conversation.ConversationType conversationType = removeEvent.getType();
        removeConversation(conversationType, removeEvent.getTargetId());
    }

    public void onEventMainThread(Event.ClearConversationEvent clearConversationEvent) {
        RLog.d(TAG, "ClearConversationEvent");
        List typeList = clearConversationEvent.getTypes();

        for(int i = mAdapter.getCount() - 1; i >= 0; --i) {
            if(typeList.indexOf(((UIConversation)mAdapter.getItem(i)).getConversationType()) >= 0) {
                mAdapter.remove(i);
            }
        }

        mAdapter.notifyDataSetChanged();
    }

    public void onEventMainThread(Event.MessageDeleteEvent event) {
        RLog.d(TAG, "MessageDeleteEvent");
        int count = mAdapter.getCount();

        for(int i = 0; i < count; ++i) {
            if(event.getMessageIds().contains(Integer.valueOf(((UIConversation)mAdapter.getItem(i)).getLatestMessageId()))) {
                boolean gatherState = ((UIConversation)mAdapter.getItem(i)).getConversationGatherState();
                final String targetId = ((UIConversation)mAdapter.getItem(i)).getConversationTargetId();
                if(gatherState) {
                    RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
                        public void onSuccess(List<Conversation> conversationList) {
                            if(conversationList != null && conversationList.size() != 0) {
                                UIConversation uiConversation = makeUIConversation(conversationList);
                                int oldPos = mAdapter.findPosition(uiConversation.getConversationType(), targetId);
                                if(oldPos >= 0) {
                                    mAdapter.remove(oldPos);
                                }

                                int newIndex = getPosition(uiConversation);
                                mAdapter.add(uiConversation, newIndex);
                                mAdapter.notifyDataSetChanged();
                            }
                        }

                        public void onError(RongIMClient.ErrorCode e) {
                        }
                    }, new Conversation.ConversationType[]{((UIConversation)mAdapter.getItem(i)).getConversationType()});
                } else {
                    RongIM.getInstance().getConversation(((UIConversation)mAdapter.getItem(i)).getConversationType(), ((UIConversation)mAdapter.getItem(i)).getConversationTargetId(), new RongIMClient.ResultCallback<Conversation>() {
                        public void onSuccess(Conversation conversation) {
                            if(conversation == null) {
                                RLog.d(TAG, "onEventMainThread getConversation : onSuccess, conversation = null");
                            } else {
                                UIConversation temp = UIConversation.obtain(conversation, false);
                                int pos = mAdapter.findPosition(conversation.getConversationType(), conversation.getTargetId());
                                if(pos >= 0) {
                                    mAdapter.remove(pos);
                                }

                                int newIndex = getPosition(temp);
                                mAdapter.add(temp, newIndex);
                                mAdapter.notifyDataSetChanged();
                            }
                        }

                        public void onError(RongIMClient.ErrorCode e) {
                        }
                    });
                }
                break;
            }
        }

    }

    public void onEventMainThread(Event.ConversationNotificationEvent notificationEvent) {
        int originalIndex = mAdapter.findPosition(notificationEvent.getConversationType(), notificationEvent.getTargetId());
        if(originalIndex >= 0) {
            mAdapter.getView(originalIndex, mList.getChildAt(originalIndex - mList.getFirstVisiblePosition()), mList);
        }

    }

    public void onEventMainThread(Event.MessagesClearEvent clearMessagesEvent) {
        RLog.d(TAG, "MessagesClearEvent");
        Conversation.ConversationType conversationType = clearMessagesEvent.getType();
        String targetId = clearMessagesEvent.getTargetId();
        int position = getGatherState(conversationType)?mAdapter.findGatheredItem(conversationType):mAdapter.findPosition(conversationType, targetId);
        if(position >= 0) {
            UIConversation uiConversation = (UIConversation)mAdapter.getItem(position);
            uiConversation.clearLastMessage();
            mAdapter.getView(position, mList.getChildAt(position - mList.getFirstVisiblePosition()), mList);
        }

    }

    public void onEventMainThread(Event.OnMessageSendErrorEvent sendErrorEvent) {
        Message message = sendErrorEvent.getMessage();
        Conversation.ConversationType conversationType = message.getConversationType();
        String targetId = message.getTargetId();
        if(isConfigured(conversationType)) {
            int first = mList.getFirstVisiblePosition();
            int last = mList.getLastVisiblePosition();
            boolean gathered = getGatherState(conversationType);
            int index = gathered?mAdapter.findGatheredItem(conversationType):mAdapter.findPosition(conversationType, targetId);
            if(index >= 0) {
                UIConversation uiConversation = (UIConversation)mAdapter.getItem(index);
                message.setSentStatus(Message.SentStatus.FAILED);
                uiConversation.updateConversation(message, gathered);
                if(index >= first && index <= last) {
                    mAdapter.getView(index, mList.getChildAt(index - mList.getFirstVisiblePosition()), mList);
                }
            }
        }

    }

    public void onEventMainThread(Event.QuitDiscussionEvent event) {
        RLog.d(TAG, "QuitDiscussionEvent");
        removeConversation(Conversation.ConversationType.DISCUSSION, event.getDiscussionId());
    }

    public void onEventMainThread(Event.QuitGroupEvent event) {
        RLog.d(TAG, "QuitGroupEvent");
        removeConversation(Conversation.ConversationType.GROUP, event.getGroupId());
    }

    private void removeConversation(final Conversation.ConversationType conversationType, String targetId) {
        boolean gathered = getGatherState(conversationType);
        int index;
        if(gathered) {
            index = mAdapter.findGatheredItem(conversationType);
            if(index >= 0) {
                RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
                    public void onSuccess(List<Conversation> conversationList) {
                        int oldPos = mAdapter.findGatheredItem(conversationType);
                        if(oldPos >= 0) {
                            mAdapter.remove(oldPos);
                            if(conversationList != null && conversationList.size() > 0) {
                                UIConversation uiConversation = makeUIConversation(conversationList);
                                int newIndex = getPosition(uiConversation);
                                mAdapter.add(uiConversation, newIndex);
                            }

                            mAdapter.notifyDataSetChanged();
                        }

                    }

                    public void onError(RongIMClient.ErrorCode e) {
                    }
                }, new Conversation.ConversationType[]{conversationType});
            }
        } else {
            index = mAdapter.findPosition(conversationType, targetId);
            if(index >= 0) {
                mAdapter.remove(index);
                mAdapter.notifyDataSetChanged();
            }
        }

    }

    public void onPortraitItemClick(View v, UIConversation data) {
        Conversation.ConversationType type = data.getConversationType();
        if(getGatherState(type)) {
            RongIM.getInstance().startSubConversationList(getActivity(), type);
        } else {
            if(RongContext.getInstance().getConversationListBehaviorListener() != null) {
                boolean isDefault = RongContext.getInstance().getConversationListBehaviorListener().onConversationPortraitClick(getActivity(), type, data.getConversationTargetId());
                if(isDefault) {
                    return;
                }
            }

            data.setUnReadMessageCount(0);
            RongIM.getInstance().startConversation(getActivity(), type, data.getConversationTargetId(), data.getUIConversationTitle());
        }

    }

    public boolean onPortraitItemLongClick(View v, UIConversation data) {
        Conversation.ConversationType type = data.getConversationType();
        if(RongContext.getInstance().getConversationListBehaviorListener() != null) {
            boolean isDealt = RongContext.getInstance().getConversationListBehaviorListener().onConversationPortraitLongClick(getActivity(), type, data.getConversationTargetId());
            if(isDealt) {
                return true;
            }
        }

        if(!getGatherState(type)) {
            buildMultiDialog(data);
            return true;
        } else {
            buildSingleDialog(data);
            return true;
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UIConversation uiConversation = (UIConversation)mAdapter.getItem(position);
        Conversation.ConversationType conversationType = uiConversation.getConversationType();
        if(getGatherState(conversationType)) {
            RongIM.getInstance().startSubConversationList(getActivity(), conversationType);
        } else {
            if(RongContext.getInstance().getConversationListBehaviorListener() != null && RongContext.getInstance().getConversationListBehaviorListener().onConversationClick(getActivity(), view, uiConversation)) {
                return;
            }

            uiConversation.setUnReadMessageCount(0);
            RongIM.getInstance().startConversation(getActivity(), conversationType, uiConversation.getConversationTargetId(), uiConversation.getUIConversationTitle());
        }

    }

    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        UIConversation uiConversation = (UIConversation)mAdapter.getItem(position);
        if(RongContext.getInstance().getConversationListBehaviorListener() != null) {
            boolean isDealt = RongContext.getInstance().getConversationListBehaviorListener().onConversationLongClick(getActivity(), view, uiConversation);
            if(isDealt) {
                return true;
            }
        }

        if(!getGatherState(uiConversation.getConversationType())) {
            buildMultiDialog(uiConversation);
            return true;
        } else {
            buildSingleDialog(uiConversation);
            return true;
        }
    }

    private void buildMultiDialog(final UIConversation uiConversation) {
        String[] items = new String[2];
        if(uiConversation.isTop()) {
            items[0] = RongContext.getInstance().getString(io.rong.imkit.R.string.rc_conversation_list_dialog_cancel_top);
        } else {
            items[0] = RongContext.getInstance().getString(io.rong.imkit.R.string.rc_conversation_list_dialog_set_top);
        }

        items[1] = RongContext.getInstance().getString(io.rong.imkit.R.string.rc_conversation_list_dialog_remove);
        OptionsPopupDialog.newInstance(getActivity(), items).setOptionsPopupDialogListener(new OptionsPopupDialog.OnOptionsItemClickedListener() {
            public void onOptionsItemClicked(int which) {
                if(which == 0) {
                    RongIM.getInstance().setConversationToTop(uiConversation.getConversationType(), uiConversation.getConversationTargetId(), !uiConversation.isTop(), new RongIMClient.ResultCallback<Boolean>() {
                        public void onSuccess(Boolean aBoolean) {
                            if(uiConversation.isTop()) {
                                Toast.makeText(RongContext.getInstance(), getString(io.rong.imkit.R.string.rc_conversation_list_popup_cancel_top), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RongContext.getInstance(), getString(io.rong.imkit.R.string.rc_conversation_list_dialog_set_top), Toast.LENGTH_SHORT).show();
                            }

                        }

                        public void onError(RongIMClient.ErrorCode e) {
                        }
                    });
                } else if(which == 1) {
                    RongIM.getInstance().removeConversation(uiConversation.getConversationType(), uiConversation.getConversationTargetId(), (RongIMClient.ResultCallback)null);
                }

            }
        }).show();
    }

    private void buildSingleDialog(final UIConversation uiConversation) {
        String[] items = new String[]{RongContext.getInstance().getString(io.rong.imkit.R.string.rc_conversation_list_dialog_remove)};
        OptionsPopupDialog.newInstance(getActivity(), items).setOptionsPopupDialogListener(new OptionsPopupDialog.OnOptionsItemClickedListener() {
            public void onOptionsItemClicked(int which) {
                RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
                    public void onSuccess(List<Conversation> conversations) {
                        if(conversations != null && conversations.size() > 0) {
                            Iterator i$ = conversations.iterator();

                            while(i$.hasNext()) {
                                Conversation conversation = (Conversation)i$.next();
                                RongIMClient.getInstance().removeConversation(conversation.getConversationType(), conversation.getTargetId(), (RongIMClient.ResultCallback)null);
                            }
                        }

                    }

                    public void onError(RongIMClient.ErrorCode errorCode) {
                    }
                }, new Conversation.ConversationType[]{uiConversation.getConversationType()});
                int position = mAdapter.findGatheredItem(uiConversation.getConversationType());
                mAdapter.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        }).show();
    }

    private void makeUiConversationList(List<Conversation> conversationList) {
        Iterator i$ = conversationList.iterator();

        while(i$.hasNext()) {
            Conversation conversation = (Conversation)i$.next();
            Conversation.ConversationType conversationType = conversation.getConversationType();
            String targetId = conversation.getTargetId();
            boolean gatherState = getGatherState(conversationType);
            UIConversation uiConversation;
            int originalIndex;
            if(gatherState) {
                originalIndex = mAdapter.findGatheredItem(conversationType);
                if(originalIndex >= 0) {
                    uiConversation = (UIConversation)mAdapter.getItem(originalIndex);
                    uiConversation.updateConversation(conversation, true);
                } else {
                    uiConversation = UIConversation.obtain(conversation, true);
                    mAdapter.add(uiConversation);
                }
            } else {
                originalIndex = mAdapter.findPosition(conversationType, targetId);
                if(originalIndex < 0) {
                    uiConversation = UIConversation.obtain(conversation, false);
                    mAdapter.add(uiConversation);
                } else {
                    uiConversation = (UIConversation)mAdapter.getItem(originalIndex);
                    uiConversation.setUnReadMessageCount(conversation.getUnreadMessageCount());
                }
            }
        }

    }

    private UIConversation makeUIConversation(List<Conversation> conversations) {
        int unreadCount = 0;
        boolean topFlag = false;
        boolean isMentioned = false;
        Conversation newest = (Conversation)conversations.get(0);

        Conversation conversation;
        for(Iterator uiConversation = conversations.iterator(); uiConversation.hasNext(); unreadCount += conversation.getUnreadMessageCount()) {
            conversation = (Conversation)uiConversation.next();
            if(newest.isTop()) {
                if(conversation.isTop() && conversation.getSentTime() > newest.getSentTime()) {
                    newest = conversation;
                }
            } else if(conversation.isTop() || conversation.getSentTime() > newest.getSentTime()) {
                newest = conversation;
            }

            if(conversation.isTop()) {
                topFlag = true;
            }

            if(conversation.getMentionedCount() > 0) {
                isMentioned = true;
            }
        }

        UIConversation uiConversation1 = UIConversation.obtain(newest, getGatherState(newest.getConversationType()));
        uiConversation1.setUnReadMessageCount(unreadCount);
        uiConversation1.setTop(false);
        uiConversation1.setMentionedFlag(isMentioned);
        return uiConversation1;
    }

    private int getPosition(UIConversation uiConversation) {
        int count = mAdapter.getCount();
        int position = 0;

        for(int i = 0; i < count; ++i) {
            if(uiConversation.isTop()) {
                if(!((UIConversation)mAdapter.getItem(i)).isTop() || ((UIConversation)mAdapter.getItem(i)).getUIConversationTime() <= uiConversation.getUIConversationTime()) {
                    break;
                }

                ++position;
            } else {
                if(!((UIConversation)mAdapter.getItem(i)).isTop() && ((UIConversation)mAdapter.getItem(i)).getUIConversationTime() <= uiConversation.getUIConversationTime()) {
                    break;
                }

                ++position;
            }
        }

        return position;
    }

    private boolean isConfigured(Conversation.ConversationType conversationType) {
        for(int i = 0; i < mConversationsConfig.size(); ++i) {
            if(conversationType.equals(((ConversationConfig)mConversationsConfig.get(i)).conversationType)) {
                return true;
            }
        }

        return false;
    }

    public boolean getGatherState(Conversation.ConversationType conversationType) {
        Iterator i$ = mConversationsConfig.iterator();

        ConversationConfig config;
        do {
            if(!i$.hasNext()) {
                return false;
            }

            config = (ConversationConfig)i$.next();
        } while(!config.conversationType.equals(conversationType));

        return config.isGathered;
    }

    private Conversation.ConversationType[] getConfigConversationTypes() {
        Conversation.ConversationType[] conversationTypes = new Conversation.ConversationType[mConversationsConfig.size()];

        for(int i = 0; i < mConversationsConfig.size(); ++i) {
            conversationTypes[i] = ((ConversationConfig)mConversationsConfig.get(i)).conversationType;
        }

        return conversationTypes;
    }

    public void onDestroy() {
        EventBus.getDefault().unregister(mThis);
        super.onDestroyView();
    }

    private class ConversationConfig {
        Conversation.ConversationType conversationType;
        boolean isGathered;

        private ConversationConfig() {
        }
    }
}
