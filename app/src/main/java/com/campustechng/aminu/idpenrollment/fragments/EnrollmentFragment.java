package com.campustechng.aminu.idpenrollment.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.campustechng.aminu.idpenrollment.core.SourceAfisUtils;
import com.campustechng.aminu.idpenrollment.models.EnrollmentModel;
import com.campustechng.aminu.idpenrollment.core.FingerPrintMatcher;
import com.campustechng.aminu.idpenrollment.models.HistoryModel;
import com.campustechng.aminu.idpenrollment.core.Logger;
import com.campustechng.aminu.idpenrollment.core.Operations;
import com.campustechng.aminu.idpenrollment.R;
import com.campustechng.aminu.idpenrollment.core.SessionManager;
import com.campustechng.aminu.idpenrollment.activity.BaseActivity;
import com.campustechng.aminu.idpenrollment.activity.FingerprintCaptureActivity;
import com.campustechng.aminu.idpenrollment.activity.IDPDetailsActivity;
import com.campustechng.aminu.idpenrollment.activity.QRCodeScannerActivity;
import com.campustechng.aminu.idpenrollment.sourceafis.simple.Fingerprint;
import com.campustechng.aminu.idpenrollment.sourceafis.simple.Person;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.TimeZone;

import asia.kanopi.fingerscan.Status;

import static android.app.Activity.RESULT_OK;


