package com.pocketbooks;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class NewAccountActivity extends Activity {
	private static String TAG = "newAccount";
	EditText accountName;
	EditText accountBalance;
	Button done;
	AccountData accounts;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		accounts = new AccountData(this);
		
		// Setup GUI
		setContentView(R.layout.new_account_activity_layout);
		accountName = (EditText) findViewById(R.id.new_account_name_edit_text);
		accountBalance = (EditText) findViewById(R.id.new_account_balance_edit_text);
		done = (Button) findViewById(R.id.new_account_done_button);
		
		done.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int balance = 0;
				if(accountName.getText() != null){
					if(accountBalance.getText().length() != 0){
						Log.d(TAG, "this is the getText..." + accountBalance.getEditableText());
						try{
							balance = Integer.parseInt(accountBalance.getText().toString());
						} catch(NumberFormatException e){
							balance = 0;
							Log.d(TAG, "ERROR! ERROR! \"balance\" not a number!!!!");
						}
					}
					Log.d(TAG, "about to create table " + accountName.getText().toString() + " with a value of " + balance);
					accounts.createTable(accountName.getText().toString(), balance);
				}
				finish();
			}	
		});
	}
}
