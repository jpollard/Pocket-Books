package com.pocketbooks;

import androidx.room.Query;

public class AccountDao
{
    @Query("SELECT * FROM account")
    List<Account> getAll();


}
