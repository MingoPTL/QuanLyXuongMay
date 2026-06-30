package com.xuongmay.service;

import com.xuongmay.dao.NhanVienDAO;
import com.xuongmay.dao.PhanCongSanPhamDAO;
import com.xuongmay.dao.SanPhamDAO;
import com.xuongmay.model.NhanVien;
import com.xuongmay.model.PhanCongSanPham;
import com.xuongmay.model.SanPham;
import com.xuongmay.model.ChuyenMon;
import java.util.List;
import java.util.stream.Collectors;

public class SanXuatService {
    private final NhanVienDAO nvDao = new NhanVienDAO();
    private final PhanCongSanPhamDAO pcDao = new PhanCongSanPhamDAO();
    private final SanPhamDAO spDao = new SanPhamDAO();

    // Employee operations
    public List<NhanVien> getAllNhanVien() {
        return nvDao.getAll();
    }

    public List<NhanVien> getNhanVienByChuyenMon(ChuyenMon cm) {
        return nvDao.getAll().stream()
                .filter(n -> n.getChuyenMon() == cm)
                .collect(Collectors.toList());
    }

    public NhanVien getNhanVienById(String id) {
        return nvDao.getById(id);
    }

    public void addNhanVien(NhanVien nv) {
        nvDao.add(nv);
    }

    public void updateNhanVien(NhanVien nv) {
        nvDao.update(nv);
    }

    public void deleteNhanVien(String id) {
        nvDao.delete(id);
    }

    // Assignment operations
    public List<PhanCongSanPham> getAllPhanCong() {
        return pcDao.getAll();
    }

    public void addPhanCong(PhanCongSanPham pc) {
        pcDao.add(pc);
    }

    public void updatePhanCong(PhanCongSanPham pc) {
        pcDao.update(pc);
    }

    public void deletePhanCong(String id) {
        pcDao.delete(id);
    }
}
