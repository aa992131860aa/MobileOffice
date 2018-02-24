package com.mobileoffice.controller.new_monitor;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobileoffice.R;
import com.mobileoffice.entity.BoxUse;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.BoxUseJson;
import com.mobileoffice.json.PhotoJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.DisplayUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;
import com.mobileoffice.view.SecurityCodeView;
import com.mobileoffice.view.com.bigkoo.pickerview.TimePickerView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by 99213 on 2017/6/29.
 */

public class BaseInfoFragment extends NewMonitorBaseFragment implements SecurityCodeView.InputCompleteListener, View.OnClickListener {
    private static final String TAG = "BaseInfoFragment";
    private View root;
    private SecurityCodeView scv_frg_base_info;
    private EditText edt_frg_base_info_organ_seg;
    private TextView tv_frg_base_info_date;
    private TextView tv_frg_base_info_time;

    /**
     * recycler view
     */
    private TimePickerView pvTime;
    private TimePickerView pvDate;

    //下一步
    private TextView tv_frg_base_info_next;
    private String organSeg = "";
    private String getTime = "";
    private String openPsd = "";
    private static ViewPager mViewPager;
    private LinearLayout ll_frg_base_info;
    private static TextView textView;
    //箱号
    private TextView tv_box_no;
    private LinearLayout ll_box_no;
    private TextView edt_box_no;
    //默认的器官段号
    private String mHintOrganSeg = "";
    private BoxNoPopup mBoxNoPopup;
    private List<BoxUse> mBoxUses = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.frg_base_info, container, false);
        init();
        initTimePicker();
        initDatePicker();
        //设置老的器官编号
        if (CONSTS.TRANSFER_STATUS == 1) {
            objBean.setOldOrganSeg(objBean.getOrganSeg());
        }
        if ("scan".equals(CONSTS.TYPE)) {
            ll_box_no.setVisibility(View.GONE);
            tv_box_no.setVisibility(View.VISIBLE);
            tv_box_no.setText(SharePreUtils.getString("boxNo", "", getActivity()));
        } else if ("handle".equals(CONSTS.TYPE)) {
            ll_box_no.setVisibility(View.VISIBLE);
            tv_box_no.setVisibility(View.GONE);
        }

        edtBoxNo();
        return root;
    }

    private void edtBoxNo() {
        edt_box_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                    @SuppressLint("WrongConstant")
//                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService("input_method");
//                    imm.showSoftInput(edt_box_no, InputMethodManager.SHOW_FORCED);// 显示输入法
                loadBoxUse(SharePreUtils.getString("hospital", "", getActivity()), v);
            }
        });
