package com.example.Fson.WorkClass.camera;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.Fson.R;
import com.example.Fson.WorkClass.MainActivity;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usbcameracommon.UVCCameraHandlerMultiSurface;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

public class UVCCamera {
    /**
     * Tag for the {@link Log}.
     */
    private static final String TAG = "UVCCamera";
    private static final Boolean DEBUG = false;

    private Activity activity;
    private Context context;
    private Bundle savedInstanceState;
    /**
     * the fragment of UVCCamera
     */
    private UVCCameraFragment mUVCCameraFragment;
    /**
     * for accessing USB
     */
    private USBMonitor mUSBMonitor;
    private UsbDevice mUsbDevice;

    private static BooleanUvcCamera booleanUvcCamera;
    private BooleanUvcCamera mBooleanUvcCamera;

    private UVCCamera() {
    }

    public static synchronized UVCCamera getInstance(MainActivity mainActivity, Context context, final Bundle savedInstanceState){
        UVCCamera mUVCCamera = new UVCCamera();
        mUVCCamera.activity = (Activity)context;
        mUVCCamera.context = context;
        mUVCCamera.savedInstanceState = savedInstanceState;

        booleanUvcCamera = mainActivity;
        return mUVCCamera;
    }

    /**
     *
     */
    public void onStart() {
        mUSBMonitor = new USBMonitor(context, mOnDeviceConnectListener);
        if (null == savedInstanceState) {
            mUVCCameraFragment = mUVCCameraFragment.newInstance();
            activity.getFragmentManager().beginTransaction().add(R.id.container, mUVCCameraFragment).commit();
        }
        mUSBMonitor.register();
    }

    /**
     *
     */
    protected void onStop() {
    }

    /**
     *
     */
    public void onDestroy() {
        if (mUSBMonitor != null) {
            mUSBMonitor.destroy();
            mUSBMonitor = null;
        }
    }

    //================================================================================

    /**
     * usb device connect listeners
     * {@link USBMonitor.OnDeviceConnectListener}
     */
    private final USBMonitor.OnDeviceConnectListener mOnDeviceConnectListener = new USBMonitor.OnDeviceConnectListener() {
        @Override
        public void onAttach(UsbDevice usbDevice) {
            try {
                if (DEBUG) Toast.makeText(activity, "onAttach", Toast.LENGTH_LONG).show();
                if (usbDevice instanceof UsbDevice) {
//                    mUSBMonitor.requestPermission((UsbDevice)usbDevice);
                    mUsbDevice = usbDevice;
                    if (mBooleanUvcCamera != null) {
                        mBooleanUvcCamera.IsUvcCamera(true);
                    }
                }
            }catch (Exception e){
                StringWriter stringWriter = new StringWriter();
                e.printStackTrace(new PrintWriter(stringWriter));
                if (DEBUG) Toast.makeText(activity, "onAttach:error:\n" + stringWriter.toString(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

        @Override
        public void onDettach(UsbDevice usbDevice) {
            if (DEBUG) Toast.makeText(activity, "onDettach", Toast.LENGTH_LONG).show();
            if (mBooleanUvcCamera != null) {
                mBooleanUvcCamera.IsUvcCamera(false);
            }
        }

        @Override
        public void onConnect(UsbDevice usbDevice, USBMonitor.UsbControlBlock usbControlBlock, boolean b) {
            if (DEBUG) Toast.makeText(activity, "onConnect", Toast.LENGTH_LONG).show();
            FragmentManager fragmentManager = activity.getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.show(mUVCCameraFragment).commit();
            mUVCCameraFragment.onConnect(usbControlBlock);
//            if (mBooleanUvcCamera != null) {
//                mBooleanUvcCamera.IsUvcCamera(true);
//            }

//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    booleanUvcCamera.IsUvcCamera(false);
//                    mUVCCameraFragment.stopPreview();
//                }
//            }, 10000);
        }

        @Override
        public void onDisconnect(UsbDevice usbDevice, USBMonitor.UsbControlBlock usbControlBlock) {
            if (DEBUG) Toast.makeText(activity, "onDisconnect", Toast.LENGTH_LONG).show();
            try {
                FragmentManager fragmentManager = activity.getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if (null != mUVCCameraFragment) {
                    fragmentTransaction.hide(mUVCCameraFragment).commit();
                }
                mUVCCameraFragment.onDisconnect();
            } catch (final Exception e) {
                if (DEBUG) {
                    StringWriter stringWriter = new StringWriter();
                    e.printStackTrace(new PrintWriter(stringWriter));
                    Toast.makeText(activity, "onDisconnect():error:" + stringWriter, Toast.LENGTH_LONG).show();
                } else {
                    e.printStackTrace();
                }
                if (null != callBack) callBack.onDisconnectError();
            } finally {
//                mUSBMonitor.unregister();
                if (null != callBack) callBack.onDisconnect();
            }
        }

        @Override
        public void onCancel(UsbDevice usbDevice) {
            if (DEBUG) Toast.makeText(activity, "onCancel", Toast.LENGTH_LONG).show();
        }
    };


    public interface BooleanUvcCamera{
        void IsUvcCamera(Boolean b);
    }

    public void setIsUvcCamera(BooleanUvcCamera mBooleanUvcCamera) {
        this.mBooleanUvcCamera = mBooleanUvcCamera;
    }

    public void startPreview() {
        try {
            if (mUsbDevice instanceof UsbDevice) {
                mUSBMonitor.requestPermission((UsbDevice)mUsbDevice);
//                mBooleanUvcCamera.IsUvcCamera(true);
            }
        } catch (final Exception e) {
            if (DEBUG) {
                StringWriter stringWriter = new StringWriter();
                e.printStackTrace(new PrintWriter(stringWriter));
                Toast.makeText(activity, "startPreview():error:" + stringWriter, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void stopPreview() {
        if (mUsbDevice instanceof UsbDevice) {
            mUVCCameraFragment.stopPreview();
        }
    }

    //================================================================================

    public void captureStill(String path) {
        if (DEBUG) Toast.makeText(activity, "UVCCamera:captureStill:", Toast.LENGTH_LONG).show();
        mUVCCameraFragment.captureStill(path);
    }

    public void startRecording(File file) {
        if (DEBUG) Toast.makeText(activity, "UVCCamera:startRecording:", Toast.LENGTH_LONG).show();
        mUVCCameraFragment.startRecording(file);
    }

    public void stopRecording(String path) {
        if (DEBUG) Toast.makeText(activity, "UVCCamera:stopRecording:", Toast.LENGTH_LONG).show();
        mUVCCameraFragment.stopRecording(path);
    }

    public void addCallback(final UVCCameraHandlerMultiSurface.CameraCallback callback) {
        mUVCCameraFragment.setCallback(new UVCCameraFragment.Callback() {
            @Override
            public void onViewCreated(UVCCameraHandlerMultiSurface mCameraHandler) {
                if (null != mCameraHandler) mCameraHandler.addCallback(callback);
            }
        });
    }

    //================================================================================

    private OnDeviceConnectCallBack callBack;

    public interface OnDeviceConnectCallBack {
        void onDisconnect();
        void onDisconnectError();
    }

    public void setCallBack(OnDeviceConnectCallBack callBack) {
        this.callBack = callBack;
    }
}
