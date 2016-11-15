package com.drjing.xibao.module.application.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.library.third.ormlite.dao.Dao;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.view.materialwidget.PaperButton;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.config.AppConfig;
import com.drjing.xibao.module.entity.UserParam;
import com.drjing.xibao.module.ui.UIHelper;
import com.drjing.xibao.provider.db.DatabaseHelper;
import com.drjing.xibao.provider.db.entity.User;
import com.kristain.common.security.SHA1;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * 修改密码
 * Created by kristain on 15/12/29.
 */
public class ModifyPwdActivity extends SwipeBackActivity {

    /**
     * 后退返回按钮
     */
    private Button btnBack;
    /**
     * 标题栏文案
     */
    private TextView textHeadTitle;


    private PaperButton submit_button;

    private EditText old_passwd,new_passwd,comfirm_passwd;

    private DatabaseHelper dbHelper;
    private Dao<User, String> userDao;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);
        dbHelper = DatabaseHelper.gainInstance(this, AppConfig.DB_NAME, AppConfig.DB_VERSION);
        try {
            userDao = (Dao<User, String>)dbHelper.getDao(User.class);
            List<User> users = userDao.queryBuilder().query();
            if(users==null || users.size()==0 || StringUtils.isEmpty(users.get(0).getId())){
                UIHelper.showLogin(this);
                return;
            }
            user = users.get(0);
        } catch (SQLException e) {
            e.printStackTrace();
            UIHelper.showLogin(this);
        }
        initView();
        initEvent();
    }

    private void initView() {
        btnBack = (Button)findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("修改密码");

        submit_button = (PaperButton) findViewById(R.id.submit_button);
        old_passwd = (EditText)findViewById(R.id.old_passwd);
        new_passwd = (EditText)findViewById(R.id.new_passwd);
        comfirm_passwd = (EditText)findViewById(R.id.comfirm_passwd);

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
         * 提交修改密码
         */
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validParam()){
                    doModifyPwd();
                }
            }
        });
    }

    /**
     * 提交参数判断
     * @return
     */
    private boolean validParam(){
        if(StringUtils.isEmpty(old_passwd.getText().toString())
                ||StringUtils.isEmpty(new_passwd.getText().toString())
                ||StringUtils.isEmpty(comfirm_passwd.getText().toString())){
            Toast.makeText(ModifyPwdActivity.this,"密码不能为空",Toast.LENGTH_LONG).show();
            return false;
        }

        if(!new_passwd.getText().toString().equals(comfirm_passwd.getText().toString())){
            Toast.makeText(ModifyPwdActivity.this,"新密码不一致",Toast.LENGTH_LONG).show();
           return false;
        }
        return true;
    }

    /**
     * 修改密码
     */
    private void doModifyPwd(){
        UserParam param = new UserParam();
        param.setCurrent_password(SHA1.encrypt(old_passwd.getText().toString()));
        param.setPassword(SHA1.encrypt(new_passwd.getText().toString()));
        param.setConfirm_password(SHA1.encrypt(comfirm_passwd.getText().toString()));
        HttpClient.postModifyPwd(param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String body) {
                JSONObject object = JSON.parseObject(body);
                if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                    Toast.makeText(ModifyPwdActivity.this, "修改密码成功", Toast.LENGTH_LONG).show();
                    //TODO 修改数据库中的密码
                    user.setPassword(new_passwd.getText().toString());
                    try {
                        userDao.deleteBuilder().delete();
                        userDao.create(user);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    submit_button.setClickable(true);
                    finish();
                } else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))){
                    UIHelper.showLogin(ModifyPwdActivity.this);
                }else {
                    Log.i("postModifyPwdTAG", "成功返回数据:" + body);
                    Toast.makeText(ModifyPwdActivity.this, "修改密码失败:"+body, Toast.LENGTH_LONG).show();
                    submit_button.setClickable(true);
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(ModifyPwdActivity.this, "修改密码失败:"+request.toString(), Toast.LENGTH_LONG).show();
                Log.i("postModifyPwdTAG", "失败返回数据:" + request.toString());
                submit_button.setClickable(true);
            }
        }, ModifyPwdActivity.this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != dbHelper) dbHelper.releaseAll();
    }
}
