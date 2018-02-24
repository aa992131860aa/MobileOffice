package com.mobileoffice.controller.rong;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobileoffice.MainActivity;
import com.mobileoffice.R;
import com.mobileoffice.controller.cloud_monitor.PreviewInfoActivity;
import com.mobileoffice.controller.message.ConversationFragment;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.ContactListJson;
import com.mobileoffice.json.TransferJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;
import com.mobileoffice.view.DialogMaker;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

public class ConversationActivity extends FragmentActivity implements View.OnClickListener {

    private TextView mTextView;
    private LinearLayout ll_conversation_activity_back;
    private LinearLayout ll_history_search;
    private String TAG = "ConversationActivity";
    private Dialog dialog;
    private String mTargetId;
    private boolean mIsDisconnect = false;
    private ConversationFragment fragment;
    private Conversation.ConversationType mConversationType;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.conversation_activity);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //状态栏透明颜色
            getWindow().setStatusBarColor(getResources().getColor(R.color.main));
        }
           //为了设置全屏
        ViewGroup mContentView = (ViewGroup) findViewById(android.R.id.content);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 使其不为系统 View 预留空间.
            ViewCompat.setFitsSystemWindows(mChildView, true);
        }

        CONSTS.MODIFY_TRANSFER = null;
        mTextView = (TextView) findViewById(R.id.title);
        ll_history_search = (LinearLayout) findViewById(R.id.ll_history_search);
        ll_conversation_activity_back = (LinearLayout) findViewById(R.id.ll_conversation_activity_back);
        ll_conversation_activity_back.setOnClickListener(this);
        mTextView.setText(getIntent().getData().getQueryParameter("title"));
        //保护-,说明是群组
        if (mTextView.getText().toString().contains("-")) {
            ll_history_search.setVisibility(View.VISIBLE);
            ll_history_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ll_history_search.setEnabled(false);
                    skipTranferDetail();
                }
            });

            loadGroupInfoList();
        } else {
            ll_history_search.setVisibility(View.GONE);
        }

        isPushMessage(getIntent());

    }

    /**
     * 判断是否是 Push 消息，判断是否需要做 connect 操作
     */
    private void isPushMessage(Intent intent) {

        if (intent == null || intent.getData() == null)
            return;
        mConversationType = Conversation.ConversationType.valueOf(intent.getData()
                .getLastPathSegment().toUpperCase(Locale.US));
        mTargetId = intent.getData().getQueryParameter("targetId");

        //push
        if (intent.getData().getScheme().equals("rong") && intent.getData().getQueryParameter("isFromPush") != null) {

            //通过intent.getData().getQueryParameter("push") 为true，判断是否是push消息
            if (intent.getData().getQueryParameter("isFromPush").equals("true")) {
                //只有收到系统消息和不落地 push 消息的时候，pushId 不为 null。而且这两种消息只能通过 server 来发送，客户端发送不了。
                //RongIM.getInstance().getRongIMClient().recordNotificationEvent(id);

                mIsDisconnect = true;
                // finish();
                loadRongServer();

                getContactList();
            }else{
                enterFragment(mConversationType, mTargetId);
            }

        } else {
            enterFragment(mConversationType, mTargetId);

        }
        //ToastUtil.showToast(intent+","+intent.getData(),this);
    }

    /**
     * 加载融云服务
     */
    private void loadRongServer() {

        String token = SharePreUtils.getString("token", "", this);
        Log.e(TAG, "token:" + token);

        RongIM.connect(token, new RongIMClient.ConnectCallback() {

            @Override
            public void onSuccess(String userId) {
                enterFragment(mConversationType, mTargetId);

                //               RongIM.getInstance().startConversation(ConversationActivity.this, Conversation.ConversationType.PRIVATE,mTargetId,getIntent().getData().getQueryParameter("title"));
//                RongIM.setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
//                    @Override
//                    public boolean onReceived(Message message, int i) {
//                        Log.e(TAG, "userIDI:" + i+":"+message.getContent());
//                        return false;
//                    }
//                });

            }


            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Log.e(TAG, "connect failure errorCode is :" + errorCode.getValue());
            }

            @Override
            public void onTokenIncorrect() {
                Log.e(TAG, "token is error , please check token and appkey ");
            }
        });
    }

    private void skipTranferDetail() {
        showWaitDialog(getResources().getString(R.string.loading), false, "loading");
        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "getTransferByOrganSeg");
        params.addBodyParameter("organSeg", CONSTS.ORGAN_SEG);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //LogUtil.e(TAG,result);
                TransferJson transferJson = new Gson().fromJson(result, TransferJson.class);
                if (transferJson != null && transferJson.getResult() == CONSTS.SEND_OK) {

                    Intent intent = new Intent();
//                    TransferJson.ObjBean oldObjBean = transferJson.getObj();
//                    TransferJson.ObjBean TObjBean = new TransferJson.ObjBean();
//                    TObjBean.setPhone(oldObjBean.getPhone());
//                    TObjBean.setBlood(oldObjBean.getBlood());
//                    TObjBean.setBloodNum(oldObjBean.getBloodNum());
//                    TObjBean.setContactName(oldObjBean.getContactName());
//                    TObjBean.setContactPhone(oldObjBean.getContactPhone());
//                    TObjBean.setDistance(oldObjBean.getDistance());
//                    TObjBean.setEndLati(oldObjBean.getEndLati());
//                    TObjBean.setEndLong(oldObjBean.getEndLong());
//                    TObjBean.setFromCity(oldObjBean.getFromCity());
//                    TObjBean.setGetTime(oldObjBean.getGetTime());
//                    TObjBean.setIsStart(oldObjBean.getIsStart());
//                    TObjBean.setOpenPsd(oldObjBean.getOpenPsd());
//                    TObjBean.setOpoName(oldObjBean.getOpoName());
//                    TObjBean.setOrgan(oldObjBean.getOrgan());
//                    TObjBean.setOrganNum(oldObjBean.getOrganNum());
//                    TObjBean.setOrganSeg(oldObjBean.getOrganSeg());
//                    TObjBean.setSampleOrgan(oldObjBean.getSampleOrgan());
//                    TObjBean.setStartLati(oldObjBean.getStartLati());
//                    TObjBean.setStartLong(oldObjBean.getStartLong());
//                    TObjBean.setToHosp(oldObjBean.getToHosp());
//                    TObjBean.setTracfficType(oldObjBean.getTracfficType());
//                    TObjBean.setTracfficNumber(oldObjBean.getTracfficNumber());
//                    TObjBean.setSampleOrganNum(oldObjBean.getSampleOrganNum());
//                    TObjBean.setToHospName(oldObjBean.getToHospName());
//                    TObjBean.setTrueName(oldObjBean.getTrueName());
//                    TObjBean.setTransferid(oldObjBean.getTransferid());
//                    TObjBean.setBoxNo(oldObjBean.getBoxNo());

                    intent.putExtra("preview", transferJson.getObj());
                    intent.setClass(ConversationActivity.this, PreviewInfoActivity.class);
                    startActivity(intent);

                }
                ll_history_search.setEnabled(true);
                dismissDialog();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ll_history_search.setEnabled(true);
                dismissDialog();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                ll_history_search.setEnabled(true);
            }

            @Override
            public void onFinished() {
                ll_history_search.setEnabled(true);
            }
        });
    }
    /**
     * 加载联系人
     */
    private void getContactList() {

        RequestParams params = new RequestParams(URL.CONTACT);
        params.addBodyParameter("action", "getContactList");
        params.addBodyParameter("phone", SharePreUtils.getString("phone", "", this));
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    Log.e(TAG, "result:" + result);
                    ContactListJson json = new Gson().fromJson(result, ContactListJson.class);
                    if (json != null && json.getResult() == CONSTS.SEND_OK) {
                        if (json.getObj() != null) {
                            List<ContactListJson.ObjBean> objBeens = json.getObj();
                            for (int i = 0; i < objBeens.size(); i++) {
                                UserInfo mUserInfo;
                                if ("0".equals(objBeens.get(i).getIsUploadPhoto()) && !"".equals(objBeens.get(i).getWechatUrl())) {
                                    mUserInfo = new UserInfo(objBeens.get(i).getContactPhone(), objBeens.get(i).getTrueName(), Uri.parse(objBeens.get(i).getWechatUrl()));
                                } else if ("1".equals(objBeens.get(i).getIsUploadPhoto()) && !"".equals(objBeens.get(i).getPhotoFile())) {
                                    mUserInfo = new UserInfo(objBeens.get(i).getContactPhone(), objBeens.get(i).getTrueName(), Uri.parse(objBeens.get(i).getPhotoFile()));
                                } else {
                                    mUserInfo = new UserInfo(objBeens.get(i).getContactPhone(), objBeens.get(i).getTrueName(), Uri.parse(URL.CONTACT_PERSON_PHOTO));
                                }

                                RongIM.getInstance().refreshUserInfoCache(mUserInfo);

                            }
                        }
                    } else {

                    }
                } catch (Exception ex) {
                    Log.e(TAG, "phone1113:" + ex.getMessage() + "," + ex.getLocalizedMessage());
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "phone1114:" + ex.getMessage() + "," + ex.getLocalizedMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    //刷新群组人员的头像
    private void loadGroupInfoList() {

        RequestParams params = new RequestParams(URL.RONG);
        params.addBodyParameter("action", "getGroupInfoList");
        params.addBodyParameter("organSeg", CONSTS.ORGAN_SEG);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {

                    Log.e(TAG, "result11111:" + result);

                    ContactListJson json = new Gson().fromJson(result, ContactListJson.class);
                    if (json != null && json.getResult() == CONSTS.SEND_OK) {
                        final List<UserInfo> userInfos = new ArrayList<>();
                        if (json.getObj() != null) {
                            List<ContactListJson.ObjBean> objBeens = json.getObj();

                            for (int i = 0; i < objBeens.size(); i++) {
                                //ToastUtil.showToast("gg"+i,ConversationActivity.this);
                                UserInfo mUserInfo;
                                if ("0".equals(objBeens.get(i).getIsUploadPhoto()) && !"".equals(objBeens.get(i).getWechatUrl())) {
                                    mUserInfo = new UserInfo(objBeens.get(i).getContactPhone(), objBeens.get(i).getTrueName(), Uri.parse(objBeens.get(i).getWechatUrl()));
                                } else if ("1".equals(objBeens.get(i).getIsUploadPhoto()) && !"".equals(objBeens.get(i).getPhotoFile())) {
                                    mUserInfo = new UserInfo(objBeens.get(i).getContactPhone(), objBeens.get(i).getTrueName(), Uri.parse(objBeens.get(i).getPhotoFile()));
                                } else {
                                    mUserInfo = new UserInfo(objBeens.get(i).getContactPhone(), objBeens.get(i).getTrueName(), Uri.parse(URL.CONTACT_PERSON_PHOTO));
                                }
                                userInfos.add(mUserInfo);
                                RongIM.getInstance().refreshUserInfoCache(mUserInfo);

                            }
                            if (objBeens.size() > 0) {
                                ConversationFragment.mListAdapter.notifyDataSetChanged();
                            }
                        }

                        RongIM.getInstance().setGroupMembersProvider(new RongIM.IGroupMembersProvider() {
                            @Override
                            public void getGroupMembers(String s, RongIM.IGroupMemberCallback iGroupMemberCallback) {
                                  iGroupMemberCallback.onGetGroupMembersResult(userInfos);
                            }
                        });

                    } else {

                    }
                } catch (Exception ex) {
                    Log.e(TAG, "phone1113:" + ex.getMessage() + "," + ex.getLocalizedMessage());
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "phone1114:" + ex.getMessage() + "," + ex.getLocalizedMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_conversation_activity_back:

                hintKbTwo();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        finish();
                    }
                }.start();


                if (mIsDisconnect) {
                    startActivity(new Intent(this, MainActivity.class));
                }
                break;
        }
    }

    //此方法只是关闭软键盘
    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 等待对话框
     *
     * @author blue
     */
    public Dialog showWaitDialog(String msg, boolean isCanCancelabel, Object tag) {
        if (null == dialog || !dialog.isShowing()) {
            dialog = DialogMaker.showCommenWaitDialog(this, msg, null, isCanCancelabel, tag);
        }
        return dialog;
    }

    /**
     * 关闭对话框
     *
     * @author blue
     */
    public void dismissDialog() {
        if (null != dialog && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsDisconnect) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 加载会话页面 ConversationFragmentEx 继承自 ConversationFragment
     *
     * @param mConversationType 会话类型
     * @param mTargetId         会话 Id
     */
    private void enterFragment(Conversation.ConversationType mConversationType, String mTargetId) {

        fragment = new ConversationFragment();

        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId).build();

        fragment.setUri(uri);
        Log.e(TAG,"packageName:"+getApplicationInfo().packageName+",name:"+mConversationType.getName().toLowerCase()+",target:"+mTargetId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //xxx 为你要加载的 id
        transaction.add(R.id.conversation, fragment);
        transaction.commitAllowingStateLoss();
    }
}
