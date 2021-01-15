package com.example.nyahn_fileexplorer.Utils;

import android.util.Log;

import com.example.nyahn_fileexplorer.Models.FileData;

import java.io.File;
import java.util.Comparator;

public class SortFileData implements Comparator<FileData> {
    private static final String TAG = SortFileData.class.getSimpleName();

    @Override
    public int compare(FileData o1, FileData o2) {
        String firstFileName = o1.getFile().getName();
        String secondFileName = o2.getFile().getName();

        Log.d(TAG, "firstFileName = " + firstFileName);
        Log.d(TAG, "secondFileName = " + secondFileName);
        Log.d(TAG, "first compareTo second = " + firstFileName.compareTo(secondFileName));

        return firstFileName.compareTo(secondFileName);
    }
}
