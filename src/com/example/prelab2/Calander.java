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

import java.sql.Date;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;

public class Calander extends Activity {
	private CalendarView calendar;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calander);
        calendar = (CalendarView)findViewById(R.id.calendarView1);
        calendar.setOnClickListener(_OnClickLIstner);
        calendar.setOnDateChangeListener(_OnDateChangeListener);
       
    }
   
    private OnClickListener _OnClickLIstner = new OnClickListener(){
		public void onClick(View view){
			Date  date = new Date(calendar.getDate());
			
			Intent intent = new Intent(Calander.this, com.example.prelab2.Today.class);
			
			intent.putExtra("data",
	                   new int[] {date.getDay(),date.getMonth(),date.getYear()});
			startActivity(intent);}};
		
	
	
	private OnDateChangeListener _OnDateChangeListener = new OnDateChangeListener(){

		public void onSelectedDayChange(CalendarView view, int year, int month,
				int dayOfMonth) {
			
			Intent intent = new Intent(Calander.this, com.example.prelab2.Today.class);
			intent.putExtra("data", new int[] {dayOfMonth, month, year});
			
			startActivity(intent);
		}
		
	};

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}
	

	
}
