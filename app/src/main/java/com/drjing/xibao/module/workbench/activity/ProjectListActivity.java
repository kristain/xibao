package com.drjing.xibao.module.workbench.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
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
import com.drjing.xibao.module.entity.CategroyEntity;
import com.drjing.xibao.module.performance.adapter.BaseAdapterHelper;
import com.drjing.xibao.module.performance.adapter.QuickAdapter;
import com.drjing.xibao.module.ui.UIHelper;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目列表页面
 * Created by kristain on 15/12/30.
 */
public class ProjectListActivity extends SwipeBackActivity {

    /**
     * 后退按钮
     */
    private Button btnBack;

    /**
     * Title栏文案
     */
    private TextView textHeadTitle;


    private Bundle bundle;

    private ListView list_view;
    QuickAdapter<CategroyEntity> adapter;

    private TextView btnRight;

    private String projectIds;
    private String projectNames;
    private String[] selectedIds;
    private List<CategroyEntity> selectedProjectList = new ArrayList<CategroyEntity>();
    List<CategroyEntity> list;

    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cate_product_list);
        bundle=getIntent().getExtras();
        if(!StringUtils.isEmpty(bundle.getString("projectIds"))){
            selectedIds = bundle.getString("projectIds").split(",");
        }
        initView();
        initEvent();
        loadData();
    }


    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("项目列表");
        list_view = (ListView)findViewById(R.id.list_view);
        btnRight =(TextView)findViewById(R.id.btnRight);
        btnRight.setText("完成");
        btnRight.setVisibility(View.VISIBLE);
        list_view.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        adapter = new QuickAdapter<CategroyEntity>(this,R.layout.project_list_item_multiple) {
       // adapter = new QuickAdapter<CategroyEntity>(this, R.layout.project_list_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, final CategroyEntity item) {
                helper.setText(R.id.text1, item.getName());
                        //.setText(R.id.project_id, item.getId());
               /*((CheckBox) helper.getView().findViewById(R.id.select_btn)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            if(selectedProjectList!=null){
                                if(!selectedProjectList.contains(item)){
                                    selectedProjectList.add(item);
                                }
                            }
                        }else{
                            if(selectedProjectList!=null && selectedProjectList.size()>0){
                                selectedProjectList.remove(item);
                            }
                        }
                    }
                });*/
            }
        };

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("TAG", "点击:" + position);
                if (((CheckedTextView) view.findViewById(R.id.text1)).isChecked()) {
                    if (selectedProjectList != null) {
                        if (!selectedProjectList.contains(list.get(position))) {
                            selectedProjectList.add(list.get(position));
                        }
                    }
                } else {
                    if (selectedProjectList != null && selectedProjectList.size() > 0) {
                        selectedProjectList.remove(list.get(position));
                    }
                }
            }
        });



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
         * 点击完成
         */
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                projectIds="";
                projectNames="";
                if(selectedProjectList!=null && selectedProjectList.size()>0){
                    for (int i=0;i<selectedProjectList.size();i++){
                        projectIds +=selectedProjectList.get(i).getId()+",";
                        projectNames+=selectedProjectList.get(i).getName()+",";
                    }
                    projectIds = projectIds.substring(0,projectIds.length()-1);
                    projectNames = projectNames.substring(0,projectNames.length()-1);
                }
                Intent intent = getIntent();
                Log.e("TAG","projectIds:"+projectIds);
                intent.putExtra("projectIds", projectIds);
                intent.putExtra("projectNames", projectNames);
                setResult(SaleLogAddActivity.REQUESTXMCODE, intent);
                finish();
            }
        });
    }

    /**
     * 初始化数据
     */
    private void loadData(){
        CategroyEntity param= new CategroyEntity();
        param.setId(bundle.getString("cid"));
        if(!StringUtils.isEmpty(param.getId())){
            HttpClient.getCateGoryProjectList(param, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("getProjectListTAG", "成功返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        list = JSONArray.parseArray(object.getString("data"), CategroyEntity.class);
                        adapter.addAll(list);
                        list_view.setAdapter(adapter);
                        if(list!=null && list.size()>0){
                            if (selectedIds != null && selectedIds.length > 0) {
                                for (int i = 0; i < selectedIds.length; i++) {
                                    for (int j=0;j<list.size();j++){
                                        if (selectedIds[i].equals(list.get(j).getId())) {
                                            Log.e("TAG", "selectedIds[i]:" + selectedIds[i] + " id:" + list.get(j).getId() + "选中");
                                            list_view.setItemChecked(i,true);
                                            if (selectedProjectList != null) {
                                                    selectedProjectList.add(list.get(j));
                                            }
                                            break;
                                        }
                                    }

                                }
                            }
                        }
                    } else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(ProjectListActivity.this);
                    } else {
                        Log.i("getProjectListTAG", "失败返回数据:" + body.toString());
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("getProjectListTAG", "失败返回数据:" + request.toString());
                    }
            }, this);
        }else{
            Toast.makeText(this,"缺少请求参数[cid]",Toast.LENGTH_LONG).show();
        }

    }

    //
}
