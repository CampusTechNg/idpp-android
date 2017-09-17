package com.campustechng.aminu.idpenrollment.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.campustechng.aminu.idpenrollment.R;

import asia.kanopi.fingerscan.Fingerprint;
import asia.kanopi.fingerscan.Status;

public class FingerprintCaptureActivity extends AppCompatActivity {

    private TextView tvStatus;
    private TextView tvError;
    private Fingerprint fingerprint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_capture);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvError = (TextView) findViewById(R.id.tvError);
        fingerprint = new Fingerprint();
    }

    @Override
    protected void onStart() {
        fingerprint.scan(this, printHandler, updateHandler);
        super.onStart();
    }

    @Override
    protected void onStop() {
        fingerprint.turnOffReader();
        super.onStop();
    }


    Handler updateHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            int status = msg.getData().getInt("status");
            tvError.setText("");
            switch (status) {
                case Status.INITIALISED:
                    tvStatus.setText("Setting up reader");
                    break;
                case Status.SCANNER_POWERED_ON:
                    tvStatus.setText("Reader powered on");
                    break;
                case Status.READY_TO_SCAN:
                    tvStatus.setText("Ready to scan finger");
                    break;
                case Status.FINGER_DETECTED:
                    tvStatus.setText("Finger detected");
                    break;
                case Status.RECEIVING_IMAGE:
                    tvStatus.setText("Receiving image");
                    break;
                case Status.FINGER_LIFTED:
                    tvStatus.setText("Finger has been lifted off reader");
                    break;
                case Status.SCANNER_POWERED_OFF:
                    tvStatus.setText("Reader is off");
                    break;
                case Status.SUCCESS:
                    tvStatus.setText("Fingerprint successfully captured");
                    break;
                case Status.ERROR:
                    tvStatus.setText("Error");
                    tvError.setText(msg.getData().getString("errorMessage"));
                    break;
                default:
                    tvStatus.setText(String.valueOf(status));
                    tvError.setText(msg.getData().getString("errorMessage"));
                    break;

            }
        }
    };

    Handler printHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            byte[] image;
            byte[] pgm;
            String errorMessage = "empty";
            int status = msg.getData().getInt("status");
            Intent intent = new Intent();
            intent.putExtra("status", status);
            if (status == Status.SUCCESS) {
                image = msg.getData().getByteArray("img");
                pgm = msg.getData().getByteArray("pgm");
                intent.putExtra("img", image);
                intent.putExtra("pgm",pgm);
            } else {
                errorMessage = msg.getData().getString("errorMessage");
                intent.putExtra("errorMessage", errorMessage);
            }
            setResult(RESULT_OK, intent);
            finish();
        }
    };

}
