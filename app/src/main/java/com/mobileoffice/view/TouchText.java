package com.mobileoffice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by 99213 on 2017/8/8.
 */

public class TouchText extends TextView {
    public TouchText(Context context) {
        this(context,null);
    }

    public TouchText(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TouchText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

}
