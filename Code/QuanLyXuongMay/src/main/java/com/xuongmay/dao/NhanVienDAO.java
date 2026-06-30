package com.xuongmay.dao;

import com.xuongmay.model.ChuyenMon;
import com.xuongmay.model.NhanVien;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {
    private static final List<NhanVien> list = new ArrayList<>();

    static {
        list.add(new NhanVien("NV001", "Nguyễn Thị Hoa", "0909111222", ChuyenMon.ThoMay, "Kinh nghiệm 5 năm"));
        list.add(new NhanVien("NV002", "Trần Văn Hùng", "0909333444", ChuyenMon.ThoMay, "May nhanh, tay nghề cao"));
        list.add(new NhanVien("NV003", "Lê Thị Mai", "0909555666", ChuyenMon.ThoMay, "Chuyên may viền"));
        list.add(new NhanVien("NV004", "Phạm Văn Bình", "0909777888", ChuyenMon.ThoUi, "Ủi phẳng, cẩn thận"));
        list.add(new NhanVien("NV005", "Nguyễn Thị Lan", "0909999000", ChuyenMon.ThoUi, "Ủi nhanh, đúng tiến độ"));
    }

    public List<NhanVien> getAll() {
        return new ArrayList<>(list);
    }

    public NhanVien getById(String id) {
        return list.stream().filter(n -> n.getMaNhanVien().equals(id)).findFirst().orElse(null);
    }

    public void add(NhanVien nv) {
        list.add(nv);
    }

    public void update(NhanVien nv) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMaNhanVien().equals(nv.getMaNhanVien())) {
                list.set(i, nv);
                return;
            }
        }
    }

    public void delete(String id) {
        list.removeIf(n -> n.getMaNhanVien().equals(id));
    }
}
