package com.nethergrim.combogymdiary;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.nethergrim.combogymdiary.DynamicListView.onElementsSwapped;

public class TestActivity extends Activity implements onElementsSwapped {

	private DynamicListView list;
	private ArrayList<String> exercisesList = new ArrayList<String>();
	private ArrayList<Integer> setValuesList = new ArrayList<Integer>();
	private DB db;
	private String[] exersices;
	private int trainingId = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		db = new DB(this);
		db.open();

		if (db.getTrainingList(trainingId) != null) {
			exersices = db.convertStringToArray(db.getTrainingList(trainingId));
			for (int i = 0; i < exersices.length; i++) {
				exercisesList.add(exersices[i]);
				setValuesList.add(0);
			}
		}

		list = (DynamicListView) findViewById(R.id.list);

		StableArrayAdapter adapter = new StableArrayAdapter(this,
				R.layout.my_list_item2, exercisesList);

		list.setList(exercisesList);
		list.setAdapter(adapter);
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//		list.setActivity(this);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View itemClicked,
					int position, long id) {
				Toast.makeText(TestActivity.this, "pressed " + position,
						Toast.LENGTH_SHORT).show();
			}
		});

	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onSwapped(ArrayList arrayList, int indexOne, int indexTwo) {
		Toast.makeText(TestActivity.this,
				"swapped " + indexOne + " with " + indexTwo, Toast.LENGTH_SHORT)
				.show();
		swapElements(setValuesList, indexOne, indexTwo);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void swapElements(ArrayList arrayList, int indexOne, int indexTwo) {
		Object temp = arrayList.get(indexOne);
		arrayList.set(indexOne, arrayList.get(indexTwo));
		arrayList.set(indexTwo, temp);
	}

}
