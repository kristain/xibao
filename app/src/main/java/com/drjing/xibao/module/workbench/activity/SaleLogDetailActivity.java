package com.drjing.xibao.module.workbench.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.drjing.xibao.common.view.materialwidget.PaperButton;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.module.entity.NurseTagEntity;
import com.drjing.xibao.module.entity.OrderEntity;
import com.drjing.xibao.module.entity.OrderTypeEnum;
import com.drjing.xibao.module.performance.adapter.BaseAdapterHelper;
import com.drjing.xibao.module.performance.adapter.QuickAdapter;
import com.drjing.xibao.module.ui.UIHelper;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.List;

/**
 * 销售日志确认
 * Created by kristain on 15/12/30.
 */
public class SaleLogDetailActivity extends SwipeBackActivity {

    /**
     * 后退按钮
     */
    private Button btnBack;

    /**
     * Title栏文案
     */
    private TextView textHeadTitle;

    private ListView listView;


    private Bundle bundle;

    QuickAdapter<NurseTagEntity> adapter;


    private PaperButton submit_btn;

    private TextView tip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_log_detail);
        bundle = getIntent().getExtras();
        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("销售日志");
        tip =  (TextView)findViewById(R.id.tip);
        listView = (ListView) findViewById(R.id.listView);
        submit_btn = (PaperButton)findViewById(R.id.submit_btn);
        adapter = new QuickAdapter<NurseTagEntity>(this, R.layout.sale_log_detail_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, final NurseTagEntity item) {
                List<NurseTagEntity> list = JSONArray.parseArray(item.getProjectList(), NurseTagEntity.class);
                helper.setText(R.id.creater_text, item.getCategoryName())
                        .setText(R.id.money, item.getAmount()+"元")
                        .setText(R.id.order,bundle.getString("order_no"));
                String product="";
                for (int i=0;i<list.size();i++){
                    product+=list.get(i).getName()+" ";
                }
                ((TextView) helper.getView().findViewById(R.id.time)).setText(product);

                /**
                 * 点击查看
                 */
                ((TextView) helper.getView().findViewById(R.id.detail_btn)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle item = new Bundle();
                        item.putString("code", bundle.getString("code"));
                        item.putString("order_type", OrderTypeEnum.SALELOG.getCode());
                        UIHelper.showOrdersDetail(SaleLogDetailActivity.this, item);
                    }
                });

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
         * 确认销售日志
         */
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitSaleLog();
            }
        });

    }

    /**
     * 数据初始化
     */
    private void loadData() {
        OrderEntity entity = new OrderEntity();
        entity.setId(Integer.parseInt(bundle.getString("code")));
        if(!StringUtils.isEmpty(entity.getId()+"")){
            HttpClient.getSaleLogList(entity, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("getSaleLogListTAG", "返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        List<NurseTagEntity> list = JSONArray.parseArray(object.getString("data"), NurseTagEntity.class);
                        if(list==null || list.size()==0){
                            tip.setVisibility(View.VISIBLE);
                            submit_btn.setVisibility(View.GONE);
                            listView.setVisibility(View.GONE);
                        }else{
                            adapter.clear();
                            adapter.addAll(list);
                            listView.setAdapter(adapter);
                        }

                    } else if (HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(SaleLogDetailActivity.this);
                    } else {
                        Toast.makeText(SaleLogDetailActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("getSaleLogListTAG", "失败返回数据:" + request.toString());
                }
            }, SaleLogDetailActivity.this);
        }else{
            Toast.makeText(SaleLogDetailActivity.this, "缺少请求参数[code]", Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * 删除销售日志
     */
    private void submitSaleLog() {
        OrderEntity entity = new OrderEntity();
        entity.setId(Integer.parseInt(bundle.getString("code")));
        if(!StringUtils.isEmpty(entity.getId()+"")){
            HttpClient.saleLogSubmit(entity, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("saleLogSubmitTAG", "返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        Intent intent = getIntent();
                        intent.putExtra("position", bundle.getString("position"));
                        setResult(RESULT_OK, intent);
                        finish();
                    } else if (HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(SaleLogDetailActivity.this);
                    } else {
                        Toast.makeText(SaleLogDetailActivity.this, "提交失败",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("deleteSaleLogTAG", "失败返回数据:" + request.toString());
                    Toast.makeText(SaleLogDetailActivity.this, "提交失败",
                            Toast.LENGTH_SHORT).show();
                }
            }, SaleLogDetailActivity.this);
        }else{
            Toast.makeText(SaleLogDetailActivity.this, "缺少请求参数[id]",
                    Toast.LENGTH_SHORT).show();
        }

    }


}
