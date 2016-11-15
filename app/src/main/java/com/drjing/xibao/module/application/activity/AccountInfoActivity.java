package com.drjing.xibao.module.application.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.view.materialspinner.NiceSpinner;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.module.entity.CustomerCardEntity;
import com.drjing.xibao.module.entity.CustomerEntity;
import com.drjing.xibao.module.ui.UIHelper;
import com.kristain.common.utils.DateTimeUtils;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 账户详情
 * Created by kristain on 15/12/29.
 */
public class AccountInfoActivity extends SwipeBackActivity {

    /**
     * 后退返回按钮
     */
    private Button btnBack;
    /**
     * 标题栏文案
     */
    private TextView textHeadTitle;

    private TextView project_check,project_info,course_info,product_info;

    private TextView name,card_validate,project_discount,product_discount,consume_time_text,
            money,product_money,project_money,course_money;
    private NiceSpinner card_no;

    private Bundle bundle;

    List<CustomerCardEntity> list;
    ArrayList<String> dataset = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        bundle=getIntent().getExtras();
        initView();
        initEvent();
        getCustomerCard();
    }

    private void initView() {
        btnBack = (Button)findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("账户详情");
        product_info = (TextView)findViewById(R.id.product_info);
        project_check = (TextView)findViewById(R.id.project_check);
        course_info = (TextView)findViewById(R.id.course_info);
        project_info = (TextView)findViewById(R.id.project_info);

        card_no = (NiceSpinner)findViewById(R.id.card_no);
        name =(TextView)findViewById(R.id.name);
        card_validate=(TextView)findViewById(R.id.card_validate);
        project_discount=(TextView)findViewById(R.id.project_discount);
        product_discount=(TextView)findViewById(R.id.product_discount);
        consume_time_text=(TextView)findViewById(R.id.consume_time_text);
        money=(TextView)findViewById(R.id.money);
        product_money=(TextView)findViewById(R.id.product_money);
        project_money=(TextView)findViewById(R.id.project_money);
        course_money=(TextView)findViewById(R.id.course_money);

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
        product_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showProductInfo(AccountInfoActivity.this);
            }
        });

        /**
         * 点击切换会员卡
         */
        card_no.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CustomerCardEntity item = getCardByCardno(list, card_no.getText().toString());
                if (item != null) {
                    name.setText(item.getCustomerName());
                    card_validate.setText("会员年限：" +(StringUtils.isEmpty(item.getCreateTime())?"": DateTimeUtils.formatFriendly(Long.parseLong(item.getCreateTime()))));
                    project_discount.setText("项目折扣：" + item.getProjectDis());
                    product_discount.setText("产品折扣：" + item.getProductDis());
                    consume_time_text.setText((StringUtils.isEmpty(item.getLastPay())?"":DateTimeUtils.formatDateTime(Long.parseLong(item.getLastPay()))));
                    money.setText("$" + item.getCashBalance());
                    product_money.setText("$" + item.getProductBalance());
                    project_money.setText("$" + item.getProjectBalance());
                    course_money.setText("$" + item.getCourseBalance());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    /**
     * 护理日志标签+1
     */
    private void getCustomerCard(){
        CustomerEntity entity = new CustomerEntity();
        if(!StringUtils.isEmpty(bundle.getString("customer_id")+"")){
            entity.setId(Integer.parseInt(bundle.getString("customer_id")));
            HttpClient.getCustomerCardInfo(entity, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("getCustomerCardInfoTAG", "返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        list = JSONArray.parseArray(object.getString("data"), CustomerCardEntity.class);
                        if (list != null && list.size() > 0) {
                            for (int i = 0; i < list.size(); i++) {
                                dataset.add(list.get(i).getCardNumber());
                            }
                            card_no.attachDataSource(dataset);
                            card_no.setText(list.get(0).getCardNumber());
                            name.setText(list.get(0).getCustomerName());
                            card_validate.setText("会员年限：" + (StringUtils.isEmpty(list.get(0).getCreateTime())?"":DateTimeUtils.formatFriendly(Long.parseLong(list.get(0).getCreateTime()))));
                            project_discount.setText("项目折扣：" + list.get(0).getProjectDis());
                            product_discount.setText("产品折扣：" + list.get(0).getProductDis());
                            consume_time_text.setText((StringUtils.isEmpty(list.get(0).getLastPay())?"":DateTimeUtils.formatDateTime(Long.parseLong(list.get(0).getLastPay()))));
                            money.setText("$" + list.get(0).getCashBalance());
                            product_money.setText("$" + list.get(0).getProductBalance());
                            project_money.setText("$" + list.get(0).getProjectBalance());
                            course_money.setText("$" + list.get(0).getCourseBalance());
                        }
                    }else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))){
                        UIHelper.showLogin(AccountInfoActivity.this);
                    } else {
                        Toast.makeText(AccountInfoActivity.this, "获取失败",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("getCustomerCardInfoTAG", "失败返回数据:" + request.toString());

                }
            }, AccountInfoActivity.this);
        }else{
            Toast.makeText(AccountInfoActivity.this, "缺少参数[customer_id]",
                    Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 根据分类名称获取分类ID
     * @param list
     * @param cardno
     * @return
     */
    private CustomerCardEntity getCardByCardno(List<CustomerCardEntity> list,String cardno){
        if(list==null && list.size()==0){
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).getCardNumber().equals(cardno)){
                return list.get(i);
            }
        }
        return null;
    }



}
