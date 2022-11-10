package com.pocketbooks;

import java.math.BigDecimal;

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
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

//import com.admob.android.ads.AdManager;
//import com.admob.android.ads.AdView;

public class TransactionsActivity extends Activity {
	private static String TAG = "PocketBooks::Transactions Activity";
	
//	AdView adView;
	ListView list;
	AccountData transactions;
	Cursor accountInfo;
	Cursor cursor;
	Intent transactionIntent;
	Intent editTransactionIntent;
	TextView mAccountName;
	TextView mAccountBalance;
	LinearLayout mNewTransaction;
	LinearLayout mHeader;
	long id;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final Intent newTransactionIntent = new Intent(this, NewTransactionActivity.class);
        
        setContentView(R.layout.transactions_activity_layout);
        
        mHeader = (LinearLayout) findViewById(R.id.header);
        transactions = new AccountData(this);
        mAccountName = (TextView) findViewById(R.id.header_account);
        mAccountBalance = (TextView) findViewById(R.id.header_balance);
        transactionIntent = getIntent();
        id = transactionIntent.getLongExtra(AccountData.ACCOUNT_ID, 0);
        accountInfo = transactions.getAccountInfo(id);
        accountInfo.moveToFirst();
        startManagingCursor(accountInfo);

        //AdManager.setTestDevices( new String[] { "61288A13F61EE945752EE32D7DB60B3D" } );
  //      adView = (AdView) findViewById(R.id.ad);

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
       // SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.transactions_activity_listview_row, cursor, from, to);
       TransactionAdapter adapter = new TransactionAdapter(this, R.layout.transactions_activity_listview_row, cursor, from, to);


        Log.d(TAG, "Setting adapter");
        
        list.setAdapter(adapter);
        registerForContextMenu(list);
        
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
    	updateBalance();

    	//adView.requestFreshAd();
    	//adView.bringToFront();

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
    
    /**
     * update the balance of the account as displayed at the top of the screen.
     */
    public void updateBalance(){
    	accountInfo.moveToFirst();
    	
    	String amountNoDecimal = accountInfo.getString(accountInfo.getColumnIndex(AccountData.ACCOUNT_BALANCE));
    	BigDecimal accountBalance = new BigDecimal(amountNoDecimal);
    	accountBalance = accountBalance.movePointLeft(2);
    	
    	mAccountBalance.setTextColor(Color.WHITE);
    	mHeader.setBackgroundColor(AccountData.GREEN);
    	if(accountBalance.signum() < 0){
    		mHeader.setBackgroundColor(AccountData.RED);
    	}
    	
        mAccountName.setText(accountInfo.getString(accountInfo.getColumnIndex(AccountData.ACCOUNT_NAME)));
        mAccountBalance.setText(accountBalance.toPlainString());
        
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
    			updateBalance();
    			
    			return true;
    	}
    	return false;
    }
}