package com.presensi.app.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Ent_Presensi {

    @SerializedName("time")
    @Expose
    private String time;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("hari")
    @Expose
    private String hari;

    @SerializedName("ket_presence")
    @Expose
    private String ket_presence;

    @SerializedName("nip")
    @Expose
    private String nip;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("nama")
    @Expose
    private String nama;

    @SerializedName("imei")
    @Expose
    private String imei;

    @SerializedName("latitude")
    @Expose
    private String latitude;

    @SerializedName("longitude")
    @Expose
    private String longitude;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("jarak")
    @Expose
    private String jarak;

    @SerializedName("presensi_datang")
    @Expose
    private String presensi_datang;

    @SerializedName("presensi_pulang")
    @Expose
    private String presensi_pulang;

    @SerializedName("id_lokasi_presence")
    @Expose
    private int id_lokasi_presence;

    @SerializedName("lokasi_presence")
    @Expose
    private String lokasi_presence;

    @SerializedName("id_unit_kerja")
    @Expose
    private String id_unit_kerja;

    @SerializedName("kode_mesin")
    @Expose
    private String kode_mesin;

    @SerializedName("response")
    @Expose
    private int response;

    @SerializedName("radius")
    @Expose
    private int radius;

    @SerializedName("verified")
    @Expose
    private int verified;

    @SerializedName("tanggal")
    @Expose
    private String tanggal;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("data")
    @Expose
    private List<Ent_Presensi> data;

    public String getKet_presence() {
        return ket_presence;
    }

    public void setKet_presence(String ket_presence) {
        this.ket_presence = ket_presence;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public List<Ent_Presensi> getData() {
        return data;
    }

    public void setData(List<Ent_Presensi> data) {
        this.data = data;
    }

    public int getVerified() {
        return verified;
    }

    public void setVerified(int verified) {
        this.verified = verified;
    }

    public String getLokasi_presence() {
        return lokasi_presence;
    }

    public void setLokasi_presence(String lokasi_presence) {
        this.lokasi_presence = lokasi_presence;
    }

    public String getHari() {
        return hari;
    }

    public void setHari(String hari) {
        this.hari = hari;
    }

    public String getKode_mesin() {
        return kode_mesin;
    }

    public void setKode_mesin(String kode_mesin) {
        this.kode_mesin = kode_mesin;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getId_lokasi_presence() {
        return id_lokasi_presence;
    }

    public void setId_lokasi_presence(int id_lokasi_presence) {
        this.id_lokasi_presence = id_lokasi_presence;
    }

    public String getId_unit_kerja() {
        return id_unit_kerja;
    }

    public void setId_unit_kerja(String id_unit_kerja) {
        this.id_unit_kerja = id_unit_kerja;
    }

    public String getPresensi_datang() {
        return presensi_datang;
    }

    public void setPresensi_datang(String presensi_datang) {
        this.presensi_datang = presensi_datang;
    }

    public String getPresensi_pulang() {
        return presensi_pulang;
    }

    public void setPresensi_pulang(String presensi_pulang) {
        this.presensi_pulang = presensi_pulang;
    }

    public String getJarak() {
        return jarak;
    }

    public void setJarak(String jarak) {
        this.jarak = jarak;
    }

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public String getDate() {
        return date;
    }
//
    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

//    public int getId_user() {
//        return id_user;
//    }
//
//    public void setId_user(int id_user) {
//        this.id_user = id_user;
//    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId_skpd() {
        return id_unit_kerja;
    }

    public void setId_skpd(int id_skpd) {
        this.id_unit_kerja = id_unit_kerja;
    }
}
