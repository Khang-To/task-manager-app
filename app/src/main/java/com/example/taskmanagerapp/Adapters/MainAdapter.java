package com.example.taskmanagerapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanagerapp.Models.MainModelItem;
import com.example.taskmanagerapp.R;
import com.example.taskmanagerapp.TacVuActivity;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_QUAN_TRONG = 0;
    private static final int TYPE_TAC_VU = 1;
    private static final int TYPE_DANH_SACH = 2;
    private static final int TYPE_DIVIDER = 3;


    private List<MainModelItem> list;
    private Context context;

    public MainAdapter(Context context, List<MainModelItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_QUAN_TRONG) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_quan_trong_main, parent, false);
            return new TacVuViewHolder(view);
        } else if (viewType == TYPE_TAC_VU) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_tac_vu_main, parent, false);
            return new QuanTrongViewHolder(view);
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
        if (holder instanceof DanhSachViewHolder) {
            ((DanhSachViewHolder) holder).txtTenDanhSach.setText(item.getTenDanhSach());
        }

//        //sự kiện khi click vào các item
        switch (item.getViewType()) {
            case TYPE_TAC_VU:
                TacVuViewHolder tacVuHolder = (TacVuViewHolder) holder;
                tacVuHolder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, TacVuActivity.class);
                    context.startActivity(intent);
                });
                break;
//
//            case TYPE_QUAN_TRONG:
//                QuanTrongViewHolder qtHolder = (QuanTrongViewHolder) holder;
//                qtHolder.itemView.setOnClickListener(v -> {
//                    Intent intent = new Intent(context, QuanTrongActivity.class);
//                    context.startActivity(intent);
//                });
//                break;
//
//            case TYPE_DANH_SACH:
//                DanhSachViewHolder dsHolder = (DanhSachViewHolder) holder;
//                dsHolder.txtListTitle.setText(item.getTenDanhSach());
//               break;
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
        public TacVuViewHolder(View itemView) {
            super(itemView);
        }
    }

    class QuanTrongViewHolder extends RecyclerView.ViewHolder {
        public QuanTrongViewHolder(View itemView) {
            super(itemView);
        }
    }

    class DividerViewHolder extends RecyclerView.ViewHolder {
        public DividerViewHolder(View itemView) {
            super(itemView);
        }
    }

    class DanhSachViewHolder extends RecyclerView.ViewHolder {
        TextView txtTenDanhSach;

        public DanhSachViewHolder(View itemView) {
            super(itemView);
            txtTenDanhSach = itemView.findViewById(R.id.txtListTitle);
        }
    }
}
