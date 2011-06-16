package com.pocketbooks;

import java.math.BigDecimal;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

public class NewTransactionActivity extends Activity {
	private static int DATE_DIALOG = 0;
	
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
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.new_transaction_activity_layout);
		
		transactionIntent = getIntent();
		id = transactionIntent.getLongExtra(AccountData.ACCOUNT_ID, 0);
		accounts = new AccountData(this);
		payeeEditText = (EditText) findViewById(R.id.Payee_editText);
		depositRadioButton = (RadioButton) findViewById(R.id.desposit_RadioButton);
		withdrawlRadioButton = (RadioButton) findViewById(R.id.withdrawl_RadioButton);
		amountEditText = (EditText) findViewById(R.id.amount_EditText);
		dateEditText = (EditText) findViewById(R.id.date_EditText);
		noteEditText = (EditText) findViewById(R.id.note_EditText);
		categorySpinner = (Spinner) findViewById(R.id.category_Spinner);
		doneButton = (Button) findViewById(R.id.new_transaction_activity_done_Button);
		
		Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		
		dateEditText.setText(new StringBuilder().append(day).append("/").append(month + 1).append("/").append(year));
		
		dateEditText.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showDialog(DATE_DIALOG);
			}
			
		});
		
		doneButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String payeeString = payeeEditText.getEditableText().toString();
				BigDecimal amountBD = new BigDecimal(0.00);
				amountBD = amountBD.setScale(2, BigDecimal.ROUND_HALF_UP);
				if(amountEditText.length() > 0){
					amountBD = new BigDecimal(amountEditText.getEditableText().toString());
					
					if(withdrawlRadioButton.isChecked()){
						amountBD.negate();
					}
				}
				String dateString = dateEditText.getText().toString();
				String memoString = noteEditText.getEditableText().toString();
				
				accounts.addTransaction(id, payeeString, amountBD, dateString, memoString);
				finish();
			}
		});
		
	}

}
