package com.example.mr_lvs.absenmahasiswa.adapterdosen;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mr_lvs.absenmahasiswa.R;
import com.example.mr_lvs.absenmahasiswa.getandset.DataAbsen;

import java.util.List;

public class AdapterAbsenNgajar extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<DataAbsen> item;

    public AdapterAbsenNgajar(Activity activity, List<DataAbsen> item) {
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
            convertView = inflater.inflate(R.layout.content_absen_ngajar, null);


        TextView kodemk         = (TextView) convertView.findViewById(R.id.txtKodeMk);
        TextView nidn           = (TextView) convertView.findViewById(R.id.txtNidndosen);
        TextView pertemuanke    = (TextView) convertView.findViewById(R.id.mingguKe);

        kodemk.setText(item.get(position).getKodemk());
        nidn.setText(item.get(position).getNidn());
        pertemuanke.setText("Pertemuan : " + item.get(position).getMingguke());

        return convertView;
    }
}
