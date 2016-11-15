package com.drjing.xibao.module;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.drjing.xibao.GlobalApplication;
import com.drjing.xibao.R;
import com.drjing.xibao.common.utils.SharedPreferencesUtils;
import com.drjing.xibao.common.view.viewpagerindicator.CirclePageIndicator;
import com.drjing.xibao.module.ui.UIHelper;

import java.lang.ref.WeakReference;

/**
 * Created by kristain on 15/12/20.
 */
public class SplashActivity extends FragmentActivity {

    /***整个应用Applicaiton**/
    private GlobalApplication mApplication = null;
    /**当前Activity的弱引用，防止内存泄露**/
    private WeakReference<Activity> context = null;

    /**
     * 进入主页按钮
     */
    private Button btnHome;
    private CirclePageIndicator indicator;
    private ViewPager pager;
    private GalleryPagerAdapter adapter;


    /**
     * 引导页面图片
     */
    private int[] guide_images = {
            R.drawable.newer1,
            R.drawable.newer2,
            R.drawable.newer3
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取应用Application
        mApplication = GlobalApplication.getInstance();
        //将当前Activity压入栈
        context = new WeakReference<Activity>(this);
        mApplication.pushTask(context);

        setContentView(R.layout.activity_splash);

        //判断是否是第一次进入该APP
        boolean firstTimeUse = SharedPreferencesUtils.getInstance().getBoolean("first-time-use", true);
        if(firstTimeUse) {
            initGuideGallery();
        } else {
            initLaunchLogo();
        }
    }

    /**
     * 0.5s后自动进入主页
     */
    private void initLaunchLogo() {
        ImageView guideImage = (ImageView) findViewById(R.id.guideImage);
        guideImage.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                UIHelper.showHome(SplashActivity.this);
            }
        }, 500);
    }

    /**
     * 进入引导页面
     */
    private void initGuideGallery() {
        final Animation fadeIn= AnimationUtils.loadAnimation(this, R.anim.fadein);
        btnHome = (Button) findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferencesUtils.getInstance().putBoolean("first-time-use", false);
                UIHelper.showHome(SplashActivity.this);
            }
        });

        indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        indicator.setVisibility(View.VISIBLE);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setVisibility(View.VISIBLE);

        adapter = new GalleryPagerAdapter();
        pager.setAdapter(adapter);
        indicator.setViewPager(pager);

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == guide_images.length - 1) {
                    btnHome.setVisibility(View.VISIBLE);
                    btnHome.startAnimation(fadeIn);
                } else {
                    btnHome.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public class GalleryPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return guide_images.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView item = new ImageView(SplashActivity.this);
            item.setScaleType(ImageView.ScaleType.CENTER_CROP);
            item.setImageResource(guide_images[position]);
            container.addView(item);
            return item;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }
    }

}
