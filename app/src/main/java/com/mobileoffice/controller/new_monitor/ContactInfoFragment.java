package com.mobileoffice.controller.new_monitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobileoffice.MainActivity;
import com.mobileoffice.R;
import com.mobileoffice.controller.message.contact.ContactPersonActivity;
import com.mobileoffice.entity.Opo;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.ContactListJson;
import com.mobileoffice.json.HospitalJson;
import com.mobileoffice.json.LatiLongJson;
import com.mobileoffice.json.OpoInfoJson;
import com.mobileoffice.json.OpoJson;
import com.mobileoffice.json.WeatherIconJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;
import com.mobileoffice.view.OpoInfoContactPopup;
import com.mobileoffice.view.OrganTypePopup;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by 99213 on 2017/6/29.
 */

public class ContactInfoFragment extends NewMonitorBaseFragment implements View.OnClickListener {
    private static final String TAG = "BaseInfoFragment";
    private View root;
    private static ViewPager mViewPager;
    private TextView tv_frg_contact_info_opo;
    private LinearLayout ll_frg_contact_info_contact;
    private TextView tv_frg_contact_info_person;
    private TextView tv_frg_contact_info_phone;
    private TextView tv_frg_contact_info_pre;
    private TextView tv_frg_contact_info_next;
    private static TextView textView;
    public static String toHospitalAddress = null;
    private String fromHospitalAddress = null;
    public static String startLocation;
    public static String endLocation;

    private TextView tv_person;

    private String hospital;

    public Opo mOpo = null;
    public static int REQUEST_CODE = 1001;
    public static int RESULT_CODE = 1002;
    public static int RESULT_CODE_TRANSFER = 1003;
    public static int REQUEST_CODE_CONTACT = 1004;
    public static int REQUEST_CODE_OPO = 1005;
    private AlertDialog.Builder mAlertDialog;
    private TextView tv_opo_person;

    //OPO联系人
    private String mOpoContactName;
    private String mOpoContactPhone;

    //科室协调员
    private String mDoctorPhone;

    private TextView tv_contact_name;


    //单个OPO信息
    private Opo mOpoInfo;
    OpoInfoContactPopup mOpoInfoContactPopup;
    private String mHintContactPerson;
    private String mHintContactPhone;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.frg_contact_info, container, false);

        tv_frg_contact_info_opo = (TextView) root.findViewById(R.id.tv_frg_contact_info_opo);
        ll_frg_contact_info_contact = (LinearLayout) root.findViewById(R.id.ll_frg_contact_info_contact);
        tv_frg_contact_info_person = (TextView) root.findViewById(R.id.tv_frg_contact_info_person);
        tv_frg_contact_info_phone = (TextView) root.findViewById(R.id.tv_frg_contact_info_phone);
        tv_frg_contact_info_pre = (TextView) root.findViewById(R.id.tv_frg_contact_info_pre);
        tv_frg_contact_info_next = (TextView) root.findViewById(R.id.tv_frg_contact_info_next);
        tv_contact_name = (TextView) root.findViewById(R.id.tv_contact_name);
        tv_opo_person = (TextView) root.findViewById(R.id.tv_opo_person);
        tv_person = (TextView) root.findViewById(R.id.tv_person);

        tv_frg_contact_info_opo.setOnClickListener(this);
        ll_frg_contact_info_contact.setOnClickListener(this);
        tv_frg_contact_info_pre.setOnClickListener(this);
        tv_frg_contact_info_next.setOnClickListener(this);
        tv_opo_person.setOnClickListener(this);
        tv_frg_contact_info_person.setOnClickListener(this);
        tv_contact_name.setOnClickListener(this);

        hospital = SharePreUtils.getString("hospital", "", getActivity());
