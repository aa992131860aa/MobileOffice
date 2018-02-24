package com.mobileoffice.controller.rong;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobileoffice.R;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * Created by 99213 on 2017/5/10.
 */

public class ConversationListTestActivity extends FragmentActivity {
    private List<Conversation> mConversations;
    private RecyclerView rv_test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversationtestlist);
        rv_test = (RecyclerView) findViewById(R.id.rv_test);
        rv_test.setLayoutManager(new LinearLayoutManager(this));
        mConversations = new ArrayList<>();
        final ConversationListTestAdapter adapter = new ConversationListTestAdapter(this,mConversations);
        adapter.setConversationListTextListener(new ConversationListTestAdapter.ConversationListTextListener() {
            @Override
            public void onItemClick(View view, int position) {
                RongIM.getInstance().startPrivateChat(ConversationListTestActivity.this,
                       "user",  "用户" );
            }
        });
        rv_test.setAdapter(adapter);

        RongIM.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                     Conversation c = new Conversation();
                     c.setTargetId("system");
                     conversations.add(c);
                    adapter.refresh(conversations);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });

      //  enterFragment();
    }

    /**
     * 加载 会话列表 ConversationListFragment
     */
    private void enterFragment() {

        ConversationListFragment fragment = (ConversationListFragment) getSupportFragmentManager().findFragmentById(R.id.conversationlist);
        //fragment.setAdapter(new ConversationListAdapterEx(RongContext.getInstance()));
        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//设置群组会话聚合显示
                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置讨论组会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")//设置系统会话非聚合显示
                .build();

        fragment.setUri(uri);
    }
}
