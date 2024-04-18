package com.example.store;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.store.fragment.HomeFragment;
import com.example.store.fragment.SettingFragment;
import com.example.store.fragment.ShoppingFragment;
import com.example.store.fragment.UserFragment;

public class MyViewPagerAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private String userEmail;
    private String tenKH;
    private String sdtKH;
    private String UrI;

    public MyViewPagerAdapter(FragmentManager fm, int behavior, Context context, String userEmail, String tenKH, String sdtKH, String UrI) {
        super(fm, behavior);
        this.context = context;
        this.userEmail = userEmail;
        this.tenKH = tenKH;
        this.sdtKH = sdtKH;
        this.UrI = UrI;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return HomeFragment.newInstance(userEmail, tenKH, sdtKH, UrI);
            case 1:
                return ShoppingFragment.newInstance(userEmail, tenKH, sdtKH, UrI);
            case 2:
                return UserFragment.newInstance(userEmail, tenKH, sdtKH, UrI);

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        Drawable icon = null;
        switch (position){
            case 0:
                icon = ContextCompat.getDrawable(context, R.drawable.rounded_family_home_24);
                break;
            case 2:
                icon = ContextCompat.getDrawable(context, R.drawable.baseline_account_circle_25);
                break;
            case 1:
                icon = ContextCompat.getDrawable(context, R.drawable.baseline_add_shopping_cart_24);
                break;
        }
        if (icon != null) {
            // Tạo một SpannableString để chứa hình ảnh
            SpannableString spannableString = new SpannableString(" ");
            // Thiết lập hình ảnh trong một ImageSpan
            icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
            ImageSpan imageSpan = new ImageSpan(icon);
            // Chèn hình ảnh vào SpannableString
            spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        } else {
            return null;
        }
    }

}
