package jp.gr.kmtn.stdroid.util;

import java.util.ArrayList;

import jp.gr.kmtn.stdroid.IGeneral;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.api.translate.Language;

//-----------------------------------------------------
/** 
 * Database Handle Class
 */
//-----------------------------------------------------
public class MyDbAdapter
{

    /** lock */
    private static Object       lock                                             = new Object();

    /** Context*/
    private final Context       myContext;

    /** SQLiteDatabaseHelper*/
    private DatabaseHelper      myDbHelper;

    /** DB Object*/
    private SQLiteDatabase      myDb;

    /** Data Base Name*/
    private static final String DATABASE_NAME                                    = "crowdroid";

    /** DB Version*/
    private static final int    DATABASE_VERSION                                 = 9;

    //-------------------------------------------------------
    // Table Name
    //-------------------------------------------------------
    private static final String TABLE_SETTING                                    = "setting";

    private static final String TABLE_STATUS                                     = "status";

    private static final String TABLE_TRANSLATION                                = "translation";

    private static final String TABLE_ACCOUNT                                    = "account";

    private static final String TABLE_HASHTAG                                    = "invisible_hash_tag";

    private static final String TABLE_HASHTAG_U                                  = "invisible_hash_tag_u";

    //-------------------------------------------------------
    // FIELD_NAME
    //-------------------------------------------------------	 
    public static final String  FIELD_SETTING_NAME                               = "name";

    public static final String  FIELD_SETTING_VALUE                              = "value";

    public static final String  FIELD_STATUS_NAME                                = "name";

    public static final String  FIELD_STATUS_VALUE                               = "value";

    public static final String  FIELD_TRANSLATION_ID                             = "_id";                          //auto increment

    public static final String  FIELD_TRANSLATION_UID                            = "userid";

    public static final String  FIELD_TRANSLATION_FROM                           = "languagefrom";

    public static final String  FIELD_TRANSLATION_TO                             = "languagaeto";

    public static final String  FIELD_ACCOUNT_ID                                 = "_id";

    public static final String  FIELD_ACCOUNT_UID                                = "userid";

    public static final String  FIELD_ACCOUNT_SERVICE                            = "service";

    public static final String  FIELD_ACCOUNT_NAME                               = "name";

    public static final String  FIELD_ACCOUNT_SCREEN_NAME                        = "screen_name";

    public static final String  FIELD_ACCOUNT_PASSWORD                           = "password";

    public static final String  FIELD_ACCOUNT_ACCESS_TOKEN                       = "access_token";

    public static final String  FIELD_ACCOUNT_TOKEN_SECRET                       = "token_secret";

    public static final String  FIELD_HASHTAG_ID                                 = "_id";

    public static final String  FIELD_HASHTAG_ID_U                               = "_id";

    public static final String  FIELD_HASHTAG_TAG                                = "tag";

    public static final String  FIELD_HASHTAG_TAG_U                              = "tag_u";

    //-------------------------------------------------------
    // PARAMETER_NAME
    //-------------------------------------------------------	
    public static final String  PARAM_SETTING_TWITTER_UPDATE                     = "twitter_update";

    public static final String  PARAM_SETTING_FOLLOW5_UPDATE                     = "follow5_update";

    public static final String  PARAM_SETTING_CROWDROID_BUSINESS_UPDATE          = "crowdroid_business_update";

    public static final String  PARAM_SETTING_TWITTER_UPDATE_UID                 = "twitter_update_uid";

    public static final String  PARAM_SETTING_FOLLOW5_UPDATE_UID                 = "follow5_update_uid";

    public static final String  PARAM_SETTING_CROWDROID_BUSINESS_UPDATE_UID      = "crowdroid_business_update_uid";

    public static final String  PARAM_SETTING_REFRESH_TIME                       = "refresh_time";

    public static final String  PARAM_SETTING_REFRESH_FLAG                       = "refresh_flag";

    public static final String  PARAM_SETTING_AUTOREFRESH_SWITCH_TAB             = "autorefresh_switch_tab";

    public static final String  PARAM_SETTING_NOTIFICATION                       = "notification";

    public static final String  PARAM_SETTING_TWITTER_API_PROXY_SERVER           = "twitter_api_proxy_server";

    public static final String  PARAM_SETTING_TWITTER_API_PROXY                  = "twitter_api_proxy";

    public static final String  PARAM_SETTING_NOTIFICATION_AT_MESSAGE            = "notification_at_message";

    public static final String  PARAM_SETTING_NOTIFICATION_DIRECT_MESSAGE        = "notification_direct_message";

    public static final String  PARAM_SETTING_NOTIFICATION_GENERAL_MESSAGE       = "notification_general_message";

    public static final String  PARAM_SETTING_IMAGE_PATH                         = "image_path";

    public static final String  PARAM_SETTING_FONT_COLOR                         = "font_color";

    public static final String  PARAM_SETTING_AUTO_TRANSLATION                   = "auto_translation";

