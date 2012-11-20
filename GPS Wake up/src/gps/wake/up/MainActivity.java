package gps.wake.up;

import java.io.IOException;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends MapActivity implements OnClickListener {

	private MapView mapView;
	private MyLocationOverlay myLocation;
	private EditText txtSearch;
	private ImageButton btnSearch;
	private Geocoder geoCoder;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		geoCoder = new Geocoder(this);

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		txtSearch = (EditText) findViewById(R.id.txtSearch);
		btnSearch = (ImageButton) findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(this);

		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(
				R.drawable.icon_blue);
		RadiusItemizedOverlay itemizedoverlay = new RadiusItemizedOverlay(
				drawable, this);

		GeoPoint point = new GeoPoint(19240000, -99120000);
		Alarm overlayitem = new Alarm(point, "Hola, Mundo!",
				"I'm in Mexico City!");
		overlayitem.setRadius(20000);

		GeoPoint point2 = new GeoPoint(35410000, 139460000);
		Alarm overlayitem2 = new Alarm(point2, "Sekai, konichiwa!",
				"I'm in Japan!");
		overlayitem2.setRadius(50000);

		myLocation = new MyLocationOverlay(getApplicationContext(), mapView);
		myLocation.enableMyLocation();

		myLocation.runOnFirstFix(new Runnable() {
			public void run() {
				mapView.getController().animateTo(myLocation.getMyLocation());
			}
		});

		itemizedoverlay.addOverlay(overlayitem);
		itemizedoverlay.addOverlay(overlayitem2);
		mapOverlays.add(itemizedoverlay);
		mapOverlays.add(myLocation);
		mapView.setLongClickable(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onClick(View v) {

		try {
			List<Address> addresses = geoCoder.getFromLocationName(txtSearch
					.getText().toString(), 5);

			if (addresses.size() > 0) {
				GeoPoint p = new GeoPoint(
						(int) (addresses.get(0).getLatitude() * 1E6),
						(int) (addresses.get(0).getLongitude() * 1E6));

				mapView.getController().animateTo(p);
				mapView.getController().setZoom(12);

				RadiusItemizedOverlay itemizedoverlay = new RadiusItemizedOverlay(
						this.getResources().getDrawable(R.drawable.icon_red),
						this);
				
				Alarm overlayitem = new Alarm(p, txtSearch.getText().toString(),"");
				overlayitem.setRadius(0);
				
				List<Overlay> listOfOverlays = mapView.getOverlays();
				listOfOverlays.clear();
				listOfOverlays.add(itemizedoverlay);

				mapView.invalidate();
				txtSearch.setText("");
			} else {
				AlertDialog.Builder adb = new AlertDialog.Builder(this);
				adb.setTitle("Google Map");
				adb.setMessage("Please Provide the Proper Place");
				adb.setPositiveButton("Close", null);
				adb.show();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
