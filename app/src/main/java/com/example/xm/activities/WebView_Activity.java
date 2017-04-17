package com.example.xm.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.xm.bean.StaticVar;
import com.example.xm.finebiopane.R;


public class WebView_Activity extends AppCompatActivity {

    WebView webview;
    String url;
    private Toolbar toolbar;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_aboutus);
        url = getIntent().getStringExtra("url");
        initView();
        initToolBar();
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView) findViewById(R.id.title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        title.setText("关于");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    private void initView() {
        // TODO Auto-generated method stub
        webview = (WebView) findViewById(R.id.webview);
        webview.loadUrl(url);

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSavePassword(false);

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        IntentFilter filter = new IntentFilter();
        filter.addAction(StaticVar.FINISH_ACTIVITY);
        registerReceiver(broadcastReceiver, filter);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            WebView_Activity.this.finish();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(R.anim.back_in_from_left, R.anim.back_out_to_right);
        }
        return super.onKeyDown(keyCode, event);
    }

}
