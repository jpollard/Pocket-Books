package com.pocketbooks;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class Transactions extends Activity {
	private static String TAG = "PocketBooks::Transactions Activity";
	ListView list;
	AccountData transactions;
	Cursor cursor;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent newTransaction = new Intent(this, NewTransaction.class);
        setContentView(R.layout.transactions_layout);
        
        list = (ListView) findViewById(R.id.transactionListView);
        
        transactions = new AccountData(this);
        Log.d(TAG, "Getting transactions");
        cursor = transactions.getTransactions();
        startManagingCursor(cursor);
        
        int[] to = {R.id.transaction_name, R.id.transaction_amount, R.id.transaction_date, R.id.transaction_category};
        String[] from = {AccountData.TRANSACTION_NAME, AccountData.TRANSACTION_AMOUNT, AccountData.TRANSACTION_DATE, AccountData.TRANSACTION_CATEGORY};
        Log.d(TAG, "Starting adapter");
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.transactions_row, cursor, from, to);
        Log.d(TAG, "Setting adapter");
        list.setAdapter(adapter);
    }
}