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
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Brows extends Activity implements OnClickListener {
	private Activity activity = this;
	WebView search_brow;
	EditText search;
	String searc;
	private Toast toast;
	boolean fullscreen = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//checking prefs
				SharedPreferences getprefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				fullscreen = getprefs.getBoolean("brows", false);
				if(fullscreen){
					requestWindowFeature(Window.FEATURE_NO_TITLE);
					getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
				}
		this.getWindow().requestFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.small_browser);
		Bundle extras = getIntent().getExtras();
		// get passing search word
		searc = extras.getString("search_word");
		// create browser
		search_brow = (WebView) findViewById(R.id.vbBrowser);
		//make toast
		Context context = getApplicationContext();
		CharSequence text = "Click google icon or Wiki icon to search...";
		int duration = Toast.LENGTH_LONG;
		Toast toast = Toast.makeText(context, text, duration);
		toast.setGravity(Gravity.CENTER_VERTICAL,search_brow.getWidth()/2, search_brow.getHeight()/2);
		toast.show();

		// connect web view with java
		Button google = (Button) findViewById(R.id.gbutton);
		Button wikip = (Button) findViewById(R.id.wbutton);
		final Button foward = (Button) findViewById(R.id.fowabutton);
		final Button back = (Button) findViewById(R.id.backbutton);
		search = (EditText) findViewById(R.id.geturl);
		search.setText(searc);
		// create on click listeners
		google.setOnClickListener(this);
		wikip.setOnClickListener(this);
		foward.setOnClickListener(this);
		back.setOnClickListener(this);
		
		//onforcus
		buttonToast(foward,"Foward");
		buttonToast(back,"Back");
		buttonToast(google,"Google");
		buttonToast(wikip,"Wiki");
		// java script enabled
		search_brow.getSettings().setJavaScriptEnabled(true);
		// fit to the screen
		search_brow.getSettings().setLoadWithOverviewMode(true);
		search_brow.getSettings().setUseWideViewPort(true);
		// zoom support enabled
		search_brow.getSettings().setSupportZoom(true);
		search_brow.getSettings().setBuiltInZoomControls(true);
		// loading screen
		search_brow.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int progress) {
				activity.setTitle("Loading......");
				activity.setProgress(progress * 100);
				if (progress == 100) {
					activity.setTitle(R.string.app_name);
					InputMethodManager hide_keybord = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					hide_keybord.hideSoftInputFromWindow(
							search.getWindowToken(), 0);
				}
			}
		});

		// create override url
		search_brow.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
	}
	//focus

	// click methods
	public void onClick(View what) {
		// TODO Auto-generated method stub
		switch (what.getId()) {
		case R.id.gbutton:
			search_brow.loadUrl("http://www.google.lk/search?q="
					+ search.getText().toString());
			break;
		case R.id.wbutton:
			search_brow.loadUrl("http://en.wikipedia.org/wiki/"
					+ search.getText().toString().replaceAll(" ", "_"));
			break;
		case R.id.fowabutton:
			if (search_brow.canGoForward()) {
				search_brow.goForward();
			}
			break;
		case R.id.backbutton:
			if (search_brow.canGoBack()) {
				search_brow.goBack();
			}
			break;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		// Menu items;
		MenuItem item1, item2, item3;
		item1 = menu.add("Refresh");
		item2 = menu.add("Clear History");
		item3 = menu.add("Exit");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		super.onOptionsItemSelected(item);
		if (item.getTitle() == "Exit") {
			finish();
		}
		if (item.getTitle() == "Refresh") {
			search_brow.reload();
		}
		if (item.getTitle() == "Clear History") {
			search_brow.clearHistory();
		}
		return true;
	}
	
	//button toast
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
    @Override
    protected void onPause() {
        super.onPause();    
        finish();
    }
    
    @Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}
}
