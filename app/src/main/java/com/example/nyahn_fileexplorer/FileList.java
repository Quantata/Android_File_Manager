package com.example.nyahn_fileexplorer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class FileList extends AppCompatActivity implements OnItemClick
{
    private String fileDir;
    private File file;
    private ArrayList<FileData> fileList;
    private final String rootMainDir = Environment.getExternalStorageDirectory().toString();
    private RecyclerView recyclerView;
    private FileListAdapter fileListAdapter;

    private void setToolbarTitle(){
        // toolbar as actionbar
        // setting toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.main_storage);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onClick(String fileDir) {
        this.fileDir = fileDir;
    }

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        setToolbarTitle();
        // RecyclerView 초기화
        recyclerView = findViewById(R.id.rcFileList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        fileList = new ArrayList<>();

        // External Storage
//        rootMainDir = Environment.getExternalStorageDirectory().toString();
        Log.i("FileList : ", rootMainDir);

        // rootMainDir에 해당되는 파일의 File 객체 생성
        file = new File(rootMainDir) ;
        File[] list = file.listFiles();

        for (File value : list) {
            FileData fileData = new FileData();
            fileData.setFileName(value.getName());
            fileData.setParentDir(rootMainDir);
            fileData.setPresentDir(value.getPath());
            fileList.add(fileData);
//            fileList.add(value.getName());
        }


        fileListAdapter = new FileListAdapter(fileList, this);
        recyclerView.setAdapter(fileListAdapter);
//        setListAdapter(new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, fileList));

    }


//    protected void onListItemClick(ListView l, View v, int position, long id)
//    {
//        super.onListItemClick(l, v, position, id);
//
//        // file 객체 폴더의 fileList.get(position)라는 파일에 대한 파일 객체를 생성
//        File clickedFile = new File(file, fileList.get( position ));
//
//        Log.i("FileList : ", ""+ clickedFile.isFile());
//
//        if(!clickedFile.isFile())
//        {
////            file = new File( file, fileList.get( position ));
//            file = clickedFile;
//            File[] list = file.listFiles();
//
//            fileList.clear();
//
//            for (File value : list) {
//                fileList.add(value.getName());
//            }
//            Toast.makeText(getApplicationContext(), file.toString(), Toast.LENGTH_SHORT).show();
//
//            setListAdapter(new ArrayAdapter<>(this,
//                    android.R.layout.simple_list_item_1, fileList));
//
//        }
//
//    }


    @Override
    public void onBackPressed() {
        // 현재 directory
        String parent = fileDir;

        if(rootMainDir.equals(parent)){
            startActivity(new Intent(FileList.this, MainActivity.class));
            finish();
        }
        else {
            file = new File(parent);

//            String parent = file.getParent();
//            String parent = fileDir;
            file = new File(parent);

            File[] list = file.listFiles();

            fileList.clear();

            for (File value : list) {
                FileData fileData = new FileData();
                fileData.setFileName(value.getName());
                fileData.setParentDir(parent);
                fileData.setPresentDir(value.getPath());
                fileList.add(fileData);

//            fileList.add(value.getName());
            }

            fileListAdapter = new FileListAdapter(fileList, this);
            recyclerView.setAdapter(fileListAdapter);
//        Toast.makeText(getApplicationContext(), parent, Toast.LENGTH_SHORT).show();
//        setListAdapter(new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, fileList));
        }
    }
}