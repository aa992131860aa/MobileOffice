package com.mobileoffice.controller.me;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.mobileoffice.R;
import com.mobileoffice.controller.BaseActivity;
import com.shizhefei.view.largeimage.LargeImageView;

/**
 * Created by 99213 on 2017/8/14.
 */

public class UseActivity extends BaseActivity {
    private LargeImageView largeImageView;
    private LinearLayout ll_back;

    @Override
    protected void initVariable() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.use);

        largeImageView = (LargeImageView) findViewById(R.id.imageView);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //加载普通大小图片
        try {
            largeImageView.setImage(R.drawable.mine_3guide);
        }catch(OutOfMemoryError o){

        }


    }

    @Override
    protected void initData() {

    }
}
