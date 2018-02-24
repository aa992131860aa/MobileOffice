package com.mobileoffice.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mobileoffice.MainActivity;
import com.mobileoffice.R;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.controller.login.LoginActivity;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;

import java.util.ArrayList;

/**
 * Created by 99213 on 2017/7/17.
 */

public class SkipActivity extends BaseActivity {
    ViewPager vp_skip;
    private ArrayList<View> mListImage;//保存放入viewpager的图片
    private MyViewPagerAdapter myViewPagerAdapter;


    @Override
    protected void initVariable() {
        mListImage = new ArrayList<>();

        View view1 = LayoutInflater.from(this).inflate(R.layout.yindao,null);
        ImageView iv_bg = (ImageView) view1.findViewById(R.id.iv_bg);
        ImageView iv_content = (ImageView) view1.findViewById(R.id.iv_content);
        Button btn_confirm = (Button) view1.findViewById(R.id.btn_confirm);
        btn_confirm.setVisibility(View.GONE);
        mListImage.add(view1);


        View view2 = LayoutInflater.from(this).inflate(R.layout.yindao,null);
         iv_bg = (ImageView) view2.findViewById(R.id.iv_bg);
         iv_content = (ImageView) view2.findViewById(R.id.iv_content);
        btn_confirm = (Button) view2.findViewById(R.id.btn_confirm);
        iv_bg.setImageResource(R.drawable.yindao_2);
        iv_content.setImageResource(R.drawable.yindao_2_1);
        btn_confirm.setVisibility(View.GONE);
        mListImage.add(view2);



        View view3 = LayoutInflater.from(this).inflate(R.layout.yindao,null);

        iv_bg = (ImageView) view3.findViewById(R.id.iv_bg);
        iv_content = (ImageView) view3.findViewById(R.id.iv_content);
        btn_confirm = (Button) view3.findViewById(R.id.btn_confirm);
        iv_bg.setImageResource(R.drawable.yindao_3);
        iv_content.setImageResource(R.drawable.yindao_3_1);
        btn_confirm.setVisibility(View.GONE);

        mListImage.add(view3);



        View view4 = LayoutInflater.from(this).inflate(R.layout.yindao,null);
        iv_bg = (ImageView) view4.findViewById(R.id.iv_bg);
        iv_content = (ImageView) view4.findViewById(R.id.iv_content);
        btn_confirm = (Button) view4.findViewById(R.id.btn_confirm);
        iv_bg.setImageResource(R.drawable.yindao_4);
        iv_content.setImageResource(R.drawable.yindao_4_1);
        btn_confirm.setVisibility(View.VISIBLE);
        mListImage.add(view4);


        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharePreUtils.putBoolean("first",true,SkipActivity.this);
                if(SharePreUtils.getBoolean("login",false,SkipActivity.this)){
                    startActivity(new Intent(SkipActivity.this, MainActivity.class));
                    finish();
                }else {
                    startActivity(new Intent(SkipActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });
        myViewPagerAdapter = new MyViewPagerAdapter();
        vp_skip.setAdapter(myViewPagerAdapter);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.skip);
        vp_skip = (ViewPager) findViewById(R.id.vp_skip);


    }

    @Override
    protected void initData() {

    }
    private class MyViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mListImage.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0==arg1;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO Auto-generated method stub
            container.addView(mListImage.get(position));
            return mListImage.get(position);
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mListImage.get(position));
        }
    }
}
