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

public class HospitalChildAdapter extends RecyclerView.Adapter<HospitalChildAdapter.MyHolder> {
    private Context mContext;
    private List<String> mLists;
    private HospitalChildListener mHospitalChildListener;

    interface HospitalChildListener {
        void OnHospitalParentClick(View thisView, String hospital);
    }

    public void setHospitalParentListener(HospitalChildListener listener) {
        mHospitalChildListener = listener;
    }

    public HospitalChildAdapter(Context context, List<String> lists) {
        mContext = context;
        mLists = lists;

    }

    public void refresh(List<String> lists) {
        mLists = lists;
        notifyDataSetChanged();
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.hospital_child, parent, false);
        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {


        holder.tv_hospital_child.setText(mLists.get(position));
        holder.tv_hospital_child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHospitalChildListener.OnHospitalParentClick(holder.tv_hospital_child, mLists.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView tv_hospital_child;

        public MyHolder(View itemView) {
            super(itemView);
            tv_hospital_child = (TextView) itemView.findViewById(R.id.tv_hospital_child);
        }
    }
}
