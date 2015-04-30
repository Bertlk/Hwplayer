package com.personal.hwplayer.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class VedioInfo implements Parcelable{
	
	protected String vedioName = "未知";//影片名称
	private String[] scources;//影片片源
	protected Uri[] uris;//影片URI
	private Boolean isLive = false;//是否是直播
	
	public String getVedioName() {
		return vedioName;
	}
	public void setVedioName(String vedioName) {
		if(TextUtils.isEmpty(vedioName)){
			vedioName = "未知";
		}
		this.vedioName = vedioName;
	}
	public Uri[] getUris() {
		return uris;
	}
	public void setUris(Uri[] uris) {
		this.uris = uris;
	}
	public String[] getScources() {
		return scources;
	}
	public void setScources(String[] scources) {
		this.scources = scources;
	}
	public Boolean getIsLive() {
		return isLive;
	}
	public void setIsLive(Boolean isLive) {
		this.isLive = isLive;
	}
	public VedioInfo(){}
	
	public VedioInfo(Parcel in){
		vedioName = in.readString();
		scources = in.createStringArray();
		Parcelable[] parcelableArray = in.readParcelableArray(Uri.class.getClassLoader());
		if(parcelableArray!=null && parcelableArray.length>0){
			uris = new Uri[parcelableArray.length];
			for (int i = 0; i < parcelableArray.length; i++) {
				uris[i] = (Uri) parcelableArray[i];
			}
		}
		isLive = (in.readInt()==1);
	}
	
	
	public static final Parcelable.Creator<VedioInfo> CREATOR = new Parcelable.Creator<VedioInfo>() {
		@Override
		public VedioInfo createFromParcel(Parcel in) {
			return new VedioInfo(in);
		}

		@Override
		public VedioInfo[] newArray(int size) {
			return new VedioInfo[size];
		}
	};
	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(vedioName);
		out.writeStringArray(scources);
		out.writeParcelableArray(uris, flags);
		out.writeInt(isLive?1:0);
	}
	
}
