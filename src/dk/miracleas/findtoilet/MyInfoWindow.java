package dk.miracleas.findtoilet;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public class MyInfoWindow implements InfoWindowAdapter {

	LayoutInflater inflater = null;

	MyInfoWindow(LayoutInflater inflater) {
		this.inflater = inflater;
	}

	@Override
	public View getInfoContents(Marker marker) {

		View v = inflater.inflate(R.layout.info_layout, null);

		TextView title = (TextView) v.findViewById(R.id.info_title);
		TextView snippet = (TextView) v.findViewById(R.id.info_snippet);
		
		title.setText(marker.getTitle());
		snippet.setText(marker.getSnippet());
		
		return v;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		// TODO Auto-generated method stub
		return null;
	}

}
