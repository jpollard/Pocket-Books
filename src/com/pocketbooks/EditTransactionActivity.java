package com.pocketbooks;

import java.math.BigDecimal;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

public class EditTransactionActivity extends Activity {
	final static String TAG = EditTransactionActivity.class.getSimpleName();
	
	AccountData transaction;
	EditText editTransactionName;
	EditText editTransactionAmount;
	EditText editTransactionDate;
	Spinner editTransactionCategory;
	EditText editTransactionMemo;
	Button editTransactionDone;
	Cursor editTransactionInfo;
	Intent transactionIntent;
	RadioButton editDeposit;
	RadioButton editWithdrawl;
	long id;
	
	@Override
	public void onCreate(Bundle SavedInstance){
		super.onCreate(SavedInstance);
		setContentView(R.layout.edit_transaction_activity_layout);
		
		transaction = new AccountData(this);
		editTransactionName = (EditText) findViewById(R.id.Edit_Payee_editText);
		editTransactionAmount = (EditText) findViewById(R.id.Edit_amount_EditText);
		editTransactionDate = (EditText) findViewById(R.id.Edit_date_EditText);
		editTransactionCategory = (Spinner) findViewById(R.id.Edit_category_Spinner);
		editTransactionMemo = (EditText) findViewById(R.id.Edit_note_EditText);
		editDeposit = (RadioButton) findViewById(R.id.Edit_desposit_RadioButton);
		editWithdrawl = (RadioButton) findViewById(R.id.Edit_withdrawl_RadioButton);
		editTransactionDone = (Button) findViewById(R.id.edit_transaction_activity_done_Button);
		Log.d(TAG, "Yay, made it!");
		transactionIntent = getIntent();
		Log.d(TAG, "Got intent");
		
		id = transactionIntent.getLongExtra(AccountData.TRANSACTION_ID, 0);
		Log.d(TAG, "GOT ID " + id);
		
		editTransactionInfo = transaction.getTransactionInfo(id);
		editTransactionInfo.moveToFirst();
		startManagingCursor(editTransactionInfo);
		
		editDeposit.setChecked(true);
		BigDecimal amount = new BigDecimal(editTransactionInfo.getString(editTransactionInfo.getColumnIndex(AccountData.TRANSACTION_AMOUNT)));
		if(amount.signum() < 0){
			editWithdrawl.setChecked(true);
			amount = amount.abs();
		}
		
		editTransactionName.setText(editTransactionInfo.getString(editTransactionInfo.getColumnIndex(AccountData.TRANSACTION_NAME)));
		editTransactionAmount.setText(amount.toString());
		//editTransactionAmount.setText(editTransactionInfo.getString(editTransactionInfo.getColumnIndex(AccountData.TRANSACTION_AMOUNT)));
		editTransactionDate.setText(editTransactionInfo.getString(editTransactionInfo.getColumnIndex(AccountData.TRANSACTION_DATE)));
		//editTransactionCategory.setText(editTransactionInfo.getString(editTransactionInfo.getColumnIndex(AccountData.TRANSACTION_CATEGORY)));
		editTransactionMemo.setText(editTransactionInfo.getString(editTransactionInfo.getColumnIndex(AccountData.TRANSACTION_MEMO)));
		
		editTransactionDone.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				BigDecimal newAmount = new BigDecimal("0.00").setScale(2, BigDecimal.ROUND_HALF_UP);
				if(editTransactionAmount.length() > 0){
					newAmount = new BigDecimal(editTransactionAmount.getText().toString());
					if(editWithdrawl.isChecked()){ 
						newAmount = newAmount.negate();
					}
				}
				Log.d(TAG, "Trying to update");
				transaction.updateTransaction(id, editTransactionName.getText().toString(), newAmount, editTransactionDate.getText().toString(), editTransactionMemo.getText().toString());
				finish();
			}
			
		});
	}

}
