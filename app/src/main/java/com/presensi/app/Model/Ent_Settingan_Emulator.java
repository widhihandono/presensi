package com.presensi.app.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Ent_Settingan_Emulator {

    @SerializedName("bootloader")
    @Expose
    private String bootloader;

    @SerializedName("host")
    @Expose
    private String host;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("response")
    @Expose
    private int response;

    @SerializedName("pesan")
    @Expose
    private String pesan;


    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public String getBootloader() {
        return bootloader;
    }

    public void setBootloader(String bootloader) {
        this.bootloader = bootloader;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
