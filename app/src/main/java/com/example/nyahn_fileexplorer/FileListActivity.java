package com.example.nyahn_fileexplorer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class FileListActivity extends AppCompatActivity implements OnItemClick
{
//    private String fileDir;
    private File file;
    private ArrayList<FileData> fileList;
    private final String rootMainDir = Environment.getExternalStorageDirectory().toString();
    private RecyclerView recyclerView;
    private FileListAdapter fileListAdapter;
    private FrameLayout flEmptyLayout;

    private void setToolbarTitle(){
        // toolbar as actionbar
        // setting toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.main_storage);
        setSupportActionBar(toolbar);
    }

    @Override
    public File onGetParentFile() {
        return file;
    }

    @Override
    public void onSetParentFile(File file) {
        this.file = file;
    }


    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        setToolbarTitle();
        flEmptyLayout = findViewById(R.id.flEmptyLayout);
        // RecyclerView 초기화
        recyclerView = findViewById(R.id.rcFileList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fileList = new ArrayList<>();

        // External Storage
//        rootMainDir = Environment.getExternalStorageDirectory().toString();
        Log.i("FileList : ", rootMainDir);

        // rootMainDir에 해당되는 파일의 File 객체 생성
        file = new File(rootMainDir);
        showFileList(file);

        fileListAdapter = new FileListAdapter(fileList, this);
        recyclerView.setAdapter(fileListAdapter);

    }

    @Override
    public void onBackPressed() {
        // 현재 파일의 부모 경로

        if(rootMainDir.equals(file.getPath())){
            startActivity(new Intent(FileListActivity.this, MainActivity.class));
            finish();
        }
        else {
            String parent = file.getParent();
            if(parent != null)
                file = new File(file.getParent());

            // 기존 list 삭제
            fileList.clear();
            showFileList(file);
        }
    }

    // 파일 목록 update
    public void showFileList(File parentFile){
        File[] list = parentFile.listFiles();

        if(list != null) {
            for (File value : list) {
                FileData fileData = new FileData();
                fileData.setFile(value);
                fileList.add(fileData);
            }
        }

        fileListAdapter = new FileListAdapter(fileList, this);
        recyclerView.setAdapter(fileListAdapter);
        fileListAdapter.notifyDataSetChanged();
    }
}