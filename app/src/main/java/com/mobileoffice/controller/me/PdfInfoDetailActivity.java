package com.mobileoffice.controller.me;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mobileoffice.R;
import com.mobileoffice.controller.BaseActivity;
import com.mobileoffice.controller.cloud_monitor.PdfActivity;
import com.mobileoffice.controller.cloud_monitor.PreviewInfoActivity;
import com.mobileoffice.http.URL;
import com.mobileoffice.json.PdfInfoDetailJson;
import com.mobileoffice.utils.CONSTS;
import com.mobileoffice.utils.SharePreUtils;
import com.mobileoffice.utils.ToastUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 99213 on 2017/9/5.
 */

public class PdfInfoDetailActivity extends BaseActivity implements View.OnClickListener,PdfInfoDetailAdapter.OnRecyclerViewItemClickListener {
    private LinearLayout ll_back;
    private XRecyclerView xrv_pdf;
    private String phone;
    private int page = 0;
    private List<PdfInfoDetailJson.ObjBean> mList;
    private LinearLayoutManager mLinearLayoutManager;
    private PdfInfoDetailAdapter mPdfInfoDetailAdapter;
    private LinearLayout ll_no_data;

    @Override
    protected void initVariable() {
        phone = SharePreUtils.getString("phone", "", this);
        mList = new ArrayList<>();
        mPdfInfoDetailAdapter = new PdfInfoDetailAdapter(mList,this);
        mPdfInfoDetailAdapter.setOnItemClickListener(this);
        xrv_pdf.setLayoutManager(mLinearLayoutManager);
        xrv_pdf.setAdapter(mPdfInfoDetailAdapter);
        loadPdfDetail();
        xrv_pdf.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        xrv_pdf.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);
        xrv_pdf.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                page = 0;
                loadPdfDetail();
            }

            @Override
            public void onLoadMore() {
                page++;
                loadPdfDetail();
            }
        });
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.pdf_info_detail);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        xrv_pdf = (XRecyclerView) findViewById(R.id.xrv_pdf);
        mLinearLayoutManager = new LinearLayoutManager(this);
        ll_no_data = (LinearLayout) findViewById(R.id.ll_no_data);
        ll_back.setOnClickListener(this);

    }


    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                finish();
                break;
        }
    }

    private void loadPdfDetail() {
        RequestParams params = new RequestParams(URL.DOWNLOAD_PDF);
        params.addBodyParameter("action", "pdfList");
        params.addBodyParameter("phone", phone);
        params.addBodyParameter("page", page + "");
        params.addBodyParameter("pageSize", CONSTS.PAGE_SIZE + "");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PdfInfoDetailJson pdfInfoDetailJson = new Gson().fromJson(result, PdfInfoDetailJson.class);
                if (pdfInfoDetailJson != null && pdfInfoDetailJson.getResult() == CONSTS.SEND_OK) {
                    if (page == 0) {
                        mList = pdfInfoDetailJson.getObj();
                    } else {
                        mList.addAll(pdfInfoDetailJson.getObj());
                    }
                    mPdfInfoDetailAdapter.refresh(mList);
                    ll_no_data.setVisibility(View.GONE);
                    xrv_pdf.setVisibility(View.VISIBLE);

                }else{
                      ll_no_data.setVisibility(View.VISIBLE);
                      xrv_pdf.setVisibility(View.GONE);
                }
                xrv_pdf.loadMoreComplete();
                xrv_pdf.refreshComplete();
                //ToastUtil.showToast("result:"+result,PdfInfoDetailActivity.this);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                xrv_pdf.loadMoreComplete();
                xrv_pdf.refreshComplete();
                ll_no_data.setVisibility(View.VISIBLE);
                xrv_pdf.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }
    private void downloadPdf(final String organSeg,final String organ) {

        showWaitDialog(getResources().getString(R.string.loading), false, "loading");
        final  String path = getFilesDir() + "/pdf/" + organSeg + ".pdf";
        RequestParams requestParams = new RequestParams(URL.DOWNLOAD_PDF);
        requestParams.addBodyParameter("action","pdf");
        requestParams.addBodyParameter("organSeg",organSeg);
        requestParams.addBodyParameter("phone",phone);
        requestParams.addBodyParameter("organ",organ);
        requestParams.setSaveFilePath(path);

        x.http().get(requestParams, new Callback.ProgressCallback<File>() {
            @Override
            public void onWaiting() {
            }

            @Override
            public void onStarted() {
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {

            }

            @Override
            public void onSuccess(File result) {
                dismissDialog();
                Intent intent = new Intent(PdfInfoDetailActivity.this,PdfActivity.class);
                intent.putExtra("path",result.getAbsolutePath());
                intent.putExtra("organSeg",organSeg);
                startActivity(intent);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                dismissDialog();
                ToastUtil.showToast(""+ex.getMessage(),PdfInfoDetailActivity.this);
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
    public void onItemClick(View view, int position) {
        downloadPdf(mList.get(position).getOrganSeg(),mList.get(position).getOrgan());
    }

    @Override
    public void onButtonClick(View view, int position) {

    }
}
