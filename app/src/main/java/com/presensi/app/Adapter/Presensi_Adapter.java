package com.presensi.app.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.presensi.app.Model.Ent_Presensi;
import com.presensi.app.R;
import com.presensi.app.Util.SharedPref;

import java.util.List;

public class Presensi_Adapter extends RecyclerView.Adapter<Presensi_Adapter.Holder> {
    private Context context;
    private List<Ent_Presensi> list_presensi;
    SharedPref sharedPref;

    public Presensi_Adapter(Context context, List<Ent_Presensi> list_presensi) {
        this.context = context;
        this.list_presensi = list_presensi;
        this.sharedPref = new SharedPref(context);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View root = LayoutInflater.from(context).inflate(R.layout.list_presensi_harian,viewGroup,false);
        Holder holder = new Holder(root);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {

        if(list_presensi.get(i).getKode_mesin().equals("fg"))
        {

                holder.tvJam.setText(list_presensi.get(i).getTime());
                holder.tvLokasi.setText(list_presensi.get(i).getLokasi_presence());
                holder.imgPresensi.setImageResource(R.drawable.ic_fingerprint);
                holder.imgVerified.setImageResource(R.drawable.ic_check);

        }
        else
        {

                holder.tvJam.setText(list_presensi.get(i).getTime());
                holder.tvLokasi.setText(list_presensi.get(i).getLokasi_presence());
                holder.imgPresensi.setImageResource(R.drawable.ic_smartphone);
                if(list_presensi.get(i).getVerified() == 0 || String.valueOf(list_presensi.get(i).getVerified()).isEmpty() || String.valueOf(list_presensi.get(i).getVerified()) == null)
                {
                    holder.imgVerified.setImageResource(R.drawable.ic_exclamation);
                }
                else
                {
                    holder.imgVerified.setImageResource(R.drawable.ic_check);
                }

        }



    }

    @Override
    public int getItemCount() {
        return list_presensi.size();
    }



    public class Holder extends RecyclerView.ViewHolder {
        private TextView tvJam,tvLokasi;
        private ImageView imgPresensi,imgVerified;
        public Holder(@NonNull View itemView) {
            super(itemView);

            tvJam = itemView.findViewById(R.id.tvJam);
            tvLokasi = itemView.findViewById(R.id.tvLokasi);
            imgPresensi = itemView.findViewById(R.id.imgPresensi);
            imgVerified = itemView.findViewById(R.id.imgVerified);
        }
    }



}
