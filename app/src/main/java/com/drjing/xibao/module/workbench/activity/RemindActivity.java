package com.drjing.xibao.module.workbench.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.library.third.ormlite.dao.Dao;
import com.drjing.xibao.R;
import com.drjing.xibao.common.http.AsyncHttpResponseHandler;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.utils.FuncUtils;
import com.drjing.xibao.common.view.dialog.Effectstype;
import com.drjing.xibao.common.view.dialog.NiftyDialogBuilder;
import com.drjing.xibao.common.view.materialwidget.PaperButton;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.config.AppConfig;
import com.drjing.xibao.module.entity.CustomerEntity;
import com.drjing.xibao.module.entity.NurseTagEntity;
import com.drjing.xibao.module.entity.OrderTypeEnum;
import com.drjing.xibao.module.entity.SearchParam;
import com.drjing.xibao.module.entity.SendWayEnum;
import com.drjing.xibao.module.performance.adapter.BaseAdapterHelper;
import com.drjing.xibao.module.performance.adapter.QuickAdapter;
import com.drjing.xibao.module.ui.UIHelper;
import com.drjing.xibao.provider.db.DatabaseHelper;
import com.drjing.xibao.provider.db.entity.User;
import com.kristain.common.utils.DateTimeUtils;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Request;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * 激活日志、回访日志、提醒日志
 * Created by kristain on 16/1/20.
 */
public class RemindActivity extends SwipeBackActivity implements IWXAPIEventHandler {

    /**
     * 后退按钮
     */
    private Button btnBack;

    /**
     * Title栏文案
     */
    private TextView textHeadTitle;


    private PaperButton select_template_btn,save_btn;

    private ListView list_view;

    private RadioButton call_btn, wechat_btn, msg_btn;

    private EditText msg_content;
    private Bundle bundle;

    private DatabaseHelper dbHelper;
    private Dao<User, String> userDao;
    private User user;
    QuickAdapter<NurseTagEntity> adapter;
    /**
     * 选择的模版
     */
    private String select_msg_id = "0";

