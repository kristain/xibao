package com.drjing.xibao.module.workbench.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.module.entity.UserParam;
import com.drjing.xibao.module.performance.adapter.BaseAdapterHelper;
import com.drjing.xibao.module.performance.adapter.QuickAdapter;
import com.drjing.xibao.module.ui.UIHelper;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.List;

/**
 * 单选门店顾问列表
 * Created by kristain on 16/1/3.
 */
public class ConsultantStoreSingleListActivity extends SwipeBackActivity {

    /**
     * 后退按钮
     */
    private Button btnBack;

    /**
     * Title栏文案
     */
    private TextView textHeadTitle;


    private ListView list_view;


    QuickAdapter<UserParam> adapter;

    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultant_singlelist);
        bundle = getIntent().getExtras();
        initView();
        initEvent();
        loadData();
    }


    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("顾问列表");
        list_view = (ListView)findViewById(R.id.list_view);
        adapter = new QuickAdapter<UserParam>(this, R.layout.adviser_list_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, UserParam item) {
                helper.setText(R.id.name, item.getUsername())
                        .setText(R.id.rolename, item.getRoleName())
                        .setText(R.id.adviser_id,item.getId()+"")
                        .setText(R.id.store_name,item.getStore_name());
                helper.getView().findViewById(R.id.store_name).setVisibility(View.VISIBLE);

            }
        };
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


        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=getIntent();
                intent.putExtra("accountname", ((TextView)view.findViewById(R.id.name)).getText().toString());
                intent.putExtra("adviser_id", ((TextView)view.findViewById(R.id.adviser_id)).getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    private void loadData() {
       UserParam param= new UserParam();
        param.setStore_id(bundle.getString("store_id"));
        if(!StringUtils.isEmpty(param.getStore_id())){
            HttpClient.getStoreAdviserList(param, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("getAdviserListTAG", "成功返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        List<UserParam> list = JSONArray.parseArray(object.getString("data"), UserParam.class);
                        adapter.addAll(list);
                        list_view.setAdapter(adapter);
                    } else if (HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(ConsultantStoreSingleListActivity.this);
                    } else {
                        Log.i("getAdviserListTAG", "失败返回数据:" + body.toString());
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("getAdviserListTAG", "失败返回数据:" + request.toString());
                }
            }, this);
        }else{
            Toast.makeText(this, "缺少请求参数[store_id]", Toast.LENGTH_LONG).show();
        }
    }



}
