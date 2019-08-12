package com.example.Fson.PublicClass;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.widget.Toast;

import com.serenegiant.common.R.string;
import com.serenegiant.dialog.MessageDialogFragment;
import com.serenegiant.utils.BuildCheck;
import com.serenegiant.utils.HandlerThreadHandler;
import com.serenegiant.utils.PermissionCheck;

public class BaseFragment extends Fragment {
    private static final String TAG = BaseFragment.class.getSimpleName();
    private final Handler mUIHandler = new Handler(Looper.getMainLooper());
    private final Thread mUiThread;
    private Handler mWorkerHandler;
    private long mWorkerThreadID;

    private Toast mToast;
    private ShowToastTask mShowToastTask;
    protected static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 74565;
    protected static final int REQUEST_PERMISSION_AUDIO_RECORDING = 2311527;
    protected static final int REQUEST_PERMISSION_NETWORK = 3430008;
    protected static final int REQUEST_PERMISSION_CAMERA = 5469762;

    public BaseFragment() {
        this.mUiThread = this.mUIHandler.getLooper().getThread();
        this.mWorkerThreadID = -1L;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.mWorkerHandler == null) {
            this.mWorkerHandler = HandlerThreadHandler.createHandler(TAG);
            this.mWorkerThreadID = this.mWorkerHandler.getLooper().getThread().getId();
        }
    }

    @Override
    public void onPause() {
        this.clearToast();
        super.onPause();
    }

    @Override
    public synchronized void onDestroy() {
        if (this.mWorkerHandler != null) {
            try {
                this.mWorkerHandler.getLooper().quit();
            } catch (Exception var2) {
            }

            this.mWorkerHandler = null;
        }
        super.onDestroy();
    }

    public final void runOnUiThread(Runnable task, long duration) {
        if (task != null) {
            this.mUIHandler.removeCallbacks(task);
            if (duration <= 0L && Thread.currentThread() == this.mUiThread) {
                try {
                    task.run();
                } catch (Exception var5) {
                    Log.w(TAG, var5);
                }
            } else {
                this.mUIHandler.postDelayed(task, duration);
            }

        }
    }

    public final void removeFromUiThread(Runnable task) {
        if (task != null) {
            this.mUIHandler.removeCallbacks(task);
        }
    }

    protected final synchronized void queueEvent(Runnable task, long delayMillis) {
        if (task != null && this.mWorkerHandler != null) {
            try {
                this.mWorkerHandler.removeCallbacks(task);
                if (delayMillis > 0L) {
                    this.mWorkerHandler.postDelayed(task, delayMillis);
                } else if (this.mWorkerThreadID == Thread.currentThread().getId()) {
                    task.run();
                } else {
                    this.mWorkerHandler.post(task);
                }
            } catch (Exception var5) {
            }
        }
    }

    protected final synchronized void removeEvent(Runnable task) {
        if (task != null) {
            try {
                this.mWorkerHandler.removeCallbacks(task);
            } catch (Exception var3) {
            }

        }
    }

    protected void showToast(@StringRes int msg, Object... args) {
        this.removeFromUiThread(this.mShowToastTask);
        this.mShowToastTask = new ShowToastTask(msg, args);
        this.runOnUiThread(this.mShowToastTask, 0L);
    }

    protected void clearToast() {
        this.removeFromUiThread(this.mShowToastTask);
        this.mShowToastTask = null;

        try {
            if (this.mToast != null) {
                this.mToast.cancel();
                this.mToast = null;
            }
        } catch (Exception var2) {
        }

    }

    private final class ShowToastTask implements Runnable {
        final int msg;
        final Object args;

        private ShowToastTask(@StringRes int msg, Object... args) {
            this.msg = msg;
            this.args = args;
        }

        public void run() {
            try {
                if (BaseFragment.this.mToast != null) {
                    BaseFragment.this.mToast.cancel();
                    BaseFragment.this.mToast = null;
                }

                if (this.args != null) {
                    String _msg = BaseFragment.this.getString(this.msg, new Object[]{this.args});
                    BaseFragment.this.mToast = Toast.makeText(BaseFragment.this.getActivity(), _msg, Toast.LENGTH_SHORT);
                } else {
                    BaseFragment.this.mToast = Toast.makeText(BaseFragment.this.getActivity(), this.msg, Toast.LENGTH_SHORT);
                }

                BaseFragment.this.mToast.show();
            } catch (Exception var2) {
            }

        }
    }

    @SuppressLint({"NewApi"})
    public void onMessageDialogResult(MessageDialogFragment dialog, int requestCode, String[] permissions, boolean result) {
        if (result && BuildCheck.isMarshmallow()) {
            this.requestPermissions(permissions, requestCode);
        } else {
            String[] var5 = permissions;
            int var6 = permissions.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                String permission = var5[var7];
                this.checkPermissionResult(requestCode, permission, PermissionCheck.hasPermission(this.getActivity(), permission));
            }

        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int n = Math.min(permissions.length, grantResults.length);

        for(int i = 0; i < n; ++i) {
            this.checkPermissionResult(requestCode, permissions[i], grantResults[i] == 0);
        }

    }

    protected void checkPermissionResult(int requestCode, String permission, boolean result) {
        if (!result && permission != null) {
            if ("android.permission.RECORD_AUDIO".equals(permission)) {
                this.showToast(string.permission_audio);
            }

            if ("android.permission.WRITE_EXTERNAL_STORAGE".equals(permission)) {
                this.showToast(string.permission_ext_storage);
            }

            if ("android.permission.INTERNET".equals(permission)) {
                this.showToast(string.permission_network);
            }
        }

    }

    protected boolean checkPermissionWriteExternalStorage() {
        if (!PermissionCheck.hasWriteExternalStorage(this.getActivity())) {
            MessageDialogFragment.showDialog(this, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE, string.permission_title, string.permission_ext_storage_request, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"});
            return false;
        } else {
            return true;
        }
    }

    protected boolean checkPermissionAudio() {
        if (!PermissionCheck.hasAudio(this.getActivity())) {
            MessageDialogFragment.showDialog(this, REQUEST_PERMISSION_AUDIO_RECORDING, string.permission_title, string.permission_audio_recording_request, new String[]{"android.permission.RECORD_AUDIO"});
            return false;
        } else {
            return true;
        }
    }

    protected boolean checkPermissionNetwork() {
        if (!PermissionCheck.hasNetwork(this.getActivity())) {
            MessageDialogFragment.showDialog(this, REQUEST_PERMISSION_NETWORK, string.permission_title, string.permission_network_request, new String[]{"android.permission.INTERNET"});
            return false;
        } else {
            return true;
        }
    }

    protected boolean checkPermissionCamera() {
        if (!PermissionCheck.hasCamera(this.getActivity())) {
            MessageDialogFragment.showDialog(this, REQUEST_PERMISSION_CAMERA, string.permission_title, string.permission_camera_request, new String[]{"android.permission.CAMERA"});
            return false;
        } else {
            return true;
        }
    }

}
