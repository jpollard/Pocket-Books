package com.pocketbooks;

import java.math.BigDecimal;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

public class EditTransactionActivity extends Activity {
	final static String TAG = EditTransactionActivity.class.getSimpleName();
	private static final int DATE_DIALOG = 0;
	
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
	int year;
	int month;
	int day;
	
	
	@Override
	public void onCreate(Bundle SavedInstance){
		super.onCreate(SavedInstance);
		

		setContentView(R.layout.new_transaction_activity_layout);
		
		transaction = new AccountData(this);
		editTransactionName = (EditText) findViewById(R.id.Payee_editText);
		editTransactionAmount = (EditText) findViewById(R.id.amount_EditText);
		editTransactionDate = (EditText) findViewById(R.id.date_EditText);
		editTransactionCategory = (Spinner) findViewById(R.id.category_Spinner);
		editTransactionMemo = (EditText) findViewById(R.id.note_EditText);
		editDeposit = (RadioButton) findViewById(R.id.desposit_RadioButton);
		editWithdrawl = (RadioButton) findViewById(R.id.withdrawl_RadioButton);
		editTransactionDone = (Button) findViewById(R.id.new_transaction_activity_done_Button);
		Log.d(TAG, "Yay, made it!");
		transactionIntent = getIntent();
		Log.d(TAG, "Got intent");
		
		id = transactionIntent.getLongExtra(AccountData.TRANSACTION_ID, 0);
		Log.d(TAG, "GOT ID " + id);
		
		editTransactionInfo = transaction.getTransactionInfo(id);
		editTransactionInfo.moveToFirst();
		startManagingCursor(editTransactionInfo);
		
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(editTransactionInfo.getLong(editTransactionInfo.getColumnIndex(AccountData.TRANSACTION_DATE)));
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		
		updateDate();
		
		
		
		editTransactionDate.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				Log.d(TAG, "date focus" + hasFocus);
				
				if(hasFocus){
					showDialog(DATE_DIALOG);
				}
				
			}
			
		});
		
		editTransactionDate.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "date clickity clack");
				showDialog(DATE_DIALOG);
			}
			
		});
		
		editDeposit.setChecked(true);
		BigDecimal amount = new BigDecimal(editTransactionInfo.getString(editTransactionInfo.getColumnIndex(AccountData.TRANSACTION_AMOUNT)));
		if(amount.signum() < 0){
			editWithdrawl.setChecked(true);
			amount = amount.abs();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(editTransactionInfo.getLong(editTransactionInfo.getColumnIndex(AccountData.TRANSACTION_DATE)));
		
		editTransactionName.setText(editTransactionInfo.getString(editTransactionInfo.getColumnIndex(AccountData.TRANSACTION_NAME)));
		editTransactionAmount.setText(amount.toString());
		//editTransactionAmount.setText(editTransactionInfo.getString(editTransactionInfo.getColumnIndex(AccountData.TRANSACTION_AMOUNT)));
		editTransactionDate.setText(new StringBuilder().append(cal.get(Calendar.MONTH)).append("/").append(cal.get(Calendar.DAY_OF_MONTH)).append("/").append(cal.get(Calendar.YEAR)));
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
				Calendar cal = Calendar.getInstance();
				cal.set(year, month, day);
				
				Log.d(TAG, "Trying to update");
				transaction.updateTransaction(id, editTransactionName.getText().toString(), newAmount, cal.getTimeInMillis(), editTransactionMemo.getText().toString());
				finish();
			}
			
		});
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
	    switch (id) {
	    case DATE_DIALOG:
	        return new DatePickerDialog(this, new OnDateSetListener(){

							@Override
							public void onDateSet(DatePicker view, int mYear, int mMonth, int mDay) {
								// TODO Auto-generated method stub
								year = mYear;
								month = mMonth;
								day = mDay;
								updateDate();
							}
	        	}, year, month, day);
	    }
	    return null;
	}

	/**
	 * Updates the date displayed in the dateEditText.
	 */
	public void updateDate(){
		
		editTransactionDate.setText(new StringBuilder().append(month + 1).append("/").append(day).append("/").append(year));
	}

}