    //crowdroid_business_server
    public static final String  PARAM_SETTING_CROWDROID_BUSINESS_SERVER          = "crowdroid_business_server";

    //crowdroid translate engine
    public static final String  PARAM_SETTING_CROWDROID_TRANSLATE_ENGINE         = "crowdroid_translate_engine";

    public static final String  PARAM_SETTING_CROWDROID_TRANSLATE_GOOGLE         = "google";

    public static final String  PARAM_SETTING_CROWDROID_TRANSLATE_MICROSOFT_BING = "microsoft_bing";

    public static final String  PARAM_STATUS_CURRENT_UID                         = "current_uid";

    public static final String  PARAM_STATUS_CURRENT_SERVICE                     = "current_service";

    public static final String  PARAM_STATUS_CURRENT_STATUS                      = "current_status";

    public static final String  PARAM_STATUS_NEWEST_AT_MESSAGE_ID                = "newest_at_message_id";

    public static final String  PARAM_STATUS_NEWEST_DIRECT_MESSAGE_ID            = "newest_direct_message_id";

    public static final String  PARAM_STATUS_NEWEST_GENERAL_MESSAGE_ID           = "newest_general_message_id";

    public static final String  PARAM_STATUS_CURRENT_PAGE                        = "current_page";

    public static final String  PARAM_STATUS_LAST_TRANSLATION_FROM               = "last_translation_from";

    public static final String  PARAM_STATUS_LAST_TRANSLATION_TO                 = "last_translation_to";

    public static final String  PARAM_STATUS_UTC_OFFSET                          = "utc_offset";

    //-------------------------------------------------------
    // PARAMETER_VALUE
    //-------------------------------------------------------	
    public static final String  PARAM_VALUE_ON                                   = "on";

    public static final String  PARAM_VALUE_OFF                                  = "off";

    //-------------------------------------------------------
    // SQL
    //-------------------------------------------------------
    /** Create Setting Table*/
    private static final String DATABASE_CREATE_SETTING                          = "create table "
                                                                                         + TABLE_SETTING
                                                                                         + " ("
                                                                                         + FIELD_SETTING_NAME
                                                                                         + " text not null, "
                                                                                         + FIELD_SETTING_VALUE
                                                                                         + " text "
                                                                                         + ");";

    /** Create Status Table*/
    private static final String DATABASE_CREATE_STATUS                           = "create table "
                                                                                         + TABLE_STATUS
                                                                                         + " ("
                                                                                         + FIELD_STATUS_NAME
                                                                                         + " text not null, "
                                                                                         + FIELD_STATUS_VALUE
                                                                                         + " text "
                                                                                         + ");";

    /** Create Translation Table*/
    private static final String DATABASE_CREATE_TRANSLATION                      = "create table "
                                                                                         + TABLE_TRANSLATION
                                                                                         + " ("
                                                                                         + FIELD_TRANSLATION_ID
                                                                                         + " integer primary key autoincrement, "
                                                                                         + FIELD_TRANSLATION_UID
                                                                                         + " text not null, "
                                                                                         + FIELD_TRANSLATION_FROM
                                                                                         + " text not null, "
                                                                                         + FIELD_TRANSLATION_TO
                                                                                         + " text not null "
                                                                                         + ");";

    /** Create Account Table*/
    private static final String DATABASE_CREATE_ACCOUNT                          = "create table "
                                                                                         + TABLE_ACCOUNT
                                                                                         + " ("
                                                                                         + FIELD_ACCOUNT_ID
                                                                                         + " integer primary key autoincrement, "
                                                                                         + FIELD_ACCOUNT_UID
                                                                                         + " varchar(32), "
                                                                                         + FIELD_ACCOUNT_SERVICE
                                                                                         + " varchar(32), "
                                                                                         + FIELD_ACCOUNT_NAME
                                                                                         + " text, "
                                                                                         + FIELD_ACCOUNT_SCREEN_NAME
                                                                                         + " text, "
                                                                                         + FIELD_ACCOUNT_PASSWORD
                                                                                         + " text, "
                                                                                         + FIELD_ACCOUNT_ACCESS_TOKEN
                                                                                         + " text, "
                                                                                         + FIELD_ACCOUNT_TOKEN_SECRET
                                                                                         + " text "
                                                                                         + ");";

    /** Create HashTag table*/
    private static final String DATABASE_CREATE_HASHTAG                          = "create table "
                                                                                         + TABLE_HASHTAG
                                                                                         + " ("
                                                                                         + FIELD_HASHTAG_ID
                                                                                         + " integer primary key autoincrement, "
                                                                                         + FIELD_HASHTAG_TAG
                                                                                         + " text "
                                                                                         + ");";

    /** Create HashTag table*/
    private static final String DATABASE_CREATE_HASHTAG_U                        = "create table "
                                                                                         + TABLE_HASHTAG_U
                                                                                         + " ("
                                                                                         + FIELD_HASHTAG_ID_U
                                                                                         + " integer primary key autoincrement, "
                                                                                         + FIELD_HASHTAG_TAG_U
                                                                                         + " text "
                                                                                         + ");";

