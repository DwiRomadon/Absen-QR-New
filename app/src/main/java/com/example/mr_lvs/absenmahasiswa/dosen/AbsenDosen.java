package com.example.mr_lvs.absenmahasiswa.dosen;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mr_lvs.absenmahasiswa.R;
import com.example.mr_lvs.absenmahasiswa.server.AppController;
import com.example.mr_lvs.absenmahasiswa.server.Config_URL;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AbsenDosen extends AppCompatActivity {


    private IntentIntegrator intentIntegrator;

    String getRuang;

    int socketTimeout = 30000;
    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    String kodeHari = null;
    Calendar calendar;
    SimpleDateFormat dayFormat;
    SimpleDateFormat df,df1,df2;

    private String nidn;
    private String penggal;
    private String kdmk;
    private String kelas;
    private String kdHari;
    private String jamAwal;
    private String jamAkhir;
    private String sks;
    private String prodi;
    private String thnSem;
    private String idJadwal;
    private String program;
    private String blnThnAbsen;
    private String mingguKe;


    ProgressDialog pDialog;

    @BindView(R.id.txtParsingData)
    EditText txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absen_dosen);
        ButterKnife.bind(this);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Intent intent = getIntent();
        nidn        = intent.getStringExtra("nidn");
        penggal     = intent.getStringExtra("penggal");
        kdmk        = intent.getStringExtra("kodemk") ;
        kelas       = intent.getStringExtra("kelas");
        kdHari      = intent.getStringExtra("kodeHari");
        jamAwal     = intent.getStringExtra("jamawal");
        jamAkhir    = intent.getStringExtra("jamakhir");
        sks         = intent.getStringExtra("sks");
        prodi       = intent.getStringExtra("prodi");
        program     = intent.getStringExtra("program");
        thnSem      = intent.getStringExtra("thnsem");
        idJadwal    = intent.getStringExtra("idjadwal");

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

        intentIntegrator = new IntentIntegrator(AbsenDosen.this);
        intentIntegrator.initiateScan();

        getDataBlnThn(String.valueOf(df1.format(calendar.getTime())),
                String.valueOf(df1.format(calendar.getTime())));
    }


    public void getDataBlnThn(String tglAwal, String tglAkhir){
        String tag_string_req = "req";
        pDialog.setMessage("Mohon tunggu");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config_URL.getBulanThnSem, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(String.valueOf(getApplication()), "Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean status = jObj.getBoolean("status");

                    if(status == true){

                        blnThnAbsen         = jObj.getString("blnthn");
                        mingguKe            = jObj.getString("mingguke");

                    }else {
                        String error_msg = jObj.getString("msg");
                        Toast.makeText(getApplicationContext(),
                                error_msg, Toast.LENGTH_LONG).show();

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
                hideDialog();
            }
        }){

            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("tglAwal", tglAwal);
                params.put("tglAkhir", tglAkhir);
                return params;
            }
        };

        strReq.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(strReq,tag_string_req);
    }


    public void inpurAbsenDosen(final String kdHari, final String jamAwal,final String jamAkhir,final String ruang,
                                final String kelas, final String nidn,final String kodeMK,final String sks,final String jmlhadir,
                                final String blnthnSem,final String kdProdi,final String mingguKe,final String program,final String operator,final String thnSem,
                                final String idJadwal){


        String tag_string_req = "req";
        pDialog.setMessage("Mohon tunggu");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config_URL.inputAbsenDosen, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(String.valueOf(getApplication()), "Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean status = jObj.getBoolean("status");

                    if(status == false){

                        String error_msg = jObj.getString("msg");

                        AlertDialog.Builder builder = new AlertDialog.Builder(AbsenDosen.this);
                        builder.setMessage(Html.fromHtml("<font color='#2980B9'><b>"+error_msg+"</b></font>"))
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent a = new Intent(AbsenDosen.this, Maindosen.class);
                                        startActivity(a);
                                        finish();
                                    }
                                }).show();

                    }else {

                        String error_msg = jObj.getString("msg");
                        AlertDialog.Builder builder = new AlertDialog.Builder(AbsenDosen.this);
                        builder.setTitle(Html.fromHtml("<font color='#2980B9'><b>"+error_msg+"</b></font>"));
                        builder.setMessage(Html.fromHtml("<font color='#2980B9'><b>Silahkan Input BAD</b></font>"))
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent a = new Intent(AbsenDosen.this, InputBad.class);
                                        a.putExtra("kodemk", kdmk);
                                        a.putExtra("mingguke", mingguKe);
                                        a.putExtra("blntahun", blnThnAbsen);
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
                hideDialog();
            }
        }){

            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("kodehari", kdHari);
                params.put("jamawal", jamAwal);
                params.put("jamakhir", jamAkhir);
                params.put("ruang", ruang);
                params.put("kelas", kelas);
                params.put("nidn", nidn);
                params.put("kodematakuliah", kodeMK);
                params.put("sks", sks);
                params.put("jmlhadir", jmlhadir);
                params.put("blntahunabsen", blnthnSem);
                params.put("kodeprodi", kdProdi);
                params.put("minggike", mingguKe);
                params.put("program", program);
                params.put("operator", operator);
                params.put("thnsemester", thnSem);
                params.put("idjadwal", idJadwal);
                return params;
            }
        };

        strReq.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(strReq,tag_string_req);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {

                Intent a = new Intent(AbsenDosen.this, ListPilihMatakuliah.class);
                a.putExtra("nidn", nidn);
                startActivity(a);
                finish();

            } else {
                String tag_string_req = "req";
                pDialog.setMessage("Mohon tunggu");
                showDialog();

                StringRequest strReq = new StringRequest(Request.Method.POST,
                        Config_URL.parsingRuang, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(String.valueOf(getApplication()), "Response: " + response.toString());
                        hideDialog();
                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean status = jObj.getBoolean("status");

                            if(status == true){

                                getRuang         = jObj.getString("result");
                                String ruangan   = result.getContents()+getRuang;

                                double jumlahHadir;
                                if(sks.equals("0")){
                                     jumlahHadir = 1;
                                }else {
                                     jumlahHadir = Double.parseDouble(sks)/2;
                                }

                                inpurAbsenDosen(kodeHari, jamAwal,jamAkhir,ruangan,kelas,nidn,kdmk,sks,
                                        String.valueOf(jumlahHadir),blnThnAbsen,prodi,mingguKe,program,nidn,thnSem,idJadwal);

                            }else {
                                String error_msg = jObj.getString("msg");
                                Toast.makeText(getApplicationContext(),
                                        error_msg, Toast.LENGTH_LONG).show();

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
                        hideDialog();
                    }
                }){

                    @Override
                    protected Map<String, String> getParams(){
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("nidn", nidn);
                        params.put("kodehari", kodeHari);
                        params.put("jamawal", String.valueOf(df.format(calendar.getTime())));
                        params.put("jamakhir", String.valueOf(df.format(calendar.getTime())));
                        params.put("ruang", result.getContents());
                        return params;
                    }
                };

                strReq.setRetryPolicy(policy);
                AppController.getInstance().addToRequestQueue(strReq,tag_string_req);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
