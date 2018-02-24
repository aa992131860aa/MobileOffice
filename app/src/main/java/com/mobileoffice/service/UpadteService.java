package com.mobileoffice.service;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import android.util.Log;
import android.widget.ProgressBar;
import android.widget.RemoteViews;

import com.mobileoffice.R;
import com.mobileoffice.utils.JFileKit;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 99213 on 2017/8/21.
 */

public class UpadteService extends Service {
    private String apkUrl = "";
    private Dialog noticeDialog;// 提示有软件更新的对话框
    private Dialog downloadDialog;// 下载对话框
    //private static final String savePath = "/sdcard/updatedemo/";// 保存apk的文件夹
    private  String savePath = "";// 保存apk的文件夹
    private   String saveFileName = "";
    // 进度条与通知UI刷新的handler和msg常量
    private ProgressBar mProgress;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private int progress;// 当前进度
    private int oldProgress;
    private Thread downLoadThread; // 下载线程
    private boolean interceptFlag = false;// 用户取消下载
    private ProgressBar progressBar;
    private NotificationManager manager;
    private Notification notification;
    Notification.Builder builder;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    // 通知处理刷新界面的handler
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    //mProgress.setProgress(progress);
                   // ToastUtil.showToast("progress:"+progress,UpadteService.this);


                    if(progress>oldProgress){
                       // builder.setProgress(100,progress,false);
                        //builder.setProgress(100,progress,false);
                        notification = builder
                                .setContentTitle("器官云监控")
                                //.setContentText("正在下载,请稍后...")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setProgress(100,progress,false)
                                //.setContent(remoteViews)
                                .setWhen(System.currentTimeMillis())
                                .build();

                        manager.notify(101,notification);
                        Log.e("updateService:","progress:"+progress);
                    }
                    oldProgress = progress;

                   // manager.notify(101,notification);
                    break;
                case DOWN_OVER:
                    //downloadDialog.dismiss();
                    stopForeground(true);
                    installApk();
                    break;
            }
            super.handleMessage(msg);
        }
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.update);
        savePath = JFileKit.getFilePath(this,null);
        //savePath = "/sdcard/updatedemo";// 保存apk的文件夹
        saveFileName = savePath + "/UpdateDemoRelease.apk";
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        apkUrl = intent.getStringExtra("url");

        // 构造一个前台服务
         builder = new Notification.Builder(this.getApplicationContext());

         notification = builder
                .setContentTitle("器官云监控")
                //.setContentText("正在下载,请稍后...")
                .setSmallIcon(R.mipmap.logo)
                .setProgress(100,progress,false)
                //.setContent(remoteViews)
                .setWhen(System.currentTimeMillis())
                .build();
        startForeground(101, notification);// 开始前台服务
        manager.notify(101,notification);

        downloadApk();
        return super.onStartCommand(intent, flags, startId);
    }
    private void downloadApk() {

        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }
    protected void installApk() {
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        //更改引导页是否显示
        SharePreUtils.putBoolean("first",false,this);
        SharePreUtils.getBoolean("login",true,this);

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");// File.toString()会返回路径信息
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            URL url;
            try {
                url = new URL(apkUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream ins = conn.getInputStream();
                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdir();
                }
                String apkFile = saveFileName;
                File ApkFile = new File(apkFile);
                FileOutputStream outStream = new FileOutputStream(ApkFile);
                int count = 0;
                byte buf[] = new byte[1024];
                do {
                    int numread = ins.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);
                    // 下载进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);

                    if (numread <= 0) {
                        // 下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    outStream.write(buf, 0, numread);
                } while (!interceptFlag);// 点击取消停止下载
                outStream.close();
                ins.close();
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("updateService",e.getMessage());
            }
        }
    };
    @Override
    public void onDestroy() {

        stopForeground(true);// 停止前台服务--参数：表示是否移除之前的通知
        super.onDestroy();
    }
}
