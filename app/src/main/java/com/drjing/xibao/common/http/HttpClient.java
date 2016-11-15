package com.drjing.xibao.common.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.drjing.xibao.GlobalApplication;
import com.drjing.xibao.R;
import com.drjing.xibao.common.view.dialog.LoadingProgressDialog;
import com.drjing.xibao.module.entity.ActionPlanEntity;
import com.drjing.xibao.module.entity.ArticleEntity;
import com.drjing.xibao.module.entity.CategroyEntity;
import com.drjing.xibao.module.entity.CustomerEntity;
import com.drjing.xibao.module.entity.MessageEntity;
import com.drjing.xibao.module.entity.NurseTagEntity;
import com.drjing.xibao.module.entity.OrderEntity;
import com.drjing.xibao.module.entity.QuestionEntity;
import com.drjing.xibao.module.entity.ReportEntity;
import com.drjing.xibao.module.entity.ScheduleEntity;
import com.drjing.xibao.module.entity.SearchParam;
import com.drjing.xibao.module.entity.StoreEntity;
import com.drjing.xibao.module.entity.TargetEntity;
import com.drjing.xibao.module.entity.UserParam;
import com.drjing.xibao.module.entity.WalletEntity;
import com.kristain.common.utils.StringUtils;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * HTTP请求类
 * Created by kristain on 15/2/27.
 */
public class HttpClient {

    private static final OkHttpClient mOkHttpClient = new OkHttpClient();

    public static final String USER_NAME = "kristain";
    public static final String UTF_8 = "UTF-8";
    public static final int PAGE_SIZE = 20;

    public static final String HTTP_DOMAIN = "http://xibao.b-union.net:8080/DoctorCrm-http";
    //public static final String HTTP_DOMAIN = "http://120.26.123.233:8888/DoctorCrm-http";
    public static final String HTTP_UPLOAD_IMG_URL="http://120.26.123.233:8888/DoctorCrm-http";
   // private static final String HTTP_DOMAIN = "http://sye.zhongsou.com/ent/rest";
    private static final String SHOP_RECOMMEND = "dpSearch.recommendShop"; // 推荐商家
    private static final String DOLOGIN = "/api/login";//登录
    private static final String GETACCOUNTINFO="/account/{account}/info";//获取个人信息
    private static final String GETCOMPANYINFO="/account/{companyid}/company";//获取公司信息
    private static final String GETALLSUBLIST ="/account/{uid}/staff";//获取当前用户的所有下属列表信息
    private static final String GETNEXTSUBLIST="/account/{uid}/child";//获取当前用户的下一级员工列表信息
    private static final String GETADVISERLIST="/account/{company_id}/adviser";//获取公司所有顾问
    private static final String GETSTOREADVISERLIST="/account/{store_id}/adviser";//获取门店所有顾问
    private static final String GETSTORESTAFFLIST="/account/{store_id}/beautician";//获取门店所有美容师
    private static final String GETSENCONDCATEGORY="/category/{cid}/project";//获取二级项目分类列表
    private static final String GETWALLETLIST = "/wallet/list";//获取钱包列表
    private static final String DELETEWALLET = "/wallet/{id}/delete";//删除钱包
    private static final String ADDWALLET = "/wallet/add";//新增钱包
    private static final String GETCATEGORY = "/category/list"; //获取分类列表
    private static final String GETARTICLELIST="/cms/list";//获取文章列表
    private static final String GETARTICLEDETAIL="/cms/articleInfo";//获取文章详情
    private static final String GETCUSTOMERLIST="/customer/list";//获取客户列表
    private static final String ASSIGNECUSTOMER="/customer/{staff_id}/assigne";//分配客户
    private static final String GETCUSTOMERORDERLIST="/customer/{customer_id}/orders";//获取客户订单列表
    private static final String GETCUSTOMERCARDINFO="/customer/{customer_id}/card";//获取客户会员卡信息
    private static final String GETCUSTOMERDETAIL="/customer/{customer_id}/detail";//获取客户详情
    private static final String GETCUSTOMERQUERYCONDITION="/customer/{staff_id}/queryCondition";//获取待激活客户删选条件
    private static final String GETSCHEDULELIST = "/schedule/listbydate";//获取日程
    private static final String MODIFYSCHEDULE="/schedule/{id}/update";// 修改日程
    private static final String DELETESCHEDULE="/schedule/{id}/delete";//删除日程
    private static final String ADDSCHEDULE="/schedule/add";//添加日程
    private static final String GETMESSAGELIST="/infoTemplate/list";//获取短信模板列表
    private static final String ADDMESSAGE="/infoTemplate/add";//增加短信模板
    private static final String DELETEMESSAGE="/infoTemplate/{id}/delete";//删除短信模版
    private static final String GETORDERLIST="/user/orders";//获取订单列表
    private static final String ORDERDETAIL="/user/order/{order_id}";//获取订单详情
    private static final String ORDERNOTE="/user/order/{order_id}/note";//添加订单备注
    private static final String ADVISERSALELOG="/order/adviser/tagSale/{order_id}";//顾问确认销售日志
    private static final String ADDTARGET="/target/add";//设定目标
    private static final String GETTARGETLIST="/target/list";//获取目标列表
    private static final String DELETETARGET="/target/{id}";//删除目标
    private static final String GETCOMPANYSTORELIST="/account/{uid}/store";//获取公司门店列表
    private static final String TARGETINDEX="/target/index";//获取绩效首页数据
    private static final String MODIFYPASSWD="/account/updatePassword";//密码修改
    private static final String GETSALARYLIST="/account/{uid}/salary";//获取工资列表
    private static final String REPORTLIST="/daliy/{account}";//获取日报数据
    private static final String REPORTTAGS="/daliy/tags";//获取日报标签
    private static final String GETSTOREREPORTLIST="/daliy/store/{storeid}";//通过门店id获取门店日报列表
    private static final String GETSTOREREPORTDETAIL="/daliy/store/{storeid}/detail";//通过门店id获取门店日报详细情况
    private static final String ADDREPORTTAG="/daliy/{dId}/content/{tagId}";//添加日报标签
    private static final String GETACTIONPLANLIST="/actionplan/list";//获取行动计划
    private static final String ADDACTIONPLAN="/actionplan/{uid}/add";//增加行动计划
    private static final String DELETEACTION="/actionplan/{customerId}/{arriveTime}";//删除行动计划
    private static final String GETNURINGTAGLIST="/order/{customerId}/tagNursing/list";//获取护理日志标签
    private static final String ADDNURINGTAG="/order/tagNursing/add";//新增护理日志标签
    private static final String APPENDNURINGTAG="/order/tagNursing/{tagId}/addCount";//护理日志标签+1
    private static final String GETLIFETAGLIST="/order/{customerId}/tagLife/list";//获取私密生活日志标签
    private static final String ADDLIFETAG="/order/tagLife/add";//新增私密生活日志标签
    private static final String APPENDLIFETAG="/order/tagLife/{tagId}/addCount";//私密生活日志标签+1
    private static final String GETDATETAGLIST="/order/{customerId}/tagDate/list";//获取特殊如期标签
    private static final String ADDDATETAG="/order/tagDate/add";//新增特殊如期标签
    private static final String APPENDDATETAG="/order/tagDate/{tagId}/addCount";//特殊如期标签+1
    private static final String GETTOPICTAGLIST="/order/{customerId}/tagTopic/list";//获取私密话题日志标签
    private static final String ADDTOPICTAG="/order/tagTopic/add";//新增私密话题日志标签
    private static final String APPENDTOPICTAG="/order/tagTopic/{tagId}/addCount";//私密话题日志标签+1
    private static final String GETVISITLOGLIST="/visitLog/list";//获取回访日志
    private static final String ADDVISITLOG="/visitLog/{messageId}";//添加回访日志
    private static final String SALELOGLIST="/order/{order_id}/tagSale/list";//获取销售日志标签
    private static final String ADDSALELOG="/order/tagSale/add";//新增销售日志标签
    private static final String DELETESALELOG="/order/tagSale/{id}";//删除销售日志标签
    private static final String GETSALELOGBYCUSTOMER="/customer/{customerId}/tagSale/list";//根据顾客id获取销售日志
    private static final String GETMESSAGELOGLIST="/messageLog/list";//获取提醒日志
    private static final String ADDMESSAGELOG="/messageLog/{messageId}";//添加提醒日志
    private static final String GETACTIVELOGLIST="/activeLog/list";//获取激活日志
    private static final String ADDACTIVELOG="/activeLog/{messageId}";//添加激活日志
    private static final String UPLOADIMAGE="/account/image";//上传图片
    private static final String SUGGESTSUBMIT="/account/suggest";//问题反馈
    private static final String VERSIONCHECK="/account/version/2";//版本更新
    private static final String HOMEPAGEDATA = "/index/message";//首页动态数据




