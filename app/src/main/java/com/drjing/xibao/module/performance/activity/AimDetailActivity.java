package com.drjing.xibao.module.performance.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.library.third.ormlite.dao.Dao;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.utils.DisplayUtils;
import com.drjing.xibao.common.utils.FuncUtils;
import com.drjing.xibao.common.view.materialwidget.PaperButton;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.config.AppConfig;
import com.drjing.xibao.module.entity.RoleEnum;
import com.drjing.xibao.module.entity.TargetEntity;
import com.drjing.xibao.module.entity.TargetTypeEnum;
import com.drjing.xibao.module.entity.UserParam;
import com.drjing.xibao.module.ui.UIHelper;
import com.drjing.xibao.provider.db.DatabaseHelper;
import com.drjing.xibao.provider.db.entity.User;
import com.kristain.common.utils.DateTimeUtils;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * 目标看板
 * Created by kristain on 16/1/3.
 */
public class AimDetailActivity extends SwipeBackActivity {

    /**
     * 后退返回按钮
     */
    private Button btnBack;
    /**
     * 标题栏文案
     */
    private TextView textHeadTitle;

    private Bundle bundle;

    private TextView aim_text,month_text,counselor_name,store_name;

    private RadioButton pre_month,next_month;
    /**
     * 查询目标月份
     */
    private String month =DateTimeUtils.gainCurrentMonth();
    private DatabaseHelper dbHelper;
    private Dao<User, String> userDao;
    private User user;

    private TextView complete_rate_text,complete_rate,
            receive_rate_text,receive_rate,fact_rate_text,fact_rate;
    private PaperButton paper_button;

