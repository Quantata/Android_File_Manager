package com.example.nyahn_fileexplorer;

import android.content.Context;
import android.util.Log;
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

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {
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
            mCallback.onShowBottomLayout();
            return true;
        });
        // 짧게 클릭했을 때
        holder.llFolder.setOnClickListener(v ->
        {
            File file = mCallback.onGetParentFile();
            // file 객체 폴더의 선택된 파일에 대한 파일 객체를 생성
            File clickedFile = new File(file, fileDataList.get(position).getFile().getName());

            Log.i("FileList : ", ""+ clickedFile.isFile());

            if(!clickedFile.isFile())
            {
                mCallback.onSetParentFile(clickedFile);
                fileDataList.clear();
                mCallback.onSetFileList(clickedFile);

                notifyDataSetChanged();
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
