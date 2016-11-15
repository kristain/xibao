package com.drjing.xibao.module.news.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.library.third.ormlite.dao.Dao;
import com.drjing.xibao.GlobalApplication;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.config.AppConfig;
import com.drjing.xibao.module.entity.UserParam;
import com.drjing.xibao.provider.db.DatabaseHelper;
import com.drjing.xibao.provider.db.entity.User;
import com.kristain.common.security.SHA1;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 登录页面
 * Created by kristain on 15/12/21.
 */
public class LoginActivity extends SwipeBackActivity {
    /**
     * 登录按钮
     */
    private Button btnSure;

    private UserParam param;

    private DatabaseHelper dbHelper;

    private Dao<User, String> userDao;

    private User user;

    private EditText phone, passwd;

    /***
     * 整个应用Applicaiton
     **/
    private GlobalApplication mApplication = null;
    private boolean isQuit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //获取应用Application
        mApplication = GlobalApplication.getInstance();
        initView();
        initEvent();
        dbHelper = DatabaseHelper.gainInstance(this, AppConfig.DB_NAME, AppConfig.DB_VERSION);
        dbHelper.createTable(User.class);
        try {
            userDao = (Dao<User, String>) dbHelper.getDao(User.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        btnSure = (Button) findViewById(R.id.btnSure);
        phone = (EditText) findViewById(R.id.phone);
        passwd = (EditText) findViewById(R.id.passwd);
    }

    private void initEvent() {
        /**
         *
         */
        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("登录TAG", "登录操作");
                if (validForm()) {
                    btnSure.setClickable(false);
                    param = new UserParam();
                    param.setAccountname(phone.getText().toString());
                    param.setPassword(SHA1.encrypt(passwd.getText().toString()));
//                    param.setPassword(passwd.getText().toString());
                    HttpClient.postLogin(param, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(String body) {
                            Log.i("登录TAG", "成功返回数据:" + body);
                            JSONObject object = JSON.parseObject(body);
                            if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                                JSONObject data = JSON.parseObject(object.getString("data"));
                                JSONObject company = JSON.parseObject(data.getString("company"));
                                user = new User();
                                user.setId(data.getString("id"));
                                user.setUsername(data.getString("username"));
                                user.setAccountname(data.getString("accountname"));
                                user.setSex(data.getString("sex"));
                                user.setStore_name(data.getString("store_name"));
                                user.setStore_id(data.getString("store_id"));
                                user.setRoleKey(data.getString("roleKey"));
                                user.setAvatar(data.getString("avatar"));
                                user.setMobile(data.getString("mobile"));
                                user.setCompany_id(data.getString("company_id"));
                                user.setLogo(company.getString("logo"));
                                try {
                                    if(!dbHelper.isOpen()){
                                        dbHelper = DatabaseHelper.gainInstance(LoginActivity.this, AppConfig.DB_NAME, AppConfig.DB_VERSION);
                                        userDao = (Dao<User, String>) dbHelper.getDao(User.class);
                                    }
                                    userDao.deleteBuilder().delete();
                                    userDao.create(user);
                                    //List<User> users = userDao.queryBuilder().query();
                                    //Log.i("登录TAG", "users.size:"+users.size()+" "+users.get(0).getAccountname());
                                } catch (SQLException e) {
                                    e.printStackTrace();

                                }
                                btnSure.setClickable(true);
                                finish();
                            } else {
                                Log.i("登录TAG", "登录失败返回数据:" + body);
                                Toast.makeText(LoginActivity.this, object.getString("msg"), Toast.LENGTH_LONG).show();
                                btnSure.setClickable(true);
                            }
                        }

                        @Override
                        public void onFailure(Request request, IOException e) {
                            Toast.makeText(LoginActivity.this, request.toString(), Toast.LENGTH_LONG).show();
                            Log.i("登录TAG", "失败返回数据:" + request.toString());
                            btnSure.setClickable(true);
                        }
                    }, LoginActivity.this);
                }
            }
        });
    }

    /**
     * 表单提交
     */
    private boolean validForm() {
        if (StringUtils.isEmpty(phone.getText().toString())) {
            Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_LONG).show();
            return false;
        }

        if (StringUtils.isEmpty(passwd.getText().toString())) {
            Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != dbHelper) dbHelper.releaseAll();
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
