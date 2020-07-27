package com.presensi.app.Api;

import com.presensi.app.Model.Ent_Presensi;
import com.presensi.app.Model.Ent_Settingan_Emulator;
import com.presensi.app.Model.Ent_Time;
import com.presensi.app.Model.Ent_Unit_Kerja;
import com.presensi.app.Model.Ent_lokasi_presence;
import com.presensi.app.Model.Ent_pegawai;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Api_Interface {
    @GET("Api_time/time")
    Call<Ent_Time> time();

    @FormUrlEncoded
    @POST("Api_login_presence/set_emulator")
    Call<Ent_Settingan_Emulator> set_emulator(@Field("boatloader") String boatloader,
                                                    @Field("host") String host,
                                                    @Field("id") String id);

    @FormUrlEncoded
    @POST("Api_presence/show_presensi_today")
    Call<Ent_Presensi> show_presensi_today(@Field("nip") String nip);

    @FormUrlEncoded
    @POST("Api_presence/show_presensi_datang")
    Call<List<Ent_Presensi>> show_presensi_datang(@Field("nip") String nip,
                                                  @Field("bln") String bln);

    @FormUrlEncoded
    @POST("Api_presence/show_presensi")
    Call<List<Ent_Presensi>> show_presensi(@Field("nip") String nip,
                                           @Field("tanggal") String tanggal);

    @FormUrlEncoded
    @POST("Api_presence/get_presence_per_day")
    Call<List<Ent_Presensi>> show_presensi_harian(@Field("nip") String nip);

    @FormUrlEncoded
    @POST("Api_presence/show_presensi_per_date")
    Call<List<Ent_Presensi>> show_presensi_per_date(@Field("nip") String nip,
                                                           @Field("bln") String bln,
                                                        @Field("tahun") String tahun);

    @FormUrlEncoded
    @POST("Api_presence/show_presensi_bulanan")
    Call<List<Ent_Presensi>> show_presensi_bulanan(@Field("nip") String nip,
                                                    @Field("bln") String bln);

    @FormUrlEncoded
    @POST("Api_presence/show_presensi_pulang")
    Call<List<Ent_Presensi>> show_presensi_pulang(@Field("nip") String nip,
                                                  @Field("bln") String bln);

    @FormUrlEncoded
    @POST("Api_login_presence/login")
    Call<Ent_pegawai> login(@Field("nip") String nip,
                            @Field("password") String password);

    @FormUrlEncoded
    @POST("Api_presence/presensi")
    Call<Ent_Presensi> presensi(@Field("nip") String nip,
                                @Field("status") String status,
                                @Field("latitude") String latitude,
                                @Field("longitude") String longitude,
                                @Field("imei") String imei,
                                @Field("image") String image,
                                @Field("id_unit_kerja") String id_unit_kerja,
                                @Field("id_lokasi_presence") int id_lokasi_presence,
                                @Field("ket_presence") String ket_presence,
                                @Field("time") String time,
                                @Field("date") String date);

    @GET("Api_presence/get_radius_presence")
    Call<Ent_Presensi> radius();

    @GET("Api_location_presence/get_location_unit_kerja")
    Call<List<Ent_Unit_Kerja>> getLocationUnitKerja();

    @FormUrlEncoded
    @POST("Api_location_presence/input_location_presence")
    Call<Ent_lokasi_presence> input_location(@Field("lokasi_presence") String lokasi_presence,
                                             @Field("latitude") String latitude,
                                             @Field("longitude") String longitude,
                                             @Field("id_unit_kerja") String id_unit_kerja
                                            );

    @GET("Api_location_presence/get_location_presence")
    Call<List<Ent_lokasi_presence>> get_location_presence();

    @FormUrlEncoded
    @POST("Api_location_presence/location_presence_per_unit_kerja")
    Call<List<Ent_lokasi_presence>> locationPresence(@Field("id_unit_kerja") String id_unit_kerja);

    @FormUrlEncoded
    @POST("Api_location_presence/location_presence_per_nip")
    Call<List<Ent_lokasi_presence>> locationPresence_per_nip(@Field("nip") String nip);

    @FormUrlEncoded
    @POST("Api_location_presence/get_location_presence_by_id_unit_kerja")
    Call<List<Ent_lokasi_presence>> get_locationUnitKerja(@Field("id_unit_kerja") String id_unit_kerja);

    @GET("Api_login_presence/get_version")
    Call<Ent_lokasi_presence> version();

    @FormUrlEncoded
    @POST("Api_presence/cek_imei_presence2")
    Call<List<Ent_Presensi>> cek_imei_presence(@Field("imei") String imei,
                                               @Field("status") String status,
                                               @Field("nip") String nip);

    @FormUrlEncoded
    @POST("Api_presence/cekNip")
    Call<Ent_Presensi> cekNip(@Field("imei") String imei,
                              @Field("status") String status);

    @FormUrlEncoded
    @POST("Api_presence/edit_no_hp_or_email")
    Call<Ent_pegawai> edit_no_hp_or_email(@Field("nip") String nip,
                                    @Field("no_hp") String no_hp,
                                    @Field("email") String email);

    @FormUrlEncoded
    @POST("Api_Sms/kirimSms")
    Call<Ent_pegawai> resetPassword(@Field("no_hp") String no_hp);

    @FormUrlEncoded
    @POST("Api_presence/sendSms")
    Call<Ent_pegawai> gantiPassword(@Field("no_hp") String no_hp,
                                    @Field("nip") String nip,
                                    @Field("password") String password);

    @FormUrlEncoded
    @POST("Api_presence/sendToEmail")
    Call<Ent_pegawai> sendToEmail(@Field("email") String email,
                                  @Field("nip") String nip,
                                  @Field("password") String password);

    @FormUrlEncoded
    @POST("Api_presence/sendEmailAndSms")
    Call<Ent_pegawai> sendEmailAndSms(@Field("email") String email,
                                  @Field("no_hp") String no_hp,
                                  @Field("nip") String nip,
                                  @Field("password") String password);

    @GET("Api_login_presence/cekjamupdate")
    Call<Ent_Time> cekjamupdate();
}
