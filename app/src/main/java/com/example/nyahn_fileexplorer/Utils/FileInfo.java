package com.example.nyahn_fileexplorer.Utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicLong;

public class FileInfo {
    private static final String TAG = FileInfo.class.getSimpleName();
    File file;
    Context context;


    public FileInfo(Context context, File file){
        this.context = context;
        this.file = file;
    }

    public String getFileName(){
        return file.getName();
    }
    public String getFileSize(){
        AtomicLong size = new AtomicLong(0);
        if(file.isDirectory()){
            File[] list = file.listFiles();
            for(File tempFile : list){
                size.addAndGet(calculateStorage(tempFile));
            }
        }
        else
            size.addAndGet(calculateStorage(file));

        // 파일 사이즈 읽기 쉽도록 변경
        return formatFileSize(size.get());

    }


    private String formatFileSize(long bytes) {
        return android.text.format.Formatter.formatFileSize(context, bytes);
    }

    public long calculateStorage(File pathFile){
        AtomicLong size = new AtomicLong(0);
        try {
            Files.walkFileTree(pathFile.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    size.addAndGet(attrs.size());
                    return FileVisitResult.CONTINUE;
                }
            });
            Log.d(TAG, pathFile.getName() + " = " + size);
            return size.get();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
