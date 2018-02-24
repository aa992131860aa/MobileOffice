package com.mobileoffice.controller.cloud_monitor;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lljjcoder.citypickerview.widget.CityPicker;
import com.mobileoffice.R;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.controller.message.contact.ContactPersonActivity;
import com.mobileoffice.controller.message.contact.PinyinComparator;
import com.mobileoffice.controller.message.contact.PinyinUtils;
import com.mobileoffice.controller.new_monitor.ContactAddAdapter;
import com.mobileoffice.controller.new_monitor.ContactInfoFragment;
import com.mobileoffice.controller.new_monitor.HospitalChoseActivity;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.ContactListJson;
import com.mobileoffice.json.HospitalJson;
import com.mobileoffice.json.LatiLongJson;
import com.mobileoffice.json.PhotoJson;
import com.mobileoffice.json.TransferJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.LocationUtils;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;
import com.mobileoffice.view.WebLoginPopup;
import com.mobileoffice.view.com.bigkoo.pickerview.TimePickerView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by 99213 on 2017/7/18.
 */

public class PreviewInfoActivity extends BaseActivity implements View.OnClickListener, WebLoginPopup.OnClickChangeListener, ContactAddAdapter.OnRecyclerViewItemClickListener {

    private LinearLayout ll_back;
    private String TAG = "PreviewInfoActivity";

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
    private String phone;

    //添加成员
    private RecyclerView rv_frg_preview_info;

    //传过来的值
    TransferJson.ObjBean objBean;

    private ContactAddAdapter mContactAddAdapter;
    private List<ContactListJson.ObjBean> mObjBean;
    private GridLayoutManager mLinearLayoutManager;

    //横线15dp
    private TextView tv_info_line;
    private TextView tv_transfer_line;
    private TextView tv_opo_line;

    //标识
    private TextView tv_long_tip;

    //下载pdf报告
    private LinearLayout ll_pdf;

    //解散群组(转运人和协调员)
    private Button btn_dissolve;
    private AlertDialog.Builder mAlertDialog;

    public static int FLAG = 0;

    //转运设置
    private LinearLayout ll_site;

    private LinearLayout ll_modify;
    private Button btn_modify;

    //修改的字段 基本信息

    private EditText edt_organ_seg;
    private TextView tv_get_time;
    //器官信息
    private TextView tv_organ;
    private LinearLayout ll_organ_num;
    private ImageView iv_organ_num_minus;
    private TextView tv_organ_num;
    private ImageView iv_organ_num_plus;
    private TextView tv_blood;
    private LinearLayout ll_blood_num;
    private ImageView iv_blood_num_minus;
    private TextView tv_blood_num;
    private ImageView iv_blood_num_plus;
    private TextView tv_sample;
    private LinearLayout ll_sample_num;
    private ImageView iv_sample_num_minus;
    private TextView tv_sample_num;
    private ImageView iv_sample_num_plus;
    //转运信息
    private TextView tv_start_location;
    private TextView tv_end_location;
    private TextView tv_method;
    private EditText edt_no;
    //其他信息
    private TextView tv_opo;
    private TextView tv_opo_contact_name;

    private TextView tv_modify;
    //岗位角色
    private int mRoleId;

    //时间选择器
    private TimePickerView pvDate;

    //弹出
    private PreviewModifyPop mPreviewModifyPop;

    private String startLocation;
    private String endLocation;
    private String toHospitalAddress;
    private double distance;

    //弹出密码
    private WebLoginPopup mWebLoginPopup;

    public final static int PRE_TRANSFER = 2018;

    @Override
    protected void initVariable() {
        CONSTS.IS_HISTORY_MODIFY = false;
        mRoleId = SharePreUtils.getInt("roleId", -1, this);

        //科室协调员有权利修改信息
        // if (mRoleId == CONSTS.ROLE_OPO && objBean.getAutoTransfer() == CONSTS.AURO_TRANSFER_FINISH) {
        //     ll_modify.setVisibility(View.VISIBLE);
//        } else {
//            ll_modify.setVisibility(View.GONE);
//        }
        //完善信息
        if("modify".equals(getIntent().getStringExtra("type"))){
            tv_modify.setText("取消修改");
            modifyShow();
            loadStartLocation(objBean.getFromCity());
            loadHospitalAddress(objBean.getToHospName());
        }

        mObjBean = new ArrayList<>();
        showWaitDialog(getResources().getString(R.string.loading), false, "load");

        if (objBean != null) {
            loadContact(objBean.getOrganSeg());
            previewData();
            isExistGroup(objBean.getOrganSeg());
        }
        initInfo();

        initDatePicker();

    }

    private void modifyHide() {
        //修改的隐藏
        edt_organ_seg.setVisibility(View.GONE);
        tv_get_time.setVisibility(View.GONE);
        //器官信息
        tv_organ.setVisibility(View.GONE);
        ll_organ_num.setVisibility(View.GONE);
        iv_organ_num_minus.setVisibility(View.GONE);
        tv_organ_num.setVisibility(View.GONE);
        iv_organ_num_plus.setVisibility(View.GONE);
        tv_blood.setVisibility(View.GONE);
        ll_blood_num.setVisibility(View.GONE);
        iv_blood_num_minus.setVisibility(View.GONE);
        tv_blood_num.setVisibility(View.GONE);
        iv_blood_num_plus.setVisibility(View.GONE);
        tv_sample.setVisibility(View.GONE);
        ll_sample_num.setVisibility(View.GONE);
        iv_sample_num_minus.setVisibility(View.GONE);
        tv_sample_num.setVisibility(View.GONE);
        iv_sample_num_plus.setVisibility(View.GONE);
        //转运信息
        tv_start_location.setVisibility(View.GONE);
        tv_end_location.setVisibility(View.GONE);
        tv_method.setVisibility(View.GONE);
        edt_no.setVisibility(View.GONE);
        tv_opo_contact_name.setVisibility(View.GONE);
        //其他信息
        //tv_opo.setVisibility(View.GONE);


        //未修改的显示
        tv_frg_preview_info_organ_seg.setVisibility(View.VISIBLE);
        tv_frg_preview_info_time.setVisibility(View.VISIBLE);
        tv_frg_preview_info_organ.setVisibility(View.VISIBLE);
        tv_frg_preview_info_organ_number.setVisibility(View.VISIBLE);
        tv_frg_preview_info_blood.setVisibility(View.VISIBLE);
        tv_frg_preview_info_blood_number.setVisibility(View.VISIBLE);
        tv_frg_preview_info_sample_organ.setVisibility(View.VISIBLE);
        tv_frg_preview_info_sample_organ_number.setVisibility(View.VISIBLE);
        tv_frg_preview_info_from_city.setVisibility(View.VISIBLE);
        tv_frg_preview_info_to_hosp.setVisibility(View.VISIBLE);
        tv_frg_preview_info_way.setVisibility(View.VISIBLE);
        tv_frg_preview_info_no.setVisibility(View.VISIBLE);
        tv_frg_preview_info_contact_name.setVisibility(View.VISIBLE);
        //tv_frg_preview_info_opo_name.setVisibility(View.VISIBLE);

        btn_modify.setVisibility(View.GONE);


    }

