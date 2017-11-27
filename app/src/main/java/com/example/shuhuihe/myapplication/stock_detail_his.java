package com.example.shuhuihe.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;



public class Stock_detail_his extends Fragment {
    private View rootview;
    private WebView mWebView;
    private String symbol;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootview == null) {

            rootview = inflater.inflate(R.layout.fragment_stock_detail_his, container, false);
            mWebView = rootview.findViewById(R.id.his_webview);
            String symbolTemp = getActivity().getIntent().getExtras().getString("symbol");
            String chartType = "historical";
            symbol = symbolTemp.split("-")[0];
            mWebView = rootview.findViewById(R.id.his_webview);
            loadWebView(symbol, chartType);
        }
        return rootview;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadWebView(String symbol, String function) {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.loadUrl("file:///android_asset/huihui.html?" + symbol.trim() + "&" + function);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
    }
}


