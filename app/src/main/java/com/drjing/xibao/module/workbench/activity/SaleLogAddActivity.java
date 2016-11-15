package com.drjing.xibao.module.workbench.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.view.materialspinner.NiceSpinner;
import com.drjing.xibao.common.view.materialwidget.PaperButton;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.module.entity.CategroyEntity;
import com.drjing.xibao.module.entity.CategroyEnum;
import com.drjing.xibao.module.entity.NurseTagEntity;
import com.drjing.xibao.module.ui.UIHelper;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 销售日志添加
 * Created by kristain on 15/12/30.
 */
public class SaleLogAddActivity extends SwipeBackActivity {

    /**
     * 后退按钮
     */
    private Button btnBack;

    /**
     * Title栏文案
     */
    private TextView textHeadTitle;

    private EditText money;
    private NiceSpinner select_type;
    private PaperButton submit_btn;

    private TextView select_product_text, consultant_text, select_adviser;
    private ImageView select_project;
    /**
     * 项目列表
     */
    List<CategroyEntity> categroylist;

    ArrayList<String> categroyDataset = new ArrayList<String>();


    public final static int  REQUESTGWCODE=0;//选择顾问
    public final static int REQUESTXMCODE=1;//选择项目
    /**
     * 选择顾问ID
     */
    String adviser_id;

    /**
     * 选择项目IDs
     */
    String projectIds;

    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_log_add);
        bundle = getIntent().getExtras();
        initView();
        initEvent();
        loadData();
    }


    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        select_type = (NiceSpinner) findViewById(R.id.select_type);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("销售日志");
        money = (EditText) findViewById(R.id.money);
        select_project = (ImageView) findViewById(R.id.select_project);
        submit_btn = (PaperButton) findViewById(R.id.submit_btn);

        select_product_text = (TextView) findViewById(R.id.select_product_text);
        consultant_text = (TextView) findViewById(R.id.consultant_text);
        select_adviser = (TextView) findViewById(R.id.select_adviser);
        select_project = (ImageView) findViewById(R.id.select_project);
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
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validSubmit()) {
                    addSaleLog();
                }
            }
        });

        /**
         * 点击选择项目
         */
        select_project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isEmpty(select_type.getText().toString()) && "请选择指标".equals(select_type.getText().toString())) {
                    Toast.makeText(SaleLogAddActivity.this, "请选择指标", Toast.LENGTH_LONG).show();
                    return;
                }
                UIHelper.showProjectList(SaleLogAddActivity.this,REQUESTXMCODE,getCategoryIdByName(categroylist, select_type.getText().toString()),projectIds);
            }
        });
        /**
         * 点击选择顾问
         */
        select_adviser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showConsultantSingleList(SaleLogAddActivity.this,REQUESTGWCODE,new Bundle());
            }
        });
    }

    /**
     * 校验提交参数
     *
     * @return
     */
    private boolean validSubmit() {
        if (StringUtils.isEmpty(select_type.getText().toString()) && "请选择指标".equals(select_type.getText().toString())) {
            Toast.makeText(this, "请选择指标", Toast.LENGTH_LONG).show();
            return false;
        }
        if (StringUtils.isEmpty(money.getText().toString())) {
            Toast.makeText(this, "请输入金额", Toast.LENGTH_LONG).show();
            return false;
        }
        if (StringUtils.isEmpty(select_product_text.getText().toString())) {
            Toast.makeText(this, "请选择项目", Toast.LENGTH_LONG).show();
            return false;
        }
        if(StringUtils.isEmpty(consultant_text.getText().toString())){
            Toast.makeText(this, "请选择顾问", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * 添加销售日志
     */
    private void addSaleLog() {
        NurseTagEntity entity = new NurseTagEntity();
        entity.setCategoryId(getCategoryIdByName(categroylist, select_type.getText().toString()));
        entity.setProjectIds(projectIds);
        entity.setAmount(money.getText().toString());
        entity.setOrderId(bundle.getString("order_id"));
        entity.setCustomerId(bundle.getString("customer_id"));
        entity.setAdviserId(adviser_id);
        HttpClient.addSaleLog(entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String body) {
                Log.i("addSaleLogTAG", "返回数据:" + body);
                JSONObject object = JSON.parseObject(body);
                if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                    finish();
                }else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                    UIHelper.showLogin(SaleLogAddActivity.this);
                } else {
                    Toast.makeText(SaleLogAddActivity.this, "获取失败",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                Log.i("addSaleLogTAG", "失败返回数据:" + request.toString());
            }
        }, SaleLogAddActivity.this);
    }


    private void loadData() {
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
                    select_type.attachDataSource(categroyDataset);
                } else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                    UIHelper.showLogin(SaleLogAddActivity.this);
                }else {
                    Log.i("getCateGoryListTAG", "获取数据失败:" + body);
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                Log.i("getCateGoryListTAG", "失败返回数据:" + request.toString());
            }
        }, SaleLogAddActivity.this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("onActivityResultTAG","requestCode:"+requestCode+" resultCode:"+resultCode);
        switch (resultCode) {
            case RESULT_OK:
                //选择顾问
                if(data!=null){
                    Bundle b=data.getExtras(); //data为B中回传的Intent
                    String accountname=b.getString("accountname");//str即为回传的值
                    adviser_id=b.getString("adviser_id");
                    consultant_text.setText(accountname);
                }
                break;
            case REQUESTXMCODE:
                //选择项目
                if(data!=null){
                    Bundle b=data.getExtras(); //data为B中回传的Intent
                    projectIds = b.getString("projectIds");
                    select_product_text.setText(b.getString("projectNames"));
                }
                break;
            default:
                break;
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

}
