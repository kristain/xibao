package com.drjing.xibao.module.application.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.module.entity.CategroyEntity;
import com.drjing.xibao.module.entity.CategroyEnum;
import com.drjing.xibao.module.ui.UIHelper;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.List;

/**
 * 我的应用
 * Created by kristain on 15/12/21.
 */
public class ApplicationFragment extends Fragment {

    private Activity context;
    private View root;
    private TextView textHeadTitle;
    private RelativeLayout product_layout,knowlege_layout,market_layout,enterprise_layout,intract_layout,shop_layout;
    private String product_id,knowlege_id,market_id,enterprise_id,intract_id,shop_id;
    private LinearLayout main_layout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return root = inflater.inflate(R.layout.fragment_application, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        initView();
    }

    void initView() {
        textHeadTitle = (TextView) root.findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("应用");
        product_layout = (RelativeLayout) root.findViewById(R.id.product_layout);
        knowlege_layout = (RelativeLayout) root.findViewById(R.id.knowlege_layout);
        market_layout = (RelativeLayout) root.findViewById(R.id.market_layout);
        enterprise_layout = (RelativeLayout) root.findViewById(R.id.enterprise_layout);
        intract_layout = (RelativeLayout) root.findViewById(R.id.intract_layout);
        shop_layout =  (RelativeLayout) root.findViewById(R.id.shop_layout);
        main_layout = (LinearLayout)root.findViewById(R.id.main_layout);

        //产品库
        product_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("title_text", "产品库");
                bundle.putString("category_id", product_id);
                UIHelper.showArtcleList(getActivity(), bundle);
            }
        });

        //知识库
        knowlege_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("title_text", "知识库");
                bundle.putString("category_id", knowlege_id);
                UIHelper.showArtcleList(getActivity(), bundle);
            }
        });

        //喜阅教育
        market_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("title_text", "喜阅教育");
                bundle.putString("category_id", market_id);
                UIHelper.showArtcleList(getActivity(), bundle);
            }
        });


        //企业微刊
        enterprise_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle =new Bundle();
                bundle.putString("title_text","企业微刊");
                bundle.putString("category_id", enterprise_id);
                UIHelper.showArtcleList(getActivity(),bundle);
            }
        });

        //喜鹊互动
        intract_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("title_text", "喜鹊互动");
                bundle.putString("category_id", intract_id);
                UIHelper.showArtcleList(getActivity(), bundle);
            }
        });

        //我的小店
        shop_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("title_text", "我的小店");
                bundle.putString("category_id", shop_id);
                UIHelper.showArtcleList(getActivity(), bundle);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        CategroyEntity param = new CategroyEntity();
        param.setCatetype(CategroyEnum.CMS.code);
        HttpClient.getCateGoryList(param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String body) {
                Log.i("getCateGoryListTAG", "成功返回数据:" + body);
                JSONObject object = JSON.parseObject(body);
                if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                    List<CategroyEntity> list = JSONArray.parseArray(object.getString("data"), CategroyEntity.class);
                    for (int i=0;i<list.size();i++){
                        if("产品库".equals(list.get(i).getName())){
                            product_layout.setVisibility(View.VISIBLE);
                            product_id = list.get(i).getId();
                        }
                        if("知识库".equals(list.get(i).getName())){
                            knowlege_layout.setVisibility(View.VISIBLE);
                            market_id=list.get(i).getId();
                        }
                        if("企业微刊".equals(list.get(i).getName())){
                            enterprise_layout.setVisibility(View.VISIBLE);
                            enterprise_id=list.get(i).getId();
                        }
                        if("喜阅教育".equals(list.get(i).getName())){
                            market_layout.setVisibility(View.VISIBLE);
                            market_id=list.get(i).getId();
                        }

                        if("喜鹊互动".equals(list.get(i).getName())){
                            intract_layout.setVisibility(View.VISIBLE);
                            market_id=list.get(i).getId();
                        }
                        if("我的小店".equals(list.get(i).getName())){
                            shop_layout.setVisibility(View.VISIBLE);
                            market_id=list.get(i).getId();
                        }
                    }
                    main_layout.setVisibility(View.VISIBLE);
                } else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                    UIHelper.showLogin(getActivity());
                }else{
                    Log.i("getCateGoryListTAG", "获取数据失败:" + body);
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                Log.i("getCateGoryListTAG", "失败返回数据:" + request.toString());
            }
        }, context);
    }
}
