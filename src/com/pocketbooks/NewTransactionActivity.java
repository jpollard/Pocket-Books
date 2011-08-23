package com.pocketbooks;

import java.math.BigDecimal;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
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
import android.widget.TextView;

public class NewTransactionActivity extends Activity {
	private static final int DATE_DIALOG = 0;
	private static String TAG = "NewTransactionActivity";
	
	EditText payeeEditText;
	EditText amountEditText;
	EditText dateEditText;
	EditText noteEditText;
	Spinner	categorySpinner;
	RadioButton depositRadioButton;
	RadioButton withdrawlRadioButton;
	Button doneButton;
	AccountData accounts;
	int year;
	int month;
	int day;
	long id;
	Intent transactionIntent;
	TextView headerAccount;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.new_transaction_activity_layout);
		
		headerAccount = (TextView) findViewById(R.id.header_account);
		headerAccount.setText("New Transaction");
		transactionIntent = getIntent();
		id = transactionIntent.getLongExtra(AccountData.ACCOUNT_ID, 0);
		
		accounts = new AccountData(this);
		payeeEditText = (EditText) findViewById(R.id.Payee_editText);
		depositRadioButton = (RadioButton) findViewById(R.id.desposit_RadioButton);
		withdrawlRadioButton = (RadioButton) findViewById(R.id.withdrawl_RadioButton);
		amountEditText = (EditText) findViewById(R.id.amount_EditText);
		dateEditText = (EditText) findViewById(R.id.date_EditText);
		noteEditText = (EditText) findViewById(R.id.note_EditText);
		//categorySpinner = (Spinner) findViewById(R.id.category_Spinner);
		doneButton = (Button) findViewById(R.id.new_transaction_activity_done_Button);
		
		Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		
		updateDate();
		
		
		
		dateEditText.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				Log.d(TAG, "date focus" + hasFocus);
				
				if(hasFocus){
					showDialog(DATE_DIALOG);
				}
				
			}
			
		});
		
		dateEditText.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "date clickity clack");
				showDialog(DATE_DIALOG);
			}
			
		});
		
		doneButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String payeeString = payeeEditText.getEditableText().toString();
				
				BigDecimal amountBD = new BigDecimal(0.00);
				
				
				
				if(amountEditText.length() > 0){
					amountBD = new BigDecimal(amountEditText.getEditableText().toString());
					amountBD = amountBD.setScale(2, BigDecimal.ROUND_HALF_UP);
					
					if(withdrawlRadioButton.isChecked()){
						Log.d(TAG, "NEGATE!!!!! Biatch!");
						amountBD = amountBD.negate();
					}
				}
				
				
				//String dateString = dateEditText.getText().toString();
				
				Calendar cal = Calendar.getInstance();
				cal.set(year, month, day);
				
				String memoString = noteEditText.getEditableText().toString();
				
				
				accounts.addTransaction(id, payeeString, amountBD, cal.getTimeInMillis(), memoString);
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
		
		dateEditText.setText(new StringBuilder().append(month + 1).append("/").append(day).append("/").append(year));
	}
	
}
