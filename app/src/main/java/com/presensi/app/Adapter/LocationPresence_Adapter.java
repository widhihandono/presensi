package com.presensi.app.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.presensi.app.Model.Ent_lokasi_presence;
import com.presensi.app.R;

import java.util.ArrayList;
import java.util.List;

public class LocationPresence_Adapter extends RecyclerView.Adapter<LocationPresence_Adapter.Holder> implements Filterable {
    private List<Ent_lokasi_presence> list_location;
    private Context context;
    List<Ent_lokasi_presence> filterList;

    public LocationPresence_Adapter(List<Ent_lokasi_presence> list_location, Context context) {
        this.list_location = list_location;
        this.context = context;
        filterList = list_location;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_location_presence,viewGroup,false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        holder.tvUnitKerja.setText(list_location.get(i).getLokasi_presence());

//        holder.itemView.setOnClickListener(l->{
//            Intent intent = new Intent(context, Add_Location_Activity.class);
//            intent.putExtra("id_lokasi_presence",list_location.get(i).getId_lokasi_presence());
//            intent.putExtra("id_unit_kerja",list_location.get(i).getId_unit_kerja());
//            intent.putExtra("latitude",list_location.get(i).getLatitude());
//            intent.putExtra("longitude",list_location.get(i).getLongitude());
//            intent.putExtra("lokasi_presence",list_location.get(i).getLokasi_presence());
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
//        });
    }

    @Override
    public int getItemCount() {
        return list_location.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String cari = constraint.toString();
                if(cari.isEmpty())
                {
                    list_location = filterList;
                }
                else
                {
                    List<Ent_lokasi_presence> mListLocation = new ArrayList<>();
                    for(Ent_lokasi_presence data : filterList)
                    {
                        if(data.getLokasi_presence().toLowerCase().contains(cari))
                        {
                            mListLocation.add(data);
                        }
                    }
                    list_location = mListLocation;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = list_location;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                list_location = (List<Ent_lokasi_presence>) results.values;
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
