package com.example.Fson.WorkClass.Speech;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.example.Fson.ToolsClass.JsonParser;
import com.example.Fson.ToolsClass.iflytek.FucUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * Created by PC on 2018/4/1.
 */

public class SpeechRecognition {

    private int x = 0;
    private SpeechSynthesizer mTts;
    public boolean isIniting = false;
    private static SpeechRecognition mSpeechRecognition;
    private static SpeechCallBack mSpeechCallBack;
    private static Context mcontext;
    private String TAG = "提示信息";
    //科大讯飞
    private SpeechRecognizer mIat;  // 语音听写对象
    // 本地语法文件
    private String mLocalGrammar = null;
    private String mLocalKey = null;
    private ArrayList<String> mLocalKeys = new ArrayList<>();
    private static final String GRAMMAR_TYPE_BNF = "bnf";
    // 语法、词典临时变量
    private String mContent;
    private boolean SpeechListing = false;

    private AudioTrack audioTrack;
    private int audioBufSize;
    String path  = "";
    Player player = new Player();
    private static MediaPlayer mMediaPlayer = null; // 声明 MediaPlayer引用
    public ArrayList<String> playStrings = new ArrayList<>();
    public Handler playHandler = new Handler();
    public AudioManager audioManager = null;

    private SpeechRecognition(){}
    /* 对SpeechRecognition类的所有对象的getInstance同步 */
    public static synchronized SpeechRecognition getInstance(){
        if(mSpeechRecognition == null){
            mSpeechRecognition = new SpeechRecognition();
        }
        return mSpeechRecognition;
    }

