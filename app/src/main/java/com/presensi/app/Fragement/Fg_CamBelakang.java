package com.presensi.app.Fragement;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Size;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.presensi.app.R;
import com.presensi.app.Util.CameraControllerV2WithPreview;
import com.presensi.app.Util.CameraControllerV2WithoutPreview;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fg_CamBelakang.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fg_CamBelakang#newInstance} factory method to
 * create an instance of this fragment.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Fg_CamBelakang extends Fragment {
    private TextureView textureView;
    //    Switch startstoppreview;
    Button camDepan,camBack;
    private Size previewsize;
    private Size jpegSizes[] = null;
    //    private TextureView textureView;
    private CameraDevice cameraDevice;
    private CaptureRequest.Builder previewBuilder;
    private CameraCaptureSession previewSession;
    Button getpicture;
//    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
//
//    static {
//        ORIENTATIONS.append(Surface.ROTATION_0, 90);
//        ORIENTATIONS.append(Surface.ROTATION_90, 0);
//        ORIENTATIONS.append(Surface.ROTATION_180, 270);
//        ORIENTATIONS.append(Surface.ROTATION_270, 180);
//    }

    private OnFragmentInteractionListener mListener;

    CameraControllerV2WithPreview ccv2WithPreview;
    CameraControllerV2WithoutPreview ccv2WithoutPreview;


    public Fg_CamBelakang() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fg_CamBelakang.
     */
    // TODO: Rename and change types and number of parameters
    public static Fg_CamBelakang newInstance(String param1, String param2) {
        Fg_CamBelakang fragment = new Fg_CamBelakang();
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
        View view = inflater.inflate(R.layout.fragment_fg__cam_belakang, container, false);
        textureView =  view.findViewById(R.id.textureview);
        getPermissions();
        ccv2WithPreview = new CameraControllerV2WithPreview(getActivity(), textureView, true);

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

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
    //==========================================================================================================
//    void getPicture() {
//        if (cameraDevice == null) {
//            return;
//        }
//        CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
//        try {
//            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
//            if (characteristics != null) {
//                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
//            }
//            int width = 640, height = 480;
//            if (jpegSizes != null && jpegSizes.length > 0) {
//                width = jpegSizes[0].getWidth();
//                height = jpegSizes[0].getHeight();
//            }
//            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
//            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
//            outputSurfaces.add(reader.getSurface());
//            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
//            final CaptureRequest.Builder capturebuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
//            capturebuilder.addTarget(reader.getSurface());
//            capturebuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
//            int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
//            capturebuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
//            ImageReader.OnImageAvailableListener imageAvailableListener = new ImageReader.OnImageAvailableListener() {
//                @Override
//                public void onImageAvailable(ImageReader reader) {
//                    Image image = null;
//                    try {
//                        image = reader.acquireLatestImage();
//                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
//                        byte[] bytes = new byte[buffer.capacity()];
//                        buffer.get(bytes);
//                        save(bytes);
//                    } catch (Exception ee) {
//                    } finally {
//                        if (image != null)
//                            image.close();
//                    }
//                }
//
//                void save(byte[] bytes) {
//                    File file12 = getOutputMediaFile();
////                    File file = getOutputMediaFile();
////                    Intent intent = new Intent(getActivity(), Presence_Activity.class);
////                    intent.putExtra("presensi", getIntent().getExtras().getString("presensi"));
////                    intent.putExtra("imageUri", PhotoProvider.getPhotoUri(new File(file12, "cropped")).toString());
////                    intent.putExtra("currentPhotoPath",file12.getAbsolutePath());
////                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                    startActivity(intent);
//
////                    Toast.makeText(getApplication(),file12.getAbsolutePath(),Toast.LENGTH_LONG).show();
//                    OutputStream outputStream = null;
//                    try {
//                        outputStream = new FileOutputStream(file12);
//                        outputStream.write(bytes);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    } finally {
//                        try {
//                            if (outputStream != null)
//                                outputStream.close();
//                        } catch (Exception e) {
//                        }
//                    }
//                }
//            };
//            HandlerThread handlerThread = new HandlerThread("takepicture");
//            handlerThread.start();
//            final Handler handler = new Handler(handlerThread.getLooper());
//            reader.setOnImageAvailableListener(imageAvailableListener, handler);
//            final CameraCaptureSession.CaptureCallback previewSSession = new CameraCaptureSession.CaptureCallback() {
//                @Override
//                public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
//                    super.onCaptureStarted(session, request, timestamp, frameNumber);
//                }
//
//                @Override
//                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
//                    super.onCaptureCompleted(session, request, result);
//                    startCamera();
//                }
//            };
//            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
//                @Override
//                public void onConfigured(CameraCaptureSession session) {
//                    try {
//                        session.capture(capturebuilder.build(), previewSSession, handler);
//                    } catch (Exception e) {
//                    }
//                }
//
//                @Override
//                public void onConfigureFailed(CameraCaptureSession session) {
//                }
//            }, handler);
//
//        } catch (Exception e) {
//        }
//
//    }

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    public void openCamera() {
//        CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
//        try {
//            String camerId = manager.getCameraIdList()[0];
//            CameraCharacteristics characteristics = manager.getCameraCharacteristics(camerId);
//            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
//            previewsize = map.getOutputSizes(SurfaceTexture.class)[0];
//            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            manager.openCamera(camerId, stateCallback, null);
//        }catch (Exception e)
//        {
//        }
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private TextureView.SurfaceTextureListener surfaceTextureListener=new TextureView.SurfaceTextureListener() {
//        @Override
//        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//            openCamera();
//        }
//        @Override
//        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//            openCamera();
//        }
//        @Override
//        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//
//            if(cameraDevice != null){
//                cameraDevice.close();
//
//                cameraDevice = null;
//            }
//
//            return false;
//        }
//        @Override
//        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//
//        }
//    };
//    private CameraDevice.StateCallback stateCallback=new CameraDevice.StateCallback() {
//        @Override
//        public void onOpened(CameraDevice camera) {
//            cameraDevice=camera;
//            startCamera();
//        }
//        @Override
//        public void onDisconnected(CameraDevice camera) {
//        }
//        @Override
//        public void onError(CameraDevice camera, int error) {
//        }
//    };
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        if(cameraDevice!=null)
//        {
//            cameraDevice.close();
//        }
//    }
//    void  startCamera()
//    {
//        if(cameraDevice==null||!textureView.isAvailable()|| previewsize==null)
//        {
//            return;
//        }
//        SurfaceTexture texture=textureView.getSurfaceTexture();
//        if(texture==null)
//        {
//            return;
//        }
//        texture.setDefaultBufferSize(previewsize.getWidth(),previewsize.getHeight());
//        Surface surface=new Surface(texture);
//        try
//        {
//            previewBuilder=cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//        }catch (Exception e)
//        {
//        }
//        previewBuilder.addTarget(surface);
//        try
//        {
//            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
//                @Override
//                public void onConfigured(CameraCaptureSession session) {
//                    previewSession=session;
//                    getChangedPreview();
//                }
//                @Override
//                public void onConfigureFailed(CameraCaptureSession session) {
//                }
//            },null);
//        }catch (Exception e)
//        {
//        }
//    }
//    void getChangedPreview()
//    {
//        if(cameraDevice==null)
//        {
//            return;
//        }
//        previewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
//        HandlerThread thread=new HandlerThread("changed Preview");
//        thread.start();
//        Handler handler=new Handler(thread.getLooper());
//        try
//        {
//            previewSession.setRepeatingRequest(previewBuilder.build(), null, handler);
//        }catch (Exception e){}
//    }
//
//    private static File getOutputMediaFile() {
//        File mediaStorageDir = new File(
//                Environment
//                        .getExternalStorageDirectory(),
//                "Camera2Test");
//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//                Log.d("Camera2Test", "failed to create directory");
//                return null;
//            }
//        }
//        // Create a media file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
//                .format(new Date());
//        File mediaFile;
//        mediaFile = new File(mediaStorageDir.getPath() + File.separator
//                + "IMG_" + timeStamp + ".jpg");
//        return mediaFile;
//    }
}
