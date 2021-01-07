package com.example.nyahn_fileexplorer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nyahn_fileexplorer.FileListAdapter;
import com.example.nyahn_fileexplorer.MainActivity;
import com.example.nyahn_fileexplorer.OnItemClick;
import com.example.nyahn_fileexplorer.R;
import com.example.nyahn_fileexplorer.Utils.ManageFile;
import com.example.nyahn_fileexplorer.models.FileData;

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

    private LinearLayout cdBottomSheet;
    private LinearLayout llBottomManageLayout;
    private LinearLayout llBottomMoveLayout;
    private LinearLayout llFileCopy;
    private LinearLayout llFileMove;
    private LinearLayout llFileRename;
    private LinearLayout llFileDelete;
    private LinearLayout llFileInfo;

    ManageFile manageFile;

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

    // 복사, 이동 등 file function layout
    @Override
    public void onShowBottomLayout(boolean showBottomLayout) {
        if(showBottomLayout) {
//            llBottomManageLayout.setVisibility(View.VISIBLE);
            cdBottomSheet.setVisibility(View.VISIBLE);
        } else {
//            llBottomManageLayout.setVisibility(View.INVISIBLE);
            cdBottomSheet.setVisibility(View.INVISIBLE);
        }
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
        manageFile = new ManageFile();

        flEmptyLayout = findViewById(R.id.flEmptyLayout);
        // RecyclerView 초기화
        recyclerView = findViewById(R.id.rcFileList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fileList = new ArrayList<>();

        cdBottomSheet = findViewById(R.id.cdBottomSheet);
        llBottomManageLayout = findViewById(R.id.llBottomManageLayout);
        llBottomMoveLayout = findViewById(R.id.llBottomMoveLayout);
        llFileCopy = findViewById(R.id.llFileCopy);
        llFileMove = findViewById(R.id.llFileMove);
        llFileRename = findViewById(R.id.llFileRename);
        llFileDelete = findViewById(R.id.llFileDelete);
        llFileInfo = findViewById(R.id.llFileInfo);

    }

    // 파일 버튼 클릭시
    public void onClickButton(View view){
        // TODO: if/else로 변경 static에서 기본형으로 바뀌면서 효율성 면에서 R.id.~ 사용시 if/else문으로 사용 하는걸 권장
        switch (view.getId()){
            // 파일 기능
            case R.id.llFileCopy:
                Toast.makeText(this, "복사하겠습니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.llFileMove:
                Toast.makeText(this, "이동하겠습니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.llFileRename:
                Toast.makeText(this, "이름 수정.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.llFileDelete:
                Toast.makeText(this, "삭제.", Toast.LENGTH_SHORT).show();

            case R.id.llFileInfo:
                Toast.makeText(this, "속성.", Toast.LENGTH_SHORT).show();
                break;
            // 복사, 이동시 나타나는 버튼
            case R.id.llCancel:
                Toast.makeText(this, "취소.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.llFilePaste:
                Toast.makeText(this, "복사하기.", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "기본버튼입니다.", Toast.LENGTH_SHORT).show();
                break;
        }
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

    // fileList를 매개변수로 받았을시 목록 변경
    public void showFileList(ArrayList<FileData> fileList){
        this.fileList = fileList;
//        File[] list = parentFile.listFiles();
//
//        if(list != null && list.length > 0) {
//            flEmptyLayout.setVisibility(View.INVISIBLE);
//            for (File value : list) {
//                FileData fileData = new FileData();
//                fileData.setFile(value);
//                fileList.add(fileData);
//            }
//        } else {
//            flEmptyLayout.setVisibility(View.VISIBLE);
//        }

        fileListAdapter = new FileListAdapter(fileList, this);
        recyclerView.setAdapter(fileListAdapter);
        fileListAdapter.notifyDataSetChanged();
    }
}