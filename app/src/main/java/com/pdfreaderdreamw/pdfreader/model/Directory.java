package com.pdfreaderdreamw.pdfreader.model;

import java.io.File;
import java.util.List;

public class Directory {
    private File currentDir;
    private List<FileModel> data;
    private int index;

    public Directory(File currentDir, List<FileModel> data) {
        this.currentDir = currentDir;
        this.data = data;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public File getCurrentDir() {
        return currentDir;
    }

    public String getName() {
        return currentDir.getName();
    }

    public void setCurrentDir(File currentDir) {
        this.currentDir = currentDir;
    }

    public List<FileModel> getData() {
        return data;
    }

    public void setData(List<FileModel> data) {
        this.data = data;
    }
}
