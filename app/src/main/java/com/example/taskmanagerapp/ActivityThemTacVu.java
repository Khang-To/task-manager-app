package com.example.taskmanagerapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

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
        datNgayHan = view.findViewById(R.id.datNgayHan);
        datTGNhac = view.findViewById(R.id.datTGNhac);

        datNgayHan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String selectedDate =dayOfMonth + "/" + (month + 1) + "/" + year;
                                datNgayHan.setText(selectedDate);
                            }
                        },
                        year, month, day
                );
                datePickerDialog.show();
            }
        });

        datTGNhac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                                // Sau khi chọn ngày, mở tiếp TimePickerDialog
                                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                int minute = calendar.get(Calendar.MINUTE);

                                TimePickerDialog timePickerDialog = new TimePickerDialog(
                                        getContext(),
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                                                String datetime = selectedDayOfMonth + "/" + (selectedMonth + 1) + "/" + selectedYear
                                                        + " - " + String.format("%02d:%02d", selectedHour, selectedMinute);
                                                datTGNhac.setText(datetime); // Gán text mới cho nút hoặc TextView
                                            }
                                        },
                                        hour, minute, true
                                );

                                timePickerDialog.show();
                            }
                        },
                        year, month, day
                );

                datePickerDialog.show();

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.them_tac_vu,container,false);
    }


}
