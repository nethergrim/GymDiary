package com.nethergrim.combogymdiary;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MeasurementsActivity extends BasicMenuActivity  implements LoaderCallbacks<Cursor> {

	ListView lvMeasurements;
	DB db;
	SimpleCursorAdapter scAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMenuDrawer.setContentView(R.layout.activity_measurements);
		getActionBar().setTitle(getResources().getString(R.string.measurements));
		lvMeasurements = (ListView)findViewById(R.id.lvMeasurements);
		db = new DB(this);
		db.open();
		String[] from = new String[] { DB.DATE};
		int[] to = new int[] { R.id.tvCatName };
	    scAdapter = new SimpleCursorAdapter(this, R.layout.list_with_arrow, null, from, to, 0);
	    lvMeasurements.setAdapter(scAdapter);
	    getSupportLoaderManager().initLoader(0, null, this);
	    getSupportLoaderManager().getLoader(0).forceLoad();
	    
	    lvMeasurements.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View itemClicked, int position,long id) {
        			Log.d(LOG_TAG, "Clicked pos = "+position+" id = "+id);
        			LinearLayout par = (LinearLayout) itemClicked;
        			TextView t = (TextView)par.findViewById(R.id.tvCatName);
        			
        			String date = (String) t.getText();
        			gotoDetailed(position, id,date);
         			
        		}
        	}); 
	    
	    
	    AdView adView = (AdView)this.findViewById(R.id.adView2);
	    AdRequest adRequest = new AdRequest.Builder().build();
	    adView.loadAd(adRequest);
	}
	
	private void gotoDetailed(int position, long id, String date){
		Log.d(LOG_TAG, "Clicked date = "+date);
		Intent gotoDetailed = new Intent (this,MeasurementsDetailedActivity.class);
		gotoDetailed.putExtra("clicked_position_of_measurements", position);
		gotoDetailed.putExtra("clicked_id", id);
		gotoDetailed.putExtra("date", date);
		startActivity(gotoDetailed);
	}
	
	@Override
	  public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
	    return new MyCursorLoader(this, db);
	  }

	  @Override
	  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
	    scAdapter.swapCursor(cursor);
	  }

	  @Override
	  public void onLoaderReset(Loader<Cursor> loader) {
	  }
	  
	  static class MyCursorLoader extends CursorLoader {

	    DB db;
	    Cursor cursor;
	    
	    public MyCursorLoader(Context context, DB db) {
	      super(context);
	      this.db = db;
	    }
	    
	    @Override
	    public Cursor loadInBackground() {
	      cursor = db.getDataMeasures(null, null, null, DB.DATE, null, DB.DATE);
	      return cursor;
	    }
	  }
	  
	  @Override
      public boolean onCreateOptionsMenu(Menu menu) {
              getMenuInflater().inflate(R.menu.measurements, menu);
              return true;
      }

      @Override
      public boolean onOptionsItemSelected(MenuItem item) {
              switch (item.getItemId()) {
              case R.id.action_add_measurements: {
                      Intent intent = new Intent (this,AddingMeasurementActivity.class);
                      startActivity(intent);
                      return true;
              }
              case android.R.id.home:
                      if (mMenuDrawer.isActivated()) {
                              mMenuDrawer.closeMenu();
                      }else
                              mMenuDrawer.toggleMenu();
                      return true;
              }
              return false;
      } 
	  
	  
	@Override
	public void onClick(View arg0) {
		pressButton(arg0.getId());
	}

	protected void onDestroy() {
	    super.onDestroy();
	    db.close();
	  }

}
