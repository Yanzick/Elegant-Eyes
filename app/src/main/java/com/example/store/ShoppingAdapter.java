package com.example.store;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.ViewHolder> {

    private List<Shopping> itemList;
    private Context context;

    public ShoppingAdapter(Context context, List<Shopping> itemList) {
        this.context = context;
        this.itemList = itemList;
    }
    public void setData(List<Shopping> productList) {
        this.itemList = productList;
        notifyDataSetChanged(); // Cập nhật giao diện sau khi thay đổi dữ liệu
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Shopping item = itemList.get(position);
        if (item == null){
            return;
        }
        holder.txtName.setText(item.getName());
        holder.txtPrice.setText(String.valueOf(item.getPrice()));
        holder.txtRating.setText(String.valueOf(item.getRating()));

        Picasso.get().load(item.getImageUrl()).into(holder.imgProduct);
        holder.Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickGoto(item);
            }
        });

    }
    private void onClickGoto(Shopping item){
        Intent intent =  new Intent(context, SanPham.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Name", item);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout Layout;

        ImageView imgProduct;
        TextView txtName, txtPrice, txtRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Layout = itemView.findViewById(R.id.item);
            imgProduct = itemView.findViewById(R.id.image_product);
            txtName = itemView.findViewById(R.id.text_product_name);
            txtPrice = itemView.findViewById(R.id.text_product_price);
            txtRating = itemView.findViewById(R.id.rating_product);
        }
    }
}

