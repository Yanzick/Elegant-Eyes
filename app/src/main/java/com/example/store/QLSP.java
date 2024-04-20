package com.example.store;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.store.ItemSpacingDecoration;
import com.example.store.Photo;
import com.example.store.PhotoAdapter;
import com.example.store.R;
import com.example.store.Shopping;
import com.example.store.ShoppingAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator3;

public class QLSP extends AppCompatActivity {

    private Button ADD;
    private RecyclerView recyclerView;
    private ShoppingAdapter productAdapter;
    private SearchView searchView;
    private List<Shopping> originalProductList; // Danh sách sản phẩm gốc
    private List<Shopping> filteredProductList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qlsp);

        // Khởi tạo ViewPager2 và CircleIndicator3 từ layout

        ADD =findViewById(R.id.ADD);
        // Tạo danh sách sản phẩm trống ban đầu
        List<Shopping> productList = new ArrayList<>();

        // Tạo adapter cho ViewPager2 và RecyclerView

        productAdapter = new ShoppingAdapter(this, productList);

        // Gắn adapter vào ViewPager2 và RecyclerView

        recyclerView = findViewById(R.id.rcvMuaSam);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(productAdapter);
        recyclerView.addItemDecoration(new ItemSpacingDecoration(this, R.dimen.item_spacing));

        // Gắn ViewPager2 vào CircleIndicator3
        searchView = findViewById(R.id.searchView);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi thanh tìm kiếm được nhấp vào
                searchView.setIconified(false); // Mở rộng thanh tìm kiếm
            }
        });
        ADD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QLSP.this, ADDSP.class);
                startActivity(intent);
            }
        });
        // Load dữ liệu sản phẩm từ Firestore
        initViews();
        loadProducts();

    }

    private void initViews() {
        // Tìm kiếm SearchView trong layout
        searchView = findViewById(R.id.searchView);

        // Khởi tạo danh sách sản phẩm trống
        originalProductList = new ArrayList<>();
        filteredProductList = new ArrayList<>();

        // Khởi tạo adapter cho RecyclerView và gán adapter cho RecyclerView
        productAdapter = new ShoppingAdapter(this, filteredProductList);
        recyclerView = findViewById(R.id.rcvMuaSam);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(productAdapter);
        recyclerView.addItemDecoration(new ItemSpacingDecoration(this, R.dimen.item_spacing));

        // Xử lý sự kiện tìm kiếm khi người dùng nhập vào thanh tìm kiếm
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Khi có sự thay đổi trong thanh tìm kiếm, lọc dữ liệu và cập nhật RecyclerView
                filterProducts(newText);
                return true;
            }
        });
    }

    private void filterProducts(String query) {
        // Xóa dữ liệu trong danh sách sản phẩm đã lọc
        filteredProductList.clear();

        // Nếu không có từ khóa tìm kiếm, hiển thị tất cả sản phẩm
        if (query.isEmpty()) {
            filteredProductList.addAll(originalProductList);
        } else {
            // Nếu có từ khóa tìm kiếm, lọc sản phẩm theo từ khóa
            for (Shopping product : originalProductList) {
                if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredProductList.add(product);
                }
            }
        }

        // Cập nhật RecyclerView với danh sách sản phẩm đã lọc
        productAdapter.notifyDataSetChanged();
    }



    private void loadProducts() {
        // Truy vấn dữ liệu từ Firestore
        List<Shopping> productList = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("SanPham")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Lấy thông tin của sản phẩm từ document
                                String name = document.getString("TenSP");
                                String price = document.getString("Price");
                                String Url = document.getString("UrI");
                                String Rate = document.getString("Rate");
                                String ID = document.getString("MaSP");
                                String MT = document.getString("MT");

                                // Tạo đối tượng Shopping và thêm vào danh sách sản phẩm
                                Shopping product = new Shopping(name,Url,price,Rate,ID, MT);
                                productList.add(product);
                            }
                            originalProductList.clear(); // Xóa dữ liệu cũ
                            originalProductList.addAll(productList); // Cập nhật dữ liệu mới từ Firestore
                            filterProducts(""); // Lọc dữ liệu với chuỗi tìm kiếm trống ban đầu
                        } else {
                            // Xử lý khi không thể truy vấn dữ liệu
                        }
                    }
                });
        productAdapter.setData(filteredProductList);
    }


}
