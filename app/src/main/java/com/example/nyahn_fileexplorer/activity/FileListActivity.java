package com.example.nyahn_fileexplorer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nyahn_fileexplorer.FileListAdapter;
import com.example.nyahn_fileexplorer.MainActivity;
import com.example.nyahn_fileexplorer.OnItemClick;
import com.example.nyahn_fileexplorer.R;
import com.example.nyahn_fileexplorer.models.FileData;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.util.ArrayList;

public class FileListActivity extends AppCompatActivity implements OnItemClick
{
    // 현재 파일
    private File file;
    private ArrayList<FileData> fileList;
    private final String rootMainDir = Environment.getExternalStorageDirectory().toString();

    private RecyclerView recyclerView;
    private FileListAdapter fileListAdapter;
    private FrameLayout flEmptyLayout;

    private LinearLayout llBottomLayout;
    private LinearLayout llFileCopy;
    private LinearLayout llFileMove;
    private LinearLayout llFileRename;
    private LinearLayout llFileDelete;
    private LinearLayout llFileInfo;

//    private BottomSheetBehavior bottomSheetBehavior;

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

    @Override
    public void onSetFileList(File file) {
        showFileList(file);
    }

    @Override
    public void onShowBottomLayout() {
        llBottomLayout.setVisibility(View.VISIBLE);
    }

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        setToolbarTitle();
        init();
        // External Storage
//        rootMainDir = Environment.getExternalStorageDirectory().toString();
        Log.i("FileList : ", rootMainDir);

        // rootMainDir에 해당되는 파일의 File 객체 생성
        file = new File(rootMainDir);
        showFileList(file);

        fileListAdapter = new FileListAdapter(fileList, this);
        recyclerView.setAdapter(fileListAdapter);

    }

    public void init(){
        flEmptyLayout = findViewById(R.id.flEmptyLayout);
        // RecyclerView 초기화
        recyclerView = findViewById(R.id.rcFileList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fileList = new ArrayList<>();

        llBottomLayout = findViewById(R.id.llBottomLayout);
        llFileCopy = findViewById(R.id.llFileCopy);
        llFileMove = findViewById(R.id.llFileMove);
        llFileRename = findViewById(R.id.llFileRename);
        llFileDelete = findViewById(R.id.llFileDelete);
        llFileInfo = findViewById(R.id.llFileInfo);

//        bottomSheetBehavior = BottomSheetBehavior.from((FrameLayout)findViewById(R.id.cdBottomSheet));

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

        if(list != null && list.length > 0) {
            flEmptyLayout.setVisibility(View.INVISIBLE);
            for (File value : list) {
                FileData fileData = new FileData();
                fileData.setFile(value);
                fileList.add(fileData);
            }
        } else {
            flEmptyLayout.setVisibility(View.VISIBLE);
        }

        fileListAdapter = new FileListAdapter(fileList, this);
        recyclerView.setAdapter(fileListAdapter);
        fileListAdapter.notifyDataSetChanged();
    }
}