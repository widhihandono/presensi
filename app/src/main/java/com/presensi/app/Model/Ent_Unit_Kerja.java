package com.presensi.app.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Ent_Unit_Kerja {
    @SerializedName("id_unit_kerja")
    @Expose
    private String id_unit_kerja;

    @SerializedName("unit_kerja")
    @Expose
    private String unit_kerja;

    @SerializedName("kelompok_unit_kerja")
    @Expose
    private String kelompok_unit_kerja;


    public String getId_unit_kerja() {
        return id_unit_kerja;
    }

    public void setId_unit_kerja(String id_unit_kerja) {
        this.id_unit_kerja = id_unit_kerja;
    }

    public String getUnit_kerja() {
        return unit_kerja;
    }

    public void setUnit_kerja(String unit_kerja) {
        this.unit_kerja = unit_kerja;
    }

    public String getKelompok_unit_kerja() {
        return kelompok_unit_kerja;
    }

    public void setKelompok_unit_kerja(String kelompok_unit_kerja) {
        this.kelompok_unit_kerja = kelompok_unit_kerja;
    }

}
