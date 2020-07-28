package com.presensi.app.Fragement;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.presensi.app.Adapter.Presensi_new_Adapter;
import com.presensi.app.Api.Api_Client;
import com.presensi.app.Api.Api_Interface;
import com.presensi.app.Model.Ent_Presensi;
import com.presensi.app.R;
import com.presensi.app.Util.SharedPref;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fg_PresensiDatang.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fg_PresensiDatang#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fg_PresensiDatang extends Fragment {
    private Api_Interface api_interface;
    private RecyclerView rvHistory;
    private RecyclerView.LayoutManager layoutManager;
    private Presensi_new_Adapter presensi_new_adapter;
    private SharedPref sharedPref;
    private TextView tvDate,tvBulan,tvYear,tvImgYear,tvTampil;
    private TableLayout tableLayout;
    private CheckBox checkBox;
    private TextView tv;
    private int cek=-1,tahun = -1;
    private int bulan = 1;
    String thn = "";

    public Fg_PresensiDatang() {
        // Required empty public constructor
    }


    public static Fg_PresensiDatang newInstance(String param1, String param2) {
        Fg_PresensiDatang fragment = new Fg_PresensiDatang();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fg__presensi_datang, container, false);

        sharedPref = new SharedPref(getActivity());
        api_interface = Api_Client.getClient().create(Api_Interface.class);
//        rvHistory = view.findViewById(R.id.rvHistory);
        tvDate = view.findViewById(R.id.tvDate);
        tvBulan = view.findViewById(R.id.tvBulan);
        tvImgYear = view.findViewById(R.id.tvImgYear);
        tvYear = view.findViewById(R.id.tvYear);
        tvTampil = view.findViewById(R.id.tvTampil);

        SimpleDateFormat sdfNama = new SimpleDateFormat("MMMM");
        Date date2 = new Date();

        tvBulan.setText(sdfNama.format(date2));

        tableLayout=view.findViewById(R.id.tableLayout);
