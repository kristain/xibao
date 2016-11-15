package com.drjing.xibao.module.workbench.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.library.third.ormlite.dao.Dao;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.view.materialspinner.CalendarSpinner;
import com.drjing.xibao.common.view.materialspinner.NiceSpinner;
import com.drjing.xibao.common.view.materialwidget.PaperButton;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.config.AppConfig;
import com.drjing.xibao.module.entity.CategroyEntity;
import com.drjing.xibao.module.entity.CategroyEnum;
import com.drjing.xibao.module.entity.RoleEnum;
import com.drjing.xibao.module.entity.ScheduleEntity;
import com.drjing.xibao.module.performance.fragment.CalendarDialogFragment;
import com.drjing.xibao.module.ui.UIHelper;
import com.drjing.xibao.provider.db.DatabaseHelper;
import com.drjing.xibao.provider.db.entity.User;
import com.kristain.common.utils.DateTimeUtils;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 添加日程
 * Created by kristain on 15/12/30.
 */
public class ScheduleAddActivity extends SwipeBackActivity implements CalendarDialogFragment.SelectListener {

    /**
     * 后退按钮
     */
    private Button btnBack;

    /**
     * Title栏文案
     */
    private TextView textHeadTitle;

    private NiceSpinner select_schedule,notice_cycle,notice_day;

    private CalendarSpinner select_date;

    private TextView add_customer;

    private PaperButton submit_btn;

    private EditText content;

    private DatabaseHelper dbHelper;
    private Dao<User, String> userDao;
    private User user;

