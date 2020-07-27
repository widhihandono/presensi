package com.presensi.app.Fragement;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.presensi.app.Adapter.Presensi_Adapter;
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
 * {@link Fg_PresensiPulang.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fg_PresensiPulang#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fg_PresensiPulang extends Fragment {

    private OnFragmentInteractionListener mListener;

    private Api_Interface api_interface;
    private RecyclerView rvHistory;
    private RecyclerView.LayoutManager layoutManager;
    private Presensi_Adapter presensi_adapter;
    private SharedPref sharedPref;
    private TextView tvDate;

    public Fg_PresensiPulang() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fg_PresensiPulang.
     */
    // TODO: Rename and change types and number of parameters
    public static Fg_PresensiPulang newInstance(String param1, String param2) {
        Fg_PresensiPulang fragment = new Fg_PresensiPulang();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fg__presensi_pulang, container, false);

        sharedPref = new SharedPref(getActivity());
        api_interface = Api_Client.getClient().create(Api_Interface.class);
        rvHistory = view.findViewById(R.id.rvHistory);
        tvDate = view.findViewById(R.id.tvDate);
        layoutManager = new LinearLayoutManager(getActivity());
        rvHistory.setLayoutManager(layoutManager);


        SimpleDateFormat sdf = new SimpleDateFormat("M");
        Date date = new Date();

        history_presensi(sdf.format(date));

        tvDate.setOnClickListener(l->{
            showDialogMonth();
        });

        return view;
    }

    private void showDialogMonth()
    {
        String[] listItems = {"Januari", "Februari", "Maret", "April", "Mei","Juni","Juli","Agustus","September","Oktober","November","Desember"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose item");

        int checkedItem = 1; //this will checked the item when user open the dialog
        builder.setSingleChoiceItems(listItems, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                history_presensi(String.valueOf(which+1));

            }
        });

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void history_presensi(String argument)
    {

        Call<List<Ent_Presensi>> showPresensiCall = api_interface.show_presensi_pulang(sharedPref.sp.getString("nip",""),argument);
        showPresensiCall.enqueue(new Callback<List<Ent_Presensi>>() {
            @Override
            public void onResponse(Call<List<Ent_Presensi>> call, Response<List<Ent_Presensi>> response) {
                List<Ent_Presensi> listPresensi = response.body();
                presensi_adapter = new Presensi_Adapter(getActivity(),listPresensi);
                rvHistory.setAdapter(presensi_adapter);
            }

            @Override
            public void onFailure(Call<List<Ent_Presensi>> call, Throwable t) {
                Toast toast = Toast.makeText(getActivity(),t.getMessage(),Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
