package com.example.taskmanagerapp;

import static androidx.core.content.ContentProviderCompat.requireContext;
import static com.example.taskmanagerapp.ThemTacVuActivity.TAG;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Build;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ChiTietTacVuActivity extends AppCompatActivity {
    private ImageButton btnTroVeChiTietTV, Star;
    private TextView txtNgayDenHan, txtNhacToi, tvTenTacVu, txtChiTietTV;
    private boolean isStarFilled = true; // Biến để theo dõi trạng thái ngôi sao
    private EditText editTextNoiDungCV;
    private CheckBox checkBoxCV;
    private int id, trangThai, loai;
    private String ten;
    private EditText   txtGhiChu;
//    private TextView datNgayHan3, datTGNhac3;
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

//        datNgayHan3 = findViewById(R.id.datNgayHan3);
//        datTGNhac3 = findViewById(R.id.datTGNhac3);
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
                finish();
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
                    target.setBackgroundResource(R.drawable.bg_dachon_tg);
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
                                String result = String.format(Locale.getDefault(), "%02d/%02d/%04d - %02d:%02d",
                                        dayOfMonth, month1 + 1, year1, hourOfDay, minute1);
                                target.setBackgroundResource(R.drawable.bg_dachon_tg);
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

        String noiDung = editTextNoiDungCV.getText().toString();
        String thoiGianNhac = txtNhacToi.getText().toString();
        String ghiChu = txtGhiChu.getText().toString();
        int newLoai = isStarFilled ? 1 : 0;
        int newTrangThai = checkBoxCV.isChecked() ? 1 : 0;
        String ngayDenHan = txtNgayDenHan.getText().toString().trim();

        if (ngayDenHan.equals("Ngày đến hạn")) {
            ngayDenHan = "";
        }

        if (thoiGianNhac.equals("Nhắc tôi")) {
            thoiGianNhac = "";
        }

        try {
            CongViec cv = new CongViec(id, noiDung, thoiGianNhac, ngayDenHan, ghiChu, newTrangThai, newLoai, danhSachId);
            long taskId = dbHelper.capNhatCongViec(cv);
            if (!thoiGianNhac.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault());
                Date date = sdf.parse(thoiGianNhac);
                if (date != null) {
                    long triggerTime = date.getTime();
                    Intent intent = new Intent(ChiTietTacVuActivity.this, ReminderReceiver.class);
                    intent.putExtra("taskId", (int) taskId);
                    intent.putExtra("tenCV", noiDung);
                    intent.putExtra("ngayDenHan", ngayDenHan);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            ChiTietTacVuActivity.this,
                            (int) taskId,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                    );

                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (alarmManager.canScheduleExactAlarms()) {
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                        } else {
                            Toast.makeText(this, "Bạn cần cấp quyền đặt báo thức chính xác.", Toast.LENGTH_LONG).show();
                        }
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi lưu thông tin.", Toast.LENGTH_SHORT).show();
            return;
        }

        sendBroadcast(new Intent(ACTION_CONG_VIEC_CHANGED));
        Toast.makeText(this, "Đã lưu thông tin.", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Kết thúc hàm luuThongTin()");
    }

    private void loadDataFromDatabase() {
        CongViec congViec = dbHelper.getCongViecById(id);
        if (congViec != null) {
            editTextNoiDungCV.setText(congViec.getTen());

            // Nếu ngày đến hạn rỗng -> để lại chữ mặc định
            if (congViec.getNgayDenHan() == null || congViec.getNgayDenHan().isEmpty()) {
                txtNgayDenHan.setText("Ngày đến hạn");
                txtNgayDenHan.setBackgroundResource(R.drawable.bg_macdinh);
            } else {
                txtNgayDenHan.setText(congViec.getNgayDenHan());
                txtNgayDenHan.setBackgroundResource(R.drawable.bg_dachon_tg);
            }

            // Xử lý nhắc tới
            if (congViec.getNgayNhac() == null || congViec.getNgayNhac().isEmpty()) {
                txtNhacToi.setText("Nhắc tôi");
                txtNhacToi.setBackgroundResource(R.drawable.bg_macdinh);
            } else {
                txtNhacToi.setText(congViec.getNgayNhac());
                txtNhacToi.setBackgroundResource(R.drawable.bg_dachon_tg);
            }

            txtGhiChu.setText(congViec.getGhiChu());
            checkBoxCV.setChecked(congViec.getTrangThai() == 1);
            danhSachId = congViec.getDanhSachId();
            if (congViec.getLoai() == 1) {
                Star.setImageResource(R.drawable.ic_star_filled);
                isStarFilled = true;
            } else {
                Star.setImageResource(R.drawable.ic_star_border);
                isStarFilled = false;
            }
        }
    }


}