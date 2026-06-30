package com.xuongmay.dao;

import com.xuongmay.model.NhaCungCap;
import java.util.ArrayList;
import java.util.List;

public class NhaCungCapDAO {
    private static final List<NhaCungCap> list = new ArrayList<>();

    static {
        list.add(new NhaCungCap("NCC001", "Dệt may Thành Công", "0901234567", "12 Lũy Bán Bích, Tân Phú, HCM"));
        list.add(new NhaCungCap("NCC002", "Vải sỉ Tân Bình", "0987654321", "Lý Thường Kiệt, Tân Bình, HCM"));
        list.add(new NhaCungCap("NCC003", "Phong Phú Fabric", "0918273645", "KCN Tân Tạo, Bình Tân, HCM"));
    }

    public List<NhaCungCap> getAll() {
        return new ArrayList<>(list);
    }

    public NhaCungCap getById(String id) {
        return list.stream().filter(n -> n.getMaNhaCungCap().equals(id)).findFirst().orElse(null);
    }

    public void add(NhaCungCap ncc) {
        list.add(ncc);
    }

    public void update(NhaCungCap ncc) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMaNhaCungCap().equals(ncc.getMaNhaCungCap())) {
                list.set(i, ncc);
                return;
            }
        }
    }

    public void delete(String id) {
        list.removeIf(n -> n.getMaNhaCungCap().equals(id));
    }
}
