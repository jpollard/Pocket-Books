package com.pocketbooks;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "pocketBooksTransactions")
public class Transaction
{
    @PrimaryKey
    public int _id;

    @ColumnInfo(name = "account_id")
    public int accountId;

    @ColumnInfo(name = "transaction_number")
    public int transactionNumber;

    @ColumnInfo(name = "transaction_amount")
    public int transactionAmount;

    @ColumnInfo(name = "transaction_name")
    public String transactionName;

    @ColumnInfo(name = "transaction_memo")
    public String transactionMemo;

    @ColumnInfo(name = "transaction_category")
    public String transactionCategory;

    @ColumnInfo(name = "transaction_date")
    public Date transactionDate;
}
