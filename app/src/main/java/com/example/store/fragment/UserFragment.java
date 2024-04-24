package com.example.store.fragment;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.store.Login;
import com.example.store.Pass;
import com.example.store.Profile;
import com.example.store.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private Uri imageUri;
    private StorageReference storageReference;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userEmail;
    private Button Edit, Repass;
    private SharedPreferences sharedPreferences;

    private ImageButton Logout;
    private TextView nameTextView, Name, STD, address, Pass, Email;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user, container, false);
        Edit = rootView.findViewById(R.id.profile);
        Repass = rootView.findViewById(R.id.repass);
        Logout = rootView.findViewById(R.id.logout);
        sharedPreferences = getActivity().getSharedPreferences("loginPrefs", MODE_PRIVATE);

        Bundle args = getArguments();
        if (args != null) {
            userEmail = args.getString("Email");

            Log.d("UrI","UrI: "+ userEmail);
            /*String tenKH = args.getString("TenKH");
            if (tenKH != null) {
                TextView nameTextView = rootView.findViewById(R.id.nameuser);
                nameTextView.setText(tenKH);
            }*/
            nameTextView = rootView.findViewById(R.id.nameuser);
            Name = rootView.findViewById(R.id.Name);
            Email = rootView.findViewById(R.id.Email);
            STD = rootView.findViewById(R.id.STD);
            address = rootView.findViewById(R.id.Address);

            String sdtKH = args.getString("SDT");
            String UrI = args.getString("UrI");
            Log.d("UrI","UrI: "+ UrI);
            imageView = rootView.findViewById(R.id.imageView);
            /*if (UrI != null && !UrI.isEmpty()) {
                Picasso.get().load(UrI).into(imageView);
            }else{
                imageView.setImageResource(R.drawable.baseline_account_circle_25);
            }*/
            fetchUserProfileImage(userEmail);

            mAuth = FirebaseAuth.getInstance();
            storageReference = FirebaseStorage.getInstance().getReference("uploads");
            db = FirebaseFirestore.getInstance();

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFileChooser();
                }
            });
        }
        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Profile.class);
                intent.putExtra("Email", userEmail);
                startActivity(intent);
            }
        });
        Repass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Pass.class);
                intent.putExtra("Email", userEmail);
                startActivity(intent);
            }
        });
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();

                // Xóa dữ liệu đăng nhập từ SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("isChecked");
                editor.remove("userEmail");
                editor.remove("TenKH");
                editor.remove("SDT");
                editor.remove("UrI");
                editor.apply();

                // Chuyển hướng đến màn hình đăng nhập
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return rootView;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn hình ảnh"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
                uploadImageToFirebase();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Lỗi khi chọn hình ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImageToFirebase() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            final String userID = user.getUid();
            if (imageUri != null) { // Kiểm tra xem imageUri có giá trị không
                final StorageReference fileReference = storageReference.child(userEmail + "/profile.jpg");

                // Tải hình ảnh lên Firebase Storage
                fileReference.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Lấy URL của hình ảnh sau khi tải lên thành công
                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageURL = uri.toString();
                                        // Cập nhật URL vào Firestore
                                        DocumentReference userRef = db.collection("KhachHang").document(userEmail);
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("UrI", imageURL);
                                        userRef.update(data)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(getContext(), "Cập nhật hình ảnh thành công", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getContext(), "Lỗi khi cập nhật hình ảnh", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Lỗi khi tải hình ảnh lên Firebase Storage", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(getContext(), "Vui lòng chọn một hình ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void fetchUserProfileImage(String userEmail) {
        FirebaseFirestore.getInstance().collection("KhachHang").document(userEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String tenKH = document.getString("TenKH");
                                if (tenKH != null) {

                                    nameTextView.setText(tenKH);
                                    Name.setText(tenKH);
                                }
                                String std = document.getString("SDT");
                                if (STD != null) {

                                    STD.setText(std);

                                }
                                String email = document.getString("Email");
                                if (Email != null) {

                                    Email.setText(email);
                                }
                                String dc = document.getString("Address");
                                if (address != null) {
                                    address.setText(dc);
                                }
                                String UrI = document.getString("UrI");
                                if (UrI != null && !UrI.isEmpty()) {
                                    // Nếu UrI không rỗng, cập nhật ImageView với UrI
                                    Picasso.get().load(UrI).into(imageView);
                                } else {
                                    // Nếu UrI rỗng, hiển thị mặc định
                                    imageView.setImageResource(R.drawable.baseline_account_circle_25);
                                }
                            } else {
                                // Nếu không có document tồn tại, thông báo lỗi
                                Log.d("UserFragment", "Không có thông tin người dùng");
                            }
                        } else {
                            // Nếu truy vấn thất bại, thông báo lỗi
                            Log.d("UserFragment", "Lỗi khi truy vấn Firestore: " + task.getException().getMessage());
                        }
                    }
                });

    }


    public static UserFragment newInstance(String userEmail, String tenKH, String sdtKH, String UrI) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString("Email", userEmail);
        args.putString("TenKH", tenKH);
        args.putString("STD", sdtKH);
        args.putString("UrI", UrI);
        fragment.setArguments(args);
        return fragment;
    }
}

