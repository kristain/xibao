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
import com.drjing.xibao.module.entity.RoleEnum;
import com.drjing.xibao.module.performance.adapter.BaseAdapterHelper;
import com.drjing.xibao.module.performance.adapter.QuickAdapter;
import com.drjing.xibao.module.ui.UIHelper;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.List;

/**
 * 日报明细
 * Created by kristain on 15/12/30.
 */
public class ReportDetailActivity extends SwipeBackActivity {

    private static final String TAG="ReportDetailActivity";
    /**
     * 后退按钮
     */
    private Button btnBack;

    /**
     * Title栏文案
     */
    private TextView textHeadTitle;
    private ListView submit_listview,nosubmit_listview;

    QuickAdapter<ReportEntity> adapter;

    QuickAdapter<ReportEntity> nosubmit_adapter;

    private TextView fill_report;

    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);
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
        fill_report = (TextView)findViewById(R.id.fill_report);
        if(RoleEnum.BOSS.getCode().equals(bundle.getString("role_key")) || RoleEnum.AREAMANAGER.getCode().equals(bundle.getString("role_key"))){
            fill_report.setVisibility(View.GONE);
        }


        submit_listview = (ListView)findViewById(R.id.submit_listview);
        nosubmit_listview = (ListView)findViewById(R.id.nosubmit_listview);
        adapter = new QuickAdapter<ReportEntity>(this, R.layout.account_list_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, ReportEntity item) {
                helper.setText(R.id.name, item.getUsername())
                .setText(R.id.role,item.getRoleName())
                        .setText(R.id.account_name, item.getAccount())
                .setText(R.id.content, "0".equals(item.getCount())?"全部完成":"还有" + item.getCount() + "个任务未完成");
            }
        };

        nosubmit_adapter = new QuickAdapter<ReportEntity>(this, R.layout.account_list_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, ReportEntity item) {
                helper.setText(R.id.name, item.getUsername())
                        .setText(R.id.role, item.getRoleName())
                        .setText(R.id.account_name, item.getAccount())
                        .setText(R.id.content, "还有" + item.getCount() + "个任务未完成");
                //helper.getView().findViewById(R.id.content).setVisibility(View.GONE);
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

        /**
         * 点击填写我的日报
         */
        fill_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showReport(ReportDetailActivity.this);
            }
        });

        /**
         * 点击已提交的日报
         */
        submit_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("account_name",((TextView) view.findViewById(R.id.account_name)).getText().toString());
                bundle.putString("role_key",RoleEnum.getCodeByMsg(((TextView) view.findViewById(R.id.role)).getText().toString()));
                UIHelper.showStoreReportView(ReportDetailActivity.this, bundle);
            }
        });

        /**
         * 点击未提交的日报
         */
        nosubmit_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("account_name",((TextView) view.findViewById(R.id.account_name)).getText().toString());
                bundle.putString("role_key",RoleEnum.getCodeByMsg(((TextView) view.findViewById(R.id.role)).getText().toString()));
                UIHelper.showStoreReportView(ReportDetailActivity.this, bundle);
            }
        });
    }


    private void loadData(){
        if(StringUtils.isEmpty(bundle.getString("storeid"))){
            return;
        }
        ReportEntity param = new ReportEntity();
        param.setDay(bundle.getString("day"));
        param.setStoreid(bundle.getString("storeid"));
        if(!StringUtils.isEmpty(param.getStoreid())){
            HttpClient.getStoreReportDetail(param, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("getStoreReportDetailTAG", "成功返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        JSONObject data = JSON.parseObject(object.getString("data"));
                        List<ReportEntity> list = JSONArray.parseArray(data.getString("haveList"), ReportEntity.class);
                        List<ReportEntity> notList = JSONArray.parseArray(data.getString("notList"), ReportEntity.class);
                        adapter.addAll(list);
                        nosubmit_adapter.addAll(notList);
                        submit_listview.setAdapter(adapter);
                        nosubmit_listview.setAdapter(nosubmit_adapter);
                    }  else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(ReportDetailActivity.this);
                    }else {
                        Log.i("getStoreReportDetailTAG", "获取数据失败:" + body);
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("getStoreReportDetailTAG", "失败返回数据:" + request.toString());
                }
            }, ReportDetailActivity.this);
        }else{
            Toast.makeText(this,"缺少请求参数[storeid]",Toast.LENGTH_LONG).show();
        }

    }

}
