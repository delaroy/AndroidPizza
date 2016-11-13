package com.delaroystudios.pizza;

import static com.delaroystudios.pizza.Constants.CRUST;
import static com.delaroystudios.pizza.Constants.SIZE;
import static com.delaroystudios.pizza.Constants.TABLE_NAME;
import static com.delaroystudios.pizza.Constants.TOPPINGS_LEFT;
import static com.delaroystudios.pizza.Constants.TOPPINGS_RIGHT;
import static com.delaroystudios.pizza.Constants.TOPPINGS_WHOLE;
import static android.provider.BaseColumns._ID;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.delaroystudios.pizza.R;

public class OrderPage extends ListActivity implements OnClickListener {
	private PizzaData data;
	private boolean hasShown = false;
	private TextView totalText;
	private Cursor cursorEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_page);
		getListView().setChoiceMode(1);

		getListView().setOnItemClickListener(new OnItemClickListener() {

			public void onNothingSelected(AdapterView arg0) {

			}

			public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3) {
				cursorEdit = (Cursor) getListView().getItemAtPosition(arg2);
				editPizza();
			}

		});

		data = new PizzaData(this);
		try {
			Cursor cursor = getEvents();
			showEvents(cursor);
		} finally {
			data.close();
		}

		// Set up click listeners for all the buttons
		View getNewPizzaButton = findViewById(R.id.new_pizza_button);
		getNewPizzaButton.setOnClickListener(this);
		View getCheckoutButton = findViewById(R.id.checkout_button);
		getCheckoutButton.setOnClickListener(this);
		totalText = (TextView) findViewById(R.id.total);
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			Cursor cursor = getEvents();
			showEvents(cursor);
		} finally {
			data.close();
		}
		if (getListView().getCount() == 0)
			totalText.setText("Current Total: $0.00");
		else
			totalText.setText("Current Total: $" + (getListView().getCount() * 9.99));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.new_pizza_button:
			if (!hasShown) {
				openHowToDialog();
				hasShown = true;
			} else {
				openSizeSelectionDialog();
			}
			break;
		case R.id.checkout_button:
			if (getListView().getCount() != 0)
				checkOutDialog();
			else {
				new AlertDialog.Builder(this).setTitle("Info").setMessage("You must order a pizza, before you can checkout.").setCancelable(false)
						.setNeutralButton("OK", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								openSizeSelectionDialog();
							}
						}).show();
			}
			break;
		// More buttons go here (if any) ...
		}
	}

	private void openHowToDialog() {
		new AlertDialog.Builder(this).setTitle(R.string.how_to_title).setMessage(R.string.how_to_text).setCancelable(false)
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						openSizeSelectionDialog();
					}
				}).show();
	}

	private void openSizeSelectionDialog() {
		new AlertDialog.Builder(this).setTitle(R.string.pizza_size).setItems(R.array.pizza_size, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String size;
				if (which == 0)
					size = "Small";
				else if (which == 1)
					size = "Medium";
				else if (which == 2)
					size = "Large";
				else
					size = "Party";
				openCrustSelectionDialog(size);
			}
		}).show();
	}

	protected void openCrustSelectionDialog(final String size) {
		new AlertDialog.Builder(this).setTitle(R.string.crust_selection).setItems(R.array.pizza_crust, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String crust;
				if (which == 0)
					crust = "Thin";
				else if (which == 1)
					crust = "Thick";
				else if (which == 2)
					crust = "Deepdish";
				else
					crust = "Stuffed";
				addEvent(size, crust, "none", "none", "none");
				startPizzaCreation();
			}
		}).show();
	}

	protected void startPizzaCreation() {
		//		int currentPizza = cursorEdit.getInt(0);
		//		Bundle bundle = new Bundle();
		//		bundle.putInt("ID", currentPizza);

		Intent intent = new Intent(OrderPage.this, NewPizza.class);
		//		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void checkOutDialog() {
		new AlertDialog.Builder(this).setTitle(R.string.checkout_title).setMessage(R.string.checkout_text).setCancelable(false)
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						data.getWritableDatabase().delete(TABLE_NAME, null, null);
						finish();
					}
				}).show();
	}

	private void addEvent(String size, String crust, String toppingsWhole, String toppingsLeft, String toppingsRight) {
		SQLiteDatabase db = data.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(SIZE, size);
		values.put(CRUST, crust);
		values.put(TOPPINGS_WHOLE, toppingsWhole);
		values.put(TOPPINGS_LEFT, toppingsLeft);
		values.put(TOPPINGS_RIGHT, toppingsRight);
		db.insertOrThrow(TABLE_NAME, null, values);
	}

	private static String[] FROM = { SIZE, CRUST, TOPPINGS_WHOLE, TOPPINGS_LEFT, TOPPINGS_RIGHT };
	private static String ORDER_BY = SIZE + " DESC";

	private Cursor getEvents() {
		SQLiteDatabase db = data.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, ORDER_BY);
		startManagingCursor(cursor);
		return cursor;
	}

	private static int[] TO = { R.id.email_url, R.id.comment, R.id.wholePizzaHeader, R.id.leftHalfOnly, R.id.rightHalfOnly };

	private void showEvents(Cursor cursor) {
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.listview, cursor, FROM, TO);
		setListAdapter(adapter);
	}

	private void editPizza() {
		int currentPizza = cursorEdit.getInt(0);
		Bundle bundle = new Bundle();
		bundle.putInt(_ID, currentPizza);

		Intent intent = new Intent(OrderPage.this, NewPizza.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			//			startActivity(new Intent(this, Prefs.class));
			return true;
		case R.id.help:
			new AlertDialog.Builder(this).setTitle(R.string.help_title).setMessage(R.string.edit_order_help).setCancelable(false)
					.setNeutralButton("OK", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).show();
			return true;
		case R.id.exit:
			new AlertDialog.Builder(this).setTitle(R.string.exit).setMessage("Are you sure you want to exit?").setCancelable(true)
					.setNeutralButton("Yes", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							System.exit(0);
						}
					}).setNegativeButton("No", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).show();
			return true;
		}
		return false;
	}

}
