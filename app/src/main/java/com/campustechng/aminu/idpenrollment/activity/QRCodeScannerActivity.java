package com.campustechng.aminu.idpenrollment.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRCodeScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private static final String TAG = "QRCodeActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
       // setContentView(R.layout.activity_barcode_scanner);
       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

    }

    @Override
    public void onResume() {
        super.onResume();
        // Register ourselves as a handler for scan results.
        mScannerView.setResultHandler(this);
        // Start camera on resume
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop camera on pause
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        // Prints scan results
        Log.v(TAG, rawResult.getText());
        // Prints the scan format (qrcode, pdf417 etc.)
        Log.v(TAG, rawResult.getBarcodeFormat().toString());
        //If you would like to resume scanning, call this method below:
        //mScannerView.resumeCameraPreview(this);
        Intent intent = new Intent();
        intent.putExtra("QR_CODE_RESULT", rawResult.getText());
        setResult(RESULT_OK, intent);
        finish();
    }

}
