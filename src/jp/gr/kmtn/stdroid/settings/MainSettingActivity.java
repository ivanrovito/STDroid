package jp.gr.kmtn.stdroid.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.gr.kmtn.stdroid.R;
import jp.gr.kmtn.stdroid.dialog.SearchServiceDialog;
import jp.gr.kmtn.stdroid.notification.NotificationCheckService;
import jp.gr.kmtn.stdroid.util.MyDbAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class MainSettingActivity extends Activity implements
        OnItemClickListener
{

    // ---------------------------------------
    // Setting Menu
    // ---------------------------------------
    public static String       SETTING_MENU_AUTO_REFRESH    = null;

    public static String       SETTING_MENU_MULTI_TWEET     = null;

    public static String       SETTING_MENU_ACCOUNT_MANAGE  = null;

    public static String       SETTING_MENU_TRANSLATION     = null;

    public static String       SETTING_MENU_SET_WALL_PAPER  = null;

    public static String       SETTING_MENU_NOTIFICATION    = null;

    public static String       SETTING_MENU_FONT_COLOR      = null;

    public static String       SETTING_MENU_DEFAULT_SETTING = null;

    public static String       SETTING_MENU_HASHTAG_SETTING = null;

    /** List of Setting Menu Icon */
    private int[]              settingMenuImages            =
                                                            {
            R.drawable.mainsetting_ic_menu_refresh,
            android.R.drawable.ic_menu_share, android.R.drawable.ic_menu_send,
            android.R.drawable.ic_menu_edit,
            android.R.drawable.ic_menu_gallery,
            android.R.drawable.ic_menu_info_details,
            android.R.drawable.ic_menu_mapmode,
            android.R.drawable.ic_menu_agenda,
            android.R.drawable.ic_menu_manage              };

    /** Mode (all menu available)*/
    public static final int    MODE_ALL_MENU_AVAILABLE      = 0;

    /** Mode (account manage disabled)*/
    public static final int    MODE_ACCOUNT_MANAGE_DISABLED = 1;

    /** Mode*/
    public static int          mode                         = MODE_ALL_MENU_AVAILABLE;

    /** Intent Key (MODE)*/
    public static final String KEY_SETTING_MODE             = "mode";

    private MyDbAdapter        myDb;

    private static final int   REFRESH                      = 1;

    private static final int   COLORSELECT_DIGLOG           = 3;

    private static final int   NOTIFICATION_DIALOG          = 2;

    /** Font Color*/
    private int                selectedFontColor;                                      ;

    //-----------------------------------------------------------------------------
    /**
     *  Called when Activity is Created.
     */
    //-----------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_main);

        //----------------------------------
        // Set Mode
        //----------------------------------
        mode = getIntent().getExtras().getInt(KEY_SETTING_MODE);

        //----------------------------------
        // Init String
        //----------------------------------
        SETTING_MENU_AUTO_REFRESH = getString(R.string.activity_defaultsetting_autorefresh);

        SETTING_MENU_MULTI_TWEET = getString(R.string.activity_setting_multi_tweet);

        SETTING_MENU_ACCOUNT_MANAGE = getString(R.string.activity_setting_account_manage);

        SETTING_MENU_TRANSLATION = getString(R.string.activity_defaultsetting_translate);

        SETTING_MENU_SET_WALL_PAPER = getString(R.string.activity_defaultsetting_setwallpaper);

        SETTING_MENU_NOTIFICATION = getString(R.string.activity_defaultsetting_notification);

        SETTING_MENU_FONT_COLOR = getString(R.string.activity_defaultsetting_fontcolor);

        SETTING_MENU_DEFAULT_SETTING = getString(R.string.activity_mainsetting_defaultsetting);

        SETTING_MENU_HASHTAG_SETTING = getString(R.string.activity_mainsetting_hashtag);

        //----------------------------------
        // Prepare Menu
        //----------------------------------
        String[] settingMenuNames =
        { SETTING_MENU_AUTO_REFRESH, SETTING_MENU_MULTI_TWEET,
                SETTING_MENU_TRANSLATION, SETTING_MENU_ACCOUNT_MANAGE,
                SETTING_MENU_SET_WALL_PAPER, SETTING_MENU_NOTIFICATION,
                SETTING_MENU_FONT_COLOR, SETTING_MENU_HASHTAG_SETTING,
                SETTING_MENU_DEFAULT_SETTING };
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < settingMenuNames.length; i++)
        {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("select_mode", settingMenuNames[i]);
            item.put("image", this.settingMenuImages[i]);
            data.add(item);
        }
        MySimpleAdapter adapter = new MySimpleAdapter(this, data,
                R.layout.list_item_setting, new String[]
                { "select_mode", "image" }, new int[]
                { R.id.TextView_setting, R.id.ImageView_setting });

        ListView listView = (ListView) findViewById(R.id.settinglist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    //-----------------------------------------------------------------------------
    /**
     *  Called when Activity is Started.
     */
    //-----------------------------------------------------------------------------
    @Override
    public void onStart()
    {
        super.onStart();

        if (this.myDb == null)
        {
            this.myDb = new MyDbAdapter(this);
            this.myDb.open();
        }
    }

    //-----------------------------------------------------------------------------
    /**
     *  Called when Activity is Stopped.
     */
    //-----------------------------------------------------------------------------
    @Override
    public void onStop()
    {
        super.onStop();
        this.myDb.close();
        this.myDb = null;
    }

    //-----------------------------------------------------------------------------
    /**
     *  Called when Item on List View is clicked.
     */
    //-----------------------------------------------------------------------------
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id)
    {

        switch (position)
        {
        case 0:
            showDialog(REFRESH);
            break;
        case 1:
            startActivity(new Intent(this, MultiUpdateSettingActivity.class));
            break;
        case 2:
            startActivity(new Intent(this, TranslateSettingActivity.class));
            break;
        case 3:

            if (mode != MODE_ACCOUNT_MANAGE_DISABLED)
            {
                SearchServiceDialog sd = new SearchServiceDialog(this);
                sd.setTitle(R.string.activity_mainsetting_pleasesetaccount);
                sd.show();
            }
            break;
        case 4:
        {
            Intent it = new Intent();
            it.setType("image/*");
            it.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(it, 1);
            Toast.makeText(this,
                    R.string.toast_actvity_mainsetting_selectwalpaper,
                    Toast.LENGTH_SHORT).show();
        }
            break;
        case 5:
        {
            showDialog(NOTIFICATION_DIALOG);
        }
            break;
        case 6:
            showDialog(COLORSELECT_DIGLOG);
            break;
        case 7: //Filter
            startActivity(new Intent(this, KeywordTabActivity.class));
            break;
        case 8:
            startActivity(new Intent(this, DefaultSettingActivity.class));
            break;
        default:
            break;
        }

    }

    //-----------------------------------------------------------------------------
    /**
     *  Called when showDialog() has called.
     */
    //-----------------------------------------------------------------------------
    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
        case 1:
            return showAutoRefreshSettingDialog();

        case NOTIFICATION_DIALOG:
        {
            return notificationDialog();
        }
        case COLORSELECT_DIGLOG:
        {
            return colorSelectDiglog();
        }
        }

        return super.onCreateDialog(id);
    }

    //-----------------------------------------------------------------------------
    /**
     *  Show Auto Refresh Setting Dialog
     */
    //-----------------------------------------------------------------------------
    private Dialog showAutoRefreshSettingDialog()
    {

        //Prepare Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.activity_defaultsetting_autorefresh);

        //Init Views
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(
                R.layout.dialog_setting_auto_refresh, null);
        builder.setView(view);
        final CheckBox refreshTimeCheck = (CheckBox) view.findViewById(R.id.auto_refresh);
        final CheckBox autoRefreshSwichTab = (CheckBox) view.findViewById(R.id.auto_refresh_switch_tab);
        final Spinner refreshTimeSpinnner = (Spinner) view.findViewById(R.id.Spinner_refreshtime);

        //-----------------------------------
        //Set Current Status
        //-----------------------------------
        String status = this.myDb.getSettingValue(MyDbAdapter.PARAM_SETTING_REFRESH_FLAG);
        if (status != null && status.equals(MyDbAdapter.PARAM_VALUE_ON))
        {
            refreshTimeCheck.setChecked(true);
        }
        else
        {
            refreshTimeCheck.setChecked(false);
        }

        //-----------------------------------
        //Set Current Auto Refresh Tab
        //-----------------------------------
        String autoRefreshSwitch = this.myDb.getSettingValue(MyDbAdapter.PARAM_SETTING_AUTOREFRESH_SWITCH_TAB);
        if (autoRefreshSwitch != null
                && autoRefreshSwitch.equals(MyDbAdapter.PARAM_VALUE_ON))
        {
            autoRefreshSwichTab.setChecked(true);
        }
        else
        {
            autoRefreshSwichTab.setChecked(false);
        }

        //-----------------------------------
        //Prepare Spinner
        //-----------------------------------
        final String[] refreshTime =
        { "1", "2", "5", "10", "20", "50", "60" };
        ArrayList<String> spinnerData = new ArrayList<String>();
        for (int i = 0; i < refreshTime.length; i++)
        {
            if (i == 0)
            {
                spinnerData.add(refreshTime[i]
                        + getString(R.string.activity_mainsetting_minute));
            }
            else
            {
                spinnerData.add(refreshTime[i]
                        + getString(R.string.activity_mainsetting_minutes));
            }
        }

        //Set Adapter
        ArrayAdapter<String> refreshadapter = new ArrayAdapter<String>(
                MainSettingActivity.this, android.R.layout.simple_spinner_item,
                spinnerData);
        refreshadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        refreshTimeSpinnner.setAdapter(refreshadapter);

        //Set Current Value as default
        String currentRefreshTime = this.myDb.getSettingValue(MyDbAdapter.PARAM_SETTING_REFRESH_TIME);
        if (currentRefreshTime != null)
        {
            for (int i = 0; i < refreshTime.length; i++)
            {
                if (currentRefreshTime.equals(refreshTime[i]))
                {
                    refreshTimeSpinnner.setSelection(i);
                    break;
                }
            }
        }

        //-----------------------------------
        // Set OK Button
        //-----------------------------------
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        //Update DB value
                        String selectedStatus = refreshTimeCheck.isChecked() ? MyDbAdapter.PARAM_VALUE_ON : MyDbAdapter.PARAM_VALUE_OFF;
                        String selectedTime = refreshTime[refreshTimeSpinnner.getSelectedItemPosition()];
                        String selectedSwitch = autoRefreshSwichTab.isChecked() ? MyDbAdapter.PARAM_VALUE_ON : MyDbAdapter.PARAM_VALUE_OFF;
                        MainSettingActivity.this.myDb.updateSetting(
                                MyDbAdapter.PARAM_SETTING_REFRESH_FLAG,
                                selectedStatus);
                        MainSettingActivity.this.myDb.updateSetting(
                                MyDbAdapter.PARAM_SETTING_REFRESH_TIME,
                                selectedTime);
                        MainSettingActivity.this.myDb.updateSetting(
                                MyDbAdapter.PARAM_SETTING_AUTOREFRESH_SWITCH_TAB,
                                selectedSwitch);
                    }
                });

        //-----------------------------------
        // Set Cancel Button
        //-----------------------------------
        builder.setNegativeButton(android.R.string.cancel, null);

        return builder.create();
    }

    //-----------------------------------------------------------------------------
    /**
     *  Show Notification Setting Dialog
     */
    //-----------------------------------------------------------------------------
    private Dialog notificationDialog()
    {

        //Prepare Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(SETTING_MENU_NOTIFICATION);

        //Init Views
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(
                R.layout.dialog_setting_notification, null);
        builder.setView(view);
        final CheckBox activationCheck = (CheckBox) view.findViewById(R.id.notification);
        final CheckBox directMessageCheck = (CheckBox) view.findViewById(R.id.direct_message_check_box);
        final CheckBox atMessageCheck = (CheckBox) view.findViewById(R.id.at_message_check_box);
        final CheckBox generalMessageCheck = (CheckBox) view.findViewById(R.id.general_message_check_box);

        //-----------------------------------
        //Prepare Check Box
        //-----------------------------------
        //Activate Status
        String status = this.myDb.getSettingValue(MyDbAdapter.PARAM_SETTING_NOTIFICATION);
        if (status != null && status.equals(MyDbAdapter.PARAM_VALUE_ON))
        {
            activationCheck.setChecked(true);
        }
        else
        {
            activationCheck.setChecked(false);
        }

        //At Message
        String atMessage = this.myDb.getSettingValue(MyDbAdapter.PARAM_SETTING_NOTIFICATION_AT_MESSAGE);
        if (atMessage != null && atMessage.equals(MyDbAdapter.PARAM_VALUE_ON))
        {
            atMessageCheck.setChecked(true);
        }
        else
        {
            atMessageCheck.setChecked(false);
        }

        //Direct Message
        String directMessage = this.myDb.getSettingValue(MyDbAdapter.PARAM_SETTING_NOTIFICATION_DIRECT_MESSAGE);
        if (directMessage != null
                && directMessage.equals(MyDbAdapter.PARAM_VALUE_ON))
        {
            directMessageCheck.setChecked(true);
        }
        else
        {
            directMessageCheck.setChecked(false);
        }

        //General Message
        String generalMessage = this.myDb.getSettingValue(MyDbAdapter.PARAM_SETTING_NOTIFICATION_GENERAL_MESSAGE);
        if (generalMessage != null
                && generalMessage.equals(MyDbAdapter.PARAM_VALUE_ON))
        {
            generalMessageCheck.setChecked(true);
        }
        else
        {
            generalMessageCheck.setChecked(false);
        }

        //-----------------------------------
        // Set OK Button
        //-----------------------------------
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        //Get Selected Status
                        String selectedStatus = activationCheck.isChecked() ? MyDbAdapter.PARAM_VALUE_ON : MyDbAdapter.PARAM_VALUE_OFF;
                        String selectedAtMessage = atMessageCheck.isChecked() ? MyDbAdapter.PARAM_VALUE_ON : MyDbAdapter.PARAM_VALUE_OFF;
                        String selectedDirectMessage = directMessageCheck.isChecked() ? MyDbAdapter.PARAM_VALUE_ON : MyDbAdapter.PARAM_VALUE_OFF;
                        String selectedGeneralMessage = generalMessageCheck.isChecked() ? MyDbAdapter.PARAM_VALUE_ON : MyDbAdapter.PARAM_VALUE_OFF;

                        //Update DB
                        MainSettingActivity.this.myDb.updateSetting(
                                MyDbAdapter.PARAM_SETTING_NOTIFICATION,
                                selectedStatus);
                        MainSettingActivity.this.myDb.updateSetting(
                                MyDbAdapter.PARAM_SETTING_NOTIFICATION_AT_MESSAGE,
                                selectedAtMessage);
                        MainSettingActivity.this.myDb.updateSetting(
                                MyDbAdapter.PARAM_SETTING_NOTIFICATION_DIRECT_MESSAGE,
                                selectedDirectMessage);
                        MainSettingActivity.this.myDb.updateSetting(
                                MyDbAdapter.PARAM_SETTING_NOTIFICATION_GENERAL_MESSAGE,
                                selectedGeneralMessage);

                        //Start/Stop Service
                        Intent serviceIntent = new Intent(
                                MainSettingActivity.this,
                                NotificationCheckService.class);
                        if (selectedStatus.equals(MyDbAdapter.PARAM_VALUE_ON))
                        {
                            startService(serviceIntent);
                        }

                    }
                });

        //-----------------------------------
        // Set Cancel Button
        //-----------------------------------
        builder.setNegativeButton(android.R.string.cancel, null);

        return builder.create();

    }

    //-----------------------------------------------------------------------------
    /**
     *  Show Font Color Setting Dialog
     */
    //-----------------------------------------------------------------------------
    private Dialog colorSelectDiglog()
    {
        //Prepare Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(SETTING_MENU_FONT_COLOR);

        //Init Views
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.dialog_setting_font_color,
                null);
        builder.setView(view);
        final ImageView colorImageview = (ImageView) view.findViewById(R.id.color_imageview);
        ImageView yellowButton = (ImageButton) view.findViewById(R.id.yellow);
        ImageView blackButton = (ImageButton) view.findViewById(R.id.black);
        ImageView greenButton = (ImageButton) view.findViewById(R.id.green);
        ImageView whiteButton = (ImageButton) view.findViewById(R.id.white);
        ImageView blueButton = (ImageButton) view.findViewById(R.id.blue);

        //Set Current Color
        String currentValue = this.myDb.getSettingValue(MyDbAdapter.PARAM_SETTING_FONT_COLOR);
        this.selectedFontColor = Integer.valueOf(currentValue);
        colorImageview.setBackgroundColor(this.selectedFontColor);

        //Add Listener
        yellowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                colorImageview.setBackgroundColor(Color.YELLOW);
                MainSettingActivity.this.selectedFontColor = Color.YELLOW;
            }
        });
        blackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                colorImageview.setBackgroundColor(Color.BLACK);
                MainSettingActivity.this.selectedFontColor = Color.BLACK;
            }
        });
        greenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                colorImageview.setBackgroundColor(Color.GREEN);
                MainSettingActivity.this.selectedFontColor = Color.GREEN;
            }
        });
        whiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                colorImageview.setBackgroundColor(Color.WHITE);
                MainSettingActivity.this.selectedFontColor = Color.WHITE;
            }
        });
        blueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                colorImageview.setBackgroundColor(Color.BLUE);
                MainSettingActivity.this.selectedFontColor = Color.BLUE;
            }
        });

        //-----------------------------------
        // Set OK Button
        //-----------------------------------
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        MainSettingActivity.this.myDb.updateSetting(
                                MyDbAdapter.PARAM_SETTING_FONT_COLOR,
                                String.valueOf(MainSettingActivity.this.selectedFontColor));
                    }
                });

        //-----------------------------------
        // Set Cancel Button
        //-----------------------------------
        builder.setNegativeButton(android.R.string.cancel, null);

        return builder.create();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //Open DB
        if (this.myDb == null)
        {
            this.myDb = new MyDbAdapter(this);
            this.myDb.open();
        }
        try
        {
            Uri uri = data.getData();
            String path = getRealPathFromURI(uri);
            this.myDb.updateSetting(MyDbAdapter.PARAM_SETTING_IMAGE_PATH, path);
        }
        catch (Exception e)
        {
            Log.w("StatusDroid", "Error Occured", e);
        }
    }

    //---------------------------------------------------------
    /**
     * Get Real Path of Image file from URI data
     */
    //---------------------------------------------------------
    public String getRealPathFromURI(Uri contentUri)
    {

        String path;

        // can post image
        String[] proj =
        { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        path = cursor.getString(column_index);
        cursor.close();

        return path;
    }
}