    private void modifyShow() {
        //修改的显示
        edt_organ_seg.setVisibility(View.VISIBLE);
        tv_get_time.setVisibility(View.VISIBLE);
        //器官信息
        tv_organ.setVisibility(View.VISIBLE);
        ll_organ_num.setVisibility(View.VISIBLE);
        iv_organ_num_minus.setVisibility(View.VISIBLE);
        tv_organ_num.setVisibility(View.VISIBLE);
        iv_organ_num_plus.setVisibility(View.VISIBLE);
        tv_blood.setVisibility(View.VISIBLE);
        ll_blood_num.setVisibility(View.VISIBLE);
        iv_blood_num_minus.setVisibility(View.VISIBLE);
        tv_blood_num.setVisibility(View.VISIBLE);
        iv_blood_num_plus.setVisibility(View.VISIBLE);
        tv_sample.setVisibility(View.VISIBLE);
        ll_sample_num.setVisibility(View.VISIBLE);
        iv_sample_num_minus.setVisibility(View.VISIBLE);
        tv_sample_num.setVisibility(View.VISIBLE);
        iv_sample_num_plus.setVisibility(View.VISIBLE);
        //转运信息
        tv_start_location.setVisibility(View.VISIBLE);
        tv_end_location.setVisibility(View.VISIBLE);
        tv_method.setVisibility(View.VISIBLE);
        edt_no.setVisibility(View.VISIBLE);

        //其他信息
        //tv_opo.setVisibility(View.VISIBLE);
        tv_opo_contact_name.setVisibility(View.VISIBLE);

        //未修改的隐藏
        tv_frg_preview_info_organ_seg.setVisibility(View.GONE);
        tv_frg_preview_info_time.setVisibility(View.GONE);
        tv_frg_preview_info_organ.setVisibility(View.GONE);
        tv_frg_preview_info_organ_number.setVisibility(View.GONE);
        tv_frg_preview_info_blood.setVisibility(View.GONE);
        tv_frg_preview_info_blood_number.setVisibility(View.GONE);
        tv_frg_preview_info_sample_organ.setVisibility(View.GONE);
        tv_frg_preview_info_sample_organ_number.setVisibility(View.GONE);
        tv_frg_preview_info_from_city.setVisibility(View.GONE);
        tv_frg_preview_info_to_hosp.setVisibility(View.GONE);
        tv_frg_preview_info_way.setVisibility(View.GONE);
        tv_frg_preview_info_no.setVisibility(View.GONE);
        //tv_frg_preview_info_opo_name.setVisibility(View.GONE);

        tv_frg_preview_info_contact_name.setVisibility(View.GONE);
        btn_modify.setVisibility(View.VISIBLE);
    }

