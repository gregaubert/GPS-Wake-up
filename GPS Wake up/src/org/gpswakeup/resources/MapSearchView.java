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

public class MapSearchView extends SearchView implements OnQueryTextListener {
	
	private Geocoder mGeoCoder;
	private Context mContext;
	
	public MapSearchView(Context context, Context themedContext) {
		super(themedContext);
		mGeoCoder = new Geocoder(context);
		mContext = context;
		//setupSearchView();
		setOnQueryTextListener(this);
	}
	
//	private void setupSearchView() {
//
//		SearchManager searchManager = (SearchManager) mContext.getSystemService(Context.SEARCH_SERVICE);
//		if (searchManager != null) {
//			List<SearchableInfo> searchables = searchManager.getSearchablesInGlobalSearch();
//
//			SearchableInfo info = searchManager.getSearchableInfo(((Activity)mContext).getComponentName());
//			for (SearchableInfo inf : searchables) {
//				if (inf.getSuggestAuthority() != null
//						&& inf.getSuggestAuthority().startsWith("applications")) {
//					info = inf;
//				}
//			}
//			setSearchableInfo(info);
//		}
//		setOnQueryTextListener(this);
//	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		
		if(!((MainActivity)mContext).isOnline()){
			Utility.makeCenterToast(mContext, R.string.toast_no_connection, Toast.LENGTH_SHORT).show();
			return true;
		}
			
		try {
			List<Address> addresses = mGeoCoder.getFromLocationName(query, 5);

			if (addresses.size() > 0) {
				GeoPoint p = new GeoPoint(
						(int) (addresses.get(0).getLatitude() * 1E6),
						(int) (addresses.get(0).getLongitude() * 1E6));

				OverlayManager.getInstance().addSearch(p, addresses.get(0).getAddressLine(0));
				OverlayManager.getInstance().invalidate();
				
				setQuery("", false);
				Utility.makeCenterToast(mContext, R.string.toast_search_finish, Toast.LENGTH_SHORT).show();
				//menuitem.collapseActionView();	// Pour revenir a l'icon apr�s la recherche
				
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
		// TODO Auto-generated method stub
		return false;
	}
}