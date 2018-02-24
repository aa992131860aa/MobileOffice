package com.mobileoffice.controller.cloud_monitor;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobileoffice.MainActivity;
import com.mobileoffice.R;
import com.mobileoffice.application.LocalApplication;
import com.mobileoffice.controller.new_monitor.NewMonitorBaseFragment;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.ContactListJson;
import com.mobileoffice.json.ContactPersonJson;
import com.mobileoffice.json.PhotoJson;
import com.mobileoffice.json.TransferJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.ConstantsUtil;
import com.mobileoffice.utils.DisplayUtil;
import com.mobileoffice.utils.LogUtil;
import com.mobileoffice.utils.SeekBarHeart;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;
import com.mobileoffice.view.AnimTextView;
import com.mobileoffice.view.SpeedDialPopup;
import com.mobileoffice.view.TransferPopup;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by 99213 on 2017/4/23.
 */

public class TransferAdapter extends RecyclerView.Adapter<TransferAdapter.MyViewHolder> {
    private String TAG = "TransferAdapter";

    private List<TransferJson.ObjBean> mTransfers;
    private Context mContext;
    private AlertDialog.Builder mAlertDialog;
    private String phone;
    private TransferPopup mTransferPopup;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private List<ContactListJson.ObjBean> mObjBean;
    private MainActivity mTransferDetailActivity;


