package com.pocketbooks;

import androidx.room.Database;
import androidx.room.RoomDatabase;

public class PocketbooksDatabase
{
    @Database(entities = {Account.class}, version = 1)
    public abstract class PocketbooksRoomDatabase extends RoomDatabase
    {
        public abstract AccountDao accountDao();
    }
}
