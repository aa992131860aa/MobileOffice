package com.mobileoffice.controller.message;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

import com.google.gson.Gson;
import com.mobileoffice.R;
import com.mobileoffice.controller.cloud_monitor.TransferDetailActivity;
import com.mobileoffice.controller.message.contact.ContactPersonInfoActivity;
import com.mobileoffice.entity.Transfer;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.ContactSearchJson;
import com.mobileoffice.json.PersonInfoJson;
import com.mobileoffice.json.PersonInfosJson;
import com.mobileoffice.json.TransferJson;
import com.mobileoffice.json.WechatJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.DisplayUtil;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;
import com.mobileoffice.view.DialogMaker;

import io.rong.common.RLog;
import io.rong.imkit.RongContext;
import io.rong.imkit.R.bool;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imkit.mention.RongMentionManager;
import io.rong.imkit.model.ConversationKey;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imkit.utilities.RongUtils;
import io.rong.imkit.utils.RongDateUtils;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imkit.widget.DebouncedOnClickListener;
import io.rong.imkit.widget.ProviderContainerView;

import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imkit.widget.provider.IContainerItemProvider.MessageProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.OperationCallback;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.PublicServiceProfile;
import io.rong.imlib.model.ReadReceiptInfo;
import io.rong.imlib.model.UnknownMessage;
import io.rong.imlib.model.UserInfo;
import io.rong.imlib.model.Conversation.ConversationType;
import io.rong.imlib.model.Message.MessageDirection;
import io.rong.imlib.model.Message.SentStatus;
import io.rong.message.TextMessage;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.Serializable;
import java.util.List;

public class MessageListAdapter extends BaseAdapter<UIMessage> {
    private static final String TAG = "MessageListAdapter";
    private LayoutInflater mInflater;
    private Context mContext;
    private OnItemHandlerListener mOnItemHandlerListener;
    boolean evaForRobot = false;
    boolean robotMode = true;
    private boolean timeGone = false;
    TransferJson.ObjBean objBean = null;
    private Dialog dialog;

