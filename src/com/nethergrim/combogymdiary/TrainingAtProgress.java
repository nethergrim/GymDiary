package com.nethergrim.combogymdiary;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mobeta.android.dslv.DragSortListView;
import com.nethergrim.combogymdiary.Dialog1.MyInterface;

@SuppressLint("SimpleDateFormat")
public class TrainingAtProgress extends BasicMenuActivity  implements MyInterface, OnCheckedChangeListener{
	
	private ToggleButton tglTimerOn;
	private Boolean tglChecked = true,turnOff = false,vibrate = false;
	private EditText etTimer;
	private DB db;
	private Cursor cursor;	
	private int trainingIdAtTable = 0;
	private ArrayAdapter<String> adapter;
	private String[] exersices;
	private String traName = "", exeName = "",date = "",tValue="";
	private SharedPreferences sp;
	private int checkedPosition = 0,set = 0, currentSet = 0,oldReps = 0,oldWeight = 0,timerValue = 0,vibrateLenght=0,currentId=0;
	private DialogFragment dlg1;
	private ProgressDialog pd;
	private long startTime  = 0;
	private Handler h;
	private WheelView reps, weights;
	private TextView infoText,setInfo;
	private ArrayList<String> alMain = new ArrayList<String>();
	private ArrayList<Integer> alSet = new ArrayList<Integer>();
	private int seconds, minutes,secDelta = 0,minDelta = 0;
	private boolean isTrainingProgress = false;
	private Handler timerHandler = new Handler();
	private LinearLayout llBack, llSave, llForward, llBottom;
	private ImageView ivBack,ivForward;
	private Animation anim = null;
	private DragSortListView list;
	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
	  mMenuDrawer.setContentView(R.layout.training_at_progress_new_wheel_new_list);
	  Log.d(LOG_TAG, "onConfigurationChanged");
	  initUi(false);  
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate");
        db = new DB(this);
		db.open();
        mMenuDrawer.setContentView(R.layout.training_at_progress_new_wheel_new_list);
        
        sp = PreferenceManager.getDefaultSharedPreferences(this); 
        isTrainingProgress = sp.getBoolean(TRAINING_AT_PROGRESS, false);
        if (isTrainingProgress) {
        	traName = sp.getString(TRAINING_NAME, "");
        } else {
        	traName = getIntent().getStringExtra("trainingName");
        }
        getActionBar().setTitle(traName);
        String[] strArrExtra = {traName};    
        
        cursor = db.getDataTrainings(null, DB.TRA_NAME + "=?", strArrExtra, null, null, null);
        if (cursor.moveToFirst()){
        	trainingIdAtTable = cursor.getInt(0);
        	exersices = db.convertStringToArray(cursor.getString(2)) ;
        	 for (int i = 0; i < exersices.length; i++) {
             	alMain.add(exersices[i]);
             }
        } else {
        	Log.d(LOG_TAG, "ERROR curor is empty");
        }
        