public class EnrollmentFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = "enrollment";
    private static final int CAMERA_START_REQUEST = 0;
    private static final int FINGERPRINT_CAPTURE_REQUEST = 1;
    private static final String RIGHT_THUMB = "right_thumb";
    private static final String LEFT_THUMB = "left_thumb";
    private static final String RIGHT_INDEX = "right_index";
    private static final String LEFT_INDEX = "left_index";
    private static final int QR_CODE_REQUEST = 2;
    private String CURRENT_BUNDLE = "";





    // TODO: Rename and change types of parameters
    private float oldXvalue, oldYvalue;
    private String strState, strLga, selection;
    private String strGender, marital_status, strAge;
    private boolean saveFlag = true;
    private Bitmap bitmap, rightThumb, leftThumb, qrCodeImage, profileBitmap;
    private Bundle bundle;
    private TextView idpID, first_name, last_name, other_names, dateOfBirth;
    private TextView gender, maritalStatus,estimatedAge, state, localGovernment, currentTextView;
    private ImageView profilePicture, rightThumbFingerprint, leftThumbFingerprint, scanToken;
    private RadioButton rb_date_of_birth, rb_estimated_age;
    FloatingActionButton save;
    QRCodeWriter qrCodeWriter;
    private Logger logger;
    private Cursor result;
    EnrollmentModel model;
    HistoryModel historyModel;
    String popUpContents[];
    FingerPrintMatcher fingerPrintMatcher;
    ByteArrayOutputStream out;
    PopupWindow popupWindow;
    private OnFragmentInteractionListener mListener;
    SessionManager sessionManager;

    public EnrollmentFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger = Logger.getInstance(getContext());
        qrCodeWriter = new QRCodeWriter();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sessionManager = ((BaseActivity)getActivity()).sessionManager;
        if(sessionManager.getStringValue(SessionManager.KEY_COODINATES).equals(getString(R.string.default_coordinates)) || sessionManager.getStringValue(SessionManager.KEY_LOCATION_NAME).equals(""))
            Toast.makeText(getContext(),"Please set your location in settings.",Toast.LENGTH_LONG).show();
        View v = getView();
        idpID = (TextView) v.findViewById(R.id.id);
        first_name = (TextView) v.findViewById(R.id.firstName);
        last_name = (TextView) v.findViewById(R.id.lastName);
        other_names = (TextView) v.findViewById(R.id.othernames);
        dateOfBirth = (TextView) v.findViewById(R.id.txt_date_of_birth);
        estimatedAge = (TextView) v.findViewById(R.id.estimated_age);
        estimatedAge.setEnabled(false);
        gender = (TextView) v.findViewById(R.id.id_gender);
        maritalStatus = (TextView) v.findViewById(R.id.marital_status);
        state = (TextView) v.findViewById(R.id.state);
        localGovernment = (TextView) v.findViewById(R.id.local_government);
        rb_date_of_birth = (RadioButton) v.findViewById(R.id.rb_date_of_birth);
        rb_estimated_age = (RadioButton) v.findViewById(R.id.rb_estimated_age);
        profilePicture = (ImageView) v.findViewById(R.id.profile);
        rightThumbFingerprint = (ImageView) v.findViewById(R.id.right_thumb);
        rightThumb = BitmapFactory.decodeResource(getResources(),R.drawable.fingerprint);
        rightThumbFingerprint.setImageBitmap(rightThumb);
        leftThumb = BitmapFactory.decodeResource(getResources(),R.drawable.fingerprint);
        leftThumbFingerprint = (ImageView) v.findViewById(R.id.left_thumb);
        leftThumbFingerprint.setImageBitmap(leftThumb);
        scanToken = (ImageView) v.findViewById(R.id.scan_token);
        save = (FloatingActionButton) v.findViewById(R.id.fab);
        oldXvalue = save.getX();
        oldYvalue = save.getY();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        final int height = displaymetrics.heightPixels;
        final int width = displaymetrics.widthPixels;
       /* save.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent me){
                switch (me.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        oldXvalue = v.getX() - me.getRawX();
                        oldYvalue = v.getY() - me.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                      if(width > ( me.getRawX()+50) && height > ( me.getRawY()+50) && me.getRawX() > 50 && me.getRawY() > 200) {
                          v.animate()
                                  .x(me.getRawX() + oldXvalue)
                                  .y(me.getRawY() + oldYvalue)
                                  .setDuration(0)
                                  .start();
                      }
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });*/


        //attaching events
        dateOfBirth.setOnClickListener(onClickListener);
        rb_date_of_birth.setOnClickListener(onClickListener);
        rb_estimated_age.setOnClickListener(onClickListener);
        profilePicture.setOnClickListener(onClickListener);
        rightThumbFingerprint.setOnClickListener(onClickListener);
        leftThumbFingerprint.setOnClickListener(onClickListener);
        maritalStatus.setOnClickListener(onClickListener);
        estimatedAge.setOnClickListener(onClickListener);
        gender.setOnClickListener(onClickListener);
        state.setOnClickListener(onClickListener);
        localGovernment.setOnClickListener(onClickListener);
        scanToken.setOnClickListener(onClickListener);
        save.setOnClickListener(onClickListener);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAMERA_START_REQUEST:
                if(resultCode == RESULT_OK) {
                    Bitmap image = (Bitmap) data.getExtras().get("data");
                    profilePicture.setImageBitmap(image);
                    profileBitmap = image;
                }
                break;
            case FINGERPRINT_CAPTURE_REQUEST:
                if(resultCode == RESULT_OK) {
                    int status = data.getIntExtra("status", Status.ERROR);
                    if (status == Status.SUCCESS) {
                        byte[] img = data.getByteArrayExtra("img");
                        Bitmap fingerPrintImage = BitmapFactory.decodeByteArray(img, 0, img.length);
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        fingerPrintImage.compress(Bitmap.CompressFormat.PNG, 100, out);
                        fingerPrintImage = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                        switch (CURRENT_BUNDLE) {
                            case RIGHT_THUMB:
                                rightThumb = fingerPrintImage;
                                rightThumbFingerprint.setImageBitmap(fingerPrintImage);
                                break;
                            case LEFT_THUMB:
                                leftThumb = fingerPrintImage;
                                leftThumbFingerprint.setImageBitmap(fingerPrintImage);
                                break;
                        }
                    } else {
                        String errorMesssage = data.getStringExtra("errorMessage");
                        Toast.makeText(getContext(), "-- Error: " + errorMesssage + " --", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case QR_CODE_REQUEST:
                if(data == null)
                    return;
                String scanResult = data.getStringExtra("QR_CODE_RESULT");
                if(scanResult.trim().length() > 0) {
                    logger = Logger.getInstance(getContext());
                    result = logger.selectIDP(scanResult);
                    if(result != null && result.moveToNext()) {
                        model = Operations.generateEnrollmentModelFromCursor(getContext(),result);
                        IDPDetailsActivity.model = model;
                        startActivity(new Intent(getContext(),IDPDetailsActivity.class));
                    }else{
                        idpID.setText(scanResult);
                    }

                } else {

                }
                break;
        }
    }



    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.scan_token:
                    startActivityForResult(new Intent(getContext(),QRCodeScannerActivity.class),QR_CODE_REQUEST);
                    break;
                case R.id.estimated_age:
                    popUpContents = getResources().getStringArray(R.array.age);
                    currentTextView = estimatedAge;
                    popupWindow = popupDropDown();
                    popupWindow.showAsDropDown(v, -5, 0);
                    break;
                case R.id.id_gender:
                    popUpContents = getResources().getStringArray(R.array.gender);
                    currentTextView = gender;
                    popupWindow = popupDropDown();
                    popupWindow.showAsDropDown(v, -5, 0);
                    break;
                case R.id.marital_status:
                    popUpContents = getResources().getStringArray(R.array.status);
                    currentTextView = maritalStatus;
                    popupWindow = popupDropDown();
                    popupWindow.showAsDropDown(v, -5, 0);
                    break;
                case R.id.state:
                    popUpContents = getResources().getStringArray(R.array.states);
                    currentTextView = state;
                    popupWindow = popupDropDown();
                    popupWindow.showAsDropDown(v, -5, 0);
                    break;
                case R.id.local_government:
                    populatePopUpContents();
                    currentTextView = localGovernment;
                    popupWindow = popupDropDown();
                    popupWindow.showAsDropDown(v, -5, 0);
                    break;
                case R.id.rb_date_of_birth:
                    dateOfBirth.setEnabled(true);
                    rb_estimated_age.setChecked(false);
                    estimatedAge.setEnabled(false);
                    break;
                case R.id.rb_estimated_age:
                    rb_date_of_birth.setChecked(false);
                    estimatedAge.setEnabled(true);
                    dateOfBirth.setEnabled(false);
                    break;
                case R.id.txt_date_of_birth:
                    dateOfBirth.setError(null);
                    displayDatePicker();
                    break;
                case R.id.profile:
                    Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                    startActivityForResult(cameraIntent, CAMERA_START_REQUEST);
                    break;
                case R.id.right_thumb:
                    CURRENT_BUNDLE = RIGHT_THUMB;
                    setBundle();
                    startScan();
                    break;
                case R.id.left_thumb:
                    CURRENT_BUNDLE = LEFT_THUMB;
                    setBundle();
                    startScan();
                    break;
                case R.id.fab:
                    //verifyFingerprint(rightThumb);
                    saveFlag = true;
                    save();
                    if(saveFlag){
                        if(Operations.isDeviceConnected(getContext()))
                            Operations.postUserData(getContext(),model);
                        else
                            sessionManager.setStringSetValue(SessionManager.KEY_SYNC_IDS,model.IDP_ID);
                        showReliefCard();
                        clearFields();
                    }

                    break;
            }
        }
    };

    private void displayDatePicker() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        DatePickerDialog datePicker = new DatePickerDialog(getContext(),
                datePickerListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        datePicker.setCancelable(false);
        datePicker.setTitle("Select the date");
        datePicker.show();
    }

    private void populatePopUpContents() {
        if(state.getText().toString().equals("Adamawa"))
            popUpContents = getResources().getStringArray(R.array.adamawa_lga);
        else if(state.getText().toString().equals("Borno"))
            popUpContents = getResources().getStringArray(R.array.borno_lga);
        else if(state.getText().toString().equals("Yobe"))
            popUpContents = getResources().getStringArray(R.array.yobe_lga);
    }


    public PopupWindow popupDropDown() {

        // initialize a pop up window type
        PopupWindow popupWindow = new PopupWindow(getContext());

        // the drop down list is a list view
        ListView listView = new ListView(getContext());

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

    }

    private void setCurrentTextView(String text) {
        currentTextView.setText(text);
    }


    private ArrayAdapter<String> dropDownListAdapter(String list[]) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list) {

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

    private void setBundle() {
        bundle = new Bundle();
        bundle.putString("finger", CURRENT_BUNDLE);
    }

    private void startScan() {
        Intent fingerprintIntent = new Intent(getContext(), FingerprintCaptureActivity.class);
        fingerprintIntent.putExtras(bundle);
        startActivityForResult(fingerprintIntent, FINGERPRINT_CAPTURE_REQUEST);
    }






    private void save()  {
        checkRequiredFields();
       // verifyFingerprints();
        if(saveFlag) {
            Log.i("enrollment","all fields provided");
        try {
            prepareEnrollmentModel();
            prepareHistory();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
            Log.i("enrollment","saving to db");
        saveToDb();
       }
    }


    private void showReliefCard() {
        IDPDetailsActivity.model = model;
        startActivity(new Intent(getContext(),IDPDetailsActivity.class));
        getActivity().finish();
    }

    private void checkRequiredFields() {
        if(first_name.getText().toString().trim().length() == 0) {
            first_name.setError("Firstname is required.");
            saveFlag = false;
        }
        if(last_name.getText().toString().trim().length() == 0) {
            last_name.setError("Lastname is required.");
            saveFlag = false;
        }
        if(rb_date_of_birth.isChecked()) {
            if(dateOfBirth.getText().toString().trim().length() == 0) {
                dateOfBirth.setError("Date of birth is required.");
                saveFlag = false;
            }
        }
        if(rb_estimated_age.isChecked()) {
            dateOfBirth.setError(null);
            if(estimatedAge.equals(getString(R.string.drop_down_default_value))) {
                Toast.makeText(getContext(),"Estimated age is required",Toast.LENGTH_SHORT).show();
                saveFlag = false;
            }
        }
        if(gender.equals(getString(R.string.drop_down_default_value))) {
            Toast.makeText(getContext(),"Gender is required",Toast.LENGTH_SHORT).show();
            saveFlag = false;
        }
        if(maritalStatus.equals(getString(R.string.drop_down_default_value))) {
            Toast.makeText(getContext(),"Marital status is required",Toast.LENGTH_SHORT).show();
            saveFlag = false;
        }
        if(state.equals(getString(R.string.drop_down_default_value))) {
            Toast.makeText(getContext(),"State is required",Toast.LENGTH_SHORT).show();
            saveFlag = false;
        }
        if(localGovernment.equals(getString(R.string.drop_down_default_value))) {
            Toast.makeText(getContext(),"Local government is required",Toast.LENGTH_SHORT).show();
            saveFlag = false;
        }
        if(profileBitmap == null) {
            Toast.makeText(getContext(),"Profile picture not taken", Toast.LENGTH_SHORT).show();
            saveFlag = false;
        }
        if((rightThumb == null)  || (leftThumb == null)) {
            Toast.makeText(getContext(),"Atleast a fingerprint is need.", Toast.LENGTH_SHORT).show();
            saveFlag = false;
        }
    }

    public void clearFields() {
        idpID.setText("");
        first_name.setText("");
        last_name.setText("");
        other_names.setText("");
        dateOfBirth.setText("");
        gender.setText(getString(R.string.drop_down_default_value));
        maritalStatus.setText(getString(R.string.drop_down_default_value));
        state.setText(getString(R.string.drop_down_default_value));
        localGovernment.setText(getString(R.string.drop_down_default_value));
        profilePicture.setImageBitmap(null);
        rightThumbFingerprint.setImageBitmap(null);
        leftThumbFingerprint.setImageBitmap(null);
    }


    private void saveToDb() {
        long id = logger.insertEnrolllment(model);
        if(id > 0){
           id = logger.insertHistory(historyModel);
            if(id > 0) {
                saveFlag = true;
            }else
                saveFlag = false;
        }else
            saveFlag = false;

    }


    private void prepareEnrollmentModel() throws Exception {
        String id;
        if(idpID.getText().toString().trim().length() > 0){
            id= idpID.getText().toString();
        }else{
            String IMEI = Operations.getIMEI(getContext());
            id = "IDP"+IMEI+System.currentTimeMillis();
        }
        generateQRCode(id);
        model = new EnrollmentModel();
        model.IDP_ID = id;
        model.FIRST_NAME = first_name.getText().toString();
        model.LAST_NAME = last_name.getText().toString();
        model.OTHER_NAMES = other_names.getText().toString();
        model.DOB = rb_date_of_birth.isChecked() ?  dateOfBirth.getText().toString():null;
        model.ESTIMATED_AGE = rb_estimated_age.isChecked()?  estimatedAge.getText().toString():null;
        model.GENDER = gender.getText().toString();
        model.MARITAL_STATUS = maritalStatus.getText().toString();
        model.STATE = state.getText().toString();
        model.LOCAL_GOVERNMENT = localGovernment.getText().toString();
        if (profileBitmap !=  null) {
            out = new ByteArrayOutputStream();
            profileBitmap.compress(Bitmap.CompressFormat.PNG,100,out);
            model.PROFILE = out.toByteArray();
        }else{
            model.PROFILE =null;
        }
        if(rightThumb != null) {
            out = new ByteArrayOutputStream();
            rightThumb.compress(Bitmap.CompressFormat.PNG,100,out);
           // byte[] template = SourceAfisUtils.generateTemplate(out.toByteArray(),rightThumb.getWidth(),rightThumb.getHeight());
            model.RIGHT_THUMB = out.toByteArray();
        }else {
            model.RIGHT_THUMB = null;
        }
        if(leftThumb != null) {
            out = new ByteArrayOutputStream();
            leftThumb.compress(Bitmap.CompressFormat.PNG,100,out);
           // byte[] template = SourceAfisUtils.generateTemplate(out.toByteArray(),leftThumb.getWidth(),leftThumb.getHeight());
            model.LEFT_THUMB = out.toByteArray();
        }else {
            model.LEFT_THUMB = null;
        }
       if(qrCodeImage != null) {
           Log.i("qrcode","not null");
           out = new ByteArrayOutputStream();
           qrCodeImage.compress(Bitmap.CompressFormat.PNG,100,out);
           model.BARCODE = out.toByteArray();
       }else {
           model.BARCODE = null;
       }
       model.LOCATION = sessionManager.getStringValue(SessionManager.KEY_LOCATION_NAME)+" "+sessionManager.getStringValue(SessionManager.KEY_COODINATES);
        model.TIME_STAMP = System.currentTimeMillis();

    }

    public void prepareHistory(){
        Log.i("time_stmap",String.valueOf(model.TIME_STAMP));
        Log.i("idpID",String.valueOf(model.IDP_ID));
        historyModel = new  HistoryModel();
        historyModel.IDP_ID = model.IDP_ID;
        historyModel.DATE = model.TIME_STAMP;
        historyModel.EVENT = String.format("Registered at %s.",model.LOCATION);
    }

    public void generateQRCode(String content) {
        try {
            qrCodeWriter = new QRCodeWriter();
            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 512, 512, hintMap);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            qrCodeImage = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    qrCodeImage.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
        }
        catch (WriterException e) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_enrollment, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            String year1 = String.valueOf(selectedYear);
            String month1 = String.valueOf(selectedMonth + 1);
            String day1 = String.valueOf(selectedDay);
            dateOfBirth.setText(day1 + "/" + month1 + "/" + year1);
        }
    };

}
