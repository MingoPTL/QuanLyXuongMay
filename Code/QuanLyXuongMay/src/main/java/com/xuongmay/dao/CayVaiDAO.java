package com.xuongmay.dao;

import com.xuongmay.model.CayVai;
import com.xuongmay.model.LoVai;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CayVaiDAO {
    private static final List<CayVai> list = new ArrayList<>();

    static {
        LoVaiDAO loDao = new LoVaiDAO();
        List<LoVai> los = loDao.getAll();
        if (los.size() >= 3) {
            // Lô 1 (LV001)
            list.add(new CayVai("CV001", "Đen", los.get(0), 100.5, "Kệ A1", "Kaki trơn màu đen", 2));
            list.add(new CayVai("CV002", "Xanh đen", los.get(0), 120.0, "Kệ A1", "Kaki trơn màu xanh đen", 1));
            list.add(new CayVai("CV003", "Xám", los.get(0), 95.2, "Kệ A2", "Kaki trơn màu xám", 3));

            // Lô 2 (LV002)
            list.add(new CayVai("CV004", "Trắng", los.get(1), 150.0, "Kệ B1", "Cotton 4 chiều màu trắng", 0));
            list.add(new CayVai("CV005", "Đỏ đô", los.get(1), 140.5, "Kệ B2", "Cotton 4 chiều màu đỏ đô", 0));
            list.add(new CayVai("CV006", "Vàng", los.get(1), 130.0, "Kệ B2", "Cotton 4 chiều màu vàng", 0));
            list.add(new CayVai("CV007", "Xanh lá", los.get(1), 145.8, "Kệ B3", "Cotton 4 chiều màu xanh lá", 0));

            // Lô 3 (LV003)
            list.add(new CayVai("CV008", "Be", los.get(2), 80.0, "Kệ C1", "Linen hoa nhí màu be", 0));
            list.add(new CayVai("CV009", "Xanh dương", los.get(2), 85.5, "Kệ C2", "Linen kẻ sọc xanh dương", 0));

            // Lô 4 (LV004)
            list.add(new CayVai("CV010", "Xanh Jean", los.get(3), 200.0, "Kệ D1", "Jean xanh co giãn", 5));
        }
    }

    public List<CayVai> getAll() {
        return new ArrayList<>(list);
    }

    public List<CayVai> getByLoVaiId(String loVaiId) {
        return list.stream()
                .filter(cv -> cv.getLoVai() != null && cv.getLoVai().getMaLo().equals(loVaiId))
                .collect(Collectors.toList());
    }

    public CayVai getById(String id) {
        return list.stream().filter(c -> c.getTenCayVai().equals(id)).findFirst().orElse(null);
    }

    public void add(CayVai cv) {
        list.add(cv);
    }

    public void update(CayVai cv) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getTenCayVai().equals(cv.getTenCayVai())) {
                list.set(i, cv);
                return;
            }
        }
    }

    public void delete(String id) {
        list.removeIf(c -> c.getTenCayVai().equals(id));
    }
}
