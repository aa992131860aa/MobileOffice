package com.mobileoffice.controller.message.contact;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.mobileoffice.R;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.ContactPersonJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.SharePreUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;

/**
 * Created by 99213 on 2017/7/24.
 */

public class ContactActivity extends BaseActivity implements ContactFragmentAdapter.HospitalChildListener, View.OnClickListener {
    private RecyclerView rv_contact;
    private LinearLayoutManager mLinearLayoutManager;
    private ContactFragmentAdapter mContactFragmentAdapter;
    private List<ContactPersonJson.ObjBean> mObjBean;
    private LinearLayout ll_history_back;
    private LinearLayout ll_no_data;
    @Override
    protected void initVariable() {
        recycler();
        loadContactPerson();
    }

    private void recycler() {
        mObjBean = new ArrayList<>();
        mLinearLayoutManager = new LinearLayoutManager(this);
        rv_contact = (RecyclerView) findViewById(R.id.rv_contact);
        rv_contact.setLayoutManager(mLinearLayoutManager);
        mContactFragmentAdapter = new ContactFragmentAdapter(this, mObjBean);
        rv_contact.setAdapter(mContactFragmentAdapter);
        mContactFragmentAdapter.setHospitalParentListener(this);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.contact_group);
        rv_contact = (RecyclerView) findViewById(R.id.rv_contact);
        ll_history_back = (LinearLayout) findViewById(R.id.ll_history_back);
        ll_no_data = (LinearLayout) findViewById(R.id.ll_no_data);
        ll_history_back.setOnClickListener(this);
    }

    private void loadContactPerson() {
        RequestParams params = new RequestParams(URL.RONG);
        params.addBodyParameter("action", "getGroupList");
        params.addBodyParameter("phone", SharePreUtils.getString("phone", "", this));
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ContactPersonJson contactPersonJson = new Gson().fromJson(result, ContactPersonJson.class);
                if (contactPersonJson != null && contactPersonJson.getResult() == CONSTS.SEND_OK) {
                    mObjBean = contactPersonJson.getObj();
                    mContactFragmentAdapter.refresh(contactPersonJson.getObj());
                    ll_no_data.setVisibility(View.GONE);
                    rv_contact.setVisibility(View.VISIBLE);
                } else {
                    ll_no_data.setVisibility(View.VISIBLE);
                    rv_contact.setVisibility(View.GONE);
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

    @Override
    protected void initData() {

    }

    @Override
    public void OnHospitalParentClick(int position) {
        RongIM.getInstance().startGroupChat(this, mObjBean.get(position).getOrganSeg(), mObjBean.get(position).getGroupName());
        CONSTS.ORGAN_SEG = mObjBean.get(position).getOrganSeg();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_history_back:
                finish();
                break;
        }
    }
}
