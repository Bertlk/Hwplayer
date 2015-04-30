package com.personal.hwplayer.model;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.util.Log;

public class VedioListData {

    private static final String[] sProjection = new String[] {
            Video.Media._ID, Video.Media.DATA, Video.Media.DATE_MODIFIED, Video.Media.BUCKET_ID,
            Video.Media.TITLE, Video.Media.MINI_THUMB_MAGIC, Video.Media.MIME_TYPE,
            Video.Media.DURATION, Video.Media.SIZE, Video.Media.DATE_ADDED,
    };
    
    
    final public static int INDEX_ID = indexOf(sProjection, Video.Media._ID);								//ID Int
    final public static int INDEX_DATA = indexOf(sProjection, Video.Media.DATA);							//路径 String
    final public static int INDEX_DATE_MODIFIED = indexOf(sProjection, Video.Media.DATE_MODIFIED); 			//最近修改日期	 long
    final public static int INDEX_BUCKET_ID = indexOf(sProjection, Video.Media.BUCKET_ID);					//The bucket id of the image		 long
    final public static int INDEX_TITLE = indexOf(sProjection, Video.Media.TITLE);							//标题  String
    final public static int INDEX_MINI_THUMB_MAGIC = indexOf(sProjection, Video.Media.MINI_THUMB_MAGIC);	//缩略图 id  long
    final public static int INDEX_MIME_TYPE = indexOf(sProjection, Video.Media.MIME_TYPE);					//MIME_TYPE String 
    final public static int INDEX_DURATION = indexOf(sProjection, Video.Media.DURATION);					//总时长  long
    final public static int INDEX_SIZE = indexOf(sProjection, Video.Media.SIZE);							//文件大小 long
    final public static int INDEX_DATE_ADDED = indexOf(sProjection, Video.Media.DATE_ADDED);				//插入数据时间
    
    
    private ContentResolver mContentResolver;
    private Uri mStroageUri;
    private Options options;
    
    private ArrayList<LocalVedioInfo> localVedioInfos = new ArrayList<LocalVedioInfo>();
    
    public VedioListData(ContentResolver cr, Uri uri){
        mContentResolver = cr;
        mStroageUri = uri;
        
        options = new Options();    
        options.inDither = false;    
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inSampleSize  = 2;
    }
    
    /**
     * 查询视频列表
     */
    public ArrayList<LocalVedioInfo> query(){
    	//TODO 查询视频列表
    	Cursor cursor = createCursor();
        if (cursor == null) {
            Log.e("Exception", "unable to create video cursor for " + mStroageUri);
            throw new UnsupportedOperationException();
        }
        
        localVedioInfos.clear();
        while(cursor.moveToNext()){
        	LocalVedioInfo localVedioInfo = new LocalVedioInfo();
        	localVedioInfo.parse(cursor, mContentResolver,options);
        	localVedioInfos.add(localVedioInfo);
        }
        cursor.close();
        return localVedioInfos;
        
    }
    
    /**
     * 获取本地列表
     * @return
     */
    public ArrayList<LocalVedioInfo> getLocalVedioInfo(){
    	return localVedioInfos;
    }
    
    private Cursor createCursor() {
        Cursor c = Images.Media.query(mContentResolver, mStroageUri, sProjection, null, null,
                sortOrder());
        return c;
    }
    
    private String sortOrder() {
        //return Video.Media._ID + (mSort == ImageManager.SORT_ASCENDING ? " ASC " : " DESC");
        return Video.Media.DATE_MODIFIED + " DESC ";
    }
    
    
    private static int indexOf(String[] array, String s) {
    	
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(s)) {
                return i;
            }
        }
        return -1;
    }
	
}
