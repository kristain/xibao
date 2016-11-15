package com.drjing.xibao.module.workbench.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.library.third.ormlite.dao.Dao;
import com.drjing.xibao.R;
import com.drjing.xibao.config.AppConfig;
import com.drjing.xibao.module.entity.RoleEnum;
import com.drjing.xibao.module.ui.UIHelper;
import com.drjing.xibao.provider.db.DatabaseHelper;
import com.drjing.xibao.provider.db.entity.User;
import com.kristain.common.utils.StringUtils;

import java.sql.SQLException;
import java.util.List;

/**
 * 工作台Fragment
 * Created by kristain on 15/12/21.
 */
public class WorkbenchFragment extends Fragment {

    private Activity context;
    private View root;
    private TextView textHeadTitle;


    private DatabaseHelper dbHelper;
    private Dao<User, String> userDao;
    private User user;

    private final static int  REQUESTCODE=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return root = inflater.inflate(R.layout.fragment_workbench, container, false);
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
        try {
            dbHelper = DatabaseHelper.gainInstance(context, AppConfig.DB_NAME, AppConfig.DB_VERSION);
            userDao = (Dao<User, String>) dbHelper.getDao(User.class);
            List<User> users = userDao.queryBuilder().query();
            if (users == null || users.size() == 0 || StringUtils.isEmpty(users.get(0).getId())) {
                UIHelper.showLogin(context);
                return;
            }
            user = users.get(0);
        } catch (SQLException e) {
            e.printStackTrace();
            UIHelper.showLogin(context);
        }
    }

    void initView() {
        textHeadTitle = (TextView) root.findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("工作台");

        //我的订单
        root.findViewById(R.id.myorder_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(RoleEnum.AREAMANAGER.getCode().equals(user.getRoleKey())||RoleEnum.BOSS.getCode().equals(user.getRoleKey())){
                    Bundle bundle = new Bundle();
                    bundle.putString("user_id",user.getId());
                    UIHelper.showOrderSearch(getActivity(),bundle);
                }else{
                    UIHelper.showMyOrderList(getActivity());
                }
            }
        });

        //客户管理
        root.findViewById(R.id.customer_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("rolekey",user.getRoleKey());
                bundle.putString("user_id", user.getId());
                bundle.putString("company_id",user.getCompany_id());
                if(RoleEnum.CONSULTANT.getCode().equals(user.getRoleKey())){
                    UIHelper.showAdviserCustomerList(getActivity(),bundle);
                }else if(RoleEnum.STAFF.getCode().equals(user.getRoleKey())){
                    UIHelper.showCustomerList(getActivity());
                }else if(RoleEnum.STOREMANAGER.getCode().equals(user.getRoleKey())){
                    UIHelper.showStoreCustomerList(getActivity(), bundle);
                } else {
                    UIHelper.showCustomerStoreList(getActivity());
                }
            }
        });

        //行动计划表
        root.findViewById(R.id.action_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RoleEnum.STAFF.getCode().equals(user.getRoleKey())) {
                    UIHelper.showActionPlan(getActivity());
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("rolekey",user.getRoleKey());
                    bundle.putString("user_id", user.getId());
                    UIHelper.showLeaderActionPlan(getActivity(),bundle);
                }
            }
        });

        //工作日报
        root.findViewById(R.id.work_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //根据角色跳转到不同的页面
                if (RoleEnum.STAFF.getCode().equals(user.getRoleKey())) {
                    UIHelper.showReport(getActivity());
                } else {
                    UIHelper.showReportStore(getActivity());
               }
            }
        });

        //日程
        root.findViewById(R.id.schedule_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showMonthSchedule(getActivity());
            }
        });

    }

    private void initData() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null != dbHelper) dbHelper.releaseAll();
    }
}
