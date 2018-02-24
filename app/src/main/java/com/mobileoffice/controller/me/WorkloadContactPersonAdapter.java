package com.mobileoffice.controller.me;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobileoffice.R;
import com.mobileoffice.json.Workload;
import com.mobileoffice.json.WorkloadAllJson;
import com.mobileoffice.view.RoundImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by 99213 on 2017/4/23.
 */

public class WorkloadContactPersonAdapter extends RecyclerView.Adapter<WorkloadContactPersonAdapter.MyViewHolder> {


    private static final String TAG = "PdfInfoDetailAdapter";
    private List<WorkloadAllJson.ObjBean> mObjBeans;
    private Context mContext;


    private List<Workload> mWorkloads;

    private GridLayoutManager mGridLayoutManager;
    private WorkloadContactPersonItemAdapter mWorkloadContactPersonItemAdapter;


    //自定义监听事件
    interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);

        void onButtonClick(View view, int position);

    }

    /**
     * 初始化recyclerView
     */
    private void initRecycler(RecyclerView pRv, List<Workload> pWorkloads) {
        mWorkloads = pWorkloads;
        mGridLayoutManager = new GridLayoutManager(mContext, 4);
        //pRv.addItemDecoration(new DividerGridItemDecoration(mContext));
        mWorkloadContactPersonItemAdapter = new WorkloadContactPersonItemAdapter(pWorkloads, mContext);
        pRv.setLayoutManager(mGridLayoutManager);
        pRv.setAdapter(mWorkloadContactPersonItemAdapter);

    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;


    public void refresh(List<WorkloadAllJson.ObjBean> objBeens) {

        mObjBeans = objBeens;
        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public WorkloadContactPersonAdapter(List<WorkloadAllJson.ObjBean> objBeens, Context context) {
        mObjBeans = objBeens;
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.workload_contact_person_item, parent, false);

        MyViewHolder holder = new MyViewHolder(view);

        return holder;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        WorkloadAllJson.ObjBean objBean = mObjBeans.get(position);

        Picasso.with(mContext).load(objBean.getPhotoUrl()).error(R.drawable.msg_2list_linkman).into(holder.riv_url);
        holder.tv_name.setText(objBean.getTrueName());
        holder.tv_role.setText(objBean.getRoleName());
        if ("医生".equals(objBean.getRoleName())) {
            holder.iv_flag.setImageResource(R.drawable.mine_5work_xie_yi);
        } else if ("护士".equals(objBean.getRoleName())) {
            holder.iv_flag.setImageResource(R.drawable.mine_5work_xie_hu);
        }

        initRecycler(holder.rv_content, objBean.getWorkloads());

    }

    @Override
    public int getItemCount() {

        return mObjBeans.size();


    }


    //自定义ViewHolder
    class MyViewHolder extends RecyclerView.ViewHolder {


        RoundImageView riv_url;
        TextView tv_name;
        TextView tv_role;
        ImageView iv_flag;
        RecyclerView rv_content;


        public MyViewHolder(View view) {
            super(view);
            riv_url = (RoundImageView) view.findViewById(R.id.riv_url);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_role = (TextView) view.findViewById(R.id.tv_role);
            iv_flag = (ImageView) view.findViewById(R.id.iv_flag);
            rv_content = (RecyclerView) view.findViewById(R.id.rv_content);


        }
    }

    class WorkloadContactPersonItemAdapter extends RecyclerView.Adapter<WorkloadContactPersonItemAdapter.MyViewHolder> {


        private static final String TAG = "PdfInfoDetailAdapter";
        private List<Workload> mObjBeans;
        private Context mContext;


        public void refresh(List<Workload> objBeens) {

            mObjBeans = objBeens;
            this.notifyDataSetChanged();
        }


        public WorkloadContactPersonItemAdapter(List<Workload> objBeens, Context context) {
            mObjBeans = objBeens;
            mContext = context;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.workload_person_item_item, parent, false);

            MyViewHolder holder = new MyViewHolder(view);

            return holder;

        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            Workload workload = mWorkloads.get(position);
            holder.tv_role.setText(workload.getPostRole());
            holder.tv_count.setText(workload.getCount()+"");

            if (position % 4 == 0) {
                holder.tv_left.setVisibility(View.GONE);
            } else if (position % 4 == 1) {
                holder.tv_left.setVisibility(View.GONE);
            } else if (position % 4 == 2) {
                holder.tv_left.setVisibility(View.GONE);
            } else if (position % 4 == 3) {
                holder.tv_left.setVisibility(View.GONE);
                holder.tv_right.setVisibility(View.GONE);
            }
            holder.tv_top.setVisibility(View.GONE);
            if (position / 4 == mObjBeans.size() / 4) {
                holder.tv_bottom.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {

            return mObjBeans.size();


        }


        //自定义ViewHolder
        class MyViewHolder extends RecyclerView.ViewHolder {


            TextView tv_role;
            TextView tv_count;

            TextView tv_top;
            TextView tv_right;
            TextView tv_bottom;
            TextView tv_left;


            public MyViewHolder(View view) {
                super(view);

                tv_role = (TextView) view.findViewById(R.id.tv_role);
                tv_count = (TextView) view.findViewById(R.id.tv_count);

                tv_top = (TextView) view.findViewById(R.id.tv_top);
                tv_right = (TextView) view.findViewById(R.id.tv_right);
                tv_bottom = (TextView) view.findViewById(R.id.tv_bottom);
                tv_left = (TextView) view.findViewById(R.id.tv_left);


            }
        }

    }
}

