package com.example.nyahn_fileexplorer.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nyahn_fileexplorer.Adapter.DirectoryListAdapter;
import com.example.nyahn_fileexplorer.Adapter.FileListAdapter;
import com.example.nyahn_fileexplorer.MainActivity;
import com.example.nyahn_fileexplorer.Models.DialogMode;
import com.example.nyahn_fileexplorer.Models.Mode;
import com.example.nyahn_fileexplorer.Interface.OnItemClick;
import com.example.nyahn_fileexplorer.OnFileManage;
import com.example.nyahn_fileexplorer.R;
import com.example.nyahn_fileexplorer.Utils.Define;
import com.example.nyahn_fileexplorer.Utils.FileInfo;
import com.example.nyahn_fileexplorer.Utils.FileManage;
import com.example.nyahn_fileexplorer.Models.FileData;
import com.example.nyahn_fileexplorer.Utils.Singleton;
import com.example.nyahn_fileexplorer.Utils.SortFileData;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class FileListActivity extends AppCompatActivity implements OnItemClick, OnFileManage
{
    private static final String TAG = FileListActivity.class.getSimpleName();

    boolean isMimeType;
    int imageRootDir;
    ImageView ivRootDir;

//    private Mode currentMode = Mode.BASIC_MODE;
    private Mode currentMode = Singleton.getInstance().getCurrentMode();
    // 현재 파일
    private File file;
    // 현재 파일이 갖고 있는 파일 목록
    private ArrayList<FileData> fileList;
    // File 기능
    private FileManage fileManage;
    // 선택된 파일 리스트
    private ArrayList<FileData> selectedFileDataList;
    // 선택된 파일 Position

    Toolbar toolbar;
    ImageView ivAdd;
    TextView tvToolbarTitle;
    private RecyclerView rcDirectory; // 디렉토리 구조
    // directory 구조 list
    private ArrayList<File> directoryList;
    DirectoryListAdapter directoryListAdapter;

    private String rootDir = "";
    private String rootDirName = "";
    private RecyclerView rcFile;
    private FileListAdapter fileListAdapter;
    private FrameLayout flEmptyLayout;

    private LinearLayout cdBottomSheet;
    private LinearLayout llBottomManageLayout;
    private LinearLayout llBottomMoveLayout;
    private LinearLayout llFileRename;
    private TextView tvFileRename;
    private LinearLayout llFileInfo;
    private TextView tvFileInfo;

//    private BottomSheetBehavior bottomSheetBehavior;

    private void setToolbar(){
        // toolbar as actionbar
        // setting toolbar
        toolbar = findViewById(R.id.toolbar);
        ivAdd = toolbar.findViewById(R.id.ivAdd);
        tvToolbarTitle = toolbar.findViewById(R.id.tvToolbarTitle);

        ivAdd.setVisibility(View.VISIBLE);
        ivAdd.setOnClickListener(v -> {
            showDialog(DialogMode.DIALOG_ADD);
        });
        tvToolbarTitle.setText(rootDirName);
//        toolbar.setTitle(rootDirName);
        setSupportActionBar(toolbar);
    }

    public void setToolbarTitle(String title) {
        toolbar.setTitle(title);
    }

    @Override
    public File onGetCurrentFile() {
        return file;
    }

    @Override
    public void onSetCurrentFile(File file) {
        this.file = file;
    }

    @Override
    public void onSetFileList(File file) {
        showFileList(file);
    }

    // 복사, 이동 등 file function layout
    @Override
    public void onShowBottomLayout() {
        currentMode = Singleton.getInstance().getCurrentMode();

        if(currentMode == Mode.SELECTED_MODE){
            cdBottomSheet.setVisibility(View.VISIBLE);
            llBottomManageLayout.setVisibility(View.VISIBLE);
        }
        else if(currentMode == Mode.MOVE_MODE){
            llBottomManageLayout.setVisibility(View.GONE);
            llBottomMoveLayout.setVisibility(View.VISIBLE);
        }
        else if(currentMode == Mode.COPY_MODE){
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

//1        return currentMode;
        return Singleton.getInstance().getCurrentMode();
    }

    @Override
    public void onSetMode(Mode mode) {
        Singleton.getInstance().setCurrentMode(mode);
//1        currentMode = mode;
    }

    @Override
    public void onAddDirectoryList(File addFile) {
        directoryList.add(addFile);
        Log.d(TAG, "directoryList size =" + directoryList.size());
        directoryListAdapter.notifyDataSetChanged();

        setToolbarTitle(addFile.getName());
        // 최하단으로 scroll
        rcDirectory.scrollToPosition(directoryListAdapter.getItemCount() - 1);
    }

    @Override
    public void onBackDirectoryList(int clickedPosition) {

        Log.d(TAG, "ClickedPosition =" + clickedPosition);
        Log.d(TAG, "DirectoryList.size =" + directoryList.size());

        // 뒤에서 부터 삭제
        for(int i = directoryList.size()-1; i > clickedPosition; i--){
            directoryList.remove(i);
            directoryListAdapter.notifyItemRemoved(i);
        }
        // 현재 위치 bold 처리 위해 추가
        directoryListAdapter.notifyItemChanged(directoryListAdapter.getItemCount()-1);

        //Toolbar title 지정
        if(directoryList.size() == 0)
            setToolbar();
        else
            setToolbarTitle(directoryList.get(clickedPosition).getName());
    }

    @Override
    public void onSetChangeStatus(boolean active) {
        TypedValue typedValue;
//        if(mode == Mode.RENAME_MODE){
//            typedValue = new TypedValue();
//
//            if(active)
//                getTheme().resolveAttribute(R.attr.bottomTextColor, typedValue, true);
//            else
//                getTheme().resolveAttribute(R.attr.bottomTextColorHint, typedValue, true);
//
//            tvFileRename.setTextColor(typedValue.data);
//            llFileRename.setClickable(active);
//            Log.d(TAG, "Rename Mode Clickable = " + active);
//        }

            typedValue = new TypedValue();

            if(active)
                getTheme().resolveAttribute(R.attr.bottomTextColor, typedValue, true);
            else
                getTheme().resolveAttribute(R.attr.bottomTextColorHint, typedValue, true);

            // Rename관련
            tvFileRename.setTextColor(typedValue.data);
            llFileRename.setClickable(active);
            // File Info 관련
            tvFileInfo.setTextColor(typedValue.data);
            llFileInfo.setClickable(active);

            Log.d(TAG, "INFO Mode Clickable = " + active);
    }

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);
        Log.d(TAG, "*******onCreate()********");

        // storage 종류 판별
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null) {
            isMimeType = bundle.getBoolean(Define.MimeType, false);
            rootDir = bundle.getString(Define.STORAGE, "");
            rootDirName = bundle.getString(Define.STORAGE_NAME, "");
            imageRootDir = bundle.getInt(Define.ROOTDIR_IMAGE, 0);
            Log.d(TAG, "RootDirectory = " + rootDir);
            Log.d(TAG, "RootDirectory Name = " + rootDirName);
            Log.d(TAG, "isMimeType = " + isMimeType);
            Log.d(TAG, "imageRootDir = " + imageRootDir);
        }

        setToolbar();
        init();

        if(isMimeType)
            showDialog(DialogMode.DIALOG_MIMETYPE);
        else {
            // rootMainDir에 해당되는 파일의 File 객체 생성
            file = new File(rootDir);
            fileListAdapter = new FileListAdapter(this, fileList, this);
            rcFile.setAdapter(fileListAdapter);
            showFileList(file);
        }

        onShowBottomLayout();

    }

    public void init(){
        fileManage = new FileManage(this);
//        fileManage = new FileManage();

        // Directory RecyclerView 초기화
        rcDirectory = findViewById(R.id.rcDirectory);
        // for horizontal scroll
        rcDirectory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        directoryList = new ArrayList<>();
        // 마지막 item으로 자동 scroll
        directoryListAdapter = new DirectoryListAdapter(directoryList, this);
        rcDirectory.setAdapter(directoryListAdapter);

        // FileList RecyclerView 초기화
        rcFile = findViewById(R.id.rcFile);
        rcFile.setLayoutManager(new LinearLayoutManager(this));
        fileList = new ArrayList<>();

        flEmptyLayout = findViewById(R.id.flEmptyLayout);
        cdBottomSheet = findViewById(R.id.cdBottomSheet);
        llBottomManageLayout = findViewById(R.id.llBottomManageLayout);
        llBottomMoveLayout = findViewById(R.id.llBottomMoveLayout);

        llFileRename = findViewById(R.id.llFileRename);
        tvFileRename = findViewById(R.id.tvFileRename);
        llFileInfo = findViewById(R.id.llFileInfo);
        tvFileInfo = findViewById(R.id.tvFileInfo);

        ivRootDir = findViewById(R.id.ivRootDir);
        if(imageRootDir != 0)
            ivRootDir.setBackgroundResource(imageRootDir);
        else
            ivRootDir.setBackgroundResource(R.drawable.inbox);
    }


    // 파일 버튼 클릭시
    public void onClickButton(View view){
        // TODO: if/else로 변경 static에서 기본형으로 바뀌면서 효율성 면에서 R.id.~ 사용시 if/else문으로 사용 하는걸 권장
        switch (view.getId()){
            // 파일 기능
            case R.id.llFileCopy:   // 구현 완료
                // COPY_MODE로 변경, 붙여넣기 클릭시 사용
//1                currentMode = Mode.COPY_MODE;
                Singleton.getInstance().setCurrentMode(Mode.COPY_MODE);
                onShowBottomLayout();   // 취소/붙여넣기

                // 선택된 파일(들)이 있는 경로의 파일 목록
//2                selectedFileDataList = fileListAdapter.getSelectedFileList();
                fileListAdapter.setSingletonSelectedFileList();

                // 현재 화면의 recyclerView fileList 선택부분 초기화
                fileListAdapter.setClearSelectedFileList();
                break;

            case R.id.llFileMove:
                // MOVE_MODE로 변경, 붙여넣기 클릭시 사용
//1                currentMode = Mode.MOVE_MODE;
                Singleton.getInstance().setCurrentMode(Mode.MOVE_MODE);
                // 어느 path의 파일이 선택된건지 저장
                Singleton.getInstance().setCurrentFile(file);

                onShowBottomLayout();   // 취소/붙여넣기
                // 선택된 파일(들)이 있는 경로의 파일 목록 저장
//2                selectedFileDataList = fileListAdapter.getSelectedFileList();
                fileListAdapter.setSingletonSelectedFileList();

                // 현재 화면의 recyclerView fileList 선택부분 초기화
                fileListAdapter.setClearSelectedFileList();
                break;

            case R.id.llFileRename:
//1                currentMode = Mode.BASIC_MODE;
                Singleton.getInstance().setCurrentMode(Mode.BASIC_MODE);

                fileListAdapter.setSingletonSelectedFileList(); // 추가
                //이름 변경 Dialog한 뒤 notifySetData
//2                showDialog(DialogMode.DIALOG_RENAME, fileListAdapter.getSelectedFileList());
                showDialog(DialogMode.DIALOG_RENAME);

                // Layout 내림
                onShowBottomLayout();
                break;
            case R.id.llFileDelete:
//1                currentMode = Mode.BASIC_MODE;
                Singleton.getInstance().setCurrentMode(Mode.BASIC_MODE);

                // Layout 내림
                onShowBottomLayout();

                fileListAdapter.setSingletonSelectedFileList(); // 추가
//2                showDialog(DialogMode.DIALOG_DELETE, fileListAdapter.getSelectedFileList());
                showDialog(DialogMode.DIALOG_DELETE);

                break;

            case R.id.llFileInfo:
//1                currentMode = Mode.BASIC_MODE;
                Singleton.getInstance().setCurrentMode(Mode.BASIC_MODE);

                // Layout 내림
                onShowBottomLayout();

                fileListAdapter.setSingletonSelectedFileList(); // 추가
//2                showDialog(DialogMode.DIALOG_INFO, fileListAdapter.getSelectedFileList());
                showDialog(DialogMode.DIALOG_INFO);
                break;

            // 복사, 이동시 나타나는 버튼
            case R.id.llCancel:
//1                currentMode = Mode.BASIC_MODE;
                Singleton.getInstance().setCurrentMode(Mode.BASIC_MODE);
                onShowBottomLayout();

                // 선택 해제
//                selectedFileDataList.clear();
                Singleton.getInstance().setSelectedFileDataListClear();
                fileListAdapter.setClearSelectedFileList();
                // Basic 모드로 바꾸고
                // showBottom 호출하면 됨.
                break;

            case R.id.llFilePaste:
                // Mode가 COPY인지 MOVE인지 확인 후 붙여 넣기
//1                fileManage.pasteFile(currentMode, selectedFileDataList, file);
                fileManage.pasteFile(file);

                // 복사된 파일 List 보여주기
                showFileList(file);

                // 선택된 fileList 초기화
//2                selectedFileDataList.clear();
                Singleton.getInstance().setSelectedFileDataListClear();
                Singleton.getInstance().setCurrentMode(Mode.BASIC_MODE);

                // 붙여넣기 끝난 후 Basic_Mode로 변경
//3                currentMode = Mode.BASIC_MODE;
                onShowBottomLayout();
                break;

            case R.id.ivRootDir:
                onBackDirectoryList(-1);
                file = new File(rootDir);
                onSetFileList(file);
                break;

            default:
                Toast.makeText(this, "기본버튼입니다.", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    @Override
    public void onBackPressed() {
        currentMode = Singleton.getInstance().getCurrentMode();

        if(currentMode == Mode.SELECTED_MODE){
//1            currentMode = Mode.BASIC_MODE;
            Singleton.getInstance().setCurrentMode(Mode.BASIC_MODE);
            onShowBottomLayout();
            // selectedFileDataList에는 따로저장된게 없음으로 adapter의 selectedList만 clear해주면 됨.
            fileListAdapter.setClearSelectedFileList();
        }
        else {
            // 현재 파일의 부모 경로
            if(rootDir.equals(file.getPath())){
                startActivity(new Intent(FileListActivity.this, MainActivity.class));
                finish();
                overridePendingTransition(0, 0);
            }
            else {
                String parent = file.getParent();
                if(parent != null)
                    file = new File(file.getParent());

                // toolbar title 변경
//                if(rootDir.equals(file.getPath()))
//                    toolbar.setTitle(R.string.main_storage);
//                else
                toolbar.setTitle(file.getName());

                // 기존 list 삭제
                fileList.clear();
                // list size = 마지막 위치+1, 마지막 위치의 이전위치로 가려면 list size-2
                onBackDirectoryList(directoryList.size()-2);
                showFileList(file);
            }
        }
    }

    // 파일 목록 update
    public void showFileList(File currentFile){
        ArrayList<FileData> directories = new ArrayList<>();
        ArrayList<FileData> files = new ArrayList<>();
        FileInfo fileInfo;

        file = currentFile;
        File[] list = file.listFiles();

        // 붙여넣기시 기존 파일 + 붙여넣기 안되는 문제 해결
        fileList.clear();
        if(list == null || list.length == 0){
            flEmptyLayout.setVisibility(View.VISIBLE);
        } else {
            flEmptyLayout.setVisibility(View.GONE);

            // @value : 선택한 파일의 하위 파일
            for (File value : list) {
                // .으로 시작하는 파일(히든파일)은 불러오지 않도록 함
                if(value.getName().startsWith(".")) continue;

                FileData fileData = new FileData();
                fileData.setFile(value);

                fileInfo = new FileInfo();

                // 현재 파일의 Folder, File Number 설정
                fileInfo.setFileNum(fileData);

                if(fileData.getFile().isDirectory()) {
                    directories.add(fileData);
                }
                else {
                    files.add(fileData);
                }
            }
            // 정렬
            directories.sort(new SortFileData());
            files.sort(new SortFileData());

            // directory먼저 보여주기
            fileList.addAll(directories);
            fileList.addAll(files);
        }

        fileListAdapter.notifyDataSetChanged();
    }

    // 잘되지만 back눌렀을때 activity보이게 하는거나 하는것 때문에 일단 막아놓겠음.
    // 파일 목록 update
    public void showMimeTypeFileList(File selectedFile){
        ArrayList<FileData> directories = new ArrayList<>();
        ArrayList<FileData> files = new ArrayList<>();
        FileInfo fileInfo;

        file = new File(Objects.requireNonNull(selectedFile.getParent()));

        File[] list = file.listFiles();

        Singleton.getInstance().setSelectedFileDataListClear();

        if(list == null || list.length == 0){
            flEmptyLayout.setVisibility(View.VISIBLE);
        } else {
            flEmptyLayout.setVisibility(View.GONE);

            // @value : 선택한 파일의 하위 파일
            for (File value : list) {
                // .으로 시작하는 파일(히든파일)은 불러오지 않도록 함
                if(value.getName().startsWith(".")) continue;

                FileData fileData = new FileData();
                fileData.setFile(value);

                fileInfo = new FileInfo();

                // 현재 파일의 Folder, File Number 설정
                fileInfo.setFileNum(fileData);

                if(fileData.getFile().isDirectory()) {
                    directories.add(fileData);
                }
                else {
                    files.add(fileData);
                }
            }
            // 정렬
            directories.sort(new SortFileData());
            files.sort(new SortFileData());

            // directory먼저 보여주기
            fileList.addAll(directories);
            fileList.addAll(files);
        }

        fileListAdapter = new FileListAdapter(this, fileList, this);
        rcFile.setAdapter(fileListAdapter);

        // TODO: 언젠가 directory를 넣든가,,,근데 그러면 backpressed 도,,ㅠ
    }

    protected void showDialog(DialogMode dialogMode){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // builder 이용해서는 dismiss() 수행 불가능, builder를 담을 AlertDialog 객체 생성
//        AlertDialog alertDialog = builder.create();

        if(dialogMode == DialogMode.DIALOG_DELETE) {
            // 배경 터치 불가
            builder.setCancelable(false);

            builder.setTitle(R.string.file_delete);
            builder.setMessage(String.format(getResources().getString(R.string.confirm_delete),
                    Singleton.getInstance().getSelectedFileDataList().size()));
            builder.setPositiveButton("확인", (dialog, which) -> {
                // 현재 화면의 선택된 파일 List 선택 해제
                fileListAdapter.setClearSelectedFileList();

                //현재 화면에서 선택된 fileList 삭제 함수
                fileManage.deleteFile(Singleton.getInstance().getSelectedFileDataList());

                // 선택된 파일 clear
                Singleton.getInstance().setSelectedFileDataListClear();
                // 파일 List 갱신
                showFileList(file);

            });
            builder.setNegativeButton("취소",
                    (dialog, which) ->
                            // 현재 화면의 선택된 파일 List 선택 해제
                            fileListAdapter.setClearSelectedFileList());

            builder.show();
        }

        if(dialogMode == DialogMode.DIALOG_RENAME){
            // 배경 터치 불가
            builder.setCancelable(false);

            EditText edittext = new EditText(this);

            // Edittext margin 주기 위해 LinearLayout 구현
            LinearLayout container = new LinearLayout(getApplicationContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.topMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
            params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
            params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);

            edittext.setLayoutParams(params);
            edittext.setSingleLine();

            // edittext 기존 파일 이름
            edittext.setText(Singleton.getInstance().getSelectedFileDataList().get(0).getFile().getName());
            edittext.requestFocus();
            edittext.selectAll();

            // 키보드 관리자 생성 및 보이기
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            // 키보드가 올라와있으면 내리고, 내려와있으면 올림
            // 열리는 용도로 사용 -> ShowSoftInput이 안먹힘
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

            container.addView(edittext);

            builder.setTitle(R.string.file_rename);
            builder.setView(container);
            builder.setPositiveButton("이름 변경", (dialog, which) -> {
                /*
                 * 이름 변경은 하나의 파일만 가능 but 사용하는 함수 쓰기 위해 selectedFileDataList 사용
                 */


                // 현재 화면의 선택된 파일 List 선택 해제
                fileListAdapter.setClearSelectedFileList();

                //// 현재 화면에서 선택된 fileList 갖고와서 이름 변경 함수
                fileManage.renameFile(Singleton.getInstance().getSelectedFileDataList(), edittext.getText().toString());

                // 선택된 파일 clear
                Singleton.getInstance().setSelectedFileDataListClear();
                // 파일 List 갱신
                showFileList(file);

                // 키보드 숨기기
                imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
            });
            builder.setNegativeButton(
                    "취소",
                    (dialog, which) ->{
                        // 현재 화면의 선택된 파일 List 선택 해제
                        fileListAdapter.setClearSelectedFileList();
                        // 키보드 숨기기
                        imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
                    });

            builder.show();
        }

        if(dialogMode == DialogMode.DIALOG_INFO){
            // 배경 터치 불가
            builder.setCancelable(false);

            builder.setTitle(R.string.file_info);
            // 한 개만 가능
//            File selectedFile = selectedDataList.get(0).getFile();
            FileData selectedFile = Singleton.getInstance().getSelectedFileDataList().get(0);

            // TODO: file정보 layout에 적용
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.alert_info_dialog, null);
            builder.setView(view);
            TextView tvFileName = (TextView) view.findViewById(R.id.tvFileName);
            TextView tvFileSize = (TextView) view.findViewById(R.id.tvFileSize);
            TextView tvFileLastModify = (TextView) view.findViewById(R.id.tvFileLastModify);
            TextView tvFileSubInfo = (TextView) view.findViewById(R.id.tvFileSubInfo);
            TextView tvFilePath = (TextView) view.findViewById(R.id.tvFilePath);

            FileInfo fileInfo = new FileInfo(this, selectedFile.getFile());

            if(file.exists()) {
                tvFileName.setText(fileInfo.getFileName());
                tvFileSize.setText(fileInfo.getFileSize());
                tvFileLastModify.setText(fileInfo.getFileLastModify());

                if(file.isDirectory()) {
                    tvFileSubInfo.setVisibility(View.VISIBLE);
                    tvFileSubInfo.setText(
                            //TODO: 변경
                            String.format(getResources().getString(R.string.file_contents),
                                    fileInfo.getTotalFolderNum(), fileInfo.getTotalFileNum()
                            ));
                }
                else
                    tvFileSubInfo.setVisibility(View.GONE);

                tvFilePath.setText(fileInfo.getFilePath());
            }
            // 현재 화면의 선택된 파일 List 선택 해제
            fileListAdapter.setClearSelectedFileList();

            builder.setPositiveButton("확인", (dialog, which) -> dialog.dismiss());

            builder.show();
        }

        if(dialogMode == DialogMode.DIALOG_MIMETYPE){
            // 배경 터치 불가
            builder.setCancelable(false);

            builder.setTitle(R.string.file_info);
            // 한 개만 가능
//            File selectedFile = selectedDataList.get(0).getFile();
            FileData selectedFile = Singleton.getInstance().getSelectedFileDataList().get(0);
            showMimeTypeFileList(selectedFile.getFile());

            // TODO: file정보 layout에 적용
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.alert_info_dialog, null);
            builder.setView(view);
            TextView tvFileName = (TextView) view.findViewById(R.id.tvFileName);
            TextView tvFileSize = (TextView) view.findViewById(R.id.tvFileSize);
            TextView tvFileLastModify = (TextView) view.findViewById(R.id.tvFileLastModify);
            TextView tvFileSubInfo = (TextView) view.findViewById(R.id.tvFileSubInfo);
            TextView tvFilePath = (TextView) view.findViewById(R.id.tvFilePath);

            FileInfo fileInfo = new FileInfo(this, selectedFile.getFile());

            if(file.exists()) {
                tvFileName.setText(fileInfo.getFileName());
                tvFileSize.setText(fileInfo.getFileSize());
                tvFileLastModify.setText(fileInfo.getFileLastModify());

                if(file.isDirectory()) {
                    tvFileSubInfo.setVisibility(View.VISIBLE);
                    tvFileSubInfo.setText(
                            //TODO: 변경
                            String.format(getResources().getString(R.string.file_contents),
                                    fileInfo.getTotalFolderNum(), fileInfo.getTotalFileNum()
                            ));
                }
                else
                    tvFileSubInfo.setVisibility(View.GONE);

                tvFilePath.setText(fileInfo.getFilePath());
            }
            // 현재 화면의 선택된 파일 List 선택 해제
            fileListAdapter.setClearSelectedFileList();

            builder.setPositiveButton("확인", (dialog, which) -> {
                dialog.dismiss();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            });

            builder.show();
        }

        if(dialogMode == DialogMode.DIALOG_ADD){
            // 배경 터치 불가
            builder.setCancelable(false);

            EditText edittext = new EditText(this);

            // Edittext margin 주기 위해 LinearLayout 구현
            LinearLayout container = new LinearLayout(getApplicationContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.topMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
            params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
            params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
            params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.dialog_bottom_margin);
            container.setOrientation(LinearLayout.VERTICAL);

            edittext.setLayoutParams(params);
            edittext.setSingleLine();

            // edittext 기존 파일 이름
            edittext.setText("");
            edittext.requestFocus();
//            edittext.selectAll();

            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            textParams.topMargin = getResources().getDimensionPixelSize(R.dimen.dialog_bottom_margin);
            textParams.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
            textParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
            textParams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.dialog_bottom_margin);

            TextView tvNotice = new TextView(this);
            tvNotice.setLayoutParams(textParams);
            tvNotice.setTextSize(12);
            tvNotice.setTextColor(ContextCompat.getColor(this, R.color.red));
            tvNotice.setText(getString(R.string.already_exist));
            tvNotice.setVisibility(View.INVISIBLE);


            // 키보드 관리자 생성 및 보이기
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            // 키보드가 올라와있으면 내리고, 내려와있으면 올림
            // 열리는 용도로 사용 -> ShowSoftInput이 안먹힘
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

            container.addView(edittext);
            container.addView(tvNotice);

            builder.setTitle(R.string.create_folder);
            builder.setView(container);
            builder.setPositiveButton(getString(R.string.confirm), (dialog, which) -> {});
            builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {});

            AlertDialog dialog = builder.create();
            dialog.show();

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {
                        String path = file.getPath() + "/" + edittext.getText().toString();
                        Log.d(TAG, "path = " + path);
                        // 존재하면 true, 존재하지 않으면 dir.mkdir();
                        boolean wantToCloseDialog = !fileManage.createDirectory(path);

                        if(wantToCloseDialog){   // 기존 폴더가 존재하지 않으면
                            tvNotice.setVisibility(View.VISIBLE);
//                            Toast.makeText(this, "대상이 이미 존재합니다.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            // 키보드 숨기기
                            imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
                            dialog.dismiss();
                            showFileList(file);
                            tvNotice.setVisibility(View.INVISIBLE);
                        }

                    });
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setOnClickListener(v -> {
                        // 키보드 숨기기
                        imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
                        dialog.dismiss();
                    });
        }
    }


