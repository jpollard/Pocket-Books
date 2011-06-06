package com.pocketbooks;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class TransactionsActivity extends Activity {
	private static String TAG = "PocketBooks::Transactions Activity";
	ListView list;
	AccountData transactions;
	Cursor accountInfo;
	Cursor cursor;
	Intent transactionIntent;
	TextView mAccountName;
	TextView mAccountBalance;
	LinearLayout mNewAccount;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent newTransaction = new Intent(this, NewTransactionActivity.class);
        setContentView(R.layout.transactions_activity_layout);
        transactions = new AccountData(this);
        mAccountName = (TextView) findViewById(R.id.accountName);
        mAccountBalance = (TextView) findViewById(R.id.accountBalance);
        mNewAccount = (LinearLayout) findViewById(R.id.footer);
        mNewAccount.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
        	
        });

        transactionIntent = getIntent();
        long id = transactionIntent.getLongExtra(AccountData.ACCOUNT_ID, 0);
        accountInfo = transactions.getAccountInfo(id);
        accountInfo.moveToFirst();
        startManagingCursor(accountInfo);
        
        Log.d(TAG, "" + accountInfo.getColumnCount());
        mAccountName.setText(accountInfo.getString(accountInfo.getColumnIndex(AccountData.ACCOUNT_NAME)));
        mAccountBalance.setText(accountInfo.getString(accountInfo.getColumnIndex(AccountData.ACCOUNT_BALANCE)));
        
        
        
        list = (ListView) findViewById(R.id.transactionListView);
        
        Log.d(TAG, "Getting transactions");
        cursor = transactions.getTransactions();
        startManagingCursor(cursor);
        
        int[] to = {R.id.transaction_name, R.id.transaction_amount, R.id.transaction_date, R.id.transaction_category};
        String[] from = {AccountData.TRANSACTION_NAME, AccountData.TRANSACTION_AMOUNT, AccountData.TRANSACTION_DATE, AccountData.TRANSACTION_CATEGORY};
        Log.d(TAG, "Starting adapter");
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.transactions_activity_listview_row, cursor, from, to);
        Log.d(TAG, "Setting adapter");
        list.setAdapter(adapter);
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	Log.d(TAG, "transactionActivity: onResume");
    	accountInfo.requery();
    	cursor.requery();
    }
    
    
    @Override
    public void onPause(){
    	super.onPause();
    	Log.d(TAG, "transactionActivity: onPause");
    	accountInfo.deactivate();
    	cursor.deactivate();
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	accountInfo.close();
    	cursor.close();
    }
}