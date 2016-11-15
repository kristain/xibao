package com.drjing.xibao.module.workbench.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.view.materialviewpager.adapter.RecyclerViewMaterialAdapter;
import com.drjing.xibao.module.entity.MessageEntity;
import com.drjing.xibao.module.ui.UIHelper;
import com.drjing.xibao.module.workbench.adapter.MessageListNoDeleteAdapter;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 提醒短信模版列表页面
 * Created by kristain on 15/12/20.
 */
public class NoticeMessageFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    ArrayList<MessageEntity> cards;
    private static NoticeMessageFragment instance;

    public static NoticeMessageFragment newInstance(String content) {
        if (instance == null) {
            instance = new NoticeMessageFragment();
        }
        return instance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_swiperefresh_layout, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        //设置下拉刷新监听
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /*new Handler().postDelayed(new Runnable() {
                    public void run() {
                        //new GetDataTask().execute();
                        getInfoTemplateList();
                    }
                }, 500);*/
                getInfoTemplateList();
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        //new GetDataTask().execute();
        getInfoTemplateList();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        swipeRefreshLayout.setRefreshing(false);
    }

    private class GetDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            // Simulates a background job.
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {

            for (int k = 0; k <= 4; k++) {
                MessageEntity obj = new MessageEntity();
                obj.setMsg("3亲爱的,今天有小雨加雪，降温幅度比较大，记得多加衣服，注意保暖！世界那么大，感谢我们相识，家祝您及家人周末愉快！天天好心情！");
                cards.add(obj);
            }
            mAdapter.notifyDataSetChanged();
            //停止刷新动画
            swipeRefreshLayout.setRefreshing(false);
            super.onPostExecute(result);
        }
    }


    /**
     * 获取短信模板列表
     */
    private void getInfoTemplateList(){
        MessageEntity param = new MessageEntity();
        param.setPage(0);
        param.setPageSize(100);
        param.setType("3");
        HttpClient.getInfoTemplateList(param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String body) {
                JSONObject object = JSON.parseObject(body);
                Log.i("getInfoTemplateListTAG", "成功返回数据:" + body);
                if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                    JSONObject data = JSON.parseObject(object.getString("data"));
                    List<MessageEntity> tagList = JSONArray.parseArray(data.getString("list"), MessageEntity.class);
                    cards = new ArrayList<MessageEntity>();
                    for (int k = 0; k <tagList.size(); k++) {
                        MessageEntity obj = new MessageEntity();
                        obj.setId(tagList.get(k).getId());
                        obj.setMsg(tagList.get(k).getContent());
                        cards.add(obj);
                    }
                    mAdapter = new RecyclerViewMaterialAdapter(new MessageListNoDeleteAdapter(cards, getActivity(),"3"));
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    //停止刷新动画
                    swipeRefreshLayout.setRefreshing(false);
                } else if(HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                    UIHelper.showLogin(getActivity());
                }else {
                    Log.i("getInfoTemplateListTAG", "成功返回数据:" + body);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                Log.i("getInfoTemplateListTAG", "失败返回数据:" + request.toString());
                swipeRefreshLayout.setRefreshing(false);
            }
        }, getActivity());
    }
}

