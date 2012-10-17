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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class Welcom extends Activity {
	String formattedDate;
	private EditText current_date;
	private Toast toast;

	// private ListView list;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcom);

		//current_date.setFocusable(false);
		final Button exit = (Button) findViewById(R.id.button_exit);
		final Button gotocalendar = (Button) findViewById(R.id.go_to_calander);
		final Button gototoday = (Button) findViewById(R.id.go_to_add_new);
		final Button showall = (Button) findViewById(R.id.go_to_view_all);
		final Button gotoset = (Button) findViewById(R.id.gotosettings);

		current_date = (EditText) findViewById(R.id.current_date);
		// list = (ListView)findViewById(R.id.current_day_list);

		Calendar calendar = Calendar.getInstance();

		formattedDate = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))
				+ "/" + String.valueOf(calendar.get(Calendar.MONTH) + 1) + "/"
				+ String.valueOf(calendar.get(Calendar.YEAR));

		current_date.setText(formattedDate);
		current_date.setFocusable(false);
		
		//set forcus change name
		buttonToast(gotocalendar, "Go to Calendar");
		buttonToast(exit, "   Exit");
		buttonToast(gotoset, "Settings");
		buttonToast(gototoday, "Add it now");
		buttonToast(showall, "Show all");

		//set forcus go to today
		gototoday.setFocusable(true);
	}
	
	public void onClick(View view) {

		switch (view.getId()) {

		case R.id.go_to_calander:
			Intent intent1 = new Intent(Welcom.this, Calander.class);
			startActivity(intent1);
			break;

		case R.id.go_to_add_new:
			Intent intent2 = new Intent(Welcom.this, Addnew.class);
			intent2.putExtra("data", formattedDate);
			startActivity(intent2);
			break;

		case R.id.go_to_view_all:
			Intent intent3 = new Intent(Welcom.this, Viewall.class);
			startActivity(intent3);
			break;
			
		case R.id.gotosettings:
			Intent prefs = new Intent(Welcom.this, Pref.class);
			startActivity(prefs);
			break;

		case R.id.button_exit:
			Intent intent = new Intent(this, Welcom.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
	
}
