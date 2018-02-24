package com.mobileoffice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.TextView;

import com.mobileoffice.utils.LogUtil;

/**
 * Created by 99213 on 2017/8/28.
 */

public class AnimTextView extends TextView implements Animation.AnimationListener {
    private TextView textView;
    private Thread thread;
    private boolean isStart = false;
    private int flag = 0;
    private Context mContext;
    AnimationSet aset;
    private String TAG = "AnimTextView";

    public AnimTextView(Context context) {
        this(context, null);
    }

    public AnimTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        textView = this;


    }


    public void setTextStatus(String text) {

        if ("转运中 ".equals(text)||"设备未开启".equals(text)) {

            aset = new AnimationSet(true);
            AlphaAnimation aa = new AlphaAnimation(1, 0);
            aa.setDuration(2000);
            aset.addAnimation(aa);
            aa.setAnimationListener(this);
            textView.startAnimation(aset);
            LogUtil.e(TAG, "转运中..." + aset);

        } else {
            textView.clearAnimation();
        }


        this.setText(text);
    }



    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        AnimationSet aset2 = new AnimationSet(true);
        AlphaAnimation aa = new AlphaAnimation(0, 1);
        aa.setDuration(2000);
        aset2.addAnimation(aa);
        textView.startAnimation(aset2);
        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                textView.startAnimation(aset);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
