package com.example.Fson.WorkClass.camera;

import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.Fson.PublicClass.BaseFragment;
import com.example.Fson.R;
import com.serenegiant.usb.Size;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.UVCCamera;
import com.serenegiant.usbcameracommon.UVCCameraHandlerMultiSurface;
import com.serenegiant.widget.UVCCameraTextureView;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class UVCCameraFragment extends BaseFragment implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    /**
     * Tag for the {@link Log}.
     */
    private static final String TAG = "UVCCameraFragment";
    private static final Boolean DEBUG = false;


    /**
     * set true if you want to record movie using MediaSurfaceEncoder
     * (writing frame data into Surface camera from MediaCodec
     *  by almost same way as USBCameratest2)
     * set false if you want to record movie using MediaVideoEncoder
     */
    private static final boolean USE_SURFACE_ENCODER = false;
    /**
     * preview resolution(width)
     * if your camera does not support specific resolution and mode,
     * {@link UVCCamera#setPreviewSize(int, int, int)} throw exception
     */
//    private static final int PREVIEW_WIDTH = 1920;
    private static final int PREVIEW_WIDTH = 1280;//640
    /**
     * preview resolution(height)
     * if your camera does not support specific resolution and mode,
     * {@link UVCCamera#setPreviewSize(int, int, int)} throw exception
     */