    //自定义监听事件
    interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);

        void onLeftPhoneClick(View view, String postRole, String name, String phone, int position);

        void onPhoneClick(View view, String postRole, String name, String phone);

        void onEditClick(View view, int position);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void refreshList(List<TransferJson.ObjBean> transfers) {
        mTransfers = transfers;
        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public TransferAdapter(List<TransferJson.ObjBean> transfers, Context context) {
        mTransfers = transfers;
        mContext = context;
        phone = SharePreUtils.getString("phone", "", mContext);

        mTransferDetailActivity = (MainActivity) mContext;
    }

    private void sendGroupMessage() {
        RequestParams params = new RequestParams(URL.RONG);
        params.addBodyParameter("action", "sendGroupMessageStart");
        params.addBodyParameter("phone", SharePreUtils.getString("phone", "", mContext));
        params.addBodyParameter("organSeg", NewMonitorBaseFragment.objBean.getOrganSeg());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

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

    /**
     * 获取群组名
     */
    private void getGroupName(final String organSeg) {

        RequestParams params = new RequestParams(URL.RONG);
        params.addBodyParameter("action", "getGroupName");
        params.addBodyParameter("organSeg", organSeg);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    RongIM.getInstance().refreshGroupInfoCache(new Group(organSeg, photoJson.getMsg(), Uri.parse(URL.TOMCAT_SOCKET + "images/team.png")));
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.showToast("网络错误", mContext);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    private void sendTransferSms(String phones, String content) {
        RequestParams params = new RequestParams(URL.SMS);
        params.addBodyParameter("action", "sendTransfer");
        params.addBodyParameter("phones", phones);
        params.addBodyParameter("content", content);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

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

    private void getGroupPhones(String organSeg, final int position, final String type) {
        RequestParams params = new RequestParams(URL.RONG);
        params.addBodyParameter("action", "getGroupInfoOrganSeg");
        params.addBodyParameter("organSeg", organSeg);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    String phones = photoJson.getMsg();
                    String content = "";
                    if ("start".equals(type)) {
                        content = "本次转运医师:" + mTransfers.get(position).getTrueName() + ",科室协调员:" + mTransfers.get(position).getContactName() + "。器官段号:" + mTransfers.get(position).getOrganSeg() + "，" + mTransfers.get(position).getFromCity() + "的" + mTransfers.get(position).getOrgan() + "转运已开始。";
                        sendListTransferSms(phones, content);
                    }
//                    else {
//                        content = "器官段号:" + mTransfers.get(position).getOrganSeg() + "," + mTransfers.get(position).getFromCity() + "的" + mTransfers.get(position).getOrgan() + "转运已结束，请注意查看。";
//                        sendTransferSms(phones, content);
//                    }

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

    private void sendListTransferSms(String phones, String content) {

        RequestParams params = new RequestParams(URL.SMS);
        params.addBodyParameter("action", "sendListTransfer");
        params.addBodyParameter("phones", phones);
        params.addBodyParameter("content", content);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

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


    /**
     * 获取结束的经纬度
     */
    private void loadEndLocation(final String organSeg, final String boxNo) {

        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "updateStart");
        params.addBodyParameter("organSeg", organSeg);
        params.addBodyParameter("isStart", "1");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    ConstantsUtil.sendTransferStatus(boxNo, "设备推送", CONSTS.START, mContext);
                    CONSTS.IS_START = 1;
                    sendGroupMessage();
                    if (CONSTS.NO_START_VIEW != null) {
                        CONSTS.NO_START_VIEW.setVisibility(View.GONE);
                    }
                    getGroupName(organSeg);
                    //通知转运监控
                    noticeTransfer(organSeg, "");
                    //初始化状态
                    MainActivity.distance = 0;
                    MainActivity.temperature = "";
                    MainActivity.weather = "";
                    MainActivity.endLocation = "";
                    NewMonitorBaseFragment.objBean = new TransferJson.ObjBean();
                    CONSTS.CONTACT_LIST = new ArrayList<ContactListJson.ObjBean>();

                } else {
                    ToastUtil.showToast("创建失败", mContext);
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.showToast("网络错误", mContext);
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
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.transfer_item, parent, false);

        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    /**
     * 通知云监控改变
     *
     * @param organSeg
     */
    private void noticeTransfer(String organSeg, String type) {
        RequestParams params = new RequestParams(URL.PUSH);
        params.addBodyParameter("action", "sendPushTransfer");
        params.addBodyParameter("organSeg", organSeg);
        params.addBodyParameter("type", type);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

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

    private void deleteTransfer(final String organSeg, final int position, final String pBoxNo, final TransferPopup mTransferPopup) {
        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "deleteTransfer");
        params.addBodyParameter("phone", phone);
        params.addBodyParameter("organSeg", organSeg);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    ConstantsUtil.sendTransferStatus(pBoxNo, "设备推送", CONSTS.DELETE, mContext);
                    ToastUtil.showToast("删除成功", mContext);
                    if (mTransferPopup != null) {
                        mTransferPopup.dismiss();
                    }

                    mTransfers.remove(position);
                    CONSTS.IS_START = 1;
                    notifyDataSetChanged();

                    //移除会话列表
                    RongIM.getInstance().removeConversation(Conversation.ConversationType.GROUP, organSeg, new RongIMClient.ResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {

                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });


                    //loadContactPerson();

                } else {
                    ToastUtil.showToast("删除失败", mContext);
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

    private void stopTransfer(final String organSeg, final int position, final TransferPopup transferPopup, final String boxNo) {
        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "shutDownTransfer");
        params.addBodyParameter("organSeg", organSeg);
        params.addBodyParameter("boxNo", boxNo);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    ToastUtil.showToast("转运已结束", mContext);
                    ConstantsUtil.sendTransferStatus(boxNo, "设备推送", CONSTS.STOP, mContext);
                    mTransfers.get(position).setStatus("done");
                    TransferJson.ObjBean objBean = mTransfers.get(position);
                    mTransfers.remove(position);
                    for (int i = 0; i < mTransfers.size(); i++) {
                        String status = mTransfers.get(i).getStatus();
                        if ("done".equals(status)) {
                            mTransfers.add(i, objBean);
                            refreshList(mTransfers);
                            break;
                        }
                    }

                    transferPopup.dismiss();

                    //通知转运监控
                    noticeTransfer(organSeg, "");

                    //loadContactPerson();
                    RongIM.getInstance().setConversationToTop(Conversation.ConversationType.GROUP, organSeg, false, new RongIMClient.ResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {

                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });

                } else {
                    ToastUtil.showToast("停止转运失败", mContext);
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

    private void loadContactPerson() {
        RequestParams params = new RequestParams(URL.RONG);
        params.addBodyParameter("action", "getGroupList");
        params.addBodyParameter("phone", phone);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ContactPersonJson contactPersonJson = new Gson().fromJson(result, ContactPersonJson.class);
                if (contactPersonJson != null && contactPersonJson.getResult() == CONSTS.SEND_OK) {
                    List<ContactPersonJson.ObjBean> mObjBean = contactPersonJson.getObj();
                    for (int i = 0; i < mObjBean.size(); i++) {
                        RongIM.getInstance().refreshGroupInfoCache(new Group(mObjBean.get(i).getOrganSeg(), mObjBean.get(i).getGroupName(), Uri.parse(URL.TOMCAT_SOCKET + "images/team.png")));
                    }

                } else {

                }
                //ToastUtil.showToast(contactPersonJson.getMsg()+","+contactPersonJson.getObj().size(),getActivity());
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

    public void showPopMenu(View view, final int pos) {
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_item, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                // myAdapter.removeItem(pos);
                return false;
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                // Toast.makeText(getApplicationContext(), "关闭PopupMenu", Toast.LENGTH_SHORT).show();
            }
        });
        popupMenu.setGravity(Gravity.CENTER_VERTICAL);

        popupMenu.show();


    }

    /**
     * 加载联系人
     */
    private void loadContact(final String organSeg, final View v) {

        RequestParams params = new RequestParams(URL.RONG);
        params.addBodyParameter("action", "getContact");
        params.addBodyParameter("organSeg", organSeg);
        params.addBodyParameter("phone", phone);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //ToastUtil.showToast("result:"+result,mContext);
                ContactListJson contactPersonJson = new Gson().fromJson(result, ContactListJson.class);
                mTransferDetailActivity.dismissDialog();
                if (contactPersonJson != null && contactPersonJson.getResult() == CONSTS.SEND_OK) {

                    if (contactPersonJson.getObj() != null && contactPersonJson.getObj().size() > 0) {
                        mObjBean = contactPersonJson.getObj();


                        SpeedDialPopup speedDialPopup = new SpeedDialPopup((Activity) mContext, mObjBean, "");

                        speedDialPopup.showPopupWindow(v);
                        speedDialPopup.setOnClickChangeListener(new SpeedDialPopup.OnClickChangeListener() {
                            @Override
                            public void OnClickChange(int position) {
                                mOnItemClickListener.onPhoneClick(v, mObjBean.get(position).getPostRole(), mObjBean.get(position).getTrueName(), mObjBean.get(position).getPhone());
                            }
                        });


                    }
                } else {

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mTransferDetailActivity.dismissDialog();
                //ToastUtil.showToast("ex:"+ex.getMessage(),mContext);
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
        final TransferJson.ObjBean objBean = mTransfers.get(position);
        if ("transfering".equals(objBean.getStatus())) {
            holder.ll_transfer.setBackgroundResource(R.drawable.layout_seletor_coner);
            if ("0".equals(objBean.getIsStart()) && CONSTS.IS_START == 0) {
                holder.tv_transfer_item_status.setTextStatus("待转运");
                holder.tv_transfer_item_status.setTextColor(mContext.getResources().getColor(R.color.blue));
            } else {
                if(objBean.getDeviceStatus()==1) {
                    holder.tv_transfer_item_status.setTextStatus("转运中 ");
                    holder.tv_transfer_item_status.setTextColor(mContext.getResources().getColor(R.color.blue));
                }else{
                    holder.tv_transfer_item_status.setTextStatus("设备未开启 ");
                    holder.tv_transfer_item_status.setTextColor(mContext.getResources().getColor(R.color.highlight));
                }
            }


        } else {
            //holder.ll_transfer.setBackgroundResource(R.drawable.layout_seletor_coner_finish);
            holder.tv_transfer_item_status.setTextStatus("已完成");
            holder.tv_transfer_item_status.setTextColor(mContext.getResources().getColor(R.color.font_black_6));


        }

        holder.tv_transfer.setText(mTransfers.get(position).getTrueName());
        holder.tv_xieyun.setText(mTransfers.get(position).getContactName());
        holder.ll_speed_dial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("0".equals(mTransfers.get(position).getIsStart()) && CONSTS.IS_START == 0) {

                } else {
                    loadContact(mTransfers.get(position).getOrganSeg(), v);
                }
            }
        });
        holder.ll_transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // mOnItemClickListener.onItemClick(view, position);
                //ToastUtil.showToast("gg");
                //ToastUtil.showToast("isStart:"+mTransfers.get(position).getIsStart()+",is:"+CONSTS.IS_START,mContext);
                if ("0".equals(mTransfers.get(position).getIsStart()) && CONSTS.IS_START == 0) {

                } else {
                    Intent intent = new Intent();
                    intent.putExtra("transfer", mTransfers.get(position));
                    intent.setClass(mContext, TransferDetailActivity.class);
                    mContext.startActivity(intent);
                }

            }
        });
        holder.ll_transfer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //ToastUtil.showToast("GG",mContext);
                //showPopMenu(view,position);
                if ("0".equals(objBean.getIsStart()) || ("done".equals(objBean.getStatus()))) {
                    return true;
                }

                mTransferPopup = new TransferPopup(mContext, position, objBean.getAutoTransfer());
                mTransferPopup.setOnClickChangeListener(new TransferPopup.OnClickChangeListener() {
                    @Override
                    public void OnClickChange(final int position) {

                        mAlertDialog = new AlertDialog.Builder(mContext);
                        //mAlertDialog.setTitle("tis");

                        mAlertDialog.setMessage("是否停止转运");


                        mAlertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mTransferPopup.dismiss();
                            }
                        });
                        mAlertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                stopTransfer(objBean.getOrganSeg(), position, mTransferPopup, objBean.getBoxNo());
                                getGroupPhones(objBean.getOrganSeg(), position, "stop");
                            }
                        });
                        mAlertDialog.show();

                    }

                    @Override
                    public void OnDelTransfer(final int position) {
                        mAlertDialog = new AlertDialog.Builder(mContext);
                        //mAlertDialog.setTitle("tis");

                        mAlertDialog.setMessage("        是否删除自动创建转运，删除后，群组也将一起删除！！！");


                        mAlertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mTransferPopup.dismiss();
                            }
                        });
                        mAlertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //通知转运监控
                                noticeTransfer(objBean.getOrganSeg(), "delete");
                                deleteTransfer(objBean.getOrganSeg(), position, objBean.getBoxNo(), mTransferPopup);
                            }
                        });
                        mAlertDialog.show();
                    }
                });
                if (phone.equals(objBean.getPhone()) || phone.equals(objBean.getContactPhone())) {
                    mTransferPopup.showPopupWindow(view);
                }

                return true;
            }
        });


        //未开始
        if ("0".equals(objBean.getIsStart()) && CONSTS.IS_START == 0) {
            //CONSTS.IS_START = 1;上海
            holder.rl_transfer_item_no_start.setVisibility(View.VISIBLE);
            CONSTS.NO_START_VIEW = holder.rl_transfer_item_no_start;
            holder.iv_transfer_item_start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAlertDialog = new AlertDialog.Builder(mContext);
                    //mAlertDialog.setTitle("tis");

                    mAlertDialog.setMessage("是否开始转运");


                    mAlertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    mAlertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            loadEndLocation(objBean.getOrganSeg(), objBean.getBoxNo());
                            getGroupPhones(objBean.getOrganSeg(), position, "start");

                        }
                    });
                    if (phone.equals(objBean.getPhone()) || phone.equals(objBean.getContactPhone())) {
                        mAlertDialog.show();
                    } else {
                        ToastUtil.showToast("        您不是创建转运信息的人，无法操作，请联系创建人。", mContext);
                    }


                }
            });
            holder.iv_transfer_item_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (phone.equals(objBean.getPhone()) || phone.equals(objBean.getContactPhone())) {
                        mAlertDialog = new AlertDialog.Builder(mContext);
                        //mAlertDialog.setTitle("tis");

                        mAlertDialog.setMessage("        是否删除转运，删除后，群组也将一起删除！！！");


                        mAlertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        mAlertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //通知转运监控
                                noticeTransfer(objBean.getOrganSeg(), "delete");
                                deleteTransfer(objBean.getOrganSeg(), position, objBean.getBoxNo(), null);

                            }
                        });
                        mAlertDialog.show();
                    } else {
                        ToastUtil.showToast("        您不是创建转运信息的人，无法操作，请联系创建人。", mContext);//眉山市
                    }
                }
            });
            holder.iv_transfer_item_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mOnItemClickListener.onEditClick(view, position);
                }
            });


        } else {
            holder.rl_transfer_item_no_start.setVisibility(View.GONE);

        }

        if (!"".equals(objBean.getModifyOrganSeg())) {
            holder.tv_transfer_item_organ_seg.setText(objBean.getModifyOrganSeg());
        } else {
            holder.tv_transfer_item_organ_seg.setText(objBean.getOrganSeg());
        }
        holder.tv_transfer_item_organ.setText(objBean.getOrgan());
        if ("心脏".equals(objBean.getOrgan())) {
            holder.iv_transfer_item_organ.setImageResource(R.drawable.cloud_1index_1heart);
            if ("done".equals(objBean.getStatus())) {
                holder.iv_transfer_item_organ.setImageResource(R.drawable.cloud_1index_1heart_black);
                //imageColorToGray(holder.iv_transfer_item_organ, R.drawable.cloud_1index_1heart);
            }

        } else if ("肝脏".equals(objBean.getOrgan())) {
            holder.iv_transfer_item_organ.setImageResource(R.drawable.cloud_1index_1liver);
            if ("done".equals(objBean.getStatus())) {
                holder.iv_transfer_item_organ.setImageResource(R.drawable.cloud_1index_1liver_black);
                //imageColorToGray(holder.iv_transfer_item_organ, R.drawable.cloud_1index_1liver);
            }
        } else if ("肺".equals(objBean.getOrgan())) {
            holder.iv_transfer_item_organ.setImageResource(R.drawable.cloud_1index_1lung);
            if ("done".equals(objBean.getStatus())) {
                holder.iv_transfer_item_organ.setImageResource(R.drawable.cloud_1index_1lung_black);
                //imageColorToGray(holder.iv_transfer_item_organ, R.drawable.cloud_1index_1lung);
            }
        } else if ("肾脏".equals(objBean.getOrgan())) {
            holder.iv_transfer_item_organ.setImageResource(R.drawable.cloud_1index_1kidneys);
            if ("done".equals(objBean.getStatus())) {
                holder.iv_transfer_item_organ.setImageResource(R.drawable.cloud_1index_1kidneys_black);
                //imageColorToGray(holder.iv_transfer_item_organ, R.drawable.cloud_1index_1kidneys);
            }
        } else if ("眼角膜".equals(objBean.getOrgan())) {
            holder.iv_transfer_item_organ.setImageResource(R.drawable.cloud_1index_1cornea);
            if ("done".equals(objBean.getStatus())) {
                holder.iv_transfer_item_organ.setImageResource(R.drawable.cloud_1index_1cornea_black);
                //imageColorToGray(holder.iv_transfer_item_organ, R.drawable.cloud_1index_1cornea);
            }
        } else if ("胰脏".equals(objBean.getOrgan())) {
            holder.iv_transfer_item_organ.setImageResource(R.drawable.cloud_1index_1pancreas);
            if ("done".equals(objBean.getStatus())) {
                holder.iv_transfer_item_organ.setImageResource(R.drawable.cloud_1index_1pancreas_black);
                //imageColorToGray(holder.iv_transfer_item_organ, R.drawable.cloud_1index_1pancreas);
            }
        }
        if (objBean.getOrganSeg().contains("AP")) {
            if (objBean.getAutoTransfer() == CONSTS.AURO_TRANSFER_FINISH_NO) {
                holder.iv_example.setVisibility(View.VISIBLE);
                holder.iv_auto.setImageResource(R.drawable.cloud_1index_auto);
                holder.iv_example.setImageResource(R.drawable.cloud_index_imperfect);
            } else {
                holder.iv_example.setVisibility(View.GONE);
                holder.iv_auto.setImageResource(R.drawable.cloud_1index_auto_black);
                //imageColorToGray(holder.iv_auto, R.drawable.cloud_1index_auto);
            }
            holder.iv_auto.setVisibility(View.VISIBLE);
        }else{
            holder.iv_auto.setVisibility(View.GONE);

            if("示例".equals(objBean.getOpenPsd())){
                holder.iv_example.setImageResource(R.drawable.cloud_index_example);
                holder.iv_example.setVisibility(View.VISIBLE);
            }else{
                holder.iv_example.setVisibility(View.GONE);
            }
        }


        if ("done".equals(objBean.getStatus())) {
            holder.iv_info.setImageResource(R.drawable.cloud_1index_id_black);
            holder.iv_blood.setImageResource(R.drawable.cloud_1index_blood_black);
            holder.iv_speed_dial.setImageResource(R.drawable.cloud_1index_phone_black);
//            imageColorToGray(holder.iv_info, R.drawable.cloud_1index_id);
//            imageColorToGray(holder.iv_blood, R.drawable.cloud_1index_blood);
//            imageColorToGray(holder.iv_speed_dial, R.drawable.cloud_1index_phone);

            holder.iv_left_phone.setTextColor(mContext.getResources().getColor(R.color.font_black_6));
            holder.iv_left_phone.setBackgroundResource(R.drawable.team_border_font_6);
            holder.iv_right_phone.setTextColor(mContext.getResources().getColor(R.color.font_black_6));
            holder.iv_right_phone.setBackgroundResource(R.drawable.team_border_font_6);
            holder.ll_speed_dial.setBackgroundResource(R.drawable.edit_border_green_font_6);

            holder.ll_bg.setBackgroundResource(R.drawable.cloud_1index_bg_black);
            holder.iv_time.setImageResource(R.drawable.cloud_1index_time_black);
            //layoutToGray(holder.ll_bg, R.drawable.cloud_1index_bg);
            //imageColorToGray(holder.iv_time, R.drawable.cloud_1index_time);
            holder.tv_transfer_item_time.setTextColor(mContext.getResources().getColor(R.color.font_black_6));
            holder.iv_dot.setImageResource(R.drawable.dot_gay);
            holder.sbh_transfer_item.setDone(true);
            holder.sbh_transfer_item.setHeartMove(R.drawable.cloud_1index_2now);
            holder.iv_heart.setVisibility(View.INVISIBLE);

        } else {
            holder.ll_speed_dial.setBackgroundResource(R.drawable.edit_border_green_white);
            holder.iv_speed_dial.setImageResource(R.drawable.cloud_1index_phone);
            holder.iv_info.setImageResource(R.drawable.cloud_1index_id);
            holder.iv_blood.setImageResource(R.drawable.cloud_1index_blood);
            holder.iv_left_phone.setTextColor(mContext.getResources().getColor(R.color.highlight));
            holder.iv_right_phone.setTextColor(mContext.getResources().getColor(R.color.highlight));
            holder.iv_left_phone.setBackgroundResource(R.drawable.team_border);
            holder.iv_right_phone.setBackgroundResource(R.drawable.team_border);
            holder.ll_bg.setBackgroundResource(R.drawable.cloud_1index_bg);
            holder.iv_time.setImageResource(R.drawable.cloud_1index_time);
            holder.tv_transfer_item_time.setTextColor(mContext.getResources().getColor(R.color.blue));
            holder.iv_dot.setImageResource(R.drawable.dot_yellow);
            holder.sbh_transfer_item.setDone(false);
            holder.sbh_transfer_item.setMove(true);
            holder.sbh_transfer_item.setHeartMove(R.drawable.cloud_1index_2now_move);
            holder.iv_heart.setVisibility(View.VISIBLE);
            holder.iv_heart.setImageResource(R.drawable.cloud_1index_now);
            // Glide.with(mContext).load(R.drawable.cloud_1index_now).into(holder.iv_heart);
        }
        //剩余距离
        try {
            int count;
            if ("0.0".equals(objBean.getDistance())) {
                count = 100;
            } else {
                count = (int) (Double.parseDouble(objBean.getNowDistance()) / Double.parseDouble(objBean.getDistance()) * 100);
                if (count > 100) {
                    count = 100;
                }

            }

            holder.sbh_transfer_item.setCount(count, objBean.getOrganSeg());
            if ("done".equals(objBean.getStatus())) {

            } else {
                int heartWidth = LocalApplication.getInstance().screenW - DisplayUtil.dip2px(mContext, 16 + 20 + 10) * 2 - DisplayUtil.dip2px(mContext, 19) * 28 / 38 * 2;
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DisplayUtil.dip2px(mContext, 32), DisplayUtil.dip2px(mContext, 32));
                int leftMargin = heartWidth * count / 100;

                params.leftMargin = leftMargin;
                holder.iv_heart.setLayoutParams(params);

            }
            int distance = (int) (Double.parseDouble(objBean.getDistance()) - Double.parseDouble(objBean.getNowDistance()));
            if (distance < 0) {
                distance = 0;
            }
            holder.tv_distance.setText("剩余" + distance + "km");
        } catch (Exception e) {
            holder.tv_distance.setText("剩余" + objBean.getDistance() + "km");
        }

        holder.tv_transfer_item_blood.setText(objBean.getBlood() + "型");
        holder.tv_transfer_item_phone.setText(objBean.getPhone());
        holder.tv_transfer_item_contact_phone.setText(objBean.getContactPhone());
        holder.tv_transfer_item_start.setText(objBean.getFromCity().split("市")[0]);
        holder.tv_transfer_item_end.setText(objBean.getToHosp());

        holder.sbh_transfer_item.startAnim();
        LogUtil.e(TAG, "onStop3");
        if (objBean.getIsStart().equals("0")) {
            holder.tv_transfer_item_info.setText(objBean.getGetTime() + "  已创建好转运");
        } else if (objBean.getIsStart().equals("1")) {
            holder.tv_transfer_item_info.setText(objBean.getGetTime() + "  已开始转运");
        }
        if (objBean.getPushException() != null && !"".equals(objBean.getPushException())) {
            holder.tv_transfer_item_info.setText(objBean.getPushException());
        }
        if ("done".equals(objBean.getStatus())) {
            holder.tv_transfer_item_info.setText(objBean.getGetTime() + "  已完成转运");
            holder.tv_transfer_item_info.setTextColor(mContext.getResources().getColor(R.color.font_black_6));
        }


        try {

            holder.tv_transfer_item_time.setText(objBean.getTime());
            //打电话
            holder.ll_transfer_person.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onLeftPhoneClick(view, "转运医师", holder.tv_transfer.getText().toString(), holder.tv_transfer_item_phone.getText().toString().trim(), position);
                }
            });
            //打电话
            holder.ll_help_person.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onLeftPhoneClick(view, "协调专员", holder.tv_xieyun.getText().toString(), holder.tv_transfer_item_contact_phone.getText().toString().trim(), position);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

