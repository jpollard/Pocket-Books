package com.pocketbooks;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.math.BigDecimal;
import java.util.Calendar;

public class NewTransactionActivity extends AppCompatActivity {
	private static final int DATE_DIALOG = 0;
	private static String TAG = "NewTransactionActivity";

	ActionBar actionBar;
	EditText payeeEditText;
	EditText amountEditText;
	EditText dateEditText;
	EditText noteEditText;
	Spinner	categorySpinner;
	SwitchCompat transactionTypeSwitch;
	AccountData accounts;
	Cursor editTransactionInfo;
	int year;
	int month;
	int day;
	long accountID;
	long transactionID;
	Intent transactionIntent;

	
	@Override
	public void onCreate(Bundle savedInstanceState){
		Log.d(TAG, "entering crupdate activity");
		super.onCreate(savedInstanceState);
		transactionIntent = getIntent();
		Log.d(TAG, "got intent");

		setContentView(R.layout.new_transaction_activity_layout);

		accountID = transactionIntent.getLongExtra(AccountData.ACCOUNT_ID, 0);
		actionBar = getSupportActionBar();
		actionBar.setTitle("New Transaction");
		actionBar.setIcon(R.drawable.ic_action_name);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);

		accounts = new AccountData(this);
		payeeEditText = (EditText) findViewById(R.id.Payee_editText);
		transactionTypeSwitch = (SwitchCompat) findViewById(R.id.transactionType_Switch);
		amountEditText = (EditText) findViewById(R.id.amount_EditText);
		dateEditText = (EditText) findViewById(R.id.date_EditText);
		noteEditText = (EditText) findViewById(R.id.note_EditText);

		Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		
		updateDate();
		
		transactionTypeSwitch.setChecked(false);
		transactionTypeSwitch.setTextColor(getResources().getColor(R.color.PB_RED));
		transactionTypeSwitch.setText("Withdrawal");
		
		dateEditText.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				Log.d(TAG, "date focus" + hasFocus);
				
				if(hasFocus){
					showDialog(DATE_DIALOG);
				}
			}
		});
		
		dateEditText.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Log.d(TAG, "date clickity clack");
				showDialog(DATE_DIALOG);
			}
		});

		transactionTypeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				if (transactionTypeSwitch.isChecked()){
					transactionTypeSwitch.setTextColor(getResources().getColor(R.color.PB_GREEN));
					transactionTypeSwitch.setText("Deposit");
				}
				if (!transactionTypeSwitch.isChecked()){
					transactionTypeSwitch.setText("Withdrawal");
					transactionTypeSwitch.setTextColor(getResources().getColor(R.color.PB_RED));
				}
			}
		});

		if (transactionIntent.getLongExtra("trans_id", 0) != 0) {
			transactionID = transactionIntent.getLongExtra("trans_id", 0);
			Log.d(TAG, "has transaction_id " + transactionID);
			editTransactionInfo = accounts.getTransactionInfo(transactionID);
			editTransactionInfo.moveToFirst();

			transactionTypeSwitch.setChecked(true);
			BigDecimal amount = new BigDecimal(editTransactionInfo.getString(editTransactionInfo.getColumnIndex(AccountData.TRANSACTION_AMOUNT)));
			amount = amount.movePointLeft(2);

			if(amount.signum() < 0){
				transactionTypeSwitch.setChecked(false);
				amount = amount.abs();
			}
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(editTransactionInfo.getLong(editTransactionInfo.getColumnIndex(AccountData.TRANSACTION_DATE)));

			payeeEditText.setText(editTransactionInfo.getString(editTransactionInfo.getColumnIndex(AccountData.TRANSACTION_NAME)));
			amountEditText.setText(amount.toString());
			//editTransactionAmount.setText(editTransactionInfo.getString(editTransactionInfo.getColumnIndex(AccountData.TRANSACTION_AMOUNT)));
			dateEditText.setText(new StringBuilder().append(cal.get(Calendar.MONTH)).append("/").append(cal.get(Calendar.DAY_OF_MONTH)).append("/").append(cal.get(Calendar.YEAR)));
			//editTransactionCategory.setText(editTransactionInfo.getString(editTransactionInfo.getColumnIndex(AccountData.TRANSACTION_CATEGORY)));
			noteEditText.setText(editTransactionInfo.getString(editTransactionInfo.getColumnIndex(AccountData.TRANSACTION_MEMO)));
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
	    switch (id) {
	    case DATE_DIALOG:
	        return new DatePickerDialog(this, new OnDateSetListener(){

							@Override
							public void onDateSet(DatePicker view, int mYear, int mMonth, int mDay) {
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

	// method to inflate the options menu when
	// the user opens the menu for the first time
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {

		getMenuInflater().inflate(R.menu.new_transaction_activity_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	// methods to control the operations that will
	// happen when user clicks on the action buttons
	@Override
	public boolean onOptionsItemSelected( @NonNull MenuItem item ) {
		switch (item.getItemId()){
			case R.id.confirmNewTransaction:
				String payeeString = payeeEditText.getEditableText().toString();
				BigDecimal amountBD = new BigDecimal(0.00);

				if(amountEditText.length() > 0){
					amountBD = new BigDecimal(amountEditText.getEditableText().toString());
					amountBD = amountBD.setScale(2, BigDecimal.ROUND_HALF_UP);

					if(!transactionTypeSwitch.isChecked()){
						Log.d(TAG, "NEGATE!!!!! Biatch!");
						amountBD = amountBD.negate();
					}
				}

				//String dateString = dateEditText.getText().toString();
				Calendar cal = Calendar.getInstance();
				cal.set(year, month, day);

				String memoString = noteEditText.getEditableText().toString();

				if(0 != transactionID){
					accounts.updateTransaction(transactionID, payeeString, amountBD, cal.getTimeInMillis(), memoString);
				} else {
					accounts.addTransaction(accountID, payeeString, amountBD, cal.getTimeInMillis(), memoString);
				}

				finish();
				break;

		}
		return super.onOptionsItemSelected(item);
	}
}
