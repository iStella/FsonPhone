package com.example.Fson.ToolsClass;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

/**
 * Created by jiping on 8/12/17.
 */

public class PlaySound {
    private SpeechSynthesizer mTts;
    boolean isIniting = false;

    public void Init(Context context){
        SpeechUtility.createUtility(context, SpeechConstant.APPID + "=57a05e6c");
        mTts = SpeechSynthesizer.createSynthesizer(context, mTtsInitListener);
    }

    public PlaySound(Context context){
        if (!isIniting){
            Init(context);
        }
    }

    /**************************** 科大讯飞语音合成 ********************************
     * 初始化合成监听。
     * */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                Log.e("","初始化失败,错误码：" + code);
                isIniting = false;
            } else {
                Log.e("","初始化成功" );
                isIniting = true;
                mTts_setParam();
            }
        }
    };

    private  void mTts_setParam(){
//        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
//        //设置发音人
//        mTts.setParameter(SpeechConstant.SPEED, "50");
//        //设置语速
//        mTts.setParameter(SpeechConstant.VOLUME, "100");
//        //设置音量,范围 0~100
//        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);//设置云端
        //清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        //设置使用本地引擎
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        //设置发音人资源路径 使用离线合成SDK时使用
        //mSynthesizer.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath());
        //设置发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, "");
        //设置合成速度
        mTts.setParameter(SpeechConstant.SPEED, "50");
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, "100");//设置音量，范围0~100
        //设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        //mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        //mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");
        // 设置播放器音频流类型
        //mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
    }

    /*
    开始播放声音
    */
    public void playSound(final String voicText) {
        mTts.startSpeaking(voicText, mTtsListener);
    }

    public void pauseSpeak() {
        mTts.pauseSpeaking();
    }

    public void stopSpeak() {
        mTts.stopSpeaking();
    }

    public void restartSpeak() {
        mTts.resumeSpeaking();
    }

    public boolean isSpeeking(){
        return mTts.isSpeaking();
    }

    public void mtsdestroy(){
        mTts.destroy();
    }

    /*
    合成回调监听。
    */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
            //showTip("开始播放");
        }
        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {}
        @Override
        public void onSpeakPaused() {}
        @Override
        public void onSpeakResumed() {}
        @Override
        public void onSpeakProgress(int i, int i1, int i2) {
            // 播放进度}
        }
        @Override
        public void onCompleted(SpeechError speechError) {
            if (speechError == null) {
                /*
                mHanler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(thiscontext,"xxxxxxx",Toast.LENGTH_LONG).show();
                    }
                }, 1300);
                */
            } else if (speechError != null) {
                //showTip(speechError.getPlainDescription(true));
            }
        }
        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {}
    };
}