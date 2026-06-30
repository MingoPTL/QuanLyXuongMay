package com.xuongmay.dao;

import com.xuongmay.model.LoVai;
import com.xuongmay.model.NhaCungCap;
import com.xuongmay.model.TrangThaiLoVai;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoVaiDAO {
    private static final List<LoVai> list = new ArrayList<>();

    static {
        NhaCungCapDAO nccDao = new NhaCungCapDAO();
        List<NhaCungCap> nccs = nccDao.getAll();
        if (!nccs.isEmpty()) {
            list.add(new LoVai("LV001", "Lô Kaki Thun Hàn Quốc", nccs.get(0), LocalDate.now().minusDays(10), 200, "Kaki", 50000, "Vải nhập khẩu", TrangThaiLoVai.DangSuDung));
            list.add(new LoVai("LV002", "Lô Thun Cotton 4 Chiều", nccs.get(1), LocalDate.now().minusDays(5), 350, "Thun Cotton", 35000, "Mềm mịn co giãn", TrangThaiLoVai.ChuaSuDung));
            list.add(new LoVai("LV003", "Lô Linen Hoa Tiết", nccs.get(2), LocalDate.now().minusDays(2), 150, "Linen", 60000, "Vải linen mát mẻ", TrangThaiLoVai.ChuaSuDung));
            list.add(new LoVai("LV004", "Lô Jean Thun Dày", nccs.get(0), LocalDate.now().minusDays(12), 400, "Jean", 70000, "Jean chất lượng cao", TrangThaiLoVai.RaSanPham));
        }
    }

    public List<LoVai> getAll() {
        return new ArrayList<>(list);
    }

    public LoVai getById(String id) {
        return list.stream().filter(l -> l.getMaLo().equals(id)).findFirst().orElse(null);
    }

    public void add(LoVai lo) {
        list.add(lo);
    }

    public void update(LoVai lo) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMaLo().equals(lo.getMaLo())) {
                list.set(i, lo);
                return;
            }
        }
    }

    public void delete(String id) {
        list.removeIf(l -> l.getMaLo().equals(id));
    }
}
