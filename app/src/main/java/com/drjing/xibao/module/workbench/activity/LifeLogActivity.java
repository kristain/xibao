package com.drjing.xibao.module.workbench.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.common.view.tagview.TagContainerLayout;
import com.drjing.xibao.common.view.tagview.TagView;
import com.drjing.xibao.module.entity.NurseTagEntity;
import com.drjing.xibao.module.ui.UIHelper;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 私密生活日志
 * Created by kristain on 15/12/30.
 */
public class LifeLogActivity extends SwipeBackActivity {

    /**
     * 后退按钮
     */
    private Button btnBack;

    /**
     * Title栏文案
     */
    private TextView textHeadTitle;

    private Bundle bundle;

    private EditText tagName;

    private TextView submit_btn;

    private TagContainerLayout tagcontainerLayout;


    List<NurseTagEntity> list = new ArrayList<NurseTagEntity>();

    private TextView log_remark;

    private LinearLayout  add_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nurse_log);
        bundle=getIntent().getExtras();
        initView();
        initEvent();
        getLifeTagsList();
    }


    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("私密日志");
        tagName = (EditText)findViewById(R.id.tagName);
        submit_btn = (TextView)findViewById(R.id.submit_btn);
        log_remark = (TextView)findViewById(R.id.log_remark);
        tagcontainerLayout = (TagContainerLayout)findViewById(R.id.tagcontainerLayout);
        add_layout = (LinearLayout)findViewById(R.id.add_layout);
        if("customer".equals(bundle.getString("source"))){
            add_layout.setVisibility(View.GONE);
        }
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
         * 提交
         */
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtils.isEmpty(tagName.getText().toString())) {
                    submit_btn.setEnabled(false);
                    addLifeTag();
                } else {
                    Toast.makeText(LifeLogActivity.this, "请输入标签内容", Toast.LENGTH_LONG).show();
                }
            }
        });

        /**
         * 点击标签
         */
        tagcontainerLayout.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                addLifeTagCount(getIdByName(list, text));
            }

            @Override
            public void onTagLongClick(final int position, String text) {

            }
        });
    }


    /**
     * 获取生活日志标签
     */
    private void getLifeTagsList(){
        NurseTagEntity entity = new NurseTagEntity();
        entity.setCustomerId(bundle.getString("customer_id"));
        if(!StringUtils.isEmpty(entity.getCustomerId())){
            HttpClient.getLifeTagList(entity, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("getLifeTagListTAG", "返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        list = JSONArray.parseArray(object.getString("data"), NurseTagEntity.class);
                        List<String> item = new ArrayList<String>();
                        if (list != null) {
                            tagcontainerLayout.removeAllTags();
                            for (int i = 0; i < list.size(); i++) {
                                item.add(list.get(i).getTagName() + " | " + list.get(i).getCount()+"   ");
                            }
                            tagcontainerLayout.setTags(item);
                        }
                    } else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(LifeLogActivity.this);
                    }  else {
                        Toast.makeText(LifeLogActivity.this, "获取失败",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("getLifeTagListTAG", "失败返回数据:" + request.toString());
                }
            }, LifeLogActivity.this);
        }else{
            Toast.makeText(LifeLogActivity.this, "缺少请求参数[customer_id]",
                    Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 新增特殊日期标签
     */
    private void addLifeTag(){
        NurseTagEntity entity = new NurseTagEntity();
        entity.setTagName(tagName.getText().toString());
        entity.setSpeDate("");
        entity.setCustomerId(bundle.getString("customer_id"));
        entity.setOrderId(bundle.getString("order_id"));
        HttpClient.addLifeTag(entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String body) {
                submit_btn.setEnabled(true);
                JSONObject object = JSON.parseObject(body);
                if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                    tagName.setText("");
                    Toast.makeText(LifeLogActivity.this, "添加标签成功",
                            Toast.LENGTH_SHORT).show();
                    getLifeTagsList();
                } else if (HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                    UIHelper.showLogin(LifeLogActivity.this);
                } else {
                    Log.i("addLifeTag", "返回数据:" + body);
                    Toast.makeText(LifeLogActivity.this, "添加标签失败",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                submit_btn.setEnabled(true);
                Log.i("addLifeTag", "失败返回数据:" + request.toString());
                Toast.makeText(LifeLogActivity.this, "添加标签失败",
                        Toast.LENGTH_SHORT).show();
            }
        }, LifeLogActivity.this);
    }


    /**
     * 私密日志标签+1
     */
    private void addLifeTagCount(String tagId){
        NurseTagEntity entity = new NurseTagEntity();
        entity.setCustomerId(bundle.getString("customer_id"));
        entity.setOrderId(bundle.getString("order_id"));
        entity.setTagId(tagId);
        if(!StringUtils.isEmpty(entity.getTagId())){
            HttpClient.addLifeCount(entity, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("addLifeCountTAG", "返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        Toast.makeText(LifeLogActivity.this, "提交成功",
                                Toast.LENGTH_SHORT).show();
                        getLifeTagsList();
                    }  else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(LifeLogActivity.this);
                    } else {
                        Toast.makeText(LifeLogActivity.this, "提交失败",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("addLifeCountTAG", "失败返回数据:" + request.toString());

                }
            }, LifeLogActivity.this);
        }else{
            Toast.makeText(LifeLogActivity.this, "缺少请求参数[tagId]",
                    Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * 根据分类名称获取分类ID
     * @param list
     * @param text
     * @return
     */
    private String getIdByName(List<NurseTagEntity> list,String text){
        if(list==null || list.size()==0){
            return "";
        }
        if(StringUtils.isEmpty(text)){
            return "";
        }
        String[] txt =   text.split(" ");
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).getTagName().equals(txt[0])){
                return list.get(i).getId()+"";
            }
        }
        return "";
    }
}
