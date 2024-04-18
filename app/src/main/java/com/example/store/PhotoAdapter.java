package com.example.store;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>{
    private List<Photo> mList;

    public PhotoAdapter(List<Photo> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo,parent,false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Photo photo = mList.get(position);
        if (photo == null){return;}
        holder.imgPhoto.setImageResource(photo.getResourceID());
    }

    @Override
    public int getItemCount() {
        if (mList!= null) {
            return mList.size();
        }
            return 0;

    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder{
        private ImageView imgPhoto;
        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.img_photo);
        }
    }
}
