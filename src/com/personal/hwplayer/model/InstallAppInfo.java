package com.personal.hwplayer.model;

import java.io.Serializable;

public class InstallAppInfo implements Serializable{

	private static final long serialVersionUID = -1681368806514767518L;

	@SuppressWarnings("unused")
	private static final String TAG = "InstallAppInfo";
	
	private String packageName;
	private String version;

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "{package:" + packageName + ", version:" + version + "}";
	}

}
