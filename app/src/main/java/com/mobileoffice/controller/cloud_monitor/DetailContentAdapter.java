package com.mobileoffice.controller.cloud_monitor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobileoffice.R;
import com.mobileoffice.controller.cloud_monitor.frag.ExceptionEntity;
import com.mobileoffice.entity.PathInfo;
import com.mobileoffice.utils.DisplayUtil;


import java.util.List;

/**
 * Created by 99213 on 2017/7/26.
 */

public class DetailContentAdapter extends RecyclerView.Adapter<DetailContentAdapter.MyHolder> {
    private Context mContext;
    private List<PathInfo> mLists;
    private DetailContentAdapter.OnDetailClickListener mHospitalChildListener;

    public interface OnDetailClickListener {
        void OnClick(View view, int position);
    }

    public void setDetailClickListener(DetailContentAdapter.OnDetailClickListener listener) {
        mHospitalChildListener = listener;
    }

    public DetailContentAdapter(Context context, List<PathInfo> lists) {
        mContext = context;
        mLists = lists;

    }

    public void refresh(List<PathInfo> lists) {
        mLists = lists;
        notifyDataSetChanged();
    }

    @Override
    public DetailContentAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.detail_content, parent, false);
        DetailContentAdapter.MyHolder myHolder = new DetailContentAdapter.MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(final DetailContentAdapter.MyHolder holder, final int position) {


        if (position == 0) {
            holder.tv_top_line.setVisibility(View.GONE);
            holder.tv_dot.setBackgroundResource(R.drawable.cloud_4location_now);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DisplayUtil.dip2px(mContext, 14), DisplayUtil.dip2px(mContext, 14));
            params.setMargins(0, DisplayUtil.dip2px(mContext, 17), 0, 0);

            holder.tv_dot.setLayoutParams(params);
            holder.tv_dot.setGravity(Gravity.CENTER_HORIZONTAL);

            holder.tv_time.setText(mLists.get(position).getTime());

            holder.tv_content.setTextSize(16);
           // holder.tv_time.setTextSize(16);
            holder.tv_date.setVisibility(View.VISIBLE);
            holder.tv_date.setTextSize(18);
            holder.tv_date.setText(mLists.get(position).getMonth());
            holder.tv_content.setText(mLists.get(position).getContent());
            holder.tv_content.setTextColor(mContext.getResources().getColor(R.color.orange_text));
            holder.tv_time.setTextColor(mContext.getResources().getColor(R.color.orange_text));
        } else {
            holder.tv_date.setVisibility(View.GONE);
            holder.tv_top_line.setVisibility(View.VISIBLE);
            holder.tv_date.setText(mLists.get(position).getTime());
            holder.tv_dot.setBackgroundResource(R.drawable.cloud_4location_past);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DisplayUtil.dip2px(mContext, 17), DisplayUtil.dip2px(mContext, 17));
            params.setMargins(0, 0, 0, 0);

            holder.tv_content.setTextSize(14);
            holder.tv_time.setTextSize(14);
            holder.tv_dot.setLayoutParams(params);
            holder.tv_dot.setGravity(Gravity.CENTER_HORIZONTAL);

            holder.tv_time.setText(mLists.get(position).getTime());
            holder.tv_content.setText(mLists.get(position).getContent());
            holder.tv_content.setTextColor(mContext.getResources().getColor(R.color.font_black_9));
            holder.tv_time.setTextColor(mContext.getResources().getColor(R.color.font_black_9));

        }
        if(position==mLists.size()-1){
            holder.tv_bottom.setVisibility(View.INVISIBLE);
        }else{
            holder.tv_bottom.setVisibility(View.VISIBLE);
        }
        holder.ll_contain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHospitalChildListener != null) {
                    mHospitalChildListener.OnClick(v, position);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView tv_top_line;
        TextView tv_dot;

        TextView tv_time;
        TextView tv_content;
        TextView tv_date;

        LinearLayout ll_contain;
        TextView tv_bottom;

        public MyHolder(View itemView) {
            super(itemView);
            tv_top_line = (TextView) itemView.findViewById(R.id.tv_top_line);
            tv_dot = (TextView) itemView.findViewById(R.id.tv_dot);

            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);

            ll_contain = (LinearLayout) itemView.findViewById(R.id.ll_contain);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            tv_bottom = (TextView) itemView.findViewById(R.id.tv_bottom);

        }
    }
}