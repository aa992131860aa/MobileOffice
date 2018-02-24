package com.mobileoffice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.mobileoffice.utils.LogUtil;

/**
 * Created by 99213 on 2017/8/11.
 */

public class TouchRelative extends RelativeLayout {
   private String TAG = "TouchRelative";
    public TouchRelative(Context context) {
        this(context, null);
    }

    public TouchRelative(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchRelative(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtil.e(TAG,"event:"+event.getX());
        return false;
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        return true;
//    }
//
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        return  false;
//    }
}
