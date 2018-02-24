package com.mobileoffice.controller.cloud_monitor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobileoffice.R;
import com.mobileoffice.controller.cloud_monitor.frag.ExceptionEntity;
import com.mobileoffice.controller.cloud_monitor.frag.TransferBaseFragment;
import com.mobileoffice.entity.PathInfo;
import com.mobileoffice.json.TransferRecordJson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 99213 on 2017/8/11.
 */

public class MapRightFragment extends TransferBaseFragment {
    private View root;
    //时间轴
    private RecyclerView rv_content;
    private LinearLayoutManager mLinearLayoutManager;
    private DetailContentAdapter mDetailContentAdapter;
    private List<PathInfo> mDetails;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.map_right, container, false);


        return root;
    }


    public static final MapRightFragment newInstance()
    {
        MapRightFragment fragment = new MapRightFragment();
        Bundle bundle = new Bundle();


        fragment.setArguments(bundle);

        return fragment ;
    }


    private void recycler() {
        mDetails = new ArrayList<>();
        //mDetails.add("1");
        rv_content = (RecyclerView) root.findViewById(R.id.rv_content);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mDetailContentAdapter = new DetailContentAdapter(getActivity(), mDetails);
        rv_content.setLayoutManager(mLinearLayoutManager);
        rv_content.setAdapter(mDetailContentAdapter);


    }

    @Override
    public void initData() {

    }
}
