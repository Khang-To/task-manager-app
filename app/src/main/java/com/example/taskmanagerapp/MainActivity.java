package com.example.taskmanagerapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanagerapp.Adapters.MainAdapter;
import com.example.taskmanagerapp.DataBase.DataBaseHelper;
import com.example.taskmanagerapp.Models.MainModelItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MainAdapter adapter;
    private List<MainModelItem> itemList;
    private DataBaseHelper dbHelper;
    private Button btnThemDanhSach;
    private Button btnThemTacVu; // Nút để mở ThemTacVuActivity NA thêm

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Kiểm tra xem quyền thông báo đã được cấp chưa
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                // Yêu cầu quyền thông báo
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }


        // Tạo Notification Channel cho các thông báo trên Android 8.0 trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "TASK_CHANNEL_ID",
                    "Nhắc nhở công việc",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Kênh nhắc nhở công việc");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }


        recyclerView = findViewById(R.id.recycleViewMain);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DataBaseHelper(this);
        itemList = new ArrayList<>();

        // Thêm 2 mục cứng
        itemList.add(new MainModelItem(0, -1, null)); // Tác vụ
        itemList.add(new MainModelItem(1, -1, null)); // Quan trọng

        // Thêm divider ngăn cách
        itemList.add(new MainModelItem(3, -1, null)); // Divider

        // Lấy danh sách từ database và thêm vào danh sách hiển thị
        List<MainModelItem> danhSachTuDB = dbHelper.getAllDanhSach();
        for (MainModelItem ds : danhSachTuDB) {
            itemList.add(new MainModelItem(2, ds.getId(), ds.getTenDanhSach()));
        }

        // Gán adapter
        MainAdapter adapter = new MainAdapter(this, itemList);
        recyclerView.setAdapter(adapter);

        //set event cho nút thêm danh sách
        btnThemDanhSach = findViewById(R.id.btnThemDanhSach);
        btnThemDanhSach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ThemDanhSachActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền đã được cấp, bạn có thể tiếp tục gửi thông báo
            } else {
                // Quyền bị từ chối, bạn có thể thông báo cho người dùng hoặc làm gì đó
                // Ví dụ: Thông báo cho người dùng rằng họ không thể nhận thông báo
                Toast.makeText(this, "Bạn cần cấp quyền thông báo để nhận nhắc nhở!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}