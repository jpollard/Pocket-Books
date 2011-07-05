package com.pocketbooks;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class TransactionsActivity extends Activity {
	private static String TAG = "PocketBooks::Transactions Activity";
	ListView list;
	AccountData transactions;
	Cursor accountInfo;
	Cursor cursor;
	Intent transactionIntent;
	Intent editTransactionIntent;
	TextView mAccountName;
	TextView mAccountBalance;
	LinearLayout mNewTransaction;
	long id;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final Intent newTransactionIntent = new Intent(this, NewTransactionActivity.class);
        
        
        setContentView(R.layout.transactions_activity_layout);
    
        transactions = new AccountData(this);
        mAccountName = (TextView) findViewById(R.id.accountName);
        mAccountBalance = (TextView) findViewById(R.id.accountBalance);
        transactionIntent = getIntent();
        id = transactionIntent.getLongExtra(AccountData.ACCOUNT_ID, 0);
        accountInfo = transactions.getAccountInfo(id);
        accountInfo.moveToFirst();
        startManagingCursor(accountInfo);
        
        mNewTransaction = (LinearLayout) findViewById(R.id.footer);
        
        
        mNewTransaction.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(newTransactionIntent.putExtra(AccountData.ACCOUNT_ID, id));
			}
        	
        });  
        
        list = (ListView) findViewById(R.id.transactionListView);
        
        Log.d(TAG, "Getting transactions");
        cursor = transactions.getTransactions(id);
        startManagingCursor(cursor);
        
        int[] to = {R.id.transaction_name, R.id.transaction_amount, R.id.transaction_date, R.id.transaction_category, R.id.transaction_memo};
        String[] from = {AccountData.TRANSACTION_NAME, AccountData.TRANSACTION_AMOUNT, AccountData.TRANSACTION_DATE, AccountData.TRANSACTION_CATEGORY, AccountData.TRANSACTION_MEMO};
        
        Log.d(TAG, "Starting adapter");
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.transactions_activity_listview_row, cursor, from, to);
        

        Log.d(TAG, "Setting adapter");
        
        list.setAdapter(adapter);
        registerForContextMenu(list);
//        list.setLongClickable(true);
//        list.setOnItemLongClickListener(new OnItemLongClickListener(){
//
//			@Override
//			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
//					int arg2, long arg3) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//        	
//        });
//        list.setOnLongClickListener(new OnLongClickListener(){
//
//			@Override
//			public boolean onLongClick(View arg0) {
//				// TODO Auto-generated method stub
//				long id;
//				id = list.getItemIdAtPosition(list.getSelectedItemPosition());
//				editTransactionIntent.putExtra(AccountData.TRANSACTION_ID, id);
//				startActivity(editTransactionIntent);
//				return true;
//			}
//        	
//        });
        
        
        
    }
    
    @Override
    public void onResume(){
    	//TODO SET Header method 
    	super.onResume();
    	Log.d(TAG, "transactionActivity: onResume");
    	accountInfo.requery();
    	cursor.requery();
    	
    	accountInfo.moveToFirst();
    	Log.d(TAG, "" + accountInfo.getColumnCount());
    	
    	String accountBalance = accountInfo.getString(accountInfo.getColumnIndex(AccountData.ACCOUNT_BALANCE));
    	mAccountBalance.setTextColor(Color.GREEN);
    	
    	if(Float.parseFloat(accountBalance) < 0){
    		mAccountBalance.setTextColor(Color.RED);
    	}
    	
        mAccountName.setText(accountInfo.getString(accountInfo.getColumnIndex(AccountData.ACCOUNT_NAME)));
        mAccountBalance.setText(accountInfo.getString(accountInfo.getColumnIndex(AccountData.ACCOUNT_BALANCE)));
        
        
    }
    
    
    @Override
    public void onPause(){
    	super.onPause();
    	Log.d(TAG, "transactionActivity: onPause");
    	accountInfo.deactivate();
    	cursor.deactivate();
    }
    
    @Override
    public void onStop(){
    	super.onStop();
    	Log.d(TAG, "transactionActivity: onStop");
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	
    	accountInfo.close();
    	cursor.close();
    	
    	transactions.close();
    }
    
    // Context Menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo ){
    	super.onCreateContextMenu(menu, v, menuInfo);
    	
    	menu.setHeaderTitle("Transactions Options");
    	menu.add(0, 0, 0, "Edit");
    	menu.add(0, 1, 0, "Delete");
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item){
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	
    	switch(item.getItemId()){
    		case 0:
    			editTransactionIntent = new Intent(this, EditTransactionActivity.class);
    			Log.d(TAG, "id of editTran " + info.id);
    			editTransactionIntent.putExtra(AccountData.TRANSACTION_ID, info.id);
    			startActivity(editTransactionIntent);
    			return true;
    		case 1:
    			
    			//TODO set Header method
    			transactions.deleteTransaction(info.id);
    			accountInfo.deactivate();
    			accountInfo.requery();
    			cursor.deactivate();
    			cursor.requery();
    			return true;
    	}
    	return false;
    }
}