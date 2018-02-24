package com.mobileoffice.controller.cloud_monitor;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobileoffice.R;
import com.mobileoffice.controller.message.contact.ContactPersonActivity;
import com.mobileoffice.json.ContactListJson;
import com.mobileoffice.view.RoundImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by 99213 on 2017/4/23.
 */

public class PreviewAddAdapter extends RecyclerView.Adapter<PreviewAddAdapter.MyViewHolder> {


    private static final String TAG = "ContactPersonAddAdapter";
    private List<ContactListJson.ObjBean> mObjBeans;
    private Context mContext;

    private TextView topLastView;


    //自定义监听事件
    interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);

        void onButtonClick(View view, int position);

    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;


    public void refresh(List<ContactListJson.ObjBean> objBeens) {

        mObjBeans = objBeens;
        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public PreviewAddAdapter(List<ContactListJson.ObjBean> objBeens, Context context) {
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
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        if (position == mObjBeans.size() + 1) {
            holder.iv_contact_add_item_icon.setVisibility(View.GONE);
            holder.riv_contact_add_item_photo.setImageResource(R.drawable.cloud_3xinxi_minus);
            holder.tv_contact_add_item_name.setVisibility(View.GONE);
            holder.riv_contact_add_item_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ContactPersonActivity.class);
                    intent.putExtra("type", "minus");
                    mContext.startActivity(intent);
                }
            });
        } else if (position == mObjBeans.size()) {
            holder.iv_contact_add_item_icon.setVisibility(View.GONE);
            holder.riv_contact_add_item_photo.setImageResource(R.drawable.cloud_3xinxi_add);
            holder.tv_contact_add_item_name.setVisibility(View.GONE);
            holder.riv_contact_add_item_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ContactPersonActivity.class);
                    intent.putExtra("type", "add");
                    mContext.startActivity(intent);
                }
            });
        } else if (position == 0) {
            String trueName = mObjBeans.get(position).getTrueName();
            String flag = mObjBeans.get(position).getIsUploadPhoto();
            String photoFile = mObjBeans.get(position).getPhotoFile();
            String wechatUrl = mObjBeans.get(position).getWechatUrl();

            holder.tv_contact_add_item_name.setText(trueName);
            if ("0".equals(flag) && wechatUrl != null && !"".equals(wechatUrl)) {
                Picasso.with(mContext).load(wechatUrl).error(R.drawable.msg_2list_linkman).into(holder.riv_contact_add_item_photo);
            } else if ("1".equals(flag) && photoFile != null && !"".equals(photoFile)) {
                Picasso.with(mContext).load(photoFile).error(R.drawable.msg_2list_linkman).into(holder.riv_contact_add_item_photo);
            }


        } else if (position == 1) {
            holder.iv_contact_add_item_icon.setBackgroundResource(R.drawable.team_border);
            holder.iv_contact_add_item_icon.setText("协");
            String trueName = mObjBeans.get(position).getTrueName();
            String flag = mObjBeans.get(position).getIsUploadPhoto();
            String photoFile = mObjBeans.get(position).getPhotoFile();
            String wechatUrl = mObjBeans.get(position).getWechatUrl();
            holder.tv_contact_add_item_name.setText(trueName);
            if ("0".equals(flag) && wechatUrl != null && !"".equals(wechatUrl)) {
                Picasso.with(mContext).load(wechatUrl).error(R.drawable.msg_2list_linkman).into(holder.riv_contact_add_item_photo);
            } else if ("1".equals(flag) && photoFile != null && !"".equals(photoFile)) {
                Picasso.with(mContext).load(photoFile).error(R.drawable.msg_2list_linkman).into(holder.riv_contact_add_item_photo);
            }


        } else {
            holder.iv_contact_add_item_icon.setVisibility(View.GONE);
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


        }

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

        public MyViewHolder(View view) {
            super(view);
            riv_contact_add_item_photo = (RoundImageView) view.findViewById(R.id.riv_contact_add_item_photo);
            iv_contact_add_item_icon = (TextView) view.findViewById(R.id.iv_contact_add_item_icon);
            tv_contact_add_item_name = (TextView) view.findViewById(R.id.tv_contact_add_item_name);
        }
    }

}
