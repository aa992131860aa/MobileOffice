package com.mobileoffice.controller.cloud_monitor;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.google.gson.Gson;
import com.mobileoffice.R;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.PhotoJson;
import com.mobileoffice.json.TransferPushSiteJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by 99213 on 2017/11/15.
 */

public class TransferSiteActivity extends BaseActivity implements View.OnClickListener {
    //推送设置
    private Switch switch_temperature;
    private Switch switch_open;
    private Switch switch_collision;


    private String mOrganSeg;
    private String mPhone;

    private LinearLayout ll_back;

    @Override
    protected void initVariable() {
        mOrganSeg = getIntent().getStringExtra("organSeg");
        mPhone = SharePreUtils.getString("phone", "", this);

        getPushSite(mOrganSeg, mPhone);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.transfer_site);
        switch_temperature = (Switch) findViewById(R.id.switch_temperature);
        switch_collision = (Switch) findViewById(R.id.switch_collision);
        switch_open = (Switch) findViewById(R.id.switch_open);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);

        switch_temperature.setOnClickListener(this);
        switch_collision.setOnClickListener(this);
        switch_open.setOnClickListener(this);
        ll_back.setOnClickListener(this);


    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_temperature:
                if (switch_temperature.isChecked()) {
                    setPushSite(switch_temperature,mOrganSeg, mPhone, "temperature", "0");
                } else {
                    setPushSite(switch_temperature,mOrganSeg, mPhone, "temperature", "1");
                }
                break;
            case R.id.switch_collision:
                if (switch_collision.isChecked()) {
                    setPushSite(switch_collision,mOrganSeg, mPhone, "collision", "0");
                } else {
                    setPushSite(switch_collision,mOrganSeg, mPhone, "collision", "1");
                }
                break;
            case R.id.switch_open:
                if (switch_open.isChecked()) {
                    setPushSite(switch_open,mOrganSeg, mPhone, "open", "0");
                } else {
                    setPushSite(switch_open,mOrganSeg, mPhone, "open", "1");
                }
                break;
            case R.id.ll_back:
                finish();
                break;
        }
    }

    private void getPushSite(String organSeg, final String phone) {
        showWaitDialog(getResources().getString(R.string.loading), true, "");
        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "getTransferPushSite");
        params.addBodyParameter("phone", phone);
        params.addBodyParameter("organSeg", organSeg);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                TransferPushSiteJson transferPushSiteJson = new Gson().fromJson(result, TransferPushSiteJson.class);
                if (transferPushSiteJson != null && transferPushSiteJson.getResult() == CONSTS.SEND_OK) {
                    if (transferPushSiteJson.getObj().getTemperatureStatus() == 1) {
                        switch_temperature.setChecked(false);
                    }
                    if (transferPushSiteJson.getObj().getCollisionStatus() == 1) {
                        switch_collision.setChecked(false);
                    }
                    if (transferPushSiteJson.getObj().getOpenStatus() == 1) {
                        switch_open.setChecked(false);
                    }
                }

                dismissDialog();

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                dismissDialog();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void setPushSite(final  Switch s,String organSeg, final String phone, final String type, final String status) {

        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "setTransferPushSite");
        params.addBodyParameter("phone", phone);
        params.addBodyParameter("organSeg", organSeg);
        params.addBodyParameter("type", type);
        params.addBodyParameter("status", status);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                ToastUtil.showToast(result,TransferSiteActivity.this);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {

                } else {
                      if("temperature".equals(type)){
                         // ToastUtil.showToast("温度示警设置失败",TransferSiteActivity.this);
                          if("0".equals(status)){
                              s.setChecked(false);
                          }else{
                              s.setChecked(true);
                          }
                      }
                    if("collision".equals(type)){
                        //ToastUtil.showToast("碰撞提醒设置失败",TransferSiteActivity.this);
                        if("0".equals(status)){
                            s.setChecked(false);
                        }else{
                            s.setChecked(true);
                        }
                    }
                    if("open".equals(type)){
                        //ToastUtil.showToast("开箱提醒设置失败",TransferSiteActivity.this);
                        if("0".equals(status)){
                            s.setChecked(false);
                        }else{
                            s.setChecked(true);
                        }
                    }
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
