package com.example.nyahn_fileexplorer;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nyahn_fileexplorer.models.FileData;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {
    // 선택됐는지 확인하기 위한 Array
    private SparseBooleanArray mSelectedItems = new SparseBooleanArray(0);
    private HashSet<Integer> mSelectedPositions = new HashSet<>();

    // Activity의 file 변수 Update 위함
    private OnItemClick mCallback;
    private final ArrayList<FileData> fileDataList;
    public FileListAdapter(ArrayList<FileData> files, OnItemClick listener) {
        this.fileDataList = files;
        this.mCallback = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.listview_item, parent, false) ;
        FileListAdapter.ViewHolder vh = new FileListAdapter.ViewHolder(view) ;

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


        // 길게 클릭했을 때
        holder.llFolder.setOnLongClickListener(v -> {
            if(mCallback.onGetMode() == Mode.MOVE_MODE){ // move mode였을때
                for(int i : mSelectedPositions) {
                    if (holder.getAdapterPosition() == i)
                        holder.llFolder.setBackgroundColor(Color.WHITE);
                }
                mSelectedPositions.clear();
                notifyDataSetChanged();
            }
            else {
                // false : 선택되지 않음
                // true : 선택됨
                boolean selected = mSelectedItems.get(holder.getAdapterPosition());

                // Basic_Mode였다면 SelectedMode
                // Selected_Mode라면 아무일도 일어나지 않도록

                if (selected) {
                    // 선택된 상태라면 선택 해제
                    mSelectedItems.put(holder.getAdapterPosition(), false);
                    holder.llFolder.setBackgroundColor(Color.WHITE);

                    // 선택된 부분을 배열에서 지우고 size가 0인 경우 basic_mode로 변경
                    mSelectedPositions.remove(holder.getAdapterPosition());
                    if(mSelectedPositions.size() == 0){
                        mCallback.onSetMode(Mode.BASIC_MODE);
                    }
                } else {
                    // 선택되지 않은 상태라면 선택
                    mSelectedItems.put(holder.getAdapterPosition(), true);
                    holder.llFolder.setBackgroundColor(Color.GRAY);

                    mSelectedPositions.add(holder.getAdapterPosition());
                    mCallback.onSetMode(Mode.SELECTED_MODE);
                }
                mCallback.onShowBottomLayout();



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

                if (!clickedFile.isFile()) {
                    mCallback.onSetParentFile(clickedFile);
                    fileDataList.clear();
                    mCallback.onSetFileList(clickedFile);

                    notifyDataSetChanged();
                }
            }
            else if(mCallback.onGetMode() == Mode.SELECTED_MODE){
                // false : 선택되지 않음
                // true : 선택됨
                boolean selected = mSelectedItems.get(holder.getAdapterPosition());

                if (selected) {
                    // 선택된 상태라면 선택 해제
                    mSelectedItems.put(holder.getAdapterPosition(), false);
                    holder.llFolder.setBackgroundColor(Color.WHITE);

                    // 선택된 부분을 배열에서 지우고 size가 0인 경우 basic_mode로 변경
                    mSelectedPositions.remove(holder.getAdapterPosition());
                    if(mSelectedPositions.size() == 0){
                        mCallback.onSetMode(Mode.BASIC_MODE);
                    }
                } else {
                    // 선택되지 않은 상태라면 선택
                    mSelectedItems.put(holder.getAdapterPosition(), true);
                    holder.llFolder.setBackgroundColor(Color.GRAY);

                    mSelectedPositions.add(holder.getAdapterPosition());
                    mCallback.onSetMode(Mode.SELECTED_MODE);
                }
                mCallback.onShowBottomLayout();
            }

        });

    }

    @Override
    public int getItemCount() {
        return fileDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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
