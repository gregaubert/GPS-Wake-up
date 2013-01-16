package org.gpswakeup.resources;

import java.io.IOException;
import java.util.List;

import org.gpswakeup.activity.MainActivity;
import org.gpswakeup.activity.R;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.google.android.maps.GeoPoint;

/**
 * This is the MapSearchView implementing the querylistener and taking care of the search.
 * Last update : 16.01.2013
 * @author Gregoire Aubert
 */
public class MapSearchView extends SearchView implements OnQueryTextListener {
	
	// MEMBERS
	private Geocoder mGeoCoder;
	private Context mContext;
	
	/**
	 * Constructor of the searchview
	 * @param context is the activity context
	 * @param themedContext is the action bar themed context
	 */
	public MapSearchView(Context context, Context themedContext) {
		super(themedContext);
		mGeoCoder = new Geocoder(context);
		mContext = context;
		setOnQueryTextListener(this);
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		
		// Check if there is an network connection
		if(!((MainActivity)mContext).isOnline()){
			Utility.makeCenterToast(mContext, R.string.toast_no_connection, Toast.LENGTH_SHORT).show();
			return true;
		}
			
		try {
			
			// If there is launch the search
			List<Address> addresses = mGeoCoder.getFromLocationName(query, 5);
			
			if (addresses.size() > 0) {
				boolean first = true;
				OverlayManager.getInstance().clearSearch();
				
				// Try to mark on the map all the results
				for(Address address : addresses){
					GeoPoint p = new GeoPoint(
							(int) (address.getLatitude() * 1E6),
							(int) (address.getLongitude() * 1E6));
	
					OverlayManager.getInstance().addSearch(p, address.getAddressLine(0));
					
					// Move only to the first address found
					if(first){
						first = false;
						OverlayManager.getInstance().moveMapTo(p);
					}
				}
				OverlayManager.getInstance().invalidate();
				
				Utility.makeCenterToast(mContext, R.string.toast_search_finish, Toast.LENGTH_SHORT).show();
				
				// Collapse the search view at the end
				MainActivity.collapseSearchView();
				setQuery("", false);
				
			} else {
				Utility.makeCenterToast(mContext, R.string.toast_no_result, Toast.LENGTH_SHORT).show();
			}
		} catch (IOException e) {
			Utility.makeCenterToast(mContext, R.string.toast_search_error, Toast.LENGTH_SHORT).show();
			Log.i("SEARCH", "Erreur dans la recherche, stacktrace :");
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}
}
