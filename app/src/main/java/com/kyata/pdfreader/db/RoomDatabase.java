package com.kyata.pdfreader.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;

import com.kyata.pdfreader.dao.FavoriteDao;
import com.kyata.pdfreader.dao.HistoryDao;
import com.kyata.pdfreader.dao.RecentDao;
import com.kyata.pdfreader.model.FavoriteFile;
import com.kyata.pdfreader.model.RecentFile;
import com.kyata.pdfreader.model.SearchHistory;

@Database(entities = {FavoriteFile.class, RecentFile.class, SearchHistory.class}, version = 7, exportSchema = false)
public abstract class RoomDatabase extends androidx.room.RoomDatabase {
    public abstract FavoriteDao favoritetDao();

    public abstract RecentDao recentDao();

    public abstract HistoryDao historyDao();

    private static RoomDatabase instance;

    public static RoomDatabase getDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, RoomDatabase.class, "pdf_db").allowMainThreadQueries()
                    .fallbackToDestructiveMigration().build();
        }
        return instance;
    }
}
