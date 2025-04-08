package com.example.taskmanagerapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;

public class ActivityThemTacVu extends BottomSheetDialogFragment {

    public static final String TAG = "ThemTacVu";
    public static ActivityThemTacVu newInstance()
    {
        return new ActivityThemTacVu();
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

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.them_tac_vu,container,false);
    }


}
