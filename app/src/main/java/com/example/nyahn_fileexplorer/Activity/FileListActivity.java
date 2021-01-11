package com.example.nyahn_fileexplorer.Activity;

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

import com.example.nyahn_fileexplorer.Adapter.FileListAdapter;
import com.example.nyahn_fileexplorer.MainActivity;
import com.example.nyahn_fileexplorer.Models.Mode;
import com.example.nyahn_fileexplorer.OnItemClick;
import com.example.nyahn_fileexplorer.OnFileManage;
import com.example.nyahn_fileexplorer.R;
import com.example.nyahn_fileexplorer.Utils.FileManage;
import com.example.nyahn_fileexplorer.Models.FileData;

import java.io.File;
import java.util.ArrayList;

public class FileListActivity extends AppCompatActivity implements OnItemClick, OnFileManage
{
    private static final String TAG = FileListActivity.class.getSimpleName();

    private Mode presentMode = Mode.BASIC_MODE;
    // 현재 파일
    private File file;
    // 현재 파일이 갖고 있는 파일 목록
    private ArrayList<FileData> fileList;
    // File 기능
    private FileManage fileManage;
    // 선택된 파일 리스트
    private ArrayList<FileData> selectedFileDataList;
    // 선택된 파일 Position

    private String rootDir = "";
    private RecyclerView recyclerView;
    private FileListAdapter fileListAdapter;
    private FrameLayout flEmptyLayout;

    private LinearLayout cdBottomSheet;
    private LinearLayout llBottomManageLayout;
    private LinearLayout llBottomMoveLayout;
//    private LinearLayout llFileCopy;
//    private LinearLayout llFileMove;
//    private LinearLayout llFileRename;
//    private LinearLayout llFileDelete;
//    private LinearLayout llFileInfo;

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
    public void onShowBottomLayout() {
        if(presentMode == Mode.SELECTED_MODE){
            cdBottomSheet.setVisibility(View.VISIBLE);
            llBottomManageLayout.setVisibility(View.VISIBLE);
        }
        else if(presentMode == Mode.MOVE_MODE){
            llBottomManageLayout.setVisibility(View.GONE);
            llBottomMoveLayout.setVisibility(View.VISIBLE);
        }
        else if(presentMode == Mode.COPY_MODE){
            llBottomManageLayout.setVisibility(View.GONE);
            llBottomMoveLayout.setVisibility(View.VISIBLE);
        }
        else { // Basic_mode
            cdBottomSheet.setVisibility(View.GONE);
            llBottomManageLayout.setVisibility(View.GONE);
            llBottomMoveLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public Mode onGetMode() {
        return presentMode;
    }

    @Override
    public void onSetMode(Mode mode) {
        presentMode = mode;
    }

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        // storage 종류 판별
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        String storage = bundle.getString("STORAGE");
        if ("MAIN".equals(storage)){
            rootDir = Environment.getExternalStorageDirectory().toString();
        } else {
            File sdcard = null;
            File file = new File(Environment.getStorageDirectory().getPath());
            File[] list = file.listFiles();
            for(File el : list){
                if(el.getName().contains("-")){
                    sdcard = el;
                }
            }
            rootDir = sdcard.getPath();
        }

        setToolbarTitle();
        init();
        // External Storage
//        rootMainDir = Environment.getExternalStorageDirectory().toString();
        Log.d(TAG, "RootDirectory = "+ rootDir);

        // rootMainDir에 해당되는 파일의 File 객체 생성
        file = new File(rootDir);
        showFileList(file);

        fileListAdapter = new FileListAdapter(fileList, this);
        recyclerView.setAdapter(fileListAdapter);

    }

    public void init(){
        fileManage = new FileManage();

        flEmptyLayout = findViewById(R.id.flEmptyLayout);
        // RecyclerView 초기화
        recyclerView = findViewById(R.id.rcFileList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fileList = new ArrayList<>();

        cdBottomSheet = findViewById(R.id.cdBottomSheet);
        llBottomManageLayout = findViewById(R.id.llBottomManageLayout);
        llBottomMoveLayout = findViewById(R.id.llBottomMoveLayout);
//        llFileCopy = findViewById(R.id.llFileCopy);
//        llFileMove = findViewById(R.id.llFileMove);
//        llFileRename = findViewById(R.id.llFileRename);
//        llFileDelete = findViewById(R.id.llFileDelete);
//        llFileInfo = findViewById(R.id.llFileInfo);

    }


    // 파일 버튼 클릭시
    public void onClickButton(View view){
        // TODO: if/else로 변경 static에서 기본형으로 바뀌면서 효율성 면에서 R.id.~ 사용시 if/else문으로 사용 하는걸 권장
        switch (view.getId()){
            // 파일 기능
            case R.id.llFileCopy:
                // COPY_MODE로 변경, 붙여넣기 클릭시 사용
                presentMode = Mode.COPY_MODE;
                onShowBottomLayout();   // 취소/붙여넣기
                // 선택된 파일(들)이 있는 경로의 파일 목록
                selectedFileDataList = fileListAdapter.getSelectedFileList();

                // 현재 선택된 파일 복사하는 로직
                // 현재 선택된 FileList와 현재 이동된 file Path
//                manageFile.copyFile(fileListAdapter.getSelectedFileList(), file.getPath());
                // Basic Mode로 바꿔주고 onShowBottomLayout하면 될듯 -> 이건 Paste나 cancel에서
                Toast.makeText(this, "복사하겠습니다.", Toast.LENGTH_SHORT).show();

                break;
            case R.id.llFileMove:
                presentMode = Mode.MOVE_MODE;
                Toast.makeText(this, "이동하겠습니다.", Toast.LENGTH_SHORT).show();
                onShowBottomLayout();
                break;
            case R.id.llFileRename:
                Toast.makeText(this, "이름 수정.", Toast.LENGTH_SHORT).show();
                presentMode = Mode.BASIC_MODE;
                //이름 변경 Dialog한 뒤 notifySetData

                // Layout 내림
                onShowBottomLayout();
                break;
            case R.id.llFileDelete:
                presentMode = Mode.BASIC_MODE;
                // Delete Dialog

                // 삭제 완료되었습니다. Dialog

                // Layout 내림
                onShowBottomLayout();
                Toast.makeText(this, "삭제.", Toast.LENGTH_SHORT).show();

            case R.id.llFileInfo:
                Toast.makeText(this, "속성.", Toast.LENGTH_SHORT).show();
                break;
            // 복사, 이동시 나타나는 버튼
            case R.id.llCancel:
                presentMode = Mode.BASIC_MODE;
                onShowBottomLayout();
                // 선택 해제
                fileListAdapter.setClearSelectedFileList();
                Toast.makeText(this, "취소", Toast.LENGTH_SHORT).show();
                // Basic 모드로 바꾸고
                // showBottom 호출하면 됨.
                break;
            case R.id.llFilePaste:
                // Mode가 COPY인지 MOVE인지 확인 후 붙여 넣기
                fileManage.pasteFile(presentMode, selectedFileDataList, file);
                selectedFileDataList.clear();
                // 복사된 파일 List 보여주기
                showFileList(file);

                // 붙여넣기 끝난 후 Basic_Mode로 변경
                presentMode = Mode.BASIC_MODE;
                onShowBottomLayout();
                Toast.makeText(this, "붙여넣기.", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "기본버튼입니다.", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    @Override
    public void onBackPressed() {
        // 현재 파일의 부모 경로
        if(rootDir.equals(file.getPath())){
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
            // 붙여넣기시 기존 파일 + 붙여넣기 안되는 문제 해결
            fileList.clear();
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