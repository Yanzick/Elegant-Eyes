package com.example.store.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.store.R;

public class SettingFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        Bundle args = getArguments();
        if (args != null) {
            String userEmail = args.getString("Email");
            String tenKH = args.getString("TenKH");
            String sdtKH = args.getString("SDT");
            String UrI = args.getString("UrI");
            // Sử dụng dữ liệu ở đây
        }
        return rootView;
    }
    public static SettingFragment newInstance(String userEmail, String tenKH, String sdtKH, String UrI) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString("Email", userEmail);
        args.putString("TenKH", tenKH);
        args.putString("STD", sdtKH);
        args.putString("UrI", UrI);
        fragment.setArguments(args);
        return fragment;
    }
}
