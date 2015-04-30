package com.personal.hwplayer.ui.activity;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.personal.hwplayer.R;
import com.personal.hwplayer.model.VedioInfo;
import com.personal.hwplayer.ui.view.VerticalSeekBar;
import com.personal.hwplayer.utils.FileUtils;
import com.personal.hwplayer.utils.StringUtil;
import com.personal.hwplayer.utils.SystemUtil;

public class VitamioPlayerActivity extends BaseActivity {

	public static final String VEDIO_TYPE_KEY = "vedio_type_key";
	public static final int VEDIO_SINGLE_SCOURCE = 1; //一部电影
	public static final int VEDIO_LIST_SCOURCE = 2;	  //多部电影
	
	public static final String VEDIO_DATA_KEY = "vedio_data_key";//电影信息key
	public static final String VEDIO_LIST_CURRENTPOSITION_KEY = "Vedio_list_currentposition_key";//多部电影时当前电影下标
	public static final String VEDIO_LIST_CURRENTINDEXT_KEY = "Vedio_list_currentIndext_key";//电影为多片时,当前片的下标
	
	
	private final static int PROGRESS_CHANGED = 0;
	private final static int UPDATE_TIME = 1;
	
	
	private final static int HIDE_CONTROLER_VIEW = 9;
	private final static int REMOVE_LOADING_VIEW = 10;
	
	private View video_cl;
	private VideoView video_view;
	private View video_buffer;
	private View player_loading;
	private View frame;
	private SeekBar playbackProgressBar,vioceProgressBar;
	private VerticalSeekBar brightness_seekBar;
	private TextView current_time,total_time;
	private Button btn_play_pause,btn_back,btn_forward,brightness_controler,btn_exit;
	private Button btn_voice;
	private TextView video_name,last_modify;
	private ImageView battery_state;
	
	private View play_button_layout,info_frame,info_brightness;
	private Animation play_controler_in,play_controler_out,
					  playinfo_frame_in,playinfo_frame_out,
					  brightness_frame_in,brightness_frame_out;
	
	private ArrayList<VedioInfo> vedioInfos;
	private int currentVedioIndext = 0;//当前电影的下标
	private int currentIndext = 0;//当前片段的下标
	
	private AudioManager mAudioManager;
	private int currentVolume = 0;//当前声音
	private int mAudioMax;//最大声音
	private float currentBrightness;
	private float mBrightnessMax = 255f;
	private float mBrightnessMin = 50f;
	
	private int batteryLevel = 0;//当前电量
	
	private int mCurrentPosition = 0; //压后台 记录当前播放时长
	
	private BroadcastReceiver batteryReceiver;
	private IntentFilter batteryIntentFilter;
	
	private SimpleDateFormat formater = null;
	
	private boolean isStop = true;
	private boolean isSilent = false;
	private boolean isBrightnessControlerShowing = false;
	private boolean isControlerShowing = false;
	private Handler mHandler;
	
	private Intent currentIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Vitamio.initialize(VitamioPlayerActivity.this);
		
		// 设置全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		// 设置屏幕保持光亮
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		
		// 声音管理：通过它可以调音
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		// 得到当前的音量0-15
		currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		mAudioMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		
		try {
			currentBrightness = (float) Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
			
			if(currentBrightness<mBrightnessMin){
				currentBrightness = mBrightnessMin;
				updateBright(currentBrightness);
			}else if(currentBrightness>mBrightnessMax){
				currentBrightness = mBrightnessMax;
				updateBright(currentBrightness);
			}
			
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		
		setContentView(R.layout.activity_vitamio_player);
		initView();
		initListener();
		initAnimations();
		initData();
		initHandler();
		initBreadcastReceiver();
		startPlay();
	}
	
