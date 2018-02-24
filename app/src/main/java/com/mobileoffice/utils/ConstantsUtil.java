package com.mobileoffice.utils;


import android.content.Context;

import com.google.gson.Gson;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.YunBaJson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import static com.mobileoffice.application.LocalApplication.getContext;

/**
 * 系统常量表
 * 
 * @author blue
 * 
 */
public class ConstantsUtil
{
	// 服务器地址
	public static String SERVER_URL = "";

	/**
	 * 用户类型
	 * 
	 * @author andrew
	 * 
	 */
	public enum MemberType
	{
		/**
		 * 管理者
		 */
		administrator,
		/**
		 * 销售代表
		 */
		delegate,
	}
	public static void sendTransferStatus(String pAlias, String title, String content, Context pContext){
		RequestParams params = new RequestParams(URL.YUN_BA);
		params.setAsJsonContent(true);
		YunBaJson yunBaJson = new YunBaJson();
		yunBaJson.setMethod("publish_to_alias");
		yunBaJson.setAppkey(CONSTS.PAD_YUN_BA_APPKEY);
		yunBaJson.setSeckey(CONSTS.PAD_YUN_BA_SECKEY);
		yunBaJson.setAlias(pAlias);
		yunBaJson.setMsg(content);

		YunBaJson.OptsBean optsBean = new YunBaJson.OptsBean();
		optsBean.setQos(1);
		optsBean.setTime_to_live(36000);

		YunBaJson.OptsBean.ApnJsonBean apnJsonBean =  new YunBaJson.OptsBean.ApnJsonBean();


		YunBaJson.OptsBean.ApnJsonBean.ApsBean apsBean = new YunBaJson.OptsBean.ApnJsonBean.ApsBean();
		apsBean.setAlert("器官云监控");
		apsBean.setBadge(3);
		apsBean.setSound("bingbong.aiff");

		apnJsonBean.setAps(apsBean);




		YunBaJson.OptsBean.ThirdPartyPushBean thirdPartyPushBean = new YunBaJson.OptsBean.ThirdPartyPushBean();
		thirdPartyPushBean.setNotification_title(title);
		thirdPartyPushBean.setNotification_content(content);

		optsBean.setThird_party_push(thirdPartyPushBean);
		optsBean.setApn_json(apnJsonBean);

		yunBaJson.setOpts(optsBean);
		String json = new Gson().toJson(yunBaJson);

		params.setBodyContent(json);

		x.http().post(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				//ToastUtil.showToast(result,getContext());
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {

			}

			@Override
			public void onCancelled(CancelledException cex) {

			}

			@Override
			public void onFinished() {

			}
		});
	}

}
