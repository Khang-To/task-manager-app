package com.example.taskmanagerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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
}