package com.mobileoffice.http;


import com.mobileoffice.entity.Push;
import com.mobileoffice.entity.Transfer;
import com.mobileoffice.entity.Upload;
import com.mobileoffice.utils.LogUtil;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class HttpHelper extends BaseApi {
    private static String TAG = "HttpHelper";
    private HttpService mHttpService = getRetrofit().create(HttpService.class);


    // 获取token
    public Observable<String> getToken(String userId, String userName, String photoUrl) {

        return mHttpService.getToken(userId, userName, photoUrl)
                .map(new HttpResultFunc<String>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    //根据userId获取用户在线状态 1 在线 0 不在线
    public Observable<String> checkOnline(String userId) {
        return mHttpService.checkOnline(userId)
                .map(new HttpResultFunc<String>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    //获取apk版本号
    public Observable<Upload> getUploadAPK() {
        return mHttpService.getUploadAPK()
                .map(new HttpResultFunc<Upload>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    //获取推送未读数
    public Observable<Integer> getUnreadPushNum(String user_info_id) {
        return mHttpService.getUnreadPushNum(user_info_id)
                .map(new HttpResultFunc<Integer>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    //发送验证码
    public Observable<Integer> pushMessage(String code,String phone) {
        return mHttpService.pushMessage(code,phone)
                .map(new HttpResultFunc<Integer>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    //清楚系统消息未读数
    public Observable<Integer> clearUnreadPushMessageNum(String user_info_id){
        return mHttpService.clearUnreadPushMessageNum(user_info_id)
                .map(new HttpResultFunc<Integer>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
//   //获取系统消息列表
//    public Observable<List<Push>> getPushList(int page, int pageSize){
//       return mHttpService.getPushList(page,pageSize)
//               .map(new HttpResultFunc<List<Push>>())
//               .subscribeOn(Schedulers.io())
//               .unsubscribeOn(Schedulers.io())
//               .observeOn(AndroidSchedulers.mainThread());
//    }
//    //获取转运信息
//    public Observable<List<Transfer>> getTransfersStatus(int page, int pageSize,String status){
//        return mHttpService.getTransfersStatus(page,pageSize,status)
//                .map(new HttpResultFunc<List<Transfer>>())
//                .subscribeOn(Schedulers.io())
//                .unsubscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }
//    //获取历史转运信息
//    public Observable<List<Transfer>> getTransfersHistory(int page, int pageSize,String beganDate,String endDate,String organType){
//        return mHttpService.getTransfersHistory(page,pageSize,beganDate,endDate,organType)
//                .map(new HttpResultFunc<List<Transfer>>())
//                .subscribeOn(Schedulers.io())
//                .unsubscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }
    // 导出
//    public Observable<ExportBean> exportPhone(String phone, String tid) {
//        return mHttpService.exportPhone(phone, tid)
//                .map(new_monitor HttpResultFunc<ExportBean>())
//                .subscribeOn(Schedulers.io())
//                .unsubscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }

    /**
     * 用来统一处理status，将data剥离出来返回给subscriber
     */
    private class HttpResultFunc<T> implements Func1<HttpResult<T>, T> {

        @Override
        public T call(HttpResult<T> httpResult) {

            if (!httpResult.getStatus().equals("OK")) {

                LogUtil.e(TAG, "HttpResultFunc1: " + httpResult.getMsg());


                if (httpResult.getStatus().equals("NOT FOUND") && !httpResult.getMsg().equals("")) {
                    throw new HttpException(httpResult.getMsg());
                }
            }

            LogUtil.e(TAG, "HttpResultFunc2: " + httpResult.getMsg());
//            String url = ((Upload)httpResult.getData()).getUrl();
//            LogUtil.e(TAG,url);
            return httpResult.getData();
        }

    }

}
