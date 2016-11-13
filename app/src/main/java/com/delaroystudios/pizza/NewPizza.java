package com.delaroystudios.pizza;

import static android.provider.BaseColumns._ID;
import static com.delaroystudios.pizza.Constants.SIZE;
import static com.delaroystudios.pizza.Constants.TABLE_NAME;
import static com.delaroystudios.pizza.Constants.TOPPINGS_LEFT;
import static com.delaroystudios.pizza.Constants.TOPPINGS_RIGHT;
import static com.delaroystudios.pizza.Constants.TOPPINGS_WHOLE;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import android.widget.Gallery;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.delaroystudios.pizza.R;

public class NewPizza extends Activity implements OnClickListener {

	private RadioButton wholeRadio;
	private RadioButton leftRadio;
	private TextView wholeText;
	private TextView leftText;
	private TextView rightText;
	private ArrayList<String> wList = new ArrayList<String>();
	private ArrayList<String> lList = new ArrayList<String>();
	private ArrayList<String> rList = new ArrayList<String>();
	private ArrayList<String> topingList = new ArrayList<String>();
	private PizzaData data;
	int id = 999;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_pizza);
		data = new PizzaData(this);
		createToppingList();

		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null && bundle.size() > 0)
			id = bundle.getInt(_ID);

		wholeRadio = (RadioButton) findViewById(R.id.whole);
		leftRadio = (RadioButton) findViewById(R.id.left);
		wholeText = (TextView) findViewById(R.id.whole_text);
		leftText = (TextView) findViewById(R.id.left_text);
		rightText = (TextView) findViewById(R.id.right_text);

		if (id != 999) {
			Cursor pizzaCursor = getPizza();
			if (pizzaCursor.moveToFirst()) {
				wholeText.setText(pizzaCursor.getString(pizzaCursor.getColumnIndex(TOPPINGS_WHOLE)));
				leftText.setText(pizzaCursor.getString(pizzaCursor.getColumnIndex(TOPPINGS_LEFT)));
				rightText.setText(pizzaCursor.getString(pizzaCursor.getColumnIndex(TOPPINGS_RIGHT)));
				String[] wholeToppings = wholeText.getText().toString().split("[,]+");
				setArrayList(wList, wholeToppings);
				String[] leftToppings = leftText.getText().toString().split("[,]+");
				setArrayList(lList, leftToppings);
				String[] rightToppings = rightText.getText().toString().split("[,]+");
				setArrayList(rList, rightToppings);
			}
		}

		Gallery gallery = (Gallery) findViewById(R.id.gallery);
		gallery.setAdapter(new ImageAdapter(this));

		gallery.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("rawtypes")
			public void onItemClick(AdapterView parent, View v, int position, long id) {
				if (wList.isEmpty())
					wList.clear();
				if (lList.isEmpty())
					lList.clear();
				if (rList.isEmpty())
					rList.clear();
				if (wholeRadio.isChecked()) { // Whole Pizza
					if (wList.contains(topingList.get(position))) {
						displayMessage(position, " removed");
						wList.remove(wList.indexOf(topingList.get(position)));
						if (wList.isEmpty())
							wholeText.setText("");
						else
							wholeText.setText(editString(wList));
					} else if (rList.contains(topingList.get(position))) {
						rList.remove(rList.indexOf(topingList.get(position)));
						wList.add(topingList.get(position));
						rightText.setText(editString(rList));
						wholeText.setText(editString(wList));
						displayMessage(position, " added");
					} else if (lList.contains(topingList.get(position))) {
						lList.remove(lList.indexOf(topingList.get(position)));
						wList.add(topingList.get(position));
						leftText.setText(editString(lList));
						wholeText.setText(editString(wList));
						displayMessage(position, " added");
					} else {
						displayMessage(position, " added");
						wList.add(topingList.get(position));
						wholeText.setText(editString(wList));
					}
				} else if (leftRadio.isChecked()) { // Left side of pizza.
					if (lList.contains(topingList.get(position))) {
						displayMessage(position, " removed");
						lList.remove(lList.indexOf(topingList.get(position)));
						if (lList.isEmpty())
							leftText.setText("");
						else
							leftText.setText(editString(lList));
					} else if (rList.contains(topingList.get(position))) { // Checking if the same topping is on the right side of pizza.
						displayMessage(position, " added to whole pizza");
						rList.remove(rList.indexOf(topingList.get(position)));
						rightText.setText(editString(rList));
						wList.add(topingList.get(position));
						wholeText.setText(editString(wList));
					} else if (wList.contains(topingList.get(position))) {
						displayMessage(position, " have already been added to the whole pizza");
					} else {
						displayMessage(position, " added");
						lList.add(topingList.get(position));
						leftText.setText(editString(lList));
					}
				} else { // Right side of pizza.
					if (rList.contains(topingList.get(position))) {
						displayMessage(position, " removed");
						rList.remove(rList.indexOf(topingList.get(position)));
						if (rList.isEmpty())
							rightText.setText("");
						else
							rightText.setText(editString(rList));
					} else if (lList.contains(topingList.get(position))) { // Checking if the same topping is on the left side of pizza.
						displayMessage(position, " added to whole pizza");
						lList.remove(lList.indexOf(topingList.get(position)));
						leftText.setText(editString(lList));
						wList.add(topingList.get(position));
						wholeText.setText(editString(wList));
					} else if (wList.contains(topingList.get(position))) {
						displayMessage(position, " have already been added to the whole pizza");
					} else {
						displayMessage(position, " added");
						rList.add(topingList.get(position));
						rightText.setText(editString(rList));
					}
				}
			}

		});

		View getAddToCartButton = findViewById(R.id.add_to_cart_button);
		getAddToCartButton.setOnClickListener(this);
		View getCancelButton = findViewById(R.id.cancel_button);
		getCancelButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_to_cart_button:
			addToCart();
			finish();
			break;
		case R.id.cancel_button:
			if (id == 999)
				data.getWritableDatabase().delete(TABLE_NAME, TOPPINGS_WHOLE + "='none'", null);
			else
				data.getWritableDatabase().delete(TABLE_NAME, _ID + "='" + id + "'", null);
			finish();
			break;
		}

	}

	private void addToCart() {
		String toppingsWhole = "";
		String toppingsLeft = "";
		String toppingsRight = "";
		if (wList.size() > 0) {
			toppingsWhole = toppingsWhole + editString(wList);
		}
		if (lList.size() > 0) {
			toppingsLeft = toppingsLeft + editString(lList);
		}
		if (rList.size() > 0) {
			toppingsRight = toppingsRight + editString(rList);
		}
		updatePizza(toppingsWhole, toppingsLeft, toppingsRight);
	}

	private void createToppingList() {
		topingList.add("Anchovies");
		topingList.add("Bacon");
		topingList.add("Banana Peppers");
		topingList.add("Black Olives");
		topingList.add("Chicken");
		topingList.add("Green Peppers");
		topingList.add("Ham");
		topingList.add("Jalapeno Peppers");
		topingList.add("Extra Cheese");
		topingList.add("Mushrooms");
		topingList.add("Onion");
		topingList.add("Pepperoni");
		topingList.add("Pineapple");
		topingList.add("Sausage");
		topingList.add("Roma Tomatoes");
	}

	private void displayMessage(int position, String message) {
		Toast.makeText(NewPizza.this, topingList.get(position) + message, Toast.LENGTH_SHORT).show();
	}

	private String editString(List<String> list) {
		String toppings = "";
		String withOutComma;
		for (String item : list) {
			toppings += item + ", ";
		}
		if (toppings.equals(""))
			withOutComma = "";
		else
			withOutComma = toppings.substring(0, toppings.length() - 2);
		return withOutComma;
	}

	private void updatePizza(String toppingsWhole, String toppingsLeft, String toppingsRight) {
		SQLiteDatabase db = data.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(TOPPINGS_WHOLE, toppingsWhole);
		values.put(TOPPINGS_LEFT, toppingsLeft);
		values.put(TOPPINGS_RIGHT, toppingsRight);
		if (id == 999)
			db.update(TABLE_NAME, values, TOPPINGS_WHOLE + "='none'", null);
		else
			db.update(TABLE_NAME, values, _ID + "='" + id + "'", null);
	}

	private static String ORDER_BY = SIZE + " DESC";

	private Cursor getPizza() {
		SQLiteDatabase db = data.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, _ID + "='" + id + "'", null, null, null, ORDER_BY);
		startManagingCursor(cursor);
		return cursor;
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
			// startActivity(new Intent(this, Prefs.class));
			return true;
		case R.id.help:
			new AlertDialog.Builder(this).setTitle(R.string.help_title).setMessage(R.string.add_ingredients_help).setCancelable(false)
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

	private void setArrayList(ArrayList<String> list, String[] toppings) {
		for (int i = 0; i < toppings.length; i++) {
			list.add(toppings[i].trim());
		}
	}

}
