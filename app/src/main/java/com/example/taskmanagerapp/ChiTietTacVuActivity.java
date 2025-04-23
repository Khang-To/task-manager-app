package com.example.taskmanagerapp;

import static com.example.taskmanagerapp.ThemTacVuActivity.TAG;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.taskmanagerapp.DataBase.DataBaseHelper;
import com.example.taskmanagerapp.Models.CongViec;

import java.util.Calendar;

public class ChiTietTacVuActivity extends AppCompatActivity {
    private ImageButton btnTroVeChiTietTV, Star;
    private TextView txtNgayDenHan, txtNhacToi, tvTenTacVu, txtChiTietTV;
    private boolean isStarFilled = true; // Biến để theo dõi trạng thái ngôi sao
    private EditText editTextNoiDungCV;
    private CheckBox checkBoxCV;
    private int id, trangThai, loai;
    private String ten;
    private EditText   txtGhiChu;
    private TextView datNgayHan3, datTGNhac3;
    private Button btnSave;
    private static final String TAG = "ChiTietTacVuActivity";
    public static final String ACTION_CONG_VIEC_CHANGED = "com.example.taskmanagerapp.CONG_VIEC_CHANGED";
    private DataBaseHelper dbHelper;
    private int danhSachId;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chi_tiet_tac_vu);
        // Khởi tạo dbHelper ở đây
        dbHelper = new DataBaseHelper(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.chitiet), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ view
        txtNgayDenHan = findViewById(R.id.datNgayHan3);
        txtNhacToi = findViewById(R.id.datTGNhac3);
        btnTroVeChiTietTV = findViewById(R.id.btnTroVeChiTietTV);
        Star = findViewById(R.id.Star); // Ánh xạ Star
        checkBoxCV = findViewById(R.id.CheckBoxCV);
        editTextNoiDungCV = findViewById(R.id.editTextNoiDungCV);
        tvTenTacVu = findViewById(R.id.editTextNoiDungCV);
        txtChiTietTV = findViewById(R.id.txtChiTietTV);

        checkBoxCV.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editTextNoiDungCV.setPaintFlags(editTextNoiDungCV.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                editTextNoiDungCV.setPaintFlags(editTextNoiDungCV.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        });

        datNgayHan3 = findViewById(R.id.datNgayHan3);
        datTGNhac3 = findViewById(R.id.datTGNhac3);
        txtGhiChu = findViewById(R.id.txtGhiChu);
        btnSave = findViewById(R.id.btnLuuGhiChu);

        // Đặt hình ảnh mặc định là ngôi sao tô màu
        Star.setImageResource(R.drawable.ic_star_filled);

        // Sự kiện chọn ngày đến hạn
        txtNgayDenHan.setOnClickListener(v -> showDatePicker(txtNgayDenHan));

        // Sự kiện chọn nhắc tôi (ngày + giờ)
        txtNhacToi.setOnClickListener(v -> showDateTimePicker(txtNhacToi));

        // Sự kiện cho nút btnTroVeChiTietTV
        btnTroVeChiTietTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChiTietTacVuActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        // onclick cho btn
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                luuThongTin();
            }
        });
        // Sự kiện cho nút Star (ngôi sao)
        Star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thay đổi trạng thái
                isStarFilled = !isStarFilled;

                // Thay đổi hình ảnh dựa trên trạng thái
                if (isStarFilled) {
                    Star.setImageResource(R.drawable.ic_star_filled);
                } else {
                    Star.setImageResource(R.drawable.ic_star_border);
                }
            }
        });
        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        ten = intent.getStringExtra("ten");
        trangThai = intent.getIntExtra("trangThai", 0);
        loai = intent.getIntExtra("loai", 0);
        danhSachId = intent.getIntExtra("danhSachId",-1);

        // Hiển thị dữ liệu lên các View
        tvTenTacVu.setText(ten);
        if (trangThai == 1) {
            checkBoxCV.setChecked(true);
            editTextNoiDungCV.setPaintFlags(editTextNoiDungCV.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            checkBoxCV.setChecked(false);
            editTextNoiDungCV.setPaintFlags(editTextNoiDungCV.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
        if (loai == 1) {
            Star.setImageResource(R.drawable.ic_star_filled);
            isStarFilled = true;
        } else {
            Star.setImageResource(R.drawable.ic_star_border);
            isStarFilled = false;
        }
        // Nhận thông tin "source"
        String source = intent.getStringExtra("source");

        // Thay đổi nội dung của txtChiTietTV
        if ("QuanTrongActivity".equals(source)) {
            txtChiTietTV.setText("Chi tiết Quan Trọng");
        } else if ("TacVuActivity".equals(source)) {
            txtChiTietTV.setText("Chi tiết Tác Vụ");
        }
        // Tải dữ liệu từ database và hiển thị lên các View
        loadDataFromDatabase();
    }

    private void showDatePicker(TextView target) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    String date = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    target.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showDateTimePicker(TextView target) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                            (timeView, hourOfDay, minute1) -> {
                                String result = dayOfMonth + "/" + (month1 + 1) + "/" + year1
                                        + " " + String.format("%02d:%02d", hourOfDay, minute1);
                                target.setText(result);
                            }, hour, minute, true);
                    timePickerDialog.show();
                }, year, month, day);
        datePickerDialog.show();
    }
    //

    //
    private void luuThongTin() {
        Log.d(TAG, "Bắt đầu hàm luuThongTin()");

        // 1. Lấy dữ liệu từ các View
        String noiDung = editTextNoiDungCV.getText().toString();
        String thoiGianNhac = txtNhacToi.getText().toString();
        String ngayDenHan = txtNgayDenHan.getText().toString();
        //String thoiGianNhac = txtNhacToi.getText().toString();
        String ghiChu = txtGhiChu.getText().toString();
        int newLoai = isStarFilled ? 1 : 0;
        int newTrangThai = checkBoxCV.isChecked() ? 1 : 0;

        Log.d(TAG, "noiDung: " + noiDung);
        Log.d(TAG, "thoiGianNhac: " + thoiGianNhac);
        Log.d(TAG, "ngayDenHan: " + ngayDenHan);
        Log.d(TAG, "ghiChu: " + ghiChu);
        Log.d(TAG, "newLoai: " + newLoai);
        Log.d(TAG, "newTrangThai: " + newTrangThai);

        // 2. Kiểm tra dữ liệu đầu vào
        if (noiDung.isEmpty() || ngayDenHan.isEmpty() || thoiGianNhac.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin.", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Dữ liệu không đầy đủ. Không lưu.");
            return; // Dừng lại nếu dữ liệu không đầy đủ
        }

        // 3. Cập nhật thông tin vào cơ sở dữ liệu
        try {
            Log.d(TAG, "Gọi hàm capNhatCongViec()");
            CongViec cv = new CongViec(id, noiDung, thoiGianNhac, ngayDenHan, ghiChu, newTrangThai, newLoai,danhSachId);
            dbHelper.capNhatCongViec(cv);
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi cập nhật cơ sở dữ liệu: " + e.getMessage());
            Toast.makeText(this, "Lỗi khi lưu thông tin.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 4. Gửi thông báo cập nhật
        Log.d(TAG, "Gửi thông báo cập nhật");
        Intent intent = new Intent(ACTION_CONG_VIEC_CHANGED);
        sendBroadcast(intent);

        // 5. Thông báo thành công
        Toast.makeText(this, "Đã lưu thông tin.", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Kết thúc hàm luuThongTin()");
    }
    private void loadDataFromDatabase() {
        // Lấy dữ liệu từ database dựa trên id
        CongViec congViec = dbHelper.getCongViecById(id);

        // Kiểm tra xem congViec có null không
        if (congViec != null) {
            // Hiển thị dữ liệu lên các View
            editTextNoiDungCV.setText(congViec.getTen()); // Sử dụng getter
            txtNgayDenHan.setText(congViec.getNgayDenHan()); // Sử dụng getter
            txtNhacToi.setText(congViec.getNgayNhac()); // Sử dụng getter
            txtGhiChu.setText(congViec.getGhiChu()); // Sử dụng getter
            // Cập nhật trạng thái checkbox và ngôi sao nếu cần
            checkBoxCV.setChecked(congViec.getTrangThai() == 1); // Sử dụng getter
            if (congViec.getLoai() == 1) { // Sử dụng getter
                Star.setImageResource(R.drawable.ic_star_filled);
                isStarFilled = true;
            } else {
                Star.setImageResource(R.drawable.ic_star_border);
                isStarFilled = false;
            }
        }
    }


}