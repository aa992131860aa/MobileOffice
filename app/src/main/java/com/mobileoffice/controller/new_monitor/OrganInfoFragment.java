package com.mobileoffice.controller.new_monitor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobileoffice.R;
import com.mobileoffice.utils.CONSTS;

/**
 * Created by 99213 on 2017/6/29.
 */

public class OrganInfoFragment extends NewMonitorBaseFragment implements View.OnClickListener {
    private View root;
    private ImageView iv_frg_organ_info_organ_minus;
    private TextView tv_frg_organ_info_organ_number;
    private ImageView iv_frg_organ_info_organ_plus;
    private LinearLayout ll_frg_organ_info_heart;
    private TextView tv_frg_organ_info_heart;
    private ImageView iv_frg_organ_info_heart;
    private LinearLayout ll_frg_organ_info_liver;
    private ImageView iv_frg_organ_info_liver;
    private TextView tv_frg_organ_info_liver;
    private LinearLayout ll_frg_organ_info_lung;
    private ImageView iv_frg_organ_info_lung;
    private TextView tv_frg_organ_info_lung;
    private LinearLayout ll_frg_organ_info_kidney;
    private ImageView iv_frg_organ_info_kidney;
    private TextView tv_frg_organ_info_kidney;
    private LinearLayout ll_frg_organ_info_pancreas;
    private ImageView iv_frg_organ_info_pancreas;
    private TextView tv_frg_organ_info_pancreas;
    private LinearLayout ll_frg_organ_info_cornea;
    private ImageView iv_frg_organ_info_cornea;
    private TextView tv_frg_organ_info_cornea;
    private ImageView iv_frg_organ_info_blood_minus;
    private TextView tv_frg_organ_info_blood_number;
    private ImageView iv_frg_organ_info_blood_plus;
    private LinearLayout ll_frg_organ_info_blood_a;
    private ImageView iv_frg_organ_info_blood_a;
    private LinearLayout ll_frg_organ_info_blood_b;
    private ImageView iv_frg_organ_info_blood_b;
    private LinearLayout ll_frg_organ_info_blood_ab;
    private ImageView iv_frg_organ_info_blood_ab;
    private LinearLayout ll_frg_organ_info_blood_o;
    private ImageView iv_frg_organ_info_blood_o;
    private ImageView iv_frg_organ_info_sample_organ_minus;
    private TextView tv_frg_organ_info_sample_organ_number;
    private ImageView iv_frg_organ_info_sample_organ_plus;
    private LinearLayout ll_frg_organ_info_spleen;
    private ImageView iv_frg_organ_info_spleen;
    private LinearLayout ll_frg_organ_info_blood_xueguan;
    private ImageView iv_frg_organ_info_blood_xueguan;
    private TextView tv_frg_organ_info_pre;
    private TextView tv_frg_organ_info_next;

