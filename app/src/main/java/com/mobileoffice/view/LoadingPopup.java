package com.mobileoffice.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.mobileoffice.R;

import java.lang.reflect.Field;

public class LoadingPopup extends PopupWindow {
    private Context mContext;
    private int statusBarHeight = 0;
    public LoadingPopup(final Activity context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.new_monitor_pop, null);


        this.setContentView(v);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //ToastUtil.showToast(DisplayUtil.dip2px(context,50)*2+","+LocalApplication.getInstance().screenH,context);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

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
            this.showAsDropDown(parent, 0, 0);
        } else {
            this.dismiss();
        }
    }


    public interface OnClickChangeListener {
        public void OnClickChange(int position);
    }

    public void setOnClickChangeListener(OnClickChangeListener clickChangeListener) {
        mListener = clickChangeListener;
    }

    private OnClickChangeListener mListener;

}