package com.mobileoffice.controller.message.contact;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobileoffice.R;
import com.mobileoffice.controller.cloud_monitor.PreviewInfoActivity;
import com.mobileoffice.controller.cloud_monitor.TransferDetailActivity;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.PhotoJson;
import com.mobileoffice.json.PushListJson;
import com.mobileoffice.json.TransferJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;
import com.mobileoffice.view.DialogMaker;
import com.mobileoffice.view.RoundImageView;
import com.squareup.picasso.Picasso;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
 * Created by 99213 on 2017/4/23.
 */

public class SystemMessageAdapter extends RecyclerView.Adapter<SystemMessageAdapter.MyViewHolder> {
    private String TAG = "SystemMessageAdapter";
    //是否显示header
    private boolean isHeader = false;
    private int HEADER = 1;
    private int CONTENT = 2;
    private Dialog dialog;

    //自定义监听事件
    interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
        void onAgreeItemClick(View view, int position);
        void onRefuseItemClick(View view, int position);
        void onItemLongClick(View view,int position);
    }

    private boolean isLoad = false;

    /**
     * 更新未读数
     */
    private void updateSystemPosition(final String content, final String time) {
        String phone = SharePreUtils.getString("phone", "", mContext);
        RequestParams params = new RequestParams(URL.USER);
        params.addBodyParameter("action", "updateSystemPosition");
        params.addBodyParameter("phone", phone);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                PushListJson pushListJson = gson.fromJson(result, PushListJson.class);
                if (pushListJson != null && pushListJson.getResult() == CONSTS.SEND_OK) {
                    CONSTS.UNREAD_PUSH_NUM = 0;
                    CONSTS.NEW_SYSTEM_MESSAGE = content;
                    CONSTS.NEW_SYSTEM_TIME = time;
                    SharePreUtils.putInt("unread_push_num", CONSTS.UNREAD_PUSH_NUM, mContext);
                    SharePreUtils.putString("new_system_message", CONSTS.NEW_SYSTEM_MESSAGE, mContext);
                    SharePreUtils.putString("new_system_time", CONSTS.NEW_SYSTEM_TIME, mContext);
                    Intent i = new Intent("com.mobile.office.system.message");
                    mContext.sendBroadcast(i);
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


    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void refreshList(List<PushListJson.ObjBean> pushes, boolean isHeader) {
        if (pushes != null) {
            mPushes = pushes;
        }
        this.isHeader = isHeader;
        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    private List<PushListJson.ObjBean> mPushes;
    private Context mContext;

    public SystemMessageAdapter(List<PushListJson.ObjBean> pushes, Context context) {
        mPushes = pushes;
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == CONTENT) {
            view = LayoutInflater.from(mContext).inflate(R.layout.system_message_item, parent, false);

        } else if (viewType == HEADER) {
            view = LayoutInflater.from(mContext).inflate(R.layout.system_history, parent, false);
        }
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    private void skipTranferDetail(String transferId,final String type) {
        showWaitDialog(mContext.getResources().getString(R.string.loading), false, "loading");
        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "getTransferByTransferId");
        params.addBodyParameter("transferId", transferId);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e(TAG,result);
                TransferJson transferJson = new Gson().fromJson(result, TransferJson.class);
                if (transferJson != null && transferJson.getResult() == CONSTS.SEND_OK) {

                    Intent intent = new Intent();
                    intent.putExtra("transfer", transferJson.getObj());
                    intent.putExtra("type",type);
                    intent.setClass(mContext, TransferDetailActivity.class);
                    mContext.startActivity(intent);

                }
                dismissDialog();

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
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

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        if (mPushes.size() > 0) {



            if (position == 0) {
                if (isHeader) {
                    holder.ll_history_footer.setVisibility(View.VISIBLE);
                } else {
                    holder.ll_history_footer.setVisibility(View.GONE);
                }
            } else {

                final PushListJson.ObjBean objBean = mPushes.get(position - 1);
                if (position == mPushes.size() && !isLoad) {
                    updateSystemPosition(objBean.getContent(), objBean.getCreateTime());
                    isLoad = true;

                }
                holder.tv_system_message_time.setText(objBean.getCreateTime());

                //跳转到详情页
                holder.ll_system_message_item_system.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        skipTranferDetail(objBean.getTransferId(),objBean.getType());
                    }
                });
                //LogUtil.e(TAG,"objBean:"+objBean.toString());
                if ("power".equals(objBean.getType())) {
                    holder.ll_system_message_item_system.setVisibility(View.VISIBLE);
                    holder.ll_system_message_item_friend.setVisibility(View.GONE);
                    holder.tv_type.setText("电量提醒");
                    holder.tv_content.setText("        "+objBean.getContent());
                    holder.iv_logo.setImageResource(R.drawable.mes_auto_power);

                }
                else if ("open".equals(objBean.getType())) {
                    holder.ll_system_message_item_system.setVisibility(View.VISIBLE);
                    holder.ll_system_message_item_friend.setVisibility(View.GONE);
                    holder.tv_type.setText("开箱提醒");
                    holder.tv_content.setText("        "+objBean.getContent());
                    holder.iv_logo.setImageResource(R.drawable.mes_auto_open);

                } else if ("collision".equals(objBean.getType())) {
                    holder.ll_system_message_item_system.setVisibility(View.VISIBLE);
                    holder.ll_system_message_item_friend.setVisibility(View.GONE);
                    holder.tv_type.setText("碰撞异常");
                    holder.tv_content.setText("        "+objBean.getContent());
                    holder.iv_logo.setImageResource(R.drawable.mes_auto_crash);
                } else if ("temperature".equals(objBean.getType())) {
                    holder.ll_system_message_item_system.setVisibility(View.VISIBLE);
                    holder.ll_system_message_item_friend.setVisibility(View.GONE);
                    holder.tv_type.setText("温度异常");
                    holder.tv_content.setText("        "+objBean.getContent());
                    holder.iv_logo.setImageResource(R.drawable.mes_auto_temp);
                } else if ("add".equals(objBean.getType())) {
                    holder.ll_system_message_item_system.setVisibility(View.GONE);
                    holder.ll_system_message_item_friend.setVisibility(View.VISIBLE);
                    holder.tv_system_message_item_true_name.setText(objBean.getTrueName());
                    holder.tv_system_message_item_hospital.setText(objBean.getHospital());
                    //微信
                    if ("0".equals(objBean.getIsUploadPhoto())) {
                        Picasso.with(mContext).load(objBean.getWechatUrl()).error(R.drawable.msg_2list_linkman).into(holder.riv_system_message_item);
                    }
                    //手机
                    else if ("1".equals(objBean.getIsUploadPhoto())) {
                        Picasso.with(mContext).load(objBean.getPhotoFile()).error(R.drawable.msg_2list_linkman).into(holder.riv_system_message_item);
                    }

                    holder.tv_system_message_item_disagree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            RequestParams params = new RequestParams(URL.CONTACT);
                            params.addBodyParameter("action", "refuse");
                            params.addBodyParameter("pushId", objBean.getId() + "");

                            x.http().get(params, new Callback.CommonCallback<String>() {
                                @Override
                                public void onSuccess(String result) {
                                    PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                                    if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                                        holder.tv_system_message_item_disagree.setText("已拒绝");
                                        holder.tv_system_message_item_disagree.setEnabled(false);
                                        holder.tv_system_message_item_line.setVisibility(View.GONE);
                                        holder.tv_system_message_item_agree.setVisibility(View.GONE);
                                        mOnItemClickListener.onRefuseItemClick(view,position-1);
                                        mPushes.get(position-1).setType("refuse");
                                    } else {
                                        ToastUtil.showToast("请求失败", mContext);//卢
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
                    });
                    LogUtil.e(TAG, "otherId:" + objBean.getOtherId());
                    holder.tv_system_message_item_agree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            RequestParams params = new RequestParams(URL.CONTACT);
                            params.addBodyParameter("action", "add");
                            params.addBodyParameter("otherId", objBean.getOtherId() + "");
                            params.addBodyParameter("pushId", objBean.getId() + "");
                            params.addBodyParameter("phone", objBean.getPhone());
                            x.http().get(params, new Callback.CommonCallback<String>() {
                                @Override
                                public void onSuccess(String result) {
                                    PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                                    if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                                        holder.tv_system_message_item_agree.setText("已同意");
                                        holder.tv_system_message_item_agree.setEnabled(false);
                                        holder.tv_system_message_item_line.setVisibility(View.GONE);
                                        holder.tv_system_message_item_disagree.setVisibility(View.GONE);
                                        mOnItemClickListener.onAgreeItemClick(view,position-1);
                                        mPushes.get(position-1).setType("agree");
                                    } else {
                                        ToastUtil.showToast("请求失败", mContext);
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
                    });
                } else if ("refuse".equals(objBean.getType())) {
                    holder.ll_system_message_item_system.setVisibility(View.GONE);
                    holder.ll_system_message_item_friend.setVisibility(View.VISIBLE);
                    holder.tv_system_message_item_true_name.setText(objBean.getTrueName());
                    holder.tv_system_message_item_hospital.setText(objBean.getHospital());
                    //微信
                    if ("0".equals(objBean.getIsUploadPhoto())) {
                        Picasso.with(mContext).load(objBean.getWechatUrl()).error(R.drawable.msg_2list_linkman).into(holder.riv_system_message_item);
                    }
                    //手机
                    else if ("1".equals(objBean.getIsUploadPhoto())) {
                        Picasso.with(mContext).load(objBean.getPhotoFile()).error(R.drawable.msg_2list_linkman).into(holder.riv_system_message_item);
                    }
                    holder.tv_system_message_item_disagree.setText("已拒绝");
                    holder.tv_system_message_item_line.setVisibility(View.GONE);
                    holder.tv_system_message_item_agree.setVisibility(View.GONE);
                } else if ("agree".equals(objBean.getType())) {
                    holder.ll_system_message_item_system.setVisibility(View.GONE);
                    holder.ll_system_message_item_friend.setVisibility(View.VISIBLE);
                    holder.tv_system_message_item_true_name.setText(objBean.getTrueName());
                    holder.tv_system_message_item_hospital.setText(objBean.getHospital());
                    //微信
                    if ("0".equals(objBean.getIsUploadPhoto())) {
                        Picasso.with(mContext).load(objBean.getWechatUrl()).error(R.drawable.msg_2list_linkman).into(holder.riv_system_message_item);
                    }
                    //手机
                    else if ("1".equals(objBean.getIsUploadPhoto())) {
                        Picasso.with(mContext).load(objBean.getPhotoFile()).error(R.drawable.msg_2list_linkman).into(holder.riv_system_message_item);
                    }
                    holder.tv_system_message_item_agree.setText("已同意");
                    holder.tv_system_message_item_line.setVisibility(View.GONE);
                    holder.tv_system_message_item_disagree.setVisibility(View.GONE);
                }
            }
           if(holder.ll_system_message_item_friend!=null&&holder.ll_system_message_item_friend.getVisibility()==View.VISIBLE) {
               holder.ll_system_message_item_friend.setOnLongClickListener(new View.OnLongClickListener() {
                   @Override
                   public boolean onLongClick(View v) {
                       mOnItemClickListener.onItemLongClick(v, position);
                       return true;
                   }
               });
           }
            if(holder.ll_system_message_item_system!=null&&holder.ll_system_message_item_system.getVisibility()==View.VISIBLE) {
                holder.ll_system_message_item_system.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        mOnItemClickListener.onItemLongClick(v, position);
                        return true;
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mPushes.size() == 0) {
            return mPushes.size();
        } else {
            return mPushes.size() + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && mPushes.size() > 0) {
            return HEADER;
        } else if (mPushes.size() > 0) {
            return CONTENT;
        }

        return super.getItemViewType(position);
    }

    //自定义ViewHolder，用于加载图片
    class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ll_system_message_item_friend;
        TextView tv_system_message_time;
        RoundImageView riv_system_message_item;
        TextView tv_system_message_item_true_name;
        TextView tv_system_message_item_hospital;
        TextView tv_system_message_item_disagree;
        TextView tv_system_message_item_agree;
        TextView tv_system_message_item_line;

        LinearLayout ll_system_message_item_system;

        LinearLayout ll_history_footer;

        TextView tv_content;
        TextView tv_type;
        ImageView iv_logo;


        public MyViewHolder(View view) {
            super(view);
            ll_system_message_item_friend = (LinearLayout) view.findViewById(R.id.ll_system_message_item_friend);
            tv_system_message_time = (TextView) view.findViewById(R.id.tv_system_message_time);
            riv_system_message_item = (RoundImageView) view.findViewById(R.id.riv_system_message_item);
            tv_system_message_item_true_name = (TextView) view.findViewById(R.id.tv_system_message_item_true_name);
            tv_system_message_item_hospital = (TextView) view.findViewById(R.id.tv_system_message_item_hospital);
            tv_system_message_item_disagree = (TextView) view.findViewById(R.id.tv_system_message_item_disagree);
            tv_system_message_item_agree = (TextView) view.findViewById(R.id.tv_system_message_item_agree);
            tv_system_message_item_line = (TextView) view.findViewById(R.id.tv_system_message_item_line);
            tv_content = (TextView) view.findViewById(R.id.tv_content);
            tv_type = (TextView) view.findViewById(R.id.tv_type);


            ll_system_message_item_system = (LinearLayout) view.findViewById(R.id.ll_system_message_item_system);

            ll_history_footer = (LinearLayout) view.findViewById(R.id.ll_history_footer);
            iv_logo = (ImageView) view.findViewById(R.id.iv_logo);
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
