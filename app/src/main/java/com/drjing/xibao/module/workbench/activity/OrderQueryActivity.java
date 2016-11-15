package com.drjing.xibao.module.workbench.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.view.calendarview.CalendarDay;
import com.drjing.xibao.common.view.calendarview.CustomerDatePickerDialog;
import com.drjing.xibao.common.view.materialspinner.NiceSpinner;
import com.drjing.xibao.common.view.materialwidget.PaperButton;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.module.entity.CategroyEntity;
import com.drjing.xibao.module.entity.OrderEntity;
import com.drjing.xibao.module.entity.OrderStatusEnum;
import com.drjing.xibao.module.entity.StoreEntity;
import com.drjing.xibao.module.entity.TargetEntity;
import com.drjing.xibao.module.ui.UIHelper;
import com.kristain.common.utils.DateTimeUtils;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 订单筛选
 * Created by kristain on 15/12/21.
 */
public class OrderQueryActivity extends SwipeBackActivity {


    /**
     * 后退返回按钮
     */
    private Button btnBack;
    /**
     * 标题栏文案
     */
    private TextView textHeadTitle;

    private EditText order_no, account_name, mobile;

    private RadioGroup is_member;
    private NiceSpinner select_beautician, select_store, select_servicestatus, select_adviser;

    private TextView service_startdate, service_enddate, order_startdate, order_enddate;

    private PaperButton submit_btn;


    private OrderEntity param;


    /**
     * 项目列表
     */
    List<CategroyEntity> categroylist;

    /**
     * 门店列表
     */
    List<StoreEntity> storelist;
    ArrayList<String> storeDataset = new ArrayList<String>();
    ArrayList<String> statusDataset = new ArrayList<>(Arrays.asList("取消订单审核中", "取消订单", "未支付", "待服务", "开始服务", "服务完成", "待评价"));


    private Bundle bundle;

    CalendarDay calendar = CalendarDay.today();


    private String adviser_id = "";
    private String staff_id="";

