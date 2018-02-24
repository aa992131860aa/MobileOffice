package com.mobileoffice.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.mobileoffice.R;

/**
 * Created by 99213 on 2017/6/13.
 */

public class SeekBarHeart extends View {
    private String TAG = "SeekBarHeart";
    private int width;
    private int height;
    private int count;
    private int mArcRadius = 1;
    private int mRect = 1;
    private int space = 2;
    private Context mContext;

    //转运状态
    private boolean done = false;
    private int mHeartId = R.drawable.cloud_1index_2now;
    private boolean mIsMove = false;

    public SeekBarHeart(Context context) {
        this(context, null);
    }

    public SeekBarHeart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekBarHeart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        CONSTS.HEART_START = true;
        mContext = context;
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SeekBarHeart, defStyleAttr, 0);
        int n = typedArray.getIndexCount();
        for (int i = 0; i < n; i++) {
            int key = typedArray.getIndex(i);
            if (key == R.styleable.SeekBarHeart_count) {
                count = typedArray.getInt(key, 0);
                break;
            }
        }
        LogUtil.e(TAG, "count:" + count);

    }

    public void setDone(boolean done) {
        this.done = done;
        invalidate();
    }

    public void setMove(boolean move) {
        mIsMove = move;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        width = widthSize;
        height = heightSize;
        setMeasuredDimension(width, height);
    }
    private Bitmap imageColorToGray(int id) {

        Bitmap originImg = BitmapFactory.decodeResource(mContext.getResources(), id);
        Bitmap grayImg = Bitmap.createBitmap(originImg.getWidth(), originImg.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(grayImg);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorMatrixFilter);
        canvas.drawBitmap(originImg, 0, 0, paint);
          return grayImg;

    }
    public void setHeartMove(int id){
        mHeartId = id;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Bitmap bitmap = ((BitmapDrawable) mContext.getResources().getDrawable(mHeartId)).getBitmap();

        mArcRadius = height * 28 / 38 / 2;
        mRect = height * 12 / 38 / 2;
        LogUtil.e(TAG, "height:" + height + ",mArcRadius:" + mArcRadius + ",mRect:" + mRect);
        Paint paintOut = new Paint();
        paintOut.setStyle(Paint.Style.STROKE);
        paintOut.setAntiAlias(true);
        paintOut.setStrokeWidth(2);
        if (done) {
           bitmap = imageColorToGray(mHeartId);
            paintOut.setColor(Color.parseColor("#666666"));
        } else {
            paintOut.setColor(Color.parseColor("#f72361"));
        }
        //canvas.drawRect(0,0,width,height,paintOut);
        Paint p = new Paint();

        // canvas.drawRect(1,1,width-100,20,p);
        //左边
        int angle = (int) Math.toDegrees(Math.asin((mRect)
                / (float) mArcRadius));
        RectF rectF = new RectF(space, height / 2 - mArcRadius, mArcRadius * 2 + space, mArcRadius + height / 2);
        canvas.drawArc(rectF, angle, 360 - 2 * angle, false, paintOut);
        //画线
        int startTopPoint = (int) (mArcRadius + space + Math.sqrt(mArcRadius * mArcRadius - mRect * mRect));
        int endTopPoint = height / 2 - mRect;
        int endBottomPoint = height / 2 + mRect;
        canvas.drawLine(startTopPoint, endTopPoint, width - startTopPoint + 2, endTopPoint, paintOut);
        canvas.drawLine(startTopPoint, endBottomPoint, width - startTopPoint + 2, endBottomPoint, paintOut);
        Paint paintStart = new Paint();
        paintStart.setStyle(Paint.Style.FILL);
        paintStart.setAntiAlias(true);
        if (done) {
            paintStart.setColor(Color.parseColor("#666666"));
        } else {
            paintStart.setColor(Color.parseColor("#F88AAA"));
        }

        canvas.drawOval(rectF, paintStart);

        //画填充矩形
        LinearGradient lg ;

        if (done) {

            lg = new LinearGradient(0, 0, width / 100 * count + bitmap.getWidth() / 4, mRect, Color.parseColor("#666666"), Color.parseColor("#666666"), Shader.TileMode.REPEAT);
        } else {
            if(count>=99){
                lg = new LinearGradient(0, 0, width / 100 * count  + bitmap.getWidth() / 4, mRect, Color.parseColor("#F88AAA"), Color.parseColor("#f72361"), Shader.TileMode.REPEAT);
            }else {
                lg = new LinearGradient(0, 0, width / 100 * count + bitmap.getWidth() / 4, mRect, Color.parseColor("#F88AAA"), Color.parseColor("#f72361"), Shader.TileMode.REPEAT);
            }
        }

        paintStart.setShader(lg);
        if(count>=99) {
            canvas.drawRect(startTopPoint, endTopPoint, width / 100 * count + bitmap.getWidth() / 4 - 10, endBottomPoint, paintStart);
        }else{
            canvas.drawRect(startTopPoint, endTopPoint, width / 100 * count + bitmap.getWidth() / 4, endBottomPoint, paintStart);

        }
        if(count>98){
            paintOut.setStyle(Paint.Style.FILL);
            RectF rectFRight = new RectF(width - startTopPoint, height / 2 - mArcRadius, width - space, mArcRadius + height / 2);
            canvas.drawArc(rectFRight, 180 + angle, 360 - 2 * angle, false, paintOut);
        }else {
            //右边圆
            RectF rectFRight = new RectF(width - startTopPoint, height / 2 - mArcRadius, width - space, mArcRadius + height / 2);
            canvas.drawArc(rectFRight, 180 + angle, 360 - 2 * angle, false, paintOut);
        }

        //画图片
//        if (CONSTS.HEART_START) {
//            if (count < flag) {
//                if (animNum % 16 == 0) {
//                   // canvas.drawBitmap(bitmap, width / 100 * count, 0, paintStart);
//                } else {
//
//                }
//            } else {
//                if (animNum % 2 == 0) {
//                    canvas.drawBitmap(bitmap, width / 100 * count, 0, paintStart);
//                } else {
//
//                }
//            }
//        } else {
//            canvas.drawBitmap(bitmap, width / 100 * count, 0, paintStart);
//        }
        //   if(isStart){

        // }else{
        if(mIsMove){

        }else {
            canvas.drawBitmap(bitmap, width / 100 * count > bitmap.getWidth() ? (width / 100 * count - bitmap.getWidth() / 2) : width / 100 * count, -DisplayUtil.dip2px(mContext,5), paintStart);
        }
        // }
        LogUtil.e(TAG, "bitmap:" + bitmap.getWidth());
    }

    public void setCount(int count, String organSeg) {


        this.count = count;
    }

    public void startAnim() {

    }

    public void stopAnim() {

        CONSTS.HEART_START = false;
    }
}
