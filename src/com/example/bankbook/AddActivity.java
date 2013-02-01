package com.example.bankbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.passbook.R;
import com.google.gson.Gson;

public class AddActivity extends Activity {
	boolean showHomeAsUp = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(showHomeAsUp);
		
		Button addButton = (Button) findViewById(R.id.addButton);
		addButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO Fetch what filename is in use from shared prefs.
				
				EditText headlineInput = (EditText) findViewById(R.id.add_activity_headline_input);
				EditText dateInput = (EditText) findViewById(R.id.add_activity_date_input);
				EditText magnitudeInput = (EditText) findViewById(R.id.add_activity_magnitude_input);
				
				String headline = headlineInput.getText().toString();
				String date = dateInput.getText().toString();
				String magnitude = magnitudeInput.getText().toString();
				
				
				
				// Show a toast if any information is missing
				if(headline.matches("") || date.matches("") || magnitude.matches("")) {
					Toast toast = Toast.makeText(getApplicationContext(), "Missing information.", Toast.LENGTH_SHORT);
					toast.show();
				}
				else {
					ArrayList<Transaction> transactions = new ArrayList<Transaction>();
					String jsonFromFile = "";
					Gson gson = new Gson();
					
					try {
						FileInputStream fis = openFileInput(MainActivity.FILENAME[0]);
						int ch;
						StringBuffer stringBuffer = new StringBuffer("");
						while((ch = fis.read()) != -1) {
							stringBuffer.append((char) ch);
						}
						jsonFromFile = stringBuffer.toString();
						Log.d("Json from file", jsonFromFile);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					if(jsonFromFile.matches("") != true) {
						// Create Transaction[] from json file.
						Transaction[] previousTransactions = gson.fromJson(jsonFromFile, Transaction[].class);
						transactions = new ArrayList<Transaction>(Arrays.asList(previousTransactions));
					}
					
					// Create Transaction object and append to array
					Transaction transaction = new Transaction(headline, date, Double.valueOf(magnitude));
					transactions.add(transaction);
					Transaction[] allTransactions = new Transaction[transactions.size()];
					transactions.toArray(allTransactions);
					
					// Convert new list of Transactions to json and save in file.
					String json = gson.toJson(allTransactions);
					try {
						FileOutputStream fos = openFileOutput(MainActivity.FILENAME[0], Context.MODE_PRIVATE);
						fos.write(json.getBytes());
						fos.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					Intent intent = new Intent(AddActivity.this, MainActivity.class);
					AddActivity.this.startActivity(intent);
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_add, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				Intent intent = new Intent(this, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

}
