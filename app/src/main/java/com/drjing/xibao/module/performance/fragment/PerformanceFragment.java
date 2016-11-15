package com.drjing.xibao.module.performance.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.library.third.ormlite.dao.Dao;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.utils.FuncUtils;
import com.drjing.xibao.common.view.circularprogressbar.CompleteRateCircleBar;
import com.drjing.xibao.config.AppConfig;
import com.drjing.xibao.module.entity.TargetEntity;
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
 * 业绩Fragment
 * Created by kristain on 15/12/21.
 */
public class PerformanceFragment extends Fragment {

    private Activity context;
    private View root;
    private TextView textHeadTitle;
    private CompleteRateCircleBar receive_circle,sale_circle,product_circle,consume_circle;
    private RelativeLayout mysalve_layout,wallet_layout,aim_layout,rank_layout;

    private LinearLayout receive_pre_layout,sale_pre_layout,product_pre_layout,consume_pre_layout;

    private TextView receive_pre,receive_get,sale_pre,sale_get,product_pre,product_get,consume_pre,consume_get;
            //product_ratio,consume_ratio,receive_ratio,sale_ratio;
    private Bundle bundle = new Bundle();

    private DatabaseHelper dbHelper;
    private Dao<User, String> userDao;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return root = inflater.inflate(R.layout.fragment_performance, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        dbHelper = DatabaseHelper.gainInstance(context, AppConfig.DB_NAME, AppConfig.DB_VERSION);
        try {
            userDao = (Dao<User, String>)dbHelper.getDao(User.class);
            List<User> users = userDao.queryBuilder().query();
            if(users==null || users.size()==0 || StringUtils.isEmpty(users.get(0).getId())){
                UIHelper.showLogin(context);
                return;
            }
            user = users.get(0);
        } catch (SQLException e) {
            e.printStackTrace();
            UIHelper.showLogin(context);
        }
        initView();
        initEvent();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    void initView() {
        textHeadTitle = (TextView) root.findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("业绩");
        receive_circle = (CompleteRateCircleBar) root.findViewById(R.id.receive_circle);
        sale_circle = (CompleteRateCircleBar) root.findViewById(R.id.sale_circle);
        product_circle = (CompleteRateCircleBar) root.findViewById(R.id.product_circle);
        consume_circle = (CompleteRateCircleBar) root.findViewById(R.id.consume_circle);
        mysalve_layout= (RelativeLayout) root.findViewById(R.id.mysalve_layout);
        wallet_layout= (RelativeLayout) root.findViewById(R.id.wallet_layout);
        aim_layout= (RelativeLayout) root.findViewById(R.id.aim_layout);
        rank_layout= (RelativeLayout) root.findViewById(R.id.rank_layout);
        receive_pre_layout= (LinearLayout) root.findViewById(R.id.receive_pre_layout);
        sale_pre_layout= (LinearLayout) root.findViewById(R.id.sale_pre_layout);
        product_pre_layout= (LinearLayout) root.findViewById(R.id.product_pre_layout);
        consume_pre_layout= (LinearLayout) root.findViewById(R.id.consume_pre_layout);
        receive_pre = (TextView)root.findViewById(R.id.receive_pre);
        receive_get = (TextView)root.findViewById(R.id.receive_get);
        product_pre = (TextView)root.findViewById(R.id.product_pre);
        product_get = (TextView)root.findViewById(R.id.product_get);
        sale_pre = (TextView)root.findViewById(R.id.sale_pre);
        sale_get = (TextView)root.findViewById(R.id.sale_get);
        consume_pre = (TextView)root.findViewById(R.id.consume_pre);
        consume_get = (TextView)root.findViewById(R.id.consume_get);
        //product_ratio = (TextView)root.findViewById(R.id.product_ratio);
        //consume_ratio = (TextView)root.findViewById(R.id.consume_ratio);
        //receive_ratio = (TextView)root.findViewById(R.id.receive_ratio);
        //sale_ratio = (TextView)root.findViewById(R.id.sale_ratio);
    }

    private void initData() {
        TargetEntity entity = new TargetEntity();
        entity.setMonth(DateTimeUtils.gainCurrentMonth());
        entity.setUid(user.getId());
        entity.setType("");
        HttpClient.getTargetIndex(entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String body) {
                Log.i("getTargetIndexTAG", "返回数据:" + body);
                JSONObject object = JSON.parseObject(body);
                if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                    JSONArray arr = JSONArray.parseArray(object.getString("data"));
                    if(arr.size()>0){
                        JSONObject data = arr.getJSONObject(0);
                        //生美计划目标
                        receive_get.setText(FuncUtils.formatMoney4(data.getLongValue("action_health_beauty") + ""));
                        //生美实际分配
                        receive_pre.setText(FuncUtils.formatMoney4(data.getLongValue("target_health_beauty") + ""));
                        //医美计划目标
                        sale_get.setText(FuncUtils.formatMoney4(data.getLongValue("action_medical_beauty") + ""));
                        //医美实际分配
                        sale_pre.setText(FuncUtils.formatMoney4(data.getLongValue("target_medical_beauty") + ""));
                        //产品计划目标
                        product_get.setText(FuncUtils.formatMoney4(data.getLongValue("action_project") + ""));
                        //产品实际分配
                        product_pre.setText(FuncUtils.formatMoney4(data.getLongValue("target_project") + ""));
                        //消耗计划目标
                        consume_get.setText(FuncUtils.formatMoney4(data.getLongValue("action_consume") + ""));
                        //消耗实际分配
                        consume_pre.setText(FuncUtils.formatMoney4(data.getLongValue("target_consume")+""));

                        float receiverate = FuncUtils.getCircleRate(data.getLongValue("target_health_beauty")+"", data.getLongValue("sale_health_beauty")+"");
                        float salerate = FuncUtils.getCircleRate(data.getLongValue("target_medical_beauty")+"", data.getLongValue("sale_medical_beauty")+"");
                        float productrate = FuncUtils.getCircleRate(data.getLongValue("target_project")+"", data.getLongValue("sale_project")+"");
                        float consumerate = FuncUtils.getCircleRate(data.getLongValue("target_consume")+"", data.getLongValue("sale_consume")+"");
                       // receive_circle.setSweepAngle(receiverate * 360 / 100);
                        receive_circle.setSweepAngle(Math.round(receiverate * 360 / 100));
                        receive_circle.setText(Math.round(receiverate) + "");
                        sale_circle.setSweepAngle(Math.round(salerate * 360 / 100));
                        sale_circle.setText(Math.round(salerate) + "");
                        product_circle.setSweepAngle(Math.round(productrate * 360 / 100));
                        product_circle.setText(Math.round(productrate) + "");
                        consume_circle.setSweepAngle(Math.round(consumerate * 360 / 100));
                        consume_circle.setText(Math.round(consumerate) + "");
                        //product_ratio.setText("完成率:"+ Math.round(productrate)+"%");
                        //sale_ratio.setText("完成率:"+ Math.round(salerate)+"%");
                        //consume_ratio.setText("完成率:"+ Math.round(consumerate)+"%");
                        //receive_ratio.setText("完成率:"+ Math.round(receiverate)+"%");
                    }
                } else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                    UIHelper.showLogin(getActivity());
                }else {
                    Toast.makeText(getActivity(), "获取失败",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                Log.i("getTargetIndexTAG", "失败返回数据:" + request.toString());
            }
        }, getActivity(),false);
    }

    private void initEvent(){
        //设定目标
        aim_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("role_key",user.getRoleKey());
                bundle.putString("accountname",user.getAccountname());
                bundle.putString("id",user.getId());
                UIHelper.showAimSetting(getActivity(),bundle);
            }
        });
        //钱包
        wallet_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showWallet(getActivity());
            }
        });
        //我的历史工资
        mysalve_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showMySalaryList(getActivity());
            }
        });
        //静博士英雄榜
        rank_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showRank(getActivity());
            }
        });
        //生美预收目标看板
        receive_pre_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("target_title","生美预收");
                UIHelper.showAimDetail(getActivity(),bundle);
            }
        });
        //医美预收目标看板
        sale_pre_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("target_title","医美预收");
                UIHelper.showAimDetail(getActivity(),bundle);
            }
        });
        //产品目标看板
        product_pre_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("target_title","产品目标");
                UIHelper.showAimDetail(getActivity(),bundle);
            }
        });
        //消耗目标看板
        consume_pre_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("target_title", "消耗目标");
                UIHelper.showAimDetail(getActivity(), bundle);
            }
        });
        /**
         * 生美预售点击
         */
        receive_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("target_title","生美预收");
                UIHelper.showAimDetail(getActivity(),bundle);
            }
        });

        sale_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("target_title","医美预收");
                UIHelper.showAimDetail(getActivity(),bundle);
            }
        });
        product_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("target_title","产品目标");
                UIHelper.showAimDetail(getActivity(),bundle);
            }
        });
        consume_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("target_title", "消耗目标");
                UIHelper.showAimDetail(getActivity(), bundle);
            }
        });
    }



}
