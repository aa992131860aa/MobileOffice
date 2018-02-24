package com.mobileoffice.controller.new_monitor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobileoffice.R;
import com.mobileoffice.entity.BoxUse;

import java.util.List;

/**
 * Created by 99213 on 2017/4/23.
 */

public class BoxNoAdapter extends RecyclerView.Adapter<BoxNoAdapter.MyViewHolder> {


    private static final String TAG = "BoxNoAdapter";
    private List<BoxUse> mObjBeans;
    private Context mContext;


    //自定义监听事件
    interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);

        void onButtonClick(View view, int position);

    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;


    public void refresh(List<BoxUse> objBeens) {

        mObjBeans = objBeens;
        this.notifyDataSetChanged();
    }


    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public BoxNoAdapter(List<BoxUse> objBeens, Context context) {
        mObjBeans = objBeens;
        mContext = context;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.pop_use_item, parent, false);

        MyViewHolder holder = new MyViewHolder(view);

        return holder;

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        if (position == mObjBeans.size() - 1) {
            holder.tv_line.setVisibility(View.GONE);
        } else {
            holder.tv_line.setVisibility(View.VISIBLE);
        }

        final BoxUse boxUse = mObjBeans.get(position);
        if("使用中".equals(boxUse.getStatus())){
            holder.tv_box_no.setTextColor(mContext.getResources().getColor(R.color.font_black_c));
            holder.tv_box_status.setTextColor(mContext.getResources().getColor(R.color.font_black_c));

        }else{
            //if("空闲".equals(boxUse.getStatus())){
                holder.tv_box_no.setTextColor(mContext.getResources().getColor(R.color.font_black));
                holder.tv_box_status.setTextColor(mContext.getResources().getColor(R.color.font_black));
           // }

        }
        holder.tv_box_no.setText(boxUse.getBoxNo());
        holder.tv_box_status.setText(boxUse.getStatus());
        holder.ll_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!"使用中".equals(boxUse.getStatus())) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            }
        });

    }


    @Override
    public int getItemCount() {

        return mObjBeans.size();


    }


    //自定义ViewHolder
    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_box_no;
        TextView tv_box_status;
        TextView tv_line;
        LinearLayout ll_box;


        public MyViewHolder(View view) {
            super(view);


            tv_box_no = (TextView) view.findViewById(R.id.tv_box_no);
            tv_box_status = (TextView) view.findViewById(R.id.tv_box_status);
            tv_line = (TextView) view.findViewById(R.id.tv_line);
            ll_box = (LinearLayout) view.findViewById(R.id.ll_box);

        }
    }

}
