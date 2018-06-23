package com.tistory.dayglo.smarting_android;

import android.webkit.WebView;
import android.webkit.WebViewClient;

class WebViewClientClass extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return super.shouldOverrideUrlLoading(view, url);
    }
}
