package com.rdc.gduthelper.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.rdc.gduthelper.R;
import com.rdc.gduthelper.app.GDUTHelperApp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by seasonyuu on 15/9/10.
 */
public class PictureActivity extends BaseActivity implements View.OnClickListener {
	private SubsamplingScaleImageView ivPicture;
	private Toolbar toolbar;
	private Bitmap bitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(android.support.v7.appcompat.R.style.Theme_AppCompat_NoActionBar);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture);

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		ivPicture = (SubsamplingScaleImageView) findViewById(R.id.picture);
		ivPicture.setMinimumDpi(20);

		bitmap = getIntent().getParcelableExtra("avatar");
		ivPicture.setImage(ImageSource.bitmap(bitmap));
		ivPicture.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (toolbar.getVisibility() == View.VISIBLE)
			toolbar.setVisibility(View.GONE);
		else toolbar.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_picture, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;
			case R.id.picture_save:
				if (!Environment.getExternalStorageDirectory().exists()) {
					Toast.makeText(this, "找不到外置存储", Toast.LENGTH_SHORT).show();
					return true;
				}
				String path = savePicture();
				Toast.makeText(this, "图片保存到 " + path, Toast.LENGTH_SHORT).show();
				return true;
			case R.id.picture_share:
				File f = new File(Environment.getExternalStoragePublicDirectory(
						Environment.DIRECTORY_PICTURES), "教务系统头像" + GDUTHelperApp.userXh + ".jpg");
				if (!f.exists())
					savePicture();
				Uri u = Uri.fromFile(f);

				ShareActionProvider share = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
				Intent i = new Intent();
				i.setAction(Intent.ACTION_SEND);
				i.putExtra(Intent.EXTRA_STREAM, u);
				i.setType("image/*");
				share.setShareIntent(i);
				return true;

		}
		return super.onOptionsItemSelected(item);
	}

	private String savePicture() {
		File file = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), "教务系统头像" + GDUTHelperApp.userXh + ".jpg");
		try {
			if (!file.exists()) {

				file.createNewFile();
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
				fileOutputStream.flush();
				fileOutputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file.getPath();
	}
}
