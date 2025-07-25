package com.pocketbooks;

import android.database.Cursor;
import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface AccountDao
{
    @Query("SELECT * FROM pocketBooksAccounts")
    Cursor getAll();
}
