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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class Alarminfo extends Activity {

	String searc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarminfo);

		TextView tt = (TextView) findViewById(R.id.alarmtext);
		Bundle extras = getIntent().getExtras();
		// get passing search word
		searc = extras.getString("time");
		tt.setText(searc);
		tt.setTextSize(20);

		Thread time = new Thread() {
			public void run() {
				try {
					sleep(2500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					Intent intent = new Intent(Alarminfo.this, Welcom.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
				}

			}
		};
		time.start();
	}

	@Override
	protected void onPause() {
		super.onPause(); // To change body of overridden methods use File |
							// Settings | File Templates.
		finish();
	}

}
