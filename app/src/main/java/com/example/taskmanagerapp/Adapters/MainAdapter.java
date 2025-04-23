package com.example.taskmanagerapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AlertDialog;

import com.example.taskmanagerapp.DataBase.DataBaseHelper;
import com.example.taskmanagerapp.Models.MainModelItem;
import com.example.taskmanagerapp.QuanTrongActivity;
import com.example.taskmanagerapp.R;
import com.example.taskmanagerapp.TacVuActivity;
import com.example.taskmanagerapp.ThemDanhSachActivity;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //khai báo các loại item
    private static final int TYPE_QUAN_TRONG = 0;
    private static final int TYPE_TAC_VU = 1;
    private static final int TYPE_DANH_SACH = 2;
    private static final int TYPE_DIVIDER = 3;


    private List<MainModelItem> list;
    private Context context;
    private DanhSachCallback callback;

    //interface dùng để tạo hàm reloadDanhSach()
    public interface DanhSachCallback {
        void reloadDanhSach();
    }

    public MainAdapter(Context context, List<MainModelItem> list, DanhSachCallback callback) {
        this.context = context;
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_QUAN_TRONG) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_quan_trong_main, parent, false);
            return new QuanTrongViewHolder(view);
        } else if (viewType == TYPE_TAC_VU) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_tac_vu_main, parent, false);
            return new TacVuViewHolder(view);
        } else if(viewType == TYPE_DANH_SACH){
            View view = LayoutInflater.from(context).inflate(R.layout.item_danh_sach_main, parent, false);
            return new DanhSachViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.item_divider, parent, false);
            return new DividerViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MainModelItem item = list.get(position);

        //set event gọi các activity
        switch (item.getViewType()) {
            case TYPE_TAC_VU:
                if (holder instanceof TacVuViewHolder) {
                    // Gọi database để lấy số lượng tác vụ chưa hoàn thành với danhSachId = 0
                    DataBaseHelper db = new DataBaseHelper(context);
                    int soLuongChuaHoanThanh = db.demTacVuChuaHoanThanhTheoDanhSach(0); // bạn phải tạo hàm này

                    if (soLuongChuaHoanThanh > 0) {
                        ((TacVuViewHolder) holder).txtSoLuongTacVu.setText(String.valueOf(soLuongChuaHoanThanh));
                    } else {
                        ((TacVuViewHolder) holder).txtSoLuongTacVu.setText(""); // để trống nếu không có
                    }
                    // set event click cho item
                    holder.itemView.setOnClickListener(v -> {
                        Intent intent = new Intent(context, TacVuActivity.class);
                        context.startActivity(intent);
                    });
                }
                break;

            case TYPE_QUAN_TRONG:
                if (holder instanceof QuanTrongViewHolder) {
                    DataBaseHelper db = new DataBaseHelper(context);
                    int soLuongChuaHoanThanh = db.demTacVuQuanTrongChuaHoanThanh();

                    if(soLuongChuaHoanThanh > 0){
                        ((QuanTrongViewHolder) holder).txtSoLuongTacVuQT.setText(String.valueOf(soLuongChuaHoanThanh));
                    }
                    else{
                        ((QuanTrongViewHolder) holder).txtSoLuongTacVuQT.setText("");
                    }
                    holder.itemView.setOnClickListener(v -> {
                        Intent intent = new Intent(context, QuanTrongActivity.class);
                        context.startActivity(intent);
                    });
                }
                break;

            //---------------chỗ này đã chỉnh sửa
            case TYPE_DANH_SACH:
                if (holder instanceof DanhSachViewHolder) {
                    DanhSachViewHolder dsHolder = (DanhSachViewHolder) holder;
                    dsHolder.txtTenDanhSach.setText(item.getTenDanhSach());

                    // Gọi database để lấy số lượng tác vụ chưa hoàn thành với danhSachId = 0
                    DataBaseHelper db = new DataBaseHelper(context);
                    int soLuongChuaHoanThanh = db.demTacVuChuaHoanThanhTheoDanhSach(item.getId()); // bạn phải tạo hàm này

                    if (soLuongChuaHoanThanh > 0) {
                        ((DanhSachViewHolder) holder).txtSoLuongTacVuTrongDS.setText(String.valueOf(soLuongChuaHoanThanh));
                    } else {
                        ((DanhSachViewHolder) holder).txtSoLuongTacVuTrongDS.setText(""); // để trống nếu không có
                    }

                    //Sự kiện click vào item
                    dsHolder.itemView.setOnClickListener(v -> {
                        Intent intent = new Intent(context, ThemDanhSachActivity.class);
                        intent.putExtra("id",item.getId());
                        intent.putExtra("tenDanhSach", item.getTenDanhSach());
                        context.startActivity(intent);
                    });

                    //Sự kiện khi giữ item
                    dsHolder.itemView.setOnLongClickListener(view -> {
                        PopupMenu popup = new PopupMenu(context, view);
                        popup.getMenuInflater().inflate(R.menu.menu_danh_sach, popup.getMenu());
                        popup.setOnMenuItemClickListener(menuItem -> {
                            int itemId = menuItem.getItemId();
                            if (itemId == R.id.menu_sua) {
                                showDialogSua(item.getId(), item.getTenDanhSach());
                                return true;
                            } else if (itemId == R.id.menu_xoa) {
                                showDialogXoa(item.getId());
                                return true;
                            } else {
                                return false;
                            }
                        });
                        popup.show();
                        return true;
                    });
                }
                break;
            //----------------
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getViewType();
    }

    class TacVuViewHolder extends RecyclerView.ViewHolder {
        TextView txtSoLuongTacVu;
        public TacVuViewHolder(View itemView) {
            super(itemView);
            txtSoLuongTacVu = itemView.findViewById(R.id.txtSoLuongTacVu);
        }
    }

    class QuanTrongViewHolder extends RecyclerView.ViewHolder {
        TextView txtSoLuongTacVuQT;
        public QuanTrongViewHolder(View itemView) {
            super(itemView);
            txtSoLuongTacVuQT = itemView.findViewById(R.id.txtSoLuongTacVuQT);
        }
    }

    class DividerViewHolder extends RecyclerView.ViewHolder {
        public DividerViewHolder(View itemView) {
            super(itemView);
        }
    }

    class DanhSachViewHolder extends RecyclerView.ViewHolder {
        TextView txtTenDanhSach;
        TextView txtSoLuongTacVuTrongDS;

        public DanhSachViewHolder(View itemView) {
            super(itemView);
            txtTenDanhSach = itemView.findViewById(R.id.txtListTitle);
            txtSoLuongTacVuTrongDS = itemView.findViewById(R.id.txtSoLuongTacVuDS);
        }
    }

    //Show dialog sửa tên
    private void showDialogSua(int id, String tenCu) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Sửa tên danh sách");

        final EditText input = new EditText(context);
        input.setText(tenCu);
        builder.setView(input);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String tenMoi = input.getText().toString().trim();
            if (!tenMoi.isEmpty()) {
                DataBaseHelper db = new DataBaseHelper(context);
                String tenKhongTrung = db.generateUniqueTenDanhSach(tenMoi);
                db.suaTenDanhSach(id, tenKhongTrung);
                if (callback != null) {
                    callback.reloadDanhSach();
                }
                Toast.makeText(context,"Sửa tên danh sách thành công",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(context,"Tên danh sách không được để trống!",Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    //dialog xác nhận xóa
    private void showDialogXoa(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc muốn xóa danh sách này?");

        builder.setPositiveButton("Xóa", (dialog, which) -> {
            DataBaseHelper db = new DataBaseHelper(context);
            db.xoaDanhSach(id);
            if (callback != null) {
                callback.reloadDanhSach();
            }
            Toast.makeText(context,"Xóa danh sách thành công",Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

}
