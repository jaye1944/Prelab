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
import java.util.List;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MyAlert extends Activity {

	private String day;
	private String month;
	private String year;

	private String formattedDate;

	private DataBaseHandler database;
	private ListView list;

	private Vibrator v;
	private MediaPlayer al;// clipmusic
	private Uri rtone;// ringtone
	private Ringtone rt;// rtone

	private String what;
	private String tone;

	boolean stop = true;

	private String[] menuItems;

	private Toast toast;
	private AlertDialog deletConferm;

	PowerManager pm;
	KeyguardManager keyguardManager;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// remove keyguard on the starting (sleep mode)
		keyguardManager = (KeyguardManager) getApplicationContext()
				.getSystemService(Context.KEYGUARD_SERVICE);
		KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
		keyguardLock.disableKeyguard();
		// load the content view
		try {
			setContentView(R.layout.alertlayout);
		} catch (Exception e) {
			e.printStackTrace();
		}
		final Button exit = (Button) findViewById(R.id.buttonexits);
		final Button gotocalendar = (Button) findViewById(R.id.buttongotocal);
		// get preferences
		SharedPreferences getprefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		what = getprefs.getString("vlist", "1");
		tone = getprefs.getString("alarm", "1");
		// vibrator enable due to prefs
		if (what.contentEquals("2")) {
			v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			setNormal();
		} else if (what.contentEquals("3")) {
			v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			setStyle();
		}

		// tone enable prefs
		if (tone.contentEquals("2")) {
			rtone = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			rt = RingtoneManager.getRingtone(getApplicationContext(), rtone);
			rt.play();
		} else if (tone.contentEquals("3")) {
			al = MediaPlayer.create(MyAlert.this, R.raw.alarm1);
			al.start();
			al.setLooping(true);
		} else if (tone.contentEquals("4")) {
			al = MediaPlayer.create(getApplicationContext(), R.raw.alarm2);
			al.start();
			al.setLooping(true);
		}
		// create list
		list = (ListView) findViewById(R.id.alert_list);
		database = new DataBaseHandler(this);
		database.open();

		Calendar cal = Calendar.getInstance();
		year = String.valueOf(cal.get(Calendar.YEAR));
		month = String.valueOf(cal.get(Calendar.MONTH) + 1);
		day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));

		formattedDate = day + "/" + month + "/" + year;

		List<Practical> practicals = database.getTodayPracticals(formattedDate);
		//load particles
		ArrayAdapter<Practical> adapter = new ArrayAdapter<Practical>(this,
				R.layout.list_view_item, R.id.txtTitle, practicals);
		// set the label particle
		View header = (View) getLayoutInflater().inflate(
				R.layout.list_view_header, null);
		list.addHeaderView(header, null, false);
		list.setAdapter(adapter);
		registerForContextMenu(list);

		list.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// stop vibrate & tone
				stop = false;
				alarmStop(tone);
				if (!(what.contentEquals("1"))) {
					stop();
				}
				view.showContextMenu();
			}
		});
		// set forcus buttons
		buttonToast(exit, "   Exit");
		buttonToast(gotocalendar, "Go to Calendar");

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		Object object = list.getItemAtPosition(info.position);
		String str = String.valueOf(object);
		menu.setHeaderTitle(str);
		menuItems = new String[] { "Edit", "Delete", "Search" };
		menu.add(Menu.NONE, 0, 0, menuItems[0]);
		menu.add(Menu.NONE, 1, 1, menuItems[1]);
		menu.add(Menu.NONE, 2, 2, menuItems[2]);

	}

	public boolean onContextItemSelected(MenuItem item) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		Object object = list.getItemAtPosition(info.position);
		final String str = String.valueOf(object);
		int menuItemIndex = item.getItemId();
		menuItems = new String[] { "Edit", "Delete", "Search" };
		String menuItemName = menuItems[menuItemIndex];
		
		if (menuItemName == "Edit") {
			Intent intent = new Intent(MyAlert.this, Edit.class);
			intent.putExtra("data", str);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();

		} else if (menuItemName == "Delete") {
			deletConferm = new AlertDialog.Builder(this).create();
			deletConferm.setTitle("Are You Sure");
			deletConferm.setMessage("Delete and go to welcom page");
			deletConferm.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
					new DialogInterface.OnClickListener() {

						private PendingIntent pendingAlarmIntent;

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String[] str_ary = str.split("\t");
							Practical practical = new Practical(str_ary[0],
									str_ary[1], str_ary[2]);

							long id = database.getPracticalId(str_ary[0]);
							Intent alarmIntent = new Intent(MyAlert.this,
									MyAlarmService.class);

							// alarmIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

							alarmIntent.putExtra("nel.example.alarms1",
									"My message");
							pendingAlarmIntent = PendingIntent.getService(
									Addnew.getActivityContext(), (int) id,
									alarmIntent, 0);
							AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

							alarmManager.cancel(pendingAlarmIntent);
							pendingAlarmIntent.cancel();

							database.deletePractical(practical, str_ary[0]);
							try {
								Intent intent = new Intent(MyAlert.this,
										Welcom.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);
								finish();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
			deletConferm.setButton(DialogInterface.BUTTON_NEGATIVE, "NO",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

						}
					});
			deletConferm.setIcon(R.drawable.error);
			deletConferm.show();

		} else if (menuItemName == "Search") {
			String[] word_arry = str.split("\t");
			Intent intent = new Intent(MyAlert.this, Brows.class);
			intent.putExtra("search_word", word_arry[0]);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		}
		return true;
	}

	// handle buttons
	public void onClick(View view) {

		switch (view.getId()) {

		case R.id.buttongotocal:
			if (stop) {
				if (!(what.contentEquals("1"))) {
					stop();
				}
			}
			alarmStop(tone);
			Intent intent = new Intent(MyAlert.this, Calander.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;

		case R.id.buttonexits:
			if (stop) {
				if (!(what.contentEquals("1"))) {
					stop();
				}
			}
			alarmStop(tone);
			Intent intentfinish = new Intent(MyAlert.this, Welcom.class);
			intentfinish.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intentfinish);
			finish();
			break;
		}
	}

	// v normal
	public void setNormal() {
		long[] pattern = { 0, 200, 500 };
		v.vibrate(pattern, 0);
	}

	// v style
	public void setStyle() {
		int dot = 200; // Length of a Morse Code "dot" in milliseconds
		int dash = 500; // Length of a Morse Code "dash" in milliseconds
		int short_gap = 200; // Length of Gap Between dots/dashes
		int medium_gap = 500; // Length of Gap Between Letters
		int long_gap = 1000; // Length of Gap Between Words
		long[] pattern = { 0, // Start immediately
				dot, short_gap, dot, short_gap, dot, // s
				medium_gap, dash, short_gap, dash, short_gap, dash, // o
				medium_gap, dot, short_gap, dot, short_gap, dot, // s
				long_gap };
		v.vibrate(pattern, 0);
	}

	// v stop

	public void stop() {
		v.cancel();
	}

	// alarm stop if it enable
	public void alarmStop(String tone) {
		if (tone.contentEquals("2")) {
			rt.stop();
		} else if (tone.contains("3")) {
			al.stop();
		} else if (tone.contains("4")) {
			al.stop();
		}
	}

	// button toast making due to their name
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

	// handle menu & back key down events
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			stopService();
			return false;
		case KeyEvent.KEYCODE_BACK:
			stopService();
			return false;
		}
		return true;
	}

	// stop the vibrator & alarm if they enable
	public void stopService() {
		if (stop) {
			if (!(what.contentEquals("1"))) {
				stop();
			}
			alarmStop(tone);
		}
		finish();
	}
}
