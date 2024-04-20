package com.example.store;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {
    private Button SP, Don, KH;
    private ImageButton Logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin2);
        SP = findViewById(R.id.SP);
        Don = findViewById(R.id.Don);
        KH = findViewById(R.id.KH);
        //Logout.findViewById(R.id.logout);

        /*Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(AdminActivity.this, Admin.class);
                startActivity(intent);
                finish();
            }
        });*/
        SP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AdminActivity.this, QLSP.class);
                startActivity(intent);

            }
        });
        KH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AdminActivity.this, QLKHMain.class);
                startActivity(intent);

            }
        });
        Don.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AdminActivity.this, QLDHMain.class);
                startActivity(intent);

            }
        });
    }
}