    private String organ = "肝脏";
    private int organNum;
    private String blood = "";
    private int bloodNum;
    private String sampleOrgan = "";
    private int sampleOrganNum;
    private static ViewPager viewPager;
    private static TextView textView;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.frg_organ_info, container, false);
        initView();


        return root;
    }

    public static OrganInfoFragment newIntance(ViewPager v, TextView t) {
        OrganInfoFragment organInfoFragment = new OrganInfoFragment();
        viewPager = v;
        textView = t;
        return organInfoFragment;

    }

    private void initOrgan() {
        iv_frg_organ_info_heart.setImageResource(R.drawable.newtrs_table2_heart);
        iv_frg_organ_info_liver.setImageResource(R.drawable.newtrs_table2_liver);
        iv_frg_organ_info_lung.setImageResource(R.drawable.newtrs_table2_lung);
        iv_frg_organ_info_kidney.setImageResource(R.drawable.newtrs_table2_kidney);
        iv_frg_organ_info_pancreas.setImageResource(R.drawable.newtrs_table2_pancreas);
        iv_frg_organ_info_cornea.setImageResource(R.drawable.newtrs_table2_cornea);

        tv_frg_organ_info_heart.setTextColor(getResources().getColor(R.color.font_black_6));
        tv_frg_organ_info_liver.setTextColor(getResources().getColor(R.color.font_black_6));
        tv_frg_organ_info_lung.setTextColor(getResources().getColor(R.color.font_black_6));
        tv_frg_organ_info_kidney.setTextColor(getResources().getColor(R.color.font_black_6));
        tv_frg_organ_info_pancreas.setTextColor(getResources().getColor(R.color.font_black_6));
        tv_frg_organ_info_cornea.setTextColor(getResources().getColor(R.color.font_black_6));
    }

    private void initBlood() {
        iv_frg_organ_info_blood_a.setImageResource(R.drawable.newtrs_table2_blooda);
        iv_frg_organ_info_blood_b.setImageResource(R.drawable.newtrs_table2_bloodb);
        iv_frg_organ_info_blood_ab.setImageResource(R.drawable.newtrs_table2_bloodab);
        iv_frg_organ_info_blood_o.setImageResource(R.drawable.newtrs_table2_bloodo);
    }

    private void initSampleOrgan() {
        iv_frg_organ_info_spleen.setImageResource(R.drawable.newtrs_table2_spleen);
        iv_frg_organ_info_blood_xueguan.setImageResource(R.drawable.newtrs_table2_xueguan);

    }

    private void initView() {
        iv_frg_organ_info_organ_minus = (ImageView) root.findViewById(R.id.iv_frg_organ_info_organ_minus);
        tv_frg_organ_info_organ_number = (TextView) root.findViewById(R.id.tv_frg_organ_info_organ_number);
        iv_frg_organ_info_organ_plus = (ImageView) root.findViewById(R.id.iv_frg_organ_info_organ_plus);
        ll_frg_organ_info_heart = (LinearLayout) root.findViewById(R.id.ll_frg_organ_info_heart);
        tv_frg_organ_info_heart = (TextView) root.findViewById(R.id.tv_frg_organ_info_heart);
        iv_frg_organ_info_heart = (ImageView) root.findViewById(R.id.iv_frg_organ_info_heart);
        ll_frg_organ_info_liver = (LinearLayout) root.findViewById(R.id.ll_frg_organ_info_liver);
        iv_frg_organ_info_liver = (ImageView) root.findViewById(R.id.iv_frg_organ_info_liver);
        tv_frg_organ_info_liver = (TextView) root.findViewById(R.id.tv_frg_organ_info_liver);
        ll_frg_organ_info_lung = (LinearLayout) root.findViewById(R.id.ll_frg_organ_info_lung);
        iv_frg_organ_info_lung = (ImageView) root.findViewById(R.id.iv_frg_organ_info_lung);
        tv_frg_organ_info_lung = (TextView) root.findViewById(R.id.tv_frg_organ_info_lung);
        ll_frg_organ_info_kidney = (LinearLayout) root.findViewById(R.id.ll_frg_organ_info_kidney);
        iv_frg_organ_info_kidney = (ImageView) root.findViewById(R.id.iv_frg_organ_info_kidney);
        tv_frg_organ_info_kidney = (TextView) root.findViewById(R.id.tv_frg_organ_info_kidney);
        ll_frg_organ_info_pancreas = (LinearLayout) root.findViewById(R.id.ll_frg_organ_info_pancreas);
        iv_frg_organ_info_pancreas = (ImageView) root.findViewById(R.id.iv_frg_organ_info_pancreas);
        tv_frg_organ_info_pancreas = (TextView) root.findViewById(R.id.tv_frg_organ_info_pancreas);
        ll_frg_organ_info_cornea = (LinearLayout) root.findViewById(R.id.ll_frg_organ_info_cornea);
        iv_frg_organ_info_cornea = (ImageView) root.findViewById(R.id.iv_frg_organ_info_cornea);
        tv_frg_organ_info_cornea = (TextView) root.findViewById(R.id.tv_frg_organ_info_cornea);
        iv_frg_organ_info_blood_minus = (ImageView) root.findViewById(R.id.iv_frg_organ_info_blood_minus);
        tv_frg_organ_info_blood_number = (TextView) root.findViewById(R.id.tv_frg_organ_info_blood_number);
        iv_frg_organ_info_blood_plus = (ImageView) root.findViewById(R.id.iv_frg_organ_info_blood_plus);
        ll_frg_organ_info_blood_a = (LinearLayout) root.findViewById(R.id.ll_frg_organ_info_blood_a);
        iv_frg_organ_info_blood_a = (ImageView) root.findViewById(R.id.iv_frg_organ_info_blood_a);
        ll_frg_organ_info_blood_b = (LinearLayout) root.findViewById(R.id.ll_frg_organ_info_blood_b);
        iv_frg_organ_info_blood_b = (ImageView) root.findViewById(R.id.iv_frg_organ_info_blood_b);
        ll_frg_organ_info_blood_ab = (LinearLayout) root.findViewById(R.id.ll_frg_organ_info_blood_ab);
        iv_frg_organ_info_blood_ab = (ImageView) root.findViewById(R.id.iv_frg_organ_info_blood_ab);
        ll_frg_organ_info_blood_o = (LinearLayout) root.findViewById(R.id.ll_frg_organ_info_blood_o);
        iv_frg_organ_info_blood_o = (ImageView) root.findViewById(R.id.iv_frg_organ_info_blood_o);
        iv_frg_organ_info_sample_organ_minus = (ImageView) root.findViewById(R.id.iv_frg_organ_info_sample_organ_minus);
        tv_frg_organ_info_sample_organ_number = (TextView) root.findViewById(R.id.tv_frg_organ_info_sample_organ_number);
        iv_frg_organ_info_sample_organ_plus = (ImageView) root.findViewById(R.id.iv_frg_organ_info_sample_organ_plus);
        ll_frg_organ_info_spleen = (LinearLayout) root.findViewById(R.id.ll_frg_organ_info_spleen);
        iv_frg_organ_info_spleen = (ImageView) root.findViewById(R.id.iv_frg_organ_info_spleen);
        ll_frg_organ_info_blood_xueguan = (LinearLayout) root.findViewById(R.id.ll_frg_organ_info_blood_xueguan);
        iv_frg_organ_info_blood_xueguan = (ImageView) root.findViewById(R.id.iv_frg_organ_info_blood_xueguan);
        tv_frg_organ_info_pre = (TextView) root.findViewById(R.id.tv_frg_organ_info_pre);
        tv_frg_organ_info_next = (TextView) root.findViewById(R.id.tv_frg_organ_info_next);


        iv_frg_organ_info_organ_minus.setOnClickListener(this);
        iv_frg_organ_info_organ_plus.setOnClickListener(this);
        ll_frg_organ_info_heart.setOnClickListener(this);
        ll_frg_organ_info_liver.setOnClickListener(this);
        ll_frg_organ_info_lung.setOnClickListener(this);
        ll_frg_organ_info_kidney.setOnClickListener(this);
        ll_frg_organ_info_pancreas.setOnClickListener(this);
        ll_frg_organ_info_cornea.setOnClickListener(this);
        iv_frg_organ_info_blood_minus.setOnClickListener(this);
        tv_frg_organ_info_blood_number.setOnClickListener(this);
        iv_frg_organ_info_blood_plus.setOnClickListener(this);
        ll_frg_organ_info_blood_a.setOnClickListener(this);
        ll_frg_organ_info_blood_b.setOnClickListener(this);
        ll_frg_organ_info_blood_ab.setOnClickListener(this);
        ll_frg_organ_info_blood_o.setOnClickListener(this);
        iv_frg_organ_info_sample_organ_minus.setOnClickListener(this);
        iv_frg_organ_info_sample_organ_plus.setOnClickListener(this);
        ll_frg_organ_info_spleen.setOnClickListener(this);
        ll_frg_organ_info_blood_xueguan.setOnClickListener(this);
        tv_frg_organ_info_pre.setOnClickListener(this);
        tv_frg_organ_info_next.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_frg_organ_info_organ_minus:
                String organNumStr = tv_frg_organ_info_organ_number.getText().toString().trim();
                organNum = Integer.parseInt(organNumStr);
                organNum--;
                if (organNum < 1) {
                    organNum = 1;
                } else {
                    tv_frg_organ_info_organ_number.setText(organNum + "");
                }
                break;
            case R.id.iv_frg_organ_info_organ_plus:
                String organNumStr2 = tv_frg_organ_info_organ_number.getText().toString().trim();
                organNum = Integer.parseInt(organNumStr2);
                organNum++;
                if (organNum > 10) {
                    organNum = 10;
                } else {
                    tv_frg_organ_info_organ_number.setText(organNum + "");
                }
                break;
            case R.id.ll_frg_organ_info_heart:
                initOrgan();
                iv_frg_organ_info_heart.setImageResource(R.drawable.newtrs_table2_heart_on);
                tv_frg_organ_info_heart.setTextColor(getResources().getColor(R.color.highlight));
                organ = "心脏";
                break;
            case R.id.ll_frg_organ_info_liver:
                initOrgan();
                iv_frg_organ_info_liver.setImageResource(R.drawable.newtrs_table2_liver_on);
                tv_frg_organ_info_liver.setTextColor(getResources().getColor(R.color.highlight));
                organ = "肝脏";
                break;
            case R.id.ll_frg_organ_info_lung:
                initOrgan();
                iv_frg_organ_info_lung.setImageResource(R.drawable.newtrs_table2_lung_on);
                tv_frg_organ_info_lung.setTextColor(getResources().getColor(R.color.highlight));
                organ = "肺";
                break;
            case R.id.ll_frg_organ_info_kidney:
                initOrgan();
                iv_frg_organ_info_kidney.setImageResource(R.drawable.newtrs_table2_kidney_on);
                tv_frg_organ_info_kidney.setTextColor(getResources().getColor(R.color.highlight));
                organ = "肾脏";
                break;
            case R.id.ll_frg_organ_info_pancreas:
                initOrgan();
                iv_frg_organ_info_pancreas.setImageResource(R.drawable.newtrs_table2_pancreas_on);
                tv_frg_organ_info_pancreas.setTextColor(getResources().getColor(R.color.highlight));
                organ = "胰脏";
                break;
            case R.id.ll_frg_organ_info_cornea:
                initOrgan();
                iv_frg_organ_info_cornea.setImageResource(R.drawable.newtrs_table2_cornea_on);
                tv_frg_organ_info_cornea.setTextColor(getResources().getColor(R.color.highlight));
                organ = "眼角膜";
                break;
            case R.id.iv_frg_organ_info_blood_minus:
                String bloodNumStr = tv_frg_organ_info_blood_number.getText().toString().trim();
                organNum = Integer.parseInt(bloodNumStr);
                organNum--;
                if (organNum < 1) {
                    organNum = 1;
                } else {
                    tv_frg_organ_info_blood_number.setText(organNum + "");
                }
                break;
            case R.id.tv_frg_organ_info_blood_number:
                break;
            case R.id.iv_frg_organ_info_blood_plus:
                String bloodNumStr2 = tv_frg_organ_info_blood_number.getText().toString().trim();
                organNum = Integer.parseInt(bloodNumStr2);
                organNum++;
                if (organNum > 10) {
                    organNum = 10;
                } else {
                    tv_frg_organ_info_blood_number.setText(organNum + "");
                }
                break;
            case R.id.ll_frg_organ_info_blood_a:
                initBlood();
                iv_frg_organ_info_blood_a.setImageResource(R.drawable.newtrs_table2_blooda_on);
                blood = "A";
                break;
            case R.id.ll_frg_organ_info_blood_b:
                initBlood();
                iv_frg_organ_info_blood_b.setImageResource(R.drawable.newtrs_table2_bloodb_on);
                blood = "B";
                break;
            case R.id.ll_frg_organ_info_blood_ab:
                initBlood();
                iv_frg_organ_info_blood_ab.setImageResource(R.drawable.newtrs_table2_bloodab_on);
                blood = "AB";
                break;
            case R.id.ll_frg_organ_info_blood_o:
                initBlood();
                iv_frg_organ_info_blood_o.setImageResource(R.drawable.newtrs_table2_bloodo_on);
                blood = "O";
                break;
            case R.id.iv_frg_organ_info_sample_organ_minus:
                String sampleOrganNumStr = tv_frg_organ_info_sample_organ_number.getText().toString().trim();
                organNum = Integer.parseInt(sampleOrganNumStr);
                organNum--;
                if (organNum < 1) {
                    organNum = 1;
                } else {
                    tv_frg_organ_info_sample_organ_number.setText(organNum + "");
                }
                break;
            case R.id.iv_frg_organ_info_sample_organ_plus:
                String sampleOrganNumStr2 = tv_frg_organ_info_sample_organ_number.getText().toString().trim();
                organNum = Integer.parseInt(sampleOrganNumStr2);
                organNum++;
                if (organNum > 10) {
                    organNum = 10;
                } else {
                    tv_frg_organ_info_sample_organ_number.setText(organNum + "");
                }
                break;
            case R.id.ll_frg_organ_info_spleen:
                initSampleOrgan();
                iv_frg_organ_info_spleen.setImageResource(R.drawable.newtrs_table2_spleen_on);
                sampleOrgan = "脾脏";
                break;
            case R.id.ll_frg_organ_info_blood_xueguan:
                initSampleOrgan();
                iv_frg_organ_info_blood_xueguan.setImageResource(R.drawable.newtrs_table2_xueguan_on);
                sampleOrgan = "血管";
                break;
            case R.id.tv_frg_organ_info_pre:
                viewPager.setCurrentItem(0);
                break;
            case R.id.tv_frg_organ_info_next:
                objBean.setOrgan(organ);
                objBean.setOrganNum(tv_frg_organ_info_organ_number.getText().toString().trim());
                objBean.setBlood(blood);
                objBean.setBloodNum(tv_frg_organ_info_blood_number.getText().toString().trim());
                objBean.setSampleOrgan(sampleOrgan);
                objBean.setSampleOrganNum(tv_frg_organ_info_sample_organ_number.getText().toString().trim());
                viewPager.setCurrentItem(2);

                break;
        }
    }

    @Override
    public void initData() {
        if (CONSTS.TRANSFER_STATUS == 0) {
            textView.setText("新建转运(2/4)");
        } else if (CONSTS.TRANSFER_STATUS == 1) {
            if(objBean.getAutoTransfer()==CONSTS.AURO_TRANSFER_FINISH_NO){
                textView.setText("完善资料(2/4)");
            }else {
                textView.setText("修改转运(2/4)");
            }
        }
        Log.e("transfer:111", "null" + objBean.toString());
        if (objBean.getOrgan() != null && !"".equals(objBean.getOrgan())) {

            initOrgan();
            if ("心脏".equals(objBean.getOrgan())) {
                iv_frg_organ_info_heart.setImageResource(R.drawable.newtrs_table2_heart_on);
                tv_frg_organ_info_heart.setTextColor(getResources().getColor(R.color.highlight));
            } else if ("肝脏".equals(objBean.getOrgan())) {
                iv_frg_organ_info_liver.setImageResource(R.drawable.newtrs_table2_liver_on);
                tv_frg_organ_info_liver.setTextColor(getResources().getColor(R.color.highlight));
            } else if ("肺".equals(objBean.getOrgan())) {
                iv_frg_organ_info_lung.setImageResource(R.drawable.newtrs_table2_lung_on);
                tv_frg_organ_info_lung.setTextColor(getResources().getColor(R.color.highlight));
            } else if ("肾脏".equals(objBean.getOrgan())) {
                iv_frg_organ_info_kidney.setImageResource(R.drawable.newtrs_table2_kidney_on);
                tv_frg_organ_info_kidney.setTextColor(getResources().getColor(R.color.highlight));
            } else if ("眼角膜".equals(objBean.getOrgan())) {
                iv_frg_organ_info_cornea.setImageResource(R.drawable.newtrs_table2_cornea_on);
                tv_frg_organ_info_cornea.setTextColor(getResources().getColor(R.color.highlight));
            } else if ("胰脏".equals(objBean.getOrgan())) {
                iv_frg_organ_info_pancreas.setImageResource(R.drawable.newtrs_table2_pancreas_on);
                tv_frg_organ_info_pancreas.setTextColor(getResources().getColor(R.color.highlight));
            }
            tv_frg_organ_info_organ_number.setText(objBean.getOrganNum());
            organ = objBean.getOrgan();
            organNum = Integer.parseInt(objBean.getOrganNum());

        }
        if (objBean.getBlood() != null && !"".equals(objBean.getBlood())) {
            initBlood();
            if ("A".equals(objBean.getBlood())) {
                iv_frg_organ_info_blood_a.setImageResource(R.drawable.newtrs_table2_blooda_on);
            } else if ("B".equals(objBean.getBlood())) {
                iv_frg_organ_info_blood_b.setImageResource(R.drawable.newtrs_table2_bloodb_on);
            } else if ("AB".equals(objBean.getBlood())) {
                iv_frg_organ_info_blood_ab.setImageResource(R.drawable.newtrs_table2_bloodab_on);
            } else if ("O".equals(objBean.getBlood())) {
                iv_frg_organ_info_blood_o.setImageResource(R.drawable.newtrs_table2_bloodo_on);
            }
            blood = objBean.getBlood();

            tv_frg_organ_info_blood_number.setText(objBean.getBloodNum());
            bloodNum = Integer.parseInt(objBean.getBloodNum());
        }

        if (objBean.getSampleOrgan() != null && !"".equals(objBean.getSampleOrgan())) {
            initSampleOrgan();
            if ("脾脏".equals(objBean.getSampleOrgan())) {
                iv_frg_organ_info_spleen.setImageResource(R.drawable.newtrs_table2_spleen_on);
            } else if ("血管".equals(objBean.getSampleOrgan())) {
                iv_frg_organ_info_blood_xueguan.setImageResource(R.drawable.newtrs_table2_xueguan_on);
            }
            sampleOrgan = objBean.getSampleOrgan();
            tv_frg_organ_info_sample_organ_number.setText(objBean.getSampleOrganNum());
            sampleOrganNum = Integer.parseInt(objBean.getSampleOrganNum());

        }

    }
}
