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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InputBad extends AppCompatActivity {

    String kodemk;
    String mingguke;
    String blnThn;

    int socketTimeout = 30000;
    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    @BindView(R.id.beritaAcara)
    EditText edBeritaAcara;


    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_bad);

        ButterKnife.bind(this);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Intent i    = getIntent();
        kodemk      = i.getStringExtra("kodemk");
        mingguke    = i.getStringExtra("mingguke");
        blnThn      = i.getStringExtra("blntahun");
    }

    @OnClick(R.id.btKirim)
    void kirimBad(){
        String strBeritaAcara = edBeritaAcara.getText().toString();

        if(!strBeritaAcara.isEmpty()){
            inputBAD(strBeritaAcara, kodemk, mingguke, blnThn);
        }else {
            Toast.makeText(getApplicationContext(), "Data tidak boleh kosong", Toast.LENGTH_LONG).show();
        }
    }


    public void inputBAD(final String beritaacara, final String kdmk,final String mingguke,final String blnthn){


        String tag_string_req = "req";
        pDialog.setMessage("Mohon tunggu");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config_URL.inpuBAD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(String.valueOf(getApplication()), "Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean status = jObj.getBoolean("status");

                    if(status == true){

                        String error_msg = jObj.getString("msg");
                        /*Toast.makeText(getApplicationContext(),
                                error_msg, Toast.LENGTH_LONG).show();*/


                        AlertDialog.Builder builder = new AlertDialog.Builder(InputBad.this);
                        builder.setTitle(Html.fromHtml("<font color='#2980B9'><b>"+error_msg+"</b></font>"))
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent a = new Intent(InputBad.this, Maindosen.class);
                                        startActivity(a);
                                        finish();
                                    }
                                }).show();


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
                params.put("beritaacara", beritaacara);
                params.put("kdmk", kdmk);
                params.put("mingguke", mingguke);
                params.put("blnthn", blnThn);
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
