package com.drjing.xibao.module.workbench.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.drjing.xibao.R;
import com.drjing.xibao.common.view.materialwidget.PaperButton;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.kristain.common.utils.StringUtils;

/**
 * 客户筛选
 * Created by kristain on 16/1/4.
 */
public class CustomerSearchActivity extends SwipeBackActivity {

    /**
     * 后退返回按钮
     */
    private Button btnBack;
    /**
     * 标题栏文案
     */
    private TextView textHeadTitle;

    private PaperButton submit_btn;

    private EditText name,mobile;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_search);
        initView();
        initEvent();
    }

    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("客户筛选");
        submit_btn = (PaperButton)findViewById(R.id.submit_btn);
        name = (EditText)findViewById(R.id.name);
        mobile= (EditText)findViewById(R.id.mobile);
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
         * 点击查询
         */
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!StringUtils.isEmpty(name.getText().toString()) || !StringUtils.isEmpty(mobile.getText().toString())){
                    Intent intent = getIntent();
                    intent.putExtra("name", name.getText().toString().trim());
                    intent.putExtra("mobile", mobile.getText().toString().trim());
                    setResult(RESULT_OK,intent);
                }
                finish();
            }
        });

    }



}
