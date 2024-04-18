package com.example.store;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {
    private EditText emailEditText, sdtEditText, addressEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Ánh xạ các thành phần trong layout
        emailEditText = findViewById(R.id.Email);
        sdtEditText = findViewById(R.id.SDT);
        addressEditText = findViewById(R.id.address);
        saveButton = findViewById(R.id.profile);

        // Nhận dữ liệu từ Intent
        String email = getIntent().getStringExtra("Email");
        if (email != null) {
            emailEditText.setText(email);
        }

        // Thiết lập onClickListener cho Button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDataToFirestore();
            }
        });
        displayDataIfExists(email);
    }

    private void addDataToFirestore() {
        // Lấy dữ liệu từ các EditText
        String email = emailEditText.getText().toString();
        String sdt = sdtEditText.getText().toString();
        String address = addressEditText.getText().toString();

        // Thêm dữ liệu vào Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("Email", email);
        data.put("SDT", sdt);
        data.put("Address", address);

        db.collection("KhachHang").document(email)
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Thêm hoặc cập nhật dữ liệu thành công
                        Toast.makeText(Profile.this, "Data added or updated in Firestore", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xử lý khi thêm hoặc cập nhật dữ liệu thất bại
                        Toast.makeText(Profile.this, "Failed to add or update data in Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void displayDataIfExists(String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("KhachHang").document(email);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Dữ liệu tồn tại, hiển thị lên các EditText
                    String em = documentSnapshot.getString("Email");
                    String sdt = documentSnapshot.getString("SDT");
                    String address = documentSnapshot.getString("Address");

                    // Hiển thị dữ liệu lên các EditText
                    emailEditText.setText(em);
                    sdtEditText.setText(sdt);
                    addressEditText.setText(address);
                } else {
                    // Dữ liệu không tồn tại, bạn có thể thực hiện các xử lý phù hợp ở đây
                    Toast.makeText(Profile.this, "No data exists for this email", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Xử lý khi truy vấn dữ liệu thất bại
                Toast.makeText(Profile.this, "Failed to fetch data from Firestore", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
