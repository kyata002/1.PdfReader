package com.kyata.pdfreader.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;

import com.kyata.pdfreader.data.dao.FavoriteDao;
import com.kyata.pdfreader.data.dao.HistoryDao;
import com.kyata.pdfreader.data.dao.RecentDao;
import com.kyata.pdfreader.data.model.FavoriteFile;
import com.kyata.pdfreader.data.model.RecentFile;
import com.kyata.pdfreader.data.model.SearchHistory;

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
