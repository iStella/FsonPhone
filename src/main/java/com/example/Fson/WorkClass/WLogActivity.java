package com.example.Fson.WorkClass;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.Fson.ControlClass.WorkDb;
import com.example.Fson.BeanClass.ItemBean;
import com.example.Fson.BeanClass.RecordBean;
import com.example.Fson.R;
import com.example.Fson.ToolsClass.NumberFormatClass;
import com.example.Fson.WorkClass.Speech.SpeechCallBack;
import com.example.Fson.WorkClass.Speech.SpeechRecognition;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by PC on 2017/9/29.
 */

public class WLogActivity extends Activity implements SensorEventListener, SpeechCallBack {
    private SpeechRecognition mSpeechRecognition;
    private LinearLayout.LayoutParams MW = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    private LinearLayout.LayoutParams FF_200_50 = new LinearLayout.LayoutParams(200, 50);
    private LinearLayout.LayoutParams FF_350_50 = new LinearLayout.LayoutParams(350, 50);
    private LinearLayout.LayoutParams FF_450_50 = new LinearLayout.LayoutParams(450, 50);

    private LinearLayout head_line1;
    private ScrollView scrollView1, scrollView2;
    private LinearLayout area_line1, area_line2;
    private TextView mytext1 ;
    private static final String EXITACTION = "action.exit";
    private WorkDb workDb;
    private float x = 0.0f, y = 0.0f;
    private int head_line1_left, head_line1_top, head_line1_right, head_line1_bottom = 0;
    private int head_line1_layout_left, head_line1_layout_top, head_line1_layout_right, head_line1_layout_bottom = 0;
    private int scrollView1_left, scrollView1_top, scrollView1_right, scrollView1_bottom = 0;
    private int scrollView1_layout_left, scrollView1_layout_top, scrollView1_layout_right, scrollView1_layout_bottom = 0;
    private SensorManager sensorManager = null;
    private Sensor gyroSensor = null;
    private String is_worked = "false";
    private String user_id = "null";
    private int item_num;
    private ArrayList<RecordBean> recordBeanArrayList = new ArrayList<>();
    private int scroll_top = 0;
    private int scroll_index = 0;
    private boolean animing = false;
    private Handler sensorhandler = new Handler();
    private boolean gyroSensoring = false;
    private NumberFormatClass numberFormatClass;
    private ArrayList<String> numStrings = new ArrayList<>();
    private ImageView point_icon1, point_icon2, point_icon3;
    private Handler pointHander = new Handler();
    //private TextView wakeup;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.w_log_activity);
        initView();
        initInfo();
        initBulid();
    }
    /* 初始化view */
    private void initView() {
        //wakeup = (TextView) findViewById(R.id.wakeup);
        head_line1 = (LinearLayout)findViewById(R.id.head_line1);
        scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
        scrollView2 = (ScrollView) findViewById(R.id.scrollView2);
        area_line1 = (LinearLayout)findViewById(R.id.area_line1);
        area_line2 = (LinearLayout)findViewById(R.id.area_line2);
        mytext1 = (TextView) findViewById(R.id.mytext1);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        point_icon1 = (ImageView) findViewById(R.id.point_icon1);
        point_icon2 = (ImageView) findViewById(R.id.point_icon2);
        point_icon3 = (ImageView) findViewById(R.id.point_icon3);
    }
    /* 初始化信息 */
    private void initInfo() {
        mSpeechRecognition = SpeechRecognition.getInstance();
        workDb = new WorkDb(this, "");
        Bundle bundle = this.getIntent().getExtras();
        is_worked = bundle.getString("is_worked");
        user_id = bundle.getString("user_id");
        item_num = bundle.getInt("item_num");
        if (is_worked.equals("false")){
            mytext1.setText("进行中");
            getWorkRecords("false");//false 进行中
        } else {
            mytext1.setText("已完成");
            getWorkRecords("true");//true 已完成
        }
        numberFormatClass = new NumberFormatClass();
        String[] nativenum = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "百"};
        for (String num : nativenum) {
            numStrings.add(num);
        }
    }
    /* 初始化绑定 */
    private void initBulid() {
        /*
        wakeup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpeechRecognition.startSpeech();
            }
        });
        */
    }
    @Override
    public void getResult(String result){
        switch (result) {
            case "进行中":
                mSpeechRecognition.startMediaPlay();
                if (is_worked.equals("true")){
                    Intent intent = new Intent(WLogActivity.this, WLogActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("is_worked", "false");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
                break;
            case "已完成":
                mSpeechRecognition.startMediaPlay();
                if (is_worked.equals("false")){
                    Intent intent = new Intent(WLogActivity.this, WLogActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("is_worked", "true");
                    intent.putExtras(bundle);
                    startActivity(intent);
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
                            boolean b = false;
                        /* id口令 */
                            for (RecordBean recordBean : recordBeanArrayList){
                                if (String.valueOf(numberFormatClass.chineseNumberInt(result)).equals(String.valueOf(recordBeanArrayList.indexOf(recordBean) + 1))){
                                    mSpeechRecognition.startMediaPlay();
                                    if (is_worked.equals("false")){
                                        Intent intent = new Intent(WLogActivity.this, MainActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putInt("item_num", Integer.parseInt(recordBean.getForeign_item_id()));
                                        bundle.putString("work_id", recordBean.getWork_id());
                                        bundle.putString("user_id", user_id);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                    b = true;
                                    break;
                                }
                            }
                            if (!b){
                                Log.e("无匹配id，重新加载listening", result);
                                mSpeechRecognition.startSpeech();
                            }
                        }else {
                            Log.e("无匹配项，重新加载listening", result);
                            mSpeechRecognition.startSpeech();
                        }
                    } else {
                        Log.e("无匹配项，重新加载listening", result);
                        mSpeechRecognition.startSpeech();
                    }
                } else {
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

    public void getWorkRecords(final String is_worked){
        recordBeanArrayList = workDb.getWorkingRecords(item_num, is_worked);
        scroll_index = recordBeanArrayList.size();
        for (final RecordBean recordBean : recordBeanArrayList){
            ItemBean itemBean = workDb.getItem(Integer.parseInt(recordBean.getForeign_item_id()));
            final LinearLayout layout1 = new LinearLayout(this);
            layout1.setLayoutParams(MW);
            layout1.setOrientation(LinearLayout.HORIZONTAL);
            final LinearLayout layout2 = new LinearLayout(this);
            layout2.setLayoutParams(MW);
            layout2.setOrientation(LinearLayout.HORIZONTAL);

            TextView textView = new TextView(this);
            textView.setLayoutParams(FF_200_50);
            textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_bottom_lavender));
            textView.setTextSize(28);
            textView.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            textView.setText(String.valueOf(recordBeanArrayList.indexOf(recordBean) + 1));

            TextView textView1 = new TextView(this);
            textView1.setLayoutParams(FF_200_50);
            textView1.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_bottom_lavender));
            textView1.setTextSize(28);
            textView1.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            textView1.setText(recordBean.getForeign_item_id());

            TextView textView2 = new TextView(this);
            textView2.setLayoutParams(FF_450_50);
            textView2.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_bottom_lavender));
            textView2.setTextSize(28);
            textView2.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            String title = itemBean.getTitle();
            if (title.length() > 12) title = title.substring(0,12) + "...";
            textView2.setText(title);

            TextView textView3 = new TextView(this);
            textView3.setLayoutParams(FF_350_50);
            textView3.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_bottom_lavender));
            textView3.setTextSize(28);
            textView3.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            textView3.setText(recordBean.getWork_id());

            TextView textView4 = new TextView(this);
            textView4.setLayoutParams(FF_200_50);
            textView4.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_bottom_lavender));
            textView4.setTextSize(28);
            textView4.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            textView4.setText("**%");

            TextView textView5 = new TextView(this);
            textView5.setLayoutParams(FF_350_50);
            textView5.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_bottom_lavender));
            textView5.setTextSize(28);
            textView5.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            textView5.setText(recordBean.getTimestamp());

            layout1.addView(textView1);
            layout1.addView(textView2);
            layout1.addView(textView3);
            layout1.addView(textView4);
            layout1.addView(textView5);
            layout2.addView(textView);
            area_line1.addView(layout1);
            area_line2.addView(layout2);
        }
    }

    /* 获取最新位置 */
    public void getposition() {
        head_line1_layout_left = head_line1.getLeft();
        head_line1_layout_top = head_line1.getTop();
        head_line1_layout_right = head_line1.getRight();
        head_line1_layout_bottom = head_line1.getBottom();
        head_line1_left = head_line1.getLeft();
        head_line1_top = head_line1.getTop();
        head_line1_right = head_line1.getRight();
        head_line1_bottom = head_line1.getBottom();

        scrollView1_layout_left = scrollView1.getLeft();
        scrollView1_layout_top = scrollView1.getTop();
        scrollView1_layout_right = scrollView1.getRight();
        scrollView1_layout_bottom = scrollView1.getBottom();
        scrollView1_left = scrollView1.getLeft();
        scrollView1_top = scrollView1.getTop();
        scrollView1_right = scrollView1.getRight();
        scrollView1_bottom = scrollView1.getBottom();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    /* 监听晃动变化 */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!gyroSensoring){
            x = event.values[0];
            y = event.values[1];
            gyroSensoring = true;
        } else {
            float toX = event.values[0] - x;
            float toY = event.values[1] - y;
            getposition();
            if (Math.abs(toX) > 0.0f){
                if (Math.abs(toX) > 300.0f){
                    if (toX < 0.0f){
                        toX = toX + 360;
                    }else{
                        toX = toX - 360;
                    }
                }
                float long_x = scrollView1.getWidth() * Math.abs(toX) / 30;
                //左移
                if (toX < 0.0f) {;
                    scrollView1_left = (int) long_x;
                    int range = 200 - scrollView1_layout_left;
                    if (range < scrollView1_left) {
                        scrollView1_left = range;
                    }
                }
                //右移
                if (toX > 0.0f) {
                    scrollView1_left = - (int) long_x;
                    int range = - (scrollView1.getWidth() - 854) - scrollView1_layout_left;
                    if (range > scrollView1_left) {
                        scrollView1_left = range;
                    }
                }
                scrollView1.layout(scrollView1_layout_left + scrollView1_left, scrollView1_layout_top, scrollView1_layout_right + scrollView1_left, scrollView1_layout_bottom);
                head_line1.layout(head_line1_layout_left + scrollView1_left, head_line1_layout_top, head_line1_layout_right + scrollView1_left, head_line1_layout_bottom);
            }
            if (scroll_index > 7){      // 列数 index>7 可以发生位移
                if (Math.abs(toY) > 0.0f) {
                    getposition();
                    /* >300 在分界线发生偏移 */
                    if (Math.abs(toY) > 300.0f){
                        if (toY < 0.0f){
                            toY = toY + 360;
                        }else{
                            toY = toY - 360;
                        }
                    }
                    //上移
                    if (toY < - 0.1f) {
                        if (!animing){
                            animing = true;
                            sensorhandler.postDelayed(sensorrunnable, 1000);
                            scroll_top -= 300;
                            if (scroll_top < 0) scroll_top = 0;
                            scrollView1.smoothScrollTo(0, scroll_top);
                            scrollView2.smoothScrollTo(0, scroll_top);
                        }
                    }
                    //下移
                    if (toY > 0.1f) {
                        if (!animing) {
                            animing = true;
                            sensorhandler.postDelayed(sensorrunnable, 1000);
                            scroll_top += 300;
                            if (scroll_top > (scroll_index - 7) * 50) scroll_top = (scroll_index - 7) * 50;
                            scrollView1.smoothScrollTo(0, scroll_top);
                            scrollView2.smoothScrollTo(0, scroll_top);
                        }
                    }
                }
            }
            x = event.values[0];
            y = event.values[1];
        }
    }

    Runnable sensorrunnable = new Runnable() {
        @Override
        public void run() {
            animing = false;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mSpeechRecognition.loadSpeech(this, this);
        mSpeechRecognition.freshPlayStrings("请选择工作序号。");
        mSpeechRecognition.startSpeech();
        sensorhandler.postDelayed(sensorrunnable, 1000);
        registerReceiver(exitReceiver, new IntentFilter(EXITACTION));
        sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onRestart(){
        super.onRestart();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mSpeechRecognition.stopSpeech();
        mSpeechRecognition.stopSpeak();
        sensorhandler.removeCallbacks(sensorrunnable);
        unregisterReceiver(exitReceiver);
        sensorManager.unregisterListener(this);
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        workDb.onDestory();
    }
    private BroadcastReceiver exitReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

}

