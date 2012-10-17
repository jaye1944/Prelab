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
 * the object which holds the data of a row in the data bse
 */

package com.example.prelab2;

public class Practical {
	
	private long id;		//index number of the object
	private String name;	//name of the practical
	private String date;	//date of the practical
	private String time;	//time of the practical
	
	
	//empty constructor 
	public Practical(){
		
	}
	
	//constructor with all data
	public Practical(long id, String name, String date, String time){
		this.id = id;
		this.name = name;
		this.date = date;
		this.time = time;
	}
	
	//constructor with out id
	public Practical(String name, String date, String time){
		this.name = name;
		this.date = date;
		this.time = time;
	}
	
	public long getId(){
		return this.id;
	}
	
	public void setId(long id){
		this.id = id;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getDate(){
		return this.date;
	}
	
	public void setDate(String date){
		this.date = date;
	}
	
	public String getTime(){
		return this.time;
	}
	
	public void setTime(String time){
		this.time = time;
	}
	
	@Override
	public String toString(){
		String string ;
		string = this.name +"\t"+this.date+"\t"+this.time;
		return string;
	}

}
