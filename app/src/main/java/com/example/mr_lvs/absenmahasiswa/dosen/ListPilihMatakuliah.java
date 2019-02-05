package com.example.mr_lvs.absenmahasiswa.dosen;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mr_lvs.absenmahasiswa.R;
import com.example.mr_lvs.absenmahasiswa.adapterdosen.AdapterPilihMataKul;
import com.example.mr_lvs.absenmahasiswa.getandset.DataPilihMatkul;
import com.example.mr_lvs.absenmahasiswa.server.AppController;
import com.example.mr_lvs.absenmahasiswa.server.Config_URL;
import com.google.zxing.integration.android.IntentIntegrator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ListPilihMatakuliah extends AppCompatActivity {

    ProgressDialog pDialog;

    AdapterPilihMataKul adapter;
    ListView list;

    ArrayList<DataPilihMatkul> newsList = new ArrayList<DataPilihMatkul>();

    String kodeHari = null;

    String kdmk;
    Calendar calendar;
    SimpleDateFormat dayFormat;
    SimpleDateFormat df,df1,df2;
    private String nidn;

    int socketTimeout = 30000;
    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_pilih_matakuliah);
        TextView copy        = (TextView) findViewById(R.id.copy);
        copy.setText("UBL Apps \u00a9 2019 MIS - Universitas Bandar Lampung");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pilih Matakuliah");

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        list = (ListView) findViewById(R.id.array_list);
        newsList.clear();

        adapter = new AdapterPilihMataKul(ListPilihMatakuliah.this, newsList);

        list.setAdapter(adapter);

        Intent intent = getIntent();
        nidn   = intent.getStringExtra("nidn");

        //txtJamAwal.setText(df.format(calendar.getTime()));
        calendar = Calendar.getInstance();
        dayFormat = new SimpleDateFormat("EEEE", Locale.US);

        df = new SimpleDateFormat("HH:mm:ss");
        df1 = new SimpleDateFormat("yyyy-MM-dd");
        df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if(dayFormat.format(calendar.getTime()).equals("Sunday")){
            kodeHari = "7";
        }else if(dayFormat.format(calendar.getTime()).equals("Monday"))  {
            kodeHari = "1";
        }else if(dayFormat.format(calendar.getTime()).equals("Tuesday"))  {
            kodeHari = "2";
        }else if(dayFormat.format(calendar.getTime()).equals("Wednesday"))  {
            kodeHari = "3";
        }else if(dayFormat.format(calendar.getTime()).equals("Thursday"))  {
            kodeHari = "4";
        }else if(dayFormat.format(calendar.getTime()).equals("Friday"))  {
            kodeHari = "5";
        }else if(dayFormat.format(calendar.getTime()).equals("Saturday"))  {
            kodeHari = "6";
        }else{
            System.out.println("Sorry your day is wrong");
        }

        getKodeMKAndNameMK(nidn, kodeHari,String.valueOf(df.format(calendar.getTime())),
                String.valueOf(df.format(calendar.getTime())));
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(ListPilihMatakuliah.this, IntroAbsen.class);
        a.putExtra("nidn", nidn);
        startActivity(a);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // Fungsi get JSON Mahasiswa
    private void getKodeMKAndNameMK(final String nidns, final String kodeHari, final String jamAwal, final String jamAkhir) {

        String tag_string_req = "req";

        pDialog.setMessage("Loading.....");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config_URL.getKodeKelas, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(String.valueOf(getApplication()), "Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean status = jObj.getBoolean("status");

                    if(status == true){

                        String getObject = jObj.getString("data");
                        JSONArray jsonArray = new JSONArray(getObject);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String noMk         = jsonObject.getString("NoMk");
                            String kls          = jsonObject.getString("Kelas");
                            String penggal      = jsonObject.getString("Penggal");

                            getMatakuliah(nidn,noMk, String.valueOf(df.format(calendar.getTime())),
                                    String.valueOf(df.format(calendar.getTime())),kls);
                        }

                    }else {
                        String error_msg = jObj.getString("msg");
                        //Toast.makeText(getApplicationContext(),
                        //        error_msg + " Atau tutup aplikasi dan masuk kembali", Toast.LENGTH_LONG).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(ListPilihMatakuliah.this);
                        builder.setTitle(Html.fromHtml("<font color='#2980B9'><b>Peringatan !</b></font>"));
                        builder.setMessage(Html.fromHtml("<font color='#2980B9'><b>"+error_msg+"</b></font>"))
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent a = new Intent(ListPilihMatakuliah.this, Maindosen.class);
                                        a.putExtra("nidn", nidn);
                                        startActivity(a);
                                        finish();
                                    }
                                }).show();
                    }

                }catch (JSONException e){
                    //JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error){
                Log.e(String.valueOf(getApplication()), "Error : " + error.getMessage());
                error.printStackTrace();
                ImageView image = new ImageView(ListPilihMatakuliah.this);
                image.setImageResource(R.drawable.ic_check_connection);
                AlertDialog.Builder builder = new AlertDialog.Builder(ListPilihMatakuliah.this);
                builder.setTitle(Html.fromHtml("<font color='#2980B9'><b></b></font>"))
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent a = new Intent(ListPilihMatakuliah.this, Maindosen.class);
                                a.putExtra("nidn", nidn);
                                startActivity(a);
                                finish();
                            }
                        }).setView(image)
                        .show();
                hideDialog();
            }
        }){

            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("nidn", nidns);
                params.put("kodehari", kodeHari);
                params.put("jamawal", jamAwal);
                params.put("jamakhir", jamAkhir);
                return params;
            }
        };

        strReq.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(strReq,tag_string_req);

    }


    // Fungsi get JSON Mahasiswa
    private void getMatakuliah(final String nidn, final String noMk, final String jamAwal, final String jamAkhir, final String kelas) {

        String tag_string_req = "req";

        pDialog.setMessage("Loading.....");
        showDialog();

        String tag_json_obj = "json_obj_req";
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.indexDos, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response: ", response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean status = jObj.getBoolean("status");

                    if(status == true){

                        String getObject = jObj.getString("result");
                        JSONArray jsonArray = new JSONArray(getObject);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            DataPilihMatkul news = new DataPilihMatkul();

                            news.setMatakuliah(jsonObject.getString("Nama_MK"));
                            news.setNidn(jsonObject.getString("NIDN"));
                            news.setNomk(jsonObject.getString("NoMk"));

                            news.setKelas(jsonObject.getString("Kelas"));
                            news.setPenggal(jsonObject.getString("Penggal"));
                            news.setKdHari(jsonObject.getString("Kd_hari"));
                            news.setJamAwal(jsonObject.getString("JamAwal"));
                            news.setJamAkhir(jsonObject.getString("JamAkhir"));
                            news.setSks(jsonObject.getString("SKS"));
                            news.setKdProdi(jsonObject.getString("Kd_Jur"));
                            news.setProgram(jsonObject.getString("Kd_Program"));
                            news.setThnSem(jsonObject.getString("ThnSmester"));
                            news.setIdJadwal(jsonObject.getString("Id"));


                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    // TODO Auto-generated method stub
                                    Intent a = new Intent(ListPilihMatakuliah.this, AbsenDosen.class);
                                    a.putExtra("nidn", nidn);
                                    a.putExtra("kodemk", newsList.get(position).getNomk());
                                    a.putExtra("kelas", newsList.get(position).getKelas());
                                    a.putExtra("penggal", newsList.get(position).getPenggal());
                                    a.putExtra("kodeHari", newsList.get(position).getKdHari());
                                    a.putExtra("jamawal", newsList.get(position).getJamAwal());
                                    a.putExtra("jamakhir", newsList.get(position).getJamAkhir());
                                    a.putExtra("sks", newsList.get(position).getSks());
                                    a.putExtra("prodi", newsList.get(position).getKdProdi());
                                    a.putExtra("program", newsList.get(position).getProgram());
                                    a.putExtra("thnsem", newsList.get(position).getThnSem());
                                    a.putExtra("idjadwal", newsList.get(position).getIdJadwal());
                                    startActivity(a);
                                }
                            });

                            newsList.add(news);
                        }
                    }else {
                        String error_msg = jObj.getString("msg");
                        AlertDialog.Builder builder = new AlertDialog.Builder(ListPilihMatakuliah.this);
                        builder.setTitle(Html.fromHtml("<font color='#2980B9'><b>Peringatan !</b></font>"));
                        builder.setMessage(Html.fromHtml("<font color='#2980B9'><b>"+error_msg+"</b></font>"))
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent a = new Intent(ListPilihMatakuliah.this, Maindosen.class);
                                        a.putExtra("nidn", nidn);
                                        startActivity(a);
                                        finish();
                                    }
                                }).show();
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error){
                Log.e(String.valueOf(getApplication()), "Error : " + error.getMessage());
                error.printStackTrace();
                ImageView image = new ImageView(ListPilihMatakuliah.this);
                image.setImageResource(R.drawable.ic_check_connection);
                AlertDialog.Builder builder = new AlertDialog.Builder(ListPilihMatakuliah.this);
                builder.setTitle(Html.fromHtml("<font color='#2980B9'><b></b></font>"))
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent a = new Intent(ListPilihMatakuliah.this, Maindosen.class);
                                a.putExtra("nidn", nidn);
                                startActivity(a);
                                finish();
                            }
                        }).setView(image)
                        .show();
                hideDialog();
            }
        }){

            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("nidn", nidn);
                params.put("nomk", noMk);
                params.put("jamawal", jamAwal);
                params.put("jamakhir", jamAkhir);
                params.put("kelas", kelas);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
