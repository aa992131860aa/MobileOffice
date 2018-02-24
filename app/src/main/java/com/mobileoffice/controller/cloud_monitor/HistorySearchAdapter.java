package com.mobileoffice.controller.cloud_monitor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobileoffice.R;
import com.mobileoffice.json.ContactSearchJson;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.view.RoundImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by 99213 on 2017/4/23.
 */

public class HistorySearchAdapter extends RecyclerView.Adapter<HistorySearchAdapter.MyViewHolder> {


    private static final String TAG = "ContactPersonAddAdapter";
    private List<ContactSearchJson.ObjBean> mObjBeans;
    private Context mContext;
    private int divide = -1;
    private int topTotal = -1;
    private int bottomTotal = -1;
    private TextView topLastView;


    //自定义监听事件
    interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);

        void onButtonClick(View view, int position);

    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;


    public void refresh(List<ContactSearchJson.ObjBean> objBeens) {
        divide = -1;
        mObjBeans = objBeens;
        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public HistorySearchAdapter(List<ContactSearchJson.ObjBean> objBeens, Context context) {
        mObjBeans = objBeens;
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.contact_person_add_item, parent, false);

        MyViewHolder holder = new MyViewHolder(view);

        return holder;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        ContactSearchJson.ObjBean objBean = mObjBeans.get(position);
        holder.tv_contact_person_add_item_hospital.setText(objBean.getName());
        holder.tv_contact_person_add_item_name.setText(objBean.getTrue_name());

        //图片显示
        if("0".equals(objBean.getIs_upload_photo())){

            if(objBean.getWechat_url()!=null&&!"".equals(objBean.getWechat_url())){
                Picasso.with(mContext).load(objBean.getWechat_url()).error(R.drawable.msg_2list_linkman).into(holder.riv_contact_person_add_item);
            }else{
                Picasso.with(mContext).load(objBean.getPhoto_url()).error(R.drawable.msg_2list_linkman).into(holder.riv_contact_person_add_item);
                LogUtil.e(TAG,"url:"+objBean.getPhoto_url());
            }

        }else{
            //用户已上传图片 1
            Picasso.with(mContext).load(objBean.getPhoto_url()).error(R.drawable.msg_2list_linkman).into(holder.riv_contact_person_add_item);
        }
        //Picasso.with(mContext).load(mObjBeans.g)
        holder.tv_contact_person_add_item_line.setVisibility(View.VISIBLE);
        if (objBean.getIs_friend() == 0) {
            topLastView = holder.tv_contact_person_add_item_line;
            divide++;
            holder.btn_contact_person_add_item.setVisibility(View.GONE);
        } else {
            holder.btn_contact_person_add_item.setVisibility(View.VISIBLE);
        }
        //设置分割线
        if (position > divide && divide != -1) {
            holder.tv_contact_person_add_item_diver.setVisibility(View.VISIBLE);
            divide = -1;
            if (topLastView != null) {
                topLastView.setVisibility(View.GONE);
            }
        } else {
            holder.tv_contact_person_add_item_diver.setVisibility(View.GONE);
        }
        if (position == mObjBeans.size() - 1) {
            holder.tv_contact_person_add_item_line.setVisibility(View.GONE);
        }

        //点击事件
        holder.ll_contact_person_add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v,position);
            }
        });
        holder.btn_contact_person_add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onButtonClick(v,position);
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
        LinearLayout ll_contact_person_add_item;
        RoundImageView riv_contact_person_add_item;
        TextView tv_contact_person_add_item_name;
        TextView tv_contact_person_add_item_hospital;
        Button btn_contact_person_add_item;

        TextView tv_contact_person_add_item_line;
        TextView tv_contact_person_add_item_diver;

        public MyViewHolder(View view) {
            super(view);
            ll_contact_person_add_item = (LinearLayout) view.findViewById(R.id.ll_contact_person_add_item);
            riv_contact_person_add_item = (RoundImageView) view.findViewById(R.id.riv_contact_person_add_item);
            tv_contact_person_add_item_name = (TextView) view.findViewById(R.id.tv_contact_person_add_item_name);
            tv_contact_person_add_item_hospital = (TextView) view.findViewById(R.id.tv_contact_person_add_item_hospital);
            btn_contact_person_add_item = (Button) view.findViewById(R.id.btn_contact_person_add_item);

            tv_contact_person_add_item_line = (TextView) view.findViewById(R.id.tv_contact_person_add_item_line);
            tv_contact_person_add_item_diver = (TextView) view.findViewById(R.id.tv_contact_person_add_item_diver);
        }
    }

}
