package com.example.nyahn_fileexplorer.Utils;

import com.example.nyahn_fileexplorer.Models.FileData;
import com.example.nyahn_fileexplorer.Models.Mode;

import java.io.File;
import java.util.ArrayList;

// holder를 이용한 초기화 방식
public class Singleton {
    // Private constructor prevents instantiation from other classes
    private Singleton() { }

    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class SingletonHolder {
        public static final Singleton INSTANCE = new Singleton();
    }

    public static Singleton getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /*
        //저장할때
        Singleton.getInstance().setCurrentMode(저장할 모드);

        //가져올때
        Mode mode = Singleton.getInstance().getCurrentMode();
        출처: https://rinear.tistory.com/entry/Android-싱글톤-클래스-매니저-클래스-구조-및-사용법 [괴도군의 블로그]
     */

    // TODO: 필요 없을듯 currentFile
    private File currentFile;
    private Mode currentMode = Mode.BASIC_MODE;
    private ArrayList<FileData> selectedFileDataList;

    public ArrayList<FileData> getSelectedFileDataList() {
        return selectedFileDataList;
    }

    public void setSelectedFileDataList(ArrayList<FileData> selectedFileDataList) {
        this.selectedFileDataList = selectedFileDataList;
    }
    public void setSelectedFileDataListClear(){
        selectedFileDataList.clear();
    }

    public Mode getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(Mode currentMode) {
        this.currentMode = currentMode;
    }

    public void setCurrentFile(File currentFile) {
        this.currentFile = currentFile;
    }

    public File getCurrentFile() {
        return currentFile;
    }



}