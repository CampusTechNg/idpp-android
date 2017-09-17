package com.campustechng.aminu.idpenrollment.activity;


import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.campustechng.aminu.idpenrollment.models.EnrollmentModel;
import com.campustechng.aminu.idpenrollment.R;

import java.util.Calendar;
import java.util.GregorianCalendar;



public class ReliefCardActivity extends BaseActivity {

    public static EnrollmentModel IDP_MODEL = null;
    ImageView profile, qrcode;
    TextView name, gender, age, status;
    Toolbar toolbar;

    public ReliefCardActivity() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relief_card);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        profile = (ImageView)  findViewById(R.id.profileImageView);
        qrcode = (ImageView) findViewById(R.id.qrcodeImageView);
        name = (TextView) findViewById(R.id.name);
        gender = (TextView) findViewById(R.id.id_gender);
        age = (TextView) findViewById(R.id.age);
        status = (TextView) findViewById(R.id.marital_status);

        if(IDP_MODEL != null) {
            if(IDP_MODEL.PROFILE != null && IDP_MODEL.PROFILE.length > 0)
                profile.setImageBitmap(BitmapFactory.decodeByteArray(IDP_MODEL.PROFILE,0,IDP_MODEL.PROFILE.length));
            if(IDP_MODEL.BARCODE != null && IDP_MODEL.BARCODE.length > 0)
                qrcode.setImageBitmap(BitmapFactory.decodeByteArray(IDP_MODEL.BARCODE,0,IDP_MODEL.BARCODE.length));
            name.setText(String.format("%s %s %s",IDP_MODEL.FIRST_NAME,IDP_MODEL.LAST_NAME, IDP_MODEL.OTHER_NAMES));
            gender.setText(IDP_MODEL.GENDER);
            if(IDP_MODEL.DOB != null && IDP_MODEL.DOB.length() > 0) {
                String[] datePart = IDP_MODEL.DOB.split("/");

                Calendar calendar = GregorianCalendar.getInstance();
                calendar.set(Integer.valueOf(datePart[2]),Integer.valueOf(datePart[1]),Integer.valueOf(datePart[0]));
                int year = calendar.get(Calendar.YEAR);
                calendar = GregorianCalendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                int current_year = calendar.get(Calendar.YEAR);
                int a = current_year - year;
                age.setText(String.valueOf(a));
            } else {
                age.setText(IDP_MODEL.ESTIMATED_AGE);
            }

            status.setText(IDP_MODEL.MARITAL_STATUS);
        }
    }

}
