package com.campustechng.aminu.idpenrollment.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;

import com.campustechng.aminu.idpenrollment.models.EnrollmentModel;
import com.campustechng.aminu.idpenrollment.core.Logger;
import com.campustechng.aminu.idpenrollment.core.Operations;
import com.campustechng.aminu.idpenrollment.core.SessionManager;

import java.util.Date;
import java.util.Set;

/**
 * Created by Muhammad Amin on 5/4/2017.
 */

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BASE_ACTIVITY";
    protected long last_activity_time_stamp;
    public SessionManager sessionManager;
    Logger logger;
    Cursor record;
    EnrollmentModel enrollmentModel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(BaseActivity.this);
        logger = Logger.getInstance(BaseActivity.this);
        //code for syncing data
       /* registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conMan.getActiveNetworkInfo();
                if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI){
                    Log.d("WifiReceiver", "Have Wifi Connection");
                    if(!syncEnabled())
                        return;
                    Set<String> ids = sessionManager.getStringSetValue(SessionManager.KEY_SYNC_IDS);
                    for(String string : ids) {
                        record = logger.selectIDP(string);
                        if(record.moveToNext()){
                            enrollmentModel = Operations.generateEnrollmentModelFromCursor(BaseActivity.this,record);
                            Operations.postUserData(BaseActivity.this,enrollmentModel);
                            sessionManager.removeStringSetValue(SessionManager.KEY_SYNC_IDS,enrollmentModel.IDP_ID);
                        }
                    }

                }

            }
        }, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));*/
    }

    private boolean syncEnabled() {
        return sessionManager.getBooleanValue(SessionManager.KEY_SYNC);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        last_activity_time_stamp = new Date().getTime();
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onResume() {
        super.onResume();
        String string = sessionManager.getStringValue(SessionManager.KEY_LOGOUT_TIME);
        int logout_time = Operations.getTime(string.length() > 0 ? string:"5 mins");
        Log.i(TAG,String.valueOf(logout_time));
        long now = new Date().getTime();
        long diff_in_minutes =  ((now - last_activity_time_stamp) / (60 * 1000) % 60);
        Log.i(TAG,String.valueOf(diff_in_minutes));
       /* if (diff_in_minutes > (logout_time*60)) {
            Intent i = new Intent(BaseActivity.this, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            startActivity(i);
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
