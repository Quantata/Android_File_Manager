package com.example.nyahn_fileexplorer;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileList extends ListActivity
{
    private File file;
    private List<String> fileList;
    private final String rootMainDir = Environment.getExternalStorageDirectory().toString();

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        fileList = new ArrayList<>();

        // External Storage
//        rootMainDir = Environment.getExternalStorageDirectory().toString();
        Log.i("FileList : ", rootMainDir);

        // rootMainDir에 해당되는 파일의 File 객체 생성
        file = new File(rootMainDir) ;
        File[] list = file.listFiles();

        for (File value : list) {
            fileList.add(value.getName());
        }

        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, fileList));

    }

    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);

        // file 객체 폴더의 fileList.get(position)라는 파일에 대한 파일 객체를 생성
        File clickedFile = new File(file, fileList.get( position ));

        Log.i("FileList : ", ""+ clickedFile.isFile());

        if(!clickedFile.isFile())
        {
//            file = new File( file, fileList.get( position ));
            file = clickedFile;
            File[] list = file.listFiles();

            fileList.clear();

            for (File value : list) {
                fileList.add(value.getName());
            }
            Toast.makeText(getApplicationContext(), file.toString(), Toast.LENGTH_SHORT).show();

            setListAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, fileList));

        }

    }


    @Override
    public void onBackPressed() {

        String parent = file.getParent();
        file = new File(parent);

        File[] list = file.listFiles();

        fileList.clear();

        for (File value : list) {
            fileList.add(value.getName());
        }
        Toast.makeText(getApplicationContext(), parent, Toast.LENGTH_SHORT).show();
        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, fileList));

    }
}