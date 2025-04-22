package com.pocketbooks;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Account
{
    @PrimaryKey
    public int _id;

    @ColumnInfo(name = "account_name")
    public String accountName;

    @ColumnInfo(name = "account_balance")
    public String accountBalance;
}
