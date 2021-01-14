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

        // 선택된 파일이 디렉토리 였을때
        if(sourceFile.isDirectory()){
            try {
                // directory 객체 생성, targetFile + sourceFile의 폴더 이름
                File newDirectory = new File(targetFile, sourceFile.getName());
                if(!newDirectory.exists()) {
                    // 옮길 곳에 디렉토리 하나 만들고, sourceFiles는 리스트고 sourceFile 자체의 Path로 만들어 줘야함
                    // 빈 폴더인 경우 여기서 끝
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
                } else { // 같은 이름 Directory가 이미 존재할 때
                    // if(Dialog 결과값: 덮어쓰기 == true) { 기존 directory삭제 및 다시 copy 진행 } else { move로 이름 바꿔서 진행 }
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
                    // TODO: 현재 같은 이름의 폴더 있으면 pass, 코드상엔 덮어쓰기 -> 예외처리하기(이름변경 버튼 누르면 ->(1) 이렇게 만들어 버림)
                    Files.copy(sourceFile.toPath(),
                            newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e){

                    Log.d(TAG, "Excpetion = " + e);
                }

            } else {    // 덮어쓰기 또는 이름 변경, 건너뛰기 alert 띄우기
                // if (다이얼로그 결과: 덮어쓰기 == true) { replacing } else { move, rename }
            }
            Log.d(TAG, "newFile = " + newFile.getPath());
        }
*/
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
