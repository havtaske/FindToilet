package dk.miracleas.findtoilet;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyListAdapter extends ArrayAdapter<Toilet> {

	private Context context;

	public MyListAdapter(Context context, int textViewResourceId, List<Toilet> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View currentRowView = convertView;
		if (currentRowView == null) {
			LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			currentRowView = li.inflate(R.layout.list_layout, null);

		}

		TextView title = (TextView) currentRowView.findViewById(R.id.list_title);
		TextView range = (TextView) currentRowView.findViewById(R.id.list_range);

		title.setText(getItem(position).getStreet());
		range.setText(formatDist(getItem(position).getDistance()));
		return currentRowView;
	}

	// format distance
	private String formatDist(float meters) {
		if (meters < 1000) {
			return ((int) meters) + context.getResources().getString(R.string.meters);
		} else if (meters < 10000) {
			return formatDec(meters / 1000f, 1) + context.getResources().getString(R.string.kilometers);
		} else {
			return ((int) (meters / 1000f)) + context.getResources().getString(R.string.kilometers);
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