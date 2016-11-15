package com.drjing.xibao.module.news.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.library.third.ormlite.dao.Dao;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.view.circularprogressbar.CircleBar;
import com.drjing.xibao.common.view.pulltozoomview.PullToZoomScrollViewEx;
import com.drjing.xibao.config.AppConfig;
import com.drjing.xibao.module.entity.OrderTypeEnum;
import com.drjing.xibao.module.entity.RoleEnum;
import com.drjing.xibao.module.performance.adapter.BaseAdapterHelper;
import com.drjing.xibao.module.ui.UIHelper;
import com.drjing.xibao.provider.db.DatabaseHelper;
import com.drjing.xibao.provider.db.entity.User;
import com.kristain.common.utils.DateTimeUtils;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

/**
 * 动态页面Fragment
 * Created by kristain on 15/12/21.
 */
public class NewsFragment extends Fragment {

    private Activity context;
    private View root;
    private PullToZoomScrollViewEx scrollView;
    private RelativeLayout header_view;
    private CircleBar circleBar;
    private ImageView iv_user_head;

    private Bundle bundle = new Bundle();

    private DatabaseHelper dbHelper;
    private Dao<User, String> userDao;
    private User user;

    private TextView account_name, role_name, date_text, month_rate;
    private ImageView logo;

    private LinearLayout account_layout;

    Calendar cal = Calendar.getInstance();
    private static final String TAG = "NewsFragment";