	private void initView(){
		video_cl =  findViewById(R.id.video_cl);
		video_view = (VideoView) findViewById(R.id.video_view);
		video_buffer = findViewById(R.id.video_buffer);
		player_loading = findViewById(R.id.player_loading);
		frame = findViewById(R.id.frame);
		
		info_frame = findViewById(R.id.info_frame);
		play_button_layout = findViewById(R.id.play_button_layout);
		info_brightness = findViewById(R.id.info_brightness);
		
		playbackProgressBar = (SeekBar) frame.findViewById(R.id.PlaybackProgressBar);
//		PlaybackProgressBar.setThumbOffset(13);
		playbackProgressBar.setMax(100);
		playbackProgressBar.setSecondaryProgress(0);
		
		vioceProgressBar = (SeekBar) frame.findViewById(R.id.VioceProgressBar);
//		VioceProgressBar.setThumbOffset(13);
		vioceProgressBar.setMax(mAudioMax);
		vioceProgressBar.setProgress(currentVolume);
		
		brightness_seekBar = (VerticalSeekBar) frame.findViewById(R.id.brightness_seekBar);
		brightness_seekBar.setMax((int)(mBrightnessMax-mBrightnessMin));
		brightness_seekBar.setProgress((int)(currentBrightness-mBrightnessMin));
		
		current_time = (TextView) frame.findViewById(R.id.current_time);
		total_time = (TextView) frame.findViewById(R.id.total_time);
		
		btn_play_pause = (Button) findViewById(R.id.btn_play_pause);
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_forward = (Button) findViewById(R.id.btn_forward);
		brightness_controler = (Button) findViewById(R.id.brightness_controler);
		btn_exit = (Button) findViewById(R.id.btn_exit);
		
		video_name = (TextView) frame.findViewById(R.id.video_name);
		last_modify = (TextView) frame.findViewById(R.id.last_modify);
		battery_state = (ImageView) frame.findViewById(R.id.battery_state);
		btn_voice = (Button) findViewById(R.id.btn_voice);
		
		
		video_view.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);
		
	}
	
	@SuppressLint("NewApi")
	private void initListener(){
		
		player_loading.setOnClickListener(this);
		video_cl.setOnClickListener(this);
		btn_play_pause.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		btn_forward.setOnClickListener(this);
		brightness_controler.setOnClickListener(this);
		btn_exit.setOnClickListener(this);
		btn_voice.setOnClickListener(this);
		info_frame.setOnClickListener(this);
		play_button_layout.setOnClickListener(this);
		
		
		//设置准备监听
		video_view.setOnPreparedListener(new MyOnPreparedListener());
		//设置错误监听
		video_view.setOnErrorListener(new MyOnErrorListener());
		//设置卡顿监听
		video_view.setOnInfoListener(new MyOnInfoListener());
		//设置播放完毕监听
		video_view.setOnCompletionListener(new MyOnCompletionListener());
		
		
		playbackProgressBar.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener4vedio());
		vioceProgressBar.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener4Audio());
		brightness_seekBar.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener4Bright());
	}
	
	
	private void initData(){
		Intent intent = getIntent();
		currentIntent = intent;
		Uri uri = intent.getData();
		
		if(uri!=null){
			String name = FileUtils.getFileNameNoFormat(uri.toString());
			if(vedioInfos == null){
				vedioInfos = new ArrayList<VedioInfo>();
			}else{
				vedioInfos.clear();
			}
			
			VedioInfo vedioInfo = new VedioInfo();
			vedioInfo.setVedioName(name);
			vedioInfo.setUris(new Uri[]{uri});
			vedioInfos.add(vedioInfo);
			
			currentVedioIndext = 0;//当前电影的下标
			currentIndext = 0;//当前片段的下标
			
		}else{
			
			int type = intent.getIntExtra(VEDIO_TYPE_KEY, 0);
			switch (type) {
			case VEDIO_SINGLE_SCOURCE:
				VedioInfo info = intent.getParcelableExtra(VEDIO_DATA_KEY);
				if(info!=null){
					vedioInfos = new ArrayList<VedioInfo>();
					vedioInfos.add(info);
				}
				break;
			case VEDIO_LIST_SCOURCE:
				vedioInfos = intent.getParcelableArrayListExtra(VEDIO_DATA_KEY);
				break;
				
			default:
				break;
			}
			
		}
		
		if(vedioInfos==null||vedioInfos.size()==0){
			showShortToast("数据有误");
			return;
		}
		
		currentVedioIndext = intent.getIntExtra(VEDIO_LIST_CURRENTPOSITION_KEY, 0);
		currentIndext = intent.getIntExtra(VEDIO_LIST_CURRENTINDEXT_KEY, 0);
		
	}
	
	@SuppressLint("HandlerLeak")
	private void initHandler(){
		mHandler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				
				switch (msg.what) {
				case PROGRESS_CHANGED:
					updateProgress();
					break;
				case UPDATE_TIME:
					updateTime();
					break;
				case HIDE_CONTROLER_VIEW:
					hideControlerView();
					break;
				case REMOVE_LOADING_VIEW:
					player_loading.setVisibility(View.GONE);
					break;

				default:
					break;
				}
			}
			
		};
		updateTime();
	}
	
	private void initBreadcastReceiver(){
		batteryReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				int level = intent.getIntExtra("level", 0);
				if(batteryLevel != level){
					batteryLevel = level;
					updateBatteryLevel();
				}
				 
				// level加%就是当前电量了
			}
		};
		if(batteryIntentFilter==null){
			batteryIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
			// 监听电量变化
			registerReceiver(batteryReceiver, batteryIntentFilter);
		}
	}
	
	/**
	 * 初始化动画
	 */
	private void initAnimations(){
		brightness_frame_in = AnimationUtils.loadAnimation(this, R.anim.brightness_frame_in);
		brightness_frame_out = AnimationUtils.loadAnimation(this, R.anim.brightness_frame_out);
		play_controler_in = AnimationUtils.loadAnimation(this, R.anim.play_controler_in);
		play_controler_out = AnimationUtils.loadAnimation(this, R.anim.play_controler_out);
		playinfo_frame_in = AnimationUtils.loadAnimation(this, R.anim.playinfo_frame_in);
		playinfo_frame_out = AnimationUtils.loadAnimation(this, R.anim.playinfo_frame_out);
		
		brightness_frame_out.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				info_brightness.setVisibility(View.GONE);
				
			}
		});
		playinfo_frame_in.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				mHandler.removeMessages(HIDE_CONTROLER_VIEW);
				sendhideTimerMsg();
				
			}
		});
		
		playinfo_frame_out.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				frame.setVisibility(View.GONE);
				
			}
		});
		
	}
	
	/**
	 * 显示控制面板
	 */
	private void showControlerView(){
		mHandler.removeMessages(HIDE_CONTROLER_VIEW);
		if(!isControlerShowing){
			isControlerShowing = true;
			frame.setVisibility(View.VISIBLE);
			play_button_layout.startAnimation(play_controler_in);
			info_frame.startAnimation(playinfo_frame_in);
		}
	}
	
	/**
	 * 隐藏控制面板
	 */
	private void hideControlerView(){
		mHandler.removeMessages(HIDE_CONTROLER_VIEW);
		if(isControlerShowing){
			isControlerShowing = false;
			play_button_layout.startAnimation(play_controler_out);
			info_frame.startAnimation(playinfo_frame_out);
			hideBrightnessControler();
		}
	}
	
	/**
	 * 显示亮度调节控制
	 */
	private void showBrightnessControler(){
		if(!isBrightnessControlerShowing){
			isBrightnessControlerShowing = true;
			info_brightness.setVisibility(View.VISIBLE);
			info_brightness.startAnimation(brightness_frame_in);
		}
	}
	
	/**
	 * 关闭亮度调节控制
	 */
	private void hideBrightnessControler(){
		if(isBrightnessControlerShowing){
			isBrightnessControlerShowing = false;
			info_brightness.startAnimation(brightness_frame_out);
		}
	}
	
	private void sendhideTimerMsg(){
		if(video_view.isPlaying()){
			mHandler.sendEmptyMessageDelayed(HIDE_CONTROLER_VIEW, 4000);
		}
	}
	
	/**
	 * 开始播放
	 */
	private void startPlay(){
		
		mCurrentPosition = 0;
		if(vedioInfos.size()>currentVedioIndext){
			VedioInfo vedioInfo = vedioInfos.get(currentVedioIndext);
			Uri[] uris = vedioInfo.getUris();
			if(uris!=null && uris.length>currentIndext){
				video_view.setVideoURI(uris[currentIndext]);
				btn_play_pause.setBackgroundResource(R.drawable.btn_pause);
				updateName();
			}
		}else{
			showShortToast("数据有误");
			finish();
		}
		
	}
	
	private boolean nextVedio(){
		if(vedioInfos.size()>currentVedioIndext){
			VedioInfo vedioInfo = vedioInfos.get(currentVedioIndext);
			Uri[] uris = vedioInfo.getUris();
			if(uris!=null && uris.length>currentIndext+1){
//				player_loading.setVisibility(View.VISIBLE);
//				video_view.setVideoURI(uris[++currentIndext]);
				++currentIndext;
				updateName();
				openSystemPlayer();
				return true;
			}
		}else if(vedioInfos.size()>currentVedioIndext+1){
			VedioInfo vedioInfo = vedioInfos.get(++currentVedioIndext);
			Uri[] uris = vedioInfo.getUris();
			currentIndext =0;
			if(uris!=null && uris.length>currentIndext){
//				player_loading.setVisibility(View.VISIBLE);
//				video_view.setVideoURI(uris[currentIndext]);
				updateName();
				openSystemPlayer();
				return true;
			}
		}
		return false;
	}
	
	private boolean PreviousVedio(){
		if(vedioInfos.size()>currentVedioIndext){
			VedioInfo vedioInfo = vedioInfos.get(currentVedioIndext);
			Uri[] uris = vedioInfo.getUris();
			if(uris!=null && currentIndext-1>=0 &&uris.length>currentIndext-1){
//				player_loading.setVisibility(View.VISIBLE);
//				video_view.setVideoURI(uris[--currentIndext]);
				--currentIndext;
				updateName();
				openSystemPlayer();
				return true;
			}
		}else if(currentVedioIndext-1>=0 && vedioInfos.size()>currentVedioIndext-1){
			VedioInfo vedioInfo = vedioInfos.get(--currentVedioIndext);
			Uri[] uris = vedioInfo.getUris();
			currentIndext =0;
			if(uris!=null && uris.length>currentIndext){
//				player_loading.setVisibility(View.VISIBLE);
//				video_view.setVideoURI(uris[currentIndext]);
				updateName();
				openSystemPlayer();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 更新播放进度
	 */
	private void updateProgress(){
		//TODO 
		
		//获取当前播放进度
		int currentPosition = (int) video_view.getCurrentPosition();
		playbackProgressBar.setProgress(currentPosition);
		
		//获取缓冲比例
		int bufferPercentage = video_view.getBufferPercentage();
		int setSecondaryProgress = bufferPercentage * playbackProgressBar.getMax() / 100;
		playbackProgressBar.setSecondaryProgress(setSecondaryProgress);
		
		String currentPositionStr = StringUtil.stringForTime(currentPosition);
		current_time.setText(currentPositionStr);
		
		mHandler.removeMessages(PROGRESS_CHANGED);
		mHandler.sendEmptyMessageDelayed(PROGRESS_CHANGED, 1000);
	}
	
	/**
	 * 更新时间
	 */
	@SuppressLint("SimpleDateFormat")
	private void updateTime(){
		if(formater==null){
			formater = new SimpleDateFormat("HH:mm:ss");
		}
		String timeStr = StringUtil.toDefinedDate(formater,System.currentTimeMillis());
		last_modify.setText(timeStr);
		mHandler.removeMessages(UPDATE_TIME);
		mHandler.sendEmptyMessageDelayed(UPDATE_TIME, 1000);
	}
	
	private void updateBatteryLevel(){
		if (batteryLevel <= 0) {
			battery_state.setBackgroundResource(R.drawable.ic_battery_0);
		} else if (0 < batteryLevel && batteryLevel <= 10) {
			battery_state.setBackgroundResource(R.drawable.ic_battery_10);
		} else if (10 < batteryLevel && batteryLevel <= 20) {
			battery_state.setBackgroundResource(R.drawable.ic_battery_20);
		} else if (20 < batteryLevel && batteryLevel <= 40) {
			battery_state.setBackgroundResource(R.drawable.ic_battery_40);
		} else if (40 < batteryLevel && batteryLevel <= 60) {
			battery_state.setBackgroundResource(R.drawable.ic_battery_60);
		} else if (60 < batteryLevel && batteryLevel <= 80) {
			battery_state.setBackgroundResource(R.drawable.ic_battery_80);
		} else if (80 < batteryLevel && batteryLevel <= 100) {
			battery_state.setBackgroundResource(R.drawable.ic_battery_100);
		}

	}
	
	private void updateName(){
		String vedioName = vedioInfos.get(currentVedioIndext).getVedioName();
		if(vedioInfos.get(currentVedioIndext).getUris().length>1){
			video_name.setText(vedioName+" - " +(currentIndext+1));
		}else{
			video_name.setText(vedioName);
		}
	}
	
	/**
	 * 更新音量大小
	 * @param volume
	 */
	private void updateVolume(int volume){
		if (mAudioManager != null) {
			
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
			if(isSilent){
				vioceProgressBar.setEnabled(false);
			}else{
				vioceProgressBar.setEnabled(true);
				vioceProgressBar.setProgress(volume);
				currentVolume = volume;
			}
		}
	}
	
	/**
	 * 更新屏幕亮度
	 * @param brightness
	 */
	private void updateBright(float brightness){
		
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.screenBrightness = brightness/255f;
		getWindow().setAttributes(lp);
		currentBrightness = brightness;
	}
	
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.video_cl:
		case R.id.player_loading:
			if(isControlerShowing){
				hideControlerView();
			}else{
				showControlerView();
			}
			break;
		case R.id.btn_play_pause:
			if(video_view.isPlaying()){
				btn_play_pause.setBackgroundResource(R.drawable.btn_play);
				video_view.pause();
				mHandler.removeMessages(PROGRESS_CHANGED);
			}else if(!isStop){
				btn_play_pause.setBackgroundResource(R.drawable.btn_pause);
				video_view.start();
				updateProgress();
			}else if(player_loading.getVisibility() == View.VISIBLE){
				//TODO	加载中
				showShortToast("视频正在加载中,请稍后...");
			}else {
				btn_play_pause.setBackgroundResource(R.drawable.btn_pause);
				player_loading.setVisibility(View.VISIBLE);
				startPlay();
			}
			break;
		case R.id.btn_back:
			if(!PreviousVedio()){
				showShortToast("没有上一个了");
			}
			break;
		case R.id.btn_forward:
			if(!nextVedio()){
				showShortToast("没有下一个了");
			}
			break;
		case R.id.brightness_controler:
			// 弹出/关闭 亮度控制面板
			if(isBrightnessControlerShowing){
				hideBrightnessControler();
			}else{
				showBrightnessControler();
			}
			break;
		case R.id.btn_exit:
			// 关闭
			finish();
			break;
		case R.id.btn_voice:
			if(isSilent){
				isSilent= false;
				btn_voice.setBackgroundResource(R.drawable.btn_voice);
				updateVolume(currentVolume);
			}else{
				isSilent= true;
				btn_voice.setBackgroundResource(R.drawable.btn_voice_un);
				updateVolume(0);
			}
			break;
		default:
			break;
		}
		
		if(v.getId()!=R.id.video_cl && v.getId()!=R.id.player_loading){
			mHandler.removeMessages(HIDE_CONTROLER_VIEW);
			sendhideTimerMsg();
		}
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			//减小声音
			if(currentVolume >= 1){
				--currentVolume;
			}
			updateVolume(currentVolume);
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			//增大声音
			if(currentVolume < mAudioMax){
				++currentVolume;
			}
			updateVolume(currentVolume);
			return true;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		currentIntent = intent;
		Uri uri = intent.getData();
		
		if(uri!=null){
			String name = FileUtils.getFileNameNoFormat(uri.toString());
			if(vedioInfos == null){
				vedioInfos = new ArrayList<VedioInfo>();
			}else{
				vedioInfos.clear();
			}
			
			VedioInfo vedioInfo = new VedioInfo();
			vedioInfo.setVedioName(name);
			vedioInfo.setUris(new Uri[]{uri});
			vedioInfos.add(vedioInfo);
			
			currentVedioIndext = 0;//当前电影的下标
			currentIndext = 0;//当前片段的下标
			
		}else{
			
			int type = intent.getIntExtra(VEDIO_TYPE_KEY, 0);
			switch (type) {
			case VEDIO_SINGLE_SCOURCE:
				VedioInfo info = intent.getParcelableExtra(VEDIO_DATA_KEY);
				if(info!=null){
					vedioInfos = new ArrayList<VedioInfo>();
					vedioInfos.add(info);
				}
				break;
			case VEDIO_LIST_SCOURCE:
				vedioInfos = intent.getParcelableArrayListExtra(VEDIO_DATA_KEY);
				break;
				
			default:
				break;
			}
			
		}
		
		if(vedioInfos==null||vedioInfos.size()==0){
			showShortToast("数据有误");
			return;
		}
		
		currentVedioIndext = intent.getIntExtra(VEDIO_LIST_CURRENTPOSITION_KEY, 0);
		currentIndext = intent.getIntExtra(VEDIO_LIST_CURRENTINDEXT_KEY, 0);
		startPlay();
		
	}
	
	
	private void openSystemPlayer(){
		
		if(video_view!=null){
			video_view.stopPlayback();
			isStop = true;
			btn_play_pause.setBackgroundResource(R.drawable.btn_play);
		}
		Intent intent = new Intent(this, SystemPlayerActivity.class);
		intent.putExtra(SystemPlayerActivity.VEDIO_TYPE_KEY, currentIntent.getIntExtra(VEDIO_TYPE_KEY, 0));
		intent.putExtra(SystemPlayerActivity.VEDIO_DATA_KEY, currentIntent.getParcelableExtra(VEDIO_DATA_KEY));
		intent.putExtra(SystemPlayerActivity.VEDIO_LIST_CURRENTPOSITION_KEY, currentVedioIndext);
		intent.putExtra(SystemPlayerActivity.VEDIO_LIST_CURRENTINDEXT_KEY, currentIndext);
		
		intent.setData(getIntent().getData());
		startActivity(intent);
		overridePendingTransition(R.anim.fade_fast, R.anim.hold);
		finish();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(!isStop){
			if (mCurrentPosition > 0) {
				player_loading.setVisibility(View.VISIBLE);
				video_view.seekTo(mCurrentPosition);
//				video_view.start();
				playbackProgressBar.setProgress(mCurrentPosition);
				btn_play_pause.setBackgroundResource(R.drawable.btn_pause);
			}
		}
		
		mCurrentPosition = 0;
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		if(player_loading.getVisibility()==View.VISIBLE || isStop){
			isStop = true;
			video_view.stopPlayback();
			btn_play_pause.setBackgroundResource(R.drawable.btn_play);
			player_loading.setVisibility(View.GONE);
		}else {
			btn_play_pause.setBackgroundResource(R.drawable.btn_play);
			mCurrentPosition = (int) video_view.getCurrentPosition();
			video_view.pause();
			mHandler.removeMessages(PROGRESS_CHANGED);
		}
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeMessages(PROGRESS_CHANGED);
		mHandler.removeMessages(UPDATE_TIME);
		isStop = true;
		if(video_view!=null){
			video_view.stopPlayback();
			video_view = null;
		}
		if(batteryIntentFilter!=null){
			unregisterReceiver(batteryReceiver);
		}
		if(isSilent){
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
		}
	}
	
	
	private class MyOnPreparedListener implements OnPreparedListener{

		@Override
		public void onPrepared(MediaPlayer mp) {
			//获取总时长
			int duration = (int) video_view.getDuration();
			playbackProgressBar.setMax(duration);
			String maxStr = StringUtil.stringForTime(duration);
			total_time.setText(maxStr);
			
			if(video_view!=null){
				
				video_view.start();
			}
			isStop = false;
			updateProgress();
			mHandler.sendEmptyMessageDelayed(REMOVE_LOADING_VIEW, 300);
		}
		
	}
	
	private class MyOnErrorListener implements OnErrorListener{

		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			showShortToast("播放出错了");
			if(!SystemUtil.isNetOk(mContext)){
				showShortToast("请检查网络设置");
				video_view.stopPlayback();
				isStop = true;
				btn_play_pause.setBackgroundResource(R.drawable.btn_play);
				return true;
			};
				showShortToast("数据有误");
				video_view.stopPlayback();
				isStop = true;
				finish();
			
			return true;
		}
		
	}
	
	private class MyOnInfoListener implements OnInfoListener{

		@Override
		public boolean onInfo(MediaPlayer mp, int what, int extra) {
			
			switch (what) {
			case MediaPlayer.MEDIA_INFO_BUFFERING_START:
				// 监听播放卡、监听开始拖动
				if (video_buffer != null) {
					video_buffer.setVisibility(View.VISIBLE);
				}
				if(video_view!=null){
					video_view.pause();
				}
				break;

			case MediaPlayer.MEDIA_INFO_BUFFERING_END:
				// 监听卡完成、监听拖动缓冲完成
				if(video_view!=null){
					video_view.start();
				}
				if (video_buffer != null) {
					video_buffer.setVisibility(View.GONE);
				}
				break;

			default:
				break;
			}
			return true;
		}
		
	}
	
	private class MyOnCompletionListener implements OnCompletionListener{

		@Override
		public void onCompletion(MediaPlayer mp) {
			isStop = true;
			if(!nextVedio()){
				showShortToast("播放完毕");
				video_view.seekTo(0);
				updateProgress();
				video_view.stopPlayback();
				btn_play_pause.setBackgroundResource(R.drawable.btn_play);
			}
		}
		
	}
	
	private class MyOnSeekBarChangeListener4vedio implements OnSeekBarChangeListener{

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if(fromUser){
				mHandler.removeMessages(HIDE_CONTROLER_VIEW);
				video_view.seekTo(progress);
			}
			
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			mHandler.removeMessages(HIDE_CONTROLER_VIEW);
			sendhideTimerMsg();
			
		}

	}
	
	private class MyOnSeekBarChangeListener4Audio implements OnSeekBarChangeListener{
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			
			if(fromUser){
				//TODO  当用户手动拖动时
				mHandler.removeMessages(HIDE_CONTROLER_VIEW);
				updateVolume(progress);
			}
			
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			mHandler.removeMessages(HIDE_CONTROLER_VIEW);
			sendhideTimerMsg();
			
		}
		
	}
	
	private class MyOnSeekBarChangeListener4Bright implements com.personal.hwplayer.ui.view.VerticalSeekBar.OnSeekBarChangeListener{

		@Override
		public void onProgressChanged(VerticalSeekBar VerticalSeekBar,
				int progress, boolean fromUser) {
			
				mHandler.removeMessages(HIDE_CONTROLER_VIEW);
				updateBright(progress+mBrightnessMin);
		}

		@Override
		public void onStartTrackingTouch(VerticalSeekBar VerticalSeekBar) {}

		@Override
		public void onStopTrackingTouch(VerticalSeekBar VerticalSeekBar) {
			mHandler.removeMessages(HIDE_CONTROLER_VIEW);
			sendhideTimerMsg();
		}
		
	}
}