    public MessageListAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(this.mContext);
    }

    public void setOnItemHandlerListener(OnItemHandlerListener onItemHandlerListener) {
        this.mOnItemHandlerListener = onItemHandlerListener;
    }

    public long getItemId(int position) {
        UIMessage message = (UIMessage) this.getItem(position);
        return message == null ? -1L : (long) message.getMessageId();
    }

    private void loadTransfer() {

    }
    private void loadPhoneData(final String phone,final String contactPhone) {
        showWaitDialog(mContext.getResources().getString(R.string.loading),true,"loading");
        RequestParams params = new RequestParams(URL.USER);
        params.addQueryStringParameter("action", "personInfos");
        params.addQueryStringParameter("phone", phone);
        params.addQueryStringParameter("contactPhone",contactPhone);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                PersonInfosJson wechatJson = gson.fromJson(result, PersonInfosJson.class);
                LogUtil.e(TAG,"result:"+result);
                if (wechatJson != null && wechatJson.getResult() == CONSTS.SEND_OK) {


                    List<PersonInfosJson.ObjBean> objBeans = wechatJson.getObj();
                    Intent intent = new Intent(mContext, ContactPersonInfoActivity.class);
                    //intent.putExtra("type","sortAdapter");
                    ContactSearchJson.ObjBean pObjBean = new ContactSearchJson.ObjBean();
                    int isFriend = 1;
                    LogUtil.e(TAG,"isFriendSIze:"+objBeans.size());
                    for(int i=0;i<objBeans.size();i++){
                        LogUtil.e(TAG,"isFriend:"+objBeans.get(i).getUsersId()+","+ objBeans.get(i).getUsersOtherId());
                        if(objBeans.get(i).getUsersId().equals(objBeans.get(i).getUsersOtherId())){
                            isFriend = 0;
                            break;
                        }
                    }
                    if(objBeans.size()>0) {
                        PersonInfosJson.ObjBean objBean = objBeans.get(0);
                        pObjBean.setBind(objBean.getBind());
                        pObjBean.setName(objBean.getHospital_name());
                        pObjBean.setTrue_name(objBean.getTrue_name());
                        pObjBean.setPhone(contactPhone);
                        pObjBean.setOther_id(Integer.parseInt(objBean.getUsersId()));
                        pObjBean.setIs_upload_photo(objBean.getIs_upload_photo());
                        pObjBean.setPhoto_url(objBean.getPhoto_url());
                        pObjBean.setWechat_url(objBean.getWechat_url());
                        pObjBean.setIs_friend(isFriend);
                        intent.putExtra("contactPerson", pObjBean);
                        LogUtil.e(TAG,"result:contactPerson");
                        mContext.startActivity(intent);
                    }



                } else {
                    ToastUtil.showToast("网络错误,请重试", mContext);
                }
                dismissDialog();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.showToast("网络错误,请重试", mContext);
                dismissDialog();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
    protected View newView(Context context, int position, ViewGroup group) {
        LogUtil.e(TAG,"message:"+mList.get(position).getTextMessageContent()+",position:"+position+",hsitory:"+mList.get(position).getIsHistoryMessage()+",type:"+mList.get(position).getConversationType().getName());


        if (mList.get(position).getTextMessageContent() != null && "【系统消息】转运已经准备好。".equals(mList.get(position).getTextMessageContent().toString()) && position == 0||("group".equals(mList.get(position).getConversationType().getName())&&position==0)) {
            View result = this.mInflater.inflate(R.layout.transfer_info, (ViewGroup) null);
            final TextView tv_transfer_info_organSeg = (TextView) result.findViewById(R.id.tv_transfer_info_organSeg);
            final TextView tv_transfer_info_time = (TextView) result.findViewById(R.id.tv_transfer_info_time);
            final TextView tv_transfer_info_start = (TextView) result.findViewById(R.id.tv_transfer_info_start);
            final TextView tv_transfer_info_end = (TextView) result.findViewById(R.id.tv_transfer_info_end);
            final TextView tv_transfer_info_distance = (TextView) result.findViewById(R.id.tv_transfer_info_distance);
            final TextView tv_transfer_info_organ = (TextView) result.findViewById(R.id.tv_transfer_info_organ);
            final TextView tv_transfer_info_blood = (TextView) result.findViewById(R.id.tv_transfer_info_blood);
            final TextView tv_transfer_info_true_name = (TextView) result.findViewById(R.id.tv_transfer_info_true_name);
            final TextView tv_transfer_info_phone = (TextView) result.findViewById(R.id.tv_transfer_info_phone);
            final TextView tv_transfer_info_contact_name = (TextView) result.findViewById(R.id.tv_transfer_info_contact_name);
            final TextView tv_transfer_info_contact_phone = (TextView) result.findViewById(R.id.tv_transfer_info_contact_phone);
            result.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent();
                    intent.putExtra("transfer",  objBean);
                    intent.setClass(mContext, TransferDetailActivity.class);
                    mContext.startActivity(intent);
                }
            });
            if (objBean != null) {
                tv_transfer_info_organSeg.setText(objBean.getOrganSeg());
                tv_transfer_info_time.setText(objBean.getGetTime().split(" ")[0]);
                tv_transfer_info_start.setText(objBean.getFromCity().split("市")[0]);
                tv_transfer_info_end.setText(objBean.getToHospName().split("市")[0]);
                tv_transfer_info_distance.setText("共" + objBean.getDistance().split("\\.")[0] + "km");
                tv_transfer_info_organ.setText(objBean.getOrgan());
                tv_transfer_info_blood.setText(objBean.getBlood() + "型");
                tv_transfer_info_true_name.setText(objBean.getTrueName());
                tv_transfer_info_phone.setText(objBean.getPhone());
                tv_transfer_info_contact_name.setText(objBean.getContactName());
                tv_transfer_info_contact_phone.setText(objBean.getContactPhone());
                LogUtil.e(TAG, "organSeg:" + objBean.getOrganSeg() + ",time:" + objBean.getGetTime()
                        + ",start:" + objBean.getFromCity() + ",end:" + objBean.getFromCity() + ",organ:" +
                        objBean.getOrgan() + ",blood:" + objBean.getBlood() + ",trueName:" + objBean.getTrueName()
                        + ",phone:" + objBean.getPhone() + ",contactName:" + objBean.getContactName() + ",contactPhone:" + objBean.getContactPhone());

            }
            if (objBean == null) {
                RequestParams params = new RequestParams(URL.TRANSFER);
                params.addBodyParameter("action", "getTransferByOrganSeg");
                params.addBodyParameter("organSeg", CONSTS.ORGAN_SEG);
                x.http().get(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        TransferJson transferJson = new Gson().fromJson(result, TransferJson.class);
                        if (transferJson != null && transferJson.getResult() == CONSTS.SEND_OK) {
                            objBean = transferJson.getObj();
                            tv_transfer_info_organSeg.setText(transferJson.getObj().getOrganSeg());
                            tv_transfer_info_time.setText(transferJson.getObj().getGetTime().split(" ")[0]);
                            tv_transfer_info_start.setText(transferJson.getObj().getFromCity().split("市")[0]);
                            tv_transfer_info_end.setText(transferJson.getObj().getToHosp().split("市")[0]);
                            tv_transfer_info_distance.setText("共" + transferJson.getObj().getDistance().split("\\.")[0] + "km");
                            tv_transfer_info_organ.setText(transferJson.getObj().getOrgan());
                            tv_transfer_info_blood.setText(transferJson.getObj().getBlood() + "型");
                            tv_transfer_info_true_name.setText(transferJson.getObj().getTrueName());
                            tv_transfer_info_phone.setText(transferJson.getObj().getPhone());
                            tv_transfer_info_contact_name.setText(transferJson.getObj().getContactName());
                            tv_transfer_info_contact_phone.setText(transferJson.getObj().getContactPhone());


                        }
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
            return result;
        } else {
            View result = this.mInflater.inflate(layout.rc_item_message, (ViewGroup) null);
            //group.setPadding(0,0,0,0);
            MessageListAdapter.ViewHolder holder = new MessageListAdapter.ViewHolder();
            holder.leftIconView = (AsyncImageView) this.findViewById(result, id.rc_left);
            holder.rightIconView = (AsyncImageView) this.findViewById(result, id.rc_right);
            holder.nameView = (TextView) this.findViewById(result, id.rc_title);

            holder.contentView = (ProviderContainerView) this.findViewById(result, id.rc_content);
            holder.layout = (ViewGroup) this.findViewById(result, id.rc_layout);
            holder.progressBar = (ProgressBar) this.findViewById(result, id.rc_progress);
            holder.warning = (ImageView) this.findViewById(result, id.rc_warning);
            holder.readReceipt = (ImageView) this.findViewById(result, id.rc_read_receipt);
            holder.readReceiptRequest = (ImageView) this.findViewById(result, id.rc_read_receipt_request);
            holder.readReceiptStatus = (TextView) this.findViewById(result, id.rc_read_receipt_status);
            holder.time = (TextView) this.findViewById(result, id.rc_time);
            holder.sentStatus = (TextView) this.findViewById(result, id.rc_sent_status);
            holder.layoutItem = (RelativeLayout) this.findViewById(result, id.rc_layout_item_message);
            if (holder.time.getVisibility() == View.GONE) {
                this.timeGone = true;
            } else {
                this.timeGone = false;
            }

            result.setTag(holder);
            return result;
        }

    }

    private boolean getNeedEvaluate(UIMessage data) {
        String extra = "";
        String robotEva = "";
        String sid = "";
        if (data != null && data.getConversationType() != null && data.getConversationType().equals(ConversationType.CUSTOMER_SERVICE)) {
            if (data.getContent() instanceof TextMessage) {
                extra = ((TextMessage) data.getContent()).getExtra();
                if (TextUtils.isEmpty(extra)) {
                    return false;
                }

                try {
                    JSONObject e = new JSONObject(extra);
                    robotEva = e.optString("robotEva");
                    sid = e.optString("sid");
                } catch (JSONException var6) {
                    ;
                }
            }

            if (data.getMessageDirection() == MessageDirection.RECEIVE && data.getContent() instanceof TextMessage && this.evaForRobot && this.robotMode && !TextUtils.isEmpty(robotEva) && !TextUtils.isEmpty(sid) && !data.getIsHistoryMessage()) {
                return true;
            }
        }

        return false;
    }

    protected void bindView(View v, int position, final UIMessage data) {

        if (data != null) {
            final MessageListAdapter.ViewHolder holder = (MessageListAdapter.ViewHolder) v.getTag();
            if (holder == null) {
                RLog.e("MessageListAdapter", "view holder is null !");
            } else {
                Object provider;
                ProviderTag tag;
                if (this.getNeedEvaluate(data)) {
                    provider = RongContext.getInstance().getEvaluateProvider();
                    tag = RongContext.getInstance().getMessageProviderTag(data.getContent().getClass());
                } else {
                    if (RongContext.getInstance() == null || data == null || data.getContent() == null) {
                        RLog.e("MessageListAdapter", "Message is null !");
                        return;
                    }

                    provider = RongContext.getInstance().getMessageTemplate(data.getContent().getClass());
                    if (provider == null) {
                        provider = RongContext.getInstance().getMessageTemplate(UnknownMessage.class);
                        tag = RongContext.getInstance().getMessageProviderTag(UnknownMessage.class);
                    } else {
                        tag = RongContext.getInstance().getMessageProviderTag(data.getContent().getClass());
                    }

                    if (provider == null) {
                        RLog.e("MessageListAdapter", data.getObjectName() + " message provider not found !");
                        return;
                    }
                }

                final View view = holder.contentView.inflate((IContainerItemProvider) provider);
                ((IContainerItemProvider) provider).bindView(view, position, data);
                if (tag == null) {
                    RLog.e("MessageListAdapter", "Can not find ProviderTag for " + data.getObjectName());
                } else {
                    if (tag.hide()) {
                        holder.contentView.setVisibility(View.GONE);
                        holder.time.setVisibility(View.GONE);
                        holder.nameView.setVisibility(View.GONE);
                        holder.leftIconView.setVisibility(View.GONE);
                        holder.rightIconView.setVisibility(View.GONE);
                        holder.layoutItem.setVisibility(View.GONE);
                        holder.layoutItem.setPadding(0, 0, 0, 0);
                    } else {
                        holder.contentView.setVisibility(View.VISIBLE);
                        holder.layoutItem.setVisibility(View.VISIBLE);
                        holder.layoutItem.setPadding(RongUtils.dip2px(8.0F), RongUtils.dip2px(6.0F), RongUtils.dip2px(8.0F), RongUtils.dip2px(6.0F));
                    }

                    UserInfo var13;
                    GroupUserInfo var14;
                    if (data.getMessageDirection() == MessageDirection.SEND) {
                        if (tag.showPortrait()) {
                            holder.rightIconView.setVisibility(View.VISIBLE);
                            holder.leftIconView.setVisibility(View.GONE);
                        } else {
                            holder.leftIconView.setVisibility(View.GONE);
                            holder.rightIconView.setVisibility(View.GONE);
                        }

                        if (!tag.centerInHorizontal()) {
                            this.setGravity(holder.layout, 5);
                            holder.contentView.containerViewRight();
                            holder.nameView.setGravity(5);
                        } else {
                            this.setGravity(holder.layout, 17);
                            holder.contentView.containerViewCenter();
                            holder.nameView.setGravity(1);
                            holder.contentView.setBackgroundColor(0);
                        }

                        boolean time = false;

                        try {
                            time = this.mContext.getResources().getBoolean(bool.rc_read_receipt);
                        } catch (NotFoundException var12) {
                            RLog.e("MessageListAdapter", "rc_read_receipt not configure in rc_config.xml");
                            var12.printStackTrace();
                        }

                        if (data.getSentStatus() == SentStatus.SENDING) {
                            if (tag.showProgress()) {
                                //   holder.progressBar.setVisibility(View.VISIBLE);
                                holder.progressBar.setVisibility(View.GONE);
                            } else {
                                holder.progressBar.setVisibility(View.GONE);
                            }

                            holder.warning.setVisibility(View.GONE);
                            holder.readReceipt.setVisibility(View.GONE);
                        } else if (data.getSentStatus() == SentStatus.FAILED) {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.warning.setVisibility(View.VISIBLE);
                            holder.readReceipt.setVisibility(View.GONE);
                        } else if (data.getSentStatus() == SentStatus.SENT) {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.warning.setVisibility(View.GONE);
                            holder.readReceipt.setVisibility(View.GONE);
                        } else if (time && data.getSentStatus() == SentStatus.READ) {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.warning.setVisibility(View.GONE);
                            if (data.getConversationType().equals(ConversationType.PRIVATE) && tag.showReadState()) {
                                holder.readReceipt.setVisibility(View.VISIBLE);
                            } else {
                                holder.readReceipt.setVisibility(View.GONE);
                            }
                        } else {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.warning.setVisibility(View.GONE);
                            holder.readReceipt.setVisibility(View.GONE);
                        }

                        holder.readReceiptRequest.setVisibility(View.GONE);
                        holder.readReceiptStatus.setVisibility(View.GONE);
                        if (time && RongContext.getInstance().isReadReceiptConversationType(data.getConversationType()) && (data.getConversationType().equals(ConversationType.GROUP) || data.getConversationType().equals(ConversationType.DISCUSSION))) {
                            if (data.getContent() instanceof TextMessage && !TextUtils.isEmpty(data.getUId())) {
                                boolean pre = true;

                                for (int publicServiceProfile = position + 1; publicServiceProfile < this.getCount(); ++publicServiceProfile) {
                                    if (((UIMessage) this.getItem(publicServiceProfile)).getMessageDirection() == MessageDirection.SEND) {
                                        pre = false;
                                        break;
                                    }
                                }

                                long var16 = System.currentTimeMillis() - RongIMClient.getInstance().getDeltaTime();
                                if (var16 - data.getSentTime() < 120000L && pre && (data.getReadReceiptInfo() == null || !data.getReadReceiptInfo().isReadReceiptMessage())) {
                                    holder.readReceiptRequest.setVisibility(View.VISIBLE);
                                }
                            }

                            if (data.getContent() instanceof TextMessage && data.getReadReceiptInfo() != null && data.getReadReceiptInfo().isReadReceiptMessage()) {
                                if (data.getReadReceiptInfo().getRespondUserIdList() != null) {
                                    holder.readReceiptStatus.setText(String.format(view.getResources().getString(string.rc_read_receipt_status), new Object[]{Integer.valueOf(data.getReadReceiptInfo().getRespondUserIdList().size())}));
                                } else {
                                    holder.readReceiptStatus.setText(String.format(view.getResources().getString(string.rc_read_receipt_status), new Object[]{Integer.valueOf(0)}));
                                }

                                holder.readReceiptStatus.setVisibility(View.VISIBLE);
                            }
                        }

                        holder.nameView.setVisibility(View.GONE);
                        holder.readReceiptRequest.setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                RongIMClient.getInstance().sendReadReceiptRequest(data.getMessage(), new OperationCallback() {
                                    public void onSuccess() {
                                        ReadReceiptInfo readReceiptInfo = data.getReadReceiptInfo();
                                        if (readReceiptInfo == null) {
                                            readReceiptInfo = new ReadReceiptInfo();
                                            data.setReadReceiptInfo(readReceiptInfo);
                                        }

                                        readReceiptInfo.setIsReadReceiptMessage(true);
                                        holder.readReceiptStatus.setText(String.format(view.getResources().getString(string.rc_read_receipt_status), new Object[]{Integer.valueOf(0)}));
                                        holder.readReceiptRequest.setVisibility(View.GONE);
                                        holder.readReceiptStatus.setVisibility(View.VISIBLE);
                                    }

                                    public void onError(ErrorCode errorCode) {
                                        RLog.e("MessageListAdapter", "sendReadReceiptRequest failed, errorCode = " + errorCode);
                                    }
                                });
                            }
                        });
                        holder.readReceiptStatus.setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                if (mOnItemHandlerListener != null) {
                                    mOnItemHandlerListener.onReadReceiptStateClick(data.getMessage());
                                }

                            }
                        });
                        holder.rightIconView.setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                if (RongContext.getInstance().getConversationBehaviorListener() != null) {
                                    UserInfo userInfo = null;
                                    if (!TextUtils.isEmpty(data.getSenderUserId())) {
                                        userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                                        userInfo = userInfo == null ? new UserInfo(data.getSenderUserId(), (String) null, (Uri) null) : userInfo;
                                    }

                                    RongContext.getInstance().getConversationBehaviorListener().onUserPortraitClick(mContext, data.getConversationType(), userInfo);
                                }

                            }
                        });
                        holder.rightIconView.setOnLongClickListener(new OnLongClickListener() {
                            public boolean onLongClick(View v) {
                                if (RongContext.getInstance().getConversationBehaviorListener() != null) {
                                    UserInfo userInfo = null;
                                    if (!TextUtils.isEmpty(data.getSenderUserId())) {
                                        userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                                        userInfo = userInfo == null ? new UserInfo(data.getSenderUserId(), (String) null, (Uri) null) : userInfo;
                                    }

                                    return RongContext.getInstance().getConversationBehaviorListener().onUserPortraitLongClick(mContext, data.getConversationType(), userInfo);
                                } else {
                                    return true;
                                }
                            }
                        });
                        if (!tag.showWarning()) {
                            holder.warning.setVisibility(View.GONE);
                        }
                    } else {
                        if (tag.showPortrait()) {
                            holder.rightIconView.setVisibility(View.GONE);
                            holder.leftIconView.setVisibility(View.VISIBLE);
                        } else {
                            holder.leftIconView.setVisibility(View.GONE);
                            holder.rightIconView.setVisibility(View.GONE);
                        }

                        if (!tag.centerInHorizontal()) {
                            this.setGravity(holder.layout, 3);
                            holder.contentView.containerViewLeft();
                            holder.nameView.setGravity(3);
                        } else {
                            this.setGravity(holder.layout, 17);
                            holder.contentView.containerViewCenter();
                            holder.nameView.setGravity(1);
                            holder.contentView.setBackgroundColor(0);
                        }

                        holder.progressBar.setVisibility(View.GONE);
                        holder.warning.setVisibility(View.GONE);
                        holder.readReceipt.setVisibility(View.GONE);
                        holder.readReceiptRequest.setVisibility(View.GONE);
                        holder.readReceiptStatus.setVisibility(View.GONE);
                        holder.nameView.setVisibility(View.VISIBLE);
                        if (data.getConversationType() != ConversationType.PRIVATE && tag.showSummaryWithName() && data.getConversationType() != ConversationType.PUBLIC_SERVICE && data.getConversationType() != ConversationType.APP_PUBLIC_SERVICE) {
                            var13 = null;
                            if (data.getConversationType().equals(ConversationType.CUSTOMER_SERVICE) && data.getMessageDirection().equals(MessageDirection.RECEIVE)) {
                                if (data.getUserInfo() != null) {
                                    var13 = data.getUserInfo();
                                } else if (data.getMessage() != null && data.getMessage().getContent() != null) {
                                    var13 = data.getMessage().getContent().getUserInfo();
                                }

                                if (var13 != null) {
                                    holder.nameView.setText(var13.getName());
                                } else {
                                    holder.nameView.setText(data.getSenderUserId());
                                }
                            } else if (data.getConversationType() == ConversationType.GROUP) {
                                var14 = RongUserInfoManager.getInstance().getGroupUserInfo(data.getTargetId(), data.getSenderUserId());
                                if (var14 != null) {
                                    holder.nameView.setText(var14.getNickname());
                                } else {
                                    var13 = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                                    if (var13 == null) {
                                        holder.nameView.setText(data.getSenderUserId());
                                    } else {
                                        holder.nameView.setText(var13.getName());
                                    }
                                }
                            } else {
                                var13 = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                                if (var13 == null) {
                                    holder.nameView.setText(data.getSenderUserId());
                                } else {
                                    holder.nameView.setText(var13.getName());
                                }
                            }
                        } else {
                            holder.nameView.setVisibility(View.GONE);
                        }

                        holder.leftIconView.setOnClickListener(new OnClickListener() {

                            public void onClick(View v) {
                                //ToastUtil.showToast(data.getSenderUserId()+","+data.getTargetId(),mContext);
                                loadPhoneData(SharePreUtils.getString("phone","",mContext),data.getSenderUserId());
                                if (RongContext.getInstance().getConversationBehaviorListener() != null) {
                                    UserInfo userInfo = null;
                                    if (!TextUtils.isEmpty(data.getSenderUserId())) {
                                        userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                                        userInfo = userInfo == null ? new UserInfo(data.getSenderUserId(), (String) null, (Uri) null) : userInfo;
                                    }

                                    RongContext.getInstance().getConversationBehaviorListener().onUserPortraitClick(mContext, data.getConversationType(), userInfo);
                                }

                            }
                        });
                    }

                    holder.leftIconView.setOnLongClickListener(new OnLongClickListener() {
                        public boolean onLongClick(View v) {
                            UserInfo userInfo = null;
                            if (!TextUtils.isEmpty(data.getSenderUserId())) {
                                userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                                userInfo = userInfo == null ? new UserInfo(data.getSenderUserId(), (String) null, (Uri) null) : userInfo;
                            }

                            if (RongContext.getInstance().getConversationBehaviorListener() != null && RongContext.getInstance().getConversationBehaviorListener().onUserPortraitLongClick(mContext, data.getConversationType(), userInfo)) {
                                return RongContext.getInstance().getConversationBehaviorListener().onUserPortraitLongClick(mContext, data.getConversationType(), userInfo);
                            } else if (!RongContext.getInstance().getResources().getBoolean(bool.rc_enable_mentioned_message) || !data.getConversationType().equals(ConversationType.GROUP) && !data.getConversationType().equals(ConversationType.DISCUSSION)) {
                                return false;
                            } else {
                                RongMentionManager.getInstance().mentionMember(data.getConversationType(), data.getTargetId(), data.getSenderUserId());
                                return true;
                            }
                        }
                    });
                    ConversationKey mKey;
                    Uri var15;
                    PublicServiceProfile var17;
                    if (holder.rightIconView.getVisibility() == View.VISIBLE) {
                        if (data.getConversationType().equals(ConversationType.CUSTOMER_SERVICE) && data.getUserInfo() != null && data.getMessageDirection().equals(MessageDirection.RECEIVE)) {
                            var13 = data.getUserInfo();
                            var15 = var13.getPortraitUri();
                            if (var15 != null) {
                                holder.rightIconView.setAvatar(var15.toString(), 0);
                            }
                        } else if ((data.getConversationType().equals(ConversationType.PUBLIC_SERVICE) || data.getConversationType().equals(ConversationType.APP_PUBLIC_SERVICE)) && data.getMessageDirection().equals(MessageDirection.RECEIVE)) {
                            var13 = data.getUserInfo();
                            if (var13 != null) {
                                var15 = var13.getPortraitUri();
                                if (var15 != null) {
                                    holder.leftIconView.setAvatar(var15.toString(), 0);
                                }
                            } else {
                                mKey = ConversationKey.obtain(data.getTargetId(), data.getConversationType());
                                var17 = RongContext.getInstance().getPublicServiceInfoFromCache(mKey.getKey());
                                var15 = var17.getPortraitUri();
                                if (var15 != null) {
                                    holder.rightIconView.setAvatar(var15.toString(), 0);
                                }
                            }
                        } else if (!TextUtils.isEmpty(data.getSenderUserId())) {
                            var13 = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                            if (var13 != null && var13.getPortraitUri() != null) {
                                holder.rightIconView.setAvatar(var13.getPortraitUri().toString(), 0);
                            }
                        }
                    } else if (holder.leftIconView.getVisibility() == View.VISIBLE) {
                        var13 = null;
                        var14 = null;
                        if (data.getConversationType().equals(ConversationType.CUSTOMER_SERVICE) && data.getMessageDirection().equals(MessageDirection.RECEIVE)) {
                            if (data.getUserInfo() != null) {
                                var13 = data.getUserInfo();
                            } else if (data.getMessage() != null && data.getMessage().getContent() != null) {
                                var13 = data.getMessage().getContent().getUserInfo();
                            }

                            if (var13 != null) {
                                var15 = var13.getPortraitUri();
                                if (var15 != null) {
                                    holder.leftIconView.setAvatar(var15.toString(), 0);
                                }
                            }
                        } else if ((data.getConversationType().equals(ConversationType.PUBLIC_SERVICE) || data.getConversationType().equals(ConversationType.APP_PUBLIC_SERVICE)) && data.getMessageDirection().equals(MessageDirection.RECEIVE)) {
                            var13 = data.getUserInfo();
                            if (var13 != null) {
                                var15 = var13.getPortraitUri();
                                if (var15 != null) {
                                    holder.leftIconView.setAvatar(var15.toString(), 0);
                                }
                            } else {
                                mKey = ConversationKey.obtain(data.getTargetId(), data.getConversationType());
                                var17 = RongContext.getInstance().getPublicServiceInfoFromCache(mKey.getKey());
                                if (var17 != null && var17.getPortraitUri() != null) {
                                    holder.leftIconView.setAvatar(var17.getPortraitUri().toString(), 0);
                                }
                            }
                        } else if (!TextUtils.isEmpty(data.getSenderUserId())) {
                            var13 = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                            if (var13 != null && var13.getPortraitUri() != null) {
                                holder.leftIconView.setAvatar(var13.getPortraitUri().toString(), 0);
                            }
                        }
                    }
                    final int p = position;
                    if (view != null) {
                        view.setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                if (RongContext.getInstance().getConversationBehaviorListener() == null || !RongContext.getInstance().getConversationBehaviorListener().onMessageClick(mContext, v, data.getMessage())) {
                                    Object provider;
                                    if (getNeedEvaluate(data)) {
                                        provider = RongContext.getInstance().getEvaluateProvider();
                                    } else {
                                        provider = RongContext.getInstance().getMessageTemplate(data.getContent().getClass());
                                    }

                                    if (provider != null) {
                                        ((MessageProvider) provider).onItemClick(v, p, data.getContent(), data);
                                    }

                                }
                            }
                        });
                        view.setOnLongClickListener(new OnLongClickListener() {
                            public boolean onLongClick(View v) {
                                if (RongContext.getInstance().getConversationBehaviorListener() != null && RongContext.getInstance().getConversationBehaviorListener().onMessageLongClick(mContext, v, data.getMessage())) {
                                    return true;
                                } else {
                                    Object provider;
                                    if (getNeedEvaluate(data)) {
                                        provider = RongContext.getInstance().getEvaluateProvider();
                                    } else {
                                        provider = RongContext.getInstance().getMessageTemplate(data.getContent().getClass());
                                    }

                                    if (provider != null) {
                                        ((MessageProvider) provider).onItemLongClick(v, p, data.getContent(), data);
                                    }

                                    return true;
                                }
                            }
                        });
                    }

                    holder.warning.setOnClickListener(new DebouncedOnClickListener() {
                        public void onDebouncedClick(View view) {
                            if (mOnItemHandlerListener != null) {
                                mOnItemHandlerListener.onWarningViewClick(p, data.getMessage(), view);
                            }

                        }
                    });
                    if (tag.hide()) {
                        holder.time.setVisibility(View.GONE);
                    } else {
                        if (!this.timeGone) {
                            String var19 = RongDateUtils.getConversationFormatDate(data.getSentTime(), view.getContext());
                            holder.time.setText(var19);
                            if (position == 0) {
                                holder.time.setVisibility(View.VISIBLE);
                            } else {
                                UIMessage var18 = (UIMessage) this.getItem(position - 1);
                                if (RongDateUtils.isShowChatTime(data.getSentTime(), var18.getSentTime(), 180)) {
                                    holder.time.setVisibility(View.VISIBLE);
                                } else {
                                    holder.time.setVisibility(View.GONE);
                                }
                            }
                        }

                    }
                }
            }
        }

    }

    private void setGravity(View view, int gravity) {
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        params.gravity = gravity;
    }

    public void setEvaluateForRobot(boolean needEvaluate) {
        this.evaForRobot = needEvaluate;
    }

    public void setRobotMode(boolean robotMode) {
        this.robotMode = robotMode;
    }

    public interface OnItemHandlerListener {
        boolean onWarningViewClick(int var1, Message var2, View var3);

        void onReadReceiptStateClick(Message var1);
    }

    class ViewHolder {
        AsyncImageView leftIconView;
        AsyncImageView rightIconView;
        TextView nameView;
        ProviderContainerView contentView;
        ProgressBar progressBar;
        ImageView warning;
        ImageView readReceipt;
        ImageView readReceiptRequest;
        TextView readReceiptStatus;
        ViewGroup layout;
        TextView time;
        TextView sentStatus;
        RelativeLayout layoutItem;

        ViewHolder() {
        }
    }

    /**
     * 等待对话框
     *
     * @author blue
     */
    public Dialog showWaitDialog(String msg, boolean isCanCancelabel, Object tag) {
        if (null == dialog || !dialog.isShowing()) {
            dialog = DialogMaker.showCommenWaitDialog(mContext, msg, null, isCanCancelabel, tag);
        }
        return dialog;
    }

    /**
     * 关闭对话框
     *
     * @author blue
     */
    public void dismissDialog() {
        if (null != dialog && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
