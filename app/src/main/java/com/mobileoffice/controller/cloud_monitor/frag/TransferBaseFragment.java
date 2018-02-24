package com.mobileoffice.controller.cloud_monitor.frag;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.mobileoffice.json.TransferRecordJson;
import com.mobileoffice.view.DialogMaker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 99213 on 2017/8/9.
 */

public   abstract class TransferBaseFragment extends Fragment implements DialogMaker.DialogCallBack{
    public static List<TransferRecordJson.ObjBean> TRANSFER_RECORD = new ArrayList<>();
    public static List<TransferRecordJson.ObjBean> TRANSFER_RECORD_TEN = new ArrayList<>();
    public static List<TransferRecordJson.ObjBean> TRANSFER_RECORD_SAMPLE = new ArrayList<>();
    protected View mRootView;
    public Context mContext;
    protected boolean isVisible;
    private boolean isPrepared;
    protected Dialog dialog;

    //显示的数量
    public int COUNT = 8;
    //全屏显示的数量
    public int COUNT_ALL = 16;




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
