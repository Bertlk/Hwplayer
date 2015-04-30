package com.personal.hwplayer.utils;

import static android.os.Environment.MEDIA_MOUNTED;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.Toast;

import com.personal.hwplayer.ApplicationManager;
import com.personal.hwplayer.R;
import com.personal.hwplayer.model.InstallAppInfo;

/**
 * 系统信息工具<br>
 * 
 * 
 */
public class SystemUtil {
	private final static String TAG = SystemUtil.class.getSimpleName();
	private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
	private static final String INDIVIDUAL_DIR_NAME = "images";
	public static final String CACHE_DOWNLOAD = "video";
	public static final String ONLINE_PICTURE = "picture";
	public static final String DOWNLOAD_APK = "apk";
	public static final String ONLINE_IMAGE = "image";
	public static final String PHOTO_IMAGE = "photo";
	public static final String UNZIP_BOOK = "book";

	public static final int DOWNLOAD_APK_TYPE = 1;
	public static final int DOWNLOAD_IMAGE_TYPE = 2;
	public static final int CACHE_IMAGE_TYPE = 3;
	public static final int UNZIP_BOOK_TYPE = 4;
	private static final SystemUtil sSystemUtil = new SystemUtil();
	private static Context sContext;

	private static DisplayMetrics dm = new DisplayMetrics();

	private HashMap<String, String> mPathMap = new HashMap<String, String>();
	/**
	 * 手机机身存储路径
	 */
	private static final String INTERNAL_PATH = "internal";
	/**
	 * 手机SDcard存储路径
	 */
	private static final String EXTERNAL_PATH = "exterNal";

	/**
	 * 获取{@link com.SystemUtil.player.util.SystemUtil}对象<br>
	 * 单例方法<br>
	 * 同步
	 * 
	 * @return {@link com.SystemUtil.player.util.SystemUtil}对象
	 */
	public static SystemUtil getInstance() {

		if (sContext == null) {
			throw new IllegalArgumentException(
					" init(Context context) should be run first !!! ");
		}

		return sSystemUtil;
	}

	/**
	 * 初始化{@link com.SystemUtil.player.util.SystemUtil}
	 * 
	 * @param context
	 *            当前上下文
	 */
	public static void init(Context context) {

		if (context == null) {
			throw new NullPointerException("Context is null");
		}
		sContext = context;

	}

	private SystemUtil() {}

	/**
	 * 
	 * 获取机身可用空间(格式化)
	 * 
	 * @return String 机身可用空间
	 */
	public String getFreeSpaceFormatByPhoneBody() {
		long freeSpace = 0;
		// 得到外部存储的路径
		String externalStorageDirectory = getInternalPhoneBodyPath();
		if (!"".equals(externalStorageDirectory)) {
			// 计算空间
			StatFs stat = new StatFs(externalStorageDirectory);
			long blockSize = stat.getBlockSize();
			long availableBlock = stat.getAvailableBlocks();
			freeSpace = blockSize * availableBlock;
		}

		return Formatter.formatFileSize(sContext, freeSpace);
	}

	/**
	 * 获取机身可用空间(字节)
	 * 
	 * @return long 机身可用空间字节数
	 */
	public long getFreeSpaceByteByPhoneBody() {
		long freeSpace = 0;
		// 得到外部存储的路径
		String externalStorageDirectory = getInternalPhoneBodyPath();
		if (!"".equals(externalStorageDirectory)) {
			// 计算空间
			StatFs stat = new StatFs(externalStorageDirectory);
			long blockSize = stat.getBlockSize();
			long availableBlock = stat.getAvailableBlocks();
			freeSpace = blockSize * availableBlock;
		}

		return freeSpace;
	}

	/**
	 * 
	 * 获取机身空间总的大小(字节)
	 * 
	 * @return long 机身空间总的字节数
	 */
	public long getTotalSpaceByteByPhoneBody() {
		long totalSpace = 0;
		// 得到外部存储的路径
		String externalStorageDirectory = getInternalPhoneBodyPath();
		if (!"".equals(externalStorageDirectory)) {
			// 计算空间
			StatFs stat = new StatFs(externalStorageDirectory);
			long blockSize = stat.getBlockSize();
			long totalBlock = stat.getBlockCount();
			totalSpace = blockSize * totalBlock;
		}
		return totalSpace;
	}

	/**
	 * 
	 * 获取SDCard可用空间(格式化)
	 * 
	 * @return String SDCard可用空间
	 */
	public String getFreeSpaceFormatBySDCard() {
		// 总大小
		long allStorageFreeSpace = getFreeSpaceByteByAllStorage();
		// 机身大小
		long phoneBodyFreeSpace = getFreeSpaceByteByPhoneBody();
		// sdcard大小
		long sdcardFreeSpace = allStorageFreeSpace - phoneBodyFreeSpace;

		return Formatter.formatFileSize(sContext, sdcardFreeSpace);
	}

	/**
	 * 
	 * 获取SDCard可用空间(字节)
	 * 
	 * @return String SDCard可用空间
	 */
	public long getFreeSpaceByteBySDCard() {
		// 总大小
		long allStorageFreeSpace = getFreeSpaceByteByAllStorage();
		// 机身大小
		long phoneBodyFreeSpace = getFreeSpaceByteByPhoneBody();
		// sdcard大小
		long sdcardFreeSpace = allStorageFreeSpace - phoneBodyFreeSpace;

		return sdcardFreeSpace;
	}

