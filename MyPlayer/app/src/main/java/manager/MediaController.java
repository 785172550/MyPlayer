package manager;


import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.util.Log;

import com.sean.myplayer.ApplicationDMPlayer;

import java.io.File;
import java.util.ArrayList;

import model.SongDetail;

public class MediaController {

	private MediaController() {
	}

	private boolean isPaused = true;
	private MediaPlayer audioPlayer = null;
	private AudioTrack audioTrackPlayer = null;
	private int lastProgress = 0;
	private boolean useFrontSpeaker;
	private boolean playMusicAgain = false;

	public int type = 0;
	public int id = -1;
	public int currentPlaylistNum;
	public String path = "";

	private final Object playerSongDetailSync = new Object();
	// 自身实例
	private static volatile MediaController Instance = new MediaController();

	public static MediaController getInstance() {
//		MediaController localInstance = Instance;
//		if (localInstance == null) {
//			synchronized (MediaController.class) {
//				localInstance = Instance;
//				if (localInstance == null) {
//					Instance = localInstance = new MediaController();
//				}
//			}
//		}
		if(Instance == null){
			Instance = new MediaController();
		}
		return Instance;
	}

	//播放
	public boolean playAudio(SongDetail mSongDetail){
		if (mSongDetail == null) {
			return false;
		}
		if ((audioTrackPlayer != null || audioPlayer != null) && MusicPreferance.playingSongDetail != null && mSongDetail.getId() == MusicPreferance.playingSongDetail.getId()) {
			if (isPaused) {
				resumeAudio(mSongDetail);
			}
			return true;
		}
		if (audioTrackPlayer != null) {
			MusicPlayService.setIgnoreAudioFocus();
		}
		cleanupPlayer(!playMusicAgain, false);
		playMusicAgain = false;
		File file = null;

		try {
			audioPlayer = new MediaPlayer();
			audioPlayer.setAudioStreamType(useFrontSpeaker ? AudioManager.STREAM_VOICE_CALL : AudioManager.STREAM_MUSIC);
			audioPlayer.setDataSource(mSongDetail.getPath());
			audioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mediaPlayer) {
					MusicPreferance.playingSongDetail.audioProgress = 0.0f;
					MusicPreferance.playingSongDetail.audioProgressSec = 0;
					if (!MusicPreferance.playlist.isEmpty() && MusicPreferance.playlist.size() > 1) {
						//playNextSong(true);
					} else {
						cleanupPlayer(true, true);
					}
				}
			});
			audioPlayer.prepare();
			audioPlayer.start();
			//startProgressTimer();
		} catch (Exception e) {
			if (audioPlayer != null) {
				audioPlayer.release();
				audioPlayer = null;
				isPaused = false;
				MusicPreferance.playingSongDetail = null;
			}
			return false;
		}
		isPaused = false;
		lastProgress = 0;
		MusicPreferance.playingSongDetail = mSongDetail;
		//NotificationManager.getInstance().postNotificationName(NotificationManager.audioDidStarted, mSongDetail);

		if (audioPlayer != null) {
			try {
				if (MusicPreferance.playingSongDetail.audioProgress != 0) {
					int seekTo = (int) (audioPlayer.getDuration() * MusicPreferance.playingSongDetail.audioProgress);
					audioPlayer.seekTo(seekTo);
				}
			} catch (Exception e2) {
				MusicPreferance.playingSongDetail.audioProgress = 0;
				MusicPreferance.playingSongDetail.audioProgressSec = 0;
			}
		} else if (audioTrackPlayer != null) {
			if (MusicPreferance.playingSongDetail.audioProgress == 1) {
				MusicPreferance.playingSongDetail.audioProgress = 0;
			}

		}

		if (MusicPreferance.playingSongDetail != null) {
			Intent intent = new Intent(ApplicationDMPlayer.applicationContext, MusicPlayService.class);
			ApplicationDMPlayer.applicationContext.startService(intent);
		} else {
			Intent intent = new Intent(ApplicationDMPlayer.applicationContext, MusicPlayService.class);
			ApplicationDMPlayer.applicationContext.stopService(intent);
		}

		//storeResendPlay(ApplicationDMPlayer.applicationContext, mSongDetail);
		//NotificationManager.getInstance().notifyNewSongLoaded(NotificationManager.newaudioloaded, mSongDetail);

		return true;
	}

	public boolean resumeAudio(SongDetail messageObject) {
		if (audioTrackPlayer == null && audioPlayer == null || messageObject == null || MusicPreferance.playingSongDetail == null || MusicPreferance.playingSongDetail != null
				&& MusicPreferance.playingSongDetail.getId() != messageObject.getId()) {
			return false;
		}
		try {
			//startProgressTimer();
			if (audioPlayer != null) {
				audioPlayer.start();
			} else if (audioTrackPlayer != null) {
				audioTrackPlayer.play();
			}
			isPaused = false;
			//NotificationManager.getInstance().postNotificationName(NotificationManager.audioPlayStateChanged, MusicPreferance.playingSongDetail.getId());
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public void cleanupPlayer(Context context, boolean notify, boolean stopService) {
		MusicPreferance.saveLastSong(context, getPlayingSongDetail());
		MusicPreferance.saveLastSongListType(context, type);
		MusicPreferance.saveLastAlbID(context, id);
		MusicPreferance.saveLastPosition(context, currentPlaylistNum);
		MusicPreferance.saveLastPath(context, path);
		cleanupPlayer(notify, stopService);
	}

	public void cleanupPlayer(boolean notify, boolean stopService) {
		pauseAudio(getPlayingSongDetail());
		//stopProximitySensor();
		if (audioPlayer != null) {
			try {
				audioPlayer.reset();
			} catch (Exception e) {
			}
			try {
				audioPlayer.stop();
			} catch (Exception e) {
			}
			try {
				audioPlayer.release();
				audioPlayer = null;
			} catch (Exception e) {
			}
		} else if (audioTrackPlayer != null) {
			synchronized (playerSongDetailSync) {
				try {
					audioTrackPlayer.pause();
					audioTrackPlayer.flush();
				} catch (Exception e) {
				}
				try {
					audioTrackPlayer.release();
					audioTrackPlayer = null;
				} catch (Exception e) {
				}
			}
		}
		//stopProgressTimer();
		isPaused = true;
		if (stopService) {
			Intent intent = new Intent(ApplicationDMPlayer.applicationContext, MusicPlayService.class);
			ApplicationDMPlayer.applicationContext.stopService(intent);
		}
	}
	public void stopAudio() {
	}

	public boolean pauseAudio(SongDetail messageObject) {
		//stopProximitySensor();
		if (audioTrackPlayer == null && audioPlayer == null || messageObject == null || MusicPreferance.playingSongDetail == null || MusicPreferance.playingSongDetail != null
				&& MusicPreferance.playingSongDetail.getId() != messageObject.getId()) {
			return false;
		}
		//stopProgressTimer();
		try {
			if (audioPlayer != null) {
				audioPlayer.pause();
			} else if (audioTrackPlayer != null) {
				audioTrackPlayer.pause();
			}
			isPaused = true;
			//NotificationManager.getInstance().postNotificationName(NotificationManager.audioPlayStateChanged, MusicPreferance.playingSongDetail.getId());
		} catch (Exception e) {
			Log.e("tmessages", e.toString());
			isPaused = true;
			return false;
		}
		return true;
	}

	public boolean isAudioPaused() {
		return isPaused;
	}

	public SongDetail getPlayingSongDetail() {
		return MusicPreferance.playingSongDetail;
	}

	public boolean isPlayingAudio(SongDetail messageObject) {
		return !(audioTrackPlayer == null && audioPlayer == null || messageObject == null || MusicPreferance.playingSongDetail == null || MusicPreferance.playingSongDetail != null);
	}

	public boolean setPlaylist(ArrayList<SongDetail> allSongsList, SongDetail current, int type_, int id_) {
		type = type_;
		id = id_;

		if (MusicPreferance.playingSongDetail == current) {
			return playAudio(current);
		}
		//playMusicAgain = !MusicPreferance.playlist.isEmpty();
		MusicPreferance.playlist.clear();
		if (allSongsList != null && allSongsList.size() >= 1) {
			MusicPreferance.playlist.addAll(allSongsList);
		}

		currentPlaylistNum = MusicPreferance.playlist.indexOf(current);
		if (currentPlaylistNum == -1) {
			MusicPreferance.playlist.clear();
			MusicPreferance.shuffledPlaylist.clear();
			return false;
		}
//		if (shuffleMusic) {
//			currentPlaylistNum = 0;
//		}
		return playAudio(current);
	}
}
