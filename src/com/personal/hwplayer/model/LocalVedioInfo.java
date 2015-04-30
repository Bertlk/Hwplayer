package com.personal.hwplayer.model;

import com.personal.hwplayer.utils.FileUtils;
import com.personal.hwplayer.utils.StringUtil;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

public class LocalVedioInfo extends VedioInfo {

	public static final String _lock = "";

	private int voideId;			//视频id
	private String path;			//视频路径
	private String name;			//视频名称
	private Bitmap miniThumbBitmap;	//缩略图
	private String mimeType;		//视频类型
	private String duration;		//视频总时长
	private String size;			//视频文件大小
	private String createDate;		//视频创建时间

	public int getVoideId() {
		return voideId;
	}

	public void setVoideId(int voideId) {
		this.voideId = voideId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
		if(uris==null){
			this.uris = new Uri[1];
		}
		uris[0] = Uri.parse(path);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		this.vedioName = name;
	}

	public Bitmap getMiniThumbBitmap() {
		return miniThumbBitmap;
	}

	public void setMiniThumbBitmap(Bitmap miniThumbBitmap) {
		this.miniThumbBitmap = miniThumbBitmap;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	@Override
	public String toString() {
		return "LocalVedioInfo [voideId=" + voideId + ", path=" + path
				+ ", name=" + name + ", miniThumbBitmap=" + miniThumbBitmap
				+ ", mimeType=" + mimeType + ", duration=" + duration
				+ ", size=" + size + ", createDate=" + createDate + "]";
	}

	public void parse(Cursor mCursor, ContentResolver mContentResolver,
			Options options) {

		synchronized (_lock) {

			if (mCursor == null) {
				return;
			}
			voideId = mCursor.getInt(VedioListData.INDEX_ID);
			path = mCursor.getString(VedioListData.INDEX_DATA);
			name = mCursor.getString(VedioListData.INDEX_TITLE);
			// mimeType =
			// parseMimeType(mCursor.getString(VedioListData.INDEX_MIME_TYPE));
			mimeType = parseMimeTypeByPath(path);
			duration = StringUtil.stringForTime(mCursor
					.getLong(VedioListData.INDEX_DURATION));
			size = FileUtils.getFileSize(mCursor
					.getLong(VedioListData.INDEX_SIZE));
			miniThumbBitmap = MediaStore.Video.Thumbnails.getThumbnail(
					mContentResolver, voideId,
					MediaStore.Video.Thumbnails.MINI_KIND, options);

			// createDate = mCursor.getS(VedioListData.INDEX_DATE_MODIFIED);
			vedioName = name;
			uris = new Uri[1];
			uris[0] = Uri.parse(path);
		}
	}

	private String parseMimeType(String mimeType) {

		if (TextUtils.isEmpty(mimeType)) {
			return mimeType;
		}
		String[] split = mimeType.split("/");
		if (split.length == 2) {
			return split[1];
		}
		return mimeType;
	}

	private String parseMimeTypeByPath(String path) {
		if (TextUtils.isEmpty(path)) {
			return null;
		}
		String[] split = path.split("\\.");
		if (split.length == 0) {
			return null;
		}
		return split[split.length - 1];
	}

	/**
	 * 获取视频的缩略图 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
	 * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
	 * 
	 * @param videoPath
	 *            视频的路径
	 * @param width
	 *            指定输出视频缩略图的宽度
	 * @param height
	 *            指定输出视频缩略图的高度度
	 * @param kind
	 *            参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
	 *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
	 * @return 指定大小的视频缩略图
	 */
	private static Bitmap getVideoThumbnail(String videoPath, int width,
			int height, int kind) {
		Bitmap bitmap = null;
		// 获取视频的缩略图
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		// 切割缩略图
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

}
