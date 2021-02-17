package com.example.nyahn_fileexplorer.Utils;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.nyahn_fileexplorer.Activity.FileListActivity;
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

    Context context;
    public FileManage(){}
    public FileManage(Context context){
        this.context = context;
    }

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

        // 파일이 존재할 경우
        if(newDirectory.exists()){
            int i = 0;

            while(newDirectory.exists()) {
                i++;
                String fileName = sourceFile.getName();

                if(newDirectory.isDirectory())
                    newDirectory = new File(targetFile, sourceFile.getName() + " (" + i + ")");
                else {
                    String realName = fileName.substring(0, fileName.lastIndexOf("."));
                    String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

                    newDirectory = new File(targetFile, realName + " (" + i + ")." + extension);
//                newDirectory = new File(targetFile, sourceFile.getName() + " (" + i + ")");
                }
            }
        }

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
    }

    public void rename(File sourceFile, String rename){
        File file = new File(sourceFile.getPath());
        File newFile = new File(file.getParent(), rename);

        // TODO: 이미 있는 이름이면 처리
        try {
            Files.move(file.toPath(), newFile.toPath());
        } catch (IOException e){
            Log.d(TAG, "파일 이름 변경 = " + rename);
        }
    }

    // 이동 구현
    public void move(File sourceFile, File targetFile){
        Log.d(TAG, "이동중.");
        File newDirectory = new File(targetFile, sourceFile.getName());

        // 파일이 존재할 경우
        if(newDirectory.exists()){
            int i = 0;
            while(newDirectory.exists()) {
                i++;
                newDirectory = new File(targetFile, sourceFile.getName() + " (" + i + ")");
            }
        }

        if(!newDirectory.exists()) { // newDirectory(복사할 곳 + 복사한 파일의 이름)이 현재 path에 존재 하지 않으면
            // 복사할거 recursive
            // 선택된 파일의 파일 리스트
            File[] sourceFiles = sourceFile.listFiles();
            try {
                if(sourceFiles == null || sourceFiles.length == 0){ // 빈폴더나 파일일 경우
                    // java NIO
                    // File.move(이동할 파일의 Path, 이동할 곳의 Path)
                    // TODO: 현재 같은 이름의 폴더 있으면 pass, 코드상엔 덮어쓰기 -> 예외처리하기(이름변경 버튼 누르면 ->(1) 이렇게 만들어 버림)
                    Files.move(sourceFile.toPath(),
                            newDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    Log.d(TAG, "newDirectoryFile = " + newDirectory.getPath());
                }
                else {  // 복사할 파일이 파일 or 빈폴더가 아니면
                    Files.createDirectories(newDirectory.toPath());

                    // 선택된 파일 안에 파일 리스트들을 newDirectory안에 복사
                    for (File file : sourceFiles) {
                        move(file, newDirectory);
                    }

                    delete(sourceFile);
                }
            } catch (IOException e){

                Log.d(TAG, "복사 Exception = " + e);
            }
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
            // 대상 경로 파일
            String outputPath = outputFile.getPath();
            String sourcePath = "";

            boolean isSub = true;
            for(FileData fileData : fileDataList) {
                File file = fileData.getFile();
                // 소스 경로 파일
                sourcePath = file.getPath();

                // 복사된 파일이 디렉토리인 경우 자신의 디렉토리로 복사하는건지 확인
                if (file.isDirectory()) {
                    // outputPath가 sourcePath로 시작하면 진짜 시작하는건지 하나하나 확인
                    // 아니면 .../ScreenShot과 .../ScreenShot (1) 을 동일 폴더로 착각할 수 있음
                    if (outputPath.startsWith(sourcePath)) {
                        String[] outputAry = outputPath.split("/");
                        String[] inputAry = sourcePath.split("/");
                        for (int i = 0; i < inputAry.length; i++) {
                            if (!inputAry[i].equals(outputAry[i])) {
                                isSub = false;
                                break;
                            }

                        }
                        if(isSub){
                            Toast.makeText(context, "폴더를 복사할 수 없습니다. 대상폴더가 복사하려는 폴더의 하위폴더 입니다.", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }

                // 현재 소스파일이 있는 곳일 경우
                if (outputPath.equals(file.getParent())) {
                    Toast.makeText(context, "대상폴더와 소스폴더가 같습니다.", Toast.LENGTH_SHORT).show();
                    break;
                }

                move(file, outputFile);
            }
        }
    }

    // 삭제
    public void deleteFile(ArrayList<FileData> selectedList){
        for(FileData fileData : selectedList){
            delete(fileData.getFile());
        }
    }

    // 이름 변경
    public void renameFile(ArrayList<FileData> selectedList, String rename){
        for(FileData fileData : selectedList){
            rename(fileData.getFile(), rename);
        }
    }
}
