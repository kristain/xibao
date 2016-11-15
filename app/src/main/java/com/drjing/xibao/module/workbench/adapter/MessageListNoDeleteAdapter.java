package com.drjing.xibao.module.workbench.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.module.entity.MessageEntity;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.List;

/**
 * Created by kristain on 15/12/31.
 */
public class MessageListNoDeleteAdapter extends RecyclerView.Adapter<MessageHolder>{

    List<MessageEntity> contents;

    Context ctx;

    private String type;

    public MessageListNoDeleteAdapter(List<MessageEntity> contents, Context ctx, String type)
    {
        this.ctx = ctx;
        this.type = type;
        this.contents = contents;
    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, final int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_card_small_no_delete, parent, false);
        return new MessageHolder(view , new MessageHolder.IMyViewHolderClicks()
        {
            @Override
            public void onPlay(View caller)
            {
                //Toast.makeText(ctx, contents.get((int) caller.getTag()).getId()+contents.get((int) caller.getTag()).getMsg(), Toast.LENGTH_LONG).show();
                Intent intent =  ( (Activity)ctx).getIntent();
                intent.putExtra("msg", contents.get((int) caller.getTag()).getMsg());
                ( (Activity)ctx).setResult(((Activity) ctx).RESULT_OK, intent);
                ( (Activity)ctx).finish();
            }
        }){};

    }



    /**
     * 删除短信模板
     */
    private void deleletTemplate(int id){
        MessageEntity param = new MessageEntity();
        param.setId(id);
        if(!StringUtils.isEmpty(param.getId()+"")){
            HttpClient.deleteInfoTemplate(param, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    JSONObject object = JSON.parseObject(body);
                    Log.i("deleteInfoTemplateTAG", "成功返回数据:" + body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        notifyDataSetChanged();
                    } else {
                        Log.i("deleteInfoTemplateTAG", "成功返回数据:" + body);
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("deleteInfoTemplateTAG", "失败返回数据:" + request.toString());
                }
            }, ctx);
        }else{
            Toast.makeText(ctx,"缺少请求参数[id]",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onBindViewHolder(MessageHolder holder, int position)
    {
        MessageEntity song = contents.get(position);
        holder.bindSong(song,position);
    }



    @Override
    public int getItemCount() {
        return contents.size();
    }
}
