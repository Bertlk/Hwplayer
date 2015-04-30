package com.personal.hwplayer.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.text.TextUtils;

/**
 * 文本工具类
 * 
 * @author JasonZue
 * @since 2014-5-16
 */

@SuppressLint("SimpleDateFormat")
public class StringUtil {
	private static final String TAG = "StringUtil";

	private final static SimpleDateFormat dateFormater = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private final static SimpleDateFormat dateFormater2 = new SimpleDateFormat(
			"yyyy-MM-dd");

	public static boolean checkEmailAndMobile(String userName) {
		boolean flag = false;
		if (checkEmail(userName) || checkMobile(userName)) {
			flag = true;
		}
		return flag;
	}

	public static boolean checkEmail(String email) {
		boolean flag = false;
		if (email.length() > 50) {
			return flag;
		}
		try {
			String check = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			L.i("验证邮箱地址错误", e.getMessage());
			flag = false;
		}
		return flag;
	}

	public static boolean checkChinese(String pwd) {
		boolean flag = true;
		Pattern pattern = Pattern.compile("[\u4E00-\u9FA5]");
		Matcher matc = pattern.matcher(pwd);
		while (matc.find()) {
			flag = false;
		}
		return flag;
	}

	public static boolean checkPassword(String pwd) {
		boolean flag = false;
		if (pwd.length() >= 6 && pwd.length() <= 24
				&& pwd.matches("[0-9A-Za-z]*")) {
			flag = true;
		}
		return flag;
	}

	public static boolean checkMobile(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,2,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		L.v(TAG, "isMobileNO", "m.matches(): " + m.matches());
		return m.matches();
	}

	public static boolean checkNickName(String nickName) {
		boolean flag = false;
		if (nickName.trim().length() == 0) {
			return flag;
		}
		if (nickName.length() >= 1 && nickName.length() <= 25) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean isEmpty(String input) {
		if (input == null || "".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断字符串是否为null或空串
	 * 
	 * @param str
	 * @return boolean
	 */
	public static boolean isNotEmpty(String str) {
		return !TextUtils.isEmpty(str);
	}

	/**
	 * 字符串转整数
	 * 
	 * @param str
	 * @param defValue
	 * @return
	 */
	public static int toInt(String str, int defValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
		}
		return defValue;
	}

	/**
	 * 对象转整数
	 * 
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static int toInt(Object obj) {
		if (obj == null)
			return 0;
		return toInt(obj.toString(), 0);
	}

	/**
	 * 对象转整数
	 * 
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static long toLong(String obj) {
		try {
			return Long.parseLong(obj);
		} catch (Exception e) {
		}
		return 0;
	}

	/**
	 * 字符串转布尔值
	 * 
	 * @param b
	 * @return 转换异常返回 false
	 */
	public static boolean toBool(String b) {
		try {
			return Boolean.parseBoolean(b);
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 将字符串转位日期类型
	 * 
	 * @param sdate
	 * @return
	 */
	public static String toDefinedDate(SimpleDateFormat formater,long timeMillis) {
		if (timeMillis<0 || formater == null)
			return null;
			Date date = new Date(timeMillis);
			return formater.format(date);
	}
	/**
	 * 将字符串转位日期类型
	 * 
	 * @param sdate
	 * @return
	 */
	public static Date toDate(String sdate) {
		if (isEmpty(sdate))
			return null;
		try {
			return dateFormater.parse(sdate);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 以友好的方式显示时间
	 * 
	 * @param sdate
	 * @return
	 */
	public static String friendly_time(String sdate) {
		Date time = new Date(Long.valueOf(sdate));
		String ftime = "";
		Calendar cal = Calendar.getInstance();

		// 判断是否是同一天
		String curDate = dateFormater2.format(cal.getTime());
		String paramDate = dateFormater2.format(time);
		if (curDate.equals(paramDate)) {
			int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
			if (hour == 0)
				ftime = Math.max(
						(cal.getTimeInMillis() - time.getTime()) / 60000, 1)
						+ "分钟前";
			else
				ftime = hour + "小时前";
			return ftime;
		}

		long lt = time.getTime() / 86400000;
		long ct = cal.getTimeInMillis() / 86400000;
		int days = (int) (ct - lt);
		if (days == 0) {
			int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
			if (hour == 0)
				ftime = Math.max(
						(cal.getTimeInMillis() - time.getTime()) / 60000, 1)
						+ "分钟前";
			else
				ftime = hour + "小时前";
		} else if (days == 1) {
			ftime = "昨天";
		} else if (days == 2) {
			ftime = "前天";
		} else if (days > 2 && days <= 10) {
			ftime = days + "天前";
		} else if (days > 10) {
			ftime = dateFormater2.format(time);
		}
		return ftime;
	}

	/**
	 * 时间转换
	 * 
	 * @param timeMs
	 * @return
	 */
	public static String stringForTime(long timeMs) {
		long totalSeconds = timeMs / 1000;

		long seconds = totalSeconds % 60;
		long minutes = (totalSeconds / 60) % 60;
		long hours = totalSeconds / 3600;

		StringBuilder mFormatBuilder = new StringBuilder();
		Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
		String timeStr;
		if (hours > 0) {
			timeStr = mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
					.toString();
		} else {
			timeStr = mFormatter.format("%02d:%02d", minutes, seconds).toString();
		}
		mFormatter.close();
		return timeStr;
	}
	
	/**
	 * 格式化日期
	 * 
	 * @param date
	 *            日期
	 * @param patten
	 *            模版例如："yyyy-MM-dd"
	 * @return String
	 * @throws
	 */
	@SuppressLint("SimpleDateFormat")
	public static String formatDate(Date date, String patten) {
		if (date == null || TextUtils.isEmpty(patten)) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(patten);
		return sdf.format(date);
	}

	/**
	 * 解析日期
	 * 
	 * @param source
	 *            源字符串
	 * @param patten
	 *            模版例如："yyyy-mm-dd"
	 * @return
	 * @return Date
	 * @throws
	 */
	@SuppressLint("SimpleDateFormat")
	public static Date parserDate(String source, String patten) {
		if (TextUtils.isEmpty(source) || TextUtils.isEmpty(patten)) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(patten);
		try {
			return sdf.parse(source);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 判断给定字符串时间是否为今日
	 * 
	 * @param sdate
	 * @return boolean
	 */
	public static boolean isToday(String sdate) {
		boolean b = false;
		Date time = toDate(sdate);
		Date today = new Date();
		if (time != null) {
			String nowDate = dateFormater2.format(today);
			String timeDate = dateFormater2.format(time);
			if (nowDate.equals(timeDate)) {
				b = true;
			}
		}
		return b;
	}
}
