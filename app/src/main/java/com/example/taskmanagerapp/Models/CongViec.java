package com.example.taskmanagerapp.Models;

public class CongViec {
    private int id;
    private String ten;
    private String ngayNhac;
    private String ngayDenHan;
    private String ghiChu;
    private int trangThai; // 0 = chưa hoàn thành, 1 = hoàn thành
    private int loai;      // 0 = bình thường, 1 = quan trọng
    private int danhSachId;

    public CongViec(int id, String ten, String ngayNhac, String ngayDenHan, String ghiChu, int trangThai, int loai, int danhSachId) {
        this.id = id;
        this.ten = ten;
        this.ngayNhac = ngayNhac;
        this.ngayDenHan = ngayDenHan;
        this.ghiChu = ghiChu;
        this.trangThai = trangThai;
        this.loai = loai;
        this.danhSachId = danhSachId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getNgayNhac() {
        return ngayNhac;
    }

    public void setNgayNhac(String ngayNhac) {
        this.ngayNhac = ngayNhac;
    }

    public String getNgayDenHan() {
        return ngayDenHan;
    }

    public void setNgayDenHan(String ngayDenHan) {
        this.ngayDenHan = ngayDenHan;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    public int getLoai() {
        return loai;
    }

    public void setLoai(int loai) {
        this.loai = loai;
    }

    public int getDanhSachId() {
        return danhSachId;
    }

    public void setDanhSachId(int danhSachId) {
        this.danhSachId = danhSachId;
    }
}
