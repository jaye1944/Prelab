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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class Practical_Adapter extends ArrayAdapter<Practical> {
	
	 Context context; 
	 int layoutResourceId; 
	 Practical [] practical = null;
	 
	
	 
	 public Practical_Adapter(Context context, int layoutResourceID, Practical [] practical){
		 super(context, layoutResourceID, practical);
		 this.layoutResourceId = layoutResourceID;
		 this.context = context;
		 this.practical = practical;
	 }
	 
	 @Override
	 public View getView(int position, View convertView, ViewGroup parent){
		 
		 View row = convertView;
		 PracticalHolder holder = null;
		 
		 if(row == null){
			 LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			 row = inflater.inflate(layoutResourceId, parent, false);
			 holder = new PracticalHolder();
			 holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
			 
			 row.setTag(holder);
		 }
		 
		 else{
			 holder = (PracticalHolder)row.getTag();
		 }
		 
		 Practical practical1 = practical[position];
		 holder.txtTitle.setText(practical1.toString());
		 
		 return row;
		 
	 }
	 
	 
	 
	 
	 static class PracticalHolder
	    {
	       
	        TextView txtTitle;
	    }

}
