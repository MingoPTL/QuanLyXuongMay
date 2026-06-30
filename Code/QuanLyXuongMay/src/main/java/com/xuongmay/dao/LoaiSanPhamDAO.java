package com.xuongmay.dao;

import com.xuongmay.model.LoaiSanPham;
import java.util.ArrayList;
import java.util.List;

public class LoaiSanPhamDAO {
    private static final List<LoaiSanPham> list = new ArrayList<>();

    static {
        list.add(new LoaiSanPham("LSP001", "Áo thun Polo", "Áo thun Polo cổ bẻ", 35000, "Vải Cotton 4 chiều"));
        list.add(new LoaiSanPham("LSP002", "Quần Kaki Nam", "Quần dài kaki dáng ôm", 55000, "Vải Kaki Thun"));
        list.add(new LoaiSanPham("LSP003", "Đầm Linen Nữ", "Đầm chữ A họa tiết hoa nhí", 70000, "Vải Linen"));
    }

    public List<LoaiSanPham> getAll() {
        return new ArrayList<>(list);
    }

    public LoaiSanPham getById(String id) {
        return list.stream().filter(l -> l.getMaLoai().equals(id)).findFirst().orElse(null);
    }

    public void add(LoaiSanPham lsp) {
        list.add(lsp);
    }

    public void update(LoaiSanPham lsp) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMaLoai().equals(lsp.getMaLoai())) {
                list.set(i, lsp);
                return;
            }
        }
    }

    public void delete(String id) {
        list.removeIf(l -> l.getMaLoai().equals(id));
    }
}
