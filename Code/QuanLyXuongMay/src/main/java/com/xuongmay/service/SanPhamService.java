package com.xuongmay.service;

import com.xuongmay.dao.SanPhamDAO;
import com.xuongmay.dao.LoaiSanPhamDAO;
import com.xuongmay.model.SanPham;
import com.xuongmay.model.LoaiSanPham;
import java.util.List;

public class SanPhamService {
    private final SanPhamDAO spDao = new SanPhamDAO();
    private final LoaiSanPhamDAO lspDao = new LoaiSanPhamDAO();

    public List<SanPham> getAllSanPham() {
        return spDao.getAll();
    }

    public SanPham getSanPhamById(String id) {
        return spDao.getById(id);
    }

    public void addSanPham(SanPham sp) {
        spDao.add(sp);
    }

    public void updateSanPham(SanPham sp) {
        spDao.update(sp);
    }

    public void deleteSanPham(String id) {
        spDao.delete(id);
    }

    // Product types
    public List<LoaiSanPham> getAllLoaiSanPham() {
        return lspDao.getAll();
    }

    public void addLoaiSanPham(LoaiSanPham lsp) {
        lspDao.add(lsp);
    }

    public void updateLoaiSanPham(LoaiSanPham lsp) {
        lspDao.update(lsp);
    }
}
