package com.example.nyahn_fileexplorer.Utils;

import android.util.Log;

import com.example.nyahn_fileexplorer.Models.FileData;

import java.math.BigInteger;
import java.util.Comparator;

public class SortFileData implements Comparator<FileData> {
    private static final String TAG = SortFileData.class.getSimpleName();

    @Override
    public int compare(FileData o1, FileData o2) {

        String firstFileName = o1.getFile().getName();
        String secondFileName = o2.getFile().getName();

        if(extractString(firstFileName).compareTo(extractString(secondFileName)) == 0)
            return extractInt(firstFileName) - extractInt(secondFileName);


        return firstFileName.compareTo(secondFileName);
    }

    private String extractString(String s) {
        Log.d(TAG, "String s = " + s);

        // 숫자가 아닌 것은 다 "" 만들어버림
        String string = s.replaceAll("[0-9]", "");
        Log.d(TAG, "String string = " + string);

        // return "" if no characters found
        return string.isEmpty() ? "" : string;
    }

    private int extractInt(String s) {
        Log.d(TAG, "String s = " + s);

        try {
            // 숫자가 아닌 것은 다 "" 만들어버림
            String num = s.replaceAll("\\D", "");
            Log.d(TAG, "String num = " + num);
            // return 0 if no digits found
            return num.isEmpty() ? 0 : Integer.parseInt(num);
        } catch (NumberFormatException numberFormatException){
            return 0;
        }
    }
}
