package com.personal.hwplayer.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.content.Context;

public class SerializationUtil {

	public static final String POSTFIX = ".mark";
	
//	/**
//	 * 保存本地关注
//	 * @param mContext
//	 * @param ser
//	 */
//	public static void wirteSerializationBookmarks(Context mContext,Serializable ser,String bookName){
//		saveObject(mContext, ser, bookName+POSTFIX);
//	}
//	
//	/**
//	 * 读取本地关注列表
//	 * @param mContext
//	 * @return
//	 */
//	public static Bookmarks readSerializationBookmarks(Context mContext,String bookName){
//		return (Bookmarks)readObject(mContext, bookName+POSTFIX);
//	}
//	
//	/**
//	 * 清除本地关注列表
//	 * @param context
//	 */
//	public static void removeSerializationBookmarks(Context context,String bookName){
//		removeLocalDataSerialization(context, bookName+POSTFIX);
//	}
	
	/**
	 * 清除本地数据
	 * @param mContext
	 * @param type
	 */
	private static void removeLocalDataSerialization(Context mContext,String type){
		File data = mContext.getFileStreamPath(type);
		if (data.exists()){
			data.delete();
		}
	}
	
	/**
	 * 判断缓存是否存在
	 * @param cachefile
	 * @return
	 */
	public static boolean isExistDataCache(Context mContext, String cachefile)
	{
		boolean exist = false;
		File data = mContext.getFileStreamPath(cachefile);
		if(data.exists())
			exist = true;
		return exist;
	}
	
	/**
	 * 读取对象
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private static Serializable readObject(Context mContext, String file){
		if(!isExistDataCache(mContext, file))
			return null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try{
			fis = mContext.openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (Serializable)ois.readObject();
		}catch(FileNotFoundException e){
		}catch(Exception e){
			e.printStackTrace();
			//反序列化失败 - 删除缓存文件
			if(e instanceof InvalidClassException){
				File data = mContext.getFileStreamPath(file);
				data.delete();
			}
		}finally{
			try {
				ois.close();
			} catch (Exception e) {}
			try {
				fis.close();
			} catch (Exception e) {}
		}
		return null;
	}
	
	/**
	 * 保存对象
	 * @param ser
	 * @param file
	 * @throws IOException
	 */
	private static boolean saveObject(Context mContext, Serializable ser, String file) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try{
			fos = mContext.openFileOutput(file, Context.MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.flush();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}finally{
			try {
				oos.close();
			} catch (Exception e) {}
			try {
				fos.close();
			} catch (Exception e) {}
		}
	}
}
