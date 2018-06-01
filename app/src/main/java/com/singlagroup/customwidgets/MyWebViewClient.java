package com.singlagroup.customwidgets;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebViewClient extends WebViewClient {
    private Context context;
    private ProgressDialog progressDialog;
    public MyWebViewClient(Context context) {
        this.context = context;
        this.progressDialog = new ProgressDialog(context);
        this.progressDialog.setMessage("Loading.....");
    }
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        progressDialog.show();
    }
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
        return true;
    }
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        progressDialog.dismiss();
    }

}