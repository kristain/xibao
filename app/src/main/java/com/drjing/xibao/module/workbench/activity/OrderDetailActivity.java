package com.drjing.xibao.module.workbench.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.utils.FuncUtils;
import com.drjing.xibao.common.view.dialog.Effectstype;
import com.drjing.xibao.common.view.dialog.NiftyDialogBuilder;
import com.drjing.xibao.common.view.materialwidget.PaperButton;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.module.entity.OrderEntity;
import com.drjing.xibao.module.entity.OrderServiceTypeEnum;
import com.drjing.xibao.module.entity.OrderStatusEnum;
import com.drjing.xibao.module.entity.OrderTypeEnum;
import com.drjing.xibao.module.ui.UIHelper;
import com.kristain.common.utils.DateTimeUtils;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;

/**
 * 订单详情
 * Created by kristain on 15/12/21.
 */
public class OrderDetailActivity extends SwipeBackActivity {


    /**
     * 后退返回按钮
     */
    private Button btnBack;
    /**
     * 标题栏文案
     */
    private TextView textHeadTitle;


    private OrderEntity param;

    private Bundle bundle;

    private TextView order_no, custom_name, custom_phone, service_type, service_text,
            service_date_text, order_date_text, report_date_text, order_status_text, code_text, remark_text;

    private ImageView logo;
    /**
     * 拨打电话按钮
     */
    private ImageView custom_phone_bt;
    /**
     * 提交按钮
     */
    private PaperButton paper_button;

    private RadioButton nurse_log_btn, special_log_btn, secret_log_btn, secret_topic_btn, sale_log_btn, sale_log_btn2, visit_log_btn, notice_log_btn;

    private RelativeLayout nurse_log_layout, special_log_layout, secret_log_layout, secret_topic_layout, sale_log_layout2, sale_log_layout, visit_log_layout, notice_log_layout;

    /**
     * 客户ID
     */
    private String customerId;

    private String mobile;


    private boolean statusNursingFlag = false;
    private boolean statusDateFlag = false;
    private boolean statusLifeFlag = false;
    private boolean statusTopicFlag = false;
    private boolean statusSaleFlag = false;
    private boolean statusVisitFlag =false;
    private boolean statusRemind =false;

    private boolean isremark=false;


    private TextView add_remark;

    private EditText remark_edit;

    private RelativeLayout edit_remark_layout,remark_layout;

    NiftyDialogBuilder dialogBuilder;
    private TextView remark_person;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        bundle = getIntent().getExtras();
        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("订单详情");

        logo = (ImageView) findViewById(R.id.logo);
        remark_person = (TextView)findViewById(R.id.remark_person);
        order_no = (TextView) findViewById(R.id.order_no);
        custom_name = (TextView) findViewById(R.id.custom_name);
        custom_phone = (TextView) findViewById(R.id.custom_phone);
        service_type = (TextView) findViewById(R.id.service_type);
        service_text = (TextView) findViewById(R.id.service_text);
        service_date_text = (TextView) findViewById(R.id.service_date_text);
        order_date_text = (TextView) findViewById(R.id.order_date_text);
        report_date_text = (TextView) findViewById(R.id.report_date_text);
        order_status_text = (TextView) findViewById(R.id.order_status_text);
        code_text = (TextView) findViewById(R.id.code_text);
        remark_text = (TextView) findViewById(R.id.remark_text);
        custom_phone_bt = (ImageView) findViewById(R.id.custom_phone_bt);
        paper_button = (PaperButton) findViewById(R.id.paper_button);

        edit_remark_layout = (RelativeLayout)findViewById(R.id.edit_remark_layout);
        remark_layout = (RelativeLayout)findViewById(R.id.remark_layout);
        add_remark = (TextView)findViewById(R.id.add_remark);
        remark_edit = (EditText)findViewById(R.id.remark_edit);

        nurse_log_btn = (RadioButton) findViewById(R.id.nurse_log_btn);
        special_log_btn = (RadioButton) findViewById(R.id.special_log_btn);
        secret_log_btn = (RadioButton) findViewById(R.id.secret_log_btn);
        secret_topic_btn = (RadioButton) findViewById(R.id.secret_topic_btn);
        sale_log_btn = (RadioButton) findViewById(R.id.sale_log_btn);
        sale_log_btn2 = (RadioButton) findViewById(R.id.sale_log_btn2);
        visit_log_btn = (RadioButton) findViewById(R.id.visit_log_btn);
        notice_log_btn = (RadioButton) findViewById(R.id.notice_log_btn);

