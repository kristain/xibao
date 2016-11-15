package com.drjing.xibao.module.application.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.drjing.xibao.R;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;

/**
 * 关于我们
 * Created by kristain on 15/12/29.
 */
public class AboutUsActivity extends SwipeBackActivity {

    /**
     * 后退返回按钮
     */
    private Button btnBack;
    /**
     * 标题栏文案
     */
    private TextView textHeadTitle;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        initView();
        initEvent();
    }

    private void initView() {
        btnBack = (Button)findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("关于我们");
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
