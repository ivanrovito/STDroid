package jp.gr.kmtn.stdroid.dialog;

import java.util.ArrayList;

import jp.gr.kmtn.stdroid.IGeneral;
import jp.gr.kmtn.stdroid.R;
import jp.gr.kmtn.stdroid.info.BasicInfo;
import jp.gr.kmtn.stdroid.twitter.TwitterHandler;
import jp.gr.kmtn.stdroid.util.AccountInfo;
import jp.gr.kmtn.stdroid.util.CommunicationHandlerResult;
import jp.gr.kmtn.stdroid.util.MyDbAdapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class UpdateDialog extends Dialog
{

    /** Update Message Text */
    private TextView            uploadMessageText;

    /** Update Image Path Text */
    private TextView            uploadImagePathText;

    /** Update Text */
    private EditText            updateText;

    /** Counter */
    private TextView            counterText;

    /** Max Text */
    private int                 MAX_TEXT_COUNT      = 140;

    /** Translate */
    private ImageButton         translateButton;

    /** Upload Image Button */
    private ImageButton         uploadImageButton;

    /** Shorten URL Button */
    private ImageButton         shortenUrlButton;

    /** TwitLonger Button */
    private ImageButton         longTweetButton;

    /** OK Button */
    private Button              okButton;

    /** Cancel Button */
    private Button              cancelButton;

    /** Location Manager(for GPS) */
    private LocationManager     locationManager;

    /** Location Listener(for GPS) */
    LocationListener            locationListener;

    /** Location (for GPS) */
    Location                    location;

    /** Mode */
    private int                 mode                = 0;

    /** MODE NEW TWEET */
    public static final int     MODE_NEW_TWEET      = 1;

    /** MODE AT MESSAGE */
    public static final int     MODE_AT_MESSAGE     = 2;

    /** MODE DIRECT MESSAGE */
    public static final int     MODE_DIRECT_MESSAGE = 3;

    /** Target ScreenName (for Reply/DirectMessage) */
    private String              targetScreenName;

    /** account name for update */
    String                      accountName         = null;

    /** secret for update */
    String                      secret              = null;

    /** Location Array */
    String                      locationArray[]     = new String[2];

    /** Current Login Account */
    private AccountInfo         currentLoginAccountInfo;

    /** Multi update Account Info List */
    private ArrayList<String[]> multiUpdateInfo;

    /** Progress Dialog */
    private ProgressDialog      progress;

    private MyDbAdapter         db;

    private static final String IMAGEUR_API_KEY     = "d7d2c143a44dc72870dbea09c170f465";

    private static final String TWITPIC_API_KEY     = "49b501cfebb227634ae716c03bff6b0c";

    private boolean             isLongTweetEnabled  = false;

    /** Handler */
    Handler                     mHandler            = new Handler() {
                                                        @Override
                                                        public void handleMessage(
                                                                Message msg)
                                                        {

                                                            // Close Progress Dialog
                                                            UpdateDialog.this.progress.dismiss();

                                                            // Show Result Dialog
                                                            String errors = msg.getData().getString(
                                                                    "errors");
                                                            if (errors != null
                                                                    && errors.length() > 0)
                                                            {
                                                                showResultDialog(errors);
                                                            }
                                                            else
                                                            {
                                                                dismiss();
                                                            }
                                                        }

                                                    };

    /** Handler2 
     * Just a Temp. Can delete at 2.0(Nakashima)
     * */
    Handler                     mHandler2           = new Handler() {
                                                        @Override
                                                        public void handleMessage(
                                                                Message msg)
                                                        {
                                                            Toast.makeText(
                                                                    getContext(),
                                                                    getContext().getString(
                                                                            R.string.error_message_rejected_for_ngword),
                                                                    Toast.LENGTH_LONG).show();
                                                        }

                                                    };

    // -----------------------------------------------------------------------------
    /**
     * Constructor.
     */
    // -----------------------------------------------------------------------------
    public UpdateDialog(Context context, int mode, MyDbAdapter dbAdapter)
    {
        super(context);
        super.setContentView(R.layout.dialog_update);

        this.db = dbAdapter;

        // Set Mode
        this.mode = mode;

        // Set Account Info
        setAccount();

        // ----------------------
        // Init Views
        // ----------------------

        // Upload Image Text
        this.uploadMessageText = (TextView) findViewById(R.id.upload_message);

        // Upload Image Path Text
        this.uploadImagePathText = (TextView) findViewById(R.id.file_path);

        // Update Text
        this.updateText = (EditText) findViewById(R.id.update_text);

        // Counter
        this.counterText = (TextView) findViewById(R.id.counterText);

        // Translate Button
        this.translateButton = (ImageButton) findViewById(R.id.translateButton);
        this.translateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                TranslateDialog dialog = new TranslateDialog(getContext(),
                        UpdateDialog.this.updateText, UpdateDialog.this.db);
                dialog.show();

            }
        });

        // Upload Image Button
        this.uploadImageButton = (ImageButton) findViewById(R.id.uploadImageButton);
        this.uploadImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                // Open Dialog
                FileExplorer dialog = new FileExplorer(getContext());
                String[] serverMessage = new String[6];

                String type = UpdateDialog.this.db.getSettingValue(MyDbAdapter.PARAM_SETTING_TWITTER_API_PROXY);
                if (type.equals(MyDbAdapter.PARAM_VALUE_OFF))
                {
                    serverMessage[0] = TwitterHandler.AUTH_TYPE_OAUTH;
                    serverMessage[1] = TwitterHandler.CONSUMER_KEY;
                    serverMessage[2] = TwitterHandler.CONSUMER_SECRET;
                    serverMessage[3] = UpdateDialog.this.currentLoginAccountInfo.getAccessToken();
                    serverMessage[4] = UpdateDialog.this.currentLoginAccountInfo.getTokenSecret();
                    serverMessage[5] = TWITPIC_API_KEY;
                }
                else
                {
                    serverMessage[0] = TwitterHandler.AUTH_TYPE_BASIC;
                    serverMessage[5] = IMAGEUR_API_KEY;
                }
                dialog.setTarget(UpdateDialog.this.uploadImagePathText,
                        UpdateDialog.this.updateText, serverMessage);
                dialog.show();

            }
        });

        // Shorten URL Button
        this.shortenUrlButton = (ImageButton) findViewById(R.id.shortenUrlButton);
        this.shortenUrlButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                UrlShortenDialog dialog = new UrlShortenDialog(getContext(),
                        UpdateDialog.this.updateText);
                dialog.show();

            }
        });

        //Long Tweet
        this.longTweetButton = (ImageButton) findViewById(R.id.longTweetButton);
        this.longTweetButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                LongTweetDialog dialog = new LongTweetDialog(getContext(),
                        UpdateDialog.this.updateText,
                        UpdateDialog.this.currentLoginAccountInfo);
                dialog.show();

            }
        });
        //Check if Long Tweet is Enabled
        if (this.currentLoginAccountInfo.getTokenSecret() != null)
        {
            this.isLongTweetEnabled = true;
        }
        else
        {
            this.isLongTweetEnabled = false;
            this.longTweetButton.setVisibility(View.GONE);
        }

        this.currentLoginAccountInfo.getService();

        this.updateText.setText(String.valueOf(this.MAX_TEXT_COUNT));

        this.updateText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count)
            {
                int leftCount = UpdateDialog.this.MAX_TEXT_COUNT
                        - UpdateDialog.this.updateText.getText().toString().length();

                UpdateDialog.this.counterText.setText(String.valueOf(leftCount));

                if (leftCount < 0)
                {
                    UpdateDialog.this.counterText.setTextColor(0xFFFF4500);
                    UpdateDialog.this.okButton.setVisibility(android.view.View.INVISIBLE);
                    if (UpdateDialog.this.isLongTweetEnabled)
                    {
                        UpdateDialog.this.longTweetButton.setEnabled(true);
                    }

                }
                else
                {
                    UpdateDialog.this.okButton.setVisibility(android.view.View.VISIBLE);
                    UpdateDialog.this.counterText.setTextColor(0xFFF5FFFA);
                    if (UpdateDialog.this.isLongTweetEnabled)
                    {
                        UpdateDialog.this.longTweetButton.setEnabled(false);
                    }
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after)
            {}

            @Override
            public void afterTextChanged(Editable s)
            {}
        });

        // OK Button
        this.okButton = (Button) findViewById(R.id.okButton);
        this.okButton.setText(android.R.string.ok);
        this.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                updateMessage();
            }
        });

        // Cancel Button
        this.cancelButton = (Button) findViewById(R.id.cancelButton);
        this.cancelButton.setText(android.R.string.cancel);
        this.cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });

        // Set Default Title
        if (mode == MODE_NEW_TWEET)
        {
            setTitle("NEW TWEET");
            this.updateText.setText("");
        }
        else if (mode == MODE_DIRECT_MESSAGE)
        {
            this.uploadImageButton.setVisibility(View.GONE);
        }

        if (this.multiUpdateInfo.size() > 0)
        {
            this.uploadImagePathText.setVisibility(View.GONE);
            this.uploadImageButton.setVisibility(View.GONE);
            this.uploadMessageText.setVisibility(View.GONE);
        }

    }

    // -------------------------------------------------------
    /**
     * Set Account
     */
    // --------------------------------------------------------
    private void setAccount()
    {

        // Set Current Login Account Info
        this.currentLoginAccountInfo = this.db.getCurrentLoginAccountInfo();

        // Set Multi Update Account Info
        this.multiUpdateInfo = new ArrayList<String[]>();
        if (this.mode == MODE_NEW_TWEET)
        {
            ArrayList<AccountInfo> accountInfoList = this.db.getAccountList(
                    IGeneral.SERVICE_NAME_TWITTER, null);
            String uid = this.db.getSettingValue(MyDbAdapter.PARAM_SETTING_TWITTER_UPDATE_UID);
            AccountInfo account = null;
            for (AccountInfo info : accountInfoList)
            {
                if (info.getScreenName().equals(uid))
                {
                    account = info;
                    break;
                }
            }

            if (account != null)
            {
                String API_PROXY_ADDRESS = this.db.getSettingValue(MyDbAdapter.PARAM_SETTING_TWITTER_API_PROXY_SERVER);
                if (this.db.getSettingValue(
                        MyDbAdapter.PARAM_SETTING_TWITTER_API_PROXY).equals(
                        MyDbAdapter.PARAM_VALUE_ON))
                {
                    if (API_PROXY_ADDRESS != null
                            && API_PROXY_ADDRESS.length() > 0)
                    {
                        TwitterHandler.setAccount(account.getName(),
                                account.getPassWord(),
                                TwitterHandler.AUTH_TYPE_BASIC,
                                API_PROXY_ADDRESS);
                        this.multiUpdateInfo.add(new String[]
                        { account.getScreenName(), account.getPassWord(),
                                IGeneral.SERVICE_NAME_TWITTER });
                    }
                }
                else
                {
                    TwitterHandler.setAccount(
                            account.getAccessToken(),
                            account.getTokenSecret(),
                            TwitterHandler.AUTH_TYPE_OAUTH,
                            this.db.getSettingValue(MyDbAdapter.PARAM_SETTING_TWITTER_API_PROXY_SERVER));
                    this.multiUpdateInfo.add(new String[]
                    { account.getAccessToken(), account.getTokenSecret(),
                            IGeneral.SERVICE_NAME_TWITTER });
                }
            }
        }

    }

    // -------------------------------------------------------
    /**
     * this method is used to get the current location
     */
    // --------------------------------------------------------
    protected void initGps(Context context)
    {
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        this.locationListener = new LocationListener() {
            public void onLocationChanged(Location loc)
            {

                UpdateDialog.this.location = loc;
                // log it when the location changes
                if (UpdateDialog.this.location != null)
                {
                    Log.i("SuperMap", "Location changed : Lat: "
                            + UpdateDialog.this.location.getLatitude()
                            + " Lng: "
                            + UpdateDialog.this.location.getLongitude());
                    /*
                    			Toast.makeText(
                    					getContext(),
                    					location.getLatitude() + ":"
                    							+ location.getLongitude(),
                    					Toast.LENGTH_SHORT).show();
                    */
                }
            }

            public void onProviderDisabled(String provider)
            {
                Log.d("test", "providerDisabled");
                // Provider被disable时触发此函数，比如GPS被关闭
            }

            public void onProviderEnabled(String provider)
            {
                Log.d("test", "providerEnabled");
                // Provider被enable时触发此函数，比如GPS被打开
            }

            public void onStatusChanged(String provider, int status,
                    Bundle extras)
            {
                Log.d("test", "providerEnabled");
                // Provider的转态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
            }
        };
        this.locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 0, 0, this.locationListener);

        Location location = this.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location == null)
        {
            location = this.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (location != null)
        {
            this.locationArray[0] = String.valueOf(location.getLatitude());// 经度
            this.locationArray[1] = String.valueOf(location.getLongitude());// 纬度
        }
    }

    public void closeGps()
    {
        if (this.locationManager != null)
        {
            this.locationManager.removeUpdates(this.locationListener);
        }
    }

    public void setlocation(Location location)
    {
        this.locationArray[0] = String.valueOf(location.getLatitude());// 经度
        this.locationArray[1] = String.valueOf(location.getLongitude());// 纬度
    }

    // -----------------------------------------------------------------------------
    /**
     * Set Target.
     */
    // -----------------------------------------------------------------------------
    public void setTarget(String targetScreenName, String fid)
    {

        this.targetScreenName = targetScreenName;
        if (this.mode == MODE_AT_MESSAGE)
        {
            setTitle("@" + targetScreenName);
            this.updateText.setText("@" + targetScreenName + " ");
        }
        else if (this.mode == MODE_DIRECT_MESSAGE)
        {
            setTitle(getContext().getString(
                    R.string.dialog_update_directmessageto)
                    + " " + targetScreenName);
            this.updateText.setText("");
        }

    }

    // -----------------------------------------------------------------------------
    /**
     * Set Target.
     */
    // -----------------------------------------------------------------------------
    public void setTarget(BasicInfo info)
    {
        this.updateText.setText(" RT @" + info.getUserInfo().getScreenName()
                + " " + info.getStatus());
    }

    // -----------------------------------------------------------------------------
    /**
     * Update Message
     */
    // -----------------------------------------------------------------------------
    public void updateMessage()
    {

        // Get Message
        String message = this.updateText.getText().toString();

        // Prepare Progress
        this.progress = new ProgressDialog(getContext());
        this.progress.setIndeterminate(false);
        this.progress.show();

        // Send
        send(message);
    }

    // -----------------------------------------------------------------------------
    /**
     * Send Message according to its Mode.
     */
    // -----------------------------------------------------------------------------
    public void send(final String message)
    {
        this.accountName = this.currentLoginAccountInfo.getAccessToken();
        this.secret = this.currentLoginAccountInfo.getTokenSecret();

        // Prepare Thread
        Thread th = new Thread(new Runnable() {

            @Override
            public void run()
            {
                CommunicationHandlerResult result = new CommunicationHandlerResult();
                StringBuffer errors = new StringBuffer(); // serviceName1:serviceName2....
                // int result = 0;

                if (UpdateDialog.this.mode == MODE_NEW_TWEET)
                {
                    // Multi Update
                    Boolean multiUpdateFlag = false;
                    if (UpdateDialog.this.multiUpdateInfo != null
                            && UpdateDialog.this.multiUpdateInfo.size() != 0)
                    {
                        multiUpdateFlag = true;
                    }

                    // Send with Current Login User
                    result = TwitterHandler.updateStatus(message);
                    if (result.getResultCode() != 200)
                    {
                        errors.append(IGeneral.SERVICE_NAME_TWITTER + ":");
                    }

                    // Multi Update
                    if (multiUpdateFlag)
                    {
                        for (String[] info : UpdateDialog.this.multiUpdateInfo)
                        {
                            result = TwitterHandler.updateStatus(message);
                            if (result.getResultCode() != 200)
                            {
                                errors.append(IGeneral.SERVICE_NAME_TWITTER
                                        + ":");
                            }
                        }
                    }
                }
                else if (UpdateDialog.this.mode == MODE_AT_MESSAGE)
                {
                    result = TwitterHandler.updateStatus(message);
                    if (result.getResultCode() != 200)
                    {
                        errors.append(IGeneral.SERVICE_NAME_TWITTER + ":");
                    }

                }
                else if (UpdateDialog.this.mode == MODE_DIRECT_MESSAGE)
                {
                    result = TwitterHandler.directMessage(
                            UpdateDialog.this.targetScreenName, message);
                    if (result.getResultCode() != 200)
                    {
                        errors.append(IGeneral.SERVICE_NAME_TWITTER + ":"
                                + String.valueOf(result));
                    }
                }

                // Show Toast if Error
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("errors", errors.toString());
                msg.setData(bundle);
                UpdateDialog.this.mHandler.sendMessage(msg);
            }
        });
        th.start();

    }

    // -----------------------------------------------------------------------------
    /**
     * Send Message according to its Mode.
     */
    // -----------------------------------------------------------------------------
    public void sendAndUploadImage(final String message, final String filePath)
    {
        this.currentLoginAccountInfo.getService();

        this.accountName = this.currentLoginAccountInfo.getAccessToken();
        this.secret = this.currentLoginAccountInfo.getTokenSecret();

        // Prepare Thread
        Thread th = new Thread(new Runnable() {

            StringBuffer errors = new StringBuffer();

            @Override
            public void run()
            {
                CommunicationHandlerResult result = TwitterHandler.uploadImage(
                        UpdateDialog.this.accountName,
                        UpdateDialog.this.secret, filePath, message);
                if (result.getResultCode() != 200)
                {
                    this.errors.append(IGeneral.SERVICE_NAME_TWITTER + ":"
                            + String.valueOf(result));
                }

                // Show Toast if Error
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("errors", this.errors.toString());
                msg.setData(bundle);
                UpdateDialog.this.mHandler.sendMessage(msg);
            }
        });
        th.start();

    }

    // -----------------------------------------------------------------------------
    /**
     * Show Result
     */
    // -----------------------------------------------------------------------------
    private void showResultDialog(String errors)
    {

        StringBuffer message = new StringBuffer();
        String[] value = errors.split(":");
        for (int i = 0; i < value.length; i++)
        {
            message.append("Failed:" + value[i] + "\n");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage(message.toString());

        builder.setPositiveButton(android.R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();

            }
        });
        builder.show();
    }

    @Override
    public void dismiss()
    {
        closeGps();
        super.dismiss();
    }
}