	/**
	 * 
	 * 获取SDCard空间总的大小(字节)
	 * 
	 * @return long SDCard空间总的字节数
	 */
	public long getTotalSpaceByteBySDCard() {
		// 总大小
		long allStorageTotalSpace = getTotalSpaceByteByAllStorage();
		// 机身大小
		long phoneBodyTotalSpace = getTotalSpaceByteByPhoneBody();
		// sdcard大小
		long sdcardTotalSpace = allStorageTotalSpace - phoneBodyTotalSpace;

		return sdcardTotalSpace;
	}

	/**
	 * 
	 * 判断SDCard是否挂载
	 * 
	 * @return boolean <br>
	 *         true 已经挂载 <br>
	 *         false 未挂载
	 */
	public boolean isSDCardMounted() {

		// google要求4.4以上版本(包括4.4)sdcard不允许第三方应用操作,除非有root权限
		// 所以大于4.4的直接显示机身空间
		if (VERSION.SDK_INT >= 19) {

			return false;

		} else {

			// 总大小
			long allStorageTotalSpace = getTotalSpaceByteByAllStorage();
			// 机身大小
			long phoneBodyTotalSpace = getTotalSpaceByteByPhoneBody();
			// sdcard大小
			long sdcardTotalSpace = allStorageTotalSpace - phoneBodyTotalSpace;

			return sdcardTotalSpace != 0;
		}

	}

	/**
	 * 
	 * 获取SDCard或机身可用空间和总空间百分比(0-100之间)<br>
	 * (如果SDCard已挂载,返回SDCard空间百分比; 如果SDCard未挂载,返回机身空间百分比)
	 * 
	 * @return int 百分数
	 */
	public int getFreeSpacePercent(boolean isMounted) {
		long freeSpace = 0;
		long totalSpace = 0;

		if (isMounted) {
			freeSpace = getFreeSpaceByteBySDCard();
			totalSpace = getTotalSpaceByteBySDCard();
		} else {
			freeSpace = getFreeSpaceByteByPhoneBody();
			totalSpace = getTotalSpaceByteByPhoneBody();
		}

		if (totalSpace == 0) {
			return 0;
		}

		return (int) (100 * (totalSpace - freeSpace) / totalSpace);
	}

	/**
	 * 
	 * 获取SDCard和机身可用空间之和(字节)
	 * 
	 * @return long SDCard和机身可用空间之和
	 */
	public static long getFreeSpaceByteByAllStorage() {

		// 得到已经挂载的存储路径
		HashMap<String, String> mountStorageHashMap = getMountStoragePath();
		// 得到外部存储的路径
		String externalStorageDirectory = Environment
				.getExternalStorageDirectory().getAbsolutePath();
		mountStorageHashMap.put(externalStorageDirectory,
				externalStorageDirectory);

		// 遍历每个目录,并计算空间
		Set<String> keySet = mountStorageHashMap.keySet();
		long freeSpace = 0;
		for (String path : keySet) {
			StatFs stat = new StatFs(path);
			long blockSize = stat.getBlockSize();
			long availableBlock = stat.getAvailableBlocks();
			freeSpace += blockSize * availableBlock;
		}

		return freeSpace;
	}

	/**
	 * 
	 * 鑾峰彇SDCard鍜屾満韬彲鐢ㄧ┖闂翠箣鍜�(鏍煎紡鍖�)
	 * 
	 * @return long SDCard鍜屾満韬彲鐢ㄧ┖闂翠箣鍜�
	 */
	public static String getFreeSpaceFormatByAllStorage() {

		// 寰楀埌宸茬粡鎸傝浇鐨勫瓨鍌ㄨ矾寰�
		HashMap<String, String> mountStorageHashMap = getMountStoragePath();
		// 寰楀埌澶栭儴瀛樺偍鐨勮矾寰�
		String externalStorageDirectory = Environment
				.getExternalStorageDirectory().getAbsolutePath();
		mountStorageHashMap.put(externalStorageDirectory,
				externalStorageDirectory);

		// 閬嶅巻姣忎釜鐩綍,骞惰绠楃┖闂�
		Set<String> keySet = mountStorageHashMap.keySet();
		long freeSpace = 0;
		for (String path : keySet) {
			StatFs stat = new StatFs(path);
			long blockSize = stat.getBlockSize();
			long availableBlock = stat.getAvailableBlocks();
			freeSpace += blockSize * availableBlock;
		}
		return Formatter.formatFileSize(sContext, freeSpace);
	}

	/**
	 * 
	 * 鑾峰彇SDCard鍜屾満韬�荤┖闂翠箣鍜�(鏍煎紡鍖�)
	 * 
	 * @return String SDCard鍜屾満韬彲鐢ㄧ┖闂翠箣鍜�
	 */
	public static String getTotalSpaceFormatByAllStorage() {

		// 寰楀埌宸茬粡鎸傝浇鐨勫瓨鍌ㄨ矾寰�
		HashMap<String, String> mountStorageHashMap = getMountStoragePath();
		// 寰楀埌澶栭儴瀛樺偍鐨勮矾寰�
		String externalStorageDirectory = Environment
				.getExternalStorageDirectory().getAbsolutePath();
		mountStorageHashMap.put(externalStorageDirectory,
				externalStorageDirectory);

		// 閬嶅巻姣忎釜鐩綍,骞惰绠楃┖闂�
		Set<String> keySet = mountStorageHashMap.keySet();
		long totalSpace = 0;
		for (String path : keySet) {
			StatFs stat = new StatFs(path);
			long blockSize = stat.getBlockSize();
			long totalBlock = stat.getBlockCount();
			totalSpace += blockSize * totalBlock;
		}

		return Formatter.formatFileSize(sContext, totalSpace);
	}

