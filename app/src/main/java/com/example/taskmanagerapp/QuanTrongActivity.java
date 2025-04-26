package com.example.taskmanagerapp;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

        dataBaseHelper = new DataBaseHelper(this);

        recyclerView = findViewById(R.id.recyclerView);
        btnTroVe = findViewById(R.id.btnTroVe);
        imageView = findViewById(R.id.imageView);
        textView2 = findViewById(R.id.textView2);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        importantCongViecList = dataBaseHelper.getCongViecQuanTrong();
        adapter = new TacVuAdapter(this, importantCongViecList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new TacVuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CongViec cv) {
                Intent intent = new Intent(QuanTrongActivity.this, ChiTietTacVuActivity.class);
                intent.putExtra("id", cv.getId());
                intent.putExtra("ten", cv.getTen());
                intent.putExtra("trangThai", cv.getTrangThai());
                intent.putExtra("loai", cv.getLoai());
                intent.putExtra("source", "QuanTrongActivity");
                startActivity(intent);
            }
        });

        btnTroVe.setOnClickListener(v -> {
            Intent intent = new Intent(QuanTrongActivity.this, MainActivity.class);
            startActivity(intent);
        });

        congViecChangedReceiver = new CongViecChangedReceiver();

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

        checkEmpty();

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
                CongViec congViec = importantCongViecList.get(position);

                new AlertDialog.Builder(QuanTrongActivity.this)
                        .setTitle("Xác nhận")
                        .setMessage(Html.fromHtml("Bạn có chắc muốn xóa công việc <b><i>\"" + congViec.getTen() + "\"</i></b> hay không?"))
                        .setPositiveButton("Có", (dialog, which) -> {
                            DataBaseHelper dbHelper = new DataBaseHelper(QuanTrongActivity.this);
                            dbHelper.xoaCongViec(congViec.getId());

                            importantCongViecList.remove(position);
                            adapter.notifyItemRemoved(position);

                            checkEmpty();
                        })
                        .setNegativeButton("Không", (dialog, which) -> {
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

                Drawable icon = ContextCompat.getDrawable(QuanTrongActivity.this, R.drawable.ic_delete_item);
                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;

                if (dX < 0) {
                    c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                            (float) itemView.getRight(), (float) itemView.getBottom(), paint);

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
        // <<< Kết thúc vuốt để xóa
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onResume() {
        super.onResume();
        loadDanhSachCongViecQuanTrong();
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
        unregisterReceiver(congViecChangedReceiver);
    }

    private class CongViecChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadDanhSachCongViecQuanTrong();
        }
    }

    private void loadDanhSachCongViecQuanTrong() {
        DataBaseHelper dbHelper = new DataBaseHelper(this);
        importantCongViecList = dbHelper.getCongViecQuanTrong();
        adapter.setDanhSach(importantCongViecList);
        adapter.notifyDataSetChanged();
    }

    private void checkEmpty() {
        if (adapter.getItemCount() == 0) {
            imageView.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
            textView2.setVisibility(View.GONE);
        }
    }
}
