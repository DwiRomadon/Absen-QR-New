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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mr_lvs.absenmahasiswa.R;
import com.example.mr_lvs.absenmahasiswa.adapterdosen.AdapterAbsenNgajar;
import com.example.mr_lvs.absenmahasiswa.adapterdosen.AdapterPilihMataKul;
import com.example.mr_lvs.absenmahasiswa.getandset.DataAbsen;
import com.example.mr_lvs.absenmahasiswa.server.AppController;
import com.example.mr_lvs.absenmahasiswa.server.Config_URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListAbsenNgajar extends AppCompatActivity {

    ProgressDialog pDialog;

    AdapterAbsenNgajar adapter;
    ListView list;

    ArrayList<DataAbsen> newsList = new ArrayList<DataAbsen>();

    int socketTimeout = 30000;
    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    String nidn, kdmk, kelas, kodehari;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_absen_ngajar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pertemuan Ke");

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        list = (ListView) findViewById(R.id.array_list);
        newsList.clear();

        adapter = new AdapterAbsenNgajar(ListAbsenNgajar.this, newsList);

        list.setAdapter(adapter);

        Intent intent = getIntent();
        nidn     = intent.getStringExtra("nidn");
        kdmk     = intent.getStringExtra("kodemk");
        kelas    = intent.getStringExtra("kelas");
        kodehari = intent.getStringExtra("kodeHari");

        getAbsen(nidn, kdmk, kelas, kodehari);
    }

    // Fungsi get JSON Mahasiswa
    private void getAbsen(final String nidn, String kodemk, String kelas, String kodeHari) {

        String tag_string_req = "req";

        pDialog.setMessage("Loading.....");
        showDialog();

        String tag_json_obj = "json_obj_req";
        StringRequest strReq = new StringRequest(Request.Method.POST, Config_URL.listAbsenNgajar, new Response.Listener<String>() {

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

                            DataAbsen news = new DataAbsen();

                            news.setNidn(jsonObject.getString("nidn"));
                            news.setKdhari(jsonObject.getString("kdhari"));

                            news.setKelas(jsonObject.getString("kelas"));
                            news.setMingguke(jsonObject.getString("mingguke"));
                            news.setKodemk(jsonObject.getString("kdmk"));
                            news.setBlnthn(jsonObject.getString("blnthnabsen"));


                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    // TODO Auto-generated method stub
                                    Intent a = new Intent(ListAbsenNgajar.this, EditBad.class);
                                    a.putExtra("nidn", nidn);
                                    a.putExtra("kodemk", newsList.get(position).getKodemk());
                                    a.putExtra("mingguke", newsList.get(position).getMingguke());
                                    a.putExtra("kelas", newsList.get(position).getKelas());
                                    a.putExtra("kdhari", newsList.get(position).getKdhari());
                                    a.putExtra("blntahun", newsList.get(position).getBlnthn());
                                    startActivity(a);
                                }
                            });

                            newsList.add(news);
                        }
                    }else {
                        String error_msg = jObj.getString("msg");
                        AlertDialog.Builder builder = new AlertDialog.Builder(ListAbsenNgajar.this);
                        builder.setTitle(Html.fromHtml("<font color='#2980B9'><b>Peringatan !</b></font>"));
                        builder.setMessage(Html.fromHtml("<font color='#2980B9'><b>"+error_msg+"</b></font>"))
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent a = new Intent(ListAbsenNgajar.this, Maindosen.class);
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
                ImageView image = new ImageView(ListAbsenNgajar.this);
                image.setImageResource(R.drawable.ic_check_connection);
                AlertDialog.Builder builder = new AlertDialog.Builder(ListAbsenNgajar.this);
                builder.setTitle(Html.fromHtml("<font color='#2980B9'><b></b></font>"))
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent a = new Intent(ListAbsenNgajar.this, ListBad.class);
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
                params.put("nomk", kodemk);
                params.put("kodehari", kodeHari);
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

    @Override
    public void onBackPressed() {
        Intent a = new Intent(ListAbsenNgajar.this, ListBad.class);
        a.putExtra("nidn",nidn);
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
}
