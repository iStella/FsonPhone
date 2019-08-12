package com.example.Fson.ToolsClass.iflytek;

import android.content.Context;
import android.os.Bundle;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

/**
 * Created by xionglei on 16/8/8.
 * 使用科大讯飞
 */
public class RecognizerMSC implements RecognizerListener {
    private static RecognizerMSC mRecognizerMSC=null;
    private CallbackRecognizer mCallbackRecognizer=null;
    private SpeechRecognizer mIat=null;

    private RecognizerMSC(Context context, CallbackRecognizer mCallbackRecognizer) {
        init(context);
        this.mCallbackRecognizer = mCallbackRecognizer;
    }

    public static synchronized RecognizerMSC getInstance(Context context, CallbackRecognizer mCallbackRecognizer) {
        if(mRecognizerMSC==null) {
            mRecognizerMSC = new RecognizerMSC(context,mCallbackRecognizer);
        }
        return mRecognizerMSC;
    }

    private void init(Context context) {
        //1.创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
        mIat = SpeechRecognizer.createRecognizer(context, mIatInitListener);
        //2.设置听写参数，详见《科大讯飞MSC API手册(Android)》SpeechConstant类
//        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
        //设置返回格式
        //mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, 5000+"");
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT,"0");
        //设置云端识别
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        //设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
//        mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
    }

    private InitListener mIatInitListener=new InitListener() {
        @Override
        public void onInit(int code) {
            //Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                //Log.d(TAG,"初始化失败，错误码：" + code);
            }else{
                //语音识别参数设置
                //mIat_setParam();
            }
        }
    };

    /**
     * 开始识别
     */
    public void startRecognition() {
        mCallbackRecognizer.onStartSpeech();
        mIat.startListening(this);
    }
    /**
     * 停止识别录音
     */
    public void stopRecognizerRecord() {
        mIat.stopListening();
    }
    public void cancleRecognition()
    {
        mIat.cancel();
    }
    @Override
    public void onVolumeChanged(int i, byte[] bytes) {

    }

    @Override
    public void onBeginOfSpeech() {
        mCallbackRecognizer.onBeginOfSpeech();
    }

    @Override
    public void onEndOfSpeech() {
        mCallbackRecognizer.onEndOfSpeech();
    }

    @Override
    public void onResult(RecognizerResult recognizerResult, boolean b) {
        mCallbackRecognizer.onResult(recognizerResult,b);
    }

    @Override
    public void onError(SpeechError speechError) {
        mCallbackRecognizer.onError(speechError);
    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) {

    }

    public synchronized void release()
    {
        if(mIat!=null)
        {
            mIat.destroy();
            mIat=null;
            mRecognizerMSC=null;
        }
    }
    public interface CallbackRecognizer
    {
        void onStartSpeech();
        void onBeginOfSpeech();
        void onEndOfSpeech();
        void onResult(RecognizerResult recognizerResult, boolean b);
        void onError(SpeechError speechError);
    }
}
