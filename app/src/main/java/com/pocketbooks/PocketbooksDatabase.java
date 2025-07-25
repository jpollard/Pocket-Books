package com.pocketbooks;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Account.class}, version = 3)
public abstract class PocketbooksDatabase extends RoomDatabase
{
    public abstract AccountDao accountDao();
    //public abstract TransactionDao transactionDao();
}
