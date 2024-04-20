package com.example.store;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class ADDSP extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    private Uri[] imageUris = new Uri[6];
    private EditText TenSp, GiaSP, SL, MoTa, ID;
    private Button Add;
    private int documentIndex = 1;
    private FirebaseFirestore firestore;
    private ImageView[] imageViews = new ImageView[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addsp);
        firestore = FirebaseFirestore.getInstance();
        TenSp = findViewById(R.id.TenSP);
        GiaSP = findViewById(R.id.GiaSP);
        SL = findViewById(R.id.SL);
        MoTa = findViewById(R.id.MT);
        Add = findViewById(R.id.DKSP);
        ID = findViewById(R.id.ID);
        imageViews[0] = findViewById(R.id.imageView);
        imageViews[1] = findViewById(R.id.imageView2);
        imageViews[2] = findViewById(R.id.imageView3);
        imageViews[3] = findViewById(R.id.imageView4);
        imageViews[4] = findViewById(R.id.imageView5);
        imageViews[5] = findViewById(R.id.imageView6);

        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToFirestore();
            }
        });

        for (int i = 0; i < imageViews.length; i++) {
            final int index = i;
            imageViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openImageChooser(index);
                }
            });
        }
    }

    private void openImageChooser(int index) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, index);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode < 0 || requestCode >= imageUris.length) {
            return;
        }

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imageUris[requestCode] = selectedImageUri;
            imageViews[requestCode].setImageURI(selectedImageUri);
        }
    }

    private void addToFirestore() {
        String tenSanPham = TenSp.getText().toString();
        String giaSanPham = GiaSP.getText().toString();
        String soLuong = SL.getText().toString();
        String moTa = MoTa.getText().toString();
        String id = ID.getText().toString();

        Map<String, Object> orderData = new HashMap<>();
        orderData.put("TenSP", tenSanPham);
        orderData.put("Price", giaSanPham);
        orderData.put("Rate", soLuong);
        orderData.put("MT", moTa);
        orderData.put("MaSP", id);

        for (int i = 0; i < imageUris.length; i++) {
            if (imageUris[i] != null) {
                if (i == 0) {
                    // Put the first image directly under "SanPham" document
                    uploadImageToStorage(id, i);
                } else {
                    // Put subsequent images under "Uri" subcollection
                    uploadImageToStorageSubCollection(id, i);
                }
            } else {
                Toast.makeText(this, "Chưa chọn ảnh", Toast.LENGTH_SHORT).show();
            }
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("SanPham")
                .document(id)
                .set(orderData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ADDSP.this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ADDSP.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadImageToStorage(String idSanPham, final int index) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/" + idSanPham + "/" + index);
        storageReference.putFile(imageUris[index])
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                Map<String, Object> imageData = new HashMap<>();
                                imageData.put("UrI", imageUrl);
                                firestore.collection("SanPham").document(idSanPham)
                                        .update(imageData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("DangKiSanPham", "Cập nhật URL hình ảnh vào Firestore");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("DangKiSanPham", "Lỗi khi cập nhật URL hình ảnh vào Firestore", e);
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Lỗi khi tải ảnh lên Firebase Storage", e);
                    }
                });
    }

    private void uploadImageToStorageSubCollection(String idSanPham, final int index) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/" + idSanPham + "/" + index);
        storageReference.putFile(imageUris[index])
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                Map<String, Object> imageData = new HashMap<>();
                                imageData.put("Url", imageUrl);
                                firestore.collection("SanPham").document(idSanPham).collection("URI").document("UrI" + index)
                                        .set(imageData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("DangKiSanPham", "Cập nhật URL hình ảnh vào Firestore");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("DangKiSanPham", "Lỗi khi cập nhật URL hình ảnh vào Firestore", e);
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Lỗi khi tải ảnh lên Firebase Storage", e);
                    }
                });
    }
}
