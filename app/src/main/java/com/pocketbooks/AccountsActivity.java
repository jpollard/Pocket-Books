package com.pocketbooks;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class AccountsActivity extends AppCompatActivity {
	ActionBar actionBar;

	private static final String TAG = "ListActivity: ";
	AccountData accounts;
	CursorAdapter cursorAdapter;
	Cursor cursor;
	ListView list;
	FloatingActionButton mNewAccount;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Starting account");

		// Set up action bar
		actionBar = getSupportActionBar();
		actionBar.setTitle(R.string.app_name);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);

		// Setup UI
		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.accounts_activity_layout);
        list = (ListView) findViewById(R.id.accountNameListView);
        //adView = (AdView) findViewById(R.id.ad);
        
        final Intent newAccountIntent = new Intent(this, NewAccountActivity.class);
        final Intent transactionIntent = new Intent(this, TransactionsActivity.class);
        
//        View v = getLayoutInflater().inflate(R.layout.accounts_activity_header, null);
//        v.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				Log.d(TAG, "Clickity Clack: in onClick method in AccountLists starting NewAccountActivity.");
//				startActivity(newAccountIntent);
//				
//			}	
//        });
       
        mNewAccount = (FloatingActionButton) findViewById(R.id.addAccountFAB);
        mNewAccount.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				startActivity(newAccountIntent);
			}
        	
        }); 
        
        //Query current accountNames
        accounts = new AccountData(this);
        Log.d(TAG, "Starting getTables.");
        cursor = accounts.getAccounts();
        startManagingCursor(cursor);
        
        // Construct adapter
        int[] to = {R.id.account_name, R.id.account_balance};
        String[] from = {AccountData.ACCOUNT_NAME, AccountData.ACCOUNT_BALANCE};
        AccountAdapter adapter = new AccountAdapter(this, R.layout.accounts_activity_listview_row, cursor, from, to);
        
        // Link ListView to adapter
        //list.addHeaderView(v);
        list.setAdapter(adapter);
        registerForContextMenu(list);
        list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				// TODO Auto-generated method stub
				Log.d(TAG, "id is " + id + " position is " + position + " action is " + getIntent().getAction());
				Log.d(TAG, "Hopefully _id ");
				
				cursor.moveToPosition(position);
				
				//Log.d(TAG, "" + cursor.getString(cursor.getColumnIndex(AccountData.ACCOUNT_NAME) - 1));
				
				startActivity(transactionIntent.putExtra(AccountData.ACCOUNT_ID, id));
			}
        	
        });
	}
	
	@Override
	public void onResume(){
		super.onResume();
		cursor.requery();
		//adView.requestFreshAd();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		cursor.deactivate();
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		cursor.close();
		accounts.close();
	}
	
	// Context Menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo ){
    	super.onCreateContextMenu(menu, v, menuInfo);
    	
    	MenuInflater inflater =  getMenuInflater();
    	inflater.inflate(R.menu.accounts_menu, menu);
    	
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item){
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	
    	switch(item.getItemId()){
    		case R.id.account_delete:
    			Log.d(TAG, "Deleting account with id " + info.id);
    			accounts.deleteAccount(info.id);
    			cursor.deactivate();
    			cursor.requery();
    			return true;
    	}
    	return false;
    }
}