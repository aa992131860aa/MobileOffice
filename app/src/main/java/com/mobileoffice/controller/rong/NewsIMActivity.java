package com.mobileoffice.controller.rong;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mobileoffice.R;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.http.HttpHelper;
import com.mobileoffice.http.HttpObserver;
import com.mobileoffice.http.URL;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;


/**
 * Created by 99213 on 2017/5/10.
 */

public class NewsIMActivity extends BaseActivity implements View.OnClickListener, RongIM.UserInfoProvider {
    private Button btn_service1;
    private Button btn_service2;
    private Button btn_service3;
    private Button btn_session;
    private Button btn_conversation_list;
    private Button btn_create_group;
    private Button btn_add_group_user;
    private Button btn_del_group_user;
    private Button btn_check_online;
    private Button btn_discussion;
    private String TAG = "NewsIMActivity";
    private String mUserId;
    private Context mContext;
    private String admin;
    private String user;
    private String other;
    private String photoUrl1 = URL.TOMCAT_SOCKET+"images/collision.png";
    private String photoUr12 = URL.TOMCAT_SOCKET+"images/start.png";
    private String photoUr13 = URL.TOMCAT_SOCKET+"images/end.png";
    private List<UserInfo> userInfos;
    private List<String> mUserIds;

    private String mGroupId;

    @Override
    protected void initVariable() {
        //token
        admin = SharePreUtils.getString("admin", "", mContext);
        user = SharePreUtils.getString("user", "", mContext);
        other = SharePreUtils.getString("other", "", mContext);

        userInfos = new ArrayList<>();
        mUserIds = new ArrayList<>();
        UserInfo userInfo1 = new UserInfo("admin", "admin", Uri.parse(photoUrl1));
        UserInfo userInfo2 = new UserInfo("user", "user", Uri.parse(photoUr12));
        UserInfo userInfo3 = new UserInfo("user", "user", Uri.parse(photoUr13));
        userInfos.add(userInfo1);
        userInfos.add(userInfo2);
        userInfos.add(userInfo3);

        mUserIds.add("admin");
        //mUserIds.add("user");
        mUserIds.add("other");

        RongIM.setUserInfoProvider(this, true);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.news_im);
        mContext = this;
        btn_service1 = (Button) findViewById(R.id.btn_service1);
        btn_service2 = (Button) findViewById(R.id.btn_service2);
        btn_service3 = (Button) findViewById(R.id.btn_service3);
        btn_session = (Button) findViewById(R.id.btn_session);
        btn_conversation_list = (Button) findViewById(R.id.btn_conversation_list);
        btn_create_group = (Button) findViewById(R.id.btn_create_group);
        btn_add_group_user = (Button) findViewById(R.id.btn_add_group_user);
        btn_del_group_user = (Button) findViewById(R.id.btn_del_group_user);
        btn_check_online = (Button) findViewById(R.id.btn_check_online);
        btn_discussion = (Button) findViewById(R.id.btn_discussion);

