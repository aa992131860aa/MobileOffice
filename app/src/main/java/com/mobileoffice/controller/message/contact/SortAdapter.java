package com.mobileoffice.controller.message.contact;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobileoffice.R;
import com.mobileoffice.controller.cloud_monitor.PreviewInfoActivity;
import com.mobileoffice.controller.new_monitor.ContactInfoFragment;
import com.mobileoffice.json.ContactListJson;
import com.mobileoffice.json.ContactSearchJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.view.RoundImageView;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SortAdapter extends BaseAdapter {
    private Context context;
    private List<ContactListJson.ObjBean> persons;
    private LayoutInflater inflater;
    private String type;


    public SortAdapter(Context context, List<ContactListJson.ObjBean> persons) {
        this.context = context;
        this.persons = persons;
        this.inflater = LayoutInflater.from(context);

    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return persons.size();
    }

    public void refres(List<ContactListJson.ObjBean> objBeens, String type) {
        this.persons = objBeens;
        this.type = type;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return persons.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewholder = null;
        final ContactListJson.ObjBean person = persons.get(position);
        if (convertView == null) {
            viewholder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_item, null);
            viewholder.tv_tag = (TextView) convertView
                    .findViewById(R.id.tv_lv_item_tag);
            viewholder.tv_name = (TextView) convertView
                    .findViewById(R.id.tv_lv_item_name);
            viewholder.view_lv_item_line = convertView.findViewById(R.id.view_lv_item_line);
            viewholder.iv_lv_item_head = (RoundImageView) convertView.findViewById(R.id.iv_lv_item_head);
            viewholder.cb_list_item = (CheckBox) convertView.findViewById(R.id.cb_list_item);
            viewholder.rl_list_item = (RelativeLayout) convertView.findViewById(R.id.rl_list_item);
            convertView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }
        // 获取首字母的assii值
        int selection = person.getFirstPinYin().charAt(0);
        // 通过首字母的assii值来判断是否显示字母
        int positionForSelection = getPositionForSelection(selection);
        if (position == positionForSelection) {// 相等说明需要显示字母
            viewholder.tv_tag.setVisibility(View.VISIBLE);
            viewholder.tv_tag.setText(person.getFirstPinYin());
            viewholder.view_lv_item_line.setVisibility(View.GONE);
        } else {
            viewholder.tv_tag.setVisibility(View.GONE);
            viewholder.view_lv_item_line.setVisibility(View.VISIBLE);

        }
        viewholder.tv_name.setText(person.getTrueName());
        if ("0".equals(person.getIsUploadPhoto()) && person.getWechatUrl() != null && !"".equals(person.getWechatUrl())) {
            Picasso.with(context).load(person.getWechatUrl()).error(R.drawable.msg_2list_linkman).into(viewholder.iv_lv_item_head);
        } else if ("1".equals(person.getIsUploadPhoto()) && person.getPhotoFile() != null && !"".equals(person.getPhotoFile())) {
            Picasso.with(context).load(person.getPhotoFile()).error(R.drawable.msg_2list_linkman).into(viewholder.iv_lv_item_head);
        }
        if ("add".equals(type)) {
            viewholder.cb_list_item.setVisibility(View.VISIBLE);
            //viewholder.cb_list_item.setClickable(false);
            //viewholder.cb_list_item.setChecked(true);
            final ViewHolder finalViewholder = viewholder;
            viewholder.cb_list_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (finalViewholder.cb_list_item.isChecked()) {
                        CONSTS.CONTACT_POSITION.add(position);
                    } else {
                        CONSTS.CONTACT_POSITION.remove((Integer) position);
                    }

                }
            });

        } else if ("addAct".equals(type)) {
            viewholder.cb_list_item.setVisibility(View.VISIBLE);
            //viewholder.cb_list_item.setClickable(false);
            //viewholder.cb_list_item.setChecked(true);
            final ViewHolder finalViewholder = viewholder;
            viewholder.cb_list_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (finalViewholder.cb_list_item.isChecked()) {
                        CONSTS.CONTACT_POSITION.add(position);
                    } else {
                        CONSTS.CONTACT_POSITION.remove((Integer) position);
                    }


                }
            });

        } else if ("addActTransfer".equals(type)) {
            viewholder.cb_list_item.setVisibility(View.GONE);



        } else if ("minus".equals(type)) {
            viewholder.cb_list_item.setVisibility(View.VISIBLE);
            final ViewHolder finalViewholder = viewholder;
            viewholder.cb_list_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (finalViewholder.cb_list_item.isChecked()) {
                        CONSTS.CONTACT_POSITION.add(position);
                    } else {
                        CONSTS.CONTACT_POSITION.remove((Integer) position);
                    }

                    //ToastUtil.showToast("gg"+ finalViewholder.cb_list_item.isChecked(),context);
                }
            });
        } else if ("minusAct".equals(type)) {
            viewholder.cb_list_item.setVisibility(View.VISIBLE);
            final ViewHolder finalViewholder = viewholder;
            viewholder.cb_list_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (finalViewholder.cb_list_item.isChecked()) {
                        CONSTS.CONTACT_POSITION.add(position);
                    } else {
                        CONSTS.CONTACT_POSITION.remove((Integer) position);
                    }

                    //ToastUtil.showToast("gg"+ finalViewholder.cb_list_item.isChecked(),context);
                }
            });
        } else {
            viewholder.cb_list_item.setVisibility(View.GONE);
        }
        final ViewHolder checkViewHolder = viewholder;
        viewholder.rl_list_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (type != null && "transfer".equals(type)) {
                    String contactName = person.getTrueName();
                    String contactPhone = person.getContactPhone();


                    SharePreUtils.putString("transferContactName", contactName, context);
                    SharePreUtils.putString("transferContactPhone", contactPhone, context);
                    if ("0".equals(person.getIsUploadPhoto())) {
                        SharePreUtils.putString("transferContactUrl", person.getWechatUrl(), context);
                    } else if ("1".equals(person.getIsUploadPhoto())) {
                        SharePreUtils.putString("transferContactUrl", person.getPhotoFile(), context);

                    }


                    SharePreUtils.putString("contactUsersId", person.getUsersId(), context);

                    ((Activity) context).setResult(ContactInfoFragment.RESULT_CODE_TRANSFER);
                    ((Activity) context).finish();
                } else if (type != null && "contact".equals(type)) {
                    String contactName = person.getTrueName();
                    String contactPhone = person.getContactPhone();


                    SharePreUtils.putString("contactName", contactName, context);
                    SharePreUtils.putString("contactPhone", contactPhone, context);
                    if ("0".equals(person.getIsUploadPhoto())) {
                        SharePreUtils.putString("contactUrl", person.getWechatUrl(), context);
                    } else if ("1".equals(person.getIsUploadPhoto())) {
                        SharePreUtils.putString("contactUrl", person.getPhotoFile(), context);
                    }


                    SharePreUtils.putString("contactUsersId", person.getUsersId(), context);

                    ((Activity) context).setResult(ContactInfoFragment.REQUEST_CODE_CONTACT);
                    ((Activity) context).finish();
                } else if (type != null && "opo".equals(type)) {
                    String contactName = person.getTrueName();
                    String contactPhone = person.getContactPhone();


                    SharePreUtils.putString("opoName", contactName, context);
                    SharePreUtils.putString("opoPhone", contactPhone, context);
                    if ("0".equals(person.getIsUploadPhoto())) {
                        SharePreUtils.putString("opoUrl", person.getWechatUrl(), context);
                    } else if ("1".equals(person.getIsUploadPhoto())) {
                        SharePreUtils.putString("opoUrl", person.getPhotoFile(), context);
                    }


                    SharePreUtils.putString("contactUsersId", person.getUsersId(), context);

                    ((Activity) context).setResult(ContactInfoFragment.REQUEST_CODE_OPO);
                    ((Activity) context).finish();
                } else {
                    if ("addActTransfer".equals(type)) {
                        Intent intent = new Intent();
                        String contactName = person.getTrueName();
                        String contactPhone = person.getContactPhone();
                        String contactUrl = "";


                        if ("0".equals(person.getIsUploadPhoto())) {
                            contactUrl = person.getWechatUrl();
                        } else if ("1".equals(person.getIsUploadPhoto())) {
                            contactUrl = person.getPhotoFile();
                        }
//                        SharePreUtils.putString("transferName", contactName, context);
//                        SharePreUtils.putString("transferPhone", contactPhone, context);
//                        SharePreUtils.putString("transferUrl", contactUrl, context);

                        intent.putExtra("transferName",contactName);
                        intent.putExtra("transferPhone",contactPhone);
                        intent.putExtra("transferUrl",contactUrl);


                        ((Activity) context).setResult(PreviewInfoActivity.PRE_TRANSFER,intent);
                        ((Activity) context).finish();
                    } else {

                        if (checkViewHolder.cb_list_item.getVisibility() == View.VISIBLE) {
                            if (checkViewHolder.cb_list_item.isChecked()) {
                                checkViewHolder.cb_list_item.setChecked(false);
                                CONSTS.CONTACT_POSITION.remove((Integer) position);
                            } else {
                                checkViewHolder.cb_list_item.setChecked(true);
                                CONSTS.CONTACT_POSITION.add(position);
                            }


                        } else {
                            Intent intent = new Intent(context, ContactPersonInfoActivity.class);
                            //intent.putExtra("type","sortAdapter");
                            ContactSearchJson.ObjBean pObjBean = new ContactSearchJson.ObjBean();
                            pObjBean.setBind(SharePreUtils.getString("bind", "", context));
                            pObjBean.setName(SharePreUtils.getString("hospital", "", context));
                            pObjBean.setTrue_name(person.getTrueName());
                            pObjBean.setPhone(person.getContactPhone());
                            pObjBean.setOther_id(Integer.parseInt(person.getUsersId()));
                            pObjBean.setIs_upload_photo(person.getIsUploadPhoto());
                            pObjBean.setPhoto_url(person.getPhotoFile());
                            pObjBean.setWechat_url(person.getWechatUrl());
                            intent.putExtra("contactPerson", (Serializable) pObjBean);
                            context.startActivity(intent);
                        }
                    }
                }
            }
        });

        return convertView;
    }

    public int getPositionForSelection(int selection) {
        for (int i = 0; i < persons.size(); i++) {
            String Fpinyin = persons.get(i).getFirstPinYin();
            char first = Fpinyin.toUpperCase().charAt(0);
            if (first == selection) {
                return i;
            }
        }
        return -1;

    }

    class ViewHolder {
        TextView tv_tag;
        TextView tv_name;
        RoundImageView iv_lv_item_head;
        View view_lv_item_line;
        CheckBox cb_list_item;
        RelativeLayout rl_list_item;
    }

}
