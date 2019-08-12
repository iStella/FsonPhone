package com.example.Fson.WorkClass.camera;

import android.os.Handler;
import android.os.Message;

public class UVCCameraHandler extends Handler {
    public static final int MSG_STOP_RECORDING = 0;
    public static final int MSG_UPDATE_MEDIA = 1;

    private UVCCameraHandlerListeners listeners;

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == MSG_UPDATE_MEDIA) {
            listeners.updateMedia();
        }
    }

    public interface UVCCameraHandlerListeners {
        void updateMedia();
    }

    public void setListeners(UVCCameraHandlerListeners listeners) {
        this.listeners = listeners;
    }
}
