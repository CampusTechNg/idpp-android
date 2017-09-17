package com.campustechng.aminu.idpenrollment.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.campustechng.aminu.idpenrollment.core.GPS;
import com.campustechng.aminu.idpenrollment.core.Operations;
import com.campustechng.aminu.idpenrollment.R;
import com.campustechng.aminu.idpenrollment.core.SessionManager;

public class SettingsActivity extends BaseActivity implements AdapterView.OnItemClickListener, LocationListener  {

    LinearLayout location_enable, location_name, auto_sync, logout;
    CheckBox location, sync;
    TextView loc_name_textview, logout_time, coordinate_textview, currentTextView;
    String popUpContents[];
    PopupWindow popupWindow;
    LocationManager mLocationManager;
    GPS gps;
    ProgressDialog progressDialog;
    String editor_value;
    String DEFAULT_TIME = "5 mins";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        location_enable = (LinearLayout) findViewById(R.id.location_enable);
        location_name = (LinearLayout) findViewById(R.id.location_name);
        auto_sync = (LinearLayout) findViewById(R.id.sync);
        logout = (LinearLayout) findViewById(R.id.logout);
       // location = (CheckBox) findViewById(R.id.location_checkbox);
        sync = (CheckBox) findViewById(R.id.sync_checkbox);
        loc_name_textview = (TextView) findViewById(R.id.loc_name_textview);
        logout_time = (TextView) findViewById(R.id.logout_time_textview);
        coordinate_textview = (TextView) findViewById(R.id.coordinates_textview);

        location_enable.setOnClickListener(onClickListener);
        location_name.setOnClickListener(onClickListener);
        auto_sync.setOnClickListener(onClickListener);
        logout.setOnClickListener(onClickListener);


        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        boolean auto_sync = sessionManager.getBooleanValue(SessionManager.KEY_SYNC); //settings.getBoolean(getString(R.string.auto_sync), false);
        String coordinates = sessionManager.getStringValue(SessionManager.KEY_COODINATES); // settings.getString(getString(R.string.coordinates),"0.00000,0.00000");
        String loc_name = sessionManager.getStringValue(SessionManager.KEY_LOCATION_NAME); //settings.getString(getString(R.string.loc_name),"Select...");
        String logout = sessionManager.getStringValue(SessionManager.KEY_LOGOUT_TIME); //settings.getString(getString(R.string.logout_time),"5 mins");

        sync.setChecked(auto_sync);
        coordinate_textview.setText(coordinates.length()>0 ? coordinates:getString(R.string.default_coordinates));
        loc_name_textview.setText(loc_name.length()>0 ? loc_name:getString(R.string.drop_down_default_value));
        logout_time.setText(logout.length()>0 ? logout:DEFAULT_TIME);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.location_enable:
                    editor_value = SessionManager.KEY_COODINATES; //getString(R.string.coordinates);
                    gps = GPS.getInstance(SettingsActivity.this);
                    if(Operations.hasPermissions(SettingsActivity.this, Operations.PERMISSIONS)) {
                        if (!gps.isGPSEnabled()){
                            Operations.showSettingsAlert(SettingsActivity.this,"Location","You need to enable your location under settings","Settings","Cancel"); return;}
                        gps.setLocationListener(SettingsActivity.this);
                        gps.requestLocationUpadate();
                        progressDialog = ProgressDialog.show(SettingsActivity.this, "Getting coordinates",  "Please wait...", false);
                        progressDialog.setCancelable(true);
                        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
                            @Override
                            public void onCancel(DialogInterface dialog){
                                progressDialog.dismiss();
                            }});
                    }
                     else {
                        Operations.requestForPermission(SettingsActivity.this,0);
                    }
                    break;
                case R.id.location_name:
                    editor_value = SessionManager.KEY_LOCATION_NAME; //getString(R.string.loc_name);
                    currentTextView = loc_name_textview;
                    popUpContents = getResources().getStringArray(R.array.borno_lga);
                    popupWindow = popupDropDown();
                    popupWindow.showAsDropDown(v, -5, 0);
                    break;
                case R.id.sync:
                    sync.toggle();
                    sessionManager.setBooleanValue(SessionManager.KEY_SYNC,sync.isChecked());
                    break;
                case R.id.logout:
                    editor_value = SessionManager.KEY_LOGOUT_TIME; //getString(R.string.logout_time);
                    currentTextView = logout_time;
                    popUpContents = getResources().getStringArray(R.array.logout_times);
                    popupWindow = popupDropDown();
                    popupWindow.showAsDropDown(v, -5, 0);
                    break;
            }
        }
    };

    public PopupWindow popupDropDown() {

        // initialize a pop up window type
        PopupWindow popupWindow = new PopupWindow(SettingsActivity.this);

        // the drop down list is a list view
        ListView listView = new ListView(SettingsActivity.this);

        // set our adapter and pass our pop up window contents
        listView.setAdapter(dropDownListAdapter(popUpContents));

        // set the item click listener
        listView.setOnItemClickListener(this);

        // some other visual settings
        popupWindow.setFocusable(true);
        popupWindow.setWidth(350);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        // set the list view as pop up window content
        popupWindow.setContentView(listView);

        return popupWindow;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
        // add some animation when a list item was clicked
        Animation fadeInAnimation = AnimationUtils.loadAnimation(v.getContext(), android.R.anim.fade_in);
        fadeInAnimation.setDuration(10);
        v.startAnimation(fadeInAnimation);

        // dismiss the pop up
        popupWindow.dismiss();
        // get the text and set it as the button text
        String selectedItemText = ((TextView) v).getText().toString();
        setCurrentTextView(selectedItemText);
        sessionManager.setStringValue(editor_value,selectedItemText);
    }

    private void setCurrentTextView(String text) {
        currentTextView.setText(text);
    }

    private ArrayAdapter<String> dropDownListAdapter(String list[]) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SettingsActivity.this, android.R.layout.simple_list_item_1, list) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                // setting the ID and text for every items in the list
                String item = getItem(position);

                // visual settings for the list item
                TextView listItem = new TextView(getContext());

                listItem.setText(item);
                listItem.setTextSize(22);
                listItem.setPadding(10, 10, 10, 10);
                listItem.setTextColor(Color.WHITE);

                return listItem;
            }
        };
        return adapter;
    }


    @Override
    public void onLocationChanged(Location loc) {
        String coods = String.format("%.5f,%.5f",loc.getLongitude(),loc.getLatitude());
        sessionManager.setStringValue(SessionManager.KEY_COODINATES, coods);
        coordinate_textview.setText(coods);
        gps.removeLocationUpadate();
        progressDialog.dismiss();
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
