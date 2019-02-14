package com.example.mr_lvs.absenmahasiswa.server;

//This class is for storing all URLs as a model of URLs

public class Config_URL
{
    public static String base_URL           = "http://192.168.1.120/API_Absen";
    public static String login              = base_URL + "/User";
    public static String cekVersion         =  base_URL + "/User/cekVersionApp";

    //API DOSEN
    public static String indexDos           = base_URL + "/Absendosen";
    public static String getKodeKelas       = base_URL + "/Absendosen/getKodeKelas";
    public static String parsingRuang       = base_URL + "/Absendosen/parsingDataRuang/";
    public static String getBulanThnSem     = base_URL + "/Absendosen/getBlnThnabsen";
    public static String inputAbsenDosen    = base_URL + "/Absendosen/inputAbsenDosen";
    public static String inpuBAD            = base_URL + "/Absendosen/inputBAD";
    public static String listJadwalNgajar   = base_URL + "/Bad/";
    public static String listAbsenNgajar    = base_URL + "/Bad/listAbsen";
    public static String selectBad          = base_URL + "/Bad/selectBad/";
    public static String editBad            = base_URL + "/Bad/editBAD/";

    //API Mahasiswa
    public static String getIdAbsenNgajar   = base_URL + "/AbsenMahasiswa/";
    public static String getKrsMhs          = base_URL + "/AbsenMahasiswa/getDataKrs/";
    public static String getKrsKlsMhs       = base_URL + "/AbsenMahasiswa/getDataKrsKelas/";
    public static String parsingRuangMhs    = base_URL + "/AbsenMahasiswa/parsingDataRuangMhs";
    public static String inputAbsenMhs      = base_URL + "/AbsenMahasiswa/inputAbsenMhs";
}