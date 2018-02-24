package com.mobileoffice.controller.message;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobileoffice.controller.message.MessageListAdapter.OnItemHandlerListener;
import com.mobileoffice.utils.LogUtil;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import io.rong.common.RLog;
import io.rong.eventbus.EventBus;
import io.rong.imkit.IExtensionClickListener;
import io.rong.imkit.IPublicServiceMenuClickListener;
import io.rong.imkit.InputMenu;
import io.rong.imkit.R.bool;
import io.rong.imkit.R.id;
import io.rong.imkit.R.integer;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongExtension;
import io.rong.imkit.RongIM;
import io.rong.imkit.RongKitReceiver;
import io.rong.imkit.fragment.IHistoryDataResultCallback;
import io.rong.imkit.fragment.UriFragment;
import io.rong.imkit.manager.AudioPlayManager;
import io.rong.imkit.manager.AudioRecordManager;
import io.rong.imkit.manager.InternalModuleManager;
import io.rong.imkit.manager.SendImageManager;
import io.rong.imkit.manager.UnReadMessageManager;
import io.rong.imkit.mention.RongMentionManager;
import io.rong.imkit.model.ConversationInfo;
import io.rong.imkit.model.Event.CSTerminateEvent;
import io.rong.imkit.model.Event.DraftEvent;
import io.rong.imkit.model.Event.FileMessageEvent;
import io.rong.imkit.model.Event.MessageDeleteEvent;
import io.rong.imkit.model.Event.MessageRecallEvent;
import io.rong.imkit.model.Event.MessagesClearEvent;
import io.rong.imkit.model.Event.OnMessageSendErrorEvent;
import io.rong.imkit.model.Event.OnReceiveMessageEvent;
import io.rong.imkit.model.Event.OnReceiveMessageProgressEvent;
import io.rong.imkit.model.Event.PlayAudioEvent;
import io.rong.imkit.model.Event.PublicServiceFollowableEvent;
import io.rong.imkit.model.Event.ReadReceiptEvent;
import io.rong.imkit.model.Event.ReadReceiptRequestEvent;
import io.rong.imkit.model.Event.ReadReceiptResponseEvent;
import io.rong.imkit.model.Event.RemoteMessageRecallEvent;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.plugin.DefaultLocationPlugin;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.plugin.location.AMapRealTimeActivity;
import io.rong.imkit.plugin.location.IRealTimeLocationStateListener;
import io.rong.imkit.plugin.location.IUserInfoProvider;
import io.rong.imkit.plugin.location.LocationManager;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.utilities.PermissionCheckUtil;
import io.rong.imkit.utilities.PromptPopupDialog;
import io.rong.imkit.utilities.PromptPopupDialog.OnPromptButtonClickedListener;
import io.rong.imkit.widget.AutoRefreshListView;
import io.rong.imkit.widget.AutoRefreshListView.Mode;
import io.rong.imkit.widget.AutoRefreshListView.OnRefreshListener;
import io.rong.imkit.widget.AutoRefreshListView.State;
import io.rong.imkit.widget.CSEvaluateDialog;
import io.rong.imkit.widget.CSEvaluateDialog.EvaluateClickListener;
import io.rong.imkit.widget.SingleChoiceDialog;
import io.rong.imkit.widget.provider.EvaluatePlugin;
import io.rong.imlib.CustomServiceConfig;
import io.rong.imlib.CustomServiceConfig.CSEvaEntryPoint;
import io.rong.imlib.CustomServiceConfig.CSEvaType;
import io.rong.imlib.CustomServiceConfig.CSQuitSuspendType;
import io.rong.imlib.ICustomServiceListener;
import io.rong.imlib.IRongCallback.ISendMediaMessageCallback;
import io.rong.imlib.IRongCallback.ISendMessageCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.OperationCallback;
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.RongIMClient.SendImageMessageCallback;
import io.rong.imlib.location.RealTimeLocationConstant.RealTimeLocationStatus;
import io.rong.imlib.model.CSCustomServiceInfo;
import io.rong.imlib.model.CSGroupItem;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Conversation.PublicServiceType;
import io.rong.imlib.model.CustomServiceMode;
import io.rong.imlib.model.MentionedInfo;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.Message.MessageDirection;
import io.rong.imlib.model.Message.SentStatus;
import io.rong.imlib.model.PublicServiceMenu;
import io.rong.imlib.model.PublicServiceMenu.PublicServiceMenuItemType;
import io.rong.imlib.model.PublicServiceMenuItem;
import io.rong.imlib.model.PublicServiceProfile;
import io.rong.imlib.model.ReadReceiptInfo;
import io.rong.imlib.model.UserInfo;
import io.rong.message.CSPullLeaveMessage;
import io.rong.message.FileMessage;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.LocationMessage;
import io.rong.message.MediaMessageContent;
import io.rong.message.PublicServiceCommandMessage;
import io.rong.message.ReadReceiptMessage;
import io.rong.message.RecallNotificationMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import io.rong.push.RongPushClient;

public class ConversationFragment extends UriFragment implements OnScrollListener, IExtensionClickListener, IUserInfoProvider, EvaluateClickListener {
    private static final String TAG = "ConversationFragment";
    private PublicServiceProfile mPublicServiceProfile;
    private RongExtension mRongExtension;
    private boolean mEnableMention;
    private float mLastTouchY;
    private boolean mUpDirection;
    private float mOffsetLimit;
    private boolean finishing = false;
    private CSCustomServiceInfo mCustomUserInfo;
    private ConversationInfo mCurrentConversationInfo;
    private String mDraft;
    private String mTargetId;
    private ConversationType mConversationType;
    private boolean mReadRec;
    private boolean mSyncReadStatus;
    private int mNewMessageCount;
    private AutoRefreshListView mList;
    private Button mUnreadBtn;
    private ImageButton mNewMessageBtn;
    private TextView mNewMessageTextView;
    public static MessageListAdapter mListAdapter;
    private View mMsgListView;
    private LinearLayout mNotificationContainer;
    private boolean mHasMoreLocalMessages;
    private int mLastMentionMsgId;
    private long mSyncReadStatusMsgTime;
    private boolean mCSNeedToQuit = false;
    private List<String> mLocationShareParticipants;
    private CustomServiceConfig mCustomServiceConfig;
    private CSEvaluateDialog mEvaluateDialg;
    private RongKitReceiver mKitReceiver;

    private boolean robotType = true;
    private long csEnterTime;
    private boolean csEvaluate = true;

    ICustomServiceListener customServiceListener = new ICustomServiceListener() {
        public void onSuccess(CustomServiceConfig config) {
            mCustomServiceConfig = config;
            if (config.isBlack) {
                onCustomServiceWarning(getString(string.rc_blacklist_prompt), false, robotType);
            }

            if (config.robotSessionNoEva) {
                csEvaluate = false;
                mListAdapter.setEvaluateForRobot(true);
            }

            if (mRongExtension != null) {

                if (config.evaEntryPoint.equals(CSEvaEntryPoint.EVA_EXTENSION)) {
                    mRongExtension.addPlugin(new EvaluatePlugin(mCustomServiceConfig.isReportResolveStatus));
                }

                if (config.isDisableLocation) {
                    List i = mRongExtension.getPluginModules();
                    IPluginModule uiMessage = null;

                    for (int i1 = 0; i1 < i.size(); ++i1) {
                        if (i.get(i1) instanceof DefaultLocationPlugin) {
                            uiMessage = (IPluginModule) i.get(i1);
                        }
                    }

                    if (uiMessage != null) {
                        mRongExtension.removePlugin(uiMessage);
                    }
                }
            }

            if (config.quitSuspendType.equals(CSQuitSuspendType.NONE)) {
                try {
                    mCSNeedToQuit = RongContext.getInstance().getResources().getBoolean(bool.rc_stop_custom_service_when_quit);
                } catch (NotFoundException var5) {
                    var5.printStackTrace();
                }
            } else {
                mCSNeedToQuit = config.quitSuspendType.equals(CSQuitSuspendType.SUSPEND);
            }

            for (int var6 = 0; var6 < mListAdapter.getCount(); ++var6) {
                UIMessage var7 = (UIMessage) mListAdapter.getItem(var6);
                if (var7.getContent() instanceof CSPullLeaveMessage) {
                    var7.setCsConfig(config);
                }
            }

            mListAdapter.notifyDataSetChanged();
            LogUtil.e(TAG, "gg");


        }

        public void onError(int code, String msg) {
            onCustomServiceWarning(msg, false, robotType);
        }

        public void onModeChanged(CustomServiceMode mode) {

            if (mRongExtension != null) {
                mRongExtension.setExtensionBarMode(mode);
                if (!mode.equals(CustomServiceMode.CUSTOM_SERVICE_MODE_HUMAN) && !mode.equals(CustomServiceMode.CUSTOM_SERVICE_MODE_HUMAN_FIRST)) {
                    if (mode.equals(CustomServiceMode.CUSTOM_SERVICE_MODE_NO_SERVICE)) {
                        csEvaluate = false;
                    }
                } else {
                    if (mCustomServiceConfig.userTipTime > 0 && !TextUtils.isEmpty(mCustomServiceConfig.userTipWord)) {
                        startTimer(0, mCustomServiceConfig.userTipTime * 60 * 1000);
                    }

                    if (mCustomServiceConfig.adminTipTime > 0 && !TextUtils.isEmpty(mCustomServiceConfig.adminTipWord)) {
                        startTimer(1, mCustomServiceConfig.adminTipTime * 60 * 1000);
                    }

                    robotType = false;
                    csEvaluate = true;
                }

            }
        }

        public void onQuit(String msg) {
            RLog.i("ConversationFragment", "CustomService onQuit.");
            if (getHandler() != null) {
                getHandler().removeCallbacksAndMessages((Object) null);
            }

            if (mEvaluateDialg == null) {
                onCustomServiceWarning(msg, mCustomServiceConfig.quitSuspendType == CSQuitSuspendType.NONE, robotType);
            } else {
                mEvaluateDialg.destroy();
            }

            if (!mCustomServiceConfig.quitSuspendType.equals(CSQuitSuspendType.NONE)) {
                RongContext.getInstance().getEventBus().post(new CSTerminateEvent(getActivity(), msg));
            }

        }

        public void onPullEvaluation(String dialogId) {
            if (mEvaluateDialg == null) {
                onCustomServiceEvaluation(true, dialogId, robotType, csEvaluate);
            }

        }

        public void onSelectGroup(List<CSGroupItem> groups) {
            onSelectCustomerServiceGroup(groups);
        }
    };

