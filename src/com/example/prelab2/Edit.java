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

import java.util.Calendar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.Intent;

public class Edit extends Activity {

	private EditText date;
	private EditText name;
	private EditText time;

	private DataBaseHandler database;

	private String str_name;
	private String str_date;
	private String str_time;
	private String name_unedited;

	private Toast toast;
	LayoutInflater toast_inflater;
	View toast_layout;
	ImageView toast_image;
	TextView toast_text;

	String al_message;

	private boolean hview;

	private boolean timechange = false;
	private boolean datechange = false;
	String new_time;

	private AlertDialog alertDialog;
	private AlertDialog alarmAlertDialog;

	protected static final int STATIC_INTEGER_VALUE = 0;
	protected static final int STATIC_INTEGER_VALUE1 = 1;

	private int myYear, myMonth, myDay, myHour, myMinute;
	static final int ID_TIMEPICKER = 1;
	static final int ID_DATEPICKER = 2;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);

		final Button ok = (Button) findViewById(R.id.edit);
		final Button cancel = (Button) findViewById(R.id.cancel);

		// prefs

		SharedPreferences getprefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		hview = getprefs.getBoolean("clock", true);

		// custom toast
		toast_inflater = getLayoutInflater();
		toast_layout = toast_inflater.inflate(R.layout.toast_layoutx,
				(ViewGroup) findViewById(R.id.toast_layout_show));
		toast_image = (ImageView) toast_layout.findViewById(R.id.toastimage);
		toast_image.setImageResource(R.drawable.warningicn);
		toast_text = (TextView) toast_layout.findViewById(R.id.toasttext);

		database = new DataBaseHandler(this);

		date = (EditText) findViewById(R.id.date);
		name = (EditText) findViewById(R.id.name);
		time = (EditText) findViewById(R.id.time);

		Bundle bandle = this.getIntent().getExtras();
		String string = bandle.getString("data");

		String[] words = string.split("\t");

		name.setText(words[0]);
		date.setText(words[1]);
		time.setText(words[2]);
		name_unedited = words[0];

		alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Done");
		alertDialog.setMessage("Practical Edited");
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Edit.this, Alarminfo.class);
				intent.putExtra("time", al_message);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}
		});

		alertDialog.setIcon(R.drawable.ok);

		String alert = "date time combination invalid\n alarm set off for this practical";
		alarmAlertDialog = new AlertDialog.Builder(this).create();
		alarmAlertDialog.setTitle("ERROR");
		alarmAlertDialog.setMessage(alert);
		alarmAlertDialog.setButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				
				Intent intent = getIntent();
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
				// TODO Auto-generated method stub
				
			}
		});
		alarmAlertDialog.setIcon(R.drawable.error);

		//set on touch & forcus
		setOnTime(time);
		setOnDate(date);
		//buton setToast
		buttonToast(ok, "     Ok");
		buttonToast(cancel, "  Cancel");

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		int sdkver = Build.VERSION.SDK_INT;
		switch (id) {

		case ID_TIMEPICKER:
						
			if(sdkver>=16){
				TimePickerDialog myTime = new TimePickerDialog(this,
						myTimeSetListener, myHour, myMinute, hview);
				myTime.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								if (which == DialogInterface.BUTTON_POSITIVE) {
									timechange = true;
								}
							}
						});
				myTime.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								if (which == DialogInterface.BUTTON_NEGATIVE) {
									timechange = false;
									return;
								}
							}
						});
				return myTime;

			}
			else{
				return new TimePickerDialog(this, myTimeSetListener, myHour,
				 myMinute, false);
			}
			
		case ID_DATEPICKER:
			
			if(sdkver>=16){
				DatePickerDialog myDate = new DatePickerDialog(this,
						myDateSetListener, myYear, myMonth, myDay);
				myDate.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								if (which == DialogInterface.BUTTON_POSITIVE) {
									datechange = true;
								}
							}
						});
				myDate.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								if (which == DialogInterface.BUTTON_NEGATIVE) {
									datechange = false;
									return;
								}
							}
						});
				return myDate;
			}
			else{
				return new DatePickerDialog(this, myDateSetListener, myYear,
						myMonth, myDay);
			}

		default:
			return null;
		}
	}

	private TimePickerDialog.OnTimeSetListener myTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			int sdkver = Build.VERSION.SDK_INT;
			new_time = String.valueOf(hourOfDay) + ":" + String.valueOf(minute);
			
			if (sdkver>=16) {
				if(timechange){
					Edit.this.time.setText(new_time);
				}
				
			}
			else{
				Edit.this.time.setText(new_time);
			}
		}
	};

	private DatePickerDialog.OnDateSetListener myDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			int sdkver = Build.VERSION.SDK_INT;
			String date = String.valueOf(dayOfMonth) + "/"
					+ String.valueOf(monthOfYear + 1) + "/"
					+ String.valueOf(year);
			if (sdkver>=16) {
				if(datechange){
					Edit.this.date.setText(date);
				}
				
			}
			else{
				Edit.this.date.setText(date);
			}
		}

	};
	private PendingIntent pendingAlarmIntent;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_edit, menu);
		return true;
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.menu_settings:
			Intent prefe = new Intent(this, Pref.class);
			startActivity(prefe);			
			break;		
		}		
		return false;
	}

	public void onClick(View view) {

		switch (view.getId()) {

		case R.id.edit:

			if (name.getText().length() == 0) {
				toastMaker("Enter valid name", (int) name.getX(),
						(int) name.getY());
				return;
			} else {
				str_name = name.getText().toString();
			}
			if (date.getText().length() == 0) {
				toastMaker("Enter valid date", (int) name.getX(),
						(int) name.getY());
				return;
			} else {
				str_date = date.getText().toString();
			}
			if (time.getText().length() == 0) {
				toastMaker("Enter valid time", (int) name.getX(),
						(int) name.getY());
				return;
			} else {
				str_time = time.getText().toString();
			}

			Practical pra = new Practical();

			pra.setDate(str_date);
			pra.setName(str_name);
			pra.setTime(str_time);

			database.editPractical(pra, name_unedited);

			alertDialog.show();

			long id = database.getPracticalId(str_name);
			Intent alarmIntent = new Intent(Edit.this, MyAlarmService.class);
			
			pendingAlarmIntent = PendingIntent.getService(Edit.this, (int) id,
					alarmIntent, 0);

			String[] date_ar = date.getText().toString().split("/");
			String[] time_sr = time.getText().toString().split(":");

			AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

			Calendar AlarmCal = Calendar.getInstance();
			AlarmCal.clear();
			AlarmCal.setTimeInMillis(System.currentTimeMillis());
			AlarmCal.set(Calendar.YEAR, Integer.parseInt(date_ar[2]));
			AlarmCal.set(Calendar.MONTH, (Integer.parseInt(date_ar[1]) - 1));
			AlarmCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date_ar[0]));
			AlarmCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time_sr[0])); // set
																				// user
																				// selection
			AlarmCal.set(Calendar.MINUTE, Integer.parseInt(time_sr[1])); // set
																			// user
																			// selection
			AlarmCal.set(Calendar.SECOND, 0);
			long now = System.currentTimeMillis()/1000;
			long future = AlarmCal.getTimeInMillis()/1000;
			if (future > now) {

				alarmManager.set(AlarmManager.RTC_WAKEUP, AlarmCal.getTimeInMillis(), pendingAlarmIntent);
				
				String alarm = String.valueOf(AlarmCal.get(Calendar.YEAR))
						+ ":"
						+ String.valueOf(AlarmCal.get(Calendar.MONTH))
						+ ":"
						+ String.valueOf(AlarmCal.get(Calendar.DAY_OF_MONTH)
								+ ":"
								+ String.valueOf(AlarmCal
										.get(Calendar.HOUR_OF_DAY)) + ":"
								+ String.valueOf(AlarmCal.get(Calendar.MINUTE)));
				String timerem = String
						.valueOf((AlarmCal.getTimeInMillis() - System
								.currentTimeMillis()) / 1000);
				//String timenow = String
						//.valueOf((System.currentTimeMillis() / 1000));

				al_message = "Alarm at " + alarm;

			} else {
				alarmAlertDialog.show();
			}
			break;

		case R.id.cancel:
			Intent intent = new Intent(Edit.this, Welcom.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		}
	}

	// make the toast
	public void toastMaker(String message, int x, int y) {
		toast_text.setText(message);
		toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.TOP | Gravity.LEFT, x, y);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(toast_layout);
		toast.show();
	}

	// hide keyboard
	public void hideKeyboard(EditText text) {
		InputMethodManager hide_keybord = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		hide_keybord.hideSoftInputFromWindow(text.getWindowToken(), 0);
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
	//set on touch
		public void setOnTime(final EditText text){
			text.setOnTouchListener(new View.OnTouchListener() {

				@SuppressWarnings("deprecation")
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					hideKeyboard(text);
					final Calendar c = Calendar.getInstance();
					myHour = c.get(Calendar.HOUR_OF_DAY);
					myMinute = c.get(Calendar.MINUTE);
					showDialog(ID_TIMEPICKER);
					return false;
				}
			});

			text.setOnFocusChangeListener(new View.OnFocusChangeListener() {

				@SuppressWarnings("deprecation")
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						hideKeyboard(text);
						final Calendar c = Calendar.getInstance();
						myHour = c.get(Calendar.HOUR_OF_DAY);
						myMinute = c.get(Calendar.MINUTE);
						showDialog(ID_TIMEPICKER);
					}
				}
			});
		}
		
		public void setOnDate(final EditText text){
			text.setOnTouchListener(new View.OnTouchListener() {

				@SuppressWarnings("deprecation")
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					hideKeyboard(text);
					final Calendar c = Calendar.getInstance();
					myYear = c.get(Calendar.YEAR);
					myMonth = c.get(Calendar.MONTH);
					myDay = c.get(Calendar.DAY_OF_MONTH);
					showDialog(ID_DATEPICKER);
					return false;
				}
			});

			text.setOnFocusChangeListener(new View.OnFocusChangeListener() {

				@SuppressWarnings("deprecation")
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						hideKeyboard(text);
						final Calendar c = Calendar.getInstance();
						myYear = c.get(Calendar.YEAR);
						myMonth = c.get(Calendar.MONTH);
						myDay = c.get(Calendar.DAY_OF_MONTH);
						showDialog(ID_DATEPICKER);
					}
				}
			});
		}

}
