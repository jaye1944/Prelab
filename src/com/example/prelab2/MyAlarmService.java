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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MyAlarmService extends Service {
	
	 private PendingIntent pendingAlarmIntent;
	 private DataBaseHandler database;
	 private String day;
		private String month;
		private String year;

		private String formattedDate;


	@Override
	   public void onCreate() {
	    //  Toast.makeText(this, "MyAlarmService.onCreate()", Toast.LENGTH_LONG).show();
	     
	   }
	 
	   @Override
	   public IBinder onBind(Intent intent) {
	   return null;
	   }
	 
	   @Override
	   public void onDestroy() {
	   super.onDestroy();
	   Toast.makeText(this, "MyAlarmService.onDestroy()", Toast.LENGTH_LONG).show();
	   }
	 
	   @SuppressWarnings("deprecation")
	@Override
	   public void onStart(Intent intent, int startId) {
	      super.onStart(intent, startId);
	      
	      PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
	         PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "My Tag");
	         wl.acquire();
	     
	      
	      database = new DataBaseHandler(this);
			database.open();
			Calendar cal = Calendar.getInstance();
			year = String.valueOf(cal.get(Calendar.YEAR));
			month = String.valueOf(cal.get(Calendar.MONTH) + 1);
			day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));

			formattedDate = day + "/" + month + "/" + year;
			
			List<Practical> practicals = database.getTodayPracticals(formattedDate);
			
			if(practicals.isEmpty()){
				System.exit(0);
			}
			else{
				 Intent alertIntent = new Intent();
			      alertIntent.setClass( this , MyAlert.class );
			      
			      alertIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			      startActivity( alertIntent );
			}
	   }

}
