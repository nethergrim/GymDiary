package com.nethergrim.combogymdiary;

import java.util.ArrayList;

import com.nethergrim.combogymdiary.DynamicListView.onElementsSwapped;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class TestActivity extends Activity implements onElementsSwapped {

	private DynamicListView list;
	private ArrayList<String> exercisesList = new ArrayList<String>();
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
			}
		}

		list = (DynamicListView) findViewById(R.id.list);

		StableArrayAdapter adapter = new StableArrayAdapter(this,
				R.layout.my_list_item2, exercisesList);

		list.setList(exercisesList);
		list.setAdapter(adapter);
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		list.setActivity(this);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View itemClicked,
					int position, long id) {
				Toast.makeText(TestActivity.this, "pressed " + position,
						Toast.LENGTH_SHORT).show();
			}
		});

	}

	@Override
	public void onSwapped(ArrayList arrayList, int indexOne, int indexTwo) {
		Toast.makeText(TestActivity.this,
				"swapped " + indexOne + " with " + indexTwo, Toast.LENGTH_SHORT)
				.show();

	}

}
