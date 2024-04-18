package com.example.store;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator3;

public class SanPham1 extends AppCompatActivity {
    private ViewPager2 mViewPager2;
    private CircleIndicator3 mCircleIndicator3;
    private List<Photo1> photo;
    private Button add;
    private DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_san_pham);
        add = findViewById(R.id.add);
        databaseHelper = new DatabaseHelper(this);
        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            return;
        }

        Shopping1 item = (Shopping1) bundle.get("Name");
        TextView name = findViewById(R.id.name);
        name.setText(item.getName());
        TextView price = findViewById(R.id.Price);
        price.setText(String.valueOf(item.getPrice()));
        TextView MT = findViewById(R.id.MT);
        MT.setText(item.getMT());
        String ID = item.getID();
        Log.d("ID","id:" + ID);
        mViewPager2 = findViewById(R.id.viewpage2);
        mCircleIndicator3 = findViewById(R.id.Anh);

        getListFromFirebase(ID);
        Photo1Adapter adapter = new Photo1Adapter(photo);
        mViewPager2.setAdapter(adapter);
        mCircleIndicator3.setViewPager(mViewPager2); // Gắn ViewPager2 vào CircleIndicator3 sau khi đã gán adapter cho ViewPager2
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart(ID);
            }
        });

    }
    private void addToCart(String productID) {
        boolean success = databaseHelper.addData(productID);
        if (success) {
            // Xử lý khi thêm thành công
            Toast.makeText(this, "Thêm vào giỏ hàng thành công", Toast.LENGTH_SHORT);
            Log.d("TAG", "Product added to cart successfully " + productID);
        } else {
            // Xử lý khi thêm thất bại
            Log.e("TAG", "Error adding product to cart");
        }
    }
    private void getListFromFirebase(String productID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("SanPham").document(productID).collection("URI")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Photo1> photoList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String imageUrl = document.getString("Url");
                            photoList.add(new Photo1(imageUrl));
                        }
                        // Sau khi lấy được danh sách hình ảnh, bạn có thể gán cho adapter của ViewPager2
                        Photo1Adapter adapter = new Photo1Adapter(photoList);
                        mViewPager2.setAdapter(adapter);
                    } else {
                        // Xử lý khi không thể truy vấn dữ liệu
                    }
                });
    }

}