package com.mobileoffice.http;


import com.mobileoffice.entity.Push;
import com.mobileoffice.entity.Transfer;
import com.mobileoffice.entity.Upload;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface HttpService {
    // 获取聊天的token
    @GET("rongCreate")
    Observable<HttpResult<String>> getToken(@Query("userId") String userId,
                                            @Query("userName") String userName,
                                            @Query("photoUrl") String photoUrl);

    //根据userId获取用户在线状态 1 在线 0 不在线
    @GET("sendSystemMessage")
    Observable<HttpResult<String>> checkOnline(@Query("userId") String userId);

    //获取apk版本号
    @GET("uploadAPK")
    Observable<HttpResult<Upload>> getUploadAPK();

    //获取push未读数
    @GET("getUnreadPushNum")
    Observable<HttpResult<Integer>> getUnreadPushNum(@Query("user_info_id") String user_info_id);

    //发送验证码
    @GET("pushMessage")
    Observable<HttpResult<Integer>> pushMessage(@Query("code") String code, @Query("phone") String phone);

    //清除系统消息未读数
    @GET("clearUnreadPushMessageNum")
    Observable<HttpResult<Integer>> clearUnreadPushMessageNum(@Query("user_info_id") String user_info_id);

    //获取系统消息列表
    @GET("getPushList")
    Observable<HttpResult<List<Push>>> getPushList(@Query("page") int page, @Query("pageSize") int pageSize);

    //获取系统消息列表
    @GET("getTransfersStatus")
    Observable<HttpResult<List<Transfer>>> getTransfersStatus(@Query("page") int page,
                                                              @Query("pageSize") int pageSize,
                                                              @Query("status") String status);

    //获取系统历史消息列表
    @GET("getTransfersSqlAndroid")
    Observable<HttpResult<List<Transfer>>> getTransfersHistory(@Query("start") int page,
                                                               @Query("number") int pageSize,
                                                               @Query("beginDate") String beginDate,
                                                               @Query("endDate") String endDate,
                                                               @Query("organType") String organType);



}
