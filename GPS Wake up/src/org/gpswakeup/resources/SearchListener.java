package org.gpswakeup.resources;

import java.io.IOException;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.google.android.maps.GeoPoint;

public class SearchListener implements OnClickListener {
	
	private Geocoder mGeoCoder;
	private Context mContext;
	private EditText mTxtSearch;

	public SearchListener(Context context, EditText txtSearch) {
		mGeoCoder = new Geocoder(context);
		mContext = context;
		mTxtSearch = txtSearch;
	}

	@Override
	public void onClick(View v) {
		try {
			List<Address> addresses = mGeoCoder.getFromLocationName(mTxtSearch.getText().toString(), 5);

			if (addresses.size() > 0) {
				GeoPoint p = new GeoPoint(
						(int) (addresses.get(0).getLatitude() * 1E6),
						(int) (addresses.get(0).getLongitude() * 1E6));

				OverlayManager.getInstance().addSearch(p, addresses.get(0).getAddressLine(0));
				//OverlayManager.getInstance().invalidate();
				
				mTxtSearch.setText("");
				
			} else {
				AlertDialog.Builder adb = new AlertDialog.Builder(mContext);
				adb.setTitle("Google Map");
				adb.setMessage("Please Provide the Proper Place");
				adb.setPositiveButton("Close", null);
				adb.show();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
