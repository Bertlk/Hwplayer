package com.personal.hwplayer.ui.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.personal.hwplayer.R;
import com.personal.hwplayer.model.LocalVedioInfo;
import com.personal.hwplayer.model.VedioInfo;
import com.personal.hwplayer.model.VedioListData;

public class MainActivity extends BaseActivity implements OnClickListener {

	private EditText main_et;
	private Button main_bt;
	private ImageView test_iv;
	
	private static Uri sVideoStorageURI = Uri.parse("content://media/external/video/media");
	
	private VedioListData vedioListData;
	private ArrayList<LocalVedioInfo> localVedioInfos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initData();
		
	}

	private void initView(){
		main_et = (EditText) findViewById(R.id.main_et);
		main_bt = (Button) findViewById(R.id.main_bt);
		test_iv = (ImageView) findViewById(R.id.test_iv);
		
		main_bt.setOnClickListener(this);
	}
	
	private void initData(){
		vedioListData = new VedioListData(getContentResolver(), sVideoStorageURI);
		localVedioInfos = vedioListData.query();
		System.out.println("############### localVedioInfos : " + localVedioInfos);
		
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.main_bt:
			
			Intent intent = new Intent(this, LocalVedioListActivity.class);
			startActivity(intent);
			
			//点击了播放按钮
//			String scource = main_et.getText().toString().trim();
//			
//			if(TextUtils.isEmpty(scource)){
//				//TODO 为空
//				showShortToast("请输入视频源");
//			}else if(!checkedScource(scource)){
//				//TODO 源不合法
//				showShortToast("视频源不合法");
//			}else{
//				//TODO 播放
////				String[] scources = new String[]{scource};
//				String[] scources = new String[]{
//						"http://d.17url.net/mv/upload/own/zuitianmideshiguang_102202/gq/20140626103058-00001.mp4",
//						"http://d.17url.net/mv/upload/own/zuitianmideshiguang_102202/gq/20140626103058-00002.mp4",
//						"http://d.17url.net/mv/upload/own/zuitianmideshiguang_102202/gq/20140626103058-00003.mp4"};
////				String[] scources = new String[]{
////						"http://103.41.141.75/youku/6564F9066A3A7A98BDCA24DB/03000201005472B9561DD31A34407715D9D7DC-D0BA-AE99-0AFA-42B3BD862631.flv"};
////				String[] scources = new String[]{
////						"http://k.youku.com/player/getFlvPath/sid/442502104602712beec5b_00/st/flv/fileid/030002131254E76DF1BDF2080D48DD0495879C-7F36-6F25-89BB-CC76C11A924C?K=53f1c8818f361920261e333b&hd=0&myp=0&ts=235&ypp=0&ep=diaWH0mPV84E4yPdjz8bNXm3J3JdXP4J9h%2bFg9NhAbkhTJ24n0qnxu%2fEOP1CHvltdipyZOv1otXvbUlnYfc1qm4Q2TiqPPro94KR5d4gw5ZzZxhHe8%2fTx1SXRjH3&ctype=12&ev=1&token=9516&oip=3549922219"};
//				gotoPlay(scources);
//			}
			break;
		default:
			break;
		}
		
	}
	
	
	/**
	 * 检查源是否合法
	 * @param scource
	 * @return
	 */
	private boolean checkedScource(String scource){
		//TODO 检查源的合法性
		
		return true;
	}
	
	
	private void gotoPlay(String[] scources){
		
		Uri[] uris = new Uri[scources.length];
		for (int i = 0; i < scources.length; i++) {
			Uri uri = Uri.parse(scources[i]);
			uris[i] = uri;
		}
		
		VedioInfo vedioInfo = new VedioInfo();
		vedioInfo.setVedioName("最甜蜜的时光");
		vedioInfo.setUris(uris);
		
//		Intent intent = new Intent(this, SystemPlayerActivity.class);
		Intent intent = new Intent(this, VitamioPlayerActivity.class);
		intent.putExtra(SystemPlayerActivity.VEDIO_TYPE_KEY, SystemPlayerActivity.VEDIO_SINGLE_SCOURCE);
		intent.putExtra(SystemPlayerActivity.VEDIO_DATA_KEY, vedioInfo);
		startActivity(intent);
	}
	
}
