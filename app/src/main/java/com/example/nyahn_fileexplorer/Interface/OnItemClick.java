package com.example.nyahn_fileexplorer.Interface;

import com.example.nyahn_fileexplorer.Models.Mode;

import java.io.File;

public interface OnItemClick {
//    void onBackClick(File clickedFile);

    // FileListAdapter.java
    File onGetCurrentFile();
    void onSetCurrentFile(File file);
    void onSetFileList(File file);
    void onShowBottomLayout();
    Mode onGetMode();
    void onSetMode(Mode mode);

    // DirectoryList
    void onAddDirectoryList(File addFile);
    void onBackDirectoryList(int clickedPosition);

    // 다중 선택시 이름 변경 불가 / 이동시 현재 폴더에서 불가
//    void onSetChangeStatus(Mode mode, boolean active);
    void onSetChangeStatus(boolean active);
}
