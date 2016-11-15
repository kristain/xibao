package com.drjing.xibao.module.workbench.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.drjing.xibao.module.entity.NurseTagEntity;
import com.drjing.xibao.module.performance.adapter.BaseAdapterHelper;
import com.drjing.xibao.module.performance.adapter.QuickAdapter;
import com.drjing.xibao.module.performance.fragment.CalendarDialogFragment;
import com.drjing.xibao.module.performance.fragment.CalendarDialogFragment.SelectListener;
import com.drjing.xibao.module.ui.UIHelper;
import com.kristain.common.utils.DateTimeUtils;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.List;

/**
 * 特殊日期
 * Created by kristain on 15/12/30.
 */
public class SpecialLogActivity extends SwipeBackActivity implements  SelectListener {

    /**
     * 后退按钮
     */
    private Button btnBack;

    /**
     * Title栏文案
     */
    private TextView textHeadTitle;

    private Bundle bundle;
    private TextView select_date;
    private EditText tagName;

    private TextView submit_btn;

    private ListView list_view;

    private LinearLayout add_layout;

    QuickAdapter<NurseTagEntity> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_log);
        bundle=getIntent().getExtras();
        initView();
        initEvent();
        getSpecialTagsList();
    }


    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("特殊日期");
        tagName = (EditText)findViewById(R.id.tagName);
        select_date = (TextView)findViewById(R.id.date);
        submit_btn = (TextView)findViewById(R.id.submit_btn);
        list_view = (ListView)findViewById(R.id.list_view);
        add_layout = (LinearLayout)findViewById(R.id.add_layout);
        if("customer".equals(bundle.getString("source"))){
            add_layout.setVisibility(View.GONE);
        }

        adapter = new QuickAdapter<NurseTagEntity>(this, R.layout.special_log_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, final NurseTagEntity item) {
                helper.setText(R.id.date, (StringUtils.isEmpty(item.getSpeDate()) ? "" : DateTimeUtils.formatDateTime(Long.parseLong(item.getSpeDate()), DateTimeUtils.YYYY_MM_DD)))
                        .setText(R.id.content, (!StringUtils.isEmpty(item.getTagName()) && item.getTagName().length() > 5) ? item.getTagName().substring(0, 5) + ".." : item.getTagName())
                        .setText(R.id.count, item.getCount() + "")
                        .setText(R.id.content2, item.getTagName());
                helper.getView().findViewById(R.id.add_schedule).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //跳转到特殊日期页面
                        Bundle bundle = new Bundle();
                        bundle.putString("content", item.getTagName());
                        bundle.putString("alertDate",(StringUtils.isEmpty(item.getSpeDate())?"": DateTimeUtils.formatDateTime(Long.parseLong(item.getSpeDate()), DateTimeUtils.DF_YYYY_MM_DD)));
                        bundle.putString("select_schedule", "特殊日期");
                        UIHelper.showScheduleAdd(SpecialLogActivity.this, bundle);
                    }
                });

                helper.getView().findViewById(R.id.count).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addCount(item.getId()+"");
                    }
                });

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

        /**
         * 提交
         */
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isEmpty(tagName.getText().toString())) {
                    Toast.makeText(SpecialLogActivity.this, "请输入标签内容", Toast.LENGTH_LONG).show();
                   return;
                }
                if(StringUtils.isEmpty(select_date.getText().toString())|| "请选择日期".equals(select_date.getText().toString())){
                    Toast.makeText(SpecialLogActivity.this, "请选择日期", Toast.LENGTH_LONG).show();
                    return;
                }
                submit_btn.setEnabled(false);
                addSpecialTag();
            }
        });

        /**
         * 点击选择日期
         */
        select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarDialogFragment.newInstance().show(getSupportFragmentManager(), "calendar");
            }
        });

        /**
         * 点击特殊日期
         */
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               /* Tooltip.make(
                        SpecialLogActivity.this,
                        new Tooltip.Builder(101)
                                .anchor(view, Tooltip.Gravity.BOTTOM)
                                .closePolicy(Tooltip.ClosePolicy.TOUCH_ANYWHERE_NO_CONSUME, 3000)
                                .text(((TextView) view.findViewById(R.id.date)).getText().toString() + "," + ((TextView) view.findViewById(R.id.content2)).getText().toString())
                                .fadeDuration(200)
                                .fitToScreen(false)
                                .maxWidth(DisplayUtils.getScreenW(SpecialLogActivity.this) - 200)
                                .showDelay(400)
                                .toggleArrow(true)
                                .build()
                ).show();*/

                Toast.makeText(SpecialLogActivity.this,((TextView) view.findViewById(R.id.content2)).getText().toString(),Toast.LENGTH_LONG).show();
            }
        });

    }


    /**
     * 获取特殊日期标签
     */
    private void getSpecialTagsList(){
        NurseTagEntity entity = new NurseTagEntity();
        entity.setCustomerId(bundle.getString("customer_id"));
        if(!StringUtils.isEmpty(entity.getCustomerId())){
            HttpClient.getSpecialTagList(entity, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("getSpecialTagListTAG", "返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        List<NurseTagEntity> list = JSONArray.parseArray(object.getString("data"), NurseTagEntity.class);
                        adapter.clear();
                        if(list!=null && list.size()>0){
                            adapter.addAll(list);
                            list_view.setAdapter(adapter);
                        }
                    } else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(SpecialLogActivity.this);
                    }else {
                        Toast.makeText(SpecialLogActivity.this, "获取失败",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("getSpecialTagListTAG", "失败返回数据:" + request.toString());
                }
            }, SpecialLogActivity.this);
        }else{
            Toast.makeText(SpecialLogActivity.this, "缺少请求参数[customer_id]",
                    Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 新增特殊日期标签
     */
    private void addSpecialTag(){
        NurseTagEntity entity = new NurseTagEntity();
        entity.setTagName(tagName.getText().toString());
        entity.setSpeDate(select_date.getText().toString());
        entity.setCustomerId(bundle.getString("customer_id"));
        entity.setOrderId(bundle.getString("order_id"));
        HttpClient.addSpecialTag(entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String body) {
                submit_btn.setEnabled(true);
                JSONObject object = JSON.parseObject(body);
                if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                    tagName.setText("");
                    Toast.makeText(SpecialLogActivity.this, "添加标签成功",
                            Toast.LENGTH_SHORT).show();
                    getSpecialTagsList();
                } else if (HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                    UIHelper.showLogin(SpecialLogActivity.this);
                } else {
                    Log.i("addSpecialTag", "添加标签失败");
                    Toast.makeText(SpecialLogActivity.this, "添加标签失败",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                submit_btn.setEnabled(true);
                Log.i("addNurseTag", "失败返回数据:" + request.toString());
                Toast.makeText(SpecialLogActivity.this, "添加标签失败",
                        Toast.LENGTH_SHORT).show();
            }
        }, SpecialLogActivity.this);
    }


    /**
     * 特殊日期标签+1
     */
    private void addCount(String tagId){
        NurseTagEntity entity = new NurseTagEntity();
        entity.setCustomerId(bundle.getString("customer_id"));
        entity.setOrderId(bundle.getString("order_id"));
        entity.setTagId(tagId);
        if(!StringUtils.isEmpty(entity.getTagId())){
            HttpClient.addSpecialCount(entity, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("addSpecialCountTAG", "返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        Toast.makeText(SpecialLogActivity.this, "提交成功",
                                Toast.LENGTH_SHORT).show();
                        getSpecialTagsList();
                    }else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(SpecialLogActivity.this);
                    } else {
                        Toast.makeText(SpecialLogActivity.this, "提交失败",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("addSpecialCountTAG", "失败返回数据:" + request.toString());

                }
            }, SpecialLogActivity.this);
        }else{
            Toast.makeText(SpecialLogActivity.this, "缺少请求参数[tagId]",
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
    @Override
    public void onSelectComplete(String date)
    {
        select_date.setText(date);
    }
}