        nurse_log_layout = (RelativeLayout) findViewById(R.id.nurse_log_layout);
        special_log_layout = (RelativeLayout) findViewById(R.id.special_log_layout);
        secret_log_layout = (RelativeLayout) findViewById(R.id.secret_log_layout);
        secret_topic_layout = (RelativeLayout) findViewById(R.id.secret_topic_layout);
        sale_log_layout = (RelativeLayout) findViewById(R.id.sale_log_layout);
        sale_log_layout2 = (RelativeLayout) findViewById(R.id.sale_log_layout2);
        visit_log_layout = (RelativeLayout) findViewById(R.id.visit_log_layout);
        notice_log_layout = (RelativeLayout) findViewById(R.id.notice_log_layout);
        if (OrderTypeEnum.MYORDER.getCode().equals(bundle.getString("order_type"))) {
            paper_button.setText("点击查询客户其他信息");
            nurse_log_layout.setVisibility(View.GONE);
            special_log_layout.setVisibility(View.GONE);
            secret_log_layout.setVisibility(View.GONE);
            secret_topic_layout.setVisibility(View.GONE);
            sale_log_layout.setVisibility(View.GONE);
            sale_log_layout2.setVisibility(View.VISIBLE);
            visit_log_layout.setVisibility(View.VISIBLE);
            notice_log_layout.setVisibility(View.VISIBLE);
        } else if (OrderTypeEnum.REVISITORDER.getCode().equals(bundle.getString("order_type"))) {
            paper_button.setVisibility(View.GONE);
            nurse_log_layout.setVisibility(View.GONE);
            special_log_layout.setVisibility(View.GONE);
            secret_log_layout.setVisibility(View.GONE);
            secret_topic_layout.setVisibility(View.GONE);
            sale_log_layout.setVisibility(View.GONE);
            visit_log_layout.setVisibility(View.VISIBLE);
        } else if (OrderTypeEnum.REMINDORDER.getCode().equals(bundle.getString("order_type"))) {
            paper_button.setVisibility(View.GONE);
            nurse_log_layout.setVisibility(View.GONE);
            special_log_layout.setVisibility(View.GONE);
            secret_log_layout.setVisibility(View.GONE);
            secret_topic_layout.setVisibility(View.GONE);
            sale_log_layout.setVisibility(View.GONE);
            notice_log_layout.setVisibility(View.VISIBLE);
        } else if (OrderTypeEnum.UNREMARK.getCode().equals(bundle.getString("order_type"))) {
            paper_button.setVisibility(View.VISIBLE);
            paper_button.setText("点击查询客户其他信息");
            nurse_log_layout.setVisibility(View.GONE);
            special_log_layout.setVisibility(View.GONE);
            secret_log_layout.setVisibility(View.GONE);
            secret_topic_layout.setVisibility(View.GONE);
            sale_log_layout.setVisibility(View.GONE);
            notice_log_layout.setVisibility(View.GONE);
            edit_remark_layout.setVisibility(View.VISIBLE);
            remark_layout.setVisibility(View.GONE);
        } else if (OrderTypeEnum.SALELOG.getCode().equals(bundle.getString("order_type"))
                ||OrderTypeEnum.NEWORDER.getCode().equals(bundle.getString("order_type"))) {
            //新增订单和销售日志只显示"点击查询客户其他信息"
            paper_button.setVisibility(View.VISIBLE);
            paper_button.setText("点击查询客户其他信息");
            nurse_log_layout.setVisibility(View.GONE);
            special_log_layout.setVisibility(View.GONE);
            secret_log_layout.setVisibility(View.GONE);
            secret_topic_layout.setVisibility(View.GONE);
            sale_log_layout.setVisibility(View.GONE);
            notice_log_layout.setVisibility(View.GONE);
        } else if("customer_order".equals(bundle.getString("order_type"))){
            paper_button.setVisibility(View.GONE);
            notice_log_layout.setVisibility(View.VISIBLE);
            nurse_log_layout.setVisibility(View.GONE);
            special_log_layout.setVisibility(View.GONE);
            secret_log_layout.setVisibility(View.GONE);
            secret_topic_layout.setVisibility(View.GONE);
            sale_log_layout.setVisibility(View.GONE);
            sale_log_layout2.setVisibility(View.VISIBLE);
            visit_log_layout.setVisibility(View.VISIBLE);

        }else {
            paper_button.setText("提交");
        }
    }

    private void initEvent() {
        /**
         * 返回后退点击事件
         */
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 提醒日志、回访日志 、订单备注 返回上一页删除订单
                //提醒日志
                if (OrderTypeEnum.REMINDORDER.getCode().equals(bundle.getString("order_type"))&& statusRemind) {
                    Intent intent = getIntent();
                    intent.putExtra("position", bundle.getString("position"));
                    setResult(RESULT_OK, intent);
                }
                //回访日志
                if (OrderTypeEnum.REVISITORDER.getCode().equals(bundle.getString("order_type"))&& statusVisitFlag) {
                    Intent intent = getIntent();
                    intent.putExtra("position", bundle.getString("position"));
                    setResult(RESULT_OK, intent);
                }
                //订单备注
                if(OrderTypeEnum.UNREMARK.getCode().equals(bundle.getString("order_type")) && isremark){
                    Log.e("TAG","订单备注返回上一页 position:"+bundle.getString("position"));
                    Intent intent = getIntent();
                    intent.putExtra("position", bundle.getString("position"));
                    setResult(RESULT_OK, intent);
                }else{
                    Log.e("TAG","订单备注 finish:"+isremark+"position:"+bundle.getString("position"));
                }
                finish();
            }
        });

        /**
         * 拨打电话
         */
        custom_phone_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder = NiftyDialogBuilder.getInstance(OrderDetailActivity.this);
                dialogBuilder
                        .withTitle("提示")
                        .withTitleColor("#FFFFFF")
                        .withDividerColor("#11000000")
                        .withMessage("确定拨打电话吗？")
                        .withMessageColor("#FFFFFFFF")
                        .withDialogColor("#FFE74C3C")
                        .withIcon(
                                getResources().getDrawable(
                                        R.drawable.ic_favorite_white_48dp))
                        .isCancelableOnTouchOutside(true)
                        .withDuration(700)
                        .withEffect(Effectstype.Fliph)
                        .withButton1Text("确定")
                        .withButton2Text("结束")
                        .setCustomView(R.layout.dialog_effects_custom_view,
                                v.getContext())
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobile));
                                startActivity(intent);
                                dialogBuilder.dismiss();
                            }
                        }).setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                }).show();


            }
        });
        /**
         * 点击提交
         */
        paper_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (OrderTypeEnum.MYORDER.getCode().equals(bundle.getString("order_type")) ||
                        OrderTypeEnum.UNREMARK.getCode().equals(bundle.getString("order_type")) ||
                        OrderTypeEnum.SALELOG.getCode().equals(bundle.getString("order_type")) ||
                        OrderTypeEnum.NEWORDER.getCode().equals(bundle.getString("order_type"))) {
                    if(!StringUtils.isEmpty(customerId)){
                        Bundle item = new Bundle();
                        item.putString("customer_id", customerId);
                        UIHelper.showCustomerDetail(OrderDetailActivity.this, item);
                    }
                } else {
                    if (!statusNursingFlag) {
                        Toast.makeText(OrderDetailActivity.this, "请填写护理日志", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!statusDateFlag) {
                        Toast.makeText(OrderDetailActivity.this, "请填写特殊日期", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!statusLifeFlag) {
                        Toast.makeText(OrderDetailActivity.this, "请填写私密生活", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!statusTopicFlag) {
                        Toast.makeText(OrderDetailActivity.this, "请填写私密话题", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!statusSaleFlag) {
                        Toast.makeText(OrderDetailActivity.this, "请填写销售日志", Toast.LENGTH_LONG).show();
                        return;
                    }
                    doPostOrder();
                }
            }
        });
        /**
         * 点击护理日志
         */
        nurse_log_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle item = new Bundle();
                item.putString("customer_id", customerId);
                item.putString("order_id", bundle.getString("code"));
                if(!StringUtils.isEmpty(customerId)) {
                    UIHelper.showNurseLog(OrderDetailActivity.this, item);
                }
            }
        });
        /**
         * 点击特殊日期
         */
        special_log_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle item = new Bundle();
                item.putString("customer_id", customerId);
                item.putString("order_id", bundle.getString("code"));
                if(!StringUtils.isEmpty(customerId)) {
                    UIHelper.showSpecialLog(OrderDetailActivity.this, item);
                }
            }
        });
        /**
         * 点击私密日志
         */
        secret_log_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle item = new Bundle();
                item.putString("customer_id", customerId);
                item.putString("order_id", bundle.getString("code"));
                if(!StringUtils.isEmpty(customerId)) {
                    UIHelper.showLifeLog(OrderDetailActivity.this, item);
                }
            }
        });
        /**
         * 点击私密话题
         */
        secret_topic_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle item = new Bundle();
                item.putString("customer_id", customerId);
                item.putString("order_id", bundle.getString("code"));
                if(!StringUtils.isEmpty(customerId)) {
                    UIHelper.showTopicLog(OrderDetailActivity.this, item);
                }
            }
        });

        /**
         * 点击销售日志
         */
        sale_log_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle item = new Bundle();
                item.putString("order_id", bundle.getString("code"));
                item.putString("customer_id", customerId);
                if(!StringUtils.isEmpty(customerId)) {
                    UIHelper.showSaleLog(OrderDetailActivity.this, item);
                }
            }
        });

        /**
         * 点击销售日志
         */
        sale_log_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle item = new Bundle();
                item.putString("order_id", bundle.getString("code"));
                item.putString("order_id", bundle.getString("code"));
                item.putString("customer_id", customerId);
                if(!StringUtils.isEmpty(customerId)) {
                    UIHelper.showSaleLog(OrderDetailActivity.this, item);
                }
            }
        });

        /**
         * 点击回访日志
         */
        visit_log_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle item = new Bundle();
                item.putString("order_id", bundle.getString("code"));
                item.putString("customer_id", customerId);
                item.putString("order_type", OrderTypeEnum.REVISITORDER.getCode());
                item.putString("mobile",mobile);
                if(!StringUtils.isEmpty(customerId)) {
                    UIHelper.showRemindLog(OrderDetailActivity.this, item);
                }
            }
        });
        /**
         * 点击提醒日志
         */
        notice_log_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle item = new Bundle();
                item.putString("order_id", bundle.getString("code"));
                item.putString("customer_id", customerId);
                item.putString("order_type", OrderTypeEnum.REMINDORDER.getCode());
                item.putString("mobile",mobile);
                if(!StringUtils.isEmpty(customerId)) {
                    UIHelper.showRemindLog(OrderDetailActivity.this, item);
                }
            }
        });

        /**
         * 点击提交备注
         */
        add_remark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(StringUtils.isEmpty(remark_edit.getText().toString())){
                    Toast.makeText(OrderDetailActivity.this,"请填写备注",Toast.LENGTH_LONG).show();
                    return;
                }
                addOrderNote();
            }
        });

    }

    /**
     * 初始化数据
     */
    private void loadData() {
        param = new OrderEntity();
        param.setCode(bundle.getString("code"));
        if(!StringUtils.isEmpty(param.getCode())){
            HttpClient.getOrderDetail(param, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("getOrdersTAG", "成功返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        JSONObject data = JSON.parseObject(object.getString("data"));

                        JSONObject customer = JSON.parseObject(data.getString("customer"));
                        customerId = customer.getString("id");
                        order_no.setText(data.getString("code"));
                        custom_name.setText(StringUtils.formatCustomerName(customer.getString("name")));
                        mobile = customer.getString("mobile");
                        custom_phone.setText(FuncUtils.formatPhone(customer.getString("mobile")));
                        service_type.setText(OrderServiceTypeEnum.getMsgByCode(data.getString("type")) + ":");
                        service_text.setText(OrderServiceTypeEnum.STORETYPE.code.equals(data.getString("type")) ? data.getString("storeName") : data.getString("content"));
                        //服务时间
                        service_date_text.setText(FuncUtils.getOrderServerTime(data.getString("startTime"), data.getString("endTime")));
                        //下单时间
                        order_date_text.setText((StringUtils.isEmpty(data.getString("addTime"))?"":DateTimeUtils.formatDateByMill(Long.parseLong(data.getString("addTime")))));
                        //报时时间
                        report_date_text.setText(FuncUtils.getOrderServerTime(data.getString("clockTimeStart"), data.getString("clockTimeEnd")));
                        order_status_text.setText(OrderStatusEnum.getMsgByCode(data.getString("status")));

                        //备注人信息
                        remark_person.setText("备注人:"+StringUtils.formatNull(data.getString("noteOwnerName")));
                        if(OrderServiceTypeEnum.STORETYPE.code.equals(data.getString("type"))){
                            code_text.setText(StringUtils.isEmpty(data.getString("storeName")) ? "无" : StringUtils.formatNull(data.getString("storeName")));
                        }else{
                            code_text.setText(StringUtils.isEmpty(data.getString("address")) ? "无" : StringUtils.formatNull(data.getString("address")));
                        }
                        remark_text.setText(StringUtils.isEmpty(data.getString("orderNote")) ? "无" : data.getString("orderNote"));
                        if(StringUtils.isEmpty(data.getString("orderNote")) && OrderTypeEnum.UNREMARK.getCode().equals(bundle.getString("order_type"))){
                            remark_layout.setVisibility(View.GONE);
                            edit_remark_layout.setVisibility(View.VISIBLE);
                        }
                        if ("1".equals(customer.getString("vip"))) {
                            logo.setVisibility(View.VISIBLE);
                        }
                        if ("1".equals(data.getString("statusNursing"))) {
                            nurse_log_layout.setBackgroundResource(R.drawable.icon_filled);
                            statusNursingFlag = true;
                        }
                        if ("1".equals(data.getString("statusDate"))) {
                            special_log_layout.setBackgroundResource(R.drawable.icon_filled);
                            statusDateFlag = true;
                        }
                        if ("1".equals(data.getString("statusLife"))) {
                            secret_log_layout.setBackgroundResource(R.drawable.icon_filled);
                            statusLifeFlag = true;
                        }
                        if ("1".equals(data.getString("statusTopic"))) {
                            secret_topic_layout.setBackgroundResource(R.drawable.icon_filled);
                            statusTopicFlag = true;
                        }
                        if ("1".equals(data.getString("statusSale"))) {
                            sale_log_layout.setBackgroundResource(R.drawable.icon_filled);
                            sale_log_layout2.setBackgroundResource(R.drawable.icon_filled);
                            statusSaleFlag = true;
                        }
                        if ("1".equals(data.getString("statusVisit"))) {
                            visit_log_layout.setBackgroundResource(R.drawable.icon_filled);
                            statusVisitFlag =true;
                        }

                        if("1".equals(data.getString("statusRemind"))){
                            notice_log_layout.setBackgroundResource(R.drawable.icon_filled);
                            statusRemind =true;
                        }
                    }else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(OrderDetailActivity.this);
                    }  else {
                        Log.i("getOrdersTAG", "失败返回数据:" + body);
                        Toast.makeText(OrderDetailActivity.this,"获取订单详情失败",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("getOrdersTAG", "失败返回数据:" + request.toString());
                    Toast.makeText(OrderDetailActivity.this,"获取订单详情失败",Toast.LENGTH_LONG).show();
                }
            }, this);
        }else{
            Toast.makeText(OrderDetailActivity.this,"缺少请求参数[order_id]",Toast.LENGTH_LONG).show();
        }

    }

    /**
     * 提交订单总状态
     */
    private void doPostOrder() {
        param = new OrderEntity();
        param.setCode(bundle.getString("code"));
        if(!StringUtils.isEmpty(param.getCode())){
            HttpClient.doOrderStatus(param, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("doOrderStatusTAG", "成功返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        //服务日志 返回上一页 删除该订单
                        Intent intent = getIntent();
                        intent.putExtra("position", bundle.getString("position"));
                        setResult(RESULT_OK, intent);
                        finish();
                    } else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(OrderDetailActivity.this);
                    } else {
                        Toast.makeText(OrderDetailActivity.this,"提交订单状态失败",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("doOrderStatusTAG", "失败返回数据:" + request.toString());
                    Toast.makeText(OrderDetailActivity.this,"提交订单状态失败",Toast.LENGTH_LONG).show();
                }
            }, this);
        }else{
            Toast.makeText(OrderDetailActivity.this,"缺少请求参数[order_id]",Toast.LENGTH_LONG).show();
        }

    }


    /**
     * 添加订单备注
     */
    private void addOrderNote(){
        param = new OrderEntity();
        param.setOrderNote(remark_edit.getText().toString());
        param.setCode(bundle.getString("code"));
        if(!StringUtils.isEmpty(param.getCode())){
            HttpClient.addOrderNote(param, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("addOrderNoteTAG", "成功返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        edit_remark_layout.setVisibility(View.GONE);
                        remark_layout.setVisibility(View.VISIBLE);
                        remark_text.setText(remark_edit.getText().toString());
                        remark_person.setText("备注人:" + bundle.getString("accountname"));
                        //TODO 返回上一页 删除该订单
                        isremark = true;
                    } else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(OrderDetailActivity.this);
                    } else {
                        Toast.makeText(OrderDetailActivity.this,"添加备注失败",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("addOrderNoteTAG", "失败返回数据:" + request.toString());

                }
            }, this);
        }else{
            Toast.makeText(OrderDetailActivity.this,"缺少请求参数[order_id]",Toast.LENGTH_LONG).show();
        }

    }


}
