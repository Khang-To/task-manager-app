package com.example.taskmanagerapp.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.taskmanagerapp.Models.CongViec;
import com.example.taskmanagerapp.Models.MainModelItem;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "QuanLyCongViec.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TAG = "DataBaseHelper";
    // Table DanhSachCongViec
    private static final String TABLE_DANH_SACH = "DanhSachCongViec";
    private static final String COLUMN_DS_ID = "id";
    private static final String COLUMN_DS_TEN = "tenDanhSach";

    // Table CongViec
    private static final String TABLE_CONG_VIEC = "CongViec";
    private static final String COLUMN_CV_ID = "id";
    private static final String COLUMN_CV_TEN = "tenCongViec";
    private static final String COLUMN_CV_NGAY_NHAC = "ngayNhac";
    private static final String COLUMN_CV_NGAY_DEN_HAN = "ngayDenHan";
    private static final String COLUMN_CV_GHI_CHU = "ghiChu";
    private static final String COLUMN_CV_TRANG_THAI = "trangThai"; // 0 = chưa hoàn thành, 1 = hoàn thành
    private static final String COLUMN_CV_LOAI = "loai"; // 0 = thường, 1 = quan trọng
    private static final String COLUMN_CV_DS_ID = "danhSachId"; // foreign key
    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_DS = "CREATE TABLE " + TABLE_DANH_SACH + "(" +
                COLUMN_DS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DS_TEN + " TEXT)";
        db.execSQL(CREATE_TABLE_DS);

        String CREATE_TABLE_CV = "CREATE TABLE " + TABLE_CONG_VIEC + "(" +
                COLUMN_CV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CV_TEN + " TEXT, " +
                COLUMN_CV_NGAY_NHAC + " TEXT, " +
                COLUMN_CV_NGAY_DEN_HAN + " TEXT, " +
                COLUMN_CV_GHI_CHU + " TEXT, " +
                COLUMN_CV_TRANG_THAI + " INTEGER, " +
                COLUMN_CV_LOAI + " INTEGER, " +
                COLUMN_CV_DS_ID + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_CV_DS_ID + ") REFERENCES " + TABLE_DANH_SACH + "(" + COLUMN_DS_ID + "))";
        db.execSQL(CREATE_TABLE_CV);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Nếu phiên bản cũ nhỏ hơn 2, thực hiện các thay đổi cần thiết
            // Ví dụ: Thêm cột mới, sửa lỗi, v.v.
            // Trong trường hợp này, bạn đã có cột loai rồi nên không cần thêm
            // Nhưng nếu bạn muốn thêm cột mới, bạn có thể làm như sau:
            // db.execSQL("ALTER TABLE " + TABLE_CONG_VIEC + " ADD COLUMN " + COLUMN_CV_LOAI + " INTEGER DEFAULT 0");
        }
        // Sau khi thực hiện các thay đổi, bạn có thể xóa bảng cũ và tạo lại (nếu cần)
        // Hoặc giữ nguyên dữ liệu cũ (tùy thuộc vào yêu cầu của bạn)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONG_VIEC);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DANH_SACH);
        onCreate(db);
    }

    /*----------------------Các phương thức xử lý Database----------------------------*/

    //Hàm lấy toàn bộ danh sách
    public List<MainModelItem> getAllDanhSach() {
        List<MainModelItem> danhSachList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DANH_SACH, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DS_ID));
                String ten = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DS_TEN));
                danhSachList.add(new MainModelItem(2, id, ten)); // 2 là viewType danh sách
            } while (cursor.moveToNext());
        }

        cursor.close();
        return danhSachList;
    }

    // Hàm thêm danh sách
    public void themDanhSach(String tenDanhSach) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DS_TEN, tenDanhSach);
        db.insert(TABLE_DANH_SACH, null, values);
        db.close();
    }

    // Kiểm tra tên danh sách có tồn tại chưa
    public boolean isTenDanhSachExists(String tenDanhSach) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DANH_SACH + " WHERE " + COLUMN_DS_TEN + " = ?", new String[]{tenDanhSach});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // Sinh tên không trùng (abc, abc(1), abc(2), ...)
    public String generateUniqueTenDanhSach(String baseName) {
        String newName = baseName;
        int count = 1;
        while (isTenDanhSachExists(newName)) {
            newName = baseName + "(" + count + ")";
            count++;
        }
        return newName;
    }

    public List<CongViec> getAllCongViecTheoDanhSach(int danhSachId) {
        List<CongViec> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONG_VIEC + " WHERE " + COLUMN_CV_DS_ID + " = ?", new String[]{String.valueOf(danhSachId)});

        if (cursor.moveToFirst()) {
            do {
                CongViec cv = new CongViec(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CV_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CV_TEN)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CV_NGAY_NHAC)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CV_NGAY_DEN_HAN)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CV_GHI_CHU)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CV_TRANG_THAI)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CV_LOAI)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CV_DS_ID))
                );
                list.add(cv);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void themCongViec(CongViec congViec) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("tenCongViec", congViec.getTen());
        values.put("ngayNhac", congViec.getNgayNhac());
        values.put("ngayDenHan", congViec.getNgayDenHan());
        values.put("ghiChu", congViec.getGhiChu());
        values.put("trangThai", congViec.getTrangThai());
        values.put("loai", congViec.getLoai());
        values.put("danhSachId", congViec.getDanhSachId());

        db.insert("CongViec", null, values);
        db.close();
    }

    public void xoaCongViec(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("CongViec", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }
    public void capNhatTrangThai(int id, int trangThaiMoi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("trangThai", trangThaiMoi);

        db.update("CongViec", values, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void capNhatLoai(int id, int loai) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("loai", loai);
        db.update("CongViec", values, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Lấy danh sách công việc quan trọng
    public List<CongViec> getCongViecQuanTrong() {
        List<CongViec> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONG_VIEC + " WHERE " + COLUMN_CV_LOAI + " = 1", null);

        if (cursor.moveToFirst()) {
            do {
                CongViec cv = new CongViec(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CV_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CV_TEN)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CV_NGAY_NHAC)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CV_NGAY_DEN_HAN)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CV_GHI_CHU)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CV_TRANG_THAI)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CV_LOAI)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CV_DS_ID))
                );
                list.add(cv);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // Trong DataBaseHelper.java NA
    public void capNhatCongViec(CongViec congViec) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CV_TEN, congViec.getTen());
        values.put(COLUMN_CV_NGAY_NHAC, congViec.getNgayNhac());
        values.put(COLUMN_CV_NGAY_DEN_HAN, congViec.getNgayDenHan());
        values.put(COLUMN_CV_GHI_CHU, congViec.getGhiChu());
        values.put(COLUMN_CV_TRANG_THAI, congViec.getTrangThai());
        values.put(COLUMN_CV_LOAI, congViec.getLoai());
        values.put(COLUMN_CV_DS_ID, congViec.getDanhSachId());

        db.update(TABLE_CONG_VIEC, values, COLUMN_CV_ID + " = ?", new String[]{String.valueOf(congViec.getId())});
        db.close();
    }
    // Phương thức lấy công việc theo ID
    public CongViec getCongViecById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        CongViec congViec = null;
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_CONG_VIEC, null, COLUMN_CV_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                congViec = mapCursorToCongViec(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi lấy công việc từ database: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return congViec;
    }


    // Phương thức ánh xạ Cursor sang CongViec
    private CongViec mapCursorToCongViec(Cursor cursor) {
        int idcv = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CV_ID));
        String ten = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CV_TEN));
        String ngayNhac = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CV_NGAY_NHAC));
        String ngayDenHan = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CV_NGAY_DEN_HAN));
        String ghiChu = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CV_GHI_CHU));
        int trangThai = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CV_TRANG_THAI));
        int loai = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CV_LOAI));
        int danhSachId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CV_DS_ID));
        return new CongViec(idcv, ten, ngayNhac, ngayDenHan, ghiChu, trangThai, loai, danhSachId);
    }

}
