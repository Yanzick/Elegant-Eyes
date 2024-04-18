package com.example.store;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
}
