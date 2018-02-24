package com.mobileoffice.controller.login;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobileoffice.R;
import com.mobileoffice.json.RoleListJson;

import java.util.List;

/**
 * Created by 99213 on 2017/6/16.
 */

public class RoleAdapter extends RecyclerView.Adapter<RoleAdapter.MyHolder> {
    private Context mContext;
    private List<RoleListJson.ObjBean> mLists;
    private HospitalChildListener mHospitalChildListener;

    interface HospitalChildListener {
        void OnHospitalParentClick(View thisView, int position);
    }

    public void setHospitalParentListener(HospitalChildListener listener) {
        mHospitalChildListener = listener;
    }

    public RoleAdapter(Context context, List<RoleListJson.ObjBean> lists) {
        mContext = context;
        mLists = lists;

    }

    public void refresh(List<RoleListJson.ObjBean> lists) {
        mLists = lists;
        notifyDataSetChanged();
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.role_item, parent, false);
        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {

            holder.tv_role_name.setText(mLists.get(position).getRoleName());
            holder.tv_role_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mHospitalChildListener.OnHospitalParentClick(view,position);
                }
            });
    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView tv_role_name;

        public MyHolder(View itemView) {
            super(itemView);
            tv_role_name = (TextView) itemView.findViewById(R.id.tv_role_name);
        }
    }
}