    public static final String RET_SUCCESS_CODE="0";
    public static final String UNLOGIN_CODE="1000";


    private static LoadingProgressDialog proDialog = null;
    static {
        mOkHttpClient.setConnectTimeout(120, TimeUnit.SECONDS);
        mOkHttpClient.setWriteTimeout(120, TimeUnit.SECONDS);
        mOkHttpClient.setReadTimeout(120, TimeUnit.SECONDS);
        mOkHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(GlobalApplication.getInstance()), CookiePolicy.ACCEPT_ALL));
        //proDialog = LoadingProgressDialog.getDialog();
    }

    /**
     * 不会开启异步线程。
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static Response execute(Request request) throws IOException {
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 开启异步线程访问网络
     *
     * @param request
     * @param httpResponseHandler
     */
    public static void enqueue(Request request, final AsyncHttpResponseHandler httpResponseHandler,Context mcontext, final boolean load_flag) {
        if(load_flag){
            startProgressDialog("加载中",mcontext);
        }
        mOkHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Response response) throws IOException {
                httpResponseHandler.sendSuccessMessage(response);
                if(load_flag) {
                    stopProgressDialog();
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                httpResponseHandler.sendFailureMessage(request, e);
                if(load_flag) {
                    stopProgressDialog();
                }
            }
        });
    }

    /**
     * 开启异步线程访问网络, 且不在意返回结果（实现空callback）
     *
     * @param request
     */
    public static void enqueue(Request request) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Response arg0) throws IOException {

            }

            @Override
            public void onFailure(Request arg0, IOException arg1) {

            }
        });
    }

    public static String getStringFromServer(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = execute(request);
        if (response.isSuccessful()) {
            String responseUrl = response.body().string();
            return responseUrl;
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * 这里使用了HttpClient的API。只是为了方便
     *
     * @param params
     * @return
     */
    public static String formatParams(List<BasicNameValuePair> params) {
        return URLEncodedUtils.format(params, UTF_8);
    }

    /**
     * 为HttpGet 的 url 方便的添加多个name value 参数。
     *
     * @param url
     * @param params
     * @return
     */
    public static String attachHttpGetParams(String url, List<BasicNameValuePair> params) {
        return url + "?" + formatParams(params);
    }

    /**
     * 为HttpGet 的 url 方便的添加1个name value 参数。
     *
     * @param url
     * @param name
     * @param value
     * @return
     */
    public static String attachHttpGetParam(String url, String name, String value) {
        return url + "?" + name + "=" + value;
    }

    public static boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) GlobalApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
        } catch (Exception e) {
            Log.v("Connectivity", e.getMessage());
        }
        return false;
    }

    private static String encodeParams(Map<String, Object> params) {
        String param = "{}";
        if (params != null) {
            JSONObject json = new JSONObject();
            try {
                for (String key : params.keySet()) {
                    json.put(key, params.get(key));
                }
                Log.i("net_params", json.toString());
                param = Base64.encodeToString(json.toString().getBytes(UTF_8), Base64.DEFAULT);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return param;
    }


    public static void get(String url, Map<String, Object> params, AsyncHttpResponseHandler httpResponseHandler,Context mcontext,final boolean load_flag) {
        if (!isNetworkAvailable()) {
            Toast.makeText(GlobalApplication.getInstance(), R.string.no_network_connection_toast, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i("GET请求交易TAG", "请求url:" + url + "请求参数:" + JSON.toJSONString(params));
        List<BasicNameValuePair> rq = new ArrayList<BasicNameValuePair>();
        if (params != null) {
            for (String key : params.keySet()) {
                rq.add(new BasicNameValuePair(key, params.get(key).toString()));
            }
        }
        Request request = new Request.Builder().url(attachHttpGetParams(HTTP_DOMAIN + url, rq)).build();
        enqueue(request, httpResponseHandler,mcontext,load_flag);
    }


    public static void post(String url, Map<String, Object> params, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        if (!isNetworkAvailable()) {
            Toast.makeText(mcontext, R.string.no_network_connection_toast, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i("Post请求交易TAG", "请求url:" + url + "请求参数:" + JSON.toJSONString(params));
        FormEncodingBuilder builder = new FormEncodingBuilder();
        if (params != null) {
            for (String key : params.keySet()) {
                builder.add(key,params.get(key).toString());
            }
        }
        Request request = new Request.Builder().url(HTTP_DOMAIN + url).post(builder.build()).build();
        enqueue(request, httpResponseHandler,mcontext,true);
    }


    public static void delete(String url, Map<String, Object> params, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        if (!isNetworkAvailable()) {
            Toast.makeText(GlobalApplication.getInstance(), R.string.no_network_connection_toast, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i("DELETE请求交易TAG", "请求url:" + url + "请求参数:" + JSON.toJSONString(params));
        Request request = new Request.Builder().delete().url(HTTP_DOMAIN + url).build();
        enqueue(request, httpResponseHandler, mcontext, false);
    }


    /**
     * 登录
     * @param param
     * @param httpResponseHandler
     */
    public static void postLogin(UserParam param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getAccountname())){
            params.put("accountname", param.getAccountname());
        }
        if(!StringUtils.isEmpty(param.getPassword())){
            params.put("password", param.getPassword());
        }
        post(DOLOGIN, params, httpResponseHandler, mcontext);
    }



    /**
     * 修改密码
     * @param param
     * @param httpResponseHandler
     */
    public static void postModifyPwd(UserParam param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getCurrent_password())) {
            params.put("current_password", param.getCurrent_password());
        }
        if(!StringUtils.isEmpty(param.getPassword())) {
            params.put("password", param.getPassword());
        }
        if(!StringUtils.isEmpty(param.getConfirm_password())) {
            params.put("confirm_password", param.getConfirm_password());
        }
        post(MODIFYPASSWD, params, httpResponseHandler, mcontext);
    }



    /**
     * 新增钱包
     * @param param
     * @param httpResponseHandler
     */
    public static void AddWallet(WalletEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getCategoryId()+"")) {
            params.put("category_id", param.getCategoryId());
        }
        if(!StringUtils.isEmpty(param.getAmount()+"")) {
            params.put("amount", param.getAmount());
        }
        if(!StringUtils.isEmpty(param.getPercent()+"")) {
            params.put("percent", param.getPercent());
        }
        if(!StringUtils.isEmpty(param.getRemarks())) {
            params.put("remarks", param.getRemarks());
        }
        if(!StringUtils.isEmpty(param.getAddtime()+"")) {
            params.put("edit_time", param.getAddtime());
        }
        post(ADDWALLET, params, httpResponseHandler, mcontext);
    }
    /**
     * 获取钱包列表
     * @param param
     * @param httpResponseHandler
     */
    public static void getWalletList(WalletEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getPage()+"")) {
            params.put("page", param.getPage());
        }
        params.put("pageSize","100");
        get(GETWALLETLIST, params, httpResponseHandler, mcontext, false);
    }


    /**
     * 获取客户会员卡信息
     * @param param
     * @param httpResponseHandler
     */
    public static void getCustomerCardInfo(CustomerEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        get(GETCUSTOMERCARDINFO.replace("{customer_id}", param.getId()+""), params, httpResponseHandler, mcontext,true);
    }

    /**
     * 获取文章列表
     * @param param
     * @param httpResponseHandler
     */
    public static void getArticleList(ArticleEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getPage()+"")) {
            params.put("CurrentPage", param.getPage());
        }
        if(!StringUtils.isEmpty(param.getCategoryId())){
            params.put("categoryId", param.getCategoryId());
        }
        get(GETARTICLELIST, params, httpResponseHandler, mcontext,true);
    }


    /**
     * 获取激活日志列表
     * @param param
     * @param httpResponseHandler
     */
    public static void getActiveLogList(CustomerEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getCustomerId()+"")) {
            params.put("customerId", param.getCustomerId());
        }
        if(!StringUtils.isEmpty(param.getAccountName())) {
            params.put("accountName", param.getAccountName());
        }
        get(GETACTIVELOGLIST, params, httpResponseHandler, mcontext,true);
    }
    /**
     * 获取回访日志列表
     * @param param
     * @param httpResponseHandler
     */
    public static void getVisitLogList(CustomerEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getCustomerId()+"")) {
            params.put("customerId", param.getCustomerId());
        }
        if(!StringUtils.isEmpty(param.getAccountName())) {
            params.put("accountName", param.getAccountName());
        }
        get(GETVISITLOGLIST, params, httpResponseHandler, mcontext,true);
    }

    /**
     * 获取提醒日志列表
     * @param param
     * @param httpResponseHandler
     */
    public static void getMessageLogList(CustomerEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getCustomerId()+"")) {
            params.put("customerId", param.getCustomerId());
        }
        if(!StringUtils.isEmpty(param.getAccountName())) {
            params.put("accountName",param.getAccountName());
        }
        get(GETMESSAGELOGLIST, params, httpResponseHandler, mcontext,true);
    }



    /**
     * 获取销售日志列表
     * @param param
     * @param httpResponseHandler
     */
    public static void getSaleLogList(OrderEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        get(SALELOGLIST.replace("{order_id}",param.getId()+""), params, httpResponseHandler, mcontext,true);
    }



    /**
     * 根据顾客id获取销售日志
     * @param param
     * @param httpResponseHandler
     */
    public static void getSaleLogListByCustomer(NurseTagEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        get(GETSALELOGBYCUSTOMER.replace("{customerId}",param.getCustomerId()), params, httpResponseHandler, mcontext,true);
    }


    /**
     * 获取行动计划列表
     * @param param
     * @param httpResponseHandler
     */
    public static void getActionPlanList(ActionPlanEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getCustomerId())) {
            params.put("customerId", param.getCustomerId());
        }
        if(!StringUtils.isEmpty(param.getMonth())) {
            params.put("month", param.getMonth());
        }
        if(!StringUtils.isEmpty(param.getUserId())) {
            params.put("userId", param.getUserId());
        }
        if(!StringUtils.isEmpty(param.getStoreId())) {
            params.put("storeId", param.getStoreId());
        }
        get(GETACTIONPLANLIST, params, httpResponseHandler, mcontext, true);
    }


    /**
     * 获取行动计划列表
     * @param param
     * @param httpResponseHandler
     */
    public static void addActionPlan(ActionPlanEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getCustomerId())) {
            params.put("customerId", param.getCustomerId());
        }
        if(!StringUtils.isEmpty(param.getCategoryId())) {
            params.put("categoryId", param.getCategoryId());
        }
        if(!StringUtils.isEmpty(param.getProjectids())) {
            params.put("projectids", param.getProjectids());
        }
        if(!StringUtils.isEmpty(param.getArriveTime())) {
            params.put("arriveTime", param.getArriveTime());
        }
        if(!StringUtils.isEmpty(param.getAmount())) {
            params.put("amount", param.getAmount());
        }
        post(ADDACTIONPLAN.replace("{uid}", param.getUserId()), params, httpResponseHandler, mcontext);
    }




    /**
     * 删除行动计划列表
     * @param param
     * @param httpResponseHandler
     */
    public static void deleteActionPlan(ActionPlanEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        delete(DELETEACTION.replace("{customerId}", param.getCustomerId()).replace("{arriveTime}", param.getArriveTime()), params, httpResponseHandler, mcontext);
    }

    /**
     * 新增销售日志列表
     * @param param
     * @param httpResponseHandler
     */
    public static void addSaleLog(NurseTagEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getCategoryId())) {
            params.put("categoryId", param.getCategoryId());
        }
        if(!StringUtils.isEmpty(param.getProjectIds())) {
            params.put("projectIds", param.getProjectIds());
        }
        if(!StringUtils.isEmpty(param.getAmount())) {
            params.put("amount", param.getAmount());
        }
        if(!StringUtils.isEmpty(param.getOrderId())){
            params.put("orderId",param.getOrderId());
        }
        if(!StringUtils.isEmpty(param.getCustomerId())){
            params.put("customerId",param.getCustomerId());
        }
        if(!StringUtils.isEmpty(param.getAdviserId())) {
            params.put("adviserId", param.getAdviserId());
        }
        post(ADDSALELOG, params, httpResponseHandler, mcontext);
    }

    /**
     * 删除销售日志
     * @param param
     * @param httpResponseHandler
     */
    public static void deleteSaleLog(NurseTagEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        delete(DELETESALELOG.replace("{id}", param.getId() + ""), params, httpResponseHandler, mcontext);
    }

    /**
     * 添加激活日志
     * @param param
     * @param httpResponseHandler
     */
    public static void addActiveLog(NurseTagEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getSendWay())) {
            params.put("sendWay", param.getSendWay());
        }
        if(!StringUtils.isEmpty(param.getCustomerId())) {
            params.put("customerId", param.getCustomerId());
        }
        if(!StringUtils.isEmpty(param.getContent())) {
            params.put("content", param.getContent());
        }
        post(ADDACTIVELOG.replace("{messageId}", param.getMessageId()), params, httpResponseHandler, mcontext);
    }

    /**
     * 添加回访日志
     * @param param
     * @param httpResponseHandler
     */
    public static void addVisitLog(NurseTagEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getSendWay())) {
            params.put("sendWay", param.getSendWay());
        }
        if(!StringUtils.isEmpty(param.getCustomerId())) {
            params.put("customerId", param.getCustomerId());
        }
        if(!StringUtils.isEmpty(param.getContent())) {
            params.put("content", param.getContent());
        }
        if(!StringUtils.isEmpty(param.getOrderId())){
            params.put("orderId",param.getOrderId());
        }
        post(ADDVISITLOG.replace("{messageId}", param.getMessageId()), params, httpResponseHandler, mcontext);
    }





    /**
     * 添加提醒日志
     * @param param
     * @param httpResponseHandler
     */
    public static void addMessageLog(NurseTagEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getSendWay())) {
            params.put("sendWay", param.getSendWay());
        }
        if(!StringUtils.isEmpty(param.getCustomerId())) {
            params.put("customerId", param.getCustomerId());
        }
        if(!StringUtils.isEmpty(param.getContent())) {
            params.put("content", param.getContent());
        }
        if(!StringUtils.isEmpty(param.getOrderId())){
            params.put("orderId",param.getOrderId());
        }
        post(ADDMESSAGELOG.replace("{messageId}", param.getMessageId()), params, httpResponseHandler, mcontext);
    }


    /**
     * 获取护理日志TAG列表
     * @param param
     * @param httpResponseHandler
     */
    public static void getNuringTagList(NurseTagEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        get(GETNURINGTAGLIST.replace("{customerId}", param.getCustomerId()), params, httpResponseHandler, mcontext, true);
    }

    /**
     * 获取特殊日期TAG列表
     * @param param
     * @param httpResponseHandler
     */
    public static void getSpecialTagList(NurseTagEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        get(GETDATETAGLIST.replace("{customerId}", param.getCustomerId()), params, httpResponseHandler, mcontext,true);
    }


    /**
     * 获取私密日志TAG列表
     * @param param
     * @param httpResponseHandler
     */
    public static void getLifeTagList(NurseTagEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        get(GETLIFETAGLIST.replace("{customerId}", param.getCustomerId()), params, httpResponseHandler, mcontext,true);
    }

    /**
     * 获取私密话题TAG列表
     * @param param
     * @param httpResponseHandler
     */
    public static void getTopicTagList(NurseTagEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        get(GETTOPICTAGLIST.replace("{customerId}", param.getCustomerId()), params, httpResponseHandler, mcontext, true);
    }

    /**
     * 新增护理日志标签
     * @param param
     * @param httpResponseHandler
     */
    public static void addNurseTag(NurseTagEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getTagName())){
            params.put("tagName",param.getTagName());
        }
        if(!StringUtils.isEmpty(param.getCustomerId())){
            params.put("customerId",param.getCustomerId());
        }
        if(!StringUtils.isEmpty(param.getOrderId())){
            params.put("orderId",param.getOrderId());
        }
        post(ADDNURINGTAG, params, httpResponseHandler, mcontext);
    }

    /**
     * 新增特殊日期标签
     * @param param
     * @param httpResponseHandler
     */
    public static void addSpecialTag(NurseTagEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getTagName())) {
            params.put("tagName", param.getTagName());
        }
        if(!StringUtils.isEmpty(param.getSpeDate())) {
            params.put("speDate", param.getSpeDate());
        }
        if(!StringUtils.isEmpty(param.getCustomerId())) {
            params.put("customerId", param.getCustomerId());
        }
        if(!StringUtils.isEmpty(param.getOrderId())) {
            params.put("orderId", param.getOrderId());
        }
        post(ADDDATETAG, params, httpResponseHandler, mcontext);
    }

    /**
     * 新增私密日志标签
     * @param param
     * @param httpResponseHandler
     */
    public static void addLifeTag(NurseTagEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getTagName())) {
            params.put("tagName", param.getTagName());
        }
        if(!StringUtils.isEmpty(param.getCustomerId())) {
            params.put("customerId", param.getCustomerId());
        }
        if(!StringUtils.isEmpty(param.getOrderId())) {
            params.put("orderId", param.getOrderId());
        }
        post(ADDLIFETAG, params, httpResponseHandler, mcontext);
    }


    /**
     * 顾问销售日志确认
     * @param param
     * @param httpResponseHandler
     */
    public static void saleLogSubmit(OrderEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        post(ADVISERSALELOG.replace("{order_id}", param.getId()+""), params, httpResponseHandler, mcontext);
    }

    /**
     * 上传问题反馈
     * @param param
     * @param httpResponseHandler
     */
    public static void submitSuggest(QuestionEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getContent())) {
            params.put("content", param.getContent());
        }
        if(!StringUtils.isEmpty(param.getImages())) {
            params.put("images", param.getImages());
        }
        post(SUGGESTSUBMIT, params, httpResponseHandler, mcontext);
    }

    /**
     * 新增私密话题标签
     * @param param
     * @param httpResponseHandler
     */
    public static void addTopicTag(NurseTagEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getTagName())) {
            params.put("tagName", param.getTagName());
        }
        if(!StringUtils.isEmpty(param.getCustomerId())) {
            params.put("customerId", param.getCustomerId());
        }
        if(!StringUtils.isEmpty(param.getOrderId())) {
            params.put("orderId", param.getOrderId());
        }
        post(ADDTOPICTAG, params, httpResponseHandler, mcontext);
    }


    /**
     * 护理日志标签+1
     * @param param
     * @param httpResponseHandler
     */
    public static void addNurseCount(NurseTagEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getCustomerId())) {
            params.put("customerId", param.getCustomerId());
        }
        if(!StringUtils.isEmpty(param.getOrderId())){
            params.put("orderId",param.getOrderId());
        }
        post(APPENDNURINGTAG.replace("{tagId}", param.getTagId()), params, httpResponseHandler, mcontext);
    }

    /**
     * 特殊日期标签+1
     * @param param
     * @param httpResponseHandler
     */
    public static void addSpecialCount(NurseTagEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getCustomerId())) {
            params.put("customerId", param.getCustomerId());
        }
        if(!StringUtils.isEmpty(param.getOrderId())) {
            params.put("orderId", param.getOrderId());
        }
        post(APPENDDATETAG.replace("{tagId}", param.getTagId()), params, httpResponseHandler, mcontext);
    }

    /**
     * 私密日志标签+1
     * @param param
     * @param httpResponseHandler
     */
    public static void addLifeCount(NurseTagEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getCustomerId())) {
            params.put("customerId", param.getCustomerId());
        }
        if(!StringUtils.isEmpty(param.getOrderId())) {
            params.put("orderId", param.getOrderId());
        }
        post(APPENDLIFETAG.replace("{tagId}", param.getTagId()), params, httpResponseHandler, mcontext);
    }


    /**
     * 私密话题标签+1
     * @param param
     * @param httpResponseHandler
     */
    public static void addTopicCount(NurseTagEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getCustomerId())) {
            params.put("customerId", param.getCustomerId());
        }
        if(!StringUtils.isEmpty(param.getOrderId())) {
            params.put("orderId", param.getOrderId());
        }
        post(APPENDTOPICTAG.replace("{tagId}", param.getTagId()), params, httpResponseHandler, mcontext);
    }



    /**
     * 获取目标列表
     * @param param
     * @param httpResponseHandler
     * @param mcontext
     */
    public static void getTargetList(TargetEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext){
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getMonth())) {
            params.put("month", param.getMonth());
        }
        get(GETTARGETLIST, params, httpResponseHandler, mcontext, true);
    }


    /**
     *
     * @param param
     * @param httpResponseHandler
     * @param mcontext
     */
    public static void checkVersion(TargetEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext){
        Map<String, Object> params = new HashMap<String, Object>();
        get(VERSIONCHECK, params, httpResponseHandler, mcontext, true);
    }

    /**
     * 获取绩效数据
     * @param param
     * @param httpResponseHandler
     * @param mcontext
     */
    public static void getTargetIndex(TargetEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext, final boolean load_flag){
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getMonth())) {
            params.put("month", param.getMonth());
        }
        if(!StringUtils.isEmpty(param.getUid())) {
            params.put("uids", param.getUid());
        }
        if(!StringUtils.isEmpty(param.getType())) {
            params.put("type", param.getType());
        }
        get(TARGETINDEX, params, httpResponseHandler, mcontext, load_flag);
    }


    /**
     * 获取日报列表
     * @param param
     * @param httpResponseHandler
     * @param mcontext
     */
    public static void getReportList(ReportEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext){
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getCalendarDay())) {
            params.put("calendarDay", param.getCalendarDay());
        }
        get(REPORTLIST.replace("{account}", param.getAccount() + ""), params, httpResponseHandler, mcontext, true);
    }



    /**
     * 获取公司门店列表
     * @param param
     * @param httpResponseHandler
     * @param mcontext
     */
    public static void getCompanyStoreList(StoreEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext){
        Map<String, Object> params = new HashMap<String, Object>();
        get(GETCOMPANYSTORELIST.replace("{uid}", param.getId() + ""), params, httpResponseHandler, mcontext, true);
    }
    /**
     * 获取公司门店日报详情
     * @param param
     * @param httpResponseHandler
     * @param mcontext
     */
    public static void getStoreReportDetail(ReportEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext){
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getDay())) {
            params.put("day", param.getDay());
        }
        if(!StringUtils.isEmpty(param.getPageSize()+"")){
            params.put("pageSize",param.getPageSize());
        }
        get(GETSTOREREPORTDETAIL.replace("{storeid}", param.getStoreid()), params, httpResponseHandler, mcontext, true);
    }



    /**
     * 通过门店id获取门店日报列表
     * @param param
     * @param httpResponseHandler
     * @param mcontext
     */
    public static void getStoreReportList(ReportEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext){
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getDay())) {
            params.put("day", param.getDay());
        }
        if(!StringUtils.isEmpty(param.getPageSize()+"")){
            params.put("pageSize",param.getPageSize());
        }
        get(GETSTOREREPORTLIST.replace("{storeid}", param.getStoreid()+""), params, httpResponseHandler, mcontext, true);
    }



    /**
     * 获取个人信息
     * @param param
     * @param httpResponseHandler
     * @param mcontext
     */
    public static void getAccountInfo(UserParam param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext){
        Map<String, Object> params = new HashMap<String, Object>();
        get(GETACCOUNTINFO.replace("{account}", param.getAccountname()), params, httpResponseHandler, mcontext, true);
    }
    /**
     * 获取公司信息
     * @param param
     * @param httpResponseHandler
     * @param mcontext
     */
    public static void getCompanyInfo(UserParam param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext){
        Map<String, Object> params = new HashMap<String, Object>();
        get(GETCOMPANYINFO.replace("{companyid}", param.getCompanyid()), params, httpResponseHandler, mcontext, true);
    }
    /**
     * 获取顾问列表信息
     * @param param
     * @param httpResponseHandler
     * @param mcontext
     */
    public static void getAdviserList(UserParam param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext){
        Map<String, Object> params = new HashMap<String, Object>();
        get(GETADVISERLIST.replace("{company_id}", param.getCompanyid()), params, httpResponseHandler, mcontext, true);
    }

    /**
     * 获取门店顾问列表信息
     * @param param
     * @param httpResponseHandler
     * @param mcontext
     */
    public static void getStoreAdviserList(UserParam param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext){
        Map<String, Object> params = new HashMap<String, Object>();
        get(GETSTOREADVISERLIST.replace("{store_id}", param.getStore_id()), params, httpResponseHandler, mcontext, true);
    }

    /**
     * 获取门店美容师列表信息
     * @param param
     * @param httpResponseHandler
     * @param mcontext
     */
    public static void getStoreStaffList(UserParam param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext){
        Map<String, Object> params = new HashMap<String, Object>();
        get(GETSTORESTAFFLIST.replace("{store_id}", param.getStore_id()), params, httpResponseHandler, mcontext, true);
    }






    /**
     * 获取下级员工列表信息
     * @param param
     * @param httpResponseHandler
     * @param mcontext
     */
    public static void getSubPersonList(UserParam param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext,boolean isFlag){
        Map<String, Object> params = new HashMap<String, Object>();
        get(GETNEXTSUBLIST.replace("{uid}", param.getUid()), params, httpResponseHandler, mcontext, isFlag);
    }



    /**
     * 获取下属所有员工列表信息
     * @param param
     * @param httpResponseHandler
     * @param mcontext
     */
    public static void getSubAllPersonList(UserParam param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext){
        Map<String, Object> params = new HashMap<String, Object>();
        get(GETALLSUBLIST.replace("{uid}", param.getUid()), params, httpResponseHandler, mcontext, true);
    }


    /**
     * 分配用户
     * @param param
     * @param httpResponseHandler
     * @param mcontext
     */
    public static void assignCustomer(CustomerEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext){
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty( param.getRoleKey())){
            params.put("roleKey", param.getRoleKey());
        }
        if(!StringUtils.isEmpty( param.getCustomerIds())){
            params.put("customerId",param.getCustomerIds());
        }

        post(ASSIGNECUSTOMER.replace("{staff_id}", param.getStaff_id()), params, httpResponseHandler, mcontext);
    }



    /**
     * 获取日报标签
    * @param param
    * @param httpResponseHandler
    * @param mcontext
    */
    public static void getReportTags(ReportEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext){
        Map<String, Object> params = new HashMap<String, Object>();
        get(REPORTTAGS, params, httpResponseHandler, mcontext, false);
    }


    /**
     * 添加日报标签
     * @param param
     * @param httpResponseHandler
     * @param mcontext
     */
    public static void addReportTag(ReportEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext){
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getContent())){
            params.put("content", param.getContent());
        }
        post(ADDREPORTTAG.replace("{dId}", param.getdId() + "").replace("{tagId}", param.getTagId() + ""), params, httpResponseHandler, mcontext);
    }


    /**
     * 获取门店列表
     * @param param
     * @param httpResponseHandler
     * @param mcontext
     */
    public static void getStoreList(TargetEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext){
        Map<String, Object> params = new HashMap<String, Object>();
        get(GETCOMPANYSTORELIST.replace("{uid}", param.getId() + ""), params, httpResponseHandler, mcontext, false);
    }


    /**
     * 删除目标
     * @param param
     * @param httpResponseHandler
     */
    public static void deleteTarget(TargetEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        delete(DELETETARGET.replace("{id}", param.getId() + ""), params, httpResponseHandler, mcontext);
    }

    /**
     * 新增设定目标
     * @param param
     * @param httpResponseHandler
     */
    public static void AddTarget(TargetEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getMonth())){
            params.put("month", param.getMonth());
        }
        if(!StringUtils.isEmpty(param.getCategoryId())){
            params.put("categoryId", param.getCategoryId());
        }
        if(!StringUtils.isEmpty(param.getStoreId())){
            params.put("storeId", param.getStoreId());
        }
        if(!StringUtils.isEmpty(param.getAmount()+"")){
            params.put("amount", param.getAmount()+"");
        }
        post(ADDTARGET, params, httpResponseHandler, mcontext);
    }


    /**
     * 获取日程列表
     * @param param
     * @param httpResponseHandler
     */
    public static void getScheduleList(ScheduleEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getAlert_date())){
            params.put("alert_date",param.getAlert_date());
        }
        get(GETSCHEDULELIST, params, httpResponseHandler, mcontext, true);
    }


    /**
     * 修改日程
     * @param param
     * @param httpResponseHandler
     */
    public static void ModifySchedule(ScheduleEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getId()+"")) {
            params.put("id", param.getId());
        }
        if(!StringUtils.isEmpty(param.getContent())) {
            params.put("content", param.getContent());
        }
        if(!StringUtils.isEmpty(param.getAlert_date())) {
            params.put("alert_date", param.getAlert_date());
        }
        if(!StringUtils.isEmpty(param.getRemind_period())) {
            params.put("remind_period", param.getRemind_period());
        }
        if(!StringUtils.isEmpty(param.getRemind_before())) {
            params.put("remind_before", param.getRemind_before());
        }
        post(MODIFYSCHEDULE.replace("{id}", param.getId() + ""), params, httpResponseHandler, mcontext);
    }

    /**
     * 新增日程
     * @param param
     * @param httpResponseHandler
     */
    public static void AddSchedule(ScheduleEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getCategoryId())) {
            params.put("category_id", param.getCategoryId());
        }
        if(!StringUtils.isEmpty(param.getContent())) {
            params.put("content", param.getContent());
        }
        if(!StringUtils.isEmpty(param.getAlert_date())) {
            params.put("alert_date", param.getAlert_date());
        }
        if(!StringUtils.isEmpty(param.getRemind_period())) {
            params.put("remind_period", param.getRemind_period());
        }
        if(!StringUtils.isEmpty(param.getRemind_before())) {
            params.put("remind_before", param.getRemind_before());
        }
        post(ADDSCHEDULE, params, httpResponseHandler, mcontext);
    }

    /**
     * 删除日程
     * @param param
     * @param httpResponseHandler
     */
    public static void deleteSchedule(ScheduleEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        delete(DELETESCHEDULE.replace("{id}", param.getId() + ""), params, httpResponseHandler, mcontext);
    }


    /**
     * 获取文章详情
     * @param param
     * @param httpResponseHandler
     */
    public static void getArticleDetail(ArticleEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getArticleId()+"")) {
            params.put("articleId", param.getArticleId());
        }
        get(GETARTICLEDETAIL, params, httpResponseHandler, mcontext, true);
    }


    /**
     * 获取一级项目列表
     * @param param
     * @param httpResponseHandler
     */
    public static void getCateGoryList(CategroyEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getCatetype())) {
            params.put("catetype", param.getCatetype());
        }
        get(GETCATEGORY, params, httpResponseHandler, mcontext,false);
    }


    /**
     * 获取二级项目分类项目列表
     * @param param
     * @param httpResponseHandler
     */
    public static void getCateGoryProjectList(CategroyEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        get(GETSENCONDCATEGORY.replace("{cid}",param.getId()), params, httpResponseHandler, mcontext,true);
    }

    /**
     * 获取二级项目列表
     * @param param
     * @param httpResponseHandler
     */
    public static void getSencondCateGoryList(WalletEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        get(GETCATEGORY, params, httpResponseHandler, mcontext, false);
    }

    /**
     * 删除钱包
     * @param param
     * @param httpResponseHandler
     */
    public static void deleteWallet(WalletEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        delete(DELETEWALLET.replace("{id}", param.getId() + ""), params, httpResponseHandler, mcontext);
    }

    /**
     * 获取推荐商家
     * @param param
     * @param httpResponseHandler
     */
    public static void getRecommendShops(SearchParam param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        param.setLat(39.982314);
        param.setLng(116.409671);
        param.setCity("beijing");
        param.setPsize(PAGE_SIZE);

        Map<String, Object> params = new HashMap<String, Object>();
        if (param.getCity() != null) {
            params.put("city", param.getCity());
        }
        if (param.getLat() != null) {
            params.put("lat", param.getLat());
        }
        if (param.getLng() != null) {
            params.put("lng", param.getLng());
        }
        if (param.getPno() != null) {
            params.put("pno", param.getPno());
        }
        if (param.getPsize() != null) {
            params.put("psize", param.getPsize());
        }
        get(SHOP_RECOMMEND, params, httpResponseHandler, mcontext, false);
    }


    /**
     * 获取客户列表
     * @param param
     * @param httpResponseHandler
     */
    public static void getCustomers(SearchParam param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if (!StringUtils.isEmpty(param.getPno()+"")) {
            params.put("page", param.getPno());
        }
        if (!StringUtils.isEmpty(param.getType())) {
            params.put("type", param.getType());
        }
        if (!StringUtils.isEmpty(param.getName())) {
            params.put("name",param.getName());
        }
        if (!StringUtils.isEmpty(param.getStaff_id())) {
            params.put("staffId",param.getStaff_id());
        }
        if (!StringUtils.isEmpty(param.getStoreId())) {
            params.put("storeId",param.getStoreId());
        }
        if (!StringUtils.isEmpty(param.getAdviseId())) {
            params.put("adviseId",param.getAdviseId());
        }
        if (!StringUtils.isEmpty(param.getMobile())) {
            params.put("mobile", param.getMobile());
        }
        if (!StringUtils.isEmpty(param.getDays())) {
            params.put("days", param.getDays());
        }
        if(!StringUtils.isEmpty(param.getRoleKey())){
            params.put("roleKey", param.getRoleKey());
        }
        get(GETCUSTOMERLIST, params, httpResponseHandler, mcontext, false);
    }

    /**
     * 获取客户明细
     * @param param
     * @param httpResponseHandler
     * @param mcontext
     */
    public static void getCustomerDetail(CustomerEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        get(GETCUSTOMERDETAIL.replace("{customer_id}", param.getId()+""), params, httpResponseHandler, mcontext, true);
    }



    /**
     * 获取待激活客户删选条件
     * @param param
     * @param httpResponseHandler
     * @param mcontext
     */
    public static void getCustomerQueryCondition(CustomerEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        get(GETCUSTOMERQUERYCONDITION.replace("{staff_id}", param.getStaff_id()), params, httpResponseHandler, mcontext, true);
    }


    /**
     * 获取我的订单列表
     * @param param
     * @param httpResponseHandler
     */
    public static void getOrders(OrderEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if (!StringUtils.isEmpty(param.getPage()+"")) {
            params.put("page",param.getPage());
        }
        if (!StringUtils.isEmpty(param.getPageSize()+"")) {
            params.put("pageSize", param.getPageSize());
        }
        if (!StringUtils.isEmpty(param.getOrder_type())) {
            params.put("type", param.getOrder_type());
        }
        if (!StringUtils.isEmpty(param.getCode())) {
            params.put("code", param.getCode());
        }
        if (!StringUtils.isEmpty(param.getCustomerName())) {
            params.put("customerName", param.getCustomerName());
        }
        if (!StringUtils.isEmpty(param.getIsVip())) {
            params.put("isVip", param.getIsVip());
        }
        if (!StringUtils.isEmpty(param.getStaffId())) {
            params.put("staffId", param.getStaffId());
        }
        if (!StringUtils.isEmpty(param.getAdviserId())) {
            params.put("adviserId", param.getAdviserId());
        }
        if (!StringUtils.isEmpty(param.getMobile())) {
            params.put("mobile", param.getMobile());
        }
        if (!StringUtils.isEmpty(param.getCardNumber())) {
            params.put("cardNumber", param.getCardNumber());
        }
        if (!StringUtils.isEmpty(param.getProject_id())) {
            params.put("project_id", param.getProject_id());
        }
        if (!StringUtils.isEmpty(param.getServer_time_begin())) {
            params.put("serverTimeBegin", param.getServer_time_begin());
        }
        if (!StringUtils.isEmpty(param.getServer_time_end())) {
            params.put("serverTimeEnd", param.getServer_time_end());
        }
        if (!StringUtils.isEmpty(param.getOrder_time_begin())) {
            params.put("orderTimeBegin", param.getOrder_time_begin());
        }
        if (!StringUtils.isEmpty(param.getOrder_time_end())) {
            params.put("orderTimeEnd", param.getOrder_time_end());
        }
        if (!StringUtils.isEmpty(param.getStore_id())) {
            params.put("storeId", param.getStore_id());
        }
        if (!StringUtils.isEmpty(param.getStatus())) {
            params.put("status", param.getStatus());
        }
        if (!StringUtils.isEmpty(param.getCoupon_id())) {
            params.put("coupon_id", param.getCoupon_id());
        }
        get(GETORDERLIST, params, httpResponseHandler, mcontext,false);

    }

    /**
     * 获取客户订单列表
     * @param param
     * @param httpResponseHandler
     */
    public static void getCustomerOrders(OrderEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if (!StringUtils.isEmpty(param.getPage()+"")) {
            params.put("page",param.getPage());
        }
        if (!StringUtils.isEmpty(param.getPageSize()+"")) {
            params.put("pageSize", param.getPageSize());
        }
        get(GETCUSTOMERORDERLIST.replace("{customer_id}", param.getCustomerId()), params, httpResponseHandler, mcontext,false);

    }


    /**
     * 获取订单列表
     * @param param
     * @param httpResponseHandler
     */
    public static void getNewOrders(OrderEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if (!StringUtils.isEmpty(param.getPage()+"")) {
            params.put("page", param.getPage());
        }
        if (!StringUtils.isEmpty(param.getCode())) {
            params.put("code", param.getCode());
        }
        if (!StringUtils.isEmpty(param.getCustomerName())) {
            params.put("customerName", param.getCustomerName());
        }
        if (!StringUtils.isEmpty(param.getMobile())) {
            params.put("mobile", param.getMobile());
        }
        if (!StringUtils.isEmpty(param.getCardNumber())) {
            params.put("cardNumber", param.getCardNumber());
        }
        if (!StringUtils.isEmpty(param.getProject_id())) {
            params.put("project_id", param.getProject_id());
        }
        if (!StringUtils.isEmpty(param.getServer_time_begin())) {
            params.put("server_time_begin", param.getServer_time_begin());
        }
        if (!StringUtils.isEmpty(param.getServer_time_end())) {
            params.put("server_time_end", param.getServer_time_end());
        }
        if (!StringUtils.isEmpty(param.getOrder_time_begin())) {
            params.put("order_time_begin", param.getOrder_time_begin());
        }
        if (!StringUtils.isEmpty(param.getOrder_time_end())) {
            params.put("order_time_end", param.getOrder_time_end());
        }
        if (!StringUtils.isEmpty(param.getStore_id())) {
            params.put("pay_type", param.getStore_id());
        }
        if (!StringUtils.isEmpty(param.getStore_id())) {
            params.put("store_id", param.getStore_id());
        }
        if (!StringUtils.isEmpty(param.getStatus())) {
            params.put("status", param.getStatus());
        }
        if (!StringUtils.isEmpty(param.getCoupon_id())) {
            params.put("coupon_id", param.getCoupon_id());
        }
        get(GETORDERLIST.replace("{user_id}", param.getUser_id()), params, httpResponseHandler, mcontext,false);
    }

    /**
     * 获取订单详情
     * @param param
     * @param httpResponseHandler
     */
    public static void getOrderDetail(OrderEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        get(ORDERDETAIL.replace("{order_id}", param.getCode() + ""), params, httpResponseHandler, mcontext, true);
    }

    /**
     * 提交订单总订单
     * @param param
     * @param httpResponseHandler
     */
    public static void doOrderStatus(OrderEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        post(ORDERDETAIL.replace("{order_id}", param.getCode() + ""), params, httpResponseHandler, mcontext);
    }


    /**
     * 提交订单备注
     * @param param
     * @param httpResponseHandler
     */
    public static void addOrderNote(OrderEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("note",param.getOrderNote());
        post(ORDERNOTE.replace("{order_id}", param.getCode() + ""), params, httpResponseHandler, mcontext);
    }


    /**
     * 上传图片文件
     * @param param
     * @param httpResponseHandler
     */
    public static void uploadImage(QuestionEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if (!isNetworkAvailable()) {
            Toast.makeText(mcontext, R.string.no_network_connection_toast, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i("Post请求交易TAG", "请求url:" + UPLOADIMAGE + "请求参数:" + JSON.toJSONString(params));
        MediaType mediaType = MediaType.parse("image/jpeg; charset=utf-8");
        File file = new File(param.getLocalPath());
        //RequestBody body = RequestBody.create(mediaType, file);

        RequestBody body = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addPart(
                        Headers.of("Content-Disposition", "form-data; name=\"title\""),
                        RequestBody.create(null, "Square Logo"))
                .addFormDataPart("file", file.getName(), RequestBody.create(mediaType, new File(param.getLocalPath())))
                //.addPart(
                //        Headers.of("Content-Disposition", "form-data; name=\"file\""),
                //        RequestBody.create(mediaType, new File(param.getLocalPath())))
                .build();


        Request request = new Request.Builder().url(HTTP_DOMAIN + UPLOADIMAGE).post(body).build();
        enqueue(request, httpResponseHandler, mcontext, true);
    }

    /**
     * 获取个人历史薪资列表
     * @param param
     * @param httpResponseHandler
     */
    public static void getSalaryList(SearchParam param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        get(GETSALARYLIST.replace("{uid}", param.getUid()), params, httpResponseHandler, mcontext, true);
    }


    /**
     * 获取短信模版列表
     * @param param
     * @param httpResponseHandler
     */
    public static void getInfoTemplateList(MessageEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getType())){
            params.put("type",param.getType()+"");
        }
        if(!StringUtils.isEmpty(param.getPage()+"")) {
            params.put("page", param.getPage() + "");
        }
        if(!StringUtils.isEmpty(param.getPageSize()+"")) {
            params.put("pageSize", param.getPageSize() + "");
        }
        get(GETMESSAGELIST, params, httpResponseHandler, mcontext, true);
    }

    /**
     * 删除短信模版
     * @param param
     * @param httpResponseHandler
     */
    public static void deleteInfoTemplate(MessageEntity param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getId()+"")) {
            params.put("id", param.getId());
        }
        delete(DELETEMESSAGE.replace("{id}", param.getId() + ""), params, httpResponseHandler, mcontext);
    }


    /**
     * 新增短信模版
     * @param param
     * @param httpResponseHandler
     */
    public static void AddInfoTemplate(SearchParam param, AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(!StringUtils.isEmpty(param.getType())) {
            params.put("type", param.getType());
        }
        if(!StringUtils.isEmpty(param.getContent())) {
            params.put("content", param.getContent());
        }
        post(ADDMESSAGE, params, httpResponseHandler, mcontext);
    }

    /**
     * 获取首页动态数据
     */
    public static void getHomePageData(AsyncHttpResponseHandler httpResponseHandler,Context mcontext) {
        Map<String, Object> params = new HashMap<String, Object>();
        get(HOMEPAGEDATA, params, httpResponseHandler, mcontext, false);

    }

    private static void startProgressDialog(String progressMsg,Context mcontext) {
        if (proDialog == null) {
            proDialog = LoadingProgressDialog.createDialog(mcontext);
            proDialog.setMessage(progressMsg);
            proDialog.setCanceledOnTouchOutside(false);
        }
        proDialog.show();
    }

    private static void stopProgressDialog() {
        if (proDialog != null) {
            proDialog.dismiss();
            proDialog = null;
        }
    }



}
