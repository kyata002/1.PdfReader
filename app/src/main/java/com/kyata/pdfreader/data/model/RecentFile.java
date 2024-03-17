package com.kyata.pdfreader.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recent_table")
public class RecentFile {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "path")
    private String path;

    public RecentFile(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