        sp.edit().putString(TRAINING_NAME, traName).apply();
        startService(new Intent(this, MyService.class));
        Editor ed = sp.edit();
    	ed.putBoolean(TRAINING_AT_PROGRESS, true);
    	ed.apply();
        startTime = System.currentTimeMillis();
        initUi(true);
        }
  
	 @Override
	public void onChoose() {   
		 sp.edit().putString(TRAINING_NAME, "").apply();
		 BackupManager bm = new BackupManager(this);
		 Cursor tmpCursor = db.getDataMain(null, null, null, null, null, null);
		 if (tmpCursor.getCount() > 10) {
			 bm.dataChanged();
			 Backuper backUP = new Backuper();
			 boolean yes = backUP.backupToSd();
			 if (yes){
	     		Toast.makeText(getApplicationContext(),getResources().getString(R.string.backuped), Toast.LENGTH_LONG).show();
	     		}
		 	}
		 Editor ed = sp.edit();
		 ed.putBoolean(TRAINING_AT_PROGRESS, false);
		 ed.apply();
		 stopService(new Intent(this, MyService.class));
		 NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		 notificationManager.cancel(sp.getInt(BasicMenuActivity.TRA_ID, 1));
		 
		 finish(); 
		 }
	 
	Runnable timerRunnable = new Runnable() {

	        @Override
	        public void run() {
	            long millis = System.currentTimeMillis() - startTime;
	            seconds = (int) (millis / 1000);
	            minutes += minDelta;
	            seconds += secDelta;
	            minutes = (seconds / 60) ;
	            seconds = (seconds % 60) ;

	            setInfo.setText(String.format("%d:%02d", minutes, seconds) + "  " +getResources().getString(R.string.set_number)+ " " + (currentSet+1));

	            timerHandler.postDelayed(this, 500);
	        }
	    };

	@Override
	public void onPause() {
		Log.d(LOG_TAG, "onPause");
	    	saveSetsToPreferences();
	    	saveTimerToPregerences();
	    	sp.edit().putString(TRAINING_NAME, traName);
	    	timerHandler.removeCallbacks(timerRunnable);
	    	
	    	
	    	
	    	
	    	
	    	
	        super.onPause();
	        
	        
	        
	    }
	  
	public void saveTimerToPregerences(){
		sp.edit().putInt(SECONDS, seconds).apply();
		sp.edit().putInt(MINUTES, minutes).apply();
	}
	
	public void restoreTimerFromPreferences(){
		minDelta = sp.getInt(MINUTES, 0);
		secDelta = sp.getInt(SECONDS, 0);
		sp.edit().putInt(MINUTES, 0).apply();
		sp.edit().putInt(SECONDS, 0).apply();
	}
	
	private DragSortListView.DropListener onDrop =
	        new DragSortListView.DropListener() {
	            @Override
	            public void drop(int from, int to) {
	                if (from != to) {
	                    DragSortListView list =(DragSortListView) findViewById(R.id.lvSets);
	                    String item = adapter.getItem(from);
	                    adapter.remove(item);
	                    adapter.insert(item, to);
	                    list.moveCheckState(from, to);
	                    Log.d("DSLV", "Dropped item is " + list.getCheckedItemPosition());
	                    
	                    String[] tmp = new String[alMain.size()];//saving position to DB TODO convert into asynctask
	        		    for (int i = 0; i < alMain.size(); i++){
	        		    	tmp[i] = alMain.get(i);
	        		    }
	        		    db.updateRec_Training(trainingIdAtTable, 2, db.convertArrayToString(tmp));
	        		    Log.d(LOG_TAG, "Dropped :\n"+db.convertArrayToString(tmp));
	                    
	                }
	            }
	        };
	
	private void initUi(boolean init){
       	llBottom = (LinearLayout)findViewById(R.id.LLBottom);
       	anim = AnimationUtils.loadAnimation(this, R.anim.setfortraining);
       	llBack = (LinearLayout) findViewById(R.id.llBtnBack);
       	llSave = (LinearLayout) findViewById(R.id.llBtnSave);
       	llForward = (LinearLayout)findViewById(R.id.llBtnForward);
       	llBack.setOnClickListener(this);
       	llSave.setOnClickListener(this);
       	llForward.setOnClickListener(this);
       	llBack.setEnabled(false);
       	llForward.setEnabled(false);
       	ivBack = (ImageView)findViewById(R.id.imageView2);
       	ivForward = (ImageView)findViewById(R.id.imageView3);
        reps = (WheelView) findViewById(R.id.wheelReps);
        reps.setVisibleItems(5); 
        reps.setWheelBackground(R.drawable.wheel_bg_holo);
        reps.setWheelForeground(R.drawable.wheel_val_holo);
        reps.setShadowColor(0xFFFFFF, 0xFFFFFF, 0xFFFFFF);
        reps.setViewAdapter(new RepsAdapter(this));
        weights = (WheelView) findViewById(R.id.wheelWeight);
        weights.setVisibleItems(5); 
        weights.setWheelBackground(R.drawable.wheel_bg_holo);
        weights.setWheelForeground(R.drawable.wheel_val_holo);
        weights.setShadowColor(0xFFFFFF, 0xFFFFFF, 0xFFFFFF);
        weights.setViewAdapter(new WeightsAdapter(this));
        tglTimerOn = (ToggleButton) findViewById(R.id.tglTurnOff);
        tglTimerOn.setOnCheckedChangeListener(this); 
        etTimer = (EditText) findViewById(R.id.etTimerValueAtTraining);
        etTimer.setOnClickListener(this);       
        infoText = (TextView)findViewById(R.id.infoText);
        setInfo = (TextView)findViewById(R.id.tvSetInfo);
        list =(DragSortListView) findViewById(R.id.lvSets);
        list.setDropListener(onDrop);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        
        
        
        if (init && isTrainingAtProgress == false) {       
	        for (int i = 0; i < 200; i++){
	        	alSet.add(0);
	        }
        } else if (init && isTrainingProgress == true) {
        	restoreSetsFromPreferences();
        }
        
        
        
        adapter = new ArrayAdapter<String>(this, R.layout.list_item_radio, R.id.text, alMain);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View itemClicked, int position,long id) {
        		Log.d(LOG_TAG, "item selected "+position);
        		checkedPosition = position;
        		
        		initData(position);
        		}
        	});   
        list.setItemChecked(0, true);
        initData(0);
        
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        date = sdf.format(new Date(System.currentTimeMillis()));      
        dlg1 = new Dialog1();
        dlg1.setCancelable(false);
        setInfo.setTextColor( getResources().getColor(R.color.holo_orange_dark) );
        infoText.setTextColor( getResources().getColor(R.color.holo_orange_dark) );
        tglTimerOn.setChecked(true);
        
        AdView adView = (AdView)this.findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder()
        .build();
        adView.loadAd(adRequest);
        
	}

	private void initSetButtons(){
		if (set > 0 && currentSet > 0) {
			llBack.setEnabled(true);
			ivBack.setAlpha(1.0F);
		} else {
			llBack.setEnabled(false);
			ivBack.setAlpha(0.35F);
			}
		if (currentSet < set) {
			llForward.setEnabled(true);
			ivForward.setAlpha(1.0F);
		} else { 
			llForward.setEnabled(false);
			ivForward.setAlpha(0.35F);
		}
	}
	
	private void initData(int position){
		exeName = alMain.get(position);
		set = alSet.get(position);
		currentSet = set;
		setInfo.setText(String.format("%d:%02d", minutes, seconds) + "  " +getResources().getString(R.string.set_number)+ " " + (set + 1));
		tValue = db.getTimerValueByExerciseName(exeName);
		etTimer.setText(tValue);   
		if (!exeName.isEmpty()){
			timerValue = Integer.parseInt(db.getTimerValueByExerciseName(exeName));
		}
		
		initSetButtons();
		oldReps = db.getLastReps(exeName, set);
		oldWeight = db.getLastWeight(exeName, set);
		if ( oldReps>0 && oldWeight>0 ){
				infoText.setText(getResources().getString( R.string.previous_result_was)+" "+oldWeight+"x"+oldReps);
				weights.setCurrentItem(oldWeight-1);
				reps.setCurrentItem(oldReps-1);
				
			}else {    
				infoText.setText(getResources().getString(R.string.new_set));
				}
	}
	
	protected void onResume() {
		Log.d(LOG_TAG, "onResume");
		
		if (isTrainingProgress ){
			
			restoreTimerFromPreferences();
			restoreSetsFromPreferences();
			
		} 
		initData(0);
	    turnOff = sp.getBoolean("toTurnOff", false);
	    list.setKeepScreenOn(!turnOff);
	    vibrate = sp.getBoolean("vibrateOn", true);
	    String vl = sp.getString("vibtateLenght", "2");
	    vibrateLenght = Integer.parseInt(vl);
	    vibrateLenght *= 1000;
	    timerHandler.postDelayed(timerRunnable, 0);
	    
	    
	   
	   
	    
	    super.onResume();
	  }
	
	@SuppressLint("HandlerLeak")
	private void goDialogProgress () {
      pd = new ProgressDialog(this);
      pd.setTitle(R.string.resting);
      pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
      pd.setMax(timerValue);
      pd.setIndeterminate(true);
      pd.show();
      h = new Handler() {
        public void handleMessage(Message msg) {
          pd.setIndeterminate(false);
          if (pd.getProgress() < pd.getMax()) {
            pd.incrementProgressBy(1);
            h.sendEmptyMessageDelayed(0, 1000);
          } else {
        	 if (vibrate) {
			Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(vibrateLenght);
          } 
            pd.dismiss();
          }
          
        }
      };      
      h.sendEmptyMessageDelayed(0, 100);
	}
	
	@Override
    public boolean onOptionsItemSelected (MenuItem item){
    	int itemId = item.getItemId();
    	if (itemId == R.id.itemExit){
			dlg1.show(getFragmentManager(), "dlg1");			
		} else if (itemId == R.id.itemEditTrainings){
			Intent intent = new Intent(this,EditingProgramAtTraining.class);
			intent.putExtra("trName", traName);
			intent.putExtra("ifAddingExe",true);
			Log.d(LOG_TAG, "started activity for result with extra: "+traName);
			startActivityForResult(intent, 1);
		} else if (itemId == android.R.id.home){
			if (mMenuDrawer.isActivated()) {
				mMenuDrawer.closeMenu();
				}
				else{
				mMenuDrawer.toggleMenu();}
			return true;
		} else if (itemId == R.id.itemSeePreviousTraining) {
			String[] args = {traName};
			Cursor tmpCursor = db.getDataMain(null, DB.TRA_NAME+"=?", args, DB.DATE, null, null);
			if (tmpCursor.moveToLast() && (tmpCursor.getCount() > 1 || !tmpCursor.getString(3).equals(date) )) {

				
				if (tmpCursor.getString(3).equals(date) ) { // сегодня уже были сеты
					tmpCursor.moveToPrevious();}

				Intent intent_history_detailed = new Intent(this,HistoryDetailedActivity.class);
				intent_history_detailed.putExtra("date", tmpCursor.getString(3));
				intent_history_detailed.putExtra("trName", tmpCursor.getString(1));
				startActivity(intent_history_detailed);	

				
				
			} else {
				Log.d(LOG_TAG, "empty cursor");
				Toast.makeText(this,getResources().getString(R.string.no_history)  + traName, Toast.LENGTH_SHORT).show();
			}		
		}
		return false;    	
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (data == null) {return;}
	    
	    long[] itemsChecked = data.getLongArrayExtra("return_array_of_exersices");
	    for (int i = 0; i < itemsChecked.length; i++) {
	    	
	    	alMain.add( db.getExerciseByID( (int) itemsChecked[i]) );
	    	Log.d(LOG_TAG, "adding exersice :"+db.getExerciseByID( (int) itemsChecked[i]) );
	    	alSet.add(0);
	    }
	    for (int j = 0; j < 100; j++) {
	    	alSet.add(0);
	    }
	    adapter.notifyDataSetChanged();
	    
	    String[] tmp = new String[alMain.size()];//saving position to DB TODO convert into asynctask
	    for (int i = 0; i < alMain.size(); i++){
	    	tmp[i] = alMain.get(i);
	    }
	    db.updateRec_Training(trainingIdAtTable, 2, db.convertArrayToString(tmp));
	    
	  }

	@Override
	public void onCheckedChanged(CompoundButton tglTimerOn, boolean isChecked) {
		if (isChecked){
			tglChecked = true;
			etTimer.setEnabled(true);
		}
		else{
			tglChecked = false;
			etTimer.setEnabled(false);
		}			
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.main, menu);
	    return true;
	  }
	
	public void saveSetsToPreferences(){
		
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < alSet.size() ; i++) {
			
		    str.append( alSet.get(i) ).append(",");
		}
		sp.edit().putString(LIST_OF_SETS, str.toString()).apply();
		

	}
	
	public void restoreSetsFromPreferences(){
		if ( sp.contains(LIST_OF_SETS)) {
			String savedString = sp.getString(LIST_OF_SETS, "");
			StringTokenizer st = new StringTokenizer(savedString, ",");
			ArrayList<Integer> array = new ArrayList<Integer>();
			int size = st.countTokens();
			for (int i = 0; i < size; i++) {
				array.add( Integer.parseInt(st.nextToken()) );
			}
			alSet = array;
		} 
	}
	
	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();	
		if (id == R.id.btnMenu2  || id == R.id.btnMenu3 || id == R.id.btnMenu4 || id == R.id.btnCatalog || id == R.id.btnMeasure){
			finish();
		}
		if (id == R.id.btnMenu1) {
			mMenuDrawer.closeMenu();
		}else {
			pressButton(id);
		}
		if (id == R.id.llBtnSave && currentSet == set) {
			int wei = (weights.getCurrentItem() + 1);
			int rep_s = (reps.getCurrentItem()+1);
			int tmp = alSet.get(checkedPosition);
			tmp++;
			alSet.set(checkedPosition, tmp);
			set = alSet.get(checkedPosition);
			setInfo.setText(String.format("%d:%02d", minutes, seconds) + "  " +getResources().getString(R.string.set_number)+ " " + (set+1));
   			db.addRec_Main(traName, exeName, date, wei, rep_s, set);	
   			currentSet = set;
   			initSetButtons();
   			Toast.makeText(this,R.string.saved, Toast.LENGTH_SHORT).show();    			
   			oldReps = db.getLastReps(exeName, set);
   			oldWeight = db.getLastWeight(exeName, set);
   			if ( oldReps>0 && oldWeight>0 ){
   				infoText.setText(getResources().getString( R.string.previous_result_was)+" "+oldWeight+"x"+oldReps);
   				weights.setCurrentItem(oldWeight-1);
   				reps.setCurrentItem(oldReps-1);
   			}else {    
   				infoText.setText(getResources().getString(R.string.new_set));
   				}
   			if (tglChecked) {
   				goDialogProgress();    				
   			}
		}else if (id ==R.id.llBtnSave && currentSet < set){
			int wei = (weights.getCurrentItem() + 1);
			int rep_s = (reps.getCurrentItem()+1);
			db.updateRec_Main(currentId, 4, null, wei);
			db.updateRec_Main(currentId, 5, null, rep_s);
			Toast.makeText(this,R.string.resaved, Toast.LENGTH_SHORT).show();
			currentSet = set;
			initData(checkedPosition);
		} else if ( id == R.id.llBtnBack) {
			if (currentSet > 0) {
				llBottom.startAnimation(anim);
				currentSet --; 
				int weitghsS = db.getThisWeight(currentSet+1, exeName) -1;
				int repsS = db.getThisReps(currentSet+1, exeName)  -1;
				currentId = db.getThisId(currentSet+1, exeName) ;
				weights.setCurrentItem(weitghsS);
				reps.setCurrentItem(repsS);
				infoText.setText(getResources().getString( R.string.resaved_text)+" " + (weitghsS+1) +"x"+(repsS+1));
			}
			
			
		}else if ( id == R.id.llBtnForward){
			if (currentSet < set-1 ) {
				llBottom.startAnimation(anim);
				currentSet ++; 
				int weitghsS = db.getThisWeight(currentSet+1, exeName) -1;
				int repsS = db.getThisReps(currentSet+1, exeName)  -1;
				weights.setCurrentItem(weitghsS);
				reps.setCurrentItem(repsS);
				infoText.setText(getResources().getString( R.string.resaved_text)+" " + (weitghsS+1) +"x"+(repsS+1));
			} else if (currentSet  == set -1) {
				llBottom.startAnimation(anim);
				initData(checkedPosition);
			}
		}
		initSetButtons();
		
	}	
	
	@Override
	public void onBackPressed() {
		if (mMenuDrawer.isMenuVisible()) {
			mMenuDrawer.closeMenu();
		} else {
			dlg1.show(getFragmentManager(), "dlg1");
		}
	}
	

	
	protected void onDestroy(){
		
		 db.close();
	    super.onDestroy();
	   
	  }
	
	private class RepsAdapter extends AbstractWheelTextAdapter {
		ArrayList<String> reps = new ArrayList<String>();
		protected RepsAdapter(Context context) {
			super(context, R.layout.city_holo_layout, NO_RESOURCE);
			
			for (int i = 0; i < 300; i++) {
				reps.add(""+(i+1));
			}
			
			setItemTextResource(R.id.city_name);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return reps.size();
		}

		@Override
		protected CharSequence getItemText(int index) {
			return reps.get(index);
		}
	}
	
	private class WeightsAdapter extends AbstractWheelTextAdapter {
		ArrayList<String> weights = new ArrayList<String>();
		protected WeightsAdapter(Context context) {
			super(context, R.layout.city_holo_layout, NO_RESOURCE);
			
			for (int i = 0; i < 1000; i++) {
				weights.add(""+(i+1));
			}
			
			setItemTextResource(R.id.city_name);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return weights.size();
		}

		@Override
		protected CharSequence getItemText(int index) {
			return weights.get(index);
		}
	}
}
