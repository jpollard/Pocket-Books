package com.pocketbooks;

import android.database.Cursor;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface AccountDao
{
    @Query("SELECT * FROM pocketBooksAccounts")
    Cursor getAll();

    @Insert
    void createAccount(String name, int balance);

    @Delete
    void deleteAccount(Account account);

    @Update
    void updateAccount(Account account);

}
