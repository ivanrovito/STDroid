package com.anhuioss.crowdroid;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.anhuioss.crowdroid.settings.MainSettingActivity;
import com.anhuioss.crowdroid.settings.TwitterSettingActivity;
import com.anhuioss.crowdroid.twitter.TwitterHandler;
import com.anhuioss.crowdroid.util.AccountInfo;
import com.anhuioss.crowdroid.util.CommunicationHandlerResult;
import com.anhuioss.crowdroid.util.CrowdroidUpdate;
import com.anhuioss.crowdroid.util.MyDbAdapter;
import com.anhuioss.crowdroid.util.MyUncaughtExceptionHandler;

public class LoginActivity extends Activity
{

    //Option Menu
    private static final int       MENU_HELP           = 1;

    private static final int       MENU_LICENSE        = 2;

    private static final int       MENU_ABOUT          = 3;

    private static final int       MENU_SETTING        = 4;

    int                            service1;

    /** Select Service*/
    private Spinner                selectServiceSpinner;

    /** Select User*/
    private Spinner                selectUserSpinner;

    /** Add User*/
    private Button                 addAccountButton;

    /** login button*/
    private ImageButton            loginButton;

    /** account List */
    private ArrayList<AccountInfo> accountList;

    /** Service List */
    private static final String[]  services            =
                                                       { IGeneral.SERVICE_NAME_TWITTER };

    /** DB */
    private MyDbAdapter            myDbAdapter;

    /** Progress Dialog */
    private ProgressDialog         progress;

    /** Thread threadLoginActivity*/

    private Thread                 threadLoginActivity = null;

    /** Handler */
    Handler                        myHandler           = new Handler() {

                                                           @Override
                                                           public void handleMessage(
                                                                   Message msg)
                                                           {

                                                               //Close Progress Bar
                                                               LoginActivity.this.progress.dismiss();

                                                               //Get Param
                                                               Bundle bundle = msg.getData();
                                                               String toastMessage = bundle.getString("toastMessage");

                                                               //Show Toast (Change Message if the toast message was "ok")
                                                               if (toastMessage.equals(getString(R.string.error_message_communication_handler_suceeded)))
                                                               {
                                                                   Toast.makeText(
                                                                           LoginActivity.this,
                                                                           getString(R.string.activity_lgoin_loginsucceed),
                                                                           Toast.LENGTH_SHORT).show();
                                                               }
                                                               else
                                                               {
                                                                   Toast.makeText(
                                                                           LoginActivity.this,
                                                                           toastMessage,
                                                                           Toast.LENGTH_SHORT).show();
                                                               }

                                                               //Login if result is true
                                                               boolean result = msg.getData().getBoolean(
                                                                       "result");
                                                               if (result)
                                                               {
                                                                   moveToTimeLineActivity();
                                                               }

                                                           }

                                                       };

    //------------------------------------------------------------------
    /** 
     * Called when the activity is first created.
     **/
    //------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.myDbAdapter = new MyDbAdapter(this);
        this.myDbAdapter.open();

