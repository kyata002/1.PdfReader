package com.kyata.pdfreader.data.model;

public class FilePdf {
    private String name;
    private String path;

    public FilePdf(String name, String path) {
        super();
        this.name = name;
        this.path = path;
    }

    public FilePdf() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
