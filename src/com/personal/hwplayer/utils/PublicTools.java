package com.personal.hwplayer.utils;

import java.io.File;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Class Name: PlayListsActivity.java<br>
 * Function:工具类<br>
 * 
 * Modifications:<br>
 * 
 * @author ZYT DateTime 2014-5-15 下午2:06:29<br>
 * @version 1.0<br>
 * <br>
 */
public class PublicTools {

	/** 缩略图准备好，可以显示 */
	public static final int THUMBNAIL_PREPARED = 1;
	/** 缩略图空 ，显示默认 */
	public static final int THUMBNAIL_EMPTY = 0;
	/** 缩略图损坏 ，显示默认 */
	public static final int THUMBNAIL_CORRUPTED = -1;

	public static final int MINI_INTERVAL = 50;
	public static final int SHORT_INTERVAL = 150;
	public static final int MIDDLE_INTERVAL = 300;
	public static final int LONG_INTERVAL = 600;
	public static final int LONG_LONG_INTERVAL = 6000;

	private static final int FILENAMELENGTH = 80;

	public static long getBucketId(String path) {
		return path.toLowerCase().hashCode();
	}

	public static boolean isFilenameIllegal(String filename) {
		return (filename.length() <= FILENAMELENGTH);
	}

	/**
	 * Function:判断是否横屏<br>
	 * 
	 * @author ZYT DateTime 2014-5-15 下午2:36:40<br>
	 * @return true：横屏；false:竖屏 <br>
	 */
	// public static boolean isLandscape() {
	// // Log.v(TAG,"isLandscape : "+ mDisplay.getOrientation());
	// return (1 == mDisplay.getOrientation());
	// }

	public static boolean isFileExist(String filepath) {
		File file = new File(filepath);
		return file.exists();
	}

	public static void sleep(int interval) {
		try {
			Thread.sleep(interval);
		} catch (Exception e) {
		}
	}

	public static AlertDialog hint(Context context, int StringId) {
		return new AlertDialog.Builder(context)
				.setMessage(context.getString(StringId))
				.setNeutralButton("确定", null).show();
	}

	public static Cursor query(ContentResolver resolver, Uri uri,
			String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		try {
			if (resolver == null) {
				return null;
			}
			return resolver.query(uri, projection, selection, selectionArgs,
					sortOrder);
		} catch (UnsupportedOperationException ex) {
			return null;
		}
	}

	public static boolean isMediaScannerScanning(ContentResolver cr) {
		boolean result = false;
		Cursor cursor = query(cr, MediaStore.getMediaScannerUri(),
				new String[] { MediaStore.MEDIA_SCANNER_VOLUME }, null, null,
				null);
		if (cursor != null) {
			if (cursor.getCount() == 1) {
				cursor.moveToFirst();
				result = "external".equals(cursor.getString(0));
			}
			cursor.close();
		}

		return result;
	}

	public static boolean isVideoStreaming(Uri uri) {
		return ("http".equalsIgnoreCase(uri.getScheme()) || "rtsp"
				.equalsIgnoreCase(uri.getScheme()));
	}
	
	public static String replaceFilename(String filepath, String name) {
		String newPath = "";
		int lastSlash = filepath.lastIndexOf('/');
		if (lastSlash >= 0) {
			lastSlash++;
			if (lastSlash < filepath.length()) {
				newPath = filepath.substring(0, lastSlash);
			}
		}
		newPath = newPath + name;
		int lastDot = filepath.lastIndexOf('.');
		if (lastDot > 0) {
			newPath = newPath
					+ filepath.substring(lastDot, filepath.length());
		}
		return newPath;
	}
}
