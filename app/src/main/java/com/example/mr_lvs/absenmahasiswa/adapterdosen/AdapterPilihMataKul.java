package com.example.mr_lvs.absenmahasiswa.adapterdosen;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mr_lvs.absenmahasiswa.R;
import com.example.mr_lvs.absenmahasiswa.getandset.DataPilihMatkul;

import java.util.List;

public class AdapterPilihMataKul extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<DataPilihMatkul> item;

    public AdapterPilihMataKul(Activity activity, List<DataPilihMatkul> item) {
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
            convertView = inflater.inflate(R.layout.content_pilih_matkul_dosen, null);


        TextView matakuliah = (TextView) convertView.findViewById(R.id.txtNamaMatKul);
        TextView nidn = (TextView) convertView.findViewById(R.id.txtNidndosen);
        TextView nomk = (TextView) convertView.findViewById(R.id.txtKodeMk);

        matakuliah.setText(item.get(position).getMatakuliah());
        nidn.setText(item.get(position).getNidn());
        nomk.setText(item.get(position).getNomk());

        return convertView;
    }
}
