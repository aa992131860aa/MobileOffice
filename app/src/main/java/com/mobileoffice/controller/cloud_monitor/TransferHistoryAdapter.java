package com.mobileoffice.controller.cloud_monitor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobileoffice.R;
import com.mobileoffice.entity.Transfer;
import com.mobileoffice.json.TransferHistoryJson;
import com.mobileoffice.json.TransferJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.ToastUtil;

import java.util.List;

/**
 * Created by 99213 on 2017/4/23.
 */

public class TransferHistoryAdapter extends RecyclerView.Adapter<TransferHistoryAdapter.MyViewHolder> {


    private static final String TAG = "ContactPersonAddAdapter";
    private List<TransferJson.ObjBean> mTransfers;
    private Context mContext;
    private int NO_CONTENT = 0;
    private int CONTENT = 1;
    private int FOOTER = 2;
    private int loadType = -1;


    //自定义监听事件
    interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view);
    }

    //加载错误,重新加载事件
    interface OnRecyclerViewLoadErrorClickListener {
        void onLoadErrorClick(View view);


    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private OnRecyclerViewLoadErrorClickListener mOnLoadErrorClickListener = null;

    public void refreshList(List<TransferJson.ObjBean> transfers, int loadType) {
        mTransfers = transfers;
        this.loadType = loadType;
        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setOnLoadErrorClickListener(OnRecyclerViewLoadErrorClickListener listener) {
        mOnLoadErrorClickListener = listener;
    }

    public TransferHistoryAdapter(List<TransferJson.ObjBean> transfers, Context context) {
        mTransfers = transfers;
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogUtil.e(TAG, "viewType:" + viewType);
        if (viewType == NO_CONTENT) {
            return null;
        } else if (viewType == FOOTER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.history_item_footer, parent, false);

            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.history_item, parent, false);

            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if (holder != null) {
            //footer
            if (position == mTransfers.size()) {
                if (loadType == CONSTS.LOAD_CONTINUE) {
                    holder.pb_history_footer.setVisibility(View.VISIBLE);
                    holder.tv_history_footer.setText("加载更多...");
                    holder.ll_history_footer.setEnabled(false);
                } else if (loadType == CONSTS.LOAD_FINISH) {
                    holder.pb_history_footer.setVisibility(View.GONE);
                    holder.tv_history_footer.setText("没有更多了");
                    holder.ll_history_footer.setEnabled(false);
                } else if (loadType == CONSTS.LOAD_ERROR) {
                    holder.ll_history_footer.setEnabled(true);
                    holder.pb_history_footer.setVisibility(View.GONE);
                    holder.tv_history_footer.setText("重新加载");
                    holder.ll_history_footer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mOnLoadErrorClickListener.onLoadErrorClick(view);
                        }
                    });
                }
            } else {
                if(mTransfers.get(position).getOrganSeg().contains("AP")){

                     if(mTransfers.get(position).getAutoTransfer()==CONSTS.AURO_TRANSFER_FINISH_NO){
                         holder.iv_history_item.setVisibility(View.VISIBLE);
                         holder.iv_auto.setImageResource(R.drawable.cloud_1index_auto);

                     }else{
                         holder.iv_history_item.setVisibility(View.GONE);
                         holder.iv_auto.setImageResource(R.drawable.cloud_1index_auto_black);
                     }

                }else{
                    holder.iv_history_item.setVisibility(View.GONE);
                    holder.iv_auto.setVisibility(View.GONE);
                }
                if(!"".equals(mTransfers.get(position).getModifyOrganSeg())){
                    holder.tv_transfer_info_organSeg.setText(mTransfers.get(position).getModifyOrganSeg());
                }else {
                    holder.tv_transfer_info_organSeg.setText(mTransfers.get(position).getOrganSeg());
                }
                holder.tv_transfer_info_time.setText(mTransfers.get(position).getGetTime().split(" ")[0]);
                holder.tv_transfer_info_start.setText(mTransfers.get(position).getFromCity().split("市")[0]);
                if(mTransfers.get(position).getToHosp()!=null) {
                    holder.tv_transfer_info_end.setText(mTransfers.get(position).getToHosp().split("市")[0]);
                }
                if(mTransfers.get(position).getDistance()!=null) {
                    holder.tv_transfer_info_distance.setText("共" + mTransfers.get(position).getDistance().split("\\.")[0] + "km");
                }
                holder.tv_transfer_info_organ.setText(mTransfers.get(position).getOrgan());
                holder.tv_transfer_info_blood.setText(mTransfers.get(position).getBlood() + "型");
                holder.tv_transfer_info_true_name.setText(mTransfers.get(position).getTrueName());
                holder.tv_transfer_info_phone.setText(mTransfers.get(position).getPhone());
                holder.tv_transfer_info_contact_name.setText(mTransfers.get(position).getContactName());
                holder.tv_transfer_info_contact_phone.setText(mTransfers.get(position).getContactPhone());

                holder.ll_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemClickListener.onItemClick(view,position);
                    }
                });

            }
        }
    }

    @Override
    public int getItemCount() {
        if (mTransfers.size() == 0) {
            return mTransfers.size();
        } else {
            return mTransfers.size() + 1;
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (mTransfers.size() == 0) {
            return NO_CONTENT;
        } else if (position == mTransfers.size()) {
            return FOOTER;
        } else {
            return CONTENT;
        }
    }

    //自定义ViewHolder，用于加载图片
    class MyViewHolder extends RecyclerView.ViewHolder {
        //footer
        LinearLayout ll_history_footer;
        ProgressBar pb_history_footer;
        TextView tv_history_footer;

        LinearLayout ll_transfer;

        //转运历史
        TextView tv_transfer_info_organSeg;
        TextView tv_transfer_info_time;
        TextView tv_transfer_info_start;
        TextView tv_transfer_info_end;
        TextView tv_transfer_info_distance;
        TextView tv_transfer_info_organ;
        TextView tv_transfer_info_blood;
        TextView tv_transfer_info_true_name;
        TextView tv_transfer_info_phone;
        TextView tv_transfer_info_contact_name;
        TextView tv_transfer_info_contact_phone;
        LinearLayout ll_item;

        ImageView iv_history_item;
        ImageView iv_auto;

        public MyViewHolder(View view) {
            super(view);
            ll_history_footer = (LinearLayout) view.findViewById(R.id.ll_history_footer);
            pb_history_footer = (ProgressBar) view.findViewById(R.id.pb_history_footer);
            tv_history_footer = (TextView) view.findViewById(R.id.tv_history_footer);


            tv_transfer_info_organSeg = (TextView) view.findViewById(R.id.tv_transfer_info_organSeg);
            tv_transfer_info_time = (TextView) view.findViewById(R.id.tv_transfer_info_time);
            tv_transfer_info_start = (TextView) view.findViewById(R.id.tv_transfer_info_start);
            tv_transfer_info_end = (TextView) view.findViewById(R.id.tv_transfer_info_end);
            tv_transfer_info_distance = (TextView) view.findViewById(R.id.tv_transfer_info_distance);
            tv_transfer_info_organ = (TextView) view.findViewById(R.id.tv_transfer_info_organ);
            tv_transfer_info_blood = (TextView) view.findViewById(R.id.tv_transfer_info_blood);
            tv_transfer_info_true_name = (TextView) view.findViewById(R.id.tv_transfer_info_true_name);
            tv_transfer_info_phone = (TextView) view.findViewById(R.id.tv_transfer_info_phone);
            tv_transfer_info_contact_name = (TextView) view.findViewById(R.id.tv_transfer_info_contact_name);
            tv_transfer_info_contact_phone = (TextView) view.findViewById(R.id.tv_transfer_info_contact_phone);
            ll_item = (LinearLayout) view.findViewById(R.id.ll_item);

            iv_history_item = (ImageView) view.findViewById(R.id.iv_history_item);
            iv_auto = (ImageView) view.findViewById(R.id.iv_auto);

        }
    }

}
