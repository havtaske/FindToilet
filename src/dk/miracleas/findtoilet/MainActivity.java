package dk.miracleas.findtoilet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener, LocationSource {

	public static List<Toilet> toiletList = new ArrayList<Toilet>();
	private GoogleMap map;
	public static Handler UIHandler = new Handler(Looper.getMainLooper());

	private MyMenuView myMenu;

	private OnLocationChangedListener mListener;
	private LocationManager locationManager;

	private Location currentLocation = new Location("test");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		new Thread(new Runnable() {

			@Override
			public void run() {

				loadData();
				loadMarkers(toiletList);

			}
		}).start();

		myMenu = (MyMenuView) findViewById(R.id.myMenuView1);

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		if (locationManager != null) {
			boolean gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			boolean networkIsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (gpsIsEnabled) {
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 10F, this);
			} else if (networkIsEnabled) {
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 10F, this);
			} else {
				Toast.makeText(this, getResources().getString(R.string.gps_not_found), Toast.LENGTH_LONG).show();
			}
		} else {
			// Show some generic error dialog because something must have gone
			// wrong with location manager.
		}

		setUpMapIfNeeded();

	}

	@Override
	public void onPause() {
		if (locationManager != null) {
			locationManager.removeUpdates(this);
		}

		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();

		setUpMapIfNeeded();

		if (locationManager != null) {
			map.setMyLocationEnabled(true);
		}
	}

	private void setUpMapIfNeeded() {

		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (map == null) {
			// Try to obtain the map from the SupportMapFragment.
			map = ((MapFragment) getFragmentManager().findFragmentById(R.id.main_map)).getMap();
			
			// Check if we were successful in obtaining the map.
			if (map != null) {
				setUpMap();
			}
			
			map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
				
				@Override
				public void onInfoWindowClick(Marker marker) {
				
					for (int i = 0; i < toiletList.size(); i++) {
						System.out.println(marker.getId());
						if(marker.getPosition().equals(toiletList.get(i).getLatLng())){
							Toast.makeText(getApplicationContext(), toiletList.get(i).getStreet(), Toast.LENGTH_LONG).show();
						}						
					}					
				}
			});

			// register the LocationSource
			map.setLocationSource(this);
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.610841, 11.318689), 6));

		}
	}

	private void setUpMap() {
		map.setMyLocationEnabled(true);

	}

	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
	}

	@Override
	public void deactivate() {
		mListener = null;
	}

	@Override
	public void onLocationChanged(Location location) {

		if (mListener != null) {

			mListener.onLocationChanged(location);

			map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
			currentLocation.set(location);
			loadDistance();
			Collections.sort(toiletList);
			myMenu.updateList();
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "provider disabled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "provider enabled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "status changed", Toast.LENGTH_SHORT).show();
	}

	// Global run on ui
	public static void runOnUI(Runnable runnable) {
		UIHandler.post(runnable);
	}

	// Add markers to map
	public void loadMarkers(final List<Toilet> tmpList) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {

				for (int i = 0; i < tmpList.size(); i++) {

					LatLng loc = new LatLng(tmpList.get(i).getLat(), tmpList.get(i).getLon());
					map.addMarker(new MarkerOptions().position(loc).title(tmpList.get(i).getStreet()));	
					
				}
			}
		});
	}

	public void loadData() {

		toiletList = getData();

	}

	public void loadDistance() {

		// Calculate range from currentlocation to each toilet
		for (int i = 0; i < toiletList.size(); i++) {

			double lat = toiletList.get(i).getLat();
			double lng = toiletList.get(i).getLon();

			Location toiletLocation = new Location("toilet");
			toiletLocation.setLatitude(lat);
			toiletLocation.setLongitude(lng);

			toiletList.get(i).setDistance((int) currentLocation.distanceTo(toiletLocation));

		}
	}

	public List<Toilet> getData() {

		List<Toilet> tmpList1;
		List<Toilet> tmpList2;

		XmlParser parser = new XmlParser("http://beta.findtoilet.dk/xml/Danmark");
		tmpList1 = parser.parse();

		LocalXML lParser = new LocalXML(this);
		tmpList2 = lParser.getData();

		for (int i = 0; i < tmpList2.size(); i++) {

			tmpList1.add(tmpList2.get(i));
		}

		return tmpList1;

	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu:
			myMenu.toggleMenu();
			break;

		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// Close mymenu if visible else close program
	public void onBackPressed() {
		if (myMenu.getState() == View.VISIBLE) {
			myMenu.toggleMenu();
		} else {
			this.finish();
		}
	}
}
