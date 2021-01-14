package com.example.nyahn_fileexplorer.Utils;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.nyahn_fileexplorer.Models.FileData;
import com.example.nyahn_fileexplorer.Models.Mode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class FileManage {
    private static final String TAG = FileManage.class.getSimpleName();


    public void delete(File sourceFile){
        // 파일이 존재 한다면
        if(sourceFile.exists()){
            File[] list = sourceFile.listFiles();
            try {
                if(list == null || list.length == 0) {  // 빈 폴더이거나 파일일때

                    Log.d(TAG, sourceFile.getName() + "파일 삭제 성공");

                    Files.delete(sourceFile.toPath());

                } else {   // 디렉토리일 경우
                    for (File file : list) {
                        delete(file);
                    }
                    // 마지막에 자기자신 삭제
                    Files.delete(sourceFile.toPath());
                    Log.d(TAG, sourceFile.getName() + "디렉토리 삭제 성공");
                }
            } catch (IOException e){
                Log.d(TAG, "파일 삭제 실패 =" + e);
            }
        }
    }

    // 파일 복사하기 - 완료
    public void copy(File sourceFile, File targetFile) {
        File newDirectory = new File(targetFile, sourceFile.getName());

        if(!newDirectory.exists()) { // newDirectory(복사할 곳 + 복사한 파일의 이름)이 현재 path에 존재 하지 않으면
            // 복사할거 recursive
            // 선택된 파일의 파일 리스트
            File[] sourceFiles = sourceFile.listFiles();
            try {
                if(sourceFiles == null || sourceFiles.length == 0){ // 빈폴더나 파일일 경우
                    // java NIO
                    // File.copy(복사할 파일의 Path, 복사할 곳의 Path)
                    // TODO: 현재 같은 이름의 폴더 있으면 pass, 코드상엔 덮어쓰기 -> 예외처리하기(이름변경 버튼 누르면 ->(1) 이렇게 만들어 버림)
                    Files.copy(sourceFile.toPath(),
                            newDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    Log.d(TAG, "newDirectoryFile = " + newDirectory.getPath());
                }
                else {  // 복사할 파일이 파일 or 빈폴더가 아니면
                    Files.createDirectories(newDirectory.toPath());

                    // 선택된 파일 안에 파일 리스트들을 newDirectory안에 복사
                    for (File file : sourceFiles) {
                        copy(file, newDirectory);
                    }

                }
            } catch (IOException e){

                Log.d(TAG, "복사 Exception = " + e);
            }
        }
        else {
            // getName(1) 과 같이 바꾸는 과정 추가
        }
    }


    // 붙여넣기시 mode확인하고 진행
    // fileDataList : 현재 선택된 파일 리스트
    // outputPath : 붙여넣기할 Path
    public void pasteFile(Mode mode, ArrayList<FileData> fileDataList, File outputFile){
        if(mode == Mode.COPY_MODE){
            for(FileData fileData : fileDataList) {
                File file = fileData.getFile();
                copy(file, outputFile);
//                copyFile(fileDataList, outputPath);
            }
        }
        if (mode == Mode.MOVE_MODE){
            // 복사 하고

            // 삭제하는 부분
        }
    }

    public void deleteFile(ArrayList<FileData> selectedList){
        for(FileData fileData : selectedList){
            delete(fileData.getFile());
        }
    }

}
