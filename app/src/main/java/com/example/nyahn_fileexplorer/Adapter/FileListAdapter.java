package com.example.nyahn_fileexplorer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nyahn_fileexplorer.BuildConfig;
import com.example.nyahn_fileexplorer.Models.FileData;
import com.example.nyahn_fileexplorer.Models.Mode;
import com.example.nyahn_fileexplorer.Interface.OnItemClick;
import com.example.nyahn_fileexplorer.R;
import com.example.nyahn_fileexplorer.Utils.FileInfo;
import com.example.nyahn_fileexplorer.Utils.Singleton;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {
    Context context;
    private static final String TAG = FileListAdapter.class.getSimpleName();

    // 현재 List의 파일들이 선택됐는지 확인하기 위한 Array
    private HashSet<Integer> selectedPositions = new HashSet<>();

    // Activity의 file 변수 Update 위함
    private OnItemClick mCallback;
    private ArrayList<FileData> fileDataList;
    TypedValue typedValue;


    public FileListAdapter(Context context, ArrayList<FileData> files, OnItemClick listener) {
        this.context = context;
        this.fileDataList = files;
        this.mCallback = listener;
    }

    // move, copy모드일때
    public void fileSelectedMoveMode(ViewHolder holder, int position){
        fileDataList.get(position).setSelected(true);

//        holder.RlFolder.setBackgroundColor(ContextCompat.getColor(context, R.color.gray));
        typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.bottomLayoutColor, typedValue, true);
        int bottomLayoutColor = typedValue.data;
        holder.RlFolder.setBackgroundColor(bottomLayoutColor);

        selectedPositions.add(position);
        mCallback.onSetMode(Mode.SELECTED_MODE);

        mCallback.onShowBottomLayout();
    }

    // 선택된 파일 List
