package com.example.store;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class QLKHAdapter extends RecyclerView.Adapter<QLKHAdapter.BMIView> {
    private Context mContext;
    private List<QLKH> mList;
    private OnItemClickListener mListener;

    public QLKHAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<QLKH> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BMIView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kh_item, parent, false);
        return new BMIView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BMIView holder, int position) {
        QLKH BMI = mList.get(position);
        if (BMI == null) {
            return;
        }

        // Tải hình ảnh từ URL bằng Picasso

        Picasso.get().load(BMI.getImageUrl()).into(holder.imageView);

        holder.tvSanPham.setText(BMI.getName());

    }

    @Override
    public int getItemCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    public class BMIView extends RecyclerView.ViewHolder {
        private CircleImageView imageView;
        private TextView tvSanPham;

        public BMIView(@NonNull View itemView) {

            super(itemView);
            tvSanPham = itemView.findViewById(R.id.Text1);
            imageView = itemView.findViewById(R.id.Image1);
            itemView.setOnClickListener(view -> {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        String name = mList.get(position).getName();
                        mListener.onItemClick(position, name);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, String name);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }
}
