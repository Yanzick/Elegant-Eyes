package com.example.store;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class Photo1Adapter extends RecyclerView.Adapter<Photo1Adapter.PhotoViewHolder> {
    private List<Photo1> mUriList;

    public Photo1Adapter(List<Photo1> uriList) {
        this.mUriList = uriList;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        String uri = mUriList.get(position).getImageUrl(); // Lấy URL hình ảnh từ đối tượng Photo1
        Picasso.get().load(uri).into(holder.imgPhoto); // Tải hình ảnh từ URL và gắn vào ImageView
    }


    @Override
    public int getItemCount() {
        return mUriList != null ? mUriList.size() : 0;
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgPhoto;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.img_photo);
        }
    }
}
