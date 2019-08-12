package com.example.Fson.WorkClass;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Fson.BeanClass.UserBean;
import com.example.Fson.ControlClass.WorkDb;
import com.example.Fson.ToolsClass.CameraHelper;
import com.example.Fson.R;
import com.example.Fson.ToolsClass.ScanActivity;
import com.example.Fson.WorkClass.Speech.SpeechCallBack;
import com.example.Fson.WorkClass.Speech.SpeechRecognition;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by PC on 2017/9/29.
 */

public class WFirstActivity extends Activity implements SpeechCallBack{
    private SpeechRecognition mSpeechRecognition;
    private static final String EXITACTION = "action.exit";
    private boolean isLogin = false;  //未登录状态
//    private boolean isLogin = true;
    private TextView user_text, login_time;
    private TextView login;
    private ImageView login_img;
    private static final int REQUEST_CODE_SCAN = 1;
    private static final String DECODED_CONTENT_KEY = "codedContent";
//    private String user_id = "20170623";
//    private String user_name = "廖光明";
//    private String department = "质安部";
    private String user_id = "";
    private String user_name = "";
    private String department = "";
    public String mainTAG = "WFirstActivity";
    private ImageView point_icon1, point_icon2, point_icon3;
    private Handler pointHander = new Handler();
    private TextView wakeup, takelogin, starttake, loginout;
    private ArrayList<String> uploadFileStrings = new ArrayList<>();
    private ImageView layouta;
    private int column = 0;
    private WorkDb workDb;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.w_first_activity);
        initView();
        initInfo();
        initBulid();
    }
    /* 初始化view */
    private void initView() {
        layouta = (ImageView) findViewById(R.id.layouta);
        wakeup = (TextView) findViewById(R.id.wakeup);
        takelogin = (TextView) findViewById(R.id.takelogin);
        loginout = (TextView) findViewById(R.id.loginout);
        starttake = (TextView) findViewById(R.id.starttake);
        user_text = (TextView) findViewById(R.id.user_text);
        login_time = (TextView) findViewById(R.id.login_time);
        login = (TextView) findViewById(R.id.login);
        login_img = (ImageView) findViewById(R.id.login_img);
        point_icon1 = (ImageView) findViewById(R.id.point_icon1);
        point_icon2 = (ImageView) findViewById(R.id.point_icon2);
        point_icon3 = (ImageView) findViewById(R.id.point_icon3);
//createFromStream

    }
    /* 初始化信息 */
    private void initInfo() {

        workDb=new WorkDb(this,"");
        /* 初始化SpeechRecognition接口类 */
        mSpeechRecognition = SpeechRecognition.getInstance();
        login_time.setText(new SimpleDateFormat("yyyy.MM.dd HH:mm").format(new Date()));
        registerReceiver(exitReceiver, new IntentFilter(EXITACTION));   /* 加载退出广播 */

    }
    /* 初始化绑定 */
    private void initBulid() {
        wakeup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("科大讯飞", "唤醒语音。");
                mSpeechRecognition.startSpeech();
            }
        });
        takelogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLogin){
                    column = 1;
                    Intent intent = new Intent(WFirstActivity.this, ScanActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_SCAN);
                }
            }
        });
        loginout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLogin){
                    user_text.setText("工号：---------------");
                    login.setText("登录");
                    isLogin = false;
                    login_img.setImageDrawable(getResources().getDrawable(R.drawable.login1));
                    mSpeechRecognition.freshPlayStrings("注销成功，请重新登录！");
                }
            }
        });
        starttake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLogin){
                    mSpeechRecognition.freshPlayStrings("请登录！");
                }else {
                    Intent intent = new Intent(WFirstActivity.this, WSecondActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("parameter", "0");
                    bundle.putString("user_id", user_id);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }
    @Override
    public void getResult(String result){
        switch (result) {
//            case "开始检查":
//                mSpeechRecognition.startMediaPlay();
//                if (!isLogin){
//                    mSpeechRecognition.freshPlayStrings("请登录！");
//                    mSpeechRecognition.startSpeech();
//                }else {
//                    Intent intent = new Intent(WFirstActivity.this, WSecondActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("parameter", "0");
//                    intent.putExtras(bundle);
//                    startActivity(intent);
//                }
//                break;
//            case "登录":
//                mSpeechRecognition.startMediaPlay();
//                if (!isLogin){
//                    column = 1;
//                    Intent intent = new Intent(WFirstActivity.this, ScanActivity.class);
//                    startActivityForResult(intent, REQUEST_CODE_SCAN);
//                }else {
//                    mSpeechRecognition.startSpeech();
//                }
//                break;
//            case "注销":
//                mSpeechRecognition.startMediaPlay();
//                if (isLogin){
//                    user_text.setText("工号：---------------");
//                    login.setText("登录");
//                    isLogin = false;
//                    login_img.setImageDrawable(getResources().getDrawable(R.drawable.login1));
//                    mSpeechRecognition.freshPlayStrings("注销成功，请重新登录！");
//                }
//                mSpeechRecognition.startSpeech();
//                break;
            case "查看日志":
                mSpeechRecognition.startMediaPlay();
                if (!isLogin){
                    mSpeechRecognition.freshPlayStrings("请登录！");
                    mSpeechRecognition.startSpeech();
                }else {
                    Intent intent1 = new Intent(WFirstActivity.this, WSecondActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("parameter", "1");
                    bundle.putString("user_id", user_id);
                    intent1.putExtras(bundle);
                    startActivity(intent1);
                }
                break;
            case "更新工序":
                mSpeechRecognition.startMediaPlay();
                StringBuffer stringBuffer = new StringBuffer();
                File data_txt = CameraHelper.getOutputMediaFile(CameraHelper.MEDIA_TYPE_DATABASE);
                try {
                    BufferedReader br = new BufferedReader(new FileReader(data_txt));
                    String line = "";
                    while((line = br.readLine())!=null){
                        stringBuffer.append(line);
                    }
                    br.close();
                    //Log.e("strUTF8", stringBuffer.toString());
                }catch (Exception e){
                    e.printStackTrace();
                }
                WorkDb workDb = new WorkDb(WFirstActivity.this, stringBuffer.toString());
                workDb.upDate();
                mSpeechRecognition.freshPlayStrings("工序更新成功！");
                mSpeechRecognition.startSpeech();
                break;
            case "查看任务书":
                mSpeechRecognition.startMediaPlay();
                mSpeechRecognition.startSpeech();
                break;
            case "系统设置":
                mSpeechRecognition.startMediaPlay();
                mSpeechRecognition.startSpeech();
                break;
            case "后退":
                mSpeechRecognition.startMediaPlay();
                finish();
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
                timer.schedule(task,1000);
                break;
            default:
                Log.e("Main-result", result);
                mSpeechRecognition.startSpeech();
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /* 处理二维码扫描结果 */
        String scanResult = data.getStringExtra(DECODED_CONTENT_KEY);
        if (scanResult != null) {
            //开始维护流程
            String string = scanResult.toString();


            String [] stringArr = string.split(",");


            if(stringArr.length > 1){
                if (stringArr[1].length() > 0){
                    user_id = stringArr[1];
                    user_name = stringArr[2];
                    department = stringArr[3];

                    UserBean userBean=workDb.getUser(user_id);
                    if(userBean !=null) {
                        Toast.makeText(WFirstActivity.this, "工号：" + user_id + "，姓名：" + user_name + "，部门：" + department + "。", Toast.LENGTH_LONG).show();
                        user_text.setText("工号：" + user_id + "，姓名：" + user_name + "，部门：" + department);
                        login_time.setText(new SimpleDateFormat("yyyy.MM.dd HH:mm").format(new Date()));
                        login.setText("注销");
                        isLogin = true;
                        login_img.setImageDrawable(getResources().getDrawable(R.drawable.login2));
                    }
                }
            }
//            user_id = scanResult.toString();
//            Toast.makeText(WFirstActivity.this, "工号：" + user_id + "。", Toast.LENGTH_LONG).show();
//            if(user_id.length() > 0){
//                user_text.setText("工号：" + user_id);
//                login_time.setText(new SimpleDateFormat("yyyy.MM.dd HH:mm").format(new Date()));
//                login.setText("注销");
//                isLogin = true;
//                login_img.setImageDrawable(getResources().getDrawable(R.drawable.login2));
//            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.e("WFirst:", "onResume");
        mSpeechRecognition.loadSpeech(this, this);
        if (column == 1){
            column = 0;
            if (isLogin){
                mSpeechRecognition.freshPlayStrings("登录成功！");
            }else {
                mSpeechRecognition.freshPlayStrings("登录失败，请重试！");
            }
        }else {
            if (isLogin){
//                mSpeechRecognition.freshPlayStrings("可视化总检放行检查系统。");
                mSpeechRecognition.freshPlayStrings("可视化总检放行检查系统。");

            }else {
//                mSpeechRecognition.freshPlayStrings("可视化总检放行检查系统。请登录。");
                mSpeechRecognition.freshPlayStrings("可视化总检放行检查系统。请登录。");
            }
        }
        mSpeechRecognition.startSpeech();
        registerReceiver(exitReceiver, new IntentFilter(EXITACTION));

        Bitmap bmp = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "FSTZ/back.jpg" );

        Log.e("xxx", Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "FSON/back.jpg");

        layouta.setImageBitmap(bmp);
        layouta.setScaleType(ImageView.ScaleType.FIT_XY);
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.e("WFirst:", "onPause");
        mSpeechRecognition.stopSpeech();
        mSpeechRecognition.stopSpeak();
        unregisterReceiver(exitReceiver);
    }
    @Override
    protected void onRestart(){
        super.onRestart();
        Log.e("WFirst:", "onRestart");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.e("WFirst:", "onStop");
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.e("WFirst:", "onDestroy");
    }

    private BroadcastReceiver exitReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
}
