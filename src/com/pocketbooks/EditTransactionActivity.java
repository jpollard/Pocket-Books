package com.pocketbooks;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class EditTransactionActivity extends Activity {
	AccountData transaction;
	EditText editTransactionName;
	EditText editTransactionAmount;
	EditText editTransactionDate;
	Spinner editTransactionCategory;
	EditText editTransactionMemo;
	Button editTransactionDone;
	Cursor editTransactionInfo;
	Intent transactionIntent;
	long id;
	
	@Override
	public void onCreate(Bundle SavedInstance){
		super.onCreate(SavedInstance);
		transactionIntent = getIntent();
		id = transactionIntent.getLongExtra(AccountData.TRANSACTION_ID, 0);
		editTransactionInfo = transaction.getTransactionInfo(id);
	}

}
