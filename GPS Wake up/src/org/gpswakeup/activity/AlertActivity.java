package org.gpswakeup.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

public class AlertActivity extends Activity {
	
	private NotificationManager mNotificationManager;
	private AudioManager mAudioManager;
	private int mMaxVolume;

	private final int NOTIFICATION_ID = 8776445;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mAudioManager = (AudioManager) getApplication().getApplicationContext().getSystemService(AUDIO_SERVICE);
		mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
		
		int distance = (int) getIntent().getExtras().getFloat("distance");
		String alarmName = getIntent().getExtras().getString("alarm_name");
		String ringToneName = getIntent().getExtras().getString("ringtone_name");
		int volume = getIntent().getExtras().getInt("volume");
		boolean isVibrator = getIntent().getExtras().getBoolean("vibrator");
		
		NotificationCompat.Builder notifBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle(alarmName)
		        .setContentText("Distance de la destination : " + distance + " mètres environ.");
		
		if(isVibrator){
			long[] pattern = {500};
			notifBuilder.setVibrate(pattern);
		}
		
		if(ringToneName != null && !ringToneName.isEmpty() && volume > 0){
			mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, Math.max(volume * mMaxVolume / 100, 1), 0);
			notifBuilder.setSound(Uri.parse(ringToneName), AudioManager.STREAM_ALARM);
		}

		mNotificationManager.notify(NOTIFICATION_ID, notifBuilder.build());
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
							.setTitle(alarmName)
							.setMessage("Distance de la destination : " + distance + " mètres environ.")
							.setIcon(R.drawable.ic_launcher);
		builder.setNegativeButton(R.string.dialog_btn_stop, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		AlertDialog alert = builder.create();
		alert.setCanceledOnTouchOutside(true);
		alert.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				mNotificationManager.cancel(NOTIFICATION_ID);
			}
		});

		alert.show();
	}
}
