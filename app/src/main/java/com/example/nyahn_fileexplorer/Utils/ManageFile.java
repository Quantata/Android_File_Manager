package com.example.nyahn_fileexplorer.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ManageFile {
    Mode mode = Mode.COPY_MODE;
    ArrayList<FileData> fileDataList;

    // fileList 받고 삭제해서 돌려주기
    // 호출한 곳에서 setAdapter, notifyChanged

    // file 복사, 이건 붙여넣기할 때 받아와
    public void copyFile(ArrayList<FileData> fileDataList, String outputPath){
        InputStream inputStream = null;
        OutputStream outputStream = null;
        fileDataList = fileDataList;

        for(FileData fileData : fileDataList) {
            File file = fileData.getFile();
            // 폴더 일때 recursive
            if (!file.isFile()) {    // 파일이 아니면

                try {
                    // 현재 파일 Path
                    String inputPath = fileDataList.get(0).getFile().getPath();

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

            } else { // 파일 일때 그냥 붙여 넣기
                System.out.println("파일일때 그냥 붙여넣기");
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
    public void pasteFile(){

    }
}
