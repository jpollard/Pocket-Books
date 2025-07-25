package com.pocketbooks;

import androidx.room.Dao;
import androidx.room.Query;
import java.util.List;

@Dao
public interface TransactionDao
{
    @Query("SELECT * FROM pocketBooksTransactions")
    List<Transaction> getAll();

    @Query("Select * FROM pocketBooksTransactions WHERE account_id LIKE 0")
    List<Transaction> getTransactions();
}
