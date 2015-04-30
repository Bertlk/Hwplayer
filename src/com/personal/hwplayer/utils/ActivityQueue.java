package com.personal.hwplayer.utils;

import java.util.Iterator;
import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;

/**
 * Activity 队列
 * 
 */
public class ActivityQueue {

	private static final ActivityQueue INSTANCE = new ActivityQueue();

	private static LinkedList<Activity> queue;

	private ActivityQueue() {
		ActivityQueue.queue = new LinkedList<Activity>();
	}

	/**
	 * push activity to queue
	 * 
	 * @param activity
	 * @return void
	 * @throws
	 */
	public static void pushActivity(Activity activity) {
		INSTANCE.doPushActivity(activity);
		L.v("ActivityQueue", "pushActivity size=" + queue.size() + " name="
				+ activity.getLocalClassName());
	}

	/**
	 * pop activity from queue
	 * 
	 * @param activity
	 * @return void
	 * @throws
	 */
	public static void popActivity(Activity activity) {
		INSTANCE.doPopActivity(activity);
		L.v("ActivityQueue", "popActivity size=" + queue.size() + " name="
				+ activity.getLocalClassName());
	}

	/**
	 * pop the stack top activity
	 * 
	 * @return Activity
	 * @throws
	 */
	public static Activity pop() {
		if (ActivityQueue.queue != null && ActivityQueue.queue.size() > 0) {
			return ActivityQueue.queue.peek();
		} else {
			return null;
		}
	}

	/**
	 * pop the postion activity
	 * 
	 * @return Activity
	 * @throws
	 */
	public static Activity popIndex(int postion) {
		if (ActivityQueue.queue != null && ActivityQueue.queue.size() > postion) {
			return ActivityQueue.queue.get(postion);
		} else {
			return null;
		}
	}

	/**
	 * finish all activity from queue
	 * 
	 * @return void
	 * @throws
	 */
	public static void finishAllActivity() {
		INSTANCE.doFinishAll();
	}

	@SuppressLint("NewApi")
	private void doPushActivity(Activity activity) {
		// 解决系统2.2版本的bug
		if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
			queue.push(activity);
		} else {
			queue.addFirst(activity);
		}
	}

	private void doPopActivity(Activity activity) {
		ActivityQueue.queue.remove(activity);
	}

	private void doFinishAll() {
		Iterator<Activity> it = queue.iterator();
		while (it.hasNext()) {
			Activity a = it.next();
			// it.remove();
			a.finish();
		}
	}

	public static ActivityQueue getActivityQueue() {
		return INSTANCE;
	}

	public static LinkedList<Activity> getActivityLinkQueue() {
		return queue;
	}

	public static int getSize() {
		return queue.size();
	}
}
