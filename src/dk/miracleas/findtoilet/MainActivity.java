package dk.miracleas.findtoilet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

public class MainActivity extends Activity implements GooglePlayServicesClient.OnConnectionFailedListener,
		GooglePlayServicesClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener {

	private final int zoom = 13;
	public List<Toilet> toiletList = new ArrayList<Toilet>();
	private List<Marker> markerList = new ArrayList<Marker>();

	private LinearLayout ll;
	private DrawerLayout mDrawerLayout;
	private ListView list;
	private MyListAdapter adapter;
	private GoogleMap map;
	private LocationClient lClient;
	private Location curLoc;
	private LocationRequest lReq;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		ll = (LinearLayout) findViewById(R.id.ll);
		list = (ListView) findViewById(R.id.drawer_list);
		adapter = new MyListAdapter(this, R.layout.list_layout, toiletList);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				
				Toilet t = adapter.getItem(pos);
				for (int i = 0; i < toiletList.size(); i++) {
					
					if(t.getId().equals(toiletList.get(i).getId())){
						LatLng loc = new LatLng(toiletList.get(i).getLat(), toiletList.get(i).getLon());
						map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, zoom));
						mDrawerLayout.closeDrawer(ll);
					}
				}
			}
		});
		
		new Thread(new Runnable() {

			@Override
			public void run() {

				loadData();
				loadDistance();
				loadMarkers(toiletList);

			}
		}).start();
	}

	@Override
	protected void onResume() {
		super.onResume();

		setupLoc();
		setUpMapIfNeeded();


	}

	// Updates adapter with items
	public Runnable returnData = new Runnable() {

		@Override
		public void run() {

			adapter.clear();
			List<Toilet> tmpList = new ArrayList<Toilet>();
			tmpList.addAll(toiletList);
			Collections.sort(tmpList);

			if (tmpList != null && tmpList.size() > 0) {
				
				int a = tmpList.size();
				for (int i = 0; i < a; i++) {
					adapter.add(tmpList.get(i));
				}
			}
			
			adapter.notifyDataSetChanged();
			tmpList = null;
		}
	};

	// Setup all location services
	private void setupLoc() {
		
		if (lClient == null) {

			lClient = new LocationClient(this, this, this);
			lClient.connect();
		}

		if (lReq == null) {

			lReq = LocationRequest.create();
			lReq.setInterval(10000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		}
	}

	private void setUpMapIfNeeded() {

		// Setup map if needed
		if (map == null) {

			map = ((MapFragment) getFragmentManager().findFragmentById(R.id.main_map)).getMap();

			// Check if we were successful in obtaining the map.
			if (map != null) {
				map.setMyLocationEnabled(true);
				map.setInfoWindowAdapter(new MyInfoWindow(getLayoutInflater()));
			}

			map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

				@Override
				public void onInfoWindowClick(Marker marker) {
					
					for (int i = 0; i < toiletList.size(); i++) {
						
						if(marker.getId().equals(toiletList.get(i).getId())){
							LatLng loc = new LatLng(toiletList.get(i).getLat(), toiletList.get(i).getLon());
														
							Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:ll="+loc.latitude + "," + loc.longitude)); 
							startActivity(intent);
						}
					}
				}
			});
		}
	}

	@Override
	public void onLocationChanged(Location location) {

		curLoc.set(location);

		loadDistance();

		runOnUiThread(returnData);

		for (int i = 0; i < markerList.size(); i++) {

			Marker m = markerList.get(i);
			if(m.isInfoWindowShown()){
				m.hideInfoWindow();
				m.setSnippet(formatDist(toiletList.get(i).getDistance()));
				m.showInfoWindow();
			}else{
				m.setSnippet(formatDist(toiletList.get(i).getDistance()));
			}			
		}
	}

	// Add markers to map
	public void loadMarkers(final List<Toilet> tmpList) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				
				for (int i = 0; i < tmpList.size(); i++) {

					LatLng loc = new LatLng(tmpList.get(i).getLat(), tmpList.get(i).getLon());
					Marker m = map.addMarker(new MarkerOptions().position(loc).title(tmpList.get(i).getStreet())
							.snippet(formatDist(tmpList.get(i).getDistance())));
					
					markerList.add(m);

				}
			}
		});
	}

	public void loadDistance() {

		// Calculate range from currentlocation to each toilet
		for (int i = 0; i < toiletList.size(); i++) {

			double lat = toiletList.get(i).getLat();
			double lng = toiletList.get(i).getLon();

			Location toiletLocation = new Location("toilet");
			toiletLocation.setLatitude(lat);
			toiletLocation.setLongitude(lng);

			toiletList.get(i).setDistance((int) curLoc.distanceTo(toiletLocation));
		}
	}

	public void loadData() {

		toiletList.clear();
		toiletList = getData();
	}

	// Loads data from all sources
	public List<Toilet> getData() {

		List<Toilet> tmpList1;
		List<Toilet> tmpList2;

		XmlParser parser = new XmlParser("http://beta.findtoilet.dk/xml/Danmark");
		tmpList1 = parser.parse();

		LocalXML lParser = new LocalXML(this);
		tmpList2 = lParser.getData();
		
		tmpList1.addAll(tmpList2);
		for (int i = 0; i < tmpList1.size(); i++) {

			tmpList1.get(i).setId("m" +i);
		}

		tmpList2 = null;
		return tmpList1;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu:
			
			if (mDrawerLayout.isDrawerOpen(ll)) {
				mDrawerLayout.closeDrawer(ll);
			} else {
				mDrawerLayout.openDrawer(ll);
			}
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

	@Override
	public void onConnected(Bundle connectionHint) {

		curLoc = lClient.getLastLocation();
		LatLng tmpLoc = new LatLng(curLoc.getLatitude(), curLoc.getLongitude());
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(tmpLoc, zoom));
		lClient.requestLocationUpdates(lReq, this);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Stop GPS
		lClient.disconnect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
	}

	// format distance
	private String formatDist(float meters) {
		if (meters < 1000) {
			return ((int) meters) + getResources().getString(R.string.meters);
		} else if (meters < 10000) {
			return formatDec(meters / 1000f, 1) + getResources().getString(R.string.kilometers);
		} else {
			return ((int) (meters / 1000f)) + getResources().getString(R.string.kilometers);
		}
	}

	// format km
	private String formatDec(float val, int dec) {
		int factor = (int) Math.pow(10, dec);

		int front = (int) (val);
		int back = (int) Math.abs(val * (factor)) % factor;

		return front + "." + back;
	}
}
