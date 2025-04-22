package com.example.taskmanagerapp.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.taskmanagerapp.Models.CongViec;
import com.example.taskmanagerapp.Models.MainModelItem;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "QuanLyCongViec.db";
    private static final int DATABASE_VERSION = 1;

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
    //-------------sửa chỗ này để có thể lấy id của danh sách vừa tạo-----------------------
    public long themDanhSach(String tenDanhSach) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tenDanhSach", tenDanhSach);
        long id = db.insert("DanhSachCongViec", null, values);
        db.close();
        return id;  //trả về id danh sách vừa tạo ra để có thể tiếp tục thêm tác vụ ngay sau đó bên trong ds
    }
    //-------------------------------------------------------------------------------------

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

    // Đếm task chưa hoàn thành theo danh sách
    public int demTacVuChuaHoanThanhTheoDanhSach(int danhSachId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM CongViec WHERE danhSachId = ? AND trangThai = 0",
                new String[]{String.valueOf(danhSachId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // Đếm task quan trọng chưa hoàn thành
    public int demTacVuQuanTrongChuaHoanThanh() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM CongViec WHERE loai = 1 AND trangThai = 0", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    //Hàm sửa tên danh sách
    public void suaTenDanhSach(int id, String tenMoi){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tenDanhSach",tenMoi);
        db.update("DanhSachCongViec",values,"id = ?",new String[]{String.valueOf(id)});
        db.close();
    }

    //Hàm xóa danh sách và cả tác vụ bên trong
    public void xoaDanhSach(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Xóa tất cả công việc thuộc danh sách
        db.delete("CongViec", "danhSachId = ?", new String[]{String.valueOf(id)});
        // Xóa danh sách
        db.delete("DanhSachCongViec", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

}
