package com.campustechng.aminu.idpenrollment.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.campustechng.aminu.idpenrollment.models.EnrollmentModel;
import com.campustechng.aminu.idpenrollment.models.HistoryModel;
import com.campustechng.aminu.idpenrollment.models.TemplateModel;
import com.campustechng.aminu.idpenrollment.sourceafis.templates.Template;

/**
 * Created by Muhammad Amin on 4/4/2017.
 */

public class Logger extends SQLiteOpenHelper {

    static Context context;
    private static Logger instance = null;
    SQLiteDatabase db;
    private long lastMessageId = 0;
    private String rawQuery = "Select * from "+ EnrollmentEntry .TABLE_NAME + " c INNER JOIN "
            + EnrollmentEntry.TABLE_NAME + " mc on c._id = mc.contact_id JOIN "
            + EnrollmentEntry.TABLE_NAME + " m on m._id = mc.message_id WHERE m._id = ?";

    public static class EnrollmentEntry implements BaseColumns {
        public static final String TABLE_NAME = "enrollment";
        public static final String COLUMN_NAME_IDP_ID = "idp_id";
        public static final String COLUMN_NAME_FIRST_NAME = "first_name";
        public static final String COLUMN_NAME_OTHER_NAMES = "other_names";
        public static final String COLUMN_NAME_LAST_NAME = "last_name";
        public static final String COLUMN_NAME_DOB = "dob";
        public static final String COLUMN_NAME_AGE = "age";
        public static final String COLUMN_NAME_GENDER = "gender";
        public static final String COLUMN_NAME_MARITAL_STATUS = "marital_status";
        public static final String COLUMN_NAME_STATE = "state";
        public static final String COLUMN_NAME_LOCAL_GOV = "local_government";
        public static final String COLUMN_NAME_PROFILE = "profile";
        public static final String COLUMN_NAME_RIGHT_THUMB = "right_thumb";
        public static final String COLUMN_NAME_RIGHT_INDEX = "right_index";
        public static final String COLUMN_NAME_LEFT_THUMB = "left_thumb";
        public static final String COLUMN_NAME_LEFT_INDEX = "left_index";
        public static final String COLUMN_NAME_BARCODE= "barcode";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_TIME_STAMP = "time_stamp";

        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " +  TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_IDP_ID + " TEXT," +
                        COLUMN_NAME_FIRST_NAME + " TEXT," +
                        COLUMN_NAME_LAST_NAME + " TEXT,"+
                        COLUMN_NAME_OTHER_NAMES + " TEXT,"+
                        COLUMN_NAME_DOB + " TEXT,"+
                        COLUMN_NAME_AGE + " INTEGER,"+
                        COLUMN_NAME_GENDER + " TEXT,"+
                        COLUMN_NAME_MARITAL_STATUS + " TEXT,"+
                        COLUMN_NAME_STATE + " TEXT,"+
                        COLUMN_NAME_LOCAL_GOV + " TEXT,"+
                        COLUMN_NAME_PROFILE + " BLOB,"+
                        COLUMN_NAME_RIGHT_THUMB + " BLOB,"+
                        COLUMN_NAME_RIGHT_INDEX + " BLOB,"+
                        COLUMN_NAME_LEFT_THUMB + " BLOB,"+
                        COLUMN_NAME_LEFT_INDEX + " BLOB,"+
                        COLUMN_NAME_BARCODE + " BLOB,"+
                        COLUMN_NAME_LOCATION + " TEXT,"+
                        COLUMN_NAME_TIME_STAMP + " LONG);";
        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }


