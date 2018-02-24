package com.mobileoffice.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.mobileoffice.R;
import com.mobileoffice.utils.DisplayUtil;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.ToastUtil;

/**
 * Created by 99213 on 2017/10/11.
 */

public class DotView extends View {
    private int mWidth;
    private int mHeigth;
    private int mPaintColor;
    private Paint mPaint;
    private int mDotRadius;


    public DotView(Context context) {
        this(context, null);
    }

    public DotView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.DotView, defStyleAttr, 0);
        mPaintColor = typedArray.getColor(R.styleable.DotView_paintColor, getContext().getResources().getColor(R.color.gray));
        mDotRadius = typedArray.getInteger(R.styleable.DotView_dotRadius, DisplayUtil.dip2px(getContext(), 2));
        mPaint = new Paint();
        mPaint.setColor(mPaintColor);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int size = mWidth / (mDotRadius * 5);
        for (int i = 0; i < size; i++) {
            canvas.drawCircle(mDotRadius * 3*(i+1) + i*mDotRadius*2, mHeigth / 2, mDotRadius, mPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeigth = MeasureSpec.getSize(heightMeasureSpec);

    }
}
