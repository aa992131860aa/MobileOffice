package com.mobileoffice.controller.cloud_monitor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobileoffice.R;
import com.mobileoffice.json.ContactListJson;

import java.util.List;

/**
 * Created by 99213 on 2017/4/23.
 */

public class SpeedDialAdapter extends RecyclerView.Adapter<SpeedDialAdapter.MyViewHolder> {


    private static final String TAG = "PostRoleAdapter";
    private List<ContactListJson.ObjBean> mObjBeans;
    private Context mContext;

    private TextView topLastView;
    private String mPostRole;


    //自定义监听事件
    public interface OnRecyclerViewItemClickListener {
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

    public SpeedDialAdapter(List<ContactListJson.ObjBean> objBeens, Context context, String postRole) {
        mObjBeans = objBeens;
        mContext = context;
        mPostRole = postRole;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.speed_dial_item, parent, false);

        MyViewHolder holder = new MyViewHolder(view);

        return holder;

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        String name = mObjBeans.get(position).getTrueName();
        String postRole = mObjBeans.get(position).getPostRole();
        String phone = mObjBeans.get(position).getPhone();
        if ("转".equals(postRole)) {
            postRole = "转运医师";
        } else if ("协".equals(postRole)) {
            postRole = "协调专员";
        }
        else if ("无".equals(postRole) || postRole == null || "".equals(postRole)) {
            postRole = "";
        }

        holder.tv_name.setText(name);
        if(!"".equals(postRole)){
            holder.tv_post_role.setVisibility(View.VISIBLE);
            holder.tv_post_role.setText(postRole);
        }else{
            holder.tv_post_role.setVisibility(View.GONE);
        }
        holder.tv_phone.setText(phone);
        holder.ll_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v,position);
            }
        });

    }

    @Override
    public int getItemCount() {

        return mObjBeans.size();


    }


    //自定义ViewHolder
    class MyViewHolder extends RecyclerView.ViewHolder {
        //footer

        TextView tv_name;
        TextView tv_phone;
        TextView tv_post_role;
        ImageView iv_call;
        LinearLayout ll_root;

        public MyViewHolder(View view) {
            super(view);

            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_phone = (TextView) view.findViewById(R.id.tv_phone);
            tv_post_role = (TextView) view.findViewById(R.id.tv_post_role);
            iv_call = (ImageView) view.findViewById(R.id.iv_call);
            ll_root = (LinearLayout) view.findViewById(R.id.ll_root);
        }
    }

}
