package com.example.taskmanagerapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.taskmanagerapp.DataBase.DataBaseHelper;

public class ThemDanhSachActivity extends AppCompatActivity {

    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_them_danh_sach);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.danh_sach), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        //-----phần này đã chỉnh sửa
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);
        String tenDanhSach = intent.getStringExtra("tenDanhSach");

        if (id != -1 && tenDanhSach != null) {
            // Được truyền từ danh sách cụ thể
            TextView tvTitle = findViewById(R.id.tvTenDanhSach);
            tvTitle.setText(tenDanhSach);

        } else {
            // Không truyền gì là "Tạo mới danh sách"
            showDialogThemDanhSach();
        }

        //------------------------------
    }
    private void showDialogThemDanhSach() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm danh sách mới");

        final EditText input = new EditText(this);
        input.setHint("Nhập tên danh sách");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Tạo danh sách", (dialog, which) -> {
            String tenNhap = input.getText().toString().trim();
            if (!tenNhap.isEmpty()) {
                DataBaseHelper dbHelper = new DataBaseHelper(ThemDanhSachActivity.this);
                String tenKhongTrung = dbHelper.generateUniqueTenDanhSach(tenNhap);
                dbHelper.themDanhSach(tenKhongTrung);

                // Cập nhật TextView tiêu đề với tên danh sách mới
                TextView tvTitle = findViewById(R.id.tvTenDanhSach); // tvTitle là TextView trong layout
                tvTitle.setText(tenKhongTrung);

                Toast.makeText(this, "Đã tạo danh sách thành công", Toast.LENGTH_SHORT).show();
                // Không gọi finish() nữa
            } else {
                Toast.makeText(this, "Tên không được để trống", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> {
            dialog.cancel();
            finish(); // Quay về Main nếu hủy
        });

        builder.setCancelable(false); // Không cho bấm ra ngoài để thoát
        builder.show();
    }

}