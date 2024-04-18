package com.example.store.fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.store.ItemSpacingDecoration;
import com.example.store.Photo;
import com.example.store.PhotoAdapter;
import com.example.store.R;
import com.example.store.Shopping;
import com.example.store.ShoppingAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator3;

public class HomeFragment extends Fragment {

    private ViewPager2 mViewPager2;
    private CircleIndicator3 mCircleIndicator3;
    private RecyclerView recyclerView;
    private ShoppingAdapter productAdapter;
    private SearchView searchView;
    private List<Shopping> originalProductList; // Danh sách sản phẩm gốc
    private List<Shopping> filteredProductList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Khởi tạo ViewPager2 và CircleIndicator3 từ rootView
        mViewPager2 = rootView.findViewById(R.id.viewpage2);
        mCircleIndicator3 = rootView.findViewById(R.id.Anh);

        // Tạo danh sách sản phẩm trống ban đầu
        List<Shopping> productList = new ArrayList<>();

        // Tạo adapter cho ViewPager2 và RecyclerView
        PhotoAdapter photoAdapter = new PhotoAdapter(getList());
        productAdapter = new ShoppingAdapter(getContext(), productList);

        // Gắn adapter vào ViewPager2 và RecyclerView
        mViewPager2.setAdapter(photoAdapter);
        recyclerView = rootView.findViewById(R.id.rcvMuaSam);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(productAdapter);
        recyclerView.addItemDecoration(new ItemSpacingDecoration(requireContext(), R.dimen.item_spacing));
        // Gắn ViewPager2 vào CircleIndicator3
        mCircleIndicator3.setViewPager(mViewPager2);
        searchView = rootView.findViewById(R.id.searchView);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi thanh tìm kiếm được nhấp vào
                searchView.setIconified(false); // Mở rộng thanh tìm kiếm
            }
        });

        // Load dữ liệu sản phẩm từ Firestore
        initViews(rootView);
        loadProducts();
        startAutoScroll();
        Bundle args = getArguments();
        if (args != null) {
            String userEmail = args.getString("Email");
            Log.d("Email", "Email: " + userEmail);
            String tenKH = args.getString("TenKH");
            String sdtKH = args.getString("SDT");
            String UrI = args.getString("UrI");
            // Sử dụng dữ liệu ở đây
        }
        return rootView;
    }
    private void initViews(View rootView) {
        // Tìm kiếm SearchView trong layout
        searchView = rootView.findViewById(R.id.searchView);

        // Khởi tạo danh sách sản phẩm trống
        originalProductList = new ArrayList<>();
        filteredProductList = new ArrayList<>();

        // Khởi tạo adapter cho RecyclerView và gán adapter cho RecyclerView
        productAdapter = new ShoppingAdapter(getContext(), filteredProductList);
        recyclerView = rootView.findViewById(R.id.rcvMuaSam);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(productAdapter);
        recyclerView.addItemDecoration(new ItemSpacingDecoration(requireContext(), R.dimen.item_spacing));

        // Xử lý sự kiện tìm kiếm khi người dùng nhập vào thanh tìm kiếm
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Khi có sự thay đổi trong thanh tìm kiếm, lọc dữ liệu và cập nhật RecyclerView
                filterProducts(newText);
                return true;
            }
        });
    }
    private void filterProducts(String query) {
        // Xóa dữ liệu trong danh sách sản phẩm đã lọc
        filteredProductList.clear();

        // Nếu không có từ khóa tìm kiếm, hiển thị tất cả sản phẩm
        if (query.isEmpty()) {
            filteredProductList.addAll(originalProductList);
        } else {
            // Nếu có từ khóa tìm kiếm, lọc sản phẩm theo từ khóa
            for (Shopping product : originalProductList) {
                if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredProductList.add(product);
                }
            }
        }

        // Cập nhật RecyclerView với danh sách sản phẩm đã lọc
        productAdapter.notifyDataSetChanged();
    }
    private List<Photo> getList() {
        List<Photo> list = new ArrayList<>();
        list.add(new Photo(R.drawable.i1));
        list.add(new Photo(R.drawable.i2));
        list.add(new Photo(R.drawable.i3));
        list.add(new Photo(R.drawable.i4));
        return list;
    }

    private void loadProducts() {
        // Truy vấn dữ liệu từ Firestore
        List<Shopping> productList = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("SanPham")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Lấy thông tin của sản phẩm từ document
                                String name = document.getString("TenSP");
                                String price = document.getString("Price");
                                String Url = document.getString("UrI");
                                String Rate = document.getString("Rate");
                                String ID = document.getString("MaSP");
                                String MT = document.getString("MT");

                                // Tạo đối tượng Shopping và thêm vào danh sách sản phẩm
                                Shopping product = new Shopping(name,Url,price,Rate,ID, MT);
                                productList.add(product);
                            }
                            originalProductList.clear(); // Xóa dữ liệu cũ
                            originalProductList.addAll(productList); // Cập nhật dữ liệu mới từ Firestore
                            filterProducts(""); // Lọc dữ liệu với chuỗi tìm kiếm trống ban đầu
                        } else {
                            // Xử lý khi không thể truy vấn dữ liệu
                        }
                    }
                });
        productAdapter.setData(filteredProductList);
    }
    private Timer timer;
    private final long DELAY_MS = 500; // Độ trễ trước khi bắt đầu chuyển trang
    private final long PERIOD_MS = 3000; // Thời gian chuyển trang giữa các trang

    private void startAutoScroll() {
        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            public void run() {
                int currentItem = mViewPager2.getCurrentItem();
                int numItems = mViewPager2.getAdapter().getItemCount();
                if (currentItem == numItems - 1) {
                    mViewPager2.setCurrentItem(0);
                } else {
                    mViewPager2.setCurrentItem(currentItem + 1);
                }
            }
        };

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, DELAY_MS, PERIOD_MS);
    }
    public static HomeFragment newInstance(String userEmail, String tenKH, String sdtKH, String UrI) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("Email", userEmail);
        args.putString("TenKH", tenKH);
        args.putString("STD", sdtKH);
        args.putString("UrI", UrI);
        fragment.setArguments(args);
        return fragment;
    }


}

