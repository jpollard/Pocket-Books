package com.pocketbooks

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Color
import android.util.Log
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

class AccountData(var context: Context) {
    var nf = NumberFormat.getInstance(Locale.US)
    var myFormatter = NumberFormat.getInstance(Locale.US) as DecimalFormat
    var dbHelper: DBHelper
    var tableName: String? = null
    var sql: String? = null
    private var db: SQLiteDatabase? = null

    /**
     * public void close()
     *
     * closes the current SQLite database
     */
    fun close() {
        db!!.close()
    }

    /**
     * public AccountData(Context context)
     *
     * Constructor of the the AccountData class
     *
     * @param context
     */
    init {
        //this.tableName = tableName;
        dbHelper = DBHelper()
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
    fun createAccount(name: String, balance: BigDecimal) {
        var balance = balance
        val values = ContentValues()
        db = dbHelper.writableDatabase
        balance = balance.movePointRight(2)
        values.put(ACCOUNT_NAME, name)
        values.put(ACCOUNT_BALANCE, balance.toPlainString())
        Log.d(TAG, "Inital balance : " + balance.toPlainString())
        Log.d(TAG, "Account Name : $name")
        Log.d(TAG, values.toString())
        db.beginTransaction()
        try {
            db.insert(DBHelper.Companion.ACCOUNTS_TABLE, null, values)
            Log.d(TAG, "createTable sql: $name")
            db.setTransactionSuccessful()
            Log.d(TAG, "done")
        } catch (e: SQLException) {
            Log.d(TAG, "fail sql: $e")
        } finally {
            db.endTransaction()
            db.close()
            Log.d(TAG, "closed")
        }
    }// String array of columns to get from query for the cursor

    /**
     * public Cursor getAccounts()
     * returns a Cursor containing all the rows in ACCOUNTS_TABLE, unsorted.
     *
     * @return Cursor
     */
    val accounts: Cursor
        get() {
            Log.d(TAG, "Trying to get accounts")
            val cursor: Cursor

            // String array of columns to get from query for the cursor
            val columnsToQuery = arrayOf(ACCOUNT_ID, ACCOUNT_NAME, ACCOUNT_BALANCE)
            Log.d(TAG, "Trying to open DB")
            db = dbHelper.readableDatabase
            Log.d(TAG, "Opened DB")
            Log.d(TAG, "Querying DB")
            cursor = db.query(DBHelper.Companion.ACCOUNTS_TABLE, columnsToQuery, null, null, null, null, null)
            Log.d(TAG, "returning tables in a cursor. " + cursor.count)
            return cursor
        }

    /**
     * ** public void deleteAccount (Long id) **
     * Delete the account as specified by the id, as well as the transactions registered to the account.
     *
     * @param id - _id of the account
     */
    fun deleteAccount(id: Long) {
        Log.d(TAG, "delectAccount")
        val db = dbHelper.writableDatabase
        db.delete(DBHelper.Companion.ACCOUNTS_TABLE, "_id = $id", null)
    }

    /**
     * ** public Cursor getAccountInfo (Long id) **
     * This returns a Cursor that is populated with the info of one of the ACCOUNTS_TABLE rows, specified by
     * the 'id' parameter.
     *
     * @param id
     * @return Cursor
     */
    fun getAccountInfo(id: Long): Cursor {
        Log.d(TAG, "trying to get account info for account_id $id")
        val cursor: Cursor
        val columnsToQuery = arrayOf(ACCOUNT_ID, ACCOUNT_NAME, ACCOUNT_BALANCE)
        Log.d(TAG, "Trying to open DB")
        db = dbHelper.readableDatabase
        cursor = db.query(DBHelper.Companion.ACCOUNTS_TABLE, columnsToQuery, ACCOUNT_ID + " like " + java.lang.Long.toString(id), null, null, null, null)
        Log.d(TAG, "number of rows" + cursor.count + " : " + cursor.toString())
        Log.d(TAG, "returning tables in a cursor")
        return cursor
    }

    /**
     * ** public Cursor getTransactionInfo(Long id) **
     * Get the details of the transaction that has the _id passed in by the parameter.
     *
     * @param id - the transaction id of the transaction that the info needs to come from
     * @return - cursor populated with the request data
     */
    fun getTransactionInfo(id: Long): Cursor {
        Log.d(TAG, "trying to get transaction info for transaction_id $id")
        val cursor: Cursor
        val columnsToQuery = arrayOf(TRANSACTION_NAME, TRANSACTION_AMOUNT, TRANSACTION_DATE, TRANSACTION_CATEGORY, TRANSACTION_MEMO)
        db = dbHelper.readableDatabase
        cursor = db.query(DBHelper.Companion.TRANSACTIONS_TABLE, columnsToQuery, TRANSACTION_ID + " LIKE " + java.lang.Long.toString(id), null, null, null, null)
        Log.d(TAG, cursor.toString())
        return cursor
    }

    /**
     * Get the list of "transactions" from the TRANSACTION_TABLE based on the "id" of the the account that
     * they are associated with.
     * public Cursor getTransactions(long id)
     *
     * @param id - the account from which transactions are being requested.
     * @return - cursor of the transactions based on the id passed in.
     */
    fun getTransactions(id: Long): Cursor {
        Log.d(TAG, "Trying to get transactions")
        val cursor: Cursor
        val columnsToQuery = arrayOf(TRANSACTION_ID, TRANSACTION_ACCOUNT_ID, TRANSACTION_NAME, TRANSACTION_AMOUNT, TRANSACTION_DATE, TRANSACTION_CATEGORY, TRANSACTION_MEMO)
        Log.d(TAG, "Trying to open DB")
        val db = dbHelper.readableDatabase
        Log.d(TAG, "Opened DB")
        Log.d(TAG, "Querying DB")
        cursor = db.query(DBHelper.Companion.TRANSACTIONS_TABLE, columnsToQuery, TRANSACTION_ACCOUNT_ID + " like " + id, null, null, null, TRANSACTION_DATE + " DESC")
        Log.d(TAG, "returning tables in a cursor")
        try {
            Log.d(TAG, cursor.getColumnName(cursor.getColumnIndexOrThrow(TRANSACTION_ID)))
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
        }
        return cursor
    }

    /**
     * **public void addTransaction(long id, String payee, BigDecimal amount, String date, String memo)**
     * Add a new transaction to the TRANSACTIONS_TABLE, where "id" is the account id to associate the
     * transaction with it, "payee" is the person the transaction is for/from, "amount" is the amount,
     * "date" is the date, "memo" is the memo of the transaction.
     *
     * @param id     - the account_id that the transaction belongs to
     * @param payee  - a string to describe the name of the transaction (i.e. "Walmart" or "Paycheck")
     * @param amount - the amount of the transaction
     * @param date   - a string date of the transaction
     * @param memo   - a simple, but more thorough description of the transaction (i.e. "Groceries" or "2/17/09 - 2/24/09")
     */
    fun addTransaction(id: Long, payee: String?, amount: BigDecimal, date: Long, memo: String?) {
        var amount = amount
        val db = dbHelper.writableDatabase
        amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP)
        amount = amount.movePointRight(2)
        val values = ContentValues()
        values.put(TRANSACTION_ACCOUNT_ID, id)
        values.put(TRANSACTION_NAME, payee)
        values.put(TRANSACTION_AMOUNT, amount.toPlainString())
        values.put(TRANSACTION_DATE, date)
        values.put(TRANSACTION_MEMO, memo)
        db.insert(DBHelper.Companion.TRANSACTIONS_TABLE, null, values)
        db.close()
    }

