package com.delaroystudios.pizza;

import android.provider.BaseColumns;

public interface Constants extends BaseColumns {
	public static final String TABLE_NAME = "pizza";
	
	// Columns in the Events database
	public static final String SIZE = "size";
	public static final String CRUST = "crust";
	public static final String TOPPINGS_WHOLE = "toppingsWhole";
	public static final String TOPPINGS_LEFT = "toppingsLeft";
	public static final String TOPPINGS_RIGHT = "toppingsRight";
}
