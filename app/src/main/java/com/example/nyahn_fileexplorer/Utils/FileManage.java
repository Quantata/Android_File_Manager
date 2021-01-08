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
        // 선택된 파일의 파일 리스트
        File[] sourceFiles = sourceFile.listFiles();

        // 선택된 파일이 빈 폴더 또는 파일일때 - 완료
        if(sourceFiles != null && sourceFiles.length == 0){
            // Oreo 이상만 가능
            File newFile = new File(targetFile, sourceFile.getName());

            if(!newFile.exists()){
                try {
                    // java NIO
//                     File.copy(복사할 파일의 Path, 복사할 곳의 Path)
                    Files.copy(sourceFile.toPath(),
                            newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e){

                    Log.d(TAG, "Excpetion = " + e);
                }

            }
            Log.d(TAG, "newFile = " + newFile.getPath());

        }
        else {
            // 선택된 파일들의 파일 하나하나 꺼내기
            for (File file : sourceFiles) {
                // TODO: file.exists()를 통해 같은 이름을 갖고 있는 폴더 있을 경우 처리

                // 옮길 곳의 파일 객체 만듦
                File newTargetFile = new File(targetFile.getPath(), file.getName());

                // 선택된 파일이 디렉토리라면
                if (file.isDirectory()) {
                    try {
                        // 옮길 곳에 디렉토리 하나 만들고
                        Files.createDirectory(newTargetFile.toPath());
//                        Files.copy(sourceFile.toPath(),
//                                temp.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                        // 복사할거 recursive
                        copy(file, newTargetFile);
                    } catch (IOException e){
                        Log.d(TAG, "Excpetion = " + e);
                    }

                } else {
                    try {
                        // File.copy(복사할 파일의 Path, 복사할 곳의 Path)
                        Files.copy(file.toPath(),
                                newTargetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e){
                        Log.d(TAG, "Excpetion = " + e);
                    }
                }
//                    FileInputStream fis = null;
//                    FileOutputStream fos = null;
//                    try {
//                        fis = new FileInputStream(file);
//                        fos = new FileOutputStream(temp);
//                        byte[] b = new byte[4096];
//                        int cnt = 0;
//                        while ((cnt = fis.read(b)) != -1) {
//                            fos.write(b, 0, cnt);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    } finally {
//                        try {
//                            fis.close();
//                            fos.close();
//                        } catch (IOException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                    }
//                }
            }
        }
    }
/*
    // 복사될 파일 리스트
    // 여러개 선택된 파일들 복사할 때
    public void copyFile(ArrayList<FileData> selectedDataList, String outputPath) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        this.selectedDataList = selectedDataList;

        for (FileData fileData : selectedDataList) {
            File file = fileData.getFile();
            // 폴더 일때 recursive
            if (file.isDirectory()) {    // 디렉토리 이면
//                File directory = new File(file.getParent(), file.getName());
//                File[] files = directory.listFiles();
//                ArrayList<FileData> fileDatas = new ArrayList<>();
//                for (File tempFile : files) {
//                    fileData.setFile(tempFile);
//                }
//                copyFile(fileDatas, directory.getPath());
            }else{ // 파일 일때 그냥 붙여 넣기
                System.out.println("파일일때 그냥 붙여넣기");
                try {
                    // 현재 파일 Path
                    String inputPath = file.getPath();

                    inputStream = new FileInputStream(inputPath + outputPath);
                    outputStream = new FileOutputStream(outputPath + inputPath);

                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, read);
                    }
                    inputStream.close();
                    inputStream = null;

                    // write the output file (You have now copied the file)
                    outputStream.flush();
                    outputStream.close();
                    outputStream = null;

                } catch (FileNotFoundException e) {
                    Log.d(TAG, "inputStream" + e);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
    }
*/

    // file 이동
//    public void moveFile(ArrayList<FileData> fileData, Mode mode){
//        mode = Mode.MOVE_MODE;
//        // 폴더 일때 recursive
//
//        // 파일 일때 그냥 붙여 넣기
//    }

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
