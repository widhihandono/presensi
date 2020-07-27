package com.presensi.app.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.presensi.app.Api.Api_Client;
import com.presensi.app.Api.Api_Interface;
import com.presensi.app.Model.Ent_Presensi;
import com.presensi.app.R;
import com.presensi.app.Util.SharedPref;

import java.util.List;

public class Presensi_new_Adapter extends RecyclerView.Adapter<Presensi_new_Adapter.Holder> {
    private Context context;
    private List<Ent_Presensi> list_presensi;
    SharedPref sharedPref;
    Time_Adapter time_adapter;
    RecyclerView.LayoutManager layoutManager;
    private Api_Interface api_interface;


    public Presensi_new_Adapter(Context context, List<Ent_Presensi> list_presensi,RecyclerView.LayoutManager layoutManager) {
        this.context = context;
        this.list_presensi = list_presensi;
        this.sharedPref = new SharedPref(context);
        this.api_interface = Api_Client.getClient().create(Api_Interface.class);
        this.layoutManager = new LinearLayoutManager(context);

    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View root = LayoutInflater.from(context).inflate(R.layout.table_item,viewGroup,false);
        Holder holder = new Holder(root);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
//        history_presensi(list_presensi.get(i).getDate());

//        if(list_presensi.get(i).getKode_mesin().equals("hp"))
//        {
//            holder.imgPresensi.setImageResource(R.drawable.ic_smartphone);
//            holder.tvTanggal.setText(list_presensi.get(i).getHari()+" "+list_presensi.get(i).getDate());
//
//            holder.tvJam.setText(list_presensi.get(i).getTime());
//        }
//        else
//        {
//            holder.imgPresensi.setImageResource(R.drawable.ic_fingerprint);
//            holder.tvTanggal.setText(list_presensi.get(i).getHari()+" "+list_presensi.get(i).getDate());
//
//            holder.tvJam.setText(list_presensi.get(i).getTime());
//        }
        holder.tvTanggal.setText(list_presensi.get(i).getDate());
        holder.tvHari.setText(list_presensi.get(i).getHari());
        holder.tvNo.setText(i+1);
        holder.tvJam.setText(list_presensi.get(i).getTime());




    }

    @Override
    public int getItemCount() {
        return list_presensi.size();
    }



    public class Holder extends RecyclerView.ViewHolder {
        private TextView tvTanggal,tvJam,tvNo,tvHari;
//        private ImageView imgPresensi;
        public Holder(@NonNull View itemView) {
            super(itemView);

            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvJam = itemView.findViewById(R.id.tvJam);
//            tvHari = itemView.findViewById(R.id.tvHari);
//            tvNo = itemView.findViewById(R.id.tvNo);
//            imgPresensi = itemView.findViewById(R.id.imgPresensi);

        }
    }



}
