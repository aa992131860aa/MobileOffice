package com.mobileoffice.controller.message.contact;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobileoffice.R;
import com.mobileoffice.json.ContactPersonJson;
import com.mobileoffice.utils.LogUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by 99213 on 2017/6/16.
 */

public class ContactFragmentAdapter extends RecyclerView.Adapter<ContactFragmentAdapter.MyHolder> {
    private Context mContext;
    private List<ContactPersonJson.ObjBean> mLists;
    private HospitalChildListener mHospitalChildListener;
    private String TAG = "ContactFragmentAdapter";

    interface HospitalChildListener {
        void OnHospitalParentClick(int position);
    }

    public void setHospitalParentListener(HospitalChildListener listener) {
        mHospitalChildListener = listener;
    }

    public ContactFragmentAdapter(Context context, List<ContactPersonJson.ObjBean> lists) {
        mContext = context;
        mLists = lists;

    }

    public void refresh(List<ContactPersonJson.ObjBean> lists) {
        mLists = lists;
        notifyDataSetChanged();
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.contact_item, parent, false);
        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
        holder.ll_contact_item_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHospitalChildListener.OnHospitalParentClick(position);
            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat sdfSort = new SimpleDateFormat("MM.dd");
        try {


            Date date = sdf.parse(mLists.get(position).getCreateTime());
            holder.tv_contact_item_group.setText(mLists.get(position).getGroupName() + " " + sdfSort.format(date)+" ");
            holder.tv_contact_item_number.setText("(" + mLists.get(position).getUsersIds().split(",").length + ")");

            String groupName = mLists.get(position).getGroupName();
            if(groupName.contains("-心脏")){
                   holder.iv_team.setImageResource(R.drawable.msg_1index_team1);
            }else if(groupName.contains("-肝脏")){
                holder.iv_team.setImageResource(R.drawable.msg_1index_team2);
            }else if(groupName.contains("-肺")){
                holder.iv_team.setImageResource(R.drawable.msg_1index_team3);
            }else if(groupName.contains("-肾脏")){
                holder.iv_team.setImageResource(R.drawable.msg_1index_team4);
            }else if(groupName.contains("-胰脏")){
                holder.iv_team.setImageResource(R.drawable.msg_1index_team5);
            }else if(groupName.contains("-眼角膜")){
                holder.iv_team.setImageResource(R.drawable.msg_1index_team6);
            }else {
                holder.iv_team.setImageResource(R.drawable.msg_1index_team);
            }


        } catch (ParseException e) {
            e.printStackTrace();
            LogUtil.e(TAG,e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_contact_item_group;
        TextView tv_contact_item_group;
        TextView tv_contact_item_number;
        ImageView iv_team;

        public MyHolder(View itemView) {
            super(itemView);
            ll_contact_item_group = (LinearLayout) itemView.findViewById(R.id.ll_contact_item_group);
            tv_contact_item_group = (TextView) itemView.findViewById(R.id.tv_contact_item_group);
            tv_contact_item_number = (TextView) itemView.findViewById(R.id.tv_contact_item_number);
            iv_team = (ImageView) itemView.findViewById(R.id.iv_team);
        }
    }
}
