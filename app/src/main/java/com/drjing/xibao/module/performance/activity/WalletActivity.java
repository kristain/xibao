package com.drjing.xibao.module.performance.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.utils.FuncUtils;
import com.drjing.xibao.common.view.dialog.Effectstype;
import com.drjing.xibao.common.view.dialog.NiftyDialogBuilder;
import com.drjing.xibao.common.view.pulltorefresh.PullToRefreshBase;
import com.drjing.xibao.common.view.pulltorefresh.PullToRefreshListView;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.module.entity.WalletEntity;
import com.drjing.xibao.module.performance.adapter.BaseAdapterHelper;
import com.drjing.xibao.module.performance.adapter.QuickAdapter;
import com.drjing.xibao.module.ui.UIHelper;
import com.kristain.common.utils.DateTimeUtils;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.List;

/**
 * 钱包列表
 * Created by kristain on 16/1/3.
 */
public class WalletActivity extends SwipeBackActivity {

    /**
     * 后退返回按钮
     */
    private Button btnBack;
    /**
     * 标题栏文案
     */
    private TextView textHeadTitle;

    /**
     * 新增
     */
    private TextView btnRight;

    /**
     * 下拉加载列表组件
     */
    private PullToRefreshListView listView;

    private TextView total_money;


    private WalletEntity param;
    private int pno = 1;
    private boolean isLoadAll;

    private double totalAmount=0;

    QuickAdapter<WalletEntity> adapter;

    NiftyDialogBuilder dialogBuilder;

    List<WalletEntity> list;

    private int pageSize=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        initView();
        initEvent();
    }

    private void initView() {
        btnBack = (Button)findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        btnRight=(TextView)findViewById(R.id.btnRight);
        textHeadTitle.setText("钱包");
        btnRight.setText("新增");
        btnRight.setVisibility(View.VISIBLE);

        total_money= (TextView)findViewById(R.id.total_money);

        listView = (PullToRefreshListView)findViewById(R.id.listView);

        adapter = new QuickAdapter<WalletEntity>(this, R.layout.wallet_list_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, final WalletEntity entity) {
                helper.setText(R.id.project, StringUtils.formatNull(entity.getProjectName()))
                        .setText(R.id.date, DateTimeUtils.formatDateTime(entity.getEditTime(), DateTimeUtils.YYYY_MM_DD))
                        .setText(R.id.money, StringUtils.formatNull(entity.getAmount()+""))
                        .setText(R.id.remark, "备注:"+ StringUtils.formatNull(entity.getRemarks()))
                        .setBackgroundColor(R.id.project_split_view, FuncUtils.toWalletColorByCategory(entity.getCategoryId()));
                helper.getView().findViewById(R.id.delete_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder = NiftyDialogBuilder.getInstance(WalletActivity.this);
                        dialogBuilder
                                .withTitle("提示")
                                .withTitleColor("#FFFFFF")
                                .withDividerColor("#11000000")
                                .withMessage("确定删除该提成信息吗？")
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
                                        deleteWallet(entity);
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


    @Override
    protected void onResume() {
        super.onResume();
        initData();
        loadData();
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

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showWalletAdd(WalletActivity.this);
            }
        });

        // 下拉刷新
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                initData();
                loadData();
            }
        });

        // 加载更多
        listView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                loadData();
            }
        });
    }

    private void initData() {
        param = new WalletEntity();
        pno = 1;
        isLoadAll = false;
    }

    private void loadData(){
        if (isLoadAll) {
            return;
        }
        param.setPage(pno);
        listView.setFooterViewTextNormal();
        HttpClient.getWalletList(param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String body) {
                listView.onRefreshComplete();
                Log.i("getwalletListTAG", "成功返回数据:" +body);
                JSONObject object = JSON.parseObject(body);
                if(HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))){
                    JSONObject data = JSON.parseObject(object.getString("data"));
                    list = JSONArray.parseArray(data.getString("list"), WalletEntity.class);
                    totalAmount= Double.parseDouble(data.getString("totalAmount"));
                    total_money.setText(totalAmount+"元");

                    if(pageSize==0){
                        pageSize = Integer.parseInt(data.getString("pageSize"));
                    }
                    // 下拉刷新
                    if (pno == 1 && adapter.getCount() != 0) {
                        adapter.clear();
                    }

                    // 暂无数据
                    if (pno == 1 && list.isEmpty()) {
                        listView.setFooterViewTextNoData();
                        return;
                    }

                    // 已加载全部
                    if (pno > 1 && (list.isEmpty())) {
                        listView.setFooterViewTextNoMoreData();
                        isLoadAll = true;
                        return;
                    }
                    adapter.addAll(list);
                    if(list.isEmpty() || list.size() < pageSize){
                        listView.onRefreshComplete();
                        listView.setFooterViewTextNoMoreData();
                        return;
                    }
                    pno++;
                } else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                    UIHelper.showLogin(WalletActivity.this);
                }else{
                    listView.onRefreshComplete();
                    listView.setFooterViewTextError();
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(WalletActivity.this, request.toString(), Toast.LENGTH_LONG).show();
                Log.i("getwalletListTAG", "失败返回数据:" + request.toString());
                listView.onRefreshComplete();
                listView.setFooterViewTextError();
            }
        }, WalletActivity.this);
    }

    /**
     * 删除钱包
     */
    private void deleteWallet(final WalletEntity entity){
        if(!StringUtils.isEmpty(entity.getId()+"")){
            HttpClient.deleteWallet(entity, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("getwalletListTAG", "返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if(HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))){
                        total_money.setText(FuncUtils.amt_sub(totalAmount+"", entity.getAmount() + "") + "元");
                        list.remove(entity);
                        adapter.remove(entity);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(WalletActivity.this, "已删除",
                                Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(WalletActivity.this, "删除失败",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("getwalletListTAG", "失败返回数据:" + request.toString());
                    Toast.makeText(WalletActivity.this, "删除失败",
                            Toast.LENGTH_SHORT).show();
                }
            }, WalletActivity.this);
        }else{
            Toast.makeText(WalletActivity.this, "缺少请求参数[id]",
                    Toast.LENGTH_SHORT).show();
        }

    }
}
