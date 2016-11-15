package com.drjing.xibao.module.workbench.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.module.entity.ReportEntity;
import com.drjing.xibao.module.performance.adapter.BaseAdapterHelper;
import com.drjing.xibao.module.performance.adapter.QuickAdapter;
import com.drjing.xibao.module.ui.UIHelper;
import com.kristain.common.utils.DateTimeUtils;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.List;

/**
 * 日报列表
 * Created by kristain on 15/12/30.
 */
public class ReportListActivity extends SwipeBackActivity {

    private static final String TAG="ReportListActivity";
    /**
     * 后退按钮
     */
    private Button btnBack;

    /**
     * Title栏文案
     */
    private TextView textHeadTitle;

    private ListView listview;
    QuickAdapter<ReportEntity> adapter;

    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);
        bundle=getIntent().getExtras();
        initView();
        initEvent();
        loadData();
    }


    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("日报");
        listview = (ListView)findViewById(R.id.listview);
        adapter = new QuickAdapter<ReportEntity>(this, R.layout.report_list_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, ReportEntity item) {
                helper.setText(R.id.date, (StringUtils.isEmpty(item.getDay()) ? "" : DateTimeUtils.formatDateByMill(Long.parseLong(item.getDay()), DateTimeUtils.DF_YYYY_MM_DD)))
                        .setText(R.id.haveSubmit, item.getHaveSubmit() + "人提交")
                        .setText(R.id.notSubmit, item.getNotSubmit() + "人未提交");
            }
        };
    }


    private void initEvent() {
        /**
         * 返回后退点击事件
         */
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bundle.putString("day",((TextView) view.findViewById(R.id.date)).getText().toString());
                UIHelper.showStoreReportDetail(ReportListActivity.this, bundle);
            }
        });

    }


    private void loadData(){
        ReportEntity param = new ReportEntity();
        param.setDay(DateTimeUtils.gainCurrentDate(DateTimeUtils.DF_YYYY_MM_DD));
        param.setStoreid(bundle.getString("storeid"));
        if(!StringUtils.isEmpty(param.getStoreid())){
            HttpClient.getStoreReportList(param, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("getStoreReportListTAG", "成功返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        List<ReportEntity> list = JSONArray.parseArray(object.getString("data"), ReportEntity.class);
                        adapter.addAll(list);
                        listview.setAdapter(adapter);
                    } else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(ReportListActivity.this);
                    } else {
                        Log.i("getStoreReportListTAG", "获取数据失败:" + body);
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("getStoreReportListTAG", "失败返回数据:" + request.toString());
                }
            }, ReportListActivity.this);
        }else{
            Toast.makeText(this,"缺少请求参数[storeid]",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
