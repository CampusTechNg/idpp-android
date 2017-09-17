package com.campustechng.aminu.idpenrollment.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.campustechng.aminu.idpenrollment.core.SourceAfisUtils;
import com.campustechng.aminu.idpenrollment.models.EnrollmentModel;
import com.campustechng.aminu.idpenrollment.core.FingerPrintMatcher;
import com.campustechng.aminu.idpenrollment.core.Logger;
import com.campustechng.aminu.idpenrollment.core.Operations;
import com.campustechng.aminu.idpenrollment.R;
import com.campustechng.aminu.idpenrollment.activity.FingerprintCaptureActivity;
import com.campustechng.aminu.idpenrollment.activity.IDPDetailsActivity;
import com.campustechng.aminu.idpenrollment.activity.MainActivity;
import com.campustechng.aminu.idpenrollment.activity.QRCodeScannerActivity;
import com.campustechng.aminu.idpenrollment.sourceafis.simple.AfisEngine;
import com.campustechng.aminu.idpenrollment.sourceafis.simple.Finger;
import com.campustechng.aminu.idpenrollment.sourceafis.simple.Fingerprint;
import com.campustechng.aminu.idpenrollment.sourceafis.simple.Person;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import asia.kanopi.fingerscan.Status;

import static android.app.Activity.RESULT_OK;


public class VerificationFragment extends Fragment {

    private static Cursor result;
    private Logger logger;
    private static EnrollmentModel model;
    private FingerPrintMatcher fingerPrintMatcher;
    private static ImageView fingerPrintView, qrcodeView;
    private static TextView textview_fingerprint_result, textview_qrcode_result;
    static Bitmap fingerPrintImage;
    private static final int FINGER_PRINT_REQUEST = 1;
    private static final int QR_CODE_REQUEST = 2;
    private static boolean called_from_relief = false;
    private OnFragmentInteractionListener mListener;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private List<Fragment> sectionViews = new ArrayList<>();
    private final int PAGES = 2;
    private final String PAGE_1_TITLE = "Fingerprint";
    private final String PAGE_2_TITLE = "QR/Bar Code";
    private ViewPager mViewPager;

    public VerificationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verification, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        sectionViews.addAll(Arrays.asList( FingerPrintFragment.newInstance(),  QRCodeFragment.newInstance()));
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        mViewPager = (ViewPager) view.findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        if(getArguments() != null){
            String stack = getArguments().getString("stack");
            if(stack.equals("relief"))
                called_from_relief = true;
        }
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    //@Override
   /* public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(getContext(),"Activity result received.",Toast.LENGTH_SHORT).show();
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case FINGER_PRINT_REQUEST:
                    int status = data.getIntExtra("status", Status.ERROR);
                    if (status == Status.SUCCESS) {
                        byte[] img = data.getByteArrayExtra("img");
                        fingerPrintImage = BitmapFactory.decodeByteArray(img, 0, img.length);
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        fingerPrintImage.compress(Bitmap.CompressFormat.PNG, 100, out);
                        fingerPrintImage = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                        fingerPrintView.setImageBitmap(fingerPrintImage);
                        Operations.verify(getContext(), fingerPrintImage, new Operations.VolleyCallback() {
                            @Override
                            public void onSuccess(String responseResult) {
                                result = Operations.findIDP(getContext(),responseResult);
                                if(result != null && result.moveToNext()) {
                                   // Toast.makeText(getContext(),"details found",Toast.LENGTH_SHORT).show();
                                    model = Operations.generateEnrollmentModelFromCursor(getContext(),result);
                                    IDPDetailsActivity.model = model;
                                    startActivity(new Intent(getContext(),IDPDetailsActivity.class));
                                }else {

                                   // textview_fingerprint_result.setTextColor(Color.);
                                    textview_fingerprint_result.setText("Verified but record doesn't exit on device.");
                                }
                            }
                        });

                    } else {
                        String errorMesssage = data.getStringExtra("errorMessage");
                       // Toast.makeText(getContext(), "-- Error: " + errorMesssage + " --", Toast.LENGTH_LONG).show();
                    }
                    break;
                case QR_CODE_REQUEST:
                    String scanResult = data.getStringExtra("QR_CODE_RESULT");
                    if(scanResult.trim().length() > 0) {
                      //  Toast.makeText(getContext(),scanResult,Toast.LENGTH_LONG).show();
                        result = Operations.findIDP(getContext(),scanResult);
                        if(result != null && result.moveToNext()) {
                            model = Operations.generateEnrollmentModelFromCursor(getContext(),result);
                            if(called_from_relief){
                                ReliefItemsFragment.IDP_ID = model.IDP_ID;
                                ReliefItemsFragment.IDP_Name = String.format("%s %s %s",model.FIRST_NAME,model.LAST_NAME,model.OTHER_NAMES);
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                intent.putExtra("CURRENT_TAG","relief");
                                intent.putExtra("navIndex",3);
                                startActivity(intent);
                                getActivity().finish();
                            }else{
                                IDPDetailsActivity.model = model;
                                startActivity(new Intent(getContext(),IDPDetailsActivity.class));
                            }

                        }else {
                            textview_fingerprint_result.setTextColor(Color.RED);
                            textview_fingerprint_result.setText("No record found.");
                        }
                    } else {
                        textview_fingerprint_result.setTextColor(Color.RED);
                        textview_fingerprint_result.setText("Scan error.");
                    }

                    break;
            }

        }
    }*/

