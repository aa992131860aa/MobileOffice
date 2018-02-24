package com.mobileoffice.controller.message.contact;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobileoffice.R;
import com.mobileoffice.controller.cloud_monitor.CloudMonitorFragment;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.ContactPersonJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.rong.imkit.RongIM;

/**
 * Created by 99213 on 2017/5/19.
 */

public class ContactFragment extends Fragment implements View.OnClickListener {
    private View root;
    //联系人
    private LinearLayout ll_contact_person;
    //群组
    private LinearLayout ll_contact_group;
    //ll_contact_item_group
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.contact, null);
        init();


        return root;

    }

    public static final ContactFragment newInstance() {
        ContactFragment fragment = new ContactFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("organSeg", organSeg);
//        bundle.putString("type", type);
//        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void init() {
        ll_contact_person = (LinearLayout) root.findViewById(R.id.ll_contact_person);
        ll_contact_group = (LinearLayout) root.findViewById(R.id.ll_contact_group);
        ll_contact_person.setOnClickListener(this);
        ll_contact_group.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_contact_person:
                startActivity(new Intent(getActivity(), ContactPersonActivity.class));
                break;
            case R.id.ll_contact_group:
                startActivity(new Intent(getActivity(),ContactActivity.class));
        }

    }

}
