package com.example.nyahn_fileexplorer.Utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.nyahn_fileexplorer.Models.FileData;

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

    AtomicInteger totalDirNum = new AtomicInteger(0);
    AtomicInteger totalNonDirNum = new AtomicInteger(0);

    public FileInfo(){}
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
//        long totalSpace = 0;
//        blockSize = stat.getBlockSizeLong();
//        totalBlocks = stat.getBlockCountLong();
//
//        totalSpace = blockSize * totalBlocks;
//
//        File rootFile = new File(Environment.getRootDirectory().getPath());
//        stat = new StatFs(rootFile.getPath());
//        long rootFileSize = stat.getBlockSizeLong() * stat.getBlockCountLong();
//
//        String sdcardPath = Environment.getStorageDirectory().getPath();
//        stat = new StatFs(sdcardPath);
//        long sdcardStorage = stat.getBlockSizeLong() * stat.getBlockCountLong();
//        totalSpace = totalSpace + rootFileSize + sdcardStorage;


        long totalSpace = file.getTotalSpace();
//        long totalSpace = Environment.getDataDirectory().getTotalSpace();
        File rootFile = new File(Environment.getRootDirectory().getPath());

        long sdcardStorage = 0;
        if(!file.getPath().equals(Environment.getStorageDirectory().getAbsolutePath())) {
            sdcardStorage = Environment.getStorageDirectory().getTotalSpace();
            totalSpace = totalSpace + rootFile.getTotalSpace() + sdcardStorage;
        }
        else
            totalSpace = totalSpace + rootFile.getTotalSpace();

        Log.d(TAG, "****************GET CALTOTAL MEMORY****************");
        Log.d(TAG, "file.getPath = " + file.getPath());
        Log.d(TAG, "file.getTotalSpace = " + formatFileSize(file.getTotalSpace()));
        Log.d(TAG, "rootFile.getPath = " + rootFile.getPath());
        Log.d(TAG, "rootFile.getTotalSpace = " + formatFileSize(rootFile.getTotalSpace()));
        Log.d(TAG, "sdcardDir.getPath = " + Environment.getStorageDirectory().getPath());
        Log.d(TAG, "sdcardStorage.getTotalSpace = " + formatFileSize(sdcardStorage));
        Log.d(TAG, "totalspace = " + formatFileSize(totalSpace));
        Log.d(TAG, "************************************************");

        return totalSpace;
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

        Log.d(TAG, "****************GET USING MEMORY****************");
        Log.d(TAG, "file.getPath = " + file.getPath());
        Log.d(TAG, "file.getFreeSpace = " + formatFileSize(file.getFreeSpace()));
        Log.d(TAG, "rootFile.getPath = " + rootFile.getPath());
        Log.d(TAG, "rootFile.getFreeSpace = " + formatFileSize(rootFile.getFreeSpace()));
        Log.d(TAG, "getFreeSpace = " + formatFileSize(freeSize));
        Log.d(TAG, "************************************************");

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
                        totalNonDirNum.getAndAdd(1);
                        fileSize.getAndAdd(attrs.size());
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if(String.valueOf(dir.getFileName()).startsWith("."))
                       return FileVisitResult.SKIP_SUBTREE; // 숨김 파일인 경우 하위 디렉토리 검사 안함

                    totalDirNum.getAndAdd(1);
                    return FileVisitResult.CONTINUE;
                }

            });
            Log.d(TAG, pathFile.getName() + " fileSize = " + fileSize);
            Log.d(TAG, pathFile.getName() + " totalDirNum= " + totalDirNum);
            Log.d(TAG, pathFile.getName() + " totalNonDirNum= " + totalNonDirNum);
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


    public int getTotalFileNum(){
        // 속성에서 파일사이즈 검색시 같이 검색
        return totalNonDirNum.get();
    }

    public int getTotalFolderNum(){
        // 속성에서 파일사이즈 검색시 같이 검색
        return totalDirNum.get();
    }

    public void setFileNum(FileData fileData){
        // Ageis
        File currentFile = fileData.getFile();

        int dirNum = 0;
        int nonDirNum = 0;
        if(currentFile.isDirectory()) { // ex) /emulated/0/Ageis가 directory이면
            // ex) /emulated/0/Movies의 하위 fileList가 0이 아니면
            if (currentFile.listFiles() != null && currentFile.listFiles().length != 0) {
                // ex) /emulated/0/Aegis/file 의 file 개수 계산
                for (File tempFile : currentFile.listFiles()) {
                    if(!tempFile.getName().startsWith(".")) {
                        if (tempFile.isDirectory()) dirNum += 1;
                        else nonDirNum += 1;
                    }
                }
            }
        }

        fileData.setFolderNum(dirNum);
        fileData.setFileNum(nonDirNum);
    }

    public String getFilePath(){
        return file.getAbsolutePath();
    }
}