    ArrayList<String> perioddataset = new ArrayList<>(Arrays.asList("一天", "一周", "一月", "一年"));
    ArrayList<String> beforedataset = new ArrayList<>(Arrays.asList("1天", "2天", "3天", "4天","5天", "6天", "7天", "8天", "9天","10天", "11天", "12天", "13天", "14天", "15天"));
    private CategroyEntity param;
    List<CategroyEntity> list;
    ArrayList<String> dataset = new ArrayList<String>();

    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_add);
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
        initView();
        initEvent();
        initData();
    }


    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("添加一个日程");

        select_schedule=(NiceSpinner)findViewById(R.id.select_schedule);
        notice_cycle=(NiceSpinner)findViewById(R.id.notice_cycle);
        notice_day=(NiceSpinner)findViewById(R.id.notice_day);
        select_date = (CalendarSpinner)findViewById(R.id.select_date);
        add_customer = (TextView)findViewById(R.id.add_customer);
        submit_btn = (PaperButton)findViewById(R.id.submit_btn);

        content = (EditText)findViewById(R.id.content);

        notice_cycle.attachDataSource(perioddataset);
        notice_day.attachDataSource(beforedataset);
        select_date.setText(DateTimeUtils.gainCurrentDate(DateTimeUtils.DF_YYYY_MM_DD));
        //初始化数据
        if(!StringUtils.isEmpty(bundle.getString("id"))){
            content.setText(bundle.getString("content"));
            select_date.setText(bundle.getString("alertDate"));
            notice_cycle.setText(getRemind_periodById(bundle.getString("remindPeriod")));
            notice_day.setText(bundle.getString("remindBefore")+"天");
        }else{
            //由特殊日期跳转过来
            content.setText(bundle.getString("content"));
            select_date.setText(bundle.getString("alertDate"));
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
         * 点击提交
         */
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validParam()) {
                    submitSchedule();
                }
            }
        });

        /**
         * 点击添加一个客户
         */
        add_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarDialogFragment.newInstance().show(getSupportFragmentManager(), "calendar");
            }
        });
    }

    private void initData(){
        param = new CategroyEntity();
        param.setCatetype(CategroyEnum.SCHEDULE.code);
        HttpClient.getCateGoryList(param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String body) {
                Log.i("getCateGoryListTAG", "成功返回数据:" + body);
                JSONObject object = JSON.parseObject(body);
                if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                    list = JSONArray.parseArray(object.getString("data"), CategroyEntity.class);
                    for (int i = 0; i < list.size(); i++) {
                        if(user.getRoleKey().equals(RoleEnum.BOSS.code))
                        {
                            if(!list.get(i).getName().equals("个人提醒"))
                            {
                                dataset.add(list.get(i).getName());
                            }
                        }
                        else
                        {
                            dataset.add(list.get(i).getName());
                        }
                    }
                    select_schedule.attachDataSource(dataset);

                    //初始化数据
                    if(!StringUtils.isEmpty(bundle.getString("id"))){
                        select_schedule.setText(bundle.getString("categoryId"));
                    }
                    if("特殊日期".equals(bundle.getString("select_schedule"))){
                        select_schedule.setText("特殊日期");
                    }
                }else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                    UIHelper.showLogin(ScheduleAddActivity.this);
                } else {
                    Log.i("getCateGoryListTAG", "获取数据失败:" + body);
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                Log.i("getCateGoryListTAG", "失败返回数据:" + request.toString());
            }
        }, ScheduleAddActivity.this);
    }


    /**
     * 验证提交参数
     * @return
     */
    private boolean validParam(){
        if(StringUtils.isEmpty(select_schedule.getText().toString())){
            Toast.makeText(this,"请选择日程类型",Toast.LENGTH_LONG).show();
            return false;
        }
        if(StringUtils.isEmpty(notice_cycle.getText().toString())){
            Toast.makeText(this,"请选择提醒周期",Toast.LENGTH_LONG).show();
            return false;
        }
        if(StringUtils.isEmpty(notice_day.getText().toString())){
            Toast.makeText(this,"请选择提前几天提醒",Toast.LENGTH_LONG).show();
            return false;
        }
        if(StringUtils.isEmpty(content.getText().toString())){
            Toast.makeText(this,"请输入提醒内容",Toast.LENGTH_LONG).show();
            return false;
        }
        if(StringUtils.isEmpty(select_date.getText().toString()) || "请选择".equals(select_date.getText().toString())){
            Toast.makeText(this,"请选择日期",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    /**
     * 添加日程
     */
    private void submitSchedule(){
        ScheduleEntity param = new ScheduleEntity();
        param.setCategoryId(getCategoryIdByName(list, select_schedule.getText().toString()));
        param.setContent(content.getText().toString());
        param.setRemind_before(getRemind_before(notice_day.getText().toString()));
        param.setRemind_period(getRemind_period(notice_cycle.getText().toString()));
        param.setAlert_date(select_date.getText().toString());
        if(StringUtils.isEmpty(bundle.getString("id"))){
            HttpClient.AddSchedule(param, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("AddScheduleTAG", "成功返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        finish();
                    } else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(ScheduleAddActivity.this);
                    }else {
                        Log.i("AddScheduleTAG", "新增钱包失败:" + body);
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("AddScheduleTAG", "失败返回数据:" + request.toString());
                }
            }, ScheduleAddActivity.this);
        }else{
            if(!StringUtils.isEmpty(bundle.getString("id"))){
                param.setId(Integer.parseInt(bundle.getString("id")));
                HttpClient.ModifySchedule(param, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String body) {
                        Log.i("ModifyScheduleTAG", "成功返回数据:" + body);
                        JSONObject object = JSON.parseObject(body);
                        if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                            finish();
                        } else if (HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                            UIHelper.showLogin(ScheduleAddActivity.this);
                        } else {
                            Log.i("ModifyScheduleTAG", "新增钱包失败:" + body);
                        }
                    }

                    @Override
                    public void onFailure(Request request, IOException e) {
                        Log.i("ModifyScheduleTAG", "失败返回数据:" + request.toString());
                    }
                }, ScheduleAddActivity.this);
            }else{
                Toast.makeText(this,"缺少请求参数[id]",Toast.LENGTH_LONG).show();
            }

        }
    }



    /**
     * 根据分类名称获取分类ID
     * @param list
     * @param categroryName
     * @return
     */
    private String getCategoryIdByName(List<CategroyEntity> list,String categroryName){
        if(list==null || list.size()==0){
            return "";
        }
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).getName().equals(categroryName)){
                return list.get(i).getId();
            }
        }
        return "";
    }




    private String getRemind_before(String text){
        return text.replace("天","");
    }

    private String getRemind_period(String text){
        if("一天".equals(text)){
          return "1";
        }else if("一周".equals(text)){
            return "7";
        }else if("一月".equals(text)){
            return "30";
        }else if("一年".equals(text)){
            return "365";
        }
        return "1";
    }

    private String getRemind_periodById(String id){
        if("1".equals(id)){
            return "一天";
        }else if("7".equals(id)){
            return "一周";
        }else if("30".equals(id)){
            return "一月";
        }else if("365".equals(id)){
            return "一年";
        }
        return "一天";
    }


    @Override
    public void onSelectComplete(String date)
    {
        select_date.setText(date);
    }
}
