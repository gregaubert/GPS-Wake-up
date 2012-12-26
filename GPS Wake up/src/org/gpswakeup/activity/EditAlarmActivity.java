package org.gpswakeup.activity;

import org.gpswakeup.db.AlarmBD;
import org.gpswakeup.resources.Alarm;
import org.gpswakeup.resources.OverlayManager;
import org.gpswakeup.resources.Utility;

import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.maps.GeoPoint;

public class EditAlarmActivity extends SherlockActivity {

	private Alarm mAlarm;
	private EditText mTxtName;
	private TextView mTxtDistance;
	private SeekBar mSbDistance;
	private SeekBar mSbVolume;
	private CheckBox mChkVibrator;
	private CheckBox mChkRingTone;
	private AlarmBD mAlarmDB;
	private TextView mTxtRingTone;
	private String mRingTonePath = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_edit_alarm);
		initComponent();
		loadData();
	}
	
	private void initComponent(){
		mTxtName = (EditText) findViewById(R.id.txtName);
		mTxtDistance = (TextView) findViewById(R.id.txtDistance);
		mSbDistance = (SeekBar) findViewById(R.id.sbDistance);
		mSbVolume = (SeekBar) findViewById(R.id.sbVolume);
		mChkVibrator = (CheckBox) findViewById(R.id.chkVibrator);
		mChkRingTone = (CheckBox) findViewById(R.id.chkRingTone);
		mTxtRingTone = (TextView) findViewById(R.id.txtChooseRingTone);
		
		mTxtRingTone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
				intent.putExtra( RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
				intent.putExtra( RingtoneManager.EXTRA_RINGTONE_TITLE, "Choix de la sonnerie");
				if( mRingTonePath != null)
					intent.putExtra( RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(mRingTonePath));
				else
					intent.putExtra( RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri)null);

				//RingtoneManager.setActualDefaultRingtoneUri(myActivity, RingtoneManager.TYPE_RINGTONE, uri);
				startActivityForResult(intent, 0);
			}
			
		});
		
		mChkRingTone.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mSbVolume.setEnabled(isChecked);
				findViewById(R.id.txtChooseRingTone).setEnabled(isChecked);
			}
		});
		
		mSbDistance.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mTxtDistance.setText(progress + " km");
			}
		});
		
		findViewById(R.id.mainLayout).requestFocus();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
			if (uri != null)
				mRingTonePath = uri.toString();
			else
				mRingTonePath = null;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void loadData() {

		if (getIntent().getAction().equals(Utility.ACTION_NEW)) {
			mAlarm = new Alarm(-1, new GeoPoint(getIntent().getIntExtra("lat",
					0), getIntent().getIntExtra("long", 0)), 5000, getIntent()
					.getStringExtra("snippet"));
		}
		else{
			mAlarm = MainActivity.getAlarm(getIntent().getIntExtra("index", 0));
		}

		mTxtName.setText(mAlarm.getName());
		mSbDistance.setProgress(mAlarm.getRadius() / 1000);
		mSbVolume.setProgress(mAlarm.getVolume());
		mChkVibrator.setChecked(mAlarm.isVibrator());
		mChkRingTone.setChecked(!mAlarm.getAlarmName().isEmpty());
		mRingTonePath = mAlarm.getAlarmName().isEmpty()?null:mAlarm.getAlarmName();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_edit_alarm, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.menu_save:
			saveModification();
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void insertNew() {
		int id = (int) mAlarmDB.insertAlarm(mAlarm);
		mAlarm.setId(id);
		OverlayManager.getInstance().clearSearch();
		MainActivity.addAlarm(mAlarm);
	}
	
	private void updateOld(){
		mAlarmDB.updateAlarm(mAlarm);
		Utility.makeCenterToast(this, R.string.toast_edit, Toast.LENGTH_SHORT);
	}

	private void saveModification() {
		
		mAlarm.setName(mTxtName.getText().toString());
		mAlarm.setRadius(mSbDistance.getProgress() * 1000);
		mAlarm.setVibrator(mChkVibrator.isChecked());
		if(mChkRingTone.isChecked() && mRingTonePath != null){
			mAlarm.setVolume(mSbVolume.getProgress());
			mAlarm.setAlarmName(mRingTonePath);
		}
		else{
			mAlarm.setAlarmName("");
			mAlarm.setVolume(0);
		}
		
		mAlarmDB = new AlarmBD(this);
		mAlarmDB.open();
		if (getIntent().getAction().equals(Utility.ACTION_NEW))
			insertNew();
		else
			updateOld();
		mAlarmDB.close();
	}
}
