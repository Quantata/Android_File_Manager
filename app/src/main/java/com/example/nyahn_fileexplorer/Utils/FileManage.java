package com.example.nyahn_fileexplorer.Utils;

import android.util.Log;

import com.example.nyahn_fileexplorer.FileListAdapter;
import com.example.nyahn_fileexplorer.Models.FileData;
import com.example.nyahn_fileexplorer.Models.Mode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class FileManage {
    Mode mode = Mode.COPY_MODE;
    // 현재 fileDataList
    ArrayList<FileData> selectedDataList;

    // fileList 받고 삭제해서 돌려주기
    // 호출한 곳에서 setAdapter, notifyChanged

    // 하나의 copyFile 복사
    public void copy(File sourceFile, File targetFile) {
        // 선택된 파일의 파일 리스트
        File[] targetFiles = sourceFile.listFiles();

        // 빈 폴더 또는 파일일때
        if(targetFiles != null && targetFiles.length == 0){
            File newFile = new File(targetFile, sourceFile.getName());
            newFile.mkdir();
        }
        else {
            // 선택된 파일들의 파일 하나하나 꺼내기
            for (File file : targetFiles) {
                // 옮길 곳의 파일 객체 만듦
                File temp = new File(targetFile.getPath());
                // 선택된 파일이 디렉토리라면
                if (file.isDirectory()) {
                    // 옮길 곳에 디렉토리 하나 만들고
                    temp.mkdir();
                    // 복사할거 recursive
                    copy(file, temp);
                } else {
                    FileInputStream fis = null;
                    FileOutputStream fos = null;
                    try {
                        fis = new FileInputStream(file);
                        fos = new FileOutputStream(temp);
                        byte[] b = new byte[4096];
                        int cnt = 0;
                        while ((cnt = fis.read(b)) != -1) {
                            fos.write(b, 0, cnt);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            fis.close();
                            fos.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

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
                File directory = new File(file.getParent(), file.getName());
                File[] files = directory.listFiles();
                ArrayList<FileData> fileDatas = new ArrayList<>();
                for (File tempFile : files) {
                    fileData.setFile(tempFile);
                }
                copyFile(fileDatas, directory.getPath());
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
                    Log.i("ManageFile", "inputStream" + e);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
    }


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
