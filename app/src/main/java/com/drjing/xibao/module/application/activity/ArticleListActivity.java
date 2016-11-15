package com.drjing.xibao.module.application.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.view.pulltorefresh.PullToRefreshBase;
import com.drjing.xibao.common.view.pulltorefresh.PullToRefreshListView;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.module.entity.ArticleEntity;
import com.drjing.xibao.module.performance.adapter.BaseAdapterHelper;
import com.drjing.xibao.module.performance.adapter.QuickAdapter;
import com.drjing.xibao.module.ui.UIHelper;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.List;

/**
 * 新闻咨询列表
 * Created by kristain on 16/1/3.
 */
public class ArticleListActivity extends SwipeBackActivity {


    /**
     * 后退返回按钮
     */
    private Button btnBack;
    /**
     * 标题栏文案
     */
    private TextView textHeadTitle;

    private Bundle bundle;

    /**
     * 下拉加载列表组件
     */
    private PullToRefreshListView listView;


    private ArticleEntity param;
    private int pno = 1;
    private int pageSize=0;
    private boolean isLoadAll;

    QuickAdapter<ArticleEntity> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artcle_list);
        bundle=getIntent().getExtras();
        initView();
        initEvent();
    }

    private void initView() {
        btnBack = (Button)findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText(bundle.getString("title_text"));
        listView = (PullToRefreshListView)findViewById(R.id.listView);

        initData();
        adapter = new QuickAdapter<ArticleEntity>(this, R.layout.article_list_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, ArticleEntity order) {
                helper.setText(R.id.article_title, order.getTitle())
                        .setText(R.id.article_url, order.getCategoryId());
            }
        };
        listView.addFooterView();
        listView.setAdapter(adapter);
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
        // 点击事件
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ArticleEntity shop = adapter.getItem(i - 1);
                if (shop != null) {
                    Bundle bundle =new Bundle();
                    bundle.putString("articleId", shop.getId()+"");
                    bundle.putString("article_title",shop.getTitle());
                    UIHelper.showArtcleDetail(ArticleListActivity.this,bundle);
                }
            }
        });
    }

    private void initData() {
        param = new ArticleEntity();
        pno = 1;
        isLoadAll = false;
    }

    private void loadData() {
        if (isLoadAll) {
            return;
        }
        param.setCategoryId(bundle.getString("category_id"));
        param.setPage(pno);
        listView.setFooterViewTextNormal();
        HttpClient.getArticleList(param, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String body) {
                listView.onRefreshComplete();
                Log.i("getArticleListTAG", "成功返回数据:" + body);
                JSONObject object = JSON.parseObject(body);
                if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                    JSONObject data = JSON.parseObject(object.getString("data"));
                    List<ArticleEntity> list = JSONArray.parseArray(data.getString("list"), ArticleEntity.class);

                    if(pageSize==0){
                        pageSize = data.getInteger("pageSize");
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
                    if (pno > 1 && (list.isEmpty() || list.size() < pageSize)) {
                        if(list.size()>0){
                            adapter.addAll(list);
                        }
                        listView.setFooterViewTextNoMoreData();
                        isLoadAll = true;
                        return;
                    }
                    adapter.addAll(list);
                    if(pno ==1 && (list.isEmpty() || list.size() < pageSize)){
                        listView.setFooterViewTextNoMoreData();
                        isLoadAll = true;
                        return;
                    }
                    pno++;
                }else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))){
                    UIHelper.showLogin(ArticleListActivity.this);
                }else{
                    Log.i("getArticleListTAG", "获取数据失败:" + body);
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                Log.i("getArticleListTAG", "失败返回数据:" + request.toString());
                listView.onRefreshComplete();
                listView.setFooterViewTextError();
            }
        }, this);
    }
}
