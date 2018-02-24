package com.mobileoffice.controller.cloud_monitor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnDrawListener;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.joanzapata.pdfview.listener.OnPageChangeListener;
import com.mobileoffice.R;
import com.mobileoffice.application.LocalApplication;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.ToastUtil;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;

import java.io.File;

/**
 * Created by 99213 on 2017/9/4.
 */

public class PdfActivity extends BaseActivity implements OnPageChangeListener
        ,OnLoadCompleteListener, OnDrawListener ,View.OnClickListener{
    private PDFView pdfView;
    private LinearLayout ll_back;
    private TextView tv_title;
    private PopupWindow pop;
    private LinearLayout ll_popup;
    private LinearLayout ll_setting;
    private String url;
    private String organSeg;
    private TextView tv_line;
    private LinearLayout ll_photo;
    @Override
    protected void initVariable() {
        String path = getIntent().getStringExtra("path");
         organSeg = getIntent().getStringExtra("organSeg");


        //ToastUtil.showToast(""+path,this);
        if(path!=null) {
            displayFromFile(path);
            tv_title.setText("器官段号"+organSeg+".pdf");
        }
    }
    private void shareWechart() {
        if (!LocalApplication.api.isWXAppInstalled()) {
            ToastUtil.showToast("您还未安装微信客户端", this
            );
            return;
        }

        url = "http://www.lifeperfusor.com:8080/transbox/download/"+organSeg+".pdf";
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);

        msg.title = tv_title.getText().toString();
        msg.description = url;


        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.logo);

        msg.setThumbImage(thumb);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        LocalApplication.api.sendReq(req);
        CONSTS.WECHAT_SHARE_LOGIN = 1;
    }
    /****
     * 头像提示框
     */

    public void showPopupWindow() {
        pop = new PopupWindow(this);
        View view = getLayoutInflater().inflate(R.layout.item_popupwindows,
                null);
        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
        pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP){
            pop.setElevation(10f);
        }

        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
        Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
        Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);
        tv_line = (TextView) view.findViewById(R.id.tv_line);
        ll_photo = (LinearLayout) view.findViewById(R.id.ll_photo);

        tv_line.setVisibility(View.GONE);
        ll_photo.setVisibility(View.GONE);
        //bt2.setVisibility(View.GONE);
        bt1.setText("分享微信好友");

        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                shareWechart();
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });

        bt3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
    }
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.pdf);
        pdfView = (PDFView) findViewById(R.id.pdfView);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        ll_setting = (LinearLayout) findViewById(R.id.ll_setting);

        ll_back.setOnClickListener(this);
        ll_setting.setOnClickListener(this);

    }

    @Override
    protected void initData() {

    }
    private void displayFromAssets(String assetFileName ) {
        pdfView.fromAsset(assetFileName)   //设置pdf文件地址
                .defaultPage(1)         //设置默认显示第1页
                .onPageChange(this)     //设置翻页监听
                .onLoad(this)           //设置加载监听
                .onDraw(this)            //绘图监听
                .showMinimap(false)     //pdf放大的时候，是否在屏幕的右上角生成小地图
                .swipeVertical( false )  //pdf文档翻页是否是垂直翻页，默认是左右滑动翻页
                .enableSwipe(true)   //是否允许翻页，默认是允许翻页
//                 .pages()  //把 5 过滤掉
                .load();
    }

    private void displayFromFile( String path ) {
        File file = new File(path);
        pdfView.fromFile(file)   //设置pdf文件地址
                .defaultPage(6)         //设置默认显示第1页
                .onPageChange(this)     //设置翻页监听
                .onLoad(this)           //设置加载监听
                .onDraw(this)            //绘图监听
                .showMinimap(false)     //pdf放大的时候，是否在屏幕的右上角生成小地图
                .swipeVertical( false )  //pdf文档翻页是否是垂直翻页，默认是左右滑动翻页
                .enableSwipe(true)   //是否允许翻页，默认是允许翻
                // .pages( 2 ，5  )  //把2  5 过滤掉
                .load();
    }

    /**
     * 翻页回调
     * @param page
     * @param pageCount
     */
    @Override
    public void onPageChanged(int page, int pageCount) {
        //Toast.makeText( PdfActivity.this , "page= " + page +" pageCount= " + pageCount , Toast.LENGTH_SHORT).show();
    }

    /**
     * 加载完成回调
     * @param nbPages  总共的页数
     */
    @Override
    public void loadComplete(int nbPages) {
       // Toast.makeText( PdfActivity.this ,  "加载完成" + nbPages  , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
        // Toast.makeText( MainActivity.this ,  "pageWidth= " + pageWidth + "
        // pageHeight= " + pageHeight + " displayedPage="  + displayedPage , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_back:
                finish();
                break;
            case R.id.ll_setting:
                showPopupWindow();
                ll_popup.startAnimation(AnimationUtils.loadAnimation(
                        this, R.anim.activity_translate_in));
                pop.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                break;

        }
    }
}
