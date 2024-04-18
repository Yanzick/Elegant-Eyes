package com.example.store.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.store.DatabaseHelper;
import com.example.store.R;
import com.example.store.Shopping1;
import com.example.store.Shopping1Adapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ShoppingFragment extends Fragment {
    private RecyclerView recyclerView;
    private Shopping1Adapter productAdapter;
    private DatabaseHelper databaseHelper;
    private Button Buy;
    private ImageButton  Delete;
    private String userEmail;
    private TextView totalPriceTextView;
    private TextView address;
    // Biến để lưu trữ tổng giá trị
    private int totalSelectedPrice = 0;
    private List<Shopping1> productList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping, container, false);
        Bundle args = getArguments();
        if (args != null) {
            userEmail = args.getString("Email");
            address = view.findViewById(R.id.Address);
            Log.d("UrI", "UrI: " + userEmail);
            fetchUserProfileImage(userEmail);
        }
        recyclerView = view.findViewById(R.id.rcvMuaSam);
        Buy = view.findViewById(R.id.Buy);
        Delete =view.findViewById(R.id.delete);
        totalPriceTextView = view.findViewById(R.id.Mua);
        List<Shopping1> productList = new ArrayList<>();
        productAdapter = new Shopping1Adapter(getActivity(), productList);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayout);
        recyclerView.setAdapter(productAdapter);

        databaseHelper = new DatabaseHelper(getActivity());
        List<String> idList = databaseHelper.getAllIDs();
        loadProducts(idList);

        Buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> checkedIDs = productAdapter.getCheckedItemIDs();
                for (String id : checkedIDs) {
                    databaseHelper.deleteProduct(id);
                }

                // Cập nhật lại RecyclerView
                loadProducts(databaseHelper.getAllIDs());
            }
        });

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bắt sự kiện thay đổi của checkbox trong adapter
        productAdapter.setOnCheckboxChangedListener(new Shopping1Adapter.OnCheckboxChangedListener() {
            @Override
            public void onCheckboxChanged(String productId, boolean isChecked) {
                // Lấy giá của sản phẩm được chọn
                int price = getProductPrice(productId);
                // Cập nhật tổng giá trị dựa trên trạng thái của checkbox
                if (isChecked) {
                    totalSelectedPrice += price;
                } else {
                    totalSelectedPrice -= price;
                }
                // Cập nhật TextView để hiển thị tổng giá trị mới
                totalPriceTextView.setText(getString(R.string.total_price_format, totalSelectedPrice));
            }
        });

        // Các phần còn lại của onViewCreated() ...
    }
    private void loadProducts(List<String> idList) {
        productList.clear(); // Xóa danh sách sản phẩm hiện tại trước khi tải lại dữ liệu mới

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (String id : idList) {
            db.collection("SanPham").document(id)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("TenSP");
                            int price = Integer.parseInt(documentSnapshot.getString("Price"));
                            String imageUrl = documentSnapshot.getString("UrI");
                            String rating = documentSnapshot.getString("Rate");
                            String mt = documentSnapshot.getString("MT");

                            // Tạo đối tượng Shopping1 và thêm vào danh sách sản phẩm
                            Shopping1 product = new Shopping1(name, imageUrl, price, rating, id, mt);
                            productList.add(product);

                            // Nếu danh sách sản phẩm đã được xây dựng hoàn chỉnh, cập nhật RecyclerView
                            if (productList.size() == idList.size()) {
                                if (productList.isEmpty()) {
                                    recyclerView.setVisibility(View.GONE);
                                } else {
                                    recyclerView.setVisibility(View.VISIBLE);
                                    productAdapter.setData(productList);
                                    productAdapter.notifyDataSetChanged(); // Thông báo cho adapter cập nhật dữ liệu mới
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Xử lý khi truy vấn dữ liệu thất bại
                    });
        }
    }

    public static ShoppingFragment newInstance(String userEmail, String tenKH, String sdtKH, String UrI) {
        ShoppingFragment fragment = new ShoppingFragment();
        Bundle args = new Bundle();
        args.putString("Email", userEmail);
        args.putString("TenKH", tenKH);
        args.putString("STD", sdtKH);
        args.putString("UrI", UrI);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onResume() {
        super.onResume();
        // Tải lại dữ liệu khi Fragment được hiển thị lại
        loadProducts(databaseHelper.getAllIDs());
    }
    private int getProductPrice(String productId) {
        // Lặp qua danh sách sản phẩm và tìm sản phẩm có ID tương ứng
        for (Shopping1 product : productList) {
            if (product.getID().equals(productId)) {
                return product.getPrice();
            }
        }
        return 0; // Trả về 0 nếu không tìm thấy sản phẩm
    }

    private void fetchUserProfileImage(String userEmail) {
        FirebaseFirestore.getInstance().collection("KhachHang").document(userEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String dc = document.getString("Address");
                                if (address != null) {
                                    address.setText(dc);
                                }

                            } else {
                                // Nếu không có document tồn tại, thông báo lỗi
                                Log.d("UserFragment", "Không có thông tin người dùng");
                            }
                        } else {
                            // Nếu truy vấn thất bại, thông báo lỗi
                            Log.d("UserFragment", "Lỗi khi truy vấn Firestore: " + task.getException().getMessage());
                        }
                    }
                });
    }

}
