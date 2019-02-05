package com.example.mr_lvs.absenmahasiswa.adapterdosen;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mr_lvs.absenmahasiswa.R;
import com.example.mr_lvs.absenmahasiswa.getandset.DataMataKuliahMahasiswa;
import com.example.mr_lvs.absenmahasiswa.getandset.DataPilihMatkul;

import java.util.List;

public class AdapterPilihMatkulMhs extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<DataMataKuliahMahasiswa> item;

    public AdapterPilihMatkulMhs(Activity activity, List<DataMataKuliahMahasiswa> item) {
        this.activity = activity;
        this.item = item;
    }

    @Override
    public int getCount() {
        return item.size();
    }

    @Override
    public Object getItem(int location) {
        return item.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.content_krs_mhs, null);


        TextView kodeMk     = (TextView) convertView.findViewById(R.id.txtKodeMk);
        TextView namaMk     = (TextView) convertView.findViewById(R.id.txtNamaMatKul);
        TextView namaDosen  = (TextView) convertView.findViewById(R.id.txtNamadosen);

        kodeMk.setText(item.get(position).getKodeMk());
        namaMk.setText(item.get(position).getNamaMk());
        namaDosen.setText(item.get(position).getNamaDosen());

        return convertView;
    }
}
