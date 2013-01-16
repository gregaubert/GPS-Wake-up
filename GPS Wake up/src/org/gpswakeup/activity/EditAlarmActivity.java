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
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.maps.GeoPoint;

/**
 * Activity for editing an alarm options.
 * Last update : 16.01.2013
 * @author Gregoire Aubert
 */
public class EditAlarmActivity extends SherlockActivity {

	// MEMBERS
	private Alarm mAlarm;
	private EditText mTxtName;
	private TextView mTxtDistance;
	private TextView mTxtChooseRingTone;
	private TextView mTxtVolume;
	private SeekBar mSbDistance;
	private SeekBar mSbVolume;
	private CheckBox mChkVibrator;
	private CheckBox mChkRingTone;
	private AlarmBD mAlarmDB;
	private TextView mTxtRingTone;
	private ImageView mImgVolume;
	private String mRingTonePath = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_edit_alarm);
		initComponent();
		loadData();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Allow to retrieve the selected item from the ringtone picker dialog
		if (resultCode == RESULT_OK) {
			Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
			if (uri != null)
				mRingTonePath = uri.toString();
			else
				mRingTonePath = null;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_edit_alarm, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home){
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		else if(item.getItemId() == R.id.menu_save){
			saveModification();
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Initialize the component of the activity
	 */
	private void initComponent(){
		mTxtName = (EditText) findViewById(R.id.txtName);
		mTxtDistance = (TextView) findViewById(R.id.txtDistance);
		mTxtChooseRingTone = (TextView) findViewById(R.id.txtChooseRingTone);
		mTxtVolume = (TextView) findViewById(R.id.txtVolume);
		mSbDistance = (SeekBar) findViewById(R.id.sbDistance);
		mSbVolume = (SeekBar) findViewById(R.id.sbVolume);
		mChkVibrator = (CheckBox) findViewById(R.id.chkVibrator);
		mChkRingTone = (CheckBox) findViewById(R.id.chkRingTone);
		mTxtRingTone = (TextView) findViewById(R.id.txtChooseRingTone);
		mImgVolume = (ImageView) findViewById(R.id.imgVolume);
		
		mTxtRingTone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// Show the ringtone picker from android, and get the result in the onActivityResult
				Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
				intent.putExtra( RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
				intent.putExtra( RingtoneManager.EXTRA_RINGTONE_TITLE, "Choix de la sonnerie");
				if( mRingTonePath != null)
					intent.putExtra( RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(mRingTonePath));
				else
					intent.putExtra( RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri)null);

				startActivityForResult(intent, 0);
			}
			
		});
		
		mChkRingTone.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// Enable/Disable the ringtone component
				mSbVolume.setEnabled(isChecked);
				mTxtChooseRingTone.setEnabled(isChecked);
				mTxtVolume.setEnabled(isChecked);
				mTxtChooseRingTone.getCompoundDrawables()[2].setAlpha(isChecked?255:140);
				mImgVolume.getDrawable().setAlpha(isChecked?255:140);
			}
		});
		
		mSbDistance.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// Update the corresponding textview when the progressbar is changed
				mTxtDistance.setText(progress + " km");
			}
		});
		
		// So the EditView don't have the focus
		findViewById(R.id.mainLayout).requestFocus();
	}

	/**
	 * Load the data from alarm in each component of the view
	 */
	private void loadData() {

		// Create a new alarm if the intent action is ACTION_NEW, 
		// or retreive the alarm from the main activity list if this
		// is a modification.
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
	
	/**
	 * Save the modification of the alarm (new or existing)
	 */
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

	/**
	 * Insert the alarm in the database
	 */
	private void insertNew() {
		int id = (int) mAlarmDB.insertAlarm(mAlarm);
		mAlarm.setId(id);
		OverlayManager.getInstance().clearSearch();
		MainActivity.addAlarm(mAlarm);
	}
	
	/**
	 * Save the modification of the alarm in the database
	 */
	private void updateOld(){
		mAlarmDB.updateAlarm(mAlarm);
		Utility.makeCenterToast(this, R.string.toast_edit, Toast.LENGTH_SHORT);
	}
}
