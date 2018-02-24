package com.mobileoffice.utils;

import android.content.Context;
import android.widget.Toast;

import com.mobileoffice.application.LocalApplication;


public final class ToastUtil {
    private static Toast toast;

    private ToastUtil() {
    }

    /**
     * 可以连续弹吐司，不用等上个吐司消失
     */
    public static void showToast(String text, Context context) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        }
        toast.setText(text);
        toast.show();
    }

}
