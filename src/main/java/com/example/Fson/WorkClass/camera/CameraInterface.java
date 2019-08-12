package com.example.Fson.WorkClass.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.util.Log;
import android.view.SurfaceHolder;

import com.example.Fson.ToolsClass.CameraHelper;
import com.example.Fson.WorkClass.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CameraInterface {
    public static final String TAG = "TAG";
    public Camera myCamera;
    public CamcorderProfile profile;
    public Camera.Parameters parameters;
    public float mPreviwRate = -1f;
    public static CameraInterface mCameraInterface;
    public static FocusCallBack mFocusCallBack;
    private static Context mcontext;
    public int DST_RECT_WIDTH, DST_RECT_HEIGHT;
    public int previewW = 6144, previewH = 3456;   //4608  3456    3456 * 1.7777
    public Rect rect = new Rect( -10, -10, 10, 10);

    public int zooms[] = {0, 14, 29, 44, 59};
    public boolean isUnlockCamera = false;
    public boolean isPreviewing = false, isCapturing = false;

	private CameraInterface(){}
	/* 对CameraInterface类的所有对象的getInstance同步 */
	public static synchronized CameraInterface getInstance(){
		if(mCameraInterface == null){
			mCameraInterface = new CameraInterface();
		}
		return mCameraInterface;
	}
	/* load传入参数 */
	public void loadFocusing(FocusCallBack focusCallBack, Context context){
        mFocusCallBack = focusCallBack;
        mcontext = context;
    }
	/** 使用Surfaceview预览
	 * @param mySurfaceHolder
	 */
	public void doStartPreview(SurfaceHolder mySurfaceHolder){
        if (isPreviewing){
            return;
        }
        myCamera = CameraHelper.getDefaultBackFacingCameraInstance();
        Log.i(TAG, "Camera open over....");
		Log.i(TAG, "doStartPreview...");
        if (myCamera == null) {
            return;
        }
        parameters = myCamera.getParameters();
        List<Size> mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
        List<Size> mSupportedPictureSizes = parameters.getSupportedPictureSizes();
        List<Size> mSupportedVideoSizes = parameters.getSupportedVideoSizes();
        Size optimalSize = CameraHelper.getOptimalVideoSize(mSupportedVideoSizes, mSupportedPreviewSizes, previewW, previewH);
        Size pictureSize = CameraHelper.getOptimalPictureSize(mSupportedPictureSizes, previewW, previewH);
        // Use the same size for recording profile.
        //profile.videoFrameWidth = optimalSize.width;
        //profile.videoFrameHeight = optimalSize.height;
        //use biggest video
        profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        profile.videoFrameWidth = 3840;
        profile.videoFrameHeight = 2160;
        // likewise for the camera object itself.
        parameters.setPreviewSize(optimalSize.width, optimalSize.height);
        //parameters.setPreviewSize(3840, 2160);
        //parameters.setPictureSize(pictureSize.width, pictureSize.height);
        parameters.setPictureSize(3840, 2160);
        //parameters.setPictureSize(3968, 2240);
        /*设置持续对焦
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        */
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);   //CAF
        if (parameters.getMaxNumFocusAreas() > 0) {
            Log.e(TAG, "parameters_Yes");
            List<Camera.Area> focusAreas = new ArrayList<>();
            focusAreas.add(new Camera.Area(rect, 1000));
            parameters.setFocusAreas(focusAreas);
        }
        parameters.setPictureFormat(PixelFormat.JPEG);// 设置照片输出格式
        parameters.set("jpeg-quality", 100);// 照片质量
        parameters.setZoom(zooms[0]);
        parameters.setPreviewFormat(ImageFormat.NV21);
        //parameters.setPreviewFpsRange(30000, 30000);
        parameters.setPreviewFrameRate(30);
        myCamera.setParameters(parameters);
        try {
            myCamera.setPreviewDisplay(mySurfaceHolder);
            myCamera.startPreview();
            isPreviewing = true;
            myCamera.cancelAutoFocus(); // 先取消掉所有对焦功能， 有些地方说要加在startPreview后面？
            myCamera.setAutoFocusMoveCallback(myAutoFocusMoveCallback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	/* 拍照 */
	public void doTakePicture(MainActivity mainActivity3){
		if(myCamera != null && isPreviewing && !isCapturing){
            isCapturing = true;
            myCamera.takePicture(mShutterCallback, null, mainActivity3);
		}
	}
	/* 快门音效 */
	ShutterCallback mShutterCallback = new ShutterCallback() 
	// 快门音效，默认“咔擦”
	{
		public void onShutter() {
			// TODO Auto-generated method stub
			Log.i(TAG, "myShutterCallback:onShutter...");
		}
	};
	PictureCallback mRawCallback = new PictureCallback() {
        /* 拍摄未压缩的原数据回调，可以为null */
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			Log.i(TAG, "myRawCallback:onPictureTaken...");
		}
	};

	/* 释放相机 */
    public void releaseCamera(){
        isPreviewing = false;
        mPreviwRate = -1f;
        if (myCamera != null) {
            if (isUnlockCamera) {
                myCamera.lock();
                isUnlockCamera = false;
            }
            myCamera.setPreviewCallback(null);
            myCamera.stopPreview();
            myCamera.release();
            myCamera = null;
        }
    }
    /* 产生移动触发对焦 */
    public Camera.AutoFocusMoveCallback myAutoFocusMoveCallback = new Camera.AutoFocusMoveCallback() {
        @Override
        public void onAutoFocusMoving(boolean start, Camera camera) {
            //Log.e("myAutoFocusMoveCallback", String.valueOf(start));
            mFocusCallBack.getFocusing(start);
        }
    };
    /* 操作手电筒 */
    public boolean openTorch(boolean isOpening) {
        if (myCamera != null) {
            if (parameters.getFlashMode() == null){
                /* 判断闪光灯是否存在 */
                return false;
            }
            if (isOpening){
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                myCamera.setParameters(parameters);
                return true;
            }else {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                myCamera.setParameters(parameters);
                return true;
            }
        }else {
            return false;
        }
    }
    /* ZOOM重置 */
    public void setZoom(int change){
        /* 判断是否支持Zoom */
        if (myCamera.getParameters().isZoomSupported())     /* myCamera.getParameters().isSmoothZoomSupported()) */
        {
            try {
                Camera.Parameters params = myCamera.getParameters();
                final int MAX = params.getMaxZoom();
                int zoomValue = params.getZoom();
                Log.i("ZOOM", "-----------------MAX:" + MAX + "   params: " + zoomValue);
                int index = 0;
                if (zoomValue != 0) index = (zoomValue + 1) / 15;
                if (change == -1){
                    if (index > 0) {
                        index --;
                    }
                } else if (change == 1){
                    if (index < 4) {
                        index ++;
                    }
                }
                params.setZoom(zooms[index]);
                myCamera.setParameters(params);
                Log.i("ZOOM", "-----------------MAX:" + MAX + "   params: " + zooms[index]);
            } catch (Exception e) {
                Log.i("ZOOM", "--------exception zoom");
                e.printStackTrace();
            }
        }else{
            Log.e("ZOOM", "--------the phone not support zoom");
        }
    }
}