package com.nethergrim.combogymdiary;

import java.text.SimpleDateFormat;
import java.util.Date;


import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.nethergrim.combogymdiary.Dialog1.MyInterface;
import android.app.backup.BackupManager;

@SuppressLint("SimpleDateFormat")
public class TrainingAtProgress extends Activity  implements MyInterface, OnClickListener, OnCheckedChangeListener{
	
	Button btnSave;	
	ToggleButton tglTimerOn;
	Boolean tglChecked = true,turnOff = false,vibrate = false;
	EditText etTimer;
	ListView lvMain;
	DB db;
	int size = 0;
	Cursor cur_exe;	
	final String LOG_TAG = "myLogs";
	ArrayAdapter<String> adapter;
	String[] trNamesData = {};
	int[] setsPerExercises = null;
	String traName = "", exeName = "",date = "",tValue="";
	SharedPreferences sp;
	int checkedPosition = 0,set = 0,oldReps = 0,oldWeight = 0,timerValue = 0,vibrateLenght=0;
	DialogFragment dlg1;
	ProgressDialog pd;
	Handler h;
	WheelView reps;
	WheelView weights;
	TextView infoText,setInfo,tv4,tv3;
	Animation anim = null;
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
	  setContentView(R.layout.training_at_progress_new_wheel);
	  initUi(false);
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DB(this);
		db.open();
        setContentView(R.layout.training_at_progress_new_wheel);
        initUi(true);
        
        }
  
	 @Override
	public void onChoose() { 
		 Toast.makeText(this,R.string.saved, Toast.LENGTH_SHORT).show();   
		 BackupManager bm = new BackupManager(this);
		 bm.dataChanged();
		 finish(); 
		 }
	
	private void initUi(boolean init){
		ActionBar bar = getActionBar();
        Intent intent = getIntent();
        traName = intent.getStringExtra("trainingName");
        bar.setTitle(traName);     
        reps = (WheelView) findViewById(R.id.wheelReps);
        reps.setVisibleItems(5); 
        reps.setWheelBackground(R.drawable.wheel_bg_holo);
        reps.setWheelForeground(R.drawable.wheel_val_holo);
        reps.setShadowColor(0xFFFFFF, 0xFFFFFF, 0xFFFFFF);
        tv4 = (TextView)findViewById(R.id.textView4__);
        tv3 = (TextView)findViewById(R.id.textView3__);
        reps.setViewAdapter(new RepsAdapter(this));
        weights = (WheelView) findViewById(R.id.wheelWeight);
        weights.setVisibleItems(5); 
        weights.setWheelBackground(R.drawable.wheel_bg_holo);
        weights.setWheelForeground(R.drawable.wheel_val_holo);
        weights.setShadowColor(0xFFFFFF, 0xFFFFFF, 0xFFFFFF);
        weights.setViewAdapter(new WeightsAdapter(this));
        String[] strArrExtra = {traName};
        String[] strArrCol = {DB.COLUMN_ID,DB.EXE_NAME};            
        tglTimerOn = (ToggleButton) findViewById(R.id.tglTurnOff);
        tglTimerOn.setOnCheckedChangeListener(this); 
        etTimer = (EditText) findViewById(R.id.etTimerValueAtTraining);
        etTimer.setOnClickListener(this);       
        infoText = (TextView)findViewById(R.id.infoText);
        btnSave = (Button)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        setInfo = (TextView)findViewById(R.id.tvSetInfo);
        lvMain = (ListView)findViewById(R.id.lvSets);
        anim = AnimationUtils.loadAnimation(this, R.anim.myalpha);
        lvMain.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        cur_exe = db.getDataExe(strArrCol, DB.TRA_NAME + "=?", strArrExtra, (String)null, (String)null, (String)null);
        size = cur_exe.getCount();
        
        
        
        trNamesData = new String[size];
        if (init) {
        	setsPerExercises = new int[size];        
	        for (int i = 0; i < size; i++){
	        	setsPerExercises[i] = 0;
	        }
        }
        if (cur_exe.moveToFirst()){
        	int i = 0;
        	do {
        		trNamesData[i] = cur_exe.getString(1);        		
        		i++;
              } while (cur_exe.moveToNext());
        }        
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice, trNamesData);
        lvMain.setAdapter(adapter);      
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        date = sdf.format(new Date(System.currentTimeMillis()));      
        sp = PreferenceManager.getDefaultSharedPreferences(this);        
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View itemClicked, int position,long id) {
        		checkedPosition = position;
        		exeName = trNamesData[checkedPosition];
        		set = setsPerExercises[checkedPosition];
        		setInfo.setText(getResources().getString(R.string.set_number)+ " " + (set+1));
        		tValue = db.getTimerValueByExerciseName(exeName);
        		etTimer.setText(tValue);      		
        		timerValue = Integer.parseInt(db.getTimerValueByExerciseName(exeName));
        		oldReps = db.getLastReps(exeName, set);
        		oldWeight = db.getLastWeight(exeName, set);
        		if ( oldReps>0 && oldWeight>0 ){
       				infoText.setText(getResources().getString( R.string.previous_result_was)+" "+oldWeight+"x"+oldReps);
       				weights.setCurrentItem(oldWeight-1);
       				reps.setCurrentItem(oldReps-1);
       				
       			}else {    
       				infoText.setText("");
       				}
        		animate();
        		}
        	
        	});        
		exeName = trNamesData[checkedPosition];
		set = setsPerExercises[checkedPosition];
		String tValue = db.getTimerValueByExerciseName(exeName);
		etTimer.setText(tValue);
		oldReps = db.getLastReps(exeName, set);
		oldWeight = db.getLastWeight(exeName, set);
		if ( oldReps>0 && oldWeight>0 ){
			infoText.setText(getResources().getString( R.string.previous_result_was)+" "+oldWeight+"x"+oldReps);
			weights.setCurrentItem(oldWeight-1);
			reps.setCurrentItem(oldReps-1);
			}else {    
				infoText.setText("");
				}
        dlg1 = new Dialog1();
        dlg1.setCancelable(false);
        etTimer.setText( db.getTimerValueByExerciseName(trNamesData[0]) );        
        timerValue = Integer.parseInt(db.getTimerValueByExerciseName(exeName));
        tglTimerOn.setChecked(true);
        lvMain.setItemChecked(0, true);
        setInfo.setText(getResources().getString(R.string.set_number)+ " " + (set+1));
        setInfo.setTextColor( getResources().getColor(R.color.holo_orange_dark) );
        infoText.setTextColor( getResources().getColor(R.color.holo_orange_dark) );
        animate();        
	}
	
	private void animate(){
		infoText.startAnimation(anim);
		setInfo.startAnimation(anim);
		reps.startAnimation(anim);
		weights.startAnimation(anim);
		tv3.startAnimation(anim);
		tv4.startAnimation(anim);
		etTimer.startAnimation(anim);
		btnSave.startAnimation(anim);
		tglTimerOn.startAnimation(anim);
	}
	
	protected void onResume() {
	    turnOff = sp.getBoolean("toTurnOff", false);
	    lvMain.setKeepScreenOn(!turnOff);
	    vibrate = sp.getBoolean("vibrateOn", true);
	    String vl = sp.getString("vibtateLenght", "2");
	    vibrateLenght = Integer.parseInt(vl);
	    vibrateLenght *= 1000;
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
			Log.d(LOG_TAG, "vibrating for "+vibrateLenght+" millisec ");
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
		}
		return false;    	
    }
	
	@Override
	public void onCheckedChanged(CompoundButton tglTimerOn, boolean isChecked) {
		if (isChecked){
			tglChecked = true;
			btnSave.setText(R.string.save_and_rest);
			etTimer.setEnabled(true);
		}
		else{
			tglChecked = false;
			etTimer.setEnabled(false);
			btnSave.setText(R.string.save);
		}			
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.main, menu);
	    return true;
	  }
	
	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();		
		if (id == R.id.btnSave) {
			String weightValue = ""+(weights.getCurrentItem() + 1);
			String repsValue = ""+(reps.getCurrentItem()+1);
			Log.d(LOG_TAG, "Saved: weight = "+weightValue+" reps = "+repsValue);
	
			int wei = (weights.getCurrentItem() + 1);
			int rep_s = (reps.getCurrentItem()+1);
			String t = etTimer.getText().toString();
			timerValue = Integer.parseInt(t);  			
   			exeName = trNamesData[checkedPosition];  			
   			set = ++setsPerExercises[checkedPosition];
   			setInfo.setText(getResources().getString(R.string.set_number)+ " " + (set+1));
   			db.addRec_Main(traName, exeName, t, date, wei, rep_s, set);	
   			Toast.makeText(this,R.string.saved, Toast.LENGTH_SHORT).show();    			
   			oldReps = db.getLastReps(exeName, set);
   			oldWeight = db.getLastWeight(exeName, set);
   			if ( oldReps>0 && oldWeight>0 ){
   				infoText.setText(getResources().getString( R.string.previous_result_was)+" "+oldWeight+"x"+oldReps);
   				animate();
   				weights.setCurrentItem(oldWeight-1);
   				reps.setCurrentItem(oldReps-1);
   			}else {    
   				infoText.setText("");
   				}
   			if (tglChecked) {
   				goDialogProgress();    				
   			}
		}
	}	
	
	@Override
	public void onBackPressed() {
		dlg1.show(getFragmentManager(), "dlg1");
	}
	
	protected void onDestroy(){
	    super.onDestroy();
	    db.close();
	  }
	
	private class RepsAdapter extends AbstractWheelTextAdapter {
		final String reps[] = new String[] {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40","41","42","43","44","45","46","47","48","49","50","51","52","53","54","55","56","57","58","59","60","61","62","63","64","65","66","67","68","69","70","71","72","73","74","75","76","77","78","79","80","81","82","83","84","85","86","87","88","89","90","91","92","93","94","95","96","97","98","99","100","101","102","103","104","105","106","107","108","109","110","111","112","113","114","115","116","117","118","119","120","121","122","123","124","125","126","127","128","129","130","131","132","133","134","135","136","137","138","139","140","141","142","143","144","145","146","147","148","149","150","151","152","153","154","155","156","157","158","159","160","161","162","163","164","165","166","167","168","169","170","171","172","173","174","175","176","177","178","179","180","181","182","183","184","185","186","187","188","189","190","191","192","193","194","195","196","197","198","199","200","201","202","203","204","205","206","207","208","209","210","211","212","213","214","215","216","217","218","219","220","221","222","223","224","225","226","227","228","229","230","231","232","233","234","235","236","237","238","239","240","241","242","243","244","245","246","247","248","249","250","251","252","253","254","255","256","257","258","259","260","261","262","263","264","265","266","267","268","269","270","271","272","273","274","275","276","277","278","279","280","281","282","283","284","285","286","287","288","289","290","291","292","293","294","295","296","297","298","299","300","301","302","303","304","305","306","307","308","309","310","311","312","313","314","315","316","317","318","319","320","321","322","323","324","325","326","327","328","329","330","331","332","333","334","335","336","337","338","339","340","341","342","343","344","345","346","347","348","349","350"};

		protected RepsAdapter(Context context) {
			super(context, R.layout.city_holo_layout, NO_RESOURCE);

			setItemTextResource(R.id.city_name);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return reps.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return reps[index];
		}
	}
	
	private class WeightsAdapter extends AbstractWheelTextAdapter {
		final String weights[] = new String[] {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40","41","42","43","44","45","46","47","48","49","50","51","52","53","54","55","56","57","58","59","60","61","62","63","64","65","66","67","68","69","70","71","72","73","74","75","76","77","78","79","80","81","82","83","84","85","86","87","88","89","90","91","92","93","94","95","96","97","98","99","100","101","102","103","104","105","106","107","108","109","110","111","112","113","114","115","116","117","118","119","120","121","122","123","124","125","126","127","128","129","130","131","132","133","134","135","136","137","138","139","140","141","142","143","144","145","146","147","148","149","150","151","152","153","154","155","156","157","158","159","160","161","162","163","164","165","166","167","168","169","170","171","172","173","174","175","176","177","178","179","180","181","182","183","184","185","186","187","188","189","190","191","192","193","194","195","196","197","198","199","200","201","202","203","204","205","206","207","208","209","210","211","212","213","214","215","216","217","218","219","220","221","222","223","224","225","226","227","228","229","230","231","232","233","234","235","236","237","238","239","240","241","242","243","244","245","246","247","248","249","250","251","252","253","254","255","256","257","258","259","260","261","262","263","264","265","266","267","268","269","270","271","272","273","274","275","276","277","278","279","280","281","282","283","284","285","286","287","288","289","290","291","292","293","294","295","296","297","298","299","300","301","302","303","304","305","306","307","308","309","310","311","312","313","314","315","316","317","318","319","320","321","322","323","324","325","326","327","328","329","330","331","332","333","334","335","336","337","338","339","340","341","342","343","344","345","346","347","348","349","350"};

		protected WeightsAdapter(Context context) {
			super(context, R.layout.city_holo_layout, NO_RESOURCE);

			setItemTextResource(R.id.city_name);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return weights.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return weights[index];
		}
	}
}
