package com.pdfreaderdreamw.pdfreader.model;

import java.io.Serializable;


public class FileModel implements Serializable,Comparable<FileModel> {

    private String fileId;
    private String fileName;
    private int fileIcon;
    private String filePath;
    private long fileDate;
    private String fileDateShow;
    private long fileSize;
    private String fileSizeShow;
    private String fileType;
    private String fileFrom;
    private boolean isFavorite = false;
    private boolean isFile = false;
    private boolean isDir = false;
    private boolean fileSelected = false;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFileIcon() {
        return fileIcon;
    }

    public void setFileIcon(int fileIcon) {
        this.fileIcon = fileIcon;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileDate() {
        return fileDate;
    }

    public void setFileDate(long fileDate) {
        this.fileDate = fileDate;
    }

    public String getFileDateShow() {
        return fileDateShow;
    }

    public void setFileDateShow(String fileDateShow) {
        this.fileDateShow = fileDateShow;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileSizeShow() {
        return fileSizeShow;
    }

    public void setFileSizeShow(String fileSizeShow) {
        this.fileSizeShow = fileSizeShow;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileFrom() {
        return fileFrom;
    }

    public void setFileFrom(String fileFrom) {
        this.fileFrom = fileFrom;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }

    public boolean isDir() {
        return isDir;
    }

    public void setDir(boolean dir) {
        isDir = dir;
    }

    public boolean isFileSelected() {
        return fileSelected;
    }

    public void setFileSelected(boolean fileSelected) {
        this.fileSelected = fileSelected;
    }

    @Override
    public String toString() {
        return "FileModel{" +
                "fileId='" + fileId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileIcon='" + fileIcon + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileDate=" + fileDate +
                ", fileDateShow='" + fileDateShow + '\'' +
                ", fileSize=" + fileSize +
                ", fileSizeShow='" + fileSizeShow + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileFrom='" + fileFrom + '\'' +
                ", isFavorite=" + isFavorite +
                ", isFile=" + isFile +
                ", isDir=" + isDir +
                ", fileSelected=" + fileSelected +
                '}';
    }

    @Override
    public int compareTo(FileModel o) {
        if (this.fileName != null)
            return this.fileName.toLowerCase().compareTo(o.getFileName().toLowerCase());
        else
            throw new IllegalArgumentException();
    }

}
