package com.example.taskmanagerapp;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

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

public class TacVuActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton btnThemTacVu;
    private ImageButton btnTroVe;
    private LinearLayout layoutEmpty;
    private TacVuAdapter adapter;
    private List<CongViec> danhSach;

    private int danhSachId = 0; // Hoặc lấy ID từ Intent nếu cần

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tac_vu);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tacvu), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        btnThemTacVu = findViewById(R.id.btnThemTacVu);
        btnTroVe = findViewById(R.id.btnTroVe);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        danhSach = new ArrayList<>();
        adapter = new TacVuAdapter(this, danhSach);
        recyclerView.setAdapter(adapter);

        btnThemTacVu.setOnClickListener(v -> {
            ThemTacVuActivity dialog = ThemTacVuActivity.newInstance();
            dialog.setOnTaskAddedListener(() -> {
                // Gọi lại hàm load danh sách khi có tác vụ mới
                loadDanhSach();
            });
            dialog.show(getSupportFragmentManager(), ThemTacVuActivity.TAG);
        });

        btnTroVe.setOnClickListener(v -> {
            startActivity(new Intent(TacVuActivity.this, MainActivity.class));
        });

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

                new AlertDialog.Builder(TacVuActivity.this)
                        .setTitle("Xác nhận")
                        .setMessage(Html.fromHtml("Bạn có chắc muốn xóa công việc <b><i>\"" + congViec.getTen() + "\"</i></b> hay không?"))
                        .setPositiveButton("Có", (dialog, which) -> {
                            // Xóa khỏi DB
                            DataBaseHelper dbHelper = new DataBaseHelper(TacVuActivity.this);
                            dbHelper.xoaCongViec(congViec.getId());

                            // Xóa khỏi danh sách và cập nhật adapter
                            danhSach.remove(position);
                            adapter.notifyItemRemoved(position);

                            // Kiểm tra danh sách trống để hiển thị layoutEmpty
                            if (danhSach.isEmpty()) {
                                layoutEmpty.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }
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

                Drawable icon = ContextCompat.getDrawable(TacVuActivity.this, R.drawable.ic_delete_item);
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
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDanhSach();
        }

    private void loadDanhSach() {
        DataBaseHelper dbHelper = new DataBaseHelper(this);
        int danhSachId = 0;
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

        if (danhSach.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setDanhSach(danhSach);
            adapter.notifyDataSetChanged();
        }
    }
}

