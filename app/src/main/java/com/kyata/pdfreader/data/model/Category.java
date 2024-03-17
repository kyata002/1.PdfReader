package com.kyata.pdfreader.data.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Category implements Serializable {
    private String title;
    private String type;
    private List<ItemFile> list = new ArrayList<>();

    public Category(String title, String type) {
        this.title = title;
        this.type = type;
    }

    public void clearData() {
        list.clear();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ItemFile> getList() {
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }

    public void setList(List<ItemFile> list) {
        this.list = list;
    }

    public void addFile(File file, int page) {
        list.add(new ItemFile(file.getPath(), page));
    }

    public void addFile(int index, File file, int page) {
        list.add(index, new ItemFile(file.getPath(), page));
    }

    public void addFile(File file) {
        list.add(new ItemFile(file.getPath()));
    }

    public void addFile(int index, File file) {
        list.add(index, new ItemFile(file.getPath()));
    }

    @Override
    public String toString() {
        return "Category{" +
                "title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", list=" + list.size() +
                '}';
    }
}
