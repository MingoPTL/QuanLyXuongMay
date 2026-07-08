package com.xuongmay.service;

import com.xuongmay.dao.NhaCungCapDAO;
import com.xuongmay.dao.LoVaiDAO;
import com.xuongmay.dao.CayVaiDAO;
import com.xuongmay.model.NhaCungCap;
import com.xuongmay.model.LoVai;
import com.xuongmay.model.CayVai;
import com.xuongmay.model.TrangThaiLoVai;
import java.util.List;
import java.util.stream.Collectors;

public class NguyenLieuService {
    private final NhaCungCapDAO nccDao = new NhaCungCapDAO();
    private final LoVaiDAO loDao = new LoVaiDAO();
    private final CayVaiDAO cvDao = new CayVaiDAO();

    // Supplier operations
    public List<NhaCungCap> getAllNhaCungCap() {
        return nccDao.getAll();
    }

    public NhaCungCap getNhaCungCapById(String id) {
        return nccDao.getById(id);
    }

    public void addNhaCungCap(NhaCungCap ncc) {
        nccDao.add(ncc);
    }

    public void updateNhaCungCap(NhaCungCap ncc) {
        nccDao.update(ncc);
    }

    public void deleteNhaCungCap(String id) {
        nccDao.delete(id);
    }

    // Fabric Lot operations
    public List<LoVai> getAllLoVai() {
        return loDao.getAll();
    }

    // New requirement: Show only uncut fabric lots (TrangThaiLoVai = ChuaSuDung)
    public List<LoVai> getLoVaiChuaCat() {
        return loDao.getAll().stream()
                .filter(l -> l.getTrangThaiLoVai() == TrangThaiLoVai.ChuaSuDung)
                .collect(Collectors.toList());
    }

    public LoVai getLoVaiById(String id) {
        return loDao.getById(id);
    }

    public void addLoVai(LoVai lo) {
        loDao.add(lo);
    }

    public void updateLoVai(LoVai lo) {
        loDao.update(lo);
    }

    public void deleteLoVai(String id) {
        // Unassign all CayVais belonging to this lot and reset their layers to 0
        List<CayVai> rolls = cvDao.getByLoVaiId(id);
        for (CayVai cv : rolls) {
            cv.setLoVai(null);
            cv.setLuotTraiVai(0);
            cvDao.update(cv);
        }
        loDao.delete(id);
    }

    // Fabric Roll operations
    public List<CayVai> getAllCayVai() {
        return cvDao.getAll();
    }

    public List<CayVai> getCayVaiByLoVaiId(String loVaiId) {
        return cvDao.getByLoVaiId(loVaiId);
    }

    public void addCayVai(CayVai cv) {
        cvDao.add(cv);
    }

    public void updateCayVai(CayVai cv) {
        cvDao.update(cv);
    }

    public void deleteCayVai(String id) {
        cvDao.delete(id);
    }
}
