package com.mobileoffice.controller.new_monitor;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobileoffice.MainActivity;
import com.mobileoffice.R;
import com.mobileoffice.controller.message.contact.ContactPersonActivity;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.ContactListJson;
import com.mobileoffice.json.PersonInfoJson;
import com.mobileoffice.json.PhotoJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.LocationUtils;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;
import com.mobileoffice.view.NewMonitorPopup;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * Created by 99213 on 2017/6/29.
 */

public class PreviewInfoFragment extends NewMonitorBaseFragment implements View.OnClickListener {
    private static final String TAG = "BaseInfoFragment";
    private View root;
    private static ViewPager mViewPager;
    private static TextView textView;
    private LinearLayout ll_frg_preview_info_root;
    private String contactUsersId = "";
    private String usersId = "";
    private String phone = "";

    //增加,减少成员
    RelativeLayout addAddContactView;
    RelativeLayout addMinusContactView;
    //上一步,下一步
    private TextView tv_frg_preview_info_pre;
    private TextView tv_frg_preview_info_next;

    //添加成员
    private RecyclerView rv_frg_preview_info;
    private LinearLayoutManager mLinearLayoutManager;
    private ContactAddAdapter mContactAddAdapter;
    private List<ContactListJson.ObjBean> mObjBean;
    //人数
    private TextView tv_frg_preview_info_number;
    private LinearLayout ll_frg_preview_info_team;

    private LinearLayout ll_frg_preview_info_base;
    private ImageView iv_frg_preview_info_base;
    private LinearLayout ll_frg_preview_info_base_hide;
    private TextView tv_frg_preview_info_organ;
    private TextView tv_frg_preview_info_organ_number;
    private TextView tv_frg_preview_info_blood;
    private TextView tv_frg_preview_info_blood_number;
    private TextView tv_frg_preview_info_sample_organ;
    private TextView tv_frg_preview_info_sample_organ_number;
    private LinearLayout ll_frg_preview_info_organ;
    private LinearLayout ll_frg_preview_info_organ_hide;
    private ImageView iv_frg_preview_info_organ;
    private TextView tv_frg_preview_info_organ_seg;
    private TextView tv_frg_preview_info_time;
    private TextView tv_frg_preview_info_open_pwd;
    private LinearLayout ll_frg_preview_info_transfer;
    private LinearLayout ll_frg_preview_info_transfer_hide;
    private ImageView iv_frg_preview_info_transfer;
    private TextView tv_frg_preview_info_from_city;
    private TextView tv_frg_preview_info_to_hosp;
    private TextView tv_frg_preview_info_way;
    private TextView tv_frg_preview_info_no;
    private LinearLayout ll_frg_preview_info_opo;
    private LinearLayout ll_frg_preview_info_opo_hide;
    private ImageView iv_frg_preview_info_opo;
    private TextView tv_frg_preview_info_opo_name;
    private TextView tv_frg_preview_info_contact_name;
    private TextView tv_frg_preview_info_contact_phone;
    private TextView tv_frg_preview_info_box_no;
    private static RelativeLayout mTitleView;
    private LinearLayout ll_frg_preview_info;

    //小背景条
    private TextView tv_organ_space;
    private TextView tv_base_space;
    private TextView tv_transfer_space;
    private TextView tv_opo_space;

    //医院目的地地址

    private String fromHospitalAddress = null;

    private Thread thread;
    private boolean isStop, insertGroup, loadStartLocation, loadEndLocation, loadWeather;
    private String mUsersIds;
    private AlertDialog.Builder mAlertDialog;