//    public ArrayList<FileData> getSelectedFileList(){
//        ArrayList<FileData> selectedList = new ArrayList<>();
//        if(selectedPositions.size() != 0) {
//            for (int selected : selectedPositions) {
//                Log.d(TAG, "selected position =" + selected);
//                FileData fileData = fileDataList.get(selected);
//                selectedList.add(fileData);
//            }
//        }
//        return selectedList;    // 호출하는 쪽에서 NPE관련 처리 해줘야함
//    }

    public void setSingletonSelectedFileList(){
        ArrayList<FileData> selectedList = new ArrayList<>();
        if(selectedPositions.size() != 0) {
            for (int selected : selectedPositions) {
                Log.d(TAG, "selected position =" + selected);
                FileData fileData = fileDataList.get(selected);
                selectedList.add(fileData);
            }
            Singleton.getInstance().setSelectedFileDataList(selectedList);
        }
    }

    // 파일 선택 해제
    public void setClearSelectedFileList(){
        for(int i : selectedPositions) {
            fileDataList.get(i).setSelected(false);
            notifyItemChanged(i);
        }

        selectedPositions.clear();  // 선택된 파일 위치 리스트 삭제
    }

    // 파일 선택 로직
    public void fileSelected(@NonNull ViewHolder holder, int position){
        // false : 선택되지 않음
        // true : 선택됨

        boolean selected = fileDataList.get(position).isSelected();

        // Basic_Mode였다면 SelectedMode
        // Selected_Mode라면 아무일도 일어나지 않도록
        if (selected) {
            // 선택된 상태라면 선택 해제
            fileDataList.get(position).setSelected(false);
            holder.RlFolder.setBackgroundColor(Color.WHITE);

            // 선택된 부분을 배열에서 지우고 size가 0인 경우 basic_mode로 변경
            selectedPositions.remove(position);
            if(selectedPositions.size() == 0){
                mCallback.onSetMode(Mode.BASIC_MODE);
            }
        } else {
            // 선택되지 않은 상태라면 선택
            fileDataList.get(position).setSelected(true);
//            holder.RlFolder.setBackgroundColor(ContextCompat.getColor(context, R.color.gray));

            // BottomLayoutColor로 변경
            typedValue = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.bottomLayoutColor, typedValue, true);
            int bottomLayoutColor = typedValue.data;
            holder.RlFolder.setBackgroundColor(bottomLayoutColor);

            selectedPositions.add(position);
            mCallback.onSetMode(Mode.SELECTED_MODE);
        }
        mCallback.onShowBottomLayout();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.listview_item, parent, false) ;
        FileListAdapter.ViewHolder vh = new ViewHolder(view);

        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int holderPosition = holder.getAdapterPosition();
        FileData currentFileData = fileDataList.get(holder.getAdapterPosition());
        // 나중에 fileData Type에 따라 이미지 변환할 수 있도록
        if (currentFileData.getFile().isDirectory()){
            holder.ivFolderImage.setImageResource(R.drawable.folder);
            holder.tvCountORSize.setText(
                    String.format(context.getResources().getString(R.string.file_count)
                            , currentFileData.getFolderNum()+currentFileData.getFileNum()
            ));
        }
        else {
//            String fileName = FilenameUtils.getBaseName(uploadfile.getOriginalFilename());
            String fileName = currentFileData.getFile().getName();

            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            // png
            // 공백이 있을때 잘 안됨
//            String extension = MimeTypeMap.getFileExtensionFromUrl(currentFileData.getFile().getName());
            String extension = fileName.substring( fileName.lastIndexOf(".") + 1 );

            // image/png
            currentFileData.setFileExtension(mimeTypeMap.getMimeTypeFromExtension(extension));

            int resId;
            switch (extension){
                case "pdf": resId = R.drawable.pdf; break;
                case "psd": resId = R.drawable.psd;break;
                case "txt": resId = R.drawable.txt; break;
                case "ai" : resId = R.drawable.ai; break;
                case "zip":
                case "alz":
                case "7zip" : resId = R.drawable.zip; break;
                case "jpg":
                case "jpeg": resId = R.drawable.jpg; break;
//                case "apk" : resId = R.drawable.apk; break;
                case "png": resId = R.drawable.png; break;
                case "gif": resId = R.drawable.gif; break;
                case "avi": resId = R.drawable.avi; break;
                case "doc":
                case "docx": resId = R.drawable.doc; break;
                case "mp3" : resId = R.drawable.mp3; break;
//                case "mp4" : resId = R.drawable.mp4; break;
                case "xls" :
                case "xlsx" : resId = R.drawable.xls; break;
                case "ppt":
                case "pptx": resId = R.drawable.ppt; break;
//                case "hwp": resId = R.drawable.hwp; break;

                default: resId = R.drawable.blank_file; break;
            }
            holder.ivFolderImage.setImageResource(resId);
            holder.tvCountORSize.setText(new FileInfo(context, currentFileData.getFile()).getFileSize());
        }
        holder.tvFolderName.setText(currentFileData.getFile().getName());
        holder.tvLastModified.setText(
                new FileInfo(context, currentFileData.getFile()).getFileLastModify()
        );

        // Color 설정
        if(currentFileData.isSelected()) {
//            holder.RlFolder.setBackgroundColor(ContextCompat.getColor(context, R.color.gray));
            typedValue = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.bottomLayoutColor, typedValue, true);
            int bottomLayoutColor = typedValue.data;
            holder.RlFolder.setBackgroundColor(bottomLayoutColor);
        } else {
            holder.RlFolder.setBackgroundColor(Color.WHITE);
        }

        // 길게 클릭했을 때
        holder.RlFolder.setOnLongClickListener(v -> {
            if(mCallback.onGetMode() == Mode.MOVE_MODE ||
                mCallback.onGetMode() == Mode.COPY_MODE){ // move, copy mode였을때
                // 선택된 파일 리스트 해제하는 함수
                setClearSelectedFileList();

                // 선택된 부분 click
                fileSelectedMoveMode(holder, holderPosition);
            }
            else { // basic_mode, selected_mode
                fileSelected(holder, holderPosition);
            }

            // 이름 변경 활성화 관련
            mCallback.onSetChangeStatus(!(selectedPositions.size() > 1));

            return true;
        });

        // 짧게 클릭했을 때
        holder.RlFolder.setOnClickListener(v ->
        {
            if(mCallback.onGetMode() == Mode.BASIC_MODE ||
                mCallback.onGetMode() == Mode.MOVE_MODE ||
                mCallback.onGetMode() == Mode.COPY_MODE){

                File file = mCallback.onGetCurrentFile();
                // file 객체 폴더의 선택된 파일에 대한 파일 객체를 생성
                File clickedFile = new File(file, currentFileData.getFile().getName());

                Log.d(TAG, "LongClicked isFile : " + clickedFile.isFile());

                // 디렉토리일때
                if (clickedFile.isDirectory()) {
                    mCallback.onSetCurrentFile(clickedFile);
                    fileDataList.clear();
                    mCallback.onSetFileList(clickedFile);

                    notifyDataSetChanged();

                    mCallback.onAddDirectoryList(clickedFile);
                }

                //    TODO: 일반 파일일때 여는거 구현
                else {

                    // https://developer.android.com/training/sharing/send?hl=ko 일단 대충 만들음.
                    // pdf면 pdf관련 파일만 나오도록 해야함
//                    Uri fileUri = Uri.fromFile(currentFileData.getFile());// content file uri로 변경
                    Uri fileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",
                            currentFileData.getFile());


                    if(fileUri != null) {
                        Intent sendIntent = new Intent();

                        sendIntent.setAction(Intent.ACTION_VIEW);
//                        sendIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
//                        sendIntent.setType(currentFileData.getFileExtension());
                        sendIntent.setDataAndType(fileUri, currentFileData.getFileExtension());
                        // 권한 부여
                        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        sendIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                        // 열 앱이 있는지
                        PackageManager packageManager = context.getPackageManager();
                        List<ResolveInfo> activities = packageManager.queryIntentActivities(sendIntent,
                                PackageManager.MATCH_DEFAULT_ONLY);
                        boolean isIntentSafe = activities.size() > 0;

                        if (isIntentSafe) {
                            // chooser 열기
                            Intent chooser = Intent.createChooser(sendIntent, context.getResources().getString(R.string.conn_other_app));
                            context.startActivity(chooser);
                        }
                        // else { } // TODO: 연결할 앱이 없을때 활동
                    }
                }

            }
            else if(mCallback.onGetMode() == Mode.SELECTED_MODE){
                fileSelected(holder, holderPosition);
            }

            // 이름 변경 활성화 관련
            mCallback.onSetChangeStatus(!(selectedPositions.size() > 1));
        });

    }

    @Override
    public int getItemCount() {
        return fileDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFolderImage;
        TextView tvFolderName;
        RelativeLayout RlFolder;
        TextView tvLastModified;
        TextView tvCountORSize;

        // bottom layout
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            RlFolder = itemView.findViewById(R.id.RlFolder);
            ivFolderImage = itemView.findViewById(R.id.ivFolderImage);
            tvFolderName = itemView.findViewById(R.id.tvFolderName);

            tvLastModified = itemView.findViewById(R.id.tvLastModified);
            tvCountORSize = itemView.findViewById(R.id.tvCountORSize);
        }
    }
}
