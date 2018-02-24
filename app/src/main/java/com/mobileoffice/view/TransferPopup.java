package com.mobileoffice.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mobileoffice.R;
import com.mobileoffice.utils.CONSTS;

import java.lang.reflect.Field;

public class TransferPopup extends PopupWindow {
    private Context mContext;
    private int statusBarHeight = 0;
    private int mPosition ;
    public TransferPopup(final Context context,int position,int autoTransfer) {
        mContext = context;
        mPosition = position;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.menu_layout, null);


        this.setContentView(v);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //ToastUtil.showToast(DisplayUtil.dip2px(context,50)*2+","+LocalApplication.getInstance().screenH,context);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        TextView tv_start = (TextView) v.findViewById(R.id.tv_start);
        TextView tv_end = (TextView) v.findViewById(R.id.tv_end);
        TextView tv_stop_transfer = (TextView) v.findViewById(R.id.tv_stop_transfer);
        TextView tv_del_transfer = (TextView) v.findViewById(R.id.tv_del_transfer);
        TextView tv_line = (TextView) v.findViewById(R.id.tv_line);
        if(autoTransfer== CONSTS.AURO_TRANSFER_FINISH_NO){
            tv_del_transfer.setVisibility(View.VISIBLE);
            tv_line.setVisibility(View.VISIBLE);
            tv_del_transfer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.OnDelTransfer(mPosition);
                }
            });
        }else{
            tv_del_transfer.setVisibility(View.GONE);
            tv_line.setVisibility(View.GONE);
        }

        tv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        tv_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        tv_stop_transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.OnClickChange(mPosition);
            }
        });

        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(new BitmapDrawable());


    }
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = mContext.getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }
    /**
     * 显示popupWindow
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
//            this.showAsDropDown(parent, -20, 5);
            this.showAtLocation(parent, Gravity.CENTER,0,0);
            //this.showPopupWindow(parent);
        } else {
            this.dismiss();
        }
    }


    public interface OnClickChangeListener {
        public void OnClickChange(int position);
        public void OnDelTransfer(int position);
    }

    public void setOnClickChangeListener(OnClickChangeListener clickChangeListener) {
        mListener = clickChangeListener;
    }

    private OnClickChangeListener mListener;

}