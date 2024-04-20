package com.example.store;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Admin extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Ánh xạ các thành phần giao diện từ XML
        emailEditText = findViewById(R.id.Email);
        passwordEditText = findViewById(R.id.Pass);
        Button loginButton = findViewById(R.id.login);

        // Thiết lập sự kiện click cho nút đăng nhập
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy email và mật khẩu từ trường nhập liệu
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    login(email, password);
                } else {
                    // Xử lý trường hợp email hoặc password rỗng hoặc null
                    Toast.makeText(Admin.this, "Tài khoản hoặc mật khẩu không được để trống", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    // Phương thức xác minh thông tin đăng nhập
    private void login(String email, String password) {
        // Sử dụng Firebase Authentication để đăng nhập
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Đăng nhập thành công
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null && user.getEmail().equals("admin@admin.com")) {
                                // Nếu đăng nhập thành công và email là của admin
                                // Chuyển hướng đến trang admin
                                Intent intent = new Intent(Admin.this, AdminActivity.class);
                                startActivity(intent);
                                Toast.makeText(Admin.this, "Đăng nhập thành công với quyền Admin", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(Admin.this, "Tài khoản của bạn không có quyền Admin", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // Thông báo lỗi nếu thông tin đăng nhập không chính xác hoặc có lỗi xảy ra
                            Toast.makeText(Admin.this, "Email hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
