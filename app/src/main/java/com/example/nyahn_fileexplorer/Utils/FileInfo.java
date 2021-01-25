package com.example.nyahn_fileexplorer.Utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class FileInfo {
    private static final String TAG = FileInfo.class.getSimpleName();
    File file;
    Context context;

    AtomicLong fileSize = new AtomicLong(0);
    AtomicInteger numDir = new AtomicInteger(0);
    AtomicInteger numNonDir = new AtomicInteger(0);

    public FileInfo(Context context, File file){
        this.context = context;
        this.file = file;
    }

    public String getTotalMemory(){
        return formatFileSize(calculateTotalMemory());
    }
    // 파일 전체 용량
    public long calculateTotalMemory() {
//        StatFs stat = new StatFs(file.getPath());
//        long blockSize = 0;
//        long totalBlocks = 0;
//
//        blockSize = stat.getBlockSizeLong();
//        totalBlocks = stat.getBlockCountLong();
        long totalSpace = file.getTotalSpace();
        File rootFile = new File(Environment.getRootDirectory().getPath());
        totalSpace = totalSpace + rootFile.getTotalSpace();

        return  totalSpace;
    }

    // 사용 가능한 용량
    public String getUsingMemory() {
//        StatFs stat = new StatFs(file.getPath());
//        long blockSize = 0;
//        long availableBlocks = 0;
//        long usingSize = 0;
//        long totalSize = stat.getBlockSizeLong() * stat.getBlockCountLong();
//
//        blockSize = stat.getBlockSizeLong();
//        availableBlocks = stat.getAvailableBlocksLong();
//
//        usingSize = totalSize - (blockSize * availableBlocks);
        long freeSize = file.getFreeSpace();
        File rootFile = new File(Environment.getRootDirectory().getPath());
        freeSize = freeSize + rootFile.getFreeSpace();
        return formatFileSize(calculateTotalMemory() - freeSize);
    }



    // 파일 이름 가져오기
    public String getFileName(){
        return file.getName();
    }

    // 파일 사이즈 가져오기
    public String getFileSize(){
        if(file.isDirectory()){
            File[] list = file.listFiles();
            for(File tempFile : list){
                calculateStorage(tempFile);
            }
        }
        else
            calculateStorage(file);

        // 파일 사이즈 읽기 쉽도록 변경
        return formatFileSize(fileSize.get());

    }

    // 파일 사이즈 쉽게 보이도록 함
    private String formatFileSize(long bytes) {
        return android.text.format.Formatter.formatFileSize(context, bytes);
    }

    public void calculateStorage(File pathFile){
//        AtomicLong size = new AtomicLong(0);
        try {
            Files.walkFileTree(pathFile.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if(!file.getFileName().startsWith(".")) {
                        numNonDir.addAndGet(1);
                        fileSize.addAndGet(attrs.size());
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if(!dir.getFileName().startsWith("."))
                        numDir.addAndGet(1);
                    return FileVisitResult.CONTINUE;
                }

            });
            Log.d(TAG, pathFile.getName() + " fileSize = " + fileSize);
            Log.d(TAG, pathFile.getName() + " numDir= " + numDir);
            Log.d(TAG, pathFile.getName() + " numNonDir= " + numNonDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 파일의 마지막 수정날짜 가져오기
    // BasicFileAttributes를 이용해서도 가능
    public String getFileLastModify(){
        //날짜 포맷
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 a hh:mm", Locale.KOREAN);
        return simpleDateFormat.format(file.lastModified());
    }

    // 파일 내용(폴더수, 파일수) 가져오기
    public int getFileNum(){
        return numNonDir.get();
    }

    public int getFolderNum(){
        return numDir.get();
    }
}