    private TextView order_remark_text, sale_log_text, customer_text, new_order_text,
            service_log_text, notice_log_text, return_log_text, activate_text, report_text, schedule_text;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return root = inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        initData();
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        dbHelper = DatabaseHelper.gainInstance(context, AppConfig.DB_NAME, AppConfig.DB_VERSION);
        try {
            userDao = (Dao<User, String>) dbHelper.getDao(User.class);
            List<User> users = userDao.queryBuilder().query();
            if (users == null || users.size() == 0 || StringUtils.isEmpty(users.get(0).getId())) {
                UIHelper.showLogin(context);
                return;
            }
            user = users.get(0);
            if (RoleEnum.CONSULTANT.getCode().equals(user.getRoleKey())) {
                scrollView.getPullRootView().findViewById(R.id.order_remark_layout).setVisibility(View.VISIBLE);
                scrollView.getPullRootView().findViewById(R.id.sale_log_layout).setVisibility(View.VISIBLE);
                scrollView.getPullRootView().findViewById(R.id.customer_layout).setVisibility(View.VISIBLE);
                scrollView.getPullRootView().findViewById(R.id.new_order_layout).setVisibility(View.GONE);
                scrollView.getPullRootView().findViewById(R.id.service_log_layout).setVisibility(View.GONE);
                scrollView.getPullRootView().findViewById(R.id.notice_log_layout).setVisibility(View.GONE);
                scrollView.getPullRootView().findViewById(R.id.return_log_layout).setVisibility(View.GONE);
                scrollView.getPullRootView().findViewById(R.id.activate_customers_layout).setVisibility(View.GONE);
            } else if (RoleEnum.STOREMANAGER.getCode().equals(user.getRoleKey())) {
                scrollView.getPullRootView().findViewById(R.id.order_remark_layout).setVisibility(View.VISIBLE);
                scrollView.getPullRootView().findViewById(R.id.customer_layout).setVisibility(View.VISIBLE);
                scrollView.getPullRootView().findViewById(R.id.sale_log_layout).setVisibility(View.GONE);
                scrollView.getPullRootView().findViewById(R.id.new_order_layout).setVisibility(View.GONE);
                scrollView.getPullRootView().findViewById(R.id.service_log_layout).setVisibility(View.GONE);
                scrollView.getPullRootView().findViewById(R.id.notice_log_layout).setVisibility(View.GONE);
                scrollView.getPullRootView().findViewById(R.id.return_log_layout).setVisibility(View.GONE);
                scrollView.getPullRootView().findViewById(R.id.activate_customers_layout).setVisibility(View.GONE);
            } else if (RoleEnum.STAFF.getCode().equals(user.getRoleKey())) {
                scrollView.getPullRootView().findViewById(R.id.order_remark_layout).setVisibility(View.GONE);
                scrollView.getPullRootView().findViewById(R.id.customer_layout).setVisibility(View.GONE);
                scrollView.getPullRootView().findViewById(R.id.sale_log_layout).setVisibility(View.GONE);
                scrollView.getPullRootView().findViewById(R.id.new_order_layout).setVisibility(View.VISIBLE);
                scrollView.getPullRootView().findViewById(R.id.service_log_layout).setVisibility(View.VISIBLE);
                scrollView.getPullRootView().findViewById(R.id.notice_log_layout).setVisibility(View.VISIBLE);
                scrollView.getPullRootView().findViewById(R.id.return_log_layout).setVisibility(View.VISIBLE);
                scrollView.getPullRootView().findViewById(R.id.activate_customers_layout).setVisibility(View.VISIBLE);
            } else {
                scrollView.getPullRootView().findViewById(R.id.order_remark_layout).setVisibility(View.GONE);
                scrollView.getPullRootView().findViewById(R.id.customer_layout).setVisibility(View.GONE);
                scrollView.getPullRootView().findViewById(R.id.sale_log_layout).setVisibility(View.GONE);
                scrollView.getPullRootView().findViewById(R.id.new_order_layout).setVisibility(View.GONE);
                scrollView.getPullRootView().findViewById(R.id.service_log_layout).setVisibility(View.GONE);
                scrollView.getPullRootView().findViewById(R.id.notice_log_layout).setVisibility(View.GONE);
                scrollView.getPullRootView().findViewById(R.id.return_log_layout).setVisibility(View.GONE);
                scrollView.getPullRootView().findViewById(R.id.activate_customers_layout).setVisibility(View.GONE);
            }
            account_name.setText(user.getUsername());
            role_name.setText(RoleEnum.getMsgByCode(user.getRoleKey()));
            if (!StringUtils.isEmpty(user.getLogo())) {
                Picasso.with(getActivity()).load(BaseAdapterHelper.getURLWithSize(user.getLogo()))
                        .placeholder(R.drawable.icon_head)
                        .error(R.drawable.icon_head)
                        .tag(getActivity())
                        .into(logo);
            }
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
            UIHelper.showLogin(context);
        }
    }

    void initView() {
        scrollView = (PullToZoomScrollViewEx) root.findViewById(R.id.scrollView);
        View headView = LayoutInflater.from(context).inflate(R.layout.new_head_view, null, false);
        View zoomView = LayoutInflater.from(context).inflate(R.layout.new_zoom_view, null, false);
        View contentView = LayoutInflater.from(context).inflate(R.layout.new_content_view, null, false);
        scrollView.setHeaderView(headView);
        scrollView.setZoomView(zoomView);
        scrollView.setScrollContentView(contentView);

        logo = (ImageView) scrollView.getHeaderView().findViewById(R.id.logo);
        account_name = (TextView) scrollView.getHeaderView().findViewById(R.id.account_name);
        role_name = (TextView) scrollView.getHeaderView().findViewById(R.id.role_name);
        date_text = (TextView) scrollView.getHeaderView().findViewById(R.id.date_text);
        month_rate = (TextView) scrollView.getHeaderView().findViewById(R.id.month_rate);
        account_layout = (LinearLayout) scrollView.getHeaderView().findViewById(R.id.account_layout);

        order_remark_text = (TextView) scrollView.getPullRootView().findViewById(R.id.order_remark_text);
        sale_log_text = (TextView) scrollView.getPullRootView().findViewById(R.id.sale_log_text);
        customer_text = (TextView) scrollView.getPullRootView().findViewById(R.id.customer_text);
        new_order_text = (TextView) scrollView.getPullRootView().findViewById(R.id.new_order_text);
        service_log_text = (TextView) scrollView.getPullRootView().findViewById(R.id.service_log_text);
        notice_log_text = (TextView) scrollView.getPullRootView().findViewById(R.id.notice_log_text);
        return_log_text = (TextView) scrollView.getPullRootView().findViewById(R.id.return_log_text);
        activate_text = (TextView) scrollView.getPullRootView().findViewById(R.id.activate_text);
        report_text = (TextView) scrollView.getPullRootView().findViewById(R.id.report_text);
        schedule_text = (TextView) scrollView.getPullRootView().findViewById(R.id.schedule_text);


        date_text.setText("离" + (cal.get(Calendar.MONTH) + 1) + "月结束还剩" + DateTimeUtils.getDayOfMonth() + "天");
        month_rate.setText((cal.get(Calendar.MONTH) + 1) + "月完成率");

        iv_user_head = (ImageView) scrollView.getHeaderView().findViewById(R.id.iv_user_head);
        circleBar = (CircleBar) scrollView.getPullRootView().findViewById(R.id.circle);



        //顾问角色 点击订单备注
        scrollView.getPullRootView().findViewById(R.id.order_remark_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("order_type", OrderTypeEnum.UNREMARK.getCode());
                bundle.putString("user_id",user.getId());
                bundle.putString("accountname",user.getUsername());
                UIHelper.showOrderList(context, bundle);
            }
        });

        //顾问角色 销售日志备注
        scrollView.getPullRootView().findViewById(R.id.sale_log_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("order_type", OrderTypeEnum.SALELOG.getCode());
                bundle.putString("user_id",user.getId());
                UIHelper.showOrderList(context, bundle);
            }
        });

        //顾问角色 未分配客户
        scrollView.getPullRootView().findViewById(R.id.customer_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("rolekey", user.getRoleKey());
                bundle.putString("user_id", user.getId());
                bundle.putString("company_id", user.getCompany_id());
                bundle.putString("store_id",user.getStore_id());
                UIHelper.showUnAssignCustomList(context, bundle);
            }
        });

        //点击新增订单
        scrollView.getPullRootView().findViewById(R.id.new_order_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("order_type", OrderTypeEnum.NEWORDER.getCode());
                bundle.putString("user_id",user.getId());
                UIHelper.showOrderList(context, bundle);
            }
        });
        //点击服务日志
        scrollView.getPullRootView().findViewById(R.id.service_log_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("order_type", OrderTypeEnum.SERVICEORDER.getCode());
                bundle.putString("user_id",user.getId());
                UIHelper.showOrderList(context, bundle);
            }
        });
        //点击提醒日志
        scrollView.getPullRootView().findViewById(R.id.notice_log_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("order_type", OrderTypeEnum.REMINDORDER.getCode());
                bundle.putString("user_id",user.getId());
                UIHelper.showOrderList(context, bundle);
            }
        });
        //点击回访日志
        scrollView.getPullRootView().findViewById(R.id.return_log_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("order_type", OrderTypeEnum.REVISITORDER.getCode());
                bundle.putString("user_id",user.getId());
                UIHelper.showOrderList(context, bundle);
            }
        });

        //点击待激活客户
        scrollView.getPullRootView().findViewById(R.id.activate_customers_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // UIHelper.showActivateCycle(context);
                bundle.putString("days", "");
                UIHelper.showUnActivateCustomList(context, bundle);
            }
        });

        iv_user_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //点击日程
        scrollView.getPullRootView().findViewById(R.id.schedule_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showSchedule(context);
            }
        });

        //点击日报
        scrollView.getPullRootView().findViewById(R.id.report_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RoleEnum.STOREMANAGER.getCode().equals(user.getRoleKey())) {
                    Bundle bundle = new Bundle();
                    //TODO
                    bundle.putString("storeid", user.getStore_id());
                    bundle.putString("role_key",user.getRoleKey());
                    UIHelper.showStoreReportList(context, bundle);
                } else if (RoleEnum.STAFF.getCode().equals(user.getRoleKey()) || RoleEnum.CONSULTANT.getCode().equals(user.getRoleKey())) {
                    UIHelper.showReport(context);
                } else {
                    UIHelper.showReportStore(context);
                }
            }
        });

        /**
         * 点击用户名
         */
        account_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showMemberInfo(context);
            }
        });


    }

    private void initData() {

    }

    private void loadData() {
        HttpClient.getHomePageData(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String body) {
                Log.i(TAG, "getHomePageDataTAG返回数据:" + body);
                JSONObject object = JSON.parseObject(body);
                if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                    JSONObject data = JSON.parseObject(object.getString("data"));
                    order_remark_text.setText("您还有" + data.getString("countOrderNote") + "个订单未备注");
                    sale_log_text.setText("您还有" + data.getString("countSaleLog") + "个销售日志未确认");
                    customer_text.setText("您还有" + data.getString("countNotAssigned") + "个客户未分配");
                    new_order_text.setText("您还有" + data.getString("countNewOrder") + "个订单还未服务");
                    service_log_text.setText("您还有" + data.getString("countService") + "位客户还未填写服务日志");
                    notice_log_text.setText("您还有" + data.getString("countRemind") + "个客户还未提醒到店");
                    return_log_text.setText("您还有" + data.getString("countVisit") + "个已完成订单还未回访");
                    activate_text.setText("您有" + data.getString("countSleepCustomer") + "个睡眠客未激活");
                    report_text.setText("今日已有" + data.getString("countDaliy") + "人提交了日报");
                    schedule_text.setText("今日有" + data.getString("countSchedule") + "个日程");
                    //完成率
                    final String completion =StringUtils.isEmpty(data.getString("completion"))?"0":data.getString("completion");
                    circleBar.setSweepAngle((float)Double.parseDouble(completion)*360/100);
                    //circleBar.startCustomAnimation();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            circleBar.setText(Math.round(Double.parseDouble(completion))+"");
                        }
                    }, 500);

                } else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))){
                    UIHelper.showLogin(context);
                }else {
                    Toast.makeText(getActivity(), object.getString("msg"), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                Log.i(TAG, "getHomePageDataTAG失败返回数据:" + request.toString());
            }
        }, getActivity());

    }
}
