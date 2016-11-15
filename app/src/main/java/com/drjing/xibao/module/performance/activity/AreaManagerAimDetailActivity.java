package com.drjing.xibao.module.performance.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.utils.DisplayUtils;
import com.drjing.xibao.common.utils.FuncUtils;
import com.drjing.xibao.common.view.materialwidget.PaperButton;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.module.entity.RoleEnum;
import com.drjing.xibao.module.entity.TargetEntity;
import com.drjing.xibao.module.entity.TargetTypeEnum;
import com.drjing.xibao.module.entity.UserParam;
import com.drjing.xibao.module.performance.adapter.BaseAdapterHelper;
import com.drjing.xibao.module.performance.adapter.QuickAdapter;
import com.drjing.xibao.module.ui.UIHelper;
import com.kristain.common.utils.DateTimeUtils;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 区域经理目标看板
 * Created by kristain on 16/1/3.
 */
public class AreaManagerAimDetailActivity extends SwipeBackActivity {

    /**
     * 后退返回按钮
     */
    private Button btnBack;
    /**
     * 标题栏文案
     */
    private TextView textHeadTitle;

    private Bundle bundle;

    private TextView aim_text;

    private RadioButton pre_month,next_month;
    /**
     * 查询目标月份
     */
    private String month;

    private ListView list_view;

    QuickAdapter<TargetEntity> adapter;

