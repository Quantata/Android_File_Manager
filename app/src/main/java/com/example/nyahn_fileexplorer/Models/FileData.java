package com.example.nyahn_fileexplorer.Models;

import java.io.File;

public class FileData {

    File file;
    boolean selected;   // 파일이 선택 되었는지 확인

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
