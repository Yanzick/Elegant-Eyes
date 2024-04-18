package com.example.store.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
                List<String> checkedProductIDs = productAdapter.getCheckedItemIDs();

                // Gọi phương thức để thêm đơn hàng vào Firestore
                addOrderToFirestore(userEmail, checkedProductIDs);

                loadProducts(databaseHelper.getAllIDs());

                // Hiển thị thông báo cho người dùng
                Custom();
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
    private void Custom() {
        // Inflate custom toast layout
        View layout = getLayoutInflater().inflate(R.layout.toast,
                (ViewGroup) requireActivity().findViewById(R.id.toast));

        // Creating the Toast
        Toast toast = new Toast(requireActivity().getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(layout);

        // Create a grey background view
        View greyBackground = new View(requireActivity().getApplicationContext());
        greyBackground.setBackgroundColor(Color.parseColor("#80000000")); // Màu xám với độ trong suốt
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        greyBackground.setLayoutParams(params);

        // Add the grey background view behind the toast
        ViewGroup decorView = (ViewGroup) requireActivity().getWindow().getDecorView();
        decorView.addView(greyBackground);

        // Show the toast
        toast.show();

        // Remove the grey background view after the toast is hidden
        toast.getView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {}

            @Override
            public void onViewDetachedFromWindow(View v) {
                decorView.removeView(greyBackground);
            }
        });
    }
    private void addOrderToFirestore(String userEmail, List<String> productIDs) {
        // Tạo một collection mới trong Firestore để lưu trữ các đơn hàng
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String orderId = generateRandomOrderId(); // Tạo mã đơn hàng ngẫu nhiên
        DocumentReference orderRef = db.collection("Product").document(orderId); // Tạo document mới cho đơn hàng

        // Tạo một object Order chứa các thông tin cần thiết
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("Email", userEmail); // Thêm thông tin về người mua
        orderData.put("ProductIDs", productIDs); // Thêm danh sách ID sản phẩm

        // Thêm đơn hàng vào Firestore
        orderRef.set(orderData)
                .addOnSuccessListener(aVoid -> {
                    // Xử lý khi thêm đơn hàng thành công
                    showToast("Đặt hàng thành công");
                })
                .addOnFailureListener(e -> {
                    // Xử lý khi thêm đơn hàng thất bại
                    showToast("Đặt hàng thất bại: " + e.getMessage());
                });
    }
    private String generateRandomOrderId() {
        // Tạo một chuỗi ngẫu nhiên gồm 4 ký tự số
        Random random = new Random();
        StringBuilder orderIdBuilder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            orderIdBuilder.append(random.nextInt(10)); // Thêm một ký tự số ngẫu nhiên vào chuỗi
        }
        return orderIdBuilder.toString();
    }
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }



}
