package com.pocketbooks;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pocketBooksAccounts")
public class Account
{
    @PrimaryKey(autoGenerate = true)
    public int _id;

    @ColumnInfo(name = "account_name")
    public String accountName;

    @ColumnInfo(name = "account_balance")
    public String accountBalance;
}
