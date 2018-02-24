package com.mobileoffice.controller.new_monitor;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobileoffice.json.TransferJson;
import com.mobileoffice.json.TransferingJson;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.view.DialogMaker;

/**
 * Created by 99213 on 2017/7/1.
 */

public abstract class NewMonitorBaseFragment extends Fragment implements DialogMaker.DialogCallBack{
    public static TransferJson.ObjBean objBean = new TransferJson.ObjBean();
    protected View mRootView;
    public Context mContext;
    protected boolean isVisible;
    private boolean isPrepared;
    protected Dialog dialog;
     int roleId;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            lazyLoad();
        } else {
            isVisible = false;
            onInvisible();
        }
    }


    /**
     * 等待对话框
     *
     * @author blue
     */
    public Dialog showWaitDialog(String msg, boolean isCanCancelabel, Object tag)
    {
        if (null == dialog || !dialog.isShowing())
        {
            dialog = DialogMaker.showCommenWaitDialog(getActivity(), msg, this, isCanCancelabel, tag);
        }
        return dialog;
    }
    /**
     * 关闭对话框
     *
     * @author blue
     */
    public void dismissDialog()
    {
        if (null != dialog && dialog.isShowing())
        {
            dialog.dismiss();
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        roleId = SharePreUtils.getInt("roleId", -1, getActivity());
        setHasOptionsMenu(true);
    }


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        if (mRootView == null) {
//            mRootView = initView();
//        }
//        return mRootView;
//    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        isPrepared = true;
        lazyLoad();
    }

    /**
     * 懒加载
     */
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }

        initData();
    }

    // 不可见
    protected void onInvisible() {

    }

//    public abstract View initView();

    public abstract void initData();
    @Override
    public void onButtonClicked(Dialog dialog, int position, Object tag)
    {
    }

    @Override
    public void onCancelDialog(Dialog dialog, Object tag)
    {
    }
}
