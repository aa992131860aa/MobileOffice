package com.mobileoffice.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.dsw.calendar.component.MonthView;
import com.dsw.calendar.theme.DefaultDayTheme;
import com.dsw.calendar.theme.IDayTheme;
import com.dsw.calendar.views.GridCalendarView;
import com.mobileoffice.R;
import com.mobileoffice.application.LocalApplication;
import com.mobileoffice.utils.DisplayUtil;

import java.util.ArrayList;

public class CalendarPopup extends PopupWindow {

    int position = 0;

    public CalendarPopup(final Activity context) {
        position = 0;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.calendar_pop, null);
        ImageView iv_calendar_pop_start_close = (ImageView) v.findViewById(R.id.iv_calendar_pop_start_close);
        ImageView iv_calendar_pop_end_close = (ImageView) v.findViewById(R.id.iv_calendar_pop_end_close);
        final TextView tv_calendar_pop_start = (TextView) v.findViewById(R.id.tv_calendar_pop_start);
        final TextView tv_calendar_pop_end = (TextView) v.findViewById(R.id.tv_calendar_pop_end);
        final LinearLayout ll_calendar_pop_start = (LinearLayout) v.findViewById(R.id.ll_calendar_pop_start);
        final LinearLayout ll_calendar_pop_end = (LinearLayout) v.findViewById(R.id.ll_calendar_pop_end);
        TextView tv_calendar_pop_ok = (TextView) v.findViewById(R.id.tv_calendar_pop_ok);
        GridCalendarView gcv_calendar_pop = (GridCalendarView) v.findViewById(R.id.gcv_calendar_pop);

        IDayTheme dayTheme = new DefaultDayTheme();
        gcv_calendar_pop.setDayTheme(dayTheme);

        iv_calendar_pop_start_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_calendar_pop_start.setText("");
            }
        });
        iv_calendar_pop_end_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_calendar_pop_end.setText("");
            }
        });
        gcv_calendar_pop.setDateClick(new MonthView.IDateClick() {
            @Override
            public void onClickOnDate(int year, int month, int day) {
                String monthStr = month + "";
                String dayStr = day + "";
                if (month < 10) {
                    monthStr = "0" + month;
                }
                if (day < 10) {
                    dayStr = "0" + day;
                }
                if (position == 0) {
                    tv_calendar_pop_start.setText(year + "-" + monthStr + "-" + dayStr);
                    //跳转第二个
                    ll_calendar_pop_end.setBackgroundResource(R.drawable.edit_border_high);
                    ll_calendar_pop_start.setBackgroundResource(R.drawable.edit_border_white_font9);
                    position = 1;
                } else if (position == 1) {

                    tv_calendar_pop_end.setText(year + "-" + monthStr + "-" + dayStr);
                }
            }
        });
        tv_calendar_pop_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position = 0;
                ll_calendar_pop_start.setBackgroundResource(R.drawable.edit_border_high);
                ll_calendar_pop_end.setBackgroundResource(R.drawable.edit_border_white_font9);


            }
        });
        tv_calendar_pop_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position = 1;
                ll_calendar_pop_end.setBackgroundResource(R.drawable.edit_border_high);
                ll_calendar_pop_start.setBackgroundResource(R.drawable.edit_border_white_font9);
            }
        });
        tv_calendar_pop_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.OnClickChange(tv_calendar_pop_start.getText().toString().trim(), tv_calendar_pop_end.getText().toString().trim());
            }
        });
        this.setContentView(v);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(new BitmapDrawable());


    }

    /**
     * 显示popupWindow
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
//            this.showAsDropDown(parent, -20, 5);
            this.showAsDropDown(parent, 0, 1);
        } else {
            this.dismiss();
        }
    }


    public interface OnClickChangeListener {
        public void OnClickChange(String startTime, String endTime);
    }

    public void setOnClickChangeListener(OnClickChangeListener clickChangeListener) {
        mListener = clickChangeListener;
    }

    private OnClickChangeListener mListener;

}