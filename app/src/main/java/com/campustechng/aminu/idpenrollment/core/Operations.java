package com.campustechng.aminu.idpenrollment.core;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.campustechng.aminu.idpenrollment.R;
import com.campustechng.aminu.idpenrollment.models.EnrollmentModel;
import com.campustechng.aminu.idpenrollment.models.HistoryModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Muhammad Amin on 4/7/2017.
 */

public class Operations {
    public static Bitmap photo = null;
    public static String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static String getDate(long timeStamp) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);
        return String.format("%d/%d/%d", day, month + 1, year);
    }

    public static String generateURL(String host, String endpoint) {
        return String.format("http://%s/%s", host, endpoint);
    }

    public static String convertImageToString(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return imageString;
    }

    public static void postUserData(final Context context, EnrollmentModel enrollmentModel) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://192.168.137.1:3000/enroll"; // Operations.generateURL(context.getString(R.string.host),context.getString(R.string.enroll_endpoint));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", enrollmentModel.IDP_ID);
            jsonObject.put("first_name", enrollmentModel.FIRST_NAME);
            jsonObject.put("last_name", enrollmentModel.LAST_NAME);
            jsonObject.put("other_names", enrollmentModel.OTHER_NAMES);
            jsonObject.put("dob", enrollmentModel.DOB);
            //jsonObject.put("yob", userData.getName());
            jsonObject.put("gender", enrollmentModel.GENDER);
            jsonObject.put("marital_status", enrollmentModel.MARITAL_STATUS);
            jsonObject.put("state", enrollmentModel.STATE);
            jsonObject.put("lga", enrollmentModel.LOCAL_GOVERNMENT);
            jsonObject.put("photo", convertImageToString(BitmapFactory.decodeByteArray(enrollmentModel.PROFILE, 0, enrollmentModel.PROFILE.length)));
            jsonObject.put("finger_1", convertImageToString(BitmapFactory.decodeByteArray(enrollmentModel.RIGHT_THUMB, 0, enrollmentModel.RIGHT_THUMB.length)));
            jsonObject.put("finger_6", convertImageToString(BitmapFactory.decodeByteArray(enrollmentModel.LEFT_THUMB, 0, enrollmentModel.LEFT_THUMB.length)));

        } catch (Exception e) {
            // Log.e("Json object", e.getMessage());
            return;
        }

        //final String requestBody = jsonObject.toString();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("Response:", response.toString());
                            VolleyLog.v("Response:%n %s", response.toString(4));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
       /* StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                           // JSONObject jsonObject1 = new JSONObject(response);
                            Log.i("User details", response);
                        }catch (Exception ex) {

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  Log.e("User details", "error occured.");
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(new String(response.data));
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);*/
        queue.add(req);
    }

    public static Cursor findIDP(Context context, String scanResult) {
        Logger logger = Logger.getInstance(context);
        Cursor result = logger.selectIDP(scanResult);
        return result;
    }

    public static EnrollmentModel generateEnrollmentModelFromCursor(Context context, Cursor result) {
        EnrollmentModel model = new EnrollmentModel();
        model.IDP_ID = result.getString(result.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_IDP_ID));
        model.FIRST_NAME = result.getString(result.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_FIRST_NAME));
        model.LAST_NAME = result.getString(result.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_LAST_NAME));
        model.OTHER_NAMES = result.getString(result.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_OTHER_NAMES));
        model.GENDER = result.getString(result.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_GENDER));
        model.DOB = result.getString(result.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_DOB));
        model.ESTIMATED_AGE = result.getString(result.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_AGE));
        model.MARITAL_STATUS = result.getString(result.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_MARITAL_STATUS));
        model.STATE = result.getString(result.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_STATE));
        model.LOCAL_GOVERNMENT = result.getString(result.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_LOCAL_GOV));
        model.LOCATION = result.getString(result.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_LOCATION));
        model.TIME_STAMP = result.getLong(result.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_TIME_STAMP));
        model.PROFILE = result.getBlob(result.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_PROFILE));
        model.RIGHT_THUMB = result.getBlob(result.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_RIGHT_THUMB));
        model.LEFT_THUMB = result.getBlob(result.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_LEFT_THUMB));
        model.BARCODE = result.getBlob(result.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_BARCODE));
        return model;
    }
    static String res = "";
    public static void verify(final Context context, Bitmap finger, final  VolleyCallback callBack) {
        RequestQueue queue = Volley.newRequestQueue(context);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        finger.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        String URL = "http://192.168.137.1:3000/identify";
        //sending image to server
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                // progressDialog.dismiss();

                //Toast.makeText(context, "Vollery response: " + s, Toast.LENGTH_LONG).show();
                try{
                    /*JSONObject jsonObject = new JSONObject(s);
                    String id = jsonObject.getString("body");
                    Toast.makeText(context, "Vollery response: " + id, Toast.LENGTH_LONG).show();*/
                    callBack.onSuccess(s);


                }catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("volley Error", volleyError.toString());
               // Toast.makeText(context, "Some error occurred -> " + volleyError, Toast.LENGTH_LONG).show();
            }
        }) {
            //adding parameters to send
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("finger", imageString);
                return parameters;
            }
        };
        queue.add(request);
    }

    public static void requestDetails(final Context context, String id, final VolleyCallback callback) throws Exception {
        RequestQueue queue = Volley.newRequestQueue(context);

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(""));
        outputStreamWriter.write("");

        String URL = "http://192.168.137.1:3000/"+id;
        //sending image to server
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,URL, new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
                            Log.v("Response:%n %s", response.toString());
                            //Toast.makeText(context,response.toString(),Toast.LENGTH_SHORT).show();
                            callback.onSuccess(response.toString());
                        } catch (JSONException e) {
                           // Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(context,error.toString(),Toast.LENGTH_SHORT).show();
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        queue.add(req);
    }

    public static void requestImage(Context context, String path, final VolleyCallback callback){
        RequestQueue queue = Volley.newRequestQueue(context);
        String URL = "http://192.168.137.1:3000/"+path;
        ImageRequest ir = new ImageRequest(URL, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                photo = response;
                callback.onSuccess("");
            }
        }, 0, 0, null, null);
        queue.add(ir);
    }

    public static String getIMEI(Context context) {
        TelephonyManager mngr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = mngr.getDeviceId();
        return imei;
    }

    public static HistoryModel prepareHistoryModel(String id, String s) {
        HistoryModel historyModel = new  HistoryModel();
        historyModel.IDP_ID = id;
        historyModel.DATE = System.currentTimeMillis();
        historyModel.EVENT = s;
        return historyModel;
    }

    public static EnrollmentModel convertToEnrollmentModel(Context context, JSONObject jsonObject) {
        EnrollmentModel enrollment = new EnrollmentModel();
        try {
            enrollment.IDP_ID = jsonObject.getString("id");
            enrollment.FIRST_NAME = jsonObject.getString("first_name");
            enrollment.LAST_NAME = jsonObject.getString("last_name");
            enrollment.OTHER_NAMES = jsonObject.getString("other_names");
            enrollment.GENDER = jsonObject.getString("gender");
            enrollment.MARITAL_STATUS = jsonObject.getString("marital_status");
            enrollment.STATE = jsonObject.getString("state");
            enrollment.LOCAL_GOVERNMENT = jsonObject.getString("lga");
            if(jsonObject.optString("dob").trim().length() > 0)
                enrollment.DOB = jsonObject.getString("dob");
            else
                enrollment.DOB = jsonObject.getString("yob");

            if(photo != null){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG,100,out);
                enrollment.PROFILE =  out.toByteArray();
            } else {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                photo = BitmapFactory.decodeResource(context.getResources(), R.drawable.fingerprint);
                photo.compress(Bitmap.CompressFormat.PNG,100,out);
                enrollment.PROFILE =  out.toByteArray();
            }

            //enrollment
        }catch (Exception ex) {

        }
       return enrollment;
    }

    public static byte[][] convertImageTo2D(byte[] image, int width, int height) {
        byte [][] image2d = new byte[width][height];
        int x = 0;
        for(int i=0;i<width;i++)
        {
            for(int j=0;j<height;j++)
            {
                image2d[i][j] = image[x];
                x++;
            }
        }

        return image2d;
    }

    public static interface VolleyCallback{
        void onSuccess(String result);
    }

    /**
     * Function to show settings alert dialog
     */
    public static AlertDialog showSettingsAlert(final Context context, String title, String message, String positiveButton, String negativeButton)
    {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        //Setting Dialog Title
        alertDialog.setTitle(title);

        //Setting Dialog Message
        alertDialog.setMessage(message);


        alertDialog.setPositiveButton(positiveButton, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });


        alertDialog.setNegativeButton(negativeButton, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
               // dialogCancelListener.onCancel();
                dialog.cancel();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
        return  alert;
    }

    public static void requestForPermission(Activity activity, int PERMISSON_CODE) {
        Log.i("Permisson","perm not granted");
        ActivityCompat.requestPermissions(activity, Operations.PERMISSIONS, PERMISSON_CODE);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            //Check for granular permissions in mashmallow and above
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                for (String permission : permissions) {
                    if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        return false;
                    }
                }
            }
        }
        return  true;
    }

    public static int getTime(String value) {
        String values[] = value.split("\\s");
        return Integer.valueOf(values[0]);
    }

    public static boolean isDeviceConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;


        return false;
    }
}
