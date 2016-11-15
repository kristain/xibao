package com.drjing.xibao.module.application.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.drjing.xibao.R;
import com.drjing.xibao.common.view.dialog.WxShareDialog;
import com.drjing.xibao.common.view.swipebacklayout.SwipeBackActivity;
import com.drjing.xibao.config.AppConfig;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 新闻明晰页面
 * Created by kristain on 16/1/3.
 */
public class ArticleDetailActivity extends SwipeBackActivity {


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

    private IWXAPI wxApi;

    private Dialog msShareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artcle_detail);
        bundle=getIntent().getExtras();
        wxApi = WXAPIFactory.createWXAPI(this, AppConfig.WX_APP_ID);
        wxApi.registerApp(AppConfig.WX_APP_ID);

        initView();
        initEvent();
    }

    private void initView() {
        btnBack = (Button)findViewById(R.id.btnBack);
        btnRight =(TextView) findViewById(R.id.btnRight);
        btnRight.setText("分享");
        btnBack.setVisibility(View.VISIBLE);
        btnRight.setVisibility(View.VISIBLE);
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
        //webview.loadUrl(bundle.getString("articleId"));
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
                        ArticleDetailActivity.this, "分享文本",
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

    /**
     * 微信分享 （这里仅提供一个分享网页的示例，其它请参看官网示例代码）
     * @param flag(0:分享到微信好友，1：分享到微信朋友圈)
     */
    private void wechatShare(int flag){
       /* WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "http://www.baidu.com";
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "这里填写标题";
        msg.description = "这里填写内容";
        //这里替换一张自己工程里的图片资源
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.icon_head);
        msg.setThumbImage(thumb);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = flag==0?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;
        wxApi.sendReq(req);*/
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
