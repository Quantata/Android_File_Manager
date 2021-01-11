package com.example.nyahn_fileexplorer.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nyahn_fileexplorer.Models.FileData;
import com.example.nyahn_fileexplorer.Models.Mode;
import com.example.nyahn_fileexplorer.OnItemClick;
import com.example.nyahn_fileexplorer.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

//  Adapter에서 MOVE_MODE = MOVE_MODE, COPY_MODE
public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {
    Context context;
    private static final String TAG = FileListAdapter.class.getSimpleName();

    // 현재 List의 파일들이 선택됐는지 확인하기 위한 Array
    private HashSet<Integer> selectedPositions = new HashSet<>();

    // Activity의 file 변수 Update 위함
    private OnItemClick mCallback;
    private ArrayList<FileData> fileDataList;

    public FileListAdapter(Context context, ArrayList<FileData> files, OnItemClick listener) {
        this.context = context;
        this.fileDataList = files;
        this.mCallback = listener;
    }

    // move모드일때
    public void fileSelectedMoveMode(ViewHolder holder, int position){
        fileDataList.get(position).setSelected(true);
        holder.llFolder.setBackgroundColor(ContextCompat.getColor(context, R.color.gray));

        selectedPositions.add(position);
        mCallback.onSetMode(Mode.SELECTED_MODE);

        mCallback.onShowBottomLayout();
    }

    // 선택된 파일 List
    public ArrayList<FileData> getSelectedFileList(){
        ArrayList<FileData> selectedList = new ArrayList<>();
        if(selectedPositions.size() != 0) {
            for (int selected : selectedPositions) {
                FileData fileData = fileDataList.get(selected);
                selectedList.add(fileData);
            }
        }
        return selectedList;    // 호출하는 쪽에서 NPE관련 처리 해줘야함
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
            holder.llFolder.setBackgroundColor(Color.WHITE);

            // 선택된 부분을 배열에서 지우고 size가 0인 경우 basic_mode로 변경
            selectedPositions.remove(position);
            if(selectedPositions.size() == 0){
                mCallback.onSetMode(Mode.BASIC_MODE);
            }
        } else {
            // 선택되지 않은 상태라면 선택
            fileDataList.get(position).setSelected(true);
            holder.llFolder.setBackgroundColor(ContextCompat.getColor(context, R.color.gray));

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
        // 나중에 fileData Type에 따라 이미지 변환할 수 있도록
        if (!fileDataList.get(position).getFile().isFile()){
            holder.ivFolderImage.setImageResource(R.drawable.folder);
        }
        else {
            holder.ivFolderImage.setImageResource(R.drawable.text);
        }
        holder.tvFolderName.setText(fileDataList.get(position).getFile().getName());


        // Color 설정
        if(fileDataList.get(position).isSelected())
            holder.llFolder.setBackgroundColor(ContextCompat.getColor(context, R.color.gray));
        else
            holder.llFolder.setBackgroundColor(Color.WHITE);

        // 길게 클릭했을 때
        holder.llFolder.setOnLongClickListener(v -> {
            if(mCallback.onGetMode() == Mode.MOVE_MODE ||
                mCallback.onGetMode() == Mode.COPY_MODE){ // move, copy mode였을때
                // 선택된 파일 리스트 해제하는 함수
                setClearSelectedFileList();

                // 선택된 부분 click
                fileSelectedMoveMode(holder, holder.getAdapterPosition());
            }
            else { // basic_mode, selected_mode
                fileSelected(holder, holder.getAdapterPosition());

            }
            return true;
        });

        // 짧게 클릭했을 때
        holder.llFolder.setOnClickListener(v ->
        {
            if(mCallback.onGetMode() == Mode.BASIC_MODE ||
                mCallback.onGetMode() == Mode.MOVE_MODE ||
                mCallback.onGetMode() == Mode.COPY_MODE){

                File file = mCallback.onGetParentFile();
                // file 객체 폴더의 선택된 파일에 대한 파일 객체를 생성
                File clickedFile = new File(file, fileDataList.get(position).getFile().getName());

                Log.d(TAG, "LongClicked isFile : " + clickedFile.isFile());

                // 디렉토리일때
                if (clickedFile.isDirectory()) {
                    mCallback.onSetParentFile(clickedFile);
                    fileDataList.clear();
                    mCallback.onSetFileList(clickedFile);

                    notifyDataSetChanged();

                    // toolbar title 변경
                    mCallback.onSetToolbarTitle(clickedFile.getName());
                }
                /*
                    TODO: 일반 파일일때 구현

                else {

                }
                */
            }
            else if(mCallback.onGetMode() == Mode.SELECTED_MODE){
                fileSelected(holder, holder.getAdapterPosition());
            }

        });

    }

    @Override
    public int getItemCount() {
        return fileDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFolderImage;
        TextView tvFolderName;
        LinearLayout llFolder;

        // bottom layout
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llFolder = itemView.findViewById(R.id.llFolder);
            ivFolderImage = itemView.findViewById(R.id.ivFolderImage);
            tvFolderName = itemView.findViewById(R.id.tvFolderName);

        }
    }
}