        btn_service1.setOnClickListener(this);
        btn_service2.setOnClickListener(this);
        btn_session.setOnClickListener(this);
        btn_conversation_list.setOnClickListener(this);
        btn_create_group.setOnClickListener(this);
        btn_service3.setOnClickListener(this);
        btn_add_group_user.setOnClickListener(this);
        btn_del_group_user.setOnClickListener(this);
        btn_check_online.setOnClickListener(this);
        btn_discussion.setOnClickListener(this);
    }

    /**
     * 连接融云服务器
     *
     * @param
     */
    private void connectRongServer(final String token) {


        RongIM.connect(token, new RongIMClient.ConnectCallback() {

            @Override
            public void onSuccess(String userId) {
                mUserId = userId;



            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Log.e(TAG,
                        "connect failure errorCode is :" + errorCode.getValue());
            }

            @Override
            public void onTokenIncorrect() {
                Log.e(TAG, "token is error , please check token and appkey ");
            }
        });
    }

    @Override
    protected void initData() {

    }

    private void getToken(String userId, String userName, String photoUrl) {
        LogUtil.e(TAG, "come in");
//        new HttpHelper().getToken(userId, userName, photoUrl)
//                .subscribe(new HttpObserver<String>() {
//                    @Override
//                    public void onComplete() {
//                        LogUtil.e(TAG, "onComplete");
//                    }
//
//                    @Override
//                    public void onSuccess(String data) {
//                        LogUtil.e(TAG, data);
//                        if (!TextUtils.isEmpty(data)) {
//                            connectRongServer(data);
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        super.onError(e);
//                        LogUtil.e(TAG, "onError:" + e);
//                    }
//
//                });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_service1:
                ToastUtil.showToast("gg",NewsIMActivity.this);

                    connectRongServer("3AVBwJOyf1QfT7EhL1mkizF0LOtfnojkMz5JSfRserOwT2qw3OqDGuDdVrtLnH/tMpTQAC5nI6YL6myvWXrwTh6CEZKTwHNx");


                break;
            case R.id.btn_service2:
                if (TextUtils.isEmpty(user)) {
                    //getToken("user", "user", "");
                } else {
                    connectRongServer(user);
                }
                break;
            case R.id.btn_service3:
                if (TextUtils.isEmpty(other)) {
                    getToken("other", "other", "");
                } else {
                    connectRongServer(other);
                }
                break;
            case R.id.btn_session:
                if (mUserId != null && RongIM.getInstance() != null) {
                    //此处聊天是写死的 实际开发中 大家应该写成动态的
//                    RongIM.getInstance().startPrivateChat(this,
//                            mUserId.equals("admin") ? "user" : "admin", mUserId.equals("admin") ? "用户" : "管理员");

                }
                RongIM.getInstance().startPrivateChat(this,"18398850875","张小康");
                break;
            case R.id.btn_conversation_list:
                startActivity(new Intent(this, ConversationListActivity.class));
                break;
            case R.id.btn_create_group:
                //创建讨论组
//                RongIM.getInstance().createDiscussion("讨论组1", mUserIds, new_monitor RongIMClient.CreateDiscussionCallback() {
//                    @Override
//                    public void onSuccess(String s) {
//                        LogUtil.e(TAG,"s:"+s);
//                    }
//
//                    @Override
//                    public void onError(RongIMClient.ErrorCode errorCode) {
//                        LogUtil.e(TAG,"errorCode:"+errorCode);
//                    }
//                });

                //创建讨论组并进入会话
                RongIM.getInstance().createDiscussionChat(this, mUserIds, "讨论组3", new RongIMClient.CreateDiscussionCallback() {
                    @Override
                    public void onSuccess(String s) {
                        LogUtil.e(TAG, "s:" + s);
                        mGroupId = s;
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
                break;
            case R.id.btn_add_group_user:
                List<String> addUserIds = new ArrayList<>();
                addUserIds.add("user");
                /**
                 * 添加一名或者一组用户加入讨论组。
                 *
                 * @param discussionId 讨论组 Id。
                 * @param userIdList   邀请的用户 Id 列表。
                 * @param callback     执行操作的回调。
                 */
                RongIM.getInstance().addMemberToDiscussion(mGroupId, addUserIds, new RongIMClient.OperationCallback() {

                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });

                break;
            case R.id.btn_del_group_user:
                /**
                 * 供创建者将某用户移出讨论组。
                 *
                 * 移出自己或者调用者非讨论组创建者将产生 {@link RongIMClient.ErrorCode#UNKNOWN} 错误。
                 *
                 * @param discussionId 讨论组 Id。
                 * @param userId       用户 Id。
                 * @param callback     执行操作的回调。
                 */
                RongIM.getInstance().removeMemberFromDiscussion(mGroupId, "user", new RongIMClient.OperationCallback() {

                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
                break;
            case R.id.btn_check_online:
//                new HttpHelper().checkOnline(mUserId).subscribe(new HttpObserver<String>() {
//                    @Override
//                    public void onComplete() {
//
//                    }
//
//                    @Override
//                    public void onSuccess(String model) {
//                        LogUtil.e(TAG,"checkOnlineStatus:"+model);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        super.onError(e);
//                        LogUtil.e(TAG,"checkOnlineStatusError:"+e.getMessage());
//                    }
//                });
                break;
            case R.id.btn_discussion:
                 startActivity(new Intent(this,ConversationListTestActivity.class));
//                RongIM.getInstance().disconnect();
//                btn_service1.setClickable(true);
//                btn_service2.setClickable(true);
//                btn_service3.setClickable(true);
                break;
        }
    }

    @Override
    public UserInfo getUserInfo(String s) {
        for (UserInfo u : userInfos) {
            if (s.equals(u.getName())) {
                return u;
            }
        }
        return null;
    }
}
