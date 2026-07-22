package com.xuongmay.model;

public class CayVai {
    private Long   idCayVai; // IDENTITY PK từ DB (null khi tạo mới)
    private String tenCayVai;
    private String mauSac;
    private LoVai loVai;
    private double chieuDai;
    private String viTri;
    private String ghiChu;
    private int luotTraiVai;

    public CayVai() {}

    public CayVai(String tenCayVai, String mauSac, LoVai loVai, double chieuDai, String viTri, String ghiChu, int luotTraiVai) {
        this.tenCayVai = tenCayVai;
        this.mauSac = mauSac;
        this.loVai = loVai;
        this.chieuDai = chieuDai;
        this.viTri = viTri;
        this.ghiChu = ghiChu;
        this.luotTraiVai = luotTraiVai;
    }

    public Long getIdCayVai() { return idCayVai; }
    public void setIdCayVai(Long idCayVai) { this.idCayVai = idCayVai; }

    public String getTenCayVai() {
        return tenCayVai;
    }

    public void setTenCayVai(String tenCayVai) {
        this.tenCayVai = tenCayVai;
    }

    public String getMauSac() {
        return mauSac;
    }

    public void setMauSac(String mauSac) {
        this.mauSac = mauSac;
    }

    public LoVai getLoVai() {
        return loVai;
    }

    public void setLoVai(LoVai loVai) {
        this.loVai = loVai;
    }

    public double getChieuDai() {
        return chieuDai;
    }

    public void setChieuDai(double chieuDai) {
        this.chieuDai = chieuDai;
    }

    public String getViTri() {
        return viTri;
    }

    public void setViTri(String viTri) {
        this.viTri = viTri;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public int getLuotTraiVai() {
        return luotTraiVai;
    }

    public void setLuotTraiVai(int luotTraiVai) {
        this.luotTraiVai = luotTraiVai;
    }

    @Override
    public String toString() {
        return tenCayVai;
    }
}
