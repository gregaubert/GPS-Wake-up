package org.gpswakeup.activity;

import org.gpswakeup.resources.Alarm;
import org.gpswakeup.resources.AlarmToggleAdapter;
import org.gpswakeup.resources.AlarmToggleAdapter.ViewHolder;
import org.gpswakeup.resources.Utility;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * Activity displaying the list of the saved alarms.
 * Last update : 16.01.2013
 * @author Gregoire Aubert
 */
public class WakeupListActivity extends SherlockListActivity {
	
	// MEMBERS
	private ActionMode mMode;
	
	// STATIC MEMBERS
	private static ArrayAdapter<Alarm> mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Show the Up button in the action bar.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Create the adapter for the alarms to be displayed with a custom view
		mAdapter = new AlarmToggleAdapter(this, MainActivity.getAlarms());
		setListAdapter(mAdapter);
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		// When the user long click on a list item we display an action mode in the action bar
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				// Don't launch two action mode at the same time
				if (mMode != null) {
		            return false;
		        }
				getListView().setSelection(position);
				// Here we start the action mode, the alarm is stocked in the tag of the toggle object of the view of the adapter
				mMode = startActionMode(new ActionModeAlarm((Alarm)((ViewHolder)view.getTag()).toggle.getTag(), view));
				return true;
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		// So the data are updated if the activity was just bring to front
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Launch the intent to edit the alarm
	 * @param alarm is the alarm to be edited
	 */
	private void editAlarm(Alarm alarm) {
		Intent intent = new Intent(this, EditAlarmActivity.class);
		intent.setAction(Utility.ACTION_EDIT);
		intent.putExtra("index", MainActivity.getAlarmIndex(alarm));
		startActivity(intent);
	}

	/**
	 * Delete an alarm from the whole application and database
	 * @param alarm is the alarm to be deleted
	 */
	private void deleteAlarm(Alarm alarm) {
		if(MainActivity.deleteAlarm(alarm)){
			((AlarmToggleAdapter)getListAdapter()).remove(alarm);
			Utility.makeCenterToast(this, R.string.toast_delete, Toast.LENGTH_SHORT);
		}
		else
			Utility.makeCenterToast(this, R.string.toast_delete_error, Toast.LENGTH_SHORT);
	}
	
	/**
	 * Private class defining the callback for the action mode.
	 * The action mode is a new way of showing context menu introduced by the action bar.
	 * @author Gregoire Aubert
	 */
	private final class ActionModeAlarm implements ActionMode.Callback {
		
		// MEMBERS
		private Alarm mAlarm;
		private View mView;
		
		/**
		 * Constructor of the action mode callback
		 * @param alarm is the alarm selected by the user on which the actions will be performed
		 * @param view is the view associated to the alarm
		 */
		public ActionModeAlarm(Alarm alarm, View view) {
			super();
			mView = view;
			mAlarm = alarm;
		}
        
		@Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Load the menu item from the xml
			mode.getMenuInflater().inflate(R.menu.activity_wakeup_list, menu);			
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        	switch (item.getItemId()) {
            case R.id.menu_edit:
                editAlarm(mAlarm);
                mode.finish();
                return true;
            case R.id.menu_delete:
            	deleteAlarm(mAlarm);
                mode.finish();
            	return true;
            default:
                return false;
        	}
        }

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mView.setSelected(false);
			mMode = null;
		}
    }
}
