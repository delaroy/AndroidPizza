package com.delaroystudios.pizza;

import com.delaroystudios.pizza.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;

public class Pizza extends Activity implements OnClickListener {

	private EditText name;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Set up click listeners for all the buttons
		View getStartedButton = findViewById(R.id.get_started_button);
		getStartedButton.setOnClickListener(this);

		name = (EditText) findViewById(R.id.get_name);
		final int oldgrav = name.getGravity();
		name.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (name.getText().toString().equals(""))
					name.setGravity(oldgrav);
				else
					name.setGravity(Gravity.CENTER);
				return false;
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.get_started_button:
			if (getName().equals("") || getName() == null) {
				new AlertDialog.Builder(this).setTitle(R.string.incomplete_title).setMessage(R.string.enter_name_text).setCancelable(false)
						.setNeutralButton("OK", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						}).show();
			} else {
				Intent i = new Intent(this, OrderPage.class);
				startActivity(i);
			}
			break;
		// More buttons go here (if any) ...
		}
	}

	public String getName() {
		return name.getText().toString();
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
			new AlertDialog.Builder(this).setTitle(R.string.help_title).setMessage(R.string.enter_name_help).setCancelable(false)
					.setNeutralButton("OK", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).show();
			return true;
		case R.id.exit:
			new AlertDialog.Builder(this).setTitle(R.string.exit).setMessage("Are you sure you want to exit?").setCancelable(true)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

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