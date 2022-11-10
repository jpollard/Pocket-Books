package com.pocketbooks;

import java.math.BigDecimal;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class AccountAdapter extends SimpleCursorAdapter{
    private final static String TAG = "AccountAdapter";

    Context context;
    int layout;
    Cursor c;
    String[] from;
    int[] to;
    private final LayoutInflater inflater;
    private int accountNameIndex;
    private int accountAmountIndex;

    public AccountAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to);

        this.context = context;
        this.layout = layout;
        this.c = c;
        this.from = from;
        this.to = to;
        inflater = LayoutInflater.from(context);

        accountNameIndex = c.getColumnIndex(AccountData.ACCOUNT_NAME);
        accountAmountIndex = c.getColumnIndex(AccountData.ACCOUNT_BALANCE);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        super.newView(context, cursor, parent);

        View v = inflater.inflate(layout, parent, false);

        return v;

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor){
        super.bindView(view, context, cursor);

        TextView accountName = (TextView) view.findViewById(R.id.account_name);
        TextView accountBalance = (TextView) view.findViewById(R.id.account_balance);
        accountName.setTextColor(AccountData.GREEN);
        accountName.setText(cursor.getString(accountNameIndex));

        //TODO Setup BigDecimal - CurrencyFormat
        String amountNoDecimal = cursor.getString(accountAmountIndex);

        BigDecimal amount = new BigDecimal(amountNoDecimal);
        amount = amount.movePointLeft(2);

        accountBalance.setTextColor(AccountData.GREEN);
        if(amount.signum() < 0){
            accountBalance.setTextColor(AccountData.RED);
        }
        accountBalance.setText(amount.toPlainString());

    }
}
