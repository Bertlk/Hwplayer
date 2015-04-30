package com.personal.hwplayer.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.personal.hwplayer.ui.Stackable;
import com.personal.hwplayer.utils.ActivityQueue;
import com.personal.hwplayer.utils.SystemUtil;

public class BaseActivity extends Activity implements Stackable,
		OnClickListener {

	protected final String TAG = this.getClass().getSimpleName();

	protected Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SystemUtil.SetFullScreen(this);
		mContext = getApplicationContext();
		push();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		pop();
	}

	@Override
	public void push() {
		ActivityQueue.pushActivity(this);
	}

	@Override
	public void pop() {
		ActivityQueue.popActivity(this);
	}

	/**
	 * Toast短显示
	 * 
	 * @param int pResId
	 */
	protected void showShortToast(int pResId) {
		showShortToast(mContext.getString(pResId));
	}

	/**
	 * Toast短显示
	 * 
	 * @param String
	 *            pMsg
	 */
	protected void showShortToast(String pMsg) {
		Toast.makeText(mContext, pMsg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Toast长显示
	 * 
	 * @param int pResId
	 */
	protected void showLongToast(int pResId) {
		showLongToast(mContext.getString(pResId));
	}

	/**
	 * Toast长显示
	 * 
	 * @param String
	 *            pMsg
	 */
	protected void showLongToast(String pMsg) {
		Toast.makeText(mContext, pMsg, Toast.LENGTH_LONG).show();
	}

	/**
	 * 控件的点击事件
	 */
	@Override
	public void onClick(View v) {
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
//		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
//		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	@Override
	public void finish() {
		super.finish();
//		if (!(this instanceof SplashActivity)) {
//			overridePendingTransition(anim.slide_in_left, anim.slide_out_right);
//		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
//		overridePendingTransition(anim.slide_in_left, anim.slide_out_right);
	}

}