    private void recycler() {

        mObjBean = new ArrayList<>();
        mLinearLayoutManager = new GridLayoutManager(this, 5);
        rv_frg_preview_info.setLayoutManager(mLinearLayoutManager);
        mContactAddAdapter = new ContactAddAdapter(mObjBean, this, "type");
        mContactAddAdapter.setOnItemClickListener(this);
        rv_frg_preview_info.setAdapter(mContactAddAdapter);

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
    protected void onStart() {
        super.onStart();
        if (FLAG == 0 && objBean.getAutoTransfer() == CONSTS.AURO_TRANSFER_FINISH) {
            if (mObjBean != null && mObjBean.size() > 1) {
                initYun();
            }

            mObjBean.addAll(CONSTS.CONTACT_LIST);

            tv_frg_preview_info_number.setText(mObjBean.size() + "人");
            mContactAddAdapter.refresh(mObjBean, objBean.getOrganSeg(), tv_long_tip, objBean.getAutoTransfer());
        }

        //String hospitalName = SharePreUtils.getString("hospital", "", this);
    }

    private void initYun() {

        //转运人
        mObjBean.get(0).setContactPhone(mObjBean.get(0).getPhone());
        ContactListJson.ObjBean objBeanYun = mObjBean.get(0);
        //科室协调员
        mObjBean.get(1).setContactPhone(mObjBean.get(1).getPhone());
        ContactListJson.ObjBean objBeanXie = mObjBean.get(1);
        //OPO人员
        mObjBean.get(2).setContactPhone(mObjBean.get(2).getPhone());
        ContactListJson.ObjBean objBeanOpo = mObjBean.get(2);

        mObjBean = new ArrayList<>();

        mObjBean.add(objBeanYun);
        mObjBean.add(objBeanXie);
        mObjBean.add(objBeanOpo);
        longClickFlag();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        FLAG = 0;
        setContentView(R.layout.preview_info);
        CONSTS.CONTACT_LIST = new ArrayList<>();
        CONSTS.CONTACT_LIST_PREVIEW = new ArrayList<>();
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        //判断是否修改,修改了用静态变量,没修过用传递的值
        if(CONSTS.MODIFY_TRANSFER==null) {
            objBean = (TransferJson.ObjBean) getIntent().getSerializableExtra("preview");
        }else{
            objBean = CONSTS.MODIFY_TRANSFER;
        }
        tv_long_tip = (TextView) findViewById(R.id.tv_long_tip);
        rv_frg_preview_info = (RecyclerView) findViewById(R.id.rv_frg_preview_info);
        tv_frg_preview_info_number = (TextView) findViewById(R.id.tv_frg_preview_info_number);
        ll_frg_preview_info_team = (LinearLayout) findViewById(R.id.ll_frg_preview_info_team);

        ll_frg_preview_info_base = (LinearLayout) findViewById(R.id.ll_frg_preview_info_base);
        iv_frg_preview_info_base = (ImageView) findViewById(R.id.iv_frg_preview_info_base);

        ll_frg_preview_info_base_hide = (LinearLayout) findViewById(R.id.ll_frg_preview_info_base_hide);
        tv_frg_preview_info_organ = (TextView) findViewById(R.id.tv_frg_preview_info_organ);
        tv_frg_preview_info_organ_number = (TextView) findViewById(R.id.tv_frg_preview_info_organ_number);
        tv_frg_preview_info_blood = (TextView) findViewById(R.id.tv_frg_preview_info_blood);
        tv_frg_preview_info_blood_number = (TextView) findViewById(R.id.tv_frg_preview_info_blood_number);
        tv_frg_preview_info_sample_organ = (TextView) findViewById(R.id.tv_frg_preview_info_sample_organ);
        tv_frg_preview_info_sample_organ_number = (TextView) findViewById(R.id.tv_frg_preview_info_sample_organ_number);

        ll_frg_preview_info_organ = (LinearLayout) findViewById(R.id.ll_frg_preview_info_organ);
        ll_frg_preview_info_organ_hide = (LinearLayout) findViewById(R.id.ll_frg_preview_info_organ_hide);
        iv_frg_preview_info_organ = (ImageView) findViewById(R.id.iv_frg_preview_info_organ);
        tv_frg_preview_info_organ_seg = (TextView) findViewById(R.id.tv_frg_preview_info_organ_seg);
        tv_frg_preview_info_time = (TextView) findViewById(R.id.tv_frg_preview_info_time);
        tv_frg_preview_info_open_pwd = (TextView) findViewById(R.id.tv_frg_preview_info_open_pwd);

        ll_frg_preview_info_transfer = (LinearLayout) findViewById(R.id.ll_frg_preview_info_transfer);
        ll_frg_preview_info_transfer_hide = (LinearLayout) findViewById(R.id.ll_frg_preview_info_transfer_hide);
        iv_frg_preview_info_transfer = (ImageView) findViewById(R.id.iv_frg_preview_info_transfer);
        tv_frg_preview_info_from_city = (TextView) findViewById(R.id.tv_frg_preview_info_from_city);
        tv_frg_preview_info_to_hosp = (TextView) findViewById(R.id.tv_frg_preview_info_to_hosp);
        tv_frg_preview_info_way = (TextView) findViewById(R.id.tv_frg_preview_info_way);
        tv_frg_preview_info_no = (TextView) findViewById(R.id.tv_frg_preview_info_no);

        ll_frg_preview_info_opo = (LinearLayout) findViewById(R.id.ll_frg_preview_info_opo);
        ll_frg_preview_info_opo_hide = (LinearLayout) findViewById(R.id.ll_frg_preview_info_opo_hide);
        iv_frg_preview_info_opo = (ImageView) findViewById(R.id.iv_frg_preview_info_opo);
        tv_frg_preview_info_opo_name = (TextView) findViewById(R.id.tv_frg_preview_info_opo_name);
        tv_frg_preview_info_contact_name = (TextView) findViewById(R.id.tv_frg_preview_info_contact_name);
        tv_frg_preview_info_contact_phone = (TextView) findViewById(R.id.tv_frg_preview_info_contact_phone);

        tv_frg_preview_info_box_no = (TextView) findViewById(R.id.tv_frg_preview_info_box_no);
        tv_info_line = (TextView) findViewById(R.id.tv_info_line);
        tv_transfer_line = (TextView) findViewById(R.id.tv_transfer_line);
        tv_opo_line = (TextView) findViewById(R.id.tv_opo_line);

        ll_pdf = (LinearLayout) findViewById(R.id.ll_pdf);
        btn_dissolve = (Button) findViewById(R.id.btn_dissolve);

        ll_site = (LinearLayout) findViewById(R.id.ll_site);


        ll_modify = (LinearLayout) findViewById(R.id.ll_modify);
        btn_modify = (Button) findViewById(R.id.btn_modify);

        //修改的字段 基本信息

        edt_organ_seg = (EditText) findViewById(R.id.edt_organ_seg);
        tv_get_time = (TextView) findViewById(R.id.tv_get_time);
        //器官信息
        tv_organ = (TextView) findViewById(R.id.tv_organ);
        ll_organ_num = (LinearLayout) findViewById(R.id.ll_organ_num);
        iv_organ_num_minus = (ImageView) findViewById(R.id.iv_organ_num_minus);
        tv_organ_num = (TextView) findViewById(R.id.tv_organ_num);
        iv_organ_num_plus = (ImageView) findViewById(R.id.iv_organ_num_plus);
        tv_blood = (TextView) findViewById(R.id.tv_blood);
        ll_blood_num = (LinearLayout) findViewById(R.id.ll_blood_num);
        iv_blood_num_minus = (ImageView) findViewById(R.id.iv_blood_num_minus);
        tv_blood_num = (TextView) findViewById(R.id.tv_blood_num);
        iv_blood_num_plus = (ImageView) findViewById(R.id.iv_blood_num_plus);
        tv_sample = (TextView) findViewById(R.id.tv_sample);
        ll_sample_num = (LinearLayout) findViewById(R.id.ll_sample_num);
        iv_sample_num_minus = (ImageView) findViewById(R.id.iv_sample_num_minus);
        tv_sample_num = (TextView) findViewById(R.id.tv_sample_num);
        iv_sample_num_plus = (ImageView) findViewById(R.id.iv_sample_num_plus);
        //转运信息
        tv_start_location = (TextView) findViewById(R.id.tv_start_location);
        tv_end_location = (TextView) findViewById(R.id.tv_end_location);
        tv_method = (TextView) findViewById(R.id.tv_method);
        edt_no = (EditText) findViewById(R.id.edt_no);
        //其他信息
        tv_opo = (TextView) findViewById(R.id.tv_opo);
        tv_opo_contact_name = (TextView) findViewById(R.id.tv_opo_contact_name);
        tv_modify = (TextView) findViewById(R.id.tv_modify);


        ll_pdf.setOnClickListener(this);
        btn_dissolve.setOnClickListener(this);
        ll_frg_preview_info_base.setOnClickListener(this);
        ll_frg_preview_info_organ.setOnClickListener(this);
        ll_frg_preview_info_transfer.setOnClickListener(this);
        ll_frg_preview_info_opo.setOnClickListener(this);
        ll_frg_preview_info_base.setOnClickListener(this);
        ll_frg_preview_info_organ.setOnClickListener(this);
        ll_frg_preview_info_opo.setOnClickListener(this);
        ll_frg_preview_info_transfer.setOnClickListener(this);

        ll_site.setOnClickListener(this);
        ll_frg_preview_info_team.setOnClickListener(this);

        ll_modify.setOnClickListener(this);
        btn_modify.setOnClickListener(this);


        tv_get_time.setOnClickListener(this);
        //器官信息
        tv_organ.setOnClickListener(this);

        iv_organ_num_minus.setOnClickListener(this);
        tv_organ_num.setOnClickListener(this);
        iv_organ_num_plus.setOnClickListener(this);
        tv_blood.setOnClickListener(this);

        iv_blood_num_minus.setOnClickListener(this);
        tv_blood_num.setOnClickListener(this);
        iv_blood_num_plus.setOnClickListener(this);
        tv_sample.setOnClickListener(this);

        iv_sample_num_minus.setOnClickListener(this);
        tv_sample_num.setOnClickListener(this);
        iv_sample_num_plus.setOnClickListener(this);
        //转运信息
        tv_start_location.setOnClickListener(this);
        tv_end_location.setOnClickListener(this);
        tv_opo_contact_name.setOnClickListener(this);
        tv_method.setOnClickListener(this);


        phone = SharePreUtils.getString("phone", "", this);

        recycler();

    }

    private void previewData() {
        if (!"".equals(objBean.getModifyOrganSeg())) {

            tv_frg_preview_info_organ_seg.setText(objBean.getModifyOrganSeg());
            edt_organ_seg.setText(objBean.getModifyOrganSeg());
            edt_organ_seg.setSelection(objBean.getModifyOrganSeg().length());
        } else {
            tv_frg_preview_info_organ_seg.setText(objBean.getOrganSeg());
            edt_organ_seg.setText(objBean.getOrganSeg());
            edt_organ_seg.setSelection(objBean.getOrganSeg().length());
        }
        if(edt_organ_seg.getText().toString().contains("AP")) {
            edt_organ_seg.setBackgroundResource(R.drawable.edit_border_high);
        }else{
            edt_organ_seg.setBackgroundResource(R.drawable.edit_border_white_gray);
        }

        tv_frg_preview_info_organ.setText(objBean.getOrgan());
        tv_organ.setText(objBean.getOrgan());

        if("".equals(tv_organ.getText().toString())) {
            tv_organ.setBackgroundResource(R.drawable.edit_border_high);
        }else{
            tv_organ.setBackgroundResource(R.drawable.edit_border_white_gray);
        }

        tv_frg_preview_info_organ_number.setText(objBean.getOrganNum());
        tv_organ_num.setText(objBean.getOrganNum());
        tv_frg_preview_info_blood.setText(objBean.getBlood());
        tv_blood.setText(objBean.getBlood());

        if("".equals(tv_blood.getText().toString())) {
            tv_blood.setBackgroundResource(R.drawable.edit_border_high);
        }else{
            tv_blood.setBackgroundResource(R.drawable.edit_border_white_gray);
        }

        tv_frg_preview_info_blood_number.setText(objBean.getBloodNum());
        tv_blood_num.setText(objBean.getBloodNum());
        tv_frg_preview_info_sample_organ.setText(objBean.getSampleOrgan());
        tv_sample.setText(objBean.getSampleOrgan());

        if("".equals(tv_sample.getText().toString())) {
            tv_sample.setBackgroundResource(R.drawable.edit_border_high);
        }else{
            tv_sample.setBackgroundResource(R.drawable.edit_border_white_gray);
        }

        tv_frg_preview_info_sample_organ_number.setText(objBean.getSampleOrganNum());
        tv_sample_num.setText(objBean.getSampleOrganNum());


        tv_frg_preview_info_time.setText(objBean.getGetTime());
        tv_get_time.setText(objBean.getGetTime());

        if("".equals(tv_get_time.getText().toString())) {
            tv_get_time.setBackgroundResource(R.drawable.edit_border_high);
        }else{
            tv_get_time.setBackgroundResource(R.drawable.edit_border_white_gray);
        }

        tv_frg_preview_info_open_pwd.setText(objBean.getOpenPsd());

        tv_frg_preview_info_from_city.setText(objBean.getFromCity());
        tv_start_location.setText(objBean.getFromCity());

        if("".equals(tv_start_location.getText().toString())) {
            tv_start_location.setBackgroundResource(R.drawable.edit_border_high);
        }else{
            tv_start_location.setBackgroundResource(R.drawable.edit_border_white_gray);
        }

        tv_frg_preview_info_to_hosp.setText(objBean.getToHospName());
        tv_end_location.setText(objBean.getToHospName());

        if("".equals(tv_end_location.getText().toString())) {
            tv_end_location.setBackgroundResource(R.drawable.edit_border_high);
        }else{
            tv_end_location.setBackgroundResource(R.drawable.edit_border_white_gray);
        }

        tv_frg_preview_info_way.setText(objBean.getTracfficType());
        tv_method.setText(objBean.getTracfficType());

        if("".equals(tv_method.getText().toString())) {
            tv_method.setBackgroundResource(R.drawable.edit_border_high);
        }else{
            tv_method.setBackgroundResource(R.drawable.edit_border_white_gray);
        }

        tv_frg_preview_info_no.setText(objBean.getTracfficNumber());
        edt_no.setText(objBean.getTracfficNumber());

        if("".equals(edt_no.getText().toString())) {
            edt_no.setBackgroundResource(R.drawable.edit_border_high);
        }else{
            edt_no.setBackgroundResource(R.drawable.edit_border_white_gray);
        }

        tv_frg_preview_info_opo_name.setText(objBean.getOpoName());
        tv_frg_preview_info_contact_name.setText(objBean.getOpoContactName());
        tv_frg_preview_info_contact_phone.setText(objBean.getOpoContactPhone());
        tv_frg_preview_info_box_no.setText(objBean.getBoxNo());

        tv_opo_contact_name.setText(objBean.getOpoContactName());

        if("".equals(tv_opo_contact_name.getText().toString())) {
            tv_opo_contact_name.setBackgroundResource(R.drawable.edit_border_high);
        }else{
            tv_opo_contact_name.setBackgroundResource(R.drawable.edit_border_white_gray);
        }


    }

    private void downloadPdf(final String organSeg, final String organ) {

        showWaitDialog(getResources().getString(R.string.loading), false, "loading");
        final String path = getFilesDir() + "/pdf/" + organSeg + ".pdf";
        RequestParams requestParams = new RequestParams(URL.DOWNLOAD_PDF);
        requestParams.addBodyParameter("action", "pdf");
        requestParams.addBodyParameter("organSeg", organSeg);
        requestParams.addBodyParameter("phone", phone);
        requestParams.addBodyParameter("organ", organ);
        requestParams.setSaveFilePath(path);

        x.http().get(requestParams, new Callback.ProgressCallback<File>() {
            @Override
            public void onWaiting() {
            }

            @Override
            public void onStarted() {
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {

            }

            @Override
            public void onSuccess(File result) {
                dismissDialog();
                Intent intent = new Intent(PreviewInfoActivity.this, PdfActivity.class);
                intent.putExtra("path", result.getAbsolutePath());
                intent.putExtra("organSeg", organSeg);
                FLAG = 1;
                startActivity(intent);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                dismissDialog();
                //ToastUtil.showToast("" + ex.getMessage(), PreviewInfoActivity.this);
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

    private List<ContactListJson.ObjBean> getData(List<ContactListJson.ObjBean> objBeens) {

        List<ContactListJson.ObjBean> listarray = new ArrayList<>();
        for (int i = 0; i < objBeens.size(); i++) {

            String pinyin = PinyinUtils.getPingYin(objBeens.get(i).getTrueName());
            String Fpinyin = pinyin.substring(0, 1).toUpperCase();

            ContactListJson.ObjBean person = new ContactListJson.ObjBean();
            person.setTrueName(objBeens.get(i).getTrueName());
            person.setIsUploadPhoto(objBeens.get(i).getIsUploadPhoto());
            person.setPhotoFile(objBeens.get(i).getPhotoFile());
            person.setWechatUrl(objBeens.get(i).getWechatUrl());
            person.setPinYin(pinyin);
            person.setContactPhone(objBeens.get(i).getContactPhone());
            person.setUsersId(objBeens.get(i).getUsersId());
            person.setName(objBeens.get(i).getName());
            person.setPostRole(objBeens.get(i).getPostRole());
            LogUtil.e(TAG, "trueNamePin:" + objBeens.get(i).getTrueName()
                    + ",getIsUploadPhoto:" + person.getIsUploadPhoto() + ",getPhotoFile:" + person.getPhotoFile() + ",getWechatUrl:" + person.getWechatUrl() + ",getPinYin:" + person.getPinYin() + ",getContactPhone:" + person.getContactPhone() + ",getUsersId:" + person.getUsersId());
            // 正则表达式，判断首字母是否是英文字母
            if (Fpinyin.matches("[A-Z]")) {
                person.setFirstPinYin(Fpinyin);
            } else {
                person.setFirstPinYin("#");
            }

            listarray.add(person);
        }
        return listarray;

    }

    private void longClickFlag() {
        if (phone.equals(mObjBean.get(1).getPhone())) {
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
                LogUtil.e(TAG, result);
                ContactListJson contactPersonJson = new Gson().fromJson(result, ContactListJson.class);
                dismissDialog();
                if (contactPersonJson != null && contactPersonJson.getResult() == CONSTS.SEND_OK) {

                    if (contactPersonJson.getObj() != null && contactPersonJson.getObj().size() > 0) {
                        mObjBean = contactPersonJson.getObj();
                        if (mObjBean.size() > 2) {

                            for (int i = 0; i < 3; i++) {
                                mObjBean.get(i).setContactPhone(mObjBean.get(i).getPhone());
                                CONSTS.CONTACT_LIST_PREVIEW.add(mObjBean.get(i));
                            }
                            for (int i = 3; i < mObjBean.size(); i++) {
                                mObjBean.get(i).setContactPhone(mObjBean.get(i).getPhone());
                                CONSTS.CONTACT_LIST.add(mObjBean.get(i));
                                LogUtil.e(TAG, "contactName:" + mObjBean.get(i).getTrueName());
                            }
                            if (phone.equals(mObjBean.get(1).getPhone())) {
                                tv_long_tip.setText("长按角色选择岗位角色");
                                tv_long_tip.setVisibility(View.VISIBLE);
                            } else {
                                tv_long_tip.setVisibility(View.VISIBLE);
                                tv_long_tip.setText("科室协调员可长按头像进行岗位分配");
                            }
                            longClickFlag();

                            CONSTS.CONTACT_LIST = (ArrayList<ContactListJson.ObjBean>) getData(CONSTS.CONTACT_LIST);
                            Collections.sort(CONSTS.CONTACT_LIST, new PinyinComparator());

                        } else {

                        }
                        tv_frg_preview_info_number.setText(mObjBean.size() + "人");
                        mContactAddAdapter.refresh(mObjBean, organSeg, tv_long_tip, objBean.getAutoTransfer());
                    }
                } else {

                }
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

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            //修改
            case R.id.ll_modify:
                String modifyName = tv_modify.getText().toString().trim();
                if ("修改基本信息".equals(modifyName)) {
                    mWebLoginPopup = new WebLoginPopup(this);
                    mWebLoginPopup.showPopupWindow(view);
                    mWebLoginPopup.setOnClickChangeListener(this);

                } else {
                    tv_modify.setText("修改基本信息");
                    modifyHide();
                }
                break;
            //获取时间
            case R.id.tv_get_time:
                pvDate.show(view);
                break;


            //器官类型
            case R.id.tv_organ:
                final List<String> organs = new ArrayList<>();
                organs.add("心脏");
                organs.add("肝脏");
                organs.add("肾脏");
                organs.add("肺");
                organs.add("胰脏");
                organs.add("眼角膜");

                mPreviewModifyPop = new PreviewModifyPop(this, organs, tv_organ.getWidth());
                mPreviewModifyPop.showAsDropDown(view);
                mPreviewModifyPop.setOnClickChangeListener(new PreviewModifyPop.OnClickChangeListener() {
                    @Override
                    public void OnClickChange(int position) {
                        tv_organ.setText(organs.get(position));
                        tv_frg_preview_info_organ.setText(organs.get(position));
                        objBean.setOrgan(organs.get(position));
                        mPreviewModifyPop.dismiss();
                    }
                });
                break;
            case R.id.iv_organ_num_minus:
                String numStr = tv_organ_num.getText().toString();
                int num = Integer.parseInt(numStr);
                if (num > 1) {
                    num--;
                    tv_organ_num.setText(num + "");
                    tv_frg_preview_info_organ_number.setText(num+"");
                    objBean.setOrganNum(num+"");
                }
                break;
            case R.id.iv_organ_num_plus:
                numStr = tv_organ_num.getText().toString();
                num = Integer.parseInt(numStr);
                if (num < 10) {
                    num++;
                    tv_organ_num.setText(num + "");
                    tv_frg_preview_info_organ_number.setText(num+"");
                    objBean.setOrganNum(num+"");
                }
                break;

            //血液类型
            case R.id.tv_blood:
                final List<String> bloods = new ArrayList<>();
                bloods.add("A");
                bloods.add("AB");
                bloods.add("B");
                bloods.add("O");


                mPreviewModifyPop = new PreviewModifyPop(this, bloods, tv_blood.getWidth());
                mPreviewModifyPop.showAsDropDown(view);
                mPreviewModifyPop.setOnClickChangeListener(new PreviewModifyPop.OnClickChangeListener() {
                    @Override
                    public void OnClickChange(int position) {
                        tv_blood.setText(bloods.get(position));
                        tv_frg_preview_info_blood.setText(bloods.get(position));
                        objBean.setBlood(bloods.get(position));
                        mPreviewModifyPop.dismiss();
                    }
                });
                break;
            case R.id.iv_blood_num_minus:
                numStr = tv_blood_num.getText().toString();
                num = Integer.parseInt(numStr);
                if (num > 1) {
                    num--;
                    tv_frg_preview_info_blood_number.setText(num+"");
                    tv_blood_num.setText(num + "");
                    objBean.setBloodNum(numStr+"");
                }
                break;
            case R.id.iv_blood_num_plus:
                numStr = tv_blood_num.getText().toString();
                num = Integer.parseInt(numStr);
                if (num < 10) {
                    num++;
                    tv_frg_preview_info_blood_number.setText(num+"");
                    tv_blood_num.setText(num + "");
                    objBean.setBloodNum(numStr+"");
                }
                break;


            //组织样本类型
            case R.id.tv_sample:
                final List<String> samples = new ArrayList<>();
                samples.add("脾脏");
                samples.add("血管");


                mPreviewModifyPop = new PreviewModifyPop(this, samples, tv_sample.getWidth());
                mPreviewModifyPop.showAsDropDown(view);
                mPreviewModifyPop.setOnClickChangeListener(new PreviewModifyPop.OnClickChangeListener() {
                    @Override
                    public void OnClickChange(int position) {
                        tv_sample.setText(samples.get(position));
                        tv_frg_preview_info_sample_organ.setText(samples.get(position));
                        objBean.setSampleOrgan(samples.get(position));
                        mPreviewModifyPop.dismiss();
                    }
                });
                break;
            case R.id.iv_sample_num_minus:
                numStr = tv_sample_num.getText().toString();
                num = Integer.parseInt(numStr);
                if (num > 1) {
                    num--;
                    tv_sample_num.setText(num + "");
                    tv_frg_preview_info_sample_organ_number.setText(num+"");
                    objBean.setSampleOrganNum(num+"");
                }
                break;
            case R.id.iv_sample_num_plus:
                numStr = tv_sample_num.getText().toString();
                num = Integer.parseInt(numStr);
                if (num < 10) {
                    num++;
                    tv_sample_num.setText(num + "");
                    tv_frg_preview_info_sample_organ_number.setText(num+"");
                    objBean.setSampleOrganNum(num+"");
                }
                break;


            //转运方式
            case R.id.tv_method:
                final List<String> methods = new ArrayList<>();
                methods.add("飞机");
                methods.add("火车");
                methods.add("救护车");
                methods.add("其他");


                mPreviewModifyPop = new PreviewModifyPop(this, methods, tv_method.getWidth());
                mPreviewModifyPop.showAsDropDown(view);
                mPreviewModifyPop.setOnClickChangeListener(new PreviewModifyPop.OnClickChangeListener() {
                    @Override
                    public void OnClickChange(int position) {
                        tv_method.setText(methods.get(position));
                        tv_frg_preview_info_way.setText(methods.get(position));
                        objBean.setTracfficType(methods.get(position));
                        mPreviewModifyPop.dismiss();
                    }
                });
                break;
            //起始地
            case R.id.tv_start_location:
                cityPicker();
                break;
            //目的地
            case R.id.tv_end_location:
                startActivityForResult(new Intent(this, HospitalChoseActivity.class), 200);
                break;
            //修改ＯＰＯ人员
            case R.id.tv_opo_contact_name:
                Intent intentOpo = new Intent(this, ContactPersonActivity.class);
                intentOpo.putExtra("type", "opo");
                intentOpo.putExtra("roleId", 2);
                startActivityForResult(intentOpo, ContactInfoFragment.REQUEST_CODE_OPO);
                break;
            //修改信息
            case R.id.btn_modify:
                if(mObjBean.size()<=2){
                    ToastUtil.showToast("请选择转运人",this);
                }else {
                    updateTransfer();
                }
                break;


            case R.id.ll_site:
                Intent siteIntent = new Intent(this, TransferSiteActivity.class);
                siteIntent.putExtra("organSeg", objBean.getOrganSeg());
                startActivity(siteIntent);

                break;
            case R.id.ll_pdf:
                downloadPdf(objBean.getOrganSeg(), objBean.getOrgan());
                break;
            case R.id.ll_back:
                finish();
                break;
            case R.id.ll_frg_preview_info_base:
                if (ll_frg_preview_info_base_hide.getVisibility() == View.VISIBLE) {
                    ll_frg_preview_info_base_hide.setVisibility(View.GONE);
                    iv_frg_preview_info_base.setImageResource((R.drawable.cloud_3xinxi_down));
                    tv_transfer_line.setVisibility(View.GONE);
                } else {
                    ll_frg_preview_info_base_hide.setVisibility(View.VISIBLE);
                    iv_frg_preview_info_base.setImageResource((R.drawable.cloud_3xinxi_up));
                    tv_transfer_line.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.ll_frg_preview_info_organ:
                if (ll_frg_preview_info_organ_hide.getVisibility() == View.VISIBLE) {
                    ll_frg_preview_info_organ_hide.setVisibility(View.GONE);
                    iv_frg_preview_info_organ.setImageResource((R.drawable.cloud_3xinxi_down));
                    tv_info_line.setVisibility(View.GONE);
                } else {
                    ll_frg_preview_info_organ_hide.setVisibility(View.VISIBLE);
                    iv_frg_preview_info_organ.setImageResource((R.drawable.cloud_3xinxi_up));
                    tv_info_line.setVisibility(View.VISIBLE);
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
                    tv_opo_line.setVisibility(View.GONE);
                } else {
                    ll_frg_preview_info_transfer_hide.setVisibility(View.VISIBLE);
                    iv_frg_preview_info_transfer.setImageResource((R.drawable.cloud_3xinxi_up));
                    tv_opo_line.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btn_dissolve:
                mAlertDialog = new AlertDialog.Builder(this);
                mAlertDialog.setMessage("解散群组后,所有人都无法接受消息!");
                mAlertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                mAlertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteGroup(objBean.getOrganSeg());

                    }
                });
                mAlertDialog.show();
                break;

        }

    }

    // 回调方法，从第二个页面回来的时候会执行这个方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PRE_TRANSFER &&data!=null) {
//            String transferName = SharePreUtils.getString("transferName", "", this);
//            String transferPhone = SharePreUtils.getString("transferPhone", "", this);
//            String transferUrl = SharePreUtils.getString("transferUrl", "", this);

            String transferName = data.getStringExtra("transferName");
            String transferPhone = data.getStringExtra("transferPhone");
            String transferUrl = data.getStringExtra("transferUrl");

            ContactListJson.ObjBean objBeanTemp = new ContactListJson.ObjBean();
            objBeanTemp.setIsUploadPhoto("0");
            objBeanTemp.setWechatUrl(transferUrl);
            objBeanTemp.setTrueName(transferName);
            objBeanTemp.setContactPhone(transferPhone);
            mObjBean.add(0, objBeanTemp);

            String names = ""; //发送的通话人
            String namePhones = ""; //发送短信的电话
            String usersIds = ""; //人员

            for (int i = 0; i < mObjBean.size(); i++) {


                usersIds += mObjBean.get(i).getContactPhone()==null?mObjBean.get(i).getPhone():mObjBean.get(i).getContactPhone();
                //Log.e(TAG,mObjBean.get(i).getContactPhone()+","+mObjBean.get(i).getPhone());
                if (i == mObjBean.size() - 1) {

                } else {
                    usersIds += ",";
                }
            }

            names = mObjBean.get(0).getTrueName();
            namePhones = mObjBean.get(0).getContactPhone();


            getGroupName(objBean.getOrganSeg(), usersIds, names, namePhones);

            mContactAddAdapter.refresh(mObjBean);
        } else if (requestCode == 200) {
            String hospitalName = data.getStringExtra("hospitalName");
            tv_end_location.setText(hospitalName);
            tv_frg_preview_info_to_hosp.setText(hospitalName);
            objBean.setToHospName(hospitalName);
            loadHospitalAddress(hospitalName);
        } else if (requestCode == ContactInfoFragment.REQUEST_CODE_OPO) {

            String opoName = SharePreUtils.getString("opoName", "", this);
            String opoPhone = SharePreUtils.getString("opoPhone", "", this);
            String opoUrl = SharePreUtils.getString("opoUrl", "", this);

            objBean.setOpoContactName(opoName);
            objBean.setOpoContactPhone(opoPhone);
            objBean.setOpoContactUrl(opoUrl);

            tv_frg_preview_info_contact_name.setText(opoName);
            tv_frg_preview_info_contact_phone.setText(opoPhone);

            if (!"".equals(opoName)) {
                tv_frg_preview_info_contact_name.setText(opoName);
                tv_frg_preview_info_contact_phone.setText(opoPhone);
                tv_opo_contact_name.setText(opoName);
//                tv_frg_contact_info_person.setText(contactName + " " + contactPhone);
//                tv_frg_contact_info_phone.setText(contactPhone);
            }

        }
    }

    /**
     * 群组发送消息
     */
    private void sendGroupMessage(String organSeg, String content) {
        RequestParams params = new RequestParams(URL.RONG);
        params.addBodyParameter("action", "sendGroupMsg");
        params.addBodyParameter("phone", phone);
        params.addBodyParameter("organSeg", organSeg);
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

    /**
     * @param organSeg
     */
    private void getGroupName(final String organSeg, final String usersIds, final String names, final String namePhones) {
        showWaitDialog(getResources().getString(R.string.loading), false, "load");
        RequestParams params = new RequestParams(URL.RONG);
        params.addBodyParameter("action", "getGroup");
        params.addBodyParameter("organSeg", organSeg);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {


                    addGroup(organSeg, photoJson.getMsg(), usersIds, names, namePhones);

                } else {
                    dismissDialog();
                }
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

    private void sendTransferSms(String phones, String content) {
        RequestParams params = new RequestParams(URL.SMS);
        params.addBodyParameter("action", "sendTransfer");
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

    private void addGroup(final String organSeg, String groupName, String usersIds, final String names, final String namesPhones) {

        RequestParams params = new RequestParams(URL.RONG);

        params.addBodyParameter("action", "addGroup");

        params.addBodyParameter("organSeg", organSeg);
        params.addBodyParameter("groupName", groupName);
        params.addBodyParameter("usersIds", usersIds);
        //LogUtil.e(TAG, "usersIds:" + usersIds);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                dismissDialog();
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {

                    String content = "我已邀请【" + names + "】加入转运组";
                    sendGroupMessage(organSeg, content);
                    String inviteContent = SharePreUtils.getString("trueName", "", PreviewInfoActivity.this) + " 邀请你加入器官段号为" + organSeg + "的转运团队，详情请至APP查看。";
                    sendTransferSms(namesPhones, inviteContent);


                    noticeTransfer(organSeg, "");
                } else {
                    ToastUtil.showToast("加入群组失败", PreviewInfoActivity.this);
                }

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

    private void cityPicker() {
        CityPicker cityPicker = new CityPicker.Builder(this)
                .textSize(20)
                .title("地址选择")
                .titleBackgroundColor("#f5f5f5")
                .titleTextColor("#1d4499")
                .backgroundPop(0x60000000)
                .confirTextColor("#057dff")
                .cancelTextColor("#057dff")
                .province("浙江省")
                .city("杭州市")
                .district("江干区")
                .textColor(Color.parseColor("#050505"))
                .provinceCyclic(true)
                .cityCyclic(false)
                .districtCyclic(false)
                .visibleItemsCount(5)
                .itemPadding(10)
                .onlyShowProvinceAndCity(false)
                .build();
        cityPicker.show();

        //监听方法，获取选择结果
        cityPicker.setOnCityItemClickListener(new CityPicker.OnCityItemClickListener() {
            @Override
            public void onSelected(String... citySelected) {
                //省份
                String province = citySelected[0];
                //城市
                String city = citySelected[1];
                //区县（如果设定了两级联动，那么该项返回空）
                String district = citySelected[2];
                //邮编
                String code = citySelected[3];
                tv_start_location.setText(city + district);
                tv_frg_preview_info_from_city.setText(city + district);
                objBean.setFromCity(city + district);
                loadStartLocation(city + district);

            }

            @Override
            public void onCancel() {
                // Toast.makeText(MainActivity.this, "已取消", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteGroup(String organSeg) {
        RequestParams params = new RequestParams(URL.RONG);
        params.addBodyParameter("action", "deleteGroup");
        params.addBodyParameter("organSeg", organSeg);
        params.addBodyParameter("phone", phone);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    btn_dissolve.setVisibility(View.GONE);
                    ToastUtil.showToast("解散群组成功", PreviewInfoActivity.this);
                } else {
                    ToastUtil.showToast("解散群组失败", PreviewInfoActivity.this);
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

    private void isExistGroup(String organSeg) {
        RequestParams params = new RequestParams(URL.RONG);
        params.addBodyParameter("action", "getGroupOrganSeg");
        params.addBodyParameter("organSeg", organSeg);
        params.addBodyParameter("phone", phone);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                //ToastUtil.showToast("解散群组成功"+result, PreviewInfoActivity.this);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    if (phone.equals(objBean.getPhone()) || phone.equals(objBean.getContactPhone())) {
                        btn_dissolve.setVisibility(View.GONE);//
                    } else {
                        btn_dissolve.setVisibility(View.GONE);
                    }
                } else {

                    btn_dissolve.setVisibility(View.GONE);
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

    public String getDateBase(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd mm:HH");
        return sdf.format(date);
    }

    private void initDatePicker() {
        //控制时间范围(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
        //因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(2013, 0, 23);
        Calendar endDate = Calendar.getInstance();

        //endDate.set(2019, 11, 28);
        //时间选择器
        pvDate = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                // 这里回调过来的v,就是show()方法里面所添加的 View 参数，如果show的时候没有添加参数，v则为null

                /*btn_Time.setText(getTime(date));*/
                //Button btn = (Button) v;
                //btn.setText(getTime(date));
                tv_get_time.setText(getDateBase(date));
            }
        })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(new boolean[]{true, true, true, true, true, false})
                .setLabel("年", "月", "日", "点", "分", "")
                .isCenterLabel(false)
                .setDividerColor(Color.DKGRAY)
                // .setTitleBgColor(0xFF666666)//标题背景颜色 Night mode
                .setContentSize(18)
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setBackgroundId(0x55999999) //设置外部遮罩颜色

                .setDecorView(null)
                .build();
    }

    /**
     * 根据医院名称获取地址
     *
     * @param hospitalName
     */
    private void loadHospitalAddress(final String hospitalName) {
        RequestParams params = new RequestParams(URL.USER);
        params.addBodyParameter("action", "getHospitalAddress");
        params.addBodyParameter("hospitalName", hospitalName);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                HospitalJson hospitalJson = new Gson().fromJson(result, HospitalJson.class);
                if (hospitalJson != null && hospitalJson.getResult() == CONSTS.SEND_OK) {
                    toHospitalAddress = hospitalJson.getObj() == null ? null : hospitalJson.getObj().getAddress();
                    LogUtil.e(TAG, toHospitalAddress + ":toHospitalAddress");
                    loadEndLocation(toHospitalAddress);

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

    /**
     * 获取开始的经纬度 杭州市江干区
     */
    private void loadStartLocation(String address) {

        RequestParams params = new RequestParams(URL.GAO_DE_LOCATION_URL + address);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                LatiLongJson latiLongJson = new Gson().fromJson(result, LatiLongJson.class);

                if (latiLongJson != null && "1".equals(latiLongJson.getStatus())) {

                    if (latiLongJson.getGeocodes() != null && latiLongJson.getGeocodes().length > 0) {
                        startLocation = latiLongJson.getGeocodes()[0].getLocation();

                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(TAG, ex.getMessage());
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
     * 获取结束的经纬度
     */
    private void loadEndLocation(String address) {

        RequestParams params = new RequestParams(URL.GAO_DE_LOCATION_URL + address);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                LatiLongJson latiLongJson = new Gson().fromJson(result, LatiLongJson.class);

                if (latiLongJson != null && "1".equals(latiLongJson.getStatus())) {

                    if (latiLongJson.getGeocodes() != null && latiLongJson.getGeocodes().length > 0) {
                        endLocation = latiLongJson.getGeocodes()[0].getLocation();

                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(TAG, "onErrorEnd:" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void updateTransfer() {

        showWaitDialog("修改中...", true, "");
        String organ = tv_organ.getText().toString().trim();
        String organNum = tv_organ_num.getText().toString().trim();
        String blood = tv_blood.getText().toString().trim();
        String bloodNum = tv_blood_num.getText().toString().trim();
        String sample = tv_sample.getText().toString().trim();
        String sampleNum = tv_sample_num.getText().toString().trim();
        String fromCity = tv_start_location.getText().toString().trim();
        String getTime = tv_get_time.getText().toString().trim();

        String toHospName = tv_end_location.getText().toString().trim();

        String modifyOrganSeg = edt_organ_seg.getText().toString().trim();

        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "updateTransfer");

        params.addBodyParameter("organSeg", objBean.getOrganSeg());
        params.addBodyParameter("organ", organ);
        params.addBodyParameter("organNum", organNum);
        params.addBodyParameter("blood", blood);
        params.addBodyParameter("bloodNum", bloodNum);
        params.addBodyParameter("sampleOrgan", sample);
        params.addBodyParameter("sampleOrganNum", sampleNum);
        params.addBodyParameter("contactName", objBean.getContactName());
        params.addBodyParameter("contactPhone", objBean.getContactPhone());
        params.addBodyParameter("fromCity", fromCity);
        params.addBodyParameter("getTime", getTime);
        params.addBodyParameter("openPsd", objBean.getOpenPsd());
        params.addBodyParameter("opoName", objBean.getOpoName());
        params.addBodyParameter("toHospName", toHospName);

        if("".equals(objBean.getTrueName())){
            objBean.setTrueName(mObjBean.get(0).getTrueName());
            objBean.setPhone(mObjBean.get(0).getPhone()==null?mObjBean.get(0).getContactPhone():mObjBean.get(0).getPhone());
        }
        params.addBodyParameter("phone", objBean.getPhone());
        params.addBodyParameter("trueName", objBean.getTrueName());
        params.addBodyParameter("tracfficType", objBean.getTracfficType());
        params.addBodyParameter("tracfficNumber", objBean.getTracfficNumber());
        //params.addBodyParameter("usersIds", usersIds);
        if (startLocation != null && startLocation.contains(",") && endLocation != null && endLocation.contains(",")) {

            distance = LocationUtils.getDistance(Double.parseDouble(startLocation.split(",")[1]), Double.parseDouble(startLocation.split(",")[0]), Double.parseDouble(endLocation.split(",")[1]), Double.parseDouble(endLocation.split(",")[0]));
            params.addBodyParameter("distance", distance + "");
        } else {
            params.addBodyParameter("distance", objBean.getDistance() + "");
        }

        if (toHospitalAddress == null) {
            params.addBodyParameter("toHosp", objBean.getToHosp());
        } else {
            params.addBodyParameter("toHosp", toHospitalAddress.split("市")[0]);
        }
        params.addBodyParameter("opoContactName", objBean.getOpoContactName());
        params.addBodyParameter("opoContactPhone", objBean.getOpoContactPhone());
        if (modifyOrganSeg.equals(objBean.getOrganSeg())) {
            params.addBodyParameter("modifyOrganSeg", "");
        } else {
            params.addBodyParameter("modifyOrganSeg", modifyOrganSeg);
        }
        params.addBodyParameter("historyModify", "modify");
        params.addBodyParameter("status", objBean.getStatus());
        params.addBodyParameter("autoTransfer", "1");
        params.addBodyParameter("isStart", objBean.getIsStart());


        if (startLocation != null && startLocation.contains(",") && endLocation != null && endLocation.contains(",")) {

            params.addBodyParameter("startLong", startLocation.split(",")[0]);
            params.addBodyParameter("startLati", startLocation.split(",")[1]);
            params.addBodyParameter("endLong", endLocation.split(",")[0]);
            params.addBodyParameter("endLati", endLocation.split(",")[1]);

        } else {
            params.addBodyParameter("startLong", objBean.getStartLong());
            params.addBodyParameter("startLati", objBean.getStartLati());
            params.addBodyParameter("endLong", objBean.getEndLong());
            params.addBodyParameter("endLati", objBean.getEndLati());
        }
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                LogUtil.e(TAG, "result:" + result);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    ToastUtil.showToast("修改完成", PreviewInfoActivity.this);
                    noticeTransfer(objBean.getOrganSeg(), "");
                    CONSTS.IS_HISTORY_MODIFY = true;

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

    @Override
    public void OnClickChange(String pwd) {
        if ("".equals(pwd)) {
            ToastUtil.showToast("请输入密码", this);
        } else {
            String hospitalName = SharePreUtils.getString("hospital", "", this);
            loadWebLogin(pwd, hospitalName);
            //ToastUtil.showToast(hospitalName+","+pwd, this);
        }
    }

    private void loadWebLogin(String pwd, String hospitalName) {
        RequestParams params = new RequestParams(URL.USER);
        params.addBodyParameter("action", "loginWeb");
        params.addBodyParameter("hospital", hospitalName);
        params.addBodyParameter("pwd", pwd);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    tv_modify.setText("取消修改");
                    mWebLoginPopup.dismiss();
                    modifyShow();
                    loadStartLocation(objBean.getFromCity());
                    loadHospitalAddress(objBean.getToHospName());
                } else {
                    ToastUtil.showToast("密码错误", PreviewInfoActivity.this);
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
    protected synchronized void onDestroy() {
        super.onDestroy();
        //保存保存的信息
        CONSTS.MODIFY_TRANSFER = objBean;
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, ContactPersonActivity.class);
        //是previewActivity传来的

        intent.putExtra("type", "addActTransfer");

        //intent.putExtra("organSeg", organSeg);
        PreviewInfoActivity.FLAG = 0;
        startActivityForResult(intent, PRE_TRANSFER);

    }
}