	/**
	 * 
	 * 获取SDCard和机身总空间之和(字节)
	 * 
	 * @return String SDCard和机身可用空间之和
	 */
	public static long getTotalSpaceByteByAllStorage() {

		// 得到已经挂载的存储路径
		HashMap<String, String> mountStorageHashMap = getMountStoragePath();
		// 得到外部存储的路径
		String externalStorageDirectory = Environment
				.getExternalStorageDirectory().getAbsolutePath();
		mountStorageHashMap.put(externalStorageDirectory,
				externalStorageDirectory);

		// 遍历每个目录,并计算空间
		Set<String> keySet = mountStorageHashMap.keySet();
		long totalSpace = 0;
		for (String path : keySet) {

			StatFs stat = new StatFs(path);
			long blockSize = stat.getBlockSize();
			long totalBlock = stat.getBlockCount();
			totalSpace += blockSize * totalBlock;
		}

		return totalSpace;
	}

	/**
	 * 获取个人缓存目录可用存储空间(图片缓存模块私有)
	 * 
	 * @return 个人缓存目录可用存储空间
	 */
	public static int getFreeSpaceByIndividualCache() {
		int freeSize = 0;

		if (hasStorage()) {
			StatFs statFs = new StatFs(getIndividualCacheDirectory()
					.getAbsolutePath());

			try {
				long nBlocSize = statFs.getBlockSize();
				long nAvailaBlock = statFs.getAvailableBlocks();
				freeSize = (int) (nBlocSize * nAvailaBlock / 1024 / 1024);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return freeSize;
	}

	/**
	 * 
	 * 返回当前设备已经挂载的所有路径
	 * 
	 * @return ArrayList<String> 路径集合
	 */
	public static ArrayList<String> getAllStoragePath() {
		// 得到已经挂载的存储路径
		HashMap<String, String> mountStorageHashMap = getMountStoragePath();
		// 得到外部存储的路径
		String externalStorageDirectory = Environment
				.getExternalStorageDirectory().getAbsolutePath();
		File file = new File(externalStorageDirectory);
		if (file.exists()) {
			mountStorageHashMap.put(externalStorageDirectory,
					externalStorageDirectory);
		}

		// 遍历每个目录,并计算空间
		Set<String> keySet = mountStorageHashMap.keySet();
		return new ArrayList<String>(keySet);
	}

	/**
	 * 
	 * 通过adb shell mount方式获取存储路径(解决机身和sdcard共存时,获取不到sdcard路径问题)
	 * 
	 * @return HashMap<String, String> 存储路径集合
	 */
	private static HashMap<String, String> getMountStoragePath() {
		HashMap<String, String> mountStorageHashMap = new HashMap<String, String>();
		try {
			Runtime runtime = Runtime.getRuntime();
			Process proc = runtime.exec("mount");
			InputStream is = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			String line;
			BufferedReader br = new BufferedReader(isr);
			while ((line = br.readLine()) != null) {

				if (line.contains("secure"))
					continue;
				if (line.contains("asec"))
					continue;

				// 判断磁盘格式(firmware是网络模块,非存储,过滤)
				if ((line.contains("fat") || line.contains("fuse"))
						&& (!line.contains("firmware") && !line
								.contains("ext4"))) {
					String columns[] = line.split(" ");
					if (columns != null && columns.length > 1) {
						String path = columns[1];
						File file = new File(path);
						if (file.exists()) {
							// 将不规则的路径转换成规则的路径
							String fileCanonicalPath = file.getCanonicalPath();

							// 过滤特殊路径
							if (fileCanonicalPath.contains("legacy")
									|| fileCanonicalPath
											.contains("Android/obb")
									|| fileCanonicalPath.contains("shell")
									|| fileCanonicalPath.contains("data")) {
								String externalStorageDirectory = Environment
										.getExternalStorageDirectory()
										.getAbsolutePath();
								File fileTemp = new File(
										externalStorageDirectory);
								if (fileTemp.exists()) {
									fileCanonicalPath = externalStorageDirectory;
								}

							}

							mountStorageHashMap.put(fileCanonicalPath,
									fileCanonicalPath);
						}

					}
				}
				// else if (line.contains("fuse")) {
				// String columns[] = line.split(" ");
				// if (columns != null && columns.length > 1) {
				// sdcardPath = columns[1];
				// }
				// }
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mountStorageHashMap;
	}

	private static boolean checkFsWritable() {
		// Create a temporary file to see whether a volume is really writeable.
		// It's important not to put it in the root directory which may have a
		// limit on the number of files.

		// L.d(TAG, "checkFsWritable directoryName ==   "
		// + PathCommonDefines.APP_FOLDER_ON_SD);

		File directory = new File(getIndividualCacheDirectory()
				.getAbsolutePath());
		if (!directory.isDirectory()) {
			if (!directory.mkdirs()) {
				L.e(TAG, "checkFsWritable",
						"checkFsWritable directoryName 000  ");
				return false;
			}
		}
		File f = new File(getIndividualCacheDirectory().getAbsolutePath(),
				".probe");
		try {
			// Remove stale file if any()
			if (f.exists()) {
				f.delete();
			}
			if (!f.createNewFile()) {
				return false;
			}
			f.delete();
			return true;
		} catch (IOException ex) {
			return false;
		}
	}

	private static boolean hasStorage() {
		boolean hasStorage = false;
		String str = Environment.getExternalStorageState();

		if (str.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
			hasStorage = checkFsWritable();
		}

		return hasStorage;
	}

	/**
	 * Returns application cache directory. Cache directory will be created on
	 * SD card <i>("/Android/data/[app_package_name]/cache")</i> if card is
	 * mounted and app has appropriate permission. Else - Android defines cache
	 * directory on device's file system.
	 * 
	 * @param context
	 *            Application context
	 * @return Cache {@link File directory}.<br />
	 *         <b>NOTE:</b> Can be null in some unpredictable cases (if SD card
	 *         is unmounted and {@link android.content.Context#getCacheDir()
	 *         Context.getCacheDir()} returns null).
	 */
	public static File getCacheDirectory() {
		return getCacheDirectory(true);
	}

	/**
	 * Returns application cache directory. Cache directory will be created on
	 * SD card <i>("/Android/data/[app_package_name]/cache")</i> (if card is
	 * mounted and app has appropriate permission) or on device's file system
	 * depending incoming parameters.
	 * 
	 * @param context
	 *            Application context
	 * @param preferExternal
	 *            Whether prefer external location for cache
	 * @return Cache {@link File directory}.<br />
	 *         <b>NOTE:</b> Can be null in some unpredictable cases (if SD card
	 *         is unmounted and {@link android.content.Context#getCacheDir()
	 *         Context.getCacheDir()} returns null).
	 */
	static File getCacheDirectory(boolean preferExternal) {
		File appCacheDir = null;
		if (preferExternal
				&& MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				&& hasExternalStoragePermission(sContext)) {
			appCacheDir = getExternalCacheDir(sContext);
		}
		if (appCacheDir == null) {
			appCacheDir = sContext.getCacheDir();
		}
		if (appCacheDir == null) {
			String cacheDirPath = "/data" + File.separator + "data"
					+ File.separator + sContext.getPackageName() + "/cache/";
			L.w(TAG,"getCacheDirectory",
					"Can't define system cache directory! '%s' will be used."
							+ cacheDirPath);
			appCacheDir = new File(cacheDirPath);
		}
		return appCacheDir;
	}

	/**
	 * Returns individual application cache directory (for only image caching
	 * from ImageLoader). Cache directory will be created on SD card
	 * <i>("/Android/data/[app_package_name]/cache/uil-images")</i> if card is
	 * mounted and app has appropriate permission. Else - Android defines cache
	 * directory on device's file system.
	 * 
	 * @param context
	 *            Application context
	 * @return Cache {@link File directory}
	 */
	public static File getIndividualCacheDirectory() {
		File cacheDir = getCacheDirectory();
		File individualCacheDir = new File(cacheDir, INDIVIDUAL_DIR_NAME);
		if (!individualCacheDir.exists()) {
			if (!individualCacheDir.mkdir()) {
				individualCacheDir = cacheDir;
			}
		}
		return individualCacheDir;
	}

	/**
	 * Returns specified application cache directory. Cache directory will be
	 * created on SD card by defined path if card is mounted and app has
	 * appropriate permission. Else - Android defines cache directory on
	 * device's file system.
	 * 
	 * @param context
	 *            Application context
	 * @param cacheDir
	 *            Cache directory path (e.g.: "AppCacheDir",
	 *            "AppDir/cache/images")
	 * @return Cache {@link File directory}
	 */
	public static File getOwnCacheDirectory(Context context, String cacheDir) {
		File appCacheDir = null;
		if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				&& hasExternalStoragePermission(context)) {
			appCacheDir = new File(Environment.getExternalStorageDirectory(),
					cacheDir);
		}
		if (appCacheDir == null
				|| (!appCacheDir.exists() && !appCacheDir.mkdirs())) {
			appCacheDir = context.getCacheDir();
		}
		return appCacheDir;
	}

	private static File getExternalCacheDir(Context context) {
		File dataDir = new File(new File(
				Environment.getExternalStorageDirectory(), "Android"), "data");
		File appCacheDir = new File(
				new File(dataDir, context.getPackageName()), "cache");
		if (!appCacheDir.exists()) {
			if (!appCacheDir.mkdirs()) {
				L.w(TAG,"getExternalCacheDir","Unable to create external cache directory");
				return null;
			}
			try {
				new File(appCacheDir, ".nomedia").createNewFile();
			} catch (IOException e) {
				L.i(TAG,
						"Can't create \".nomedia\" file in application external cache directory");
			}
		}
		return appCacheDir;
	}

	private static boolean hasExternalStoragePermission(Context context) {
		int perm = context
				.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
		return perm == PackageManager.PERMISSION_GRANTED;
	}

	/**
	 * 检测sdcard是否可用
	 * 
	 * @return true为可用，否则为不可用
	 */
	public static boolean sdCardIsAvailable() {
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED))
			return false;
		return true;
	}

	/**
	 * 获取缓存目录
	 * 
	 * @param type
	 *            1:缓存下载目录 ， 2： 海报， 3：下载apk 4.cache文件夹
	 * @return
	 */
	public static String getEBookCacheFolder(int type) {
		String status = Environment.getExternalStorageState();
		L.v(TAG, "getFoneCacheFolder status:", status);
		if (!status.equals(Environment.MEDIA_MOUNTED)) {
			String path = null;
			switch (type) {
			case DOWNLOAD_APK_TYPE:
				path = SystemUtil.getInstance().getRootPath() + File.separator
						+ DOWNLOAD_APK;
				break;
			case DOWNLOAD_IMAGE_TYPE:
				path = SystemUtil.getInstance().getRootPath() + File.separator
						+ ONLINE_IMAGE;
				break;
			case CACHE_IMAGE_TYPE:
				path = SystemUtil.getInstance().getRootPath() + File.separator
						+ PHOTO_IMAGE;
				break;
			case UNZIP_BOOK_TYPE:
				path = SystemUtil.getInstance().getRootPath() + File.separator
				+ UNZIP_BOOK;
				break;
			default:
				break;
			}
			return path;
		}

		File file = new File(SystemUtil.getInstance().getRootPath());
		if (!file.exists()) {
			file.mkdirs();
		}

		File childFile = null;
		switch (type) {
		case DOWNLOAD_APK_TYPE:
			childFile = new File(SystemUtil.getInstance().getRootPath()
					+ File.separator + DOWNLOAD_APK);
			if (!childFile.exists()) {
				childFile.mkdirs();
			}
			break;
		case DOWNLOAD_IMAGE_TYPE:
			childFile = new File(SystemUtil.getInstance().getRootPath()
					+ File.separator + ONLINE_IMAGE);
			if (!childFile.exists()) {
				childFile.mkdirs();
			}
			break;
		case CACHE_IMAGE_TYPE:
			childFile = new File(SystemUtil.getInstance().getRootPath()
					+ File.separator + PHOTO_IMAGE);
			if (!childFile.exists()) {
				childFile.mkdirs();
			}
			break;
		case UNZIP_BOOK_TYPE:
			childFile = new File(SystemUtil.getInstance().getRootPath()
					+ File.separator + UNZIP_BOOK);
			if (!childFile.exists()) {
				childFile.mkdirs();
			}
			break;
		default:
			break;
		}

		if (childFile == null) {
			return null;
		}

		L.v(TAG, "getFoneCacheFolder childFile:", "childFile getPath : "
				+ childFile.getPath() + " type : " + type);

		return childFile.getPath();
	}

	/**
	 * 删除指定路径文件夹中文件(包括该文件夹中的文件 及子文件夹)
	 * 
	 * @param path
	 * @return 参数X描述... （注意：请将参数、描述都对齐）
	 * @return boolean
	 * @throws
	 */
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 删除指定路径文件夹
	 * 
	 * @param folderPath
	 * @return void
	 * @throws
	 */
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 获取内部机身存储路径
	 * 
	 * @return 机身存储路径
	 */
	public String getInternalPhoneBodyPath() {
		// 获取存储路径
		ArrayList<String> filePathList = getAllStoragePath();

		// 保存存储路径
		saveToPathMap(filePathList);
		String internalPath = mPathMap.get(INTERNAL_PATH);
		return internalPath == null ? "" : internalPath;
	}

	/**
	 * 
	 * 获取外部Sdcard存储路径
	 * 
	 * @return 外部Sdcard存储路径
	 */
	public String getExternalSDCardPath() {

		// 获取存储路径
		ArrayList<String> filePathList = getAllStoragePath();

		// 保存存储路径
		saveToPathMap(filePathList);

		String externalPath = mPathMap.get(EXTERNAL_PATH);

		return externalPath == null ? "" : externalPath;
	}

	public void saveToPathMap(ArrayList<String> arrayPath) {
		L.v(TAG, "saveToPathMap", "arrayPath=" + arrayPath);
		mPathMap.clear();
		String sysInternalPath = Environment.getExternalStorageDirectory()
				.getPath();
		L.v(TAG, "saveToPathMap", "sysInternalPath=" + sysInternalPath);
		if (arrayPath == null || arrayPath.size() == 0) {
			mPathMap.put(INTERNAL_PATH, sysInternalPath);
			return;
		}

		// 海信手机特殊处理
		if (android.os.Build.MODEL.equals("HS-U939")) {
			for (int i = 0; i < arrayPath.size(); i++) {
				String path = arrayPath.get(i);
				if (path.equals(sysInternalPath) && new File(path).exists()) {// 外置sd
					L.v(TAG, "saveToPathMap", "HS-U939 EXTERNAL=" + path);
					if (Environment.getExternalStorageState().equals(
							Environment.MEDIA_MOUNTED)) {
						mPathMap.put(EXTERNAL_PATH, path);
					}
				} else {// 内置sd
					if (!path.equals(sysInternalPath)
							&& new File(path).exists()
							&& new File(path).canWrite()
							&& filterStoragePath(path)) {
						L.v(TAG, "saveToPathMap", "HS-U939 INTERNAL=" + path);
						mPathMap.put(INTERNAL_PATH, path);
					}
				}
			}
			return;
		}

		if (isSpecialPhone(android.os.Build.MODEL)) {
			L.v(TAG, "saveToPathMap", "isSpecialPhone=true");
			for (int i = 0; i < arrayPath.size(); i++) {
				String path = arrayPath.get(i);
				if (path.equals(sysInternalPath)) { // 外置sd
					if (new File(path).exists() && new File(path).canWrite()) {
						L.v(TAG, "saveToPathMap", "EXTERNAL_PATH=" + path);
						mPathMap.put(EXTERNAL_PATH, path);
					} else {
						L.v(TAG, "saveToPathMap", "not write INTERNAL_PATH="
								+ path);
					}
				} else {// 外置sd
					if (new File(path).exists() && new File(path).canWrite()
							&& filterStoragePath(path)) {
						L.v(TAG, "saveToPathMap", "INTERNAL_PATH=" + path);
						mPathMap.put(INTERNAL_PATH, path);
					} else {
						L.v(TAG, "saveToPathMap", "not write INTERNAL_PATH="
								+ path);
					}
				}
			}
		} else {
			L.v(TAG, "saveToPathMap", "isSpecialPhone=false");
			for (int i = 0; i < arrayPath.size(); i++) {
				String path = arrayPath.get(i);
				if (path.equals(sysInternalPath)) { // 内置sd
					if (new File(path).exists() && new File(path).canWrite()) {
						L.v(TAG, "saveToPathMap", "INTERNAL_PATH=" + path);
						mPathMap.put(INTERNAL_PATH, path);
					} else {
						L.v(TAG, "saveToPathMap", "not write INTERNAL_PATH="
								+ path);
					}
				} else {// 外置sd
					if (new File(path).exists() && new File(path).canWrite()
							&& filterStoragePath(path)) {
						L.v(TAG, "saveToPathMap", "EXTERNAL=" + path);
						mPathMap.put(EXTERNAL_PATH, path);
					} else {
						L.v(TAG, "saveToPathMap", "not write EXTERNAL_PATH="
								+ path);
					}
				}
			}
		}
		if (mPathMap.size() == 0) {
			mPathMap.put(INTERNAL_PATH, sysInternalPath);
		}
	}

	/**
	 * 特殊手机：只有外置sd卡，没有手机存储
	 * 
	 * @param phone
	 * @return
	 */
	public boolean isSpecialPhone(String phone) {
		L.v(TAG, "isSpecialPhone", "phone : " + phone);
		String phones[] = sContext.getResources().getStringArray(
				R.array.cache_phone_facturers);
		L.v(TAG, "isSpecialPhone", "phones : " + phones);
		boolean res = false;
		for (int i = 0; i < phones.length; i++) {
			if (phones[i].toLowerCase(Locale.getDefault()).equals(
					phone.toLowerCase(Locale.getDefault()))) {
				res = true;
				break;
			}
		}
		return res;
	}

	/**
	 * 过滤特殊手机扫到的假的外置sd卡路径
	 * 
	 * @param path
	 * @return
	 */
	public boolean filterStoragePath(String path) {
		boolean res = true;
		if (path.equals("/firmware")) {
			res = false;
		}
		return res;
	}

	/**
	 * 
	 * 获取离线缓存路径(默认存储在sdcard中)
	 * 
	 * @return 离线缓存路径
	 */
	public String getOfflineCachePath() {
		String path = getRootPath();
		path += File.separator + CACHE_DOWNLOAD;
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return path;
	}

	/**
	 * 
	 * 获取在线播放缓存路径(默认存储在sdcard中)
	 * 
	 * @return 在线播放缓存路径
	 */
	public String getOnlineCachePath() {
		String path = getRootPath();
		path += File.separator + ONLINE_IMAGE;
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return path;
	}

	/**
	 * 
	 * 获取离线缓存路径(默认存储在sdcard中)
	 * 
	 * @return 离线缓存路径
	 */
	public String getRootPath() {

		boolean isMounted = isSDCardMounted();
		String path = "";
		if (isMounted) {
			path = getExternalSDCardPath();
		} else {
			path = getInternalPhoneBodyPath();
		}

		// 尝试创建路径
		path = createPath(path);

		// google要求4.4以上版本(包括4.4)sdcard不允许第三方应用操作,除非有root权限
		// 所以要判断路径是否创建成功
		if (VERSION.SDK_INT >= 19) {

			path = getInternalPhoneBodyPath();

			path = createPath(path);

		}
		return path;
	}

	private String createPath(String path) {
		path += File.separator
				+ sContext.getResources().getString(R.string.cache_app_name)
				+ File.separator + "download";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return path;
	}

	/**
	 * 
	 * 存储设备是否挂载
	 * 
	 * @return boolean 是否挂载
	 */
	public boolean isStorageMounted() {
		boolean result = false;
		// 获取存储路径
		ArrayList<String> filePathList = getAllStoragePath();

		// 保存存储路径
		saveToPathMap(filePathList);
		for (String folderPath : filePathList) {
			File file = new File(folderPath);

			if (file.canRead() && file.canWrite()) {
				result = true;
			} else {
				result = false;
			}

		}
		return result;
	}

	/**
	 * 获取指定包名app的信息
	 * 
	 * @param packageName
	 * @return
	 */
	public PackageInfo getPackageInfo(Context context, String packageName) {

		if (TextUtils.isEmpty(packageName))
			return null;
		try {
			return context.getPackageManager().getPackageInfo(packageName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
		} catch (NameNotFoundException e) {
			return null;
		}
	}

	/**
	 * 安装指定路径下的.apk
	 * 
	 * @param filePath
	 * 
	 * @return void
	 */
	public void installApp(String filePath) {
		File file = new File(filePath);

		if (file.exists()) {

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(file),
					"application/vnd.android.package-archive");
			sContext.startActivity(intent);

		} else {
			L.e(TAG, "installApp", " file is not exists !!! ");
		}

	}

	/**
	 * 卸载指定包名的app
	 * 
	 * @param pageName
	 */
	public void uninstall(String pageName) {

		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_DELETE);
		intent.setData(Uri.parse("package:" + pageName));
		sContext.startActivity(intent);
	}

