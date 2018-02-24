package com.mobileoffice.controller.new_monitor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobileoffice.R;
import com.mobileoffice.controller.cloud_monitor.PreviewInfoActivity;
import com.mobileoffice.controller.message.contact.ContactPersonActivity;
import com.mobileoffice.controller.message.contact.ContactPersonInfoActivity;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.ContactListJson;
import com.mobileoffice.json.ContactSearchJson;
import com.mobileoffice.json.PostRoleJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;
import com.mobileoffice.view.PostRolePopup;
import com.mobileoffice.view.RoundImageView;
import com.squareup.picasso.Picasso;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 99213 on 2017/4/23.
 */

public class ContactAddAdapter extends RecyclerView.Adapter<ContactAddAdapter.MyViewHolder> {


    private static final String TAG = "ContactPersonAddAdapter";
    private List<ContactListJson.ObjBean> mObjBeans;
    private Context mContext;

    private TextView topLastView;
    //预览activity标志
    private String type = "";
    private String organSeg = "";
    private TextView mLongClickFlag;
    private int mAutoTransfer ;

    //自定义监听事件
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;


    public void refresh(List<ContactListJson.ObjBean> objBeens) {

        mObjBeans = objBeens;
        this.notifyDataSetChanged();
    }


    public void refresh(List<ContactListJson.ObjBean> objBeens, String organSeg) {
        this.organSeg = organSeg;
        mObjBeans = objBeens;
        this.notifyDataSetChanged();
    }
    public void refresh(List<ContactListJson.ObjBean> objBeens, String organSeg,TextView longClickFlag) {
        this.organSeg = organSeg;
        mObjBeans = objBeens;
        this.notifyDataSetChanged();
        mLongClickFlag = longClickFlag;
    }
    public void refresh(List<ContactListJson.ObjBean> objBeens, String organSeg,TextView longClickFlag,int autoTransfer) {
        this.organSeg = organSeg;
        mObjBeans = objBeens;
        this.notifyDataSetChanged();
        mLongClickFlag = longClickFlag;
        mAutoTransfer = autoTransfer;
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public ContactAddAdapter(List<ContactListJson.ObjBean> objBeens, Context context, String type) {
        mObjBeans = objBeens;
        mContext = context;
        this.type = type;
    }

    public ContactAddAdapter(List<ContactListJson.ObjBean> objBeens, Context context) {
        mObjBeans = objBeens;
        mContext = context;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.contact_add_item, parent, false);

        MyViewHolder holder = new MyViewHolder(view);

        return holder;

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        if (position == mObjBeans.size() + 1) {
//            if(mAutoTransfer==CONSTS.AURO_TRANSFER_FINISH_NO){
//                holder.riv_contact_add_item_photo.setVisibility(View.GONE);
//                holder.iv_contact_add_item_icon.setVisibility(View.GONE);
//                holder.tv_contact_add_item_name.setVisibility(View.GONE);
//            }else {
                //不是创建人无法删除组员
                if (mObjBeans.size() > 0) {
                    String phone = SharePreUtils.getString("phone", "", mContext);
                    LogUtil.e(TAG, "phone:" + phone + "," + mObjBeans.get(0).getContactPhone());
                    if (phone.equals(mObjBeans.get(0).getContactPhone())) {

                        holder.riv_contact_add_item_photo.setVisibility(View.VISIBLE);
                    } else if (phone.equals(mObjBeans.get(1).getContactPhone())) {
                        holder.riv_contact_add_item_photo.setVisibility(View.VISIBLE);

                    } else {
                        holder.riv_contact_add_item_photo.setVisibility(View.GONE);
                    }

                }
                holder.iv_contact_add_item_icon.setVisibility(View.GONE);
                holder.riv_contact_add_item_photo.setImageResource(R.drawable.cloud_3xinxi_minus);
                holder.tv_contact_add_item_name.setVisibility(View.GONE);
                //ToastUtil.showToast(holder.tv_contact_add_item_name.getHeight()+"",mContext);
                holder.riv_contact_add_item_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, ContactPersonActivity.class);
                        if ("type".equals(type)) {
                            intent.putExtra("type", "minusAct");

                            intent.putExtra("organSeg", organSeg);
                        } else {
                            intent.putExtra("type", "minus");
                        }
                        PreviewInfoActivity.FLAG = 0;
                        mContext.startActivity(intent);

                    }
                });
            //}
        } else if (position == mObjBeans.size()) {
            //只有两个成员
            if(mObjBeans.size()<=2){
//
//                holder.riv_contact_add_item_photo.setVisibility(View.GONE);
//                holder.iv_contact_add_item_icon.setVisibility(View.GONE);
//                holder.tv_contact_add_item_name.setVisibility(View.GONE);
                holder.iv_contact_add_item_icon.setVisibility(View.VISIBLE);
                holder.iv_contact_add_item_icon.setText("运");
                holder.tv_contact_add_item_name.setVisibility(View.GONE);
                holder.riv_contact_add_item_photo.setImageResource(R.drawable.cloud_3xinxi_add);
                holder.riv_contact_add_item_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       mOnItemClickListener.onItemClick(view,position);

                    }
                });


            }else {

                holder.iv_contact_add_item_icon.setVisibility(View.GONE);
                holder.riv_contact_add_item_photo.setImageResource(R.drawable.cloud_3xinxi_add);
                holder.tv_contact_add_item_name.setVisibility(View.GONE);
                holder.riv_contact_add_item_photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, ContactPersonActivity.class);
                        //是previewActivity传来的
                        if ("type".equals(type)) {
                            intent.putExtra("type", "addAct");
                            intent.putExtra("organSeg", organSeg);
                        } else {
                            intent.putExtra("type", "add");
                            intent.putExtra("organSeg", organSeg);
                        }
                        PreviewInfoActivity.FLAG = 0;
                        mContext.startActivity(intent);
                    }
                });
            }
        } else if (position == 0) {
            if(mAutoTransfer==CONSTS.AURO_TRANSFER_FINISH_NO&&mObjBeans.size()<=2){
                holder.iv_contact_add_item_icon.setText("协");
            }else {
                holder.iv_contact_add_item_icon.setText("运");
            }
            final String trueName = mObjBeans.get(position).getTrueName();
            final String flag = mObjBeans.get(position).getIsUploadPhoto();
            final String photoFile = mObjBeans.get(position).getPhotoFile();
            final String wechatUrl = mObjBeans.get(position).getWechatUrl();

            holder.tv_contact_add_item_name.setText(trueName);
            if ("0".equals(flag) && wechatUrl != null && !"".equals(wechatUrl)) {
                Picasso.with(mContext).load(wechatUrl).error(R.drawable.msg_2list_linkman).into(holder.riv_contact_add_item_photo);
            } else if ("1".equals(flag) && photoFile != null && !"".equals(photoFile)) {
                Picasso.with(mContext).load(photoFile).error(R.drawable.msg_2list_linkman).into(holder.riv_contact_add_item_photo);
            }

            holder.ll_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ContactPersonInfoActivity.class);

                    String pPhone = mObjBeans.get(position).getPhone();
                    String pContactPhone = mObjBeans.get(position).getContactPhone();

                    intent.putExtra("contactPhone", pContactPhone==null?pPhone:pContactPhone);

                    mContext.startActivity(intent);
                    PreviewInfoActivity.FLAG = 1;
                }
            });


        } else if (position == 1) {
            if(mAutoTransfer==CONSTS.AURO_TRANSFER_FINISH_NO&&mObjBeans.size()<=2){
                holder.iv_contact_add_item_icon.setText("OPO");
            }else {
                holder.iv_contact_add_item_icon.setText("协");
            }
            String trueName = mObjBeans.get(position).getTrueName();
            String flag = mObjBeans.get(position).getIsUploadPhoto();
            String photoFile = mObjBeans.get(position).getPhotoFile();
            String wechatUrl = mObjBeans.get(position).getWechatUrl();
            LogUtil.e(TAG,"obj:"+mObjBeans.get(position));
            holder.tv_contact_add_item_name.setText(trueName);
            if ("0".equals(flag) && wechatUrl != null && !"".equals(wechatUrl)) {
                Picasso.with(mContext).load(wechatUrl).error(R.drawable.msg_2list_linkman).into(holder.riv_contact_add_item_photo);
            } else if ("1".equals(flag) && photoFile != null && !"".equals(photoFile)) {
                Picasso.with(mContext).load(photoFile).error(R.drawable.msg_2list_linkman).into(holder.riv_contact_add_item_photo);
            }

            holder.ll_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ContactPersonInfoActivity.class);


                    String pPhone = mObjBeans.get(position).getPhone();
                    String pContactPhone = mObjBeans.get(position).getContactPhone();

                    intent.putExtra("contactPhone", pContactPhone==null?pPhone:pContactPhone);

                    mContext.startActivity(intent);
                    PreviewInfoActivity.FLAG = 1;
                }
            });
        }
        else if (position == 2) {
            holder.iv_contact_add_item_icon.setText("OPO");
            holder.iv_contact_add_item_icon.setBackgroundResource(R.drawable.two_team_border);
            String trueName = mObjBeans.get(position).getTrueName();
            holder.tv_contact_add_item_name.setText(trueName);
            String flag = mObjBeans.get(position).getIsUploadPhoto();
            String photoFile = mObjBeans.get(position).getPhotoFile();
            String wechatUrl = mObjBeans.get(position).getWechatUrl();
            if ("0".equals(flag) && wechatUrl != null && !"".equals(wechatUrl)) {
                Picasso.with(mContext).load(wechatUrl).error(R.drawable.msg_2list_linkman).into(holder.riv_contact_add_item_photo);
            } else if ("1".equals(flag) && photoFile != null && !"".equals(photoFile)) {
                Picasso.with(mContext).load(photoFile).error(R.drawable.msg_2list_linkman).into(holder.riv_contact_add_item_photo);
            }

            holder.ll_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ContactPersonInfoActivity.class);


                    String pPhone = mObjBeans.get(position).getPhone();
                    String pContactPhone = mObjBeans.get(position).getContactPhone();

                    intent.putExtra("contactPhone", pContactPhone==null?pPhone:pContactPhone);

                    mContext.startActivity(intent);
                    PreviewInfoActivity.FLAG = 1;
                }
            });
        }

        else {
            if(mObjBeans.get(position).getPostRole()==null){
                holder.iv_contact_add_item_icon.setVisibility(View.GONE);
            }
            else if("无".equals(mObjBeans.get(position).getPostRole().trim())){
                holder.iv_contact_add_item_icon.setVisibility(View.GONE);
            }else if(!"".equals(mObjBeans.get(position).getPostRole().trim())){
                holder.iv_contact_add_item_icon.setVisibility(View.VISIBLE);
                holder.iv_contact_add_item_icon.setBackgroundResource(R.drawable.two_team_border);
                holder.iv_contact_add_item_icon.setText(mObjBeans.get(position).getPostRole().trim());
            }else{
                holder.iv_contact_add_item_icon.setVisibility(View.GONE);
            }

            String trueName = mObjBeans.get(position).getTrueName();
            String flag = mObjBeans.get(position).getIsUploadPhoto();
            String photoFile = mObjBeans.get(position).getPhotoFile();
            String wechatUrl = mObjBeans.get(position).getWechatUrl();
            //ToastUtil.showToast("flag:"+flag+",photoFile:"+photoFile+",wechatUrl:"+wechatUrl,mContext);
            holder.tv_contact_add_item_name.setText(trueName);
            if ("0".equals(flag) && wechatUrl != null && !"".equals(wechatUrl)) {
                Picasso.with(mContext).load(wechatUrl).error(R.drawable.msg_2list_linkman).into(holder.riv_contact_add_item_photo);
            } else if ("1".equals(flag) && photoFile != null && !"".equals(photoFile)) {
                Picasso.with(mContext).load(photoFile).error(R.drawable.msg_2list_linkman).into(holder.riv_contact_add_item_photo);
            }
            holder.ll_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ContactPersonInfoActivity.class);

                    String pPhone = mObjBeans.get(position).getPhone();
                    String pContactPhone = mObjBeans.get(position).getContactPhone();

                    intent.putExtra("contactPhone", pContactPhone==null?pPhone:pContactPhone);

                    mContext.startActivity(intent);
                    PreviewInfoActivity.FLAG = 1;
                }
            });
            holder.ll_content.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    if(mLongClickFlag!=null&&"长按角色选择岗位角色".equals(mLongClickFlag.getText().toString())){
                        String pPhone = mObjBeans.get(position).getPhone();
                        String pContactPhone = mObjBeans.get(position).getContactPhone();
                        loadPostRoles(v,holder.iv_contact_add_item_icon, pContactPhone==null?pPhone:pContactPhone,mObjBeans.get(position).getPostRole(),position);

                    }else{
                        ToastUtil.showToast("科室协调员才有权限分配岗位角色",mContext);
                    }

                    return true;
                }
            });

        }

    }

    /**
     *
     * @param organSeg
     * @param phone
     * @param postRoleId
     * @param v
     * @param name
     */
    private void dealPostRoles(String organSeg, String phone, String postRoleId,final TextView v, final String name,final int position){
        RequestParams params = new RequestParams(URL.OPO);
        params.addBodyParameter("action","dealPostRole");
        params.addBodyParameter("organSeg",organSeg);
        params.addBodyParameter("phone",phone);
        params.addBodyParameter("postRoleId",postRoleId);

        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {

                v.setText(name.trim());
                mObjBeans.get(position).setPostRole(name);

                //ToastUtil.showToast("position:"+position,mContext);
                v.setBackgroundResource(R.drawable.two_team_border);
                if("无".equals(name.trim())){
                    v.setVisibility(View.GONE);
                }else{
                    v.setVisibility(View.VISIBLE);
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
     * 获取岗位角色
     */
    private void loadPostRoles(final View v,final TextView textView,final String phone,final String postRole,final int pPosition){
        RequestParams params = new RequestParams(URL.OPO);
        params.addBodyParameter("action","getPostRoles");
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {

                final PostRoleJson postRoleJson = new Gson().fromJson(result,PostRoleJson.class);
                if(postRoleJson!=null&&postRoleJson.getResult()== CONSTS.SEND_OK){

                    final PostRolePopup postRolePopup =    new PostRolePopup((Activity)mContext,postRoleJson.getObj(),postRole);
                    postRolePopup.setOnClickChangeListener(new PostRolePopup.OnClickChangeListener() {
                        @Override
                        public void OnClickChange(int position) {
                               dealPostRoles(organSeg,phone,postRoleJson.getObj().get(position).getPostRoleId()+"",textView,postRoleJson.getObj().get(position).getPostRole(),pPosition);
                              postRolePopup.dismiss();
                        }
                    });
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        postRolePopup.showPopupWindow(v);
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
    public int getItemCount() {

        return mObjBeans.size() + 2;


    }


    //自定义ViewHolder
    class MyViewHolder extends RecyclerView.ViewHolder {
        //footer
        RoundImageView riv_contact_add_item_photo;
        TextView iv_contact_add_item_icon;
        TextView tv_contact_add_item_name;
        LinearLayout ll_content;

        public MyViewHolder(View view) {
            super(view);
            riv_contact_add_item_photo = (RoundImageView) view.findViewById(R.id.riv_contact_add_item_photo);
            iv_contact_add_item_icon = (TextView) view.findViewById(R.id.iv_contact_add_item_icon);
            tv_contact_add_item_name = (TextView) view.findViewById(R.id.tv_contact_add_item_name);
            ll_content = (LinearLayout) view.findViewById(R.id.ll_content);
        }
    }

}
