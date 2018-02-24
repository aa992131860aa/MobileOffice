package com.mobileoffice.controller.login;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobileoffice.R;

import java.util.List;

/**
 * Created by 99213 on 2017/6/16.
 */

public class HospitalParentAdapter extends RecyclerView.Adapter<HospitalParentAdapter.MyHolder> {
    private Context mContext;
    private List<String> mLists;
    private HospitalParentListener mHospitalParentListener;
    private View firstView;
    private int lastPosition = -1;
    public interface HospitalParentListener {
        void OnHospitalParentClick(View thisView, View firstView, String province,int position);
    }

    public void setHospitalParentListener(HospitalParentListener listener) {
        mHospitalParentListener = listener;
    }

    public HospitalParentAdapter(Context context, List<String> lists) {
        mContext = context;
        mLists = lists;
        if (mLists.size() == 0) {
            mLists.add("定位中...");
        } else {
            mLists.remove("定位中...");
        }
    }

    public void refresh(List<String> lists,int position) {
        lastPosition = position;
        mLists = lists;
        mLists.remove("定位中...");
        notifyDataSetChanged();
    }
    public void refresh(int position) {

        lastPosition = position;
        notifyDataSetChanged();
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.hospital_parent, parent, false);
        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {

        if (position == 0 && lastPosition==-1) {
            holder.tv_hospital_parent.setBackgroundColor(Color.WHITE);
            firstView = holder.tv_hospital_parent;
            holder.tv_hospital_parent.setTextColor(mContext.getResources().getColor(R.color.highlight));
        }
        else{
            holder.tv_hospital_parent.setBackgroundColor(mContext.getResources().getColor(R.color.bg));
            holder.tv_hospital_parent.setTextColor(mContext.getResources().getColor(R.color.font_black_3));
        }
        if(position==lastPosition){
            holder.tv_hospital_parent.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            holder.tv_hospital_parent.setTextColor(mContext.getResources().getColor(R.color.highlight));
        }
        holder.tv_hospital_parent.setText(mLists.get(position));
        holder.tv_hospital_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHospitalParentListener.OnHospitalParentClick(holder.tv_hospital_parent,firstView,mLists.get(position),position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView tv_hospital_parent;

        public MyHolder(View itemView) {
            super(itemView);
            tv_hospital_parent = (TextView) itemView.findViewById(R.id.tv_hospital_parent);
        }
    }
}
