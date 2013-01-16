package org.gpswakeup.resources;

import java.util.List;

import org.gpswakeup.activity.R;
import org.gpswakeup.db.AlarmBD;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * This class is the adapter for the alarms list. So we can have a custom view for the alarms.
 * Last update : 16.01.2013
 * @author Gregoire Aubert
 */
public class AlarmToggleAdapter extends ArrayAdapter<Alarm> {
	
	// MEMBERS
	private final List<Alarm> mList;
	private final Activity mContext;
	private AlarmBD mAlarmDB;

	/**
	 * Constructor of the adapter
	 * @param context is the context of the activity
	 * @param list is the alarms list from the main activity
	 */
	public AlarmToggleAdapter(Activity context, List<Alarm> list) {
		super(context, R.layout.alarm_list_item, list);
	    mContext = context;
	    mList = list;
	    mAlarmDB = new AlarmBD(mContext);		
	}
	
	/**
	 * The view holder contain all the element of the custom view
	 * @author Gregoire Aubert
	 */
	public static class ViewHolder {
		public TextView text1;
		public TextView text2;
		public ToggleButton toggle;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Match a view with an alarm
		View view = null;
		
		// If there is no view attributed we create a new one (first call the position)
		if (convertView == null) {
			
			// Inflate the custom view for the alarm
			LayoutInflater inflator = mContext.getLayoutInflater();
			view = inflator.inflate(R.layout.alarm_list_item, null);
			
			// Link the view holder with the item of the view
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.text1 = (TextView) view.findViewById(R.id.text1);
			viewHolder.text2 = (TextView) view.findViewById(R.id.text2);
			viewHolder.toggle = (ToggleButton) view.findViewById(R.id.toggle);
			
			// Define the action on the toggle button, we have to save the status of the alarm in the database
			viewHolder.toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
							// Retrieve the alarm reference from the tag field of the toggle element
							Alarm alarm = (Alarm) viewHolder.toggle.getTag();
							
							// Update the alarm in the application
							alarm.setEnabled(isChecked);
						    mAlarmDB.open();
							mAlarmDB.enableAlarmByID(alarm.getId(), isChecked);
							mAlarmDB.close();
							
							// Refresh the overlays on the map (change the color of the marker)
							OverlayManager.getInstance().refreshAlarm(alarm);
						}
					});
			
			// Store the viewHolder in the view and the alarm in the viewHolder
			view.setTag(viewHolder);
			viewHolder.toggle.setTag(mList.get(position));
		} else {
			// Update the alarm in the viewHolder according to the given position
			view = convertView;
			((ViewHolder) view.getTag()).toggle.setTag(mList.get(position));
		}
		
		// Update the value of the elements in the view according to the linked alarm
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.text1.setText(mList.get(position).getName());
		holder.text2.setText(mList.get(position).getRadius() / 1000 + " km");
		holder.toggle.setChecked(mList.get(position).isEnabled());
		return view;
	}
}
