package com.kyata.pdfreader.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.kyata.pdfreader.model.RecentFile;

import java.util.List;

@Dao
public interface RecentDao {
    @Query("SELECT * FROM recent_table ORDER BY id DESC")
    List<RecentFile> getList();

    @Query("SELECT * FROM recent_table WHERE path=:path")
    RecentFile getObject(String path);

    @Update
    void update(RecentFile recentFile);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void add(RecentFile recentFile);

    @Query("DELETE  FROM recent_table WHERE path=:path")
    void delete(String path);

    @Query("DELETE  FROM recent_table WHERE id=:id")
    void delete(int id);

    @Query("DELETE  FROM recent_table")
    void deleteAll();

}