    //-----------------------------------------------------
    /** 
     * Database Handle Class
     */
    //-----------------------------------------------------
    public class DatabaseHelper extends SQLiteOpenHelper
    {

        public DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        //-----------------------------------------------------
        /** 
         * Called only when DB is first Created
         * 
         * you can check the DB file at /data/data/[package-name]/databases
         */
        //-----------------------------------------------------
        public void onCreate(SQLiteDatabase db)
        {

            db.execSQL(DATABASE_CREATE_SETTING);
            db.execSQL(DATABASE_CREATE_STATUS);
            db.execSQL(DATABASE_CREATE_TRANSLATION);
            db.execSQL(DATABASE_CREATE_ACCOUNT);
            db.execSQL(DATABASE_CREATE_HASHTAG);
            db.execSQL(DATABASE_CREATE_HASHTAG_U);
            setDefaultValue(db);
        }

        //-----------------------------------------------------
        /**
         *prepare record for the db setting and status 
         *you can check the data in the database
         */
        //------------------------------------------------------
        private void setDefaultValue(SQLiteDatabase db)
        {

            /**these are for table setting*/
            ContentValues values = new ContentValues();

            values.put(FIELD_SETTING_NAME, PARAM_SETTING_TWITTER_UPDATE);
            values.put(FIELD_SETTING_VALUE, PARAM_VALUE_OFF);
            db.insert(TABLE_SETTING, null, values);

            values.put(FIELD_SETTING_NAME, PARAM_SETTING_TWITTER_UPDATE_UID);
            values.put(FIELD_SETTING_VALUE, "");
            db.insert(TABLE_SETTING, null, values);

            values.put(FIELD_SETTING_NAME, PARAM_SETTING_FOLLOW5_UPDATE);
            values.put(FIELD_SETTING_VALUE, PARAM_VALUE_OFF);
            db.insert(TABLE_SETTING, null, values);
            //         714
            values.put(FIELD_SETTING_NAME,
                    PARAM_SETTING_TWITTER_API_PROXY_SERVER);
            values.put(FIELD_SETTING_VALUE, "");
            db.insert(TABLE_SETTING, null, values);

            values.put(FIELD_SETTING_NAME, PARAM_SETTING_TWITTER_API_PROXY);
            values.put(FIELD_SETTING_VALUE, PARAM_VALUE_OFF);
            db.insert(TABLE_SETTING, null, values);

            values.put(FIELD_SETTING_NAME, PARAM_SETTING_FOLLOW5_UPDATE_UID);
            values.put(FIELD_SETTING_VALUE, "");
            db.insert(TABLE_SETTING, null, values);

            values.put(FIELD_SETTING_NAME,
                    PARAM_SETTING_CROWDROID_BUSINESS_UPDATE);
            values.put(FIELD_SETTING_VALUE, PARAM_VALUE_OFF);
            db.insert(TABLE_SETTING, null, values);

            values.put(FIELD_SETTING_NAME,
                    PARAM_SETTING_CROWDROID_BUSINESS_UPDATE_UID);
            values.put(FIELD_SETTING_VALUE, "");
            db.insert(TABLE_SETTING, null, values);

            values.put(FIELD_SETTING_NAME, PARAM_SETTING_REFRESH_TIME);
            values.put(FIELD_SETTING_VALUE, "10");
            db.insert(TABLE_SETTING, null, values);

            values.put(FIELD_SETTING_NAME, PARAM_SETTING_REFRESH_FLAG);
            values.put(FIELD_SETTING_VALUE, PARAM_VALUE_OFF);
            db.insert(TABLE_SETTING, null, values);

            values.put(FIELD_SETTING_NAME, PARAM_SETTING_AUTOREFRESH_SWITCH_TAB);
            values.put(FIELD_SETTING_VALUE, PARAM_VALUE_ON);
            db.insert(TABLE_SETTING, null, values);

            values.put(FIELD_SETTING_NAME,
                    PARAM_SETTING_NOTIFICATION_AT_MESSAGE);
            values.put(FIELD_SETTING_VALUE, PARAM_VALUE_OFF);
            db.insert(TABLE_SETTING, null, values);

            values.put(FIELD_SETTING_NAME,
                    PARAM_SETTING_NOTIFICATION_DIRECT_MESSAGE);
            values.put(FIELD_SETTING_VALUE, PARAM_VALUE_OFF);
            db.insert(TABLE_SETTING, null, values);

            values.put(FIELD_SETTING_NAME,
                    PARAM_SETTING_NOTIFICATION_GENERAL_MESSAGE);
            values.put(FIELD_SETTING_VALUE, PARAM_VALUE_OFF);
            db.insert(TABLE_SETTING, null, values);

            values.put(FIELD_SETTING_NAME, PARAM_SETTING_NOTIFICATION);
            values.put(FIELD_SETTING_VALUE, PARAM_VALUE_OFF);
            db.insert(TABLE_SETTING, null, values);

            values.put(FIELD_SETTING_NAME, PARAM_SETTING_IMAGE_PATH);
            values.put(FIELD_SETTING_VALUE, "");
            db.insert(TABLE_SETTING, null, values);

            values.put(FIELD_SETTING_NAME, PARAM_SETTING_FONT_COLOR);
            values.put(FIELD_SETTING_VALUE, "-1");//default is white
            db.insert(TABLE_SETTING, null, values);

            values.put(FIELD_SETTING_NAME, PARAM_SETTING_AUTO_TRANSLATION);
            values.put(FIELD_SETTING_VALUE, PARAM_VALUE_OFF);
            db.insert(TABLE_SETTING, null, values);

            values.put(FIELD_SETTING_NAME,
                    PARAM_SETTING_CROWDROID_BUSINESS_SERVER);
            values.put(FIELD_SETTING_VALUE, "");
            db.insert(TABLE_SETTING, null, values);

            values.put(FIELD_SETTING_NAME,
                    PARAM_SETTING_CROWDROID_TRANSLATE_ENGINE);
            //default is google, you can choose microsoft bing
            values.put(FIELD_SETTING_VALUE,
                    PARAM_SETTING_CROWDROID_TRANSLATE_GOOGLE);
            db.insert(TABLE_SETTING, null, values);

            /**these are for table status*/
            values.put(FIELD_STATUS_NAME, PARAM_STATUS_CURRENT_UID);
            values.put(FIELD_STATUS_VALUE, "");
            db.insert(TABLE_STATUS, null, values);

            values.put(FIELD_STATUS_NAME, PARAM_STATUS_CURRENT_SERVICE);
            values.put(FIELD_STATUS_VALUE, IGeneral.SERVICE_NAME_TWITTER);
            db.insert(TABLE_STATUS, null, values);

            values.put(FIELD_STATUS_NAME, PARAM_STATUS_CURRENT_STATUS);
            values.put(FIELD_STATUS_VALUE, "1");
            db.insert(TABLE_STATUS, null, values);

            values.put(FIELD_STATUS_NAME, PARAM_STATUS_NEWEST_AT_MESSAGE_ID);
            values.put(FIELD_STATUS_VALUE, "");
            db.insert(TABLE_STATUS, null, values);

            values.put(FIELD_STATUS_NAME, PARAM_STATUS_NEWEST_DIRECT_MESSAGE_ID);
            values.put(FIELD_STATUS_VALUE, "");
            db.insert(TABLE_STATUS, null, values);

            values.put(FIELD_STATUS_NAME,
                    PARAM_STATUS_NEWEST_GENERAL_MESSAGE_ID);
            values.put(FIELD_STATUS_VALUE, "");
            db.insert(TABLE_STATUS, null, values);

            values.put(FIELD_STATUS_NAME, PARAM_STATUS_CURRENT_PAGE);
            values.put(FIELD_STATUS_VALUE, "1");
            db.insert(TABLE_STATUS, null, values);

            values.put(FIELD_STATUS_NAME, PARAM_STATUS_LAST_TRANSLATION_FROM);
            values.put(FIELD_STATUS_VALUE, Language.ENGLISH);
            db.insert(TABLE_STATUS, null, values);

            values.put(FIELD_STATUS_NAME, PARAM_STATUS_LAST_TRANSLATION_TO);
            values.put(FIELD_STATUS_VALUE, Language.ENGLISH);
            db.insert(TABLE_STATUS, null, values);

            values.put(FIELD_STATUS_NAME, PARAM_STATUS_UTC_OFFSET);
            values.put(FIELD_STATUS_VALUE, "0");
            db.insert(TABLE_STATUS, null, values);

        }

