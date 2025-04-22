package com.pocketbooks;

public class TransactionDao
{
    @Query("SELECT * FROM transaction")
    List<Transaction> getAll();

}
