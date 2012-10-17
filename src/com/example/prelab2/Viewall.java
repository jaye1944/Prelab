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
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.app.AlertDialog;
import android.content.DialogInterface;


public class Viewall extends Activity {

	private DataBaseHandler database;
	private ListView listview;
	private String[] menuItems;
	
	private AlertDialog alertDialog;
	private PendingIntent pendingAlarmIntent1;
	private AlertDialog deletConferm;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewall);

		listview = (ListView) findViewById(R.id.list);
		//listview.setText;
		
		 alertDialog = new AlertDialog.Builder(this).create();
	        alertDialog.setTitle("ERROR");
	        alertDialog.setMessage("No Practicals Found");
	        alertDialog.setButton("Ok ", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(Viewall.this, Welcom.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					
				}
			});	        
	        alertDialog.setIcon(R.drawable.error);
		
		database = new DataBaseHandler(this);
		database.open();

		List<Practical> practicals1 = database.getAllPractical();
		
		if(practicals1.isEmpty()){
			alertDialog.show();			
		}else{			
			ArrayAdapter<Practical> adapter = new ArrayAdapter<Practical>(this,
					R.layout.list_view_item, R.id.txtTitle, practicals1);
			
			View header = (View)getLayoutInflater().inflate(R.layout.list_view_header, null);
			listview.addHeaderView(header, null, false);
			listview.setAdapter(adapter);
			registerForContextMenu(listview);			
		}
		listview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				view.showContextMenu();
			}
		});
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		Object object = listview.getItemAtPosition(info.position);
		String str = String.valueOf(object);
		menu.setHeaderTitle(str);
		menuItems = new String[] { "Edit", "Delete" , "Cancel" };
		menu.add(Menu.NONE, 0, 0, menuItems[0]);
		menu.add(Menu.NONE, 1, 1, menuItems[1]);
		menu.add(Menu.NONE, 2, 2, menuItems[2]);
	}

	public boolean onContextItemSelected(MenuItem item) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		Object object = listview.getItemAtPosition(info.position);
		final String str = String.valueOf(object);
		int menuItemIndex = item.getItemId();
		menuItems = new String[] { "Edit", "Delete","Cancel" };
		String menuItemName = menuItems[menuItemIndex];
		//edit
		if (menuItemName == "Edit") {
			Intent intent = new Intent(Viewall.this, Edit.class);
			intent.putExtra("data", str);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		}
		//delete
		else if (menuItemName == "Delete") {
			 	deletConferm = new AlertDialog.Builder(this).create();
			    deletConferm.setTitle("Attention");
			    deletConferm.setMessage("Are you sure you want to delete this..?? ");
			    deletConferm.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {					

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String[] str_ary = str.split("\t");
						Practical practical = new Practical( str_ary[0], str_ary[1],str_ary[2]);
						
						long id = database.getPracticalId(str_ary[0]);
						Intent alarmIntent = new Intent(Viewall.this, MyAlarmService.class);
						
						//cancel alarm
						alarmIntent.putExtra("nel.example.alarms1", "My message");
						pendingAlarmIntent1 = PendingIntent.getService(Addnew.getActivityContext(),(int) id, alarmIntent, 0);
						AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
						
						alarmManager.cancel(pendingAlarmIntent1);
						pendingAlarmIntent1.cancel();
						
						database.deletePractical(practical, str_ary[0]);
						
						//reload aftre delete
						try{
						Intent refresh = getIntent();
						startActivity(refresh);
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
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

}
