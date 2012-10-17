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

/*
 * this class handles all the methods which will need to deal
 * with the database
 */

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHandler extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 5; //database version 
	
	private static final String DATABASE_NAME = "Practical_data";	//database name
	
	private static final String TABLE_CONTACTS = "Practicals"; //table name
	
	 private static final String KEY_ID = "_id";	//column id holds the index of the row
	 private static final String KEY_NAME = "name";	//column name holds the name of the practical
	public static final String KEY_DATE = "date";	//holds the date
	 private static final String KEY_TIME = "time";	//holds the time
	 private static final String [] ALL_COLUMNS = { KEY_ID, KEY_NAME, KEY_DATE, KEY_TIME }; //all columns
	 
	 //constructor make the database
	 public DataBaseHandler(Context context) {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    }
	 
	 
	 //make the table 
	 @Override
	    public void onCreate(SQLiteDatabase db) {
	        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
	                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT, " + KEY_DATE + " TEXT, " +KEY_TIME +" TEXT" + ")";
	        db.execSQL(CREATE_CONTACTS_TABLE);
	    }
	 //upgrade the database  older tables will be droped 
	 @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        // Drop older table if existed
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
	 
	        // Create tables again
	        onCreate(db);
	    }
	 
	 
	 /*
	  * this method to add new practical for the table
	  * this get input a object of the Practical class 
	  * returns nothing.
	  */
	public void addPractical(Practical practical) {
	        SQLiteDatabase db = this.getWritableDatabase(); //writable data base to write the data
	 
	        ContentValues values = new ContentValues(); 	//contentvalues object to hold the data while inserting 
	        values.put(KEY_NAME, practical.getName()); 	
	        values.put(KEY_DATE, practical.getDate() );
	        values.put(KEY_TIME, practical.getTime());
	        
	 
	       
	        db.insert(TABLE_CONTACTS, null, values); 	 // Inserting Row
	        db.close(); 	// Closing database connection
	    }
	 
	/*
	 * this method is to get all the practicals in the table
	 * this get inputs nothing 
	 * returns list of a practical objects 
	 */
	 public List<Practical> getAllPractical(){
		
		 SQLiteDatabase db = this.getReadableDatabase(); //readable database to read the data
		 
		 List<Practical> practicals = new ArrayList<Practical>();
		 
		 Cursor cursor = db.query(TABLE_CONTACTS, ALL_COLUMNS, null, null, null, null, null);	//query returns a cursor object
		 cursor.moveToFirst();
		 
		 //adding data to the List from the cursor object
		 while(!cursor.isAfterLast()){
			 Practical practical = cursortopractical(cursor);
			 practicals.add(practical);
			 
			 cursor.moveToNext(); 
		 }
		 
		 cursor.close(); //closing the cursor
		 return  practicals;
		  
	 }
	 
	 /*
	  * this method will return practicals on a selected date
	  * this will get the date, a String object as its input
	  * return list of the practicals
	  */
	 public List<Practical> getTodayPracticals( String date ){
		 
		 SQLiteDatabase db = this.getReadableDatabase(); //getting readable data base
		 
		 List<Practical> practicals = new ArrayList<Practical>();
		 
		 //raw query will returns the practicals which has the selected date to a cursor object
		 Cursor cursor = db.rawQuery(" select * from " +TABLE_CONTACTS+ "  where "+ KEY_DATE+ " = ? ", new String[] {date});
		 cursor.moveToFirst();
		 
		 //adding data to list from cursor
		 while(!cursor.isAfterLast()){
			 Practical practical = cursortopractical(cursor);
			 practicals.add(practical);
			 cursor.moveToNext();
		 }
		 
		 cursor.close();	//closing the cursor
		 return practicals;
		
	 }
	 
	 /*
	  * this method to get the count of the practicals
	  */
	 public int getPracticalCount(){
		 
		 int count =0;
		 SQLiteDatabase db = this.getReadableDatabase();	//readable database
		 
		 Cursor cursor = db.query(TABLE_CONTACTS, ALL_COLUMNS, null, null, null, null, null); //query
		 cursor.moveToFirst();
		 
		 //counting practicals in the cursor object
		 while(!cursor.isAfterLast()){
			 count = count+1;
			 cursor.moveToNext();
		 }
		 cursor.close();	//closing the cursor
		 return count;
	 }
	 
	 /*
	  * this method will delete a practical from the table
	  * this will get the practical object which should delete as its input
	  * returns nothing
	  */
 public void deletePractical(Practical practical, String name){
		 
		 SQLiteDatabase db1 = this.getReadableDatabase();
		 Practical practical1 = new Practical();
		 
		 Cursor cursor1 = db1.rawQuery(" select * from "+ TABLE_CONTACTS + " where "+ KEY_NAME + " = ? " , new String[] {name});
		cursor1.moveToFirst();
		 while(!cursor1.isAfterLast()){
		 practical1 = cursortopractical(cursor1);
		 cursor1.moveToNext();
		 
		 }
		 
			long id = practical1.getId();
			
			SQLiteDatabase db= this.getWritableDatabase();
			db.delete(TABLE_CONTACTS, KEY_ID + " = "+ id, null);
			
			
		}
 
 public long getPracticalId(String name){
	 SQLiteDatabase db = this.getReadableDatabase();
	 Practical practical = new Practical();
	 
	 Cursor cursor = db.rawQuery(" select * from "+ TABLE_CONTACTS + " where "+ KEY_NAME + " = ? " , new String[] {name});
	 cursor.moveToFirst();
	 
	 while(!cursor.isAfterLast()){
		 practical = cursortopractical(cursor);
		 cursor.moveToNext();
	 }
	 
	 return practical.getId();
 }
 
	 
	 //this is to open the database 
	 public void open() throws SQLException {
	      SQLiteDatabase db = this.getWritableDatabase();
	  }
	 /*
	  * this method will edit a given practical
	  * 
	  */
	 public void editPractical(Practical practical, String name) {
		 SQLiteDatabase dbs = this.getReadableDatabase();
		 Practical practical1 = new Practical();
		 
		 Cursor cursor1 = dbs.rawQuery(" select * from "+ TABLE_CONTACTS+ " where "+ KEY_NAME+ " = ? ", new String[] {name});
		 cursor1.moveToFirst();
		 
		 practical1 = cursortopractical(cursor1);
		 	long id = practical1.getId();
		 
	        SQLiteDatabase db = this.getWritableDatabase();
	 
	        ContentValues values = new ContentValues();
	        values.put(KEY_NAME, practical.getName()); // Contact Name
	        values.put(KEY_DATE, practical.getDate() );
	        values.put(KEY_TIME, practical.getTime());
	        
	        db.update(TABLE_CONTACTS, values,  KEY_ID+" = " + id, null); //updating
	    }
	 public boolean cheakPractical(Practical practical){
		 boolean retval = false;
		
		 String name = practical.getName();
		 SQLiteDatabase db = this.getReadableDatabase();
		 
		 Cursor cursor1 = db.rawQuery(" select * from "+ TABLE_CONTACTS + " where "+ KEY_NAME + " = ? " , new String[] {name});
		
		 if(cursor1.getCount()==0){
			 retval = false;
		 }
		 else{
			 retval = true;
		 }
		 
		return retval ;
		 
	 }
	 
	 
	 /*
	  * this method is to convert data in the cursor object 
	  * to a practical object
	  * this will get a cursor object as its input 
	  * return a practical object 
	  */
	 private Practical cursortopractical(Cursor cursor){
		 Practical practical = new Practical();
		 
		practical.setId(cursor.getLong(0)); 
		practical.setName(cursor.getString(1));
		practical.setDate(cursor.getString(2));
		practical.setTime(cursor.getString(3));
		 
		 return practical;
	 }
	 
	 

}
