package com.pdfreaderdreamw.pdfreader.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.pdfreaderdreamw.pdfreader.model.FavoriteFile;

import java.util.List;

@Dao
public interface FavoriteDao {
    @Query("SELECT * FROM favorite_table ORDER BY id DESC")
    List<FavoriteFile> getList();

    @Query("SELECT * FROM favorite_table WHERE path =:path ")
    FavoriteFile getObject(String path);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void add(FavoriteFile favoriteFile);

    @Update
    void update(FavoriteFile favoriteFile);

    @Query("DELETE FROM favorite_table WHERE id =:id ")
    void delete(int id);

    @Query("DELETE FROM favorite_table WHERE path =:path ")
    void delete(String path);

    @Query("DELETE FROM favorite_table")
    void deleteAll();
}
