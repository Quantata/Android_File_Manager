package com.example.nyahn_fileexplorer.Models;

import java.io.File;

public class FileData {

    File file;
    boolean selected;   // 파일이 선택 되었는지 확인

    // 이 부분 fileList 목록 할때 사용되는데 현재 합쳐져서 사용되서 굳이 두개를 나눠야하나 생각중
    int folderNum;
    int fileNum;


    String fileExtension;



    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public int getFolderNum() {
        return folderNum;
    }

    public void setFolderNum(int folderNum) {
        this.folderNum = folderNum;
    }

    public int getFileNum() {
        return fileNum;
    }

    public void setFileNum(int fileNum) {
        this.fileNum = fileNum;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

}