        //Set Error Handler
        Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler(
                this));

        //Display Dialog if Error has occurred last time.
        MyUncaughtExceptionHandler.showBugReportDialogIfExist(this);

        //Init Views
        this.selectServiceSpinner = (Spinner) findViewById(R.id.selectServiceSpinner);
        this.selectUserSpinner = (Spinner) findViewById(R.id.selectUserSpinner);
        this.addAccountButton = (Button) findViewById(R.id.addAccountButton);
        this.loginButton = (ImageButton) findViewById(R.id.loginButton);

        //Prepare Service Spinner
        ArrayList<String> serviceList = new ArrayList<String>();
        for (int i = 0; i < services.length; i++)
        {
            serviceList.add(services[i]);
        }
        ArrayAdapter<String> serviceAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, serviceList);
        serviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.selectServiceSpinner.setAdapter(serviceAdapter);
        this.selectServiceSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id)
            {

                setUserSpinner(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            // TODO Auto-generated method stub

            }
        });

        //Prepare User Spinner
        this.selectUserSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id)
            {

                //Register to DB (current_service/current_twitter_user/current_follow5_user)(account table)
                String uid = LoginActivity.this.accountList.get(
                        LoginActivity.this.selectUserSpinner.getSelectedItemPosition()).getUserId();
                String name = LoginActivity.this.accountList.get(
                        LoginActivity.this.selectUserSpinner.getSelectedItemPosition()).getName();
                String screenName = LoginActivity.this.accountList.get(
                        LoginActivity.this.selectUserSpinner.getSelectedItemPosition()).getScreenName();
                int service = LoginActivity.this.selectServiceSpinner.getSelectedItemPosition();//0(twitter)1(follow5)
                LoginActivity.this.myDbAdapter.updateLoginStatus(
                        services[service], uid);
                LoginActivity.this.myDbAdapter.updateAccount2(uid,
                        services[service], name, screenName);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            // TODO Auto-generated method stub

            }
        });
        //Set Default
        setUserSpinner(IGeneral.SERVICE_ID_TWITTER);

        //Prepare Add Account Button
        this.addAccountButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v)
            {
                //Open Dialog
                Intent intent1 = new Intent(LoginActivity.this,
                        TwitterSettingActivity.class);
                LoginActivity.this.startActivity(intent1);
            }

        });

        //Prepare Login Button		
        this.loginButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v)
            {
                LoginActivity.this.service1 = LoginActivity.this.selectServiceSpinner.getSelectedItemPosition();//0(twitter)1(follow5)2(crowdroid_business)
                login();
            }

        });

    }

    //------------------------------------------------------------------
    /** 
     * Called when the activity is started.
     **/
    //------------------------------------------------------------------
    @Override
    public void onStart()
    {
        super.onStart();
        if (this.myDbAdapter == null)
        {
            this.myDbAdapter = new MyDbAdapter(this);
            this.myDbAdapter.open();
        }
        setUserSpinner(this.selectServiceSpinner.getSelectedItemPosition());
    }

    //--------------------------------------------------
    /**
     *Called when Activity is Stopped
     */
    //-------------------------------------------------
    @Override
    public void onStop()
    {
        if (this.threadLoginActivity != null
                && this.threadLoginActivity.isAlive())
        {
            try
            {
                this.threadLoginActivity.join();
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                Log.w("StatusDroid", "Error Occured", e);
            }
            catch (Exception e)
            {
                Log.w("StatusDroid", "Error Occured", e);
            }
            this.threadLoginActivity = null;
        }
        //Close DB
        if (this.myDbAdapter != null)
        {
            this.myDbAdapter.close();
            this.myDbAdapter = null;
        }
        super.onStop();
    }

    //------------------------------------------------------
    /**
     * Set Spinner
     */
    //-------------------------------------------------------
    private void setUserSpinner(int service)
    {

        //Get User List
        ArrayList<String> screenNameList = getScreenNameList(service);

        //Set to Spinners
        ArrayAdapter<String> userAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, screenNameList);
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.selectUserSpinner.setAdapter(userAdapter);

    }

    //------------------------------------------------------
    /**
     * Get Screen Name List
     */
    //-------------------------------------------------------
    private ArrayList<String> getScreenNameList(int service)
    {

        ArrayList<String> screenNameList = new ArrayList<String>();

        //Change SelectUserSpineer(uid,name,screen_name,access_token,token_secret);
        this.accountList = this.myDbAdapter.getAccountList(services[service],
                null);

        //Extract screen Name 
        for (AccountInfo account : this.accountList)
        {
            String screenName = account.getScreenName();
            if (screenName == null)
            {
                screenName = "";
            }
            screenNameList.add(screenName);
        }

        return screenNameList;
    }

    //------------------------------------------------------
    /**
     * Create Option Menu
     */
    //-------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        // TODO ヘルプページとライセンスページを作成する
        //        menu.add(0, MENU_HELP, 1, IGeneral.OPTION_MENU_TEXT_HELP).setIcon(
        //                IGeneral.OPTION_MENU_IMAGE_HELP);
        //        menu.add(0, MENU_LICENSE, 2, IGeneral.OPTION_MENU_TEXT_LICENSE).setIcon(
        //                IGeneral.OPTION_MENU_IMAGE_LICENSE);
        menu.add(0, MENU_ABOUT, 3, IGeneral.OPTION_MENU_TEXT_ABOUT).setIcon(
                IGeneral.OPTION_MENU_IMAGE_ABOUT);
        menu.add(0, MENU_SETTING, 3, IGeneral.OPTION_MENU_TEXT_SETTINGS).setIcon(
                IGeneral.OPTION_MENU_IMAGE_SETTINGS);
        return true;
    }

    //------------------------------------------------------
    /**
     * Called when Menu Item Selected
     */
    //-------------------------------------------------------
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {

        switch (item.getItemId())
        {
        case MENU_HELP:
        {
            Intent intent = new Intent(this, ShowWebContentsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(IGeneral.TYPE, IGeneral.TYPE_HELP);
            intent.putExtras(bundle);
            startActivity(intent);
            break;
        }
        case MENU_LICENSE:
        {
            Intent intent = new Intent(this, ShowWebContentsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(IGeneral.TYPE, IGeneral.TYPE_LICENSE);
            intent.putExtras(bundle);
            startActivity(intent);
            break;
        }
        case MENU_ABOUT:
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.activity_lgoin_about);
            LayoutInflater inflater = LayoutInflater.from(this);
            final View textEntryView = inflater.inflate(R.layout.dialog_about,
                    null);
            builder.setView(textEntryView);
            TextView textAbout = (TextView) textEntryView.findViewById(R.id.TextView01_about);
            textAbout.setLinksClickable(true);
            String version_name = "";
            try
            {
                version_name = getPackageManager().getPackageInfo(
                        getPackageName(), 0).versionName;

            }
            catch (Exception e)
            {
                Log.w("StatusDroid", "Error Occured", e);
            }

            String about = getString(R.string.activity_login_softwareName)
                    + version_name + getString(R.string.activity_login_MiniSDK)
                    + "1.6\n" + getString(R.string.activity_login_developer);

            textAbout.setText(about);
            builder.setPositiveButton(R.string.ok, null);
            builder.create();
            builder.show();
            break;
        }
        case MENU_SETTING:
        {
            Intent intent = new Intent(this, MainSettingActivity.class);
            intent.putExtra(MainSettingActivity.KEY_SETTING_MODE,
                    MainSettingActivity.MODE_ALL_MENU_AVAILABLE);
            startActivity(intent);
            break;
        }
        default:
        {
            break;
        }

        }
        return super.onMenuItemSelected(featureId, item);

    }

    //--------------------------------------------------
    /**
     * Login 
     */
    //--------------------------------------------------
    private void login()
    {
        //Get Selected Service
        int service = this.selectServiceSpinner.getSelectedItemPosition();

        //return if account list is not created.
        if (this.accountList == null || this.accountList.size() == 0)
        {
            return;
        }

        //Check if selected account is available
        checkAccount(service);
    }

    //------------------------------------------------
    /**
     * check account is available
     */
    //------------------------------------------------
    private void checkAccount(final int service)
    {

        //Show Progress Dialog
        this.progress = new ProgressDialog(LoginActivity.this);
        this.progress.setIndeterminate(false);
        this.progress.show();

        //Create Thread
        this.threadLoginActivity = new Thread(new Runnable() {

            @Override
            public void run()
            {

                boolean resultFlag = false;
                //Get Selected Parameter
                int service = LoginActivity.this.selectServiceSpinner.getSelectedItemPosition();
                AccountInfo account = LoginActivity.this.accountList.get(LoginActivity.this.selectUserSpinner.getSelectedItemPosition());

                CommunicationHandlerResult result = null;

                String twitterApiProxy = LoginActivity.this.myDbAdapter.getSettingValue(MyDbAdapter.PARAM_SETTING_TWITTER_API_PROXY);
                String apiServer = LoginActivity.this.myDbAdapter.getSettingValue(MyDbAdapter.PARAM_SETTING_TWITTER_API_PROXY_SERVER);
                //Use Proxy API
                if (twitterApiProxy.equals(MyDbAdapter.PARAM_VALUE_ON))
                {
                    result = TwitterHandler.verifyUser(account.getScreenName(),
                            account.getPassWord(),
                            TwitterHandler.AUTH_TYPE_BASIC, apiServer);
                    if (result.getResultCode() == 200)
                    {
                        //Set Account Info to Handler
                        TwitterHandler.setAccount(account.getScreenName(),
                                account.getPassWord(),
                                TwitterHandler.AUTH_TYPE_BASIC, apiServer);
                        resultFlag = true;
                    }
                    else
                    {
                        resultFlag = false;
                    }
                }
                //Use Twitter Original Server
                else
                {
                    result = TwitterHandler.verifyUser(
                            account.getAccessToken(), account.getTokenSecret(),
                            TwitterHandler.AUTH_TYPE_OAUTH,
                            TwitterHandler.TWITTER_ORIGINAL_API_SERVER);
                    if (result.getResultCode() == 200)
                    {
                        //Set Account Info to Handler
                        TwitterHandler.setAccount(account.getAccessToken(),
                                account.getTokenSecret(),
                                TwitterHandler.AUTH_TYPE_OAUTH,
                                TwitterHandler.TWITTER_ORIGINAL_API_SERVER);
                        resultFlag = true;
                    }
                    else
                    {
                        resultFlag = false;
                    }
                }

                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putBoolean("result", resultFlag);
                //toast message
                bundle.putString("toastMessage",
                        getString(result.getResultMessage()));
                msg.setData(bundle);
                LoginActivity.this.myHandler.sendMessage(msg);

            }
        }, "check account thread");
        try
        {
            this.threadLoginActivity.join();

        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            Log.w("StatusDroid", "Error Occured", e);
        }
        catch (Exception e)
        {
            Log.w("StatusDroid", "Error Occured", e);
        }
        this.threadLoginActivity.start();

    }

    //--------------------------------------------------------------------------
    /**
     * Move to TimeLine Activity.
     */
    //--------------------------------------------------------------------------
    private void moveToTimeLineActivity()
    {

        int service = this.selectServiceSpinner.getSelectedItemPosition();//0(twitter)1(follow5)2(crowdroid_business)

        //Register to DB (current_service/current_twitter_user/current_follow5_user)(account table)
        String uid = this.accountList.get(
                this.selectUserSpinner.getSelectedItemPosition()).getUserId();
        String name = this.accountList.get(
                this.selectUserSpinner.getSelectedItemPosition()).getName();
        String screenName = this.accountList.get(
                this.selectUserSpinner.getSelectedItemPosition()).getScreenName();
        if (this.myDbAdapter != null)
        {
            this.myDbAdapter.updateLoginStatus(services[service], uid);
            this.myDbAdapter.updateAccount2(uid, services[service], name,
                    screenName);

            //Start TimeLine Activity
            Intent intent = new Intent(LoginActivity.this,
                    TimeLineActivity.class);
            startActivity(intent);
        }

    }

}
