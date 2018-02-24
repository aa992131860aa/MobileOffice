package com.mobileoffice.controller.cloud_monitor.frag;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobileoffice.R;
import com.mobileoffice.json.WeatherHourJson;
import com.mobileoffice.utils.DisplayUtil;
import com.mobileoffice.utils.LogUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by 99213 on 2017/7/26.
 */

public class WeatherContentAdapter extends RecyclerView.Adapter<WeatherContentAdapter.MyHolder> {
    private Context mContext;
    private List<WeatherHourJson.ShowapiResBodyBean.HourListBean> mLists;
    private WeatherContentAdapter.HospitalChildListener mHospitalChildListener;
    private String TAG = "WeatherContentAdapter";
    interface HospitalChildListener {
        void OnHospitalParentClick(View thisView, String hospital);
    }

    public void setHospitalParentListener(WeatherContentAdapter.HospitalChildListener listener) {
        mHospitalChildListener = listener;
    }

    public WeatherContentAdapter(Context context, List<WeatherHourJson.ShowapiResBodyBean.HourListBean> lists) {
        mContext = context;
        mLists = lists;

    }

    public void refresh(List<WeatherHourJson.ShowapiResBodyBean.HourListBean> lists) {
        mLists = lists;
        notifyDataSetChanged();
        LogUtil.e(TAG,"lists size:"+lists);
    }

    @Override
    public WeatherContentAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.weather_item, parent, false);
        WeatherContentAdapter.MyHolder myHolder = new WeatherContentAdapter.MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(final WeatherContentAdapter.MyHolder holder, final int position) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat sdfOld = new SimpleDateFormat("yyyyMMddHHmm");
        try {
            holder.tv_time.setText(sdf.format(sdfOld.parse(mLists.get(position).getTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //holder.tv_time.setText(mLists.get(position).getTime());
        holder.tv_content.setText("天气:"+mLists.get(position).getWeather()+",温度:"+mLists.get(position).getTemperature()+"℃");

    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {


        TextView tv_time;
        TextView tv_content;

        public MyHolder(View itemView) {
            super(itemView);


            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);

        }
    }
}