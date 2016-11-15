package com.drjing.xibao.wxapi;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.drjing.xibao.R;
import com.drjing.xibao.common.http.HttpClient;
import com.drjing.xibao.common.view.dialog.WxShareDialog;
import com.drjing.xibao.config.AppConfig;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

/**
 * Created by kristain on 16/1/15.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;

    /**
     * 后退返回按钮
     */
    private Button btnBack;

    /**
     * 分享按钮
     */
    private TextView btnRight;
    /**
     * 标题栏文案
     */
    private TextView textHeadTitle;

    private WebView webview;
    private Bundle bundle;

    private Dialog msShareDialog;

    private String web_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, AppConfig.WX_APP_ID);
        api.registerApp(AppConfig.WX_APP_ID);
        api.handleIntent(getIntent(), this);

        setContentView(R.layout.activity_artcle_detail);
        bundle=getIntent().getExtras();
        web_url = HttpClient.HTTP_DOMAIN+"/cms/articleInfo?articleId="+bundle.getString("articleId");
        initView();
        initEvent();
        loadData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    private void initView() {
        btnBack = (Button)findViewById(R.id.btnBack);
        btnRight =(TextView) findViewById(R.id.btnRight);
        btnRight.setText("分享");
        btnBack.setVisibility(View.VISIBLE);
        //btnRight.setVisibility(View.VISIBLE);
        textHeadTitle = (TextView) findViewById(R.id.textHeadTitle);
        textHeadTitle.setText("文章详情");
        webview = (WebView)findViewById(R.id.webview);
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);


        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                //super.onReceivedError(view, request, error);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("mailto:") || url.startsWith("geo:") || url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                webview.loadUrl(url);
                return true;
            }
        });
        webview.loadUrl(web_url);
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


        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                msShareDialog = WxShareDialog.showDialog(
                        WXEntryActivity.this, "分享文本",
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                msShareDialog.dismiss();
                            }

                        },
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                wechatShare(0);
                            }

                        }, new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                wechatShare(1);
                            }
                        });


            }
        });

    }



    private void wechatShare(int flag){
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = web_url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = bundle.getString("article_title");
        msg.description = "这里填写内容";
        //这里替换一张自己工程里的图片资源
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.icon_head);
        msg.setThumbImage(thumb);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = flag==0?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
        /*Log.e("++wechatShare","wechatShare"+api);
        WXTextObject textObj = new WXTextObject();
        textObj.text = "这里填写标题";
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = "这里填写标题";

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text");
        req.message = msg;
        req.scene = flag==0?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);*/
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
    @Override
    public void onReq(BaseReq arg0) {
       // Log.e("++onReq++",arg0.openId);
       // Toast.makeText(this,"arg0.openId:"+arg0.openId+"arg0.transaction:"+arg0.transaction,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.e("++onResp++", resp.errCode + "");
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

    private void loadData() {
       /* ArticleEntity param = new ArticleEntity();
        param.setArticleId(Integer.parseInt(bundle.getString("articleId")));
        HttpClient.getArticleDetail(param, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String body) {
                Log.i("getArticleDetailTAG", "成功返回数据:" + body);
                webview.getSettings().setDefaultTextEncodingName("UTF -8") ;
                //webview.loadDataWithBaseURL();
                webview.loadDataWithBaseURL("about：blank",body, "text/html", "UTF-8","");
            }

            @Override
            public void onFailure(Request request, IOException e) {
                Log.i("getArticleDetailTAG", "失败返回数据:" + request.toString());

            }
        }, this);*/
    }

    @Override
    protected void onPause ()
    {
        webview.reload ();
        super.onPause ();
    }
}
