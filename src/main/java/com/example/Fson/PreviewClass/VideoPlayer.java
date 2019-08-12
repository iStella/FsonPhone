package com.example.Fson.PreviewClass;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.Fson.BeanClass.ItemBean;
import com.example.Fson.ControlClass.WorkDb;
import com.example.Fson.R;
import com.example.Fson.ToolsClass.NumberFormatClass;
import com.example.Fson.WorkClass.Speech.SpeechCallBack;
import com.example.Fson.WorkClass.Speech.SpeechRecognition;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class VideoPlayer extends Activity implements SpeechCallBack{
    private SpeechRecognition mSpeechRecognition;
    private SurfaceView surfaceView;
    private LinearLayout line2, line_move;
    private int window_width, window_height;
    private TextView btnPause, btnPlayUrl, text_time;
    private LinearLayout line_delete;
    private boolean is_delete = false;
    private ImageView play_img;
    private String dataPath;
    private SeekBar skbProgress;
    private MyPlayer player;
    private static final String EXITACTION = "action.exit";
    private WorkDb workDb;
    private String foreign_title_id = "";
    private String title = "";
    private String foreign_step_id = "";
    private String work_id = "";
    private String url = "";
    private String bad_url = "";
    private String[] urls;
    private int order, item_num;
    private ItemBean itemBean;
    private ImageView point_icon1, point_icon2, point_icon3;
    private Handler pointHander = new Handler();
    private TextView wakeup;
    private NumberFormatClass numberFormatClass;
    private ArrayList<String> numStrings = new ArrayList<>();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.player_activity);

        window_height = this.getWindowManager().getDefaultDisplay().getHeight();
        surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView1);
        line_move = (LinearLayout)findViewById(R.id.line_move);
        Handler mHandler = new Handler();
        mHandler.postDelayed(runnable, 3000);
        line2 = (LinearLayout)findViewById(R.id.line2);
        line2.getBackground().setAlpha(170);
        wakeup = (TextView) findViewById(R.id.wakeup);
        text_time = (TextView) this.findViewById(R.id.text_time);
        btnPause = (TextView) this.findViewById(R.id.btnPause);
        btnPlayUrl = (TextView) this.findViewById(R.id.btnPlayUrl);
        line_delete = (LinearLayout) findViewById(R.id.line_delete);
        play_img = (ImageView) findViewById(R.id.play_img);
        skbProgress = (SeekBar) this.findViewById(R.id.skbProgress);
        skbProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
        point_icon1 = (ImageView) findViewById(R.id.point_icon1);
        point_icon2 = (ImageView) findViewById(R.id.point_icon2);
        point_icon3 = (ImageView) findViewById(R.id.point_icon3);

        mSpeechRecognition = SpeechRecognition.getInstance();
        workDb = new WorkDb(this, "");
        Bundle bundle = this.getIntent().getExtras();
        foreign_title_id = bundle.getString("foreign_title_id");
        title = bundle.getString("title");
        foreign_step_id = bundle.getString("foreign_step_id");
        work_id = bundle.getString("work_id");
        dataPath = bundle.getString("dataPath");
        order = bundle.getInt("order");
        item_num = bundle.getInt("item_num");
        itemBean = workDb.getItem(item_num);
        bad_url = workDb.getLog(foreign_title_id, foreign_step_id, work_id).getBad_url();
        url = workDb.getLog(foreign_title_id, foreign_step_id, work_id).getUrl();
        if (bad_url.length() > 0){
            if (url.length() > 0){
                url = bad_url + "," + url;
            }else {
                url = bad_url;
            }
        }
        urls = url.split(",");

        /* 初始化定义转义字符 */
        numberFormatClass = new NumberFormatClass();
        String[] nativenum = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "百"};
        for (String num : nativenum) {
            numStrings.add(num);
        }

        wakeup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpeechRecognition.startSpeech();
            }
        });
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //ObjectAnimator animator = ObjectAnimator.ofFloat(line1, "Y", line1.getTop(), window_height - line1.getHeight() + line_move.getHeight());
            //animator.setDuration(500);
            //animator.start();
        }
    };

    @Override
    public void getResult(String result) {
        switch (result) {
            case "播放":
                mSpeechRecognition.startMediaPlay();
                if (!player.isPlaying){
                    btnPause.setText("暂停");
                    play_img.setBackground(getResources().getDrawable(R.drawable.videopause));
                    player.play();
                }
                mSpeechRecognition.startSpeech();
                break;
            case "暂停":
                mSpeechRecognition.startMediaPlay();
                if (player.isPlaying) {
                    btnPause.setText("播放");
                    play_img.setBackground(getResources().getDrawable(R.drawable.play));
                    player.pause();
                    /*
                    ObjectAnimator animator = ObjectAnimator.ofFloat(line1, "Y", window_height - line1.getHeight() + line_move.getHeight(), window_height - line1.getHeight());
                    animator.setDuration(500);
                    animator.start();
                    */
                }
                mSpeechRecognition.startSpeech();
                break;
            case "停播":
                mSpeechRecognition.startMediaPlay();
                btnPause.setText("播放");
                play_img.setBackground(getResources().getDrawable(R.drawable.play));
                player.stop();
                finish();
                break;
            case "重播":
                mSpeechRecognition.startMediaPlay();
                btnPause.setText("暂停");
                play_img.setBackground(getResources().getDrawable(R.drawable.videopause));
                player.restart(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "FSON/" + itemBean.getTitle() + "/" + work_id + "/" + foreign_title_id + "." + title + "/" + foreign_title_id + "." + foreign_step_id + "/" + dataPath);
                mSpeechRecognition.startSpeech();
                break;
            case "删除":
                mSpeechRecognition.startMediaPlay();
                is_delete = true;
                line_delete.setVisibility(View.VISIBLE);
                mSpeechRecognition.startSpeech();
                break;
            case "确定":
                mSpeechRecognition.startMediaPlay();
                if (is_delete){
                    is_delete = false;
                    line_delete.setVisibility(View.GONE);
                    /*
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "FSON/" + itemBean.getTitle() + "/" + work_id  + "/" + itemBean.getTitle() + "/" + work_id + "/" + foreign_title_id + "." + title + "/" + foreign_title_id + "." + foreign_step_id + "/" + dataPath);
                    if (file.exists()){
                        file.delete();
                    }
                    */
                    String url = "";
                    String url1 = workDb.getLog(foreign_title_id, foreign_step_id, work_id).getBad_url();
                    String url2 = workDb.getLog(foreign_title_id, foreign_step_id, work_id).getUrl();
                    String[] url1s = new String[]{};
                    String[] url2s = new String[]{};
                    if (url1.length() > 0) url1s = url1.split(",");
                    if (url2.length() > 0) url2s = url2.split(",");
                    if (order > url1s.length - 1){
                        for (int i = 0 ; i < urls.length; i ++){
                            if (i != order && i > url1s.length - 1){
                                if (url.equals("")){
                                    url = urls[i];
                                }else {
                                    url += "," + urls[i];
                                }
                            }
                        }
                        workDb.updateurl(foreign_title_id, foreign_step_id, work_id, url);
                    }else {
                        for (int i = 0 ; i < urls.length; i ++){
                            if (i != order && i < urls.length - url2s.length){
                                if (url.equals("")){
                                    url = urls[i];
                                }else {
                                    url += "," + urls[i];
                                }
                            }
                        }
                        workDb.updatebadurl(foreign_title_id, foreign_step_id, work_id, url);
                    }
                    if (!url.equals("")){
                        urls = url.split(",");
                        if (urls.length > 0){
                            if (order > 0){
                                order --;
                                targetPreview();
                            }else if (order == 0){
                                order = urls.length - 1;
                                targetPreview();
                            }
                        }
                    }
                    finish();
                }else {
                    mSpeechRecognition.startSpeech();
                }
                break;
            case "取消":
                mSpeechRecognition.startMediaPlay();
                if (is_delete){
                    is_delete = false;
                    line_delete.setVisibility(View.GONE);
                }
                mSpeechRecognition.startSpeech();
                break;
            case "上一张":
                mSpeechRecognition.startMediaPlay();
                if (order < urls.length - 1){
                    order ++;
                    targetPreview();
                    finish();
                } else if (order == urls.length - 1){
                    order = 0;
                    targetPreview();
                    finish();
                }
                break;
            case "下一张":
                mSpeechRecognition.startMediaPlay();
                if (order > 0 ){
                    order --;
                    targetPreview();
                    finish();
                }else if (order == 0){
                    order = urls.length - 1;
                    targetPreview();
                    finish();
                }
                break;
            case "后退":
                mSpeechRecognition.startMediaPlay();
                this.finish();
                break;
            case "退出":
                mSpeechRecognition.startMediaPlay();
                TimerTask task = new TimerTask() {
                    public void run() {
                        Intent exitintent = new Intent(EXITACTION);
                        sendBroadcast(exitintent);
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task, 1000);
                break;
            default:
                Log.e("Main-result", result);
                if (result.length() > 6){
                    if (result.substring(0, 6).equals("select")){
                        mSpeechRecognition.startMediaPlay();
                        String value = result.substring(6, result.length());
                        /* 字符分割 */
                        ArrayList<String> resultList = new ArrayList<>();
                        String[] results = value.split("");
                        for (String string : results){
                            if (string.length() != 0){
                                resultList.add(string);
                                Log.e("string", string);
                            }
                        }
                        /* 判读是否属于数字区间 */
                        boolean goOn = true;
                        for (String num : resultList){
                            Log.e("numStrings.indexOf(num)", String.valueOf(numStrings.indexOf(num)));
                            if (numStrings.indexOf(num) < 0){
                                goOn = false;
                            }
                        }
                        if (goOn){
                            int index = numberFormatClass.chineseNumberInt(result) - 1;
                            if (index >= 0 && index < urls.length){
                                order = index;
                                targetPreview();
                                finish();
                            }else {
                                Log.e("无匹配项，重新加载listening", result);
                                mSpeechRecognition.startSpeech();
                            }
                        }else {
                            Log.e("无匹配项，重新加载listening", result);
                            mSpeechRecognition.startSpeech();
                        }
                    }else {
                        Log.e("无匹配项，重新加载listening", result);
                        mSpeechRecognition.startSpeech();
                    }
                }else {
                    Log.e("无匹配项，重新加载listening", result);
                    mSpeechRecognition.startSpeech();
                }
                break;
        }
    }
    @Override
    public void getpoint(boolean speechpoint){
        if (speechpoint) {
            point_icon1.setImageDrawable(null);
            point_icon2.setImageDrawable(null);
            point_icon3.setImageDrawable(null);
            pointHander.postDelayed(pointThread1, 250);
        } else {
            point_icon1.setImageDrawable(getResources().getDrawable(R.drawable.point_icon1));
            point_icon2.setImageDrawable(getResources().getDrawable(R.drawable.point_icon1));
            point_icon3.setImageDrawable(getResources().getDrawable(R.drawable.point_icon1));
            pointHander.removeCallbacks(pointThread1);
            pointHander.removeCallbacks(pointThread2);
        }
    }
    private Thread pointThread1 = new Thread(){
        @Override
        public void run(){
            point_icon1.setImageDrawable(getResources().getDrawable(R.drawable.point_icon2));
            point_icon2.setImageDrawable(getResources().getDrawable(R.drawable.point_icon2));
            point_icon3.setImageDrawable(getResources().getDrawable(R.drawable.point_icon2));
            pointHander.postDelayed(pointThread2, 250);
        }
    };
    private Thread pointThread2 = new Thread(){
        @Override
        public void run(){
            point_icon1.setImageDrawable(null);
            point_icon2.setImageDrawable(null);
            point_icon3.setImageDrawable(null);
            pointHander.postDelayed(pointThread1, 250);
        }
    };

    /* 跳转预览界面 */
    public void targetPreview(){
        dataPath = urls[order];
        if (dataPath.substring(dataPath.length() - 3 , dataPath.length()).equals("jpg") || dataPath.substring(dataPath.length() - 3 , dataPath.length()).equals("png")) {
            Intent intent = new Intent(VideoPlayer.this, ImageShow.class);
            Bundle bundle = new Bundle();
            bundle.putString("foreign_title_id", foreign_title_id);
            bundle.putString("title", title);
            bundle.putString("foreign_step_id", foreign_step_id);
            bundle.putString("work_id", work_id);
            bundle.putString("dataPath", dataPath);
            bundle.putInt("order", order);
            bundle.putInt("item_num", item_num);
            intent.putExtras(bundle);
            startActivity(intent);
        }else if (dataPath.substring(dataPath.length() - 3 , dataPath.length()).equals("mp4")){
            Intent intent = new Intent(VideoPlayer.this, VideoPlayer.class);
            Bundle bundle = new Bundle();
            bundle.putString("foreign_title_id", foreign_title_id);
            bundle.putString("title", title);
            bundle.putString("foreign_step_id", foreign_step_id);
            bundle.putString("work_id", work_id);
            bundle.putString("dataPath", dataPath);
            bundle.putInt("order", order);
            bundle.putInt("item_num", item_num);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
        int progress;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
            this.progress = progress * player.mediaPlayer.getDuration() / seekBar.getMax();
            if (progress >= 98){
                //player.restart(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "FSON/" + itemBean.getTitle() + "/" + work_id  + "/" + itemBean.getTitle() + "/" + work_id + "/" + foreign_title_id + "." + title + "/" + foreign_title_id + "." + foreign_step_id + "/" + dataPath);
                btnPause.setText("播放");
                play_img.setBackground(getResources().getDrawable(R.drawable.play));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
            Log.e("onStopTrackingTouch", "onStopTrackingTouch");
            player.mediaPlayer.seekTo(progress);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();//dataPath = Environment.getExternalStorageDirectory().getPath()+"/WLZ_Path/VID_19700105_092736.mp4";   //VID_19700105_092736
        player = new MyPlayer(surfaceView, skbProgress, Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "FSON/" + itemBean.getTitle() + "/" + work_id + "/" + foreign_title_id + "." + title + "/" + foreign_title_id + "." + foreign_step_id + "/" + dataPath, btnPlayUrl, text_time);
        registerReceiver(exitReceiver, new IntentFilter(EXITACTION));
        //isPlaying = player.playUrl(dataPath);
        mSpeechRecognition.loadSpeech(this, this);
        mSpeechRecognition.freshPlayStrings("请确认图像质量。");
        mSpeechRecognition.startSpeech();
    }
    @Override
    protected void onPause(){
        super.onPause();
        mSpeechRecognition.stopSpeech();
        mSpeechRecognition.stopSpeak();
        btnPause.setText("播放");
        play_img.setBackground(getResources().getDrawable(R.drawable.play));
        player.pause();
        player.stop();
        player.releaseMediaPlayer();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        workDb.onDestory();
        unregisterReceiver(exitReceiver);
    }

    private BroadcastReceiver exitReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
}