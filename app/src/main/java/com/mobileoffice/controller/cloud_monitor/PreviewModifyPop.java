package com.mobileoffice.controller.cloud_monitor;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mobileoffice.R;

import java.util.List;

public class PreviewModifyPop extends PopupWindow {

    private RecyclerView rv_popup_single;
    private LinearLayoutManager mLinearLayoutManager;

    private List<String> mData;
    private Activity mContext;
   private TransferAdapter mAdapter;

    public PreviewModifyPop(final Activity context, List<String> dataList,int width) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.popup_single, null);
        mContext = context;
        mData = dataList;

        this.setContentView(v);
        this.setWidth(width);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(new BitmapDrawable());

        rv_popup_single = (RecyclerView) v.findViewById(R.id.rv_popup_single);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        rv_popup_single.setLayoutManager(mLinearLayoutManager);
        mAdapter = new TransferAdapter();
        rv_popup_single.setAdapter(mAdapter);
    }


    /**
     * 显示popupWindow
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
//            this.showAsDropDown(parent, -20, 5);
            this.showAsDropDown(parent, 0, 1);
        } else {
            this.dismiss();
        }
    }


    public interface OnClickChangeListener {
         void OnClickChange(int position);
    }

    public void setOnClickChangeListener(OnClickChangeListener clickChangeListener) {
        mListener = clickChangeListener;
    }

    private OnClickChangeListener mListener;

    class TransferAdapter extends RecyclerView.Adapter<TransferAdapter.MyViewHolder> {
        private String TAG = "TransferAdapter";


        @Override
        public TransferAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_pop, parent, false);

            TransferAdapter.MyViewHolder holder = new TransferAdapter.MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(TransferAdapter.MyViewHolder holder, final int position) {
                holder.id_popup_tv.setText(mData.get(position));
            holder.id_popup_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.OnClickChange(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        //自定义ViewHolder，用于加载图片
        class MyViewHolder extends RecyclerView.ViewHolder {
             TextView id_popup_tv;

            public MyViewHolder(View view) {
                super(view);
                id_popup_tv = (TextView) view.findViewById(R.id.id_popup_tv);

            }
        }
    }
}