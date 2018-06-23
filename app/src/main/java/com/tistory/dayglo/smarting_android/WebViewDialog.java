package com.tistory.dayglo.smarting_android;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.webkit.WebView;

public class WebViewDialog extends Dialog {
    String uri = "http://smarts.asuscomm.com:1001/stream";

    public WebViewDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_web_view);

        WebView mWebView = (WebView) findViewById( R.id.web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(uri);
        mWebView.setWebViewClient(new WebViewClientClass());
    }

}
