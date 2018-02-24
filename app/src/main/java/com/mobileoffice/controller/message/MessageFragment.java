package com.mobileoffice.controller.message;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobileoffice.R;

import com.mobileoffice.controller.message.contact.ContactFragment;
import com.mobileoffice.controller.message.contact.ContactPersonAddActivity;
import com.mobileoffice.controller.new_monitor.NewMonitorBaseFragment;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.ContactListJson;
import com.mobileoffice.json.ContactPersonJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.DisplayUtil;
import com.mobileoffice.utils.SharePreUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.UserInfo;



/**
 * Created by 99213 on 2017/5/19.
 */

public class MessageFragment extends NewMonitorBaseFragment implements View.OnClickListener {
    private View root;
    public static ViewPager vp_message;
    private List<Fragment> mFragments;
    private ContactFragment mContactFragment;
    private ConversationListFragment mConversationListFragment;
    private Context mContext;
    private MessageFragmentPagerAdapter mMessageFragmentPagerAdapter;

    //标题(消息 联系人)
    private LinearLayout ll_title_message;
    private TextView tv_title_message;
    private TextView tv_title_message_line;

    private LinearLayout ll_title_contact;
    private TextView tv_title_contact;
    private TextView tv_title_contact_line;

    //添加联系人
    private LinearLayout ll_message_add;
    private String TAG = "MessageFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.message, null);
        mContext = getActivity();
        initView();
        mFragments = new ArrayList<>();
        enterFragment();
        mContactFragment = new ContactFragment();
        mFragments.add(mConversationListFragment);
        mFragments.add(mContactFragment);
        mMessageFragmentPagerAdapter = new MessageFragmentPagerAdapter(getChildFragmentManager(), mFragments);
        vp_message.setAdapter(mMessageFragmentPagerAdapter);
        vp_message.setCurrentItem(0);
        vp_message.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //ToastUtil.showToast("position:" + position);
                if (position == 0) {
                    switchMessage();
                } else if (position == 1) {
                    switchContact();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        return root;
    }

    public static final MessageFragment newInstance() {
        MessageFragment fragment = new MessageFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("organSeg", organSeg);
//        bundle.putString("type", type);
//        fragment.setArguments(bundle);

        return fragment;
    }
    private void switchMessage() {
        tv_title_message.setTextSize(DisplayUtil.px2dip(getActivity(), getResources().getDimension(R.dimen.title_size)));
        tv_title_message.getPaint().setFakeBoldText(true);
        tv_title_contact.setTextSize(DisplayUtil.px2dip(getActivity(), getResources().getDimension(R.dimen.title_small)));
        tv_title_contact.getPaint().setFakeBoldText(false);
        tv_title_message_line.setVisibility(View.VISIBLE);
        tv_title_contact_line.setVisibility(View.GONE);
    }

    private void switchContact() {
        tv_title_contact.setTextSize(DisplayUtil.px2dip(getActivity(), getResources().getDimension(R.dimen.title_size)));
        tv_title_contact.getPaint().setFakeBoldText(true);
        tv_title_message.setTextSize(DisplayUtil.px2dip(getActivity(), getResources().getDimension(R.dimen.title_small)));
        tv_title_message.getPaint().setFakeBoldText(false);
        tv_title_message_line.setVisibility(View.GONE);
        tv_title_contact_line.setVisibility(View.VISIBLE);
    }

    private void initView() {
        vp_message = (ViewPager) root.findViewById(R.id.vp_message);

        ll_title_message = (LinearLayout) root.findViewById(R.id.ll_title_message);
        tv_title_message = (TextView) root.findViewById(R.id.tv_title_message);
        tv_title_message_line = (TextView) root.findViewById(R.id.tv_title_message_line);

        ll_title_contact = (LinearLayout) root.findViewById(R.id.ll_title_contact);
        tv_title_contact = (TextView) root.findViewById(R.id.tv_title_contact);
        tv_title_contact_line = (TextView) root.findViewById(R.id.tv_title_contact_line);
        ll_message_add = (LinearLayout) root.findViewById(R.id.ll_message_add);

        ll_message_add.setOnClickListener(this);
        ll_title_message.setOnClickListener(this);
        ll_title_contact.setOnClickListener(this);

//        RongIM.setGroupInfoProvider(new RongIM.GroupInfoProvider() {
//            @Override
//            public Group getGroupInfo(String s) {
//                LogUtil.e(TAG,"s:"+s);
//                Group group = new Group(s,"群聊",Uri.parse(URL.TOMCAT_SOCKET+"images/team.png"));
//                return group;
//            }
//        }, true);
    }

