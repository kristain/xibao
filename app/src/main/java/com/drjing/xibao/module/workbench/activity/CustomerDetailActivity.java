package com.drjing.xibao.module.workbench.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.utils.FuncUtils;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.module.entity.CustomerEntity;
import com.drjing.xibao.module.entity.OrderTypeEnum;
import com.drjing.xibao.module.ui.UIHelper;
import com.kristain.common.utils.DateTimeUtils;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;

/**
 * Created by kristain on 16/1/4.
 */
public class CustomerDetailActivity extends SwipeBackActivity {

    /**
     * 后退返回按钮
     */
    private Button btnBack;
    /**
     * 标题栏文案
     */
    private TextView textHeadTitle;


    private ImageView vip_logo;

    private RelativeLayout account_detail,order_detail,datelog_detail,nurselog_detail,
            secretlog_detail,secrettopic_detail,salelog_detail,
            activatelog_detail,complainlog_detail;

    private TextView name,tel,last_service_time,last_threemonth,beautician_name,counselor_name,shop_name;

    private Bundle bundle;

    private String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);
        bundle=getIntent().getExtras();
        initView();
        initEvent();
        getCustomerDetail();
    }

    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("客户详情");

        vip_logo = (ImageView)findViewById(R.id.vip_logo);

        tel = (TextView)findViewById(R.id.tel);
        last_service_time = (TextView)findViewById(R.id.last_service_time);
        last_threemonth = (TextView)findViewById(R.id.last_threemonth);
        beautician_name = (TextView)findViewById(R.id.beautician_name);
        counselor_name = (TextView)findViewById(R.id.counselor_name);
        shop_name = (TextView)findViewById(R.id.shop_name);
        name = (TextView)findViewById(R.id.name);

        account_detail = (RelativeLayout)findViewById(R.id.account_detail);
        order_detail = (RelativeLayout)findViewById(R.id.order_detail);
        nurselog_detail = (RelativeLayout)findViewById(R.id.nurselog_detail);
        datelog_detail = (RelativeLayout)findViewById(R.id.datelog_detail);
        secretlog_detail = (RelativeLayout)findViewById(R.id.secretlog_detail);
        secrettopic_detail = (RelativeLayout)findViewById(R.id.secrettopic_detail);
        salelog_detail = (RelativeLayout)findViewById(R.id.salelog_detail);
        activatelog_detail = (RelativeLayout)findViewById(R.id.activatelog_detail);
        complainlog_detail = (RelativeLayout)findViewById(R.id.complainlog_detail);
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
        //账户详情
        account_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showAccountInfo(CustomerDetailActivity.this, bundle);
            }
        });
        //订单信息
        order_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showCustomerOrderList(CustomerDetailActivity.this, bundle);
            }
        });
        //护理日志
        nurselog_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("source","customer");
                UIHelper.showNurseLog(CustomerDetailActivity.this, bundle);
            }
        });
        //特殊日期
        datelog_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("source","customer");
                UIHelper.showSpecialLog(CustomerDetailActivity.this, bundle);
            }
        });
        //私密日志
        secretlog_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("source","customer");
                UIHelper.showLifeLog(CustomerDetailActivity.this, bundle);
            }
        });
        //私密话题
        secrettopic_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("source","customer");
                bundle.putString("source","customer");
                UIHelper.showTopicLog(CustomerDetailActivity.this, bundle);
            }
        });
        //销售日志
        salelog_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle item = new Bundle();
                bundle.putString("source","customer");
                item.putString("customer_id", bundle.getString("customer_id"));
                UIHelper.showSaleLog(CustomerDetailActivity.this,item);
            }
        });
        //激活日志
        activatelog_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("source","customer");
                bundle.putString("customer_id",bundle.getString("customer_id"));
                bundle.putString("mobile",mobile);
                bundle.putString("order_type", OrderTypeEnum.ACTIVATE.getCode());
                UIHelper.showRemindLog(CustomerDetailActivity.this, bundle);
            }
        });
        //投诉日志
        complainlog_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }



    /**
     * 获取客户明细
     */
    private void getCustomerDetail(){
        CustomerEntity param = new CustomerEntity();
        if(!StringUtils.isEmpty(bundle.getString("customer_id"))){
            param.setId(Integer.parseInt(bundle.getString("customer_id")));
            HttpClient.getCustomerDetail(param, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    JSONObject object = JSON.parseObject(body);
                    Log.i("getCustomerDetailTAG", "成功返回数据:" + body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        JSONObject data = JSON.parseObject(object.getString("data"));
                        if("1".equals(data.getString("vip"))){
                            vip_logo.setVisibility(View.VISIBLE);
                        }
                        name.setText(StringUtils.formatCustomerName(data.getString("name")));
                        mobile = data.getString("mobile");
                        tel.setText(FuncUtils.formatPhone(data.getString("mobile")));
                        last_service_time.setText(StringUtils.isEmpty(data.getString("lastOrderTime"))?"":DateTimeUtils.formatDateByMill(Long.parseLong(data.getString("lastOrderTime"))));
                        last_threemonth.setText("到店"+data.getString("countToStore")+"次,上门"+data.getString("countToDoor")+"次");
                        beautician_name.setText("美容师:"+ StringUtils.formatNull(data.getString("staffName")));
                        counselor_name.setText("顾问:"+StringUtils.formatNull(data.getString("adviser")));
                        shop_name.setText( StringUtils.formatNull(data.getString("storeName")));
                    } else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(CustomerDetailActivity.this);
                    } else {
                        Log.i("getCustomerDetailTAG", "成功返回数据:" + body);
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("getCustomerDetailTAG", "失败返回数据:" + request.toString());
                }
            }, CustomerDetailActivity.this);
        }else{
            Toast.makeText(this,"缺少请求参数[customer_id]",Toast.LENGTH_LONG).show();
        }


    }

}