//        layoutManager = new LinearLayoutManager(getActivity());
//        rvHistory.setLayoutManager(layoutManager);


        SimpleDateFormat sdfMonth = new SimpleDateFormat("M");
        Date month = new Date();
        SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
        Date year = new Date();
        tvYear.setText(sdfYear.format(year));

        history_presensi(sdfMonth.format(month),sdfYear.format(year));

        tvDate.setOnClickListener(l->{
            if(cek == -1)
            {
                showDialogMonth(Integer.parseInt(sdfMonth.format(month))-1);
            }
            else
            {
                showDialogMonth(cek);
            }

        });

        tvImgYear.setOnClickListener(l->{
                showDialogYear(tahun);


        });

        tvTampil.setOnClickListener(l->{
            tableLayout.removeAllViews();
//            Toast.makeText(getActivity(),String.valueOf(bulan),Toast.LENGTH_LONG).show();
            history_presensi(String.valueOf(bulan),tvYear.getText().toString());
        });
        return view;
    }


    private void showDialogYear(int checkItem)
    {
        String[] listItems = {"2016", "2017", "2018", "2019", "2020","2021","2022","2023","2024","2025","2026","2027","2028","2029","2030"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose item");

        int checkedItem = checkItem; //this will checked the item when user open the dialog
        builder.setSingleChoiceItems(listItems, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tvYear.setText(listItems[which]);
                tahun = which;
                thn = listItems[which];
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void showDialogMonth(int checkItem)
    {
        String[] listItems = {"Januari", "Februari", "Maret", "April", "Mei","Juni","Juli","Agustus","September","Oktober","November","Desember"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose item");

        int checkedItem = checkItem; //this will checked the item when user open the dialog
        builder.setSingleChoiceItems(listItems, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bulan = which+1;
                cek = which;
                tvBulan.setText(listItems[which]);

                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void history_presensi_bulanan(String bulan)
    {
        Call<List<Ent_Presensi>> showPresensiCall = api_interface.show_presensi_bulanan(sharedPref.sp.getString("nip",""),bulan);
        showPresensiCall.enqueue(new Callback<List<Ent_Presensi>>() {
            @Override
            public void onResponse(Call<List<Ent_Presensi>> call, Response<List<Ent_Presensi>> response) {
                List<Ent_Presensi> listPresensi = response.body();

//                presensi_new_adapter = new Presensi_new_Adapter(getActivity(),listPresensi,layoutManager);
//
//                rvHistory.setAdapter(presensi_new_adapter);
                for (int i=0;i<listPresensi.size();i++){

                    View tableRow =LayoutInflater.from(getActivity()).inflate(R.layout.table_item,null,false);
                    TextView tvTanggal  = (TextView) tableRow.findViewById(R.id.tvTanggal);
                    TableLayout tableLayout_2  =  tableRow.findViewById(R.id.tableLayout_2);
                    tvTanggal.setText(listPresensi.get(i).getTanggal());

                            List<Ent_Presensi> listTime = listPresensi.get(i).getData();
                            for(int a=0;a<listTime.size();a++)
                            {

                                View tableRow_2 =LayoutInflater.from(getActivity()).inflate(R.layout.table_item_2,null,false);
                                TextView tvJam = tableRow_2.findViewById(R.id.tvJam);

                                ImageView imgPresensi = tableRow_2.findViewById(R.id.imgPresensi);
                                ImageView imgVerified = tableRow_2.findViewById(R.id.imgVerified);

                                if(listTime.get(a).getKode_mesin().equals("hp"))
                                {
                                    imgPresensi.setImageResource(R.drawable.ic_smartphone);
                                    if(listTime.get(a).getVerified() == 0 || String.valueOf(listTime.get(a).getVerified()).isEmpty() || String.valueOf(listTime.get(a).getVerified()) == null)
                                    {
                                        imgVerified.setImageResource(R.drawable.ic_exclamation);
                                    }
                                    else
                                    {
                                        imgVerified.setImageResource(R.drawable.ic_check);
                                    }
                                }
                                else
                                {
                                    imgPresensi.setImageResource(R.drawable.ic_fingerprint);
                                    imgVerified.setImageResource(R.drawable.ic_check);
                                }

                                tvJam.setText(listTime.get(a).getTime());

                                tableLayout_2.addView(tableRow_2);
                            }

                    tableLayout.addView(tableRow);



                }
            }

            @Override
            public void onFailure(Call<List<Ent_Presensi>> call, Throwable t) {
                Toast toast = Toast.makeText(getActivity(),"Couldn't get data",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        });
    }

    private void history_presensi(String bulan,String tahun)
    {
        Call<List<Ent_Presensi>> showPresensiCall = api_interface.show_presensi_per_date(sharedPref.sp.getString("nip",""),bulan,tahun);
        showPresensiCall.enqueue(new Callback<List<Ent_Presensi>>() {
            @Override
            public void onResponse(Call<List<Ent_Presensi>> call, Response<List<Ent_Presensi>> response) {
                if(!response.isSuccessful())
                {
                    Toast toast = Toast.makeText(getActivity(),"Terjadi gangguan pada server. Belum bisa mengambil data History Presence, tunggu beberapa saat lagi...mohon maaaf atas ketidaknyamanan ini.",Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
                else
                {
                    List<Ent_Presensi> listPresensi = response.body();

//                presensi_new_adapter = new Presensi_new_Adapter(getActivity(),listPresensi,layoutManager);
//
//                rvHistory.setAdapter(presensi_new_adapter);
                    for (int i=0;i<listPresensi.size();i++){

                        View tableRow =LayoutInflater.from(getActivity()).inflate(R.layout.table_item,null,false);
                        TextView tvTanggal  = (TextView) tableRow.findViewById(R.id.tvTanggal);
                        TableLayout tableLayout_2  =  tableRow.findViewById(R.id.tableLayout_2);
                        tvTanggal.setText(listPresensi.get(i).getDate()+" "+listPresensi.get(i).getHari());

                        Call<List<Ent_Presensi>> showPresensiPerDate = api_interface.show_presensi(sharedPref.sp.getString("nip",""),listPresensi.get(i).getDate());
                        showPresensiPerDate.enqueue(new Callback<List<Ent_Presensi>>() {
                            @Override
                            public void onResponse(Call<List<Ent_Presensi>> call, Response<List<Ent_Presensi>> response) {
                                List<Ent_Presensi> listTime = response.body();
                                for(int a=0;a<listTime.size();a++)
                                {

                                    View tableRow_2 =LayoutInflater.from(getActivity()).inflate(R.layout.table_item_2,null,false);
                                    TextView tvJam = tableRow_2.findViewById(R.id.tvJam);

                                    ImageView imgPresensi = tableRow_2.findViewById(R.id.imgPresensi);
                                    ImageView imgVerified = tableRow_2.findViewById(R.id.imgVerified);

                                    if(listTime.get(a).getKode_mesin().equals("hp"))
                                    {
                                        imgPresensi.setImageResource(R.drawable.ic_smartphone);
                                        if(listTime.get(a).getVerified() == 0 || String.valueOf(listTime.get(a).getVerified()).isEmpty() || String.valueOf(listTime.get(a).getVerified()) == null)
                                        {
                                            imgVerified.setImageResource(R.drawable.ic_exclamation);
                                        }
                                        else
                                        {
                                            imgVerified.setImageResource(R.drawable.ic_check);
                                        }
                                    }
                                    else
                                    {
                                        imgPresensi.setImageResource(R.drawable.ic_fingerprint);
                                        imgVerified.setImageResource(R.drawable.ic_check);
                                    }

                                    tvJam.setText(listTime.get(a).getTime());

                                    tableLayout_2.addView(tableRow_2);
                                }
                            }

                            @Override
                            public void onFailure(Call<List<Ent_Presensi>> call, Throwable t) {
                                Toast toast = Toast.makeText(getActivity(),"Couldn't get data",Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER,0,0);
                                toast.show();
                            }
                        });

                        tableLayout.addView(tableRow);



                    }
                }

            }

            @Override
            public void onFailure(Call<List<Ent_Presensi>> call, Throwable t) {
                Toast toast = Toast.makeText(getActivity(),"Couldn't get data",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
