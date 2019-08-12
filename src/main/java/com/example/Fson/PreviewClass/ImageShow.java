package com.example.Fson.PreviewClass;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

/**
 * Created by PC on 2017/9/11.
 */

public class ImageShow extends Activity implements SensorEventListener, SpeechCallBack {
    private SpeechRecognition mSpeechRecognition;
    private String dataPath;
    private ImageView main_img;
    private ImageView img;
    private ImageView img_navy;
    private TextView toLeft, toTop, toRight, toBottom, tick;
    private LinearLayout line_delete;
    private boolean is_delete = false;
    private int left, top, right, bottom = 0;
    private int layout_left, layout_top, layout_right, layout_bottom = 0;
    private Bitmap bmp = null;
    private int level = 1;
    private int blank = 0, default_left = 0;
    private float x = 0.0f, y = 0.0f;
    private SensorManager sensorManager = null;
    private Sensor gyroSensor = null;
    private int[] zoomsbt = {R.id.zoom1, R.id.zoom2, R.id.zoom3, R.id.zoom4, R.id.zoom5};
    private static final String EXITACTION = "action.exit";
    private WorkDb workDb;
    private String foreign_title_id ="";
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
    private float road = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.image_activity);

        wakeup = (TextView) findViewById(R.id.wakeup);
        main_img = (ImageView) findViewById(R.id.main_img);
        img = (ImageView) findViewById(R.id.img);
        img_navy = (ImageView) findViewById(R.id.img_navy);
        line_delete = (LinearLayout) findViewById(R.id.line_delete);
        tick = (TextView)findViewById(R.id.tick);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        blank = (840 - 480 * 4/3) / 2;
        default_left = (480 * 2 * 4/3 - 840) / 2;
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
        Log.e("order", String.valueOf(order));

        int urls_len = 0;
        for (String url : urls) {
            if (url.substring(url.length() - 3, url.length()).equals("jpg") || url.substring(url.length() - 3, url.length()).equals("png")) {
                urls_len ++;
            }
        }
        tick.setText((order + 1) + "/" + urls.length);

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
    @Override
    public void getResult(String result) {
        switch (result) {
            case "放大":
                mSpeechRecognition.startMediaPlay();
                if (level < 5){
                    level ++;
                    TextView zoomview = (TextView) findViewById(zoomsbt[level - 1]);
                    zoomview.setBackground(getResources().getDrawable(R.drawable.border_zoom_navy));
                    /* 修改img_navy尺寸 */
                    LinearLayout.LayoutParams params_navy = new LinearLayout.LayoutParams(img_navy.getLayoutParams());
                    /* 不能避免小数，直接数字
                    params_navy.width = img_navy.getWidth() * (level - 1) / level;
                    params_navy.height = img_navy.getHeight() * (level - 1) / level; */
                    params_navy.width = img_navy.getWidth() * (level - 1) / level;
                    params_navy.height = img_navy.getHeight() * (level - 1) / level;
                    if (level == 4) {
                        params_navy.width = 52;
                    }
                    if (level == 5) {
                        params_navy.width = 42;
                    }
                    params_navy.setMargins((210 - params_navy.width) / 2, (120 - params_navy.height) / 2, 0, 0);
                    img_navy.setLayoutParams(params_navy);
                    /* 修改main_img尺寸 */
                    LinearLayout.LayoutParams params_main = new LinearLayout.LayoutParams(main_img.getLayoutParams());
                    params_main.width = main_img.getWidth() * level / (level - 1);
                    params_main.height = main_img.getHeight() * level / (level - 1);
                    main_img.setLayoutParams(params_main);
                }
                mSpeechRecognition.startSpeech();
                break;
            case "缩小":
                mSpeechRecognition.startMediaPlay();
                if (level > 1){
                    level --;
                    TextView zoomview = (TextView) findViewById(zoomsbt[level]);
                    zoomview.setBackground(getResources().getDrawable(R.drawable.border_zoom_null));
                    /* 修改img_navy尺寸 */
                    LinearLayout.LayoutParams params_navy = new LinearLayout.LayoutParams(img_navy.getLayoutParams());
                    params_navy.width = img_navy.getWidth() * (level + 1) / level;
                    params_navy.height = img_navy.getHeight() * (level + 1) / level;
                    if (level == 4) {
                        params_navy.width = 52;
                    }
                    if (level == 3) {
                        params_navy.width = 70;
                    }
                    params_navy.setMargins(0, 0, 0, 0);
                    img_navy.setLayoutParams(params_navy);
                    /* 修改main_img尺寸 */
                    LinearLayout.LayoutParams params_main = new LinearLayout.LayoutParams(main_img.getLayoutParams());
                    params_main.width = main_img.getWidth() * level / (level + 1);
                    params_main.height = main_img.getHeight() * level / (level + 1);
                    main_img.setLayoutParams(params_main);
                }
                mSpeechRecognition.startSpeech();
                break;
            case "左转":
                mSpeechRecognition.startMediaPlay();
                /* 缩小 不太合适 */
                //level = 1;
                road -= 90;
                main_img.setPivotX(main_img.getWidth() / 2);
                main_img.setPivotY(main_img.getHeight() / 2);//支点在图片中心
                main_img.setRotation(road);
                img.setPivotX(img.getWidth() / 2);
                img.setPivotY(img.getHeight() / 2);
                img.setRotation(road);
                mSpeechRecognition.startSpeech();
                break;
            case "右转":
                mSpeechRecognition.startMediaPlay();
                road += 90;
                //level = 1;
                main_img.setPivotX(main_img.getWidth() / 2);
                main_img.setPivotY(main_img.getHeight() / 2);
                main_img.setRotation(road);
                img.setPivotX(img.getWidth() / 2);
                img.setPivotY(img.getHeight() / 2);
                img.setRotation(road);
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
                }else {
                    mSpeechRecognition.startSpeech();
                }
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
            Intent intent = new Intent(ImageShow.this, ImageShow.class);
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
            Intent intent = new Intent(ImageShow.this, VideoPlayer.class);
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
    /* 还原Zoom */
    public void emptyZoom() {
        /* 待修改 */
        TextView zoomview2 = (TextView) findViewById(zoomsbt[1]);
        TextView zoomview3 = (TextView) findViewById(zoomsbt[2]);
        TextView zoomview4 = (TextView) findViewById(zoomsbt[3]);
        TextView zoomview5 = (TextView) findViewById(zoomsbt[4]);
        zoomview2.setBackground(getResources().getDrawable(R.drawable.border_zoom_null));
        zoomview3.setBackground(getResources().getDrawable(R.drawable.border_zoom_null));
        zoomview4.setBackground(getResources().getDrawable(R.drawable.border_zoom_null));
        zoomview5.setBackground(getResources().getDrawable(R.drawable.border_zoom_null));
    }
    /* 获取最新位置 */
    public void getposition() {
        layout_left = main_img.getLeft();
        layout_top = main_img.getTop();
        layout_right = main_img.getRight();
        layout_bottom = main_img.getBottom();
        left = main_img.getLeft();
        top = main_img.getTop();
        right = main_img.getRight();
        bottom = main_img.getBottom();
//        Log.e("layout_left", String.valueOf( main_img.getLeft()));
//        Log.e("layout_top", String.valueOf( main_img.getTop()));
//        Log.e("layout_right", String.valueOf( main_img.getRight()));
//        Log.e("layout_bottom", String.valueOf( main_img.getBottom()));
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    /* 监听晃动变化 */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (level == 1){
            x = event.values[0];
            y = event.values[1];
        }
        if (level > 1) {
            float toX = event.values[0] - x;
            float toY = event.values[1] - y;
            if (Math.abs(toX) > 0.0f){
                getposition();
                if (Math.abs(toX) > 300.0f){
                    if (toX < 0.0f){
                        toX = toX + 360;
                    }else{
                        toX = toX - 360;
                    }
                }
                float long_x = main_img.getHeight() / 2 * 4/3 * Math.abs(toX) / 12;
                //左移
                if (toX < 0) {
                    left = (int) long_x;
                    int range = (default_left - (level - 2) * blank) - layout_left;
                    if (range < left) {
                        left = range;
                    }
                }
                //右移
                if (toX > 0) {
                    left = - (int) long_x;
                    int range = - (main_img.getHeight() * 4/3 - (default_left - (level - 2) * blank)) - layout_left;
                    if (range > left) {
                        left = range;
                    }
                }
                //Log.e("long_x>>>", "" + left);
                main_img.clearAnimation();
                main_img.layout(layout_left + left, layout_top, layout_right + left, layout_bottom);
            }
            if (Math.abs(toY) > 0.0f) {
                getposition();
                if (Math.abs(toY) > 300.0f){
                    if (toY < 0.0f){
                        toY = toY + 360;
                    }else{
                        toY = toY - 360;
                    }
                }
                float long_y = main_img.getHeight() / 2 * Math.abs(toY) / 12;
                //上移
                if (toY < 0) {
                    top = (int) long_y;
                    int range = 240 - layout_top;
                    if (range < top) {
                        top = range;
                    }
                }
                //下移
                if (toY > 0) {
                    top = - (int) long_y;
                    int range = - main_img.getHeight() + 240 - layout_top;
                    if (range > top) {
                        top = range;
                    }
                }
                //Log.e("long_y>>>", "" + top);
                main_img.clearAnimation();
                main_img.layout(layout_left, layout_top + top, layout_right, layout_bottom + top);
            }
            x = event.values[0];
            y = event.values[1];
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        try {
            bmp = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "FSON/" + itemBean.getTitle() + "/" + work_id + "/" + foreign_title_id + "." + title + "/" + foreign_title_id + "." + foreign_step_id + "/" + dataPath);
            main_img.setImageBitmap(bmp);
            main_img.setScaleType(ImageView.ScaleType.FIT_CENTER);
            img.setImageBitmap(bmp);
            img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }catch (Exception e){
            e.printStackTrace();
        }
        /* 销毁activity的广播 */
        registerReceiver(exitReceiver, new IntentFilter(EXITACTION));
        //isPlaying = player.playUrl(dataPath);
        mSpeechRecognition.loadSpeech(this, this);
        mSpeechRecognition.freshPlayStrings("请确认图像质量。");
        mSpeechRecognition.startSpeech();
        sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mSpeechRecognition.stopSpeech();
        mSpeechRecognition.stopSpeak();
        sensorManager.unregisterListener(this);
    }
    protected void onDestroy(){
        super.onDestroy();
        workDb.onDestory();
        sensorManager.unregisterListener(this);
        unregisterReceiver(exitReceiver);
    }
    private BroadcastReceiver exitReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
}
