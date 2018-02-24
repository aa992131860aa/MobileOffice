package com.mobileoffice.controller.me;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobileoffice.R;
import com.mobileoffice.json.Workload;

import java.util.List;

/**
 * Created by 99213 on 2017/4/23.
 */

public class WorkloadPersonAdapter extends RecyclerView.Adapter<WorkloadPersonAdapter.MyViewHolder> {


    private static final String TAG = "PdfInfoDetailAdapter";
    private List<Workload> mObjBeans;
    private Context mContext;

    private TextView topLastView;


    //自定义监听事件
    interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);

        void onButtonClick(View view, int position);

    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;


    public void refresh(List<Workload> objBeens) {

        mObjBeans = objBeens;
        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public WorkloadPersonAdapter(List<Workload> objBeens, Context context) {
        mObjBeans = objBeens;
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.workload_person_item, parent, false);

        MyViewHolder holder = new MyViewHolder(view);

        return holder;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        Workload workload = mObjBeans.get(position);
        holder.tv_role_name.setText(workload.getPostRole());
        holder.tv_count.setText(workload.getCount()+"");

    }

    @Override
    public int getItemCount() {

        return mObjBeans.size();


    }


    //自定义ViewHolder
    class MyViewHolder extends RecyclerView.ViewHolder {


        TextView tv_role_name;
        TextView tv_count;

        public MyViewHolder(View view) {
            super(view);

            tv_role_name = (TextView) view.findViewById(R.id.tv_role_name);
            tv_count = (TextView) view.findViewById(R.id.tv_count);

        }
    }

}
