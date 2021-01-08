package com.example.nyahn_fileexplorer;

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
import androidx.recyclerview.widget.RecyclerView;

import com.example.nyahn_fileexplorer.Models.FileData;
import com.example.nyahn_fileexplorer.Models.Mode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

//  Adapter에서 MOVE_MODE = MOVE_MODE, COPY_MODE
public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {
    // 선택됐는지 확인하기 위한 Array
    private HashSet<Integer> mSelectedPositions = new HashSet<>();

    // Activity의 file 변수 Update 위함
    private OnItemClick mCallback;
    private ArrayList<FileData> fileDataList;

    public FileListAdapter(ArrayList<FileData> files, OnItemClick listener) {
        this.fileDataList = files;
        this.mCallback = listener;
    }

    // 선택된 파일 List
    public ArrayList<FileData> getSelectedFileList(){
        ArrayList<FileData> selectedList = new ArrayList<>();
        if(mSelectedPositions.size() != 0) {
            for (int selected : mSelectedPositions) {
                FileData fileData = fileDataList.get(selected);
                selectedList.add(fileData);
            }
        }
        return selectedList;    // 호출하는 쪽에서 NPE관련 처리 해줘야함
    }

    // 파일 선택 해제
    public void setClearSelectedFileList(){
        for(int i : mSelectedPositions) {
            fileDataList.get(i).setSelected(false);
            notifyItemChanged(i);
        }
        mSelectedPositions.clear();
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
            mSelectedPositions.remove(position);
            if(mSelectedPositions.size() == 0){
                mCallback.onSetMode(Mode.BASIC_MODE);
            }
        } else {
            // 선택되지 않은 상태라면 선택
            fileDataList.get(position).setSelected(true);
            holder.llFolder.setBackgroundColor(Color.GRAY);

            mSelectedPositions.add(position);
            mCallback.onSetMode(Mode.SELECTED_MODE);
        }
        mCallback.onShowBottomLayout();
    }

    public void fileSelectedMoveMode(ViewHolder holder, int position){
        fileDataList.get(position).setSelected(true);
        holder.llFolder.setBackgroundColor(Color.GRAY);

        mSelectedPositions.add(position);
        mCallback.onSetMode(Mode.SELECTED_MODE);

        mCallback.onShowBottomLayout();
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
            holder.llFolder.setBackgroundColor(Color.GRAY);
        else
            holder.llFolder.setBackgroundColor(Color.WHITE);

        // 길게 클릭했을 때
        holder.llFolder.setOnLongClickListener(v -> {
            if(mCallback.onGetMode() == Mode.MOVE_MODE){ // move mode였을때
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
                mCallback.onGetMode() == Mode.MOVE_MODE){
                File file = mCallback.onGetParentFile();
                // file 객체 폴더의 선택된 파일에 대한 파일 객체를 생성
                File clickedFile = new File(file, fileDataList.get(position).getFile().getName());

                Log.i("FileList : ", "" + clickedFile.isFile());

                if (clickedFile.isDirectory()) {
                    mCallback.onSetParentFile(clickedFile);
                    fileDataList.clear();
                    mCallback.onSetFileList(clickedFile);

                    notifyDataSetChanged();
                }
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
