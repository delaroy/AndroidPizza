package com.delaroystudios.pizza;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import static android.provider.BaseColumns._ID;
import static com.delaroystudios.pizza.Constants.CRUST;
import static com.delaroystudios.pizza.Constants.SIZE;
import static com.delaroystudios.pizza.Constants.TABLE_NAME;
import static com.delaroystudios.pizza.Constants.TOPPINGS_LEFT;
import static com.delaroystudios.pizza.Constants.TOPPINGS_RIGHT;
import static com.delaroystudios.pizza.Constants.TOPPINGS_WHOLE;



public class PizzaData extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "pizza.db";
	private static final int DATABASE_VERSION = 4;
	
	public PizzaData(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + SIZE
				+ " TEXT NOT NULL, " + CRUST + " TEXT NOT NULL, " + TOPPINGS_WHOLE + " TEXT, " + TOPPINGS_LEFT + 
				" TEXT, " +  TOPPINGS_RIGHT + " TEXT);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
	

   
}
