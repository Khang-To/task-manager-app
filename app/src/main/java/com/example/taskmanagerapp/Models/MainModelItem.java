package com.example.taskmanagerapp.Models;

public class MainModelItem {
    private int viewType; // 0 = Quan trọng, 1 = Tác vụ, 2 = Danh sách từ DB, 3 = divider
    private int id; // id chỉ dùng cho viewType = 2
    private String tenDanhSach; // chỉ dùng khi viewType = 2

    public MainModelItem(int viewType, int id, String tenDanhSach) {
        this.viewType = viewType;
        this.id = id;
        this.tenDanhSach = tenDanhSach;
    }

    public int getViewType() {
        return viewType;
    }

    public int getId() {
        return id;
    }

    public String getTenDanhSach() {
        return tenDanhSach;
    }
}
