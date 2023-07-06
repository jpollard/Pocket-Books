package com.pocketbooks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.util.Log;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class AccountData {
	public static int GREEN = Color.parseColor("#216C2A");
	public static int YELLOW = Color.parseColor("#F6FFDA");
	public static int GHOST_WHITE = Color.parseColor("#E8E9F3");
	public static int SILVER = Color.parseColor("#CECECE");
	public static int RED = Color.parseColor("#C6372F");
	public static int YELLOWGREEN = Color.parseColor("#bfdeb9");
	
	public static String ACCOUNT_NAME = "account_name";
	public static String ACCOUNT_BALANCE = "account_balance";
	public static String ACCOUNT_ID = "_id";
	public static String TRANSACTION_ID = "_id";
	public static String TRANSACTION_ACCOUNT_ID = "account_id";
	public static String TRANSACTION_NUMBER = "transaction_number";
	public static String TRANSACTION_AMOUNT = "transaction_amount"; 
	public static String TRANSACTION_NAME = "transaction_name";
	public static String TRANSACTION_MEMO = "transaction_memo";
	public static String TRANSACTION_CATEGORY = "transaction_category";
	public static String TRANSACTION_DATE = "transaction_date"; 
	
	NumberFormat nf = NumberFormat.getInstance(Locale.US);
	
	DecimalFormat myFormatter =  (DecimalFormat) NumberFormat.getInstance(Locale.US);

	private static final String TAG = AccountData.class.getSimpleName();
	Context context;
	DBHelper dbHelper;
	String tableName;
	String sql;
	private SQLiteDatabase db;
	
	/**
	 * public void close()
	 * 
	 * closes the current SQLite database
	 */
	public void close(){
		db.close();
	}
	
	/**
	 * public AccountData(Context context)
	 * 
	 * Constructor of the the AccountData class
	 * 
	 * @param context
	 */
	public AccountData(Context context){
		this.context = context;
		//this.tableName = tableName;
		dbHelper = new DBHelper();
	}
	
	/**
	 * public void createAccount(String name, float balance)
	 * 
	 * Creates a new account in the AccountTable of the name 'name' with the initial balance of 'balance'.
	 * 
	 * Example: createAccount("Bank One", 100.00)
	 * this would insert "Bank One" into the ACCOUNTS_TABLE.ACCOUNT_NAME column and "100.00" into 
	 * ACCOUNTS_TABLE.ACCOUNT_BALANCE.
	 * 
	 * @param name, balance
	 */
	public void createAccount(String name, BigDecimal balance){
		ContentValues values = new ContentValues();
		db = dbHelper.getWritableDatabase();
		
		balance = balance.movePointRight(2);
		
		
		values.put(ACCOUNT_NAME, name);
		values.put(ACCOUNT_BALANCE, balance.toPlainString());
		
		Log.d(TAG, "Inital balance : " + balance.toPlainString());
		Log.d(TAG, "Account Name : " + name);
		Log.d(TAG, values.toString());
		
		db.beginTransaction();
		try{
			db.insert(DBHelper.ACCOUNTS_TABLE, null, values);
			Log.d(TAG, "createTable sql: " + name);
			db.setTransactionSuccessful();
			Log.d(TAG, "done");
		}catch(SQLException e){
			Log.d(TAG, "fail sql: " + e);
		}finally{
			db.endTransaction();
			db.close();
			Log.d(TAG, "closed");
		}		
	}
	
	/**
	 * public Cursor getAccounts()
	 * 
	 * returns a Cursor containing all the rows in ACCOUNTS_TABLE, unsorted.
	 * 
	 * @return Cursor
	 */
	public Cursor getAccounts(){
		Log.d(TAG, "Trying to get accounts");
		Cursor cursor;
		
		// String array of columns to get from query for the cursor
		String[] columnsToQuery = {ACCOUNT_ID, ACCOUNT_NAME, ACCOUNT_BALANCE};
		
		Log.d(TAG, "Trying to open DB");
		db = dbHelper.getReadableDatabase();
		Log.d(TAG, "Opened DB");
		
		Log.d(TAG, "Querying DB");
		cursor = db.query(DBHelper.ACCOUNTS_TABLE, columnsToQuery, null, null, null, null, null);
		
		Log.d(TAG, "returning tables in a cursor. " + cursor.getCount());
		return cursor;	
	}
	
	/**
	 * <b> public void deleteAccount (Long id) </b>
	 * Delete the account as specified by the id, as well as the transactions registered to the account.
	 * 
	 * @param id - _id of the account
	 */
	public void deleteAccount(Long id){
		Log.d(TAG, "delectAccount");
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(DBHelper.ACCOUNTS_TABLE, "_id = " + id, null);
	}
	
	/**
	 * <b> public Cursor getAccountInfo (Long id) </b>
	 * 
	 * This returns a Cursor that is populated with the info of one of the ACCOUNTS_TABLE rows, specified by
	 * the 'id' parameter.
	 * 
	 * @param id
	 * @return Cursor
	 */
	public Cursor getAccountInfo(Long id){
		Log.d(TAG, "trying to get account info for account_id " + id);
		Cursor cursor;
		String[] columnsToQuery = {ACCOUNT_ID, ACCOUNT_NAME, ACCOUNT_BALANCE};
		Log.d(TAG, "Trying to open DB");
		
		db = dbHelper.getReadableDatabase();
		
		cursor = db.query(DBHelper.ACCOUNTS_TABLE, columnsToQuery, ACCOUNT_ID + " like " + Long.toString(id), null, null, null, null);
		Log.d(TAG, "number of rows" + cursor.getCount() + " : " + cursor.toString());
		
		Log.d(TAG, "returning tables in a cursor");
		return cursor;
	}
	
	/**
	 * <b> public Cursor getTransactionInfo(Long id) </b>
	 * 
	 * Get the details of the transaction that has the _id passed in by the parameter.
	 * 
	 * @param id - the transaction id of the transaction that the info needs to come from
	 * @return - cursor populated with the request data
	 */
	public Cursor getTransactionInfo(Long id){
		Log.d(TAG, "trying to get transaction info for transaction_id " + id);
		Cursor cursor;
		String[] columnsToQuery = {AccountData.TRANSACTION_NAME, AccountData.TRANSACTION_AMOUNT, AccountData.TRANSACTION_DATE, AccountData.TRANSACTION_CATEGORY, AccountData.TRANSACTION_MEMO};
		db = dbHelper.getReadableDatabase();
		
		cursor = db.query(DBHelper.TRANSACTIONS_TABLE, columnsToQuery, TRANSACTION_ID + " LIKE " + Long.toString(id), null, null, null, null);
		Log.d(TAG,cursor.toString());
		return cursor;
	}
	
	/**
	 * 
	 * Get the list of "transactions" from the TRANSACTION_TABLE based on the "id" of the the account that
	 * they are associated with.
	 * 
	 * public Cursor getTransactions(long id)
	 * 
	 * @param id - the account from which transactions are being requested.
	 * @return - cursor of the transactions based on the id passed in.
	 */
	public Cursor getTransactions(long id){
		Log.d(TAG, "Trying to get transactions");
		Cursor cursor;
		String[] columnsToQuery = {TRANSACTION_ID, TRANSACTION_ACCOUNT_ID, TRANSACTION_NAME, TRANSACTION_AMOUNT, TRANSACTION_DATE, TRANSACTION_CATEGORY, TRANSACTION_MEMO};
		
		Log.d(TAG, "Trying to open DB");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Log.d(TAG, "Opened DB");
		
		Log.d(TAG, "Querying DB");
		cursor = db.query(DBHelper.TRANSACTIONS_TABLE, columnsToQuery, TRANSACTION_ACCOUNT_ID + " like " + id, null, null, null, TRANSACTION_DATE + " DESC");
		Log.d(TAG, "returning tables in a cursor");
		Log.d(TAG, cursor.getColumnName(cursor.getColumnIndex(TRANSACTION_ID)));
		
		
		return cursor;
	}
	
	/**
	 * <b>public void addTransaction(long id, String payee, BigDecimal amount, String date, String memo)</b>
	 * 
	 * Add a new transaction to the TRANSACTIONS_TABLE, where "id" is the account id to associate the 
	 * transaction with it, "payee" is the person the transaction is for/from, "amount" is the amount, 
	 * "date" is the date, "memo" is the memo of the transaction.
	 * 
	 * @param id - the account_id that the transaction belongs to
	 * @param payee - a string to describe the name of the transaction (i.e. "Walmart" or "Paycheck")
	 * @param amount - the amount of the transaction
	 * @param date - a string date of the transaction
	 * @param memo - a simple, but more thorough description of the transaction (i.e. "Groceries" or "2/17/09 - 2/24/09")
	 *
	 */
	public void addTransaction(long id, String payee, BigDecimal amount, long date, String memo){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);
		amount = amount.movePointRight(2);
		
		
		
		ContentValues values = new ContentValues();
		values.put(AccountData.TRANSACTION_ACCOUNT_ID, id);
		values.put(AccountData.TRANSACTION_NAME, payee);
		values.put(AccountData.TRANSACTION_AMOUNT, amount.toPlainString());
		values.put(AccountData.TRANSACTION_DATE, date);
		values.put(AccountData.TRANSACTION_MEMO, memo);
		
		db.insert(DBHelper.TRANSACTIONS_TABLE, null, values);
		db.close();
	}
	
	/**
	 * <b> public void updateTransaction (long id, String payee, BigDecimal amount, String date, String memo)</b>
	 * 
	 * Update the transaction represented by id to the new values that have been passed in.
	 * 
	 * @param id - the transaction id
	 * @param payee - a string to describe the name of the transaction (i.e. "Walmart" or "Paycheck")
	 * @param amount - the amount of the transaction
	 * @param date - a string date of the transaction
	 * @param memo - a simple, but more thorough description of the transaction (i.e. "Groceries" or "2/17/09")
	 * 
	 */
	public void updateTransaction(long id, String payee, BigDecimal amount, long date, String memo){
		Log.d(TAG, "Updating Transaction");
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);
		amount = amount.movePointRight(2);
		
		ContentValues values = new ContentValues();
		values.put(AccountData.TRANSACTION_NAME, payee);
		values.put(AccountData.TRANSACTION_AMOUNT, amount.toPlainString());
		values.put(AccountData.TRANSACTION_DATE, date);
		values.put(AccountData.TRANSACTION_MEMO, memo);
		
		db.update(DBHelper.TRANSACTIONS_TABLE, values, "_id = " + id, null);
		db.close();
	}
	
	/**
	 * <b> public void deleteTransaction (long id)</b>
	 * 
	 * Delete transaction from the TRANSACTIONS_TABLE based on the "id" parameter.
	 * 
	 * @param id - the id of the transaction to delete.
	 */
	public void deleteTransaction(long id){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		db.delete(DBHelper.TRANSACTIONS_TABLE, "_id = " + id, null);
	}
	/**
	 * 
	 * @author jwp
	 *
	 */
	private class DBHelper extends SQLiteOpenHelper{
		private static final String SQL_NAME = "pocketBooks.db";
		private static final String ACCOUNTS_TABLE = "pocketBooksAccounts";
		private static final String TRANSACTIONS_TABLE = "pocketBooksTransactions";		
		private static final int SQL_VERSION = 1;

		public DBHelper() {
			super(context, SQL_NAME, null, SQL_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			
			try {
				// TODO Create category table
				
				//Create Accounts table
				sql = String.format("CREATE TABLE %s(_id integer primary key autoincrement, " +
						"account_name varchar, " +
						"account_balance TEXT)", ACCOUNTS_TABLE);
				Log.d(TAG, "createTable sql: " + sql);
				db.execSQL(sql);
				
				//Create Transactions table
				sql = String.format("CREATE TABLE %s(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
						"account_id INTEGER REFERENCES %s(_id) NOT NULL, " +
						"transaction_number INTEGER, transaction_amount INTEGER, " +
						"transaction_name CHARVAR, " +
						"transaction_memo CHARVAR, " +
						"transaction_category CHARVAR, " +
						"transaction_date DATE)", 
						TRANSACTIONS_TABLE, ACCOUNTS_TABLE);
				Log.d(TAG, "createTable sql: " + sql);
				db.execSQL(sql);
				
				
				//Create insert trigger
				sql = String.format("CREATE TRIGGER transaction_insert_trigger BEFORE INSERT ON %s " +
						"BEGIN " +
						"UPDATE %s SET " +
						"account_balance = (account_balance + new.transaction_amount) " +
						"WHERE _id = new.account_id; end;" 
								, TRANSACTIONS_TABLE, ACCOUNTS_TABLE);
				db.execSQL(sql);
				
				//Create account delete trigger
				sql = String.format("CREATE TRIGGER account_delete_trigger BEFORE DELETE ON %s " +
						"BEGIN " +
						"DELETE FROM %s WHERE account_id = old._id; end; ", ACCOUNTS_TABLE, TRANSACTIONS_TABLE);
				db.execSQL(sql);
				
				//Create transaction delete trigger
				sql = String.format("CREATE TRIGGER transaction_delete_trigger BEFORE DELETE ON %s " +
						"BEGIN " +
						"UPDATE %s SET " +
						"account_balance = (account_balance - old.transaction_amount) " +
						"WHERE _id = old.account_id; end;" , TRANSACTIONS_TABLE, ACCOUNTS_TABLE);
				Log.d(TAG, sql);
				db.execSQL(sql);
				
				//Create index 
				sql = String.format("CREATE INDEX account_id_index ON %s (%s)", TRANSACTIONS_TABLE, TRANSACTION_ACCOUNT_ID);
				db.execSQL(sql);
				
				//Create update trigger
				sql = String.format("CREATE TRIGGER transaction_update_trigger BEFORE UPDATE ON %s " +
						"BEGIN " +
						"UPDATE %s SET " +
						"account_balance = (account_balance - old.transaction_amount + new.transaction_amount) " +
						"WHERE _id = old.account_id; end;", TRANSACTIONS_TABLE, ACCOUNTS_TABLE);
				db.execSQL(sql);
				
				Log.d(TAG, "done with TABLES############################################");
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				Log.d(TAG, "FAIL!!!__________________----------------------------------------------------------------------------------------------------------------- : " + e);
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
				
			
		}
		
	}
}
