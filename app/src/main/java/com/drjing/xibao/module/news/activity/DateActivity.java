package com.drjing.xibao.module.news.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.drjing.xibao.R;
import com.drjing.xibao.common.view.calendarview.CalendarDay;
import com.drjing.xibao.common.view.calendarview.MaterialCalendarView;
import com.drjing.xibao.common.view.calendarview.OnDateSelectedListener;
import com.drjing.xibao.common.view.calendarview.OnMonthChangedListener;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * 服务日志搜索选择日期页面
 * Created by kristain on 15/12/28.
 */
public class DateActivity extends SwipeBackActivity implements OnDateSelectedListener, OnMonthChangedListener {

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();

    /**
     * 日历控件
     */
    private MaterialCalendarView calendarView;

    /**
     * 后退按钮
     */
    private Button btnBack;

    /**
     * Title栏文案
     */
    private TextView textHeadTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicelog_search_date);
        initView();
        initEvent();
    }


    private void initView() {
        calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("选择服务时间");
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

        //Setup initial text


    }


    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected) {
        //getSelectedDatesString();
        Intent intent = getIntent();
        intent.putExtra("server_time", getSelectedDatesString());
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        //noinspection ConstantConditions
        Log.e("onMonthChanged:", FORMATTER.format(date.getDate()));
    }

    private String getSelectedDatesString() {
        CalendarDay date = calendarView.getSelectedDate();
        if (date == null) {
            return "No Selection";
        }
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        return  sdf.format(date.getDate());
    }
}
