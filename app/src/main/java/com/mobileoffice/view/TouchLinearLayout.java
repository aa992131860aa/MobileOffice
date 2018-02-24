package com.mobileoffice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.mobileoffice.utils.LogUtil;

/**
 * Created by 99213 on 2017/8/8.
 */

public class TouchLinearLayout extends LinearLayout {
   private String TAG = "TouchLinearLayout";
    public TouchLinearLayout(Context context) {
        this(context, null);
    }

    public TouchLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
       // return super.onInterceptTouchEvent(ev);
        LogUtil.e(TAG,"evX:"+ev.getAction());
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
    //
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        return super.dispatchTouchEvent(ev);
//       // return false;
//    }
}
