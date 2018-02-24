package com.mobileoffice.controller.rong;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobileoffice.R;

import java.util.List;

import io.rong.imlib.model.Conversation;

/**
 * Created by 99213 on 2017/5/15.
 */

public  class ConversationListTestAdapter extends RecyclerView.Adapter<ConversationListTestAdapter.MyViewHolder>{
    private Context mAdapterContext;
    private List<Conversation> mAdapterConversations;
    private ConversationListTextListener mListener;
    interface  ConversationListTextListener{
        void onItemClick(View view, int position);
    }
    public void setConversationListTextListener(ConversationListTextListener listener){
        mListener = listener;
    }
    public ConversationListTestAdapter(Context context,List<Conversation> conversations){
        mAdapterContext = context;
        mAdapterConversations = conversations;

    }
    public void refresh(List<Conversation> conversations){


        mAdapterConversations = conversations;
        notifyDataSetChanged();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(mAdapterContext).inflate(R.layout.dialog_alert_common_layout,null);
        return new MyViewHolder(root);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.dialog_content_tv.setText(mAdapterConversations.get(position).getTargetId());
        holder.dialog_content_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(view,position);
            }
        });
    }

    @Override
    public int getItemCount() {


        return mAdapterConversations.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView dialog_content_tv;
        public MyViewHolder(View itemView) {
            super(itemView);
            dialog_content_tv = (TextView) itemView.findViewById(R.id.dialog_content_tv);
        }
    }
}
