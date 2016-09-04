package manager;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RemoteControlClient;
import android.os.Build;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import model.SongDetail;
import phonemedia.DMPlayerUtility;


public class MusicPlayService extends Service implements AudioManager.OnAudioFocusChangeListener{


	public static final String NOTIFY_PLAY = "musicplayer.play";
	public static final String NOTIFY_PAUSE = "musicplayer.pause";

	private AudioManager audioManager;
	private PhoneStateListener phoneStateListener;
	private RemoteControlClient remoteControlClient;
	private static boolean ignoreAudioFocus = false;

	private static boolean supportBigNotifications = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	private static boolean supportLockScreenControls = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {

		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		try {
			phoneStateListener = new PhoneStateListener() {
				@Override
				public void onCallStateChanged(int state, String incomingNumber) {
					if (state == TelephonyManager.CALL_STATE_RINGING) {
						if (MediaController.getInstance().isPlayingAudio(MediaController.getInstance().getPlayingSongDetail())
								&& !MediaController.getInstance().isAudioPaused()) {
							MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingSongDetail());
						}
					} else if (state == TelephonyManager.CALL_STATE_IDLE) {

					} else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {

					}
					super.onCallStateChanged(state, incomingNumber);
				}
			};
			TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			if (mgr != null) {
				mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
			}
		} catch (Exception e) {
			Log.e("tmessages", e.toString());
		}
		super.onCreate();

	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			SongDetail messageObject = MediaController.getInstance().getPlayingSongDetail();
			if (messageObject == null) {
				DMPlayerUtility.runOnUIThread(new Runnable() {
					@Override
					public void run() {
						stopSelf();
					}
				});
				return START_STICKY;
			}
			if (supportLockScreenControls) {
				ComponentName remoteComponentName = new ComponentName(getApplicationContext(), MusicPlayerReceiver.class.getName());
				try {
					if (remoteControlClient == null) {
						audioManager.registerMediaButtonEventReceiver(remoteComponentName);
						Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
						mediaButtonIntent.setComponent(remoteComponentName);
						PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
						remoteControlClient = new RemoteControlClient(mediaPendingIntent);
						audioManager.registerRemoteControlClient(remoteControlClient);
					}
					remoteControlClient.setTransportControlFlags(RemoteControlClient.FLAG_KEY_MEDIA_PLAY | RemoteControlClient.FLAG_KEY_MEDIA_PAUSE
							| RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE | RemoteControlClient.FLAG_KEY_MEDIA_STOP
							| RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS | RemoteControlClient.FLAG_KEY_MEDIA_NEXT);
				} catch (Exception e) {
					Log.e("tmessages", e.toString());
				}
			}
			//createNotification(messageObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (remoteControlClient != null) {
			RemoteControlClient.MetadataEditor metadataEditor = remoteControlClient.editMetadata(true);
			metadataEditor.clear();
			metadataEditor.apply();
			audioManager.unregisterRemoteControlClient(remoteControlClient);
			audioManager.abandonAudioFocus(this);
		}
		try {
			TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			if (mgr != null) {
				mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
			}
		} catch (Exception e) {
			Log.e("tmessages", e.toString());
		}
	}

	@Override
	public void onAudioFocusChange(int focusChange) {
		if (ignoreAudioFocus) {
			ignoreAudioFocus = false;
			return;
		}
		if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
			if (MediaController.getInstance().isPlayingAudio(MediaController.getInstance().getPlayingSongDetail())
					&& !MediaController.getInstance().isAudioPaused()) {
				MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingSongDetail());
			}
		} else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
			// MediaController.getInstance().playAudio(MediaController.getInstance().getPlayingMessageObject());
		}
	}

	public static void setIgnoreAudioFocus() {
		ignoreAudioFocus = true;
	}
}
