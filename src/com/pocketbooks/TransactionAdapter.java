package com.pocketbooks;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class TransactionAdapter extends SimpleCursorAdapter{
	private static String TAG = "TransactionAdapter";
	private int[] colors = new int[] {AccountData.YELLOW, AccountData.YELLOWGREEN};
	Context context;
	int layout;
	Cursor c;
	String[] from;
	int[] to;
	private LayoutInflater inflater;
	private int transactionNameIndex;
	private int transactionAmountIndex;
	private int transactionDateIndex;
	private int transactionMemoIndex;

	public TransactionAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.layout = layout;
		this.c = c;
		this.from = from;
		this.to = to;
		inflater = LayoutInflater.from(context);
		
		transactionNameIndex = c.getColumnIndex(AccountData.TRANSACTION_NAME);
		transactionDateIndex = c.getColumnIndex(AccountData.TRANSACTION_DATE);
		transactionAmountIndex = c.getColumnIndex(AccountData.TRANSACTION_AMOUNT);
		transactionMemoIndex = c.getColumnIndex(AccountData.TRANSACTION_MEMO);
		
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent){
		super.newView(context, cursor, parent);
		
		View v = inflater.inflate(layout, parent, false);
		
		return v;
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = super.getView(position, convertView, parent);
		int colorPos = position % colors.length;
		v.setBackgroundColor(colors[colorPos]);
		return v;
	}
	
	@Override 
	public void bindView(View view, Context context, Cursor cursor){
		super.bindView(view, context, cursor);
		
		
		TextView transactionName = (TextView) view.findViewById(R.id.transaction_name);
		TextView transactionAmount = (TextView) view.findViewById(R.id.transaction_amount);
		TextView transactionDate = (TextView) view.findViewById(R.id.transaction_date);
		TextView transactionMemo = (TextView) view.findViewById(R.id.transaction_memo);
		
		transactionName.setText(cursor.getString(transactionNameIndex));
		transactionMemo.setText(cursor.getString(transactionMemoIndex));
		
		//TODO Setup BigDecimal - CurrencyFormat
		
		
		BigDecimal amount = new BigDecimal(cursor.getString(transactionAmountIndex));
		amount = amount.movePointLeft(2);
		transactionAmount.setTextColor(AccountData.GREEN);
		
		if(amount.signum() < 0){
			transactionAmount.setTextColor(AccountData.RED);
		}
		
		transactionAmount.setText(amount.toPlainString());
		
		long mDate = cursor.getLong(transactionDateIndex);
		Date date = new Date();
		date.setTime(mDate);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		Log.d(TAG, date.toLocaleString());
		transactionDate.setText(new StringBuilder().append(cal.get(Calendar.MONTH) + 1).append("/").append(cal.get(Calendar.DAY_OF_MONTH)).append("/").append(cal.get(Calendar.YEAR)));
		
		
	}
}
