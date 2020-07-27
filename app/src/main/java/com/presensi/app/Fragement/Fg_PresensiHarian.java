package com.presensi.app.Fragement;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.presensi.app.Adapter.Presensi_Adapter;
import com.presensi.app.Api.Api_Client;
import com.presensi.app.Api.Api_Interface;
import com.presensi.app.Model.Ent_Presensi;
import com.presensi.app.R;
import com.presensi.app.Util.SharedPref;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fg_PresensiHarian.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fg_PresensiHarian#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fg_PresensiHarian extends Fragment {
    private Api_Interface api_interface;
    private RecyclerView rvHistory;
    private RecyclerView.LayoutManager layoutManager;
    private Presensi_Adapter presensi_adapter;
    private SharedPref sharedPref;

    public Fg_PresensiHarian() {
        // Required empty public constructor
    }


    public static Fg_PresensiHarian newInstance(String param1, String param2) {
        Fg_PresensiHarian fragment = new Fg_PresensiHarian();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fg__presensi_harian, container, false);

        sharedPref = new SharedPref(getActivity());
        api_interface = Api_Client.getClient().create(Api_Interface.class);
        rvHistory = view.findViewById(R.id.rvHistory);
        layoutManager = new LinearLayoutManager(getActivity());
        rvHistory.setLayoutManager(layoutManager);

        history_presensi();

        return view;
    }


    private void history_presensi()
    {
        Call<List<Ent_Presensi>> showPresensiCall = api_interface.show_presensi_harian(sharedPref.sp.getString("nip",""));
        showPresensiCall.enqueue(new Callback<List<Ent_Presensi>>() {
            @Override
            public void onResponse(Call<List<Ent_Presensi>> call, Response<List<Ent_Presensi>> response) {
                List<Ent_Presensi> listPresensi = response.body();

                presensi_adapter = new Presensi_Adapter(getActivity(),listPresensi);
                rvHistory.setAdapter(presensi_adapter);
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
