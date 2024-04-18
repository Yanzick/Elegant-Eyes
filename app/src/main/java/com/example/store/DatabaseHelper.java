package com.example.store;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "cart.db";
    private static final String TABLE_NAME = "cart_table";
    private static final String COL_ID = "ID";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" + COL_ID + " TEXT PRIMARY KEY UNIQUE)";
        db.execSQL(createTable);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Kiểm tra xem ID đã tồn tại trong cơ sở dữ liệu chưa
        if (!isIDExists(id)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_ID, id);

            long result = db.insert(TABLE_NAME, null, contentValues);
            return result != -1;
        } else {
            // Nếu ID đã tồn tại, không thêm mới và trả về false
            return false;
        }
    }

    private boolean isIDExists(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_ID + " FROM " + TABLE_NAME + " WHERE " + COL_ID + "=?", new String[]{id});
        boolean exists = cursor.getCount() > 0;
        cursor.close();

        return exists;
    }

    public List<String> getAllIDs() {
        List<String> idList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_ID + " FROM " + TABLE_NAME, null);
        if (cursor != null) { // Kiểm tra cursor khác null trước khi truy cập
            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(COL_ID);
                do {
                    String id = cursor.getString(idIndex);
                    idList.add(id);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return idList;
    }
    public boolean deleteProduct(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, COL_ID + " = ?", new String[]{id});

        return result > 0;
    }

}
