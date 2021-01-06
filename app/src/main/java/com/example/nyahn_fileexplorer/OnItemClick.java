package com.example.nyahn_fileexplorer;

import java.io.File;

public interface OnItemClick {
//    void onBackClick(File clickedFile);
    File onGetParentFile();
    void onSetParentFile(File file);
    void onSetFileList(File file);
    void onShowBottomLayout();
}