        //-----------------------------------------------------
        /** 
         * Called when DB is Updated
         */
        //-----------------------------------------------------
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            if (oldVersion == 8 && newVersion == 9)
            {

                db.execSQL(DATABASE_CREATE_HASHTAG_U);

                ContentValues values = new ContentValues();
                values.put(FIELD_SETTING_NAME,
                        PARAM_SETTING_AUTOREFRESH_SWITCH_TAB);
                values.put(FIELD_SETTING_VALUE, PARAM_VALUE_ON);
                db.insert(TABLE_SETTING, null, values);

            }
            else
            {
                db.execSQL("DROP TABLE IF EXISTS setting");
                db.execSQL("DROP TABLE IF EXISTS status");
                db.execSQL("DROP TABLE IF EXISTS translation");
                db.execSQL("DROP TABLE IF EXISTS account");
                db.execSQL("DROP TABLE IF EXISTS invisible_hash_tag");
                db.execSQL("DROP TABLE IF EXISTS invisible_hash_tag_u");
                onCreate(db);
            }

        }

    }

    //-----------------------------------------------------
    /** 
    * Constructor for TodoDbAdapter
    */
    //-----------------------------------------------------
    public MyDbAdapter(Context ctx)
    {
        this.myContext = ctx;
    }

    //-----------------------------------------------------
    /** 
    * Open Database and return TodoDbAdapter Instance.
    */
    //-----------------------------------------------------
    public MyDbAdapter open() throws SQLException
    {
        this.myDbHelper = new DatabaseHelper(this.myContext);

        try
        {
            this.myDb = this.myDbHelper.getWritableDatabase();
        }
        catch (SQLException e)
        {
            Log.w("StatusDroid", "Error Occured", e);
        }
        catch (Exception e)
        {
            Log.w("StatusDroid", "Error Occured", e);
        }

        return this;
    }

    //-----------------------------------------------------
    /** 
     * Close Database
     */
    //-----------------------------------------------------
    public void close()
    {
        this.myDbHelper.close();
    }

    //----------------------------
    // Setting
    //----------------------------

    //-------------------------------------------------------
    /**
     * Update the param in Setting Table.
     */
    //--------------------------------------------------------
    public boolean updateSetting(String name, String value)
    {
        synchronized (lock)
        {
            ContentValues values = new ContentValues();
            values.put(FIELD_SETTING_NAME, name);
            values.put(FIELD_SETTING_VALUE, value);
            return this.myDb.update(TABLE_SETTING, values, FIELD_SETTING_NAME
                    + "=" + "'" + name + "'", null) > 0;
        }
    }

    //---------------------------------------------------------------
    /**
     * Get the param value form Setting Table.
     */
    //---------------------------------------------------------------
    public String getSettingValue(String name)
    {
        synchronized (lock)
        {
            String value = null;
            Cursor cursor = this.myDb.query(true, TABLE_SETTING, new String[]
            { FIELD_SETTING_NAME, FIELD_SETTING_VALUE }, FIELD_SETTING_NAME
                    + "=" + "'" + name + "'", null, null, null, null, null);

            //Extract value from Cursor
            if (cursor != null)
            {
                cursor.moveToFirst();
            }
            value = cursor.getString(cursor.getColumnIndex(FIELD_SETTING_VALUE));

            //Close Cursor
            cursor.close();

            return value;
        }
    }

    //----------------------------
    // Status
    //----------------------------

    //-------------------------------------------------------
    /**
     * Update the param in Status Table.
     */
    //--------------------------------------------------------
    public boolean updateStatus(String name, String value)
    {

        synchronized (lock)
        {
            ContentValues values = new ContentValues();
            values.put(FIELD_STATUS_NAME, name);
            values.put(FIELD_STATUS_VALUE, value);
            return this.myDb.update(TABLE_STATUS, values, FIELD_STATUS_NAME
                    + "=" + "'" + name + "'", null) > 0;
        }
    }

    //---------------------------------------------------------------
    /**
     * Get the param value form Status Table.
     */
    //---------------------------------------------------------------
    public String getStatusValue(String name)
    {
        synchronized (lock)
        {
            String value = null;
            Cursor myCursor = this.myDb.query(true, TABLE_STATUS, new String[]
            { FIELD_STATUS_NAME, FIELD_STATUS_VALUE }, FIELD_STATUS_NAME + "="
                    + "'" + name + "'", null, null, null, null, null);

            //Extract value from Cursor
            if (myCursor != null)
            {
                myCursor.moveToFirst();
            }

            value = myCursor.getString(myCursor.getColumnIndex(FIELD_STATUS_VALUE));

            //Close Cursor
            myCursor.close();

            return value;
        }
    }

    //----------------------------
    // Account
    //----------------------------

    //----------------------------------------------------------
    /**
     * update the account info in account table.
     */
    //------------------------------------------------------------
    public boolean updateAccount1(String uid, String service, String name,
            String screen_name, String password, String access_token,
            String token_secret)
    {
        synchronized (lock)
        {
            ContentValues values = new ContentValues();

            values.put(FIELD_ACCOUNT_UID, uid);
            values.put(FIELD_ACCOUNT_SERVICE, service);
            values.put(FIELD_ACCOUNT_NAME, name);
            values.put(FIELD_ACCOUNT_SCREEN_NAME, screen_name);
            values.put(FIELD_ACCOUNT_PASSWORD, password);
            values.put(FIELD_ACCOUNT_ACCESS_TOKEN, access_token);
            values.put(FIELD_ACCOUNT_TOKEN_SECRET, token_secret);

            return this.myDb.update(TABLE_ACCOUNT, values, FIELD_ACCOUNT_UID
                    + "=" + "'" + uid + "'" + " AND " + FIELD_ACCOUNT_SERVICE
                    + "=" + "'" + service + "'", null) > 0;
        }
    }

    //----------------------------------------------------------
    /**
     * update the account info in account table.
     * Only Update (uid/name/screenName)
     */
    //------------------------------------------------------------
    public boolean updateAccount2(String uid, String service, String name,
            String screen_name)
    {

        synchronized (lock)
        {
            ContentValues values = new ContentValues();

            values.put(FIELD_ACCOUNT_UID, uid);
            values.put(FIELD_ACCOUNT_NAME, name);
            values.put(FIELD_ACCOUNT_SCREEN_NAME, screen_name);

            return this.myDb.update(TABLE_ACCOUNT, values, FIELD_ACCOUNT_UID
                    + "=" + "'" + uid + "'" + " AND " + FIELD_ACCOUNT_SERVICE
                    + "=" + "'" + service + "'", null) > 0;
        }
    }

    //------------------------------------------------------------
    /**
     * Insert a record in Account Table.
     */
    //-------------------------------------------------------------
    public boolean insertAccount(String uid, String service, String name,
            String screen_name, String password, String access_token,
            String token_secret)
    {

        synchronized (lock)
        {
            ContentValues values = new ContentValues();

            values.put(FIELD_ACCOUNT_UID, uid);
            values.put(FIELD_ACCOUNT_SERVICE, service);
            values.put(FIELD_ACCOUNT_NAME, name);
            values.put(FIELD_ACCOUNT_SCREEN_NAME, screen_name);
            values.put(FIELD_ACCOUNT_PASSWORD, password);
            values.put(FIELD_ACCOUNT_ACCESS_TOKEN, access_token);
            values.put(FIELD_ACCOUNT_TOKEN_SECRET, token_secret);

            return this.myDb.insert(TABLE_ACCOUNT, null, values) > 0;
        }
    }

    //-------------------------------------------------------------
    /**
     * Insert a hash tag into table hash tag,<br/>
     * if table has setting hash tag, then refresh it.
     */
    //-------------------------------------------------------------
    public void insertKeyword(String tag)
    {
        synchronized (lock)
        {
            ContentValues values = new ContentValues();

            values.put(FIELD_HASHTAG_TAG, tag);

            Cursor mCursor = this.myDb.query(true, TABLE_HASHTAG, new String[]
            { FIELD_HASHTAG_ID, FIELD_HASHTAG_TAG }, FIELD_HASHTAG_TAG + "="
                    + "'" + tag + "'", null, null, null, null, null);

            try
            {
                if (mCursor != null)
                {

                    mCursor.moveToFirst();

                    if (!mCursor.getString(
                            mCursor.getColumnIndex(MyDbAdapter.FIELD_HASHTAG_TAG)).equals(
                            tag))
                    {
                        this.myDb.insert(TABLE_HASHTAG, null, values);
                        return;
                    }
                }
            }
            catch (Exception e)
            {

                this.myDb.insert(TABLE_HASHTAG, null, values);
            }
            return;
        }
    }

    //-------------------------------------------------------------
    /**
     * Insert a hash tag into table hash tag,<br/>
     * if table has setting hash tag, then refresh it.
     */
    //-------------------------------------------------------------
    public void insertKeyword_u(String tag_u)
    {
        synchronized (lock)
        {
            ContentValues values = new ContentValues();

            values.put(FIELD_HASHTAG_TAG_U, tag_u);

            Cursor mCursor = this.myDb.query(true, TABLE_HASHTAG_U,
                    new String[]
                    { FIELD_HASHTAG_ID_U, FIELD_HASHTAG_TAG_U },
                    FIELD_HASHTAG_TAG_U + "=" + "'" + tag_u + "'", null, null,
                    null, null, null);

            try
            {
                if (mCursor != null)
                {

                    mCursor.moveToFirst();

                    if (!mCursor.getString(
                            mCursor.getColumnIndex(MyDbAdapter.FIELD_HASHTAG_TAG_U)).equals(
                            tag_u))
                    {
                        this.myDb.insert(TABLE_HASHTAG_U, null, values);
                        return;
                    }
                }
            }
            catch (Exception e)
            {
                this.myDb.insert(TABLE_HASHTAG_U, null, values);
            }
            return;
        }
    }

    //------------------------------------------------------------
    /**
     * delete a record in Account table.
     */
    //------------------------------------------------------------
    public boolean deleteAccount(long id)
    {
        synchronized (lock)
        {
            if (id == -1)
            {
                return this.myDb.delete(TABLE_ACCOUNT, null, null) > 0;
            }
            else
            {
                return this.myDb.delete(TABLE_ACCOUNT, FIELD_ACCOUNT_ID + " = "
                        + id, null) > 0;
            }
        }
    }

    //------------------------------------------------------------
    /**
     * delete a record in hash tag table.
     */
    //------------------------------------------------------------
    public boolean deleteKeyword(long id)
    {
        synchronized (lock)
        {

            if (id == -1)
            {
                return this.myDb.delete(TABLE_HASHTAG, null, null) > 0;
            }
            else
            {
                return this.myDb.delete(TABLE_HASHTAG, FIELD_HASHTAG_ID + " = "
                        + id, null) > 0;
            }
        }
    }

    //------------------------------------------------------------
    /**
     * delete a record in hash tag table.
     */
    //------------------------------------------------------------
    public boolean deleteKeyword_u(long id)
    {
        synchronized (lock)
        {

            if (id == -1)
            {
                return this.myDb.delete(TABLE_HASHTAG_U, null, null) > 0;
            }
            else
            {
                return this.myDb.delete(TABLE_HASHTAG_U, FIELD_HASHTAG_ID_U
                        + " = " + id, null) > 0;
            }
        }
    }

    //---------------------------------------------------------------------------------
    /** 
     * Set Get hash tag list.
     */
    //---------------------------------------------------------------------------------
    public Cursor getKeywordsCursor()
    {

        synchronized (lock)
        {
            String orderby = FIELD_HASHTAG_ID + ',' + FIELD_HASHTAG_TAG
                    + " desc, " + FIELD_HASHTAG_TAG;

            Cursor mCursor = this.myDb.query(TABLE_HASHTAG, new String[]
            { FIELD_HASHTAG_ID, FIELD_HASHTAG_TAG }, null, null, null, null,
                    orderby);

            return mCursor;
        }
    }

    //---------------------------------------------------------------------------------
    /** 
     * Set Get hash tag list.
     */
    //---------------------------------------------------------------------------------
    public Cursor getKeywordsCursor_u()
    {

        synchronized (lock)
        {
            String orderby = FIELD_HASHTAG_ID_U + ',' + FIELD_HASHTAG_TAG_U
                    + " desc, " + FIELD_HASHTAG_TAG_U;

            Cursor mCursor = this.myDb.query(TABLE_HASHTAG_U, new String[]
            { FIELD_HASHTAG_ID_U, FIELD_HASHTAG_TAG_U }, null, null, null,
                    null, orderby);

            return mCursor;
        }
    }

    //---------------------------------------------------------------------------------
    /** 
     * Set Get hash tag list.
     */
    //---------------------------------------------------------------------------------
    public ArrayList<String> getKeywordsList()
    {
        synchronized (lock)
        {
            ArrayList<String> keywordsList = new ArrayList<String>();
            Cursor cursor = getKeywordsCursor();

            if (cursor != null)
            {
                while (cursor.moveToNext())
                {
                    keywordsList.add(cursor.getString(cursor.getColumnIndex(FIELD_HASHTAG_TAG)));
                }
            }

            cursor.close();

            return keywordsList;
        }
    }

    //---------------------------------------------------------------------------------
    /** 
     * Set Get hash tag list.
     */
    //---------------------------------------------------------------------------------
    public ArrayList<String> getKeywordsList_u()
    {
        synchronized (lock)
        {
            ArrayList<String> keywordsList = new ArrayList<String>();
            Cursor cursor = getKeywordsCursor_u();

            if (cursor != null)
            {
                while (cursor.moveToNext())
                {
                    keywordsList.add(cursor.getString(cursor.getColumnIndex(FIELD_HASHTAG_TAG_U)));
                }
            }

            cursor.close();

            return keywordsList;
        }
    }

    //-----------------------------------------------------
    /** 
     * Get Account List with Cursor.
     */
    //-----------------------------------------------------
    public Cursor getAccountCursor(String service, String uid)
    {

        synchronized (lock)
        {
            //Set Condition
            String condition = null;
            if (uid == null)
            {
                condition = FIELD_ACCOUNT_SERVICE + "=" + "'" + service + "'";
            }
            else
            {
                condition = FIELD_ACCOUNT_SERVICE + "=" + "'" + service + "'"
                        + " AND " + FIELD_ACCOUNT_UID + "=" + "'" + uid + "'";
            }

            //Set Order
            String orderby = FIELD_ACCOUNT_UID + ", " + FIELD_ACCOUNT_SERVICE
                    + ", " + FIELD_ACCOUNT_NAME + ", "
                    + FIELD_ACCOUNT_SCREEN_NAME + ", " + FIELD_ACCOUNT_PASSWORD
                    + ", " + FIELD_ACCOUNT_ACCESS_TOKEN + ", "
                    + FIELD_ACCOUNT_TOKEN_SECRET + " desc, "
                    + FIELD_ACCOUNT_UID;

            //Execute
            Cursor cursor = this.myDb.query(TABLE_ACCOUNT, new String[]
            { FIELD_ACCOUNT_ID, FIELD_ACCOUNT_UID, FIELD_ACCOUNT_SERVICE,
                    FIELD_ACCOUNT_NAME, FIELD_ACCOUNT_SCREEN_NAME,
                    FIELD_ACCOUNT_PASSWORD, FIELD_ACCOUNT_ACCESS_TOKEN,
                    FIELD_ACCOUNT_TOKEN_SECRET }, condition,

            null, null, null, orderby);

            return cursor;
        }
    }

    //---------------------------------------------------------------------------------
    /** 
     * Get Account List with Array List.
     */
    //---------------------------------------------------------------------------------
    public ArrayList<AccountInfo> getAccountList(String service, String uid)
    {
        synchronized (lock)
        {
            ArrayList<AccountInfo> accountList = new ArrayList<AccountInfo>();
            Cursor cursor = getAccountCursor(service, uid);

            //Create Array List
            while (cursor.moveToNext())
            {

                AccountInfo info = new AccountInfo();

                info.setUserId(cursor.getString(cursor.getColumnIndex(FIELD_ACCOUNT_UID)));
                info.setService(cursor.getString(cursor.getColumnIndex(FIELD_ACCOUNT_SERVICE)));
                info.setName(cursor.getString(cursor.getColumnIndex(FIELD_ACCOUNT_NAME)));
                info.setScreenName(cursor.getString(cursor.getColumnIndex(FIELD_ACCOUNT_SCREEN_NAME)));
                info.setPassword(cursor.getString(cursor.getColumnIndex(FIELD_ACCOUNT_PASSWORD)));
                info.setAccessToken(cursor.getString(cursor.getColumnIndex(FIELD_ACCOUNT_ACCESS_TOKEN)));
                info.setTokenSecret(cursor.getString(cursor.getColumnIndex(FIELD_ACCOUNT_TOKEN_SECRET)));

                accountList.add(info);
            }
            cursor.close();

            return accountList;
        }
    }

    //----------------------------
    // Translate
    //----------------------------

    //------------------------------------------------------------
    /**
     * Insert a record in table translation.
     */
    //-------------------------------------------------------------
    public boolean insertTranslation(String uid, String from, String to)
    {
        synchronized (lock)
        {

            ContentValues values = new ContentValues();
            values.put(FIELD_TRANSLATION_UID, uid);
            values.put(FIELD_TRANSLATION_TO, to);
            values.put(FIELD_TRANSLATION_FROM, from);

            return this.myDb.insert(TABLE_TRANSLATION, null, values) > 0;

        }

    }

    //------------------------------------------------------------
    /**
     * delete a record in table Translation table.
     */
    //------------------------------------------------------------
    public boolean deleteTranslation(String id)
    {

        synchronized (lock)
        {
            if (id.equals("-1"))
            {
                return this.myDb.delete(TABLE_TRANSLATION, null, null) > 0;
            }
            else
            {
                return this.myDb.delete(TABLE_TRANSLATION, FIELD_TRANSLATION_ID
                        + "=" + "'" + id + "'", null) > 0;
            }
        }
    }

    //-----------------------------------------------------
    /** 
     * Get Translation Setting List with Cursor.
     */
    //-----------------------------------------------------
    public Cursor getTranslationCursor()
    {
        synchronized (lock)
        {
            String orderby = FIELD_TRANSLATION_ID + ", "
                    + FIELD_TRANSLATION_FROM + " desc, " + FIELD_TRANSLATION_TO;

            Cursor cursor = this.myDb.query(TABLE_TRANSLATION, new String[]
            { FIELD_TRANSLATION_ID, FIELD_TRANSLATION_UID,
                    FIELD_TRANSLATION_FROM, FIELD_TRANSLATION_TO }, null, null,
                    null, null, orderby);

            return cursor;
        }
    }

    //-----------------------------------------------------
    /** 
     * Get Translation Setting List with ArrayList.
     */
    //-----------------------------------------------------
    public ArrayList<String[]> getTranslationList()
    {

        synchronized (lock)
        {
            ArrayList<String[]> translationList = new ArrayList<String[]>();

            Cursor cursor = getTranslationCursor();

            while (cursor.moveToNext())
            {
                String[] value = new String[3];

                value[0] = cursor.getString(cursor.getColumnIndex(FIELD_TRANSLATION_UID));
                value[1] = cursor.getString(cursor.getColumnIndex(FIELD_TRANSLATION_FROM));
                value[2] = cursor.getString(cursor.getColumnIndex(FIELD_TRANSLATION_TO));

                translationList.add(value);
            }
            cursor.close();

            return translationList;

        }
    }

    //---------------------------------
    // Others
    //---------------------------------

    //------------------------------------synchronized---------------------------------------------
    /** 
     * Get Verification Info of current login user.<br />
     @return AccountInfo
     */
    //---------------------------------------------------------------------------------
    public AccountInfo getCurrentLoginAccountInfo()
    {

        synchronized (lock)
        {
            String service = getStatusValue(PARAM_STATUS_CURRENT_SERVICE);
            String uid = getStatusValue(PARAM_STATUS_CURRENT_UID);

            ArrayList<AccountInfo> list = getAccountList(service, uid);
            AccountInfo accountInfo = null;
            if (list != null && list.size() > 0)
            {
                accountInfo = list.get(0);
            }

            return accountInfo;
        }
    }

    //---------------------------------------------------------------------------------
    /** 
     * Set Get Verification Info of current login user.
     */
    //---------------------------------------------------------------------------------
    public boolean updateLoginStatus(String service, String uid)
    {

        synchronized (lock)
        {
            if (updateStatus(PARAM_STATUS_CURRENT_SERVICE, service)
                    && updateStatus(PARAM_STATUS_CURRENT_UID, uid))
            {
                return true;
            }
            else
            {
                return false;
            }

        }
    }
}