    /**
     * ** public void updateTransaction (long id, String payee, BigDecimal amount, String date, String memo)**
     * Update the transaction represented by id to the new values that have been passed in.
     *
     * @param id - the transaction id
     * @param payee - a string to describe the name of the transaction (i.e. "Walmart" or "Paycheck")
     * @param amount - the amount of the transaction
     * @param date - a string date of the transaction
     * @param memo - a simple, but more thorough description of the transaction (i.e. "Groceries" or "2/17/09")
     */
    fun updateTransaction(id: Long, payee: String?, amount: BigDecimal, date: Long, memo: String?) {
        var amount = amount
        Log.d(TAG, "Updating Transaction")
        val db = dbHelper.writableDatabase
        amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP)
        amount = amount.movePointRight(2)
        val values = ContentValues()
        values.put(TRANSACTION_NAME, payee)
        values.put(TRANSACTION_AMOUNT, amount.toPlainString())
        values.put(TRANSACTION_DATE, date)
        values.put(TRANSACTION_MEMO, memo)
        db.update(DBHelper.Companion.TRANSACTIONS_TABLE, values, "_id = $id", null)
        db.close()
    }

    /**
     * ** public void deleteTransaction (long id)**
     * Delete transaction from the TRANSACTIONS_TABLE based on the "id" parameter.
     *
     * @param id - the id of the transaction to delete.
     */
    fun deleteTransaction(id: Long) {
        val db = dbHelper.writableDatabase
        db.delete(DBHelper.Companion.TRANSACTIONS_TABLE, "_id = $id", null)
    }

    /**
     *
     * @author jwp
     */
    inner class DBHelper : SQLiteOpenHelper(context, Companion.SQL_NAME, null, Companion.SQL_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            // TODO Auto-generated method stub
            try {
                // TODO Create category table

                //Create Accounts table
                sql = String.format("CREATE TABLE %s(_id integer primary key autoincrement, " +
                        "account_name varchar, " +
                        "account_balance TEXT)", Companion.ACCOUNTS_TABLE)
                Log.d(TAG, "createTable sql: $sql")
                db.execSQL(sql)

                //Create Transactions table
                sql = String.format("CREATE TABLE %s(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "account_id INTEGER REFERENCES %s(_id) NOT NULL, " +
                        "transaction_number INTEGER, transaction_amount INTEGER, " +
                        "transaction_name CHARVAR, " +
                        "transaction_memo CHARVAR, " +
                        "transaction_category CHARVAR, " +
                        "transaction_date DATE)",
                        Companion.TRANSACTIONS_TABLE, Companion.ACCOUNTS_TABLE)
                Log.d(TAG, "createTable sql: $sql")
                db.execSQL(sql)


                //Create insert trigger
                sql = String.format("CREATE TRIGGER transaction_insert_trigger BEFORE INSERT ON %s " +
                        "BEGIN " +
                        "UPDATE %s SET " +
                        "account_balance = (account_balance + new.transaction_amount) " +
                        "WHERE _id = new.account_id; end;", Companion.TRANSACTIONS_TABLE, Companion.ACCOUNTS_TABLE)
                db.execSQL(sql)

                //Create account delete trigger
                sql = String.format("CREATE TRIGGER account_delete_trigger BEFORE DELETE ON %s " +
                        "BEGIN " +
                        "DELETE FROM %s WHERE account_id = old._id; end; ", Companion.ACCOUNTS_TABLE, Companion.TRANSACTIONS_TABLE)
                db.execSQL(sql)

                //Create transaction delete trigger
                sql = String.format("CREATE TRIGGER transaction_delete_trigger BEFORE DELETE ON %s " +
                        "BEGIN " +
                        "UPDATE %s SET " +
                        "account_balance = (account_balance - old.transaction_amount) " +
                        "WHERE _id = old.account_id; end;", Companion.TRANSACTIONS_TABLE, Companion.ACCOUNTS_TABLE)
                Log.d(TAG, sql!!)
                db.execSQL(sql)

                //Create index 
                sql = String.format("CREATE INDEX account_id_index ON %s (%s)", Companion.TRANSACTIONS_TABLE, TRANSACTION_ACCOUNT_ID)
                db.execSQL(sql)

                //Create update trigger
                sql = String.format("CREATE TRIGGER transaction_update_trigger BEFORE UPDATE ON %s " +
                        "BEGIN " +
                        "UPDATE %s SET " +
                        "account_balance = (account_balance - old.transaction_amount + new.transaction_amount) " +
                        "WHERE _id = old.account_id; end;", Companion.TRANSACTIONS_TABLE, Companion.ACCOUNTS_TABLE)
                db.execSQL(sql)
                Log.d(TAG, "done with TABLES############################################")
            } catch (e: SQLException) {
                // TODO Auto-generated catch block
                Log.d(TAG, "FAIL!!!__________________----------------------------------------------------------------------------------------------------------------- : $e")
            }
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            // TODO Auto-generated method stub
        }

        companion object {
            private const val SQL_NAME = "pocketBooks.db"
            const val ACCOUNTS_TABLE = "pocketBooksAccounts"
            const val TRANSACTIONS_TABLE = "pocketBooksTransactions"
            private const val SQL_VERSION = 1
        }
    }

    companion object {
        @JvmField
        var GREEN = Color.parseColor("#216C2A")
        var YELLOW = Color.parseColor("#F6FFDA")
        var GHOST_WHITE = Color.parseColor("#E8E9F3")
        var SILVER = Color.parseColor("#CECECE")

        @JvmField
        var RED = Color.parseColor("#C6372F")
        var YELLOWGREEN = Color.parseColor("#bfdeb9")

        @JvmField
        var ACCOUNT_NAME = "account_name"

        @JvmField
        var ACCOUNT_BALANCE = "account_balance"

        @JvmField
        var ACCOUNT_ID = "_id"
        var TRANSACTION_ID = "_id"
        var TRANSACTION_ACCOUNT_ID = "account_id"
        var TRANSACTION_NUMBER = "transaction_number"

        @JvmField
        var TRANSACTION_AMOUNT = "transaction_amount"

        @JvmField
        var TRANSACTION_NAME = "transaction_name"

        @JvmField
        var TRANSACTION_MEMO = "transaction_memo"

        @JvmField
        var TRANSACTION_CATEGORY = "transaction_category"

        @JvmField
        var TRANSACTION_DATE = "transaction_date"
        private val TAG = AccountData::class.java.simpleName
    }
}