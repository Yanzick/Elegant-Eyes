package com.example.store;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    private EditText STD;
    private EditText Pass;
    private EditText RePass;
    private EditText Name;
    private EditText Email;
    private Button Regis;
    private FirebaseAuth mAuth;
    private Button Back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        Name = findViewById(R.id.Name);
        STD =  findViewById(R.id.STD);
        Email = findViewById(R.id.Email);
        Pass = findViewById(R.id.RePass);
        RePass = findViewById(R.id.RePass1);
        Regis = findViewById(R.id. Register);
        Back = findViewById(R.id.Back);
        Regis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }
    private void register() {
        String tenKH = Name.getText().toString();
        String sdtKH = STD.getText().toString();
        String emailedit = Email.getText().toString();
        String passedit = Pass.getText().toString();
        String repassedit = RePass.getText().toString();
        if (TextUtils.isEmpty(tenKH) || TextUtils.isEmpty(sdtKH) || TextUtils.isEmpty(emailedit) || TextUtils.isEmpty(passedit) || TextUtils.isEmpty(repassedit)) {
            Toast.makeText(getApplicationContext(), "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Kiểm tra xem mật khẩu nhập lại có trùng khớp với mật khẩu mới hay không
        if (!passedit.equals(repassedit)) {
            Toast.makeText(getApplicationContext(), "Mật khẩu nhập lại không trùng khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tiếp tục với việc tạo tài khoản khi mật khẩu nhập lại trùng khớp
        mAuth.createUserWithEmailAndPassword(emailedit, passedit).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    saveUserDataToFirestore(emailedit);
                    Toast.makeText(getApplicationContext(), "Tạo tài khoản thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Register.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Tạo tài khoản không thành công", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveUserDataToFirestore(String email) {
        // Lấy thông tin từ EditText
        String tenKH = Name.getText().toString();
        String sdtKH = STD.getText().toString();

        // Kiểm tra xem các trường thông tin có đầy đủ không
        if (TextUtils.isEmpty(tenKH) || TextUtils.isEmpty(sdtKH)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo một đối tượng Map để lưu thông tin người dùng
        Map<String, Object> userData = new HashMap<>();
        userData.put("TenKH", tenKH);
        userData.put("SDT", sdtKH);
        userData.put("Email", email);

        // Thêm dữ liệu vào Firestore trong collection "KhachHang"
        FirebaseFirestore.getInstance().collection("KhachHang").document(email)
                .set(userData)
                .addOnSuccessListener(documentReference -> {
                    // Thêm dữ liệu thành công
                    Toast.makeText(getApplicationContext(), "Lưu thông tin người dùng thành công", Toast.LENGTH_SHORT).show();
                    Log.d("Ten Nguoi Dung","Ten: "+tenKH);
                })
                .addOnFailureListener(e -> {
                    // Xử lý khi thêm dữ liệu thất bại
                    Toast.makeText(getApplicationContext(), "Lỗi khi lưu thông tin người dùng", Toast.LENGTH_SHORT).show();
                });
    }
}