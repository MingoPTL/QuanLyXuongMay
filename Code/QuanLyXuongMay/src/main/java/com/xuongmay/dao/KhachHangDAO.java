package com.xuongmay.dao;

import com.xuongmay.model.KhachHang;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO {
    private static final List<KhachHang> list = new ArrayList<>();

    static {
        list.add(new KhachHang("KH001", "Nguyễn Văn An", "0909000111", "12 Lũy Bán Bích, Tân Phú, HCM", "0314567890", "Khách sỉ lâu năm"));
        list.add(new KhachHang("KH002", "Trần Thị Bình", "0909000222", "456 CMT8, Quận 3, HCM", "0314567891", "Thanh toán đúng hạn"));
        list.add(new KhachHang("KH003", "Lê Hoàng Long", "0909000333", "789 Quang Trung, Gò Vấp, HCM", "0314567892", "Khách hàng VIP"));
    }

    public List<KhachHang> getAll() {
        return new ArrayList<>(list);
    }

    public KhachHang getById(String id) {
        return list.stream().filter(k -> k.getMaKhachHang().equals(id)).findFirst().orElse(null);
    }

    public void add(KhachHang kh) {
        list.add(kh);
    }

    public void update(KhachHang kh) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMaKhachHang().equals(kh.getMaKhachHang())) {
                list.set(i, kh);
                return;
            }
        }
    }

    public void delete(String id) {
        list.removeIf(k -> k.getMaKhachHang().equals(id));
    }
}
