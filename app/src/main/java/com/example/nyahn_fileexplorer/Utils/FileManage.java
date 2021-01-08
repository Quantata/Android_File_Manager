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

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileManage {
    private static final String TAG = FileManage.class.getSimpleName();

    Mode mode = Mode.COPY_MODE;
    // 현재 fileDataList
    ArrayList<FileData> selectedDataList;

    public void copy(File sourceFile, File targetFile) {

        // 선택된 파일이 디렉토리 였을때
        if(sourceFile.isDirectory()){
            try {
                // directory 생성, targetFile + sourceFile의 폴더 이름
                File newDirectory = new File(targetFile, sourceFile.getName());
                if(!newDirectory.exists()) {
                    // 옮길 곳에 디렉토리 하나 만들고, sourceFiles는 리스트고 sourceFile 자체의 Path로 만들어 줘야함
                    Files.createDirectories(newDirectory.toPath());

                    // 복사할거 recursive
                    // file = sourceFiles의 파일, newTargetFile은
                    // 선택된 파일의 파일 리스트
                    File[] sourceFiles = sourceFile.listFiles();
                    if (sourceFiles != null && sourceFiles.length != 0) {
                        for (File file : sourceFiles) {
                            copy(file, newDirectory);
                        }
                    }
                }

            } catch (IOException e) {
                Log.d(TAG, "Excpetion = " + e);
            }
        } else { // 선택된 파일이 빈폴더/파일이였을때
            // targetFile/sourceFile.jpg 이런식
            File newFile = new File(targetFile, sourceFile.getName());

            if(!newFile.exists()){
                try {
                    // java NIO
                    // File.copy(복사할 파일의 Path, 복사할 곳의 Path)
                    // TODO: 현재 같은 이름의 폴더 있으면 pass, 코드상엔 덮어쓰기 -> 예외처리하기
                    Files.copy(sourceFile.toPath(),
                            newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e){

                    Log.d(TAG, "Excpetion = " + e);
                }

            }
            Log.d(TAG, "newFile = " + newFile.getPath());
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
}
