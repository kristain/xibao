package com.drjing.xibao;

import com.drjing.xibao.common.AppException;
import com.drjing.xibao.common.MApplication;

/**
 * Created by kristain on 15/12/20.
 */
public class GlobalApplication extends MApplication {

    private static GlobalApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
        registerUncaughtExceptionHandler();
    }

    public GlobalApplication() {
        app = this;
    }

    public static synchronized GlobalApplication getInstance() {
        if (app == null) {
            app = new GlobalApplication();
        }
        return app;
    }

    /**
     * 退出APP时手动调用
     */
    @Override
    public void exit() {
        try {
            //关闭所有Activity
            removeAll();
            //退出进程
            System.exit(0);
        } catch (Exception e) {
        }
    }


    // 注册App异常崩溃处理器
    private void registerUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
    }
}
