package com.nethergrim.combogymdiary.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.SimpleExpandableListAdapter;

import com.nethergrim.combogymdiary.AdapterHelper;
import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.activities.CatalogDetailedActivity;

public class CatalogFragment extends Fragment {

    private ExpandableListView elvMain;
    private AdapterHelper ah;
    private SimpleExpandableListAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_catalog, null);
        elvMain = (ExpandableListView) v.findViewById(R.id.elvMain);
        ah = new AdapterHelper(getActivity());
        adapter = ah.getAdapter();
        elvMain.setAdapter(adapter);
        getActivity().getActionBar().setTitle(
                getResources().getString(R.string.exe_catalog));
        elvMain.setOnChildClickListener(new OnChildClickListener() {
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                gotoDetailed(groupPosition, childPosition, id);
                return false;
            }
        });
        elvMain.setOnGroupClickListener(new OnGroupClickListener() {
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return false;
            }
        });
        elvMain.setOnGroupCollapseListener(new OnGroupCollapseListener() {
            public void onGroupCollapse(int groupPosition) {
            }
        });
        elvMain.setOnGroupExpandListener(new OnGroupExpandListener() {
            public void onGroupExpand(int groupPosition) {
            }
        });
        return v;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void gotoDetailed(int groupPosition, int childPosition, long id) {
        Intent gotoDetailedActivity = new Intent(getActivity(),
                CatalogDetailedActivity.class);
        gotoDetailedActivity.putExtra("groupPosition", groupPosition);
        gotoDetailedActivity.putExtra("childPosition", childPosition);
        gotoDetailedActivity.putExtra("id", id);
        startActivity(gotoDetailedActivity);
    }
}
