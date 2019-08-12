package com.example.Fson.ToolsClass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

/**
 * Created by PC on 2018/1/19.
 */

public class MediaButtonReceiver extends BroadcastReceiver {
    private static String TAG = "MediaButtonReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        // 获得KeyEvent对象
        KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        //Log.e(TAG, "Action ---->" + action + "  KeyEvent ---->" + action.toString());
        if (Intent.ACTION_MEDIA_BUTTON.equals(action) && (event.ACTION_DOWN == event.getAction())) {
            // 获得按键码
            int keycode = event.getKeyCode();
            switch (keycode) {
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    //播放下一首
                    Intent nextIntent = new Intent();
                    nextIntent.setAction("android.intent.action.MEDIA_CLICK");
                    nextIntent.putExtra("keycode","next");
                    context.sendBroadcast(nextIntent);
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    //播放上一首
                    Intent previousIntent = new Intent();
                    previousIntent.setAction("android.intent.action.MEDIA_CLICK");
                    previousIntent.putExtra("keycode","previous");
                    context.sendBroadcast(previousIntent);
                    break;
                case KeyEvent.KEYCODE_HEADSETHOOK:
                    //中间按钮,暂停or播放
                    //可以通过发送一个新的广播通知正在播放的视频页面,暂停或者播放视频
                    Intent centerIntent = new Intent();
                    centerIntent.setAction("android.intent.action.MEDIA_CLICK");
                    centerIntent.putExtra("keycode","center");
                    context.sendBroadcast(centerIntent);
                default:
                    break;
            }
        } else if (Intent.ACTION_MEDIA_BUTTON.equals(action) && (event.ACTION_UP == event.getAction())){

        }
    }
}
