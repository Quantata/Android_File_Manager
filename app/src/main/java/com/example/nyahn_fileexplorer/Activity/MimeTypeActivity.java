package com.example.nyahn_fileexplorer.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.example.nyahn_fileexplorer.Adapter.FileListAdapter;
import com.example.nyahn_fileexplorer.Models.DialogMode;
import com.example.nyahn_fileexplorer.Models.FileData;
import com.example.nyahn_fileexplorer.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class MimeTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mime_type);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_VIEW.equals(action) && type != null) {
            Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
            String path = getRealPathFromUri(this, imageUri);
            FileListActivity fileListActivity = new FileListActivity();
            ArrayList<FileData> selectedDataList = new ArrayList<>();
            FileData fileData = new FileData();
            fileData.setFile(new File(path));
            selectedDataList.add(fileData);

            fileListActivity.showDialog(DialogMode.DIALOG_INFO, selectedDataList);
//            if ("text/plain".equals(type)) {
//                handleSendText(intent); // Handle text being sent
//            } else if (type.startsWith("image/")) {
//                handleSendImage(intent); // Handle single image being sent
//            }
        }
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}