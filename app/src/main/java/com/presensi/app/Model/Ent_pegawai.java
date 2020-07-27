package com.presensi.app.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Ent_pegawai {

    @SerializedName("response")
    @Expose
    private int response;

    @SerializedName("radius")
    @Expose
    private int radius;

    @SerializedName("id_unit_kerja")
    @Expose
    private String id_unit_kerja;

    @SerializedName("nama")
    @Expose
    private String nama;

    @SerializedName("nip")
    @Expose
    private String nip;

    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("no_hp")
    @Expose
    private String no_hp;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("unit_kerja")
    @Expose
    private String unit_kerja;

    @SerializedName("level")
    @Expose
    private String level;

    @SerializedName("lokasi")
    @Expose
    private List<Ent_lokasi_presence> lokasi;
//    }


    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<Ent_lokasi_presence> getLokasi() {
        return lokasi;
    }

    public void setLokasi(List<Ent_lokasi_presence> lokasi) {
        this.lokasi = lokasi;
    }

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public String getId_unit_kerja() {
        return id_unit_kerja;
    }

    public void setId_unit_kerja(String id_unit_kerja) {
        this.id_unit_kerja = id_unit_kerja;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNo_hp() {
        return no_hp;
    }

    public void setNo_hp(String no_hp) {
        this.no_hp = no_hp;
    }

    public String getUnit_kerja() {
        return unit_kerja;
    }

    public void setUnit_kerja(String unit_kerja) {
        this.unit_kerja = unit_kerja;
    }
}