//    private void imageColorToGray(ImageView imageView, int id) {
//        Bitmap originImg = BitmapFactory.decodeResource(mContext.getResources(), id);
//        Bitmap grayImg = Bitmap.createBitmap(originImg.getWidth(), originImg.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(grayImg);
//        Paint paint = new Paint();
//        ColorMatrix colorMatrix = new ColorMatrix();
//        colorMatrix.setSaturation(0);
//        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);
//        paint.setColorFilter(colorMatrixFilter);
//        canvas.drawBitmap(originImg, 0, 0, paint);
//
//        imageView.setImageBitmap(grayImg);
//    }
//
//    private void layoutToGray(LinearLayout layout, int id) {
//        try {
//            Bitmap originImg = BitmapFactory.decodeResource(mContext.getResources(), id);
//            Bitmap grayImg = Bitmap.createBitmap(originImg.getWidth(), originImg.getHeight(), Bitmap.Config.RGB_565);
//            Canvas canvas = new Canvas(grayImg);
//            Paint paint = new Paint();
//            ColorMatrix colorMatrix = new ColorMatrix();
//            colorMatrix.setSaturation(0);
//            ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);
//            paint.setColorFilter(colorMatrixFilter);
//            canvas.drawBitmap(originImg, 0, 0, paint);
//            BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), grayImg);
//            layout.setBackground(bd);
//        } catch (Exception e) {
//
//        }
//    }

    @Override
    public int getItemCount() {
        return mTransfers.size();
    }

    //自定义ViewHolder，用于加载图片
    class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ll_transfer;
        TextView tv_transfer_item_organ_seg;
        AnimTextView tv_transfer_item_status;
        ImageView iv_transfer_item_organ;
        TextView tv_transfer_item_organ;
        TextView tv_transfer_item_blood;
        TextView tv_transfer_item_time;
        TextView tv_transfer_item_phone;
        TextView tv_transfer_item_contact_phone;
        TextView tv_transfer_item_start;
        TextView tv_transfer_item_end;
        SeekBarHeart sbh_transfer_item;
        TextView tv_transfer_item_info;
        RelativeLayout rl_transfer_item_no_start;
        ImageView iv_transfer_item_start;
        ImageView iv_transfer_item_del;
        ImageView iv_transfer_item_edit;
        LinearLayout ll_transfer_person;
        LinearLayout ll_help_person;
        LinearLayout ll_cover;
        TextView tv_distance;
        ImageView iv_info;
        ImageView iv_auto;
        ImageView iv_blood;
        TextView iv_left_phone;
        TextView iv_right_phone;
        LinearLayout ll_bg;
        ImageView iv_time;
        ImageView iv_dot;

        TextView tv_speed_dial;
        ImageView iv_speed_dial;
        LinearLayout ll_speed_dial;
        TextView tv_transfer;
        TextView tv_xieyun;
        GifImageView iv_heart;

        ImageView iv_example;


        public MyViewHolder(View view) {
            super(view);
            ll_transfer = (LinearLayout) view.findViewById(R.id.ll_transfer);
            tv_transfer_item_organ_seg = (TextView) view.findViewById(R.id.tv_transfer_item_organ_seg);
            tv_transfer_item_status = (AnimTextView) view.findViewById(R.id.tv_transfer_item_status);

            iv_transfer_item_organ = (ImageView) view.findViewById(R.id.iv_transfer_item_organ);
            tv_transfer_item_organ = (TextView) view.findViewById(R.id.tv_transfer_item_organ);
            tv_transfer_item_blood = (TextView) view.findViewById(R.id.tv_transfer_item_blood);
            tv_transfer_item_time = (TextView) view.findViewById(R.id.tv_transfer_item_time);
            tv_transfer_item_phone = (TextView) view.findViewById(R.id.tv_transfer_item_phone);
            tv_transfer_item_contact_phone = (TextView) view.findViewById(R.id.tv_transfer_item_contact_phone);
            tv_transfer_item_start = (TextView) view.findViewById(R.id.tv_transfer_item_start);
            tv_transfer_item_end = (TextView) view.findViewById(R.id.tv_transfer_item_end);
            sbh_transfer_item = (SeekBarHeart) view.findViewById(R.id.sbh_transfer_item);
            tv_transfer_item_info = (TextView) view.findViewById(R.id.tv_transfer_item_info);
            rl_transfer_item_no_start = (RelativeLayout) view.findViewById(R.id.rl_transfer_item_no_start);
            iv_transfer_item_start = (ImageView) view.findViewById(R.id.iv_transfer_item_start);
            iv_transfer_item_del = (ImageView) view.findViewById(R.id.iv_transfer_item_del);
            iv_transfer_item_edit = (ImageView) view.findViewById(R.id.iv_transfer_item_edit);
            ll_transfer_person = (LinearLayout) view.findViewById(R.id.ll_transfer_person);
            ll_help_person = (LinearLayout) view.findViewById(R.id.ll_help_person);
            ll_cover = (LinearLayout) view.findViewById(R.id.ll_cover);
            tv_distance = (TextView) view.findViewById(R.id.tv_distance);
            iv_info = (ImageView) view.findViewById(R.id.iv_info);
            iv_auto = (ImageView) view.findViewById(R.id.iv_auto);
            iv_blood = (ImageView) view.findViewById(R.id.iv_blood);
            iv_left_phone = (TextView) view.findViewById(R.id.iv_left_phone);
            iv_right_phone = (TextView) view.findViewById(R.id.iv_right_phone);
            ll_bg = (LinearLayout) view.findViewById(R.id.ll_bg);
            iv_time = (ImageView) view.findViewById(R.id.iv_time);
            iv_dot = (ImageView) view.findViewById(R.id.iv_dot);

            tv_speed_dial = (TextView) view.findViewById(R.id.tv_speed_dial);
            iv_speed_dial = (ImageView) view.findViewById(R.id.iv_speed_dial);
            ll_speed_dial = (LinearLayout) view.findViewById(R.id.ll_speed_dial);

            tv_transfer = (TextView) view.findViewById(R.id.tv_transfer);
            tv_xieyun = (TextView) view.findViewById(R.id.tv_xieyun);

            iv_heart = (GifImageView) view.findViewById(R.id.iv_heart);
            iv_example = (ImageView) view.findViewById(R.id.iv_example);
        }
    }


}
