package com.example.mr_lvs.absenmahasiswa.users;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.example.mr_lvs.absenmahasiswa.Mahasiswa.MainMahasiswa;
import com.example.mr_lvs.absenmahasiswa.R;
import com.example.mr_lvs.absenmahasiswa.dosen.Maindosen;
import com.example.mr_lvs.absenmahasiswa.server.AppController;
import com.example.mr_lvs.absenmahasiswa.server.Config_URL;
import com.example.mr_lvs.absenmahasiswa.session.SessionManager;
import com.marozzi.roundbutton.RoundButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{

    private static final String TAG = Login.class.getSimpleName();


    private SliderLayout mDemoSlider;

    EditText edNidn, edPassword;
    RoundButton btnLogin;

    private ProgressDialog pDialog;
    private SessionManager session;
    SharedPreferences prefs;

    int socketTimeout = 30000;
    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);


    String nidns;
    String nama;
    String email;
    String level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        mDemoSlider = (SliderLayout)findViewById(R.id.slider);
        TextView copy        = (TextView) findViewById(R.id.copy);
        copy.setText("UBL Apps \u00a9 2019 MIS - Universitas Bandar Lampung");

        edNidn     = (EditText) findViewById(R.id.nidnNpm);
        edPassword = (EditText) findViewById(R.id.password);
        btnLogin   = (RoundButton)  findViewById(R.id.btLogin);

        slider();

        prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session     = new SessionManager(getApplicationContext());
        nidns       = prefs.getString("nidn","");
        nama        = prefs.getString("nama","");
        email       = prefs.getString("email", "");
        level       = prefs.getString("level", "");
        if(session.isLoggedIn()){

            if(level.equals("1")){
                Intent a = new Intent(getApplicationContext(), MainMahasiswa.class);
                a.putExtra("nidn", nidns);
                a.putExtra("nama", nama);
                a.putExtra("email", email);
                a.putExtra("level", level);
                startActivity(a);
                finish();
            }else if(level.equals("2")){
                Intent a = new Intent(getApplicationContext(), Maindosen.class);
                a.putExtra("nidn", nidns);
                a.putExtra("nama", nama);
                a.putExtra("email", email);
                a.putExtra("level", level);
                startActivity(a);
                finish();
            }else {
                Toast.makeText(getApplicationContext(), "Maaf akun anda tidak diizinkan untuk mengakses aplikasi ini", Toast.LENGTH_LONG).show();
            }
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String u = edNidn.getText().toString();
                String p = edPassword.getText().toString();

                if (u.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Nidn / NPM tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }else if(p.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Password tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }else if(p.isEmpty() && u.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Username dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }else {
                    checkLogin(u, p);
                }
            }
        });
    }


    public void checkLogin(final String nidn, final String password){

        String tag_string_req = "req_login";

        btnLogin.startAnimation();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config_URL.login, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                btnLogin.revertAnimation();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean status = jObj.getBoolean("status");

                    if(status == true){
                        nidns               = jObj.getString("username");
                        nama                = jObj.getString("nama");
                        email               = jObj.getString("email");
                        level               = jObj.getString("level");
                        String msg          = jObj.getString("msg");


                        session.setLogin(true);
                        storeRegIdinSharedPref(getApplicationContext(),nidns, nama, email, level);

                        if(level.equals("1")){
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            session.setLogin(true);
                            Intent a = new Intent(getApplicationContext(), MainMahasiswa.class);
                            a.putExtra("nidn", nidns);
                            a.putExtra("nama", nama);
                            a.putExtra("email", email);
                            a.putExtra("level", level);
                            startActivity(a);
                            finish();
                        }else if(level.equals("2")){
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            Intent a = new Intent(getApplicationContext(), Maindosen.class);
                            a.putExtra("nidn", nidns);
                            a.putExtra("nama", nama);
                            a.putExtra("email", email);
                            a.putExtra("level", level);
                            startActivity(a);
                            finish();
                        }else {
                            Toast.makeText(getApplicationContext(), "Maaf akun anda tidak diizinkan untuk mengakses aplikasi ini", Toast.LENGTH_LONG).show();
                        }

                    }else {
                        String error_msg = jObj.getString("msg");
                        Toast.makeText(getApplicationContext(), error_msg, Toast.LENGTH_LONG).show();

                    }

                }catch (JSONException e){
                    //JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error){
                Log.e(TAG, "Login Error : " + error.getMessage());
                error.printStackTrace();
                btnLogin.revertAnimation();
            }
        }){

            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", nidn);
                params.put("password", password);
                return params;
            }
        };

        strReq.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void storeRegIdinSharedPref(Context context,String iduser,String namaKar, String userName, String jabatan) {

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("nidn", iduser);
        editor.putString("nama", namaKar);
        editor.putString("email", userName);
        editor.putString("level", jabatan);
        editor.commit();
    }

    public void slider(){
        HashMap<String,Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("Bandar Lampung University",R.drawable.slide_1);
        file_maps.put("Solution For Present And Future",R.drawable.slide_3);
        file_maps.put("To Be A World Class Entrepreneurial University",R.drawable.slides_3);

        for(String name : file_maps.keySet()){
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra",name);

            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
