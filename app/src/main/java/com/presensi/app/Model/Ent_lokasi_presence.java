package com.presensi.app.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Ent_lokasi_presence {

    @SerializedName("id_lokasi_presence")
    @Expose
    private int id_lokasi_presence;

    @Expose
    private int UID;

    @SerializedName("lokasi_presence")
    @Expose
    private String lokasi_presence;

    @SerializedName("latitude")
    @Expose
    private String latitude;

    @SerializedName("longitude")
    @Expose
    private String longitude;

    @SerializedName("id_unit_kerja")
    @Expose
    private String id_unit_kerja;

    @SerializedName("response")
    @Expose
    private int response;

    @SerializedName("radius")
    @Expose
    private int radius;

    //Version

    @SerializedName("version")
    @Expose
    private String version;

    @SerializedName("tanggal")
    @Expose
    private String tanggal;

    @SerializedName("url")
    @Expose
    private String url;


    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getUID() {
        return UID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public int getId_lokasi_presence() {
        return id_lokasi_presence;
    }

    public void setId_lokasi_presence(int id_lokasi_presence) {
        this.id_lokasi_presence = id_lokasi_presence;
    }

    public String getLokasi_presence() {
        return lokasi_presence;
    }

    public void setLokasi_presence(String lokasi_presence) {
        this.lokasi_presence = lokasi_presence;
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

    public String getId_unit_kerja() {
        return id_unit_kerja;
    }

    public void setId_unit_kerja(String id_unit_kerja) {
        this.id_unit_kerja = id_unit_kerja;
    }
}
