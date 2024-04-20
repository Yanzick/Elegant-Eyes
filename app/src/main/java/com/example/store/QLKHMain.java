package com.example.store;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class QLKHMain extends AppCompatActivity {
    private RecyclerView recyclerViewSanPham;
    private QLKHAdapter BMIAdapter;
    private FirebaseFirestore db;
    private String maNV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qlhk);
        recyclerViewSanPham = findViewById(R.id.rcvQLKH);
        BMIAdapter = new QLKHAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerViewSanPham.setLayoutManager(linearLayoutManager);
        recyclerViewSanPham.setAdapter(BMIAdapter);
        Intent intent = getIntent();
        maNV = intent.getStringExtra("maNV");
        BMIAdapter.setOnItemClickListener(new QLKHAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, String name) {
                Intent intent = new Intent(QLKHMain.this, ChiTietKhachHang.class);
                intent.putExtra("Email", name); // Truyền tên của nhân viên được chọn sang màn hình chi tiết
                startActivity(intent);
            }
        });
        // Lấy dữ liệu từ Firestore và cập nhật RecyclerView
        getDataFromFirestore();
    }
    private void getDataFromFirestore() {
        db = FirebaseFirestore.getInstance();
        List<QLKH> productList = new ArrayList<>();
        db.collection("KhachHang")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getString("Email");
                                String imageUrl = document.getString("UrI");
                                productList.add(new QLKH(name,imageUrl));
                                Log.d("KH", "TenKH: " + name);
                            }
                            // Cập nhật RecyclerView sau khi lấy dữ liệu
                            BMIAdapter.setData(productList);
                        } else {
                            Log.e("MainActivity_SanPham", "Lỗi khi lấy dữ liệu: ", task.getException());
                        }
                    }
                });
    }

}
