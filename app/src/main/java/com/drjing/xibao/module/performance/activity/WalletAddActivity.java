package com.drjing.xibao.module.performance.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.drjing.xibao.common.view.materialspinner.CalendarSpinner;
import com.drjing.xibao.common.view.materialspinner.NiceSpinner;
import com.drjing.xibao.common.view.materialwidget.PaperButton;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.module.entity.CategroyEntity;
import com.drjing.xibao.module.entity.CategroyEnum;
import com.drjing.xibao.module.entity.WalletEntity;
import com.drjing.xibao.module.ui.UIHelper;
import com.kristain.common.utils.DateTimeUtils;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 钱包新增页面
 * Created by kristain on 16/1/3.
 */
public class WalletAddActivity extends SwipeBackActivity {

    /**
     * 后退返回按钮
     */
    private Button btnBack;
    /**
     * 标题栏文案
     */
    private TextView textHeadTitle;

    /**
     * 提交
     */
    private PaperButton submit_button;


    private EditText remark,point,money;

    private NiceSpinner select_type;

    private CalendarSpinner select_date;

    private CategroyEntity param;
    List<CategroyEntity> list;
    ArrayList<String> dataset = new ArrayList<String>();

    CalendarDay calendar = CalendarDay.today();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_add);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        btnBack = (Button)findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("钱包");
        submit_button = (PaperButton) findViewById(R.id.submit_button);
        remark = (EditText)findViewById(R.id.remark);
        point = (EditText)findViewById(R.id.point);
        money = (EditText)findViewById(R.id.money);
        select_date =(CalendarSpinner) findViewById(R.id.select_date);
        select_type = (NiceSpinner) findViewById(R.id.select_type);
        submit_button.setClickable(false);
        submit_button.setEnabled(false);

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
         * 提交
         */
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isEmpty(money.getText().toString())) {
                    Toast.makeText(WalletAddActivity.this, "请输入金额", Toast.LENGTH_LONG).show();
                    return;
                }

                if (StringUtils.isEmpty(point.getText().toString())) {
                    Toast.makeText(WalletAddActivity.this, "请输入点数", Toast.LENGTH_LONG).show();
                    return;
                }

                submitWallet();
            }
        });

        /**
         * 选择日期
         */
        select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = select_date.getText().toString();
                if (!"请选择日期".equals(date) && !StringUtils.isEmpty(date)) {
                    calendar = CalendarDay.from(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(5, 7)) - 1, Integer.parseInt(date.substring(8, 10)));
                }
                showDatePickerDialog(WalletAddActivity.this, calendar, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        select_date.setText(year + "-" + DateTimeUtils.formatMonth((monthOfYear + 1)) + "-" + DateTimeUtils.formatDay(dayOfMonth));
                    }
                });
            }
        });
    }


    private void initData(){
        param = new CategroyEntity();
        param.setCatetype(CategroyEnum.PROJECT.code);
        HttpClient.getCateGoryList(param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String body) {
                Log.i("getwalletListTAG", "成功返回数据:" + body);
                JSONObject object = JSON.parseObject(body);
                if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                    list = JSONArray.parseArray(object.getString("data"), CategroyEntity.class);
                    for (int i = 0; i < list.size(); i++) {
                        dataset.add(list.get(i).getName());
                    }
                    select_type.attachDataSource(dataset);
                } else {
                    Log.i("getwalletListTAG", "获取数据失败:" + body);
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                Log.i("getwalletListTAG", "失败返回数据:" + request.toString());
            }
        }, WalletAddActivity.this);
    }

    /**
     * 添加钱包
     */
    private void submitWallet(){
        WalletEntity param = new WalletEntity();
        if(!StringUtils.isEmpty(getCategoryIdByName(list,select_type.getText().toString()))){
            param.setCategoryId(Integer.parseInt(getCategoryIdByName(list,select_type.getText().toString())));
            param.setAmount(Double.parseDouble(money.getText().toString()));
            param.setPercent(Double.parseDouble(point.getText().toString()));
            param.setRemarks(remark.getText().toString());
            param.setAddtime(select_date.getText().toString());
            HttpClient.AddWallet(param, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("AddWalletTAG", "成功返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        finish();
                    } else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(WalletAddActivity.this);
                    }else {
                        Log.i("AddWalletTAG", "新增钱包失败:" + body);
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("AddWalletTAG", "失败返回数据:" + request.toString());
                }
            }, WalletAddActivity.this);
        }else{
            Toast.makeText(this,"缺少请求参数[categoryId]",Toast.LENGTH_LONG).show();
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
