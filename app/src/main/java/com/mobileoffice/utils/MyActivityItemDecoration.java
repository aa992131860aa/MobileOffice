package com.mobileoffice.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2016-09-10.
 */

public class MyActivityItemDecoration extends RecyclerView.ItemDecoration
{
    private static final int[] ATTRS = {android.R.attr.listDivider};
    private Drawable mDivider;
    private Context mContext;
    public MyActivityItemDecoration(Context context)
    {
        mContext = context;
        TypedArray array = context.obtainStyledAttributes(ATTRS);
        // 获取分隔条
        mDivider = array.getDrawable(0);
        array.recycle();
    }
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state)
    {
        super.onDrawOver(c, parent, state);
        int count = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth()-parent.getPaddingRight();
        for(int i = 0; i < count; i++)
        {
            View v = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) v.getLayoutParams();
            int top = v.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left,top,right,bottom);
            mDivider.draw(c);
        }
    }
//    //由于Divider也有长宽高，每一个Item需要向下或者向右偏移
//    @Override
//    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//
//            //画竖线，就是往右偏移一个分割线的宽度
//            outRect.set(DisplayUtil.dip2px(mContext,10), DisplayUtil.dip2px(mContext,5), DisplayUtil.dip2px(mContext,10), DisplayUtil.dip2px(mContext,5));
//
//    }
}