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
import com.mobileoffice.json.PostRoleJson;
import com.mobileoffice.view.RoundImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by 99213 on 2017/4/23.
 */

public class PostRoleAdapter extends RecyclerView.Adapter<PostRoleAdapter.MyViewHolder> {


    private static final String TAG = "PostRoleAdapter";
    private List<PostRoleJson.PostRole> mObjBeans;
    private Context mContext;

    private TextView topLastView;
    private String mPostRole;


    //自定义监听事件
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);

        void onButtonClick(View view, int position);

    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;


    public void refresh(List<PostRoleJson.PostRole> objBeens) {

        mObjBeans = objBeens;
        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public PostRoleAdapter(List<PostRoleJson.PostRole> objBeens, Context context,String postRole) {
        mObjBeans = objBeens;
        mContext = context;
        mPostRole = postRole;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.post_role_item, parent, false);

        MyViewHolder holder = new MyViewHolder(view);

        return holder;

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

            holder.tv_post_role.setText(mObjBeans.get(position).getPostRole());
            if(mObjBeans.get(position).getPostRole().trim().equals(mPostRole==null?"":mPostRole.trim())){
                holder.tv_post_role.setBackgroundResource(R.drawable.red_post_role_border);
                holder.tv_post_role.setTextColor(mContext.getResources().getColor(R.color.highlight));
            }else{
                holder.tv_post_role.setBackgroundResource(R.drawable.post_role_border);
                holder.tv_post_role.setTextColor(mContext.getResources().getColor(R.color.font_black_3));
            }
            holder.tv_post_role.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v,position);
                    holder.tv_post_role.setTextColor(mContext.getResources().getColor(R.color.highlight));
                    holder.tv_post_role.setBackgroundResource(R.drawable.red_post_role_border);
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

        TextView tv_post_role;

        public MyViewHolder(View view) {
            super(view);

            tv_post_role = (TextView) view.findViewById(R.id.tv_post_role);
        }
    }

}
