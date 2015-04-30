package com.personal.hwplayer.ui.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.personal.hwplayer.R;
import com.personal.hwplayer.model.LocalVedioInfo;
import com.personal.hwplayer.model.VedioListData;
import com.personal.hwplayer.ui.adapter.LocalVedioAdapter;

public class LocalVedioListActivity extends BaseActivity implements OnItemClickListener {

	private ListView localvedio_list;   
	private Button common_title_left_bt;
	private TextView common_title_text_tv;
	
	private ArrayList<LocalVedioInfo> localVedioInfos;
	private VedioListData vedioListData;
	private LocalVedioAdapter mAdapter;
	
	private static final Uri sVideoStorageURI = Uri.parse("content://media/external/video/media");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_localvedio);
		initView();
		initData();
	}
	
	private void initView(){
		
		common_title_left_bt = (Button) findViewById(R.id.common_title_left_bt);
		common_title_left_bt.setText("返回");
		common_title_left_bt.setOnClickListener(this);
		
		common_title_text_tv = (TextView) findViewById(R.id.common_title_text_tv);
		common_title_text_tv.setText("本地视频");
		common_title_text_tv.setVisibility(View.VISIBLE);
		
		localvedio_list = (ListView) findViewById(R.id.localvedio_list);
		
		View header = new View(mContext);
		LayoutParams layoutParams = new LayoutParams(0, 0);
		header.setLayoutParams(layoutParams);
		localvedio_list.addHeaderView(header);
		
		View footer = new View(mContext);
		footer.setLayoutParams(layoutParams);
		localvedio_list.addFooterView(footer);
		
		mAdapter = new LocalVedioAdapter(localVedioInfos, mContext);
		localvedio_list.setAdapter(mAdapter);
		localvedio_list.setOnItemClickListener(this);
		
	}
	
	private void initData(){
		vedioListData = new VedioListData(getContentResolver(), sVideoStorageURI);
		localVedioInfos = vedioListData.query();
		mAdapter.setData(localVedioInfos);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		int playPosition = position-1;
		if(localVedioInfos==null||playPosition<0||playPosition>=localVedioInfos.size()){
			return;
		}
		
		Intent intent = new Intent(this, SystemPlayerActivity.class);
		intent.putExtra(SystemPlayerActivity.VEDIO_TYPE_KEY, SystemPlayerActivity.VEDIO_LIST_SCOURCE);
		intent.putExtra(SystemPlayerActivity.VEDIO_DATA_KEY, localVedioInfos);
		intent.putExtra(SystemPlayerActivity.VEDIO_LIST_CURRENTPOSITION_KEY, position-1);
		startActivity(intent);
		
	}
	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.common_title_left_bt:
			finish();
			break;

		default:
			break;
		}
	}
	
	private void recycle(){
		if(localVedioInfos!=null){
			int size = localVedioInfos.size();
			for (int i = 0; i < size; i++) {
				Bitmap miniThumbBitmap = localVedioInfos.get(i).getMiniThumbBitmap();
				if(miniThumbBitmap!=null){
					miniThumbBitmap.recycle();
				}
			}
			localVedioInfos.clear();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		recycle();
	}
	
}