    public ConversationFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ToastUtil.showToast("lalalala");
        RLog.i("ConversationFragment", "onCreate");
        InternalModuleManager.getInstance().onLoaded();

        try {
            this.mEnableMention = this.getActivity().getResources().getBoolean(bool.rc_enable_mentioned_message);
        } catch (NotFoundException var6) {
            RLog.e("ConversationFragment", "rc_enable_mentioned_message not found in rc_config.xml");
        }

        try {
            this.mReadRec = this.getResources().getBoolean(bool.rc_read_receipt);
            this.mSyncReadStatus = this.getResources().getBoolean(bool.rc_enable_sync_read_status);
        } catch (NotFoundException var5) {
            RLog.e("ConversationFragment", "rc_read_receipt not found in rc_config.xml");
            var5.printStackTrace();
        }

        this.mKitReceiver = new RongKitReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PHONE_STATE");
        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");

        try {
            this.getActivity().registerReceiver(this.mKitReceiver, filter);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //oastUtil.showToast("lalalala",getActivity());
        View view = inflater.inflate(layout.rc_fr_conversation, container, false);
        this.mRongExtension = (RongExtension) view.findViewById(id.rc_extension);
        LogUtil.e(TAG, "gg:" + CustomServiceMode.CUSTOM_SERVICE_MODE_NO_SERVICE);
        this.mRongExtension.setFragment(this);
        this.mOffsetLimit = 70.0F * this.getActivity().getResources().getDisplayMetrics().density;
        this.mMsgListView = this.findViewById(view, id.rc_layout_msg_list);
        this.mList = (AutoRefreshListView) this.findViewById(this.mMsgListView, id.rc_list);
        this.mList.requestDisallowInterceptTouchEvent(true);
        this.mList.setMode(Mode.START);
        this.mList.setTranscriptMode(2);
        this.mListAdapter = this.onResolveAdapter(this.getActivity());
        this.mList.setAdapter(this.mListAdapter);
        this.mList.setOnRefreshListener(new OnRefreshListener() {
            public void onRefreshFromStart() {
                if (mHasMoreLocalMessages) {
                    getHistoryMessage(mConversationType, mTargetId, 30, 1);
                    LogUtil.e(TAG, "gg1");
                } else {
                    getRemoteHistoryMessages(mConversationType, mTargetId, 10);
                }

            }

            public void onRefreshFromEnd() {
            }
        });
        this.mList.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == 2 && mList.getCount() - mList.getHeaderViewsCount() == 0) {
                    if (mHasMoreLocalMessages) {
                        getHistoryMessage(mConversationType, mTargetId, 30, 1);
                    } else if (mList.getRefreshState() != State.REFRESHING) {
                        getRemoteHistoryMessages(mConversationType, mTargetId, 10);
                    }

                    return true;
                } else {
                    if (event.getAction() == 1 && mRongExtension != null && mRongExtension.isExtensionExpanded()) {

                        mRongExtension.collapseExtension();
                    }

                    return false;
                }
            }
        });
        if (RongContext.getInstance().getNewMessageState()) {
            this.mNewMessageTextView = (TextView) this.findViewById(view, id.rc_new_message_number);
            this.mNewMessageBtn = (ImageButton) this.findViewById(view, id.rc_new_message_count);
            this.mNewMessageBtn.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    mList.smoothScrollToPosition(mList.getCount() + 1);
                    mNewMessageBtn.setVisibility(View.GONE);
                    mNewMessageTextView.setVisibility(View.GONE);
                    mNewMessageCount = 0;

                }
            });
        }

        if (RongContext.getInstance().getUnreadMessageState()) {
            this.mUnreadBtn = (Button) this.findViewById(this.mMsgListView, id.rc_unread_message_count);
        }

        this.mList.addOnScrollListener(this);
        this.mListAdapter.setOnItemHandlerListener(new OnItemHandlerListener() {
            public boolean onWarningViewClick(final int position, final Message data, View v) {
                if (!onResendItemClick(data)) {
                    RongIMClient.getInstance().deleteMessages(new int[]{data.getMessageId()}, new ResultCallback<Boolean>() {
                        public void onSuccess(Boolean aBoolean) {
                            if (aBoolean.booleanValue()) {
                                mListAdapter.remove(position);
                                data.setMessageId(0);
                                if (data.getContent() instanceof ImageMessage) {
                                    RongIM.getInstance().sendImageMessage(data, (String) null, (String) null, (SendImageMessageCallback) null);
                                } else if (data.getContent() instanceof LocationMessage) {
                                    RongIM.getInstance().sendLocationMessage(data, (String) null, (String) null, (ISendMessageCallback) null);
                                } else if (data.getContent() instanceof MediaMessageContent) {
                                    RongIM.getInstance().sendMediaMessage(data, (String) null, (String) null, (ISendMediaMessageCallback) null);
                                } else {
                                    RongIM.getInstance().sendMessage(data, (String) null, (String) null, (ISendMessageCallback) null);
                                }
                            }

                        }

                        public void onError(ErrorCode e) {
                        }
                    });
                }

                return true;
            }

            public void onReadReceiptStateClick(Message message) {
                onReadReceiptStateClick(message);
            }
        });
        return view;
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == 1) {
            if (this.mRongExtension != null) {
                this.mRongExtension.collapseExtension();
            }
        } else if (scrollState == 0) {
            int last = this.mList.getLastVisiblePosition();
            if (this.mList.getCount() - last > 2) {
                this.mList.setTranscriptMode(1);
            } else {
                this.mList.setTranscriptMode(2);
            }

            if (this.mNewMessageBtn != null && last == this.mList.getCount() - 1) {
                this.mNewMessageCount = 0;
                this.mNewMessageBtn.setVisibility(View.GONE);
                this.mNewMessageTextView.setVisibility(View.GONE);
            }
        }

    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    public void onResume() {
        RongPushClient.clearAllPushNotifications(this.getActivity());
        super.onResume();
    }

    public final void getUserInfo(String userId, UserInfoCallback callback) {
        UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(userId);
        if (userInfo != null) {
            callback.onGotUserInfo(userInfo);
        }

    }

    public MessageListAdapter onResolveAdapter(Context context) {
        return new MessageListAdapter(context);
    }

    protected void initFragment(Uri uri) {
        RLog.d("ConversationFragment", "initFragment : " + uri + ",this=" + this);
        if (uri != null) {
            String typeStr = uri.getLastPathSegment().toUpperCase(Locale.US);
            this.mConversationType = ConversationType.valueOf(typeStr);
            this.mTargetId = uri.getQueryParameter("targetId");
            this.getHistoryMessage(this.mConversationType, this.mTargetId, 30, 3);
            this.mRongExtension.setConversation(this.mConversationType, this.mTargetId);
            RongIMClient.getInstance().getTextMessageDraft(this.mConversationType, this.mTargetId, new ResultCallback<String>() {
                public void onSuccess(String s) {
                    mDraft = s;
                    if (mRongExtension != null) {
                        EditText editText = mRongExtension.getInputEditText();
                        editText.setText(s);
                        editText.setSelection(editText.length());
                        mRongExtension.setExtensionClickListener(ConversationFragment.this);
                    }

                }

                public void onError(ErrorCode e) {

                    if (mRongExtension != null) {
                        mRongExtension.setExtensionClickListener(ConversationFragment.this);
                    }

                }
            });
            this.mCurrentConversationInfo = ConversationInfo.obtain(this.mConversationType, this.mTargetId);
            RongContext.getInstance().registerConversationInfo(this.mCurrentConversationInfo);
            this.mNotificationContainer = (LinearLayout) this.mMsgListView.findViewById(id.rc_notification_container);
            if (this.mConversationType.equals(ConversationType.CUSTOMER_SERVICE) && this.getActivity() != null && this.getActivity().getIntent() != null && this.getActivity().getIntent().getData() != null) {
                this.mCustomUserInfo = (CSCustomServiceInfo) this.getActivity().getIntent().getParcelableExtra("customServiceInfo");
            }

            LocationManager.getInstance().bindConversation(this.getActivity(), this.mConversationType, this.mTargetId);
            LocationManager.getInstance().setUserInfoProvider(this);
            LocationManager.getInstance().setParticipantChangedListener(new IRealTimeLocationStateListener() {
                private View mRealTimeBar;
                private TextView mRealTimeText;

                public void onParticipantChanged(List<String> userIdList) {
                    if (!isDetached()) {
                        if (this.mRealTimeBar == null) {
                            this.mRealTimeBar = inflateNotificationView(layout.rc_notification_realtime_location);
                            this.mRealTimeText = (TextView) this.mRealTimeBar.findViewById(id.real_time_location_text);
                            this.mRealTimeBar.setOnClickListener(new OnClickListener() {
                                public void onClick(View v) {
                                    RealTimeLocationStatus status = RongIMClient.getInstance().getRealTimeLocationCurrentState(mConversationType, mTargetId);
                                    if (status == RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_INCOMING) {
                                        PromptPopupDialog intent = PromptPopupDialog.newInstance(getActivity(), "", getResources().getString(string.rc_real_time_join_notification));
                                        intent.setPromptButtonClickedListener(new OnPromptButtonClickedListener() {
                                            public void onPositiveButtonClicked() {
                                                int result = LocationManager.getInstance().joinLocationSharing();
                                                if (result == 0) {
                                                    Intent intent = new Intent(getActivity(), AMapRealTimeActivity.class);
                                                    if (mLocationShareParticipants != null) {
                                                        intent.putStringArrayListExtra("participants", (ArrayList) mLocationShareParticipants);
                                                    }

                                                    startActivity(intent);
                                                } else if (result == 1) {
                                                    Toast.makeText(getActivity(), string.rc_network_exception, Toast.LENGTH_SHORT).show();
                                                } else if (result == 2) {
                                                    Toast.makeText(getActivity(), string.rc_location_sharing_exceed_max, Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });
                                        intent.show();
                                    } else {
                                        Intent intent1 = new Intent(getActivity(), AMapRealTimeActivity.class);
                                        if (mLocationShareParticipants != null) {
                                            intent1.putStringArrayListExtra("participants", (ArrayList) mLocationShareParticipants);
                                        }

                                        startActivity(intent1);
                                    }

                                }
                            });
                        }

                        mLocationShareParticipants = userIdList;
                        if (userIdList != null) {
                            if (userIdList.size() == 0) {
                                hideNotificationView(this.mRealTimeBar);
                            } else {
                                if (userIdList.size() == 1 && userIdList.contains(RongIM.getInstance().getCurrentUserId())) {
                                    this.mRealTimeText.setText(getResources().getString(string.rc_you_are_sharing_location));
                                } else if (userIdList.size() == 1 && !userIdList.contains(RongIM.getInstance().getCurrentUserId())) {
                                    this.mRealTimeText.setText(String.format(getResources().getString(string.rc_other_is_sharing_location), new Object[]{getNameFromCache((String) userIdList.get(0))}));
                                } else {
                                    this.mRealTimeText.setText(String.format(getResources().getString(string.rc_others_are_sharing_location), new Object[]{Integer.valueOf(userIdList.size())}));
                                }

                                showNotificationView(this.mRealTimeBar);
                            }
                        } else {
                            hideNotificationView(this.mRealTimeBar);
                        }

                    }
                }

                public void onErrorException() {
                    if (!isDetached()) {
                        hideNotificationView(this.mRealTimeBar);
                        if (mLocationShareParticipants != null) {
                            mLocationShareParticipants.clear();
                            mLocationShareParticipants = null;
                        }
                    }

                }
            });
            if (this.mConversationType.equals(ConversationType.CHATROOM)) {
                boolean msg = this.getActivity() != null && this.getActivity().getIntent().getBooleanExtra("createIfNotExist", true);
                int message = this.getResources().getInteger(integer.rc_chatroom_first_pull_message_count);
                if (msg) {
                    RongIMClient.getInstance().joinChatRoom(this.mTargetId, message, new OperationCallback() {
                        public void onSuccess() {
                            RLog.i("ConversationFragment", "joinChatRoom onSuccess : " + mTargetId);
                        }

                        public void onError(ErrorCode errorCode) {
                            RLog.e("ConversationFragment", "joinChatRoom onError : " + errorCode);
                            if (getActivity() != null) {
                                if (errorCode != ErrorCode.RC_NET_UNAVAILABLE && errorCode != ErrorCode.RC_NET_CHANNEL_INVALID) {
                                    onWarningDialog(getString(string.rc_join_chatroom_failure));
                                } else {
                                    onWarningDialog(getString(string.rc_notice_network_unavailable));
                                }
                            }

                        }
                    });
                } else {
                    RongIMClient.getInstance().joinExistChatRoom(this.mTargetId, message, new OperationCallback() {
                        public void onSuccess() {
                            RLog.i("ConversationFragment", "joinExistChatRoom onSuccess : " + mTargetId);
                        }

                        public void onError(ErrorCode errorCode) {
                            RLog.e("ConversationFragment", "joinExistChatRoom onError : " + errorCode);
                            if (getActivity() != null) {
                                if (errorCode != ErrorCode.RC_NET_UNAVAILABLE && errorCode != ErrorCode.RC_NET_CHANNEL_INVALID) {
                                    onWarningDialog(getString(string.rc_join_chatroom_failure));
                                } else {
                                    onWarningDialog(getString(string.rc_notice_network_unavailable));
                                }
                            }

                        }
                    });
                }
            } else if (this.mConversationType != ConversationType.APP_PUBLIC_SERVICE && this.mConversationType != ConversationType.PUBLIC_SERVICE) {
                if (this.mConversationType.equals(ConversationType.CUSTOMER_SERVICE)) {
                    this.onStartCustomService(this.mTargetId);
                } else if (this.mEnableMention && (this.mConversationType.equals(ConversationType.DISCUSSION) || this.mConversationType.equals(ConversationType.GROUP))) {
                    RongMentionManager.getInstance().createInstance(this.mConversationType, this.mTargetId, this.mRongExtension.getInputEditText());
                }
            } else {
                PublicServiceCommandMessage msg1 = new PublicServiceCommandMessage();
                msg1.setCommand(PublicServiceMenuItemType.Entry.getMessage());
                Message message1 = Message.obtain(this.mTargetId, this.mConversationType, msg1);
                RongIMClient.getInstance().sendMessage(message1, (String) null, (String) null, new ISendMessageCallback() {
                    public void onAttached(Message message) {
                    }

                    public void onSuccess(Message message) {
                    }

                    public void onError(Message message, ErrorCode errorCode) {
                    }
                });
                PublicServiceType publicServiceType;
                if (this.mConversationType == ConversationType.PUBLIC_SERVICE) {
                    publicServiceType = PublicServiceType.PUBLIC_SERVICE;
                } else {
                    publicServiceType = PublicServiceType.APP_PUBLIC_SERVICE;
                }

                RongIM.getInstance().getPublicServiceProfile(publicServiceType, this.mTargetId, new ResultCallback<PublicServiceProfile>() {
                    public void onSuccess(PublicServiceProfile publicServiceProfile) {
                        ArrayList inputMenuList = new ArrayList();
                        PublicServiceMenu menu = publicServiceProfile.getMenu();
                        ArrayList items = menu != null ? menu.getMenuItems() : null;
                        if (items != null && mRongExtension != null) {
                            mPublicServiceProfile = publicServiceProfile;
                            Iterator i$ = items.iterator();

                            while (i$.hasNext()) {
                                PublicServiceMenuItem item = (PublicServiceMenuItem) i$.next();
                                InputMenu inputMenu = new InputMenu();
                                inputMenu.title = item.getName();
                                inputMenu.subMenuList = new ArrayList();
                                Iterator i$1 = item.getSubMenuItems().iterator();

                                while (i$1.hasNext()) {
                                    PublicServiceMenuItem i = (PublicServiceMenuItem) i$1.next();
                                    inputMenu.subMenuList.add(i.getName());
                                }

                                inputMenuList.add(inputMenu);
                            }

                            mRongExtension.setInputMenu(inputMenuList, true);
                        }

                    }

                    public void onError(ErrorCode e) {
                    }
                });
            }
        }

        RongIMClient.getInstance().getConversation(this.mConversationType, this.mTargetId, new ResultCallback<Conversation>() {
            public void onSuccess(Conversation conversation) {
                if (conversation != null && getActivity() != null) {
                    final int unreadCount = conversation.getUnreadMessageCount();
                    if (unreadCount > 0) {
                        if (mReadRec && mConversationType == ConversationType.PRIVATE && RongContext.getInstance().isReadReceiptConversationType(ConversationType.PRIVATE)) {
                            RongIMClient.getInstance().sendReadReceiptMessage(mConversationType, mTargetId, conversation.getSentTime());
                        }

                        if (mSyncReadStatus && (mConversationType == ConversationType.PRIVATE || mConversationType == ConversationType.GROUP || mConversationType == ConversationType.DISCUSSION)) {
                            RongIMClient.getInstance().syncConversationReadStatus(mConversationType, mTargetId, conversation.getSentTime(), (OperationCallback) null);
                        }
                    }

                    if (conversation.getMentionedCount() > 0) {
                        getLastMentionedMessageId(mConversationType, mTargetId);
                    } else {
                        RongIM.getInstance().clearMessagesUnreadStatus(mConversationType, mTargetId, (ResultCallback) null);
                    }

                    if (unreadCount > 10 && mUnreadBtn != null) {
                        if (unreadCount > 150) {
                            mUnreadBtn.setText(String.format("%s%s", new Object[]{"150+", getActivity().getResources().getString(string.rc_new_messages)}));
                        } else {
                            mUnreadBtn.setText(String.format("%s%s", new Object[]{Integer.valueOf(unreadCount), getActivity().getResources().getString(string.rc_new_messages)}));
                        }

                        mUnreadBtn.setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                mUnreadBtn.setClickable(false);
                                TranslateAnimation animation = new TranslateAnimation(0.0F, 500.0F, 0.0F, 0.0F);
                                animation.setDuration(500L);
                                mUnreadBtn.startAnimation(animation);
                                animation.setFillAfter(true);
                                animation.setAnimationListener(new AnimationListener() {
                                    public void onAnimationStart(Animation animation) {
                                    }

                                    public void onAnimationEnd(Animation animation) {
                                        mUnreadBtn.setVisibility(View.GONE);
                                        if (unreadCount <= 30) {
                                            if (mList.getCount() < 30) {
                                                mList.smoothScrollToPosition(mList.getCount() - unreadCount);
                                            } else {
                                                mList.smoothScrollToPosition(30 - unreadCount);
                                            }
                                        } else if (unreadCount > 30) {
                                            getHistoryMessage(mConversationType, mTargetId, unreadCount - 30 - 1, 2);

                                        }

                                    }

                                    public void onAnimationRepeat(Animation animation) {
                                    }
                                });
                            }
                        });
                        TranslateAnimation translateAnimation = new TranslateAnimation(300.0F, 0.0F, 0.0F, 0.0F);
                        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0F, 1.0F);
                        translateAnimation.setDuration(1000L);
                        alphaAnimation.setDuration(2000L);
                        AnimationSet set = new AnimationSet(true);
                        set.addAnimation(translateAnimation);
                        set.addAnimation(alphaAnimation);
                        mUnreadBtn.setVisibility(View.VISIBLE);
                        mUnreadBtn.startAnimation(set);
                        set.setAnimationListener(new AnimationListener() {
                            public void onAnimationStart(Animation animation) {
                            }

                            public void onAnimationEnd(Animation animation) {
                                getHandler().postDelayed(new Runnable() {
                                    public void run() {
                                        TranslateAnimation animation = new TranslateAnimation(0.0F, 700.0F, 0.0F, 0.0F);
                                        animation.setDuration(700L);
                                        animation.setFillAfter(true);
                                        mUnreadBtn.startAnimation(animation);
                                    }
                                }, 4000L);
                            }

                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                    }
                }

            }

            public void onError(ErrorCode e) {
            }
        });
        this.getHistoryMessage(this.mConversationType, this.mTargetId, 30, 3);
        LogUtil.e(TAG, "gg12");
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    public void hideNotificationView(View notificationView) {
        if (notificationView != null) {
            View view = this.mNotificationContainer.findViewById(notificationView.getId());
            if (view != null) {
                this.mNotificationContainer.removeView(view);
                if (this.mNotificationContainer.getChildCount() == 0) {
                    this.mNotificationContainer.setVisibility(View.GONE);
                }
            }

        }
    }

    public void showNotificationView(View notificationView) {
        if (notificationView != null) {
            View view = this.mNotificationContainer.findViewById(notificationView.getId());
            if (view == null) {
                this.mNotificationContainer.addView(notificationView);
                this.mNotificationContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    public View inflateNotificationView(@LayoutRes int layout) {
        return LayoutInflater.from(this.getActivity()).inflate(layout, this.mNotificationContainer, false);
    }

    public boolean onResendItemClick(Message message) {
        return false;
    }

    public void onReadReceiptStateClick(Message message) {
    }

    public void onSelectCustomerServiceGroup(final List<CSGroupItem> groupList) {
        if (this.getActivity() != null) {
            ArrayList singleDataList = new ArrayList();
            singleDataList.clear();

            for (int i = 0; i < groupList.size(); ++i) {
                if (((CSGroupItem) groupList.get(i)).getOnline()) {
                    singleDataList.add(((CSGroupItem) groupList.get(i)).getName());
                }
            }

            if (singleDataList.size() == 0) {
                RongIMClient.getInstance().selectCustomServiceGroup(this.mTargetId, (String) null);
            } else {
                final SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(this.getActivity(), singleDataList);
                singleChoiceDialog.setTitle(this.getActivity().getResources().getString(string.rc_cs_select_group));
                singleChoiceDialog.setOnOKButtonListener(new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int selItem = singleChoiceDialog.getSelectItem();
                        RongIMClient.getInstance().selectCustomServiceGroup(mTargetId, ((CSGroupItem) groupList.get(selItem)).getId());
                    }
                });
                singleChoiceDialog.setOnCancelButtonListener(new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        RongIMClient.getInstance().selectCustomServiceGroup(mTargetId, (String) null);
                    }
                });
                singleChoiceDialog.show();
            }
        }
    }

    public void onPause() {
        this.finishing = this.getActivity().isFinishing();
        if (this.finishing) {
            this.destroy();
        }

        super.onPause();
    }

    private void destroy() {
        RongIM.getInstance().clearMessagesUnreadStatus(this.mConversationType, this.mTargetId, (ResultCallback) null);
        if (this.getHandler() != null) {
            this.getHandler().removeCallbacksAndMessages((Object) null);
        }

        if (this.mEnableMention && (this.mConversationType.equals(ConversationType.DISCUSSION) || this.mConversationType.equals(ConversationType.GROUP))) {
            RongMentionManager.getInstance().destroyInstance(this.mConversationType, this.mTargetId);
        }

        if (this.mConversationType.equals(ConversationType.CHATROOM)) {
            SendImageManager.getInstance().cancelSendingImages(this.mConversationType, this.mTargetId);
            RongIM.getInstance().quitChatRoom(this.mTargetId, (OperationCallback) null);
        }

        if (this.mConversationType.equals(ConversationType.CUSTOMER_SERVICE) && this.mCSNeedToQuit) {
            this.onStopCustomService(this.mTargetId);
        }

        if (this.mSyncReadStatus && this.mSyncReadStatusMsgTime > 0L && (this.mConversationType.equals(ConversationType.DISCUSSION) || this.mConversationType.equals(ConversationType.GROUP))) {
            RongIMClient.getInstance().syncConversationReadStatus(this.mConversationType, this.mTargetId, this.mSyncReadStatusMsgTime, (OperationCallback) null);
        }

        EventBus.getDefault().unregister(this);
        AudioPlayManager.getInstance().stopPlay();
        AudioRecordManager.getInstance().destroyRecord();

        try {
            if (this.mKitReceiver != null) {
                this.getActivity().unregisterReceiver(this.mKitReceiver);
            }
        } catch (Exception var2) {
            var2.printStackTrace();
        }

        RongContext.getInstance().unregisterConversationInfo(this.mCurrentConversationInfo);
        LocationManager.getInstance().quitLocationSharing();
        LocationManager.getInstance().setParticipantChangedListener((IRealTimeLocationStateListener) null);
        LocationManager.getInstance().setUserInfoProvider((IUserInfoProvider) null);
        LocationManager.getInstance().unBindConversation();
        this.destroyExtension();
    }

    private void destroyExtension() {
        String text = this.mRongExtension.getInputEditText().getText().toString();
        if (TextUtils.isEmpty(text) && !TextUtils.isEmpty(this.mDraft) || !TextUtils.isEmpty(text) && TextUtils.isEmpty(this.mDraft) || !TextUtils.isEmpty(text) && !TextUtils.isEmpty(this.mDraft) && !text.equals(this.mDraft)) {
            RongIMClient.getInstance().saveTextMessageDraft(this.mConversationType, this.mTargetId, text, (ResultCallback) null);
            DraftEvent draft = new DraftEvent(this.mConversationType, this.mTargetId, text);
            RongContext.getInstance().getEventBus().post(draft);
        }

        this.mRongExtension.onDestroy();
        this.mRongExtension = null;
    }

    public void onDestroy() {
        if (!this.finishing) {
            this.destroy();
        }

        super.onDestroy();
    }

    public boolean isLocationSharing() {
        return LocationManager.getInstance().isSharing();
    }

    public void showQuitLocationSharingDialog(final Activity activity) {
        PromptPopupDialog.newInstance(activity, this.getString(string.rc_ext_warning), this.getString(string.rc_real_time_exit_notification), this.getString(string.rc_action_bar_ok)).setPromptButtonClickedListener(new OnPromptButtonClickedListener() {
            public void onPositiveButtonClicked() {
                activity.finish();
            }
        }).show();
    }

    public boolean onBackPressed() {

        if (this.mRongExtension != null && this.mRongExtension.isExtensionExpanded()) {

            this.mRongExtension.collapseExtension();
            return true;
        } else {
            return this.mConversationType != null && this.mCustomServiceConfig != null && this.mConversationType.equals(ConversationType.CUSTOMER_SERVICE) && this.mCustomServiceConfig.quitSuspendType.equals(CSQuitSuspendType.NONE) ? this.onCustomServiceEvaluation(false, "", this.robotType, this.csEvaluate) : false;
        }
    }

    public boolean handleMessage(android.os.Message msg) {
        InformationNotificationMessage info;
        switch (msg.what) {
            case 0:
                if (this.getActivity() != null && this.mCustomServiceConfig != null) {
                    info = new InformationNotificationMessage(this.mCustomServiceConfig.userTipWord);
                    RongIM.getInstance().insertMessage(ConversationType.CUSTOMER_SERVICE, this.mTargetId, this.mTargetId, info, System.currentTimeMillis(), (ResultCallback) null);
                    return true;
                }

                return true;
            case 1:
                if (this.getActivity() != null && this.mCustomServiceConfig != null) {
                    info = new InformationNotificationMessage(this.mCustomServiceConfig.adminTipWord);
                    RongIM.getInstance().insertMessage(ConversationType.CUSTOMER_SERVICE, this.mTargetId, this.mTargetId, info, System.currentTimeMillis(), (ResultCallback) null);
                    return true;
                }

                return true;
            default:
                return false;
        }
    }

    public void onWarningDialog(String msg) {
        Builder builder = new Builder(this.getActivity());
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(layout.rc_cs_alert_warning);
        TextView tv = (TextView) window.findViewById(id.rc_cs_msg);
        tv.setText(msg);
        window.findViewById(id.rc_btn_ok).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                alertDialog.dismiss();
                FragmentManager fm = getChildFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                } else {
                    getActivity().finish();
                }

            }
        });
    }

    public void onCustomServiceWarning(String msg, final boolean evaluate, final boolean robotType) {
        if (this.getActivity() != null) {
            Builder builder = new Builder(this.getActivity());
            builder.setCancelable(false);
            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
            Window window = alertDialog.getWindow();
            window.setContentView(layout.rc_cs_alert_warning);
            TextView tv = (TextView) window.findViewById(id.rc_cs_msg);
            tv.setText(msg);
            window.findViewById(id.rc_btn_ok).setOnClickListener(new OnClickListener() {
                @SuppressWarnings("WrongConstant")
                public void onClick(View v) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService("input_method");
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    alertDialog.dismiss();
                    if (evaluate) {
                        onCustomServiceEvaluation(false, "", robotType, evaluate);
                    } else {
                        FragmentManager fm = getChildFragmentManager();
                        if (fm.getBackStackEntryCount() > 0) {
                            fm.popBackStack();
                        } else {
                            getActivity().finish();
                        }
                    }

                }
            });
        }
    }

    @SuppressWarnings("WrongConstant")
    public boolean onCustomServiceEvaluation(boolean isPullEva, String dialogId, boolean robotType, boolean evaluate) {
        if (evaluate && this.getActivity() != null) {
            long currentTime = System.currentTimeMillis();
            int interval = 60;

            try {
                interval = this.getActivity().getResources().getInteger(integer.rc_custom_service_evaluation_interval);
            } catch (NotFoundException var10) {
                var10.printStackTrace();
            }

            if (currentTime - this.csEnterTime < (long) (interval * 1000) && !isPullEva) {
                InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService("input_method");
                if (imm != null && imm.isActive() && this.getActivity().getCurrentFocus() != null && this.getActivity().getCurrentFocus().getWindowToken() != null) {
                    imm.hideSoftInputFromWindow(this.getActivity().getCurrentFocus().getWindowToken(), 2);
                }

                FragmentManager fm = this.getChildFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                } else {
                    this.getActivity().finish();
                }

                return false;
            }

            this.mEvaluateDialg = new CSEvaluateDialog(this.getContext(), this.mTargetId);
            this.mEvaluateDialg.setClickListener(this);
            this.mEvaluateDialg.setOnCancelListener(new OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    if (mEvaluateDialg != null) {
                        mEvaluateDialg = null;
                    }

                }
            });
            if (this.mCustomServiceConfig.evaluateType.equals(CSEvaType.EVA_UNIFIED)) {
                this.mEvaluateDialg.showStarMessage(this.mCustomServiceConfig.isReportResolveStatus);
            } else if (robotType) {
                this.mEvaluateDialg.showRobot(true);
            } else {
                this.mEvaluateDialg.showStar(dialogId);
            }
        }

        return true;
    }

    public void onSendToggleClick(View v, String text) {
        if (!TextUtils.isEmpty(text) && !TextUtils.isEmpty(text.trim())) {
            TextMessage textMessage = TextMessage.obtain(text);
            MentionedInfo mentionedInfo = RongMentionManager.getInstance().onSendButtonClick();
            if (mentionedInfo != null) {
                textMessage.setMentionedInfo(mentionedInfo);
            }

            Message message = Message.obtain(this.mTargetId, this.mConversationType, textMessage);
            RongIM.getInstance().sendMessage(message, (String) null, (String) null, (ISendMessageCallback) null);
        } else {
            RLog.e("ConversationFragment", "text content must not be null");
        }
    }

    public void onImageResult(List<Uri> selectedImages, boolean origin) {
        SendImageManager.getInstance().sendImages(this.mConversationType, this.mTargetId, selectedImages, origin);
        if (this.mConversationType.equals(ConversationType.PRIVATE)) {
            RongIMClient.getInstance().sendTypingStatus(this.mConversationType, this.mTargetId, "RC:ImgMsg");
        }

    }

    public void onEditTextClick(EditText editText) {
    }

    public void onLocationResult(double lat, double lng, String poi, Uri thumb) {
        LocationMessage locationMessage = LocationMessage.obtain(lat, lng, poi, thumb);
        Message message = Message.obtain(this.mTargetId, this.mConversationType, locationMessage);
        RongIM.getInstance().sendLocationMessage(message, (String) null, (String) null, (ISendMessageCallback) null);
        if (this.mConversationType.equals(ConversationType.PRIVATE)) {
            RongIMClient.getInstance().sendTypingStatus(this.mConversationType, this.mTargetId, "RC:LBSMsg");
        }

    }

    public void onSwitchToggleClick(View v, ViewGroup inputBoard) {
        if (this.robotType) {
            RongIMClient.getInstance().switchToHumanMode(this.mTargetId);
        }

    }

    public void onVoiceInputToggleTouch(View v, MotionEvent event) {
        String[] permissions = new String[]{"android.permission.RECORD_AUDIO"};
        if (!PermissionCheckUtil.checkPermissions(this.getActivity(), permissions)) {
            if (event.getAction() == 0) {
                PermissionCheckUtil.requestPermissions(this, permissions, 100);
            }

        } else {
            if (event.getAction() == 0) {
                AudioPlayManager.getInstance().stopPlay();
                AudioRecordManager.getInstance().startRecord(v.getRootView(), this.mConversationType, this.mTargetId);
                this.mLastTouchY = event.getY();
                this.mUpDirection = false;
                ((Button) v).setText(string.rc_audio_input_hover);
            } else if (event.getAction() == 2) {
                if (this.mLastTouchY - event.getY() > this.mOffsetLimit && !this.mUpDirection) {
                    AudioRecordManager.getInstance().willCancelRecord();
                    this.mUpDirection = true;
                    ((Button) v).setText(string.rc_audio_input);
                } else if (event.getY() - this.mLastTouchY > -this.mOffsetLimit && this.mUpDirection) {
                    AudioRecordManager.getInstance().continueRecord();
                    this.mUpDirection = false;
                    ((Button) v).setText(string.rc_audio_input_hover);
                }
            } else if (event.getAction() == 1 || event.getAction() == 3) {
                AudioRecordManager.getInstance().stopRecord();
                ((Button) v).setText(string.rc_audio_input);
            }

            if (this.mConversationType.equals(ConversationType.PRIVATE)) {
                RongIMClient.getInstance().sendTypingStatus(this.mConversationType, this.mTargetId, "RC:VcMsg");
            }

        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100 && grantResults[0] != 0) {
            Toast.makeText(this.getActivity(), this.getResources().getString(string.rc_permission_grant_needed), Toast.LENGTH_SHORT).show();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void onEmoticonToggleClick(View v, ViewGroup extensionBoard) {
    }

    public void onPluginToggleClick(View v, ViewGroup extensionBoard) {
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int cursor;
        int offset;
        if (count == 0) {
            cursor = start + before;
            offset = -before;
        } else {
            cursor = start;
            offset = count;
        }

        if (!this.mConversationType.equals(ConversationType.GROUP) && !this.mConversationType.equals(ConversationType.DISCUSSION)) {
            if (this.mConversationType.equals(ConversationType.PRIVATE) && offset != 0) {
                RongIMClient.getInstance().sendTypingStatus(this.mConversationType, this.mTargetId, "RC:TxtMsg");
            }
        } else {
            RongMentionManager.getInstance().onTextEdit(this.mConversationType, this.mTargetId, cursor, offset, s.toString());
        }

    }

    public void afterTextChanged(Editable s) {
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getKeyCode() == 67 && event.getAction() == 0) {
            EditText editText = (EditText) v;
            int cursorPos = editText.getSelectionStart();
            RongMentionManager.getInstance().onDeleteClick(this.mConversationType, this.mTargetId, editText, cursorPos);
        }

        return false;
    }

    public void onMenuClick(int root, int sub) {
        if (this.mPublicServiceProfile != null) {
            PublicServiceMenuItem item = (PublicServiceMenuItem) this.mPublicServiceProfile.getMenu().getMenuItems().get(root);
            if (sub >= 0) {
                item = (PublicServiceMenuItem) item.getSubMenuItems().get(sub);
            }

            if (item.getType().equals(PublicServiceMenuItemType.View)) {
                IPublicServiceMenuClickListener msg = RongContext.getInstance().getPublicServiceMenuClickListener();
                if (msg == null || !msg.onClick(this.mConversationType, this.mTargetId, item)) {
                    String action = "io.rong.imkit.intent.action.webview";
                    Intent intent = new Intent(action);
                    intent.setPackage(this.getActivity().getPackageName());
                    intent.addFlags(268435456);
                    intent.putExtra("url", item.getUrl());
                    this.getActivity().startActivity(intent);
                }
            }

            PublicServiceCommandMessage msg1 = PublicServiceCommandMessage.obtain(item);
            RongIMClient.getInstance().sendMessage(this.mConversationType, this.mTargetId, msg1, (String) null, (String) null, new ISendMessageCallback() {
                public void onAttached(Message message) {
                }

                public void onSuccess(Message message) {
                }

                public void onError(Message message, ErrorCode errorCode) {
                }
            });
        }

    }

    public void onPluginClicked(IPluginModule pluginModule, int position) {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102) {
            this.getActivity().finish();
        } else {
            this.mRongExtension.onActivityPluginResult(requestCode, resultCode, data);
        }

    }

    private String getNameFromCache(String targetId) {
        UserInfo info = RongContext.getInstance().getUserInfoFromCache(targetId);
        return info == null ? targetId : info.getName();
    }

    @Subscribe
    public void onEventMainThread(ReadReceiptRequestEvent event) {
        RLog.d("ConversationFragment", "ReadReceiptRequestEvent");
        if ((this.mConversationType.equals(ConversationType.GROUP) || this.mConversationType.equals(ConversationType.DISCUSSION)) && RongContext.getInstance().isReadReceiptConversationType(event.getConversationType()) && event.getConversationType().equals(this.mConversationType) && event.getTargetId().equals(this.mTargetId)) {
            for (int i = 0; i < this.mListAdapter.getCount(); ++i) {
                if (((UIMessage) this.mListAdapter.getItem(i)).getUId().equals(event.getMessageUId())) {
                    final UIMessage uiMessage = (UIMessage) this.mListAdapter.getItem(i);
                    ReadReceiptInfo readReceiptInfo = uiMessage.getReadReceiptInfo();
                    if (readReceiptInfo == null) {
                        readReceiptInfo = new ReadReceiptInfo();
                        uiMessage.setReadReceiptInfo(readReceiptInfo);
                    }

                    if (readReceiptInfo.isReadReceiptMessage() && readReceiptInfo.hasRespond()) {
                        return;
                    }

                    readReceiptInfo.setIsReadReceiptMessage(true);
                    readReceiptInfo.setHasRespond(false);
                    ArrayList messageList = new ArrayList();
                    messageList.add(((UIMessage) this.mListAdapter.getItem(i)).getMessage());
                    RongIMClient.getInstance().sendReadReceiptResponse(event.getConversationType(), event.getTargetId(), messageList, new OperationCallback() {
                        public void onSuccess() {
                            uiMessage.getReadReceiptInfo().setHasRespond(true);
                        }

                        public void onError(ErrorCode errorCode) {
                            RLog.e("ConversationFragment", "sendReadReceiptResponse failed, errorCode = " + errorCode);
                        }
                    });
                    break;
                }
            }
        }

    }

    @Subscribe
    public void onEventMainThread(ReadReceiptResponseEvent event) {
        RLog.d("ConversationFragment", "ReadReceiptResponseEvent");
        if ((this.mConversationType.equals(ConversationType.GROUP) || this.mConversationType.equals(ConversationType.DISCUSSION)) && RongContext.getInstance().isReadReceiptConversationType(event.getConversationType()) && event.getConversationType().equals(this.mConversationType) && event.getTargetId().equals(this.mTargetId)) {
            for (int i = 0; i < this.mListAdapter.getCount(); ++i) {
                if (((UIMessage) this.mListAdapter.getItem(i)).getUId().equals(event.getMessageUId())) {
                    UIMessage uiMessage = (UIMessage) this.mListAdapter.getItem(i);
                    ReadReceiptInfo readReceiptInfo = uiMessage.getReadReceiptInfo();
                    if (readReceiptInfo == null) {
                        readReceiptInfo = new ReadReceiptInfo();
                        readReceiptInfo.setIsReadReceiptMessage(true);
                        uiMessage.setReadReceiptInfo(readReceiptInfo);
                    }

                    readReceiptInfo.setRespondUserIdList(event.getResponseUserIdList());
                    int first = this.mList.getFirstVisiblePosition();
                    int last = this.mList.getLastVisiblePosition();
                    int position = this.getPositionInListView(i);
                    if (position >= first && position <= last) {
                        this.mListAdapter.getView(i, this.getListViewChildAt(i), this.mList);
                    }
                    break;
                }
            }
        }

    }

    @Subscribe
    public void onEventMainThread(MessageDeleteEvent deleteEvent) {
        RLog.d("ConversationFragment", "MessageDeleteEvent");
        if (deleteEvent.getMessageIds() != null) {
            Iterator i$ = deleteEvent.getMessageIds().iterator();

            while (i$.hasNext()) {
                long messageId = (long) ((Integer) i$.next()).intValue();
                int position = this.mListAdapter.findPosition(messageId);
                if (position >= 0) {
                    this.mListAdapter.remove(position);
                }
            }

            this.mListAdapter.notifyDataSetChanged();
        }

    }

    @Subscribe
    public void onEventMainThread(PublicServiceFollowableEvent event) {
        RLog.d("ConversationFragment", "PublicServiceFollowableEvent");
        if (event != null && !event.isFollow()) {
            this.getActivity().finish();
        }

    }

    @Subscribe
    public void onEventMainThread(MessagesClearEvent clearEvent) {
        RLog.d("ConversationFragment", "MessagesClearEvent");
        if (clearEvent.getTargetId().equals(this.mTargetId) && clearEvent.getType().equals(this.mConversationType)) {
            this.mListAdapter.clear();
            this.mListAdapter.notifyDataSetChanged();
        }

    }

    @Subscribe
    public void onEventMainThread(MessageRecallEvent event) {
        RLog.d("ConversationFragment", "MessageRecallEvent");
        if (event.isRecallSuccess()) {
            RecallNotificationMessage recallNotificationMessage = event.getRecallNotificationMessage();
            int position = this.mListAdapter.findPosition((long) event.getMessageId());
            if (position != -1) {
                ((UIMessage) this.mListAdapter.getItem(position)).setContent(recallNotificationMessage);
                int first = this.mList.getFirstVisiblePosition();
                int last = this.mList.getLastVisiblePosition();
                int listPos = this.getPositionInListView(position);
                if (listPos >= first && listPos <= last) {
                    this.mListAdapter.getView(position, this.getListViewChildAt(position), this.mList);
                }
            }
            mListAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this.getActivity(), string.rc_recall_failed, Toast.LENGTH_SHORT).show();
        }

    }

    @Subscribe
    public void onEventMainThread(RemoteMessageRecallEvent event) {
        RLog.d("ConversationFragment", "RemoteMessageRecallEvent");
        int position = this.mListAdapter.findPosition((long) event.getMessageId());
        int first = this.mList.getFirstVisiblePosition();
        int last = this.mList.getLastVisiblePosition();
        if (position >= 0) {
            UIMessage uiMessage = (UIMessage) this.mListAdapter.getItem(position);
            if (uiMessage.getMessage().getContent() instanceof VoiceMessage) {
                AudioPlayManager.getInstance().stopPlay();
            }

            if (uiMessage.getMessage().getContent() instanceof FileMessage) {
                RongIM.getInstance().cancelDownloadMediaMessage(uiMessage.getMessage(), (OperationCallback) null);
            }

            uiMessage.setContent(event.getRecallNotificationMessage());
            int listPos = this.getPositionInListView(position);
            if (listPos >= first && listPos <= last) {
                this.mListAdapter.getView(position, this.getListViewChildAt(position), this.mList);
            }
        }

    }

    @Subscribe
    public void onEventMainThread(Message msg) {
        RLog.d("ConversationFragment", "Event message : " + msg.getMessageId() + ", " + msg.getObjectName() + ", " + msg.getSentStatus());
        if (this.mTargetId.equals(msg.getTargetId()) && this.mConversationType.equals(msg.getConversationType()) && msg.getMessageId() > 0) {
            int position = this.mListAdapter.findPosition((long) msg.getMessageId());
            if (position >= 0) {
                ((UIMessage) this.mListAdapter.getItem(position)).setMessage(msg);
                this.mListAdapter.getView(position, this.getListViewChildAt(position), this.mList);
            } else {
                UIMessage uiMessage = UIMessage.obtain(msg);
                if (msg.getContent() instanceof CSPullLeaveMessage) {
                    uiMessage.setCsConfig(this.mCustomServiceConfig);
                }

                this.mListAdapter.add(uiMessage);
                this.mListAdapter.notifyDataSetChanged();
            }

            if (msg.getSenderUserId() != null && msg.getSenderUserId().equals(RongIM.getInstance().getCurrentUserId()) && this.mList.getLastVisiblePosition() - 1 != this.mList.getCount()) {
                this.mList.smoothScrollToPosition(this.mList.getCount());
            }

            if (this.mConversationType.equals(ConversationType.CUSTOMER_SERVICE) && msg.getMessageDirection() == MessageDirection.SEND && !this.robotType && this.mCustomServiceConfig.userTipTime > 0 && !TextUtils.isEmpty(this.mCustomServiceConfig.userTipWord)) {
                this.startTimer(0, this.mCustomServiceConfig.userTipTime * 60 * 1000);
            }
        }

    }

    @Subscribe
    public void onEventMainThread(FileMessageEvent event) {
        Message msg = event.getMessage();
        RLog.d("ConversationFragment", "FileMessageEvent message : " + msg.getMessageId() + ", " + msg.getObjectName() + ", " + msg.getSentStatus());
        if (this.mTargetId.equals(msg.getTargetId()) && this.mConversationType.equals(msg.getConversationType()) && msg.getMessageId() > 0) {
            int position = this.mListAdapter.findPosition((long) msg.getMessageId());
            UIMessage uiMessage;
            if (position >= 0) {
                uiMessage = (UIMessage) this.mListAdapter.getItem(position);
                uiMessage.setMessage(msg);
                uiMessage.setProgress(event.getProgress());
                ((UIMessage) this.mListAdapter.getItem(position)).setMessage(msg);
                this.mListAdapter.getView(position, this.getListViewChildAt(position), this.mList);
            } else {
                uiMessage = UIMessage.obtain(msg);
                uiMessage.setProgress(event.getProgress());
                this.mListAdapter.add(uiMessage);
                this.mListAdapter.notifyDataSetChanged();
            }

            if (msg.getSenderUserId() != null && msg.getSenderUserId().equals(RongIM.getInstance().getCurrentUserId()) && this.mList.getLastVisiblePosition() - 1 != this.mList.getCount()) {
                this.mList.smoothScrollToPosition(this.mList.getCount());
            }

            if (this.mConversationType.equals(ConversationType.CUSTOMER_SERVICE) && msg.getMessageDirection() == MessageDirection.SEND && !this.robotType && this.mCustomServiceConfig.userTipTime > 0 && !TextUtils.isEmpty(this.mCustomServiceConfig.userTipWord)) {
                this.startTimer(0, this.mCustomServiceConfig.userTipTime * 60 * 1000);
            }
        }

    }

    @Subscribe
    public void onEventMainThread(GroupUserInfo groupUserInfo) {
        RLog.d("ConversationFragment", "GroupUserInfoEvent " + groupUserInfo.getGroupId() + " " + groupUserInfo.getUserId() + " " + groupUserInfo.getNickname());
        if (groupUserInfo.getNickname() != null && groupUserInfo.getGroupId() != null) {
            int count = this.mListAdapter.getCount();
            int first = this.mList.getFirstVisiblePosition();
            int last = this.mList.getLastVisiblePosition();

            for (int i = 0; i < count; ++i) {
                UIMessage uiMessage = (UIMessage) this.mListAdapter.getItem(i);
                if (uiMessage.getSenderUserId().equals(groupUserInfo.getUserId())) {
                    uiMessage.setNickName(true);
                    UserInfo userInfo = uiMessage.getUserInfo();
                    if (userInfo != null) {
                        userInfo.setName(groupUserInfo.getNickname());
                    } else {
                        userInfo = new UserInfo(groupUserInfo.getUserId(), groupUserInfo.getNickname(), (Uri) null);
                    }

                    uiMessage.setUserInfo(userInfo);
                    int pos = this.getPositionInListView(i);
                    if (pos >= first && pos <= last) {
                        this.mListAdapter.getView(i, this.getListViewChildAt(i), this.mList);
                    }
                }
            }

        }
    }

    private View getListViewChildAt(int adapterIndex) {
        int header = this.mList.getHeaderViewsCount();
        int first = this.mList.getFirstVisiblePosition();
        return this.mList.getChildAt(adapterIndex + header - first);
    }

    private int getPositionInListView(int adapterIndex) {
        int header = this.mList.getHeaderViewsCount();
        return adapterIndex + header;
    }

    private int getPositionInAdapter(int listIndex) {
        int header = this.mList.getHeaderViewsCount();
        return listIndex <= 0 ? 0 : listIndex - header;
    }

    @Subscribe
    public void onEventMainThread(OnMessageSendErrorEvent event) {
        this.onEventMainThread(event.getMessage());
    }

    @Subscribe
    public void onEventMainThread(OnReceiveMessageEvent event) {
        Message message = event.getMessage();
        RLog.i("ConversationFragment", "OnReceiveMessageEvent, " + message.getMessageId() + ", " + message.getObjectName() + ", " + message.getReceivedStatus().toString());
        ConversationType conversationType = message.getConversationType();
        String targetId = message.getTargetId();
        if (this.mConversationType.equals(conversationType) && this.mTargetId.equals(targetId) && this.shouldUpdateMessage(message, event.getLeft())) {
            if (event.getLeft() == 0 && message.getConversationType().equals(ConversationType.PRIVATE) && RongContext.getInstance().isReadReceiptConversationType(ConversationType.PRIVATE) && message.getMessageDirection().equals(MessageDirection.RECEIVE)) {
                if (this.mReadRec) {
                    RongIMClient.getInstance().sendReadReceiptMessage(message.getConversationType(), message.getTargetId(), message.getSentTime());
                }

                if (this.mSyncReadStatus) {
                    RongIMClient.getInstance().syncConversationReadStatus(message.getConversationType(), message.getTargetId(), message.getSentTime(), (OperationCallback) null);
                }
            }

            if (this.mSyncReadStatus) {
                this.mSyncReadStatusMsgTime = message.getSentTime();
            }

            if (message.getMessageId() > 0) {
                message.getReceivedStatus().setRead();
                RongIMClient.getInstance().setMessageReceivedStatus(message.getMessageId(), message.getReceivedStatus(), (ResultCallback) null);
                if (message.getMessageDirection().equals(MessageDirection.RECEIVE)) {
                    UnReadMessageManager.getInstance().onMessageReceivedStatusChanged();
                }

                if (this.mConversationType.equals(ConversationType.CUSTOMER_SERVICE) && !this.robotType && this.mCustomServiceConfig.adminTipTime > 0 && !TextUtils.isEmpty(this.mCustomServiceConfig.adminTipWord)) {
                    this.startTimer(1, this.mCustomServiceConfig.adminTipTime * 60 * 1000);
                }
            }

            if (this.mNewMessageBtn != null && this.mList.getCount() - this.mList.getLastVisiblePosition() > 2 && MessageDirection.SEND != message.getMessageDirection() && message.getConversationType() != ConversationType.CHATROOM && message.getConversationType() != ConversationType.CUSTOMER_SERVICE && message.getConversationType() != ConversationType.APP_PUBLIC_SERVICE && message.getConversationType() != ConversationType.PUBLIC_SERVICE) {
                ++this.mNewMessageCount;
                if (this.mNewMessageCount > 0) {
                    this.mNewMessageBtn.setVisibility(View.VISIBLE);
                    this.mNewMessageTextView.setVisibility(View.VISIBLE);
                }

                if (this.mNewMessageCount > 99) {
                    this.mNewMessageTextView.setText("99+");
                } else {
                    this.mNewMessageTextView.setText(this.mNewMessageCount + "");
                }
            }

            this.onEventMainThread(event.getMessage());
        }

    }

    public final void onEventBackgroundThread(final PlayAudioEvent event) {
        this.getHandler().post(new Runnable() {
            public void run() {
                handleAudioPlayEvent(event);
            }
        });
    }

    private void handleAudioPlayEvent(PlayAudioEvent event) {
        RLog.i("ConversationFragment", "PlayAudioEvent");
        int first = this.mList.getFirstVisiblePosition();
        int last = this.mList.getLastVisiblePosition();
        int position = this.mListAdapter.findPosition((long) event.messageId);
        if (event.continuously && position >= 0) {
            while (first <= last) {
                ++position;
                ++first;
                UIMessage uiMessage = (UIMessage) this.mListAdapter.getItem(position);
                if (uiMessage != null && uiMessage.getContent() instanceof VoiceMessage && uiMessage.getMessageDirection().equals(MessageDirection.RECEIVE) && !uiMessage.getReceivedStatus().isListened()) {
                    uiMessage.continuePlayAudio = true;
                    this.mListAdapter.getView(position, this.getListViewChildAt(position), this.mList);
                    break;
                }
            }
        }

    }

    @Subscribe
    public void onEventMainThread(OnReceiveMessageProgressEvent event) {
        if (this.mList != null) {
            int first = this.mList.getFirstVisiblePosition();

            for (int last = this.mList.getLastVisiblePosition(); first <= last; ++first) {
                int position = this.getPositionInAdapter(first);
                UIMessage uiMessage = (UIMessage) this.mListAdapter.getItem(position);
                if (uiMessage.getMessageId() == event.getMessage().getMessageId()) {
                    uiMessage.setProgress(event.getProgress());
                    if (this.isResumed()) {
                        this.mListAdapter.getView(position, this.getListViewChildAt(position), this.mList);
                    }
                    break;
                }
            }
        }

    }

    @Subscribe
    public void onEventMainThread(UserInfo userInfo) {
        RLog.i("ConversationFragment", "userInfo " + userInfo.getUserId());
        int first = this.mList.getFirstVisiblePosition();
        int last = this.mList.getLastVisiblePosition();

        for (int i = 0; i < this.mListAdapter.getCount(); ++i) {
            UIMessage uiMessage = (UIMessage) this.mListAdapter.getItem(i);
            if (userInfo.getUserId().equals(uiMessage.getSenderUserId()) && !uiMessage.isNickName()) {
                if (uiMessage.getConversationType().equals(ConversationType.CUSTOMER_SERVICE) && uiMessage.getMessage() != null && uiMessage.getMessage().getContent() != null && uiMessage.getMessage().getContent().getUserInfo() != null) {
                    uiMessage.setUserInfo(uiMessage.getMessage().getContent().getUserInfo());
                } else {
                    uiMessage.setUserInfo(userInfo);
                }

                int position = this.getPositionInListView(i);
                if (position >= first && position <= last) {
                    this.mListAdapter.getView(i, this.getListViewChildAt(i), this.mList);
                }
            }
        }

    }

    @Subscribe
    public void onEventMainThread(PublicServiceProfile publicServiceProfile) {
        RLog.i("ConversationFragment", "publicServiceProfile");
        int first = this.mList.getFirstVisiblePosition();

        for (int last = this.mList.getLastVisiblePosition(); first <= last; ++first) {
            int position = this.getPositionInAdapter(first);
            UIMessage message = (UIMessage) this.mListAdapter.getItem(position);
            if (message != null && (TextUtils.isEmpty(message.getTargetId()) || publicServiceProfile.getTargetId().equals(message.getTargetId()))) {
                this.mListAdapter.getView(position, this.getListViewChildAt(position), this.mList);
            }
        }

    }

    @Subscribe
    public void onEventMainThread(ReadReceiptEvent event) {
        RLog.i("ConversationFragment", "ReadReceiptEvent");
        if (RongContext.getInstance().isReadReceiptConversationType(event.getMessage().getConversationType()) && this.mTargetId.equals(event.getMessage().getTargetId()) && this.mConversationType.equals(event.getMessage().getConversationType()) && event.getMessage().getMessageDirection().equals(MessageDirection.RECEIVE)) {
            ReadReceiptMessage content = (ReadReceiptMessage) event.getMessage().getContent();
            long ntfTime = content.getLastMessageSendTime();

            for (int i = this.mListAdapter.getCount() - 1; i >= 0; --i) {
                UIMessage uiMessage = (UIMessage) this.mListAdapter.getItem(i);
                if (uiMessage.getMessageDirection().equals(MessageDirection.SEND) && uiMessage.getSentStatus() == SentStatus.SENT && ntfTime >= uiMessage.getSentTime()) {
                    uiMessage.setSentStatus(SentStatus.READ);
                    int first = this.mList.getFirstVisiblePosition();
                    int last = this.mList.getLastVisiblePosition();
                    int position = this.getPositionInListView(i);
                    if (position >= first && position <= last) {
                        this.mListAdapter.getView(i, this.getListViewChildAt(i), this.mList);
                    }
                }
            }
        }

    }

    public MessageListAdapter getMessageAdapter() {
        return this.mListAdapter;
    }

    public boolean shouldUpdateMessage(Message message, int left) {
        return true;
    }

    public void getHistoryMessage(ConversationType conversationType, String targetId, int lastMessageId, int reqCount, final IHistoryDataResultCallback<List<Message>> callback) {
        RongIMClient.getInstance().getHistoryMessages(conversationType, targetId, lastMessageId, reqCount, new ResultCallback<List<Message>>() {
            public void onSuccess(List<Message> messages) {
                if (callback != null) {
                    callback.onResult(messages);
                }

            }

            public void onError(ErrorCode e) {
                RLog.e("ConversationFragment", "getHistoryMessages " + e);
                if (callback != null) {
                    callback.onResult((List<Message>) null);
                }

            }
        });
    }

    private void getHistoryMessage(ConversationType conversationType, String targetId, final int reqCount, final int scrollMode) {
        this.mList.onRefreshStart(Mode.START);
        if (conversationType.equals(ConversationType.CHATROOM)) {
            this.mList.onRefreshComplete(0, 0, false);
            RLog.w("ConversationFragment", "Should not get local message in chatroom");
        } else {
            this.mList.onRefreshStart(Mode.START);
            int last = this.mListAdapter.getCount() == 0 ? -1 : (((UIMessage) this.mListAdapter.getItem(0)) == null ? -1 : mListAdapter.getItem(0).getMessageId());


            this.getHistoryMessage(conversationType, targetId, last, reqCount, new IHistoryDataResultCallback<List<Message>>() {
                public void onResult(List<Message> messages) {
                    RLog.i("ConversationFragment", "getHistoryMessage " + (messages != null ? messages.size() : 0));
                    mHasMoreLocalMessages = (messages != null ? messages.size() : 0) == reqCount;
                    mList.onRefreshComplete(reqCount, reqCount, false);
                    if (messages != null && messages.size() > 0) {
                        Iterator index = messages.iterator();

                        while (index.hasNext()) {
                            Message message = (Message) index.next();
                            boolean contains = false;

                            for (int uiMessage = 0; uiMessage < mListAdapter.getCount(); ++uiMessage) {
                                contains = ((mListAdapter.getItem(uiMessage)) == null ? -2 : (mListAdapter.getItem(uiMessage)).getMessageId()) == message.getMessageId();
                                if (contains) {
                                    break;
                                }
                            }

                            if (!contains) {
                                UIMessage var7 = UIMessage.obtain(message);
                                if (message.getContent() instanceof CSPullLeaveMessage) {
                                    var7.setCsConfig(mCustomServiceConfig);
                                }

                                mListAdapter.add(var7, 0);
                            }
                        }

                        if (scrollMode == 3) {
                            mList.setTranscriptMode(2);
                        } else {
                            mList.setTranscriptMode(0);
                        }

                        mListAdapter.notifyDataSetChanged();
                        LogUtil.e(TAG, "gg1");
                        if (mLastMentionMsgId > 0) {
                            int var6 = mListAdapter.findPosition((long) mLastMentionMsgId);
                            mList.smoothScrollToPosition(var6);
                            mLastMentionMsgId = 0;
                        } else if (2 == scrollMode) {
                            mList.setSelection(0);
                        } else if (scrollMode == 3) {
                            mList.setSelection(mList.getCount());
                        } else {
                            mList.setSelection(messages.size() + 1);
                        }

                        sendReadReceiptResponseIfNeeded(messages);
                    } else {
                        mList.onRefreshComplete(reqCount, reqCount, false);
                    }

                }

                public void onError() {
                    mList.onRefreshComplete(reqCount, reqCount, false);
                }
            });
        }
    }

    public void getRemoteHistoryMessages(ConversationType conversationType, String targetId, long dateTime, int reqCount, final IHistoryDataResultCallback<List<Message>> callback) {
        RongIMClient.getInstance().getRemoteHistoryMessages(conversationType, targetId, dateTime, reqCount, new ResultCallback<List<Message>>() {
            public void onSuccess(List<Message> messages) {
                if (callback != null) {
                    callback.onResult(messages);
                }

            }

            public void onError(ErrorCode e) {
                RLog.e("ConversationFragment", "getRemoteHistoryMessages " + e);
                if (callback != null) {
                    callback.onResult((List<Message>) null);
                }

            }
        });
    }

    private void getRemoteHistoryMessages(ConversationType conversationType, String targetId, final int reqCount) {
        this.mList.onRefreshStart(Mode.START);
        if (this.mConversationType.equals(ConversationType.CHATROOM)) {
            this.mList.onRefreshComplete(0, 0, false);
            RLog.w("ConversationFragment", "Should not get remote message in chatroom");
        } else {
            long dateTime = this.mListAdapter.getCount() == 0 ? 0L : ((UIMessage) this.mListAdapter.getItem(0)).getSentTime();
            this.getRemoteHistoryMessages(conversationType, targetId, dateTime, reqCount, new IHistoryDataResultCallback<List<Message>>() {
                public void onResult(List<Message> messages) {
                    RLog.i("ConversationFragment", "getRemoteHistoryMessages " + (messages == null ? 0 : messages.size()));
                    mList.onRefreshComplete(messages == null ? 0 : messages.size(), reqCount, false);
                    if (messages != null && messages.size() > 0) {
                        ArrayList remoteList = new ArrayList();
                        Iterator i$ = messages.iterator();

                        while (i$.hasNext()) {
                            Message uiMessage = (Message) i$.next();
                            if (uiMessage.getMessageId() > 0) {
                                UIMessage uiMessage1 = UIMessage.obtain(uiMessage);
                                if (uiMessage.getContent() instanceof CSPullLeaveMessage) {
                                    uiMessage1.setCsConfig(mCustomServiceConfig);
                                }

                                remoteList.add(uiMessage1);
                            }
                        }

                        List remoteList1 = filterMessage(remoteList);
                        if (remoteList1 != null && remoteList1.size() > 0) {
                            i$ = remoteList1.iterator();

                            while (i$.hasNext()) {
                                UIMessage uiMessage2 = (UIMessage) i$.next();
                                mListAdapter.add(uiMessage2, 0);
                            }

                            mList.setTranscriptMode(0);
                            mListAdapter.notifyDataSetChanged();
                            LogUtil.e(TAG, "gg2");
                            mList.setSelection(messages.size() + 1);
                            sendReadReceiptResponseIfNeeded(messages);
                        }
                    } else {
                        mList.onRefreshComplete(0, reqCount, false);
                    }

                }

                public void onError() {
                    mList.onRefreshComplete(0, reqCount, false);
                }
            });
        }
    }

    private List<UIMessage> filterMessage(List<UIMessage> srcList) {
        Object destList;
        if (this.mListAdapter.getCount() > 0) {
            destList = new ArrayList();

            for (int i = 0; i < this.mListAdapter.getCount(); ++i) {
                Iterator i$ = srcList.iterator();

                while (i$.hasNext()) {
                    UIMessage msg = (UIMessage) i$.next();
                    if (!((List) destList).contains(msg) && msg.getMessageId() != ((UIMessage) this.mListAdapter.getItem(i)).getMessageId()) {
                        ((List) destList).add(msg);
                    }
                }
            }
        } else {
            destList = srcList;
        }

        return (List) destList;
    }

    private void getLastMentionedMessageId(ConversationType conversationType, String targetId) {
        RongIMClient.getInstance().getUnreadMentionedMessages(conversationType, targetId, new ResultCallback<List<Message>>() {
            public void onSuccess(List<Message> messages) {
                if (messages != null && messages.size() > 0) {
                    mLastMentionMsgId = ((Message) messages.get(0)).getMessageId();
                    int index = mListAdapter.findPosition((long) mLastMentionMsgId);
                    RLog.i("ConversationFragment", "getLastMentionedMessageId " + mLastMentionMsgId + " " + index);
                    if (mLastMentionMsgId > 0 && index >= 0) {
                        mList.smoothScrollToPosition(index);
                        mLastMentionMsgId = 0;
                    }
                }

                RongIM.getInstance().clearMessagesUnreadStatus(mConversationType, mTargetId, (ResultCallback) null);
            }

            public void onError(ErrorCode e) {
                RongIM.getInstance().clearMessagesUnreadStatus(mConversationType, mTargetId, (ResultCallback) null);
            }
        });

    }

    private void sendReadReceiptResponseIfNeeded(List<Message> messages) {
        if (this.mReadRec && (this.mConversationType.equals(ConversationType.GROUP) || this.mConversationType.equals(ConversationType.DISCUSSION)) && RongContext.getInstance().isReadReceiptConversationType(this.mConversationType)) {
            ArrayList responseMessageList = new ArrayList();
            Iterator i$ = messages.iterator();

            while (i$.hasNext()) {
                Message message = (Message) i$.next();
                ReadReceiptInfo readReceiptInfo = message.getReadReceiptInfo();
                if (readReceiptInfo != null && readReceiptInfo.isReadReceiptMessage() && !readReceiptInfo.hasRespond()) {
                    responseMessageList.add(message);
                }
            }

            if (responseMessageList.size() > 0) {
                RongIMClient.getInstance().sendReadReceiptResponse(this.mConversationType, this.mTargetId, responseMessageList, (OperationCallback) null);
            }
        }

    }

    public void onExtensionCollapsed() {
    }

    public void onExtensionExpanded(int h) {
        this.mList.setTranscriptMode(2);
        this.mList.smoothScrollToPosition(this.mList.getCount());
    }

    public void onStartCustomService(String targetId) {
        this.csEnterTime = System.currentTimeMillis();

        this.mRongExtension.setExtensionBarMode(CustomServiceMode.CUSTOM_SERVICE_MODE_NO_SERVICE);
        RongIMClient.getInstance().startCustomService(targetId, this.customServiceListener, this.mCustomUserInfo);
    }

    public void onStopCustomService(String targetId) {
        RongIMClient.getInstance().stopCustomService(targetId);
    }

    public final void onEvaluateSubmit() {
        if (this.mEvaluateDialg != null) {
            this.mEvaluateDialg.destroy();
            this.mEvaluateDialg = null;
        }

        if (this.mCustomServiceConfig.quitSuspendType.equals(CSQuitSuspendType.NONE)) {
            this.getActivity().finish();
        }

    }

    public final void onEvaluateCanceled() {
        if (this.mEvaluateDialg != null) {
            this.mEvaluateDialg.destroy();
            this.mEvaluateDialg = null;
        }

        if (this.mCustomServiceConfig.quitSuspendType.equals(CSQuitSuspendType.NONE)) {
            this.getActivity().finish();
        }

    }

    private void startTimer(int event, int interval) {
        this.getHandler().removeMessages(event);
        this.getHandler().sendEmptyMessageDelayed(event, (long) interval);
    }

    private void stopTimer(int event) {
        this.getHandler().removeMessages(event);
    }

    public ConversationType getConversationType() {
        return this.mConversationType;
    }

    public String getTargetId() {
        return this.mTargetId;
    }
}
