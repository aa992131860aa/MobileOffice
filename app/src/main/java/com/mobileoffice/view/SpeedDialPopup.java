package com.mobileoffice.view;

import android.app.Activity;
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
import android.widget.TextView;

import com.mobileoffice.R;
import com.mobileoffice.controller.cloud_monitor.SpeedDialAdapter;
import com.mobileoffice.json.ContactListJson;

import java.util.List;

public class SpeedDialPopup extends PopupWindow implements SpeedDialAdapter.OnRecyclerViewItemClickListener{
    private Context mContext;
    private LinearLayoutManager mGridLayoutManager;
    private SpeedDialAdapter mPostRoleAdapter;
    private RecyclerView rv_post_role;
    private List<ContactListJson.ObjBean> mPostRoles;
    private String mPostRole;


    public SpeedDialPopup(final Activity context, List<ContactListJson.ObjBean> postRoles, String postRole) {
        mContext = context;
        mPostRole = postRole;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.post_role, null);
        mPostRoles = postRoles;

        LinearLayout ll_root = (LinearLayout) v.findViewById(R.id.ll_root);
        TextView tv_title = (TextView) v.findViewById(R.id.tv_title);
        rv_post_role = (RecyclerView) v.findViewById(R.id.rv_post_role);
        this.setContentView(v);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(new BitmapDrawable());
        ll_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        tv_title.setText("选择呼叫对象");
        recycler(postRoles,postRole);


    }

    private void recycler(List<ContactListJson.ObjBean> postRoles,String postRole) {
        mGridLayoutManager = new LinearLayoutManager(mContext);
        rv_post_role.setLayoutManager(mGridLayoutManager);
        mPostRoleAdapter = new SpeedDialAdapter(postRoles, mContext,postRole);
        rv_post_role.setAdapter(mPostRoleAdapter);
        mPostRoleAdapter.setOnItemClickListener(this);


    }

    /**
     * 显示popupWindow
     */

    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
//            this.showAsDropDown(parent, -20, 5);
            this.showAtLocation(parent, Gravity.CENTER, 0, 0);
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
        public void OnClickChange(int position);
    }

    public void setOnClickChangeListener(OnClickChangeListener clickChangeListener) {
        mListener = clickChangeListener;
    }

    private OnClickChangeListener mListener;

}