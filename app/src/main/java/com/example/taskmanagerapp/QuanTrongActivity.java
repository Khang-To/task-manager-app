package com.example.taskmanagerapp;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanagerapp.Adapters.TacVuAdapter;
import com.example.taskmanagerapp.DataBase.DataBaseHelper;
import com.example.taskmanagerapp.Models.CongViec;

import java.util.List;

public class QuanTrongActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageButton btnTroVe;
    private DataBaseHelper dataBaseHelper;
    private TacVuAdapter adapter;
    private List<CongViec> importantCongViecList;
    private CongViecChangedReceiver congViecChangedReceiver;
    public static final String ACTION_CONG_VIEC_CHANGED = "com.example.taskmanagerapp.CONG_VIEC_CHANGED";
    private ImageView imageView;
    private TextView textView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quan_trong);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.quantrong), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo DataBaseHelper
        dataBaseHelper = new DataBaseHelper(this);

        // Ánh xạ RecyclerView và ImageButton
        recyclerView = findViewById(R.id.recyclerView);
        btnTroVe = findViewById(R.id.btnTroVe);
        imageView = findViewById(R.id.imageView);
        textView2 = findViewById(R.id.textView2);
        // Thiết lập LayoutManager cho RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // Lấy danh sách công việc quan trọng từ DataBaseHelper
        importantCongViecList = dataBaseHelper.getCongViecQuanTrong();

        // Khởi tạo và thiết lập Adapter cho RecyclerView
        adapter = new TacVuAdapter(this, importantCongViecList);
        recyclerView.setAdapter(adapter);

        // Thiết lập OnItemClickListener cho adapter
        adapter.setOnItemClickListener(new TacVuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CongViec cv) {
                // Tạo Intent để chuyển sang ChiTietTacVuActivity
                Intent intent = new Intent(QuanTrongActivity.this, ChiTietTacVuActivity.class);
                // Truyền dữ liệu của công việc qua Intent
                intent.putExtra("id", cv.getId());
                intent.putExtra("ten", cv.getTen());
                intent.putExtra("trangThai", cv.getTrangThai());
                intent.putExtra("loai", cv.getLoai());
                // Truyền thông tin về nguồn gốc
                intent.putExtra("source", "QuanTrongActivity");
                // Khởi chạy ChiTietTacVuActivity
                startActivity(intent);
            }
        });

        // Xử lý sự kiện click cho btnTroVe
        btnTroVe.setOnClickListener(v -> {
            Intent intent = new Intent(QuanTrongActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Khởi tạo BroadcastReceiver
        congViecChangedReceiver = new CongViecChangedReceiver();
        // Thêm AdapterDataObserver
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkEmpty();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                checkEmpty();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                checkEmpty();
            }
        });

        // Kiểm tra dữ liệu ban đầu
        checkEmpty();
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onResume() {
        super.onResume();
        // Đăng ký BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter(ACTION_CONG_VIEC_CHANGED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(congViecChangedReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(congViecChangedReceiver, intentFilter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Hủy đăng ký BroadcastReceiver
        unregisterReceiver(congViecChangedReceiver);
    }

    // BroadcastReceiver để nhận thông báo khi có thay đổi trong SQLite
    private class CongViecChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Cập nhật lại danh sách công việc quan trọng
            loadDanhSachCongViecQuanTrong();
        }
    }

    // Hàm load lại danh sách công việc quan trọng
    private void loadDanhSachCongViecQuanTrong() {
        DataBaseHelper dbHelper = new DataBaseHelper(this);
        importantCongViecList = dbHelper.getCongViecQuanTrong();
        adapter.setDanhSach(importantCongViecList);
        adapter.notifyDataSetChanged();
    }
    // Hàm kiểm tra và cập nhật giao diện
    private void checkEmpty() {
        if (adapter.getItemCount() == 0) {
            // RecyclerView trống
            imageView.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);
        } else {
            // RecyclerView có dữ liệu
            imageView.setVisibility(View.GONE);
            textView2.setVisibility(View.GONE);
        }
    }
}
