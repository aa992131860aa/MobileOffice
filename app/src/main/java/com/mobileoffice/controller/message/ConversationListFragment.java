package com.mobileoffice.controller.message;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mobileoffice.R;
import com.mobileoffice.controller.message.ConversationListAdapter.OnPortraitItemClick;
import com.mobileoffice.controller.message.contact.SystemMessageActivity;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.PushListJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.view.DragPointView;
import com.mobileoffice.view.MyScrollView;

import org.greenrobot.eventbus.Subscribe;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.rong.eventbus.EventBus;
import io.rong.imkit.R.bool;
import io.rong.imkit.R.drawable;
import io.rong.imkit.R.id;
import io.rong.imkit.R.string;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.IHistoryDataResultCallback;
import io.rong.imkit.fragment.UriFragment;
import io.rong.imkit.manager.InternalModuleManager;
import io.rong.imkit.model.Event.AudioListenedEvent;
import io.rong.imkit.model.Event.ClearConversationEvent;
import io.rong.imkit.model.Event.ConnectEvent;
import io.rong.imkit.model.Event.ConversationNotificationEvent;
import io.rong.imkit.model.Event.ConversationRemoveEvent;
import io.rong.imkit.model.Event.ConversationTopEvent;
import io.rong.imkit.model.Event.ConversationUnreadEvent;
import io.rong.imkit.model.Event.CreateDiscussionEvent;
import io.rong.imkit.model.Event.DraftEvent;
import io.rong.imkit.model.Event.MessageDeleteEvent;
import io.rong.imkit.model.Event.MessageLeftEvent;
import io.rong.imkit.model.Event.MessageRecallEvent;
import io.rong.imkit.model.Event.MessagesClearEvent;
import io.rong.imkit.model.Event.OnMessageSendErrorEvent;
import io.rong.imkit.model.Event.OnReceiveMessageEvent;
import io.rong.imkit.model.Event.PublicServiceFollowableEvent;
import io.rong.imkit.model.Event.QuitDiscussionEvent;
import io.rong.imkit.model.Event.QuitGroupEvent;
import io.rong.imkit.model.Event.ReadReceiptEvent;
import io.rong.imkit.model.Event.RemoteMessageRecallEvent;
import io.rong.imkit.model.Event.SyncReadStatusEvent;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.utilities.OptionsPopupDialog;
import io.rong.imkit.utilities.OptionsPopupDialog.OnOptionsItemClickedListener;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Discussion;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.Message.SentStatus;
import io.rong.imlib.model.PublicServiceProfile;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ReadReceiptMessage;
import io.rong.push.RongPushClient;

public class ConversationListFragment extends UriFragment implements OnItemClickListener, OnItemLongClickListener, OnPortraitItemClick, View.OnClickListener {
    private String TAG = "ConversationList";
    private List<ConversationListFragment.ConversationConfig> mConversationsConfig;
    private ConversationListFragment mThis;
    private ConversationListAdapter mAdapter;
    private ListView mList;
    private LinearLayout mNotificationBar;
    private ImageView mNotificationBarImage;
    private TextView mNotificationBarText;
    private boolean isShowWithoutConnected = false;

    //红色标点
    private DragPointView dpv_system_num;
    public static TextView tv_system_content;
    private SystemMessageReceiver mSystemMessageReceiver;
    private IntentFilter mIntentFilter;
    public static TextView tv_system_time;
    private RelativeLayout rl_message_system;
    private static MyScrollView sv_top;

    public ConversationListFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mThis = this;
        this.TAG = this.getClass().getSimpleName();
        this.mConversationsConfig = new ArrayList();
        EventBus.getDefault().register(this);
        InternalModuleManager.getInstance().onLoaded();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            //进入系统消息列表
            case R.id.rl_message_system:
                startActivity(new Intent(getActivity(), SystemMessageActivity.class));

