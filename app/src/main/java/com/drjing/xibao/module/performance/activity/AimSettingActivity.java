package com.drjing.xibao.module.performance.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.utils.FuncUtils;
import com.drjing.xibao.common.view.calendarview.CalendarDay;
import com.drjing.xibao.common.view.calendarview.CustomerDatePickerDialog;
import com.drjing.xibao.common.view.dialog.Effectstype;
import com.drjing.xibao.common.view.dialog.NiftyDialogBuilder;
import com.drjing.xibao.common.view.materialspinner.CalendarSpinner;
import com.drjing.xibao.common.view.materialspinner.NiceSpinner;
import com.drjing.xibao.common.view.materialwidget.PaperButton;
import com.drjing.xibao.common.view.pulltorefresh.PullToRefreshBase;
import com.drjing.xibao.common.view.pulltorefresh.PullToRefreshListView;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.module.entity.CategroyEntity;
import com.drjing.xibao.module.entity.CategroyEnum;
import com.drjing.xibao.module.entity.RoleEnum;
import com.drjing.xibao.module.entity.StoreEntity;
import com.drjing.xibao.module.entity.TargetEntity;
import com.drjing.xibao.module.performance.adapter.BaseAdapterHelper;
import com.drjing.xibao.module.performance.adapter.QuickAdapter;
import com.drjing.xibao.module.ui.UIHelper;
import com.kristain.common.utils.DateTimeUtils;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 目标设定
 * Created by kristain on 16/1/3.
 */
public class AimSettingActivity extends SwipeBackActivity {

    /**
     * 后退返回按钮
     */
    private Button btnBack;
    /**
     * 标题栏文案
     */
    private TextView textHeadTitle;

    private CalendarSpinner select_date;
    private NiceSpinner select_type, select_shop;

    CalendarDay calendar = CalendarDay.today();

    /**
     * 项目列表
     */
    List<CategroyEntity> categroylist;

    /**
     * 门店列表
     */
    List<StoreEntity> storelist;
    ArrayList<String> storeDataset = new ArrayList<String>();
    ArrayList<String> categroyDataset = new ArrayList<String>();


    private TextView role_name, user_name;
    /**
     * 下拉加载列表组件
     */
    private PullToRefreshListView listView;

    /**
     * 查询目标月份
     */
    private String month = DateTimeUtils.gainCurrentMonth();

    private int pno = 1;
    private boolean isLoadAll;
    List<TargetEntity> targetList = new ArrayList<TargetEntity>();

    QuickAdapter<TargetEntity> adapter;

    private RadioGroup select_year, select_month;


    private PaperButton submit_button;
    private EditText money;

