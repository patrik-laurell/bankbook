package com.example.bankbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.passbook.R;
import com.google.gson.Gson;

public class MainActivity extends Activity {
	private final String[] hashMapKeys = {"headline", "date", "magnitude"};
	public static final String[] FILENAME = {"visa_card"};
	SimpleAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Fill the listView with transactions. See http://www.heikkitoivonen.net/blog/2009/02/15/multicolumn-listview-in-android/ for further info.
		ListView listView = (ListView) findViewById(R.id.main_list_view);
		ArrayList<HashMap<String, String>> arrayList = getContentArrayList();
        adapter = new SimpleAdapter(this, arrayList, R.layout.row,
        		new String[] {hashMapKeys[0], hashMapKeys[1], hashMapKeys[2]}, new int[] {R.id.headline_cell, R.id.date_cell, R.id.magnitude_cell});
        listView.setAdapter(adapter);
        
        
        // Enable the contextual action bar when item is selected.
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
        	boolean[] itemStates = new boolean[getContentArrayList().size()];
        	
        	public void deleteSelectedItems() {
        		// Remove selected items from list.
        		Log.d("CAB", itemStates.toString());
        		ArrayList<HashMap<String, String>> list = getContentArrayList();
        		for(int i=0;i<list.size();i++) {
        			if(itemStates[i]) {
        				list.remove(i);
        			}
        		}
        		Log.d("CAB", "Removed items from tmp list");
        		// Convert list to json
        		Gson gson = new Gson();
        		String json = gson.toJson(list.toArray());
        		Log.d("CAB", "Wrote: \n"+json);
        		// Write new list to file.
        		try {
					FileOutputStream fos = openFileOutput(MainActivity.FILENAME[0], Context.MODE_PRIVATE);
					fos.write(json.getBytes());
					fos.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
        		// Update the arraylist connected to adapter. Then notify adater about data changes.
        		arrayList = list;
        		Log.d("CAB", "ArrayList: "+gson.toJson(arrayList));
        		adapter.notifyDataSetChanged();
        	}

        	@Override
			public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
				itemStates[position] = checked;
			}
        	
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch(item.getItemId()) {
					case R.id.main_cab_delete: 	
						deleteSelectedItems();
						mode.finish();
					default:
						return false;
				}
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				// Inflate menu for CAB
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.main_context, menu);
				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
				// TODO Auto-generated method stub
				return false;
			}
        	
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	//Defin
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.main_action_bar_add:
				Intent intent = new Intent(this, AddActivity.class);
				startActivity(intent);
				return true;
			case R.id.main_action_bar_edit:
				ListView list = (ListView) findViewById(R.id.main_list_view);
				int childCount = list.getChildCount();
				for(int i=0; i<childCount; i++) {
					View currentRow = list.getChildAt(i);
					CheckBox checkBox = (CheckBox) currentRow.findViewById(R.id.main_list_check_box);
					checkBox.setVisibility(View.VISIBLE);
					checkBox.setPadding(0, 0, 0, 0);
				}
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private ArrayList<HashMap<String, String>> getContentArrayList() {
		ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map;
		ArrayList<Transaction> transactionList = getTransactionHistory();
		
		for(Transaction transaction : transactionList) {
			map = new HashMap<String, String>();
			map.put(hashMapKeys[0], transaction.getHeadline());
			map.put(hashMapKeys[1], transaction.getDate());
			map.put(hashMapKeys[2], Double.valueOf(transaction.getMagnitude()).toString());
			arrayList.add(map);
		}
		return arrayList;
	}
	
	private ArrayList<Transaction> getTransactionHistory() {
		String jsonFromFile = "";
		
		// Fetch history from file and put in jsonFromFile.
		try {
			FileInputStream fis = openFileInput(MainActivity.FILENAME[0]);
			StringBuffer buffer = new StringBuffer("");
			int ch;
			while((ch = fis.read()) != -1) {
				buffer.append((char) ch);
			}
			jsonFromFile = buffer.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Convert jsonFromFile to ArrayList
		Gson gson = new Gson();
		Transaction[] transactions = gson.fromJson(jsonFromFile, Transaction[].class);
		ArrayList<Transaction> arrayList = new ArrayList<Transaction>(Arrays.asList(transactions));
		
		return arrayList;
	}

}
