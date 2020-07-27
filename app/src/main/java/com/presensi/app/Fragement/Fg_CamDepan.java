package com.presensi.app.Fragement;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.presensi.app.R;
import com.presensi.app.Util.CameraControllerV2WithPreview_depan;
import com.presensi.app.Util.CameraControllerV2WithoutPreview;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fg_CamDepan.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fg_CamDepan#newInstance} factory method to
 * create an instance of this fragment.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Fg_CamDepan extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {

    private OnFragmentInteractionListener mListener;
    CameraControllerV2WithPreview_depan ccv2WithPreview;
    CameraControllerV2WithoutPreview ccv2WithoutPreview;

    private TextureView textureView;

    public Fg_CamDepan() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fg_CamDepan.
     */
    // TODO: Rename and change types and number of parameters
    public static Fg_CamDepan newInstance(String param1, String param2) {
        Fg_CamDepan fragment = new Fg_CamDepan();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fg__cam_depan, container, false);
        textureView =  view.findViewById(R.id.textureview);
            getPermissions();
                ccv2WithPreview = new CameraControllerV2WithPreview_depan(getActivity(), textureView, false);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    //======================Camera Api 2=========================
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(ccv2WithPreview != null) {
            ccv2WithPreview.closeCamera();
        }
        if(ccv2WithoutPreview != null) {
            ccv2WithoutPreview.closeCamera();
        }
    }

    private void getPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                //Requesting permission.
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @Override //Override from ActivityCompat.OnRequestPermissionsResultCallback Interface
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                }
                return;
            }
        }
    }
}
