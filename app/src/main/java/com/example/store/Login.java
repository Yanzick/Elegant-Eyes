package com.example.store;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    private EditText Email;
    private EditText pass;
    private Button Login;
    private Button Forgot;
    private FirebaseAuth mAuth;
    private CheckBox rememberCheckBox;
    private SharedPreferences sharedPreferences;
    private Button Back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth= FirebaseAuth.getInstance();
        Email = findViewById(R.id.Email);
        Login = findViewById(R.id.login);
        pass = findViewById(R.id.Pass);
        Forgot = findViewById(R.id.buttonForgot);
        Back = findViewById(R.id.Back);
        rememberCheckBox = findViewById(R.id.checkBox);
        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        Forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ForgotPass();
            }
        });
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });
        boolean isChecked = sharedPreferences.getBoolean("isChecked", false);

        // Kiểm tra nếu checkbox đã được tích và người dùng đã đăng nhập trước đó
        if (isChecked && mAuth.getCurrentUser() != null) {
            // Lấy dữ liệu người dùng từ SharedPreferences
            String userEmail = sharedPreferences.getString("userEmail", "");
            Log.d("Login", "User Email from SharedPreferences: " + userEmail);
            String tenKH = sharedPreferences.getString("TenKH", "");
            String sdtKH = sharedPreferences.getString("SDT", "");
            String uri = sharedPreferences.getString("UrI", "");

            // Chuyển hướng người dùng đến màn hình Home và truyền dữ liệu
            Intent intent = new Intent(Login.this, Home.class);
            intent.putExtra("Email", userEmail);
            intent.putExtra("TenKH", tenKH);
            Log.d("Uri","Uri: "+tenKH);
            intent.putExtra("SDT", sdtKH);
            intent.putExtra("UrI", uri);
            Log.d("Uri","Uri: "+uri);
            startActivity(intent);
            finish();
        }
    }
    private void login() {
        String emailedit, passedit;
        emailedit = Email.getText().toString();
        passedit = pass.getText().toString();

        if(TextUtils.isEmpty(emailedit)){
            Toast.makeText(this, "Vui lòng nhập địa chỉ email!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(passedit)){
            Toast.makeText(this, "Vui lòng nhập mật khẩu!",Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(emailedit,passedit).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if (rememberCheckBox.isChecked()) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isChecked", true);
                        editor.putString("userEmail", emailedit); // Lưu userEmail vào SharedPreferences
                        editor.apply();
                    }
                    getUserDataFromFirestore(emailedit);
                    Toast.makeText(getApplicationContext(), "Đăng nhập thành công",Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(getApplicationContext(), "Tên đăng nhập hoặc mật khẩu không hợp lệ",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void getUserDataFromFirestore(String userEmail) {
        FirebaseFirestore.getInstance().collection("KhachHang").document(userEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Document tồn tại, lấy dữ liệu và chuyển sang Home
                                String tenKH = document.getString("TenKH");
                                String sdtKH = document.getString("SDT");
                                Log.d("Email", "Email: " + sdtKH);
                                String uri = document.getString("UrI");
                                // Chuyển sang activity Home và truyền dữ liệu
                                Intent intent = new Intent(Login.this, Home.class);
                                intent.putExtra("Email", userEmail);
                                intent.putExtra("TenKH", tenKH);
                                intent.putExtra("SDT", sdtKH);
                                intent.putExtra("UrI", uri);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "Không có thông tin người dùng", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Lỗi khi truy vấn Firestore: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void ForgotPass() {
        String userEmail = Email.getText().toString().trim();

        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ email của bạn để khôi phục mật khẩu.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gửi email khôi phục mật khẩu
        mAuth.sendPasswordResetEmail(userEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Một email khôi phục mật khẩu đã được gửi đến địa chỉ email của bạn. Vui lòng kiểm tra hòm thư đến của bạn.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Login.this, "Gửi email khôi phục mật khẩu thất bại. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void moveToHomeScreen() {
        // TODO: Chuyển đến màn hình home
        Intent intent = new Intent(Login.this, Home.class);
        startActivity(intent);
    }
}