//    private static final int PREVIEW_HEIGHT = 1080;
    private static final int PREVIEW_HEIGHT = 720;//480

    /**
     * preview mode
     * if your camera does not support specific resolution and mode,
     * {@link UVCCamera#setPreviewSize(int, int, int)} throw exception
     * 0:YUYV, other:MJPEG
     */
    private static final int PREVIEW_MODE = 1;
    /**
     * for accessing USB
     */
    private USBMonitor mUSBMonitor;
    /**
     * Handler to execute camera related methods sequentially on private thread
     */
    private UVCCameraHandlerMultiSurface mCameraHandler;
    private Callback callback;
    /**
     * for camera preview display
     */
    private UVCCameraTextureView mUVCCameraView;

    public static synchronized UVCCameraFragment newInstance() {
        return new UVCCameraFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_uvccamera, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        view.findViewById(R.id.takepic).setOnClickListener(this);

        mUVCCameraView = view.findViewById(R.id.camera_texture);
        mUVCCameraView.setOnLongClickListener(mOnLongClickListener);
        mUVCCameraView.setAspectRatio(PREVIEW_WIDTH / (float)PREVIEW_HEIGHT);

//        mUSBMonitor = new USBMonitor(getActivity(), mOnDeviceConnectListener);
        mCameraHandler = UVCCameraHandlerMultiSurface.createHandler(getActivity(), mUVCCameraView,
                USE_SURFACE_ENCODER ? 0 : 1, PREVIEW_WIDTH, PREVIEW_HEIGHT, PREVIEW_MODE);
        callback.onViewCreated(mCameraHandler);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
//        mUSBMonitor.register();
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();

        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).
        if (mUVCCameraView.isAvailable()) {
            openCamera(mUVCCameraView.getWidth(), mUVCCameraView.getHeight());
        } else {
//            mUVCCameraView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    @Override
    public void onStop() {
        stopPreview();
        mCameraHandler.close();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (mCameraHandler != null) {
            mCameraHandler.release();
            mCameraHandler = null;
        }
//        if (mUSBMonitor != null) {
//            mUSBMonitor.destroy();
//            mUSBMonitor = null;
//        }
        mUVCCameraView = null;
        super.onDestroy();
    }

    //================================================================================

    private void openCamera(int width, int height) {

    }

    private void closeCamera() {

    }

    private void startBackgroundThread() {

    }

    private void stopBackgroundThread() {

    }

    @Override
    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.picture: {
//                takePicture();
//                break;
//            }
//        }
    }

    /**
     * capture still image when you long click on preview image(not on buttons)
     */
    private final View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(final View view) {
//            switch (view.getId()) {
//                case R.id.camera_view:
//                    if (mCameraHandler.isOpened()) {
//                        if (checkPermissionWriteExternalStorage()) {
//                            mCameraHandler.captureStill();
//                        }
//                        return true;
//                    }
//            }
            return false;
        }
    };

    //================================================================================

    /**
     * usb device connect listeners
     * {@link USBMonitor.OnDeviceConnectListener}
     */
    private final USBMonitor.OnDeviceConnectListener mOnDeviceConnectListener = new USBMonitor.OnDeviceConnectListener() {
        @Override
        public void onAttach(UsbDevice usbDevice) {
            if (usbDevice instanceof UsbDevice) {
                mUSBMonitor.requestPermission((UsbDevice)usbDevice);
            }
        }

//        @Override
        public void onDettach(UsbDevice usbDevice) {

        }

        @Override
        public void onConnect(UsbDevice usbDevice, USBMonitor.UsbControlBlock usbControlBlock, boolean b) {
            mCameraHandler.open(usbControlBlock);
            startPreview();
        }

        @Override
        public void onDisconnect(UsbDevice usbDevice, USBMonitor.UsbControlBlock usbControlBlock) {
            if (mCameraHandler != null) {
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        stopPreview();
                    }
                }, 0);
            }
        }

        @Override
        public void onCancel(UsbDevice usbDevice) {

        }
    };

    //================================================================================

    /**
     * for camera preview display
     */
    private int mPreviewSurfaceId;
    private void startPreview() {
        if (DEBUG) Toast.makeText(getActivity(), "startPreview()", Toast.LENGTH_LONG).show();

        mCameraHandler.startPreview();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (DEBUG) Toast.makeText(getActivity(), "startPreview():runOnUiThread", Toast.LENGTH_LONG).show();
                try {
                    mUVCCameraView = getActivity().findViewById(R.id.camera_texture);
                    final SurfaceTexture st = mUVCCameraView.getSurfaceTexture();
                    if (st != null) {
                        final Surface surface = new Surface(st);
                        mPreviewSurfaceId = surface.hashCode();
                        mCameraHandler.addSurface(mPreviewSurfaceId, surface, false);
                    }
                    mCameraHandler.setAutoFocus(true);
                } catch (final Exception e) {
                    Log.w(TAG, e);
                    if (DEBUG) {
                        StringWriter stringWriter = new StringWriter();
                        e.printStackTrace(new PrintWriter(stringWriter));
                        Toast.makeText(getActivity(), "startPreview():try:error:" + stringWriter, Toast.LENGTH_LONG).show();
                    }
                }
                if (DEBUG) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            List<Size> list = getSupportedSizeList();
                            String s = "USBCamera support size\n";
                            if (list != null) {
                                for (Size size : list){
                                    s += String.valueOf(size.width) + "\n";
                                    s += String.valueOf(size.height) + "\n";
                                    s += "------------------------" + "\n";
                                    Log.e("pre", String.valueOf(size.width));
                                    Log.e("pre", String.valueOf(size.height));
                                    Log.e("pre", "----------------");
                                }
                                Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
                            }
                        }
                    }, 5000);
                }
            }
        });
    }

    public void stopPreview() {
        try {
            if (mPreviewSurfaceId != 0) {
                mCameraHandler.removeSurface(mPreviewSurfaceId);
                mPreviewSurfaceId = 0;
            }
            mCameraHandler.close();
        } catch (Exception e) {
            if (DEBUG) {
                StringWriter stringWriter = new StringWriter();
                e.printStackTrace(new PrintWriter(stringWriter));
                Toast.makeText(getActivity(), stringWriter.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    //================================================================================


    protected void onConnect(USBMonitor.UsbControlBlock usbControlBlock) {
        mCameraHandler.open(usbControlBlock);
        if (DEBUG) Toast.makeText(getActivity(), "USBCamera support size:ready", Toast.LENGTH_LONG).show();
        startPreview();
    }

    protected void onDisconnect() {
        if (mCameraHandler != null) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    stopPreview();
                }
            }, 0);
        }
    }

    //================================================================================

    public void captureStill(String path) {
        if (DEBUG) Toast.makeText(getActivity(), "UVCCameraFragment:captureStill:", Toast.LENGTH_LONG).show();
        if (mCameraHandler.isOpened()) {
            if (checkPermissionWriteExternalStorage()) {
                if (DEBUG) Toast.makeText(getActivity(), "path:" + path, Toast.LENGTH_LONG).show();
                if (path.equals("")) {
                    mCameraHandler.captureStill();
                } else {
                    mCameraHandler.captureStill(path);
                }
            }
        }
    }

    protected void startRecording(File file) {
        if (DEBUG) Toast.makeText(getActivity(), "UVCCameraFragment:startRecording:", Toast.LENGTH_LONG).show();
        if (DEBUG) Toast.makeText(getActivity(), "UVCCameraFragment:\n" + file.toString(), Toast.LENGTH_LONG).show();
        try {
            if (mCameraHandler.isOpened()) {
                if (DEBUG) Toast.makeText(getActivity(), "UVCCameraFragment:mCameraHandler:isOpened:true", Toast.LENGTH_LONG).show();
                if (checkPermissionWriteExternalStorage() && checkPermissionAudio()) {
                    if (!mCameraHandler.isRecording()) {
                        if (DEBUG) Toast.makeText(getActivity(), "UVCCameraFragment:startRecording", Toast.LENGTH_LONG).show();
                        mCameraHandler.startRecording(file);
                    }
                }
            }
        } catch (Exception e) {
            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            if (DEBUG) Toast.makeText(getActivity(), "mCameraHandler.startRecording:\n" + stringWriter, Toast.LENGTH_LONG).show();
        }
    }

    protected void stopRecording(String path) {
        if (mCameraHandler.isOpened()) {
            if (checkPermissionWriteExternalStorage() && checkPermissionAudio()) {
                if (mCameraHandler.isRecording()) {
//                    mCameraHandler.stopRecording();
//                    mCameraHandler.updateMedia(path);
                    mCameraHandler.stopRecording(path);
                }
            }
        }
    }

    protected List<Size> getSupportedSizeList() {
        try {
            return mCameraHandler.getSupportedSizeList();
        } catch (Exception e) {
            if (DEBUG) {
                StringWriter stringWriter = new StringWriter();
                e.printStackTrace(new PrintWriter(stringWriter));
                Toast.makeText(getActivity(), "getSupportedSizeList:error:\n" + stringWriter.toString(), Toast.LENGTH_LONG).show();
            } else {
                e.printStackTrace();
            }
        }
        return null;
    }

    public interface Callback {
        void onViewCreated(UVCCameraHandlerMultiSurface mCameraHandler);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

//    public void addCallback(UVCCameraHandlerMultiSurface.CameraCallback callback) {
//        if (null != mCameraHandler) mCameraHandler.addCallback(callback);
//    }

}
