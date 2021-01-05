package com.example.nyahn_fileexplorer;

public class FileData {
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getParentDir() {
        return parentDir;
    }

    public void setParentDir(String parentDir) {
        this.parentDir = parentDir;
    }

    String fileName; // 파일 이름
    String parentDir; // 상위 디렉토리
    String presentDir; // 현재 디렉토리

    public String getPresentDir() {
        return presentDir;
    }

    public void setPresentDir(String presentDir) {
        this.presentDir = presentDir;
    }



}
