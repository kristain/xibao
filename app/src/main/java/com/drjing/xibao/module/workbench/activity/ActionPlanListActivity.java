package com.drjing.xibao.module.workbench.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.library.third.ormlite.dao.Dao;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.view.calendarview.CalendarDay;
import com.drjing.xibao.common.view.calendarview.CustomerDatePickerDialog;
import com.drjing.xibao.common.view.materialspinner.CalendarSpinner;
import com.drjing.xibao.common.view.materialspinner.NiceSpinner;
import com.drjing.xibao.common.view.materialwidget.PaperButton;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.config.AppConfig;
import com.drjing.xibao.module.entity.ActionPlanEntity;
import com.drjing.xibao.module.entity.CategroyEntity;
import com.drjing.xibao.module.entity.CategroyEnum;
import com.drjing.xibao.module.performance.fragment.CalendarDialogFragment;
import com.drjing.xibao.module.ui.UIHelper;
import com.drjing.xibao.provider.db.DatabaseHelper;
import com.drjing.xibao.provider.db.entity.User;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 添加行动计划表列表页面
 * Created by kristain on 15/12/30.
 */
public class ActionPlanListActivity extends SwipeBackActivity implements CalendarDialogFragment.SelectListener {

    /**
     * 后退按钮
     */
    private Button btnBack;

    /**
     * Title栏文案
     */
    private TextView textHeadTitle;

    private Bundle bundle;

    private CalendarSpinner select_date;

    private NiceSpinner select_cate;

    private EditText money;

    private PaperButton submit_button;

    private ImageView select_project;

    private TextView select_product_text;

    CalendarDay calendar = CalendarDay.today();

    /**
     * 项目列表
     */
    List<CategroyEntity> categroylist;

    ArrayList<String> categroyDataset = new ArrayList<String>();

    /**
     * 选择项目IDs
     */
    String projectIds;

    public final static int REQUESTXMCODE = 1;//选择项目


    private DatabaseHelper dbHelper;
    private Dao<User, String> userDao;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_plan_list);
        bundle = getIntent().getExtras();
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
        textHeadTitle.setText("添加行动计划表");
        select_date = (CalendarSpinner) findViewById(R.id.select_date);
        select_cate = (NiceSpinner) findViewById(R.id.select_cate);
        money = (EditText) findViewById(R.id.money);
        submit_button = (PaperButton) findViewById(R.id.submit_button);
        select_project = (ImageView) findViewById(R.id.select_project);
        select_product_text = (TextView) findViewById(R.id.select_product_text);

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
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(StringUtils.isEmpty(select_date.getText().toString()) && "选择到店时间".equals(select_date.getText().toString()) ){
                    Toast.makeText(ActionPlanListActivity.this, "请选择到店时间", Toast.LENGTH_LONG).show();
                    return;
                }
                if(StringUtils.isEmpty(money.getText().toString())){
                    Toast.makeText(ActionPlanListActivity.this, "请输入金额", Toast.LENGTH_LONG).show();
                    return;
                }
                if(StringUtils.isEmpty(select_product_text.getText().toString())){
                    Toast.makeText(ActionPlanListActivity.this, "请选择项目", Toast.LENGTH_LONG).show();
                    return;
                }
                doAddActionPlan();

            }
        });


        /**
         * 选择日期
         */
        select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarDialogFragment.newInstance().show(getSupportFragmentManager(), "calendar");
            }
        });

        /**
         * 点击选择项目
         */
        select_project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isEmpty(select_cate.getText().toString()) && "请选择指标".equals(select_cate.getText().toString())) {
                    Toast.makeText(ActionPlanListActivity.this, "请选择指标", Toast.LENGTH_LONG).show();
                    return;
                }
                UIHelper.showProjectList(ActionPlanListActivity.this, REQUESTXMCODE, getCategoryIdByName(categroylist, select_cate.getText().toString()), projectIds);

            }
        });

    }

    /**
     * 获取产品类型
     */
    private void initData() {
        CategroyEntity param = new CategroyEntity();
        param.setCatetype(CategroyEnum.PROJECT.code);
        HttpClient.getCateGoryList(param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String body) {
                Log.i("getCateGoryListTAG", "成功返回数据:" + body);
                JSONObject object = JSON.parseObject(body);
                if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                    categroylist = JSONArray.parseArray(object.getString("data"), CategroyEntity.class);
                    for (int i = 0; i < categroylist.size(); i++) {
                        categroyDataset.add(categroylist.get(i).getName());
                    }
                    select_cate.attachDataSource(categroyDataset);
                } else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                    UIHelper.showLogin(ActionPlanListActivity.this);
                } else {
                    Log.i("getCateGoryListTAG", "获取数据失败:" + body);
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                Log.i("getCateGoryListTAG", "失败返回数据:" + request.toString());
            }
        }, ActionPlanListActivity.this);
    }

    /**
     * 新增行动计划
     */
    private void doAddActionPlan() {
        ActionPlanEntity param = new ActionPlanEntity();
        param.setCustomerId(bundle.getString("customer_id"));
        param.setCategoryId(getCategoryIdByName(categroylist, select_cate.getText().toString()));
        param.setProjectids(projectIds);
        param.setUserId(user.getId());
        param.setArriveTime(select_date.getText().toString());
        param.setAmount(money.getText().toString());
        if(!StringUtils.isEmpty(param.getUserId())){
            HttpClient.addActionPlan(param, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("addActionPlanTAG", "成功返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        finish();
                    } else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(ActionPlanListActivity.this);
                    }else {
                        Log.i("addActionPlanTAG", "获取数据失败:" + body);
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("addActionPlanTAG", "失败返回数据:" + request.toString());
                }
            }, ActionPlanListActivity.this);
        }else{
            Toast.makeText(this,"缺少请求参数[uid]",Toast.LENGTH_LONG).show();
        }

    }


    /**
     * 根据分类名称获取分类ID
     *
     * @param list
     * @param categroryName
     * @return
     */
    private String getCategoryIdByName(List<CategroyEntity> list, String categroryName) {
        if (list == null || list.size() == 0) {
            return "";
        }

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName().equals(categroryName)) {
                return list.get(i).getId();
            }
        }
        return "";
    }


    /**
     * 显示年月选择对话框
     *
     * @param context
     * @param day
     * @param callback
     */
    public static void showDatePickerDialog(Context context, CalendarDay day,
                                            DatePickerDialog.OnDateSetListener callback) {
        if (day == null) {
            day = CalendarDay.today();
        }
        CustomerDatePickerDialog dialog = new CustomerDatePickerDialog(
                context, callback, day.getYear(), day.getMonth(), day.getDay()
        );

        DatePicker dp = dialog.getDatePicker();
        if (dp != null) {
            ((ViewGroup) ((ViewGroup) dp.getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
        }
        dialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case REQUESTXMCODE:
                //选择项目
                if (data != null) {
                    Bundle b = data.getExtras(); //data为B中回传的Intent
                    projectIds = b.getString("projectIds");
                    select_product_text.setText(b.getString("projectNames"));
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != dbHelper) dbHelper.releaseAll();
    }

    @Override
    public void onSelectComplete(String date)
    {
        select_date.setText(date);
    }

}
