package com.example.taskmanagerapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanagerapp.ChiTietTacVuActivity;
import com.example.taskmanagerapp.DataBase.DataBaseHelper;
import com.example.taskmanagerapp.Models.CongViec;
import com.example.taskmanagerapp.R;

import java.util.Collections;
import java.util.List;

public class TacVuAdapter extends RecyclerView.Adapter<TacVuAdapter.TacVuViewHolder> {

    private Context context;
    private List<CongViec> list;
    private DataBaseHelper dbHelper;
    private OnItemClickListener listener;

    public TacVuAdapter(Context context, List<CongViec> list) {
        this.context = context;
        this.list = list;
        this.dbHelper = new DataBaseHelper(context);
    }

    // Interface để xử lý sự kiện click vào item
    public interface OnItemClickListener {
        void onItemClick(CongViec cv);
    }

    // Thiết lập listener cho sự kiện click
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
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

        // Hiển thị tên công việc
        holder.tvTenTacVu.setText(cv.getTen());

        // Xử lý trạng thái hoàn thành/chưa hoàn thành
        if (cv.getTrangThai() == 1) {
            holder.tvTenTacVu.setPaintFlags(holder.tvTenTacVu.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvTenTacVu.setAlpha(0.5f);
            holder.imageCheckBox.setImageResource(R.drawable.ic_checked_radio); // icon đã hoàn thành
        } else {
            holder.tvTenTacVu.setPaintFlags(holder.tvTenTacVu.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.tvTenTacVu.setAlpha(1f);
            holder.imageCheckBox.setImageResource(R.drawable.ic_uncheck_radio); // icon chưa hoàn thành
        }

        // Gán icon sao (quan trọng/không quan trọng)
        holder.imageStar.setImageResource(cv.getLoai() == 1 ? R.drawable.ic_star1 : R.drawable.ic_star2);

        // Bắt sự kiện tick hoàn thành
        holder.imageCheckBox.setOnClickListener(v -> {
            int newTrangThai = (cv.getTrangThai() == 1) ? 0 : 1;
            cv.setTrangThai(newTrangThai);
            dbHelper.capNhatTrangThai(cv.getId(), newTrangThai);
            sapXepLaiDanhSach();
        });

        // Bắt sự kiện sao
        holder.imageStar.setOnClickListener(v -> {
            int newLoai = (cv.getLoai() == 1) ? 0 : 1;
            cv.setLoai(newLoai);
            dbHelper.capNhatLoai(cv.getId(), newLoai);
            Intent intent = new Intent("com.example.taskmanagerapp.CONG_VIEC_CHANGED");
            context.sendBroadcast(intent);
            notifyItemChanged(holder.getAdapterPosition());
        });

        // Bắt sự kiện click vào item
        holder.itemView.setOnClickListener(v -> {
            // Gọi listener nếu có
            if (listener != null) {
                listener.onItemClick(cv);
            } else {
                // Tạo Intent để chuyển sang ChiTietTacVuActivity
                Intent intent = new Intent(context, ChiTietTacVuActivity.class);
                // Truyền dữ liệu của công việc qua Intent
                intent.putExtra("id", cv.getId());
                intent.putExtra("ten", cv.getTen());
                intent.putExtra("trangThai", cv.getTrangThai());
                intent.putExtra("loai", cv.getLoai());
                intent.putExtra("danhSachId", cv.getDanhSachId());
                // Truyền thông tin về nguồn gốc
                intent.putExtra("source", "TacVuActivity");
                // Khởi chạy ChiTietTacVuActivity
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // Cập nhật danh sách công việc
    public void setDanhSach(List<CongViec> danhSach) {
        this.list = danhSach;
        notifyDataSetChanged();
    }

    // Sắp xếp lại danh sách theo trạng thái và độ quan trọng
    private void sapXepLaiDanhSach() {
        Collections.sort(list, (cv1, cv2) -> {
            // Ưu tiên công việc chưa hoàn thành
            if (cv1.getTrangThai() != cv2.getTrangThai()) {
                return cv1.getTrangThai() - cv2.getTrangThai();
            }
            // Ưu tiên công việc quan trọng
            return cv2.getLoai() - cv1.getLoai();
        });
        notifyDataSetChanged();
    }

    // ViewHolder để giữ các View của mỗi item
    public static class TacVuViewHolder extends RecyclerView.ViewHolder {
        ImageButton imageCheckBox, imageStar;
        TextView tvTenTacVu;

        public TacVuViewHolder(@NonNull View itemView) {
            super(itemView);
            imageCheckBox = itemView.findViewById(R.id.checkBoxTrangThai);
            imageStar = itemView.findViewById(R.id.imageStar);
            tvTenTacVu = itemView.findViewById(R.id.tvTenTacVu);
        }
    }


}