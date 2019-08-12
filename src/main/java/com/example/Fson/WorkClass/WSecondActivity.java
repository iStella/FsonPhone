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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.Fson.BeanClass.ItemBean;
import com.example.Fson.ControlClass.WorkDb;
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

public class WSecondActivity extends Activity implements SensorEventListener, SpeechCallBack {
    private SpeechRecognition mSpeechRecognition;
    private LinearLayout.LayoutParams MW = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    private LinearLayout.LayoutParams M_50_4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 50, 4);
    private LinearLayout.LayoutParams M_50_1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 50, 1);
    private ScrollView scrollView1;
    private LinearLayout area_line1;
    private TextView mytext1, updatedata, back, system_out;
    private static final String EXITACTION = "action.exit";
    private WorkDb workDb;
    private ArrayList<ItemBean> itemBeanArrayList = new ArrayList<>();
    private float x = 0.0f, y = 0.0f;
    private SensorManager sensorManager = null;
    private Sensor gyroSensor = null;
    private boolean animing = false;
    private int scroll_top = 0;
    private int scroll_index = 0;
    private boolean gyroSensoring = false;
    private Handler sensorhandler = new Handler();
    private String parameter = "0";
    private String user_id = "null";
    private NumberFormatClass numberFormatClass;
    private ArrayList<String> numStrings = new ArrayList<>();
    private ImageView point_icon1, point_icon2, point_icon3;
    private Handler pointHander = new Handler();
    //private TextView wakeup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.w_second_activity);
        initView();
        initInfo();
        initBulid();
    }

    /* 初始化view */
    private void initView() {
        //wakeup = (TextView) findViewById(R.id.wakeup);
        area_line1 = (LinearLayout) findViewById(R.id.area_line1);
        scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
        mytext1 = (TextView) findViewById(R.id.mytext1);
        IntentFilter filter = new IntentFilter();
        filter.addAction(EXITACTION);
        //注册receiver
        registerReceiver(exitReceiver, filter);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        point_icon1 = (ImageView) findViewById(R.id.point_icon1);
        point_icon2 = (ImageView) findViewById(R.id.point_icon2);
        point_icon3 = (ImageView) findViewById(R.id.point_icon3);

        updatedata = (TextView) findViewById(R.id.updatedata);
        back = (TextView) findViewById(R.id.back);
        system_out = (TextView) findViewById(R.id.system_out);

    }

    /* 初始化信息 */
    private void initInfo() {
        /* 初始化SpeechRecognition接口类 */
        mSpeechRecognition = SpeechRecognition.getInstance();
        Bundle bundle = this.getIntent().getExtras();
        parameter = bundle.getString("parameter");
        user_id = bundle.getString("user_id");
        workDb = new WorkDb(this, "");
        getAllItems();
        numberFormatClass = new NumberFormatClass();
        String[] nativenum = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "百"};
        for (String num : nativenum) {
            numStrings.add(num);
        }

    }

    /* 初始化绑定 */
    private void initBulid() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == updatedata) {
                    workDb.workToTrue();
                } else if (view == back) {
                    finish();
                } else if (view == system_out) {
                    sendBroadcast(new Intent(EXITACTION).putExtra("closeAll", 1));
                }

            }
        };
        updatedata.setOnClickListener(onClickListener);
        back.setOnClickListener(onClickListener);
        system_out.setOnClickListener(onClickListener);

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
    public void getResult(String result) {
//        switch (result) {
//            case "更新工序":
//                workDb.workToTrue();
//                mSpeechRecognition.startSpeech();
//                break;
//            case "后退":
//                mSpeechRecognition.startMediaPlay();
//                this.finish();
//                break;
//            case "退出":
//                mSpeechRecognition.startMediaPlay();
//                TimerTask task = new TimerTask() {
//                    public void run() {
//                        Intent exitintent = new Intent(EXITACTION);
//                        sendBroadcast(exitintent);
//                    }
//                };
//                Timer timer = new Timer();
//                timer.schedule(task,1000);
//                break;
//            default:
//                Log.e("Main-result", result);
//                if (result.length() > 6){
//                    if (result.substring(0, 6).equals("select")){
//                        mSpeechRecognition.startMediaPlay();
//                        String value = result.substring(6, result.length());
//                        /* 字符分割 */
//                        ArrayList<String> resultList = new ArrayList<>();
//                        String[] results = value.split("");
//                        for (String string : results){
//                            if (string.length() != 0){
//                                resultList.add(string);
//                                Log.e("string", string);
//                            }
//                        }
//                        /* 判读是否属于数字区间 */
//                        boolean goOn = true;
//                        for (String num : resultList){
//                            Log.e("numStrings.indexOf(num)", String.valueOf(numStrings.indexOf(num)));
//                            if (numStrings.indexOf(num) < 0){
//                                goOn = false;
//                            }
//                        }
//                        if (goOn){
//                            boolean b = false;
//                            for (ItemBean itemBean : itemBeanArrayList){
//                                if (String.valueOf(numberFormatClass.chineseNumberInt(result)).equals(String.valueOf(itemBeanArrayList.indexOf(itemBean) + 1))){
//                                    mSpeechRecognition.startMediaPlay();
//                                    if (parameter.equals("0")){
//                                        Intent intent = new Intent(WSecondActivity.this, MainActivity.class);
//                                        Bundle bundle = new Bundle();
//                                        bundle.putInt("item_num", Integer.parseInt(itemBean.getNum()));
//                                        bundle.putString("work_id", "null");
//                                        bundle.putString("user_id", user_id);
//                                        intent.putExtras(bundle);
//                                        startActivity(intent);
//                                    } else if (parameter.equals("1")) {
//                                        Intent intent = new Intent(WSecondActivity.this, WLogActivity.class);
//                                        Bundle bundle = new Bundle();
//                                        bundle.putInt("item_num", Integer.parseInt(itemBean.getNum()));
//                                        bundle.putString("is_worked", "false");
//                                        bundle.putString("user_id", user_id);
//                                        intent.putExtras(bundle);
//                                        startActivity(intent);
//                                    }
//                                    b = true;
//                                    break;
//                                }
//                            }
//                            if (!b){
//                                Log.e("无匹配id，重新加载listening", result);
//                                mSpeechRecognition.startSpeech();
//                            }
//                        }else {
//                            Log.e("无匹配项，重新加载listening", result);
//                            mSpeechRecognition.startSpeech();
//                        }
//                    } else {
//                        Log.e("无匹配项，重新加载listening", result);
//                        mSpeechRecognition.startSpeech();
//                    }
//                } else {
//                    Log.e("无匹配项，重新加载listening", result);
//                    mSpeechRecognition.startSpeech();
//                }
//                break;
//        }
    }

    @Override
    public void getpoint(boolean speechpoint) {
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

    private Thread pointThread1 = new Thread() {
        @Override
        public void run() {
            point_icon1.setImageDrawable(getResources().getDrawable(R.drawable.point_icon2));
            point_icon2.setImageDrawable(getResources().getDrawable(R.drawable.point_icon2));
            point_icon3.setImageDrawable(getResources().getDrawable(R.drawable.point_icon2));
            pointHander.postDelayed(pointThread2, 250);
        }
    };
    private Thread pointThread2 = new Thread() {
        @Override
        public void run() {
            point_icon1.setImageDrawable(null);
            point_icon2.setImageDrawable(null);
            point_icon3.setImageDrawable(null);
            pointHander.postDelayed(pointThread1, 250);
        }
    };

    /* 插入Item列表 */
    public void getAllItems() {
        itemBeanArrayList = workDb.getItems();


        scroll_index = itemBeanArrayList.size();
        for (final ItemBean itemBean : itemBeanArrayList) {
            LinearLayout layout = new LinearLayout(this);
            layout.setLayoutParams(MW);
            layout.setOrientation(LinearLayout.HORIZONTAL);

            TextView textView = new TextView(this);
            textView.setLayoutParams(M_50_4);
            textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_bottom_lavender));
            textView.setTextSize(28);
            textView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            textView.setText(String.valueOf(itemBeanArrayList.indexOf(itemBean) + 1));

            TextView textView1 = new TextView(this);
            textView1.setLayoutParams(M_50_1);
            textView1.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_bottom_lavender));
            textView1.setTextSize(28);
            textView1.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            String title = itemBean.getTitle();
            if (title.length() > 22) title = title.substring(0, 22) + "...";
            textView1.setText(title);
            //final int index = itemBeanArrayList.indexOf(itemBean);

            textView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (parameter.equals("0")) {
                        Intent intent = new Intent(WSecondActivity.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("item_num", Integer.parseInt(itemBean.getNum()));
                        bundle.putString("work_id", "null");
                        bundle.putString("user_id", user_id);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else if (parameter.equals("1")) {
                        Intent intent = new Intent(WSecondActivity.this, WLogActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("item_num", Integer.parseInt(itemBean.getNum()));
                        bundle.putString("is_worked", "false");
                        bundle.putString("user_id", user_id);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
            });
            layout.addView(textView);
            layout.addView(textView1);
            area_line1.addView(layout);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /* 监听晃动变化 */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!gyroSensoring) {
            x = event.values[0];
            y = event.values[1];
            gyroSensoring = true;
        } else {
            float toX = event.values[0] - x;
            float toY = event.values[1] - y;
            if (scroll_index > 7) {      // 列数 index>7 可以发生位移
                if (Math.abs(toY) > 0.0f) {
                    if (Math.abs(toY) > 300.0f) {
                        if (toY < 0.0f) {
                            toY = toY + 360;
                        } else {
                            toY = toY - 360;
                        }
                    }
                    //上移
                    if (toY < -0.1f) {
                        if (!animing) {
                            animing = true;
                            sensorhandler.postDelayed(sensorrunnable, 1000);
                            scroll_top -= 300;
                            if (scroll_top < 0) scroll_top = 0;
                            scrollView1.smoothScrollTo(0, scroll_top);
                        }
                    }
                    //下移
                    if (toY > 0.1f) {
                        if (!animing) {
                            animing = true;
                            sensorhandler.postDelayed(sensorrunnable, 1000);
                            scroll_top += 300;
                            if (scroll_top > (scroll_index - 7) * 50)
                                scroll_top = (scroll_index - 7) * 50;
                            scrollView1.smoothScrollTo(0, scroll_top);
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
        Log.e("WSecond:", "onResume");
        mSpeechRecognition.loadSpeech(this, this);
        mSpeechRecognition.freshPlayStrings(mytext1.getText().toString());
        registerReceiver(exitReceiver, new IntentFilter(EXITACTION));
        sensorhandler.postDelayed(sensorrunnable, 1000);
        sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("WSecond:", "onPause");
        mSpeechRecognition.stopSpeak();
        unregisterReceiver(exitReceiver);
        sensorhandler.removeCallbacks(sensorrunnable);
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("WSecond:", "onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("WSecond:", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("WSecond:", "onDestroy");

    }

    private BroadcastReceiver exitReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

}

