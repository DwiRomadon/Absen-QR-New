package com.example.mr_lvs.absenmahasiswa.Mahasiswa;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mr_lvs.absenmahasiswa.R;
import com.example.mr_lvs.absenmahasiswa.dosen.AbsenDosen;
import com.example.mr_lvs.absenmahasiswa.dosen.ListPilihMatakuliah;
import com.example.mr_lvs.absenmahasiswa.server.AppController;
import com.example.mr_lvs.absenmahasiswa.server.Config_URL;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AbsenMhs extends AppCompatActivity {

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

    private String npm;
    private String kelas;
    private String kdmk;
    private String blnThnAbsen;
    private String pertemuanKe;

    ProgressDialog pDialog;

    //@BindView(R.id.txtParsingData)
    //EditText txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absen_mhs);
        ButterKnife.bind(this);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Intent intent = getIntent();
        npm        = intent.getStringExtra("nidn");
        kdmk      = intent.getStringExtra("Kode");
        kelas     = intent.getStringExtra("Kls");

        //Toast.makeText(getApplicationContext(), npm + " " + kdmk+ " " + kelas, Toast.LENGTH_LONG).show();

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

        intentIntegrator = new IntentIntegrator(AbsenMhs.this);
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
                        pertemuanKe         = jObj.getString("mingguke");

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

    public void inpurAbsenMhs(final String pertemuanke, final String npm,final String kodeMk,final String kels){


        String tag_string_req = "req";
        pDialog.setMessage("Mohon tunggu");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config_URL.inputAbsenMhs, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(String.valueOf(getApplication()), "Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean status = jObj.getBoolean("status");

                    if(status == false){

                        String error_msg = jObj.getString("msg");

                        AlertDialog.Builder builder = new AlertDialog.Builder(AbsenMhs.this);
                        builder.setMessage(Html.fromHtml("<font color='#2980B9'><b>"+error_msg+"</b></font>"))
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent a = new Intent(AbsenMhs.this, MainMahasiswa.class);
                                        startActivity(a);
                                        finish();
                                    }
                                }).show();
                    }else {

                        String error_msg = jObj.getString("msg");
                        AlertDialog.Builder builder = new AlertDialog.Builder(AbsenMhs.this);
                        builder.setMessage(Html.fromHtml("<font color='#2980B9'><b>"+error_msg+"</b></font>"))
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent a = new Intent(AbsenMhs.this, MainMahasiswa.class);
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
                params.put("pertemuangKe", pertemuanke);
                params.put("npm", npm);
                params.put("kodeMk", kodeMk);
                params.put("Kelas", kels);
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

                Intent a = new Intent(AbsenMhs.this, ListKrsMhs.class);
                a.putExtra("nidn", npm);
                startActivity(a);
                finish();

            } else {
                String tag_string_req = "req";
                pDialog.setMessage("Mohon tunggu");
                showDialog();

                StringRequest strReq = new StringRequest(Request.Method.POST,
                        Config_URL.parsingRuangMhs, new Response.Listener<String>() {
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

                                //Toast.makeText(getApplicationContext(), ruangan,Toast.LENGTH_LONG).show();
                                /*inpurAbsenDosen(kodeHari, jamAwal,jamAkhir,ruangan,kelas,nidn,kdmk,sks,
                                        String.valueOf(jumlahHadir),blnThnAbsen,prodi,mingguKe,program,nidn,thnSem,idJadwal);*/
                                inpurAbsenMhs(pertemuanKe, npm, kdmk, kelas);

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
                        params.put("npm", npm);
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
