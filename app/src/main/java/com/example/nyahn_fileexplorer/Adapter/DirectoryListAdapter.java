package com.example.nyahn_fileexplorer.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nyahn_fileexplorer.Models.FileData;
import com.example.nyahn_fileexplorer.R;

import java.util.ArrayList;

public class DirectoryListAdapter extends RecyclerView.Adapter<DirectoryListAdapter.ViewHolder>{
    private static final String TAG = DirectoryListAdapter.class.getSimpleName();

    private ArrayList<String> directoryList;

    public DirectoryListAdapter(ArrayList<String> directoryList) {
        this.directoryList = directoryList;
    }

    @NonNull
    @Override
    public DirectoryListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.directory_item, parent, false) ;
        DirectoryListAdapter.ViewHolder vh = new DirectoryListAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull DirectoryListAdapter.ViewHolder holder, int position) {
        holder.tvDirName.setText(directoryList.get(position));
    }

    @Override
    public int getItemCount() {
        return directoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivArrowView;
        TextView tvDirName;

        // directory_item.xml
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivArrowView = itemView.findViewById(R.id.ivArrowView);
            tvDirName = itemView.findViewById(R.id.tvDirName);

        }
    }
}
