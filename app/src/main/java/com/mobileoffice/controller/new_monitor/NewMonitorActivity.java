package com.mobileoffice.controller.new_monitor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobileoffice.R;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.controller.BaseFragment;
import com.mobileoffice.controller.message.contact.ContactFragment;
import com.mobileoffice.controller.message.contact.ContactPersonInfoActivity;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.ContactSearchJson;
import com.mobileoffice.json.PersonInfoJson;
import com.mobileoffice.json.TransferJson;
import com.mobileoffice.json.TransferingJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.ToastUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 99213 on 2017/6/18.
 */

public class NewMonitorActivity extends BaseActivity implements View.OnClickListener {
    private NoScrollViewPager vp_new_monitor;
    private MyFragmentPagerAdapter mMyFragmentPagerAdapter;
    private List<Fragment> mListfragment; //创建一个List<Fragment>
    private BaseInfoFragment mBaseInfoFragment;
    private OrganInfoFragment mOrganInfoFragment;
    private TransferInfoFragment mTransferInfoFragment;
    private ContactInfoFragment mContactInfoFragment;
    private PreviewInfoFragment mPreviewInfoFragment;
    private int currentPosition = 0;
    private TextView tv_new_monitor_title;
    private LinearLayout ll_new_monitor_back;
    private RelativeLayout rl_new_monitor;

    @Override
    protected void initVariable() {
        mListfragment = new ArrayList<>();
        CONSTS.CONTACT_LIST = new ArrayList<>();



        mBaseInfoFragment = BaseInfoFragment.newIntance(vp_new_monitor, tv_new_monitor_title);
        mOrganInfoFragment = OrganInfoFragment.newIntance(vp_new_monitor, tv_new_monitor_title);
        mTransferInfoFragment = TransferInfoFragment.newIntance(vp_new_monitor, tv_new_monitor_title);
        mContactInfoFragment = ContactInfoFragment.newIntance(vp_new_monitor, tv_new_monitor_title);
        mPreviewInfoFragment = PreviewInfoFragment.newIntance(vp_new_monitor, tv_new_monitor_title, rl_new_monitor);
        mListfragment.add(mBaseInfoFragment);
        mListfragment.add(mOrganInfoFragment);
        mListfragment.add(mTransferInfoFragment);
        mListfragment.add(mContactInfoFragment);
        mListfragment.add(mPreviewInfoFragment);
        mMyFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mListfragment);

        vp_new_monitor.setAdapter(mMyFragmentPagerAdapter);
        vp_new_monitor.setCurrentItem(0); //设置当前页是第一页
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.new_monitor);
        tv_new_monitor_title = (TextView) findViewById(R.id.tv_new_monitor_title);
        vp_new_monitor = (NoScrollViewPager) findViewById(R.id.vp_new_monitor);
        ll_new_monitor_back = (LinearLayout) findViewById(R.id.ll_new_monitor_back);
        rl_new_monitor = (RelativeLayout) findViewById(R.id.rl_new_monitor);
        ll_new_monitor_back.setOnClickListener(this);

        NewMonitorBaseFragment.objBean = new TransferJson.ObjBean();
        TransferJson.ObjBean modifyTransfer = (TransferJson.ObjBean) getIntent().getSerializableExtra("modifyTransfer");
        if(modifyTransfer!=null){
            NewMonitorBaseFragment.objBean = modifyTransfer;

            //加载头像
            loadPhoneData( NewMonitorBaseFragment.objBean.getPhone(),"trueUrl");
            loadPhoneData( NewMonitorBaseFragment.objBean.getContactPhone(),"contactUrl");
            loadPhoneData( NewMonitorBaseFragment.objBean.getOpoContactPhone(),"opoContactUrl");

        }

    }
    /**
     * 加载用户信息
     */
    private void loadPhoneData(final String phone,final String pType) {
        RequestParams params = new RequestParams(URL.USER);
        params.addQueryStringParameter("action", "personInfo");
        params.addQueryStringParameter("phone", phone);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                PersonInfoJson personInfoJson = gson.fromJson(result,PersonInfoJson.class);
                if(personInfoJson!=null&&personInfoJson.getResult()==CONSTS.SEND_OK){
                    PersonInfoJson.ObjBean newObjBean = personInfoJson.getObj();
                    if("trueUrl".equals(pType)){
                        String flag = newObjBean.getIs_upload_photo();
                        if("0".equals(flag)){
                            NewMonitorBaseFragment.objBean.setTrueUrl(newObjBean.getWechat_url());
                        }else if("1".equals(flag)){
                            NewMonitorBaseFragment.objBean.setTrueUrl(newObjBean.getPhoto_url());
                        }

                    }else  if("contactUrl".equals(pType)){
                        String flag = newObjBean.getIs_upload_photo();
                        if("0".equals(flag)){
                            NewMonitorBaseFragment.objBean.setContactUrl(newObjBean.getWechat_url());
                        }else if("1".equals(flag)){
                            NewMonitorBaseFragment.objBean.setContactUrl(newObjBean.getPhoto_url());
                        }

                    }else  if("opoContactUrl".equals(pType)){
                        String flag = newObjBean.getIs_upload_photo();
                        if("0".equals(flag)){
                            NewMonitorBaseFragment.objBean.setOpoContactUrl(newObjBean.getWechat_url());
                        }else if("1".equals(flag)){
                            NewMonitorBaseFragment.objBean.setOpoContactUrl(newObjBean.getPhoto_url());
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
    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_new_monitor_back:
                finish();
                break;
        }
    }

    public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private FragmentManager fragmetnmanager;  //创建FragmentManager
        private List<Fragment> listfragment; //创建一个List<Fragment>

        //定义构造带两个参数
        public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.fragmetnmanager = fm;
            this.listfragment = list;
        }

        @Override
        public Fragment getItem(int arg0) {
            // TODO Auto-generated method stub
            return listfragment.get(arg0); //返回第几个fragment
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return listfragment.size(); //总共有多少个fragment
        }


    }
}
