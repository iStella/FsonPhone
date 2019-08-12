package com.example.Fson.PublicClass;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.example.Fson.R;
import com.example.Fson.WorkClass.MainActivity;
import com.example.Fson.WorkClass.Speech.SpeechRecognition;
import com.example.Fson.WorkClass.WFirstActivity;

public class LoadActivity extends Activity {
    private ImageView welcomeImg = null;
    private SpeechRecognition mSpeechRecognition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.load_activity);
        /* 科大讯飞　网络连接失败　错误吗10202 */
        /* 先加载一次，下一个activity加载成功！ */
        initInfo();
        welcomeImg = (ImageView) this.findViewById(R.id.welcome_img);
        AlphaAnimation anima = new AlphaAnimation(0.0f, 1.0f);
        anima.setDuration(2000);// 设置动画显示时间     3000
        welcomeImg.startAnimation(anima);
        anima.setAnimationListener(new AnimationImpl());
    }
    /* 初始化信息 */
    private void initInfo() {
        mSpeechRecognition = SpeechRecognition.getInstance();
//        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
//        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
//        am.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);//得到听筒模式的最大值
//        am.getStreamVolume(AudioManager.STREAM_VOICE_CALL);//得到听筒模式的当前值
        mSpeechRecognition.load(this);

    }

    private class AnimationImpl implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
            //welcomeImg.setBackgroundResource(R.drawable.welcome);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            skip(); // 动画结束后跳转到别的页面
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    private void skip() {
        startActivity(new Intent(LoadActivity.this, WFirstActivity.class));
//        startActivity(new Intent(LoadActivity.this, MainActivity.class));

        finish();
        /*
        Intent intent = new Intent(this, FifthActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("area_id", 2131492868);
        bundle.putInt("project_id", 2131492873);
        bundle.putInt("point_id", 2131492870);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);   //从下往上滑动
        */
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //playSound.mtsdestroy();
    }
}