    NiftyDialogBuilder dialogBuilder;


    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind_log);
        api = WXAPIFactory.createWXAPI(this, AppConfig.WX_APP_ID);
        api.registerApp(AppConfig.WX_APP_ID);
        api.handleIntent(getIntent(), this);
        bundle = getIntent().getExtras();
        dbHelper = DatabaseHelper.gainInstance(this, AppConfig.DB_NAME, AppConfig.DB_VERSION);
        try {
            userDao = (Dao<User, String>) dbHelper.getDao(User.class);
            List<User> users = userDao.queryBuilder().query();
            if (users == null || users.size() == 0 || StringUtils.isEmpty(users.get(0).getId())) {
                UIHelper.showLogin(this);
                return;
            }
            user = users.get(0);
        } catch (SQLException e) {
            e.printStackTrace();
            UIHelper.showLogin(this);
        }
        initView();
        initEvent();
        getActiveLogList();
    }


    private void initView() {
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText(OrderTypeEnum.getMsgByCode(bundle.getString("order_type")));
        select_template_btn = (PaperButton) findViewById(R.id.select_template_btn);
        save_btn = (PaperButton)findViewById(R.id.save_btn);
        list_view = (ListView) findViewById(R.id.list_view);

        call_btn = (RadioButton) findViewById(R.id.call_btn);
        wechat_btn = (RadioButton) findViewById(R.id.wechat_btn);
        msg_btn = (RadioButton) findViewById(R.id.msg_btn);
        msg_content = (EditText) findViewById(R.id.msg_content);

        adapter = new QuickAdapter<NurseTagEntity>(this, R.layout.remind_log_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, NurseTagEntity item) {
                helper.setText(R.id.account, item.getUserName())
                        .setText(R.id.sendWay, SendWayEnum.getMsgByCode(item.getSendWay()))
                        .setText(R.id.date, (StringUtils.isEmpty(item.getSendTime()) ? "" : DateTimeUtils.formatDateTime(Long.parseLong(item.getSendTime()), DateTimeUtils.YY_MM_DD)))
                        .setText(R.id.staffname, item.getRoleName())
                                .setText(R.id.content_msg, item.getContent());
                if (SendWayEnum.CALL.getCode().equals(item.getSendWay())) {
                    ((RadioButton) helper.getView().findViewById(R.id.sendWay)).setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_txrz_call), null, null, null);
                } else if (SendWayEnum.MESSAGE.getCode().equals(item.getSendWay())) {
                    ((RadioButton) helper.getView().findViewById(R.id.sendWay)).setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_txrz_msg), null, null, null);
                } else if (SendWayEnum.WECHAT.getCode().equals(item.getSendWay())) {
                    ((RadioButton) helper.getView().findViewById(R.id.sendWay)).setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_txrz_wechat), null, null, null);
                }
            }
        };
    }


    private void initEvent() {
        /**
         * 点击存为模版
         */
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isEmpty(msg_content.getText().toString())) {
                    Toast.makeText(RemindActivity.this, "请填写内容", Toast.LENGTH_LONG).show();
                    return;
                }
                addMsgTemplate();
            }
        });

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
         * 选择模版
         */
        select_template_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showMessageTemplate(RemindActivity.this);
            }
        });
        /**
         * 点击拨打电话
         */
        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isEmpty(msg_content.getText().toString())) {
                    Toast.makeText(RemindActivity.this, "请记录通话内容", Toast.LENGTH_LONG).show();
                    return;
                }
                Log.e("TAG"," 拨打电话mobile:"+bundle.getString("mobile"));
                dialogBuilder = NiftyDialogBuilder.getInstance(RemindActivity.this);
                dialogBuilder
                        .withTitle("提示")
                        .withTitleColor("#FFFFFF")
                        .withDividerColor("#11000000")
                        .withMessage("确定拨打电话吗？")
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
                                if (FuncUtils.isCellPhone(bundle.getString("mobile"))) {
                                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + bundle.getString("mobile")));
                                    startActivity(intent);
                                    addLog(select_msg_id, "call");
                                } else {
                                    Toast.makeText(RemindActivity.this, "电话号码格式不正确[" + bundle.getString("mobile") + "]", Toast.LENGTH_LONG).show();
                                }
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
        /**
         * 点击发送微信
         */
        wechat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isEmpty(msg_content.getText().toString())) {
                    Toast.makeText(RemindActivity.this, "请输入发送内容", Toast.LENGTH_LONG).show();
                    return;
                }
                wechatShare(0, msg_content.getText().toString(), msg_content.getText().toString());
                addLog(select_msg_id, "wechat");
            }
        });
        /**
         * 点击发送短信
         */
        msg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isEmpty(msg_content.getText().toString())) {
                    Toast.makeText(RemindActivity.this, "请输入发送内容", Toast.LENGTH_LONG).show();
                    return;
                }
                Log.e("TAG"," 发送短信mobile:"+bundle.getString("mobile"));
                dialogBuilder = NiftyDialogBuilder.getInstance(RemindActivity.this);
                dialogBuilder
                        .withTitle("确定发送短信吗？")
                        .withTitleColor("#FFFFFF")
                        .withDividerColor("#11000000")
                        .withMessage(msg_content.getText().toString())
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
                                if (FuncUtils.isCellPhone(bundle.getString("mobile"))) {
                                    SmsManager sms = SmsManager.getDefault();
                                    sms.sendTextMessage(bundle.getString("mobile"), null, msg_content.getText().toString(), null, null);
                                    addLog(select_msg_id, "message");
                                } else {
                                    Toast.makeText(RemindActivity.this, "电话号码格式不正确[" + bundle.getString("mobile") + "]", Toast.LENGTH_LONG).show();
                                }
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


    /**
     * 获取激活日志、回访日志、提醒日志列表
     */
    private void getActiveLogList() {
        CustomerEntity entity = new CustomerEntity();
        if (StringUtils.isEmpty(bundle.getString("customer_id"))) {
            Toast.makeText(RemindActivity.this, "缺少请求参数[customer_id]",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        entity.setCustomerId(Integer.parseInt(bundle.getString("customer_id")));
        entity.setAccountName(user.getAccountname());
        if (OrderTypeEnum.REVISITORDER.getCode().equals(bundle.getString("order_type"))) {
            //回访日志
            HttpClient.getVisitLogList(entity, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("getVisitLogListTAG", "返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        List<NurseTagEntity> list = JSONArray.parseArray(object.getString("data"), NurseTagEntity.class);
                        adapter.clear();
                        adapter.addAll(list);
                        list_view.setAdapter(adapter);
                    } else if (HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(RemindActivity.this);
                    } else {
                        Toast.makeText(RemindActivity.this, "获取失败",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("getVisitLogListTAG", "失败返回数据:" + request.toString());
                }
            }, RemindActivity.this);
        } else if (OrderTypeEnum.REMINDORDER.getCode().equals(bundle.getString("order_type"))) {
            //提醒日志
            HttpClient.getMessageLogList(entity, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("getMessageLogListTAG", "返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        List<NurseTagEntity> list = JSONArray.parseArray(object.getString("data"), NurseTagEntity.class);
                        adapter.clear();
                        adapter.addAll(list);
                        list_view.setAdapter(adapter);
                    } else if (HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(RemindActivity.this);
                    } else {
                        Toast.makeText(RemindActivity.this, "获取失败",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("getMessageLogListTAG", "失败返回数据:" + request.toString());
                }
            }, RemindActivity.this);
        } else {
            HttpClient.getActiveLogList(entity, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String body) {
                    Log.i("getActiveLogListTAG", "返回数据:" + body);
                    JSONObject object = JSON.parseObject(body);
                    if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                        List<NurseTagEntity> list = JSONArray.parseArray(object.getString("data"), NurseTagEntity.class);
                        adapter.clear();
                        adapter.addAll(list);
                        list_view.setAdapter(adapter);
                    } else if (HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                        UIHelper.showLogin(RemindActivity.this);
                    } else {
                        Toast.makeText(RemindActivity.this, "获取失败",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.i("getActiveLogListTAG", "失败返回数据:" + request.toString());
                }
            }, RemindActivity.this);
        }
    }

    /**
     * 存为模版
     */
    private void addMsgTemplate(){
        SearchParam entity = new SearchParam();
        entity.setContent(msg_content.getText().toString());
        entity.setType("4");
        HttpClient.AddInfoTemplate(entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String body) {
                Log.i("AddInfoTemplateTAG", "返回数据:" + body);
                JSONObject object = JSON.parseObject(body);
                if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                    msg_content.setText("");
                    Toast.makeText(RemindActivity.this, "添加模版成功",
                            Toast.LENGTH_SHORT).show();
                } else if (HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                    UIHelper.showLogin(RemindActivity.this);
                } else {
                    Toast.makeText(RemindActivity.this, "添加模版失败",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                Log.i("AddInfoTemplateTAG", "失败返回数据:" + request.toString());
            }
        }, RemindActivity.this);
    }


    /**
     * 添加激活日志
     */
    private void addLog(String message_id, String sendway) {
        NurseTagEntity entity = new NurseTagEntity();
        entity.setMessageId(message_id);
        entity.setSendWay(sendway);
        entity.setOrderId(bundle.getString("order_id"));
        entity.setCustomerId(bundle.getString("customer_id"));
        entity.setContent(msg_content.getText().toString());
        if (OrderTypeEnum.REVISITORDER.getCode().equals(bundle.getString("order_type"))) {
            if (!StringUtils.isEmpty(entity.getMessageId())) {
                HttpClient.addVisitLog(entity, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String body) {
                        Log.i("addVisitLogTag", "返回数据:" + body);
                        JSONObject object = JSON.parseObject(body);
                        if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                            getActiveLogList();
                        } else if (HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                            UIHelper.showLogin(RemindActivity.this);
                        } else {
                            Log.i("addVisitLogTag", "返回数据:" + body);
                        }
                    }

                    @Override
                    public void onFailure(Request request, IOException e) {
                        Log.i("addVisitLogTag", "失败返回数据:" + request.toString());
                    }
                }, RemindActivity.this);
            } else {
                Toast.makeText(this, "缺少请求参数[messageId]", Toast.LENGTH_LONG).show();
            }

        } else if (OrderTypeEnum.REMINDORDER.getCode().equals(bundle.getString("order_type"))) {
            if (!StringUtils.isEmpty(entity.getMessageId())) {
                HttpClient.addMessageLog(entity, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String body) {
                        Log.i("addMessageLogTag", "返回数据:" + body);
                        JSONObject object = JSON.parseObject(body);
                        if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                            getActiveLogList();
                        } else if (HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                            UIHelper.showLogin(RemindActivity.this);
                        } else {
                            Log.i("addMessageLogTag", "返回数据:" + body);
                        }
                    }

                    @Override
                    public void onFailure(Request request, IOException e) {
                        Log.i("addActiveLogTag", "失败返回数据:" + request.toString());
                    }
                }, RemindActivity.this);
            } else {
                Toast.makeText(this, "缺少请求参数[messageId]", Toast.LENGTH_LONG).show();
            }

        } else {
            if (!StringUtils.isEmpty(entity.getMessageId())) {
                HttpClient.addActiveLog(entity, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String body) {
                        Log.i("addActiveLogTag", "返回数据:" + body);
                        JSONObject object = JSON.parseObject(body);
                        if (HttpClient.RET_SUCCESS_CODE.equals(object.getString("status"))) {
                             getActiveLogList();
                        } else if (HttpClient.UNLOGIN_CODE.equals(object.getString("status"))) {
                            UIHelper.showLogin(RemindActivity.this);
                        } else {
                            Log.i("addActiveLogTag", "返回数据:" + body);
                        }
                    }

                    @Override
                    public void onFailure(Request request, IOException e) {
                        Log.i("addActiveLogTag", "失败返回数据:" + request.toString());
                    }
                }, RemindActivity.this);
            } else {
                Toast.makeText(RemindActivity.this, "缺少请求参数[messageId]", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (resultCode) {
            case RESULT_OK:
                if(intent!=null){
                    Bundle b=intent.getExtras(); //data为B中回传的Intent
                    if(!StringUtils.isEmpty(b.getString("msg"))){
                        msg_content.setText(b.getString("msg"));
                    }
                }
                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != dbHelper) dbHelper.releaseAll();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    private void wechatShare(int flag, String text, String description) {
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = description;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text");
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    @Override
    public void onReq(BaseReq arg0) {

    }

    @Override
    public void onResp(BaseResp resp) {
        Toast.makeText(this, "resp.errCode:" + resp.errCode + "resp.errStr:" + resp.errStr, Toast.LENGTH_LONG).show();
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                //分享成功
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //分享取消
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                //分享拒绝
                break;
        }
    }
}
