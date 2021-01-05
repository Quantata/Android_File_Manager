package com.example.nyahn_fileexplorer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        holder.folderImage.setImageResource(R.drawable.folder);
        holder.folderName.setText(fileDataList.get(position).getFileName());

        holder.folderName.setOnClickListener(v ->
        {
            String parent = fileDataList.get(position).getParentDir();

            mCallback.onClick(parent);
            // file 객체 폴더의 fileList.get(position)라는 파일에 대한 파일 객체를 생성
            File clickedFile = new File(fileDataList.get(position).getPresentDir());

            Log.i("FileList : ", ""+ clickedFile.isFile());

            if(!clickedFile.isFile())
            {
//            file = new File( file, fileList.get( position ));
                File[] list = clickedFile.listFiles();

                fileDataList.clear();

                for (File value : list) {
                    FileData fileData = new FileData();
                    fileData.setParentDir(parent);
                    fileData.setFileName(value.getName());
                    fileData.setPresentDir(value.getPath());
                    fileDataList.add(fileData);
//                    fileDataList.add(value.getName());
                }

                notifyDataSetChanged();

            }
        });
    }

    @Override
    public int getItemCount() {
        return fileDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView folderImage;
        TextView folderName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            folderImage = itemView.findViewById(R.id.folderImage);
            folderName = itemView.findViewById(R.id.folderName);
        }
    }
}
