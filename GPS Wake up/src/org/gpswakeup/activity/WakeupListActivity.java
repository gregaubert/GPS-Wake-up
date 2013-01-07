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

public class WakeupListActivity extends SherlockListActivity {
		
	private ActionMode mMode;
	private static ArrayAdapter<Alarm> mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Show the Up button in the action bar.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mAdapter = new AlarmToggleAdapter(this, MainActivity.getAlarms());
		setListAdapter(mAdapter);
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if (mMode != null) {
		            return false;
		        }
				getListView().setSelection(position);
				mMode = startActionMode(new ActionModeAlarm((Alarm)((ViewHolder)view.getTag()).toggle.getTag(), view));
				return true;
			}
		});
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

	private void editAlarm(Alarm alarm) {
		Intent intent = new Intent(this, EditAlarmActivity.class);
		intent.setAction(Utility.ACTION_EDIT);
		intent.putExtra("index", MainActivity.getAlarmIndex(alarm));
		startActivity(intent);
	}

	private void deleteAlarm(Alarm alarm) {
		if(MainActivity.deleteAlarm(alarm)){
			((AlarmToggleAdapter)getListAdapter()).remove(alarm);
			Utility.makeCenterToast(this, R.string.toast_delete, Toast.LENGTH_SHORT);
		}
		else
			Utility.makeCenterToast(this, R.string.toast_delete_error, Toast.LENGTH_SHORT);
	}
	
	private final class ActionModeAlarm implements ActionMode.Callback {
		
		private Alarm mAlarm;
		private View mView;
		
		public ActionModeAlarm(Alarm alarm, View view) {
			super();
			mView = view;
			mAlarm = alarm;
		}
        
		@Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
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
	
	@Override
	protected void onStart() {
		super.onStart();
		mAdapter.notifyDataSetChanged();
	}
}
