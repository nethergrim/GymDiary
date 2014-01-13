package com.nethergrim.combogymdiary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.widget.SimpleExpandableListAdapter;

public class AdapterHelper {
  
  final String ATTR_GROUP_NAME= "groupName";
  final String ATTR_BODY_NAME= "bodyName";

  
  
  ArrayList<Map<String, String>> groupData;
  ArrayList<Map<String, String>> childDataItem;
  ArrayList<ArrayList<Map<String, String>>> childData;
  Map<String, String> m;
  String[] groups;
  String[] pectoral;
  String[] legs;
  String[] back;
  String[] deltoids;
  String[] biceps;
  String[] triceps;
  String[] abs;
  
  Context ctx;
  AdapterHelper(Context _ctx) {
    ctx = _ctx;    
    
    groups = ctx.getResources().getStringArray(R.array.MuscleGroupsArray);
    pectoral = ctx.getResources().getStringArray(R.array.exercisesArrayChest);
    legs = ctx.getResources().getStringArray(R.array.exercisesArrayLegs);
    back = ctx.getResources().getStringArray(R.array.exercisesArrayBack);
    deltoids = ctx.getResources().getStringArray(R.array.exercisesArrayShoulders);
    biceps = ctx.getResources().getStringArray(R.array.exercisesArrayBiceps);
    triceps = ctx.getResources().getStringArray(R.array.exercisesArrayTriceps);
    abs = ctx.getResources().getStringArray(R.array.exercisesArrayAbs);
  }

  
  SimpleExpandableListAdapter adapter;
  
  
  SimpleExpandableListAdapter getAdapter() {
    
        groupData = new ArrayList<Map<String, String>>();
        for (String group : groups) {
          m = new HashMap<String, String>();
            m.put(ATTR_GROUP_NAME, group); 
            groupData.add(m);  
        }
        
        String groupFrom[] = new String[] {ATTR_GROUP_NAME};
        int groupTo[] = new int[] {android.R.id.text1};
        

        childData = new ArrayList<ArrayList<Map<String, String>>>(); 
        
        childDataItem = new ArrayList<Map<String, String>>();
        for (String phone : pectoral) {
          m = new HashMap<String, String>();
            m.put(ATTR_BODY_NAME, phone); 
            childDataItem.add(m);  
        }
        childData.add(childDataItem);
      
        childDataItem = new ArrayList<Map<String, String>>();
        for (String phone : legs) {
          m = new HashMap<String, String>();
            m.put(ATTR_BODY_NAME, phone);
            childDataItem.add(m);  
        }
        childData.add(childDataItem);
       
        childDataItem = new ArrayList<Map<String, String>>();
        for (String phone : back) {
          m = new HashMap<String, String>();
            m.put(ATTR_BODY_NAME, phone);
            childDataItem.add(m);  
        }
        childData.add(childDataItem);
        
        childDataItem = new ArrayList<Map<String, String>>();
        for (String phone : deltoids) {
          m = new HashMap<String, String>();
            m.put(ATTR_BODY_NAME, phone);
            childDataItem.add(m);  
        }
        childData.add(childDataItem);
        
        childDataItem = new ArrayList<Map<String, String>>();
        for (String phone : biceps) {
          m = new HashMap<String, String>();
            m.put(ATTR_BODY_NAME, phone);
            childDataItem.add(m);  
        }
        childData.add(childDataItem);
        
        childDataItem = new ArrayList<Map<String, String>>();
        for (String phone : triceps) {
          m = new HashMap<String, String>();
            m.put(ATTR_BODY_NAME, phone);
            childDataItem.add(m);  
        }
        childData.add(childDataItem);
        
        childDataItem = new ArrayList<Map<String, String>>();
        for (String phone : abs) {
          m = new HashMap<String, String>();
            m.put(ATTR_BODY_NAME, phone);
            childDataItem.add(m);  
        }
        childData.add(childDataItem);
        
        String childFrom[] = new String[] {ATTR_BODY_NAME};
        int childTo[] = new int[] {android.R.id.text1};
        
        adapter = new SimpleExpandableListAdapter(
            ctx,
            groupData,
            android.R.layout.simple_expandable_list_item_1,
            groupFrom,
            groupTo,
            childData,
            android.R.layout.simple_list_item_1,
            childFrom,
            childTo);
        
    return adapter;
  }
  
  String getGroupText(int groupPos) {
    return ((Map<String,String>)(adapter.getGroup(groupPos))).get(ATTR_GROUP_NAME);
  }
  
  String getChildText(int groupPos, int childPos) {
    return ((Map<String,String>)(adapter.getChild(groupPos, childPos))).get(ATTR_BODY_NAME);
  }
  
  String getGroupChildText(int groupPos, int childPos) {
    return getGroupText(groupPos) + " " +  getChildText(groupPos, childPos);
  }
}