    public final static int REQUESTGWCODE = 0;//选择顾问
    public final static int REQUESTMRSCODE = 1;//选择美容师

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_query);
        bundle = getIntent().getExtras();
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("订单筛选");

        order_no = (EditText) findViewById(R.id.order_no);
        account_name = (EditText) findViewById(R.id.account_name);
        mobile = (EditText) findViewById(R.id.mobile);

        is_member = (RadioGroup) findViewById(R.id.is_member);

        select_beautician = (NiceSpinner) findViewById(R.id.select_beautician);
        select_store = (NiceSpinner) findViewById(R.id.select_store);
        select_adviser = (NiceSpinner) findViewById(R.id.select_adviser);
        select_servicestatus = (NiceSpinner) findViewById(R.id.select_servicestatus);
        select_servicestatus.attachDataSource(statusDataset);
        service_startdate = (TextView) findViewById(R.id.service_startdate);
        service_enddate = (TextView) findViewById(R.id.service_enddate);
        order_startdate = (TextView) findViewById(R.id.order_startdate);
        order_enddate = (TextView) findViewById(R.id.order_enddate);

        submit_btn = (PaperButton) findViewById(R.id.submit_btn);

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
         * 点击选择顾问
         */
        select_adviser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("store_id", getStoreIdByName(storelist, select_store.getText().toString()));
                UIHelper.showConsultantStoreSingleList(OrderQueryActivity.this, REQUESTGWCODE, bundle);
            }
        });

        /**
         * 点击选择美容师
         */
        select_beautician.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(StringUtils.isEmpty(adviser_id)){
                   Toast.makeText(OrderQueryActivity.this,"请先选择顾问",Toast.LENGTH_LONG).show();
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("adviser_id", adviser_id);
                UIHelper.showStaffStoreSingleList(OrderQueryActivity.this, REQUESTMRSCODE, bundle);
            }
        });

        /**
         * 服务开始时间
         */
        service_startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = service_startdate.getText().toString();
                if (!"开始日期".equals(date) && !StringUtils.isEmpty(date)) {
                    calendar = CalendarDay.from(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(5, 7)) - 1, Integer.parseInt(date.substring(8, 10)));
                }
                showDatePickerDialog(OrderQueryActivity.this, calendar, new DatePickerDialog.OnDateSetListener() {
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
                showDatePickerDialog(OrderQueryActivity.this, calendar, new DatePickerDialog.OnDateSetListener() {
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
                showDatePickerDialog(OrderQueryActivity.this, calendar, new DatePickerDialog.OnDateSetListener() {
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
                showDatePickerDialog(OrderQueryActivity.this, calendar, new DatePickerDialog.OnDateSetListener() {
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
                if (!StringUtils.isEmpty(order_no.getText().toString())) {
                    bundle.putString("code", order_no.getText().toString());
                }

                if (!StringUtils.isEmpty(account_name.getText().toString())) {
                    bundle.putString("customerName", account_name.getText().toString());
                }

                if (!StringUtils.isEmpty(mobile.getText().toString())) {
                    bundle.putString("mobile", mobile.getText().toString());
                }
                if (R.id.is_member_btn == is_member.getCheckedRadioButtonId()) {
                    bundle.putString("isVip", "1");
                } else if (R.id.not_member_btn == is_member.getCheckedRadioButtonId()) {
                    bundle.putString("isVip", "0");
                }

                //服务开始日期
                if (!StringUtils.isEmpty(service_startdate.getText().toString()) && !"开始日期".equals(service_startdate.getText().toString())) {
                    bundle.putString("serverTimeBegin", service_startdate.getText().toString());
                }

                //服务结束日期
                if (!StringUtils.isEmpty(service_enddate.getText().toString()) && !"结束日期".equals(service_enddate.getText().toString())) {
                    bundle.putString("serverTimeEnd", service_enddate.getText().toString());
                }

                //下单开始日期
                if (!StringUtils.isEmpty(order_startdate.getText().toString()) && !"开始日期".equals(order_startdate.getText().toString())) {
                    bundle.putString("orderTimeBegin", order_startdate.getText().toString());
                }

                //下单结束日期
                if (!StringUtils.isEmpty(order_enddate.getText().toString()) && !"结束日期".equals(order_enddate.getText().toString())) {
                    bundle.putString("orderTimeEnd", order_enddate.getText().toString());
                }


                //选择门店
                if (!StringUtils.isEmpty(select_store.getText().toString()) && !"请选择".equals(select_store.getText().toString())) {
                    bundle.putString("storeId", getStoreIdByName(storelist, select_store.getText().toString()));
                }

                //选择顾问
                if (!StringUtils.isEmpty(select_adviser.getText().toString()) && !"请选择".equals(select_adviser.getText().toString())) {
                    bundle.putString("adviserId", adviser_id);
                }
                //选择美容师
                if (!StringUtils.isEmpty(select_beautician.getText().toString()) && !"请选择".equals(select_beautician.getText().toString())) {
                    bundle.putString("staffId", staff_id);
                }

                //选择服务状态
                if (!StringUtils.isEmpty(select_servicestatus.getText().toString()) && !"请选择".equals(select_servicestatus.getText().toString())) {
                    bundle.putString("status", OrderStatusEnum.getCodeByMsg(select_servicestatus.getText().toString()));
                }

                UIHelper.showMyOrderList(OrderQueryActivity.this, bundle);
            }
        });
    }


    /**
     * 获取门店列表、产品类型
     */
    private void initData() {
        TargetEntity entity = new TargetEntity();
        if (!StringUtils.isEmpty(bundle.getString("user_id"))) {
            entity.setId(Integer.parseInt(bundle.getString("user_id")));
            HttpClient.getStoreList(entity, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("getStoreListTAG", "返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        storelist = JSONArray.parseArray(object.getString("data"), StoreEntity.class);
                        if (storelist != null && storelist.size() > 0) {
                            for (int i = 0; i < storelist.size(); i++) {
                                storeDataset.add(storelist.get(i).getName());
                            }
                            select_store.attachDataSource(storeDataset);
                        }
                    } else if (HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(OrderQueryActivity.this);
                    } else {
                        Toast.makeText(OrderQueryActivity.this, "获取门店信息失败",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("getStoreListTAG", "失败返回数据:" + request.toString());
                }
            }, OrderQueryActivity.this);
        } else {
            Toast.makeText(OrderQueryActivity.this, "缺少参数uid",
                    Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 根据门店名称获取门店ID
     *
     * @param list
     * @param storeName
     * @return
     */
    private String getStoreIdByName(List<StoreEntity> list, String storeName) {
        if (list == null || list.size() == 0) {
            return "";
        }

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName().equals(storeName)) {
                return list.get(i).getId() + "";
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

        //DatePicker dp = dialog.getDatePicker();
        /*if (dp != null) {
            ((ViewGroup) ((ViewGroup) dp.getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
        }*/
        dialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("onActivityResultTAG", "requestCode:" + requestCode + " resultCode:" + resultCode);
        switch (resultCode) {
            case RESULT_OK:
                //选择顾问
                if (data != null) {
                    Bundle b = data.getExtras(); //data为B中回传的Intent
                    String accountname = b.getString("accountname");//str即为回传的值
                    String staffname = b.getString("staffname");//str即为回传的值
                    if(!StringUtils.isEmpty(accountname)){
                        adviser_id = b.getString("adviser_id");
                        select_adviser.setText(accountname);
                    }
                    if(!StringUtils.isEmpty(staffname)){
                        staff_id = b.getString("staff_id");
                        select_beautician.setText(staffname);
                    }

                }
                break;
            default:
                break;
        }
    }
}
