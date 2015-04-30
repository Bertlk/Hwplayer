package com.personal.hwplayer.ui.adapter;

import java.util.ArrayList;

import com.personal.hwplayer.R;
import com.personal.hwplayer.model.LocalVedioInfo;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LocalVedioAdapter extends BaseAdapter {

	private ArrayList<LocalVedioInfo> localVedioInfos = null;
	private LayoutInflater mInflater;
	
	public LocalVedioAdapter(ArrayList<LocalVedioInfo> LocalVedioInfos,Context context){
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.localVedioInfos = LocalVedioInfos;
	}
	
	public void setData(ArrayList<LocalVedioInfo> localVedioInfos){
		this.localVedioInfos = localVedioInfos;
	}
	
	@Override
	public int getCount() {
		return localVedioInfos==null?0:localVedioInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return localVedioInfos==null?null:localVedioInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Holder holder;
		if(convertView == null){
			holder = new Holder();
			convertView = mInflater.inflate(R.layout.item_localvedio_list, parent, false);
			holder.item_play_pic = (ImageView) convertView.findViewById(R.id.item_play_pic);
			holder.vedio_name = (TextView) convertView.findViewById(R.id.vedio_name);
			holder.vedio_describe = (TextView) convertView.findViewById(R.id.vedio_describe);
		}else{
			holder = (Holder) convertView.getTag();
		}
		
		LocalVedioInfo localVedioInfo = localVedioInfos.get(position);
		
		Bitmap miniThumbBitmap = localVedioInfo.getMiniThumbBitmap();
		if(miniThumbBitmap!=null && (!miniThumbBitmap.isRecycled())){
			holder.item_play_pic.setImageBitmap(miniThumbBitmap);
		}else{
			holder.item_play_pic.setImageResource(R.drawable.default_340_192);
		}
		holder.vedio_name.setText(localVedioInfo.getName());
		holder.vedio_describe.setText(localVedioInfo.getDuration());
		convertView.setTag(holder);
		return convertView;
	}
	
	private class Holder{
		public ImageView item_play_pic;
		public TextView vedio_name;
		public TextView vedio_describe;
		
	}
	
}
