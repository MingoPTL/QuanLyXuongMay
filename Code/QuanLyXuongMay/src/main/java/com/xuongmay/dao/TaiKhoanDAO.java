package com.xuongmay.dao;

import com.xuongmay.model.ChucVu;
import com.xuongmay.model.TaiKhoan;
import com.xuongmay.model.TrangThaiTaiKhoan;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaiKhoanDAO {
    private static final List<TaiKhoan> list = new ArrayList<>();

    static {
        list.add(new TaiKhoan("TK001", "admin", "admin123", ChucVu.ADMIN, TrangThaiTaiKhoan.HoatDong, LocalDate.now().minusMonths(6)));
        list.add(new TaiKhoan("TK002", "quanly1", "ql123", ChucVu.QUANLY, TrangThaiTaiKhoan.HoatDong, LocalDate.now().minusMonths(3)));
        list.add(new TaiKhoan("TK003", "quanly2", "ql123", ChucVu.QUANLY, TrangThaiTaiKhoan.NgungHoatDong, LocalDate.now().minusMonths(1)));
    }

    public List<TaiKhoan> getAll() {
        return new ArrayList<>(list);
    }

    public TaiKhoan getById(String id) {
        return list.stream().filter(t -> t.getMaTaiKhoan().equals(id)).findFirst().orElse(null);
    }

    public void add(TaiKhoan tk) {
        list.add(tk);
    }

    public void update(TaiKhoan tk) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMaTaiKhoan().equals(tk.getMaTaiKhoan())) {
                list.set(i, tk);
                return;
            }
        }
    }

    public void delete(String id) {
        list.removeIf(t -> t.getMaTaiKhoan().equals(id));
    }
}