    NiftyDialogBuilder dialogBuilder;


    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aimsetting);
        bundle = getIntent().getExtras();
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("目标设定");
        select_date = (CalendarSpinner) findViewById(R.id.select_date);
        select_date.setText(DateTimeUtils.gainCurrentMonth());
        select_type = (NiceSpinner) findViewById(R.id.select_type);
        select_shop = (NiceSpinner) findViewById(R.id.select_shop);
        submit_button = (PaperButton) findViewById(R.id.submit_button);
        money = (EditText) findViewById(R.id.money);

        role_name = (TextView) findViewById(R.id.role_name);
        user_name = (TextView) findViewById(R.id.user_name);
        role_name.setText(RoleEnum.getMsgByCode(bundle.getString("role_key") + ":"));
        user_name.setText(bundle.getString("accountname"));
        listView = (PullToRefreshListView) findViewById(R.id.listView);

        select_month = (RadioGroup) findViewById(R.id.select_month);
        select_year = (RadioGroup) findViewById(R.id.select_year);
        java.util.Date currTime = new java.util.Date();
        if (currTime.getMonth() == 0) {
            select_month.check(R.id.January);
        } else if (currTime.getMonth() == 1) {
            select_month.check(R.id.February);
        } else if (currTime.getMonth() == 2) {
            select_month.check(R.id.March);
        } else if (currTime.getMonth() == 3) {
            select_month.check(R.id.April);
        } else if (currTime.getMonth() == 4) {
            select_month.check(R.id.May);
        } else if (currTime.getMonth() == 5) {
            select_month.check(R.id.June);
        } else if (currTime.getMonth() == 6) {
            select_month.check(R.id.July);
        } else if (currTime.getMonth() == 7) {
            select_month.check(R.id.August);
        } else if (currTime.getMonth() == 8) {
            select_month.check(R.id.September);
        } else if (currTime.getMonth() == 9) {
            select_month.check(R.id.October);
        } else if (currTime.getMonth() == 10) {
            select_month.check(R.id.November);
        } else if (currTime.getMonth() == 11) {
            select_month.check(R.id.December);
        }

        adapter = new QuickAdapter<TargetEntity>(this, R.layout.target_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, final TargetEntity entity) {
                helper.setText(R.id.categroy_name, StringUtils.formatNull(entity.getCateName()))
                        .setText(R.id.money, FuncUtils.formatMoney2(entity.getAmount()) + "万");
                helper.getView().findViewById(R.id.delete_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder = NiftyDialogBuilder.getInstance(AimSettingActivity.this);
                        dialogBuilder
                                .withTitle("提示")
                                .withTitleColor("#FFFFFF")
                                .withDividerColor("#11000000")
                                .withMessage("确定删除该目标信息吗？")
                                .withMessageColor("#FFFFFFFF")
                                .withDialogColor("#FFE74C3C")
                                .withIcon(
                                        getResources().getDrawable(
                                                R.drawable.ic_favorite_white_48dp))
                                .isCancelableOnTouchOutside(true)
                                .withDuration(700)
                                .withEffect(Effectstype.Fliph)
                                .withButton1Text("确定")
                                .withButton2Text("结束")
                                .setCustomView(R.layout.dialog_effects_custom_view,
                                        v.getContext())
                                .setButton1Click(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        doDelTarget(entity);
                                        dialogBuilder.dismiss();
                                    }
                                }).setButton2Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogBuilder.dismiss();
                            }
                        }).show();
                    }
                });
            }
        };
        listView.addFooterView();
        listView.setAdapter(adapter);
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
         * 选择年月
         */
        select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = select_date.getText().toString();
                if (!"年份、月份".equals(date) && !StringUtils.isEmpty(date)) {
                    calendar = CalendarDay.from(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(5).replace("月", "")) - 1, 1);
                }
                showDatePickerDialog(AimSettingActivity.this, calendar, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        select_date.setText(year + "年" + (monthOfYear + 1) + "月");
                    }
                });
            }
        });

        /**
         * 选择月份
         */
        select_month.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String param = "";
                if (select_year.getCheckedRadioButtonId() == R.id.one_year) {
                    param = "2016";
                } else if (select_year.getCheckedRadioButtonId() == R.id.two_year) {
                    param = "2017";
                } else if (select_year.getCheckedRadioButtonId() == R.id.three_year) {
                    param = "2018";
                } else if (select_year.getCheckedRadioButtonId() == R.id.four_year) {
                    param = "2019";
                }
                if (R.id.January == checkedId) {
                    param += "01";
                } else if (R.id.February == checkedId) {
                    param += "02";
                } else if (R.id.March == checkedId) {
                    param += "03";
                } else if (R.id.April == checkedId) {
                    param += "04";
                } else if (R.id.May == checkedId) {
                    param += "05";
                } else if (R.id.June == checkedId) {
                    param += "06";
                } else if (R.id.July == checkedId) {
                    param += "07";
                } else if (R.id.August == checkedId) {
                    param += "08";
                } else if (R.id.September == checkedId) {
                    param += "09";
                } else if (R.id.October == checkedId) {
                    param += "10";
                } else if (R.id.November == checkedId) {
                    param += "11";
                } else if (R.id.December == checkedId) {
                    param += "12";
                }
                month = param;
                getTargetList(month);
            }
        });


        // 下拉刷新
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                getTargetList(month);
            }
        });

        // 加载更多
        listView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                listView.setFooterViewTextNoMoreData();
                isLoadAll = true;
            }
        });

        /**
         * 点击提交
         */
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isEmpty(money.getText().toString())) {
                    Toast.makeText(AimSettingActivity.this, "请输入点数!", Toast.LENGTH_LONG).show();
                    return;
                }
                doAddTarget();
            }
        });
    }


    /**
     * 获取门店列表、产品类型
     */
    private void initData() {
        listView.setFooterViewTextNormal();
        TargetEntity entity = new TargetEntity();
        if (!StringUtils.isEmpty(bundle.getString("id"))) {
            if (!RoleEnum.BOSS.getCode().equals(bundle.getString("role_key")) && !RoleEnum.STOREMANAGER.getCode().equals(bundle.getString("role_key"))) {
                entity.setId(Integer.parseInt(bundle.getString("id")));
                HttpClient.getStoreList(entity, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String body) {
                        Log.i("getStoreListTAG", "返回数据:" + body);
                        JSONObject object = JSON.parseObject(body);
                        if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                            storelist = JSONArray.parseArray(object.getString("data"), StoreEntity.class);
                            for (int i = 0; i < storelist.size(); i++) {
                                storeDataset.add(storelist.get(i).getName());
                            }
                            select_shop.attachDataSource(storeDataset);
                        } else if (HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                            UIHelper.showLogin(AimSettingActivity.this);
                        } else {
                            Toast.makeText(AimSettingActivity.this, "获取门店信息失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Request request, IOException e) {
                        Log.i("getStoreListTAG", "失败返回数据:" + request.toString());
                    }
                }, AimSettingActivity.this);
            } else {
                select_shop.setText("所管辖门店");
            }
        } else {
            Toast.makeText(AimSettingActivity.this, "缺少参数uid",Toast.LENGTH_SHORT).show();
        }


        CategroyEntity param = new CategroyEntity();
        param.setCatetype(CategroyEnum.PROJECT.code);
        HttpClient.getCateGoryList(param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String body) {
                Log.i("getCateGoryListTAG", "成功返回数据:" + body);
                JSONObject object = JSON.parseObject(body);
                if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                    categroylist = JSONArray.parseArray(object.getString("data"), CategroyEntity.class);
                    for (int i = 0; i < categroylist.size(); i++) {
                        categroyDataset.add(categroylist.get(i).getName());
                    }
                    select_type.attachDataSource(categroyDataset);
                } else if (HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                    UIHelper.showLogin(AimSettingActivity.this);
                } else {
                    Log.i("getCateGoryListTAG", "获取数据失败:" + body);
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                Log.i("getCateGoryListTAG", "失败返回数据:" + request.toString());
            }
        }, AimSettingActivity.this);

        getTargetList(month);
    }


    /**
     * 获取目标列表
     */
    private void getTargetList(String month) {
        listView.setFooterViewTextNormal();
        TargetEntity entity = new TargetEntity();
        entity.setMonth(month);
        HttpClient.getTargetList(entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String body) {
                Log.i("getTargetListTAG", "返回数据:" + body);
                JSONObject object = JSON.parseObject(body);
                if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                    targetList = JSONArray.parseArray(object.getString("data"), TargetEntity.class);
                    // 下拉刷新
                    if (adapter.getCount() != 0) {
                        adapter.clear();
                    }

                    // 暂无数据
                    if (targetList.isEmpty()) {
                        listView.setFooterViewTextNoData();
                        return;
                    }

                    // 已加载全部
                    if (pno > 1 && (targetList.isEmpty() || targetList.size() < HttpClient.PAGE_SIZE)) {
                        if(targetList.size()>0){
                            adapter.addAll(targetList);
                        }
                        listView.setFooterViewTextNoMoreData();
                        isLoadAll = true;
                        return;
                    }
                    adapter.addAll(targetList);
                    listView.onRefreshComplete();
                    if (pno == 1 && (targetList.isEmpty() || targetList.size() < HttpClient.PAGE_SIZE)) {
                        listView.setFooterViewTextNoMoreData();
                        isLoadAll = true;
                        return;
                    }
                } else if (HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                    UIHelper.showLogin(AimSettingActivity.this);
                } else {
                    Toast.makeText(AimSettingActivity.this, "获取失败",
                            Toast.LENGTH_SHORT).show();
                    listView.onRefreshComplete();
                    listView.setFooterViewTextError();
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                Log.i("getTargetListTAG", "失败返回数据:" + request.toString());
                listView.onRefreshComplete();
                listView.setFooterViewTextError();
            }
        }, AimSettingActivity.this);
    }

    /**
     * 删除一个目标设定
     */
    private void doDelTarget(final TargetEntity entity) {
        if (!StringUtils.isEmpty(entity.getId() + "")) {
            HttpClient.deleteTarget(entity, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("deleteTargetTAG", "返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        targetList.remove(entity);
                        adapter.remove(entity);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(AimSettingActivity.this, "删除成功",
                                Toast.LENGTH_SHORT).show();
                    } else if (HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(AimSettingActivity.this);
                    } else {
                        Toast.makeText(AimSettingActivity.this, "获取失败",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("deleteTargetTAG", "失败返回数据:" + request.toString());
                }
            }, AimSettingActivity.this);
        } else {
            Toast.makeText(AimSettingActivity.this, "缺少参数[ID]",
                    Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 添加一个目标设定
     */
    private void doAddTarget() {
        final TargetEntity entity = new TargetEntity();
        if (!StringUtils.isEmpty(bundle.getString("id"))) {
            entity.setId(Integer.parseInt(bundle.getString("id")));
            entity.setMonth(FuncUtils.formatMonth(select_date.getText().toString()));
            entity.setCategoryId(getCategoryIdByName(categroylist, select_type.getText().toString()));
            entity.setStoreId(getStoreIdByName(storelist, select_shop.getText().toString()));
            entity.setAmount((StringUtils.isEmpty(money.getText().toString()) ? "" : FuncUtils.formatWMoney3(money.getText().toString())));
            HttpClient.AddTarget(entity, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("AddTargetTAG", "返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        money.setText("");
                        /*entity.setCateName(select_type.getText().toString());
                        targetList.add(entity);
                        adapter.add(entity);
                        adapter.notifyDataSetChanged();*/
                        getTargetList(month);
                        Toast.makeText(AimSettingActivity.this, "添加目标成功", Toast.LENGTH_LONG).show();
                    } else if (HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(AimSettingActivity.this);
                    } else {
                        Toast.makeText(AimSettingActivity.this, object.getString("msg"),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("AddTargetTAG", "失败返回数据:" + request.toString());
                }
            }, AimSettingActivity.this);
        } else {
            Toast.makeText(AimSettingActivity.this, "缺少请求参数[id]",
                    Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 显示年月选择对话框
     *
     * @param context
     * @param day
     * @param callback
     */
    public static void showDatePickerDialog(Context context, CalendarDay day,
                                            DatePickerDialog.OnDateSetListener callback) {
        if (day == null) {
            day = CalendarDay.today();
        }
        CustomerDatePickerDialog dialog = new CustomerDatePickerDialog(
                context, callback, day.getYear(), day.getMonth(), day.getDay()
        );

        DatePicker dp = dialog.getDatePicker();
        if (dp != null) {
            ((ViewGroup) ((ViewGroup) dp.getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
        }
        dialog.show();
    }


    /**
     * 根据分类名称获取分类ID
     *
     * @param list
     * @param categroryName
     * @return
     */
    private String getCategoryIdByName(List<CategroyEntity> list, String categroryName) {
        if (list == null || list.size() == 0) {
            return "";
        }

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName().equals(categroryName)) {
                return list.get(i).getId();
            }
        }
        return "";
    }


    /**
     * 根据门店名称获取门店ID
     *
     * @param list
     * @param storeName
     * @return
     */
    private String getStoreIdByName(List<StoreEntity> list, String storeName) {
        if (StringUtils.isEmpty(storeName) || "所管辖门店".equals(storeName)) {
            return "0";
        }
        if (list == null || list.size() == 0) {
            return "0";
        }

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName().equals(storeName)) {
                return list.get(i).getId() + "";
            }
        }
        return "";
    }
}
