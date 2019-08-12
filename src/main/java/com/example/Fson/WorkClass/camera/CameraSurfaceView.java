package com.example.Fson.WorkClass.camera;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.Fson.WorkClass.MainActivity;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = "TAG";
	Context mContext;
	SurfaceHolder mySurfaceHolder;
    MainActivity mainActivity3;

	public CameraSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
        mySurfaceHolder = getHolder();
        mySurfaceHolder.setKeepScreenOn(true);
        //mySurfaceHolder.setFormat(PixelFormat.TRANSPARENT);//translucent半透明transparent͸透明
        mySurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mySurfaceHolder.addCallback(this);
	}

    /* surface生命周期 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i(TAG, "surfaceCreated...");
		if (CameraInterface.getInstance().isPreviewing){
			CameraInterface.getInstance().releaseCamera();
			CameraInterface.getInstance().doStartPreview(mySurfaceHolder);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.i(TAG, "surfaceChanged...");
		if (mySurfaceHolder.getSurface() == null){
            return;
        }
        try {
            CameraInterface.getInstance().doStartPreview(mySurfaceHolder);
        } catch (Exception e){
            Log.d("TAG", "Error starting camera preview: " + e.getMessage());
        }
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(TAG, "surfaceDestroyed...");
		CameraInterface.getInstance().releaseCamera();
	}

	public SurfaceHolder getSurfaceHolder(MainActivity Activity){
		mainActivity3 = Activity;
		return mySurfaceHolder;
	}
}
