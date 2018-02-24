package com.mobileoffice.controller.me;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobileoffice.R;
import com.mobileoffice.controller.message.contact.ContactPersonActivity;
import com.mobileoffice.json.ContactListJson;
import com.mobileoffice.json.PdfInfoDetailJson;
import com.mobileoffice.view.RoundImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by 99213 on 2017/4/23.
 */

public class PdfInfoDetailAdapter extends RecyclerView.Adapter<PdfInfoDetailAdapter.MyViewHolder> {


    private static final String TAG = "PdfInfoDetailAdapter";
    private List<PdfInfoDetailJson.ObjBean> mObjBeans;
    private Context mContext;

    private TextView topLastView;


    //自定义监听事件
    interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);

        void onButtonClick(View view, int position);

    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;


    public void refresh(List<PdfInfoDetailJson.ObjBean> objBeens) {

        mObjBeans = objBeens;
        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public PdfInfoDetailAdapter(List<PdfInfoDetailJson.ObjBean> objBeens, Context context) {
        mObjBeans = objBeens;
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.pdf_detail_item, parent, false);

        MyViewHolder holder = new MyViewHolder(view);

        return holder;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        PdfInfoDetailJson.ObjBean objBean = mObjBeans.get(position);

        holder.tv_name.setText("器官段号"+objBean.getOrganSeg()+".pdf");
        holder.tv_time.setText(objBean.getCreateTime().split(" ")[0]);
        holder.tv_size.setText(objBean.getPdfSize()+"k");
        if ("心脏".equals(objBean.getOrgan())) {
            holder.iv_organ.setImageResource(R.drawable.cloud_1index_1heart);
        } else if ("肝脏".equals(objBean.getOrgan())) {
            holder.iv_organ.setImageResource(R.drawable.cloud_1index_1liver);
        } else if ("肺".equals(objBean.getOrgan())) {
            holder.iv_organ.setImageResource(R.drawable.cloud_1index_1lung);
        } else if ("肾脏".equals(objBean.getOrgan())) {
            holder.iv_organ.setImageResource(R.drawable.cloud_1index_1kidneys);
        } else if ("眼角膜".equals(objBean.getOrgan())) {
            holder.iv_organ.setImageResource(R.drawable.cloud_1index_1cornea);
        } else if ("胰脏".equals(objBean.getOrgan())) {
            holder.iv_organ.setImageResource(R.drawable.cloud_1index_1pancreas);
        }

        holder.rl_content.setOnClickListener(new View.OnClickListener() {
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

        ImageView iv_organ;
        TextView tv_name;
        TextView tv_size;
        TextView tv_time;
        RelativeLayout rl_content;

        public MyViewHolder(View view) {
            super(view);
            iv_organ = (ImageView) view.findViewById(R.id.iv_organ);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_size = (TextView) view.findViewById(R.id.tv_size);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            rl_content = (RelativeLayout) view.findViewById(R.id.rl_content);

        }
    }

}
