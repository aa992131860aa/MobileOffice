package com.mobileoffice.controller.me;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.mobileoffice.R;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.PhotoJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by 99213 on 2017/8/14.
 */

public class FeedbackActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout ll_back;
    private EditText edt_content;
    private Button btn_commit;

    @Override
    protected void initVariable() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.feedback);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        edt_content = (EditText) findViewById(R.id.edt_content);
        btn_commit = (Button) findViewById(R.id.btn_commit);

        ll_back.setOnClickListener(this);
        btn_commit.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_back:
                finish();
                break;
            //提交反馈意见
            case R.id.btn_commit:
                String content = edt_content.getText().toString().trim();
                String phone = SharePreUtils.getString("phone","",this);
                if (!"".equals(content)) {
                     insertFeedback(phone,content);
                } else {
                    ToastUtil.showToast("请输入反馈内容", this);
                }
                break;
        }
    }

    private void insertFeedback(final String phone, String content) {
        RequestParams params = new RequestParams(URL.ME);
        params.addBodyParameter("action","feedback");
        params.addBodyParameter("phone", phone);
        params.addBodyParameter("content", content);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    edt_content.setText("");
                    ToastUtil.showToast("提交成功", FeedbackActivity.this);
                } else {
                    ToastUtil.showToast("提交失败", FeedbackActivity.this);
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
}
