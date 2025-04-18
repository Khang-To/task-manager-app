package com.example.taskmanagerapp;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanagerapp.Adapters.TacVuAdapter;
import com.example.taskmanagerapp.DataBase.DataBaseHelper;
import com.example.taskmanagerapp.MainActivity;
import com.example.taskmanagerapp.Models.CongViec;
import com.example.taskmanagerapp.R;
import com.example.taskmanagerapp.ThemTacVuActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ThemDanhSachActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private FloatingActionButton btnThemTacVuTrongDS;
    private List<CongViec> danhSach;
    private TacVuAdapter adapter;
    private RecyclerView recyclerViewDS;
    private int danhSachId = -1;
    private TextView tvTitle;

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

        tvTitle = findViewById(R.id.tvTenDanhSach);

        Intent intent = getIntent();
        danhSachId = intent.getIntExtra("danhSachId", -1);
        String tenDanhSach = intent.getStringExtra("tenDanhSach");

        if (danhSachId != -1 && tenDanhSach != null) {
            tvTitle.setText(tenDanhSach);
        } else {
            danhSachId = 0;  // Set default ID for "Tác vụ chung" or similar if needed.  Adjust as per your DB logic
            showDialogThemDanhSach();
        }

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));

        btnThemTacVuTrongDS = findViewById(R.id.btnThemTacVuTrongDS);
        recyclerViewDS = findViewById(R.id.recyclerViewDanhSach);
        danhSach = new ArrayList<>();
        adapter = new TacVuAdapter(this, danhSach);
        recyclerViewDS.setAdapter(adapter);

        btnThemTacVuTrongDS.setOnClickListener(v -> {
            // Corrected: Always use the activity's current danhSachId
            if (danhSachId > 0) {  // Now a more robust check after create/load, assuming list IDs are > 0
                ThemTacVuActivity dialog = ThemTacVuActivity.newInstance(danhSachId);
                dialog.setOnTaskAddedListener(this::loadDanhSach);
                dialog.show(getSupportFragmentManager(), "ThemTacVuDialog");
            } else {
                Toast.makeText(this, "Chưa có danh sách!", Toast.LENGTH_SHORT).show(); // More user-friendly
            }
        });

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                CongViec congViec = danhSach.get(position);
                new AlertDialog.Builder(ThemDanhSachActivity.this)
                        .setTitle("Xác nhận")
                        .setMessage(Html.fromHtml("Bạn có chắc muốn xóa công việc <b><i>\"" + congViec.getTen() + "\"</i></b>?"))
                        .setPositiveButton("Có", (dialog, which) -> {
                            new DataBaseHelper(ThemDanhSachActivity.this).xoaCongViec(congViec.getId());
                            danhSach.remove(position);
                            adapter.notifyItemRemoved(position);
                        })
                        .setNegativeButton("Không", (dialog, which) -> adapter.notifyItemChanged(position)) // Restore
                        .setCancelable(false)
                        .show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                Paint paint = new Paint();
                paint.setColor(Color.RED);
                Drawable icon = ContextCompat.getDrawable(ThemDanhSachActivity.this, R.drawable.ic_delete_item);
                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;

                if (dX < 0) {
                    c.drawRect(itemView.getRight() + dX, itemView.getTop(), itemView.getRight(), itemView.getBottom(), paint);
                    int iconTop = itemView.getTop() + iconMargin;
                    int iconBottom = iconTop + icon.getIntrinsicHeight();
                    int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                    int iconRight = itemView.getRight() - iconMargin;
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    icon.draw(c);
                }
            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerViewDS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDanhSach();
    }

    private void showDialogThemDanhSach() {
        EditText input = new EditText(this);
        input.setHint("Nhập tên danh sách");
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        new AlertDialog.Builder(this)
                .setTitle("Thêm danh sách mới")
                .setView(input)
                .setPositiveButton("Tạo", (dialog, which) -> {
                    String tenNhap = input.getText().toString().trim();
                    if (!tenNhap.isEmpty()) {
                        DataBaseHelper dbHelper = new DataBaseHelper(ThemDanhSachActivity.this);
                        String tenKhongTrung = dbHelper.generateUniqueTenDanhSach(tenNhap);
                        long newId = dbHelper.themDanhSach(tenKhongTrung);

                        if (newId != -1) {
                            danhSachId = (int) newId;  // Corrected: Update danhSachId after creation
                            tvTitle.setText(tenKhongTrung);
                            Toast.makeText(this, "Đã tạo danh sách", Toast.LENGTH_SHORT).show();
                            loadDanhSach(); // Load tasks for the new list
                        } else {
                            Toast.makeText(this, "Lỗi tạo danh sách!", Toast.LENGTH_SHORT).show();
                            finish();  // Or handle the error as appropriate
                        }
                    } else {
                        Toast.makeText(this, "Tên không được trống!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("Hủy", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void loadDanhSach() {
        if (danhSachId == 0) return; // Safeguard: Exit if no valid list ID

        DataBaseHelper dbHelper = new DataBaseHelper(this);
        List<CongViec> tatCaCongViec = dbHelper.getAllCongViecTheoDanhSach(danhSachId);

        //  Consider using a single list in your adapter and sorting within it for better performance
        List<CongViec> chuaHoanThanh = new ArrayList<>();
        List<CongViec> daHoanThanh = new ArrayList<>();

        for (CongViec cv : tatCaCongViec) {
            if (cv.getTrangThai() == 0) {
                chuaHoanThanh.add(cv);
            } else {
                daHoanThanh.add(cv);
            }
        }

        danhSach.clear();
        danhSach.addAll(chuaHoanThanh);
        danhSach.addAll(daHoanThanh);
        adapter.setDanhSach(danhSach);
        adapter.notifyDataSetChanged();
    }

}
