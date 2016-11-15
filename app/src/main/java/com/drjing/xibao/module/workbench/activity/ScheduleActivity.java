package com.drjing.xibao.module.workbench.activity;

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
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.module.entity.ScheduleEntity;
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
 * 日程
 * Created by kristain on 15/12/30.
 */
public class ScheduleActivity extends SwipeBackActivity {

    /**
     * 后退按钮
     */
    private Button btnBack;

    /**
     * Title栏文案
     */
    private TextView textHeadTitle;

    List<ScheduleEntity> targetList =new ArrayList<ScheduleEntity>();
    QuickAdapter<ScheduleEntity> adapter;
    private ListView todolist;

    private TextView pre_day,next_day;

    private String day = DateTimeUtils.gainCurrentDate(DateTimeUtils.DF_YYYY_MM_DD);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getScheduleList(day);
    }

    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("日程");
        todolist = (ListView)findViewById(R.id.todolist);

        pre_day=(TextView)findViewById(R.id.pre_day);
        next_day=(TextView)findViewById(R.id.next_day);

        adapter = new QuickAdapter<ScheduleEntity>(this, R.layout.schedule_list_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, final ScheduleEntity entity) {
                helper.setText(R.id.date, (StringUtils.isEmpty(entity.getAlertDate()) ? "" : DateTimeUtils.formatDateTime(Long.parseLong(entity.getAlertDate()), DateTimeUtils.YYYY_MM_DD)))
                        .setText(R.id.shedule_type, entity.getCategoryName())
                        .setText(R.id.remark, entity.getContent());
            }
        };
        todolist.setAdapter(adapter);
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
         * 点击前一天
         */
        pre_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day =  DateTimeUtils.getLastDate(day);
                getScheduleList(day);
            }
        });

        /**
         * 点击下一天
         */
        next_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = DateTimeUtils.getNextDate(day);
                getScheduleList(day);
            }
        });
    }

    /**
     * 获取日程列表
     * @param day
     */
    private void getScheduleList(String day){
        ScheduleEntity entity = new ScheduleEntity();
        entity.setAlert_date(day);
        HttpClient.getScheduleList(entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String body) {
                Log.i("getScheduleListTAG", "返回数据:" + body);
                JSONObject object = JSON.parseObject(body);
                if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                    targetList = JSONArray.parseArray(object.getString("data"), ScheduleEntity.class);
                    adapter.clear();
                    adapter.addAll(targetList);
                    todolist.setAdapter(adapter);
                }else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                    UIHelper.showLogin(ScheduleActivity.this);
                } else {
                    Toast.makeText(ScheduleActivity.this, "获取失败",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                Log.i("getScheduleListTAG", "失败返回数据:" + request.toString());
            }
        }, ScheduleActivity.this);
    }
}