                break;
        }
    }

    class SystemMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {


            //设置系统消息未读数
            if (CONSTS.UNREAD_PUSH_NUM == 0) {
                dpv_system_num.setVisibility(View.GONE);

            } else if (CONSTS.UNREAD_PUSH_NUM > 0 && CONSTS.UNREAD_PUSH_NUM < 100) {
                dpv_system_num.setVisibility(View.VISIBLE);
                dpv_system_num.setText(String.valueOf(CONSTS.UNREAD_PUSH_NUM));

            } else {
                dpv_system_num.setVisibility(View.VISIBLE);
                dpv_system_num.setText("99+");

            }
            //设置系统消息最新内容
            //tv_system_time.setText(CONSTS.NEW_SYSTEM_TIME);
            //tv_system_content.setText(CONSTS.NEW_SYSTEM_MESSAGE);
            LogUtil.e(TAG, "conversation");
            int num = 0;
            String content = "";
            String time = "";
            try {
                 num = SharePreUtils.getInt("unread_push_num", 0, getActivity());
                 content = SharePreUtils.getString("new_system_message", "", getActivity());
                 time = SharePreUtils.getString("new_system_time", "", getActivity());
            }catch (Exception e){

            }

            if (num == 0) {
                dpv_system_num.setVisibility(View.GONE);
            } else if (num > 0 && num < 100) {
                dpv_system_num.setVisibility(View.VISIBLE);
                dpv_system_num.setText(num + "");
            } else {
                dpv_system_num.setVisibility(View.VISIBLE);
                dpv_system_num.setText("99+");
            }
            if (!"".equals(content)) {
                tv_system_content.setText(content);
                tv_system_time.setText(time);
            }

        }
    }

    protected void initFragment(Uri uri) {
        Log.d(this.TAG, "initFragment " + uri);
        ConversationType[] defConversationType = new ConversationType[]{ConversationType.PRIVATE, ConversationType.GROUP, ConversationType.DISCUSSION, ConversationType.SYSTEM, ConversationType.CUSTOMER_SERVICE, ConversationType.CHATROOM, ConversationType.PUBLIC_SERVICE, ConversationType.APP_PUBLIC_SERVICE};
        ConversationType[] type = defConversationType;
        int arr$ = defConversationType.length;

        int len$;
        for (len$ = 0; len$ < arr$; ++len$) {
            ConversationType i$ = type[len$];
            if (uri.getQueryParameter(i$.getName()) != null) {
                ConversationListFragment.ConversationConfig conversationType = new ConversationListFragment.ConversationConfig();
                conversationType.conversationType = i$;
                conversationType.isGathered = uri.getQueryParameter(i$.getName()).equals("true");
                this.mConversationsConfig.add(conversationType);
            }
        }

        if (this.mConversationsConfig.size() == 0) {
            String var9 = uri.getQueryParameter("type");
            ConversationType[] var10 = defConversationType;
            len$ = defConversationType.length;

            for (int var11 = 0; var11 < len$; ++var11) {
                ConversationType var12 = var10[var11];
                if (var12.getName().equals(var9)) {
                    ConversationListFragment.ConversationConfig config = new ConversationListFragment.ConversationConfig();
                    config.conversationType = var12;
                    config.isGathered = false;
                    this.mConversationsConfig.add(config);
                    break;
                }
            }
        }

        this.mAdapter.clear();
        if (RongIMClient.getInstance().getCurrentConnectionStatus().equals(ConnectionStatus.DISCONNECTED)) {
            Log.d(this.TAG, "RongCloud haven\'t been connected yet, so the conversation list display blank !d!!");
            this.isShowWithoutConnected = true;
        } else {
            this.getConversationList(this.getConfigConversationTypes());
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rc_fr_conversationlist_app, container, false);
        this.mNotificationBar = (LinearLayout) this.findViewById(view, id.rc_status_bar);
        this.mNotificationBar.setVisibility(View.GONE);
        this.mNotificationBarImage = (ImageView) this.findViewById(view, id.rc_status_bar_image);
        this.mNotificationBarText = (TextView) this.findViewById(view, id.rc_status_bar_text);
        View emptyView = this.findViewById(view, id.rc_conversation_list_empty_layout);
        dpv_system_num = this.findViewById(view, R.id.dpv_system_num);
        tv_system_time = this.findViewById(view, R.id.tv_system_time);
        tv_system_content = this.findViewById(view, R.id.tv_system_content);
        //sv_top = this.findViewById(view,R.id.sv_top);
        TextView emptyText = (TextView) this.findViewById(view, id.rc_empty_tv);
        emptyText.setText(this.getActivity().getResources().getString(string.rc_conversation_list_empty_prompt));
        this.mList = this.findViewById(view, id.rc_list);
        this.mList.setEmptyView(emptyView);
        this.mList.setOnItemClickListener(this);
        this.mList.setOnItemLongClickListener(this);
        if (this.mAdapter == null) {
            this.mAdapter = this.onResolveAdapter(this.getActivity());
        }

        this.mAdapter.setOnPortraitItemClick(this);
        this.mList.setAdapter(this.mAdapter);
//         View headView = LayoutInflater.from(getActivity()).inflate(R.layout.message_header,container,false);
//        this.mList.addHeaderView(headView);
        //系统消息
        rl_message_system = this.findViewById(view, R.id.rl_message_system);
        rl_message_system.setOnClickListener(this);
        //动态注册系统未读广播
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.mobile.office.system.message");
        mSystemMessageReceiver = new SystemMessageReceiver();

        getActivity().registerReceiver(mSystemMessageReceiver, mIntentFilter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //设置系统消息最新内容


        //tv_system_time.setText(SharePreUtils.getString("new_system_time","",getActivity()));
        //tv_system_content.setText(SharePreUtils.getString("new_system_message","",getActivity()));
        loadPUshData();
        // ToastUtil.showToast("onStart",getActivity());
    }

    private void loadPUshData() {
        RequestParams params = new RequestParams(URL.USER);
        params.addBodyParameter("action", "getPushList");
        params.addBodyParameter("phone", SharePreUtils.getString("phone", "", getActivity()));
        params.addBodyParameter("page", 0 + "");
        params.addBodyParameter("pageSize", 1 + "");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                Gson gson = new Gson();
                PushListJson pushListJson = gson.fromJson(result, PushListJson.class);
                if (pushListJson != null && pushListJson.getResult() == CONSTS.SEND_OK) {
                    tv_system_time.setText(pushListJson.getObj().get(0).getCreateTime());
                    tv_system_content.setText(pushListJson.getObj().get(0).getContent());
                }

                //ToastUtil.showToast("finish"+result,getActivity());
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

                //  rv_system_message.refreshComplete();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    public static void scrollView() {
//        LogUtil.e("gg","ggggg"+sv_top);
//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                sv_top.scrollTo(0,0);
//            }
//        }.start();
        //.scrollBy(0,0);
    }

    public void onResume() {
        super.onResume();
        Log.d(this.TAG, "onResume " + RongIM.getInstance().getCurrentConnectionStatus());
        RongPushClient.clearAllPushNotifications(this.getActivity());
        this.setNotificationBarVisibility(RongIM.getInstance().getCurrentConnectionStatus());

        //设置红色标记
        if (CONSTS.UNREAD_PUSH_NUM == 0) {
            dpv_system_num.setVisibility(View.GONE);
        } else if (CONSTS.UNREAD_PUSH_NUM > 0 && CONSTS.UNREAD_PUSH_NUM < 100) {
            dpv_system_num.setVisibility(View.VISIBLE);
            dpv_system_num.setText(String.valueOf(CONSTS.UNREAD_PUSH_NUM));
        } else {
            dpv_system_num.setVisibility(View.VISIBLE);
            dpv_system_num.setText("99+");
        }
    }

    private void getConversationList(ConversationType[] conversationTypes) {
        this.getConversationList(conversationTypes, new IHistoryDataResultCallback<List<Conversation>>() {
            public void onResult(List<Conversation> data) {
                if (data != null && data.size() > 0) {
                    makeUiConversationList(data);
                    mAdapter.notifyDataSetChanged();
                    LogUtil.e(TAG, "getConversationList  " + data.size());
                } else {
                    LogUtil.e(TAG, "getConversationList return null " + RongIMClient.getInstance().getCurrentConnectionStatus());
                    isShowWithoutConnected = true;
                }

            }

            public void onError() {
            }
        });
    }

    public void getConversationList(ConversationType[] conversationTypes, final IHistoryDataResultCallback<List<Conversation>> callback) {
        RongIMClient.getInstance().getConversationList(new ResultCallback<List<Conversation>>() {
            public void onSuccess(List<Conversation> conversations) {
                if (callback != null) {
                    callback.onResult(conversations);
                }

            }

            public void onError(ErrorCode e) {
                if (callback != null) {
                    callback.onError();
                }

            }
        }, conversationTypes);
    }

    public void focusUnreadItem() {
        int first = this.mList.getFirstVisiblePosition();
        int last = this.mList.getLastVisiblePosition();
        int visibleCount = last - first + 1;
        int count = this.mList.getCount();
        if (visibleCount < count) {
            int index;
            if (last < count - 1) {
                index = first + 1;
            } else {
                index = 0;
            }

            if (!this.selectNextUnReadItem(index, count)) {
                this.selectNextUnReadItem(0, count);
            }
        }

    }

    private boolean selectNextUnReadItem(int startIndex, int totalCount) {
        int index = -1;

        for (int i = startIndex; i < totalCount; ++i) {
            UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(i);
            if (uiConversation.getUnReadMessageCount() > 0) {
                index = i;
                break;
            }
        }

        if (index >= 0 && index < totalCount) {
            this.mList.setSelection(index);
            return true;
        } else {
            return false;
        }
    }

    private void setNotificationBarVisibility(ConnectionStatus status) {
        if (!this.getResources().getBoolean(bool.rc_is_show_warning_notification)) {
            Log.e(this.TAG, "rc_is_show_warning_notification is disabled.");
        } else {
            String content = null;
            if (status.equals(ConnectionStatus.NETWORK_UNAVAILABLE)) {
                content = this.getResources().getString(string.rc_notice_network_unavailable);
            } else if (status.equals(ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT)) {
                content = this.getResources().getString(string.rc_notice_tick);
            } else if (status.equals(ConnectionStatus.CONNECTED)) {
                this.mNotificationBar.setVisibility(View.GONE);
            } else if (status.equals(ConnectionStatus.DISCONNECTED)) {
                content = this.getResources().getString(string.rc_notice_disconnect);
            } else if (status.equals(ConnectionStatus.CONNECTING)) {
                content = this.getResources().getString(string.rc_notice_connecting);
            }

            if (content != null) {
                if (this.mNotificationBar.getVisibility() == View.GONE) {
                    final String pContent = content;
                    this.getHandler().postDelayed(new Runnable() {
                        public void run() {
                            if (!RongIMClient.getInstance().getCurrentConnectionStatus().equals(ConnectionStatus.CONNECTED)) {
                                mNotificationBar.setVisibility(View.VISIBLE);
                                mNotificationBarText.setText(pContent);
                                if (RongIMClient.getInstance().getCurrentConnectionStatus().equals(ConnectionStatus.CONNECTING)) {
                                    mNotificationBarImage.setImageResource(drawable.rc_notification_connecting_animated);
                                } else {
                                    mNotificationBarImage.setImageResource(drawable.rc_notification_network_available);
                                }
                            }

                        }
                    }, 4000L);
                } else {
                    this.mNotificationBarText.setText(content);
                    if (RongIMClient.getInstance().getCurrentConnectionStatus().equals(ConnectionStatus.CONNECTING)) {
                        this.mNotificationBarImage.setImageResource(drawable.rc_notification_connecting_animated);
                    } else {
                        this.mNotificationBarImage.setImageResource(drawable.rc_notification_network_available);
                    }
                }
            }

        }
    }

    public boolean onBackPressed() {
        return false;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setAdapter(ConversationListAdapter adapter) {
        this.mAdapter = adapter;
        if (this.mList != null) {
            this.mList.setAdapter(adapter);
        }

    }

    public ConversationListAdapter onResolveAdapter(Context context) {
        this.mAdapter = new ConversationListAdapter(context);
        return this.mAdapter;
    }

    @Subscribe
    public void onEventMainThread(SyncReadStatusEvent event) {
        ConversationType conversationType = event.getConversationType();
        String targetId = event.getTargetId();
        Log.d(this.TAG, "SyncReadStatusEvent " + conversationType + " " + targetId);
        int first = this.mList.getFirstVisiblePosition();
        int last = this.mList.getLastVisiblePosition();
        int position;
        if (this.getGatherState(conversationType)) {
            position = this.mAdapter.findGatheredItem(conversationType);
        } else {
            position = this.mAdapter.findPosition(conversationType, targetId);
        }

        if (position >= 0) {
            UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(position);
            uiConversation.clearUnRead(conversationType, targetId);
            if (position >= first && position <= last) {
                this.mAdapter.getView(position, this.mList.getChildAt(position - this.mList.getFirstVisiblePosition()), this.mList);
            }
        }

    }

    @Subscribe
    public void onEventMainThread(ReadReceiptEvent event) {
        ConversationType conversationType = event.getMessage().getConversationType();
        String targetId = event.getMessage().getTargetId();
        int originalIndex = this.mAdapter.findPosition(conversationType, targetId);
        boolean gatherState = this.getGatherState(conversationType);
        if (!gatherState && originalIndex >= 0) {
            UIConversation conversation = (UIConversation) this.mAdapter.getItem(originalIndex);
            ReadReceiptMessage content = (ReadReceiptMessage) event.getMessage().getContent();
            if (content.getLastMessageSendTime() >= conversation.getUIConversationTime() && conversation.getConversationSenderId().equals(RongIMClient.getInstance().getCurrentUserId())) {
                conversation.setSentStatus(SentStatus.READ);
                this.mAdapter.getView(originalIndex, this.mList.getChildAt(originalIndex - this.mList.getFirstVisiblePosition()), this.mList);
            }
        }

    }

    @Subscribe
    public void onEventMainThread(AudioListenedEvent event) {
        Message message = event.getMessage();
        ConversationType conversationType = message.getConversationType();
        String targetId = message.getTargetId();
        Log.d(this.TAG, "Message: " + message.getObjectName() + " " + conversationType + " " + message.getSentStatus());
        if (this.isConfigured(conversationType)) {
            boolean gathered = this.getGatherState(conversationType);
            int position = gathered ? this.mAdapter.findGatheredItem(conversationType) : this.mAdapter.findPosition(conversationType, targetId);
            if (position >= 0) {
                UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(position);
                if (message.getMessageId() == uiConversation.getLatestMessageId()) {
                    uiConversation.updateConversation(message, gathered);
                    this.mAdapter.getView(position, this.mList.getChildAt(position - this.mList.getFirstVisiblePosition()), this.mList);
                }
            }
        }

    }

    public boolean shouldUpdateConversation(Message message, int left) {
        return true;
    }

    @Subscribe
    public void onEventMainThread(OnReceiveMessageEvent event) {
        Message message = event.getMessage();
        String targetId = message.getTargetId();
        ConversationType conversationType = message.getConversationType();
        int first = this.mList.getFirstVisiblePosition();
        int last = this.mList.getLastVisiblePosition();
        if (this.isConfigured(message.getConversationType()) && this.shouldUpdateConversation(event.getMessage(), event.getLeft())) {
            if (message.getMessageId() > 0) {
                boolean gathered = this.getGatherState(conversationType);
                int position;
                if (gathered) {
                    position = this.mAdapter.findGatheredItem(conversationType);
                } else {
                    position = this.mAdapter.findPosition(conversationType, targetId);
                }

                UIConversation uiConversation;
                int index;
                if (position < 0) {
                    uiConversation = UIConversation.obtain(message, gathered);
                    index = this.getPosition(uiConversation);
                    this.mAdapter.add(uiConversation, index);
                    this.mAdapter.notifyDataSetChanged();
                } else {
                    uiConversation = (UIConversation) this.mAdapter.getItem(position);
                    if (event.getMessage().getSentTime() > uiConversation.getUIConversationTime()) {
                        uiConversation.updateConversation(message, gathered);
                        this.mAdapter.remove(position);
                        index = this.getPosition(uiConversation);
                        if (index == position) {
                            this.mAdapter.add(uiConversation, index);
                            if (index >= first && index <= last) {
                                this.mAdapter.getView(index, this.mList.getChildAt(index - this.mList.getFirstVisiblePosition()), this.mList);
                            }
                        } else {
                            this.mAdapter.add(uiConversation, index);
                            if (index >= first && index <= last) {
                                this.mAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Log.i(this.TAG, "ignore update message " + event.getMessage().getObjectName());
                    }
                }

                Log.i(this.TAG, "conversation unread count : " + uiConversation.getUnReadMessageCount() + " " + conversationType + " " + targetId);
            }

            if (event.getLeft() == 0) {
                this.syncUnreadCount();
            }

            Log.d(this.TAG, "OnReceiveMessageEvent: " + message.getObjectName() + " " + event.getLeft() + " " + conversationType + " " + targetId);
        }

    }

    @Subscribe
    public void onEventMainThread(MessageLeftEvent event) {
        if (event.left == 0) {
            this.syncUnreadCount();
        }

    }

    private void syncUnreadCount() {
        if (this.mAdapter.getCount() > 0) {
            final int first = this.mList.getFirstVisiblePosition();
            final int last = this.mList.getLastVisiblePosition();

            for (int i = 0; i < this.mAdapter.getCount(); ++i) {
                final UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(i);
                ConversationType conversationType = uiConversation.getConversationType();
                String targetId = uiConversation.getConversationTargetId();
                final int position;
                if (this.getGatherState(conversationType)) {
                    position = this.mAdapter.findGatheredItem(conversationType);
                    RongIMClient.getInstance().getUnreadCount(new ResultCallback<Integer>() {
                        public void onSuccess(Integer integer) {
                            uiConversation.setUnReadMessageCount(integer.intValue());
                            if (position >= first && position <= last) {
                                mAdapter.getView(position, mList.getChildAt(position - mList.getFirstVisiblePosition()), mList);
                            }

                        }

                        public void onError(ErrorCode e) {
                        }
                    }, new ConversationType[]{conversationType});
                } else {
                    position = this.mAdapter.findPosition(conversationType, targetId);
                    RongIMClient.getInstance().getUnreadCount(conversationType, targetId, new ResultCallback<Integer>() {
                        public void onSuccess(Integer integer) {
                            uiConversation.setUnReadMessageCount(integer.intValue());
                            if (position >= first && position <= last) {
                                mAdapter.getView(position, mList.getChildAt(position - mList.getFirstVisiblePosition()), mList);
                            }

                        }

                        public void onError(ErrorCode e) {
                        }
                    });
                }
            }
        }

    }

    @Subscribe
    public void onEventMainThread(MessageRecallEvent event) {
        Log.d(this.TAG, "MessageRecallEvent");
        int count = this.mAdapter.getCount();

        for (int i = 0; i < count; ++i) {
            UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(i);
            if (event.getMessageId() == uiConversation.getLatestMessageId()) {
                boolean gatherState = ((UIConversation) this.mAdapter.getItem(i)).getConversationGatherState();
                final String targetId = ((UIConversation) this.mAdapter.getItem(i)).getConversationTargetId();
                if (gatherState) {
                    RongIM.getInstance().getConversationList(new ResultCallback<List<Conversation>>() {
                        public void onSuccess(List<Conversation> conversationList) {
                            if (conversationList != null && conversationList.size() > 0) {
                                UIConversation uiConversation = makeUIConversation(conversationList);
                                int oldPos = mAdapter.findPosition(uiConversation.getConversationType(), targetId);
                                if (oldPos >= 0) {
                                    mAdapter.remove(oldPos);


                                }

                                int newIndex = getPosition(uiConversation);
                                mAdapter.add(uiConversation, newIndex);
                                mAdapter.notifyDataSetChanged();
                            }

                        }

                        public void onError(ErrorCode e) {
                        }
                    }, new ConversationType[]{uiConversation.getConversationType()});
                } else {
                    RongIM.getInstance().getConversation(uiConversation.getConversationType(), uiConversation.getConversationTargetId(), new ResultCallback<Conversation>() {
                        public void onSuccess(Conversation conversation) {
                            if (conversation != null) {
                                UIConversation temp = UIConversation.obtain(conversation, false);
                                int pos = mAdapter.findPosition(conversation.getConversationType(), conversation.getTargetId());
                                if (pos >= 0) {
                                    mAdapter.remove(pos);
                                }

                                int newPosition = getPosition(temp);
                                mAdapter.add(temp, newPosition);
                                mAdapter.notifyDataSetChanged();
                            }

                        }

                        public void onError(ErrorCode e) {
                        }
                    });
                }
                break;
            }
        }

    }

    @Subscribe
    public void onEventMainThread(RemoteMessageRecallEvent event) {
        Log.d(this.TAG, "RemoteMessageRecallEvent");
        int count = this.mAdapter.getCount();

        for (int i = 0; i < count; ++i) {
            UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(i);
            if (event.getMessageId() == uiConversation.getLatestMessageId()) {
                boolean gatherState = uiConversation.getConversationGatherState();
                final String targetId = ((UIConversation) this.mAdapter.getItem(i)).getConversationTargetId();
                if (gatherState) {
                    RongIM.getInstance().getConversationList(new ResultCallback<List<Conversation>>() {
                        public void onSuccess(List<Conversation> conversationList) {
                            if (conversationList != null && conversationList.size() > 0) {
                                UIConversation uiConversation = makeUIConversation(conversationList);
                                int oldPos = mAdapter.findPosition(uiConversation.getConversationType(), targetId);
                                if (oldPos >= 0) {
                                    mAdapter.remove(oldPos);
                                }

                                int newIndex = getPosition(uiConversation);
                                mAdapter.add(uiConversation, newIndex);
                                mAdapter.notifyDataSetChanged();
                            }

                        }

                        public void onError(ErrorCode e) {
                        }
                    }, new ConversationType[]{((UIConversation) this.mAdapter.getItem(i)).getConversationType()});
                } else {
                    RongIM.getInstance().getConversation(uiConversation.getConversationType(), uiConversation.getConversationTargetId(), new ResultCallback<Conversation>() {
                        public void onSuccess(Conversation conversation) {
                            if (conversation != null) {
                                UIConversation temp = UIConversation.obtain(conversation, false);
                                int pos = mAdapter.findPosition(conversation.getConversationType(), conversation.getTargetId());
                                if (pos >= 0) {
                                    mAdapter.remove(pos);
                                }

                                int newPosition = getPosition(temp);
                                mAdapter.add(temp, newPosition);
                                mAdapter.notifyDataSetChanged();
                            }

                        }

                        public void onError(ErrorCode e) {
                        }
                    });
                }
                break;
            }
        }

    }

    @Subscribe
    public void onEventMainThread(Message message) {
        ConversationType conversationType = message.getConversationType();
        String targetId = message.getTargetId();
        Log.d(this.TAG, "Message: " + message.getObjectName() + " " + message.getMessageId() + " " + conversationType + " " + message.getSentStatus());
        boolean gathered = this.getGatherState(conversationType);
        if (this.isConfigured(conversationType) && message.getMessageId() > 0) {
            int position = gathered ? this.mAdapter.findGatheredItem(conversationType) : this.mAdapter.findPosition(conversationType, targetId);
            UIConversation uiConversation;
            int index;
            if (position < 0) {
                uiConversation = UIConversation.obtain(message, gathered);
                index = this.getPosition(uiConversation);
                this.mAdapter.add(uiConversation, index);
                this.mAdapter.notifyDataSetChanged();
            } else {
                uiConversation = (UIConversation) this.mAdapter.getItem(position);
                this.mAdapter.remove(position);
                uiConversation.updateConversation(message, gathered);
                index = this.getPosition(uiConversation);
                this.mAdapter.add(uiConversation, index);
                if (position == index) {
                    this.mAdapter.getView(index, this.mList.getChildAt(index - this.mList.getFirstVisiblePosition()), this.mList);
                } else {
                    this.mAdapter.notifyDataSetChanged();
                }
            }
        }

    }

    @Subscribe
    public void onEventMainThread(ConnectionStatus status) {
        Log.d(this.TAG, "ConnectionStatus, " + status.toString());
        this.setNotificationBarVisibility(status);
    }

    @Subscribe
    public void onEventMainThread(ConnectEvent event) {
        if (this.isShowWithoutConnected) {
            this.getConversationList(this.getConfigConversationTypes());
            this.isShowWithoutConnected = false;
        }

    }

    @Subscribe
    public void onEventMainThread(final CreateDiscussionEvent createDiscussionEvent) {
        Log.d(this.TAG, "createDiscussionEvent");
        final String targetId = createDiscussionEvent.getDiscussionId();
        if (this.isConfigured(ConversationType.DISCUSSION)) {
            RongIMClient.getInstance().getConversation(ConversationType.DISCUSSION, targetId, new ResultCallback<Conversation>() {
                public void onSuccess(Conversation conversation) {
                    if (conversation != null) {
                        int position;
                        if (getGatherState(ConversationType.DISCUSSION)) {
                            position = mAdapter.findGatheredItem(ConversationType.DISCUSSION);
                        } else {
                            position = mAdapter.findPosition(ConversationType.DISCUSSION, targetId);
                        }

                        conversation.setConversationTitle(createDiscussionEvent.getDiscussionName());
                        UIConversation uiConversation;
                        if (position < 0) {
                            uiConversation = UIConversation.obtain(conversation, getGatherState(ConversationType.DISCUSSION));
                            int index = getPosition(uiConversation);
                            mAdapter.add(uiConversation, index);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            uiConversation = (UIConversation) mAdapter.getItem(position);
                            uiConversation.updateConversation(conversation, getGatherState(ConversationType.DISCUSSION));
                            mAdapter.getView(position, mList.getChildAt(position - mList.getFirstVisiblePosition()), mList);
                        }
                    }

                }

                public void onError(ErrorCode e) {
                }
            });
        }

    }

    @Subscribe
    public void onEventMainThread(final DraftEvent draft) {
        ConversationType conversationType = draft.getConversationType();
        String targetId = draft.getTargetId();
        Log.i(this.TAG, "Draft : " + conversationType);
        if (this.isConfigured(conversationType)) {
            final boolean gathered = this.getGatherState(conversationType);
            final int position = gathered ? this.mAdapter.findGatheredItem(conversationType) : this.mAdapter.findPosition(conversationType, targetId);
            RongIMClient.getInstance().getConversation(conversationType, targetId, new ResultCallback<Conversation>() {
                public void onSuccess(Conversation conversation) {
                    if (conversation != null) {
                        UIConversation uiConversation;
                        if (position < 0) {
                            if (!TextUtils.isEmpty(draft.getContent())) {
                                uiConversation = UIConversation.obtain(conversation, gathered);
                                int index = getPosition(uiConversation);
                                mAdapter.add(uiConversation, index);
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            uiConversation = (UIConversation) mAdapter.getItem(position);
                            if (TextUtils.isEmpty(draft.getContent()) && !TextUtils.isEmpty(uiConversation.getDraft()) || !TextUtils.isEmpty(draft.getContent()) && TextUtils.isEmpty(uiConversation.getDraft()) || !TextUtils.isEmpty(draft.getContent()) && !TextUtils.isEmpty(uiConversation.getDraft()) && !draft.getContent().equals(uiConversation.getDraft())) {
                                uiConversation.updateConversation(conversation, gathered);
                                mAdapter.getView(position, mList.getChildAt(position - mList.getFirstVisiblePosition()), mList);
                            }
                        }
                    }

                }

                public void onError(ErrorCode e) {
                }
            });
        }

    }

    @Subscribe
    public void onEventMainThread(Group groupInfo) {
        Log.d(this.TAG, "Group: " + groupInfo.getName() + " " + groupInfo.getId());
        int count = this.mAdapter.getCount();
        if (groupInfo.getName() != null) {
            int last = this.mList.getLastVisiblePosition();
            int first = this.mList.getFirstVisiblePosition();

            for (int i = 0; i < count; ++i) {
                UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(i);
                uiConversation.updateConversation(groupInfo);
                if (i >= first && i <= last) {
                    this.mAdapter.getView(i, this.mList.getChildAt(i - first), this.mList);
                }
            }

        }
    }

    @Subscribe
    public void onEventMainThread(Discussion discussion) {
        Log.d(this.TAG, "Discussion: " + discussion.getName() + " " + discussion.getId());
        if (this.isConfigured(ConversationType.DISCUSSION)) {
            int last = this.mList.getLastVisiblePosition();
            int first = this.mList.getFirstVisiblePosition();
            int position;
            if (this.getGatherState(ConversationType.DISCUSSION)) {
                position = this.mAdapter.findGatheredItem(ConversationType.DISCUSSION);
            } else {
                position = this.mAdapter.findPosition(ConversationType.DISCUSSION, discussion.getId());
            }

            if (position >= 0) {
                for (int i = 0; i == position; ++i) {
                    UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(i);
                    uiConversation.updateConversation(discussion);
                    if (i >= first && i <= last) {
                        this.mAdapter.getView(i, this.mList.getChildAt(i - this.mList.getFirstVisiblePosition()), this.mList);
                    }
                }
            }
        }

    }

    @Subscribe
    public void onEventMainThread(GroupUserInfo groupUserInfo) {
        Log.d(this.TAG, "GroupUserInfo " + groupUserInfo.getGroupId() + " " + groupUserInfo.getUserId() + " " + groupUserInfo.getNickname());
        if (groupUserInfo.getNickname() != null && groupUserInfo.getGroupId() != null) {
            int count = this.mAdapter.getCount();
            int last = this.mList.getLastVisiblePosition();
            int first = this.mList.getFirstVisiblePosition();

            for (int i = 0; i < count; ++i) {
                UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(i);
                if (!this.getGatherState(ConversationType.GROUP) && uiConversation.getConversationTargetId().equals(groupUserInfo.getGroupId()) && uiConversation.getConversationSenderId().equals(groupUserInfo.getUserId())) {
                    uiConversation.updateConversation(groupUserInfo);
                    if (i >= first && i <= last) {
                        this.mAdapter.getView(i, this.mList.getChildAt(i - this.mList.getFirstVisiblePosition()), this.mList);
                    }
                }
            }

        }
    }

    @Subscribe
    public void onEventMainThread(UserInfo userInfo) {
        Log.i(this.TAG, "UserInfo " + userInfo.getUserId() + " " + userInfo.getName());
        int count = this.mAdapter.getCount();
        int last = this.mList.getLastVisiblePosition();
        int first = this.mList.getFirstVisiblePosition();

        for (int i = 0; i < count && userInfo.getName() != null; ++i) {
            UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(i);
            if (uiConversation.hasNickname(userInfo.getUserId())) {
                Log.i(this.TAG, "has nick name");
            } else {
                uiConversation.updateConversation(userInfo);
                if (i >= first && i <= last) {

                    this.mAdapter.getView(i, this.mList.getChildAt(i - first), this.mList);
                    Log.i(this.TAG, "UserInfo " + userInfo.getUserId() + " " + userInfo.getName() + ",i:" + i);
                }
            }
        }

    }

    @Subscribe
    public void onEventMainThread(PublicServiceProfile profile) {
        Log.d(this.TAG, "PublicServiceProfile");
        int count = this.mAdapter.getCount();
        boolean gatherState = this.getGatherState(profile.getConversationType());

        for (int i = 0; i < count; ++i) {
            UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(i);
            if (uiConversation.getConversationType().equals(profile.getConversationType()) && uiConversation.getConversationTargetId().equals(profile.getTargetId()) && !gatherState) {
                uiConversation.setUIConversationTitle(profile.getName());
                uiConversation.setIconUrl(profile.getPortraitUri());
                this.mAdapter.getView(i, this.mList.getChildAt(i - this.mList.getFirstVisiblePosition()), this.mList);
                break;
            }
        }

    }

    @Subscribe
    public void onEventMainThread(PublicServiceFollowableEvent event) {
        Log.d(this.TAG, "PublicServiceFollowableEvent");
        if (!event.isFollow()) {
            int originalIndex = this.mAdapter.findPosition(event.getConversationType(), event.getTargetId());
            if (originalIndex >= 0) {
                this.mAdapter.remove(originalIndex);
                this.mAdapter.notifyDataSetChanged();
            }
        }

    }

    @Subscribe
    public void onEventMainThread(ConversationUnreadEvent unreadEvent) {
        Log.d(TAG, "ConversationUnreadEvent");
        ConversationType conversationType = unreadEvent.getType();
        String targetId = unreadEvent.getTargetId();
        int position = this.getGatherState(conversationType) ? this.mAdapter.findGatheredItem(conversationType) : this.mAdapter.findPosition(conversationType, targetId);
        if (position >= 0) {
            int first = this.mList.getFirstVisiblePosition();
            int last = this.mList.getLastVisiblePosition();
            UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(position);
            uiConversation.clearUnRead(conversationType, targetId);
            if (position >= first && position <= last) {
                this.mAdapter.getView(position, this.mList.getChildAt(position - this.mList.getFirstVisiblePosition()), this.mList);
            }
        }

    }

    @Subscribe
    public void onEventMainThread(ConversationTopEvent setTopEvent) {
        Log.d(this.TAG, "ConversationTopEvent");
        ConversationType conversationType = setTopEvent.getConversationType();
        String targetId = setTopEvent.getTargetId();
        int position = this.mAdapter.findPosition(conversationType, targetId);
        if (position >= 0 && !this.getGatherState(conversationType)) {
            UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(position);
            if (uiConversation.isTop() != setTopEvent.isTop()) {
                uiConversation.setTop(!uiConversation.isTop());
                this.mAdapter.remove(position);
                int index = this.getPosition(uiConversation);
                this.mAdapter.add(uiConversation, index);
                if (index == position) {
                    this.mAdapter.getView(index, this.mList.getChildAt(index - this.mList.getFirstVisiblePosition()), this.mList);
                } else {
                    this.mAdapter.notifyDataSetChanged();
                }
            }
        }

    }

    @Subscribe
    public void onEventMainThread(ConversationRemoveEvent removeEvent) {
        Log.d(this.TAG, "ConversationRemoveEvent");
        ConversationType conversationType = removeEvent.getType();
        this.removeConversation(conversationType, removeEvent.getTargetId());
    }

    @Subscribe
    public void onEventMainThread(ClearConversationEvent clearConversationEvent) {
        Log.d(this.TAG, "ClearConversationEvent");
        List typeList = clearConversationEvent.getTypes();

        for (int i = this.mAdapter.getCount() - 1; i >= 0; --i) {
            if (typeList.indexOf(((UIConversation) this.mAdapter.getItem(i)).getConversationType()) >= 0) {
                this.mAdapter.remove(i);
            }
        }

        this.mAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onEventMainThread(MessageDeleteEvent event) {
        Log.d(this.TAG, "MessageDeleteEvent");
        int count = this.mAdapter.getCount();

        for (int i = 0; i < count; ++i) {
            if (event.getMessageIds().contains(Integer.valueOf(((UIConversation) this.mAdapter.getItem(i)).getLatestMessageId()))) {
                boolean gatherState = ((UIConversation) this.mAdapter.getItem(i)).getConversationGatherState();
                final String targetId = ((UIConversation) this.mAdapter.getItem(i)).getConversationTargetId();
                if (gatherState) {
                    RongIM.getInstance().getConversationList(new ResultCallback<List<Conversation>>() {
                        public void onSuccess(List<Conversation> conversationList) {
                            if (conversationList != null && conversationList.size() != 0) {
                                UIConversation uiConversation = makeUIConversation(conversationList);
                                int oldPos = mAdapter.findPosition(uiConversation.getConversationType(), targetId);
                                if (oldPos >= 0) {
                                    mAdapter.remove(oldPos);
                                }

                                int newIndex = getPosition(uiConversation);
                                mAdapter.add(uiConversation, newIndex);
                                mAdapter.notifyDataSetChanged();
                            }
                        }

                        public void onError(ErrorCode e) {
                        }
                    }, new ConversationType[]{((UIConversation) this.mAdapter.getItem(i)).getConversationType()});
                } else {
                    RongIM.getInstance().getConversation(((UIConversation) this.mAdapter.getItem(i)).getConversationType(), ((UIConversation) this.mAdapter.getItem(i)).getConversationTargetId(), new ResultCallback<Conversation>() {
                        public void onSuccess(Conversation conversation) {
                            if (conversation == null) {
                                Log.d(TAG, "onEventMainThread getConversation : onSuccess, conversation = null");
                            } else {
                                UIConversation temp = UIConversation.obtain(conversation, false);
                                int pos = mAdapter.findPosition(conversation.getConversationType(), conversation.getTargetId());
                                if (pos >= 0) {
                                    mAdapter.remove(pos);
                                }

                                int newIndex = getPosition(temp);
                                mAdapter.add(temp, newIndex);
                                mAdapter.notifyDataSetChanged();
                            }
                        }

                        public void onError(ErrorCode e) {
                        }
                    });
                }
                break;
            }
        }

    }

    @Subscribe
    public void onEventMainThread(ConversationNotificationEvent notificationEvent) {
        int originalIndex = this.mAdapter.findPosition(notificationEvent.getConversationType(), notificationEvent.getTargetId());
        if (originalIndex >= 0) {
            this.mAdapter.getView(originalIndex, this.mList.getChildAt(originalIndex - this.mList.getFirstVisiblePosition()), this.mList);
        }

    }

    @Subscribe
    public void onEventMainThread(MessagesClearEvent clearMessagesEvent) {

        Log.d(this.TAG, "MessagesClearEvent");
        ConversationType conversationType = clearMessagesEvent.getType();
        String targetId = clearMessagesEvent.getTargetId();
        int position = this.getGatherState(conversationType) ? this.mAdapter.findGatheredItem(conversationType) : this.mAdapter.findPosition(conversationType, targetId);
        if (position >= 0) {
            UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(position);
            uiConversation.clearLastMessage();
            this.mAdapter.getView(position, this.mList.getChildAt(position - this.mList.getFirstVisiblePosition()), this.mList);
        }

    }

    @Subscribe
    public void onEventMainThread(OnMessageSendErrorEvent sendErrorEvent) {
        Message message = sendErrorEvent.getMessage();
        ConversationType conversationType = message.getConversationType();
        String targetId = message.getTargetId();
        if (this.isConfigured(conversationType)) {
            int first = this.mList.getFirstVisiblePosition();
            int last = this.mList.getLastVisiblePosition();
            boolean gathered = this.getGatherState(conversationType);
            int index = gathered ? this.mAdapter.findGatheredItem(conversationType) : this.mAdapter.findPosition(conversationType, targetId);
            if (index >= 0) {
                UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(index);
                message.setSentStatus(SentStatus.FAILED);
                uiConversation.updateConversation(message, gathered);
                if (index >= first && index <= last) {
                    this.mAdapter.getView(index, this.mList.getChildAt(index - this.mList.getFirstVisiblePosition()), this.mList);
                }
            }
        }

    }

    @Subscribe
    public void onEventMainThread(QuitDiscussionEvent event) {
        Log.d(this.TAG, "QuitDiscussionEvent");
        this.removeConversation(ConversationType.DISCUSSION, event.getDiscussionId());
    }

    @Subscribe
    public void onEventMainThread(QuitGroupEvent event) {
        Log.d(this.TAG, "QuitGroupEvent");
        this.removeConversation(ConversationType.GROUP, event.getGroupId());
    }

    private void removeConversation(final ConversationType conversationType, String targetId) {
        boolean gathered = this.getGatherState(conversationType);
        int index;
        if (gathered) {
            index = this.mAdapter.findGatheredItem(conversationType);
            if (index >= 0) {
                RongIM.getInstance().getConversationList(new ResultCallback<List<Conversation>>() {
                    public void onSuccess(List<Conversation> conversationList) {
                        int oldPos = mAdapter.findGatheredItem(conversationType);
                        if (oldPos >= 0) {
                            mAdapter.remove(oldPos);
                            if (conversationList != null && conversationList.size() > 0) {
                                UIConversation uiConversation = makeUIConversation(conversationList);
                                int newIndex = getPosition(uiConversation);
                                mAdapter.add(uiConversation, newIndex);
                            }

                            mAdapter.notifyDataSetChanged();
                        }

                    }

                    public void onError(ErrorCode e) {
                    }
                }, new ConversationType[]{conversationType});
            }
        } else {
            index = this.mAdapter.findPosition(conversationType, targetId);
            if (index >= 0) {
                this.mAdapter.remove(index);
                this.mAdapter.notifyDataSetChanged();
            }
        }

    }

    public void onPortraitItemClick(View v, UIConversation data) {
        ConversationType type = data.getConversationType();

        if (this.getGatherState(type)) {
            RongIM.getInstance().startSubConversationList(this.getActivity(), type);
        } else {
            if (RongContext.getInstance().getConversationListBehaviorListener() != null) {
                boolean isDefault = RongContext.getInstance().getConversationListBehaviorListener().onConversationPortraitClick(this.getActivity(), type, data.getConversationTargetId());
                if (isDefault) {
                    return;
                }
            }

            data.setUnReadMessageCount(0);
            RongIM.getInstance().startConversation(this.getActivity(), type, data.getConversationTargetId(), data.getUIConversationTitle());
        }

    }

    public boolean onPortraitItemLongClick(View v, UIConversation data) {
        ConversationType type = data.getConversationType();
        if (RongContext.getInstance().getConversationListBehaviorListener() != null) {
            boolean isDealt = RongContext.getInstance().getConversationListBehaviorListener().onConversationPortraitLongClick(this.getActivity(), type, data.getConversationTargetId());
            if (isDealt) {
                return true;
            }
        }

        if (!this.getGatherState(type)) {
            this.buildMultiDialog(data);
            return true;
        } else {
            this.buildSingleDialog(data);
            return true;
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(position);
        if (uiConversation == null) {
            return;
        }
        ConversationType conversationType = uiConversation.getConversationType();
        if (this.getGatherState(conversationType)) {
            //ToastUtil.showToast("data1:",getActivity());
            RongIM.getInstance().startSubConversationList(this.getActivity(), conversationType);
        } else {
            if (RongContext.getInstance().getConversationListBehaviorListener() != null && RongContext.getInstance().getConversationListBehaviorListener().onConversationClick(this.getActivity(), view, uiConversation)) {
                //ToastUtil.showToast("data2:",getActivity());
                return;
            }
            //ToastUtil.showToast("data3:"+id+",group:"+uiConversation.getConversationTargetId(),getActivity());
            CONSTS.ORGAN_SEG = uiConversation.getConversationTargetId();
            uiConversation.setUnReadMessageCount(0);
            RongIM.getInstance().startConversation(this.getActivity(), conversationType, uiConversation.getConversationTargetId(), uiConversation.getUIConversationTitle());
        }

    }

    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        UIConversation uiConversation = (UIConversation) this.mAdapter.getItem(position);
        if (RongContext.getInstance().getConversationListBehaviorListener() != null) {
            boolean isDealt = RongContext.getInstance().getConversationListBehaviorListener().onConversationLongClick(this.getActivity(), view, uiConversation);
            if (isDealt) {
                return true;
            }
        }

        if (!this.getGatherState(uiConversation.getConversationType())) {
            this.buildMultiDialog(uiConversation);
            return true;
        } else {
            this.buildSingleDialog(uiConversation);
            return true;
        }
    }

    private void buildMultiDialog(final UIConversation uiConversation) {
        String[] items = new String[2];
        if (uiConversation.isTop()) {
            items[0] = RongContext.getInstance().getString(string.rc_conversation_list_dialog_cancel_top);
        } else {
            items[0] = RongContext.getInstance().getString(string.rc_conversation_list_dialog_set_top);
        }

        items[1] = RongContext.getInstance().getString(string.rc_conversation_list_dialog_remove);
        OptionsPopupDialog.newInstance(this.getActivity(), items).setOptionsPopupDialogListener(new OnOptionsItemClickedListener() {
            public void onOptionsItemClicked(int which) {
                if (which == 0) {
                    if (uiConversation.isTop() && uiConversation.getUIConversationTitle().contains("待转运") || uiConversation.isTop() && uiConversation.getUIConversationTitle().contains("转运中")) {
                        Toast.makeText(RongContext.getInstance(), "无法取消进行转运的讨论组", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    RongIM.getInstance().setConversationToTop(uiConversation.getConversationType(), uiConversation.getConversationTargetId(), !uiConversation.isTop(), new ResultCallback<Boolean>() {
                        public void onSuccess(Boolean aBoolean) {
                            if (uiConversation.isTop()) {
                                Toast.makeText(RongContext.getInstance(), getString(string.rc_conversation_list_popup_cancel_top), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RongContext.getInstance(), getString(string.rc_conversation_list_dialog_set_top), Toast.LENGTH_SHORT).show();
                            }

                        }

                        public void onError(ErrorCode e) {
                        }
                    });
                } else if (which == 1) {
                    RongIM.getInstance().removeConversation(uiConversation.getConversationType(), uiConversation.getConversationTargetId(), (ResultCallback) null);
                }

            }
        }).show();
    }

    private void buildSingleDialog(final UIConversation uiConversation) {
        String[] items = new String[]{RongContext.getInstance().getString(string.rc_conversation_list_dialog_remove)};
        OptionsPopupDialog.newInstance(this.getActivity(), items).setOptionsPopupDialogListener(new OnOptionsItemClickedListener() {
            public void onOptionsItemClicked(int which) {
                RongIM.getInstance().getConversationList(new ResultCallback<List<Conversation>>() {
                    public void onSuccess(List<Conversation> conversations) {
                        if (conversations != null && conversations.size() > 0) {
                            Iterator i$ = conversations.iterator();

                            while (i$.hasNext()) {
                                Conversation conversation = (Conversation) i$.next();
                                RongIMClient.getInstance().removeConversation(conversation.getConversationType(), conversation.getTargetId(), (ResultCallback) null);
                            }
                        }

                    }

                    public void onError(ErrorCode errorCode) {
                    }
                }, new ConversationType[]{uiConversation.getConversationType()});
                int position = mAdapter.findGatheredItem(uiConversation.getConversationType());
                mAdapter.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        }).show();
    }

    private void makeUiConversationList(List<Conversation> conversationList) {
        Iterator i$ = conversationList.iterator();

        while (i$.hasNext()) {
            Conversation conversation = (Conversation) i$.next();
            ConversationType conversationType = conversation.getConversationType();
            String targetId = conversation.getTargetId();
            boolean gatherState = this.getGatherState(conversationType);
            UIConversation uiConversation;
            int originalIndex;
            if (gatherState) {
                originalIndex = this.mAdapter.findGatheredItem(conversationType);
                if (originalIndex >= 0) {
                    uiConversation = (UIConversation) this.mAdapter.getItem(originalIndex);
                    uiConversation.updateConversation(conversation, true);
                } else {
                    uiConversation = UIConversation.obtain(conversation, true);
                    this.mAdapter.add(uiConversation);
                }
            } else {
                originalIndex = this.mAdapter.findPosition(conversationType, targetId);
                if (originalIndex < 0) {
                    uiConversation = UIConversation.obtain(conversation, false);
                    this.mAdapter.add(uiConversation);
                } else {
                    uiConversation = (UIConversation) this.mAdapter.getItem(originalIndex);
                    uiConversation.setUnReadMessageCount(conversation.getUnreadMessageCount());
                }
            }
        }

    }

    private UIConversation makeUIConversation(List<Conversation> conversations) {
        int unreadCount = 0;
        boolean topFlag = false;
        boolean isMentioned = false;
        Conversation newest = (Conversation) conversations.get(0);

        Conversation conversation;
        for (Iterator uiConversation = conversations.iterator(); uiConversation.hasNext(); unreadCount += conversation.getUnreadMessageCount()) {
            conversation = (Conversation) uiConversation.next();
            if (newest.isTop()) {
                if (conversation.isTop() && conversation.getSentTime() > newest.getSentTime()) {
                    newest = conversation;
                }
            } else if (conversation.isTop() || conversation.getSentTime() > newest.getSentTime()) {
                newest = conversation;
            }

            if (conversation.isTop()) {
                topFlag = true;
            }

            if (conversation.getMentionedCount() > 0) {
                isMentioned = true;
            }
        }

        UIConversation uiConversation1 = UIConversation.obtain(newest, this.getGatherState(newest.getConversationType()));
        uiConversation1.setUnReadMessageCount(unreadCount);
        uiConversation1.setTop(false);
        uiConversation1.setMentionedFlag(isMentioned);
        return uiConversation1;
    }

    private int getPosition(UIConversation uiConversation) {
        int count = this.mAdapter.getCount();
        int position = 0;

        for (int i = 0; i < count; ++i) {
            if (uiConversation.isTop()) {
                if (!((UIConversation) this.mAdapter.getItem(i)).isTop() || ((UIConversation) this.mAdapter.getItem(i)).getUIConversationTime() <= uiConversation.getUIConversationTime()) {
                    break;
                }

                ++position;
            } else {
                if (!((UIConversation) this.mAdapter.getItem(i)).isTop() && ((UIConversation) this.mAdapter.getItem(i)).getUIConversationTime() <= uiConversation.getUIConversationTime()) {
                    break;
                }

                ++position;
            }
        }

        return position;
    }

    private boolean isConfigured(ConversationType conversationType) {
        for (int i = 0; i < this.mConversationsConfig.size(); ++i) {
            if (conversationType.equals(((ConversationListFragment.ConversationConfig) this.mConversationsConfig.get(i)).conversationType)) {
                return true;
            }
        }

        return false;
    }

    public boolean getGatherState(ConversationType conversationType) {
        Iterator i$ = this.mConversationsConfig.iterator();

        ConversationListFragment.ConversationConfig config;
        do {
            if (!i$.hasNext()) {
                return false;
            }

            config = (ConversationListFragment.ConversationConfig) i$.next();
        } while (!config.conversationType.equals(conversationType));

        return config.isGathered;
    }

    private ConversationType[] getConfigConversationTypes() {
        ConversationType[] conversationTypes = new ConversationType[this.mConversationsConfig.size()];

        for (int i = 0; i < this.mConversationsConfig.size(); ++i) {
            conversationTypes[i] = ((ConversationListFragment.ConversationConfig) this.mConversationsConfig.get(i)).conversationType;
        }

        return conversationTypes;
    }

    public void onDestroy() {
        EventBus.getDefault().unregister(this.mThis);
        getActivity().unregisterReceiver(mSystemMessageReceiver);
        super.onDestroyView();
    }

    private class ConversationConfig {
        ConversationType conversationType;
        boolean isGathered;

        private ConversationConfig() {
        }
    }
}
