package com.drjing.xibao.module;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.drjing.xibao.GlobalApplication;
import com.drjing.xibao.R;
import com.drjing.xibao.module.application.fragment.ApplicationFragment;
import com.drjing.xibao.module.news.fragment.NewsFragment;
import com.drjing.xibao.module.performance.fragment.PerformanceFragment;
import com.drjing.xibao.module.workbench.fragment.WorkbenchFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页
 * Created by kristain on 15/12/20.
 */
public class MainActivity extends BaseFragmentActivity {
    /***
     * 整个应用Applicaiton
     **/
    private GlobalApplication mApplication = null;

    public static final int SCROLL_VIEW_NEW = 0;
    public static final int SCROLL_VIEW_PERFORMANCE = 1;
    public static final int SCROLL_VIEW_WORKBENCH = 2;
    public static final int SCROLL_VIEW_APPLICATION = 3;
    public static int mCurSel = -1;
    private boolean isQuit;

    /**
     * 动态菜单
     */
    private RadioButton fbNews;
    /**
     * 业绩菜单
     */
    private RadioButton fbPerformance;
    /**
     * 工作台菜单
     */
    private RadioButton fbWorkbench;
    /**
     * 应用菜单
     */
    private RadioButton fbApplication;

    private Fragment newFragment = new NewsFragment();
    private Fragment performanceFragment = new PerformanceFragment();
    private Fragment workbenchFragment = new WorkbenchFragment();
    private Fragment applicationFragment = new ApplicationFragment();
    private List<Fragment> fragmentList;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        //获取应用Application
        mApplication = GlobalApplication.getInstance();
        mApplication.finishActivity(SplashActivity.class.getClass());
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        mCurSel = -1;
        initFragmentList();
        initFootBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = fragmentList.get(mCurSel);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initFragmentList() {
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(newFragment);
        fragmentList.add(performanceFragment);
        fragmentList.add(workbenchFragment);
        fragmentList.add(applicationFragment);
    }

    private void initFootBar() {
        fbNews = (RadioButton) findViewById(R.id.foot_bar_new);
        fbPerformance = (RadioButton) findViewById(R.id.foot_bar_performance);
        fbWorkbench = (RadioButton) findViewById(R.id.foot_bar_workbench);
        fbApplication = (RadioButton) findViewById(R.id.main_footbar_application);

        fbNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurPoint(SCROLL_VIEW_NEW);
            }
        });

        fbPerformance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurPoint(SCROLL_VIEW_PERFORMANCE);
            }
        });

        fbWorkbench.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurPoint(SCROLL_VIEW_WORKBENCH);
            }
        });

        fbApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurPoint(SCROLL_VIEW_APPLICATION);
            }
        });

        fbNews.performClick();
    }

    public void setCurPoint(int index) {
        if (mCurSel == index)
            return;
        mCurSel = index;
        addFragmentToStack();
        setFootBtnChecked();
    }

    private void setFootBtnChecked() {
        fbNews.setChecked(mCurSel == SCROLL_VIEW_NEW);
        fbPerformance.setChecked(mCurSel == SCROLL_VIEW_PERFORMANCE);
        fbWorkbench.setChecked(mCurSel == SCROLL_VIEW_WORKBENCH);
        fbApplication.setChecked(mCurSel == SCROLL_VIEW_APPLICATION);
    }

    private void addFragmentToStack() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (mCurSel == SCROLL_VIEW_NEW) {
            if (!newFragment.isAdded()) {
                fragmentTransaction.add(R.id.fragment_container, newFragment);
            }
        } else if (mCurSel == SCROLL_VIEW_PERFORMANCE) {
            if (!performanceFragment.isAdded()) {
                fragmentTransaction.add(R.id.fragment_container, performanceFragment);
            }
        } else if (mCurSel == SCROLL_VIEW_WORKBENCH) {
            if (!workbenchFragment.isAdded()) {
                fragmentTransaction.add(R.id.fragment_container, workbenchFragment);
            }
        } else if (mCurSel == SCROLL_VIEW_APPLICATION) {
            if (!applicationFragment.isAdded()) {
                fragmentTransaction.add(R.id.fragment_container, applicationFragment);
            }
        }
        toggleFragment(fragmentTransaction);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void toggleFragment(FragmentTransaction fragmentTransaction) {
        for (int i = 0; i < fragmentList.size(); i++) {
            Fragment f = fragmentList.get(i);
            if (i == mCurSel && f.isAdded()) {
                fragmentTransaction.show(f);
            } else if (f != null && f.isAdded() && f.isVisible()) {
                fragmentTransaction.hide(f);
            }
        }
    }

    public void switchFragment(int index) {
        if (index == SCROLL_VIEW_NEW) {
            fbNews.performClick();
            fbNews.post(new Runnable() {
                @Override
                public void run() {
                    fbNews.performClick();
                }
            });
        } else if (index == SCROLL_VIEW_PERFORMANCE) {
            fbPerformance.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            fbPerformance.performClick();
                        }
                    });
        } else if (index == SCROLL_VIEW_WORKBENCH) {
            fbWorkbench.post(new Runnable() {
                @Override
                public void run() {
                    fbWorkbench.performClick();
                }
            });
        } else if (index == SCROLL_VIEW_APPLICATION) {
            fbApplication.post(new Runnable() {
                @Override
                public void run() {
                    fbApplication.performClick();
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isQuit == false) {
                isQuit = true;
                Toast.makeText(getBaseContext(), R.string.exit_app, Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isQuit = false;
                    }
                }, 2000);
            } else {
                mApplication.exit();
            }
        }
        return true;
    }
}
