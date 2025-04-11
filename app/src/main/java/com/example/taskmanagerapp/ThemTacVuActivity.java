package com.example.taskmanagerapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.taskmanagerapp.DataBase.DataBaseHelper;
import com.example.taskmanagerapp.Models.CongViec;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;

public class ThemTacVuActivity extends BottomSheetDialogFragment {

    public static final String TAG = "ThemTacVu";

    public interface OnTaskAddedListener {
        void onTaskAdded();
    }
    private OnTaskAddedListener listener;

    public void setOnTaskAddedListener(OnTaskAddedListener listener) {
        this.listener = listener;
    }

    public static ThemTacVuActivity newInstance()
    {
        return new ThemTacVuActivity();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView datNgayHan, datTGNhac ;
        ImageButton btnLuu;
        datNgayHan = view.findViewById(R.id.datNgayHan);
        datTGNhac = view.findViewById(R.id.datTGNhac);
        btnLuu = view.findViewById(R.id.btnLuu);

        datNgayHan.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view3, year1, month1, dayOfMonth) -> {
                        String selectedDate =dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                        datNgayHan.setBackgroundResource(R.drawable.bg_dachon_tg);
                        datNgayHan.setText(selectedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        datTGNhac.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view2, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                        // Sau khi chọn ngày, mở tiếp TimePickerDialog
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                getContext(),
                                (view1, selectedHour, selectedMinute) -> {
                                    @SuppressLint("DefaultLocale") String datetime = selectedDayOfMonth + "/" + (selectedMonth + 1) + "/" + selectedYear
                                            + " - " + String.format("%02d:%02d", selectedHour, selectedMinute);
                                    datTGNhac.setBackgroundResource(R.drawable.bg_dachon_tg);
                                    datTGNhac.setText(datetime);
                                },
                                hour, minute, true
                        );

                        timePickerDialog.show();
                    },
                    year, month, day
            );

            datePickerDialog.show();

        });

        btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = view.findViewById(R.id.editText);
                String tenCV = editText.getText().toString().trim();
                String ngayDenHan = datNgayHan.getText().toString().trim();
                String tgNhac = datTGNhac.getText().toString().trim();

                if (tenCV.isEmpty()) {
                    editText.setError("Vui lòng nhập công việc");
                    return;
                }

                CongViec congViec = new CongViec(0, tenCV, "", ngayDenHan, tgNhac, 0, 0, 0);

                DataBaseHelper db = new DataBaseHelper(getContext());
                db.themCongViec(congViec);
                Toast.makeText(getContext(), "Đã thêm công việc!", Toast.LENGTH_SHORT).show();
                if (listener != null) {
                    listener.onTaskAdded(); // Gọi callback báo cho Activity biết
                }

                dismiss(); // đóng BottomSheet sau khi lưu
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_them_tac_vu,container,false);
    }


}
