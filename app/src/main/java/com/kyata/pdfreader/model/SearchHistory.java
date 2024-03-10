package com.kyata.pdfreader.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "search_table")
public class SearchHistory {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String text;

    public SearchHistory(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
