package com.example.taskmanagerapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class ChiTietTacVuActivity extends AppCompatActivity {
    private ImageButton btnTroVeChiTietTV, Star;
    private TextView txtNgayDenHan, txtNhacToi;
    private boolean isStarFilled = true; // Biến để theo dõi trạng thái ngôi sao

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chi_tiet_tac_vu);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ view
        txtNgayDenHan = findViewById(R.id.datNgayHan3);
        txtNhacToi = findViewById(R.id.datTGNhac3);
        btnTroVeChiTietTV = findViewById(R.id.btnTroVeChiTietTV);
        Star = findViewById(R.id.Star); // Ánh xạ Star
        CheckBox checkBox = findViewById(R.id.CheckBoxCV);
        EditText editText = findViewById(R.id.editTextNoiDungCV);

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editText.setPaintFlags(editText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                editText.setPaintFlags(editText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        });

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

}