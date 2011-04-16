package jp.gr.kmtn.stdroid.openfunctions;

import jp.gr.kmtn.stdroid.R;
import jp.gr.kmtn.stdroid.dialog.LongTweetDialog;
import jp.gr.kmtn.stdroid.dialog.TranslateDialog;
import jp.gr.kmtn.stdroid.dialog.UrlShortenDialog;
import jp.gr.kmtn.stdroid.twitter.TwitterHandler;
import jp.gr.kmtn.stdroid.util.AccountInfo;
import jp.gr.kmtn.stdroid.util.CommunicationHandlerResult;
import jp.gr.kmtn.stdroid.util.MyDbAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MessageSentActivity extends Activity
{

    private ImageView                  imageView;

    private TextView                   notifyUsers;

    private EditText                   updateText;

    private Button                     btnOk;

    private Button                     btnCancel;

    private ImageButton                btnShortenUrl;

    private ImageButton                btnTranslate;

    private ImageButton                btnLongTweet;

    private MyDbAdapter                myDbAdapter;

    private AccountInfo                accountInfo;

    private String                     service;

    private CommunicationHandlerResult result;

    private int                        resultCode;

    private Context                    context;

    private LocationManager            locationManager;

    private LocationListener           locationListener;

    private String[]                   locationArray      = new String[2];

    /** Counter */
    private TextView                   counterText;

    /** Max Text */
    private int                        MAX_TEXT_COUNT     = 140;

    private boolean                    isLongTweetEnabled = false;

    //----------------------------------------------------------------------
    /**
     * Handler
     */
    //----------------------------------------------------------------------
    Handler                            mHandler           = new Handler() {
                                                              @Override
                                                              public void handleMessage(
                                                                      Message message)
                                                              {
                                                                  if (MessageSentActivity.this.resultCode == 200)
                                                                  {
                                                                      //Succeeded
                                                                      finish();
                                                                  }
                                                                  else
                                                                  {
                                                                      //Failed
                                                                      Toast toast = Toast.makeText(
                                                                              MessageSentActivity.this.context,
                                                                              R.string.dialog_activity_message_sent_failed,
                                                                              Toast.LENGTH_SHORT);
                                                                      toast.show();
                                                                  }

                                                                  MessageSentActivity.this.imageView.clearAnimation();
                                                              }
                                                          };

    /** Handler2 
     * Just a Temp. Can delete at 2.0(Nakashima)
     * */
    Handler                            mHandler2          = new Handler() {
                                                              @Override
                                                              public void handleMessage(
                                                                      Message msg)
                                                              {
                                                                  Toast.makeText(
                                                                          getApplicationContext(),
                                                                          getString(R.string.error_message_rejected_for_ngword),
                                                                          Toast.LENGTH_LONG).show();
                                                              }

                                                          };

    //----------------------------------------------------------------------
    /**
     * Called when activity was created
     */
    //----------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
        setContentView(R.layout.activity_sent_message);
        getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
                R.drawable.icon);

        //Set Account
        setAccount();

        //Image
        this.imageView = (ImageView) findViewById(R.id.imageanimation);

        //TextView for notifying users
        this.notifyUsers = (TextView) findViewById(R.id.notifyusers);

        //OK Button
        this.btnOk = (Button) findViewById(R.id.Button_send_ok);
        this.btnOk.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                MessageSentActivity.this.imageView.setVisibility(0);

                //Send Message
                String message = MessageSentActivity.this.updateText.getText().toString();
                sendMessage(message);
            }
        });

        //Cancel Button
        this.btnCancel = (Button) findViewById(R.id.Button_send_cancel);
        this.btnCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                finish();
            }
        });

        //Shrten URL
        this.btnShortenUrl = (ImageButton) findViewById(R.id.shortenUrlButton);
        this.btnShortenUrl.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                shortenUrl();
            }
        });

        //Translate
        this.btnTranslate = (ImageButton) findViewById(R.id.translationButton);
        this.btnTranslate.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {
                translate();
            }
        });

        //Long Tweet
        this.btnLongTweet = (ImageButton) findViewById(R.id.longTweetButton);
        this.btnLongTweet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                longTweet();
            }
        });
        //Check if Long Tweet is Enabled
        if (this.accountInfo.getTokenSecret() != null)
        {
            this.isLongTweetEnabled = true;
        }
        else
        {
            this.isLongTweetEnabled = false;
            this.btnLongTweet.setVisibility(View.GONE);
        }

        // Counter
        this.counterText = (TextView) findViewById(R.id.counterText);
        this.accountInfo.getService();

        this.counterText.setText(String.valueOf(this.MAX_TEXT_COUNT));

        //Update Text
        this.updateText = (EditText) findViewById(R.id.edit);
        this.updateText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count)
            {
                int leftCount = MessageSentActivity.this.MAX_TEXT_COUNT
                        - MessageSentActivity.this.updateText.getText().toString().length();

                MessageSentActivity.this.counterText.setText(String.valueOf(leftCount));

                if (leftCount < 0)
                {
                    MessageSentActivity.this.counterText.setTextColor(0xFFFF4500);
                    MessageSentActivity.this.btnOk.setVisibility(android.view.View.INVISIBLE);
                    if (MessageSentActivity.this.isLongTweetEnabled)
                    {
                        MessageSentActivity.this.btnLongTweet.setEnabled(true);
                    }

                }
                else
                {
                    MessageSentActivity.this.btnOk.setVisibility(android.view.View.VISIBLE);
                    MessageSentActivity.this.counterText.setTextColor(Color.BLACK);
                    if (MessageSentActivity.this.isLongTweetEnabled)
                    {
                        MessageSentActivity.this.btnLongTweet.setEnabled(false);
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

        this.context = getApplicationContext();

        //Set Intent Data
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String inputtext = bundle.getString("send");

        this.updateText.setText(inputtext);
    }

    //----------------------------------------------------------------------
    /**
     * Called when activity was started
     */
    //----------------------------------------------------------------------
    @Override
    protected void onStart()
    {
        super.onStart();
    }

    //----------------------------------------------------------------------
    /**
     * Called when activity was stopped
     */
    //----------------------------------------------------------------------
    @Override
    protected void onStop()
    {
        super.onStop();
        if (this.myDbAdapter != null)
        {
            this.myDbAdapter.close();
        }
        closeGps();
    }

    private void setAccount()
    {
        this.myDbAdapter = new MyDbAdapter(this);
        this.myDbAdapter.open();

        this.accountInfo = this.myDbAdapter.getCurrentLoginAccountInfo();

        if (this.accountInfo == null)
        {
            this.notifyUsers.setText(R.string.dialog_activity_message_sent_notifyusers);
            this.notifyUsers.setVisibility(0);
            this.btnOk.setEnabled(false);
        }
        else
        {
            //Get user information

            this.service = this.accountInfo.getService();
        }
    }

    //Start shortenUrl dialog
    private void shortenUrl()
    {
        UrlShortenDialog urlDialog = new UrlShortenDialog(this, this.updateText);
        urlDialog.show();
    }

    //Start translation dialog
    private void translate()
    {
        TranslateDialog dialog = new TranslateDialog(this, this.updateText,
                this.myDbAdapter);
        dialog.show();
    }

    //Start longtweet dialog
    private void longTweet()
    {
        LongTweetDialog dialog = new LongTweetDialog(this, this.updateText,
                this.accountInfo);
        dialog.show();
    }

    //----------------------------------------------------------------------
    /**
     * Send Message
     */
    //----------------------------------------------------------------------
    private void sendMessage(final String status)
    {

        //Progress Animation Start
        Animation animation = AnimationUtils.loadAnimation(this,
                R.anim.rotateicon);
        animation.setDuration(1000);
        this.imageView.startAnimation(animation);
        //animation.setRepeatCount(5);
        //animation.setRepeatMode(Animation.RESTART);

        //send message
        Thread interaction = new Thread(new Runnable() {

            public void run()
            {
                String twitterApiProxy = MessageSentActivity.this.myDbAdapter.getSettingValue(MyDbAdapter.PARAM_SETTING_TWITTER_API_PROXY);
                String apiServer = MessageSentActivity.this.myDbAdapter.getSettingValue(MyDbAdapter.PARAM_SETTING_TWITTER_API_PROXY_SERVER);

                //Set Account(Proxy)
                if (twitterApiProxy.equals(MyDbAdapter.PARAM_VALUE_ON))
                {
                    String password = MessageSentActivity.this.accountInfo.getPassWord();
                    String screenName = MessageSentActivity.this.accountInfo.getScreenName();
                    TwitterHandler.setAccount(screenName, password,
                            TwitterHandler.AUTH_TYPE_BASIC, apiServer);
                }
                else
                {//(Original)
                    String accessToken = MessageSentActivity.this.accountInfo.getAccessToken();
                    String tokenSecret = MessageSentActivity.this.accountInfo.getTokenSecret();
                    TwitterHandler.setAccount(accessToken, tokenSecret,
                            TwitterHandler.AUTH_TYPE_OAUTH, null);
                }

                MessageSentActivity.this.result = TwitterHandler.updateStatus(status);

                MessageSentActivity.this.resultCode = MessageSentActivity.this.result.getResultCode();
                //mHandler.sendEmptyMessage(resultCode);

                Message message = new Message();
                Bundle b = new Bundle();
                b.putInt("result", MessageSentActivity.this.resultCode);
                message.setData(b);
                MessageSentActivity.this.mHandler.sendMessage(message);

            }
        });
        interaction.start();

    }

    // -------------------------------------------------------
    /**
     * this method is used to get the current location
     */
    // --------------------------------------------------------
    protected void initGps()
    {
        this.locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);

        this.locationListener = new LocationListener() {
            public void onLocationChanged(Location loc)
            {

                Location location = loc;
                // log it when the location changes
                if (location != null)
                {
                    Log.i("SuperMap", "Location changed : Lat: "
                            + location.getLatitude() + " Lng: "
                            + location.getLongitude());
                    /*
                    Toast.makeText(
                    		context,
                    		location.getLatitude() + ":"
                    				+ location.getLongitude(),
                    		Toast.LENGTH_SHORT).show();
                    */
                }
            }

            public void onProviderDisabled(String provider)
            {
                Log.d("test", "providerDisabled");
            }

            public void onProviderEnabled(String provider)
            {
                Log.d("test", "providerEnabled");
            }

            public void onStatusChanged(String provider, int status,
                    Bundle extras)
            {
                Log.d("test", "providerEnabled");
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
            this.locationArray[0] = String.valueOf(location.getLatitude());// Latitude
            this.locationArray[1] = String.valueOf(location.getLongitude());// Longitude
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
        this.locationArray[0] = String.valueOf(location.getLatitude());// Latitude
        this.locationArray[1] = String.valueOf(location.getLongitude());// Longitude
    }

}
