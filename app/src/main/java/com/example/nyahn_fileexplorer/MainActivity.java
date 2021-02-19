package com.example.nyahn_fileexplorer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nyahn_fileexplorer.Activity.FileListActivity;
import com.example.nyahn_fileexplorer.Models.Mode;
import com.example.nyahn_fileexplorer.Utils.Define;
import com.example.nyahn_fileexplorer.Utils.FileInfo;
import com.example.nyahn_fileexplorer.Utils.Singleton;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    LinearLayout llMainStorage;
    LinearLayout llSdcardStorage;
    LinearLayout llAppStorage;

    LinearLayout cdBottomSheet;
    LinearLayout llBottomMoveLayout;
    LinearLayout llFilePaste;
    TextView tvFilePaste;
    LinearLayout llCancel;

    TextView tvInnerVolume;
    TextView tvSdcardVolume;
    TextView tvAppStorageVolume;

    File innerFile;
    File sdcardFile;
    File appFile;

    FileInfo fileInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolbarTitle();

        init();
        initClick();
        setupPermission();

        onShowBottomLayout();
    }

    public void onShowBottomLayout() {
        Mode currentMode = Singleton.getInstance().getCurrentMode();

        if(currentMode == Mode.MOVE_MODE ||
                currentMode == Mode.COPY_MODE){
            cdBottomSheet.setVisibility(View.VISIBLE);
            llBottomMoveLayout.setVisibility(View.VISIBLE);
        }
        else { // Basic_mode
            cdBottomSheet.setVisibility(View.GONE);
            llBottomMoveLayout.setVisibility(View.GONE);
        }
    }

    // request permission for sdcard
    private void setupPermission() {
        //check for permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            //ask for permission
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        // check for write permission
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    // 파일 버튼 클릭시
    public void onClickButton(View view) {
        // TODO: if/else로 변경 static에서 기본형으로 바뀌면서 효율성 면에서 R.id.~ 사용시 if/else문으로 사용 하는걸 권장
        switch (view.getId()) {
            case R.id.llCancel:
                Singleton.getInstance().setCurrentMode(Mode.BASIC_MODE);
                onShowBottomLayout();
                Singleton.getInstance().setSelectedFileDataListClear();

                break;
        }
    }
    public void init() {
        cdBottomSheet = (LinearLayout) findViewById(R.id.cdBottomSheet);
        llBottomMoveLayout = (LinearLayout) findViewById(R.id.llBottomMoveLayout);
        llCancel = (LinearLayout) findViewById(R.id.llCancel);
        llFilePaste = (LinearLayout) findViewById(R.id.llFilePaste);
        tvFilePaste = findViewById(R.id.tvFilePaste);

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.bottomTextColorHint, typedValue, true);
        // 붙여넣기
        tvFilePaste.setTextColor(typedValue.data);
        tvFilePaste.setClickable(false);
        llFilePaste.setClickable(false);


        llMainStorage = (LinearLayout)findViewById(R.id.llMainStorage);
        llSdcardStorage = (LinearLayout)findViewById(R.id.llSdcardStorage);
        llAppStorage = (LinearLayout)findViewById(R.id.llAppStorage);

        tvInnerVolume = (TextView) findViewById(R.id.tvInnerVolume);
        tvSdcardVolume = (TextView) findViewById(R.id.tvSdcardVolume);
        tvAppStorageVolume = (TextView) findViewById(R.id.tvAppStorageVolume);

        // 내부 저장소 저장용량 계산
        innerFile =  new File(Environment.getExternalStorageDirectory().getAbsolutePath());

        // 파일 정보
        fileInfo = new FileInfo(this, innerFile);

//        int usuableSpace = (int) Math.round(innerFile.getFreeSpace() / Math.pow(1024, 3));
//        int totalSpace = (int) Math.round(innerFile.getTotalSpace() / Math.pow(1024, 3));
//        int usedSpace = totalSpace - usuableSpace;
//
//        // TODO : 주석 해제
//        tvInnerVolume.setText(String.format("%dGB / %dGB", usedSpace, totalSpace));


        // TODO : 주석 해제
        tvInnerVolume.setText(String.format("%s / %s",
                fileInfo.getUsingMemory(), fileInfo.getTotalMemory()));

        // sdcard 폴더 갖고 오기
        File sdcard = null;
        sdcardFile = new File(Environment.getStorageDirectory().getAbsolutePath());
        File[] list = sdcardFile.listFiles();
        // sdcard 이름에 (-)이 포함되어 있음.
        for(File el : list){
            if(el.getName().contains("-")){
                sdcard = el;
                break;
            }
        }


        if(sdcard != null) {
//            usuableSpace = (int) Math.round(sdcardFile.getFreeSpace() / Math.pow(1024, 3));
//            totalSpace = (int) Math.round(sdcardFile.getTotalSpace() / Math.pow(1024, 3));
//            usedSpace = totalSpace - usuableSpace;
//            tvSdcardVolume.setText(String.format("%dGB / %dGB", usedSpace, totalSpace));

            fileInfo = new FileInfo(this, sdcard);
            tvSdcardVolume.setText(String.format("%s / %s",
                    fileInfo.getUsingMemory(),
                    fileInfo.getTotalMemory()));
        } else {
            // sdcard가 없을 경우 선택 비활성화
            llSdcardStorage.setEnabled(false);
        }


        // 앱 내부파일 가져오기
        appFile = new File(getApplicationContext().getFilesDir().getPath());
        Log.d(TAG, "appFile = " + appFile.getPath());
        tvAppStorageVolume.setVisibility(View.GONE);

    }
    public void initClick(){
        Intent intent;
        Bundle bundle = new Bundle();
        intent = new Intent(MainActivity.this, FileListActivity.class);

        llMainStorage.setOnClickListener(view ->{
            bundle.putString(Define.STORAGE, innerFile.getPath());
            bundle.putString(Define.STORAGE_NAME,
                    getApplicationContext().getResources().getString(R.string.main_storage));
            bundle.putInt(Define.ROOTDIR_IMAGE,
                    R.drawable.inbox);
            intent.putExtras(bundle);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });

        llSdcardStorage.setOnClickListener(v -> {
            bundle.putString(Define.STORAGE, sdcardFile.getPath());
            bundle.putString(Define.STORAGE_NAME,
                    getApplicationContext().getResources().getString(R.string.sdcard_storage));
            bundle.putInt(Define.ROOTDIR_IMAGE,
                    R.drawable.sdcard);
            intent.putExtras(bundle);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });

        llAppStorage.setOnClickListener(v -> {
            bundle.putString(Define.STORAGE, appFile.getPath());
            bundle.putString(Define.STORAGE_NAME,
                    getApplicationContext().getResources().getString(R.string.app_storage));
            bundle.putInt(Define.ROOTDIR_IMAGE,
                    R.drawable.blank_file);
            intent.putExtras(bundle);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });
    }

    private void setToolbarTitle(){
        // toolbar as actionbar
        // setting toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_main_page);
        setSupportActionBar(toolbar);
    }
}