//        edt_box_no.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//
//
//                }
//            }
//        });
//        edt_box_no.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                edt_box_no.setFocusable(true);//设置输入框可聚集
//                edt_box_no.setFocusableInTouchMode(true);//设置触摸聚焦
//                edt_box_no.requestFocus();//请求焦点
//                edt_box_no.findFocus();//获取焦点
//                @SuppressLint("WrongConstant")
//                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService("input_method");
//                imm.showSoftInput(edt_box_no, InputMethodManager.SHOW_FORCED);// 显示输入法
//            }
//        });


    }

    private void loadBoxUse(String hospital, final View v) {
        RequestParams params = new RequestParams(URL.BOX);
        params.addBodyParameter("action", "boxUse");
        params.addBodyParameter("hospital", hospital);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                BoxUseJson boxUseJson = new Gson().fromJson(result, BoxUseJson.class);
                mBoxUses = new ArrayList<>();
                BoxUse boxUse = new BoxUse();
                boxUse.setBoxNo("99999");
                boxUse.setStatus("虚拟测试");
                if (boxUseJson != null && boxUseJson.getResult() == CONSTS.SEND_OK) {
                    mBoxUses = boxUseJson.getObj();
                    mBoxUses.add(boxUse);
                    mBoxNoPopup = new BoxNoPopup(getActivity(), mBoxUses);
                    mBoxNoPopup.showPopupWindow(v);
                    mBoxNoPopup.setOnClickChangeListener(new BoxNoPopup.OnClickChangeListener() {
                        @Override
                        public void OnClickChange(int position) {
                            edt_box_no.setText(mBoxUses.get(position).getBoxNo());
                            mBoxNoPopup.dismiss();
                            //edt_box_no.setHint("");
                            edt_box_no.setTextSize(TypedValue.COMPLEX_UNIT_PX, DisplayUtil.dip2px(getActivity(), 20));
                        }
                    });
                } else {
                    mBoxUses.add(boxUse);
                    mBoxNoPopup = new BoxNoPopup(getActivity(), mBoxUses);
                    mBoxNoPopup.showPopupWindow(v);
                    mBoxNoPopup.setOnClickChangeListener(new BoxNoPopup.OnClickChangeListener() {
                        @Override
                        public void OnClickChange(int position) {
                            edt_box_no.setText(mBoxUses.get(position).getBoxNo());
                            mBoxNoPopup.dismiss();
                            //edt_box_no.setHint("");
                            edt_box_no.setTextSize(TypedValue.COMPLEX_UNIT_PX, DisplayUtil.dip2px(getActivity(), 20));
                        }
                    });
                    //ToastUtil.showToast("暂无可用箱号,可使用测试箱号99999", getActivity());
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
        if (CONSTS.TRANSFER_STATUS == 0) {
            textView.setText("新建转运(1/4)");
        } else if (CONSTS.TRANSFER_STATUS == 1) {
            if (objBean.getAutoTransfer() == CONSTS.AURO_TRANSFER_FINISH_NO) {
                textView.setText("完善资料(1/4)");
            } else {
                textView.setText("修改转运(1/4)");
            }
        }

        if (objBean.getOpenPsd() != null) {
            scv_frg_base_info.setText(objBean.getOpenPsd());
        }
        if (objBean.getGetTime() != null && objBean.getGetTime().contains(" ")) {
            tv_frg_base_info_date.setText(objBean.getGetTime().split(" ")[0]);
            tv_frg_base_info_time.setText(objBean.getGetTime().split(" ")[1]);
        }
        if (objBean.getOrganSeg() != null) {

            if (organSeg.equals(mHintOrganSeg) && !"".equals(organSeg)) {
                edt_frg_base_info_organ_seg.setHint(objBean.getOrganSeg());
            } else {
                edt_frg_base_info_organ_seg.setText(objBean.getOrganSeg());
                edt_frg_base_info_organ_seg.setSelection(objBean.getOrganSeg().length());
            }

        }

        if ("scan".equals(CONSTS.TYPE) && objBean.getBoxNo() != null) {
            ll_box_no.setVisibility(View.GONE);
            tv_box_no.setVisibility(View.VISIBLE);
            tv_box_no.setText(objBean.getBoxNo());
        } else if (objBean.getBoxNo() != null) {
            ll_box_no.setVisibility(View.VISIBLE);
            tv_box_no.setVisibility(View.GONE);
            edt_box_no.setText(objBean.getBoxNo());
            //edt_box_no.setSelection(objBean.getBoxNo().length());
        }

    }

    private void isRepeatOrganSeg(final String pOrganSeg) {
        RequestParams params = new RequestParams(URL.TRANSFER);

        if ("".equals(pOrganSeg) || pOrganSeg == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            mHintOrganSeg = "LP" + sdf.format(new Date());
            organSeg = mHintOrganSeg;
            params.addBodyParameter("organSeg", mHintOrganSeg);
        } else {
            params.addBodyParameter("organSeg", pOrganSeg);
        }


        params.addBodyParameter("action", "organRepeat");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {

                    if ("scan".equals(CONSTS.TYPE)) {
                        repeatBoxNo(tv_box_no.getText().toString());
                    } else {
                        repeatBoxNo(edt_box_no.getText().toString().trim());
                    }

                } else {
                    if ("".equals(pOrganSeg) || pOrganSeg == null) {
                        isRepeatOrganSeg("");
                    } else {
                        ToastUtil.showToast("器官段号重复,请重新填写", getActivity());
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

    public static BaseInfoFragment newIntance(ViewPager viewPage, TextView t) {
        BaseInfoFragment baseInfoFragment = new BaseInfoFragment();
        mViewPager = viewPage;
        textView = t;
        return baseInfoFragment;
    }

    private void init() {
        scv_frg_base_info = (SecurityCodeView) root.findViewById(R.id.scv_frg_base_info);
        edt_frg_base_info_organ_seg = (EditText) root.findViewById(R.id.edt_frg_base_info_organ_seg);
        tv_frg_base_info_date = (TextView) root.findViewById(R.id.tv_frg_base_info_date);
        tv_frg_base_info_time = (TextView) root.findViewById(R.id.tv_frg_base_info_time);
        tv_frg_base_info_next = (TextView) root.findViewById(R.id.tv_frg_base_info_next);
        ll_frg_base_info = (LinearLayout) root.findViewById(R.id.ll_frg_base_info);
        tv_box_no = (TextView) root.findViewById(R.id.tv_box_no);
        ll_box_no = (LinearLayout) root.findViewById(R.id.ll_box_no);
        edt_box_no = (TextView) root.findViewById(R.id.edt_box_no);


        scv_frg_base_info.setInputCompleteListener(this);
        tv_frg_base_info_date.setOnClickListener(this);
        tv_frg_base_info_time.setOnClickListener(this);
        tv_frg_base_info_next.setOnClickListener(this);

        tv_frg_base_info_date.setText(getDateBase());
        tv_frg_base_info_time.setText(getTimeBase());


    }

    @Override
    public void inputComplete() {
        openPsd = scv_frg_base_info.getEditContent();
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
        pvDate = new TimePickerView.Builder(getActivity(), new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                // 这里回调过来的v,就是show()方法里面所添加的 View 参数，如果show的时候没有添加参数，v则为null

                /*btn_Time.setText(getTime(date));*/
                //Button btn = (Button) v;
                //btn.setText(getTime(date));
                tv_frg_base_info_date.setText(getDateBase(date));
            }
        })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(new boolean[]{true, true, true, false, false, false})
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

    private void initTimePicker() {
        //控制时间范围(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
        //因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(2013, 0, 23);
        Calendar endDate = Calendar.getInstance();

        //endDate.set(2019, 11, 28);
        //时间选择器
        pvTime = new TimePickerView.Builder(getActivity(), new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                // 这里回调过来的v,就是show()方法里面所添加的 View 参数，如果show的时候没有添加参数，v则为null

                /*btn_Time.setText(getTime(date));*/
                // Button btn = (Button) v;
                tv_frg_base_info_time.setText(getTimeBase(date));
            }
        })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(new boolean[]{false, false, false, true, true, false})
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

    @Override
    public void deleteContent(boolean isDelete) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_frg_base_info_date:
                pvDate.show(view);
                break;
            case R.id.tv_frg_base_info_time:
                pvTime.show(view);
                break;
            case R.id.tv_frg_base_info_next:
                organSeg = edt_frg_base_info_organ_seg.getText().toString().trim();
                getTime = tv_frg_base_info_date.getText().toString().trim() + " " + tv_frg_base_info_time.getText().toString().trim();
                openPsd = scv_frg_base_info.getEditContent();


                if ("handle".equals(CONSTS.TYPE) && "".equals(edt_box_no.getText().toString().trim())) {
                    ToastUtil.showToast("请选择设备", getActivity());
                }
//                else if ("".equals(organSeg)) {
//                    ToastUtil.showToast("请填写器官段号", getActivity());
//                }
                else if (openPsd != null && !"".equals(openPsd) && openPsd.length() != 4) {
                    ToastUtil.showToast("请填写四位开箱密码", getActivity());
                } else {


                    if (CONSTS.TRANSFER_STATUS == 0) {
                        isRepeatOrganSeg(organSeg);


                    } else if (CONSTS.TRANSFER_STATUS == 1) {
                        //没有改动器官段号,不检验是否重复
                        if (objBean.getOldOrganSeg().equals(organSeg)) {
                            objBean.setOrganSeg(organSeg);
                            objBean.setGetTime(getTime);
                            objBean.setOpenPsd(openPsd);
                            mViewPager.setCurrentItem(1);
                        } else {
                            isRepeatOrganSeg(organSeg);
                        }

                    }

                }
                break;
        }
    }

    private void repeatBoxNo(final String boxNo) {
        RequestParams params = new RequestParams(URL.BOX);
        params.addBodyParameter("action", "start");
        params.addBodyParameter("boxNo", boxNo);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {

                    objBean.setBoxNo(boxNo);
                    objBean.setOrganSeg(organSeg);
                    objBean.setGetTime(getTime);
                    objBean.setOpenPsd(openPsd);

                    mViewPager.setCurrentItem(1);

                } else {
                    if (CONSTS.TRANSFER_STATUS == 1) {
                        objBean.setBoxNo(boxNo);
                        objBean.setOrganSeg(organSeg);
                        objBean.setGetTime(getTime);
                        objBean.setOpenPsd(openPsd);

                        mViewPager.setCurrentItem(1);
                    } else {

                        if ("99999".equals(boxNo)) {
                            objBean.setBoxNo(boxNo);
                            objBean.setOrganSeg(organSeg);
                            objBean.setGetTime(getTime);
                            objBean.setOpenPsd(openPsd);

                            mViewPager.setCurrentItem(1);
                        } else {
                            ToastUtil.showToast("该箱子无法使用", getActivity());
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.showToast("箱子无法使用", getActivity());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    public String getDateBase() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    public String getDateBase(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    public String getTimeBase() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(new Date());
    }

    public String getTimeBase(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(date);
    }


}
