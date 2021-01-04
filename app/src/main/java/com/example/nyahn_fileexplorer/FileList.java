package com.example.nyahn_fileexplorer;

import android.app.ListActivity;
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

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        fileList = new ArrayList<>();

        // sdcard
        String rootInnerDir = Environment.getExternalStorageDirectory().toString();
        Log.i("FileList : ", rootInnerDir);

        file = new File(rootInnerDir) ;
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

        File tempFile = new File(file, fileList.get( position ));

        Log.i("FileList : ", ""+ tempFile.isFile());
        if(!tempFile.isFile())
        {
            file = new File( file, fileList.get( position ));
            File[] list = file.listFiles();

            fileList.clear();

            for (File value : list) {
                fileList.add(value.getName());
            }
            Toast.makeText(getApplicationContext(), file.toString(), Toast.LENGTH_LONG).show();

            setListAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, fileList));

        }

    }


    @Override
    public void onBackPressed() {
        String parent = file.getParent().toString();
        file = new File( parent ) ;
        File[] list = file.listFiles();

        fileList.clear();

        for (File value : list) {
            fileList.add(value.getName());
        }
        Toast.makeText(getApplicationContext(), parent, Toast.LENGTH_LONG).show();
        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, fileList));


    }
}