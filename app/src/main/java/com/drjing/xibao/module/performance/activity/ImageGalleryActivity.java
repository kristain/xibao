package com.drjing.xibao.module.performance.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.drjing.xibao.R;
import com.drjing.xibao.common.view.photoview.PhotoViewAdapter;
import com.drjing.xibao.common.view.photoview.PhotoViewPager;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kristain on 15/12/21.
 */
public class ImageGalleryActivity extends SwipeBackActivity {
    private int position;
    private List<String> imgUrls; //图片列表
    private TextView headTitle;
    private Button headBackBtn;

    private PhotoViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_gallery);

        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        imgUrls = intent.getStringArrayListExtra("images");
        if(imgUrls == null) {
            imgUrls = new ArrayList<>();
        }
        initView();
        initViewEvent();
        initGalleryViewPager();
    }

    private void initView() {
        headTitle = (TextView)findViewById(R.id.textHeadTitle);
        headTitle.setText("1/" + imgUrls.size());
        headBackBtn = (Button)findViewById(R.id.btnBack);
        headBackBtn.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initViewEvent() {
        headBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initGalleryViewPager() {
        PhotoViewAdapter pagerAdapter = new PhotoViewAdapter(this, imgUrls);
        pagerAdapter.setOnItemChangeListener(new PhotoViewAdapter.OnItemChangeListener() {
            int len = imgUrls.size();
            @Override
            public void onItemChange(int currentPosition) {
                headTitle.setText((currentPosition+1) + "/" + len);
            }
        });
        mViewPager = (PhotoViewPager)findViewById(R.id.viewer);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(position);
    }
}
