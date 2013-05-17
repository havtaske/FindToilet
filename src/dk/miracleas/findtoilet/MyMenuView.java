package dk.miracleas.findtoilet;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListView;

public class MyMenuView extends LinearLayout {

	private MyListAdapter adapter;
	private LinearLayout menu;
	private ListView listView;

	public MyMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		try {
			LayoutInflater.from(context).inflate(R.layout.layout_my_menu, this, true);
		} catch (Exception e) {
		}
		menu = (LinearLayout) findViewById(R.id.menu);
		listView = (ListView) findViewById(R.id.menu_list);
		adapter = new MyListAdapter(context, R.layout.list_layout, MainActivity.toiletList);
		listView.setAdapter(adapter);

	}

	public void updateList(){
		
		MainActivity.runOnUI(returnData);
	}
	
	// Updates adapter with items
	public Runnable returnData = new Runnable() {

		@Override
		public void run() {
			adapter.clear();

			if (MainActivity.toiletList != null && MainActivity.toiletList.size() > 0) {
				int a = MainActivity.toiletList.size();
				for (int i = 0; i < a; i++) {
					adapter.add(MainActivity.toiletList.get(i));
				}
			}
			adapter.notifyDataSetChanged();
			
		}
	};
	
	public void showMenu() {
		menu.setVisibility(View.VISIBLE);
		menu.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.in_from_left));
	}

	public void hideMenu() {
		menu.setVisibility(View.GONE);
		menu.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.out_to_left));
	}

	public void toggleMenu() {
		if (menu.getVisibility() == View.GONE) {
			showMenu();
		} else {
			hideMenu();
		}
	}	
	public int getState(){
		return menu.getVisibility();
	}
}
