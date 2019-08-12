package com.example.Fson.ToolsClass.iflytek;

import android.content.Context;
import android.os.Bundle;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

/**
 * Created by jiang on 2016/8/21.
 */
public class SynthesizerMSC implements SynthesizerListener {

    private static SynthesizerMSC mSynthesizerMSC=null;
    private CallbackSynthesizer mCallbackSynthesizer=null;
    private SpeechSynthesizer mTts=null;
    private SynthesizerMSC(Context context, CallbackSynthesizer mCallbackSynthesizer)
    {
        init(context);
        this.mCallbackSynthesizer=  mCallbackSynthesizer;
    }

    public static synchronized SynthesizerMSC getInstance(Context context, CallbackSynthesizer mCallbackSynthesizer)
    {
        if(mSynthesizerMSC ==null)
        {
            mSynthesizerMSC =new SynthesizerMSC(context,mCallbackSynthesizer);
        }
        return mSynthesizerMSC;
    }


    private void init(Context context)
    {
        mTts= SpeechSynthesizer.createSynthesizer(context,mTtsInitListener);
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
        //设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");
        //设置语速 loud
        mTts.setParameter(SpeechConstant.VOLUME, "100");
        //设置音量,范围 0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);//设置云端

    }


    private InitListener mTtsInitListener=new InitListener() {
        @Override
        public void onInit(int code) {
            //Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                //Log.d(TAG,"初始化失败,错误码："+code);
            } else {
                //1.创建 SpeechSynthesizer 对象, 第二个参数:本地合成时传 InitListener
                //mTts = SpeechSynthesizer.createSynthesizer(MainActivity.this, null);
                //2.合成参数设置
                //mTts_setParam();
            }
        }
    };


    /**
     * 开始合成
     */
    public void startSynthesizer(String comStr) {
        mCallbackSynthesizer.onSpeakBegin();
        mTts.startSpeaking(comStr, mSynthesizerMSC);
    }



    @Override
    public void onSpeakBegin() {
        mCallbackSynthesizer.onSpeakBegin();
    }

    @Override
    public void onBufferProgress(int i, int i1, int i2, String s) {

    }

    @Override
    public void onSpeakPaused() {

    }

    @Override
    public void onSpeakResumed() {

    }

    @Override
    public void onSpeakProgress(int i, int i1, int i2) {

    }

    @Override
    public void onCompleted(SpeechError speechError) {
        mCallbackSynthesizer.onCompleted(speechError);

    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) {

    }



    public interface CallbackSynthesizer
    {
        void onSpeakBegin();
        void onCompleted(SpeechError speechError);

    }

}