    private double[] getFingerPrintTemplate(Bitmap fingerPrint) {
        fingerPrintMatcher = new FingerPrintMatcher(fingerPrint.getWidth(),fingerPrint.getHeight());
        fingerPrintMatcher.setFingerPrintImage(fingerPrint);
        return  fingerPrintMatcher.getFingerPrintTemplate();
    }



    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    public static class FingerPrintFragment extends Fragment {

        VerifyAsync verifyAsync;
        public FingerPrintFragment() {

        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            textview_fingerprint_result.setText(null);
            if(resultCode == RESULT_OK) {
                    int status = data.getIntExtra("status", Status.ERROR);
                    if (status == Status.SUCCESS) {
                        byte[] img = data.getByteArrayExtra("img");
                        fingerPrintImage = BitmapFactory.decodeByteArray(img, 0, img.length);
                        ByteArrayOutputStream out = convertBitmapToByteArray(fingerPrintImage);
                        fingerPrintImage = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                        fingerPrintView.setImageBitmap(fingerPrintImage);

                                   // verify();


                        Operations.verify(getContext(), fingerPrintImage, new Operations.VolleyCallback() {
                            @Override
                            public void onSuccess(String responseResult) {
                                String id = "";
                                try {
                                    JSONObject jsonObject = new JSONObject(responseResult);
                                    int code = Integer.valueOf(jsonObject.getString("code"));
                                    if(code == 1) {
                                        textview_fingerprint_result.setTextColor(Color.RED);
                                        textview_fingerprint_result.setText(jsonObject.getString("error"));
                                    }
                                     else {
                                        id = jsonObject.getString("body");
                                        result = Operations.findIDP(getContext(), id);
                                        if (result != null && result.moveToNext()) {
                                            // Toast.makeText(getContext(),"details found",Toast.LENGTH_SHORT).show();
                                            model = Operations.generateEnrollmentModelFromCursor(getContext(), result);
                                            IDPDetailsActivity.model = model;
                                            startActivity(new Intent(getContext(), IDPDetailsActivity.class));
                                        }
                                        else {
                                            textview_fingerprint_result.setTextColor(Color.BLACK);
                                            textview_fingerprint_result.setText(id+" is verified but record doesn't exit on device.");
                                        }
                                    }
                                } catch (Exception ex) {

                                }
                            }
                        });

                    } else {
                        String errorMesssage = data.getStringExtra("errorMessage");
                        textview_fingerprint_result.setText(errorMesssage);
                    }
            }
        }

        private void verify(){
            verifyAsync.execute();
        }

        ArrayList<Fingerprint> getFingerprints(byte[]... images) {
            ArrayList<Fingerprint> fingerprints = new ArrayList<>();
            for (byte[] b : images){
                Fingerprint fingerprint = new Fingerprint();
                fingerprint.setIsoTemplate(b);
                fingerprints.add(fingerprint);
            }

            return fingerprints;
        }


        private void startScan() {
            Intent fingerprintIntent = new Intent(getContext(), FingerprintCaptureActivity.class);
            startActivityForResult(fingerprintIntent, FINGER_PRINT_REQUEST);
        }

        public static FingerPrintFragment newInstance() {
            FingerPrintFragment fragment = new FingerPrintFragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            verifyAsync = new VerifyAsync();
            View rootView = inflater.inflate(R.layout.fragment_fingerprint, container, false);
            textview_fingerprint_result = (TextView) rootView.findViewById(R.id.fingerprint_result);
            fingerPrintView = (ImageView) rootView.findViewById(R.id.fingerprint_verify);
            fingerPrintView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startScan();
                }
            });
            return rootView;
        }

        class VerifyAsync extends AsyncTask<Void, Void, Person>{
            long startTimestamp;
            @Override
            protected void onPreExecute() {
                startTimestamp = System.currentTimeMillis()/1000;
            }

            @Override
            protected void onPostExecute(Person s) {
                long currentTimestamp = (System.currentTimeMillis()/1000) - startTimestamp;
                Toast.makeText(getContext(),"finished in "+(currentTimestamp/1000),Toast.LENGTH_SHORT).show();
                if(s != null){
                    Toast.makeText(getContext(),"Verified: "+s.getId(),Toast.LENGTH_SHORT).show();
                    result = Operations.findIDP(getContext(), String.valueOf(s.getId()));
                    if (result != null && result.moveToNext()) {
                        // Toast.makeText(getContext(),"details found",Toast.LENGTH_SHORT).show();
                        model = Operations.generateEnrollmentModelFromCursor(getContext(), result);
                        IDPDetailsActivity.model = model;
                        startActivity(new Intent(getContext(), IDPDetailsActivity.class));
                    }


                }else {
                    Toast.makeText(getContext(),"Not Verified:",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected Person doInBackground(Void... params) {
                Logger logger = Logger.getInstance(getContext());
                Cursor cursor = logger.selectAllEnrollments();
                ArrayList<Person> persons = new ArrayList<>();
                while (cursor.moveToNext()){
                    EnrollmentModel model =  Operations.generateEnrollmentModelFromCursor(getContext(),cursor);
                    Person person = new Person();
                    person.setId(cursor.getInt(cursor.getColumnIndex(Logger.EnrollmentEntry._ID)));
                    person.setFingerprints(getFingerprints(model.RIGHT_THUMB,model.LEFT_THUMB));
                    persons.add(person);
                }

                Log.i(getClass().getSimpleName(),"Person array size: "+persons.size());
                ByteArrayOutputStream out = convertBitmapToByteArray(fingerPrintImage);
                Fingerprint finger = new Fingerprint();
                try {
                    finger.setImage(Operations.convertImageTo2D(out.toByteArray(), fingerPrintImage.getWidth(), fingerPrintImage.getHeight()));
                }catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
                Person p = new Person(finger);
                new AfisEngine().extract(p);
                Person probed =  SourceAfisUtils.identify(persons,p);

                return probed;
            }
        }
    }

    @NonNull
    private static ByteArrayOutputStream convertBitmapToByteArray(Bitmap fingerPrintImage) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        fingerPrintImage.compress(Bitmap.CompressFormat.PNG, 100, out);
        return out;
    }


    public static class QRCodeFragment extends Fragment {

        public QRCodeFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static QRCodeFragment newInstance() {
            QRCodeFragment fragment = new QRCodeFragment();
            return fragment;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            textview_qrcode_result.setText(null);
            if(resultCode == RESULT_OK) {
                String scanResult = data.getStringExtra("QR_CODE_RESULT");
                if(scanResult.trim().length() > 0) {
                    //  Toast.makeText(getContext(),scanResult,Toast.LENGTH_LONG).show();
                    result = Operations.findIDP(getContext(),scanResult);
                    if(result != null && result.moveToNext()) {
                        model = Operations.generateEnrollmentModelFromCursor(getContext(),result);
                        if(called_from_relief){
                            ReliefItemsFragment.IDP_ID = model.IDP_ID;
                            ReliefItemsFragment.IDP_Name = String.format("%s %s %s",model.FIRST_NAME,model.LAST_NAME,model.OTHER_NAMES);
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            intent.putExtra("CURRENT_TAG","relief");
                            intent.putExtra("navIndex",3);
                            startActivity(intent);
                            getActivity().finish();
                        }else{
                            IDPDetailsActivity.model = model;
                            startActivity(new Intent(getContext(),IDPDetailsActivity.class));
                        }

                    }else {
                        textview_qrcode_result.setTextColor(Color.RED);
                        textview_qrcode_result.setText("No record found.");
                    }
                } else {
                    textview_qrcode_result.setTextColor(Color.RED);
                    textview_qrcode_result.setText("Scan error.");
                }
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_qrcode, container, false);
            qrcodeView = (ImageView) rootView.findViewById(R.id.qrcode_verify);
            textview_qrcode_result = (TextView) rootView.findViewById(R.id.qrcode_result);
            qrcodeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(getContext(),QRCodeScannerActivity.class),QR_CODE_REQUEST);
                }
            });
            return rootView;
        }
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a ContactListFragment (defined as a static inner class below).
            return sectionViews.get(position);

        }

        @Override
        public int getCount() {
            // Show total pages.
            return PAGES;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return PAGE_1_TITLE;
                case 1:
                    return PAGE_2_TITLE;
            }
            return null;
        }
    }

}
