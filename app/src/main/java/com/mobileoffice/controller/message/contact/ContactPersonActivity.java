package com.mobileoffice.controller.message.contact;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobileoffice.R;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.controller.new_monitor.NewMonitorBaseFragment;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.ContactListJson;
import com.mobileoffice.json.PhotoJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;
import com.squareup.picasso.Picasso;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by 99213 on 2017/6/21.
 */

public class ContactPersonActivity extends BaseActivity implements View.OnClickListener {
    private ListView listView;
    private SortAdapter sortadapter;
    private String TAG = "ContactPersonActivity";

    private SideBar sidebar;
    private TextView dialog;
    private LinearLayout ll_contact_person_no;
    private LinearLayout ll_contact_person_back;
    private LinearLayout ll_contact_person_add;
    private String phone;
    private RelativeLayout rl_contact_person_title;
    List<ContactListJson.ObjBean> mObjBeans = new ArrayList<>();
    private String type = null;
    private List<ContactListJson.ObjBean> selectObjBeans = new ArrayList<>();

    //标题
    private TextView tv_contact_person_title;
    private TextView tv_contact_person_finish;
    private ImageView iv_contact_person_add;

    @Override
    protected void initVariable() {

        phone = SharePreUtils.getString("phone", "", this);
        if (getIntent().getExtras() != null) {
            type = getIntent().getExtras().getString("type");
        }
        //ToastUtil.showToast("type:"+type,this);
        if (type != null && "contact".equals(type)) {
            ll_contact_person_add.setVisibility(View.GONE);
        } else if (type != null && "add".equals(type)) {
            iv_contact_person_add.setVisibility(View.GONE);
            tv_contact_person_finish.setVisibility(View.VISIBLE);
            tv_contact_person_title.setText("添加成员");
        } else if (type != null && "addAct".equals(type)) {
            iv_contact_person_add.setVisibility(View.GONE);
            tv_contact_person_finish.setVisibility(View.VISIBLE);
            tv_contact_person_title.setText("添加成员");
        } else if (type != null && "minus".equals(type)) {
            iv_contact_person_add.setVisibility(View.GONE);
            tv_contact_person_finish.setVisibility(View.VISIBLE);
            tv_contact_person_title.setText("删除成员");
        } else if (type != null && "minusAct".equals(type)) {
            iv_contact_person_add.setVisibility(View.GONE);
            tv_contact_person_finish.setVisibility(View.VISIBLE);
            tv_contact_person_title.setText("删除成员");
        } else if (type != null && "contactList".equals(type)) {
            ll_contact_person_add.setVisibility(View.GONE);
            tv_contact_person_title.setText("团队信息");
        }
        loadContactList(type);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


            }
        });
    }

    private void show() {
        ll_contact_person_no.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        rl_contact_person_title.setVisibility(View.VISIBLE);
        sidebar.setVisibility(View.VISIBLE);
    }

    private void show(int size) {
        if (size > 0) {
            ll_contact_person_no.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            sidebar.setVisibility(View.VISIBLE);
        } else {
            ll_contact_person_no.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            sidebar.setVisibility(View.GONE);
        }
    }

    private void hide() {
        ll_contact_person_no.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        sidebar.setVisibility(View.GONE);

    }

    @Override
    protected void onStart() {
        super.onStart();

        //loadContactList(type);
    }

    private void loadContactList(final String type) {

        if ("minus".equals(type)) {

            List<ContactListJson.ObjBean> objBeens = CONSTS.CONTACT_LIST;
            List<ContactListJson.ObjBean> NewObjBeens = getData(objBeens);
            mObjBeans = NewObjBeens;
            Collections.sort(mObjBeans, new PinyinComparator());
            sortadapter.refres(mObjBeans, type);
            show(mObjBeans.size());
            //show();
        } else if ("minusAct".equals(type)) {

            List<ContactListJson.ObjBean> objBeens = CONSTS.CONTACT_LIST;
            List<ContactListJson.ObjBean> NewObjBeens = getData(objBeens);
            mObjBeans = NewObjBeens;
            Collections.sort(mObjBeans, new PinyinComparator());
            sortadapter.refres(mObjBeans, type);
            show(mObjBeans.size());
        } else if ("contactList".equals(type)) {

            List<ContactListJson.ObjBean> objBeens = (List<ContactListJson.ObjBean>) getIntent().getSerializableExtra("contactList");
            List<ContactListJson.ObjBean> NewObjBeens = getData(objBeens);

            mObjBeans = NewObjBeens;
            Collections.sort(mObjBeans, new PinyinComparator());
            selectObjBeans = mObjBeans;
            sortadapter.refres(mObjBeans, type);
            show(mObjBeans.size());
        } else {
            String organSeg = getIntent().getStringExtra("organSeg");

            showWaitDialog(getResources().getString(R.string.loading), false, "load");
            RequestParams params = new RequestParams(URL.CONTACT);
            params.addBodyParameter("action", "getContactList");
            params.addBodyParameter("phone", phone);
            params.addBodyParameter("organSeg", organSeg);
            x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    //LogUtil.e(TAG, "result:" + result);
                    ContactListJson json = new Gson().fromJson(result, ContactListJson.class);
                    if (json != null && json.getResult() == CONSTS.SEND_OK) {
                        if (json.getObj() != null) {
                            List<ContactListJson.ObjBean> objBeens = json.getObj();
                            List<ContactListJson.ObjBean> newObjBeens = new ArrayList<>();
                            if ("contact".equals(type)||"transfer".equals(type)||"opo".equals(type)||"addActTransfer".equals(type)) {
                                int roleId = getIntent().getIntExtra("roleId", -1);
                                ContactListJson.ObjBean myObjBeens = new ContactListJson.ObjBean();
                                String photoFile = SharePreUtils.getString("photoFile", "", ContactPersonActivity.this);
                                String wechatUrl = SharePreUtils.getString("wechatUrl", "",ContactPersonActivity.this);
                                String flag = SharePreUtils.getString("flag", "", ContactPersonActivity.this);
                                String trueName = SharePreUtils.getString("trueName", "", ContactPersonActivity.this);
                                String phone = SharePreUtils.getString("phone", "", ContactPersonActivity.this);

                                myObjBeens.setTrueName(trueName);
                                myObjBeens.setIsUploadPhoto(flag);
                                myObjBeens.setContactPhone(phone);
                                myObjBeens.setWechatUrl(wechatUrl);
                                myObjBeens.setPhotoFile(photoFile);
                                newObjBeens.add(myObjBeens);

                                if (roleId == CONSTS.ROLE_DOCTOR) {

                                    for (int i = 0; i < objBeens.size(); i++) {

                                        //修改为不过滤
                                       // if (objBeens.get(i).getRoleId() == CONSTS.ROLE_OPO) {
                                            newObjBeens.add(objBeens.get(i));
                                        //}
                                    }
                                } else if (roleId == CONSTS.ROLE_OPO) {
                                    for (int i = 0; i < objBeens.size(); i++) {
                                        // LogUtil.e(TAG, "roleID:" + roleId + ",roleID2:" + objBeens.get(i).getRoleId());
                                        //修改为不过滤
                                        //if (objBeens.get(i).getRoleId() == CONSTS.ROLE_DOCTOR) {
                                            newObjBeens.add(objBeens.get(i));

                                       // }
                                    }
                                }else{
                                    for (int i = 0; i < objBeens.size(); i++) {
                                        // LogUtil.e(TAG, "roleID:" + roleId + ",roleID2:" + objBeens.get(i).getRoleId());
                                        //修改为不过滤
                                        //if (objBeens.get(i).getRoleId() == CONSTS.ROLE_DOCTOR) {
                                        newObjBeens.add(objBeens.get(i));

                                        // }
                                    }
                                }
                            } else {
                                newObjBeens = objBeens;

                            }

                            List<ContactListJson.ObjBean> NewObjBeens = getData(newObjBeens);


                            mObjBeans = NewObjBeens;
                            Collections.sort(mObjBeans, new PinyinComparator());
                            selectObjBeans = mObjBeans;
                            sortadapter.refres(mObjBeans, type);
                            //List<ContactListJson.ObjBean> b = mObjBeans
                            show(mObjBeans.size());
                        }
                    } else {
                        hide();
                    }
                    dismissDialog();
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.e(TAG,"ex:"+ex.getMessage());
                    hide();
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
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.contact_person);

        // TODO Auto-generated method stub
        sidebar = (SideBar) findViewById(R.id.sidebar);
        listView = (ListView) findViewById(R.id.listview);
        dialog = (TextView) findViewById(R.id.dialog);
        rl_contact_person_title = (RelativeLayout) findViewById(R.id.rl_contact_person_title);
        ll_contact_person_no = (LinearLayout) findViewById(R.id.ll_contact_person_no);
        ll_contact_person_back = (LinearLayout) findViewById(R.id.ll_contact_person_back);
        ll_contact_person_add = (LinearLayout) findViewById(R.id.ll_contact_person_add);
        tv_contact_person_title = (TextView) findViewById(R.id.tv_contact_person_title);
        tv_contact_person_finish = (TextView) findViewById(R.id.tv_contact_person_finish);
        iv_contact_person_add = (ImageView) findViewById(R.id.iv_contact_person_add);

        ll_contact_person_no.setOnClickListener(this);
        ll_contact_person_back.setOnClickListener(this);
        ll_contact_person_add.setOnClickListener(this);
        sidebar.setTextView(dialog);
        // 设置字母导航触摸监听
        sidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // TODO Auto-generated method stub
                // 该字母首次出现的位置
                int position = sortadapter.getPositionForSelection(s.charAt(0));

                if (position != -1) {
                    listView.setSelection(position);
                }
            }
        });
        //data = getData(getResources().getStringArray(R.array.listpersons));
        //data = new ArrayList<>();
        // 数据在放在adapter之前需要排序

        sortadapter = new SortAdapter(this, mObjBeans);
        listView.setAdapter(sortadapter);
    }

    @Override
    protected void initData() {

    }

    private List<ContactListJson.ObjBean> getData(List<ContactListJson.ObjBean> objBeens) {
        List<ContactListJson.ObjBean> selectArray = new ArrayList<>();
        //ToastUtil.showToast(type,this);
        //删除已经添加的人员
        if ("add".equals(type)) {

            CONSTS.CONTACT_POSITION = new ArrayList<>();
            int roleId = SharePreUtils.getInt("roleId", -1, this);
            String truePhone = NewMonitorBaseFragment.objBean.getPhone();
            String contactPhone = NewMonitorBaseFragment.objBean.getContactPhone();
            String opoContactPhone = NewMonitorBaseFragment.objBean.getOpoContactPhone();
//            if (roleId == CONSTS.ROLE_DOCTOR) {
//                contactPhone = SharePreUtils.getString("contactPhone", "", this);
//            } else if (roleId == CONSTS.ROLE_OPO) {
//                contactPhone = SharePreUtils.getString("transferContactPhone", "", this);
//            }



           // ToastUtil.showToast(CONSTS.CONTACT_LIST.size()+",", this);
            for (int j = 0; j < objBeens.size(); j++) {

                int flag = 1;
                String outPhone = objBeens.get(j).getContactPhone();

                //LogUtil.e(TAG, "contactName:" + objBeens.get(j).getTrueName() +","+ contactPhone +","+truePhone+","+opoContactPhone+ "," + outPhone);
                if (contactPhone.equals(outPhone)||truePhone.equals(outPhone)||opoContactPhone.equals(outPhone)) {
                    flag = 0;
                } else {
                    for (int i = 0; i < CONSTS.CONTACT_LIST.size(); i++) {
                        String innerPhone = CONSTS.CONTACT_LIST.get(i).getContactPhone();
                        LogUtil.e(TAG, "contactName:" + innerPhone + "," + outPhone);
                        if (outPhone.equals(innerPhone)) {

                            flag = 0;
                            break;
                        }
                    }
                }
                if (flag == 1) {
                    selectArray.add(objBeens.get(j));
                }
            }
            objBeens = selectArray;
            selectObjBeans = selectArray;
            //LogUtil.e(TAG, "select:" + selectArray.size());
        } else if ("addAct".equals(type)) {

            CONSTS.CONTACT_POSITION = new ArrayList<>();

            String contactPhone = CONSTS.CONTACT_LIST_PREVIEW.get(0).getPhone();
            String contactPhone1 = CONSTS.CONTACT_LIST_PREVIEW.get(1).getPhone();
            String contactPhone2 = CONSTS.CONTACT_LIST_PREVIEW.get(2).getPhone();
            //ToastUtil.showToast(contactPhone, this);
            for (int j = 0; j < objBeens.size(); j++) {

                int flag = 1;
                String outPhone = objBeens.get(j).getContactPhone();
                LogUtil.e(TAG, "outPhone:" + outPhone + "," + contactPhone + "," + contactPhone1+","+contactPhone2+",size:"+objBeens.size());
                if (contactPhone.equals(outPhone) || contactPhone1.equals(outPhone) || contactPhone2.equals(outPhone)) {
                    flag = 0;
                } else {
                    for (int i = 0; i < CONSTS.CONTACT_LIST.size(); i++) {
                        String innerPhone = CONSTS.CONTACT_LIST.get(i).getContactPhone();
                        //LogUtil.e(TAG, "contactName:" + innerPhone + "," + outPhone);
                        if (outPhone.equals(innerPhone)) {

                            flag = 0;
                            break;
                        }
                    }
                }
                if (flag == 1) {
                    selectArray.add(objBeens.get(j));
                }
            }
            objBeens = selectArray;
            selectObjBeans = selectArray;

        }
        List<ContactListJson.ObjBean> listarray = new ArrayList<>();
        for (int i = 0; i < objBeens.size(); i++) {

            String pinyin = PinyinUtils.getPingYin(objBeens.get(i).getTrueName());
            String Fpinyin = pinyin.substring(0, 1).toUpperCase();

            ContactListJson.ObjBean person = new ContactListJson.ObjBean();
            person.setTrueName(objBeens.get(i).getTrueName());
            person.setIsUploadPhoto(objBeens.get(i).getIsUploadPhoto());
            person.setPhotoFile(objBeens.get(i).getPhotoFile());
            person.setWechatUrl(objBeens.get(i).getWechatUrl());
            person.setPostRole(objBeens.get(i).getPostRole());
            person.setPinYin(pinyin);
            person.setContactPhone(objBeens.get(i).getContactPhone());
            person.setUsersId(objBeens.get(i).getUsersId());
            // LogUtil.e(TAG, "trueNamePin:" + objBeens.get(i).getTrueName()+ ",getIsUploadPhoto:" + person.getIsUploadPhoto() + ",getPhotoFile:" + person.getPhotoFile() + ",getWechatUrl:" + person.getWechatUrl() + ",getPinYin:" + person.getPinYin() + ",getContactPhone:" + person.getContactPhone() + ",getUsersId:" + person.getUsersId());
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

    private List<PersonBean> getData(String[] data) {
        List<PersonBean> listarray = new ArrayList<PersonBean>();
        for (int i = 0; i < data.length; i++) {
            String pinyin = PinyinUtils.getPingYin(data[i]);
            String Fpinyin = pinyin.substring(0, 1).toUpperCase();

            PersonBean person = new PersonBean();
            person.setName(data[i]);
            person.setPinYin(pinyin);
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


    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.ll_contact_person_back:
                finish();
                break;
            case R.id.ll_contact_person_add:

                if ("add".equals(type)) {
                    if (selectObjBeans.size() > 0) {
                        for (int i = 0; i < CONSTS.CONTACT_POSITION.size(); i++) {
                            CONSTS.CONTACT_LIST.add(selectObjBeans.get(CONSTS.CONTACT_POSITION.get(i)));
                            //LogUtil.e(TAG, selectObjBeans.get(CONSTS.CONTACT_POSITION.get(i)).getTrueName());
                        }
                    }
                    CONSTS.CONTACT_POSITION = new ArrayList<>();
                    finish();
                } else if ("addAct".equals(type)) {

                    String names = "";
                    String namePhones = "";
                    if (selectObjBeans.size() > 0) {

                        for (int i = 0; i < CONSTS.CONTACT_POSITION.size(); i++) {
                            CONSTS.CONTACT_LIST.add(selectObjBeans.get(CONSTS.CONTACT_POSITION.get(i)));
                            //LogUtil.e(TAG, selectObjBeans.get(CONSTS.CONTACT_POSITION.get(i)).getTrueName());

                            names += selectObjBeans.get(CONSTS.CONTACT_POSITION.get(i)).getTrueName();
                            namePhones += selectObjBeans.get(CONSTS.CONTACT_POSITION.get(i)).getContactPhone();
                            if (i == CONSTS.CONTACT_POSITION.size() - 1) {

                            } else {

                                names += ",";
                                namePhones += ",";
                            }
                        }
                    }
                    String organSeg = getIntent().getStringExtra("organSeg");
                    String usersIds = "";


                    usersIds += CONSTS.CONTACT_LIST_PREVIEW.get(0).getPhone() + ",";
                    usersIds += CONSTS.CONTACT_LIST_PREVIEW.get(1).getPhone() + ",";
                    usersIds += CONSTS.CONTACT_LIST_PREVIEW.get(2).getPhone() + ",";
                    for (int i = 0; i < CONSTS.CONTACT_LIST.size(); i++) {
                        usersIds += CONSTS.CONTACT_LIST.get(i).getContactPhone();

                        if (i == CONSTS.CONTACT_LIST.size() - 1) {

                        } else {
                            usersIds += ",";

                        }
                    }
                    getGroupName(organSeg, usersIds, names,namePhones);


                    CONSTS.CONTACT_POSITION = new ArrayList<>();

                    //加入群组
                    finish();
                } else if ("minus".equals(type)) {
                    getData(CONSTS.CONTACT_LIST);
                    //LogUtil.e(TAG,"list:"+CONSTS.CONTACT_LIST.get(0).getTrueName()+","+CONSTS.CONTACT_LIST.get(0).getPinYin());
                    Collections.sort(CONSTS.CONTACT_LIST, new PinyinComparator());
                    //ToastUtil.showToast(CONSTS.CONTACT_POSITION.get(0)+",size", this);
                    for (int i = CONSTS.CONTACT_POSITION.size() - 1; i >= 0; i--) {
                        int position = CONSTS.CONTACT_POSITION.get(i);
                        LogUtil.e(TAG,"position:"+position+",size:"+CONSTS.CONTACT_LIST.size());
                        try {
                            ContactListJson.ObjBean objBean = CONSTS.CONTACT_LIST.get(position);
                            CONSTS.CONTACT_LIST.remove(objBean);
                        }catch (Exception e){

                        }


                    }

                    CONSTS.CONTACT_POSITION = new ArrayList<>();
                    finish();
                } else if ("minusAct".equals(type)) {

                    // LogUtil.e(TAG,"list:"+CONSTS.CONTACT_LIST.get(0).getTrueName()+","+CONSTS.CONTACT_LIST.get(0).getPinYin());
                    Collections.sort(getData(CONSTS.CONTACT_LIST), new PinyinComparator());
                    //ToastUtil.showToast(CONSTS.CONTACT_POSITION.get(0)+",size", this);
                    String usersIds = "";

                    for (int i = CONSTS.CONTACT_POSITION.size() - 1; i >= 0; i--) {
                        int position = CONSTS.CONTACT_POSITION.get(i);
                       try {
                           ContactListJson.ObjBean objBean = CONSTS.CONTACT_LIST.get(position);
                           usersIds += objBean.getContactPhone() + ",";
                           CONSTS.CONTACT_LIST.remove(objBean);
                       }catch (Exception e){
                           ToastUtil.showToast("移除人员失败",this);
                       }

                    }
                    if (usersIds.contains(",")) {
                        usersIds = usersIds.substring(0, usersIds.length() - 1);
                    }
                    String organSeg = getIntent().getStringExtra("organSeg");
                    getGroupName(organSeg, usersIds, "","");
                    CONSTS.CONTACT_POSITION = new ArrayList<>();
                    finish();
                } else {
                    intent.setClass(this, ContactPersonAddActivity.class);
                    startActivity(intent);
                }
                break;


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
     * 通知云监控改变
     *
     * @param organSeg
     */
    private void noticeTransfer(String organSeg) {
        RequestParams params = new RequestParams(URL.PUSH);
        params.addBodyParameter("action", "sendPushTransfer");
        params.addBodyParameter("organSeg", organSeg);
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
    private void getGroupName(final String organSeg, final String usersIds, final String names,final String namePhones) {
        showWaitDialog(getResources().getString(R.string.loading), false, "load");
        RequestParams params = new RequestParams(URL.RONG);
        params.addBodyParameter("action", "getGroup");
        params.addBodyParameter("organSeg", organSeg);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {


                    addGroup(organSeg, photoJson.getMsg(), usersIds, names,namePhones);

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

    private void addGroup(final String organSeg, String groupName, String usersIds, final String names,final  String namesPhones) {

        RequestParams params = new RequestParams(URL.RONG);
        if ("addAct".equals(type)) {
            params.addBodyParameter("action", "addGroup");
        } else if ("minusAct".equals(type)) {
            params.addBodyParameter("action", "exitGroup");
        }
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
                    if ("addAct".equals(type)) {
                        String content = "我已邀请【" + names + "】加入转运组";
                        sendGroupMessage(organSeg, content);
                        String inviteContent = SharePreUtils.getString("trueName","",ContactPersonActivity.this)+" 邀请你加入器官段号为"+organSeg+"的转运团队，详情请至APP查看。";
                        sendTransferSms(namesPhones,inviteContent);

                    }
                    noticeTransfer(organSeg);
                } else {
                    ToastUtil.showToast("加入群组失败", ContactPersonActivity.this);
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
}
