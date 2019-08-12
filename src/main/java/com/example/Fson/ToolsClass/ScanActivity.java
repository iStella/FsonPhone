package com.example.Fson.ToolsClass;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.Fson.R;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private ZXingScannerView mScannerView;
    // 声音、震动控制
    private BeepManager beepManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*set it to be no title*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*set it to be full screen*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_scan);

        beepManager = new BeepManager(this);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view

    }

    @Override
    protected void onResume() {
        super.onResume();

        mScannerView.setResultHandler(ScanActivity.this); // Register ourselves as w_first_activity handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        mScannerView.stopCamera();           // Stop camera on pause
//    }


    @Override
    public void handleResult(Result result) {
        if(result!=null){

            beepManager.playBeepSoundAndVibrate();
            //boolean fromLiveScan = barcode != null;
            //这里处理解码完成后的结果，此处将参数回传到Activity处理
            //beepManager.playBeepSoundAndVibrate();

            Toast.makeText(this, "扫描成功", Toast.LENGTH_SHORT).show();

            Intent intent = getIntent();
            intent.putExtra("codedContent", result.getText());
            //intent.putExtra("codedBitmap", barcode);
            setResult(RESULT_OK, intent);
            finish();

        }
    }
}
