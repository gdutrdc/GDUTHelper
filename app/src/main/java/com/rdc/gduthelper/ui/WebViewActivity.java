package com.rdc.gduthelper.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.rdc.gduthelper.R;

public class WebViewActivity extends BaseActivity {
	private WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);

		Intent intent = getIntent();
		String title = intent.getStringExtra("title");
		String url = intent.getStringExtra("url");

		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.loadUrl(url);
		setTitle(title);
	}
}
