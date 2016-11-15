package com.drjing.xibao.module.workbench.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.common.view.tagview.TagNoOpertContainerLayout;
import com.drjing.xibao.common.view.tagview.TagNoneView;
import com.drjing.xibao.module.entity.ReportEntity;
import com.drjing.xibao.module.entity.RoleEnum;
import com.drjing.xibao.module.ui.UIHelper;
import com.kristain.common.utils.DateTimeUtils;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 查看员工日报
 * Created by kristain on 15/12/30.
 */
public class ReportViewActivity extends SwipeBackActivity {

    private static final String TAG="ReportActivity";
    /**
     * 后退按钮
     */
    private Button btnBack;

    /**
     * Title栏文案
     */
    private TextView textHeadTitle;

    private TextView pre_day,next_day,date_text;
    private String day = DateTimeUtils.gainCurrentDate(DateTimeUtils.DF_YYYY_MM_DD);



    private RelativeLayout havedone_ordernote_layout,havedone_counttostore_layout,havedone_counttodoor_layout,
            havedone_tagsnursing_layout,havedone_saleMedicalBeauty_layout,havedone_saleProject_layout,havedone_saleHealthBeauty_layout,
            havedone_saleConsume_layout,havedone_tagsRemind_layout,havedone_tagsRevisit_layout,havedone_tagsActive_layout,
            havedone_saleLog_layout,notdone_ordernote_layout,notdone_counttostore_layout,notdone_counttodoor_layout,
            notdone_tagsnursing_layout,notdone_saleMedicalBeauty_layout,notdone_saleProject_layout,notdone_saleHealthBeauty_layout,
            notdone_saleConsume_layout,notdone_tagsRemind_layout,notdone_tagsRevisit_layout,notdone_tagsActive_layout,
            notdone_saleLog_layout;

    private TextView havedone_ordernote,havedone_counttostore,havedone_counttodoor,havedone_tagsnursing,havedone_saleMedicalBeauty,havedone_saleProject,
            havedone_saleHealthBeauty,havedone_saleConsume,havedone_tagsRemind,havedone_tagsRevisit,havedone_tagsActive,havedone_saleLog,
            notdone_counttostore,notdone_counttodoor,notdone_tagsnursing,notdone_saleMedicalBeauty,notdone_saleProject,notdone_ordernote,
            notdone_saleHealthBeauty,notdone_saleConsume,notdone_tagsRemind,notdone_tagsRevisit,notdone_tagsActive,notdone_saleLog;

    private Bundle bundle;

    private RadioButton one_superior,two_superior,three_superior,four_superior,five_superior,six_superior,seven_superior;

    private TagNoOpertContainerLayout tagcontainerLayout;

    private TextView tag_context;