//        if (objBean.getContactPhone() == null || "".equals(objBean.getContactPhone())) {
//            getOpoList(SharePreUtils.getString("phone", "", getActivity()), objBean.getOrganSeg());
//        }
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        tv_frg_contact_info_next.setText("预览");

    }

    /**
     * 根据医院名称获取opo组织的
     *
     * @param hospital
     */
    private void loadOpoInfo(String hospital, final String flag, final View view) {
        RequestParams params = new RequestParams(URL.OPO);
        params.addBodyParameter("action", "opo");
        params.addBodyParameter("hospital", hospital);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                OpoInfoJson opo = new Gson().fromJson(result, OpoInfoJson.class);
                if (opo != null && opo.getResult() == CONSTS.SEND_OK) {
                    mOpoInfo = opo.getObj();
                    if ("first".equals(flag)) {
                        mOpoContactName = mOpoInfo.getOpoInfoContacts().get(0).getContactName();
                        mOpoContactPhone = mOpoInfo.getOpoInfoContacts().get(0).getContactPhone();

                        tv_frg_contact_info_opo.setText(mOpoInfo.getName());
                        tv_opo_person.setText(mOpoContactName + " " + mOpoContactPhone);
                    } else if ("contactName".equals(flag)) {

                        mOpoInfoContactPopup = new OpoInfoContactPopup(getActivity(), mOpoInfo.getOpoInfoContacts());
                        mOpoInfoContactPopup.showPopupWindow(view);
                        mOpoInfoContactPopup.setOnClickChangeListener(new OpoInfoContactPopup.OnClickChangeListener() {
                            @Override
                            public void OnClickChange(int position) {
                                mOpoContactName = mOpoInfo.getOpoInfoContacts().get(position).getContactName();
                                mOpoContactPhone = mOpoInfo.getOpoInfoContacts().get(position).getContactPhone();
                                tv_opo_person.setText(mOpoContactName + " " + mOpoContactPhone);
                                SharePreUtils.putString("opoContactName", mOpoContactName, getActivity());
                                SharePreUtils.putString("opoContactPhone", mOpoContactPhone, getActivity());
                                mOpoInfoContactPopup.dismiss();
                            }
                        });
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

    @Override
    public void initData() {

        if (CONSTS.TRANSFER_STATUS == 0) {
            textView.setText("新建转运(4/4)");
        } else if (CONSTS.TRANSFER_STATUS == 1) {
            if (objBean.getAutoTransfer() == CONSTS.AURO_TRANSFER_FINISH_NO) {
                textView.setText("完善资料(4/4)");
            } else {
                textView.setText("修改转运(4/4)");
            }

        }

        tv_frg_contact_info_opo.setText(hospital + "OPO");

        //选择上一次选择的转运人和协调员
        if (objBean.getTrueName() == null) {
            String transferContactName = SharePreUtils.getString("transferContactName", "", getActivity());
            String transferContactPhone = SharePreUtils.getString("transferContactPhone", "", getActivity());
            String trueUrl = SharePreUtils.getString("transferContactUrl", "", getActivity());

            if (!"".equals(transferContactName)) {
                tv_frg_contact_info_person.setText(transferContactName + " " + transferContactPhone);
                objBean.setTrueUrl(trueUrl);
            }else{
                String photoFile = SharePreUtils.getString("photoFile", "", getActivity());
                String wechatUrl = SharePreUtils.getString("wechatUrl", "",getActivity());
                String flag = SharePreUtils.getString("flag", "", getActivity());
                String trueName = SharePreUtils.getString("trueName", "", getActivity());
                String phone = SharePreUtils.getString("phone", "", getActivity());
                tv_frg_contact_info_person.setText(trueName + " " + phone);
                if("0".equals(flag)){
                    objBean.setTrueUrl(wechatUrl);
                }else if("1".equals(flag)){
                    objBean.setTrueUrl(photoFile);
                }

            }
        } else {
            tv_frg_contact_info_person.setText(objBean.getTrueName() + " " + objBean.getPhone());
            objBean.setTrueUrl(objBean.getTrueUrl());
        }

        if (objBean.getContactName() == null) {
            String contactName = SharePreUtils.getString("contactName", "", getActivity());
            String contactPhone = SharePreUtils.getString("contactPhone", "", getActivity());
            String contactUrl = SharePreUtils.getString("contactUrl", "", getActivity());

            if (!"".equals(contactName)) {
                tv_contact_name.setText(contactName + " " + contactPhone);
                objBean.setContactUrl(contactUrl);
            } else {
                getOpoList(SharePreUtils.getString("phone", "", getActivity()), "");
            }
        } else {
            tv_contact_name.setText(objBean.getContactName() + " " + objBean.getContactPhone());
            objBean.setContactUrl(objBean.getContactUrl());
        }

        if (objBean.getOpoContactName() == null) {

            String opoName = SharePreUtils.getString("opoName", "", getActivity());
            String opoPhone = SharePreUtils.getString("opoPhone", "", getActivity());
            String opoUrl = SharePreUtils.getString("opoUrl", "", getActivity());
            if (!"".equals(opoName)) {
                tv_opo_person.setText(opoName + " " + opoPhone);
                objBean.setOpoContactUrl(opoUrl);
            } else {
                //获取单个OPO信息,第一次进来
                loadOpoInfo(hospital, "first", null);

            }

        } else {
            tv_opo_person.setText(objBean.getOpoContactName() + " " + objBean.getOpoContactPhone());
            objBean.setOpoContactUrl(objBean.getOpoContactUrl());
        }

        loadStartLocation();

        loadHospitalAddress(objBean.getToHospName());


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
                    loadEndLocation();
                    loadWeather();
                } else {

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
    private void loadStartLocation() {
        fromHospitalAddress = objBean.getTrueFromCity() == null ? objBean.getFromCity() : objBean.getTrueFromCity();
        LogUtil.e(TAG, objBean.getFromCity() + "getFromCity:" + URL.GAO_DE_LOCATION_URL + fromHospitalAddress);
        RequestParams params = new RequestParams(URL.GAO_DE_LOCATION_URL + fromHospitalAddress);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                LatiLongJson latiLongJson = new Gson().fromJson(result, LatiLongJson.class);

                if (latiLongJson != null && "1".equals(latiLongJson.getStatus())) {

                    if (latiLongJson.getGeocodes() != null && latiLongJson.getGeocodes().length > 0) {
                        startLocation = latiLongJson.getGeocodes()[0].getLocation();
                        LogUtil.e(TAG, "startLocation:" + startLocation);
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
    private void loadEndLocation() {

        RequestParams params = new RequestParams(URL.GAO_DE_LOCATION_URL + toHospitalAddress);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                LatiLongJson latiLongJson = new Gson().fromJson(result, LatiLongJson.class);

                if (latiLongJson != null && "1".equals(latiLongJson.getStatus())) {

                    if (latiLongJson.getGeocodes() != null && latiLongJson.getGeocodes().length > 0) {
                        endLocation = latiLongJson.getGeocodes()[0].getLocation();
                        LogUtil.e(TAG, "endLocation:" + endLocation);
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

    /**
     * 获取天气
     */
    private void loadWeather() {
        RequestParams params = new RequestParams(URL.WEATHER);
        params.addBodyParameter("action", "weather");
        params.addBodyParameter("weatherArea", toHospitalAddress);
        LogUtil.e(TAG, "weatherArea:" + toHospitalAddress);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e(TAG, "weatherResult:" + result);
                WeatherIconJson weatherIconJson = new Gson().fromJson(result, WeatherIconJson.class);
                if (weatherIconJson != null && weatherIconJson.getShowapi_res_code() == 0) {
                    try {
                        MainActivity.weather = weatherIconJson.getShowapi_res_body().getNow().getWeather();
                        MainActivity.weatherUrl = weatherIconJson.getShowapi_res_body().getNow().getWeather_pic();
                        MainActivity.temperature = weatherIconJson.getShowapi_res_body().getNow().getTemperature();

                    } catch (Exception e) {

                    }

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(TAG, "weatherError:" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    public static ContactInfoFragment newIntance(ViewPager viewPage, TextView t) {
        ContactInfoFragment contactInfoFragment = new ContactInfoFragment();
        mViewPager = viewPage;
        textView = t;
        return contactInfoFragment;
    }


    private void loadOpo(final View v) {
        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "getOpoList");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                OpoJson opoJson = new Gson().fromJson(result, OpoJson.class);
                if (opoJson != null && opoJson.getResult() == CONSTS.SEND_OK) {
                    if (opoJson.getObj() != null) {
                        final ArrayList<String> arrayList = new ArrayList<>();
                        for (int i = 0; i < opoJson.getObj().size(); i++) {
                            arrayList.add(opoJson.getObj().get(i).getName());
                        }
                        final OrganTypePopup organTypePopup = new OrganTypePopup(getActivity(), arrayList);
                        organTypePopup.showAsDropDown(v);
                        organTypePopup.setOnClickChangeListener(new OrganTypePopup.OnClickChangeListener() {
                            @Override
                            public void OnClickChange(int position) {
                                SharePreUtils.putString("opoName", arrayList.get(position), getActivity());
                                tv_frg_contact_info_opo.setText(arrayList.get(position));
                            }
                        });
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CODE_TRANSFER) {
            // ToastUtil.showToast("gg", getActivity());

            String transferContactName = SharePreUtils.getString("transferContactName", "", getActivity());
            String transferContactPhone = SharePreUtils.getString("transferContactPhone", "", getActivity());
            String trueUrl = SharePreUtils.getString("transferContactUrl", "", getActivity());
            objBean.setTrueName(transferContactName);
            objBean.setPhone(transferContactPhone);
            objBean.setTrueUrl(trueUrl);
            tv_frg_contact_info_person.setText(transferContactName + " " + transferContactPhone);

            //tv_frg_contact_info_phone.setText(transferContactPhone);
            //ToastUtil.showToast("bb", getContext());
            LogUtil.e(TAG, "opoUrl:" + objBean.getOpoContactUrl());

        } else if (requestCode == REQUEST_CODE_CONTACT) {

            String contactName = SharePreUtils.getString("contactName", "", getActivity());
            String contactPhone = SharePreUtils.getString("contactPhone", "", getActivity());

            String contactUrl = SharePreUtils.getString("contactUrl", "", getActivity());

            objBean.setContactName(contactName);
            objBean.setContactPhone(contactPhone);
            objBean.setContactUrl(contactUrl);

            if (!"".equals(contactName)) {
                tv_contact_name.setText(contactName + " " + contactPhone);

            }

        } else if (requestCode == REQUEST_CODE_OPO) {

            String opoName = SharePreUtils.getString("opoName", "", getActivity());
            String opoPhone = SharePreUtils.getString("opoPhone", "", getActivity());

            String opoUrl = SharePreUtils.getString("opoUrl", "", getActivity());

            objBean.setOpoContactName(opoName);
            objBean.setOpoContactPhone(opoPhone);
            objBean.setOpoContactUrl(opoUrl);

            if (!"".equals(opoName)) {
                tv_opo_person.setText(opoName + " " + opoPhone);
//                tv_frg_contact_info_person.setText(contactName + " " + contactPhone);
//                tv_frg_contact_info_phone.setText(contactPhone);
            }

        }
        /**
         * 设置opo名称
         * 弹出联系人选择框
         */
        else if (resultCode == RESULT_CODE) {

            mOpo = (Opo) data.getSerializableExtra("opo");


            mOpoContactName = mOpo.getOpoInfoContacts().get(0).getContactName();
            mOpoContactPhone = mOpo.getOpoInfoContacts().get(0).getContactPhone();

            tv_frg_contact_info_opo.setText(mOpo.getName());
            tv_opo_person.setText(mOpoContactName + " " + mOpoContactPhone);

            SharePreUtils.putString("opoInfoName", mOpo.getName(), getActivity());
            SharePreUtils.putString("opoContactName", mOpoContactName, getActivity());
            SharePreUtils.putString("opoContactPhone", mOpoContactPhone, getActivity());

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.tv_frg_contact_info_opo:
                //loadOpo(view);
                Intent opoIntent = new Intent(getActivity(), HospitalOpoChoseActivity.class);
                startActivityForResult(opoIntent, REQUEST_CODE);
                break;

            //转运人员
            case R.id.tv_frg_contact_info_person:
                Intent intentPerson = new Intent(getActivity(), ContactPersonActivity.class);
                intentPerson.putExtra("type", "transfer");
                intentPerson.putExtra("roleId", roleId);
                startActivityForResult(intentPerson, RESULT_CODE_TRANSFER);
                break;

            //科室协调员
            case R.id.tv_contact_name:
                Intent intentContact = new Intent(getActivity(), ContactPersonActivity.class);
                intentContact.putExtra("type", "contact");
                intentContact.putExtra("roleId", roleId);
                startActivityForResult(intentContact, REQUEST_CODE_CONTACT);
                break;

            //OPO人员
            case R.id.tv_opo_person:
                Intent intentOpo = new Intent(getActivity(), ContactPersonActivity.class);
                intentOpo.putExtra("type", "opo");
                intentOpo.putExtra("roleId", roleId);
                startActivityForResult(intentOpo, REQUEST_CODE_OPO);
                break;

            case R.id.ll_frg_contact_info_contact:
                Intent intent = new Intent(getActivity(), ContactPersonActivity.class);
                intent.putExtra("type", "contact");
                intent.putExtra("roleId", roleId);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.tv_frg_contact_info_pre:
                mViewPager.setCurrentItem(2);
                break;

            case R.id.tv_frg_contact_info_next:
                String opoHospitalName = tv_frg_contact_info_opo.getText().toString().trim();
                String contactPerson = tv_contact_name.getText().toString().trim();
                String transferName = tv_frg_contact_info_person.getText().toString().trim();
                String opoName = tv_opo_person.getText().toString().trim();

                if ("".equals(transferName)) {
                    ToastUtil.showToast("请选择转运人", getActivity());
                } else if ("".equals(contactPerson)) {
                    ToastUtil.showToast("请选择科室协调员", getActivity());
                } else if ("".equals(opoHospitalName)) {
                    ToastUtil.showToast("请选择OPO人员", getActivity());
                } else {

                    objBean.setOpoName(opoHospitalName);

                    objBean.setTrueName(transferName.split(" ")[0]);
                    objBean.setPhone(transferName.split(" ")[1]);

                    objBean.setContactName(contactPerson.split(" ")[0]);
                    objBean.setContactPhone(contactPerson.split(" ")[1]);

                    objBean.setOpoContactName(opoName.split(" ")[0]);
                    objBean.setOpoContactPhone(opoName.split(" ")[1]);


                    mViewPager.setCurrentItem(4);
                }
                break;

        }
    }

    private void getOpoList(String phone, String organSeg) {
        RequestParams params = new RequestParams(URL.CONTACT);
        params.addBodyParameter("action", "getContactOpoList");
        params.addBodyParameter("phone", phone);
        params.addBodyParameter("organSeg", organSeg);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ContactListJson json = new Gson().fromJson(result, ContactListJson.class);
                if (json != null && json.getResult() == CONSTS.SEND_OK && json.getObj().size() > 0) {

                    String contactName = json.getObj().get(0).getTrueName();
                    String contactPhone = json.getObj().get(0).getContactPhone();
                    String flag = json.getObj().get(0).getIsUploadPhoto();
                    String contactUrl = "";
                    if ("0".equals(flag)) {
                        contactUrl = json.getObj().get(0).getWechatUrl();
                    } else if ("1".equals(flag)) {
                        contactUrl = json.getObj().get(0).getPhotoFile();
                    }


                    SharePreUtils.putString("contactName", contactName, getActivity());
                    SharePreUtils.putString("contactPhone", contactPhone, getActivity());
                    SharePreUtils.putString("contactUrl", contactUrl, getActivity());

                    objBean.setContactName(contactName);
                    objBean.setContactPhone(contactPhone);
                    objBean.setContactUrl(contactUrl);

                    if (!"".equals(contactName)) {
                        tv_contact_name.setText(contactName + " " + contactPhone);

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
