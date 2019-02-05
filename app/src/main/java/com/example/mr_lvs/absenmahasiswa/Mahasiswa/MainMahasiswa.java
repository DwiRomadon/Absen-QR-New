package com.example.mr_lvs.absenmahasiswa.Mahasiswa;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
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
import com.example.mr_lvs.absenmahasiswa.dosen.Maindosen;
import com.example.mr_lvs.absenmahasiswa.server.AppController;
import com.example.mr_lvs.absenmahasiswa.server.Config_URL;
import com.example.mr_lvs.absenmahasiswa.session.SessionManager;
import com.example.mr_lvs.absenmahasiswa.users.Login;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainMahasiswa extends AppCompatActivity {

    private SessionManager session;
    SharedPreferences prefs;

    String nidns;
    String nama;
    String email;
    String level;

    int socketTimeout = 30000;
    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_mahasiswa);
        ButterKnife.bind(this);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        getSupportActionBar().setTitle("Absen Mahasiswa");
        TextView copy        = (TextView) findViewById(R.id.copy);
        copy.setText("UBL Apps \u00a9 2019 MIS - Universitas Bandar Lampung");
        isLogin();

        cekVersion(getResources().getString(R.string.version));
    }

    @OnClick(R.id.absen)
    void absen(){
        Intent a = new Intent(MainMahasiswa.this, IntroAbsen.class);
        a.putExtra("nidn", nidns);
        startActivity(a);
        finish();
    }

    @OnClick(R.id.profil)
    void profil(){
        Toast.makeText(getApplicationContext(), "Ini Profil", Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.login_card)
    void LogOut(){
        session.setLogin(false);
        session.setSkip(false);
        session.setSessid(0);

        // Launching the login activity
        Intent intent = new Intent(MainMahasiswa.this, Login.class);
        startActivity(intent);
        finish();
    }

    public void isLogin(){
        // Session manager
        session = new SessionManager(getApplicationContext());

        //Session Login
        if(session.isLoggedIn()){
            prefs = getSharedPreferences("UserDetails",
                    Context.MODE_PRIVATE);
            nidns = prefs.getString("nidn","");
            nama  = prefs.getString("nama","");
            email = prefs.getString("email", "");
            level = prefs.getString("level", "");
        }
    }


    private void cekVersion(final String version) {

        String tag_string_req = "req";

        pDialog.setMessage("Loading.....");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config_URL.cekVersion, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(String.valueOf(getApplication()), "Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean status = jObj.getBoolean("status");

                    if(status == false){
                        String error_msg = jObj.getString("msg");
                        //Toast.makeText(getApplicationContext(),
                        //        error_msg + " Atau tutup aplikasi dan masuk kembali", Toast.LENGTH_LONG).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainMahasiswa.this);
                        builder.setTitle(Html.fromHtml("<font color='#2980B9'><b>Peringatan !</b></font>"));
                        builder.setMessage(Html.fromHtml("<font color='#2980B9'><b>"+error_msg+"</b></font>"))
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        String theurl = "http://ublapps.ubl.ac.id/ublapps/Absenqr.apk";
                                        Uri urlstr = Uri.parse(theurl);
                                        Intent urlintent = new Intent();
                                        urlintent.setData(urlstr);
                                        urlintent.setAction(Intent.ACTION_VIEW);
                                        startActivity(urlintent);
                                        finish();
                                        System.exit(0);
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
                ImageView image = new ImageView(MainMahasiswa.this);
                image.setImageResource(R.drawable.ic_check_connection);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainMahasiswa.this);
                builder.setTitle(Html.fromHtml("<font color='#2980B9'><b>Muat Ulang</b></font>"))
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent a = new Intent(MainMahasiswa.this, MainMahasiswa.class);
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
                params.put("version", version);
                return params;
            }
        };

        strReq.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(strReq,tag_string_req);

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