    private void loadContactPerson() {
        RequestParams params = new RequestParams(URL.RONG);
        params.addBodyParameter("action", "getGroupList");
        params.addBodyParameter("phone", SharePreUtils.getString("phone", "", getActivity()));
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ContactPersonJson contactPersonJson = new Gson().fromJson(result, ContactPersonJson.class);
                if (contactPersonJson != null && contactPersonJson.getResult() == CONSTS.SEND_OK) {
                    List<ContactPersonJson.ObjBean> mObjBean = contactPersonJson.getObj();
                    for (int i = 0; i < mObjBean.size(); i++) {
                        if (mObjBean.get(i).getGroupName().contains("-心脏")) {
                            RongIM.getInstance().refreshGroupInfoCache(new Group(mObjBean.get(i).getOrganSeg(), mObjBean.get(i).getGroupName(), Uri.parse(URL.TOMCAT_SOCKET+"images/team1.png")));
                        } else if (mObjBean.get(i).getGroupName().contains("-肝脏")) {
                            RongIM.getInstance().refreshGroupInfoCache(new Group(mObjBean.get(i).getOrganSeg(), mObjBean.get(i).getGroupName(), Uri.parse(URL.TOMCAT_SOCKET+"images/team2.png")));
                        } else if (mObjBean.get(i).getGroupName().contains("-肺")) {
                            RongIM.getInstance().refreshGroupInfoCache(new Group(mObjBean.get(i).getOrganSeg(), mObjBean.get(i).getGroupName(), Uri.parse(URL.TOMCAT_SOCKET+"images/team3.png")));
                        } else if (mObjBean.get(i).getGroupName().contains("-肾脏")) {
                            RongIM.getInstance().refreshGroupInfoCache(new Group(mObjBean.get(i).getOrganSeg(), mObjBean.get(i).getGroupName(), Uri.parse(URL.TOMCAT_SOCKET+"images/team4.png")));
                        } else if (mObjBean.get(i).getGroupName().contains("-胰脏")) {
                            RongIM.getInstance().refreshGroupInfoCache(new Group(mObjBean.get(i).getOrganSeg(), mObjBean.get(i).getGroupName(), Uri.parse(URL.TOMCAT_SOCKET+"images/team5.png")));
                        } else if (mObjBean.get(i).getGroupName().contains("-眼角膜")) {
                            RongIM.getInstance().refreshGroupInfoCache(new Group(mObjBean.get(i).getOrganSeg(), mObjBean.get(i).getGroupName(), Uri.parse(URL.TOMCAT_SOCKET+"images/team6.png")));
                        } else {
                            RongIM.getInstance().refreshGroupInfoCache(new Group(mObjBean.get(i).getOrganSeg(), mObjBean.get(i).getGroupName(), Uri.parse(URL.TOMCAT_SOCKET+"images/team.png")));
                        }

                    }

                } else {

                }
                //ToastUtil.showToast(contactPersonJson.getMsg()+","+contactPersonJson.getObj().size(),getActivity());
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void getContactList() {

        RequestParams params = new RequestParams(URL.CONTACT);
        params.addBodyParameter("action", "getContactList");
        params.addBodyParameter("phone", SharePreUtils.getString("phone", "", getActivity()));
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    //Log.e(TAG, "result:" + result);
                    ContactListJson json = new Gson().fromJson(result, ContactListJson.class);
                    if (json != null && json.getResult() == CONSTS.SEND_OK) {
                        if (json.getObj() != null) {
                            List<ContactListJson.ObjBean> objBeens = json.getObj();
                            for (int i = 0; i < objBeens.size(); i++) {
                                //Log.e(TAG, i + "size:" + objBeens.size() + ",phone1111:" + objBeens.get(i).getContactPhone() + ",trueName:" + objBeens.get(i).getTrueName() + ",photoFile:" + objBeens.get(i).getWechatUrl() + "," + objBeens.get(i).getPhotoFile() + "," + objBeens.get(i).getIsUploadPhoto());
                                //Log.e(TAG, i + "size:" + objBeens.size() + ",phone1112:" + objBeens.get(i).getContactPhone() + ",trueName:" + objBeens.get(i).getTrueName() + ",photoFile:" + objBeens.get(i).getWechatUrl() + "," + objBeens.get(i).getPhotoFile() + "," + objBeens.get(i).getIsUploadPhoto());
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
                    //Log.e(TAG, "phone1113:" + ex.getMessage() + "," + ex.getLocalizedMessage());
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Log.e(TAG, "phone1114:" + ex.getMessage() + "," + ex.getLocalizedMessage());
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
    public void onStart() {
        super.onStart();

        loadContactPerson();
        getContactList();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    /**
     * 加载 会话列表 ConversationListFragment
     */
    private void enterFragment() {
        mConversationListFragment = new ConversationListFragment();
        // mConversationListFragment.setAdapter(new ConversationListAdapterEx(mContext));

        //RongIM.getInstance().registerConversationTemplate(new GroupProviderTemp());
        //mConversationListFragment.setAdapter(new ConversationListAdapterEx(RongContext.getInstance()));
        Uri uri = Uri.parse("rong://" + mContext.getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//设置群组会话聚合显示
                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置讨论组会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")//设置系统会话非聚合显示
                .build();

        mConversationListFragment.setUri(uri);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            //转换为消息
            case R.id.ll_title_message:
                vp_message.setCurrentItem(0);
                break;
            //转换为联系人
            case R.id.ll_title_contact:
                vp_message.setCurrentItem(1);
                break;
            //添加联系人
            case R.id.ll_message_add:
                intent.setClass(getActivity(), ContactPersonAddActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void initData() {
        if (!CONSTS.IS_NEWS) {
            vp_message.setCurrentItem(0);
            CONSTS.IS_NEWS = true;
        }
    }
}
