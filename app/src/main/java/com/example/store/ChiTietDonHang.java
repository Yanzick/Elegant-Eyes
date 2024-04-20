package com.example.store;



import static com.google.common.io.Files.getFileExtension;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChiTietDonHang extends AppCompatActivity {

    private TextView DH, HoVaten, EmalNV, STD, MaNV, MatKhau;
    private Button DangKiNV;
    private FirebaseFirestore firestore;
    private Button chonAnhButton;
    private ImageView anhNhanVien;
    private String maNV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_don_hang);

        HoVaten = findViewById(R.id.ID);
        EmalNV = findViewById(R.id.Email);
        STD = findViewById(R.id.Price);
        DH = findViewById(R.id.PrID);
        firestore = FirebaseFirestore.getInstance();


        // Lấy mã nhân viên từ Intent
        Intent intent = getIntent();
        maNV = intent.getStringExtra("Email");
        Log.d("InfoActivity", "hoVaten: " + maNV);

        // Kiểm tra xem maNV có khác null trước khi thực hiện truy vấn Firestore
        if (maNV != null) {
            Log.e("InfoActivity", "maNV: " + maNV);
            // Truy vấn Firestore để lấy thông tin nhân viên
            firestore.collection("Product")
                    .whereEqualTo("ID", maNV)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("InfoActivity", "hoVaten: " + document.getString("ID"));
                                    Log.d("InfoActivity", "hoVaten: " + document.getString("Email"));

                                    HoVaten.setText(document.getString("ID"));
                                    EmalNV.setText(document.getString("Email"));
                                    int totalPrice = document.getLong("TotalPrice").intValue();
                                    // Hiển thị giá trị trong TextView
                                    STD.setText(String.valueOf(totalPrice));
                                    ArrayList<String> productIdList = (ArrayList<String>) document.get("ProductIDs");
                                    if (productIdList != null && !productIdList.isEmpty()) {
                                        // Sử dụng StringBuilder để tạo chuỗi hiển thị
                                        StringBuilder productIdString = new StringBuilder();
                                        for (String productId : productIdList) {
                                            // Thêm mỗi productId vào chuỗi với dấu phẩy phân cách
                                            productIdString.append(productId).append(", ");
                                        }
                                        // Xóa dấu phẩy cuối cùng
                                        if (productIdString.length() > 0) {
                                            productIdString.deleteCharAt(productIdString.length() - 2);
                                        }
                                        // Hiển thị chuỗi kết quả trong TextView
                                        DH.setText(productIdString.toString());
                                    }

                                }
                                Log.e("Success","Success");
                            } else {
                                Log.e("InfoActivity", "Error getting documents: ", task.getException());
                                Toast.makeText(ChiTietDonHang.this, "Lỗi khi truy vấn dữ liệu", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        } else {
            Toast.makeText(ChiTietDonHang.this, "Mã nhân viên không hợp lệ", Toast.LENGTH_SHORT).show();
        }

    }
}