    /**
     * 日报返回标签
     */
    private List<ReportEntity> report_tagList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_view);
        bundle = getIntent().getExtras();
        initView();
        initEvent();
        loadData(DateTimeUtils.gainCurrentDate(DateTimeUtils.DF_YYYY_MM_DD));

    }


    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("日报");
        pre_day = (TextView)findViewById(R.id.pre_day);
        next_day = (TextView)findViewById(R.id.next_day);
        date_text = (TextView)findViewById(R.id.date_text);

        one_superior=(RadioButton)findViewById(R.id.one_superior);
        two_superior=(RadioButton)findViewById(R.id.two_superior);
        three_superior=(RadioButton)findViewById(R.id.three_superior);
        four_superior=(RadioButton)findViewById(R.id.four_superior);
        five_superior=(RadioButton)findViewById(R.id.five_superior);
        six_superior=(RadioButton)findViewById(R.id.six_superior);
        seven_superior=(RadioButton)findViewById(R.id.seven_superior);

        tag_context =(TextView)findViewById(R.id.tag_context);
        tagcontainerLayout =(TagNoOpertContainerLayout)findViewById(R.id.tagcontainerLayout);

        havedone_ordernote_layout = (RelativeLayout)findViewById(R.id.havedone_ordernote_layout);
        havedone_counttostore_layout =(RelativeLayout)findViewById(R.id.havedone_counttostore_layout);
        havedone_counttodoor_layout=(RelativeLayout)findViewById(R.id.havedone_counttodoor_layout);
        havedone_tagsnursing_layout=(RelativeLayout)findViewById(R.id.havedone_tagsnursing_layout);
        havedone_saleMedicalBeauty_layout=(RelativeLayout)findViewById(R.id.havedone_saleMedicalBeauty_layout);
        havedone_saleProject_layout=(RelativeLayout)findViewById(R.id.havedone_saleProject_layout);
        havedone_saleHealthBeauty_layout=(RelativeLayout)findViewById(R.id.havedone_saleHealthBeauty_layout);
        havedone_saleConsume_layout=(RelativeLayout)findViewById(R.id.havedone_saleConsume_layout);
        havedone_tagsRemind_layout=(RelativeLayout)findViewById(R.id.havedone_tagsRemind_layout);
        havedone_tagsRevisit_layout=(RelativeLayout)findViewById(R.id.havedone_tagsRevisit_layout);
        havedone_tagsActive_layout=(RelativeLayout)findViewById(R.id.havedone_tagsActive_layout);
        havedone_saleLog_layout=(RelativeLayout)findViewById(R.id.havedone_saleLog_layout);
        notdone_ordernote_layout = (RelativeLayout)findViewById(R.id.notdone_ordernote_layout);
        notdone_counttostore_layout=(RelativeLayout)findViewById(R.id.notdone_counttostore_layout);
        notdone_counttodoor_layout=(RelativeLayout)findViewById(R.id.notdone_counttodoor_layout);
        notdone_tagsnursing_layout=(RelativeLayout)findViewById(R.id.notdone_tagsnursing_layout);
        notdone_saleMedicalBeauty_layout=(RelativeLayout)findViewById(R.id.notdone_saleMedicalBeauty_layout);
        notdone_saleProject_layout=(RelativeLayout)findViewById(R.id.notdone_saleProject_layout);
        notdone_saleHealthBeauty_layout=(RelativeLayout)findViewById(R.id.notdone_saleHealthBeauty_layout);
        notdone_saleConsume_layout=(RelativeLayout)findViewById(R.id.notdone_saleConsume_layout);
        notdone_tagsRemind_layout=(RelativeLayout)findViewById(R.id.notdone_tagsRemind_layout);
        notdone_tagsRevisit_layout=(RelativeLayout)findViewById(R.id.notdone_tagsRevisit_layout);
        notdone_tagsActive_layout=(RelativeLayout)findViewById(R.id.notdone_tagsActive_layout);
        notdone_saleLog_layout=(RelativeLayout)findViewById(R.id.notdone_saleLog_layout);

        havedone_ordernote = (TextView)findViewById(R.id.havedone_ordernote);
        havedone_counttostore =(TextView)findViewById(R.id.havedone_counttostore);
        havedone_counttodoor =(TextView)findViewById(R.id.havedone_counttodoor);
        havedone_tagsnursing =(TextView)findViewById(R.id.havedone_tagsnursing);
        havedone_saleMedicalBeauty =(TextView)findViewById(R.id.havedone_saleMedicalBeauty);
        havedone_saleProject =(TextView)findViewById(R.id.havedone_saleProject);
        havedone_saleHealthBeauty =(TextView)findViewById(R.id.havedone_saleHealthBeauty);
        havedone_saleConsume =(TextView)findViewById(R.id.havedone_saleConsume);
        havedone_tagsRemind =(TextView)findViewById(R.id.havedone_tagsRemind);
        havedone_tagsRevisit =(TextView)findViewById(R.id.havedone_tagsRevisit);
        havedone_tagsActive =(TextView)findViewById(R.id.havedone_tagsActive);
        havedone_saleLog =(TextView)findViewById(R.id.havedone_saleLog);
        notdone_ordernote = (TextView)findViewById(R.id.notdone_ordernote);
        notdone_counttostore =(TextView)findViewById(R.id.notdone_counttostore);
        notdone_counttodoor =(TextView)findViewById(R.id.notdone_counttodoor);
        notdone_tagsnursing =(TextView)findViewById(R.id.notdone_tagsnursing);
        notdone_saleMedicalBeauty =(TextView)findViewById(R.id.notdone_saleMedicalBeauty);
        notdone_saleProject =(TextView)findViewById(R.id.notdone_saleProject);
        notdone_saleHealthBeauty =(TextView)findViewById(R.id.notdone_saleHealthBeauty);
        notdone_saleConsume =(TextView)findViewById(R.id.notdone_saleConsume);
        notdone_tagsRemind =(TextView)findViewById(R.id.notdone_tagsRemind);
        notdone_tagsRevisit =(TextView)findViewById(R.id.notdone_tagsRevisit);
        notdone_tagsActive =(TextView)findViewById(R.id.notdone_tagsActive);
        notdone_saleLog =(TextView)findViewById(R.id.notdone_saleLog);
        date_text.setText(day.replaceAll("-", "."));

        if(RoleEnum.STAFF.getCode().equals(bundle.getString("role_key"))){
            //生美、医美、产品、消耗、到店服务、上门服务、护理日志、提醒日志、回访日志、激活日志
            //到店服务
            havedone_counttostore_layout.setVisibility(View.VISIBLE);
            notdone_counttostore_layout.setVisibility(View.VISIBLE);
            //上门服务
            havedone_counttodoor_layout.setVisibility(View.VISIBLE);
            notdone_counttodoor_layout.setVisibility(View.VISIBLE);
            //护理日志
            havedone_tagsnursing_layout.setVisibility(View.VISIBLE);
            notdone_tagsnursing_layout.setVisibility(View.VISIBLE);
            //提醒日志
            havedone_tagsRemind_layout.setVisibility(View.VISIBLE);
            notdone_tagsRemind_layout.setVisibility(View.VISIBLE);
            //回访日志
            havedone_tagsRevisit_layout.setVisibility(View.VISIBLE);
            notdone_tagsRevisit_layout.setVisibility(View.VISIBLE);
            //激活日志
            havedone_tagsActive_layout.setVisibility(View.VISIBLE);
            notdone_tagsActive_layout.setVisibility(View.VISIBLE);
            //订单备注
            havedone_ordernote_layout.setVisibility(View.GONE);
            notdone_ordernote_layout.setVisibility(View.GONE);
            //销售日志
            havedone_saleLog_layout.setVisibility(View.GONE);
            notdone_saleLog_layout.setVisibility(View.GONE);
        }else if(RoleEnum.CONSULTANT.getCode().equals(bundle.getString("role_key"))){
            //生美、医美、产品、消耗、订单备注、销售日志
            //到店服务
            havedone_counttostore_layout.setVisibility(View.GONE);
            notdone_counttostore_layout.setVisibility(View.GONE);
            //上门服务
            havedone_counttodoor_layout.setVisibility(View.GONE);
            notdone_counttodoor_layout.setVisibility(View.GONE);
            //护理日志
            havedone_tagsnursing_layout.setVisibility(View.GONE);
            notdone_tagsnursing_layout.setVisibility(View.GONE);
            //提醒日志
            havedone_tagsRemind_layout.setVisibility(View.GONE);
            notdone_tagsRemind_layout.setVisibility(View.GONE);
            //回访日志
            havedone_tagsRevisit_layout.setVisibility(View.GONE);
            notdone_tagsRevisit_layout.setVisibility(View.GONE);
            //激活日志
            havedone_tagsActive_layout.setVisibility(View.GONE);
            notdone_tagsActive_layout.setVisibility(View.GONE);
            //订单备注
            havedone_ordernote_layout.setVisibility(View.VISIBLE);
            notdone_ordernote_layout.setVisibility(View.VISIBLE);
            //销售日志
            havedone_saleLog_layout.setVisibility(View.VISIBLE);
            notdone_saleLog_layout.setVisibility(View.VISIBLE);
        }else if(RoleEnum.STOREMANAGER.getCode().equals(bundle.getString("role_key"))){
            //生美、医美、产品、消耗、订单备注
            //到店服务
            havedone_counttostore_layout.setVisibility(View.GONE);
            notdone_counttostore_layout.setVisibility(View.GONE);
            //上门服务
            havedone_counttodoor_layout.setVisibility(View.GONE);
            notdone_counttodoor_layout.setVisibility(View.GONE);
            //护理日志
            havedone_tagsnursing_layout.setVisibility(View.GONE);
            notdone_tagsnursing_layout.setVisibility(View.GONE);
            //提醒日志
            havedone_tagsRemind_layout.setVisibility(View.GONE);
            notdone_tagsRemind_layout.setVisibility(View.GONE);
            //回访日志
            havedone_tagsRevisit_layout.setVisibility(View.GONE);
            notdone_tagsRevisit_layout.setVisibility(View.GONE);
            //激活日志
            havedone_tagsActive_layout.setVisibility(View.GONE);
            notdone_tagsActive_layout.setVisibility(View.GONE);
            //订单备注
            havedone_ordernote_layout.setVisibility(View.VISIBLE);
            notdone_ordernote_layout.setVisibility(View.VISIBLE);
            //销售日志
            havedone_saleLog_layout.setVisibility(View.GONE);
            notdone_saleLog_layout.setVisibility(View.GONE);
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
         * 点击上一天
         */
        pre_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = DateTimeUtils.getLastDate(day);
                date_text.setText(day.replaceAll("-", "."));
                loadData(day);
            }
        });
        /**
         * 点击下一天
         */
        next_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String next_day = DateTimeUtils.getNextDate(day);
                if(!DateTimeUtils.compareDate(new Date(),DateTimeUtils.parseDate(next_day,DateTimeUtils.DF_YYYY_MM_DD))){
                    day = next_day;
                    date_text.setText(day.replaceAll("-", "."));
                    loadData(day);
                }
            }
        });


        /**
         * 点击标签
         */
        tagcontainerLayout.setOnTagClickListener(new TagNoneView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                Log.e("TAG", "点击日报标签:" + text + "position:" + position);
                tag_context.setText(getContentById(report_tagList, position));
            }

            @Override
            public void onTagLongClick(final int position, String text) {

            }
        });


    }


    private void loadData(String day){
        ReportEntity param = new ReportEntity();
        param.setCalendarDay(day);
        param.setAccount(bundle.getString("account_name"));
        if(!StringUtils.isEmpty(param.getAccount())){
            HttpClient.getReportList(param, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("getReportListTAG", "成功返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        JSONObject data = JSON.parseObject(object.getString("data"));
                        JSONObject haveDone = JSON.parseObject(data.getString("haveDone"));
                        JSONObject notDone = JSON.parseObject(data.getString("notDone"));

                        report_tagList = JSONArray.parseArray(data.getString("tagList"), ReportEntity.class);
                        if(report_tagList!=null &&  report_tagList.size()>0){
                            List<String> item = new ArrayList<String>();
                            tagcontainerLayout.removeAllTags();
                            for (int i = 0; i < report_tagList.size(); i++) {
                                item.add(report_tagList.get(i).getTagName());
                            }
                            tagcontainerLayout.setTags(item);
                            tag_context.setText(report_tagList.get(0).getContent());
                        }else{
                            tag_context.setText("");
                        }

                        List<ReportEntity> superiorList = JSONArray.parseArray(data.getString("superiorList"), ReportEntity.class);
                        if(superiorList!=null && superiorList.size()>0){
                            for (int i=0;i<superiorList.size();i++){
                                if(i==0){
                                    one_superior.setVisibility(View.VISIBLE);
                                    one_superior.setText(superiorList.get(i).getUsername());
                                }
                                if(i==1){
                                    two_superior.setVisibility(View.VISIBLE);
                                    two_superior.setText(superiorList.get(i).getUsername());
                                }
                                if(i==2){
                                    three_superior.setVisibility(View.VISIBLE);
                                    three_superior.setText(superiorList.get(i).getUsername());
                                }
                                if(i==3){
                                    four_superior.setVisibility(View.VISIBLE);
                                    four_superior.setText(superiorList.get(i).getUsername());
                                }
                                if(i==4){
                                    five_superior.setVisibility(View.VISIBLE);
                                    five_superior.setText(superiorList.get(i).getUsername());
                                }
                                if(i==5){
                                    six_superior.setVisibility(View.VISIBLE);
                                    six_superior.setText(superiorList.get(i).getUsername());
                                }
                                if(i==6){
                                    seven_superior.setVisibility(View.VISIBLE);
                                    seven_superior.setText(superiorList.get(i).getUsername());
                                }
                            }
                        }

                        //订单备注
                        if (!StringUtils.isEmpty(haveDone.getString("orderNote"))&& !RoleEnum.STAFF.getCode().equals(bundle.getString("role_key"))) {
                            havedone_ordernote_layout.setVisibility(View.VISIBLE);
                            havedone_ordernote.setText(haveDone.getString("orderNote") + "次");
                        }else{
                            havedone_ordernote_layout.setVisibility(View.GONE);
                        }
                        //销售日志
                        if (!StringUtils.isEmpty(haveDone.getString("saleLog"))&& RoleEnum.CONSULTANT.getCode().equals(bundle.getString("role_key"))) {
                            havedone_saleLog_layout.setVisibility(View.VISIBLE);
                            havedone_saleLog.setText(haveDone.getString("saleLog") + "次");
                        }else{
                            havedone_saleLog_layout.setVisibility(View.GONE);
                        }
                        //护理日志次数
                        if (!StringUtils.isEmpty(haveDone.getString("tagsNursing")) && RoleEnum.STAFF.getCode().equals(bundle.getString("role_key"))) {
                            havedone_tagsnursing_layout.setVisibility(View.VISIBLE);
                            havedone_tagsnursing.setText(haveDone.getString("tagsNursing") + "次");
                        }else{
                            havedone_tagsnursing_layout.setVisibility(View.GONE);
                        }
                        //提醒日志次数
                        if (!StringUtils.isEmpty(haveDone.getString("tagsRemind"))&& RoleEnum.STAFF.getCode().equals(bundle.getString("role_key"))) {
                            havedone_tagsRemind_layout.setVisibility(View.VISIBLE);
                            havedone_tagsRemind.setText(haveDone.getString("tagsRemind") + "次");
                        }else{
                            havedone_tagsRemind_layout.setVisibility(View.GONE);
                        }
                        //回访日志次数
                        if (!StringUtils.isEmpty(haveDone.getString("tagsRevisit"))&& RoleEnum.STAFF.getCode().equals(bundle.getString("role_key"))) {
                            havedone_tagsRevisit_layout.setVisibility(View.VISIBLE);
                            havedone_tagsRevisit.setText(haveDone.getString("tagsActive") + "次");
                        }else{
                            havedone_tagsRevisit_layout.setVisibility(View.GONE);
                        }
                        //激活日志次数
                        if (!StringUtils.isEmpty(haveDone.getString("tagsActive"))&& RoleEnum.STAFF.getCode().equals(bundle.getString("role_key"))) {
                            havedone_tagsActive_layout.setVisibility(View.VISIBLE);
                            havedone_tagsActive.setText(haveDone.getString("tagsActive") + "次");
                        }else{
                            havedone_tagsActive_layout.setVisibility(View.GONE);
                        }
                        //产品销售金额
                        if (!StringUtils.isEmpty(haveDone.getString("saleProject"))) {
                            havedone_saleProject_layout.setVisibility(View.VISIBLE);
                            havedone_saleProject.setText(haveDone.getString("saleProject") + "元");
                        }
                        //生美预收金额
                        if (!StringUtils.isEmpty(haveDone.getString("saleHealthBeauty"))) {
                            havedone_saleHealthBeauty_layout.setVisibility(View.VISIBLE);
                            havedone_saleHealthBeauty.setText(haveDone.getString("saleHealthBeauty") + "元");
                        }
                        //医美预收金额
                        if (!StringUtils.isEmpty(haveDone.getString("saleMedicalBeauty"))) {
                            havedone_saleMedicalBeauty_layout.setVisibility(View.VISIBLE);
                            havedone_saleMedicalBeauty.setText(haveDone.getString("saleMedicalBeauty") + "元");
                        }
                        //消耗金额
                        if (!StringUtils.isEmpty(haveDone.getString("saleConsume"))) {
                            havedone_saleConsume_layout.setVisibility(View.VISIBLE);
                            havedone_saleConsume.setText(haveDone.getString("saleConsume") + "元");
                        }

                        //到店次数
                        if (!StringUtils.isEmpty(haveDone.getString("counttostore")) && RoleEnum.STAFF.getCode().equals(bundle.getString("role_key"))) {
                            havedone_counttostore_layout.setVisibility(View.VISIBLE);
                            havedone_counttostore.setText(haveDone.getString("counttostore") + "次");
                        }else{
                            havedone_counttostore_layout.setVisibility(View.GONE);
                        }
                        //上门次数
                        if (!StringUtils.isEmpty(haveDone.getString("counttodoor"))&& RoleEnum.STAFF.getCode().equals(bundle.getString("role_key"))) {
                            havedone_counttodoor_layout.setVisibility(View.VISIBLE);
                            havedone_counttodoor.setText(haveDone.getString("counttodoor") + "次");
                        }else{
                            havedone_counttodoor_layout.setVisibility(View.GONE);
                        }

                        //订单备注
                        if (!StringUtils.isEmpty(notDone.getString("orderNote"))&& !RoleEnum.STAFF.getCode().equals(bundle.getString("role_key"))) {
                            notdone_ordernote_layout.setVisibility(View.VISIBLE);
                            notdone_ordernote.setText(notDone.getString("orderNote") + "次");
                        }else{
                            notdone_ordernote_layout.setVisibility(View.GONE);
                        }
                        //销售日志
                        if (!StringUtils.isEmpty(notDone.getString("saleLog"))&& RoleEnum.CONSULTANT.getCode().equals(bundle.getString("role_key"))) {
                            notdone_saleLog_layout.setVisibility(View.VISIBLE);
                            notdone_saleLog.setText(notDone.getString("saleLog") + "次");
                        }else{
                            notdone_saleLog_layout.setVisibility(View.GONE);
                        }
                        //护理日志次数
                        if (!StringUtils.isEmpty(notDone.getString("tagsNursing"))&& RoleEnum.STAFF.getCode().equals(bundle.getString("role_key"))) {
                            notdone_tagsnursing_layout.setVisibility(View.VISIBLE);
                            notdone_tagsnursing.setText(notDone.getString("tagsNursing") + "次");
                        }else{
                            notdone_tagsnursing_layout.setVisibility(View.GONE);
                        }
                        //提醒日志次数
                        if (!StringUtils.isEmpty(notDone.getString("tagsRemind"))&& RoleEnum.STAFF.getCode().equals(bundle.getString("role_key"))) {
                            notdone_tagsRemind_layout.setVisibility(View.VISIBLE);
                            notdone_tagsRemind.setText(notDone.getString("tagsRemind") + "次");
                        }else{
                            notdone_tagsRemind_layout.setVisibility(View.GONE);
                        }
                        //回访日志次数
                        if (!StringUtils.isEmpty(notDone.getString("tagsRevisit"))&& RoleEnum.STAFF.getCode().equals(bundle.getString("role_key"))) {
                            notdone_tagsRevisit_layout.setVisibility(View.VISIBLE);
                            notdone_tagsRevisit.setText(notDone.getString("tagsRevisit") + "次");
                        }else{
                            notdone_tagsRevisit_layout.setVisibility(View.GONE);
                        }
                        //激活日志次数
                        if (!StringUtils.isEmpty(notDone.getString("tagsActive"))&& RoleEnum.STAFF.getCode().equals(bundle.getString("role_key"))) {
                            notdone_tagsActive_layout.setVisibility(View.VISIBLE);
                            notdone_tagsActive.setText(notDone.getString("tagsActive") + "次");
                        }else{
                            notdone_tagsActive_layout.setVisibility(View.GONE);
                        }
                        //产品销售金额
                        if (!StringUtils.isEmpty(notDone.getString("saleProject"))) {
                            havedone_saleProject_layout.setVisibility(View.VISIBLE);
                            havedone_saleProject.setText(notDone.getString("saleProject") + "元");
                        }
                        //生美预收金额
                        if (!StringUtils.isEmpty(notDone.getString("saleHealthBeauty"))) {
                            notdone_saleHealthBeauty_layout.setVisibility(View.VISIBLE);
                            notdone_saleHealthBeauty.setText(notDone.getString("saleHealthBeauty") + "元");
                        }
                        //医美预收金额
                        if (!StringUtils.isEmpty(notDone.getString("saleMedicalBeauty"))) {
                            notdone_saleMedicalBeauty_layout.setVisibility(View.VISIBLE);
                            notdone_saleMedicalBeauty.setText(notDone.getString("saleMedicalBeauty") + "元");
                        }
                        //消耗金额
                        if (!StringUtils.isEmpty(notDone.getString("saleConsume"))) {
                            notdone_saleConsume_layout.setVisibility(View.VISIBLE);
                            notdone_saleConsume.setText(notDone.getString("saleConsume") + "元");
                        }
                        //到店次数
                        if (!StringUtils.isEmpty(notDone.getString("counttostore")) && RoleEnum.STAFF.getCode().equals(bundle.getString("role_key"))) {
                            notdone_counttostore_layout.setVisibility(View.VISIBLE);
                            notdone_counttostore.setText(notDone.getString("counttostore") + "次");
                        }else{
                            notdone_counttostore_layout.setVisibility(View.GONE);
                        }
                        //上门次数
                        if (!StringUtils.isEmpty(notDone.getString("counttodoor")) && RoleEnum.STAFF.getCode().equals(bundle.getString("role_key"))) {
                            notdone_counttodoor_layout.setVisibility(View.VISIBLE);
                            notdone_counttodoor.setText(notDone.getString("counttodoor") + "次");
                        }else{
                            notdone_counttodoor_layout.setVisibility(View.GONE);
                        }
                    } else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(ReportViewActivity.this);
                    }else {
                        Log.i("getReportListTAG", "获取数据失败:" + body);
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("getReportListTAG", "失败返回数据:" + request.toString());
                }
            }, ReportViewActivity.this);
        }else{
            Toast.makeText(this,"缺少请求参数[accout]",Toast.LENGTH_LONG).show();
        }

    }


    /**
     * 根据Tag名称获取名称
     * @param list
     * @param tagId
     * @return
     */
    private String getContentById(List<ReportEntity> list,int tagId){
        if(list==null || list.size()==0 || StringUtils.isEmpty(tagId+"")){
            return "";
        }
        if(tagId<=list.size()){
            return list.get(tagId).getContent();
        }
        return "";
    }

}
