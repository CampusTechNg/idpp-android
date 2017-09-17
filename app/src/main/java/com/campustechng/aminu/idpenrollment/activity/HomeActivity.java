package com.campustechng.aminu.idpenrollment.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.campustechng.aminu.idpenrollment.R;


public class HomeActivity extends BaseActivity {


    ImageView enroll, verify, search, histrory, settings, exit;
    Toolbar toolbar;
    Bundle bundle;
    Intent intent;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        enroll = (ImageView) findViewById(R.id.enroll);
        verify = (ImageView) findViewById(R.id.verify);
        search = (ImageView) findViewById(R.id.search);
        histrory = (ImageView) findViewById(R.id.relief);
        settings = (ImageView) findViewById(R.id.settings);
        exit = (ImageView) findViewById(R.id.logout);

        enroll.setOnClickListener(onClickListener);
        verify.setOnClickListener(onClickListener);
        search.setOnClickListener(onClickListener);
        histrory.setOnClickListener(onClickListener);
        settings.setOnClickListener(onClickListener);
        exit.setOnClickListener(onClickListener);

    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.enroll:
                    intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.putExtra("CURRENT_TAG","enroll");
                    intent.putExtra("navIndex",0);
                    startActivity(intent);
                    break;
                case R.id.verify:
                    intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.putExtra("CURRENT_TAG","verify");
                    intent.putExtra("navIndex",1);
                    startActivity(intent);
                    break;
                case R.id.search:
                    intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.putExtra("CURRENT_TAG","search");
                    intent.putExtra("navIndex",2);
                    startActivity(intent);
                    break;
                case R.id.relief:
                    intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.putExtra("CURRENT_TAG","relief");
                    intent.putExtra("navIndex",3);
                    startActivity(intent);
                    break;
                case R.id.settings:
                    startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
                    break;
                case R.id.logout:
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    break;
            }
        }
    };

}
