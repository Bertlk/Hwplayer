package com.personal.hwplayer;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.personal.hwplayer.utils.SystemUtil;

public class ApplicationManager extends Application {

	private static ApplicationManager appInstances;
	
	public static int SCREEN_WIDTH;
	public static int SCREEN_HEIGHT;

	@Override
	public void onCreate() {
		super.onCreate();
		appInstances = this;
		SystemUtil.init(getGlobalContext());
		mearsure();
	}

	public static ApplicationManager getInstance() {
		return appInstances;
	}
	
	private void mearsure(){
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) getGlobalContext()
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(dm);
		SCREEN_WIDTH = dm.widthPixels;
		SCREEN_HEIGHT = dm.heightPixels;
	}

	/**
	 * 获取全局的context
	 * 
	 * @return
	 */
	public static Context getGlobalContext() {
		return appInstances.getApplicationContext();
	}

	/**
	 * exit app
	 * 
	 * @return void
	 * @throws
	 */
	public void exitSystem() {
		AppExit(this);
	}

	/**
	 * 退出应用程序
	 */
	private void AppExit(Context context) {
		System.exit(0);
		android.os.Process.killProcess(android.os.Process.myPid());
	}

}
