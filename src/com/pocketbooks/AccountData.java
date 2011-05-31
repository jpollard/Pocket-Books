package com.pocketbooks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AccountData {
	public static String ACCOUNT_NAME = "account_name";
	public static String ACCOUNT_BALANCE = "account_balance";
	public static String _ID = "_id";
	public static String TRANSACTION_NUMBER = "transaction_number";
	public static String TRANSACTION_AMOUNT = "transaction_amount"; 
	public static String TRANSACTION_NAME = "transaction_name";
	public static String TRANSACTION_MEMO = "transaction_memo";
	public static String TRANSACTION_CATEGORY = "transaction_category";
	public static String TRANSACTION_DATE = "transaction_date"; 
	
	private static final String TAG = AccountData.class.getSimpleName();
	Context context;
	DBHelper dbHelper;
	String tableName;
	String sql;
	SQLiteDatabase db2;
	
	public AccountData(Context context){
		this.context = context;
		//this.tableName = tableName;
		dbHelper = new DBHelper();
	}
	
	/**
	 * create account table
	 * @param name
	 */
	public void createTable(String name, int balance){
		ContentValues values = new ContentValues();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
				
		values.put(ACCOUNT_NAME, name);
		values.put(ACCOUNT_BALANCE, balance);
		db.beginTransaction();
		
		try{
			db.insert(DBHelper.ACCOUNTS_TABLE, null, values);
			Log.d(TAG, "createTable sql: " + name);
			db.setTransactionSuccessful();
		}catch(SQLException e){
			Log.d(TAG, "fail sql: " + e);
		}finally{
			db.endTransaction();
			db.close();
		}		
	}
	
	/**
	 * 
	 * @return Cursor
	 */
	public Cursor getTables(){
		Log.d(TAG, "Trying to get tables");
		Cursor cursor;
		
		// String array of colums to get from query for the cursor
		String[] columnsToQuery = {_ID, ACCOUNT_NAME, ACCOUNT_BALANCE};
		Log.d(TAG, "Trying to open DB");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Log.d(TAG, "Opened DB");
		Log.d(TAG, "Querying DB");
		
		cursor = db.query(DBHelper.ACCOUNTS_TABLE, columnsToQuery, null, null, null, null, null);
		Log.d(TAG, "returning tables in a cursor.");
		return cursor;
		
	}
	
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
				sql = String.format("CREATE TABLE %s(_id int primary key, account_name varchar, account_balance int)", ACCOUNTS_TABLE);
				Log.d(TAG, "createTable sql: " + sql);
				db.execSQL(sql);
				sql = String.format("CREATE TABLE %s(_id int primary key, " +
						"account_name varchar REFERENCES %s(account_name) NOT NULL, " +
						"transaction_number int, transaction_amount int, " +
						"transaction_name charvar, " +
						"transaction_memo charvar, " +
						"transaction_category charvar, " +
						"transaction_date int)", 
						TRANSACTIONS_TABLE, ACCOUNTS_TABLE);
				Log.d(TAG, "createTable sql: " + sql);
				db.execSQL(sql);
				Log.d(TAG, "done with TABLES");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				Log.d(TAG, "FAIL!!! : " + e);
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
				
			
		}
		
	}
}
