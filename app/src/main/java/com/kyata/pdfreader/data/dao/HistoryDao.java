package com.kyata.pdfreader.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.kyata.pdfreader.data.model.SearchHistory;

import java.util.List;

@Dao
public interface HistoryDao {
    @Query("SELECT * FROM search_table ORDER BY id DESC")
    List<SearchHistory> getList();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void add(SearchHistory searchHistory);


    @Query("DELETE  FROM search_table WHERE id=:id")
    void delete(int id);

    @Query("DELETE  FROM recent_table")
    void deleteAll();
}
