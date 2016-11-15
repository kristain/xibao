package com.drjing.xibao.module.application.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.drjing.xibao.R;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;

/**
 * 产品信息列表
 * Created by kristain on 16/1/3.
 */
public class ProductListActivity extends SwipeBackActivity {


    /**
     * 后退返回按钮
     */
    private Button btnBack;
    /**
     * 标题栏文案
     */
    private TextView textHeadTitle;

    private ListView list_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        initView();
        initEvent();
    }

    private void initView() {
        btnBack = (Button)findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("产品信息");
        list_view = (ListView)findViewById(R.id.list_view);

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



    }
}