    public static class HistoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "history";
        public static final String COLUMN_NAME_IDP_ID = "idp_id";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_EVENT = "event";

        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " +  TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_IDP_ID + " TEXT," +
                        COLUMN_NAME_DATE + " LONG," +
                        COLUMN_NAME_EVENT + " TEXT);";
        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static class TemplateEntry implements BaseColumns {
        public static final String TABLE_NAME = "template";
        public static final String COLUMN_NAME_IDP_ID = "idp_id";
        public static final String COLUMN_RIGHT_THUMB = "finger1";
        public static final String COLUMN_RIGHT_INDEX = "finger2";
        public static final String COLUMN_RIGHT_MIDDLE = "finger3";
        public static final String COLUMN_RIGHT_RING = "finger4";
        public static final String COLUMN_RIGHT_PINKY = "finger5";
        public static final String COLUMN_LEFT_THUMB = "finger6";
        public static final String COLUMN_LEFT_INDEX = "finger7";
        public static final String COLUMN_LEFT_MIDDLE = "finger8";
        public static final String COLUMN_LEFT_RING = "finger9";
        public static final String COLUMN_LEFT_PINKY = "finger10";

        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " +  TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_IDP_ID + " TEXT," +
                        COLUMN_RIGHT_THUMB + " BLOB," +
                        COLUMN_RIGHT_INDEX + " BLOB," +
                        COLUMN_RIGHT_MIDDLE + " BLOB," +
                        COLUMN_RIGHT_RING + " BLOB," +
                        COLUMN_RIGHT_PINKY + " BLOB," +
                        COLUMN_LEFT_THUMB + " BLOB," +
                        COLUMN_LEFT_INDEX + " BLOB," +
                        COLUMN_LEFT_MIDDLE + " BLOB," +
                        COLUMN_LEFT_RING + " BLOB," +
                        COLUMN_LEFT_PINKY + " BLOB);";
        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "app.db"; //context.getString(R.string.db_name);
    public static final int MESSAGE_ENTRY = 1;


    private Logger(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static Logger getInstance(Context context) {
        if(instance == null) {
            instance = new Logger(context);
            return instance;
        }

        return instance;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(EnrollmentEntry.SQL_CREATE_ENTRIES);
        db.execSQL(HistoryEntry.SQL_CREATE_ENTRIES);
        db.execSQL(TemplateEntry.SQL_CREATE_ENTRIES);
        Log.i("Logger:","tables created");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(EnrollmentEntry.SQL_DELETE_ENTRIES);
        db.execSQL(HistoryEntry.SQL_DELETE_ENTRIES);
        db.execSQL(TemplateEntry.SQL_DELETE_ENTRIES);
        onCreate(db);
        Log.i("Logger","Database upgraded");
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    /***************************INSERT*********************************/

    public long insertEnrolllment( EnrollmentModel enrollmentModel) {
        // Gets the data repository in write mode
        db = getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(EnrollmentEntry.COLUMN_NAME_IDP_ID, enrollmentModel.IDP_ID);
        values.put(EnrollmentEntry.COLUMN_NAME_FIRST_NAME, enrollmentModel.FIRST_NAME);
        values.put(EnrollmentEntry.COLUMN_NAME_LAST_NAME, enrollmentModel.LAST_NAME);
        values.put(EnrollmentEntry.COLUMN_NAME_OTHER_NAMES, enrollmentModel.OTHER_NAMES);
        values.put(EnrollmentEntry.COLUMN_NAME_DOB, enrollmentModel.DOB);
        values.put(EnrollmentEntry.COLUMN_NAME_AGE, enrollmentModel.ESTIMATED_AGE);
        values.put(EnrollmentEntry.COLUMN_NAME_GENDER, enrollmentModel.GENDER);
        values.put(EnrollmentEntry.COLUMN_NAME_MARITAL_STATUS, enrollmentModel.MARITAL_STATUS);
        values.put(EnrollmentEntry.COLUMN_NAME_STATE, enrollmentModel.STATE);
        values.put(EnrollmentEntry.COLUMN_NAME_LOCAL_GOV, enrollmentModel.LOCAL_GOVERNMENT);
        values.put(EnrollmentEntry.COLUMN_NAME_PROFILE, enrollmentModel.PROFILE);
        values.put(EnrollmentEntry.COLUMN_NAME_RIGHT_THUMB, enrollmentModel.RIGHT_THUMB);
        values.put(EnrollmentEntry.COLUMN_NAME_RIGHT_INDEX, enrollmentModel.RIGHT_INDEX);
        values.put(EnrollmentEntry.COLUMN_NAME_LEFT_THUMB, enrollmentModel.LEFT_THUMB);
        values.put(EnrollmentEntry.COLUMN_NAME_LEFT_INDEX, enrollmentModel.LEFT_INDEX);
        values.put(EnrollmentEntry.COLUMN_NAME_BARCODE, enrollmentModel.BARCODE);
        values.put(EnrollmentEntry.COLUMN_NAME_LOCATION, enrollmentModel.LOCATION);
        values.put(EnrollmentEntry.COLUMN_NAME_TIME_STAMP, enrollmentModel.TIME_STAMP);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(EnrollmentEntry.TABLE_NAME, null, values);
        Log.i("Logger","Enrollment insert successfull");
        return newRowId;
    }

    public long insertHistory( HistoryModel historyModel) {
        // Gets the data repository in write mode
        db = getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        Log.i("logger",String.valueOf(historyModel.DATE));
        values.put(HistoryEntry.COLUMN_NAME_IDP_ID, historyModel.IDP_ID);
        values.put(HistoryEntry.COLUMN_NAME_DATE, historyModel.DATE);
        values.put(HistoryEntry.COLUMN_NAME_EVENT, historyModel.EVENT);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(HistoryEntry.TABLE_NAME, null, values);
        Log.i("Logger","History insert successfull");
        return newRowId;
    }

    public long insertTemplate(TemplateModel templateModel) {
        // Gets the data repository in write mode
        db = getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(HistoryEntry.COLUMN_NAME_IDP_ID, templateModel.IDP_ID);
        values.put(TemplateEntry.COLUMN_RIGHT_THUMB, templateModel.RIGHT_THUMB);
        values.put(TemplateEntry.COLUMN_RIGHT_INDEX, templateModel.RIGHT_INDEX);
        values.put(TemplateEntry.COLUMN_RIGHT_MIDDLE, templateModel.RIGHT_MIDDLE);
        values.put(TemplateEntry.COLUMN_RIGHT_RING, templateModel.RIGHT_RING);
        values.put(TemplateEntry.COLUMN_RIGHT_PINKY, templateModel.RIGHT_PINKY);
        values.put(TemplateEntry.COLUMN_LEFT_THUMB, templateModel.LEFT_THUMB);
        values.put(TemplateEntry.COLUMN_LEFT_INDEX, templateModel.LEFT_INDEX);
        values.put(TemplateEntry.COLUMN_LEFT_MIDDLE, templateModel.LEFT_MIDDLE);
        values.put(TemplateEntry.COLUMN_LEFT_RING, templateModel.LEFT_RING);
        values.put(TemplateEntry.COLUMN_LEFT_PINKY, templateModel.LEFT_PINKY);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TemplateEntry.TABLE_NAME, null, values);
        Log.i("Logger","Template insert successfull");
        return newRowId;
    }

    /**************************RETRIEVE******************************/
    public Cursor selectAllEnrollments() {
        db = getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                EnrollmentEntry._ID,
                EnrollmentEntry.COLUMN_NAME_IDP_ID,
                EnrollmentEntry.COLUMN_NAME_FIRST_NAME,
                EnrollmentEntry.COLUMN_NAME_LAST_NAME,
                EnrollmentEntry.COLUMN_NAME_OTHER_NAMES,
                EnrollmentEntry.COLUMN_NAME_DOB,
                EnrollmentEntry.COLUMN_NAME_AGE,
                EnrollmentEntry.COLUMN_NAME_GENDER,
                EnrollmentEntry.COLUMN_NAME_MARITAL_STATUS,
                EnrollmentEntry.COLUMN_NAME_STATE,
                EnrollmentEntry.COLUMN_NAME_LOCAL_GOV,
                EnrollmentEntry.COLUMN_NAME_PROFILE,
                EnrollmentEntry.COLUMN_NAME_RIGHT_THUMB,
                EnrollmentEntry.COLUMN_NAME_RIGHT_INDEX,
                EnrollmentEntry.COLUMN_NAME_LEFT_THUMB,
                EnrollmentEntry.COLUMN_NAME_LEFT_INDEX,
                EnrollmentEntry.COLUMN_NAME_BARCODE,
                EnrollmentEntry.COLUMN_NAME_LOCATION,
                EnrollmentEntry.COLUMN_NAME_TIME_STAMP
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =   EnrollmentEntry._ID + " DESC";

        Cursor cursor = db.query(
                EnrollmentEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        return cursor;
    }

    public Cursor selectIDP(String id) {
        SQLiteDatabase db = getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                EnrollmentEntry._ID,
                EnrollmentEntry.COLUMN_NAME_IDP_ID,
                EnrollmentEntry.COLUMN_NAME_FIRST_NAME,
                EnrollmentEntry.COLUMN_NAME_LAST_NAME,
                EnrollmentEntry.COLUMN_NAME_OTHER_NAMES,
                EnrollmentEntry.COLUMN_NAME_DOB,
                EnrollmentEntry.COLUMN_NAME_AGE,
                EnrollmentEntry.COLUMN_NAME_GENDER,
                EnrollmentEntry.COLUMN_NAME_MARITAL_STATUS,
                EnrollmentEntry.COLUMN_NAME_STATE,
                EnrollmentEntry.COLUMN_NAME_LOCAL_GOV,
                EnrollmentEntry.COLUMN_NAME_PROFILE,
                EnrollmentEntry.COLUMN_NAME_RIGHT_THUMB,
                EnrollmentEntry.COLUMN_NAME_RIGHT_INDEX,
                EnrollmentEntry.COLUMN_NAME_LEFT_THUMB,
                EnrollmentEntry.COLUMN_NAME_LEFT_INDEX,
                EnrollmentEntry.COLUMN_NAME_BARCODE,
                EnrollmentEntry.COLUMN_NAME_LOCATION,
                EnrollmentEntry.COLUMN_NAME_TIME_STAMP
        };


        // How you want the results sorted in the resulting Cursor
        String sortOrder =   EnrollmentEntry._ID + " DESC";
        String where = EnrollmentEntry.COLUMN_NAME_IDP_ID + " = ? or "+EnrollmentEntry._ID +" = ?";
        String[] values = {id,id};

        Cursor cursor = db.query(
                EnrollmentEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                where,                                // The columns for the WHERE clause
                values,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        return cursor;

    }

    public Cursor selectIDPHistory(String id) {
        SQLiteDatabase db = getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                HistoryEntry._ID,
                HistoryEntry.COLUMN_NAME_DATE,
                HistoryEntry.COLUMN_NAME_EVENT
        };


        // How you want the results sorted in the resulting Cursor
        String sortOrder =   HistoryEntry._ID + " DESC";
        String where = HistoryEntry.COLUMN_NAME_IDP_ID + " = ?";
        String[] values = {id};

        Cursor cursor = db.query(
                HistoryEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                where,                                // The columns for the WHERE clause
                values,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        return cursor;
    }

    public Cursor selectAllTemplates() {
        db = getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                TemplateEntry._ID,
                TemplateEntry.COLUMN_RIGHT_THUMB,
                TemplateEntry.COLUMN_RIGHT_INDEX,
                TemplateEntry.COLUMN_RIGHT_MIDDLE,
                TemplateEntry.COLUMN_RIGHT_RING,
                TemplateEntry.COLUMN_RIGHT_PINKY,
                TemplateEntry.COLUMN_LEFT_THUMB,
                TemplateEntry.COLUMN_LEFT_INDEX,
                TemplateEntry.COLUMN_LEFT_MIDDLE,
                TemplateEntry.COLUMN_LEFT_RING,
                TemplateEntry.COLUMN_LEFT_PINKY
        };


        // How you want the results sorted in the resulting Cursor
        String sortOrder =   TemplateEntry._ID + " DESC";

        Cursor cursor = db.query(
                TemplateEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        return cursor;
    }


    public Cursor queryMessageContact(String id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(rawQuery, new String[]{id});

        return cursor;
    }


    /**************************UPDATE******************************/

    public int updateMessage() {
        if(lastMessageId == 0)
            return 0; // return if fail safe has reset the last message id
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(EnrollmentEntry.COLUMN_NAME_FIRST_NAME, 1);

        String selection = EnrollmentEntry._ID + " = ?" ;
        String[] selectionArgs = { String.valueOf(lastMessageId) };

        // Insert the new row, returning the primary key value of the new row
        int rowsAffected = db.update(EnrollmentEntry.TABLE_NAME, values, selection, selectionArgs);
        return rowsAffected;
    }

    /**************************DELETE******************************/

    public void delete(String id) {
        SQLiteDatabase db = getWritableDatabase();
        // Define 'where' part of query.
        String selection =  EnrollmentEntry._ID + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { id };
        // Issue SQL statement.
        db.delete( EnrollmentEntry .TABLE_NAME, selection, selectionArgs);
    }

}