    private int screen_width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aimdetail);
        bundle=getIntent().getExtras();
        dbHelper = DatabaseHelper.gainInstance(this, AppConfig.DB_NAME, AppConfig.DB_VERSION);
        try {
            userDao = (Dao<User, String>)dbHelper.getDao(User.class);
            List<User> users = userDao.queryBuilder().query();
            if(users==null || users.size()==0 || StringUtils.isEmpty(users.get(0).getId())){
                UIHelper.showLogin(this);
                return;
            }
            user = users.get(0);
        } catch (SQLException e) {
            e.printStackTrace();
            UIHelper.showLogin(this);
        }
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
        month_text= (TextView)findViewById(R.id.month_text);
        counselor_name= (TextView)findViewById(R.id.counselor_name);
        store_name= (TextView)findViewById(R.id.store_name);
        pre_month= (RadioButton)findViewById(R.id.pre_month);
        next_month= (RadioButton)findViewById(R.id.next_month);

        textHeadTitle.setText("目标看板");
        aim_text.setText(bundle.getString("target_title"));
        month_text.setText(DateTimeUtils.gainCurrentMonth());
        counselor_name.setText(RoleEnum.getMsgByCode(user.getRoleKey())+":"+user.getUsername());
        store_name.setText(user.getStore_name());

        complete_rate_text = (TextView)findViewById(R.id.complete_rate_text);
        complete_rate = (TextView)findViewById(R.id.complete_rate);
        receive_rate_text = (TextView)findViewById(R.id.receive_rate_text);
        receive_rate = (TextView)findViewById(R.id.receive_rate);
        fact_rate_text = (TextView)findViewById(R.id.fact_rate_text);
        fact_rate = (TextView)findViewById(R.id.fact_rate);
        paper_button = (PaperButton)findViewById(R.id.paper_button);
        paper_button.setText("查看下级");
        if(RoleEnum.STAFF.getCode().equals(user.getRoleKey())){
            paper_button.setVisibility(View.GONE);
        }
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
                month_text.setText(month);
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
                month_text.setText(month);
                initData();
            }
        });
        /**
         * 点击其他查看美容师
         */
        paper_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(RoleEnum.CONSULTANT.getCode().equals(user.getRoleKey())){
                    getSubStoreStaffList();
                }else{
                    getSubPersonList();
                }
            }
        });
    }



    private void initData() {
        TargetEntity entity = new TargetEntity();
        entity.setMonth(month);
        entity.setType(TargetTypeEnum.getCodeByMsg(bundle.getString("target_title")));
        entity.setUid(user.getId());
        HttpClient.getTargetIndex(entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String body) {
                Log.i("getTargetIndexTAG", "返回数据:" + body);
                JSONObject object = JSON.parseObject(body);
                if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                    JSONArray arr = JSONArray.parseArray(object.getString("data"));
                    if (arr.size() > 0) {
                        JSONObject data = arr.getJSONObject(0);
                        if ("生美预收".equals(bundle.getString("target_title"))) {
                            receive_rate_text.setText(FuncUtils.formatMoney2(data.getLongValue("target_health_beauty") + "") + "w");
                            fact_rate_text.setText(FuncUtils.formatMoney2(data.getLongValue("sale_health_beauty") + "") + "w");
                            complete_rate_text.setText(FuncUtils.getPercentRate(data.getLongValue("target_health_beauty") + "", data.getLongValue("sale_health_beauty") + "") + "%");
                            if ("0.0".equals(FuncUtils.formatMoney(data.getLongValue("target_health_beauty") + ""))) {
                                receive_rate.setLayoutParams(new LinearLayout.LayoutParams(10, LinearLayout.LayoutParams.MATCH_PARENT));
                            } else {
                                receive_rate.setLayoutParams(new LinearLayout.LayoutParams(screen_width + 10, LinearLayout.LayoutParams.MATCH_PARENT));
                            }
                            fact_rate.setLayoutParams(new LinearLayout.LayoutParams(screen_width * Integer.parseInt(FuncUtils.getPercentRateInt(data.getLongValue("target_health_beauty") + "", data.getLongValue("sale_health_beauty") + "")) / 100 + 10, LinearLayout.LayoutParams.MATCH_PARENT));
                            complete_rate.setLayoutParams(new LinearLayout.LayoutParams(screen_width * Integer.parseInt(FuncUtils.getPercentRateInt(data.getLongValue("target_health_beauty") + "", data.getLongValue("sale_health_beauty") + "")) / 100 + 10, LinearLayout.LayoutParams.MATCH_PARENT));
                        }
                        if ("医美预收".equals(bundle.getString("target_title"))) {
                            receive_rate_text.setText(FuncUtils.formatMoney2(data.getLongValue("target_medical_beauty") + "") + "w");
                            fact_rate_text.setText(FuncUtils.formatMoney2(data.getLongValue("sale_medical_beauty") + "") + "w");
                            complete_rate_text.setText(FuncUtils.getPercentRate(data.getString("target_medical_beauty"), data.getString("sale_medical_beauty")) + "%");
                            fact_rate.setLayoutParams(new LinearLayout.LayoutParams(screen_width * Integer.parseInt(FuncUtils.getPercentRateInt(data.getLongValue("target_medical_beauty") + "", data.getLongValue("sale_medical_beauty") + "")) / 100 + 10, LinearLayout.LayoutParams.MATCH_PARENT));
                            complete_rate.setLayoutParams(new LinearLayout.LayoutParams(screen_width * Integer.parseInt(FuncUtils.getPercentRateInt(data.getLongValue("target_medical_beauty") + "", data.getLongValue("sale_medical_beauty") + "")) / 100 + 10, LinearLayout.LayoutParams.MATCH_PARENT));
                            if ("0.0".equals(FuncUtils.formatMoney(data.getLongValue("target_medical_beauty") + ""))) {
                                receive_rate.setLayoutParams(new LinearLayout.LayoutParams(10, LinearLayout.LayoutParams.MATCH_PARENT));
                            } else {
                                receive_rate.setLayoutParams(new LinearLayout.LayoutParams(screen_width + 10, LinearLayout.LayoutParams.MATCH_PARENT));
                            }
                        }
                        if ("产品目标".equals(bundle.getString("target_title"))) {
                            receive_rate_text.setText(FuncUtils.formatMoney2(data.getLongValue("target_project") + "") + "w");
                            fact_rate_text.setText(FuncUtils.formatMoney2(data.getLongValue("sale_project") + "") + "w");
                            complete_rate_text.setText(FuncUtils.getPercentRate(data.getString("target_project"), data.getString("sale_project")) + "%");
                            fact_rate.setLayoutParams(new LinearLayout.LayoutParams(screen_width * Integer.parseInt(FuncUtils.getPercentRateInt(data.getLongValue("target_project") + "", data.getLongValue("sale_project") + "")) / 100 + 10, LinearLayout.LayoutParams.MATCH_PARENT));
                            complete_rate.setLayoutParams(new LinearLayout.LayoutParams(screen_width * Integer.parseInt(FuncUtils.getPercentRateInt(data.getLongValue("target_project") + "", data.getLongValue("sale_project") + "")) / 100 + 10, LinearLayout.LayoutParams.MATCH_PARENT));
                            if ("0.0".equals(FuncUtils.formatMoney(data.getLongValue("target_project") + ""))) {
                                receive_rate.setLayoutParams(new LinearLayout.LayoutParams(10, LinearLayout.LayoutParams.MATCH_PARENT));
                            } else {
                                receive_rate.setLayoutParams(new LinearLayout.LayoutParams(screen_width + 10, LinearLayout.LayoutParams.MATCH_PARENT));
                            }
                        }
                        if ("消耗目标".equals(bundle.getString("target_title"))) {
                            receive_rate_text.setText(FuncUtils.formatMoney2(data.getLongValue("target_consume") + "") + "w");
                            fact_rate_text.setText(FuncUtils.formatMoney2(data.getLongValue("sale_consume") + "") + "w");
                            complete_rate_text.setText(FuncUtils.getPercentRate(data.getString("target_consume"), data.getString("sale_consume")) + "%");
                            fact_rate.setLayoutParams(new LinearLayout.LayoutParams(screen_width * Integer.parseInt(FuncUtils.getPercentRateInt(data.getLongValue("target_consume") + "", data.getLongValue("sale_consume") + "")) / 100 + 10, LinearLayout.LayoutParams.MATCH_PARENT));
                            complete_rate.setLayoutParams(new LinearLayout.LayoutParams(screen_width * Integer.parseInt(FuncUtils.getPercentRateInt(data.getLongValue("target_consume") + "", data.getLongValue("sale_consume") + "")) / 100 + 10, LinearLayout.LayoutParams.MATCH_PARENT));
                            if ("0.0".equals(FuncUtils.formatMoney(data.getLongValue("target_consume") + ""))) {
                                receive_rate.setLayoutParams(new LinearLayout.LayoutParams(10, LinearLayout.LayoutParams.MATCH_PARENT));
                            } else {
                                receive_rate.setLayoutParams(new LinearLayout.LayoutParams(screen_width + 10, LinearLayout.LayoutParams.MATCH_PARENT));
                            }
                        }
                    }
                } else if (HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                    UIHelper.showLogin(AimDetailActivity.this);
                } else {
                    Toast.makeText(AimDetailActivity.this, "获取失败",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                Log.i("getTargetIndexTAG", "失败返回数据:" + request.toString());
            }
        }, AimDetailActivity.this, true);
    }


    /**
     * 获取门店所有美容师列表
     */
    private void getSubStoreStaffList(){
        UserParam param= new UserParam();
        param.setUid(user.getId());
        param.setStore_id(user.getStore_id());
        if(!StringUtils.isEmpty(param.getStore_id())){
            HttpClient.getStoreStaffList(param, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("getStoreStaffListTAG", "成功返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        List<UserParam> list = JSONArray.parseArray(object.getString("data"), UserParam.class);
                        if(list!=null && list.size()>0){
                            String uids="";
                            for (int i=0;i<list.size();i++){
                                uids+=list.get(i).getId()+",";
                            }
                            bundle.putString("month",month);
                            bundle.putString("uids",uids.substring(0,uids.length()-1));
                            if(RoleEnum.CONSULTANT.getCode().equals(user.getRoleKey())){
                                UIHelper.showStaffAimDetail(AimDetailActivity.this,bundle);
                            }else if(RoleEnum.STOREMANAGER.getCode().equals(user.getRoleKey())){
                                UIHelper.showAdviserAimDetail(AimDetailActivity.this,bundle);
                            }else if(RoleEnum.AREAMANAGER.getCode().equals(user.getRoleKey())){
                                UIHelper.showStoreAimDetail(AimDetailActivity.this, bundle);
                            }else if(RoleEnum.BOSS.getCode().equals(user.getRoleKey())){
                                UIHelper.showAreaManagerAimDetail(AimDetailActivity.this,bundle);
                            }
                        }else{
                            Toast.makeText(AimDetailActivity.this, "没有其他用户", Toast.LENGTH_LONG).show();
                        }
                    } else if (HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(AimDetailActivity.this);
                    } else {
                        Log.i("getStoreStaffListTAG", "失败返回数据:" + body.toString());
                        Toast.makeText(AimDetailActivity.this, "获取美容师列表失败", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("getStoreStaffListTAG", "失败返回数据:" + request.toString());
                    Toast.makeText(AimDetailActivity.this, "获取美容师列表失败", Toast.LENGTH_LONG).show();
                }
            }, this);
        }else{
            Toast.makeText(AimDetailActivity.this, "缺少请求参数[store_id]", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 获取下一级用户
     */
    private void getSubPersonList(){
        UserParam param= new UserParam();
        param.setUid(user.getId());
        if(!StringUtils.isEmpty(param.getUid())){
            HttpClient.getSubPersonList(param, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("getSubPersonListTAG", "成功返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        List<UserParam> list = JSONArray.parseArray(object.getString("data"), UserParam.class);
                        if(list!=null && list.size()>0){
                            String uids="";
                            for (int i=0;i<list.size();i++){
                                uids+=list.get(i).getId()+",";
                            }
                            bundle.putString("month",month);
                            bundle.putString("uids",uids.substring(0,uids.length()-1));
                            if(RoleEnum.CONSULTANT.getCode().equals(user.getRoleKey())){
                                UIHelper.showStaffAimDetail(AimDetailActivity.this,bundle);
                            }else if(RoleEnum.STOREMANAGER.getCode().equals(user.getRoleKey())){
                                UIHelper.showAdviserAimDetail(AimDetailActivity.this,bundle);
                            }else if(RoleEnum.AREAMANAGER.getCode().equals(user.getRoleKey())){
                                UIHelper.showStoreAimDetail(AimDetailActivity.this, bundle);
                            }else if(RoleEnum.BOSS.getCode().equals(user.getRoleKey())){
                                UIHelper.showAreaManagerAimDetail(AimDetailActivity.this,bundle);
                            }
                        }else{
                            Toast.makeText(AimDetailActivity.this, "没有其他用户", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.i("getSubPersonListTAG", "失败返回数据:" + body.toString());
                        Toast.makeText(AimDetailActivity.this, "没有其他用户", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("getSubPersonListTAG", "失败返回数据:" + request.toString());
                    Toast.makeText(AimDetailActivity.this, "没有其他用户", Toast.LENGTH_LONG).show();
                }
            }, this,false);
        }else{
            Toast.makeText(AimDetailActivity.this, "缺少请求参数[uid]", Toast.LENGTH_LONG).show();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != dbHelper) dbHelper.releaseAll();
    }
}
