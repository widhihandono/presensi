package com.presensi.app.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Ent_Time {
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
}