    public void load(Context context){
        if (!isIniting){
            mcontext = context;
            //初始化讯飞iflytek,appid-jjg 57a05e6c  jqk 57c67a39  xxh 57a9ce49
            SpeechUtility.createUtility(mcontext, SpeechConstant.APPID + "=57a05e6c");
            // 初始化定义mIat/mTts
            mIat = SpeechRecognizer.createRecognizer(mcontext, mIatInitListener);
            mTts = SpeechSynthesizer.createSynthesizer(mcontext, mTtsInitListener);
            /* 可以分离的部分？ */
            // 初始化语法、命令词
            mLocalGrammar = FucUtil.readFile(mcontext, "grammar.txt", "utf-8");
            mLocalKey = FucUtil.readFile(mcontext, "key.txt", "utf-8");
            if (mLocalKey.length() != 0) {
                String[] Keys = mLocalKey.split("\\n|\\r");
                mLocalKey = "";
                for (String keys : Keys) {
                    if (keys.length() != 0) {
                        mLocalKeys.add(keys);
                    }
                }
                for (int i = 0; i < mLocalKeys.size() - 1; i++) {            //从左向右循环
                    for (int j = mLocalKeys.size() - 1; j > i; j--) {       //从右往左内循环
                        if (mLocalKeys.get(j).equals(mLocalKeys.get(i))) {
                            mLocalKeys.remove(j);                                //相等则移除
                        }
                    }
                }
                for (String keys : mLocalKeys) {
                    mLocalKey += keys + "|";
                }
                mLocalKey = mLocalKey.substring(0, mLocalKey.length() - 1);
            }
            /* 初始化Audio播放器 */
            audioBufSize = AudioTrack.getMinBufferSize(16000,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);

            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 16000,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    audioBufSize,
                    AudioTrack.MODE_STREAM);
            //audioTrack.setStereoVolume(1.0f, 1.0f);
            audioTrack.setStereoVolume(0.5f, 0.5f);

            mMediaPlayer = new MediaPlayer();
            try {
                AssetFileDescriptor fileDescriptor = mcontext.getAssets().openFd("beep.ogg");// assets与 res 平级
                mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
                mMediaPlayer.prepare();
                Log.e("AudioTrack", "音效文件加载成功！");
            } catch (Exception e) {
                Log.e("AudioTrack", "音效文件加载错误！");
            }
            isIniting = true;
        }
    }

    public void startMediaPlay(){
        Log.e("mMediaPlayer", "滴");
//        mMediaPlayer.start();
    }

    public void loadSpeech(SpeechCallBack speechCallBack, Context context){
        mSpeechCallBack = speechCallBack;
        mcontext = context;
        load(mcontext);
    }

    /* 识别监听器无UI */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int volume, byte[] data) {}
        @Override
        public void onResult(final RecognizerResult result, boolean isLast) {
            if (null != result) {
                Log.e("科大讯飞", "recognizer result：" + result.getResultString());
                String text = JsonParser.parseLocalGrammarResult(result.getResultString());
                mSpeechCallBack.getResult(text);
            } else {
                Log.e("科大讯飞", "recognizer result : null");
            }
        }
        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            Log.e("科大讯飞", "结束说话");
            mSpeechCallBack.getpoint(false);
        }
        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            Log.e("科大讯飞", "开始说话");
            mSpeechCallBack.getpoint(true);
        }
        @Override
        public void onError(SpeechError error) {
            if (SpeechListing){
                Log.e("科大讯飞", "onError Imat尝试重启！");
                mSpeechRecognition.startSpeech2();
            }else {
                Log.e("科大讯飞", "onError Imat禁止重启！");
                /* 因为只存在pause的一次false禁止重启，禁止后开启>>>防止尝试手动关闭语音(pause)时不能触发error重新唤起语音 */
                //SpeechListing = true;
            }
        }
        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            //Log.e("科大讯飞", "Event事件");
        }
    };

    public void startSpeech(){
        SpeechListing = true;
        if (!mIat.isListening()){
            /* 如果没有在监听-开启监听 */
            Log.e("科大讯飞", "mIat没有在监听！");
            if (mIat.startListening(mRecognizerListener) == 0){
                Log.e("科大讯飞", "mIat开启监听成功！");
            }else {
                Log.e("科大讯飞", "mIat开启监听失败！");
            }
        }else {
            /* 如果已经在监听-先关闭 */
            Log.e("科大讯飞", "mIat关闭监听！");
            mSpeechRecognition.stopSpeech();
            /* 这一步可以考虑换成startSpeech */
            SpeechListing = true;
            /* 再开启 */
            Log.e("科大讯飞", "mIat开启监听！");
            if (mIat.startListening(mRecognizerListener) == 0){
                Log.e("科大讯飞", "mIat开启监听成功！");
            }else {
                Log.e("科大讯飞", "mIat开启监听失败！");
            }
        }
    }

    public void startSpeech2(){
        SpeechListing = true;
        if (!mIat.isListening()){
            /* 如果没有在监听-开启监听 */
            Log.e("科大讯飞", "mIat没有在监听！");
            if (mIat.startListening(mRecognizerListener) == 0){
                Log.e("科大讯飞", "mIat开启成功！");
            }else {
                Log.e("科大讯飞", "mIat开启失败！");
            }
        }else {
            Log.e("科大讯飞", "mIat已在监听中！");
        }
    }

    public void stopSpeech(){
        SpeechListing = false;
        mIat.stopListening();
        if (mIat.isListening()){
            /* 如果正在监听-关闭 */
            mIat.stopListening();
            Log.e("科大讯飞", "mIat关闭成功！");
        }else {
            /* 否则-不操作 */
            Log.e("科大讯飞", "mIat不在监听中！");
        }
    }

    public boolean isListening(){
        return mIat.isListening();
    }

    public void destroySpeech(){
        mIat.cancel();
        mIat.destroy();
    }

    /* 开始播放声音 */
    public void playSound() {
        for (String s : playStrings){
            Log.e("AudioTrack：", "String>>>" + s);
        }
        if (playStrings.size() > 0){
            String voicText = playStrings.get(0);
            Log.e("AudioTrack", voicText);
            if (voicText.length() > 0){
                //测试能否解决音响延迟问题
                //voicText = "。。。。。。。" + voicText;
                Log.e("AudioTrack", voicText);
                stopSpeak();
                //mTts.startSpeaking(voicText, mTtsListener);
                path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "FSON/tts.pcm";
                File ttsFile = new File(path);
                if (ttsFile.exists()){
                    if (ttsFile.delete()){
                        Log.e("AudioTrack：", "tts.pcm删除成功！");
                        try {
                            if (ttsFile.createNewFile()){
                                mTts.synthesizeToUri(voicText, path, mTtsListener);
                                Log.e("AudioTrack：", "tts.pcm创建成功！");
                            }else {
                                Log.e("AudioTrack：", "tts.pcm创建失败！");
                            }
                        }catch (Exception e){
                            Log.e("AudioTrack：", "tts.pcm创建失败！");
                            e.printStackTrace();
                        }
                    }else {
                        Log.e("AudioTrack：", "tts.pcm删除失败！");
                    }
                }else {
                    Log.e("AudioTrack：", "无tts.pcm文件！");
                    mTts.synthesizeToUri(voicText, path, mTtsListener);
                }
            }else {
                Log.e("AudioTrack：", "播放内容为空！");
                playStrings.remove(0);
                for (String s : playStrings){
                    Log.e("AudioTrack：", "String>>>" + s);
                }
                playSound();
            }
        }else {
            Log.e("AudioTrack：", "已播完！");
        }
    }

    public void stopSpeak() {
        playHandler.removeCallbacks(playThread);
        player.b = false;
        mTts.stopSpeaking();
        audioTrack.stop();
    }

    public boolean isSpeeking(){
        return mTts.isSpeaking();
    }

    public void mtsdestroy(){
        audioTrack.release();
    }

    /* 合成回调监听 */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
            Log.e("mTtsListener", "onSpeakBegin");
        }
        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {
            //Log.e("mTts-onBProgress", String.valueOf(i));
            //Log.e("mTts-onBProgress", String.valueOf(i1));
            //Log.e("mTts-onBProgress", String.valueOf(i2));
        }
        @Override
        public void onSpeakPaused() {
            Log.e("mTtsListener", "onSpeakPaused");
        }
        @Override
        public void onSpeakResumed() {
            Log.e("mTtsListener", "onSpeakResumed");
        }
        @Override
        public void onSpeakProgress(int i, int i1, int i2) { /* 进度 */
            //Log.e("mTtsListener-onProgress", String.valueOf(i));
            //Log.e("mTtsListener-onProgress", String.valueOf(i1));
            //Log.e("mTtsListener-onProgress", String.valueOf(i2));
        }
        @Override
        public void onCompleted(SpeechError speechError) {
            if (speechError == null) {
                /* pcm文件生成成功>>> */
                Log.e("AudioTrack", "开始播报!");
                // start() status -1
                audioTrack.play();
                player = new Player();
                player.start();
            } else {
                Log.e("AudioTrack", "语音合成出错!");
                playStrings.remove(0);
                for (String s : playStrings){
                    Log.e("AudioTrack：", "String>>>" + s);
                }
                playSound();
                //showTip(speechError.getPlainDescription(true));
            }
        }
        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
            Log.e("mTts-onEvent", String.valueOf(i));
            Log.e("mTts-onEvent", String.valueOf(i1));
            Log.e("mTts-onEvent", String.valueOf(i2));
        }
    };

    private class Player extends Thread{
        byte[] readData = new byte[audioBufSize * 2];
        int skipOff = 0;
        FileInputStream fileInputStream;
        boolean b = true;

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            while(b){
                try {
                    fileInputStream = new FileInputStream(new File(path));
                    fileInputStream.skip((long)skipOff);
                    //Log.e("需要跳过的字节", String.valueOf(skipOff));
                    //Log.e("实际跳过的字节", String.valueOf(x));
                    skipOff += audioBufSize * 2;
                    int readSize = fileInputStream.read(readData, 0, audioBufSize * 2);
                    //Log.e("实际读取的字节数", String.valueOf(readSize));
                    fileInputStream.close();
                    if (readSize < audioBufSize * 2){
                        b = false;
                        if (readSize != -1){
                            audioTrack.write(readData, 0, readSize);
                        }
                        Log.e("AudioTrack：", "播报完成，删除缓存文件！");
                        /* 删除语音缓存文件 */
                        path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "FSON/tts.pcm";
                        File ttsFile = new File(path);
                        if (ttsFile.exists()){
                            if (ttsFile.delete()){
                                Log.e("AudioTrack：", "tts.pcm删除成功！");
                            }else {
                                Log.e("AudioTrack：", "tts.pcm删除失败！");
                            }
                        }else {
                            Log.e("AudioTrack：", "无tts.pcm文件！");
                        }
                        Log.e("AudioTrack：", "数S后尝试循环下一个播报！");
                        playHandler.postDelayed(playThread, 700);
                    }else {
                        audioTrack.write(readData, 0, audioBufSize * 2);
                    }
                } catch (Exception e) {
                    Log.e("AudioTrack", "Play被中断(播报错误)！");
                    // TODO: handle exception
                }
            }
        }
    }

    public Thread playThread = new Thread(){
        @Override
        public void run(){
            /* 开始数组中的下一个语音播报 */
            if (playStrings.size() > 0) playStrings.remove(0);
            for (String s : playStrings){
                Log.e("AudioTrack：", "String>>>" + s);
            }
            playSound();
        }
    };

    public void addPlayString (String voicText){
        playStrings.add(voicText);
    }

    public void freshPlayStrings (String voicText){
        playHandler.removeCallbacks(playThread);
        player.b = false;
        playStrings.clear();
        playStrings.add(voicText);
        playSound();
    }

    /* 初始化监听器语音识别(无界面) */
    private InitListener mIatInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            mIat_setParam(code);
        }
    };

    /* 初始化监听器语音合成 */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            mTts_setParam(code);
        }
    };

    private boolean mIat_setParam(int code){
        Log.e("科大讯飞", "SpeechRecognizer init() code = " + code);
        if (code != ErrorCode.SUCCESS) {
            Log.e("科大讯飞", "初始化失败,错误码：" + String.valueOf(code));
        }else{
            Log.e("科大讯飞", "初始化成功：" + String.valueOf(code));
            //设置本地识别引擎
            mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            //设置返回结果为json格式
            mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
            //设置本地识别使用语法id
            mIat.setParameter(SpeechConstant.LOCAL_GRAMMAR, "cmdaction");
            //设置本地识别的门限值
            mIat.setParameter(SpeechConstant.ASR_THRESHOLD, "30");
            mIat.setParameter(SpeechConstant.DOMAIN, "iat");
            //设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
            // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
            mIat.setParameter(SpeechConstant.VAD_BOS, "10000");
            mIat.setParameter(SpeechConstant.VAD_EOS, "1500");
            // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
            mIat.setParameter(SpeechConstant.ASR_PTT, "0");

            mContent = mLocalGrammar;
            if (mLocalKey.length() != 0){
                mContent = mContent.substring(0, mContent.length() - 2);
                mContent += mLocalKey + ";";
            }
            //Log.e("mContent", mContent);
            mIat.setParameter(SpeechConstant.TEXT_ENCODING,"utf-8");
            int ret = mIat.buildGrammar(GRAMMAR_TYPE_BNF, mContent, mLocalGrammarListener);
            if(ret != ErrorCode.SUCCESS){
                if(ret == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED){
                    Log.e("科大讯飞", "没有找到SDK，错误码：" + String.valueOf(ret));
                }else {
                    Log.e("科大讯飞", "语法构建失败，错误码：" + String.valueOf(ret));
                }
            }else {
                Log.e("科大讯飞", "语法构建成功：" + ret);
            }
        }
        return true;
    }

    private  void mTts_setParam(int code){
        if (code != ErrorCode.SUCCESS) {
            Log.e("科大讯飞", "初始化失败,错误码：" + String.valueOf(code));
            //////////???????????????
            isIniting = false;
        } else {
            Log.e("科大讯飞", "初始化成功：" + String.valueOf(code));
            isIniting = true;
//            mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
//            //设置发音人
//            mTts.setParameter(SpeechConstant.SPEED, "50");
//            //设置语速
//            mTts.setParameter(SpeechConstant.VOLUME, "100");
//            //设置音量,范围 0~100
//            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);//设置云端
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
    }

    /* 本地构建语法监听器 */
    private GrammarListener mLocalGrammarListener = new GrammarListener() {
        @Override
        public void onBuildFinish(String grammarId, SpeechError error) {
            if(error == null){
                Log.e("科大讯飞","语法构建成功：" + String.valueOf(grammarId));
            }else{
                Log.e("科大讯飞", "语法构建失败,错误码：" + String.valueOf(error.getErrorCode()));
            }
        }
    };
}
