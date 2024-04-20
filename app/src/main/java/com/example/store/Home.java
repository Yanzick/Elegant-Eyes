package com.example.store;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator3;

public class Home extends AppCompatActivity {
    private TabLayout mTab;
    private ViewPager mV;
    private MyViewPagerAdapter myViewPagerAdapter;
    private String userEmail;
    private String tenKH;
    private String sdtKH;
    private String UrI;
    private ImageView Facebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Facebook = findViewById(R.id.facebook);
        Facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở trang Facebook
                openFacebookPage();
            }
        });
        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        userEmail = intent.getStringExtra("Email");
        tenKH = intent.getStringExtra("TenKH");
        sdtKH = intent.getStringExtra("SDT");
        UrI = intent.getStringExtra("UrI");

        mTab = findViewById(R.id.tab_layout);
        mV = findViewById(R.id.viewpage);
        myViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, this, userEmail, tenKH, sdtKH,UrI);
        mV.setAdapter(myViewPagerAdapter);
        mTab.setupWithViewPager(mV);
    }
    private void openFacebookPage() {
        // Đường dẫn đến trang Facebook của bạn
        String facebookPageUrl = "https://www.facebook.com/profile.php?id=100014106780695&locale=vi_VN";

        // Tạo Intent để mở trình duyệt web với đường dẫn đến trang Facebook
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookPageUrl));

        // Khởi chạy Intent
        startActivity(intent);
    }
}
