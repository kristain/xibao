package com.drjing.xibao.module.workbench.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.drjing.xibao.R;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.module.ui.UIHelper;
import com.kristain.common.utils.StringUtils;

/**
 * 添加行动计划表
 * Created by kristain on 15/12/30.
 */
public class ActionPlanAddActivity extends SwipeBackActivity {

    /**
     * 后退按钮
     */
    private Button btnBack;

    /**
     * Title栏文案
     */
    private TextView textHeadTitle;


    private TextView customerName,staffName,adviser_name;

    private ImageView add_btn;

    private String customer_id="";

    private TextView btnRight;


    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_plan_add);
        bundle = getIntent().getExtras();
        initView();
        initEvent();
    }


    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("添加行动计划");
        customerName = (TextView)findViewById(R.id.customerName);
        staffName = (TextView)findViewById(R.id.staffName);
        adviser_name = (TextView)findViewById(R.id.adviser_name);
        add_btn = (ImageView)findViewById(R.id.add_btn);
        btnRight = (TextView)findViewById(R.id.btnRight);
        btnRight.setText("新增");
        btnRight.setVisibility(View.VISIBLE);
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
         * 点击选择客户
         */
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showMyCustomerList(ActionPlanAddActivity.this, 0,bundle);
            }
        });

        /**
         * 点击新增按钮
         */
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(StringUtils.isEmpty(customer_id)){
                    Toast.makeText(ActionPlanAddActivity.this,"请先选择客户",Toast.LENGTH_LONG).show();
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("customer_id",customer_id);
                UIHelper.showActionPlanList(ActionPlanAddActivity.this, bundle);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                //选择我的客户
                if(data!=null){
                    Bundle b=data.getExtras(); //data为B中回传的Intent
                    customer_id=b.getString("customer_id");//str即为回传的值
                    customerName.setText("顾客:"+StringUtils.formatCustomerName(b.getString("customername")));
                    staffName.setText("美容师:"+b.getString("staffname"));
                    adviser_name.setText("顾问:"+b.getString("adviser"));
                }
                break;
            default:
                break;
        }
    }
}
