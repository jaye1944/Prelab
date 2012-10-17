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

/*
 * this java class to add new practical
 */

package com.example.prelab2;

import java.util.Calendar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
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
import android.content.DialogInterface;

public class Addnew extends Activity {

	protected static final int STATIC_INTEGER_VALUE = 0;
	protected static final int STATIC_INTEGER_VALUE1 = 1;
	private EditText date;
	private EditText name;
	private EditText time;

	private String str_name;
	private String str_date;
	private String str_time;

	private DataBaseHandler database;
	private String fulldate;

	private AlertDialog alertDialog;
	private PendingIntent pendingAlarmIntent;

	private String al_message;

	private boolean hview;
	private boolean timechange = false;
	private boolean datechange = false;

	private Toast toast;
	private LayoutInflater toast_inflater;
	private View toast_layout;
	private ImageView toast_image;
	private TextView toast_text;

	private int myYear, myMonth, myDay, myHour, myMinute;
	static final int ID_TIMEPICKER = 1;
	static final int ID_DATEPICKER = 2;

	protected static int HELLO_ID = 1;

	public static Context context;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addnew);
		Addnew.context = getApplicationContext();
		// forcus buttons
		final Button save = (Button) findViewById(R.id.new_practical);
		final Button search = (Button) findViewById(R.id.browse);
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

		alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("DONE");
		alertDialog.setMessage("Practical added");
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Addnew.this, Alarminfo.class);
				intent.putExtra("time", al_message);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}
		});
		alertDialog.setIcon(R.drawable.ok);

		date = (EditText) findViewById(R.id.editText1);
		name = (EditText) findViewById(R.id.editText2);
		time = (EditText) findViewById(R.id.editText3);
		database = new DataBaseHandler(this);
		database.open();
		Bundle extras = this.getIntent().getExtras();
		fulldate = extras.getString("data");
		date.setText(fulldate);

		// set on touch & forcus
		setOnTime(time);
		setOnDate(date);

		// making forcus toasts button name
		buttonToast(save, "   Save");
		buttonToast(search, "Search it now");
		buttonToast(cancel, "  Cancel");

	}

	// due to sdk verson the dialog option must be change
	@Override
	protected Dialog onCreateDialog(int id) {
		int sdkver = Build.VERSION.SDK_INT;
		switch (id) {

		case ID_TIMEPICKER:
			if (sdkver >= 16) {
				TimePickerDialog myTime = new TimePickerDialog(this,
						myTimeSetListener, myHour, myMinute, hview);
				myTime.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								if (which == DialogInterface.BUTTON_POSITIVE) {
									timechange = true;
								}
							}
						});
				myTime.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								if (which == DialogInterface.BUTTON_NEGATIVE) {
									timechange = false;
									return;
								}
							}
						});
				return myTime;
			} else {
				return new TimePickerDialog(this, myTimeSetListener, myHour,
						myMinute, hview);
			}

		case ID_DATEPICKER:
			if (sdkver >= 16) {
				DatePickerDialog myDate = new DatePickerDialog(this,
						myDateSetListener, myYear, myMonth, myDay);
				myDate.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								if (which == DialogInterface.BUTTON_POSITIVE) {
									datechange = true;
								}
							}
						});
				myDate.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								if (which == DialogInterface.BUTTON_NEGATIVE) {
									datechange = false;
									return;
								}
							}
						});
				return myDate;
			} else {
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
			String time = String.valueOf(hourOfDay) + ":"
					+ String.valueOf(minute);
			if (sdkver >= 16) {
				if (timechange) {
					Addnew.this.time.setText(time);
				}
			} else {
				Addnew.this.time.setText(time);
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

			if (sdkver >= 16) {
				if (datechange) {
					Addnew.this.date.setText(date);
				}
			} else {
				Addnew.this.date.setText(date);
			}
		}

	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent prefs = new Intent(Addnew.this, Pref.class);
			prefs.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(prefs);
			break;
		}
		return false;
	}

	public void onClick(View view) {

		int year, month, day, hour, minute;
		year = myYear;
		month = myMonth;
		day = myDay;
		hour = myHour;
		minute = myMinute;

		switch (view.getId()) {

		case R.id.new_practical:

			if (name.getText().length() == 0) {
				toastMaker("Enter valid name", (int) name.getX(),
						(int) name.getY());
				return;
			} else {
				// name
				str_name = name.getText().toString();
			}

			if (time.getText().length() == 0) {
				toastMaker("Enter valid time", (int) time.getX(),
						(int) time.getY());
				return;
			} else {
				// time
				str_time = time.getText().toString();
			}
			// date
			str_date = date.getText().toString();

			String[] date_test = date.getText().toString().split("/");
			String[] time_test = time.getText().toString().split(":");
			Calendar AlarmCaltest = Calendar.getInstance();
			AlarmCaltest.clear();
			AlarmCaltest.setTimeInMillis(System.currentTimeMillis());
			AlarmCaltest.set(Calendar.YEAR, Integer.parseInt(date_test[2]));
			AlarmCaltest.set(Calendar.MONTH,
					(Integer.parseInt(date_test[1]) - 1));
			AlarmCaltest.set(Calendar.DAY_OF_MONTH,
					Integer.parseInt(date_test[0]));
			AlarmCaltest.set(Calendar.HOUR_OF_DAY,
					Integer.parseInt(time_test[0]));
			AlarmCaltest.set(Calendar.MINUTE, Integer.parseInt(time_test[1])); // set
			// user
			// selection
			AlarmCaltest.set(Calendar.SECOND, 0);
			long now = System.currentTimeMillis() / 1000;
			long future = AlarmCaltest.getTimeInMillis() / 1000;

			if (future < now) {
				Alert("Date or Time Combination invalid");
			} else {
				Practical pra = new Practical();
				pra.setDate(str_date);
				pra.setName(str_name);
				pra.setTime(str_time);

				boolean bool = database.cheakPractical(pra);

				if (bool == false) {
					database.addPractical(pra);
					long id = database.getPracticalId(str_name);
					Intent alarmIntent = new Intent(Addnew.this,
							MyAlarmService.class);

					alarmIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

					pendingAlarmIntent = PendingIntent.getService(Addnew.this,
							(int) id, alarmIntent, 0);

					String[] date_ar = date.getText().toString().split("/");
					String[] time_sr = time.getText().toString().split(":");

					AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
					// set the alarm
					alarmManager.set(AlarmManager.RTC_WAKEUP,
							AlarmCaltest.getTimeInMillis(), pendingAlarmIntent);

					String get = String.valueOf(year) + ":"
							+ String.valueOf(month + 1) + ":"
							+ String.valueOf(day) + ":" + String.valueOf(hour)
							+ ":" + String.valueOf(minute);
					String alarm = String.valueOf(AlarmCaltest
							.get(Calendar.YEAR))
							+ ":"
							+ String.valueOf(AlarmCaltest.get(Calendar.MONTH))
							+ ":"
							+ String.valueOf(AlarmCaltest
									.get(Calendar.DAY_OF_MONTH)
									+ ":"
									+ String.valueOf(AlarmCaltest
											.get(Calendar.HOUR_OF_DAY))
									+ ":"
									+ String.valueOf(AlarmCaltest
											.get(Calendar.MINUTE)));

					// Alarm toast
					al_message = "Alarm at " + alarm + "\n" + get;
					alertDialog.show();

				} else {
					Alert("Praticle is already entered");
				}
			}
			break;

		case R.id.browse:
			try {
				str_name = name.getText().toString();
				if (str_name.length() == 0) {
					toastMaker("Enter valid name", (int) name.getX(),
							(int) name.getY());
					return;
				} else {
					Intent next = new Intent(Addnew.this, Brows.class);
					next.putExtra("search_word", str_name);
					next.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(next);
				}
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
			break;
		case R.id.cancel:
			Intent intent1 = new Intent(Addnew.this, Welcom.class);
			intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent1);
			finish();
			break;
		}

	}

	public void setDate() {
		date.setText("test");
	}

	// make he toast
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

	public static Context getActivityContext() {
		return Addnew.context;
	}

	// button toast
	public void buttonToast(final Button bb, final String message) {
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

	// Alert
	@SuppressWarnings("deprecation")
	public void Alert(String message) {
		AlertDialog alertDialogshow = new AlertDialog.Builder(this).create();
		alertDialogshow.setTitle("ERROR");
		alertDialogshow.setMessage(message);
		alertDialogshow.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});
		alertDialogshow.setIcon(R.drawable.error);
		alertDialogshow.show();
	}

	// set on touch
	public void setOnTime(final EditText text) {
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

	public void setOnDate(final EditText text) {
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
