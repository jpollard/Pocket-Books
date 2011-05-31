package com.pocketbooks;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class AccountLists extends Activity{
	private static final String TAG = "ListActivity: ";
	AccountData accounts;
	CursorAdapter cursorAdapter;
	Cursor cursor;
	ListView list;
	TextView button;
		
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Starting account");
		
		// Setup UI
        setContentView(R.layout.main);
        list = (ListView) findViewById(R.id.accountNameListView);
        
        final Intent newAccountIntent = new Intent(this, NewAccount.class);
        final Intent transactionIntent = new Intent(this, Transactions.class);
        
        View v = getLayoutInflater().inflate(R.layout.new_account_header, null);
        v.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Log.d(TAG, "Clickity Clack: in onClick method in AccountLists starting NewAccountActivity.");
				startActivity(newAccountIntent);
				
			}	
        });
        
        //Query current accountNames
        accounts = new AccountData(this);
        Log.d(TAG, "Starting getTables.");
        cursor = accounts.getTables();
        startManagingCursor(cursor);
        
        // Construct adapter
        int[] to = {R.id.account_name, R.id.account_balance};
        String[] from = {AccountData.ACCOUNT_NAME, AccountData.ACCOUNT_BALANCE};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.accounts_row, cursor, from, to);
        
        // Link ListView to adapter
        list.addHeaderView(v);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				// TODO Auto-generated method stub
				Log.d(TAG, "id is " + id + " position is " + position + " action is " + getIntent().getAction());
				Log.d(TAG, "Hopefully _id " + list.getItemIdAtPosition(position));
				startActivity(transactionIntent);
			}
        	
        });
	}
}