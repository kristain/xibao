package com.drjing.xibao.module.workbench.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.drjing.xibao.R;
import com.drjing.xibao.common.view.calendarview.CalendarDay;
import com.drjing.xibao.common.view.calendarview.CustomerDatePickerDialog;
import com.drjing.xibao.common.view.materialspinner.NiceSpinner;
import com.drjing.xibao.common.view.materialwidget.PaperButton;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.module.entity.OrderEntity;
import com.drjing.xibao.module.entity.OrderStatusEnum;
import com.drjing.xibao.module.entity.TargetEntity;
import com.kristain.common.utils.DateTimeUtils;
import com.kristain.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 订单筛选
 * Created by kristain on 15/12/21.
 */
public class OrderSearchActivity extends SwipeBackActivity {


    /**
     * 后退返回按钮
     */
    private Button btnBack;
    /**
     * 标题栏文案
     */
    private TextView textHeadTitle;

    private EditText order_no,account_name,mobile;

    private RadioGroup is_member;
    private NiceSpinner select_servicestatus;

    private TextView service_startdate,service_enddate,order_startdate,order_enddate;

    private PaperButton submit_btn;
    ArrayList<String> statusDataset = new ArrayList<>(Arrays.asList("取消订单审核中", "取消订单", "未支付", "待服务", "开始服务", "服务完成", "待评价"));

    CalendarDay calendar = CalendarDay.today();
    private OrderEntity param ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_search);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        btnBack = (Button)findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("订单筛选");

        order_no=(EditText)findViewById(R.id.order_no);
        account_name=(EditText)findViewById(R.id.account_name);
        mobile=(EditText)findViewById(R.id.mobile);

        is_member = (RadioGroup)findViewById(R.id.is_member);
        select_servicestatus = (NiceSpinner)findViewById(R.id.select_servicestatus);

        service_startdate=(TextView)findViewById(R.id.service_startdate);
        service_enddate=(TextView)findViewById(R.id.service_enddate);
        order_startdate=(TextView)findViewById(R.id.order_startdate);
        order_enddate=(TextView)findViewById(R.id.order_enddate);

        submit_btn = (PaperButton)findViewById(R.id.submit_btn);

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
         *  服务开始时间
         */
        service_startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = service_startdate.getText().toString();
                if (!"开始日期".equals(date) && !StringUtils.isEmpty(date)) {
                    calendar = CalendarDay.from(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(5, 7)) - 1, Integer.parseInt(date.substring(8, 10)));
                }
                showDatePickerDialog(OrderSearchActivity.this, calendar, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        service_startdate.setText(year + "-" + DateTimeUtils.formatMonth((monthOfYear + 1)) + "-" + DateTimeUtils.formatDay(dayOfMonth));
                    }
                });
            }
        });

        /**
         * 服务结束时间
         */
        service_enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = service_enddate.getText().toString();
                if (!"结束日期".equals(date) && !StringUtils.isEmpty(date)) {
                    calendar = CalendarDay.from(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(5, 7)) - 1, Integer.parseInt(date.substring(8, 10)));
                }
                showDatePickerDialog(OrderSearchActivity.this, calendar, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        service_enddate.setText(year + "-" + DateTimeUtils.formatMonth((monthOfYear + 1)) + "-" + DateTimeUtils.formatDay(dayOfMonth));
                    }
                });
            }
        });
        /**
         * 下单开始时间
         */
        order_startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = order_startdate.getText().toString();
                if (!"开始日期".equals(date) && !StringUtils.isEmpty(date)) {
                    calendar = CalendarDay.from(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(5, 7)) - 1, Integer.parseInt(date.substring(8, 10)));
                }
                showDatePickerDialog(OrderSearchActivity.this, calendar, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        order_startdate.setText(year + "-" + DateTimeUtils.formatMonth((monthOfYear + 1)) + "-" + DateTimeUtils.formatDay(dayOfMonth));
                    }
                });
            }
        });

        /**
         * 下单结束时间
         */
        order_enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = order_enddate.getText().toString();
                if (!"结束日期".equals(date) && !StringUtils.isEmpty(date)) {
                    calendar = CalendarDay.from(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(5, 7)) - 1, Integer.parseInt(date.substring(8, 10)));
                }
                showDatePickerDialog(OrderSearchActivity.this, calendar, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        order_enddate.setText(year + "-" + DateTimeUtils.formatMonth((monthOfYear + 1)) + "-" + DateTimeUtils.formatDay(dayOfMonth));
                    }
                });
            }
        });

        /**
         * 点击提交
         */
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                param = new OrderEntity();
                Intent intent = getIntent();
                if(!StringUtils.isEmpty(order_no.getText().toString())){
                    param.setCode(order_no.getText().toString());
                    intent.putExtra("code", order_no.getText().toString());
                }

                if(!StringUtils.isEmpty(account_name.getText().toString())){
                    param.setCustomerName(account_name.getText().toString());
                    intent.putExtra("customerName", account_name.getText().toString());
                }

                if(!StringUtils.isEmpty(mobile.getText().toString())){
                    param.setMobile(mobile.getText().toString());
                    intent.putExtra("mobile", mobile.getText().toString());
                }

                if (R.id.is_member_btn == is_member.getCheckedRadioButtonId()) {
                    param.setIsVip("1");
                    intent.putExtra("isVip", "1");
                } else if (R.id.not_member_btn == is_member.getCheckedRadioButtonId()) {
                    param.setIsVip("0");
                    intent.putExtra("isVip", "0");
                }

                //服务开始日期
                if(!StringUtils.isEmpty(service_startdate.getText().toString())&& !"开始日期".equals(service_startdate.getText().toString())){
                    param.setStartTime(service_startdate.getText().toString());
                    intent.putExtra("serverTimeBegin", service_startdate.getText().toString());
                }

                //服务结束日期
                if(!StringUtils.isEmpty(service_enddate.getText().toString())&& !"结束日期".equals(service_enddate.getText().toString())){
                    param.setEndTime(service_enddate.getText().toString());
                    intent.putExtra("serverTimeEnd", service_enddate.getText().toString());
                }

                //下单开始日期
                if(!StringUtils.isEmpty(order_startdate.getText().toString())&& !"开始日期".equals(order_startdate.getText().toString())){
                    param.setOrder_time_begin(order_startdate.getText().toString());
                    intent.putExtra("orderTimeBegin", order_startdate.getText().toString());
                }

                //下单结束日期
                if(!StringUtils.isEmpty(order_enddate.getText().toString())&& !"结束日期".equals(order_enddate.getText().toString())){
                    param.setOrder_time_end(order_enddate.getText().toString());
                    intent.putExtra("orderTimeEnd", order_enddate.getText().toString());
                }


                //选择服务状态
                if(!StringUtils.isEmpty(select_servicestatus.getText().toString())&& !"请选择".equals(select_servicestatus.getText().toString())){
                    param.setStatus(OrderStatusEnum.getCodeByMsg(select_servicestatus.getText().toString()));
                    intent.putExtra("status", OrderStatusEnum.getCodeByMsg(select_servicestatus.getText().toString()));
                }
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }


    /**
     * 获取门店列表、产品类型
     */
    private void initData() {
        TargetEntity entity = new TargetEntity();

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

        //DatePicker dp = dialog.getDatePicker();
        /*if (dp != null) {
            ((ViewGroup) ((ViewGroup) dp.getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
        }*/
        dialog.show();
    }


}
