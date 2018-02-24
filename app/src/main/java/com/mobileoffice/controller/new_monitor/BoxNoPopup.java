package com.mobileoffice.controller.new_monitor;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.mobileoffice.R;
import com.mobileoffice.entity.BoxUse;

import java.lang.reflect.Field;
import java.util.List;

public class BoxNoPopup extends PopupWindow implements BoxNoAdapter.OnRecyclerViewItemClickListener {
    private Context mContext;
    private int statusBarHeight = 0;
    private int mPosition;
    private LinearLayoutManager mLinearLayoutManager;
    RecyclerView rv_box_use;
    BoxNoAdapter mBoxNoAdapter;

    public BoxNoPopup(final Context context, List<BoxUse> boxUses) {

        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.box_use_pop, null);



        this.setContentView(v);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout ll_root = (LinearLayout) v.findViewById(R.id.ll_root);
        //ToastUtil.showToast(DisplayUtil.dip2px(context,50)*2+","+LocalApplication.getInstance().screenH,context);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        rv_box_use = (RecyclerView) v.findViewById(R.id.rv_box_use);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mBoxNoAdapter = new BoxNoAdapter(boxUses, mContext);
        mBoxNoAdapter.setOnItemClickListener(this);
        rv_box_use.setLayoutManager(mLinearLayoutManager);
        rv_box_use.setAdapter(mBoxNoAdapter);


        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(new BitmapDrawable());


    }

    private void recycler() {


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
            this.showAtLocation(parent, Gravity.CENTER, 0, 0);
            //this.showPopupWindow(parent);
        } else {
            this.dismiss();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        mListener.OnClickChange(position);
    }

    @Override
    public void onButtonClick(View view, int position) {

    }


    public interface OnClickChangeListener {
        void OnClickChange(int position);


    }

    public void setOnClickChangeListener(OnClickChangeListener clickChangeListener) {
        mListener = clickChangeListener;
    }

    private OnClickChangeListener mListener;

}