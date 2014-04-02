package com.nethergrim.combogymdiary.fragments;

import com.nethergrim.combogymdiary.R;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainStatisticsFragment extends Fragment {


	    private FragmentTabHost mTabHost;

	    //Mandatory Constructor
	    public MainStatisticsFragment() {
	    }
	    
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	    }

	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {

	        View rootView = inflater.inflate(R.layout.fragment_main_statistics,container, false);


	        mTabHost = (FragmentTabHost)rootView.findViewById(android.R.id.tabhost);
	        mTabHost.setup(getActivity().getApplicationContext(), getChildFragmentManager(), R.id.realtabcontent);

	        mTabHost.addTab(mTabHost.newTabSpec("fragmentb").setIndicator("Fragment B"),
	                StatisticsFragment.class, null);
	        mTabHost.addTab(mTabHost.newTabSpec("fragmentc").setIndicator("Fragment C"),
	        		StatisticsFragment.class, null);
	        mTabHost.addTab(mTabHost.newTabSpec("fragmentd").setIndicator("Fragment D"),
	        		StatisticsFragment.class, null);


	        return rootView;
	    }

}
