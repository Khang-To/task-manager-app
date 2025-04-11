package com.example.taskmanagerapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanagerapp.Models.CongViec;
import com.example.taskmanagerapp.R;
import com.example.taskmanagerapp.DataBase.DataBaseHelper;


import java.util.Collections;
import java.util.List;

public class TacVuAdapter extends  RecyclerView.Adapter<TacVuAdapter.TacVuViewHolder>{
    private Context context;
    private DataBaseHelper dbHelper;

    private List<CongViec> list;

    public TacVuAdapter(Context context, List<CongViec> list) {
        this.context = context;
        this.list = list;
        this.dbHelper = new DataBaseHelper(context); // Khởi tạo DB
    }

    @NonNull
    @Override
    public TacVuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tac_vu, parent, false);
        return new TacVuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TacVuViewHolder holder, int position) {
        CongViec cv = list.get(position);

        // Tạm thời gỡ bỏ listener để tránh vòng lặp
        holder.checkBox.setOnCheckedChangeListener(null);

        holder.checkBox.setText(cv.getTen());
        holder.checkBox.setChecked(cv.getTrangThai() == 1);

        // Gạch và mờ nếu đã hoàn thành
        if (cv.getTrangThai() == 1) {
            holder.checkBox.setPaintFlags(holder.checkBox.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            holder.checkBox.setAlpha(0.5f);
        } else {
            holder.checkBox.setPaintFlags(holder.checkBox.getPaintFlags() & (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG));
            holder.checkBox.setAlpha(1f);
        }

        holder.imageStar.setImageResource(cv.getLoai() == 1 ? R.drawable.ic_star1 : R.drawable.ic_star2);

        // Đặt lại listener sau khi setChecked xong
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cv.setTrangThai(isChecked ? 1 : 0);

            // Cập nhật database
            dbHelper.capNhatTrangThai(cv.getId(), cv.getTrangThai());

            // Sắp xếp lại danh sách: an toàn nhất là gán lại list và gọi notifyDataSetChanged
            sapXepLaiDanhSach();
        });

        holder.imageStar.setOnClickListener(v -> {
            cv.setLoai(cv.getLoai() == 1 ? 0 : 1);
            notifyItemChanged(holder.getAdapterPosition());

            // TODO: nếu muốn lưu vào DB thì gọi dbHelper...
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void setDanhSach(List<CongViec> danhSach) {
        this.list = danhSach;
    }

    public static class TacVuViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        ImageButton imageStar;

        public TacVuViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBoxTrangThai);
            imageStar = itemView.findViewById(R.id.imageStar);
        }
    }

    private void sapXepLaiDanhSach() {
        Collections.sort(list, (a, b) -> Integer.compare(a.getTrangThai(), b.getTrangThai()));
        notifyDataSetChanged(); // dùng cái này để tránh crash
    }

}
