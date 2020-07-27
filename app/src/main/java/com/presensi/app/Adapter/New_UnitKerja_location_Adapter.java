package com.presensi.app.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.presensi.app.Api.Api_Client;
import com.presensi.app.Api.Api_Interface;
import com.presensi.app.Model.Ent_Unit_Kerja;
import com.presensi.app.Model.Ent_lokasi_presence;
import com.presensi.app.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class New_UnitKerja_location_Adapter extends RecyclerView.Adapter<New_UnitKerja_location_Adapter.Holder> implements Filterable {
    private List<Ent_lokasi_presence> listLokasiPresence;
    private List<Ent_Unit_Kerja> listUnitKerja;
    Context context;
    private Api_Interface api_interface;
    private LocationPresence_Adapter locationPresence_adapter;
    private Animation rotate_forward,rotate_backward;
    private boolean isArrowOpen = false;
    List<Ent_Unit_Kerja> filterList;

    public New_UnitKerja_location_Adapter(List<Ent_Unit_Kerja> listUnitKerja, Context context) {
        this.listUnitKerja = listUnitKerja;
        this.listLokasiPresence = new ArrayList<>();
        this.context = context;
        this.filterList = listUnitKerja;
        this.api_interface = Api_Client.getClient().create(Api_Interface.class);
        rotate_forward = AnimationUtils.loadAnimation(context,R.anim.rotate_forward_unit_kerja);
        rotate_backward = AnimationUtils.loadAnimation(context,R.anim.rotate_backward_unit_kerja);

    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_new_unit_kerja,viewGroup,false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        holder.tvUnitKerja.setText(listUnitKerja.get(i).getUnit_kerja());

        holder.imgShowAll.setOnClickListener(l->{
            if(isArrowOpen){

                holder.imgShowAll.startAnimation(rotate_backward);
                isArrowOpen = false;
                Log.d("Raj", "close");
                holder.rvLokasiPresence.setVisibility(View.GONE);

            } else {

                holder.imgShowAll.startAnimation(rotate_forward);
                isArrowOpen = true;
                Log.d("Raj","open");
                holder.rvLokasiPresence.setVisibility(View.VISIBLE);
            }
        });

        Call<List<Ent_lokasi_presence>> lokasiCall = api_interface.get_locationUnitKerja(listUnitKerja.get(i).getId_unit_kerja());
        lokasiCall.enqueue(new Callback<List<Ent_lokasi_presence>>() {
            @Override
            public void onResponse(Call<List<Ent_lokasi_presence>> call, Response<List<Ent_lokasi_presence>> response) {
                List<Ent_lokasi_presence> listLokasi = response.body();
                locationPresence_adapter = new LocationPresence_Adapter(listLokasi,context);
                holder.rvLokasiPresence.setAdapter(locationPresence_adapter);
            }

            @Override
            public void onFailure(Call<List<Ent_lokasi_presence>> call, Throwable t) {
                Log.d("Location_presence",t.getMessage());
            }
        });
    }




    @Override
    public int getItemCount() {
        return listUnitKerja.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String cari = constraint.toString();
                if(cari.isEmpty())
                {
                    listUnitKerja = filterList;
                }
                else
                {
                    List<Ent_Unit_Kerja> mListUnitKerja = new ArrayList<>();
                    for(Ent_Unit_Kerja data : filterList)
                    {
                        if(data.getUnit_kerja().toLowerCase().contains(cari))
                        {
                            mListUnitKerja.add(data);
                        }
                    }
                    listUnitKerja = mListUnitKerja;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = listUnitKerja;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listUnitKerja = (List<Ent_Unit_Kerja>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView tvUnitKerja;
        private ImageView imgShowAll;
        private RecyclerView rvLokasiPresence;
        private RecyclerView.LayoutManager layoutManager;
        public Holder(@NonNull View itemView) {
            super(itemView);

            tvUnitKerja = itemView.findViewById(R.id.tvUnitKerja);
            imgShowAll = itemView.findViewById(R.id.imgShowAll);
            rvLokasiPresence = itemView.findViewById(R.id.rvLokasiPresence);
            layoutManager = new LinearLayoutManager(context);
            rvLokasiPresence.setLayoutManager(layoutManager);


        }
    }
}
