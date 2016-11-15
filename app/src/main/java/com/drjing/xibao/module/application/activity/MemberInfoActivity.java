package com.drjing.xibao.module.application.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.library.third.ormlite.dao.Dao;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.config.AppConfig;
import com.drjing.xibao.module.entity.RoleEnum;
import com.drjing.xibao.module.entity.TargetEntity;
import com.drjing.xibao.module.performance.adapter.BaseAdapterHelper;
import com.drjing.xibao.module.ui.UIHelper;
import com.drjing.xibao.provider.db.DatabaseHelper;
import com.drjing.xibao.provider.db.entity.User;
import com.drjing.xibao.upgrade.AppVersionChecker;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by kristain on 15/12/29.
 */
public class MemberInfoActivity extends SwipeBackActivity {

    /**
     * 后退返回按钮
     */
    private Button btnBack;
    /**
     * 标题栏文案
     */
    private TextView textHeadTitle;

    private RelativeLayout person_info_layout,question_layout,about_layout,
            versioncheck_layout,passwd_layout,company_layout;

    private DatabaseHelper dbHelper;

    private Dao<User, String> userDao;

    private User user;

    private ImageView logo,role_logo;
    private TextView name,tel,shopname,rolename,logout_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            dbHelper = DatabaseHelper.gainInstance(this, AppConfig.DB_NAME, AppConfig.DB_VERSION);
            userDao = (Dao<User, String>)dbHelper.getDao(User.class);
            List<User> users = userDao.queryBuilder().query();
            if(users==null || users.size()==0 || StringUtils.isEmpty(users.get(0).getId())){
                UIHelper.showLogin(this);
                return;
            }
            user = users.get(0);
            name.setText(user.getUsername());
            tel.setText(user.getMobile());
            shopname.setText(user.getStore_name());
            rolename.setText(RoleEnum.getMsgByCode(user.getRoleKey()));
            if(!StringUtils.isEmpty(user.getAvatar())){
                Picasso.with(this).load(BaseAdapterHelper.getURLWithSize(user.getAvatar()))
                        .placeholder(R.drawable.user_logo)
                        .error(R.drawable.user_logo)
                        .tag(this)
                        .into(logo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            UIHelper.showLogin(this);
        }
    }

    private void initView() {
        btnBack = (Button)findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("个人中心");
        person_info_layout = (RelativeLayout)findViewById(R.id.person_info_layout);
        company_layout = (RelativeLayout)findViewById(R.id.company_layout);
        passwd_layout = (RelativeLayout)findViewById(R.id.passwd_layout);
        versioncheck_layout = (RelativeLayout)findViewById(R.id.versioncheck_layout);
        about_layout = (RelativeLayout)findViewById(R.id.about_layout);
        question_layout= (RelativeLayout)findViewById(R.id.question_layout);

        logout_btn = (TextView)findViewById(R.id.logout_btn);

        logo = (ImageView)findViewById(R.id.logo);
        role_logo= (ImageView)findViewById(R.id.role_logo);
        name= (TextView)findViewById(R.id.name);
        tel= (TextView)findViewById(R.id.tel);
        shopname= (TextView)findViewById(R.id.shopname);
        rolename= (TextView)findViewById(R.id.rolename);

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

        person_info_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        /**
         * 点击退出
         */
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    userDao.deleteBuilder().delete();
                    UIHelper.showLogin(MemberInfoActivity.this);
                } catch (SQLException e) {
                    e.printStackTrace();
                    UIHelper.showLogin(MemberInfoActivity.this);
                }
            }
        });

        //我的公司
        company_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showMyCompany(MemberInfoActivity.this);
            }
        });


        //修改密码
        passwd_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showModifyPwd(MemberInfoActivity.this);
            }
        });

        //版本更新
        versioncheck_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpClient.checkVersion(new TargetEntity(), new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String body) {
                        Log.i("checkVersionTAG", "成功返回数据:" + body);
                        JSONObject object = JSON.parseObject(body);
                        if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                            JSONObject data = JSON.parseObject(object.getString("data"));
                            final String url = data.getString("url");
                            if (AppVersionChecker.getLocalVersion(MemberInfoActivity.this) >= Integer.parseInt(data.getString("build"))) {
                                Toast.makeText(MemberInfoActivity.this,"已是最新版本",Toast.LENGTH_LONG).show();
                            }else{
                                Dialog noticeDialog;
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
                                        MemberInfoActivity.this);// Builder，可以通过此builder设置改变AleartDialog的默认的主题样式及属性相关信息
                                builder.setTitle("版本升级");
                                builder.setMessage("检测到有新版本，是否马上更新？");

                                builder.setPositiveButton("下载",  new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();// 当取消对话框后进行操作一定的代码？取消对话框
                                        AppVersionChecker.downLoadApk(MemberInfoActivity.this, url);
                                    }
                                });
                                builder.setNegativeButton("以后再说", new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                noticeDialog = builder.create();
                                noticeDialog.show();
                                /*ToolAlert.dialog(MemberInfoActivity.this, "版本升级", "检测到有新版本，是否马上更新？",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                try {
                                                    AppVersionChecker.downLoadApk(MemberInfoActivity.this, "");
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });*/
                            }
                        } else if (HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                            UIHelper.showLogin(MemberInfoActivity.this);
                        } else {
                            Log.i("checkVersionTAG", "获取数据失败:" + body);
                        }
                    }

                    @Override
                    public void onFailure(Request request, IOException e) {
                        Log.i("checkVersionTAG", "失败返回数据:" + request.toString());
                    }
                }, MemberInfoActivity.this);
            }
        });
        //关于喜报
        about_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showAboutUS(MemberInfoActivity.this);
            }
        });
        //问题反馈
        question_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showQuestion(MemberInfoActivity.this);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != dbHelper) dbHelper.releaseAll();
    }
}
