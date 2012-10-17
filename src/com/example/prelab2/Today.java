/*Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements. See the NOTICE file
distributed with this work for additional information
regarding copyright ownership. The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied. See the License for the
specific language governing permissions and limitations
under the License.*/
package com.example.prelab2;

import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class Today extends Activity {

	private String day;
	private String month;
	private String year;
	private String fulldate = null;
	private int[] date;

	private DataBaseHandler database;
	private ListView list;
	private EditText today;
	private AlertDialog alertDialog;
	private AlertDialog deletConferm;
	private String[] menuItems;
	
	private Toast toast;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_today);
		list = (ListView) findViewById(R.id.today_list);
		
		final Button gototoday = (Button) findViewById(R.id.New);
		final Button showall = (Button) findViewById(R.id.GetAll);

		Bundle extras = this.getIntent().getExtras();
		date = extras.getIntArray("data");
		today = (EditText) findViewById(R.id.today);
		day = String.valueOf(date[0]);
		month = String.valueOf((date[1] + 1));
		year = String.valueOf(date[2]);
		fulldate = (day + "/" + month + "/" + year);
		today.setText(fulldate);
		today.setFocusable(false);
		
		 alertDialog = new AlertDialog.Builder(this).create();
	        alertDialog.setTitle("ERROR");
	        alertDialog.setMessage("No Practicals Found "+ fulldate);
	        alertDialog.setButton("Ok ", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub					
				}
			});
	        
	    alertDialog.setIcon(R.drawable.error);
	    
	   
		database = new DataBaseHandler(this);
		database.open();

		List<Practical> practical = database.getTodayPracticals(fulldate);
		
		if(practical.isEmpty()){			
			alertDialog.show();
		}
		else{			
			ArrayAdapter<Practical> adapter = new ArrayAdapter<Practical>(this,
					R.layout.list_view_item, R.id.txtTitle, practical);			
			 View header = (View)getLayoutInflater().inflate(R.layout.list_view_header, null);			 
			list.addHeaderView(header, null, false);			
			list.setAdapter(adapter);
			registerForContextMenu(list);

		}
		
		
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(getApplicationContext(),
						"Click ListItem Number " + position, Toast.LENGTH_LONG)
						.show();
				view.showContextMenu();
			}
		});
		
		//set forcus buttons
		buttonToast(gototoday, "Add it now");
		buttonToast(showall, "Show all");
		
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		Object object = list.getItemAtPosition(info.position);
		String str = String.valueOf(object);
		menu.setHeaderTitle(str);
		menuItems = new String[] { "Edit", "Delete" };
		menu.add(Menu.NONE, 0, 0, menuItems[0]);
		menu.add(Menu.NONE, 1, 1, menuItems[1]);
	

	}

	public boolean onContextItemSelected(MenuItem item) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		Object object = list.getItemAtPosition(info.position);
		final String str = String.valueOf(object);
		int menuItemIndex = item.getItemId();
		menuItems = new String[] { "Edit", "Delete", "Cancel" };
		String menuItemName = menuItems[menuItemIndex];

		if (menuItemName == "Edit") {

			Intent intent = new Intent(Today.this, Edit.class);
			intent.putExtra("data", str);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();

		}

		else if (menuItemName == "Delete") {
			 deletConferm = new AlertDialog.Builder(this).create();
			    deletConferm.setTitle("Are You Sure");
			    deletConferm.setMessage("Delete and go to welcom page");
			    deletConferm.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
					
					private PendingIntent pendingAlarmIntent;

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String[] str_ary = str.split("\t");
						Practical practical = new Practical( str_ary[0], str_ary[1],
								str_ary[2]);
						
						long id = database.getPracticalId(str_ary[0]);
						Intent alarmIntent = new Intent(Today.this, MyAlarmService.class);
						
						alarmIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						
						alarmIntent.putExtra("nel.example.alarms1", "My message");
						pendingAlarmIntent = PendingIntent.getService(Addnew.getActivityContext(),
								(int) id, alarmIntent, 0);
						AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
						
						alarmManager.cancel(pendingAlarmIntent);
						pendingAlarmIntent.cancel();
						
						
						database.deletePractical(practical, str_ary[0]);
						//reload
						try{
						Intent intent = getIntent();
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						finish();
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			    deletConferm.setButton(DialogInterface.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
			    deletConferm.setIcon(R.drawable.error);
			    deletConferm.show();
			    
			
		
		}

	

		return true;
	}

	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.New:
			Intent intent1 = new Intent(Today.this, Addnew.class);
			intent1.putExtra("data", fulldate);
			intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent1);
			finish();
			break;

		case R.id.GetAll:
			Intent intent2 = new Intent(Today.this, Viewall.class);
			intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent2);
			finish();
			break;
		}
	}
	
	public void buttonToast(final Button bb, final String message){
		bb.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					toast = Toast.makeText(getBaseContext(), message,
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP | Gravity.LEFT,
							(int) bb.getX(), (int) bb.getY());
					toast.show();
				}
			}
		});		
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

}
