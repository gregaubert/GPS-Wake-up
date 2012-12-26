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

public class AlarmToggleAdapter extends ArrayAdapter<Alarm> {
	
	private final List<Alarm> mList;
	private final Activity mContext;
	private AlarmBD mAlarmDB;

	public AlarmToggleAdapter(Activity context, List<Alarm> list) {
		super(context, R.layout.alarm_list_item, list);
	    mContext = context;
	    mList = list;
	    mAlarmDB = new AlarmBD(mContext);
	    mAlarmDB.open();
		
	}

	static class ViewHolder {
		protected TextView text1;
		protected TextView text2;
		protected ToggleButton toggle;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			LayoutInflater inflator = mContext.getLayoutInflater();
			view = inflator.inflate(R.layout.alarm_list_item, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.text1 = (TextView) view.findViewById(R.id.text1);
			viewHolder.text2 = (TextView) view.findViewById(R.id.text2);
			viewHolder.toggle = (ToggleButton) view.findViewById(R.id.toggle);
			viewHolder.toggle
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
							Alarm alarm = (Alarm) viewHolder.toggle.getTag();
							alarm.setEnabled(isChecked);
							mAlarmDB.enableAlarmByID(alarm.getId(), isChecked);
							OverlayManager.getInstance().refreshAlarm(alarm);
						}
					});
			view.setTag(viewHolder);
			viewHolder.toggle.setTag(mList.get(position));
		} else {
			view = convertView;
			((ViewHolder) view.getTag()).toggle.setTag(mList.get(position));
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.text1.setText(mList.get(position).getName());
		holder.text2.setText(mList.get(position).getRadius() + " km");
		holder.toggle.setChecked(mList.get(position).isEnabled());
		return view;
	}
	
	@Override
	protected void finalize() throws Throwable {
		mAlarmDB.close();
		super.finalize();
	}
}