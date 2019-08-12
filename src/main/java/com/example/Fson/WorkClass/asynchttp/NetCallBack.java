package com.example.Fson.WorkClass.asynchttp;

import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

/**
 * Created by jiang on 2016/8/20.
 */
public abstract class NetCallBack extends AsyncHttpResponseHandler {
    @Override
    public void onStart() {
        Log.i("===","请求开始。。。。");
        super.onStart();
    }


    @Override
    public void onSuccess(int i, Header[] headers, byte[] bytes) {
        onMySuccess(new String(bytes));
        Log.i("===","请求成功。。。。");
    }

    @Override
    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
        onMyFailure(throwable);
        Log.i("===","请求失败。。。。");
    }

    public abstract void  onMySuccess(String result);
    public abstract void  onMyFailure(Throwable throwable);


}
