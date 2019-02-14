
package com.example.mr_lvs.absenmahasiswa.dosen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mr_lvs.absenmahasiswa.Mahasiswa.MainMahasiswa;
import com.example.mr_lvs.absenmahasiswa.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class IntroAbsen extends AppCompatActivity {

    String nidn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_absen2);
        ButterKnife.bind(this);
        TextView copy        = (TextView) findViewById(R.id.copy);
        copy.setText("UBL Apps \u00a9 2019 MIS - Universitas Bandar Lampung");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Manajemen Absen");

        Intent intent = getIntent();
        nidn   = intent.getStringExtra("nidn");
    }

    @OnClick(R.id.absen)
    void muliaAbsen(){
        Intent a = new Intent(IntroAbsen.this, ListPilihMatakuliah.class);
        a.putExtra("nidn",nidn);
        startActivity(a);
        finish();
    }

    @OnClick(R.id.rekapAbsen)
    void rekapAbsen(){
        Toast.makeText(getApplicationContext(), "Sedang dalam pengembangan", Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.editBad)
    void bad(){
        Intent a = new Intent(IntroAbsen.this, ListBad.class);
        a.putExtra("nidn",nidn);
        startActivity(a);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(IntroAbsen.this, Maindosen.class);
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