    //长按选择
    private TextView tv_long_tip;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.frg_preview_info, container, false);
        ll_frg_preview_info_root = (LinearLayout) root.findViewById(R.id.ll_frg_preview_info_root);
        tv_long_tip = (TextView) root.findViewById(R.id.tv_long_tip);
        tv_frg_preview_info_pre = (TextView) root.findViewById(R.id.tv_frg_preview_info_pre);
        tv_frg_preview_info_next = (TextView) root.findViewById(R.id.tv_frg_preview_info_next);
        rv_frg_preview_info = (RecyclerView) root.findViewById(R.id.rv_frg_preview_info);
        tv_frg_preview_info_number = (TextView) root.findViewById(R.id.tv_frg_preview_info_number);
        ll_frg_preview_info_team = (LinearLayout) root.findViewById(R.id.ll_frg_preview_info_team);

        ll_frg_preview_info_base = (LinearLayout) root.findViewById(R.id.ll_frg_preview_info_base);
        iv_frg_preview_info_base = (ImageView) root.findViewById(R.id.iv_frg_preview_info_base);

        ll_frg_preview_info_base_hide = (LinearLayout) root.findViewById(R.id.ll_frg_preview_info_base_hide);
        tv_frg_preview_info_organ = (TextView) root.findViewById(R.id.tv_frg_preview_info_organ);
        tv_frg_preview_info_organ_number = (TextView) root.findViewById(R.id.tv_frg_preview_info_organ_number);
        tv_frg_preview_info_blood = (TextView) root.findViewById(R.id.tv_frg_preview_info_blood);
        tv_frg_preview_info_blood_number = (TextView) root.findViewById(R.id.tv_frg_preview_info_blood_number);
        tv_frg_preview_info_sample_organ = (TextView) root.findViewById(R.id.tv_frg_preview_info_sample_organ);
        tv_frg_preview_info_sample_organ_number = (TextView) root.findViewById(R.id.tv_frg_preview_info_sample_organ_number);

        ll_frg_preview_info_organ = (LinearLayout) root.findViewById(R.id.ll_frg_preview_info_organ);
        ll_frg_preview_info_organ_hide = (LinearLayout) root.findViewById(R.id.ll_frg_preview_info_organ_hide);
        iv_frg_preview_info_organ = (ImageView) root.findViewById(R.id.iv_frg_preview_info_organ);
        tv_frg_preview_info_organ_seg = (TextView) root.findViewById(R.id.tv_frg_preview_info_organ_seg);
        tv_frg_preview_info_time = (TextView) root.findViewById(R.id.tv_frg_preview_info_time);
        tv_frg_preview_info_open_pwd = (TextView) root.findViewById(R.id.tv_frg_preview_info_open_pwd);

        ll_frg_preview_info_transfer = (LinearLayout) root.findViewById(R.id.ll_frg_preview_info_transfer);
        ll_frg_preview_info_transfer_hide = (LinearLayout) root.findViewById(R.id.ll_frg_preview_info_transfer_hide);
        iv_frg_preview_info_transfer = (ImageView) root.findViewById(R.id.iv_frg_preview_info_transfer);
        tv_frg_preview_info_from_city = (TextView) root.findViewById(R.id.tv_frg_preview_info_from_city);
        tv_frg_preview_info_to_hosp = (TextView) root.findViewById(R.id.tv_frg_preview_info_to_hosp);
        tv_frg_preview_info_way = (TextView) root.findViewById(R.id.tv_frg_preview_info_way);
        tv_frg_preview_info_no = (TextView) root.findViewById(R.id.tv_frg_preview_info_no);

        ll_frg_preview_info_opo = (LinearLayout) root.findViewById(R.id.ll_frg_preview_info_opo);
        ll_frg_preview_info_opo_hide = (LinearLayout) root.findViewById(R.id.ll_frg_preview_info_opo_hide);
        iv_frg_preview_info_opo = (ImageView) root.findViewById(R.id.iv_frg_preview_info_opo);
        tv_frg_preview_info_opo_name = (TextView) root.findViewById(R.id.tv_frg_preview_info_opo_name);
        tv_frg_preview_info_contact_name = (TextView) root.findViewById(R.id.tv_frg_preview_info_contact_name);
        tv_frg_preview_info_contact_phone = (TextView) root.findViewById(R.id.tv_frg_preview_info_contact_phone);
        ll_frg_preview_info = (LinearLayout) root.findViewById(R.id.ll_frg_preview_info);
        tv_frg_preview_info_box_no = (TextView) root.findViewById(R.id.tv_frg_preview_info_box_no);


        tv_organ_space = (TextView) root.findViewById(R.id.tv_organ_space);
        tv_base_space = (TextView) root.findViewById(R.id.tv_base_space);
        tv_transfer_space = (TextView) root.findViewById(R.id.tv_transfer_space);
        tv_opo_space = (TextView) root.findViewById(R.id.tv_opo_space);

        ll_frg_preview_info_base.setOnClickListener(this);
        ll_frg_preview_info_organ.setOnClickListener(this);
        ll_frg_preview_info_transfer.setOnClickListener(this);
        ll_frg_preview_info_opo.setOnClickListener(this);
        ll_frg_preview_info_base.setOnClickListener(this);
        ll_frg_preview_info_organ.setOnClickListener(this);
        ll_frg_preview_info_opo.setOnClickListener(this);
        ll_frg_preview_info_transfer.setOnClickListener(this);

        tv_frg_preview_info_pre.setOnClickListener(this);
        tv_frg_preview_info_next.setOnClickListener(this);
        ll_frg_preview_info_team.setOnClickListener(this);
        phone = SharePreUtils.getString("phone", "", getActivity());

        loadUserByPhone();
        initInfo();


        //加载联系人,修改的时候
        if (CONSTS.TRANSFER_STATUS == 1 && CONSTS.CONTACT_LIST.size() == 0) {
            loadContact(objBean.getOrganSeg());
        }
        //ToastUtil.showToast(""+CONSTS.CONTACT_LIST.size(),getContext());


        return root;
    }

    private void previewData() {//上海
        tv_frg_preview_info_organ.setText(objBean.getOrgan());
        tv_frg_preview_info_organ_number.setText(objBean.getOrganNum());
        tv_frg_preview_info_blood.setText(objBean.getBlood());
        tv_frg_preview_info_blood_number.setText(objBean.getBloodNum());
        tv_frg_preview_info_sample_organ.setText(objBean.getSampleOrgan());
        tv_frg_preview_info_sample_organ_number.setText(objBean.getSampleOrganNum());

        tv_frg_preview_info_box_no.setText(objBean.getBoxNo());
        tv_frg_preview_info_organ_seg.setText(objBean.getOrganSeg());
        tv_frg_preview_info_time.setText(objBean.getGetTime());
        tv_frg_preview_info_open_pwd.setText(objBean.getOpenPsd());

        tv_frg_preview_info_from_city.setText(objBean.getFromCity());
        tv_frg_preview_info_to_hosp.setText(objBean.getToHospName());
        tv_frg_preview_info_way.setText(objBean.getTracfficType());
        tv_frg_preview_info_no.setText(objBean.getTracfficNumber());


        tv_frg_preview_info_opo_name.setText(objBean.getOpoName());
        tv_frg_preview_info_contact_name.setText(objBean.getOpoContactName());
        tv_frg_preview_info_contact_phone.setText(objBean.getOpoContactPhone());
    }

    private void initInfo() {
        ll_frg_preview_info_base_hide.setVisibility(View.VISIBLE);
        ll_frg_preview_info_organ_hide.setVisibility(View.VISIBLE);
        ll_frg_preview_info_opo_hide.setVisibility(View.VISIBLE);
        ll_frg_preview_info_transfer_hide.setVisibility(View.VISIBLE);

        iv_frg_preview_info_base.setImageResource((R.drawable.cloud_3xinxi_up));
        iv_frg_preview_info_organ.setImageResource((R.drawable.cloud_3xinxi_up));
        iv_frg_preview_info_opo.setImageResource((R.drawable.cloud_3xinxi_up));
        iv_frg_preview_info_transfer.setImageResource((R.drawable.cloud_3xinxi_up));

    }

    @Override
    public void onStart() {
        super.onStart();

        if (mContactAddAdapter != null) {
            initYun();
            mObjBean.addAll(CONSTS.CONTACT_LIST);
            tv_frg_preview_info_number.setText(mObjBean.size() + "人");
            mContactAddAdapter.refresh(mObjBean, objBean.getOrganSeg(), tv_long_tip);
        }

    }

    private void longClickFlag() {
        //ToastUtil.showToast(phone+","+mObjBean.get(1).getContactPhone(),getActivity());
        if (phone.equals(mObjBean.get(1).getContactPhone())) {
            tv_long_tip.setVisibility(View.VISIBLE);
            tv_long_tip.setText("长按角色选择岗位角色");
        } else {
            tv_long_tip.setVisibility(View.VISIBLE);
            tv_long_tip.setText("科室协调员可长按头像进行岗位分配");
        }
    }

    /**
     * 加载联系人
     */
    private void loadContact(final String organSeg) {

        RequestParams params = new RequestParams(URL.RONG);
        params.addBodyParameter("action", "getContact");
        params.addBodyParameter("organSeg", organSeg);
        params.addBodyParameter("phone", phone);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e(TAG, "BBBBB:" + result);
                ContactListJson contactPersonJson = new Gson().fromJson(result, ContactListJson.class);
                dismissDialog();
                if (contactPersonJson != null && contactPersonJson.getResult() == CONSTS.SEND_OK) {

                    if (contactPersonJson.getObj() != null && contactPersonJson.getObj().size() > 0) {

                        List<ContactListJson.ObjBean> pObjBean = contactPersonJson.getObj();
                        CONSTS.CONTACT_LIST = new ArrayList<>();

                        for (int i = 3; i < pObjBean.size(); i++) {
                            pObjBean.get(i).setContactPhone(pObjBean.get(i).getPhone());
                            CONSTS.CONTACT_LIST.add(pObjBean.get(i));
                            //LogUtil.e(TAG,pObjBean.get(i).toString());

                        }
                        LogUtil.e(TAG, "BBBBB:2" + CONSTS.CONTACT_LIST.size() + ",");
                        if (CONSTS.CONTACT_LIST.size() > 0) {
                            mObjBean.addAll(CONSTS.CONTACT_LIST);
                        }
                        tv_frg_preview_info_number.setText(mObjBean.size() + "人");
                        mContactAddAdapter.refresh(mObjBean, organSeg, tv_long_tip);

                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(TAG, "BBBBB:1" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    private void initYun() {
        mObjBean = new ArrayList<>();

        //转运人
        ContactListJson.ObjBean objBeanMain = new ContactListJson.ObjBean();
        objBeanMain.setWechatUrl(objBean.getTrueUrl());
        objBeanMain.setIsUploadPhoto("0");
        objBeanMain.setTrueName(objBean.getTrueName());
        objBeanMain.setContactPhone(objBean.getPhone());
        mObjBean.add(objBeanMain);

        //科室协调员

        ContactListJson.ObjBean objBeanDepartment = new ContactListJson.ObjBean();
        objBeanDepartment.setWechatUrl(objBean.getContactUrl());
        objBeanDepartment.setIsUploadPhoto("0");
        objBeanDepartment.setTrueName(objBean.getContactName());
        objBeanDepartment.setContactPhone(objBean.getContactPhone());
        mObjBean.add(objBeanDepartment);

        //}

        //OPO人员
        ContactListJson.ObjBean objBeanOpo = new ContactListJson.ObjBean();
        objBeanOpo.setContactPhone(objBean.getOpoContactPhone());
        objBeanOpo.setTrueName(objBean.getOpoContactName());

        if (objBean.getOpoContactUrl() != null && !"".equals(objBean.getOpoContactUrl())) {
            objBeanOpo.setWechatUrl(objBean.getOpoContactUrl());
            objBeanOpo.setIsUploadPhoto("0");
            LogUtil.e(TAG, "getOpoContactUrl:" + objBean.getOpoContactUrl());
        }
        mObjBean.add(objBeanOpo);

        //判断创建人是否在其
        if(!phone.equals(objBean.getPhone())&&!phone.equals(objBean.getContactPhone())&&!phone.equals(objBean.getOpoContactPhone())){
            String trueName = SharePreUtils.getString("trueName", "", getActivity());
            String flag = SharePreUtils.getString("flag", "", getActivity());
            String wechatUrl = SharePreUtils.getString("wechatUrl", "", getActivity());
            String photoFile = SharePreUtils.getString("photoFile", "", getActivity());

            ContactListJson.ObjBean objBeanCreate = new ContactListJson.ObjBean();
            objBeanCreate.setWechatUrl(wechatUrl);
            objBeanCreate.setPhotoFile(photoFile);
            objBeanCreate.setIsUploadPhoto(flag);
            objBeanCreate.setTrueName(trueName);
            objBeanCreate.setContactPhone(phone);
            mObjBean.add(objBeanCreate);

        }





    }

    private void recycler() {


        initYun();
        mLinearLayoutManager = new GridLayoutManager(getActivity(), 5);
        rv_frg_preview_info.setLayoutManager(mLinearLayoutManager);
        mContactAddAdapter = new ContactAddAdapter(mObjBean, getActivity());
        rv_frg_preview_info.setAdapter(mContactAddAdapter);

    }


    private void loadUserByPhone() {
        RequestParams params = new RequestParams(URL.USER);
        params.addBodyParameter("action", "personInfo");
        params.addBodyParameter("phone", phone);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PersonInfoJson personInfoJson = new Gson().fromJson(result, PersonInfoJson.class);
                if (personInfoJson != null && personInfoJson.getResult() == CONSTS.SEND_OK) {
                    if (personInfoJson.getObj() != null) {
                        usersId = personInfoJson.getObj().getUsersId();
                        if (mObjBean.size() > 0) {
                            mObjBean.get(0).setUsersId(usersId);
                            mUsersIds = usersId;
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
    public void initData() {
        textView.setText("预览");


        fromHospitalAddress = objBean.getFromCity();


        recycler();
        LogUtil.e(TAG, "size:" + mObjBean.size());
        previewData();

        if (mContactAddAdapter != null && CONSTS.CONTACT_LIST.size() > 0) {
            initYun();

            //是否与前面重复
            String phone = mObjBean.get(0).getContactPhone();
            String contactPhone = mObjBean.get(1).getContactPhone();
            String opoContactPhone = mObjBean.get(2).getContactPhone();

            for (int i = CONSTS.CONTACT_LIST.size() - 1; i >= 0; i--) {
                String existsPhone = CONSTS.CONTACT_LIST.get(i).getContactPhone();
                if (phone.equals(existsPhone) || contactPhone.equals(existsPhone) || opoContactPhone.equals(existsPhone)) {
                    CONSTS.CONTACT_LIST.remove(i);
                }
            }


            mObjBean.addAll(CONSTS.CONTACT_LIST);
            tv_frg_preview_info_number.setText(mObjBean.size() + "人");
            mContactAddAdapter.refresh(mObjBean, objBean.getOrganSeg(), tv_long_tip);


        }
        longClickFlag();
    }

    public static PreviewInfoFragment newIntance(ViewPager viewPage, TextView t, RelativeLayout titleView) {
        PreviewInfoFragment previewInfoFragment = new PreviewInfoFragment();
        mViewPager = viewPage;
        textView = t;
        mTitleView = titleView;
        return previewInfoFragment;
    }

    private void sendGroupMessage() {
        RequestParams params = new RequestParams(URL.RONG);
        params.addBodyParameter("action", "sendGroupMessage");
        params.addBodyParameter("phone", phone);
        params.addBodyParameter("organSeg", objBean.getOrganSeg());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

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

    /**
     * 通知云监控改变
     *
     * @param organSeg
     */
    private void noticeTransfer(String organSeg, String type) {
        RequestParams params = new RequestParams(URL.PUSH);
        params.addBodyParameter("action", "sendPushTransfer");
        params.addBodyParameter("organSeg", organSeg);
        params.addBodyParameter("type", type);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

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

    private void insertTransfer() { //成都市
        if (fromHospitalAddress == null || "".equals(fromHospitalAddress)) {
            fromHospitalAddress = objBean.getFromCity();
        }
        if (ContactInfoFragment.toHospitalAddress == null || "".equals(ContactInfoFragment.toHospitalAddress)) {
            ContactInfoFragment.toHospitalAddress = objBean.getToHospName();
        }
        String groupName = "待转运-" + fromHospitalAddress + "-" + objBean.getOrgan();
        MainActivity.endLocation = ContactInfoFragment.toHospitalAddress.split("市")[0];
        String usersIds = "";
        for (int i = 0; i < mObjBean.size(); i++) {
            usersIds += mObjBean.get(i).getContactPhone();
            LogUtil.e(TAG, mObjBean.get(i).getTrueName() + ":" + mObjBean.get(i).getUsersId());
            if (i != mObjBean.size() - 1) {//上海市
                usersIds += ",";
            }
        }
        objBean.setPhones(usersIds);


        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "create");
        params.addBodyParameter("phone", mObjBean.get(0).getContactPhone());
        params.addBodyParameter("organSeg", objBean.getOrganSeg());
        params.addBodyParameter("organ", objBean.getOrgan());
        params.addBodyParameter("organNum", objBean.getOrganNum());
        params.addBodyParameter("blood", objBean.getBlood());
        params.addBodyParameter("bloodNum", objBean.getBloodNum());
        params.addBodyParameter("sampleOrgan", objBean.getSampleOrgan());
        params.addBodyParameter("sampleOrganNum", objBean.getSampleOrganNum());
        params.addBodyParameter("contactName", mObjBean.get(1).getTrueName());
        params.addBodyParameter("contactPhone", mObjBean.get(1).getContactPhone());
        params.addBodyParameter("fromCity", objBean.getFromCity());
        params.addBodyParameter("getTime", objBean.getGetTime());
        params.addBodyParameter("openPsd", objBean.getOpenPsd());
        params.addBodyParameter("opoName", objBean.getOpoName());
        params.addBodyParameter("toHospName", objBean.getToHospName());
        params.addBodyParameter("trueName", mObjBean.get(0).getTrueName());
        params.addBodyParameter("tracfficType", objBean.getTracfficType());
        params.addBodyParameter("tracfficNumber", objBean.getTracfficNumber());
        params.addBodyParameter("distance", MainActivity.distance + "");
        params.addBodyParameter("groupName", groupName);
        params.addBodyParameter("usersIds", usersIds);
        params.addBodyParameter("toHosp", ContactInfoFragment.toHospitalAddress.split("市")[0]);
        params.addBodyParameter("opoContactName", mObjBean.get(2).getTrueName());
        params.addBodyParameter("opoContactPhone", mObjBean.get(2).getContactPhone());

        params.addBodyParameter("boxNo", objBean.getBoxNo());
        LogUtil.e(TAG, "boxNo1:" + objBean.getBoxNo());
        if (CONSTS.TRANSFER_STATUS == 1) {
            params.addBodyParameter("isStart", "0");
        } else {
            params.addBodyParameter("isStart", "0");
        }
        if (ContactInfoFragment.startLocation != null && ContactInfoFragment.startLocation.contains(",") && ContactInfoFragment.endLocation != null && ContactInfoFragment.endLocation.contains(",")) {

            params.addBodyParameter("startLong", ContactInfoFragment.startLocation.split(",")[0]);
            params.addBodyParameter("startLati", ContactInfoFragment.startLocation.split(",")[1]);
            params.addBodyParameter("endLong", ContactInfoFragment.endLocation.split(",")[0]);
            params.addBodyParameter("endLati", ContactInfoFragment.endLocation.split(",")[1]);

        } else {
            params.addBodyParameter("startLong", "0");
            params.addBodyParameter("startLati", "0");
            params.addBodyParameter("endLong", "0");
            params.addBodyParameter("endLati", "0");
        }


        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                LogUtil.e(TAG, "result:" + result);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    sendGroupMessage();
                    getActivity().finish();
                    CONSTS.IS_START = 0;
                    new NewMonitorPopup(getActivity()).showPopupWindow(MainActivity.tv_system_line1);
                    noticeTransfer(objBean.getOrganSeg(), "");

                    RongIM.getInstance().setConversationToTop(Conversation.ConversationType.GROUP, objBean.getOrganSeg(), true, new RongIMClient.ResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {

                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
                } else if (photoJson != null && photoJson.getResult() == CONSTS.SEND_FAIL) {

                    ToastUtil.showToast("器官段号重复", getActivity());

                } else if (photoJson != null && photoJson.getResult() == CONSTS.BAD_PARAM) {
                    ToastUtil.showToast("箱子已被使用", getActivity());
                }
                dismissDialog();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(TAG, "ex:" + ex.getMessage());
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

    /**
     * 删除转运
     */
    private void deleteTransfer() {
        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "deleteTransfer");
        params.addBodyParameter("phone", SharePreUtils.getString("phone", "", mContext));
        params.addBodyParameter("organSeg", objBean.getOldOrganSeg());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e(TAG, "delete:" + result);
                insertTransfer();

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(TAG, "ex:" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void modifyTransfer(String organSeg, String modifyOrganSeg) { //成都市
        if (fromHospitalAddress == null || "".equals(fromHospitalAddress)) {
            fromHospitalAddress = objBean.getFromCity();
        }
        if (ContactInfoFragment.toHospitalAddress == null || "".equals(ContactInfoFragment.toHospitalAddress)) {
            ContactInfoFragment.toHospitalAddress = objBean.getToHospName();
        }
        String groupName = "转运中-" + fromHospitalAddress + "-" + ContactInfoFragment.toHospitalAddress.split("市")[0] + "-" + objBean.getOrgan() + "转运组";
        MainActivity.endLocation = ContactInfoFragment.toHospitalAddress.split("市")[0];
        String usersIds = "";
        for (int i = 0; i < mObjBean.size(); i++) {

            usersIds += mObjBean.get(i).getContactPhone();
            //LogUtil.e(TAG, mObjBean.get(i).getTrueName() + "::::" + mObjBean.get(i).getContactPhone()+","+mObjBean.size()+","+us);
            if (i != mObjBean.size() - 1) {//上海市
                usersIds += ",";
            }
        }
        objBean.setPhones(usersIds);


        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "updateTransfer");
        params.addBodyParameter("phone", mObjBean.get(0).getContactPhone());
        params.addBodyParameter("organSeg", organSeg);
        params.addBodyParameter("organ", objBean.getOrgan());
        params.addBodyParameter("organNum", objBean.getOrganNum());
        params.addBodyParameter("blood", objBean.getBlood());
        params.addBodyParameter("bloodNum", objBean.getBloodNum());
        params.addBodyParameter("sampleOrgan", objBean.getSampleOrgan());
        params.addBodyParameter("sampleOrganNum", objBean.getSampleOrganNum());
        params.addBodyParameter("contactName", mObjBean.get(1).getTrueName());
        params.addBodyParameter("contactPhone", mObjBean.get(1).getContactPhone());
        params.addBodyParameter("fromCity", objBean.getFromCity());
        params.addBodyParameter("getTime", objBean.getGetTime());
        params.addBodyParameter("openPsd", objBean.getOpenPsd());
        params.addBodyParameter("opoName", objBean.getOpoName());
        params.addBodyParameter("toHospName", objBean.getToHospName());
        params.addBodyParameter("trueName", mObjBean.get(0).getTrueName());
        params.addBodyParameter("tracfficType", objBean.getTracfficType());
        params.addBodyParameter("tracfficNumber", objBean.getTracfficNumber());
        params.addBodyParameter("usersIds", usersIds);
        params.addBodyParameter("distance", MainActivity.distance + "");
        params.addBodyParameter("toHosp", ContactInfoFragment.toHospitalAddress.split("市")[0]);
        params.addBodyParameter("opoContactName", mObjBean.get(2).getTrueName());
        params.addBodyParameter("opoContactPhone", mObjBean.get(2).getContactPhone());
        params.addBodyParameter("modifyOrganSeg", modifyOrganSeg);
        params.addBodyParameter("autoTransfer", "1");

        if (objBean.getAutoTransfer() == CONSTS.AURO_TRANSFER_FINISH_NO) {
            params.addBodyParameter("isStart", "1");
        } else {
            params.addBodyParameter("isStart", "0");
        }

        if (ContactInfoFragment.startLocation != null && ContactInfoFragment.startLocation.contains(",") && ContactInfoFragment.endLocation != null && ContactInfoFragment.endLocation.contains(",")) {

            params.addBodyParameter("startLong", ContactInfoFragment.startLocation.split(",")[0]);
            params.addBodyParameter("startLati", ContactInfoFragment.startLocation.split(",")[1]);
            params.addBodyParameter("endLong", ContactInfoFragment.endLocation.split(",")[0]);
            params.addBodyParameter("endLati", ContactInfoFragment.endLocation.split(",")[1]);

        } else {
            params.addBodyParameter("startLong", "0");

            params.addBodyParameter("startLati", "0");
            params.addBodyParameter("endLong", "0");
            params.addBodyParameter("endLati", "0");
        }


        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                LogUtil.e(TAG, "result:" + result);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {

                    sendGroupMessageStart();
                    getActivity().finish();
                    CONSTS.IS_START = 1;
                    if (objBean.getAutoTransfer() == CONSTS.AURO_TRANSFER_FINISH_NO) {
                        String content = "本次转运医师：" + objBean.getTrueName() + "，科室协调员：" + NewMonitorBaseFragment.objBean.getContactName() + "。器官段号：" + NewMonitorBaseFragment.objBean.getOrganSeg() + "，" + NewMonitorBaseFragment.objBean.getFromCity() + "的" + NewMonitorBaseFragment.objBean.getOrgan() + "转运已开始。";
                        sendListTransferSms(NewMonitorBaseFragment.objBean.getPhones(), content);
                        noticeTransfer(objBean.getOrganSeg(), "");
                    } else {

                        new NewMonitorPopup(getActivity()).showPopupWindow(MainActivity.tv_system_line1);
                    }
                } else {

                    ToastUtil.showToast("修改失败", getActivity());

                }
                dismissDialog();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(TAG, "ex:" + ex.getMessage());
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

    private void sendListTransferSms(String phones, String content) {

        RequestParams params = new RequestParams(URL.SMS);
        params.addBodyParameter("action", "sendListTransfer");
        params.addBodyParameter("phones", phones);
        params.addBodyParameter("content", content);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

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

    private void sendGroupMessageStart() {
        RequestParams params = new RequestParams(URL.RONG);
        params.addBodyParameter("action", "sendGroupMessageStart");
        params.addBodyParameter("phone", SharePreUtils.getString("phone", "", mContext));
        params.addBodyParameter("organSeg", NewMonitorBaseFragment.objBean.getOrganSeg());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_frg_contact_info_opo:


                break;
            case R.id.tv_frg_preview_info_pre:
                mViewPager.setCurrentItem(3);
                break;
            case R.id.tv_frg_preview_info_next:
                // CONSTS.CONTACT_LIST = new ArrayList<>();
                // CONSTS.CONTACT_POSITION = new ArrayList<>();
                //getActivity().finish();
                //new LoadingPopup(getActivity()).showPopupWindow(MainActivity.tv_system_line);

                if (ContactInfoFragment.startLocation != null && ContactInfoFragment.startLocation.contains(",") && ContactInfoFragment.endLocation != null && ContactInfoFragment.endLocation.contains(",")) {

                    MainActivity.distance = LocationUtils.getDistance(Double.parseDouble(ContactInfoFragment.startLocation.split(",")[1]), Double.parseDouble(ContactInfoFragment.startLocation.split(",")[0]), Double.parseDouble(ContactInfoFragment.endLocation.split(",")[1]), Double.parseDouble(ContactInfoFragment.endLocation.split(",")[0]));
                }
                //新建
                if (CONSTS.TRANSFER_STATUS == 0) {
                    showWaitDialog(getResources().getString(R.string.loading), false, "load");
                    insertTransfer();
                }
                //修改
                else {

                    if (objBean.getOldOrganSeg().equals(objBean.getOrganSeg())) {
                        showWaitDialog(getResources().getString(R.string.loading), true, "load");
                        modifyTransfer(objBean.getOldOrganSeg(), "");
                        //LogUtil.e(TAG,objBean.getOldOrganSeg()+","+objBean.getOrganSeg());
                        //ToastUtil.showToast("gg1",getActivity());
                    } else {

                        modifyTransfer(objBean.getOldOrganSeg(), objBean.getOrganSeg());
                        mAlertDialog = new AlertDialog.Builder(mContext);
                        //mAlertDialog.setTitle("tis");

                        mAlertDialog.setMessage("您的器官段号已修改,群组将重新建立.");


                        mAlertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        mAlertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showWaitDialog(getResources().getString(R.string.loading), false, "load");
                                deleteTransfer();
                                //通知转运监控
                                noticeTransfer(objBean.getOldOrganSeg(), "delete");
                                //移除会话列表
                                RongIM.getInstance().removeConversation(Conversation.ConversationType.GROUP, objBean.getOldOrganSeg(), new RongIMClient.ResultCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean aBoolean) {

                                    }

                                    @Override
                                    public void onError(RongIMClient.ErrorCode errorCode) {

                                    }
                                });

                            }
                        });
                        //mAlertDialog.show();


                    }

                }


                break;
            case R.id.ll_frg_preview_info_team:

                Intent intent = new Intent(getActivity(), ContactPersonActivity.class);
                intent.putExtra("type", "contactList");
                intent.putExtra("contactList", (Serializable) mObjBean);
                startActivity(intent);
                break;
            case R.id.ll_frg_preview_info_base:
                if (ll_frg_preview_info_base_hide.getVisibility() == View.VISIBLE) {
                    ll_frg_preview_info_base_hide.setVisibility(View.GONE);
                    iv_frg_preview_info_base.setImageResource((R.drawable.cloud_3xinxi_down));
                    tv_organ_space.setVisibility(View.GONE);
                } else {
                    ll_frg_preview_info_base_hide.setVisibility(View.VISIBLE);
                    iv_frg_preview_info_base.setImageResource((R.drawable.cloud_3xinxi_up));
                    tv_organ_space.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.ll_frg_preview_info_organ:
                if (ll_frg_preview_info_organ_hide.getVisibility() == View.VISIBLE) {
                    ll_frg_preview_info_organ_hide.setVisibility(View.GONE);
                    iv_frg_preview_info_organ.setImageResource((R.drawable.cloud_3xinxi_down));
                    tv_transfer_space.setVisibility(View.GONE);
                } else {
                    ll_frg_preview_info_organ_hide.setVisibility(View.VISIBLE);
                    iv_frg_preview_info_organ.setImageResource((R.drawable.cloud_3xinxi_up));
                    tv_transfer_space.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.ll_frg_preview_info_opo:
                if (ll_frg_preview_info_opo_hide.getVisibility() == View.VISIBLE) {
                    ll_frg_preview_info_opo_hide.setVisibility(View.GONE);
                    iv_frg_preview_info_opo.setImageResource((R.drawable.cloud_3xinxi_down));
                } else {
                    ll_frg_preview_info_opo_hide.setVisibility(View.VISIBLE);
                    iv_frg_preview_info_opo.setImageResource((R.drawable.cloud_3xinxi_up));
                }
                break;
            case R.id.ll_frg_preview_info_transfer:
                if (ll_frg_preview_info_transfer_hide.getVisibility() == View.VISIBLE) {
                    ll_frg_preview_info_transfer_hide.setVisibility(View.GONE);
                    iv_frg_preview_info_transfer.setImageResource((R.drawable.cloud_3xinxi_down));
                    tv_opo_space.setVisibility(View.GONE);
                } else {
                    ll_frg_preview_info_transfer_hide.setVisibility(View.VISIBLE);
                    iv_frg_preview_info_transfer.setImageResource((R.drawable.cloud_3xinxi_up));
                    tv_opo_space.setVisibility(View.VISIBLE);
                }
                break;


        }
    }

}
