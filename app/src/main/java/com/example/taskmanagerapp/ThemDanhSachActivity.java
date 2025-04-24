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
import android.widget.LinearLayout;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanagerapp.Adapters.TacVuAdapter;
import com.example.taskmanagerapp.DataBase.DataBaseHelper;
import com.example.taskmanagerapp.Models.CongViec;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ThemDanhSachActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private FloatingActionButton btnThemTacVuTrongDS;
    private int danhSachId;
    private TacVuAdapter adapter;
    private List<CongViec> danhSach;
    private RecyclerView recyclerViewThemDanhSach;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_them_danh_sach);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.themds), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        danhSachId = getIntent().getIntExtra("danhSachId", -1);
        String tenDanhSach = getIntent().getStringExtra("tenDanhSach");

        recyclerViewThemDanhSach = findViewById(R.id.recyclerViewThemDanhSach);
        recyclerViewThemDanhSach.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewThemDanhSach.setHasFixedSize(true);

        danhSach = new ArrayList<>();
        adapter = new TacVuAdapter(this, danhSach);
        recyclerViewThemDanhSach.setAdapter(adapter);

        if (danhSachId != -1 && tenDanhSach != null) {
            // Được truyền từ danh sách cụ thể
            TextView tvTitle = findViewById(R.id.tvTenDS);
            tvTitle.setText(tenDanhSach);
            loadDanhSach();
        } else {
            // Không truyền gì là "Tạo mới danh sách"
            showDialogThemDanhSach();
        }

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        btnThemTacVuTrongDS = findViewById(R.id.btnThemTacVuTrongDS);
        btnThemTacVuTrongDS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThemTacVuActivity dialog = ThemTacVuActivity.newInstance(danhSachId);
                dialog.setOnTaskAddedListener(() -> {
                    // Gọi lại hàm load danh sách khi có tác vụ mới
                    loadDanhSach();
                });
                dialog.show(getSupportFragmentManager(), ThemTacVuActivity.TAG);
            }
        });

        //Xử lý xóa tác vụ bằng cách vuốt qua trái
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                CongViec congViec = danhSach.get(position);

                new AlertDialog.Builder(ThemDanhSachActivity.this)
                        .setTitle("Xác nhận")
                        .setMessage(Html.fromHtml("Bạn có chắc muốn xóa công việc <b><i>\"" + congViec.getTen() + "\"</i></b> hay không?"))
                        .setPositiveButton("Có", (dialog, which) -> {
                            // Xóa khỏi DB
                            DataBaseHelper dbHelper = new DataBaseHelper(ThemDanhSachActivity.this);
                            dbHelper.xoaCongViec(congViec.getId());

                            // Xóa khỏi danh sách và cập nhật adapter
                            danhSach.remove(position);
                            adapter.notifyItemRemoved(position);
                        })
                        .setNegativeButton("Không", (dialog, which) -> {
                            // Khôi phục item
                            adapter.notifyItemChanged(position);
                            dialog.dismiss();
                        })
                        .setCancelable(false)
                        .show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c,
                                    @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                View itemView = viewHolder.itemView;
                Paint paint = new Paint();
                paint.setColor(Color.RED);

                Drawable icon = ContextCompat.getDrawable(ThemDanhSachActivity.this, R.drawable.ic_delete_item);
                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;

                if (dX < 0) { // Vuốt sang trái
                    // Vẽ nền đỏ
                    c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                            (float) itemView.getRight(), (float) itemView.getBottom(), paint);

                    // Vẽ icon thùng rác
                    int iconTop = itemView.getTop() + iconMargin;
                    int iconBottom = iconTop + icon.getIntrinsicHeight();
                    int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                    int iconRight = itemView.getRight() - iconMargin;

                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    icon.draw(c);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewThemDanhSach);
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

                // Lưu danh sách mới và lấy ID
                long idMoi = dbHelper.themDanhSach(tenKhongTrung);

                // Gán lại ID cho biến toàn cục
                danhSachId = (int) idMoi;

                // Cập nhật TextView tiêu đề với tên danh sách mới
                TextView tvTitle = findViewById(R.id.tvTenDS); // tvTitle là TextView trong layout item set lại tên cho danh sách
                tvTitle.setText(tenKhongTrung);

                Toast.makeText(this, "Đã tạo danh sách thành công", Toast.LENGTH_SHORT).show();

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

    private void loadDanhSach() {
        DataBaseHelper dbHelper = new DataBaseHelper(this);
        List<CongViec> tatCaCongViec = dbHelper.getAllCongViecTheoDanhSach(danhSachId);
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

    @Override
    protected void onResume() {
        super.onResume();
        loadDanhSach();
    }
}