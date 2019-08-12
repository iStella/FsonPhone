package com.example.Fson.PreviewClass;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class MyPlayer implements OnBufferingUpdateListener, OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, SurfaceHolder.Callback {
    private int videoWidth;
    private int videoHeight;
    public MediaPlayer mediaPlayer;
    private SurfaceHolder surfaceHolder;
    private TextView btnPlayUrl, text_time;
    private int oldseconds = 0;
    private String totaltime = "00:00";
    private SeekBar skbProgress;
    public boolean isPlaying = false;
    private Timer mTimer = new Timer();

    public MyPlayer(SurfaceView surfaceView, SeekBar skbProgress, String dataPath, TextView btnPlayUrl, TextView text_time) {
        this.skbProgress = skbProgress;
        this.btnPlayUrl = btnPlayUrl;
        this.text_time = text_time;
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setKeepScreenOn(true);
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(this);
        try {
            mediaPlayer.setDataSource(dataPath);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mTimer.schedule(mTimerTask, 0, 5);
    }

    /*******************************************************
     * 通过定时器和Handler来更新进度条
     ******************************************************/
    TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            if(mediaPlayer == null) return;
            if (mediaPlayer.isPlaying() && skbProgress.isPressed() == false) {
                handleProgress.sendEmptyMessage(0);
            }
        }
    };

    Handler handleProgress = new Handler() {
        public void handleMessage(Message msg) {
            if (mediaPlayer != null){
                int position = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();
                if (totaltime.equals("00:00")){
                    int totalseconds = Integer.parseInt(totalMoney(duration / 1000)) + 1;
                    int tminus = totalseconds / 60;
                    int tseconds = totalseconds % 60;
                    String str_tminus = String.valueOf(tminus);
                    String str_tseconds = String.valueOf(tseconds);
                    if (tminus < 10){
                        str_tminus = "0" + str_tminus;
                    }
                    if (tseconds < 10){
                        str_tseconds = "0" + str_tseconds;
                    }
                    totaltime = str_tminus + ":" + str_tseconds;
                    text_time.setText("00:00" + "/" + totaltime);
                }
                String nowtime = "00:00";
                int nowseconds = Integer.parseInt(totalMoney(position / 1000));
                if (nowseconds > oldseconds){
                    int nminus = nowseconds / 60;
                    int nseconds = nowseconds % 60;
                    String str_nminus = String.valueOf(nminus);
                    String str_nseconds = String.valueOf(nseconds);
                    if (nminus < 10){
                        str_nminus = "0" + str_nminus;
                    }
                    if (nseconds < 10){
                        str_nseconds = "0" + str_nseconds;
                    }
                    nowtime = str_nminus + ":" + str_nseconds;
                    text_time.setText(nowtime + "/" + totaltime);
                }
                if (duration > 0) {
                    long pos = skbProgress.getMax() * position / duration;
                    skbProgress.setProgress((int) pos);
                }
            }
        };
    };
    public String totalMoney(double money) {
        java.math.BigDecimal bigDec = new java.math.BigDecimal(money);
        double total = bigDec.setScale(2, java.math.BigDecimal.ROUND_HALF_UP).doubleValue();
        DecimalFormat df = new DecimalFormat("0");
        return df.format(total);
    }

    //*****************************************************

    public void playUrl(String videoUrl) {
        try {
            mediaPlayer.setDataSource(videoUrl);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        mediaPlayer.pause();
        isPlaying = false;
    }

    public void play() {
        mediaPlayer.start();
        isPlaying = true;
    }

    public void reset(String videoUrl) {
        try {
            skbProgress.setProgress(0);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(videoUrl);
            mediaPlayer.setDisplay(surfaceHolder);
            mediaPlayer.prepareAsync();
            isPlaying = false;
        } catch (IllegalArgumentException e) {
            isPlaying = false;
            e.printStackTrace();
        } catch (IllegalStateException e) {
            isPlaying = false;
            e.printStackTrace();
        } catch (IOException e) {
            isPlaying = false;
            e.printStackTrace();
        }
    }

    public void restart(String videoUrl) {
        try {
            text_time.setText("00:00" + "/" + totaltime);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(videoUrl);
            mediaPlayer.setDisplay(surfaceHolder);
            mediaPlayer.prepareAsync();      //prepare之后自动播放
        } catch (IllegalArgumentException e) {
            isPlaying = false;
            e.printStackTrace();
        } catch (IllegalStateException e) {
            isPlaying = false;
            e.printStackTrace();
        } catch (IOException e) {
            isPlaying = false;
            e.printStackTrace();
        }
    }

    public void stop() {
        mediaPlayer.stop();
        isPlaying = false;
    }

    public void releaseMediaPlayer() {
        isPlaying = false;
        handleProgress.removeCallbacks(null);
        mTimerTask.cancel();
        mTimer.cancel();
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        Log.e("mediaPlayer", "aaa surface changed");
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        Log.e("mediaPlayer", "aaa surfaceCreated");
        try{
            mediaPlayer.setDisplay(surfaceHolder);
            mediaPlayer.prepareAsync();      //prepare之后自动播放
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        Log.e("mediaPlayer", "aaa surfaceDestroyed");
        releaseMediaPlayer();
    }

    @Override
    /**
     * 通过onPrepared播放
     */
    public void onPrepared(MediaPlayer player) {
        videoWidth = mediaPlayer.getVideoWidth();
        videoHeight = mediaPlayer.getVideoHeight();
        if (videoHeight != 0 && videoWidth != 0) {
            player.start();
            isPlaying = true;
        }
    }

    @Override
    public boolean onError(MediaPlayer player, int whatError, int extra) {
        switch (whatError) {
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.e("播放出错:::", "MEDIA_ERROR_SERVER_DIED");
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.e("播放出错:::", "MEDIA_ERROR_UNKNOWN");
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {

    }

    @Override
    public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
        skbProgress.setSecondaryProgress(bufferingProgress);
        int currentProgress = skbProgress.getMax() * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
        Log.e(currentProgress+"% play", bufferingProgress + "% buffer");
    }

}
