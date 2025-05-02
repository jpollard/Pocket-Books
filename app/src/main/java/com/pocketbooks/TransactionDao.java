package com.pocketbooks;

import androidx.room.Query;

import java.util.List;

public class TransactionDao
{
    @Query("SELECT * FROM pocketBooksTransactions")
    List<Transaction> getAll();

    @Query("Select * FROM pocketBooksTransactions WHERE account_id LIKE id")
    List<Transaction> getTransactions(id);
}
