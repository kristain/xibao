package com.drjing.xibao.module.workbench.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.drjing.xibao.module.entity.NurseTagEntity;
import com.drjing.xibao.module.entity.OrderTypeEnum;
import com.drjing.xibao.module.performance.adapter.BaseAdapterHelper;
import com.drjing.xibao.module.performance.adapter.QuickAdapter;
import com.drjing.xibao.module.ui.UIHelper;
import com.kristain.common.utils.DateTimeUtils;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.List;

/**
 * 销售日志
 * Created by kristain on 15/12/30.
 */
public class SaleLogActivity extends SwipeBackActivity {

    /**
     * 后退按钮
     */
    private Button btnBack;

    /**
     * Title栏文案
     */
    private TextView textHeadTitle;

    private ListView listView;

    private TextView btnRight;

    private Bundle bundle;

    QuickAdapter<NurseTagEntity> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_log);
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
        btnRight = (TextView) findViewById(R.id.btnRight);
        btnRight.setText("新增");
        btnRight.setVisibility(View.VISIBLE);
        textHeadTitle.setText("销售日志");
        listView = (ListView) findViewById(R.id.listView);
        adapter = new QuickAdapter<NurseTagEntity>(this, R.layout.sale_log_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, final NurseTagEntity item) {
                List<NurseTagEntity> list = JSONArray.parseArray(item.getList(), NurseTagEntity.class);
                helper.setText(R.id.creater, StringUtils.isEmpty(item.getCreater()) ? item.getCreaterName() : item.getCreater())
                        .setText(R.id.adviser, item.getAdviserName())
                        .setText(R.id.storeName, item.getStoreName())
                        .setText(R.id.order, item.getOrderCode())
                        .setText(R.id.time, StringUtils.isEmpty(item.getTime()) ? "" : DateTimeUtils.formatDateByMill(Long.parseLong(item.getTime())));
                LinearLayout layout  = (LinearLayout) helper.getView().findViewById(R.id.product_list);
                layout.setOrientation(LinearLayout.VERTICAL);
                if (list != null && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        Log.e("TAG", "amount:" + list.get(i).getAmount() + "categoryName:" + list.get(i).getCategoryName());
                        List<NurseTagEntity> projectListjson = JSONArray.parseArray(list.get(i).getProjectList(), NurseTagEntity.class);
                        String projectLists = "";
                        if (projectListjson != null && projectListjson.size() > 0) {
                            for (int j = 0; j < projectListjson.size(); j++) {
                                projectLists += projectListjson.get(j).getName() + " ";
                            }
                        }
                        Log.e("TAG", "projectLists:" + projectLists);
                        LinearLayout myLayout = new LinearLayout(context);
                        myLayout.setBackgroundColor(getResources().getColor(R.color.new_content_item_bg));
                        TextView cateName = new TextView(context);
                        cateName.setPadding(30, 30, 0, 30);
                        cateName.setText(list.get(i).getCategoryName() + ":    " + list.get(i).getAmount() + "元");
                        myLayout.addView(cateName);
                        layout.addView(myLayout);

                        View line = new View(context);
                        line.setBackgroundColor(getResources().getColor(R.color.new_content_item_split_view));
                        line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 3));
                        layout.addView(line);

                        LinearLayout projectLayout = new LinearLayout(context);
                        projectLayout.setBackgroundColor(getResources().getColor(R.color.new_content_item_bg));
                        TextView project = new TextView(context);
                        project.setPadding(30, 30, 0, 30);
                        project.setText("项目:    " + projectLists);
                        projectLayout.addView(project);
                        layout.addView(projectLayout);

                        View line2 = new View(context);
                        line2.setBackgroundColor(getResources().getColor(R.color.new_content_item_split_view));
                        line2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 3));
                        layout.addView(line2);
                    }

                }
                /*
                helper.setText(R.id.product_name_text, item.getCategoryName())
                        .setText(R.id.money, item.getAmount()+"元")
                        .setText(R.id.project, project);*/
                helper.getView().findViewById(R.id.delete_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delSaleLog(item.getId());
                    }
                });

                /**
                 * 点击查看
                 */
                helper.getView().findViewById(R.id.detail_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("code", item.getOrderId());
                        bundle.putString("order_type", OrderTypeEnum.SALELOG.getCode());
                        UIHelper.showOrdersDetail(SaleLogActivity.this, bundle);
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

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showSaleAddLog(SaleLogActivity.this, bundle);
            }
        });
    }

    /**
     * 数据初始化
     */
    private void loadData() {
        NurseTagEntity entity = new NurseTagEntity();
        entity.setCustomerId(bundle.getString("customer_id"));
        entity.setOrderId(StringUtils.isEmpty(bundle.getString("order_id")) ? bundle.getString("customer_id") : bundle.getString("order_id"));
        if(!StringUtils.isEmpty(entity.getCustomerId())){
            HttpClient.getSaleLogListByCustomer(entity, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("getSaleLogListTAG", "返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        List<NurseTagEntity> list = JSONArray.parseArray(object.getString("data"), NurseTagEntity.class);
                        adapter.clear();
                        adapter.addAll(list);
                        listView.setAdapter(adapter);
                    } else if (HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(SaleLogActivity.this);
                    } else {
                        Toast.makeText(SaleLogActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("getSaleLogListTAG", "失败返回数据:" + request.toString());
                }
            }, SaleLogActivity.this);
        }else{
            Toast.makeText(SaleLogActivity.this, "缺少请求参数[customerId]", Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * 删除销售日志
     */
    private void delSaleLog(int id) {
        NurseTagEntity entity = new NurseTagEntity();
        entity.setId(id);
        if(!StringUtils.isEmpty(entity.getId()+"")){
            HttpClient.deleteSaleLog(entity, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("deleteSaleLogTAG", "返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        Toast.makeText(SaleLogActivity.this, "删除成功",
                                Toast.LENGTH_SHORT).show();
                        loadData();
                    } else if (HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(SaleLogActivity.this);
                    } else {
                        Toast.makeText(SaleLogActivity.this, "删除失败",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("deleteSaleLogTAG", "失败返回数据:" + request.toString());
                    Toast.makeText(SaleLogActivity.this, "删除失败",
                            Toast.LENGTH_SHORT).show();
                }
            }, SaleLogActivity.this);
        }else{
            Toast.makeText(SaleLogActivity.this, "缺少请求参数[id]",
                    Toast.LENGTH_SHORT).show();
        }

    }


}
