package com.example.apple.myaccessibilityservice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TimePicker;

import com.example.apple.myaccessibilityservice.service.AccessibilityServiceMonitor;
import com.example.apple.myaccessibilityservice.util.AccessibilitUtil;
import com.example.apple.myaccessibilityservice.util.Config;
import com.example.apple.myaccessibilityservice.util.ShareUtil;

import java.util.HashSet;


public class MainActivity1 extends AppCompatActivity{

    private WebView webview;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        webview = (WebView) findViewById(R.id.webview);

        webview.loadUrl("https://www.baidu.com/s?word=%E5%BD%95%E5%B1%8F&ts=8072133&t_kt=0&ie=utf-8&fm_kl=021394be2f&rsv_iqid=3259783768&rsv_t=b39atb4WgjYrHvo4SnKzw%252B2gDJ6qtxWMoQ%252FfSnsrWJcjtTlXAlR0HaqYzQ&sa=ib&ms=1&rsv_pq=3259783768&rsv_sug4=10587&tj=1&inputT=2660&ss=100&from=844b&isid=44006&mod=0&async=1");
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        startService();
    }


    private void startService() {
        Intent mIntent = new Intent(this, AccessibilityServiceMonitor.class);
        startService(mIntent);
    }


}