    List<TargetEntity> list = new ArrayList<TargetEntity>();
    private int screen_width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aimdetail_list);
        bundle=getIntent().getExtras();
        month = bundle.getString("month");
        screen_width = DisplayUtils.getScreenW(this);
        screen_width = screen_width * 2 / 3 - 200;
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        btnBack = (Button)findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        aim_text = (TextView)findViewById(R.id.aim_text);
        pre_month= (RadioButton)findViewById(R.id.pre_month);
        next_month= (RadioButton)findViewById(R.id.next_month);
        list_view = (ListView)findViewById(R.id.list_view);
        textHeadTitle.setText("目标看板");
        aim_text.setText(bundle.getString("target_title"));

        adapter = new QuickAdapter<TargetEntity>(this, R.layout.aimdetail_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, final TargetEntity item) {
                final JSONObject account = JSON.parseObject(item.getAccount());
                helper.setText(R.id.month_text, item.getMonth())
                        .setText(R.id.counselor_name, account.getString("roleName") + ":" + account.getString("username"))
                        .setText(R.id.store_name, account.getString("store_name"));
                if(RoleEnum.STAFF.getCode().equals(account.getString("roleKey"))){
                    helper.getView().findViewById(R.id.paper_button).setVisibility(View.GONE);
                }
                ((PaperButton) helper.getView().findViewById(R.id.paper_button)).setText("查看下级");
                if ("生美预收".equals(aim_text.getText().toString())) {
                    ((TextView)helper.getView().findViewById(R.id.receive_rate_text)).setText(FuncUtils.formatMoney2(item.getTarget_health_beauty()) + "w");
                    ((TextView)helper.getView().findViewById(R.id.fact_rate_text)).setText( FuncUtils.formatMoney2(item.getSale_health_beauty()) + "w");
                    ((TextView)helper.getView().findViewById(R.id.complete_rate_text)).setText(FuncUtils.getPercentRate(item.getTarget_health_beauty(), item.getSale_health_beauty()) + "%");
                    helper.getView().findViewById(R.id.fact_rate).setLayoutParams(new LinearLayout.LayoutParams(screen_width*Integer.parseInt(FuncUtils.getPercentRateInt(item.getTarget_health_beauty(), item.getSale_health_beauty()))/100+10, LinearLayout.LayoutParams.MATCH_PARENT));
                    helper.getView().findViewById(R.id.complete_rate).setLayoutParams(new LinearLayout.LayoutParams(screen_width*Integer.parseInt(FuncUtils.getPercentRateInt(item.getTarget_health_beauty(), item.getSale_health_beauty()))/100+10, LinearLayout.LayoutParams.MATCH_PARENT));
                    if("0.0".equals(FuncUtils.formatMoney(item.getTarget_health_beauty()))){
                        helper.getView().findViewById(R.id.receive_rate).setLayoutParams(new LinearLayout.LayoutParams(10, LinearLayout.LayoutParams.MATCH_PARENT));
                    }else{
                        helper.getView().findViewById(R.id.receive_rate).setLayoutParams(new LinearLayout.LayoutParams(screen_width + 10, LinearLayout.LayoutParams.MATCH_PARENT));
                    }
                }
                if ("医美预收".equals(aim_text.getText().toString())) {
                    ((TextView)helper.getView().findViewById(R.id.receive_rate_text)).setText(FuncUtils.formatMoney2(item.getTarget_medical_beauty())+ "万");
                    ((TextView)helper.getView().findViewById(R.id.fact_rate_text)).setText(FuncUtils.formatMoney2(item.getSale_medical_beauty())+ "万");
                    ((TextView)helper.getView().findViewById(R.id.complete_rate_text)).setText(FuncUtils.getPercentRate(item.getTarget_medical_beauty(), item.getSale_medical_beauty()) + "%");
                    helper.getView().findViewById(R.id.fact_rate).setLayoutParams(new LinearLayout.LayoutParams(screen_width*Integer.parseInt(FuncUtils.getPercentRateInt(item.getTarget_medical_beauty(), item.getSale_medical_beauty()))/100+10, LinearLayout.LayoutParams.MATCH_PARENT));
                    helper.getView().findViewById(R.id.complete_rate).setLayoutParams(new LinearLayout.LayoutParams(screen_width*Integer.parseInt(FuncUtils.getPercentRateInt(item.getTarget_medical_beauty(), item.getSale_medical_beauty()))/100+10, LinearLayout.LayoutParams.MATCH_PARENT));
                    if("0.0".equals(FuncUtils.formatMoney(item.getTarget_medical_beauty()))){
                        helper.getView().findViewById(R.id.receive_rate).setLayoutParams(new LinearLayout.LayoutParams(10, LinearLayout.LayoutParams.MATCH_PARENT));
                    }else{
                        helper.getView().findViewById(R.id.receive_rate).setLayoutParams(new LinearLayout.LayoutParams(screen_width + 10, LinearLayout.LayoutParams.MATCH_PARENT));
                    }
                }
                if ("产品目标".equals(aim_text.getText().toString())) {
                    ((TextView)helper.getView().findViewById(R.id.receive_rate_text)).setText(FuncUtils.formatMoney2(item.getTarget_project())  + "万");
                    ((TextView)helper.getView().findViewById(R.id.fact_rate_text)).setText(FuncUtils.formatMoney2(item.getSale_project()) + "万");
                    ((TextView)helper.getView().findViewById(R.id.complete_rate_text)).setText(FuncUtils.getPercentRate(item.getTarget_project(), item.getSale_project()) + "%");
                    helper.getView().findViewById(R.id.fact_rate).setLayoutParams(new LinearLayout.LayoutParams(screen_width*Integer.parseInt(FuncUtils.getPercentRateInt(item.getTarget_project(), item.getSale_project()))/100+10, LinearLayout.LayoutParams.MATCH_PARENT));
                    helper.getView().findViewById(R.id.complete_rate).setLayoutParams(new LinearLayout.LayoutParams(screen_width*Integer.parseInt(FuncUtils.getPercentRateInt(item.getTarget_project(), item.getSale_project()))/100+10, LinearLayout.LayoutParams.MATCH_PARENT));
                    if("0.0".equals(FuncUtils.formatMoney(item.getTarget_project()))){
                        helper.getView().findViewById(R.id.receive_rate).setLayoutParams(new LinearLayout.LayoutParams(10, LinearLayout.LayoutParams.MATCH_PARENT));
                    }else{
                        helper.getView().findViewById(R.id.receive_rate).setLayoutParams(new LinearLayout.LayoutParams(screen_width + 10, LinearLayout.LayoutParams.MATCH_PARENT));
                    }
                }
                if ("消耗目标".equals(aim_text.getText().toString())) {
                    ((TextView)helper.getView().findViewById(R.id.receive_rate_text)).setText(FuncUtils.formatMoney2(item.getTarget_consume()) + "万");
                    ((TextView)helper.getView().findViewById(R.id.fact_rate_text)).setText( FuncUtils.formatMoney2(item.getSale_consume()) + "万");
                    ((TextView)helper.getView().findViewById(R.id.complete_rate_text)).setText(FuncUtils.getPercentRate(item.getTarget_consume(), item.getSale_consume()) + "%");
                    helper.getView().findViewById(R.id.fact_rate).setLayoutParams(new LinearLayout.LayoutParams(screen_width*Integer.parseInt(FuncUtils.getPercentRateInt(item.getTarget_consume(), item.getSale_consume()))/100+10, LinearLayout.LayoutParams.MATCH_PARENT));
                    helper.getView().findViewById(R.id.complete_rate).setLayoutParams(new LinearLayout.LayoutParams(screen_width*Integer.parseInt(FuncUtils.getPercentRateInt(item.getTarget_consume(), item.getSale_consume()))/100+10, LinearLayout.LayoutParams.MATCH_PARENT));
                    if("0.0".equals(FuncUtils.formatMoney(item.getTarget_consume()))){
                        helper.getView().findViewById(R.id.receive_rate).setLayoutParams(new LinearLayout.LayoutParams(10, LinearLayout.LayoutParams.MATCH_PARENT));
                    }else{
                        helper.getView().findViewById(R.id.receive_rate).setLayoutParams(new LinearLayout.LayoutParams(screen_width + 10, LinearLayout.LayoutParams.MATCH_PARENT));
                    }
                }

                /**
                 * 点击查看顾问
                 */
                helper.getView().findViewById(R.id.paper_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getSubPersonList(account.getString("id"), account.getString("roleKey"));
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
         * 点击上月
         */
        pre_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                month = DateTimeUtils.getLastMonth(month);
                initData();
            }
        });

        /**
         * 点击下月
         */
        next_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                month = DateTimeUtils.getNextMonth(month);
                initData();
            }
        });

    }



    private void initData() {
        TargetEntity entity = new TargetEntity();
        entity.setMonth(month);
        entity.setType(TargetTypeEnum.getCodeByMsg(bundle.getString("target_title")));
        entity.setUid(bundle.getString("uids"));
        HttpClient.getTargetIndex(entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String body) {
                Log.i("getTargetIndexTAG", "返回数据:" + body);
                JSONObject object = JSON.parseObject(body);
                if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                    JSONArray arr = JSONArray.parseArray(object.getString("data"));
                    list = new ArrayList<TargetEntity>();
                    if (arr.size() > 0) {
                        TargetEntity item;
                        for (int i = 0; i < arr.size(); i++) {
                            JSONObject data = arr.getJSONObject(i);
                            item = new TargetEntity();
                            item.setAccount(data.getString("account"));
                            item.setTarget_consume(data.getString("target_consume"));
                            item.setTarget_health_beauty(data.getString("target_health_beauty"));
                            item.setTarget_medical_beauty(data.getString("target_medical_beauty"));
                            item.setTarget_project(data.getString("target_project"));
                            item.setSale_consume(data.getString("sale_consume"));
                            item.setSale_health_beauty(data.getString("sale_health_beauty"));
                            item.setSale_medical_beauty(data.getString("sale_medical_beauty"));
                            item.setSale_project(data.getString("sale_project"));
                            item.setAction_consume(data.getString("action_consume"));
                            item.setAction_health_beauty(data.getString("action_health_beauty"));
                            item.setAction_medical_beauty(data.getString("action_medical_beauty"));
                            item.setAction_project(data.getString("action_project"));
                            item.setMonth(month);
                            list.add(item);
                        }
                    }
                    adapter.clear();
                    adapter.addAll(list);
                    list_view.setAdapter(adapter);
                } else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                    UIHelper.showLogin(AreaManagerAimDetailActivity.this);
                }else {
                    Toast.makeText(AreaManagerAimDetailActivity.this, "获取失败",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                Log.i("getTargetIndexTAG", "失败返回数据:" + request.toString());
            }
        }, AreaManagerAimDetailActivity.this,true);
    }


    private void getSubPersonList(String user_id,final String rolekey){
        UserParam param= new UserParam();
        param.setUid(user_id);
        if(!StringUtils.isEmpty(param.getUid())){
            HttpClient.getSubPersonList(param, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("getSubPersonListTAG", "成功返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        List<UserParam> list = JSONArray.parseArray(object.getString("data"), UserParam.class);
                        if (list != null && list.size() > 0) {
                            String uids = "";
                            for (int i = 0; i < list.size(); i++) {
                                uids += list.get(i).getId() + ",";
                            }
                            bundle.putString("month", month);
                            bundle.putString("uids", uids.substring(0, uids.length() - 1));
                            if (RoleEnum.CONSULTANT.getCode().equals(rolekey)) {
                                UIHelper.showStaffAimDetail(AreaManagerAimDetailActivity.this, bundle);
                            } else if (RoleEnum.STOREMANAGER.getCode().equals(rolekey)) {
                                UIHelper.showAdviserAimDetail(AreaManagerAimDetailActivity.this, bundle);
                            } else if (RoleEnum.AREAMANAGER.getCode().equals(rolekey)) {
                                UIHelper.showStoreAimDetail(AreaManagerAimDetailActivity.this, bundle);
                            } else if (RoleEnum.BOSS.getCode().equals(rolekey)) {
                                UIHelper.showAreaManagerAimDetail(AreaManagerAimDetailActivity.this, bundle);
                            }
                        } else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                            UIHelper.showLogin(AreaManagerAimDetailActivity.this);
                        }else {
                            Toast.makeText(AreaManagerAimDetailActivity.this, "没有其他用户", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.i("getSubPersonListTAG", "失败返回数据:" + body.toString());
                        Toast.makeText(AreaManagerAimDetailActivity.this, "没有其他用户", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("getSubPersonListTAG", "失败返回数据:" + request.toString());
                    Toast.makeText(AreaManagerAimDetailActivity.this, "没有其他用户", Toast.LENGTH_LONG).show();
                }
            }, this,false);
        }else{
            Toast.makeText(AreaManagerAimDetailActivity.this, "缺少请求参数[uid]", Toast.LENGTH_LONG).show();
        }

    }
}
