package com.presensi.app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.presensi.app.Add_Location_Activity;
import com.presensi.app.Model.Ent_Unit_Kerja;
import com.presensi.app.R;

import java.util.ArrayList;
import java.util.List;

public class UnitKerja_Adapter extends RecyclerView.Adapter<UnitKerja_Adapter.Holder> implements Filterable {
    private List<Ent_Unit_Kerja> list_unitKerja;
    private Context context;
    List<Ent_Unit_Kerja> filterList;

    public UnitKerja_Adapter(List<Ent_Unit_Kerja> list_unitKerja, Context context) {
        this.list_unitKerja = list_unitKerja;
        this.context = context;
        filterList = list_unitKerja;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_unit_kerja,viewGroup,false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {

        holder.tvUnitKerja.setText(list_unitKerja.get(i).getUnit_kerja());

        holder.itemView.setOnClickListener(l->{
            Intent intent = new Intent(context, Add_Location_Activity.class);
            intent.putExtra("id_unit_kerja",list_unitKerja.get(i).getId_unit_kerja());
            intent.putExtra("unit_kerja",list_unitKerja.get(i).getUnit_kerja());
            intent.putExtra("kelompok_unit_kerja",list_unitKerja.get(i).getKelompok_unit_kerja());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list_unitKerja.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String cari = constraint.toString();
                if(cari.isEmpty())
                {
                    list_unitKerja = filterList;
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
                    list_unitKerja = mListUnitKerja;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = list_unitKerja;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                list_unitKerja = (List<Ent_Unit_Kerja>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView tvUnitKerja;
        public Holder(@NonNull View itemView) {
            super(itemView);
            tvUnitKerja = itemView.findViewById(R.id.tvUnitKerja);
        }
    }
}
