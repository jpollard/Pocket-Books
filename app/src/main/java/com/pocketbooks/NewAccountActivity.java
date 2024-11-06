package com.pocketbooks;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.math.BigDecimal;

public class NewAccountActivity extends AppCompatActivity {
	private static String TAG = "newAccount";
	EditText accountName;
	EditText accountBalance;
	Button done;
	AccountData accounts;
	ActionBar actionBar;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		accounts = new AccountData(this);
		
		// Setup GUI
		setContentView(R.layout.new_account_activity_layout);

		actionBar = getSupportActionBar();
		actionBar.setTitle(R.string.new_account_title);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		
		accountName = (EditText) findViewById(R.id.new_account_name_edit_text);
		accountBalance = (EditText) findViewById(R.id.new_account_balance_edit_text);

//		done.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				BigDecimal balance = new BigDecimal("0.00");
//				if(accountName.getText() != null){
//					if(accountBalance.getText().length() != 0){
//						Log.d(TAG, "this is the getText..." + accountBalance.getEditableText());
//						try{
//							balance = new BigDecimal(accountBalance.getText().toString());
//							balance = balance.setScale(2, BigDecimal.ROUND_HALF_UP);
//						} catch(NumberFormatException e){
//							Log.d(TAG, "ERROR! ERROR! \"balance\" not a number!!!!");
//						}
//					}
//					Log.d(TAG, "about to create table " + accountName.getText().toString() + " with a value of " + balance);
//					accounts.createAccount(accountName.getText().toString(), balance);
//									}
//				finish();
//			}
//		});
	}

	// method to inflate the options menu when
	// the user opens the menu for the first time
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {

		getMenuInflater().inflate(R.menu.new_account_activity_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	// methods to control the operations that will
	// happen when user clicks on the action buttons
	@Override
	public boolean onOptionsItemSelected( @NonNull MenuItem item ) {

		switch (item.getItemId()){
			case R.id.confirmNewAccount:
				BigDecimal balance = new BigDecimal("0.00");
				if(accountName.getText() != null){
					if(accountBalance.getText().length() != 0){
						Log.d(TAG, "this is the getText..." + accountBalance.getEditableText());
						try{
							balance = new BigDecimal(accountBalance.getText().toString());
							balance = balance.setScale(2, BigDecimal.ROUND_HALF_UP);
						} catch(NumberFormatException e){
							Log.d(TAG, "ERROR! ERROR! \"balance\" not a number!!!!");
						}
					}
					Log.d(TAG, "about to create table " + accountName.getText().toString() + " with a value of " + balance);
					accounts.createAccount(accountName.getText().toString(), balance);
				}
				finish();
				break;

		}
		return super.onOptionsItemSelected(item);
	}
}
