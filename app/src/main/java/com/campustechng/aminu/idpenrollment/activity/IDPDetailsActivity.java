package com.campustechng.aminu.idpenrollment.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.campustechng.aminu.idpenrollment.models.EnrollmentModel;
import com.campustechng.aminu.idpenrollment.core.Operations;
import com.campustechng.aminu.idpenrollment.R;
import com.campustechng.aminu.idpenrollment.fragments.ReliefItemsFragment;

public class IDPDetailsActivity extends BaseActivity {

    public static EnrollmentModel model = null;
    private ImageView photo, right_thumb, left_thumb;
    private TextView name, gender, dob_yob, marital_status, state, lga, enrollment_location, enrollment_date;
    private Button viewReliefCard, viewHistory,giveRelief;
    LinearLayout mainLayout, containerLayout;
    PopupWindow popUpWindow;
    ImageView imageView;
    Bitmap profileImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idpdetails);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        containerLayout = new LinearLayout(this);
        mainLayout = new LinearLayout(this);
        popUpWindow = new PopupWindow(this);

        photo = (ImageView) findViewById(R.id.photo) ;
        name = (TextView) findViewById(R.id.idp_name);
        gender = (TextView) findViewById(R.id.idp_gender);
        marital_status = (TextView) findViewById(R.id.idp_marital_status);
        dob_yob = (TextView) findViewById(R.id.idp_dob);
        state = (TextView) findViewById(R.id.idp_state);
        lga = (TextView) findViewById(R.id.idp_lga);
        enrollment_date = (TextView) findViewById(R.id.enrollment_date);
        enrollment_location = (TextView) findViewById(R.id.enrollment_location);
        right_thumb = (ImageView) findViewById(R.id.idp_right_thumb);
        left_thumb = (ImageView) findViewById(R.id.idp_left_thumb);
        viewReliefCard = (Button) findViewById(R.id.view_relief_card);
        viewHistory = (Button) findViewById(R.id.view_history);
        giveRelief = (Button) findViewById(R.id.give_relief_button);
        viewReliefCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReliefCardActivity.IDP_MODEL = model;
                startActivity(new Intent(IDPDetailsActivity.this,ReliefCardActivity.class));
            }
        });

        viewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IDPDetailsActivity.this,HistoryActivity.class);
                intent.putExtra("id",model.IDP_ID);
                intent.putExtra("name",model.FIRST_NAME +" "+model.LAST_NAME);
                startActivity(intent);

            }
        });
        giveRelief.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReliefItemsFragment.IDP_ID = model.IDP_ID;
                ReliefItemsFragment.IDP_Name = String.format("%s %s",model.FIRST_NAME,model.LAST_NAME);
                Intent intent = new Intent(IDPDetailsActivity.this, MainActivity.class);
                intent.putExtra("CURRENT_TAG","relief");
                intent.putExtra("navIndex",3);
                startActivity(intent);
            }
        });

        profileImage = BitmapFactory.decodeByteArray(model.PROFILE,0,model.PROFILE.length);
        imageView = new ImageView(this);
        imageView.setImageBitmap(profileImage);
        photo.setImageBitmap(profileImage);
        name.setText(String.format("%s %s %s",model.FIRST_NAME,model.LAST_NAME,model.OTHER_NAMES));
        gender.setText(model.GENDER);
        marital_status.setText(model.MARITAL_STATUS);
        if(model.DOB != null && model.DOB.length() > 0)
            dob_yob.setText(model.DOB);
        else dob_yob.setText(model.ESTIMATED_AGE);
        state.setText(model.STATE);
        lga.setText(model.LOCAL_GOVERNMENT);
        enrollment_date.setText(Operations.getDate(model.TIME_STAMP));
        enrollment_location.setText(model.LOCATION);
        right_thumb.setImageBitmap(BitmapFactory.decodeByteArray(model.RIGHT_THUMB,0,model.RIGHT_THUMB.length));
        left_thumb.setImageBitmap(BitmapFactory.decodeByteArray(model.LEFT_THUMB,0,model.LEFT_THUMB.length));



       /* ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        containerLayout.setOrientation(LinearLayout.VERTICAL);
*//*                containerLayout.addView(photo, layoutParams);
                popUpWindow.setContentView(containerLayout);*//*
        mainLayout.addView(photo, layoutParams);
        setContentView(mainLayout);


        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    popUpWindow.showAtLocation(mainLayout, Gravity.CENTER, 10, 10);
                    popUpWindow.update(50, 50, 320, 90);
            }
        });*/
    }

}
