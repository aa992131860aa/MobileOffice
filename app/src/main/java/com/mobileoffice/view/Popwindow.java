package com.mobileoffice.view;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mobileoffice.R;
import com.mobileoffice.view.com.bigkoo.pickerview.TimePickerView;


/**
 * Created by Administrator on 2015/11/1.
 */
public class Popwindow {
    private Activity mActivity;

    View bagView;

    public Popwindow(Activity activity) {
        mActivity = activity;

    }

    public Popwindow(Activity activity, View view) {
        mActivity = activity;
        bagView = view;
    }



    public void showbagPopWindow(View parent) {
        LayoutInflater inflater = (LayoutInflater)
                mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View vPopWindow = inflater.inflate(R.layout.history_date_pop, null, false);
        LinearLayout ll_main = (LinearLayout) vPopWindow.findViewById(R.id.ll_main);
        DisplayMetrics dm = new DisplayMetrics();
        //获取屏幕信息
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        int screenWidth = dm.widthPixels;

        int screenHeigh = dm.heightPixels;


        final PopupWindow popWindow = new PopupWindow(vPopWindow, screenWidth - 20, ViewGroup.LayoutParams.WRAP_CONTENT, true);









        //在PopupWindow里面就加上下面代码，让键盘弹出时，不会挡住pop窗口。
        popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //点击空白处时，隐藏掉pop窗口
        popWindow.setFocusable(true);
//        popWindow.setBackgroundDrawable(mActivity.getResources().getDrawable(
//                android.R.color.holo_blue_bright));
        //设置透明度
        backgroundAlpha(0.3f);

        //添加pop窗口关闭事件
        popWindow.setOnDismissListener(new poponDismissListener());
        popWindow.showAsDropDown(parent);

    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        mActivity.getWindow().setAttributes(lp);
    }

    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     *
     * @author cg
     */
    class poponDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            //Log.v("List_noteTypeActivity:", "我是关闭事件");
            backgroundAlpha(1f);
        }

    }
}
