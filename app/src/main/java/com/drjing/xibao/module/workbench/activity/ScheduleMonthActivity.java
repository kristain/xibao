package com.drjing.xibao.module.workbench.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.drjing.xibao.common.view.calendarview.CalendarDay;
import com.drjing.xibao.common.view.calendarview.MaterialCalendarView;
import com.drjing.xibao.common.view.calendarview.OnDateSelectedListener;
import com.drjing.xibao.common.view.calendarview.OnMonthChangedListener;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.module.entity.ScheduleEntity;
import com.drjing.xibao.module.performance.adapter.BaseAdapterHelper;
import com.drjing.xibao.module.performance.adapter.QuickAdapter;
import com.drjing.xibao.module.ui.UIHelper;
import com.kristain.common.utils.DateTimeUtils;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 月日程
 * Created by kristain on 15/12/30.
 */
public class ScheduleMonthActivity extends SwipeBackActivity implements OnDateSelectedListener, OnMonthChangedListener {

    /**
     * 后退按钮
     */
    private Button btnBack;

    /**
     * Title栏文案
     */
    private TextView textHeadTitle;

    /**
     * 日历控件
     */
    private MaterialCalendarView calendarView;

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();

    /**
     *
     */
    private ListView list_view;

    QuickAdapter<ScheduleEntity> adapter;

    private TextView btnRight;

    private boolean load_flag=false;


    List<ScheduleEntity> list = new ArrayList<ScheduleEntity>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicelog_search_date);
        initView();
        initEvent();

    }


    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("日程");
        calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        list_view = (ListView)findViewById(R.id.list_view);
        btnRight = (TextView)findViewById(R.id.btnRight);
        btnRight.setText("新增");
        btnRight.setVisibility(View.VISIBLE);

        adapter = new QuickAdapter<ScheduleEntity>(this, R.layout.schedule_monthlist_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, final ScheduleEntity entity) {
                helper.setText(R.id.schedule_name, entity.getCategoryName())
                        .setText(R.id.schedule_content, entity.getContent())
                        .setText(R.id.schedule_id,entity.getId()+"")
                        .setText(R.id.categoryId, entity.getCategoryId())
                        .setText(R.id.alertDate, (StringUtils.isEmpty(entity.getAlertDate())?"":DateTimeUtils.formatDateTime(Long.parseLong(entity.getAlertDate()),DateTimeUtils.DF_YYYY_MM_DD)))
                        .setText(R.id.remindPeriod, entity.getRemindPeriod())
                                .setText(R.id.remindBefore, entity.getRemindBefore());
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

        calendarView.setOnDateChangedListener(this);
        calendarView.setOnMonthChangedListener(this);

        /**
         * 点击新增
         */
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("id", "");
                bundle.putString("content", "");
                bundle.putString("categoryId", "");
                bundle.putString("alertDate", "");
                bundle.putString("remindPeriod", "");
                bundle.putString("remindBefore", "");
                UIHelper.showScheduleAdd(ScheduleMonthActivity.this, bundle);
            }
        });

        calendarView.setCurrentDate(DateTimeUtils.gainCurrentDate());

        /**
         *  点击详情
         */
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("id", ((TextView) view.findViewById(R.id.schedule_id)).getText().toString());
                bundle.putString("content", ((TextView) view.findViewById(R.id.schedule_content)).getText().toString());
                bundle.putString("categoryId", ((TextView) view.findViewById(R.id.schedule_name)).getText().toString());
                bundle.putString("alertDate", ((TextView) view.findViewById(R.id.alertDate)).getText().toString());
                bundle.putString("remindPeriod", ((TextView) view.findViewById(R.id.remindPeriod)).getText().toString());
                bundle.putString("remindBefore", ((TextView) view.findViewById(R.id.remindBefore)).getText().toString());
                Log.e("TAG1", ((TextView) view.findViewById(R.id.schedule_name)).getText().toString());
                load_flag = true;
                UIHelper.showScheduleAdd(ScheduleMonthActivity.this, bundle);
            }
        });



    }


    @Override
    protected void onResume() {
        super.onResume();
        if(load_flag){
            getScheduleList(getSelectedDatesString(),true);
        }
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected) {
        getScheduleList(getSelectedDatesString(), true);
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        Log.e("onMonthChanged:", FORMATTER.format(date.getDate()));
    }

    private String getSelectedDatesString() {
        CalendarDay date = calendarView.getSelectedDate();
        if (date == null) {
            return "No Selection";
        }
        return DateTimeUtils.formatDateTime(date.getDate(), DateTimeUtils.DF_YYYY_MM_DD);
    }

    /**
     * 获取日程列表
     * @param alert_date
     */
    private void getScheduleList(String alert_date, boolean load_flag){
        ScheduleEntity param= new ScheduleEntity();
        param.setAlert_date(alert_date);
        HttpClient.getScheduleList(param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String body) {
                Log.i("getScheduleListTAG", "成功返回数据:" + body);
                JSONObject object = JSON.parseObject(body);
                if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                    list = JSONArray.parseArray(object.getString("data"), ScheduleEntity.class);
                    adapter.clear();
                    adapter.addAll(list);
                    list_view.setAdapter(adapter);
                }else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                    UIHelper.showLogin(ScheduleMonthActivity.this);
                }else{
                    Log.i("getScheduleListTAG", "失败返回数据:" + body);
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(ScheduleMonthActivity.this, request.toString(), Toast.LENGTH_LONG).show();
                Log.i("getScheduleListTAG", "失败返回数据:" + request.toString());
            }
        }, ScheduleMonthActivity.this);
    }


    /**
     * 删除日程
     * @param param
     */
    private void deleteSchedule(final ScheduleEntity param){
        if(!StringUtils.isEmpty(param.getId()+"")){
            HttpClient.deleteSchedule(param, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("deleteScheduleTAG", "成功返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        list.remove(param);
                        Toast.makeText(ScheduleMonthActivity.this, "删除成功",
                                Toast.LENGTH_SHORT).show();
                    }else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(ScheduleMonthActivity.this);
                    } else{
                        Toast.makeText(ScheduleMonthActivity.this, "删除失败",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Toast.makeText(ScheduleMonthActivity.this, request.toString(), Toast.LENGTH_LONG).show();
                    Log.i("deleteScheduleTAG", "失败返回数据:" + request.toString());
                }
            }, ScheduleMonthActivity.this);
        }else{
            Toast.makeText(ScheduleMonthActivity.this, "缺少请求参数[id]",
                    Toast.LENGTH_SHORT).show();
        }


    }

}
