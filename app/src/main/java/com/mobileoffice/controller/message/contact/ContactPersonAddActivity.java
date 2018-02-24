package com.mobileoffice.controller.message.contact;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.mobileoffice.R;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.ContactSearchJson;
import com.mobileoffice.json.PhotoJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by 99213 on 2017/6/21.
 */

public class ContactPersonAddActivity extends BaseActivity implements View.OnClickListener, ContactPersonAddAdapter.OnRecyclerViewItemClickListener {
    private LinearLayout ll_contact_person_add_back;
    private RecyclerView rv_contact_person_add;
    private LinearLayout ll_contact_person_add_no;
    private LinearLayoutManager mLinearLayoutManager;
    private List<ContactSearchJson.ObjBean> mObjBeans = new ArrayList<>();
    private ContactPersonAddAdapter mContactPersonAddAdapter;
    private EditText edt_contact_person_add;
    private String otherPhone;
    private String phone;
    private String name;
    private LinearLayout ll_contact_person_add_search;
    private String TAG = "ContactPersonAddActivity";

    @Override
    protected void initVariable() {
        ll_contact_person_add_no.setVisibility(View.VISIBLE);
        rv_contact_person_add.setVisibility(View.GONE);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mContactPersonAddAdapter = new ContactPersonAddAdapter(mObjBeans, this);
        mContactPersonAddAdapter.setOnItemClickListener(this);
        rv_contact_person_add.setLayoutManager(mLinearLayoutManager);
        rv_contact_person_add.setAdapter(mContactPersonAddAdapter);


        phone = SharePreUtils.getString("phone", "", this);

        edt_contact_person_add.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String name = edt_contact_person_add.getText().toString().trim();
                loadData(phone, name);

            }
        });


    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.contact_person_add);
        ll_contact_person_add_back = (LinearLayout) findViewById(R.id.ll_contact_person_add_back);
        rv_contact_person_add = (RecyclerView) findViewById(R.id.rv_contact_person_add);
        ll_contact_person_add_no = (LinearLayout) findViewById(R.id.ll_contact_person_add_no);
        edt_contact_person_add = (EditText) findViewById(R.id.edt_contact_person_add);
        ll_contact_person_add_search = (LinearLayout) findViewById(R.id.ll_contact_person_add_search);

        ll_contact_person_add_back.setOnClickListener(this);
        ll_contact_person_add_search.setOnClickListener(this);
    }

    private void loadData(String phone, String name) {
       // ToastUtil.showToast("phone:"+phone+",name:"+name);
        RequestParams params = new RequestParams(URL.CONTACT);
        params.addBodyParameter("action","search");
        params.addQueryStringParameter("phone", phone);
        params.addBodyParameter("name", name);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                ContactSearchJson contactSearchJson = gson.fromJson(result, ContactSearchJson.class);
                if (contactSearchJson != null && contactSearchJson.getResult() == CONSTS.SEND_OK) {
                    mObjBeans = contactSearchJson.getObj() == null ? new ArrayList<ContactSearchJson.ObjBean>() : contactSearchJson.getObj();
                    mContactPersonAddAdapter.refresh(mObjBeans);
                    if(mObjBeans.size()>0){
                        rv_contact_person_add.setVisibility(View.VISIBLE);
                        ll_contact_person_add_no.setVisibility(View.GONE);
                    }else{
                        rv_contact_person_add.setVisibility(View.GONE);
                        ll_contact_person_add_no.setVisibility(View.VISIBLE);
                    }
                }else{
                    rv_contact_person_add.setVisibility(View.GONE);
                    ll_contact_person_add_no.setVisibility(View.VISIBLE);
                }
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_contact_person_add_back:
                finish();
                break;
            case R.id.ll_contact_person_add_search:
                loadData(phone,edt_contact_person_add.getText().toString().trim());
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position) {

        //ToastUtil.showToast("onItemClick");
        Intent intent = new Intent(ContactPersonAddActivity.this,ContactPersonInfoActivity.class);
        intent.putExtra("contactPerson", (Serializable) mObjBeans.get(position));
        startActivity(intent);
    }

    @Override
    public void onButtonClick(final View view, int position) {
        RequestParams params = new RequestParams(URL.PUSH);
        String trueName = SharePreUtils.getString("trueName","",this);
        params.addBodyParameter("action","add");
        params.addBodyParameter("phone",phone);
        params.addBodyParameter("content",trueName+" 请求添加好友");
        params.addBodyParameter("type","add");
        params.addBodyParameter("otherId",mObjBeans.get(position).getOther_id()+"");
        params.addBodyParameter("targetPhone",mObjBeans.get(position).getPhone());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                PhotoJson photoJson = gson.fromJson(result, PhotoJson.class);
                if(photoJson!=null&& photoJson.getResult()==CONSTS.SEND_OK){

                    ((Button)view).setText("验证中");
                    ((Button)view).setBackground(getDrawable(R.drawable.edit_border_gray));
                    ((Button)view).setEnabled(false);

                }
                LogUtil.e(TAG,"result:"+result);
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
        //ToastUtil.showToast("onButtonClick");

    }
}
