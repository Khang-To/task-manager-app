package com.example.taskmanagerapp.Models;

public class DanhSachCongViec {
    private int id;
    private String tenDanhSach;

    public DanhSachCongViec(int id, String tenDanhSach) {
        this.id = id;
        this.tenDanhSach = tenDanhSach;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTenDanhSach() {
        return tenDanhSach;
    }

    public void setTenDanhSach(String tenDanhSach) {
        this.tenDanhSach = tenDanhSach;
    }
}