//    protected void showDialog(DialogMode dialogMode, ArrayList<FileData> selectedDataList){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        // builder 이용해서는 dismiss() 수행 불가능, builder를 담을 AlertDialog 객체 생성
////        AlertDialog alertDialog = builder.create();
//
//        if(dialogMode == DialogMode.DIALOG_DELETE) {
//            // 배경 터치 불가
//            builder.setCancelable(false);
//
//            builder.setTitle(R.string.file_delete);
//            builder.setMessage(String.format(getResources().getString(R.string.confirm_delete), selectedDataList.size()));
//            builder.setPositiveButton("확인", (dialog, which) -> {
//                        // 현재 화면에서 선택된 fileList 갖고옴
//                        selectedFileDataList = selectedDataList;
//                        // 현재 화면의 선택된 파일 List 선택 해제
//                        fileListAdapter.setClearSelectedFileList();
//
//                        //삭제 함수
//                        fileManage.deleteFile(selectedFileDataList);
//
//                        // 선택된 파일 clear
//                        selectedFileDataList.clear();
//                        // 파일 List 갱신
//                        showFileList(file);
//
//                    });
//            builder.setNegativeButton("취소",
//                            (dialog, which) ->
//                            // 현재 화면의 선택된 파일 List 선택 해제
//                            fileListAdapter.setClearSelectedFileList());
//
//            builder.show();
//        }
//
//        if(dialogMode == DialogMode.DIALOG_RENAME){
//            // 배경 터치 불가
//            builder.setCancelable(false);
//
//            EditText edittext = new EditText(this);
//
//            // Edittext margin 주기 위해 LinearLayout 구현
//            LinearLayout container = new LinearLayout(getApplicationContext());
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            params.topMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
//            params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
//            params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
//
//            edittext.setLayoutParams(params);
//            edittext.setSingleLine();
//
//            // edittext 기존 파일 이름
//            edittext.setText(selectedDataList.get(0).getFile().getName());
//            edittext.requestFocus();
//            edittext.selectAll();
//
//            // 키보드 관리자 생성 및 보이기
//            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//            // 키보드가 올라와있으면 내리고, 내려와있으면 올림
//            // 열리는 용도로 사용 -> ShowSoftInput이 안먹힘
//            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
//
//            container.addView(edittext);
//
//            builder.setTitle(R.string.file_rename);
//            builder.setView(container);
//            builder.setPositiveButton("이름 변경", (dialog, which) -> {
//                /*
//                 * 이름 변경은 하나의 파일만 가능 but 사용하는 함수 쓰기 위해 selectedFileDataList 사용
//                 */
//                // 현재 화면에서 선택된 fileList 갖고옴
//                selectedFileDataList = selectedDataList;
//                // 현재 화면의 선택된 파일 List 선택 해제
//                fileListAdapter.setClearSelectedFileList();
//
//                //이름 변경 함수
//                fileManage.renameFile(selectedFileDataList, edittext.getText().toString());
//
//                // 선택된 파일 clear
//                selectedFileDataList.clear();
//                // 파일 List 갱신
//                showFileList(file);
//
//                // 키보드 숨기기
//                imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
//            });
//            builder.setNegativeButton(
//            "취소",
//                (dialog, which) ->{
//                    // 현재 화면의 선택된 파일 List 선택 해제
//                    fileListAdapter.setClearSelectedFileList();
//                    // 키보드 숨기기
//                    imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
//                });
//
//            builder.show();
//        }
//
//        if(dialogMode == DialogMode.DIALOG_INFO){
//            // 배경 터치 불가
//            builder.setCancelable(false);
//
//            builder.setTitle(R.string.file_info);
//            // 한 개만 가능
////            File selectedFile = selectedDataList.get(0).getFile();
//            FileData selectedFile = selectedDataList.get(0);
//
//            // TODO: file정보 layout에 적용
//            LayoutInflater inflater = getLayoutInflater();
//            View view = inflater.inflate(R.layout.alert_info_dialog, null);
//            builder.setView(view);
//            TextView tvFileName = (TextView) view.findViewById(R.id.tvFileName);
//            TextView tvFileSize = (TextView) view.findViewById(R.id.tvFileSize);
//            TextView tvFileLastModify = (TextView) view.findViewById(R.id.tvFileLastModify);
//            TextView tvFileSubInfo = (TextView) view.findViewById(R.id.tvFileSubInfo);
//            TextView tvFilePath = (TextView) view.findViewById(R.id.tvFilePath);
//
//            FileInfo fileInfo = new FileInfo(this, selectedFile.getFile());
//
//            if(file.exists()) {
//                tvFileName.setText(fileInfo.getFileName());
//                tvFileSize.setText(fileInfo.getFileSize());
//                tvFileLastModify.setText(fileInfo.getFileLastModify());
//
//                if(file.isDirectory()) {
//                    tvFileSubInfo.setVisibility(View.VISIBLE);
//                    tvFileSubInfo.setText(
//                            //TODO: 변경
//                            String.format(getResources().getString(R.string.file_contents),
//                                    fileInfo.getTotalFolderNum(), fileInfo.getTotalFileNum()
//                            ));
//                }
//                else
//                    tvFileSubInfo.setVisibility(View.GONE);
//
//                tvFilePath.setText(fileInfo.getFilePath());
//            }
//            // 현재 화면의 선택된 파일 List 선택 해제
//            fileListAdapter.setClearSelectedFileList();
//
//            builder.setPositiveButton("확인", (dialog, which) -> dialog.dismiss());
//
//            builder.show();
//        }
//    }
}