	/**
	 * 通过报名打开app
	 * 
	 * @param packageName
	 * @param context
	 */
	public static void openApp(String packageName, Context context) {

		L.v(TAG, "openApp", "packageName : " + packageName);

		PackageManager packageManager = context.getPackageManager();
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(packageName);

		List<ResolveInfo> apps = packageManager.queryIntentActivities(
				resolveIntent, 0);

		ResolveInfo ri = apps.iterator().next();
		if (ri != null) {
			String className = ri.activityInfo.name;

			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
					| Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

			ComponentName cn = new ComponentName(packageName, className);

			intent.setComponent(cn);
			context.startActivity(intent);
		}
	}

	private void showUninstallAPKIcon(String apkPath) {
		String PATH_PackageParser = "android.content.pm.PackageParser";
		String PATH_AssetManager = "android.content.res.AssetManager";
		try {
			// PackageParser packageParser = new PackageParser(apkPath);
			Class pkgParserCls = Class.forName(PATH_PackageParser);
			Class[] typeArgs = new Class[1];
			typeArgs[0] = String.class;
			Constructor pkgParserCt = pkgParserCls.getConstructor(typeArgs);
			Object[] valueArgs = new Object[1];
			valueArgs[0] = apkPath;
			Object pkgParser = pkgParserCt.newInstance(valueArgs);
			L.v(TAG,"ANDROID_LAB", "pkgParser:" + pkgParser.toString());
			DisplayMetrics metrics = new DisplayMetrics();
			metrics.setToDefaults();
			// PackageParser.Package mPkgInfo = packageParser.parsePackage(new
			// File(apkPath), apkPath,
			// metrics, 0);
			typeArgs = new Class[4];
			typeArgs[0] = File.class;
			typeArgs[1] = String.class;
			typeArgs[2] = DisplayMetrics.class;
			typeArgs[3] = Integer.TYPE;
			Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod(
					"parsePackage", typeArgs);
			valueArgs = new Object[4];
			valueArgs[0] = new File(apkPath);
			valueArgs[1] = apkPath;
			valueArgs[2] = metrics;
			valueArgs[3] = 0;
			Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser,
					valueArgs);
			// ApplicationInfo info = mPkgInfo.applicationInfo;
			Field appInfoFld = pkgParserPkg.getClass().getDeclaredField(
					"applicationInfo");
			ApplicationInfo info = (ApplicationInfo) appInfoFld
					.get(pkgParserPkg);
			L.v(TAG,"ANDROID_LAB", "pkg:" + info.packageName + " uid=" + info.uid);
			// Resources pRes = getResources();
			// AssetManager assmgr = new AssetManager();
			// assmgr.addAssetPath(apkPath);
			// Resources res = new Resources(assmgr, pRes.getDisplayMetrics(),
			// pRes.getConfiguration());
			// Class assetMagCls = Class.forName(PATH_AssetManager);
			// Constructor assetMagCt = assetMagCls.getConstructor((Class[])
			// null);
			// Object assetMag = assetMagCt.newInstance((Object[]) null);
			// typeArgs = new Class[1];
			// typeArgs[0] = String.class;
			// Method assetMag_addAssetPathMtd =
			// assetMagCls.getDeclaredMethod("addAssetPath",
			// typeArgs);
			// valueArgs = new Object[1];
			// valueArgs[0] = apkPath;
			// assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);
			// Resources res = getResources();
			// typeArgs = new Class[3];
			// typeArgs[0] = assetMag.getClass();
			// typeArgs[1] = res.getDisplayMetrics().getClass();
			// typeArgs[2] = res.getConfiguration().getClass();
			// Constructor resCt = Resources.class.getConstructor(typeArgs);
			// valueArgs = new Object[3];
			// valueArgs[0] = assetMag;
			// valueArgs[1] = res.getDisplayMetrics();
			// valueArgs[2] = res.getConfiguration();
			// res = (Resources) resCt.newInstance(valueArgs);
			// CharSequence label = null;
			// if (info.labelRes != 0) {
			// label = res.getText(info.labelRes);
			// }
			// // if (label == null) {
			// // label = (info.nonLocalizedLabel != null) ?
			// info.nonLocalizedLabel
			// // : info.packageName;
			// // }
			// L.d("ANDROID_LAB", "label=" + label);
			// if (info.icon != 0) {
			// Drawable icon = res.getDrawable(info.icon);
			// ImageView image = (ImageView) findViewById(R.id.apkIconBySodino);
			// image.setVisibility(View.VISIBLE);
			// image.setImageDrawable(icon);
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断手机是否已经ROOT
	 * 
	 * @return 手机是否已经ROOT
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static boolean hasRooted() {
		Process process = null;
		DataOutputStream out = null;
		try {
			process = Runtime.getRuntime().exec("su");
			out = new DataOutputStream(process.getOutputStream());
			out.writeBytes("\n");
			out.writeBytes("exit\n");
			out.flush();
			process.waitFor();
		} catch (IOException e) {
			L.e(TAG, "hasRooted", "Unexpected error - Here is what I know: "
					+ e.getMessage());
			return false;
		} catch (InterruptedException e) {
			L.e(TAG, "hasRooted",
					"InterruptedException error : " + e.getMessage());
			return false;
		} finally {
			try {
				if (out != null)
					out.close();
				process.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * 获取已安装的app信息
	 * 
	 * @param context
	 */
	public static ArrayList<InstallAppInfo> getInstallAppInfos(Context context) {
		List<PackageInfo> installedPackages = context.getPackageManager()
				.getInstalledPackages(0);
		ArrayList<InstallAppInfo> InstallAppInfoList = new ArrayList<InstallAppInfo>(
				installedPackages.size());

		for (PackageInfo installedPackage : installedPackages) {
			if ((installedPackage.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				InstallAppInfo installAppInfo = new InstallAppInfo();
				installAppInfo.setPackageName(installedPackage.packageName);
				installAppInfo.setVersion(String
						.valueOf(installedPackage.versionCode));
				InstallAppInfoList.add(installAppInfo);
			}
		}
		return InstallAppInfoList;
	}

	/**
	 * 判断网络是否连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetOkWithToast(Context context) {

		if (context == null) {
			return false;
		}

		if (getCurrentNetType(context) == 0) {
			L.v(TAG, "isNetOkWithToast", "no_network_toast");
			Toast.makeText(context, "当前无网络，请检查网络设置", Toast.LENGTH_LONG).show();
		}
		return getCurrentNetType(context) != 0;
	}

	/**
	 * 判断网络是否连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetOk(Context context) {
		return getCurrentNetType(context) != 0;
	}

	/**
	 * 
	 * @param context
	 * @return 1 : wifi 2:gprs 3:3g 4:4g 0:no net
	 */
	public static int getCurrentNetType(Context context) {
		if (null == context) {
			return 0;
		}
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);// 获取系统的连接服务
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();// 获取网络的连接情况
		if (null != activeNetInfo) {
			L.v(TAG, "getNetType : ", activeNetInfo.toString());
			if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				// 判断WIFI网
				return 1;
			} else if (activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				TelephonyManager mTelephonyManager = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);
				int type = mTelephonyManager.getNetworkType();
				if (type == TelephonyManager.NETWORK_TYPE_UNKNOWN
						|| type == TelephonyManager.NETWORK_TYPE_GPRS
						|| type == TelephonyManager.NETWORK_TYPE_EDGE
						|| type == TelephonyManager.NETWORK_TYPE_IDEN
						|| type == TelephonyManager.NETWORK_TYPE_1xRTT
						|| type == TelephonyManager.NETWORK_TYPE_CDMA) {
					// 判断gprs网
					return 2;
				} else if(type == TelephonyManager.NETWORK_TYPE_LTE){
					return 4;
				}
				else {
					// 判断3g网
					return 3;
				}
			}
		}
		return 0;
	}

	/**
	 * 获取手机服务商信息 需要加入权限<uses-permission
	 * android:name="android.permission.READ_PHONE_STATE"/>
	 * 
	 * @param context
	 * @return 1 移动 2 电信 3 联通
	 */
	public static int getProvider(Context context) {

		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		int provider = 0;
		// 返回唯一的用户ID;就是这张卡的编号神马的
		String IMSI = telephonyManager.getSubscriberId();
		// IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。

		// 1 移动 2 电信 3 联通
		if (IMSI != null) {
			if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
				provider = 1;
			} else if (IMSI.startsWith("46001")) {
				provider = 3;
			} else if (IMSI.startsWith("46003")) {
				provider = 2;
			}
		}
		return provider;
	}

	/**
	 * 设置Activity全屏
	 */
	public static void SetFullScreen(Activity act) {
		act.requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	/**
	 * 获取手机厂商
	 * 
	 * @return
	 */
	public static String getPhoneBrand() {
		return android.os.Build.MANUFACTURER + "_" + android.os.Build.MODEL;
	}

	/**
	 * 获取系统版本
	 * 
	 * @return
	 */
	public static String getSDKVersion() {
		return "Andriod" + android.os.Build.VERSION.RELEASE;
	}

	/**
	 * 获取渠道号
	 * 
	 * @return
	 */
	public static String getChannelCode() {
		String code = "own";
		try {
			ApplicationInfo info = ApplicationManager
					.getGlobalContext()
					.getPackageManager()
					.getApplicationInfo(
							ApplicationManager.getGlobalContext()
									.getPackageName(),
							PackageManager.GET_META_DATA);
			code = info.metaData.getString("channel");
		} catch (Exception e) {
			L.e(TAG, "getChannelCode", e.getClass().getSimpleName());
			code = "own";
		}
		return code;
	}

	/**
	 * 获取手机 udid
	 * 
	 * @return udid
	 */
	public static String getPhoneUdid() {

		String udidStr = PreferenceHelper.getString("udid", "");
		if (!TextUtils.isEmpty(udidStr)) {
			return udidStr;
		}
		udidStr = ((TelephonyManager) ApplicationManager.getGlobalContext()
				.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		PreferenceHelper.putString("udid", udidStr);
		return udidStr;
	}

	/**
	 * 获取应用版本号
	 * 
	 * @return
	 */
	public static int getVersionCode(Context context) {
		int version = -1;
		try {
			// 获取packagemanager的实例
			PackageManager packageManager = context.getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			PackageInfo packInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			version = packInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return version;
	}

	/**
	 * 获取应用版本名
	 * 
	 * @return
	 */
	public static String getVersionName(Context context) {
		String version = "";
		try {
			// 获取packagemanager的实例
			PackageManager packageManager = context.getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			PackageInfo packInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			version = packInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return version;
	}

	/**
	 * 获取app的信息
	 * 
	 * @param packageName
	 * @return
	 */
	public static PackageInfo getPackageInfo(Context context) {

		try {
			PackageManager packageManager = context.getPackageManager();
			return packageManager.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			return null;
		}
	}

	/**
	 * 获取分辨
	 * 
	 * @return
	 */
	public static DisplayMetrics getDisplayMetrics() {
		return dm;
	}

	/**
	 * 从相机获取
	 */
	public static void getPhotoFromCamera(Activity mActivity, Uri uri,
			int requestCode) {
		// 激活相机
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// 判断存储卡是否可以用，可用进行存储
		if (checkSDCardAvailable()) {
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			// 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
			mActivity.startActivityForResult(intent, requestCode);
		} else {
			Toast.makeText(mActivity.getApplicationContext(), "SD卡不可用",
					Toast.LENGTH_SHORT).show();
		}
	}

	public static boolean checkSDCardAvailable() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * 获取当前ip
	 * 
	 * @return
	 */
	public static String getNetIP() {
		int netType = getCurrentNetType(sContext);
		String ip = "";
		if (netType == 1) {
			// 获取wifi服务
			WifiManager wifiManager = (WifiManager) sContext
					.getSystemService(Context.WIFI_SERVICE);
			// 判断wifi是否开启
			if (!wifiManager.isWifiEnabled()) {
				wifiManager.setWifiEnabled(true);
			}
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			int ipAddress = wifiInfo.getIpAddress();
			ip = intToIp(ipAddress);
		} else {
			ip = getLocalIpAddress();
		}
		return ip;
	}

	private static String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + (i >> 24 & 0xFF);
	}

	private static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (Exception ex) {
			L.e(TAG,"WifiPreference IpAddress", ex.toString());
			return null;
		}
		return null;
	}
}
