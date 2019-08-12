package com.example.Fson.WorkClass.asynchttp;

import android.os.Handler;
import android.os.Message;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by jiang on 2016/8/19.
 */
public class HttpUtils {

    public static AsyncHttpClient client = new AsyncHttpClient();

    public static void getNewsJSON(final String url, final Handler handler) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn;
                InputStream is;
                try {
                    conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setRequestMethod("GET");
                    is = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String line = "";
                    StringBuilder result = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    Message msg = new Message();
                    msg.obj = result.toString();
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }).start();

    }


    public static void  ClientGet(String url, NetCallBack cb){
    client.get(url,cb);

    }

    public static void  ClientPost(final int i, final String url, final RequestParams params, final NetCallBack cb){
       final Handler mHandler = new Handler();
        Runnable r = new Runnable() {
            @Override
                public void run() {
                client.post(url,params,cb);
            }
        };
        mHandler.postDelayed(r, (i + 1) * 300);
    }



    public static void  ClientPost(String url, RequestParams params, NetCallBack cb){
        client.post(url,params,cb);
    }

}
