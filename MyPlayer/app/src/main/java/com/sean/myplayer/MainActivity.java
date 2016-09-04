package com.sean.myplayer;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import fragment.SlidingFragment;
import manager.MediaController;
import manager.MusicPreferance;
import model.SongDetail;
import slidingup.SlidingUpPanelLayout;
import ui.PlayPauseView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


	private static SlidingMenu slidingMenu;
	private SlidingUpPanelLayout mLayout;

	private boolean isExpand = false;

	private RelativeLayout slidepanelchildtwo_topviewtwo;
	private RelativeLayout slidepanelchildtwo_topviewone;
	private TextView txt_playesongname;
	private TextView txt_playesongname_slidetoptwo;

	//下方播放按钮
	//private ImageView bottombar_play;
	PlayPauseView btn_play;
	PlayPauseView bottombar_play;

	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.include_slidingup_panellayout);
		initview();
		getIntentData();
	}

	private void initview() {
		initsliding();
		initslidingup();
	}

	private void initslidingup() {
		mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
		// 播放按钮
		bottombar_play = (PlayPauseView) findViewById(R.id.bottombar_play);
		bottombar_play.setOnClickListener(this);

		btn_play = (PlayPauseView) findViewById(R.id.btn_play);
		btn_play.setOnClickListener(this);

		mLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
			// 滑动
			@Override
			public void onPanelSlide(View panel, float slideOffset) {
				//Log.d(TAG, "onPanelSlide: ");
				if (slideOffset == 0.0f) {
					isExpand = false;
				}
			}

			//隐藏
			@Override
			public void onPanelCollapsed(View panel) {
				//Log.d(TAG, "onPanelCollapsed: ");
				slidepanelchildtwo_topviewone.setVisibility(View.VISIBLE);
				slidepanelchildtwo_topviewtwo.setVisibility(View.INVISIBLE);
			}

			//划出
			@Override
			public void onPanelExpanded(View panel) {
				isExpand = true;
				//Log.d(TAG, "onPanelExpanded: ");
				slidepanelchildtwo_topviewone.setVisibility(View.INVISIBLE);
				slidepanelchildtwo_topviewtwo.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPanelAnchored(View panel) {
				//Log.d(TAG, "onPanelAnchored: ");
			}

			@Override
			public void onPanelHidden(View panel) {
				//Log.d(TAG, "onPanelHidden: ");
			}
		});

		slidepanelchildtwo_topviewone =
				(RelativeLayout) findViewById(R.id.slidepanelchildtwo_topviewone);
		slidepanelchildtwo_topviewtwo =
				(RelativeLayout) findViewById(R.id.slidepanelchildtwo_topviewtwo);

		txt_playesongname = (TextView) findViewById(R.id.song_name1);
		txt_playesongname_slidetoptwo = (TextView) findViewById(R.id.song_name2);

	}

	private void initsliding() {
		slidingMenu = new SlidingMenu(MainActivity.this);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		//值越大，侧滑越窄
		slidingMenu.setBehindOffset(200);
		slidingMenu.setBehindScrollScale(1);
		slidingMenu.setFadeDegree(1);
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		slidingMenu.setMenu(R.layout.slide_layout);
		//slidingMenu.setBehindCanvasTransformer();

		android.support.v4.app.Fragment slideFragment = new
				SlidingFragment(MainActivity.this, getSupportFragmentManager());
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frag_slide, slideFragment).commit();
	}

	public static void showslide() {
		slidingMenu.showMenu();
	}

	public static void hideslide() {
		slidingMenu.showContent();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.bottombar_play:
				if (manager.MediaController.getInstance() != null) {
					PlayPauseEvent(v);
				}
				break;
			case R.id.btn_play:
				if (manager.MediaController.getInstance() != null) {
					PlayPauseEvent(v);
				}
				break;
		}
	}

	// 播放暂停事件
	private void PlayPauseEvent(View v) {
		if (MediaController.getInstance().isAudioPaused()) {
			MediaController.getInstance().playAudio(MediaController.getInstance().getPlayingSongDetail());
			((PlayPauseView) v).Play();
		} else {
			MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingSongDetail());
			((PlayPauseView) v).Pause();
		}
	}

	private void getIntentData() {
		try {
			Uri data = getIntent().getData();
			if (data != null) {
				if (data.getScheme().equalsIgnoreCase("file")) {
					String path = data.getPath().toString();
					if (!TextUtils.isEmpty(path)) {
						MediaController.getInstance().cleanupPlayer(MainActivity.this, true, true);
						MusicPreferance.getPlaylist(MainActivity.this, path);
						//updateTitle(false);
						MediaController.getInstance().playAudio(MusicPreferance.playingSongDetail);
						mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
					}
				}
				if (data.getScheme().equalsIgnoreCase("http"))
					Log.d(TAG, data.getPath().toString());
				if (data.getScheme().equalsIgnoreCase("content"))
					Log.d(TAG, data.getPath().toString());

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadSongsDetails(SongDetail mDetail) {
		String contentURI = "content://media/external/audio/media/" + mDetail.getId() + "/albumart";
//		imageLoader.displayImage(contentURI, songAlbumbg, options, animateFirstListener);
//		imageLoader.displayImage(contentURI, img_bottom_slideone, options, animateFirstListener);
//		imageLoader.displayImage(contentURI, img_bottom_slidetwo, options, animateFirstListener);

		txt_playesongname.setText(mDetail.getTitle());
		//txt_songartistname.setText(mDetail.getArtist());
		txt_playesongname_slidetoptwo.setText(mDetail.getTitle());
		//txt_songartistname_slidetoptwo.setText(mDetail.getArtist());

//		if (txt_timetotal != null) {
//			long duration = Long.valueOf(mDetail.getDuration());
//			txt_timetotal.setText(duration != 0 ? String.format("%d:%02d", duration / 60, duration % 60) : "-:--");
//		}
		//updateProgress(mDetail);
	}
}
